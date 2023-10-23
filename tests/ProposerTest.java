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