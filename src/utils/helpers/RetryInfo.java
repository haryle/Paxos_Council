package utils.helpers;

public class RetryInfo {
    public final Message message;
    private int attempt;

    public RetryInfo(Message message) {
        this.message = message;
        attempt = 0;
    }

    public synchronized int getAttempt() {
        return attempt;
    }

    public synchronized void incAttempt() {
        this.attempt++;
    }

}
