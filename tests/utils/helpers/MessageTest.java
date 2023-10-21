package utils.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.helpers.AsyncMessageParser;
import utils.helpers.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTest {
    AsyncMessageParser parser;

    @BeforeEach
    void setup() {
        parser = new AsyncMessageParser();
    }

    @Test
    void fromString() {
        String testString = "0:1:PROPOSE:10:0:1:1";
        Message message = Message.fromString(testString);
        assertEquals(message.from, 0);
        assertEquals(message.to, 1);
        assertEquals(message.type, "PROPOSE");
        assertEquals(message.ID, 10);
        assertEquals(message.acceptID, 0);
        assertEquals(message.acceptValue, 1);
        assertEquals(message.timestamp, 1);
    }

    @Test
    void testToString() {
        Message message = new Message(0, 1, "PROPOSE", 0, 1, 1, 1);
        assertEquals(message.toString(), "0:1:PROPOSE:0:1:1:1;");
    }

    @Test
    void testConnectMessage() {
        Message message = Message.connect(5);
        assertEquals(message.from, 5);
        assertEquals(message.type, "CONNECT");
        message = Message.fromString(parser.append(message.toString())[0]);
        assertEquals(message.from, 5);
        assertEquals(message.type, "CONNECT");
    }

    @Test
    void testPrepareMessage() {
        Message message = Message.prepare(1, 0, 10);
        assertEquals(message.from, 1);
        assertEquals(message.to, 0);
        assertEquals(message.type, "PREPARE");
        message = Message.fromString(parser.append(message.toString())[0]);
        assertEquals(message.from, 1);
        assertEquals(message.to, 0);
        assertEquals(message.type, "PREPARE");
    }

    @Test
    void testProposeMessage(){
        Message message = Message.propose(1, 0, 10, 5);
        assertEquals(message.from, 1);
        assertEquals(message.to, 0);
        assertEquals(message.type, "PROPOSE");
        assertEquals(message.acceptValue, 5);
        message = Message.fromString(parser.append(message.toString())[0]);
        assertEquals(message.from, 1);
        assertEquals(message.to, 0);
        assertEquals(message.type, "PROPOSE");
        assertEquals(message.acceptValue, 5);
    }

    @Test
    void testPromiseNoAcceptMessage(){
        Message message = Message.promise(5, 1, 10, 3);
        assertEquals(message.from, 5);
        assertEquals(message.to, 1);
        assertEquals(message.type, "PROMISE");
        assertEquals(message.acceptID, -1);
        assertEquals(message.acceptValue, -1);
        assertEquals(message.timestamp, 3);
        message = Message.fromString(parser.append(message.toString())[0]);
        assertEquals(message.from, 5);
        assertEquals(message.to, 1);
        assertEquals(message.type, "PROMISE");
        assertEquals(message.acceptID, -1);
        assertEquals(message.acceptValue, -1);
        assertEquals(message.timestamp, 3);
    }

    @Test
    void testPromiseAcceptMessage(){
        Message message = Message.promise(5, 1, 10, 5,15, 3);
        assertEquals(message.from, 5);
        assertEquals(message.to, 1);
        assertEquals(message.type, "PROMISE");
        assertEquals(message.acceptID, 5);
        assertEquals(message.acceptValue, 15);
        assertEquals(message.timestamp, 3);
        message = Message.fromString(parser.append(message.toString())[0]);
        assertEquals(message.from, 5);
        assertEquals(message.to, 1);
        assertEquals(message.type, "PROMISE");
        assertEquals(message.acceptID, 5);
        assertEquals(message.acceptValue, 15);
        assertEquals(message.timestamp, 3);
    }

    @Test
    void testAcceptMessage(){
        Message message = Message.accept(5, 1, 10, 5, 14);
        assertEquals(message.from, 5);
        assertEquals(message.to, 1);
        assertEquals(message.type, "ACCEPT");
        assertEquals(message.ID, 10);
        assertEquals(message.acceptValue, 5);
        assertEquals(message.timestamp, 14);
        message = Message.fromString(parser.append(message.toString())[0]);
        assertEquals(message.from, 5);
        assertEquals(message.to, 1);
        assertEquals(message.type, "ACCEPT");
        assertEquals(message.ID, 10);
        assertEquals(message.acceptValue, 5);
        assertEquals(message.timestamp, 14);
    }
}