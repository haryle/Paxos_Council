import utils.helpers.Message;

import java.util.logging.Logger;

/**
 * Service class representing a Paxos Acceptor.
 * <p>
 * Acceptor handles Proposer's message and return
 * corresponding replies. The public interface contains
 * just the handleMessage method, which returns a nullable String.
 */
public class Acceptor {
    protected final Logger logger = Logger.getLogger(this.getClass().getName());
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

    /**
     * Blanket catch all method to handle message
     * Only handles PREPARE or PROPOSE
     * <p>
     * When incoming message is PREPARE, handled by handlePrepare
     * When incoming message is PROPOSE, handled by handlePropose
     * <p>
     * Otherwise ignore
     *
     * @param message incoming proposer's message
     * @return reply to PREPARE and PROPOSE, can be PROMISE, NAK_PREPARE, ACCEPT,
     * NAK_PROPOSE
     */
    public Message handleMessage(Message message) {
        Message response = null;
        if (message.type.equalsIgnoreCase("PREPARE")) response = handlePrepare(message);
        if (message.type.equalsIgnoreCase("PROPOSE")) response = handlePropose(message);
        return response;
    }

    /**
     * Phase 1 logic for acceptor
     * <p>
     * If the message ID is greater than max ID and if
     * the acceptor has not accepted any value, reply with
     * promise without ID
     * <p>
     * If the message ID is greater than max ID and if the acceptor
     * has accepted a value, reply with promise with new value
     * <p>
     * Otherwise send a NAK message
     *
     * @param message prepare message from a proposer
     * @return promise message or nak
     */
    private synchronized Message handlePrepare(Message message) {
        logger.info(String.format("Receive PREPARE - sender: %d, ID: %d", message.from,
                message.ID));
        if (message.ID <= maxID) {
            logger.info(String.format("Send NAK - sender: %d, ID: %d", message.from,
                    message.ID));
            return Message.getNakMessage(message);
        }
        maxID = message.ID;
        if (hasAccepted) {
            logger.info(String.format("Send PROMISE - sender: %d, ID: %d, acceptedID: %d, "
                                      + "acceptedValue: %d", message.from, message.ID
                    ,acceptedID, acceptedValue));
            return Message.promise(message.to, message.from, message.ID, acceptedID,
                    acceptedValue, message.timestamp);
        }
        logger.info(String.format("Send PROMISE - sender: %d, ID: %d", message.from,
                message.ID));
        return Message.promise(message.to, message.from, message.ID, message.timestamp);
    }

    /**
     * Phase 2 logic for acceptor
     * <p>
     * If the proposer message's ID is not the currently registered max_id, send a
     * nak.
     * <p>
     * If the proposer message's ID is the currently registered max_id, accept the
     * proposed value and send an accept message
     *
     * @param message propose message
     * @return accept or nak message
     */
    private synchronized Message handlePropose(Message message) {
        logger.info(String.format("Receive %s - sender: %d, ID: %d, value: %d", message.type, message.from, message.ID, message.acceptValue));
        if (message.ID == maxID) {
            hasAccepted = true;
            acceptedID = message.ID;
            acceptedValue = message.acceptValue;
            logger.info(String.format("Send ACCEPT - sender: %d, ID: %d, value: %d", message.from,
                    message.ID, message.acceptValue));
            return Message.accept(message.to, message.from, acceptedID, acceptedValue
                    , message.timestamp);
        }
        logger.info(String.format("Send NAK - sender: %d, ID: %d, value: %d", message.from,
                message.ID, message.acceptValue));
        return Message.getNakMessage(message);
    }
}
