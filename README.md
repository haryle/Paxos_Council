## Assessment Checklist:

- [x] Paxos implementation works when two councillors send voting proposals at the same time
- [x] Paxos implementation works in the case where all M1-M9 have immediate responses to voting queries
- [x] Paxos implementation works when M1 – M9 have responses to voting queries suggested by several profiles (immediate response, small delay, large delay and no response), including when M2 or M3 propose and then go offline
- [x] Testing harness for the above scenarios + evidence that they work (in the form of printouts)
- [x] Code quality check 

To understand the messaging protocol in details, please refer to [the design document](Design.md). To view the test results and 
test scenarios, please refer to [the test log](TestLogging.md).

## Summary 

The implements a council president voting scenario in which there are three candidates in total and 6 non-candidates. The candidates 
can also cast their vote. The president election scheme strictly follows the Paxos protocol outlined in the paper "Paxos made simple" by 
Leslie Lamport. 

The only modifications/additions are:
- The use of a broadcast server to ease the complexity of peer discovery 
- The use of TCP as a transport protocol to ease some aspect of communications
- The use of timeout, retry and negative acknowledgment to reduce network traffic and make testing more deterministic. 

Aside from those modifications, everything is completely asynchronous. The general workflow is:

- Proposer makes a prepare message that is broadcast by the broadcast server to every receivers.
- Receivers to respond to the message, with rejection being sent as NAK. 
- Broadcast server tracks responses for each of its message sent and resend messages if no reply is received. 
- Connection handling is fully asynchronous, there is no blocking component in the system. 
- The only way for the system to shutdown is if a leader has been selected. In which case, the central registry will 
send a SHUTDOWN message to each server, causing them to shutdown. 

## How to compile and run

### To compile the program

To compile the source files: 

```bash
make compile_src
```

To compile the test files:

```bash
make compile_test
```

To run unit-tests:

```bash
make run_test 
```

Some of the unit tests are actually integration tests, which spin up the central registry, the 6 acceptors and 3 proposers 
and make them do the voting process in a deterministic manner. This involves using Thread.sleep for X seconds, assuming 
that communication has completed after the X seconds. Due to different hardware and scheduling policy, the tests may fail.
Simply rerun the test and the result should be correct. 

### To run the program

#### Explaining some of the user-input variables: 

- PORT: (central_registry/acceptor/proposer) the central registry port number. Defaults to 12345
- MAX_ATTEMPT: (central registry) if the broadcast server does not receive a reply for a particular message, how many times will it resend the message. Defaults to 5
- TIMEOUT: (central registry) how long must the broadcast server wait between each message resend. Defaults to 1000ms
- ID: (acceptor/proposer) the councillor ID of the acceptor or proposer. Note that ID must be unique.
- MIN (proposer): minimum delay in issuing a command for a proposer. For instance, a proposer must wait 1000 ms or 1s before it 
can issue a prepare or propose message. Defaults to 1000 milliseconds.
- MAX: (proposer) maximum delay in issuing a command for a proposer. Defaults to 1000ms. Together, MIN and MAX are used to prevent the live-lock situation
outlined in the original Paxos made Simple paper. This is also used to simulate behaviours of proposers M1-M3.
- DELAY: (proposer) how long after start-up does a proposer issue its first prepare message. Defaults to 3000 ms.
- REPLY_MIN (acceptor/proposer) - minimum delay between when the acceptor sends a reply to the broadcast server and when the message is received. Defaults to 100 ms
- REPLY_MAX (acceptor/proposer) - maximum delay between when the acceptor sends a reply to the broadcast server and when the message is received. Defaults to 100 ms.
- MIN1, MAX1: MIN and MAX but only affects proposer one. To be used in target `three_proposers_canonical`
- MIN2, MAX2: MIN and MAX for proposer two. To be used in target `three_proposers_canonical`
- MIN3, MAX3: MIN and MAX for proposer three. To be used in target `three_proposer_canonical`


