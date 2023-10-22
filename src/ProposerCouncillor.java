import java.io.IOException;

public class ProposerCouncillor extends AcceptorCouncillor {
    public ProposerCouncillor(String host, int port, int councillorID) throws IOException {
        super(host, port, councillorID);
    }
}
