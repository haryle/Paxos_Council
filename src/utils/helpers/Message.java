package utils.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {
    public static final String delimiter = ";";
    public static final String separator = ":";
    public int from;
    public int to;
    public int ID;
    public int acceptID;
    public int acceptValue;
    public List<Integer> informList;
    public int timestamp = -1;
    public String type;

    public static List<Integer> StringToList(String stringList) {
        List<Integer> integerList = new ArrayList<>();
        Pattern pattern = Pattern.compile("-?\\d+"); // Regular expression to match integers

        Matcher matcher = pattern.matcher(stringList);

        // If the string contains integers
        while (matcher.find()) {
            int number = Integer.parseInt(matcher.group());
            integerList.add(number);
        }

        return integerList;
    }

    public Message(int from,
                   int to,
                   String type,
                   int ID,
                   int acceptID,
                   int acceptValue,
                   List<Integer> informList,
                   int timestamp) {
        this.from = from;
        this.to = to;
        this.type = type;
        this.ID = ID;
        this.acceptID = acceptID;
        this.acceptValue = acceptValue;
        this.informList = informList;
        this.timestamp = timestamp;
    }

    public Message(int from,
                   int to,
                   String type,
                   int ID,
                   int acceptID,
                   int acceptValue) {
        this(from, to, type, ID, acceptID, acceptValue, new ArrayList<>(), -1);
    }

    public Message(int from,
                   int to,
                   String type,
                   int ID,
                   int acceptID,
                   int acceptValue,
                   int timestamp) {
        this(from, to, type, ID, acceptID, acceptValue, new ArrayList<>(), timestamp);
    }

    public Message(Message message) {
        this(message.from, message.to, message.type, message.ID, message.acceptID,
                message.acceptValue, message.informList, message.timestamp);
    }


    public static Message inform(int to, List<Integer> informList) {
        return new Message(-1, to, "INFORM", -1, -1, -1, informList, -1);
    }

    public static Message connect(int from) {
        return new Message(from, -1, "CONNECT", -1, -1, -1, new ArrayList<>(), -1);
    }

    public static Message prepare(int from, int to, int ID) {
        return new Message(from, to, "PREPARE", ID, -1, -1);
    }

    public static Message propose(int from, int to, int ID, int value) {
        return new Message(from, to, "PROPOSE", ID, ID, value);
    }

    public static Message promise(int from, int to, int ID, int timestamp) {
        return new Message(from, to, "PROMISE", ID, -1, -1, timestamp);
    }

    public static Message promise(int from, int to, int ID, int acceptID,
                                  int acceptValue, int timestamp) {
        return new Message(from, to, "PROMISE", ID, acceptID, acceptValue, timestamp);
    }

    public static Message accept(int from, int to, int ID, int value, int timestamp) {
        return new Message(from, to, "ACCEPT", ID, ID, value, timestamp);
    }

    public static Message rejectPrepare(int from, int to, int ID, int timestamp) {
        return new Message(from, to, "NAK_PREPARE", ID, -1, -1, timestamp);
    }

    public static Message rejectPropose(int from, int to, int ID, int value,
                                        int timestamp) {
        return new Message(from, to, "NAK_PREPARE", ID, -1, value, timestamp);
    }

    public static Message getNakMessage(Message message) {
        if (message.type.equalsIgnoreCase("PREPARE"))
            return Message.rejectPrepare(message.to, message.from, message.ID,
                    message.timestamp);
        else if (message.type.equalsIgnoreCase("PROPOSE"))
            return Message.rejectPropose(message.to, message.from, message.ID,
                    message.acceptValue, message.timestamp);
        else
            return null;
    }

    public static Message fromString(String message) {
        String[] components = message.split(Message.separator);
        return new Message(
                Integer.parseInt(components[0]),
                Integer.parseInt(components[1]),
                components[2],
                Integer.parseInt(components[3]),
                Integer.parseInt(components[4]),
                Integer.parseInt(components[5]),
                StringToList(components[6]),
                Integer.parseInt(components[7]));
    }

    public String toString() {
        return from +
                separator +
                to +
                separator +
                type +
                separator +
                ID +
                separator +
                acceptID +
                separator +
                acceptValue +
                separator +
                informList +
                separator +
                timestamp +
                delimiter;
    }
}
