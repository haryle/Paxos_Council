package utils;

public class Message {
    public static final String delimiter = ";";
    public static final String separator = ":";
    public int from;
    public int to;
    public int id;
    public int value;
    public String type;

    public Message(int from,
                   int to,
                   String type,
                   int id,
                   int value) {
        this.from = from;
        this.to = to;
        this.type = type;
        this.id = id;
        this.value = value;
    }

    public static Message fromString(String message) {
        String[] components = message.split(Message.separator);
        return new Message(
                Integer.parseInt(components[0]),
                Integer.parseInt(components[1]),
                components[2],
                Integer.parseInt(components[3]),
                Integer.parseInt(components[4]));
    }

    public String toString() {
        return from +
               separator +
               to +
               separator +
               type +
               separator +
               id +
               separator +
               value +
               delimiter;
    }
}
