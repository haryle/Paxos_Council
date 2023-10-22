package utils;

import utils.helpers.Message;

public class Acceptor {
    private int maxID;
    private int acceptedID;
    private int acceptedValue;
    private boolean hasAccepted;

    public Acceptor() {
        maxID = 0;
        acceptedID = 0;
        acceptedValue = 0;
        hasAccepted = false;
    }

    public Message handleMessage(Message message) {
        if (message.type.equalsIgnoreCase("PREPARE"))
            return handlePrepare(message);
        if (message.type.equalsIgnoreCase("PROPOSE"))
            return handlePropose(message);
        return null;
    }

    private synchronized Message handlePrepare(Message message) {
        if (message.ID <= maxID)
            return Message.getNakMessage(message);
        maxID = message.ID;
        if (hasAccepted)
            return Message.promise(message.to, message.from, message.ID,
                    acceptedID, acceptedValue, message.timestamp);
        return Message.promise(message.to, message.from, message.ID, message.timestamp);
    }

    private synchronized Message handlePropose(Message message) {
        if (message.ID == maxID) {
            hasAccepted = true;
            acceptedID = message.ID;
            acceptedValue = message.acceptValue;
            return Message.accept(message.to, message.from, acceptedID, acceptedValue
                    , message.timestamp);
        }
        return Message.getNakMessage(message);
    }
}
