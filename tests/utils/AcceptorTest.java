package utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.helpers.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    public Message getFinalMessage(Message[] messages){
        Message response = null;
        for (Message message: messages)
            response = acceptor.handleMessage(message);
        return response;
    }

    public void testMessageSequence(Message[] messages, Message expected){
        Message response = getFinalMessage(messages);
        assertEquals(expected.toString(), response.toString());
    }

    @Test
    void testNoAcceptReceiveLowPrepareReturnPromiseWithNoAccept() {
        testMessageSequence(new Message[]{prepareLowID}, promiseNoAcceptLowID);
    }

    @Test
    void testNoAcceptReceiveHighPrepareReturnPromiseWithNoAccept(){
        testMessageSequence(new Message[]{prepareHighID}, promiseNoAcceptHighID);
    }

    @Test
    void testAcceptLowPrepareThenAcceptLowPropose(){
        testMessageSequence(new Message[]{prepareLowID, proposeLowID}, acceptLowValue);
    }

    @Test
    void testAcceptHighPrepareThenAcceptHighValue(){
        testMessageSequence(new Message[]{prepareHighID, proposeHighID}, acceptHighValue);
    }

    @Test
    void testPromiseLowThenPromiseHigh(){
        testMessageSequence(new Message[]{prepareLowID, prepareHighID}, promiseNoAcceptHighID);
    }

    @Test
    void testPromiseHighThenRejectLowPrepare(){
        testMessageSequence(new Message[]{prepareHighID, prepareLowID}, rejectLowPrepare);
    }

    @Test
    void testPromiseLowPrepareCannotAcceptHighPropose(){
        testMessageSequence(new Message[]{prepareLowID, proposeHighID}, rejectHighPropose);
    }

    @Test
    void testPromiseHighPrepareCannotAcceptLowPropose(){
        testMessageSequence(new Message[]{prepareHighID, proposeLowID}, rejectLowPropose);
    }

    @Test
    void testAcceptLowCanPromiseHigh(){
        testMessageSequence(new Message[]{prepareLowID, proposeLowID, prepareHighID}, promiseLowIDAcceptHighID);
    }

    @Test
    void testAcceptLowCannotAcceptHighProposeIfNotPrepared(){
        testMessageSequence(new Message[]{prepareLowID, proposeLowID, proposeHighID}, rejectHighPropose);
    }

    @Test
    void testAcceptHighWillRejectLowPrepare(){
        testMessageSequence(new Message[]{prepareHighID, proposeHighID, prepareLowID}, rejectLowPrepare);
    }

    @Test
    void testAcceptHighWillRejectLowPropose(){
        testMessageSequence(new Message[]{prepareHighID, proposeHighID, proposeLowID}, rejectLowPropose);
    }

    @Test
    void testAcceptLowCanAcceptHighWithPrepareProposeSequence(){
        testMessageSequence(new Message[]{prepareLowID, proposeLowID, prepareHighID, proposeHighID}, acceptHighValue);
    }

    @Test
    void testAcceptHighWillRejectLowPrepareAndPropose(){
        testMessageSequence(new Message[]{prepareHighID, proposeHighID, prepareLowID, proposeLowID}, rejectLowPropose);
    }

}