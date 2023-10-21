package utils.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.helpers.AsyncMessageParser;

import static org.junit.jupiter.api.Assertions.*;

class AsyncMessageParserTest {

    AsyncMessageParser parser;

    @BeforeEach
    void setup(){
        parser = new AsyncMessageParser();
    }
    @Test
    void appendStringEndsWithDelimiterOneTest() {
        String[] output = parser.append("0:0:PREPARE:0:0;");
        assertEquals(output.length, 1);
        assertEquals(output[0], "0:0:PREPARE:0:0");

        output = parser.append("0:0:PREPARE:0:1;");
        assertEquals(output.length, 1);
        assertEquals(output[0], "0:0:PREPARE:0:1");
    }

    @Test
    void appendStringEndsWithDelimiterMultipleTest() {
        String[] output = parser.append("0:0:PREPARE:0:0;0:0:PREPARE:0:1;");
        assertEquals(output.length, 2);
        assertEquals(output[0], "0:0:PREPARE:0:0");
        assertEquals(output[1], "0:0:PREPARE:0:1");

        output = parser.append("0:0:PREPARE:0:2;");
        assertEquals(output.length, 1);
        assertEquals(output[0], "0:0:PREPARE:0:2");
    }

    @Test
    void appendStringDoesNotEndsWithDelimiterMultipleTest() {
        String[] output = parser.append("0:0:PREPARE:0:0;0:0:PREPARE:0:1;0:0:PREPARE:0:");
        assertEquals(output.length, 2);
        assertEquals(output[0], "0:0:PREPARE:0:0");
        assertEquals(output[1], "0:0:PREPARE:0:1");

        output = parser.append("2;");
        assertEquals(output.length, 1);
        assertEquals(output[0], "0:0:PREPARE:0:2");
    }

    @Test
    void appendStringDoesNotEndsWithDelimiterHasNewLineMultipleTest() {
        String[] output = parser.append("0:0:PREPARE:0:0\n;0:0:PREPARE:0:1;0:0:PREPARE:0:");
        assertEquals(output.length, 2);
        assertEquals(output[0], "0:0:PREPARE:0:0");
        assertEquals(output[1], "0:0:PREPARE:0:1");

        output = parser.append("2;");
        assertEquals(output.length, 1);
        assertEquals(output[0], "0:0:PREPARE:0:2");
    }
}