Together REPLY_MIN and REPLY_MAX are used to control the reply time for the acceptors, which are used for testing. To manually run the program with the 
specified variables, just run `make <target> <variablename>=value` where the matching target is provided in the bracket. For example, we can do:

```bash 
make central_registry PORT=12345
```

Because PORT is an acceptable variable for the targets (cetral_registry/acceptor/proposer).

#### Running the program with default options - i.e. 1 sender, 1 receiver 
In the first terminal, run 

```bash
make central_registry
```

Open the second terminal, run

```bash 
make acceptor ID=<id_value>
```

Where you have to replace <id_value> with a value

On the third terminal, run 

```bash
make proposer ID=<id_value>
```

Where the proposer ID must be different from the acceptor ID.

#### What the log might look like:

In the following example, there is a proposer with ID = 2 and acceptor with ID of 1. Since there is only one acceptor,
the final learned value should be 2. 

For central registry:

```bash 
[2023-10-25 12:13:12] [INFO   ] Server started on port 12345  // Server is started 
[2023-10-25 12:13:19] [INFO   ] Receive: CONNECT - Sender: 1  // Server receives connection request from councillor 1
[2023-10-25 12:13:25] [INFO   ] Receive: CONNECT - Sender: 2  // Server receives connection request from councillor 2 
[2023-10-25 12:13:28] [INFO   ] Receive: PREPARE - Sender: 2, Receiver: 0, ID: 2, TS: -1  // Server receives a prepare message with id 2
[2023-10-25 12:13:28] [INFO   ] Broadcast: PREPARE - sender: 2  // Server broadcast the prepare message 
[2023-10-25 12:13:28] [INFO   ] Send: INFORM - Receiver: 2 [1, 2]  // Server informs the sender that there are 2 receivers 1 and 2
[2023-10-25 12:13:28] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 1, ID: 2, TS: 0  // Server sends the prepare message to 1
[2023-10-25 12:13:28] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 2, ID: 2, TS: 0  // Server sends the prepare message to 2 
[2023-10-25 12:13:28] [INFO   ] Receive: PROMISE - Sender: 1, Receiver: 2, ID: 2, TS: 0 // Server receives the promise message from 1 
[2023-10-25 12:13:28] [INFO   ] Receive: PROMISE - Sender: 2, Receiver: 2, ID: 2, TS: 0 // Server receives the promise message from 2
[2023-10-25 12:13:28] [INFO   ] Send: PROMISE - Sender: 1, Receiver: 2, ID: 2, TS: 0 // Server relays the mesage from 1 to 2
[2023-10-25 12:13:28] [INFO   ] Send: PROMISE - Sender: 2, Receiver: 2, ID: 2, TS: 0 // Server relays the mssage from 2 to 2 
[2023-10-25 12:13:29] [INFO   ] Receive: PROPOSE - Sender: 2, Receiver: 0, ID: 2, Value: 2, TS: -1 // Server reeives a propose message 
[2023-10-25 12:13:29] [INFO   ] Broadcast: PROPOSE - sender: 2     // Server begins broadcasting the propose message 
[2023-10-25 12:13:29] [INFO   ] Send: INFORM - Receiver: 2 [1, 2]  // Server informs the sender that there are 2 acceptors 
[2023-10-25 12:13:29] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 1, ID: 2, Value: 2, TS: 1  // Sends the propose messages 
[2023-10-25 12:13:29] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 2, ID: 2, Value: 2, TS: 1 
[2023-10-25 12:13:29] [INFO   ] Receive: ACCEPT - Sender: 1, Receiver: 2, ID: 2, Value: 2, TS: 1  // Receives the accept message from 1 
[2023-10-25 12:13:29] [INFO   ] Receive: ACCEPT - Sender: 2, Receiver: 2, ID: 2, Value: 2, TS: 1  // Receives the accept message from 2
[2023-10-25 12:13:29] [INFO   ] Accepted Entries: {1=2, 2=-1}  
[2023-10-25 12:13:29] [INFO   ] Accepted Entries: {1=2, 2=2} // Learner sees that the majority has reached a consensus
[2023-10-25 12:13:29] [INFO   ] LEARN: SHUTDOWN - Value: 2  // Learner sends a shutdown message with the learned value
[2023-10-25 12:13:29] [INFO   ] Send: SHUTDOWN - Value: 2 
[2023-10-25 12:13:29] [INFO   ] Send: SHUTDOWN - Value: 2 
```

