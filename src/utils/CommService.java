package utils;

import org.javatuples.Pair;
import utils.helpers.Message;
import utils.helpers.RetryInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CommService {
    protected final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Map<Integer, AsyncClientConnection> registry;
    private final Map<Pair<Integer, Integer>, RetryInfo> retryQueue;
    private final ScheduledExecutorService scheduledThreadPool;
    private int MAX_ATTEMPT;
    private int TIME_OUT;
    public CommService(int maxAttempt, int timeOut, Map<Integer,
            AsyncClientConnection> registry,
                       Map<Pair<Integer, Integer>, RetryInfo> retryQueue,
                       ScheduledExecutorService scheduledThreadPool) {
        MAX_ATTEMPT = maxAttempt;
        TIME_OUT = timeOut;
        this.registry = registry;
        this.retryQueue = retryQueue;
        this.scheduledThreadPool = scheduledThreadPool;
    }

    public Map<Integer, AsyncClientConnection> getRegistry() {
        return registry;
    }

    public void setMAX_ATTEMPT(int MAX_ATTEMPT) {
        this.MAX_ATTEMPT = MAX_ATTEMPT;
    }

    public void setTIME_OUT(int TIME_OUT) {
        this.TIME_OUT = TIME_OUT;
    }

    public Map<Pair<Integer, Integer>, RetryInfo> getRetryQueue() {
        return retryQueue;
    }

    /**
     * Add a connection to connection registry
     *
     * @param id         connection id
     * @param connection connection class
     */
    public void registerConnection(int id, AsyncClientConnection connection) {
        registry.put(id, connection);
    }

    /**
     * Receive a message and remove it from retryQueue
     *
     * @param message received message
     */
    public void receive(Message message) {
        Pair<Integer, Integer> runID = Pair.with(message.timestamp,
                message.from);
        retryQueue.remove(runID);
    }

    /**
     * Send message to receiver and trackReply.
     * <p>
     * If trackReply, will add the message info to retryQueue and create a
     * Runnable that resends the message after TIME_OUT milliseconds for MAX_RETRY times
     *
     * @param receiver   destination id
     * @param message    message
     * @param trackReply whether to trackReply
     */
    public void send(int receiver, Message message, boolean trackReply) {
        if (registry.containsKey(receiver)) {
            AsyncClientConnection connection = registry.get(receiver);
            try {
                connection.send(message);
                if (trackReply) {
                    // Add message info to retryQueue if runID does not exist
                    // otherwise increment retry Attempt
                    // Also create a scheduled task to retry after TIME_OUT
                    Pair<Integer, Integer> runID = Pair.with(message.timestamp,
                            message.to);
                    if (retryQueue.containsKey(runID))
                        retryQueue.get(runID).incAttempt();
                    else retryQueue.put(runID, new RetryInfo(message));
                    // Add retry runnable
                    scheduledThreadPool.schedule(new RetryRunnable(message), TIME_OUT
                            , TimeUnit.MILLISECONDS);
                }
            } catch (IOException e) {
                logger.info("Error sending message to: " + receiver + " message: " + message);
            }
        }
    }

    /**
     * Broadcast message to all registered receiver and track replies
     * <p>
     * Broadcast will NOT send message to sender
     *
     * @param message message to broadcast
     */
    public void broadcast(Message message) {
        int sender = message.from;
        for (int receiver : registry.keySet()) {
            if (receiver != sender) {
                Message directedMessage = new Message(message);
                directedMessage.to = receiver;
                send(receiver, directedMessage, true);
            }
        }
    }

    /**
     * Inform the sender the current set of receivers
     *
     * @param message broadcast message
     */
    public void inform(Message message) {
        int sender = message.from;
        List<Integer> receivers = new ArrayList<>(registry.keySet());
        Message reply = Message.inform(sender, message.ID, receivers);
        send(sender, reply, false);
    }

    /**
     * Shutdown the threadPool and close all socketChannel connections
     */
    public void close() {
        scheduledThreadPool.shutdown();
        for (int id : registry.keySet()) {
            try {
                registry.get(id).close();
            } catch (IOException e) {
                logger.info("Fail to close connection: " + id + " error: " + e.toString());
            }
        }
    }

    /**
     * Delayed task to be run after TIME_OUT milliseconds
     * <p>
     * <p></p>
     * Checks if the current message has been answered (if still in retryQueue).
     * <p>
     * If it has not been answered and the maximum retry attempt has not been reached,
     * resend the message with tracking. Resend with tracking means that the message
     * will be resent and another RetryRunnable task is created until either
     * MAX_RETRY has been reached, or a reply has been accepted
     * <p>
     * If it has not been answered and the maximum retry has been reached, send a NAK
     * message to sender and remove message info from retryQueue
     */
    class RetryRunnable implements Runnable {
        private final Message message;

        RetryRunnable(Message message) {
            this.message = message;
        }

        @Override
        public void run() {
            Pair<Integer, Integer> runID = Pair.with(message.timestamp, message.to);
            if (retryQueue.containsKey(runID)) {
                RetryInfo info = retryQueue.get(runID);
                // Has not reached the maximum attempts -> retry and increment number
                if (info.getAttempt() < MAX_ATTEMPT) {
                    send(message.to, message, true);
                } else {
                    // Max attempt reached -> send nak and remove from queue
                    Message nak = Message.getNakMessage(message);
                    if (nak != null) send(message.from, nak, false);
                    retryQueue.remove(runID);
                }
            }
        }
    }
}
