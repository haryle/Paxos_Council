package utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static java.net.StandardSocketOptions.*;

public class AsyncClient {

    public static final int BUF_SIZE = 1024;
    SocketChannel channel;

    public AsyncClient(String host, int port) throws IOException {
        channel = SocketChannel.open(new InetSocketAddress(host, port));
        channel.configureBlocking(false);
        channel.setOption(SO_KEEPALIVE, true);
        channel.setOption(SO_SNDBUF, BUF_SIZE);
        channel.setOption(SO_RCVBUF, BUF_SIZE);
    }

    public static void main(String[] argv) {
        AsyncClient client = null;
        try {
            client = new AsyncClient("localhost",8080);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        client.start();
    }

    public void start() {
        new Thread(new Reader()).start();
    }

    class Reader implements Runnable {
        @Override
        public void run() {
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            AsyncMessageParser parser = new AsyncMessageParser();
            while (true) {
                readBuffer.clear();
                try {
                    // Read buffered message
                    // TODO: put in a queue producer consumer style
                    int bytesRead = channel.read(readBuffer);
                    if (bytesRead == -1) break;
                    if (bytesRead > 0) {
                        readBuffer.flip();
                        byte[] data = new byte[readBuffer.limit()];
                        readBuffer.get(data);
                        String[] messages = parser.append(new String(data));
                        if (messages != null) {
                            for (String msg : messages)
                                System.out.println(msg);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
