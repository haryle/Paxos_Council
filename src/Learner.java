import utils.helpers.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Learner class: learn the accepted message and stop the election process if consensus is reached
 */
public class Learner {
    public Map<Integer, Integer> acceptEntries;

    public Learner() {
        acceptEntries = new ConcurrentHashMap<>();
    }

    public void registerAcceptor(int acceptorID) {
        if (!acceptEntries.containsKey(acceptorID))
            acceptEntries.put(acceptorID, -1);
    }

    public void handleAcceptMessage(Message message) {
        int sender = message.from;
        int value = message.acceptValue;
        acceptEntries.put(sender, value);
    }

}
