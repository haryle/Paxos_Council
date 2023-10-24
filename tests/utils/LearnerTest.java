package utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Learner;
import utils.helpers.Message;

import static org.junit.jupiter.api.Assertions.*;

class LearnerTest {

    Learner learner;

    Message firstAcceptFirst = Message.accept(1, 1, 0, 1, -1);
    Message firstAcceptSecond = Message.accept(1, 1, 0, 2, -1);
    Message firstAcceptThird = Message.accept(1, 1, 0, 3, -1);

    Message secondAcceptFirst = Message.accept(2, 1, 0, 1, -1);
    Message secondAcceptSecond = Message.accept(2, 1, 0, 2, -1);
    Message secondAcceptThird = Message.accept(2,1,0,3,-1);

    Message thirdAcceptFirst = Message.accept(3, 1, 0, 1, -1);
    Message thirdAcceptSecond = Message.accept(3, 1, 0, 2, -1);
    Message thirdAcceptThird = Message.accept(3,1,0,3,-1);



    @BeforeEach
    void setUp() {
        learner = new Learner();
        learner.registerAcceptor(1);
        learner.registerAcceptor(2);
        learner.registerAcceptor(3);
    }

    void testWhenGivenOneAcceptDoesNothing(Message message){
        Message reply=learner.handleAcceptMessage(message);
        assertNull(reply);
        assertTrue(learner.acceptEntries.containsKey(message.from));
        assertEquals((int) learner.acceptEntries.get(message.from), message.acceptValue);
    }

    void testWhenGivenTwoFormsMajoritySendsShutDown(Message first, Message second){
        learner.handleAcceptMessage(first);
        Message reply = learner.handleAcceptMessage(second);
        assertTrue(reply.type.equalsIgnoreCase("SHUTDOWN"));
        assertEquals(reply.acceptValue, second.acceptValue);
    }

    void testWhenGivenTwoNonMajoritySendsNull(Message first, Message second){
        learner.handleAcceptMessage(first);
        Message reply = learner.handleAcceptMessage(second);
        assertNull(reply);
        assertEquals((int) learner.acceptEntries.get(first.from),first.acceptValue);
        assertEquals((int) learner.acceptEntries.get(second.from),second.acceptValue);
    }

    void testWhenGivenThreeFormsMajoritySendsShutDown(Message first, Message second, Message third){
        learner.handleAcceptMessage(first);
        learner.handleAcceptMessage(second);
        Message reply = learner.handleAcceptMessage(third);
        assertTrue(reply.type.equalsIgnoreCase("SHUTDOWN"));
        assertEquals(reply.acceptValue, second.acceptValue);
    }

    @Test
    void testFirstAcceptsFirstDoNothing(){
        testWhenGivenOneAcceptDoesNothing(firstAcceptFirst);
    }

    @Test
    void testTwoAcceptForFirstSendShutdown(){
        testWhenGivenTwoFormsMajoritySendsShutDown(firstAcceptFirst, secondAcceptFirst);
    }

    @Test
    void testTwoAcceptForSecondSendShutdown(){
        testWhenGivenTwoFormsMajoritySendsShutDown(firstAcceptSecond, thirdAcceptSecond);
    }

    @Test
    void testTwoAcceptForFirstSecondSendNull(){
        testWhenGivenTwoNonMajoritySendsNull(firstAcceptFirst, secondAcceptSecond);
    }

    @Test
    void testTwoAcceptForFirstThirdSendNull(){
        testWhenGivenTwoNonMajoritySendsNull(thirdAcceptFirst, secondAcceptThird);
    }

    @Test
    void testThreeAcceptSendShutdown(){
        testWhenGivenThreeFormsMajoritySendsShutDown(firstAcceptFirst, secondAcceptSecond, thirdAcceptSecond);
    }
}