For the acceptor (ID = 1)

```bash 
[2023-10-25 12:13:19] [INFO   ] Send: 1: CONNECT - Sender: 1  // Send a CONNECT message, informing the server of its presence
[2023-10-25 12:13:28] [INFO   ] Receive: 1: PREPARE - Sender: 2, Receiver: 1, ID: 2, TS: 0  // Receive a prepare message 
[2023-10-25 12:13:28] [INFO   ] Send: 1: PROMISE - Sender: 1, Receiver: 2, ID: 2, TS: 0 // Since ID = 2 is the highest, make a promist 
[2023-10-25 12:13:29] [INFO   ] Receive: 1: PROPOSE - Sender: 2, Receiver: 1, ID: 2, Value: 2, TS: 1 // Receives a propose with the same ID =2 
[2023-10-25 12:13:29] [INFO   ] Send: 1: ACCEPT - Sender: 1, Receiver: 2, ID: 2, Value: 2, TS: 1  // Accept the propose and sends accept
[2023-10-25 12:13:29] [INFO   ] Receive: 1: SHUTDOWN - Value: 2 // Learn that the consensus value is 2 and shuts down 
[2023-10-25 12:13:29] [INFO   ] SHUTDOWN: 1: 2 
```

For the proposer (ID = 2)

```bash
[2023-10-25 12:13:25] [INFO   ] Send: 2: CONNECT - Sender: 2 // Send a CONNECT message, informing the server of its presence
[2023-10-25 12:13:28] [INFO   ] Send: 2: PREPARE - Sender: 2, Receiver: 0, ID: 2, TS: -1 // Acts as a proposer, send a prepare message 
[2023-10-25 12:13:28] [INFO   ] Receive: 2: INFORM - Receiver: 2 [1, 2] // Receives inform message 
[2023-10-25 12:13:28] [INFO   ] Receive: 2: PREPARE - Sender: 2, Receiver: 2, ID: 2, TS: 0 // Acts as an acceptor, receives a prepare message
[2023-10-25 12:13:28] [INFO   ] Send: 2: PROMISE - Sender: 2, Receiver: 2, ID: 2, TS: 0 // Send promise 
[2023-10-25 12:13:28] [INFO   ] Receive: 2: PROMISE - Sender: 1, Receiver: 2, ID: 2, TS: 0 // Accept promises from the 2 acceptors
[2023-10-25 12:13:28] [INFO   ] Receive: 2: PROMISE - Sender: 2, Receiver: 2, ID: 2, TS: 0 
[2023-10-25 12:13:29] [INFO   ] Send: 2: PROPOSE - Sender: 2, Receiver: 0, ID: 2, Value: 2, TS: -1 // Since the majority has made a promise without any id involved, send a propose with value = 2
[2023-10-25 12:13:29] [INFO   ] Receive: 2: INFORM - Receiver: 2 [1, 2] 
[2023-10-25 12:13:29] [INFO   ] Receive: 2: PROPOSE - Sender: 2, Receiver: 2, ID: 2, Value: 2, TS: 1 
[2023-10-25 12:13:29] [INFO   ] Send: 2: ACCEPT - Sender: 2, Receiver: 2, ID: 2, Value: 2, TS: 1 // Acts as a proposer, sends ACCEPT message to value of 2
[2023-10-25 12:13:29] [INFO   ] Receive: 2: SHUTDOWN - Value: 2 // Receive learned message of 2 and shutdown 
[2023-10-25 12:13:29] [INFO   ] SHUTDOWN: 2: 2 

```

For more testing scenarios, please refer to [this document](TestLogging.md)