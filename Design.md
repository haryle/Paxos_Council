## System Components:

- `AcceptorCouncillor`: is a councillor agent that can only act as an acceptor. In the Paxos context, an acceptor
  councillor is only reactive, handling
  PREPARE and PROPOSE requests from ProposerCouncillor.
- `ProposerCouncillor`: is a councillor agent that can act as both an acceptor and a proposer. In the Paxos context, a
  proposer councillor can make a
  PREPARE and PROPOSE requests to AcceptorCouncillor, and handle PROMISE and NAK_PREPARE replies from acceptor
  councillor. Additionally, it also doubly
  act as an AcceptorCouncillor, handling PROPOSE and PREPARE requests from itself and other ProposerCouncillors.
- `CentralRegistry`: is a server that has the contact address of all councillors. For simplicity, we assume that the
  CentralRegistry never goes down
  or have a fail-over solution that is completely transparent. We also assume that messages sent from Central Registry
  to the proposer is reliable.

## Communication Protocol

The following protocol is developed on the application layer, using TCP protocol in OSI layer 4.

TCP guarantees that messages are delivered, delivered in order, and delivered without data corruption. What is not
accounted for, is if one of the
communication entity has received the message but fails to respond (i.e Internal error):

### Message structure:

`<SenderID>:<ReceiverID>:<MessageType>:<MessageID>:<acceptedID>:<acceptedValue>:<receiverList>:<timestamp>`

- SenderID: councillor ID of sender. No default value
- ReceiverID: councillor ID of receiver. No default value
- MessageType: type of current message. No default value
- MessageID: ID corresponding to current message. No default value
- acceptedID: acceptedID in PROMISE message. Defaults to -1
- acceptedValue: acceptedValue in PROMISE message. Defaults to -1
- receiverList: list of receivers that the current message is sending to in INFORM message. Defaults to []
- timestamp: broadcast server timestamp. Default to -1

### Message type:

### Broadcast messages

- PREPARE: Paxos prepare message. PREPARE messages are sent from the proposer who issues the message (SenderID) field to
  the `CentralRegistry` who then broadcast to all receivers in the network.
    - SenderID: councillorID
    - ReceiverID: 0 when sent to `CentralRegistry` (broadcast ID). This field is then changed to the receiver's
      councillorID when sent from `CentralRegistry`.
    - MessageType: "PREPARE"
    - MessageID: proposer's current round ID
    - timestamp: -1 when sent from proposer, then the logical timestamp of the `CentralRegistry` when sent to receivers.
- PROPOSE: Paxos propose message. PROPOSE messages are sent from the proposer who issues the message (SenderID) field to
  the `CentralRegistry` who then broadcast to all receivers in the network.
    - SenderID: councillorID
    - ReceiverID: 0 when sent to `CentralRegistry` (broadcast ID). This field is then changed to the receiver's
      councillorID when sent from `CentralRegistry`.
    - MessageType: "PROPOSE"
    - MessageID: proposer's current round ID
    - acceptedValue: proposed value determined from Paxos protocol.
    - timestamp: -1 when sent from proposer, then the logical timestamp of the `CentralRegistry` when sent to receivers.

### Connection messages

- CONNECT: message that is sent by any councillor at start up to register itself at the registry. `CentralRegistry` uses
  the message's SenderID to keep track of servers that it is connected to.
    - SenderID: councillorID
    - ReceiverID: 0 (`CentralRegistry`)
    - MessageType: "CONNECT"

- INFORM: message that is sent by the `CentralRegistry` to a `ProposerCouncillor` after a broadcast message is received.
  This is
  to inform the proposer of whose replies can it expect from, which is used for determining the propose value in the
  Paxos protocol.
    - SenderID: 0 (`CentralRegistry`)
    - ReceiverID: proposer's councillorID
    - MessageType: "INFORM"
    - MessageID: ID of the broadcast message the `CentralRegistry` receives from the proposer.
    - receiverList: list of receivers that the `CentralRegistry` keeps track of.

### Relay messages

