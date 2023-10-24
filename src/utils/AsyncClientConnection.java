package utils;

import utils.helpers.Message;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Abstract implementation of AsyncClient With Message data type
 */
public abstract class AsyncClientConnection extends AsyncClient<Message> {

    public AsyncClientConnection(SocketChannel channel) {
        super(channel);
    }

    public AsyncClientConnection(String host, int port) throws IOException {
        super(host, port);
    }

    /**
     * Cast string message to Message message
     * @param message string message
     * @return Message message
     */
    @Override
    public Message castMessage(String message) {
        return Message.fromString(message);
    }

}
