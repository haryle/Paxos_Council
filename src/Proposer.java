import utils.helpers.Message;

import java.util.*;
import java.util.logging.Logger;

/**
 * Service class representing a Paxos Proposer
 * <p>
 * Proposer generate propose or prepare messages to be sent to acceptors.
 * Proposer also handles replies for prepare messages
 */
public class Proposer {
    protected final Logger logger = Logger.getLogger(this.getClass().getName());

    public static final int MAX_PROPOSER = 10;
    public final int councillorID; // My CouncillorID
    Random randomizer = new Random();
    private int waitMin;
    private int waitMax;
    private int ID; // Current round ID
    private Map<Integer, Message> acceptorResponse; // Responses from acceptors
    private List<Integer> acceptorList; // List of acceptors to expect responses from

    public Proposer(int councillorID, int waitMin, int waitMax) {
        this.councillorID = councillorID;
        ID = councillorID;
        acceptorResponse = new HashMap<>();
        acceptorList = new ArrayList<>();
        acceptorList.add(councillorID); // Awaits answer from self
        this.waitMin = waitMin;
        this.waitMax = waitMax;
    }

    public int getID() {
        return ID;
    }

    public Map<Integer, Message> getAcceptorResponse() {
        return acceptorResponse;
    }

    public List<Integer> getAcceptorList() {
        return acceptorList;
    }

    public void setWaitMin(int waitMin) {
        this.waitMin = waitMin;
    }

    public void setWaitMax(int waitMax) {
        this.waitMax = waitMax;
    }

    public void nextRound() {
        ID += MAX_PROPOSER;
        acceptorResponse = new HashMap<>();
        acceptorList = new ArrayList<>();
        acceptorList.add(councillorID); // Awaits message from self
    }

    /**
     * Handle a reply from broadcasting server for each prepare/propose message.
     * <p>
     * Proposer registers the list of acceptors it expects responses from and only
     * proceeds when replies from all acceptors it expects are received.
     *
     * @param message inform message
     */
    private void handleInformMessage(Message message) {
        acceptorList.addAll(message.informList);
        logger.info("Expecting reply from: " + acceptorList);
    }

    /**
     * Handle replies for prepare messages.
     * <p>
     * Proposer add the response to the list of all responses, and drop the
     * sender's councillorID from the list of acceptors it is expecting replies from
     *
     * @param message PROMISE or NAK_PREPARE messages
     */
    private void handlePrepareResponse(Message message) {
        int sender = message.from;
        acceptorResponse.put(sender, message);
        if (acceptorList.contains(sender))
            acceptorList.remove(Integer.valueOf(sender));
    }

    /**
     * Propose a value at the end of phase 1
     * <p>
     * If the majority has replied (with promise messages), if any promise contained a previously accepted value,
     * choose the value with the highest accepted ID as the proposed value, otherwise choose the councillorID
     * as proposed value. Return a PROPOSE message with the proposed value.
     * <p>
     * If has not received reply from majority, restart phase 1 after some delays
     *
     * @return a PREPARE for a reset of phase 1 or PROPOSE for the beginning of phase 2
     * @throws InterruptedException if the sleeping thread is interrupted
     */
    private Message proposeValue() throws InterruptedException {
        int numMajority = acceptorResponse.size() / 2 + 1;
        int numResponse = 0;
        int maxID = 0;
        int value = 0;
        for (Message response : acceptorResponse.values()) {
            if (response.type.equalsIgnoreCase("PROMISE")) {
                numResponse += 1;
                if (response.acceptID > maxID) {
                    maxID = response.acceptID;
                    // Only remember value with the highest ID
                    value = response.acceptValue;
                }
            }
        }
        // If it does not receive from majority -> begin next round
        if (numResponse < numMajority) {
            nextRound();
            // Sleep between wait min and wait max
            Thread.sleep(randomizer.nextInt(waitMax - waitMin + 1) + waitMin);
            return prepare();
        } else {
            // Check if any response contains a previously proposedValue
            int proposedValue = councillorID;
            if (value != 0)
                proposedValue = value;
            return propose(proposedValue);
        }
    }

    /**
     * Generate a prepare message
     *
     * @return prepare message
     */
    public Message prepare() {
        return Message.prepare(councillorID, 0, ID);
    }

    /**
     * Generate a propose message with input value
     *
     * @param proposeValue proposed value
     * @return propose message
     */
    public Message propose(int proposeValue) {
        return Message.propose(councillorID, 0, ID, proposeValue);
    }

    /**
     * Public method for handling acceptor's responses,
     * <p>
     * If the message's ID is not the current round ID, ignore the message. If the message is INFORM,
     * handleInform. If the message is a PREPARE's response (PROMISE, NAK_PREPARE), handlePrepareResponse.
     * If all replies for the current round's PREPARE is received, invoke proposeValue. Any other type of
     * messages will be ignored
     *
     * @param message acceptor's response
     * @return null, PROPOSE or PREPARE
     * @throws InterruptedException if interrupted while sleeping.
     */
    public synchronized Message handleMessage(Message message) throws InterruptedException {
        if (message.ID != ID) // Ignore messages from a different round
            return null;
        if (message.type.equalsIgnoreCase("INFORM")) {
            handleInformMessage(message);
            return null;
        }
        if (message.type.equalsIgnoreCase("PROMISE") || message.type.equalsIgnoreCase("NAK_PREPARE")) {
            handlePrepareResponse(message);
            if (acceptorList.isEmpty())
                return proposeValue();
            else{
                logger.info("Expecting reply from: " + acceptorList);
            }
        }
        return null;
    }

}
