import utils.helpers.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Learner class: learn the accepted message and stop the election process if
 * consensus is reached
 */
public class Learner {
    protected final Logger logger = Logger.getLogger(this.getClass().getName());
    public Map<Integer, Integer> acceptEntries;

    public Learner() {
        acceptEntries = new ConcurrentHashMap<>();
    }

    public void registerAcceptor(int acceptorID) {
        if (!acceptEntries.containsKey(acceptorID)) {
            logger.fine("Registering acceptor: " + acceptorID);
            acceptEntries.put(acceptorID, -1);
        }
    }

    public synchronized Message handleAcceptMessage(Message message) {
        if (message.type.equalsIgnoreCase("ACCEPT")) {
            acceptEntries.put(message.from, message.acceptValue);
            return checkTermination();
        }
        return null;
    }

    private Message checkTermination() {
        logger.fine("Accepted Entries: " + acceptEntries);
        int quorumSize = acceptEntries.size();
        int acceptCount = 0;
        Map<Integer, Integer> counter = new HashMap<>();
        for (int acceptVal : acceptEntries.values()) {
            if (acceptVal != -1) {
                if (counter.containsKey(acceptVal))
                    counter.put(acceptVal, counter.get(acceptVal) + 1);
                else
                    counter.put(acceptVal, 1);
            }
        }
        int maxValue = 0;
        int maxCount = 0;
        for (Map.Entry<Integer, Integer> entry : counter.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxValue = entry.getKey();
            }
        }
        if (maxCount > quorumSize / 2) {
            logger.fine(String.format("Learn value: %d, count: %d", maxValue,
                    maxCount));
            return Message.shutdown(maxValue);
        }
        return null;
    }
}
