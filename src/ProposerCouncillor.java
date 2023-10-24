import utils.helpers.Message;

import java.io.IOException;
import java.util.Arrays;

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
    public ProposerCouncillor(String host, int port, int councillorID, int waitMin,
                              int waitMax) throws IOException {
        super(host, port, councillorID);
        proposer = new Proposer(councillorID, waitMin, waitMax);
    }

    public static void main(String[] argv) throws IOException, InterruptedException {
        if (argv.length != 10){
            System.out.println("Usage: ProposerCouncillor -p <PORT> -id <councillorID> -min <waitMin> -max <waitMax> -d <delay>");
            System.exit(1);
        }else{
            String host = "localhost";
            int port = Integer.parseInt(argv[1]);
            int councillorID = Integer.parseInt(argv[3]);
            int waitMin = Integer.parseInt(argv[5]);
            int waitMax = Integer.parseInt(argv[7]);
            int delay = Integer.parseInt(argv[9]);
            ProposerCouncillor proposer = new ProposerCouncillor(host, port, councillorID, waitMin, waitMax);
            proposer.start();
            Thread.sleep(delay);
            proposer.prepare();
        }
    }

    /**
     * Begin the process, sending prepare message
     *
     * @throws IOException          if send issues occur
     * @throws InterruptedException if thread sleep is interrupted
     */
    public void prepare() throws IOException, InterruptedException {
        send(proposer.prepare());
    }

    /**
     * Handle incoming message
     * <p>
     * If message is INFORM, PROMISE, NAK_PREPARE, handle it as a proposer
     * <p>
     * If message is PREPARE OR PROSE, handle it as an acceptor
     *
     * @param message parametrized message
     * @throws IOException          if error with sending
     * @throws InterruptedException if sleep is interrupted
     */
    @Override
    public void handleMessage(Message message) throws IOException,
            InterruptedException {
        if (message.type.equalsIgnoreCase("SHUTDOWN"))
            close();
        Message reply = null;
        if (message.type.equalsIgnoreCase("INFORM") ||
            message.type.equalsIgnoreCase("PROMISE") ||
            message.type.equalsIgnoreCase("NAK_PREPARE")||
            message.type.equalsIgnoreCase("ACCEPT")||
            message.type.equalsIgnoreCase("NAK_PROPOSE"))
            reply = proposer.handleMessage(message);
        if (message.type.equalsIgnoreCase("PROPOSE") ||
            message.type.equalsIgnoreCase("PREPARE"))
            reply = acceptorHandler.handleMessage(message);
        if (reply != null) {
            send(reply);
        }
    }
}

