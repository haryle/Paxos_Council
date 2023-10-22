package utils;

import utils.helpers.Message;

import java.util.*;

public class Proposer {
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
    }

    private void handleInformMessage(Message message) {
        acceptorList = message.informList;
    }

    private void handlePrepareResponse(Message message) {
        int sender = message.from;
        acceptorResponse.put(sender, message);
        if (acceptorList.contains(sender))
            acceptorList.remove(Integer.valueOf(sender));
    }

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
            return Message.prepare(councillorID, 0, ID);
        } else {
            // Check if any response contains a previously proposedValue
            int proposeValue = councillorID;
            if (value != 0)
                proposeValue = value;
            // Sleep between wait min and wait max
            Thread.sleep(randomizer.nextInt(waitMax - waitMin + 1) + waitMin);
            return Message.propose(councillorID, 0, ID, proposeValue);
        }
    }

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
        }
        return null;
    }

}
