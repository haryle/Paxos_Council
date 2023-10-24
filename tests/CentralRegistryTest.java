import com.sun.org.apache.bcel.internal.generic.FADD;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class DeterministicPaxosCouncilTest {
    AtomicInteger learnedValue;
    String HOST = "localhost";

    int numAcceptors = 9; // Number of Pure Acceptors

    int numProposers = 3; // Number of Pure Proposers

    int PORT = 12345;

    int TIME_OUT = 1000;
    int MAX_ATTEMPT = 2;

    int REPLY_MIN = 10;
    int REPLY_MAX = 10;

    int WAIT_MIN = 10;
    int WAIT_MAX = 10;

    int DELAY_FIRST;

    int DELAY_SECOND;

    int DELAY_THIRD;

    CentralRegistry registry;
    List<AcceptorCouncillor> acceptors;

    List<ProposerCouncillor> proposers;


    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        registry = new CentralRegistry(PORT, MAX_ATTEMPT, TIME_OUT);
        new Thread(()->{
            try {
                registry.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        learnedValue = registry.getLearnedValue();
        acceptors = new ArrayList<>();
        proposers = new ArrayList<>();
        for (int i = numProposers; i < numAcceptors; i++) {
            AcceptorCouncillor acceptor = new AcceptorCouncillor(HOST, PORT, REPLY_MIN, REPLY_MAX, i + 1);
            acceptor.start();
            acceptors.add(acceptor);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        registry.close();
        for (AcceptorCouncillor acceptor: acceptors)
            acceptor.close();
    }

    @Test
    void testWhenOnlyOneProposesTheProposeWins() throws IOException, InterruptedException {
        ProposerCouncillor first = new ProposerCouncillor(HOST, PORT, 1, 10, 10, 10,10);
        first.start();
        first.prepare();
        Thread.sleep(1000);
        assertEquals(1, learnedValue.get());
    }

    @Test
    void testWhenTwoProposersProposesTheFirstOneWins() throws IOException, InterruptedException {
        ProposerCouncillor first = new ProposerCouncillor(HOST, PORT, 1, 10, 10, 10,10);
        ProposerCouncillor second = new ProposerCouncillor(HOST, PORT, 2, 500, 1000, 500,1000);
        first.start();
        second.start();
        first.prepare();
        second.prepare();
        Thread.sleep(10000);
        assertEquals(proposers.get(0).getCouncillorID(), learnedValue.get());
    }

}