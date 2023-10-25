package utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.helpers.Message;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommServiceFixture {
    // Mock Connections
    AsyncClientConnection firstConn, secondConn, thirdConn, fourthConn;

    CommService commService;
    // Message ID
    int ID = 10;

    // Accepted ID
    int acceptedID = 5;

    // Accepted Value
    int acceptedValue = 4;

    // Propose Value
    int proposeValue = 7;

    // Timestamp
    int timestamp = 0;

    // Sender ID
    int sender = 1;

    // List of Receivers
    int first = 1;
    int second = 2;
    int third = 3;
    int fourth = 4;

    List<Integer> commList = new ArrayList<>(Arrays.asList(first, second, third,
            fourth));

    Message informSender;
    Message prepareBroadcast;
    Message prepareFirst;

    Message prepareSecond;

    Message prepareThird;

    Message prepareFourth;

    Message connectFirst;

    Message connectSecond;

    Message connectThird;

    Message connectFourth;

    Message replyPreparePromiseFirst;

    Message replyPreparePromiseIDSecond;

    Message replyPrepareRejectIDThird;

    Message replyPreparePromiseFourth;

    Message proposeBroadcast;

    Message proposeFirst;

    Message proposeSecond;

    Message proposeThird;

    Message proposeFourth;

    Message replyProposeAcceptFirst;

    Message replyProposeAcceptSecond;

    Message replyProposeAcceptThird;

    Message replyProposeRejectFourth;

    @BeforeEach
    void setUp() {
        // Create Comm Service and Connections
        commService = new CommService(5, 5000, new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>(),
                Executors.newSingleThreadScheduledExecutor());
        // Create the messages
        // Inform Message
        informSender = Message.inform(sender, ID, commList);

        // Prepare Messages
        prepareBroadcast = Message.prepare(sender, 0, ID);
        prepareFirst = Message.prepare(sender, first, ID);
        prepareSecond = Message.prepare(sender, second, ID);
        prepareThird = Message.prepare(sender, third, ID);
        prepareFourth = Message.prepare(sender, fourth, ID);

        // Connect Messages
        connectFirst = Message.connect(first);
        connectSecond = Message.connect(second);
        connectThird = Message.connect(third);
        connectFourth = Message.connect(fourth);

        // Reply to Prepare
        replyPreparePromiseFirst = Message.promise(first, sender, ID, timestamp);
        replyPreparePromiseIDSecond = Message.promise(second, sender, ID,
                acceptedID, acceptedValue,
                timestamp);
        replyPrepareRejectIDThird = Message.rejectPrepare(third, sender, ID,
                timestamp);
        replyPreparePromiseFourth = Message.promise(fourth, sender, ID, timestamp);

        // Propose Message
        proposeBroadcast = Message.propose(sender, 0, ID, proposeValue);
        proposeFirst = Message.propose(sender, first, ID, proposeValue);
        proposeSecond = Message.propose(sender, second, ID, proposeValue);
        proposeThird = Message.propose(sender, third, ID, proposeValue);
        proposeFourth = Message.propose(sender, fourth, ID, proposeValue);

        // Reply to Propose Message
        replyProposeAcceptFirst = Message.accept(first, sender, ID, proposeValue,
                timestamp);
        replyProposeAcceptSecond = Message.accept(second, sender, ID,
                proposeValue, timestamp);
        replyProposeAcceptThird = Message.accept(third, sender, ID,
                proposeValue, timestamp);

        replyProposeRejectFourth = Message.rejectPropose(fourth, sender, ID,
                proposeValue, timestamp);

        // Create the connections:
        firstConn = new MockConnection(null);
        secondConn = new MockConnection(null);
        thirdConn = new MockConnection(null);
        fourthConn = new MockConnection(null);

        // Register sender
        commService.registerConnection(first, firstConn);
        commService.registerConnection(second, secondConn);
        commService.registerConnection(third, thirdConn);
        commService.registerConnection(fourth, fourthConn);
    }

    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        commService.close();
        firstConn.close();
        secondConn.close();
        thirdConn.close();
        fourthConn.close();
    }


    static class MockConnection extends AsyncClientConnection {
        public final List<Message> sentMessages;
        public int messageCount = 0;

        public MockConnection(SocketChannel channel) {
            super(channel);
            sentMessages = new ArrayList<>();
        }

        @Override
        public void send(Message message) throws IOException {
            messageCount++;
            sentMessages.add(message);
        }

        @Override
        public void handleMessage(Message message) throws IOException {
        }
    }
}

