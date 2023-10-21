package utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.helpers.Message;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommServiceTest {
    Message broadcastMessage = Message.prepare(0, 0, 10);
    Message prepareMessageToSecond = Message.prepare(0, 1, 10);
    Message nakMessageSecondToFirst = Message.getNakMessage(prepareMessageToSecond);
    Message replyMessageSecondToFirst = Message.promise(prepareMessageToSecond.to,
            prepareMessageToSecond.from,
            prepareMessageToSecond.ID, prepareMessageToSecond.timestamp);
    private MockConnection first;
    private MockConnection second;
    private CommService commService;

    @BeforeEach
    void setUp() {
        commService = new CommService(5, 5000, new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>(),
                Executors.newSingleThreadScheduledExecutor());
        first = new MockConnection(null);
        second = new MockConnection(null);
        commService.registerConnection(0, first);
        commService.registerConnection(1, second);
    }

    @AfterEach
    void tearDown() {
        commService.close();
    }


    @Test
    void testWhenReplyNotReceivedMessageIsResent() throws InterruptedException {
        // Prepare fixture
        int maxAttempt = 5;
        commService.setMAX_ATTEMPT(maxAttempt);
        commService.setTIME_OUT(10);
        commService.send(prepareMessageToSecond.to, prepareMessageToSecond, true);
        Thread.sleep(100);

        // Number of sent messages include the first one
        assertEquals(maxAttempt + 1, second.messageCount);
        for (Message msg : second.sentMessages) {
            assertEquals(prepareMessageToSecond.toString(), msg.toString());
        }
        // A NAK is sent when no message received f
        assertEquals(1, first.messageCount);
        assertEquals(nakMessageSecondToFirst.toString(),
                first.sentMessages.get(0).toString());
        // Check that the retryQueue is empty
        assertTrue(commService.getRetryQueue().isEmpty());
    }

    @Test
    void testWhenReplyReceivedBeforeTimeOutMessageIsNotResent() throws InterruptedException {
        int maxAttempt = 2;
        commService.setTIME_OUT(100);
        commService.setMAX_ATTEMPT(maxAttempt);
        commService.send(prepareMessageToSecond.to, prepareMessageToSecond, true);
        commService.receive(replyMessageSecondToFirst);
        Thread.sleep(300);
        // Since reply message is received, no message is resent and no nak is received
        assertEquals(1, second.messageCount);
        assertEquals(0, first.messageCount);
        assertTrue(commService.getRetryQueue().isEmpty());
    }

    @Test
    void testWhenBroadCastAndNoReplyMessagesResent() throws InterruptedException {
        // Prepare fixture
        int maxAttempt = 5;
        commService.setMAX_ATTEMPT(maxAttempt);
        commService.setTIME_OUT(10);
        commService.broadcast(broadcastMessage);
        Thread.sleep(100);

        // Number of sent messages include the first one
        assertEquals(maxAttempt + 1, second.messageCount);
        for (Message msg : second.sentMessages) {
            assertEquals(prepareMessageToSecond.toString(), msg.toString());
        }
        // A NAK is sent when no message received f
        assertEquals(1, first.messageCount);
        assertEquals(nakMessageSecondToFirst.toString(),
                first.sentMessages.get(0).toString());
        // Check that the retryQueue is empty
        assertTrue(commService.getRetryQueue().isEmpty());
    }

    @Test
    void testWhenBroadCastAndReplyReceivedNoResent() throws InterruptedException {
        int maxAttempt = 2;
        commService.setTIME_OUT(100);
        commService.setMAX_ATTEMPT(maxAttempt);
        commService.broadcast(broadcastMessage);
        commService.receive(replyMessageSecondToFirst);
        Thread.sleep(300);
        // Since reply message is received, no message is resent and no nak is received
        assertEquals(1, second.messageCount);
        assertEquals(0, first.messageCount);
        assertTrue(commService.getRetryQueue().isEmpty());
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