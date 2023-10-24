import utils.AsyncClientConnection;
import utils.helpers.Message;

import java.io.IOException;
import java.util.Random;

/**
 * Councillor who is an acceptor. AcceptorCouncillor only reacts to incoming
 * message from proposer.
 */
public class AcceptorCouncillor extends AsyncClientConnection {
    protected final int replyMin;
    protected final int replyMax;

    public int getCouncillorID() {
        return councillorID;
    }

    protected final int councillorID;
    protected final Acceptor acceptorHandler;
    Random randomizer = new Random();

    /**
     * Connect to the Broadcast Server which acts as the central communicator
     * <p>
     * Also initiate councillor ID
     *
     * @param host         :         broadcast server hostname
     * @param port         :         broadcast server port
     * @param replyMin     :         delay min in sending
     * @param replyMax     :         delay max in sending
     * @param councillorID : councillor ID
     * @throws IOException if error during setup
     */
    public AcceptorCouncillor(String host, int port, int replyMin, int replyMax,
                              int councillorID) throws IOException {
        super(host, port);
        this.replyMin = replyMin;
        this.replyMax = replyMax;
        this.councillorID = councillorID;
        acceptorHandler = new Acceptor();
    }

    public static void main(String[] argv) throws IOException, InterruptedException {
        if (argv.length != 8) {
            System.out.println("Usage: AcceptorCouncillor -p <PORT> -id " +
                               "<councillorID> + -rMin <replyMin> + -rMax <replyMax>");
            System.exit(1);
        } else {
            String host = "localhost";
            int port = Integer.parseInt(argv[1]);
            int cID = Integer.parseInt(argv[3]);
            int replyMin = Integer.parseInt(argv[5]);
            int replyMax = Integer.parseInt(argv[7]);
            AcceptorCouncillor acceptor = new AcceptorCouncillor(host, port, replyMin
                    , replyMax, cID);
            acceptor.start();
        }
    }

    public void send(Message message) throws IOException, InterruptedException {
        Thread.sleep(randomizer.nextInt(replyMax - replyMin + 1) + replyMin);
        logger.info("Send: " + Message.printString(message));
        super.send(message);
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
    public void handleMessage(Message message) throws IOException,
            InterruptedException {
        logger.info("Receive: " + Message.printString(message));
        if (message.type.equalsIgnoreCase("SHUTDOWN")) {
            logger.info("SHUTDOWN: " + message.acceptValue);
            close();
        }
        Message reply = acceptorHandler.handleMessage(message);
        // Does not send reply if the reply is null
        if (reply != null)
            send(reply);
    }
}