class OneSenderOneReceiverCommServiceTest extends CommServiceFixture {
    void whenProposerMessageHasNoReplyRetryTillNAK(Message proposerMessage) throws InterruptedException {
        int maxAttempt = 5;
        commService.setMAX_ATTEMPT(maxAttempt);
        commService.setTIME_OUT(10);
        // Nak message
        Message nakMessage = Message.getNakMessage(proposerMessage);
        // Add receiver to registry
        int receiver = proposerMessage.to;
        // Send message to receiver
        commService.send(receiver, proposerMessage, true, true);
        MockConnection receiverConn =
                (MockConnection) commService.getRegistry().get(receiver);
        MockConnection senderConn =
                (MockConnection) commService.getRegistry().get(sender);
        // Sleep to ensure all messages are sent
        Thread.sleep(100);
        // Number of sent messages include the first one
        assertEquals(maxAttempt + 1, receiverConn.sentMessages.size());
        for (Message msg : receiverConn.sentMessages) {
            assertEquals(proposerMessage.toString(), msg.toString());
        }
        // A NAK is sent when no message received f
        assertEquals(1, senderConn.messageCount);
        assert nakMessage != null;
        assertEquals(nakMessage.toString(),
                senderConn.sentMessages.get(0).toString());
        // Check that the retryQueue is empty
        assertTrue(commService.getRetryQueue().isEmpty());
    }

    void whenProposerMessageHasWrongReplyRetryTillNAK(Message proposerMessage,
                                                      Message reply)
            throws InterruptedException {
        int maxAttempt = 5;
        commService.setMAX_ATTEMPT(maxAttempt);
        commService.setTIME_OUT(10);
        // Nak message
        Message nakMessage = Message.getNakMessage(proposerMessage);
        // Add receiver to registry
        int receiver = proposerMessage.to;
        // Send message to receiver
        commService.send(receiver, proposerMessage, true, true);
        commService.receive(reply);
        MockConnection receiverConn =
                (MockConnection) commService.getRegistry().get(receiver);
        MockConnection senderConn =
                (MockConnection) commService.getRegistry().get(sender);
        // Sleep to ensure all messages are sent
        Thread.sleep(100);
        // Number of sent messages include the first one
        assertEquals(maxAttempt + 1, receiverConn.sentMessages.size());
        for (Message msg : receiverConn.sentMessages) {
            assertEquals(proposerMessage.toString(), msg.toString());
        }
        // A NAK is sent when no message received f
        assertEquals(1, senderConn.messageCount);
        assert nakMessage != null;
        assertEquals(nakMessage.toString(),
                senderConn.sentMessages.get(0).toString());
        // Check that the retryQueue is empty
        assertTrue(commService.getRetryQueue().isEmpty());
    }

    void whenProposerMessageHasReplyWillNotRetry(Message proposerMessage,
                                                 Message reply) throws InterruptedException {
        // Set timestamp to stimulate effect from server
        proposerMessage.timestamp = timestamp;
        int maxAttempt = 2;
        commService.setTIME_OUT(100);
        commService.setMAX_ATTEMPT(maxAttempt);
        // Send proposerMessage to receiver
        int receiver = proposerMessage.to;
        commService.send(receiver, proposerMessage, true, true);
        commService.receive(reply);
        Thread.sleep(300);
        // Get connections
        MockConnection receiverConn =
                (MockConnection) commService.getRegistry().get(receiver);
        MockConnection senderConn =
                (MockConnection) commService.getRegistry().get(sender);

        // Since reply message is received, no message is resent and no nak is received
        assertEquals(1, receiverConn.messageCount);
        assertEquals(0, senderConn.messageCount);
        assertTrue(commService.getRetryQueue().isEmpty());
    }

    @Test
    void testRetryTillNAKPrepareSecond() throws InterruptedException {
        whenProposerMessageHasNoReplyRetryTillNAK(prepareSecond);
    }

    @Test
    void testRetryTillNAKPrepareThird() throws InterruptedException {
        whenProposerMessageHasNoReplyRetryTillNAK(prepareThird);
    }

    @Test
    void testRetryTillNAKPrepareFourth() throws InterruptedException {
        whenProposerMessageHasNoReplyRetryTillNAK(prepareFourth);
    }

    @Test
    void testRetryTillNAKProposeSecond() throws InterruptedException {
        whenProposerMessageHasNoReplyRetryTillNAK(proposeSecond);
    }

    @Test
    void testRetryTillNAKProposeThird() throws InterruptedException {
        whenProposerMessageHasNoReplyRetryTillNAK(proposeThird);
    }

    @Test
    void testRetryTillNAKProposeFourth() throws InterruptedException {
        whenProposerMessageHasNoReplyRetryTillNAK(proposeFourth);
    }

