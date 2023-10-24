package utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Implemnentation of BroadCast server
 */
public class AsyncServer {
    public AtomicInteger getLearnedValue() {
        return learnedValue;
    }

    private final AtomicInteger learnedValue;
    protected final Learner learner;
    protected final ServerSocketChannel serverSocketChannel;
    protected final Logger logger = Logger.getLogger(this.getClass().getName());
    private final ExecutorService threadPool;
    private final AtomicInteger timestamp;    // Timestamp
    private final CommService commService;

    private final AtomicBoolean isUp;

    public AsyncServer(int port) throws IOException {
        this(port, 5, 5000);
    }

    public AsyncServer(int port, int max_attempt, int timeout) throws IOException {
        // Setup serverSocketChannel
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        logger.info("Server started on port " + port);

        // Setup threadPool and commService
        threadPool = Executors.newCachedThreadPool();
        timestamp = new AtomicInteger(0);
        commService = new CommService(
                max_attempt,
                timeout,
                new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>(),
                Executors.newSingleThreadScheduledExecutor()
        );
        learner = new Learner();
        isUp = new AtomicBoolean(true);
        learnedValue = new AtomicInteger();
    }

    /**
     * Shutdown server
     * @throws IOException if errors encountered during shutdown
     */
    public void close() throws IOException {
        threadPool.shutdown();
        serverSocketChannel.close();
        commService.close();
    }

    /**
     * Start server
     * @throws IOException errors encountered during start up
     */
    public void start() throws IOException {
        while (!Thread.currentThread().isInterrupted()&& isUp.get()) {
            SocketChannel clientChannel = serverSocketChannel.accept();
            if (clientChannel != null ) {
                clientChannel.configureBlocking(false);
                // Submit connection handling thread
                threadPool.submit(() -> {
                    AsyncClientConnection client =
                            new AsyncClientHandlerImpl(clientChannel, timestamp,
                                    commService, learner, isUp, learnedValue);
                    try {
                        client.start();
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        close();
    }

}
