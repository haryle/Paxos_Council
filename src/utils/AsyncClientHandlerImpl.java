package utils;

import utils.helpers.Message;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncClientHandlerImpl extends AsyncClientConnection {
    private final AtomicInteger timestamp;

    private final CommService commService;

    public AsyncClientHandlerImpl(SocketChannel channel, AtomicInteger timestamp,
                                  CommService commService) {
        super(channel);
        this.timestamp = timestamp;
        this.commService = commService;
    }

    @Override
    public void handleMessage(Message message) throws IOException {
        if (message.type.equalsIgnoreCase("CONNECT"))
            handleConnectMessage(message);
        else if (message.type.equalsIgnoreCase("PREPARE") || message.type.equalsIgnoreCase("PROPOSE"))
            handleBroadcastMessage(message);
        else
            handleRelayMessage(message);
    }

    /**
     * Register current connection with messageService
     *
     * @param message connection message
     */
    public void handleConnectMessage(Message message) {
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
    public void handleBroadcastMessage(Message message) throws IOException {
        commService.inform(message);
        // Register current timestamp
        message.timestamp = timestamp.getAndIncrement();
        commService.broadcast(message);
    }

    /**
     * Relay reply message from acceptors to proposers. Reply messages are of type
     * NAK, PROMISE, ACCEPT
     *
     * @param message NAK, PROMISE or ACCEPT messages
     * @throws IOException if fails to relay message
     */
    public void handleRelayMessage(Message message) throws IOException {
        commService.receive(message);
        commService.send(message.to, message, false);
    }

}
