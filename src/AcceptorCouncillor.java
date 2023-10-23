import utils.AsyncClientConnection;
import utils.helpers.Message;

import java.io.IOException;

/**
 * Councillor who is an acceptor. AcceptorCouncillor only reacts to incoming
 * message from proposer.
 */
public class AcceptorCouncillor extends AsyncClientConnection {
    protected final int councillorID;

    protected final Acceptor acceptorHandler;

    /**
     * Connect to the Broadcast Server which acts as the central communicator
     * <p>
     * Also initiate councillor ID
     *
     * @param host:         broadcast server hostname
     * @param port:         broadcast server port
     * @param councillorID: councillor ID
     * @throws IOException if error during setup
     */
    public AcceptorCouncillor(String host, int port, int councillorID) throws IOException {
        super(host, port);
        this.councillorID = councillorID;
        acceptorHandler = new Acceptor();
    }

    /**
     * Before starting, sends a connect message to the registry,
     * then starts the background read and write threads
     *
     * @throws IOException if failures during sending
     */
    public void start() throws IOException, InterruptedException {
         send(Message.connect(councillorID));
        super.start();
    }

    /**
     * Handle incoming propose and prepare messages
     * <p>
     * Generate a reply for sender and send to the central registry which relays
     * the message back to sender. If the message is directing itself, don't relay
     *
     * @param message parametrized message
     * @throws IOException if there is any sending error
     */
    @Override
    public void handleMessage(Message message) throws IOException, InterruptedException {
        Message reply = acceptorHandler.handleMessage(message);
        // Does not send reply if the reply is addressing itself or is null
        if (reply != null && reply.to != councillorID)
            send(reply);
    }
}
