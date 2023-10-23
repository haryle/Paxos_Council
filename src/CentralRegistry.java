import utils.AsyncClientHandlerImpl;
import utils.AsyncServer;
import utils.CommService;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

public class CentralRegistry extends AsyncServer {

    public CentralRegistry(int port) throws IOException {
        super(port);
    }

    public CentralRegistry(int port, int max_attempt, int timeout) throws IOException {
        super(port, max_attempt, timeout);
    }


    public static void main(String[] argv) throws IOException {
        CentralRegistry registry = new CentralRegistry(12345, 5, 5000);
        registry.start();
    }
}
