package utils;

import utils.helpers.AsyncMessageParser;
import utils.helpers.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

import static java.net.StandardSocketOptions.*;

/**
 * Abstract AsyncClient class.
 * <p>
 * Initialise by connecting to a server identified by hostname and port
 * <p>
 * Handles reading from a socket asynchronously, non-blocking. Read messages are
 * handled asynchronously.
 * <p>
 * Users will need to implement methods handleMessage to specify how each arriving
 * message is handled, and castMessage to specify how each message is partitioned and
 * cast
 *
 * @param <T> message type
 */
public abstract class AsyncClient<T> {
    public static final int BUF_SIZE = 1024;
    public static final int QUEUE_SIZE = 1024;
    protected final Logger logger = Logger.getLogger(this.getClass().getName());
    protected final Queue<T> queue;
    protected final Thread socketReader;
    protected final Thread messageHandler;
    SocketChannel channel;
    private AsyncMessageParser parser;

    public AsyncClient(String host, int port) throws IOException {
        this(host, port, Message.delimiter);
    }

    public AsyncClient(String host, int port, String delimiter) throws IOException {
        channel = SocketChannel.open(new InetSocketAddress(host, port));
        channel.configureBlocking(false);
        channel.setOption(SO_KEEPALIVE, true);
        channel.setOption(SO_SNDBUF, BUF_SIZE);
        channel.setOption(SO_RCVBUF, BUF_SIZE);
        queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        socketReader = new Thread(new SocketReader());
        messageHandler = new Thread(new MessageHandler());
        parser = new AsyncMessageParser(delimiter);
    }

    public AsyncClient(SocketChannel channel) {
        this(channel, Message.delimiter);
    }

    public AsyncClient(SocketChannel channel, String delimiter) {
        this.channel = channel;
        parser = new AsyncMessageParser(delimiter);
        queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        socketReader = new Thread(new SocketReader());
        messageHandler = new Thread(new MessageHandler());
        parser = new AsyncMessageParser(delimiter);
    }

    /**
     * Start the background Read and Write threads
     *
     * @throws IOException if thread encounters exceptions
     */
    public void start() throws IOException, InterruptedException {
        socketReader.start();
        messageHandler.start();
    }

    /**
     * Send message to channel
     *
     * @param message message to send
     * @throws IOException if fails to send message
     */
    public void send(String message) throws IOException {
        logger.info("Sending message: " + message);
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        channel.write(buffer);
    }


    /**
     * Send message to channel
     *
     * @param message message of parameterized type
     * @throws IOException if send fails
     */
    public void send(T message) throws IOException, InterruptedException {
        send(message.toString());
    }

    /**
     * Shutdown the read/write threads and close channel
     *
     * @throws IOException if fails to close
     */
    public void close() throws IOException {
        if (channel != null)
            channel.close();
        socketReader.interrupt();
        messageHandler.interrupt();
    }

    /**
     * Responds to message. To be reimplemented in children's class
     *
     * @param message parametrized message
     * @throws IOException if current thread cannot read from channel
     */
    public abstract void handleMessage(T message) throws IOException, InterruptedException;

    /**
     * Method to convert String message to parameterized type T
     *
     * @param message string message
     * @return message of type T
     */
    public abstract T castMessage(String message);

    /**
     * Background message handling thread.
     * <p>
     * Extract message from queue and handle message asynchronously
     */
    class MessageHandler implements Runnable {
        @Override
        public void run() {
            logger.info("Running MessageHandler");
            while (!Thread.currentThread().isInterrupted()) {
                while (!queue.isEmpty()) {
                    T message = queue.poll();
                    logger.info("Dequeue message: " + message);
                    try {
                        handleMessage(message);
                    } catch (IOException | InterruptedException e) {
                        try {
                            close();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * Background socket reading thread.
     * <p>
     * Asynchronously read message and put in message queue
     */
    class SocketReader implements Runnable {
        @Override
        public void run() {
            logger.info("Running SocketReader");
            ByteBuffer readBuffer = ByteBuffer.allocate(BUF_SIZE);
            while (!Thread.currentThread().isInterrupted()) {
                readBuffer.clear();
                try {
                    // Read buffered message
                    int bytesRead = channel.read(readBuffer);
                    if (bytesRead == -1) break;
                    if (bytesRead > 0) {
                        readBuffer.flip();
                        byte[] data = new byte[readBuffer.limit()];
                        readBuffer.get(data);
                        String[] messages = parser.append(new String(data));
                        // Put message in queue
                        if (messages != null) {
                            for (String msg : messages) {
                                logger.info("Received message: " + msg);
                                T message = castMessage(msg);
                                logger.info("Queuing message: " + message);
                                queue.add(message);
                            }
                        }
                    }
                } catch (IOException e) {
                    try {
                        close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                }
            }
        }
    }
}
