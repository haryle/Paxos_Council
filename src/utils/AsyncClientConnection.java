package utils;

import utils.helpers.Message;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AsyncClientConnection extends AsyncClient<Message> {

    public AsyncClientConnection(SocketChannel channel) {
        super(channel);
    }

    public AsyncClientConnection(String host, int port) throws IOException {
        super(host, port);
    }

    @Override
    public Message castMessage(String message) {
        return Message.fromString(message);
    }

}
