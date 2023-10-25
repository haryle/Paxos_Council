package utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.helpers.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsyncClientHandlerImplTest extends CommServiceFixture {

    protected AsyncClientHandlerImpl server;

    protected AtomicInteger clock;

    @Override
    @BeforeEach
    void setUp() {
        timestamp = 0;
        super.setUp();
        clock = new AtomicInteger(0);
        server = new AsyncClientHandlerImpl(null, clock, commService, null, null, null);
    }

    @Override
    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        super.tearDown();
        server.close();
    }

    void whenBroadCastReceiveInsufficientReplyWillRetryTillNAK(
            Message broadcastMessage,
            List<Message> replies) throws InterruptedException, IOException {
        int maxAttempt = 3;
        commService.setMAX_ATTEMPT(maxAttempt);
        commService.setTIME_OUT(200);
        // Expected messages to receive
        int expectedNum = 0;
        // Prepare broadcast and reply messages
        broadcastMessage.timestamp = timestamp;
        expectedNum++; // Inform message
        // Establish receivers ID and connections
        List<Integer> repliers = new ArrayList<>();
        List<MockConnection> replyConns = new ArrayList<>();
        for (Message reply : replies) {
            repliers.add(reply.from);
            if (reply.from != sender)
                replyConns.add((MockConnection) commService.getRegistry().get(reply.from));
        }
        // Establish non-receivers ID and connections
        List<Integer> nonRepliers = new ArrayList<>();
        List<MockConnection> nonReplyConns = new ArrayList<>();
        for (Map.Entry<Integer, AsyncClientConnection> entry :
                commService.getRegistry().entrySet()) {
            int id = entry.getKey();
            MockConnection conn = (MockConnection) entry.getValue();
            if (!repliers.contains(id) && id != sender) {
                nonRepliers.add(id);
                nonReplyConns.add(conn);
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
        server.handleMessage(broadcastMessage);
        // Receive replies
        for (Message reply : replies)
            server.handleMessage(reply);
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
        // Create a string list of messages that broadcaster has received
        List<String> senderSentMessageString = new ArrayList<>();
        for (Message message : senderConn.sentMessages) {
            senderSentMessageString.add(message.toString());
        }
        // Check that the broadcaster receives an inform message
        informSender.timestamp = -1;
        assertTrue(senderSentMessageString.contains(informSender.toString()));
        // Check that the broadcaster receives a NAK for each timeout message
        for (Message nakMessage : nakMessages)
            assertTrue(senderSentMessageString.contains(nakMessage.toString()));
        // Check that the broadcaster receives a relay message for each reply
        for (Message reply : replies)
            assertTrue(senderSentMessageString.contains(reply.toString()));
        // Check that the retryQueue is empty
        assertTrue(commService.getRetryQueue().isEmpty());
    }

    void whenBroadCastReceiveFullReplyWillNotRetry(Message broadcastMessage,
                                                   List<Message> replies) throws InterruptedException, IOException {
        broadcastMessage.timestamp = timestamp;
        int maxAttempt = 2;
        commService.setTIME_OUT(100);
        commService.setMAX_ATTEMPT(maxAttempt);
        // Send proposerMessage to receiver
        server.handleMessage(broadcastMessage);
        for (Message reply : replies)
            server.handleMessage(reply);
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
        // Sender should receive an inform message and all replies
        // Create a list of received messages in string
        List<String> senderSentMessageString = new ArrayList<>();
        for (Message message : senderConn.sentMessages) {
            senderSentMessageString.add(message.toString());
        }
        // Check that the broadcaster receives an inform message
        informSender.timestamp = -1;
        assertTrue(senderSentMessageString.contains(informSender.toString()));
        // Check that the broadcaster receives a relay message for each reply
        for (Message reply : replies)
            assertTrue(senderSentMessageString.contains(reply.toString()));
        // Check that the retryQueue is empty
        assertTrue(commService.getRetryQueue().isEmpty());
    }


    @Test
    void testWhenBroadcastPrepareReceiveTwoPrepareResponseWillResendMessage() throws InterruptedException, IOException {
        whenBroadCastReceiveInsufficientReplyWillRetryTillNAK(prepareBroadcast,
                new ArrayList<>(Arrays.asList(replyPrepareRejectIDThird,
                        replyPreparePromiseIDSecond)));
    }

    @Test
    void testWhenBroadcastPrepareReceiveThreePrepareResponseWithSenderWillResendMessage() throws InterruptedException, IOException {
        whenBroadCastReceiveInsufficientReplyWillRetryTillNAK(prepareBroadcast,
                new ArrayList<>(Arrays.asList(replyPreparePromiseFirst,
                        replyPrepareRejectIDThird, replyPreparePromiseIDSecond)));
    }

    @Test
    void testWhenBroadcastPrepareReceiveThreePrepareResponseWithoutSenderWillResendMessage() throws InterruptedException, IOException {
        whenBroadCastReceiveInsufficientReplyWillRetryTillNAK(prepareBroadcast,
                new ArrayList<>(Arrays.asList(replyPreparePromiseFourth,
                        replyPrepareRejectIDThird, replyPreparePromiseIDSecond)));
    }

    @Test
    void testWhenBroadCastPrepareReceiveAllPrepareResponseWillResendMessage() throws InterruptedException, IOException {
        whenBroadCastReceiveFullReplyWillNotRetry(prepareBroadcast,
                new ArrayList<>(Arrays.asList(replyPreparePromiseFirst,
                        replyPrepareRejectIDThird, replyPreparePromiseIDSecond,
                        replyPreparePromiseFourth)));
    }

}