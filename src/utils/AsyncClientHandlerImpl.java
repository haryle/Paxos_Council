package utils;

import utils.helpers.Message;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncClientHandlerImpl extends AsyncClientConnection {
    private final Learner learner;
    private final AtomicInteger timestamp;
    private final CommService commService;

    private final AtomicInteger learnedValue;

    private final AtomicBoolean isUp;

    public AsyncClientHandlerImpl(SocketChannel channel, AtomicInteger timestamp,
                                  CommService commService, Learner learner, AtomicBoolean isUp, AtomicInteger learnedValue) {
        super(channel);
        this.timestamp = timestamp;
        this.commService = commService;
        this.learner = learner;
        this.isUp = isUp;
        this.learnedValue = learnedValue;
    }

    @Override
    public void handleMessage(Message message) throws IOException {
        logger.info("Receive: " + Message.printString(message));
        if (message.type.equalsIgnoreCase("CONNECT")) {
            learner.registerAcceptor(message.from);
            handleConnectMessage(message);
        } else if (message.type.equalsIgnoreCase("PREPARE") || message.type.equalsIgnoreCase("PROPOSE"))
            handleBroadcastMessage(message);
        else if (message.type.equalsIgnoreCase("PROMISE") || message.type.equalsIgnoreCase("NAK_PREPARE"))
            handleRelayMessage(message);
        else if (message.type.equalsIgnoreCase("ACCEPT") || message.type.equalsIgnoreCase("NAK_PROPOSE"))
            handleLearnMessage(message);
    }

    /**
     * Register current connection with messageService
     *
     * @param message connection message
     */
    private void handleConnectMessage(Message message) {
        commService.registerConnection(message.from, this);
    }

    /**
     * Broadcast PREPARE and PROPOSE messages from proposer to all acceptors
     * <p>
     * Replies for each message are tracked and resent accordingly
     * Also sends to the proposer the list of processes it broadcasts the message to
     *
     * @param message PREPARE or PROPOSE message
     * @throws IOException if fails to send message
     */
    private void handleBroadcastMessage(Message message) throws IOException {
        logger.info(String.format("Broadcast: %s - sender: %d", message.type, message.from));
        commService.inform(message);
        // Register current timestamp
        message.timestamp = timestamp.getAndIncrement();
        commService.broadcast(message, true);
    }

    /**
     * Relay reply message from acceptors to proposers. Reply messages are of type
     * NAK, PROMISE, ACCEPT
     *
     * @param message NAK, PROMISE or ACCEPT messages
     * @throws IOException if fails to relay message
     */
    private void handleRelayMessage(Message message) throws IOException {
        commService.receive(message);
        commService.send(message.to, message, false, true);
    }


    private void handleLearnMessage(Message message) throws IOException {
        commService.receive(message);
        Message reply = learner.handleAcceptMessage(message);
        if (reply != null) {
            logger.info("LEARN: " + Message.printString(reply));
            learnedValue.set(reply.acceptValue);
            isUp.set(false);
        }
    }

}