- PROMISE: Paxos protocol PROMISE message sent by acceptor in response to PREPARE message. The message is first sent
  from the acceptor to the `CentralRegistry`, who then relays
  the message back to sender.
    - SenderID: acceptor's councillorID
    - ReceiverID: proposer's councillorID
    - MessageType: "PROMISE"
    - MessageID: proposer's PREPARE MessageID which is the highest ID that the acceptor has seen.
    - acceptedID: either default (-1) if the acceptor has not accepted any proposal or the ID of the accepted proposal
    - acceptedValue: either default (-1) if the acceptor has not accepted any proposal or the value of the accepted
      proposal.
    - timestamp: timestamp of the PREPARE message.
- NAK_PREPARE: message sent when the PREPARE MessageID is not the highest that the acceptor has seen. This reply is sent
  so that the `CentralRegistry`
  does not have to retry sending PREPARE message (reducing traffic). This message is sent back to the proposer so that
  it
  can determine what action to take.
    - SenderID: acceptor's councillorID
    - ReceiverID: proposer's councillorID
    - MessageType: "NAK_PREPARE"
    - MessageID: proposer's PREPARE message ID
    - timestamp: timestamp of the PREPARE message
- ACCEPT: Paxos protocol ACCEPT message sent by acceptor in response to PROPOSE message. The message is first sent
  from the acceptor to the `CentralRegistry`, which handles the message as a distinguished learner.
    - SenderID: acceptor's councillorID
    - ReceiverID: proposer's councillorID
    - MessageType: "ACCEPT"
    - MessageID: proposer's PROPOSE MessageID
    - acceptedID: proposer's PROPOSE MessageID
    - acceptedValue: proposer's PROPOSE acceptedValue
    - timestamp: timestamp of the PREPARE message.

- NAK_PROPOSE: message sent when the PROPOSE MessageID is not what is expected by the acceptor. This reply is sent
  so that the `CentralRegistry`
  does not have to retry sending PROPOSE message (reducing traffic).
    - SenderID: acceptor's councillorID
    - ReceiverID: proposer's councillorID
    - MessageType: "NAK_PROPOSE"
    - MessageID: proposer's PROPOSE MessageID
    - acceptedID: proposer's PROPOSE MessageID
    - acceptedValue: proposer's PROPOSE acceptedValue
    - timestamp: timestamp of the PROPOSE message

### ShutDown message:

SHUTDOWN: tells every one that a consensus has been reached and that each councillor can now shutdown.

- SenderID: 0 - the CentralRegistry
- ReceiverID: receiver's councillor ID
- MessageType: "SHUTDOWN"

## Macro Level Communication:

### At startup:

- Whenever a councillor (ProposerCouncillor/AcceptorCouncillor) is initiated, it sends a CONNECT message to
  CentralRegistry.
- CentralRegistry registers the connection of the sender.
- Each councillor has an ID from 1 to 9 (corresponds to M1 to M9).
- Each proposer is assigned a partitioned ID space. For example, proposer 1 ID is 1, 11, 21, 31, etc. Proposer 2 is 2,
  12, 22, 32, etc.

### When a Proposer makes a PROPOSE/PREPARE message:

- The proposer sends a PROPOSE/PREPARE message to the CentralRegistry
- CentralRegistry sends an INFORM message back to the proposer.
- Concurrently, the proposer sends the same message to itself (doubly acting as an acceptor). This reduces traffic from
  and to the CentralRegistry.
- The proposer then awaits replies for each receiver in the INFORM message. This comes as relay message from
  CentralRegistry.
- CentralRegistry meanwhile broadcast the message to all receivers.

### When the CentralRegistry receives a broadcast message:

- It then sends an INFORM message containing the list of receivers EXCLUDING the owner of the broadcast message back to
  the owner.
- It then forwards the message to every receiver in the receiver list, EXCLUDING the owner of the broadcast message (who
  should handle the message internally).
- The broadcast message has the receiver field changed to match the receiver's councillorID.
- The broadcast message has the timestamp field changed to the current logical timestamp (different messages ill have
  different timestamps).
- The CentralRegistry keeps track of the message that it broadcasts. For each sent message, if it doesn't receive a
  reply from the same receiver with a matching
  reply timestamp within a TIMEOUT period, it will resend the message for a MAX_RETRIES amount of times. If it still
  doesn't receive an appropriate reply after all
  attempts, it will send a corresponding NAK to the original broadcast message to the proposer (
  NAK_PREPARE/NAK_PROPOSE).
