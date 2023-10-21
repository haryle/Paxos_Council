import utils.AsyncClient;
import utils.helpers.Message;

import java.io.IOException;

public class Councillor extends AsyncClient<Message> {
    private final int id;

    public Councillor(String host, int port, int id) throws IOException {
        super(host, port);
        this.id = id;
    }


    public void start() throws IOException {
        // Send CONNECTION message at init
        send(Message.connect(id));
        super.start();
    }


    @Override
    public void handleMessage(Message message) throws IOException {
    }

    @Override
    public Message castMessage(String message) {
        return Message.fromString(message);
    }
}