    @Test
    void testPrepareSecondReceiveReplySecondWillNotResend() throws InterruptedException {
        whenProposerMessageHasReplyWillNotRetry(prepareSecond,
                replyPreparePromiseIDSecond);
    }

    @Test
    void testPrepareThirdReceiveReplyThirdWillNotResend() throws InterruptedException {
        whenProposerMessageHasReplyWillNotRetry(prepareThird,
                replyPrepareRejectIDThird);
    }

    @Test
    void testPrepareFourthReceiveReplyFourthWillNotResend() throws InterruptedException {
        whenProposerMessageHasReplyWillNotRetry(prepareFourth,
                replyPreparePromiseFourth);
    }

    @Test
    void testProposeSecondReceiveReplySecondWillNotResend() throws InterruptedException {
        whenProposerMessageHasReplyWillNotRetry(proposeSecond,
                replyProposeAcceptSecond);
    }

    @Test
    void testProposeThirdReceiveReplyThirdWillNotResend() throws InterruptedException {
        whenProposerMessageHasReplyWillNotRetry(proposeThird,
                replyProposeAcceptThird);
    }

    @Test
    void testProposeFourthReceiveReplyFourthWillNotResend() throws InterruptedException {
        whenProposerMessageHasReplyWillNotRetry(proposeFourth,
                replyProposeRejectFourth);
    }

    @Test
    void testPrepareTwoReceiveReplyThreeWillResend() throws InterruptedException {
        whenProposerMessageHasWrongReplyRetryTillNAK(prepareSecond,
                replyPrepareRejectIDThird);
    }

    @Test
    void testProposeThirdReceiveReplyFourthWillResend() throws InterruptedException {
        whenProposerMessageHasWrongReplyRetryTillNAK(proposeThird,
                replyProposeAcceptThird);
    }
}

class OneSenderFourReceiversCommServiceTest extends CommServiceFixture {
    void whenBroadCastReceiveInsufficientReplyWillRetryTillNAK(
            Message broadcastMessage,
            List<Message> replies) throws InterruptedException {
        int maxAttempt = 3;
        commService.setMAX_ATTEMPT(maxAttempt);
        commService.setTIME_OUT(200);
        // Number of expected messages sender will receive
        int expMsgToSender = 0;
        // Prepare broadcast and reply messages
        broadcastMessage.timestamp = timestamp;
        // Establish receivers ID and connections
        List<Integer> repliers = new ArrayList<>();
        List<MockConnection> replyConns = new ArrayList<>();
        for (Message reply : replies) {
            repliers.add(reply.from);
            if (reply.from != sender) {
                replyConns.add((MockConnection) commService.getRegistry().get(reply.from));
            }else
                expMsgToSender++;
        }

        // Establish non-receivers ID and connections
        List<Integer> nonRepliers = new ArrayList<>();
        List<MockConnection> nonReplyConns = new ArrayList<>();
        for (Map.Entry<Integer, AsyncClientConnection> entry :
                commService.getRegistry().entrySet()) {
            int id = entry.getKey();
            MockConnection conn = (MockConnection) entry.getValue();
            if (!repliers.contains(id)) {
                if (id != sender ){
                    nonRepliers.add(id);
                    nonReplyConns.add(conn);
                    expMsgToSender++;
                }else{
                    expMsgToSender += 2 + maxAttempt; // 1st message + retry + nak
                }

            }
        }

        // Prepare NAKs
        List<Message> nakMessages = new ArrayList<>();
        for (int from : nonRepliers) {
            Message directedMessage = new Message(broadcastMessage);
            directedMessage.to = from;
            nakMessages.add(Message.getNakMessage(directedMessage));
        }
        // Broadcast Message
        commService.broadcast(broadcastMessage, true);
        // Receive replies
        for (Message reply : replies)
            commService.receive(reply);
        // Sleep to ensure all messages are sent
        Thread.sleep(1000);

        // Each non reply connection should receive the first msg + retries
        for (MockConnection receiverConn : nonReplyConns) {
            assertEquals(maxAttempt + 1, receiverConn.sentMessages.size());
            for (Message msg : receiverConn.sentMessages) {
                Message directedMessage = new Message(broadcastMessage);
                directedMessage.to = msg.to;
                assertEquals(directedMessage.toString(), msg.toString());
            }
        }

        // Each reply connection should receive less than the first msg + retries
        for (MockConnection receiverConn : replyConns) {
            assertTrue(maxAttempt + 1 > receiverConn.sentMessages.size());
            for (Message msg : receiverConn.sentMessages) {
                Message directedMessage = new Message(broadcastMessage);
                directedMessage.to = msg.to;
                assertEquals(directedMessage.toString(), msg.toString());
            }
        }

        // A NAK is sent when no message received
        MockConnection senderConn =
                (MockConnection) commService.getRegistry().get(sender);
        assertEquals(expMsgToSender, senderConn.messageCount);
        // Check that the retryQueue is empty
        assertTrue(commService.getRetryQueue().isEmpty());
    }

