import utils.AsyncServer;

import java.io.IOException;

public class CentralRegistry extends AsyncServer {

    public CentralRegistry(int port) throws IOException {
        super(port);
    }

    public CentralRegistry(int port, int max_attempt, int timeout) throws IOException {
        super(port, max_attempt, timeout);
    }


    public static void main(String[] argv) throws IOException {
        if (argv.length != 6) {
            System.out.println("Usage: CentralRegistry -p <PORT> -t <TIMEOUT> -a " +
                               "<MAX_ATTEMPT>");
            System.exit(1);
        } else {
            String host = "localhost";
            int port = Integer.parseInt(argv[1]);
            int timeout = Integer.parseInt(argv[3]);
            int attempts = Integer.parseInt(argv[5]);
            CentralRegistry registry = new CentralRegistry(port, attempts, timeout);
            registry.start();
        }
    }
}
