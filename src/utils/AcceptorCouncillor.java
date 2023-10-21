package utils;

import utils.helpers.Message;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class AcceptorCouncillor extends AsyncClientConnection {
    private final int councillorID;

    private final Acceptor acceptorHandler;

    public AcceptorCouncillor(String host, int port, int councillorID) throws IOException {
        super(host, port);
        this.councillorID = councillorID;
        acceptorHandler = new Acceptor();
    }

    public void start() throws IOException {
        send(Message.connect(councillorID));
        super.start();
    }

    @Override
    public void handleMessage(Message message) throws IOException {
        if (message.type.equalsIgnoreCase("PREPARE"))
            handlePrepare(message);
        else if (message.type.equalsIgnoreCase("PROPOSE"))
            handlePropose(message);
        else
            logger.info("Acceptor receives invalid message: " + message);
    }

    public void handlePropose(Message message) throws IOException {
        Message reply = acceptorHandler.handlePropose(message);
        if (reply.to != councillorID)
            send(reply);
    }

    public void handlePrepare(Message message) throws IOException {
        Message reply = acceptorHandler.handlePrepare(message);
        if (reply.to != councillorID)
            send(reply);
    }
}