- If it does receive a matching reply, the reply is relayed back to the original proposer.

### When the Acceptor receives a PREPARE/PROPOSE message from the broadcast server:

- It acts according to the Paxos protocol and send the appropriate reply.

### When the Proposer receive replies for PROPOSE message:

- If the reply's MessageID is not the same as the proposer's current round ID, it ignores the message
- If it has received replies from the majority of receiver it is expecting (a NAK_PREPARE) does not count as reply, it
  propose a value
  (send a propose messasge) following Paxos protocol.
- Otherwise, it proceed to the next round by incrementing the round ID and resend a PREPARE message with the new round
  ID. However, it does so
  after some delays to avoid livelocking.

### When the CentralRegistry receives replies for PROPOSE message:

It handles the message as a Distinguished utils.Learner.

- If at any point in time, the majority reaches consensus on a proposed value, it sends a SHUTDOWN message to every
  councillor, causing everyone to
  shutdown. It then outputs the agreed upon value to the screen.

## Normal Operation:

A proposer sends a broadcast message to every acceptor via CentralRegistry. Each acceptor responds on time which then
relays back to the
proposer to reach a consensus.

## Possible breaking points in one and multiple-clients:

- Acceptors respond after small to moderate delays.
- Acceptors do not respond (Internal Server Error) - this also represent the possibility of a councillor going offline.
- Two proposers sending messages at the same time causing live locking

## How the scenarios are tested:

- Two proposer sending messages at the same time can be done using thread send:

```C
Thread thread1 = new Thread(proposer1.propose());
Thread thread2 = new Thread(proposer2.propose());
thread1.start();
thread2.start();
```

Live locking can be checked by checking the log.

- Acceptors responds after a small to moderate delay: each acceptor send method now accepts a delay parameter. This
  simulates network delay in sending and receiving message.

```C
public void send(Message message, int delay);
```

The behaviour of the system can be tested by verifying the log in the presence of deterministic delay

- Acceptors do not respond (Internal Server Error): each acceptor send methods now has a delay time that is greater
  than MAX_WAIT_TIME from CentralRegistry. This means the server is registered as being down:

```C
public void send(Message message, int MAX_WAIT_TIME);  
```

The behaviour of the system can be tested by verifying the log in the presence of deterministic delay.

## Design of the testing framework:

### Paxos behavior testing:

Unit tests for proposer and acceptor to ensure that their response to a message matches with the behaviour specified
under the
Paxos protocol. For instance, the following psedo code for acceptor and proposer testing. What this does is given a
current
state of acceptor/proposer and given a message (prepare/proposer/etc), the output should be predictable based on Paxos
protocol.