    void whenBroadCastReceiveFullReplyWillNotRetry(Message broadcastMessage,
                                                   List<Message> replies) throws InterruptedException {
        broadcastMessage.timestamp = timestamp;
        int maxAttempt = 2;
        commService.setTIME_OUT(100);
        commService.setMAX_ATTEMPT(maxAttempt);
        // Send proposerMessage to receiver
        commService.broadcast(broadcastMessage, true);
        for (Message reply : replies)
            commService.receive(reply);
        Thread.sleep(300);
        // Get connections
        List<MockConnection> receiverConns = new ArrayList<>();
        for (int receiver : commList) {
            if (receiver != sender) {
                receiverConns.add((MockConnection) commService.getRegistry().get(receiver));
            }
        }
        MockConnection senderConn =
                (MockConnection) commService.getRegistry().get(sender);
        // Since reply message is received, no message is resent and no nak is received
        for (MockConnection receiverConn : receiverConns)
            assertEquals(1, receiverConn.messageCount);
        // Receive only the prepare message
        assertEquals(1, senderConn.messageCount);
        assertTrue(commService.getRetryQueue().isEmpty());
    }

    @Test
    void testPrepareReceiveOneReplyWillResend() throws InterruptedException {
        whenBroadCastReceiveInsufficientReplyWillRetryTillNAK(prepareBroadcast,
                Collections.singletonList(replyPrepareRejectIDThird));
    }

    @Test
    void testPrepareReceiveTwoRepliesWillResend() throws InterruptedException {
        whenBroadCastReceiveInsufficientReplyWillRetryTillNAK(prepareBroadcast,
                Arrays.asList(replyPrepareRejectIDThird, replyPreparePromiseIDSecond));
    }

    @Test
    void testPrepareReceiveThreeRepliesContainingSenderWillResend() throws InterruptedException {
        whenBroadCastReceiveInsufficientReplyWillRetryTillNAK(prepareBroadcast,
                Arrays.asList(replyPrepareRejectIDThird, replyPreparePromiseIDSecond, replyPreparePromiseFirst));
    }

    @Test
    void testPrepareReceiveThreeRepliesNotContainingSenderWillResend() throws InterruptedException {
        whenBroadCastReceiveInsufficientReplyWillRetryTillNAK(prepareBroadcast,
                Arrays.asList(replyPrepareRejectIDThird, replyPreparePromiseIDSecond, replyPreparePromiseFourth));
    }

    @Test
    void testPrepareReceiveAllRepliesWillNotResend() throws InterruptedException {
        whenBroadCastReceiveFullReplyWillNotRetry(prepareBroadcast,
                Arrays.asList(replyPrepareRejectIDThird, replyPreparePromiseIDSecond,
                        replyPreparePromiseFourth, replyPreparePromiseFirst));
    }


    @Test
    void testProposeReceiveOneReplyWillResend() throws InterruptedException {
        whenBroadCastReceiveInsufficientReplyWillRetryTillNAK(proposeBroadcast,
                Collections.singletonList(replyProposeAcceptThird));
    }

    @Test
    void testProposeReceiveTwoRepliesWillResend() throws InterruptedException {
        whenBroadCastReceiveInsufficientReplyWillRetryTillNAK(proposeBroadcast,
                Arrays.asList(replyProposeRejectFourth, replyProposeAcceptSecond));
    }

    @Test
    void testProposeReceiveThreeRepliesContainingSenderWillResend() throws InterruptedException {
        whenBroadCastReceiveInsufficientReplyWillRetryTillNAK(proposeBroadcast,
                Arrays.asList(replyProposeAcceptFirst, replyProposeAcceptSecond, replyProposeAcceptThird));
    }

    @Test
    void testProposeReceiveThreeRepliesNotContainingSenderWillResend() throws InterruptedException {
        whenBroadCastReceiveInsufficientReplyWillRetryTillNAK(proposeBroadcast,
                Arrays.asList(replyProposeRejectFourth, replyProposeAcceptSecond, replyProposeAcceptThird));
    }

    @Test
    void testProposeReceiveAllRepliesWillNotResend() throws InterruptedException {
        whenBroadCastReceiveFullReplyWillNotRetry(proposeBroadcast,
                Arrays.asList(replyProposeAcceptSecond, replyProposeAcceptThird,
                        replyProposeRejectFourth, replyProposeAcceptFirst));
    }
}

