import org.javatuples.Pair;

public class Acceptor {
    private boolean hasAccepted;            // Whether the acceptor has made any promise
    private Pair<Integer, Integer> timestamp; // Accepted/Promised timestamp

    private int value; // Accepted Value

    public Acceptor() {
        hasAccepted = false;
        timestamp = Pair.with(0, 0);
        value = 0;
    }

    /**
     * Acceptor handling when the received number is greater than any promised number.
     * <p>
     * If it has accepted a value, returns the number and accepted value
     * <p>
     * If it has not accepted a value, returns the proposed number and null
     *
     * @param proposedTS - proposed number from proposer
     * @return - (acceptedNumber, acceptedValue) or (proposalNumber, NULL)
     */
    private String prepareOk(Pair<Integer, Integer> proposedTS) {
        String retVal = "prepare-ok:";
        retVal = hasAccepted ? retVal + timestamp + ":" + value :
                retVal + proposedTS + ":" + "NULL";
        timestamp = proposedTS;
        hasAccepted = false; // Old value obsolete
        return retVal;
    }

    /**
     * Send negative acknowledgement for the following cases:
     * <p>
     * In prepare/promise phase, if the proposal number is less than the accepted
     * number.
     * <p>
     * In accept phase, if the accept number is less than the accepted number
     *
     * @return "NAK"
     */
    private String prepareNAK() {
        return "NAK";
    }

    private String prepareAccept(Pair<Integer, Integer> proposedTS, int proposedValue) {
        this.timestamp = proposedTS;
        this.value = proposedValue;
        this.hasAccepted = true;
        return String.valueOf(proposedValue);
    }

    /**
     * Handle the case when a prepare message is sent from a proposer.
     * <p>
     * If the proposed number is greater than the previous promised number:
     * <p>
     * If it has accepted a value, returns the number and accepted value
     * <p>
     * If it has not accepted a value, returns the proposed number and null
     * Otherwise send NAK
     *
     * @param proposedTS proposed number/timestamp
     * @return acknowledgement/negative acknowledgement message
     */
    public String handlePrepare(Pair<Integer, Integer> proposedTS) {
        if (proposedTS.compareTo(timestamp) >= 0) // ProposedTS > promised TS
            return prepareOk(proposedTS);
        else
            return prepareNAK();
    }

    public String handleAccept(Pair<Integer, Integer> proposedTS, int proposalValue) {
        if (proposedTS.compareTo(timestamp) >= 0)
            return prepareAccept(proposedTS, proposalValue);
        else
            return prepareNAK();
    }
}