```Java
class AcceptorTest {
    Acceptor acceptor;

    int from = 2;
    int to = 4;

    int lowID = 3;
    int highID = 5;
    int lowIDValue = 11;
    int highIDValue = 12;

    Message prepareLowID = Message.prepare(from, to, lowID);
    Message rejectLowPrepare = Message.getNakMessage(prepareLowID);
    Message prepareHighID = Message.prepare(from, to, highID);
    Message proposeLowID = Message.propose(from, to, lowID, lowIDValue);
    Message rejectLowPropose = Message.getNakMessage(proposeLowID);
    Message proposeHighID = Message.propose(from, to, highID, highIDValue);
    Message rejectHighPropose = Message.getNakMessage(proposeHighID);
    Message promiseNoAcceptLowID = Message.promise(to, from, lowID, -1);
    Message promiseNoAcceptHighID = Message.promise(to, from, highID, -1);
    Message promiseLowIDAcceptHighID = Message.promise(to, from, highID, lowID,
            lowIDValue, -1);

    Message acceptLowValue = Message.accept(to, from, lowID, lowIDValue, -1);

    Message acceptHighValue = Message.accept(to, from, highID, highIDValue, -1);

    @BeforeEach
    void setUp() {
        acceptor = new Acceptor();
    }

    public Message getFinalMessage(Message[] messages) {
        Message response = null;
        for (Message message : messages)
            response = acceptor.handleMessage(message);
        return response;
    }

    public void testMessageSequence(Message[] messages, Message expected) {
        Message response = getFinalMessage(messages);
        assertEquals(expected.toString(), response.toString());
    }

    @Test
    void testNoAcceptReceiveLowPrepareReturnPromiseWithNoAccept() {
        testMessageSequence(new Message[]{prepareLowID}, promiseNoAcceptLowID);
    }

    @Test
    void testNoAcceptReceiveHighPrepareReturnPromiseWithNoAccept() {
        testMessageSequence(new Message[]{prepareHighID}, promiseNoAcceptHighID);
    }

    @Test
    void testAcceptLowPrepareThenAcceptLowPropose() {
        testMessageSequence(new Message[]{prepareLowID, proposeLowID}, acceptLowValue);
    }

    @Test
    void testAcceptHighPrepareThenAcceptHighValue() {
        testMessageSequence(new Message[]{prepareHighID, proposeHighID}, acceptHighValue);
    }

    @Test
    void testPromiseLowThenPromiseHigh() {
        testMessageSequence(new Message[]{prepareLowID, prepareHighID}, promiseNoAcceptHighID);
    }

    @Test
    void testPromiseHighThenRejectLowPrepare() {
        testMessageSequence(new Message[]{prepareHighID, prepareLowID}, rejectLowPrepare);
    }

    @Test
    void testPromiseLowPrepareCannotAcceptHighPropose() {
        testMessageSequence(new Message[]{prepareLowID, proposeHighID}, rejectHighPropose);
    }

    @Test
    void testPromiseHighPrepareCannotAcceptLowPropose() {
        testMessageSequence(new Message[]{prepareHighID, proposeLowID}, rejectLowPropose);
    }

    @Test
    void testAcceptLowCanPromiseHigh() {
        testMessageSequence(new Message[]{prepareLowID, proposeLowID, prepareHighID}, promiseLowIDAcceptHighID);
    }

    @Test
    void testAcceptLowCannotAcceptHighProposeIfNotPrepared() {
        testMessageSequence(new Message[]{prepareLowID, proposeLowID, proposeHighID}, rejectHighPropose);
    }

    @Test
    void testAcceptHighWillRejectLowPrepare() {
        testMessageSequence(new Message[]{prepareHighID, proposeHighID, prepareLowID}, rejectLowPrepare);
    }

    @Test
    void testAcceptHighWillRejectLowPropose() {
        testMessageSequence(new Message[]{prepareHighID, proposeHighID, proposeLowID}, rejectLowPropose);
    }

    @Test
    void testAcceptLowCanAcceptHighWithPrepareProposeSequence() {
        testMessageSequence(new Message[]{prepareLowID, proposeLowID, prepareHighID, proposeHighID}, acceptHighValue);
    }

    @Test
    void testAcceptHighWillRejectLowPrepareAndPropose() {
        testMessageSequence(new Message[]{prepareHighID, proposeHighID, prepareLowID, proposeLowID}, rejectLowPropose);
    }

}
```

