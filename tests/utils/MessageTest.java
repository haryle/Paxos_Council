package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void fromString() {
        String testString = "0:1:PROPOSE:0:1";
        Message message = Message.fromString(testString);
        assertEquals(message.from, 0);
        assertEquals(message.to, 1);
        assertEquals(message.type, "PROPOSE");
        assertEquals(message.id, 0);
        assertEquals(message.value, 1);
    }

    @Test
    void testToString() {
        Message message = new Message(0, 1, "PROPOSE", 0, 1);
        assertEquals(message.toString(), "0:1:PROPOSE:0:1;");
    }
}