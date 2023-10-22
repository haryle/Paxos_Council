//package utils;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import utils.helpers.Message;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class AsyncClientHandlerImplTest extends CommServiceFixture {
//
//    public Message informMessage;
//
//    protected AsyncClientHandlerImpl server;
//
//    protected AtomicInteger timestamp;
//
//    @Override
//    @BeforeEach
//    void setUp() {
//        super.setUp();
//        timestamp = new AtomicInteger(0);
//        server = new AsyncClientHandlerImpl(null, timestamp, commService);
//        prepareMessageToSecond.timestamp = 0;
//        nakMessageSecondToFirst.timestamp = 0;
//        replyPromiseWithoutAcceptID.timestamp = 0;
//        replyPromiseWithAcceptID.timestamp = 0;
//        replyRejectPropose.timestamp = 0;
//        ArrayList<Integer> arrayList = new ArrayList<>();
//        arrayList.add(0);
//        arrayList.add(1);
//        informMessage = Message.inform(0, 10, arrayList);
//    }
//
//    @Override
//    @AfterEach
//    void tearDown() throws IOException {
//        super.tearDown();
//        server.close();
//    }
//
//    @Test
//    void testHandleBroadcastMessageWithNoReplyWillResend() throws IOException, InterruptedException {
//        int maxAttempt = 5;
//        commService.setMAX_ATTEMPT(maxAttempt);
//        commService.setTIME_OUT(10);
//        server.handleMessage(broadcastMessage);
//        Thread.sleep(100);
//
//        // Number of sent messages include the first one
//        assertEquals(maxAttempt + 1, second.messageCount);
//        for (Message msg : second.sentMessages) {
//            assertEquals(prepareMessageToSecond.toString(), msg.toString());
//        }
//        // An inform message to send
//        // A NAK is sent when no message received f
//        assertEquals(2, first.messageCount);
//        // Get a list of receivers
//        assertEquals(informMessage.toString(), first.sentMessages.get(0).toString());
//        assertEquals(nakMessageSecondToFirst.toString(),
//                first.sentMessages.get(1).toString());
//        // Check that the retryQueue is empty
//        assertTrue(commService.getRetryQueue().isEmpty());
//    }
//
//    void testHandleBroadcastMessageWithReplyWillNotResend(Message reply) throws IOException, InterruptedException {
//        int maxAttempt = 2;
//        commService.setTIME_OUT(100);
//        commService.setMAX_ATTEMPT(maxAttempt);
//        server.handleMessage(broadcastMessage);
//        server.handleMessage(reply);
//        Thread.sleep(300);
//        // Since reply message is received, no message is resent and no nak is received
//        assertEquals(1, second.messageCount);
//
//        // Only 2 messages, first is inform, second is the reply
//        assertEquals(2, first.messageCount);
//        // First message is inform
//        assertEquals(informMessage.toString(), first.sentMessages.get(0).toString());
//        // Second message is reply
//        assertEquals(reply.toString(), first.sentMessages.get(1).toString());
//        // Queue is empty
//        assertTrue(commService.getRetryQueue().isEmpty());
//    }
//
//    @Test
//    void testWhenBroadcastRelayReplyNoID() throws IOException, InterruptedException{
//        testHandleBroadcastMessageWithReplyWillNotResend(replyPromiseWithoutAcceptID);
//    }
//
//    @Test
//    void testWhenBroadcastRelayReplyWithID() throws IOException, InterruptedException{
//        testHandleBroadcastMessageWithReplyWillNotResend(replyPromiseWithAcceptID);
//    }
//
//    @Test
//    void testWhenBroadcastRelayReplyReject() throws IOException, InterruptedException{
//        testHandleBroadcastMessageWithReplyWillNotResend(replyRejectPropose);
//    }
//
//}