```Java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.helpers.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit testing of proposer
 */
class ProposerUnitTest {
    Proposer proposer;

    int proposerID = 1;
    List<Integer> receivers;

    Message proposerSelfResponse;

    Message firstNak;

    Message firstPromiseNoID;

    Message firstPromiseLowID;

    Message firstPromiseHighID;

    Message secondNak;

    Message secondPromiseNoID;

    Message secondPromiseLowID;

    Message secondPromiseHighID;

    Message thirdNak;

    Message thirdPromiseNoID;

    Message thirdPromiseLowID;

    Message thirdPromiseHighID;

    @BeforeEach
    void setUp() throws InterruptedException {
        proposer = new Proposer(proposerID, 10, 11);
        receivers = new ArrayList<>(Arrays.asList(1, 2, 3));
        receivers.remove(Integer.valueOf(proposerID));
        Message inform = Message.inform(proposer.councillorID, proposer.getID(),
                receivers);
        proposer.handleMessage(inform);
        Message prepare = Message.prepare(proposer.councillorID, 0, proposer.getID());
        firstNak = Message.rejectPrepare(1, proposer.councillorID, proposer.getID(), 0);
        secondNak = Message.rejectPrepare(2, proposer.councillorID,
                proposer.getID(), 0);
        thirdNak = Message.rejectPrepare(3, proposer.councillorID,
                proposer.getID(), 0);
        firstPromiseNoID = Message.promise(1, proposer.councillorID, proposer.getID()
                , 0);
        secondPromiseNoID = Message.promise(2, proposer.councillorID,
                proposer.getID(), 0);
        thirdPromiseNoID = Message.promise(3, proposer.councillorID, proposer.getID()
                , 0);
        firstPromiseLowID = Message.promise(1, proposer.councillorID,
                proposer.getID(), 4, 4, 0);
        secondPromiseLowID = Message.promise(2, proposer.councillorID,
                proposer.getID(), 4, 4, 0);
        thirdPromiseLowID = Message.promise(3, proposer.councillorID,
                proposer.getID(), 4, 4, 0);
        firstPromiseHighID = Message.promise(1, proposer.councillorID,
                proposer.getID(), 5, 5, 0);
        secondPromiseHighID = Message.promise(2, proposer.councillorID,
                proposer.getID(), 5, 5, 0);
        thirdPromiseHighID = Message.promise(3, proposer.councillorID,
                proposer.getID(), 5, 5, 0);
    }

    @Test
    void testRegisterAcceptorsWhenReceivingAcceptorList() throws InterruptedException {
        receivers.add(proposerID);
        assertEquals(receivers.size(), proposer.getAcceptorList().size());
        assertTrue(proposer.getAcceptorList().containsAll(receivers));
    }

    @Test
    void testNextRoundEverythingIsReset() throws InterruptedException {
        Message inform = Message.inform(proposer.councillorID, proposer.getID(),
                receivers);
        proposer.handleMessage(inform);
        proposer.nextRound();
        assertEquals(proposer.councillorID + Proposer.MAX_PROPOSER, proposer.getID());
        assertTrue(proposer.getAcceptorList().contains(proposerID));
    }

    @Test
    void testInformFromPreviousRoundDoesNotRegister() throws InterruptedException {
        proposer.nextRound();
        Message inform = Message.inform(proposer.councillorID, proposer.councillorID,
                receivers);
        proposer.handleMessage(inform);
        assertTrue(proposer.getAcceptorList().contains(proposerID));
    }

    void testHandlingUnhandledMessage(Message message, int sender) throws InterruptedException {
        Message reply = proposer.handleMessage(message);
        assertFalse(proposer.getAcceptorResponse().containsKey(sender));
        assertNull(reply);
    }

    @Test
    void testDoNothingWhenReceiveAcceptMessage() throws InterruptedException {
        int sender = 1;
        Message message = Message.accept(sender, proposer.councillorID,
                proposer.getID(),
                proposer.councillorID, 0);
        testHandlingUnhandledMessage(message, sender);
        assertTrue(proposer.getAcceptorList().contains(sender));
    }

    @Test
    void testDoNothingWhenReceiveConnectMessage() throws InterruptedException {
        int sender = 1;
        Message message = Message.connect(sender);
        testHandlingUnhandledMessage(message, sender);
        assertTrue(proposer.getAcceptorList().contains(sender));
    }

    @Test
    void testWhenReceivePromiseFromOneSenderRemoveSenderFromListAndRecordMessage() throws InterruptedException {
        Message promise = Message.promise(1, proposer.councillorID, proposer.getID(),
                0);
        Message reply = proposer.handleMessage(promise);
        assertNull(reply);
        assertTrue(proposer.getAcceptorResponse().containsKey(1));
        assertFalse(proposer.getAcceptorList().contains(1));
        assertTrue(proposer.getAcceptorResponse().containsValue(promise));
    }

    private Message fixtureReceiveThreeMessagesMovesNextRound(Message first,
                                                              Message second,
                                                              Message third) throws InterruptedException {
        proposer.handleMessage(first);
        proposer.handleMessage(second);
        Message reply = proposer.handleMessage(third);
        assertTrue(proposer.getAcceptorResponse().isEmpty());
        assertTrue(proposer.getAcceptorList().contains(proposerID));
        assertEquals(proposer.councillorID + Proposer.MAX_PROPOSER, proposer.getID());
        return reply;
    }

    private Message fixtureReceiveThreeMessages(Message first,
                                                Message second,
                                                Message third) throws InterruptedException {
        proposer.handleMessage(first);
        proposer.handleMessage(second);
        Message reply = proposer.handleMessage(third);
        assertFalse(proposer.getAcceptorResponse().isEmpty());
        assertTrue(proposer.getAcceptorList().isEmpty());
        assertEquals(proposer.councillorID, proposer.getID());
        return reply;
    }

    @Test
    void testPropose_NAKNAKNAK_NextRoundSendPrepapre() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessagesMovesNextRound(firstNak, secondNak
                , thirdNak);
        Message expectedPrepare = Message.prepare(proposer.councillorID, 0,
                proposer.councillorID + Proposer.MAX_PROPOSER);
        assertEquals(expectedPrepare.toString(), reply.toString());
    }

    @Test
    void testPropose_NAKNAKNO_NextRoundSendPrepapre() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessagesMovesNextRound(firstNak, secondNak
                , thirdPromiseNoID);
        Message expectedPrepare = Message.prepare(proposer.councillorID, 0,
                proposer.councillorID + Proposer.MAX_PROPOSER);
        assertEquals(expectedPrepare.toString(), reply.toString());
    }

    @Test
    void testPropose_NAKNAKLOW_NextRoundSendPrepare() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessagesMovesNextRound(firstNak, secondNak
                , thirdPromiseLowID);
        Message expectedPrepare = Message.prepare(proposer.councillorID, 0,
                proposer.councillorID + Proposer.MAX_PROPOSER);
        assertEquals(expectedPrepare.toString(), reply.toString());
    }

    @Test
    void testPropose_NAKNAKHIGH_NextRoundSendPrepare() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessagesMovesNextRound(firstNak, secondNak
                , thirdPromiseHighID);
        Message expectedPrepare = Message.prepare(proposer.councillorID, 0,
                proposer.councillorID + Proposer.MAX_PROPOSER);
        assertEquals(expectedPrepare.toString(), reply.toString());
    }

    @Test
    void testPropose_NAKNOLOW_SendLowValue() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessages(firstPromiseNoID,
                secondNak, thirdPromiseLowID);
        Message expectPropose = Message.propose(proposer.councillorID, 0,
                proposer.getID(), thirdPromiseLowID.acceptValue);
        assertEquals(expectPropose.toString(), reply.toString());
    }

    @Test
    void testPropose_NAKNOHIGH_SendHighValue() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessages(firstPromiseNoID,
                secondNak, thirdPromiseHighID);
        Message expectPropose = Message.propose(proposer.councillorID, 0,
                proposer.getID(), thirdPromiseHighID.acceptValue);
        assertEquals(expectPropose.toString(), reply.toString());
    }

    @Test
    void testPropose_NAKLOWHIGH_SendHighValue() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessages(firstPromiseLowID,
                secondNak, thirdPromiseHighID);
        Message expectPropose = Message.propose(proposer.councillorID, 0,
                proposer.getID(), thirdPromiseHighID.acceptValue);
        assertEquals(expectPropose.toString(), reply.toString());
    }

    @Test
    void testPropose_NONONO_SendProposeCouncillorID() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessages(firstPromiseNoID,
                secondPromiseNoID, thirdPromiseNoID);
        Message expectPropose = Message.propose(proposer.councillorID, 0,
                proposer.getID(), proposer.councillorID);
        assertEquals(expectPropose.toString(), reply.toString());
    }

    @Test
    void testPropose_NONONAK_SendProposeCouncillorID() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessages(firstPromiseNoID,
                secondPromiseNoID, thirdNak);
        Message expectPropose = Message.propose(proposer.councillorID, 0,
                proposer.getID(), proposer.councillorID);
        assertEquals(expectPropose.toString(), reply.toString());
    }

    @Test
    void testPropose_NONOLOW_SendProposeLowValue() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessages(firstPromiseNoID,
                secondPromiseNoID, thirdPromiseLowID);
        Message expectPropose = Message.propose(proposer.councillorID, 0,
                proposer.getID(), thirdPromiseLowID.acceptValue);
        assertEquals(expectPropose.toString(), reply.toString());
    }

    @Test
    void testPropose_NONOHIGH_SendProposeHighValue() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessages(firstPromiseNoID,
                secondPromiseNoID, thirdPromiseHighID);
        Message expectPropose = Message.propose(proposer.councillorID, 0,
                proposer.getID(), thirdPromiseHighID.acceptValue);
        assertEquals(expectPropose.toString(), reply.toString());
    }

    @Test
    void testPropose_NOLOWHIGH_SendProposeHighValue() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessages(firstPromiseNoID,
                secondPromiseLowID, thirdPromiseHighID);
        Message expectPropose = Message.propose(proposer.councillorID, 0,
                proposer.getID(), thirdPromiseHighID.acceptValue);
        assertEquals(expectPropose.toString(), reply.toString());
    }

    @Test
    void testPropose_LOWLOWLOW_SendProposeLowValue() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessages(firstPromiseLowID,
                secondPromiseLowID, thirdPromiseLowID);
        Message expectPropose = Message.propose(proposer.councillorID, 0,
                proposer.getID(), firstPromiseLowID.acceptValue);
        assertEquals(expectPropose.toString(), reply.toString());
    }

    @Test
    void testPropose_LOWLOWNAK_SendProposeLowValue() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessages(firstPromiseLowID,
                secondPromiseLowID, thirdNak);
        Message expectPropose = Message.propose(proposer.councillorID, 0,
                proposer.getID(), firstPromiseLowID.acceptValue);
        assertEquals(expectPropose.toString(), reply.toString());
    }

    @Test
    void testPropose_LOWLOWNO_SendProposeLowValue() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessages(firstPromiseLowID,
                secondPromiseLowID, thirdPromiseNoID);
        Message expectPropose = Message.propose(proposer.councillorID, 0,
                proposer.getID(), firstPromiseLowID.acceptValue);
        assertEquals(expectPropose.toString(), reply.toString());
    }

    @Test
    void testPropose_LOWLOWHIGH_SendProposeHighValue() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessages(firstPromiseLowID,
                secondPromiseLowID, thirdPromiseHighID);
        Message expectPropose = Message.propose(proposer.councillorID, 0,
                proposer.getID(), firstPromiseHighID.acceptValue);
        assertEquals(expectPropose.toString(), reply.toString());
    }

    @Test
    void testPropose_HIGHHIGHHIGH_SendProposeHighValue() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessages(firstPromiseHighID,
                secondPromiseHighID, thirdPromiseHighID);
        Message expectPropose = Message.propose(proposer.councillorID, 0,
                proposer.getID(), firstPromiseHighID.acceptValue);
        assertEquals(expectPropose.toString(), reply.toString());
    }

    @Test
    void testPropose_HIGHHIGHNAK_SendProposeHighValue() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessages(firstPromiseHighID,
                secondPromiseHighID, thirdNak);
        Message expectPropose = Message.propose(proposer.councillorID, 0,
                proposer.getID(), firstPromiseHighID.acceptValue);
        assertEquals(expectPropose.toString(), reply.toString());
    }

    @Test
    void testPropose_HIGHHIGHNo_SendProposeHighValue() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessages(firstPromiseHighID,
                secondPromiseHighID, thirdPromiseNoID);
        Message expectPropose = Message.propose(proposer.councillorID, 0,
                proposer.getID(), firstPromiseHighID.acceptValue);
        assertEquals(expectPropose.toString(), reply.toString());
    }

    @Test
    void testPropose_HIGHHIGHLOW_SendProposeHighValue() throws InterruptedException {
        Message reply = fixtureReceiveThreeMessages(firstPromiseHighID,
                secondPromiseHighID, thirdPromiseLowID);
        Message expectPropose = Message.propose(proposer.councillorID, 0,
                proposer.getID(), firstPromiseHighID.acceptValue);
        assertEquals(expectPropose.toString(), reply.toString());
    }
}
```

### Deterministic testing:

- Standard operating condition:
    - Let proposer 1 proposes a value while proposer 2 and proposer 3 do no propose value. The system should halt and
      proposer 1 declared winner.
    - Let proposer 1 propose a value together with proposer 2, but proposer 2 prepare message reaching the majority
      first. Proposer 1 then sleep for a long time while propser 2
      send a propose message with value of 2. Proposer 2 should be declared the winner.
    - Let proposer 1 propose a value together with proposer 2 and proposer 2 prepare message reaches the majority first.
      However, proposer 2 then sleeps for a long time when proposer 1
      begins the next prepare round and propose value of 1. Proposer 1 should be declared the winner.

- Non standard operating condition:
    - Perform UAT to verify the logic in the log is correct. 