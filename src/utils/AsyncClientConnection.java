package utils;

import utils.helpers.Message;

import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AsyncClientConnection extends AsyncClient<Message> {
    protected final AtomicInteger timestamp;
    protected final CommService commService;

    public AsyncClientConnection(SocketChannel channel,
                                 AtomicInteger timestamp,
                                 CommService commService) {
        super(channel);
        this.timestamp = timestamp;
        this.commService = commService;
    }

    @Override
    public Message castMessage(String message) {
        return Message.fromString(message);
    }

}
