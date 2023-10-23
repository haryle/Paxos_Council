import utils.helpers.Message;

import java.io.IOException;

/**
 * A councillor who acts as both a proposer and an acceptor
 */
public class ProposerCouncillor extends AcceptorCouncillor {

    protected Proposer proposer;

    /**
     * Connect to the central broadcasting server
     * Also register councillor ID. Initiate proposer
     *
     * @param host         hostname of the broadcasting server
     * @param port         port of the broadcasting server
     * @param councillorID councillor ID
     * @param waitMin      min wait period before beginning the next round
     * @param waitMax      max wait period before beginning the next round
     * @throws IOException if there is any error in connecting to the councillor
     */
    public ProposerCouncillor(String host, int port, int councillorID, int waitMin, int waitMax) throws IOException {
        super(host, port, councillorID);
        proposer = new Proposer(councillorID, waitMin, waitMax);
    }

    /**
     * Send message to central server.
     * <p>
     * If message is a broadcast message (to is 0), send one
     * to itself.
     *
     * @param message message of parameterized type
     * @throws IOException if there is sending error
     */
    @Override
    public void send(Message message) throws IOException, InterruptedException {
        if (message.to == 0 &&
                (message.type.equalsIgnoreCase("PREPARE") || message.type.equalsIgnoreCase("PROPOSE"))) {
            Message directedMessage = new Message(message);
            directedMessage.to = councillorID;
            handleMessage(directedMessage);
        }
        super.send(message);
    }

    @Override
    public void handleMessage(Message message) throws IOException, InterruptedException {
        Message reply = null;
        if (message.type.equalsIgnoreCase("INFORM") ||
                message.type.equalsIgnoreCase("PROMISE") ||
                message.type.equalsIgnoreCase("NAK_PREPARE"))
            reply = proposer.handleMessage(message);
        if (message.type.equalsIgnoreCase("PROPOSE") ||
                message.type.equalsIgnoreCase("PREPARE"))
            reply = acceptorHandler.handleMessage(message);
        if (reply != null && reply.to != councillorID)
            send(reply);
    }
}

