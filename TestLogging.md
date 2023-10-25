Note: for the more deterministic scenarios where the prepare messages are sent sequentially and a new prepare message is 
issued only after the first round of accept message being send, refer to the unit test at `tests/CentralRegistryTest.java`

## Test scenario 1 - Two councillors send voting proposals at the same time

### How to run
Open the first terminal, run

```bash
make central_registry
```

Open the second terminal, set up all acceptors

```bash 
make acceptors 
```

Open the third terminal, run two proposers:

```bash 
make two_proposers_simultaneous
```

### What happens

In this example, we have two proposers 1 and 3, with 8 acceptors. The proposers and acceptors respond almost immediately. 
The two proposers made their prepare message at the same time; proposer 1 with ID=1 and proposer 3 with ID=3. Regardless of the 
sequence of receiving events, all acceptors will promise with ID=3 after receiving the two prepare messages. Proposer 3 will 
then send a propose message with value of 3, which will be accepted as the learned result. Proposer 1 may also send a propose 
message with value of 1, but may be rejected, which prompts it to move to the next round of prepare with ID 11. However,
it will receive a promise with an accepted value of 3, which means it will propose the value of 3. 

### Log results 
This is the log result for the central server. The log results for acceptors and proposers are mangled up so not reproduced.
Assessor can replicate the result by following the steps in how to run 

```bash 
[2023-10-25 12:37:23] [INFO   ] Server started on port 12345 
[2023-10-25 12:37:33] [INFO   ] Receive: CONNECT - Sender: 4 
[2023-10-25 12:37:33] [INFO   ] Receive: CONNECT - Sender: 5 
[2023-10-25 12:37:33] [INFO   ] Receive: CONNECT - Sender: 7 
[2023-10-25 12:37:33] [INFO   ] Receive: CONNECT - Sender: 6 
[2023-10-25 12:37:33] [INFO   ] Receive: CONNECT - Sender: 8 
[2023-10-25 12:37:33] [INFO   ] Receive: CONNECT - Sender: 9 
[2023-10-25 12:37:59] [INFO   ] Receive: CONNECT - Sender: 3 
[2023-10-25 12:37:59] [INFO   ] Receive: CONNECT - Sender: 1  // Registration sequence 
[2023-10-25 12:38:02] [INFO   ] Receive: PREPARE - Sender: 3, Receiver: 0, ID: 3, TS: -1 // Prepare message from councillor 3 
[2023-10-25 12:38:02] [INFO   ] Broadcast: PREPARE - sender: 3 
[2023-10-25 12:38:02] [INFO   ] Send: INFORM - Receiver: 3 [1, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 12:38:02] [INFO   ] Receive: PREPARE - Sender: 1, Receiver: 0, ID: 1, TS: -1 // Prepare message from councillor 1 
[2023-10-25 12:38:02] [INFO   ] Broadcast: PREPARE - sender: 1  
[2023-10-25 12:38:02] [INFO   ] Send: INFORM - Receiver: 1 [1, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 12:38:02] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 1, ID: 3, TS: 0 // Prepare messages are broadcast to all acceptors
[2023-10-25 12:38:02] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 1, ID: 1, TS: 1 
[2023-10-25 12:38:02] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 3, ID: 1, TS: 1 
[2023-10-25 12:38:02] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 12:38:02] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 4, ID: 1, TS: 1 
[2023-10-25 12:38:02] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 4, ID: 3, TS: 0 
[2023-10-25 12:38:02] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 5, ID: 3, TS: 0 
[2023-10-25 12:38:02] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 6, ID: 3, TS: 0 
[2023-10-25 12:38:02] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 7, ID: 3, TS: 0 
[2023-10-25 12:38:02] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 8, ID: 3, TS: 0 
[2023-10-25 12:38:02] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 9, ID: 3, TS: 0 
[2023-10-25 12:38:02] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 5, ID: 1, TS: 1 
[2023-10-25 12:38:02] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 6, ID: 1, TS: 1 
[2023-10-25 12:38:02] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 7, ID: 1, TS: 1 
[2023-10-25 12:38:02] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 8, ID: 1, TS: 1 
[2023-10-25 12:38:02] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 9, ID: 1, TS: 1 
[2023-10-25 12:38:02] [INFO   ] Receive: PROMISE - Sender: 6, Receiver: 3, ID: 3, TS: 0 // 6 has ID = 3
[2023-10-25 12:38:02] [INFO   ] Receive: PROMISE - Sender: 8, Receiver: 3, ID: 3, TS: 0 // 8 has ID = 3
[2023-10-25 12:38:02] [INFO   ] Send: PROMISE - Sender: 6, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 12:38:02] [INFO   ] Receive: PROMISE - Sender: 3, Receiver: 1, ID: 1, TS: 1 // 3 has ID = 1
[2023-10-25 12:38:02] [INFO   ] Send: PROMISE - Sender: 8, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 12:38:02] [INFO   ] Receive: PROMISE - Sender: 9, Receiver: 3, ID: 3, TS: 0  // 9 has ID = 3
[2023-10-25 12:38:02] [INFO   ] Receive: PROMISE - Sender: 1, Receiver: 3, ID: 3, TS: 0  // 1 has ID = 3
[2023-10-25 12:38:02] [INFO   ] Send: PROMISE - Sender: 1, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 12:38:02] [INFO   ] Receive: PROMISE - Sender: 7, Receiver: 3, ID: 3, TS: 0  // 7 has ID = 3
[2023-10-25 12:38:02] [INFO   ] Send: PROMISE - Sender: 9, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 12:38:02] [INFO   ] Send: PROMISE - Sender: 7, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 12:38:02] [INFO   ] Receive: PROMISE - Sender: 4, Receiver: 1, ID: 1, TS: 1  // 4 has ID = 1
[2023-10-25 12:38:02] [INFO   ] Send: PROMISE - Sender: 4, Receiver: 1, ID: 1, TS: 1 
[2023-10-25 12:38:02] [INFO   ] Send: PROMISE - Sender: 3, Receiver: 1, ID: 1, TS: 1 
[2023-10-25 12:38:02] [INFO   ] Receive: PROMISE - Sender: 5, Receiver: 3, ID: 3, TS: 0 // 5 has ID = 3
[2023-10-25 12:38:02] [INFO   ] Send: PROMISE - Sender: 5, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 12:38:02] [INFO   ] Receive: PROMISE - Sender: 3, Receiver: 3, ID: 3, TS: 0  // 3 receives the 2nd prepare and promise ID = 3
[2023-10-25 12:38:02] [INFO   ] Receive: NAK_PREPARE - Sender: 1, Receiver: 1, ID: 1, TS: 1 // 1 receives prepare from 1 and rejects (ID = 3)
[2023-10-25 12:38:03] [INFO   ] Send: NAK_PREPARE - Sender: 1, Receiver: 1, ID: 1, TS: 1 
[2023-10-25 12:38:03] [INFO   ] Receive: NAK_PREPARE - Sender: 5, Receiver: 1, ID: 1, TS: 1 // 5 receives prepare from 1 and rejects (ID = 3)
[2023-10-25 12:38:03] [INFO   ] Receive: NAK_PREPARE - Sender: 8, Receiver: 1, ID: 1, TS: 1 // 8 receives prepare from 1 and rejects (ID = 3)
[2023-10-25 12:38:03] [INFO   ] Send: NAK_PREPARE - Sender: 8, Receiver: 1, ID: 1, TS: 1 
[2023-10-25 12:38:03] [INFO   ] Send: PROMISE - Sender: 3, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 12:38:03] [INFO   ] Receive: NAK_PREPARE - Sender: 7, Receiver: 1, ID: 1, TS: 1 // 7 receives prepare from 1 and rejects (ID = 3)
[2023-10-25 12:38:02] [INFO   ] Receive: NAK_PREPARE - Sender: 9, Receiver: 1, ID: 1, TS: 1 // 9 receives prepare from 1 and rejects (ID = 3)
[2023-10-25 12:38:02] [INFO   ] Receive: NAK_PREPARE - Sender: 6, Receiver: 1, ID: 1, TS: 1 // 6 receives prepare from 1 and rejects (ID = 3)
[2023-10-25 12:38:03] [INFO   ] Send: NAK_PREPARE - Sender: 6, Receiver: 1, ID: 1, TS: 1 
[2023-10-25 12:38:03] [INFO   ] Receive: PROMISE - Sender: 4, Receiver: 3, ID: 3, TS: 0 // 4 receives the 2nd prepare and promise ID = 3
[2023-10-25 12:38:03] [INFO   ] Send: NAK_PREPARE - Sender: 9, Receiver: 1, ID: 1, TS: 1 
[2023-10-25 12:38:03] [INFO   ] Send: NAK_PREPARE - Sender: 7, Receiver: 1, ID: 1, TS: 1 
[2023-10-25 12:38:03] [INFO   ] Send: NAK_PREPARE - Sender: 5, Receiver: 1, ID: 1, TS: 1 
[2023-10-25 12:38:03] [INFO   ] Send: PROMISE - Sender: 4, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 12:38:03] [INFO   ] Receive: PROPOSE - Sender: 3, Receiver: 0, ID: 3, Value: 3, TS: -1 // 3 at this point sees that majority has accepted ID = 3 and propose value = 3
[2023-10-25 12:38:03] [INFO   ] Broadcast: PROPOSE - sender: 3 
[2023-10-25 12:38:03] [INFO   ] Send: INFORM - Receiver: 3 [1, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 12:38:03] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 1, ID: 3, Value: 3, TS: 2 
[2023-10-25 12:38:03] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 3, ID: 3, Value: 3, TS: 2 
[2023-10-25 12:38:03] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 4, ID: 3, Value: 3, TS: 2 
[2023-10-25 12:38:03] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 5, ID: 3, Value: 3, TS: 2 
[2023-10-25 12:38:03] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 6, ID: 3, Value: 3, TS: 2 
[2023-10-25 12:38:03] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 7, ID: 3, Value: 3, TS: 2 
[2023-10-25 12:38:03] [INFO   ] Receive: PREPARE - Sender: 1, Receiver: 0, ID: 11, TS: -1  // 1 at this point sees that its messages were rejected and begins the next round of prepare 
[2023-10-25 12:38:03] [INFO   ] Broadcast: PREPARE - sender: 1 
[2023-10-25 12:38:03] [INFO   ] Send: INFORM - Receiver: 1 [1, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 12:38:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 12:38:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 3, ID: 11, TS: 3 
[2023-10-25 12:38:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 4, ID: 11, TS: 3 
[2023-10-25 12:38:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 5, ID: 11, TS: 3 
[2023-10-25 12:38:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 6, ID: 11, TS: 3 
[2023-10-25 12:38:03] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 8, ID: 3, Value: 3, TS: 2 
[2023-10-25 12:38:03] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 9, ID: 3, Value: 3, TS: 2 
[2023-10-25 12:38:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 7, ID: 11, TS: 3 
[2023-10-25 12:38:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 8, ID: 11, TS: 3 
[2023-10-25 12:38:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 9, ID: 11, TS: 3 
[2023-10-25 12:38:03] [INFO   ] Receive: ACCEPT - Sender: 4, Receiver: 3, ID: 3, Value: 3, TS: 2  // Every one accepts ID = 3, value = 3
[2023-10-25 12:38:03] [INFO   ] Accepted Entries: {1=-1, 3=-1, 4=3, 5=-1, 6=-1, 7=-1, 8=-1, 9=-1} 
[2023-10-25 12:38:03] [INFO   ] Receive: ACCEPT - Sender: 7, Receiver: 3, ID: 3, Value: 3, TS: 2 
[2023-10-25 12:38:03] [INFO   ] Accepted Entries: {1=-1, 3=-1, 4=3, 5=-1, 6=-1, 7=3, 8=-1, 9=-1} 
[2023-10-25 12:38:03] [INFO   ] Receive: ACCEPT - Sender: 1, Receiver: 3, ID: 3, Value: 3, TS: 2 
[2023-10-25 12:38:03] [INFO   ] Accepted Entries: {1=3, 3=-1, 4=3, 5=-1, 6=-1, 7=3, 8=-1, 9=-1} 
[2023-10-25 12:38:03] [INFO   ] Receive: ACCEPT - Sender: 5, Receiver: 3, ID: 3, Value: 3, TS: 2 
[2023-10-25 12:38:03] [INFO   ] Accepted Entries: {1=3, 3=-1, 4=3, 5=3, 6=-1, 7=3, 8=-1, 9=-1} 
[2023-10-25 12:38:03] [INFO   ] Receive: ACCEPT - Sender: 3, Receiver: 3, ID: 3, Value: 3, TS: 2 
[2023-10-25 12:38:03] [INFO   ] Accepted Entries: {1=3, 3=3, 4=3, 5=3, 6=-1, 7=3, 8=-1, 9=-1} 
[2023-10-25 12:38:03] [INFO   ] LEARN: SHUTDOWN - Value: 3 // Value of 3 is learned at shutdown (majority agrees on the value)
[2023-10-25 12:38:03] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 12:38:03] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 12:38:03] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 12:38:03] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 12:38:03] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 12:38:03] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 12:38:03] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 12:38:03] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 12:38:03] [INFO   ] Receive: ACCEPT - Sender: 6, Receiver: 3, ID: 3, Value: 3, TS: 2 
[2023-10-25 12:38:03] [INFO   ] Receive: ACCEPT - Sender: 8, Receiver: 3, ID: 3, Value: 3, TS: 2 
[2023-10-25 12:38:03] [INFO   ] Accepted Entries: {1=3, 3=3, 4=3, 5=3, 6=-1, 7=3, 8=3, 9=-1} 
[2023-10-25 12:38:03] [INFO   ] LEARN: SHUTDOWN - Value: 3 
[2023-10-25 12:38:03] [INFO   ] Receive: ACCEPT - Sender: 9, Receiver: 3, ID: 3, Value: 3, TS: 2 
[2023-10-25 12:38:03] [INFO   ] Accepted Entries: {1=3, 3=3, 4=3, 5=3, 6=-1, 7=3, 8=3, 9=3} 
[2023-10-25 12:38:03] [INFO   ] LEARN: SHUTDOWN - Value: 3 
[2023-10-25 12:38:03] [INFO   ] Accepted Entries: {1=3, 3=3, 4=3, 5=3, 6=3, 7=3, 8=3, 9=3} 
[2023-10-25 12:38:03] [INFO   ] LEARN: SHUTDOWN - Value: 3 
[2023-10-25 12:38:03] [INFO   ] Receive: PROMISE - Sender: 7, Receiver: 1, ID: 11, aID: 3, aVal: 3, TS: 3 // This is the next round. 7 replies to ID = 11 with value of 3. So now proposer 1 can not propose its value of 1
[2023-10-25 12:38:03] [INFO   ] Send: PROMISE - Sender: 7, Receiver: 1, ID: 11, aID: 3, aVal: 3, TS: 3 // This server is shutdown because the followed up result will not change the learned value
[2023-10-25 12:38:03] [INFO   ] Receive: PROMISE - Sender: 5, Receiver: 1, ID: 11, aID: 3, aVal: 3, TS: 3 
[2023-10-25 12:38:03] [INFO   ] Send: PROMISE - Sender: 5, Receiver: 1, ID: 11, aID: 3, aVal: 3, TS: 3 
[2023-10-25 12:38:03] [INFO   ] Error sending message to: 1 message: 7:1:PROMISE:11:3:3:[]:3; 
[2023-10-25 12:38:03] [INFO   ] Error sending message to: 1 message: 5:1:PROMISE:11:3:3:[]:3; 
[2023-10-25 12:38:04] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 12:38:04] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 3, ID: 11, TS: 3 
[2023-10-25 12:38:04] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 4, ID: 11, TS: 3 
[2023-10-25 12:38:04] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 6, ID: 11, TS: 3 
[2023-10-25 12:38:04] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 8, ID: 11, TS: 3 
[2023-10-25 12:38:04] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 9, ID: 11, TS: 3 
```

## Test scenario 2 - When M1-M9 respond immediately - Three proposers 

### How to run 

Open the first terminal, run

```bash
make central_registry
```

Open the second terminal, set up all acceptors

```bash 
make acceptors 
```

Open the third terminal, run two proposers:

```bash 
make three_proposers_simultaneous
```

### What happens
Since all three proposers send their prepare message immediately, the one with the highest ID wins (councillor 3 because its message's ID starts at 3).
The scenario has the same logic as scenario 1.


### Log results 
Realisation 1: the prepare message from 3 comes out last, but before any successful propose message from 1 and 2. 
This means all acceptors promise to not accept anything with ID < 3, which means they will reject proposals from 1 and 2.
3 becomes the winner. 

```bash 
[2023-10-25 13:14:06] [INFO   ] Server started on port 12345 
[2023-10-25 13:14:16] [INFO   ] Receive: CONNECT - Sender: 4 
[2023-10-25 13:14:16] [INFO   ] Receive: CONNECT - Sender: 6 
[2023-10-25 13:14:16] [INFO   ] Receive: CONNECT - Sender: 5 
[2023-10-25 13:14:16] [INFO   ] Receive: CONNECT - Sender: 9 
[2023-10-25 13:14:16] [INFO   ] Receive: CONNECT - Sender: 7 
[2023-10-25 13:14:16] [INFO   ] Receive: CONNECT - Sender: 8 
[2023-10-25 13:14:27] [INFO   ] Receive: CONNECT - Sender: 1 
[2023-10-25 13:14:28] [INFO   ] Receive: CONNECT - Sender: 2 
[2023-10-25 13:14:28] [INFO   ] Receive: CONNECT - Sender: 3 
[2023-10-25 13:14:31] [INFO   ] Receive: PREPARE - Sender: 1, Receiver: 0, ID: 1, TS: -1  // Proposer 1 sends the 1st prepare 
[2023-10-25 13:14:31] [INFO   ] Broadcast: PREPARE - sender: 1 
[2023-10-25 13:14:31] [INFO   ] Send: INFORM - Receiver: 1 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 2, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 3, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 4, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 5, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 6, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 7, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 8, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 9, ID: 1, TS: 0  // Proposer 1 prepare is broadcasted
[2023-10-25 13:14:31] [INFO   ] Receive: PREPARE - Sender: 2, Receiver: 0, ID: 2, TS: -1 // Proposer 2 prepare and broadcast 
[2023-10-25 13:14:31] [INFO   ] Broadcast: PREPARE - sender: 2 
[2023-10-25 13:14:31] [INFO   ] Send: INFORM - Receiver: 2 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 1, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 3, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 4, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 5, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 6, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 7, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 8, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 9, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 1, Receiver: 1, ID: 1, TS: 0  // 1 - ID = 1
[2023-10-25 13:14:31] [INFO   ] Receive: PREPARE - Sender: 3, Receiver: 0, ID: 3, TS: -1  // Proposer 3 prepare and broadcast 
[2023-10-25 13:14:31] [INFO   ] Broadcast: PREPARE - sender: 3 
[2023-10-25 13:14:31] [INFO   ] Send: INFORM - Receiver: 3 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 1, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 2, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 1, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 7, Receiver: 1, ID: 1, TS: 0  // 7 - ID = 1
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 7, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 4, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 5, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 6, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 7, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 8, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 9, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 2, Receiver: 1, ID: 1, TS: 0 // 2 - ID = 1
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 2, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 8, Receiver: 1, ID: 1, TS: 0  // 8 - ID = 1
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 6, Receiver: 1, ID: 1, TS: 0  // 6 - ID = 1
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 8, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 3, Receiver: 1, ID: 1, TS: 0 // 3 - ID = 1
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 3, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 9, Receiver: 1, ID: 1, TS: 0  // 9 - ID = 1
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 4, Receiver: 1, ID: 1, TS: 0  // 4 - ID = 1
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 9, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 6, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 4, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 5, Receiver: 1, ID: 1, TS: 0 // 5 - ID = 1
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 5, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 1, Receiver: 2, ID: 2, TS: 1 // 1 now sees prepare 2, change promise to ID = 2
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 1, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 3, Receiver: 2, ID: 2, TS: 1 // 3 now sees prepare 2, change promise to ID = 2
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 3, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 5, Receiver: 2, ID: 2, TS: 1 // 5 now sees prepare 2, change promise to ID = 2
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 5, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 7, Receiver: 2, ID: 2, TS: 1 // 7 now sees prepare 2, change promise to ID = 2
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 7, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 4, Receiver: 2, ID: 2, TS: 1 // 4 now sees prepare 2, change promise to ID = 2
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 4, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 9, Receiver: 2, ID: 2, TS: 1 // 9 now sees prepare 2, change promise to ID = 2
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 9, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 2, Receiver: 2, ID: 2, TS: 1 // 2 now sees prepare 2, change promise to ID = 2
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 2, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 6, Receiver: 2, ID: 2, TS: 1 // 6 now sees prepare 2, change promise to ID = 2
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 6, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 8, Receiver: 2, ID: 2, TS: 1 // 8 now sees prepare 2, change promise to ID = 2
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 8, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 1, Receiver: 3, ID: 3, TS: 2 // 1 now sees prepare 3, change promise to ID = 3
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 1, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 3, Receiver: 3, ID: 3, TS: 2 // 3 now sees prepare 3, change promise to ID = 3
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 3, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 4, Receiver: 3, ID: 3, TS: 2 // 4 now sees prepare 3, change promise to ID = 3
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 4, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 6, Receiver: 3, ID: 3, TS: 2 // 6 now sees prepare 3, change promise to ID = 3
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 6, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 7, Receiver: 3, ID: 3, TS: 2 // 7 now sees prepare 3, change promise to ID = 3
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 7, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 9, Receiver: 3, ID: 3, TS: 2 // 9 now sees prepare 3, change promise to ID = 3
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 9, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 2, Receiver: 3, ID: 3, TS: 2 // 2 now sees prepare 3, change promise to ID = 3
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 2, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 8, Receiver: 3, ID: 3, TS: 2 // 8 now sees prepare 3, change promise to ID = 3
[2023-10-25 13:14:31] [INFO   ] Receive: PROMISE - Sender: 5, Receiver: 3, ID: 3, TS: 2 // 5 now sees prepare 3, change promise to ID = 3
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 5, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Send: PROMISE - Sender: 8, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 13:14:31] [INFO   ] Receive: PROPOSE - Sender: 2, Receiver: 0, ID: 2, Value: 2, TS: -1 // 2 proposes ID = 2, value = 2, not knowing that everyone has seen 3
[2023-10-25 13:14:31] [INFO   ] Broadcast: PROPOSE - sender: 2 
[2023-10-25 13:14:31] [INFO   ] Send: INFORM - Receiver: 2 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 1, ID: 2, Value: 2, TS: 3 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 2, ID: 2, Value: 2, TS: 3 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 3, ID: 2, Value: 2, TS: 3 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 4, ID: 2, Value: 2, TS: 3 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 5, ID: 2, Value: 2, TS: 3 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 6, ID: 2, Value: 2, TS: 3 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 7, ID: 2, Value: 2, TS: 3 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 8, ID: 2, Value: 2, TS: 3 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 9, ID: 2, Value: 2, TS: 3 
[2023-10-25 13:14:31] [INFO   ] Receive: PROPOSE - Sender: 1, Receiver: 0, ID: 1, Value: 1, TS: -1 // 1 proposes ID = 1, value = 1, not knowing everyone has seen 2 and then 3
[2023-10-25 13:14:31] [INFO   ] Broadcast: PROPOSE - sender: 1 
[2023-10-25 13:14:31] [INFO   ] Send: INFORM - Receiver: 1 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 1, ID: 1, Value: 1, TS: 4 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 2, ID: 1, Value: 1, TS: 4 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 3, ID: 1, Value: 1, TS: 4 
[2023-10-25 13:14:31] [INFO   ] Receive: PROPOSE - Sender: 3, Receiver: 0, ID: 3, Value: 3, TS: -1  // 3 proposes ID = 3, value = 3 which is accepted 
[2023-10-25 13:14:31] [INFO   ] Broadcast: PROPOSE - sender: 3 
[2023-10-25 13:14:31] [INFO   ] Send: INFORM - Receiver: 3 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 1, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 2, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 3, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 4, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 5, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 4, ID: 1, Value: 1, TS: 4 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 6, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 7, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 5, ID: 1, Value: 1, TS: 4 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 8, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 9, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 6, ID: 1, Value: 1, TS: 4 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 7, ID: 1, Value: 1, TS: 4 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 8, ID: 1, Value: 1, TS: 4 
[2023-10-25 13:14:31] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 9, ID: 1, Value: 1, TS: 4 
[2023-10-25 13:14:31] [INFO   ] Receive: NAK_PROPOSE - Sender: 7, Receiver: 2, ID: 2, Value: 2 TS: 3 // Propose from 2 with ID = 2, value = 2 is rejected
[2023-10-25 13:14:31] [INFO   ] Receive: NAK_PROPOSE - Sender: 9, Receiver: 2, ID: 2, Value: 2 TS: 3 
[2023-10-25 13:14:31] [INFO   ] Receive: NAK_PROPOSE - Sender: 5, Receiver: 2, ID: 2, Value: 2 TS: 3 
[2023-10-25 13:14:31] [INFO   ] Receive: NAK_PROPOSE - Sender: 4, Receiver: 2, ID: 2, Value: 2 TS: 3 
[2023-10-25 13:14:31] [INFO   ] Receive: NAK_PROPOSE - Sender: 6, Receiver: 2, ID: 2, Value: 2 TS: 3 
[2023-10-25 13:14:31] [INFO   ] Receive: NAK_PROPOSE - Sender: 8, Receiver: 2, ID: 2, Value: 2 TS: 3 
[2023-10-25 13:14:31] [INFO   ] Receive: NAK_PROPOSE - Sender: 2, Receiver: 2, ID: 2, Value: 2 TS: 3 
[2023-10-25 13:14:32] [INFO   ] Receive: NAK_PROPOSE - Sender: 3, Receiver: 2, ID: 2, Value: 2 TS: 3 
[2023-10-25 13:14:32] [INFO   ] Receive: NAK_PROPOSE - Sender: 1, Receiver: 2, ID: 2, Value: 2 TS: 3 
[2023-10-25 13:14:32] [INFO   ] Receive: ACCEPT - Sender: 7, Receiver: 3, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:32] [INFO   ] Accepted Entries: {1=-1, 2=-1, 3=-1, 4=-1, 5=-1, 6=-1, 7=3, 8=-1, 9=-1} 
[2023-10-25 13:14:32] [INFO   ] Receive: ACCEPT - Sender: 5, Receiver: 3, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:32] [INFO   ] Accepted Entries: {1=-1, 2=-1, 3=-1, 4=-1, 5=3, 6=-1, 7=3, 8=-1, 9=-1} 
[2023-10-25 13:14:32] [INFO   ] Receive: NAK_PROPOSE - Sender: 1, Receiver: 1, ID: 1, Value: 1 TS: 4  // Propose from 1 with ID = 1, value = 1 is rejected 
[2023-10-25 13:14:32] [INFO   ] Receive: ACCEPT - Sender: 9, Receiver: 3, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:32] [INFO   ] Accepted Entries: {1=-1, 2=-1, 3=-1, 4=-1, 5=3, 6=-1, 7=3, 8=-1, 9=3} 
[2023-10-25 13:14:32] [INFO   ] Receive: ACCEPT - Sender: 6, Receiver: 3, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:32] [INFO   ] Receive: ACCEPT - Sender: 4, Receiver: 3, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:32] [INFO   ] Accepted Entries: {1=-1, 2=-1, 3=-1, 4=-1, 5=3, 6=3, 7=3, 8=-1, 9=3} 
[2023-10-25 13:14:32] [INFO   ] Accepted Entries: {1=-1, 2=-1, 3=-1, 4=3, 5=3, 6=3, 7=3, 8=-1, 9=3} 
[2023-10-25 13:14:32] [INFO   ] LEARN: SHUTDOWN - Value: 3 
[2023-10-25 13:14:32] [INFO   ] Receive: NAK_PROPOSE - Sender: 2, Receiver: 1, ID: 1, Value: 1 TS: 4 
[2023-10-25 13:14:32] [INFO   ] Receive: ACCEPT - Sender: 8, Receiver: 3, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:32] [INFO   ] Accepted Entries: {1=-1, 2=-1, 3=-1, 4=3, 5=3, 6=3, 7=3, 8=3, 9=3} 
[2023-10-25 13:14:32] [INFO   ] LEARN: SHUTDOWN - Value: 3 
[2023-10-25 13:14:32] [INFO   ] Receive: NAK_PROPOSE - Sender: 3, Receiver: 1, ID: 1, Value: 1 TS: 4 
[2023-10-25 13:14:32] [INFO   ] Send: SHUTDOWN - Value: 3 // Value = 3 is learned and shutdown is sent 
[2023-10-25 13:14:32] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:14:32] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:14:32] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:14:32] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:14:32] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:14:32] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:14:32] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:14:32] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:14:32] [INFO   ] Receive: NAK_PROPOSE - Sender: 6, Receiver: 1, ID: 1, Value: 1 TS: 4 
[2023-10-25 13:14:32] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 4, ID: 1, Value: 1, TS: 4 
[2023-10-25 13:14:32] [INFO   ] Resend: PROPOSE - Sender: 3, Receiver: 1, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:32] [INFO   ] Resend: PROPOSE - Sender: 3, Receiver: 2, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:32] [INFO   ] Resend: PROPOSE - Sender: 3, Receiver: 3, ID: 3, Value: 3, TS: 5 
[2023-10-25 13:14:32] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 5, ID: 1, Value: 1, TS: 4 
[2023-10-25 13:14:32] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 7, ID: 1, Value: 1, TS: 4 
[2023-10-25 13:14:32] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 8, ID: 1, Value: 1, TS: 4 
[2023-10-25 13:14:32] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 9, ID: 1, Value: 1, TS: 4 
```

An alternative realisation is produced as follows: prepare for 3 comes out before prepare for 1 and prepare for 2 were issued.
Prepare for 1 and 2 are thus rejected, forcing them to begin the next prepare round with ID = 11 and 12 respectively. However,
propose from 3 with value = 3 was accepted during this time. 

```bash
[2023-10-25 13:35:43] [INFO   ] Server started on port 12345 
[2023-10-25 13:35:52] [INFO   ] Receive: CONNECT - Sender: 9 
[2023-10-25 13:35:53] [INFO   ] Receive: CONNECT - Sender: 5 
[2023-10-25 13:35:53] [INFO   ] Receive: CONNECT - Sender: 7 
[2023-10-25 13:35:53] [INFO   ] Receive: CONNECT - Sender: 4 
[2023-10-25 13:35:53] [INFO   ] Receive: CONNECT - Sender: 8 
[2023-10-25 13:35:53] [INFO   ] Receive: CONNECT - Sender: 6 
[2023-10-25 13:36:00] [INFO   ] Receive: CONNECT - Sender: 3 
[2023-10-25 13:36:00] [INFO   ] Receive: CONNECT - Sender: 2 
[2023-10-25 13:36:00] [INFO   ] Receive: CONNECT - Sender: 1 
[2023-10-25 13:36:03] [INFO   ] Receive: PREPARE - Sender: 3, Receiver: 0, ID: 3, TS: -1 // Prepare with ID 3 first sent 
[2023-10-25 13:36:03] [INFO   ] Broadcast: PREPARE - sender: 3 
[2023-10-25 13:36:03] [INFO   ] Send: INFORM - Receiver: 3 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 13:36:03] [INFO   ] Receive: PREPARE - Sender: 2, Receiver: 0, ID: 2, TS: -1 
[2023-10-25 13:36:03] [INFO   ] Broadcast: PREPARE - sender: 2 
[2023-10-25 13:36:03] [INFO   ] Send: INFORM - Receiver: 2 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 1, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 3, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 4, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 5, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 6, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 7, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 1, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 8, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 9, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 2, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 4, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 5, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 6, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 7, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 8, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 9, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 3, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 3, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 6, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Receive: PREPARE - Sender: 1, Receiver: 0, ID: 1, TS: -1 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 6, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Broadcast: PREPARE - sender: 1 
[2023-10-25 13:36:03] [INFO   ] Send: INFORM - Receiver: 1 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 2, ID: 1, TS: 2 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 3, ID: 1, TS: 2 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 5, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 5, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 4, ID: 1, TS: 2 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 9, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 5, ID: 1, TS: 2 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 6, ID: 1, TS: 2 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 7, ID: 1, TS: 2 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 8, ID: 1, TS: 2 
[2023-10-25 13:36:03] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 9, ID: 1, TS: 2 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 8, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 4, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 4, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 8, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 1, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 2, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 7, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 2, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 9, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 1, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 7, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 3, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 3, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 9, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 9, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 1, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 1, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 8, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 6, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 6, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 7, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 7, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 5, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 5, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 8, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 2, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Receive: PROMISE - Sender: 4, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 4, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:03] [INFO   ] Send: PROMISE - Sender: 2, Receiver: 3, ID: 3, TS: 0 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PREPARE - Sender: 4, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Send: NAK_PREPARE - Sender: 4, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PREPARE - Sender: 9, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Send: NAK_PREPARE - Sender: 9, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PREPARE - Sender: 3, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Send: NAK_PREPARE - Sender: 3, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PREPARE - Sender: 2, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Send: NAK_PREPARE - Sender: 2, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PREPARE - Sender: 8, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PREPARE - Sender: 5, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Send: NAK_PREPARE - Sender: 5, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Send: NAK_PREPARE - Sender: 8, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PREPARE - Sender: 1, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PREPARE - Sender: 7, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Send: NAK_PREPARE - Sender: 7, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PREPARE - Sender: 6, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Send: NAK_PREPARE - Sender: 6, Receiver: 1, ID: 1, TS: 2 
[2023-10-25 13:36:04] [INFO   ] Send: NAK_PREPARE - Sender: 1, Receiver: 1, ID: 1, TS: 2  // PREPARE for 1 and 2 rejected 
[2023-10-25 13:36:04] [INFO   ] Receive: PROPOSE - Sender: 3, Receiver: 0, ID: 3, Value: 3, TS: -1  // 3 Proposes 3
[2023-10-25 13:36:04] [INFO   ] Broadcast: PROPOSE - sender: 3 
[2023-10-25 13:36:04] [INFO   ] Send: INFORM - Receiver: 3 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 1, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 2, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 3, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 4, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 5, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 6, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 7, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 8, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 9, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Receive: PROPOSE - Sender: 2, Receiver: 0, ID: 2, Value: 2, TS: -1 
[2023-10-25 13:36:04] [INFO   ] Broadcast: PROPOSE - sender: 2 
[2023-10-25 13:36:04] [INFO   ] Send: INFORM - Receiver: 2 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 1, ID: 2, Value: 2, TS: 4 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 2, ID: 2, Value: 2, TS: 4 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 3, ID: 2, Value: 2, TS: 4 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 4, ID: 2, Value: 2, TS: 4 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 5, ID: 2, Value: 2, TS: 4 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 6, ID: 2, Value: 2, TS: 4 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 7, ID: 2, Value: 2, TS: 4 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 8, ID: 2, Value: 2, TS: 4 
[2023-10-25 13:36:04] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 9, ID: 2, Value: 2, TS: 4 
[2023-10-25 13:36:04] [INFO   ] Receive: ACCEPT - Sender: 2, Receiver: 3, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Accepted Entries: {1=-1, 2=3, 3=-1, 4=-1, 5=-1, 6=-1, 7=-1, 8=-1, 9=-1} 
[2023-10-25 13:36:04] [INFO   ] Receive: ACCEPT - Sender: 6, Receiver: 3, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Accepted Entries: {1=-1, 2=3, 3=-1, 4=-1, 5=-1, 6=3, 7=-1, 8=-1, 9=-1} 
[2023-10-25 13:36:04] [INFO   ] Receive: ACCEPT - Sender: 8, Receiver: 3, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Receive: PREPARE - Sender: 1, Receiver: 0, ID: 11, TS: -1 
[2023-10-25 13:36:04] [INFO   ] Broadcast: PREPARE - sender: 1 
[2023-10-25 13:36:04] [INFO   ] Send: INFORM - Receiver: 1 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 13:36:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 1, ID: 11, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 2, ID: 11, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 3, ID: 11, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 4, ID: 11, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 5, ID: 11, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 6, ID: 11, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 7, ID: 11, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 8, ID: 11, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 9, ID: 11, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Receive: ACCEPT - Sender: 9, Receiver: 3, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Receive: ACCEPT - Sender: 7, Receiver: 3, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Receive: ACCEPT - Sender: 4, Receiver: 3, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Receive: ACCEPT - Sender: 3, Receiver: 3, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Receive: ACCEPT - Sender: 5, Receiver: 3, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Accepted Entries: {1=-1, 2=3, 3=-1, 4=-1, 5=-1, 6=3, 7=-1, 8=3, 9=-1} 
[2023-10-25 13:36:04] [INFO   ] Accepted Entries: {1=-1, 2=3, 3=-1, 4=-1, 5=-1, 6=3, 7=-1, 8=3, 9=3} 
[2023-10-25 13:36:04] [INFO   ] Accepted Entries: {1=-1, 2=3, 3=-1, 4=-1, 5=3, 6=3, 7=-1, 8=3, 9=3} 
[2023-10-25 13:36:04] [INFO   ] Accepted Entries: {1=-1, 2=3, 3=3, 4=-1, 5=3, 6=3, 7=-1, 8=3, 9=3} 
[2023-10-25 13:36:04] [INFO   ] LEARN: SHUTDOWN - Value: 3 
[2023-10-25 13:36:04] [INFO   ] LEARN: SHUTDOWN - Value: 3 
[2023-10-25 13:36:04] [INFO   ] Accepted Entries: {1=-1, 2=3, 3=3, 4=3, 5=3, 6=3, 7=-1, 8=3, 9=3} 
[2023-10-25 13:36:04] [INFO   ] LEARN: SHUTDOWN - Value: 3 
[2023-10-25 13:36:04] [INFO   ] Accepted Entries: {1=-1, 2=3, 3=3, 4=3, 5=3, 6=3, 7=3, 8=3, 9=3} 
[2023-10-25 13:36:04] [INFO   ] LEARN: SHUTDOWN - Value: 3 
[2023-10-25 13:36:04] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:36:04] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:36:04] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:36:04] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PROPOSE - Sender: 7, Receiver: 2, ID: 2, Value: 2 TS: 4 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PROPOSE - Sender: 8, Receiver: 2, ID: 2, Value: 2 TS: 4 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PROPOSE - Sender: 4, Receiver: 2, ID: 2, Value: 2 TS: 4 
[2023-10-25 13:36:04] [INFO   ] Receive: ACCEPT - Sender: 1, Receiver: 3, ID: 3, Value: 3, TS: 3 
[2023-10-25 13:36:04] [INFO   ] Accepted Entries: {1=3, 2=3, 3=3, 4=3, 5=3, 6=3, 7=3, 8=3, 9=3} 
[2023-10-25 13:36:04] [INFO   ] LEARN: SHUTDOWN - Value: 3 
[2023-10-25 13:36:04] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PROPOSE - Sender: 6, Receiver: 2, ID: 2, Value: 2 TS: 4 
[2023-10-25 13:36:04] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:36:04] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PROPOSE - Sender: 9, Receiver: 2, ID: 2, Value: 2 TS: 4 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PROPOSE - Sender: 2, Receiver: 2, ID: 2, Value: 2 TS: 4 
[2023-10-25 13:36:04] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:36:04] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PROPOSE - Sender: 3, Receiver: 2, ID: 2, Value: 2 TS: 4 
[2023-10-25 13:36:04] [INFO   ] Receive: NAK_PROPOSE - Sender: 5, Receiver: 2, ID: 2, Value: 2 TS: 4 
[2023-10-25 13:36:04] [INFO   ] Receive: PROMISE - Sender: 6, Receiver: 1, ID: 11, aID: 3, aVal: 3, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Send: PROMISE - Sender: 6, Receiver: 1, ID: 11, aID: 3, aVal: 3, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Receive: PROMISE - Sender: 2, Receiver: 1, ID: 11, aID: 3, aVal: 3, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Send: PROMISE - Sender: 2, Receiver: 1, ID: 11, aID: 3, aVal: 3, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Error sending message to: 1 message: 6:1:PROMISE:11:3:3:[]:5; 
[2023-10-25 13:36:04] [INFO   ] Receive: PROMISE - Sender: 8, Receiver: 1, ID: 11, aID: 3, aVal: 3, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Send: PROMISE - Sender: 8, Receiver: 1, ID: 11, aID: 3, aVal: 3, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Error sending message to: 1 message: 2:1:PROMISE:11:3:3:[]:5; 
[2023-10-25 13:36:04] [INFO   ] Receive: PROMISE - Sender: 4, Receiver: 1, ID: 11, aID: 3, aVal: 3, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Error sending message to: 1 message: 8:1:PROMISE:11:3:3:[]:5; 
[2023-10-25 13:36:04] [INFO   ] Send: PROMISE - Sender: 4, Receiver: 1, ID: 11, aID: 3, aVal: 3, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Receive: PROMISE - Sender: 7, Receiver: 1, ID: 11, aID: 3, aVal: 3, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Send: PROMISE - Sender: 7, Receiver: 1, ID: 11, aID: 3, aVal: 3, TS: 5 
[2023-10-25 13:36:04] [INFO   ] Error sending message to: 1 message: 7:1:PROMISE:11:3:3:[]:5; 
[2023-10-25 13:36:04] [INFO   ] Error sending message to: 1 message: 4:1:PROMISE:11:3:3:[]:5; 
[2023-10-25 13:36:05] [INFO   ] Resend: PROPOSE - Sender: 2, Receiver: 1, ID: 2, Value: 2, TS: 4 
[2023-10-25 13:36:05] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 1, ID: 11, TS: 5  // 1 beginning the next round 
[2023-10-25 13:36:05] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 3, ID: 11, TS: 5 
[2023-10-25 13:36:05] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 5, ID: 11, TS: 5 
[2023-10-25 13:36:05] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 9, ID: 11, TS: 5 

```

## Test scenario 3 - Same as scenario 2. However, proposer 2 and 3 are less responsive - i.e time taken to issue new proposer's message is significantly delayed 

### How to run: 

```bash
make central_registry
```

Open the second terminal, set up all acceptors

```bash 
make acceptors 
```

Open the third terminal, run two proposers:

```bash 
make three_proposers_canonical MIN2=30000 MAX2=30000 MIN3=50000 MAX3=50000
```

### What happens
Proposer 1 sends prepare message to all acceptors at the same time as proposer 2 and 3. Proposer 3 Prepare message wins. Propser 3 
then issues propose message with value 3 while proposer 1 restart the prepare message with ID=11. Since proposer 3 is less 
responsive, proposer 1 completes the new prepare + propose round with value = 1 before proposer 2 completes its round. This 
causes the learned value to be 1 instead. 

### Log result

```bash
[2023-10-25 14:25:45] [INFO   ] Server started on port 12345 
[2023-10-25 14:25:48] [INFO   ] Receive: CONNECT - Sender: 7 
[2023-10-25 14:25:48] [INFO   ] Receive: CONNECT - Sender: 6 
[2023-10-25 14:25:48] [INFO   ] Receive: CONNECT - Sender: 9 
[2023-10-25 14:25:48] [INFO   ] Receive: CONNECT - Sender: 5 
[2023-10-25 14:25:49] [INFO   ] Receive: CONNECT - Sender: 4 
[2023-10-25 14:25:49] [INFO   ] Receive: CONNECT - Sender: 8 
[2023-10-25 14:25:56] [INFO   ] Receive: CONNECT - Sender: 3 
[2023-10-25 14:25:56] [INFO   ] Receive: CONNECT - Sender: 1 
[2023-10-25 14:25:56] [INFO   ] Receive: CONNECT - Sender: 2 
[2023-10-25 14:25:59] [INFO   ] Receive: PREPARE - Sender: 3, Receiver: 0, ID: 3, TS: -1 
[2023-10-25 14:25:59] [INFO   ] Broadcast: PREPARE - sender: 3 
[2023-10-25 14:25:59] [INFO   ] Send: INFORM - Receiver: 3 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 14:25:59] [INFO   ] Receive: PREPARE - Sender: 1, Receiver: 0, ID: 1, TS: -1 
[2023-10-25 14:25:59] [INFO   ] Broadcast: PREPARE - sender: 1 
[2023-10-25 14:25:59] [INFO   ] Send: INFORM - Receiver: 1 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 14:25:59] [INFO   ] Receive: PREPARE - Sender: 2, Receiver: 0, ID: 2, TS: -1 
[2023-10-25 14:25:59] [INFO   ] Broadcast: PREPARE - sender: 2 
[2023-10-25 14:25:59] [INFO   ] Send: INFORM - Receiver: 2 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 1, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 3, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 2, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 4, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 5, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 3, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 6, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 7, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 8, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 1, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 2, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 4, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 5, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 6, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 7, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 8, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 9, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 4, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 5, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 6, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 7, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 8, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 9, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 9, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 3, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 6, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 6, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 3, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 1, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 1, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 4, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 4, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 9, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 9, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 7, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 5, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 2, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 5, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 7, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 2, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 8, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 8, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Receive: NAK_PREPARE - Sender: 2, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: NAK_PREPARE - Sender: 2, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Receive: NAK_PREPARE - Sender: 3, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Receive: NAK_PREPARE - Sender: 9, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: NAK_PREPARE - Sender: 9, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: NAK_PREPARE - Sender: 3, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 5, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 6, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 6, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 5, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Receive: NAK_PREPARE - Sender: 1, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 4, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 4, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: NAK_PREPARE - Sender: 1, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 7, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 7, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 8, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 8, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 1, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 1, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Receive: NAK_PREPARE - Sender: 4, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: NAK_PREPARE - Sender: 4, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Receive: NAK_PREPARE - Sender: 9, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Send: NAK_PREPARE - Sender: 9, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 14:26:00] [INFO   ] Receive: NAK_PREPARE - Sender: 6, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: NAK_PREPARE - Sender: 6, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 3, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 3, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Receive: NAK_PREPARE - Sender: 8, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: NAK_PREPARE - Sender: 8, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Receive: NAK_PREPARE - Sender: 7, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: NAK_PREPARE - Sender: 7, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Receive: NAK_PREPARE - Sender: 5, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Send: NAK_PREPARE - Sender: 5, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 2, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 2, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 14:26:00] [INFO   ] Receive: PREPARE - Sender: 1, Receiver: 0, ID: 11, TS: -1 
[2023-10-25 14:26:00] [INFO   ] Broadcast: PREPARE - sender: 1 
[2023-10-25 14:26:00] [INFO   ] Send: INFORM - Receiver: 1 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 2, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 3, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 4, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 5, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 6, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 7, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 8, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 9, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 1, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 1, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 8, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 8, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 4, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 4, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 6, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 6, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 7, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 7, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 9, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 9, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Receive: PROMISE - Sender: 5, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:00] [INFO   ] Send: PROMISE - Sender: 5, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:01] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 2, ID: 11, TS: 3 
[2023-10-25 14:26:01] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 3, ID: 11, TS: 3 
[2023-10-25 14:26:02] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 2, ID: 11, TS: 3 
[2023-10-25 14:26:02] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 3, ID: 11, TS: 3 
[2023-10-25 14:26:03] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 2, ID: 11, TS: 3 
[2023-10-25 14:26:03] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 3, ID: 11, TS: 3 
[2023-10-25 14:26:04] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 2, ID: 11, TS: 3 
[2023-10-25 14:26:04] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 3, ID: 11, TS: 3 
[2023-10-25 14:26:05] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 2, ID: 11, TS: 3 
[2023-10-25 14:26:05] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 3, ID: 11, TS: 3 
[2023-10-25 14:26:06] [INFO   ] Send: NAK_PREPARE - Sender: 2, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:06] [INFO   ] Send: NAK_PREPARE - Sender: 3, Receiver: 1, ID: 11, TS: 3 
[2023-10-25 14:26:06] [INFO   ] Receive: PROPOSE - Sender: 1, Receiver: 0, ID: 11, Value: 1, TS: -1 
[2023-10-25 14:26:06] [INFO   ] Broadcast: PROPOSE - sender: 1 
[2023-10-25 14:26:06] [INFO   ] Send: INFORM - Receiver: 1 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 14:26:06] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 1, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:06] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 2, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:06] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 3, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:06] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 4, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:06] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 5, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:06] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 6, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:06] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 7, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:06] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 8, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:06] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 9, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:06] [INFO   ] Receive: ACCEPT - Sender: 9, Receiver: 1, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:06] [INFO   ] Accepted Entries: {1=-1, 2=-1, 3=-1, 4=-1, 5=-1, 6=-1, 7=-1, 8=-1, 9=1} 
[2023-10-25 14:26:06] [INFO   ] Receive: ACCEPT - Sender: 7, Receiver: 1, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:06] [INFO   ] Accepted Entries: {1=-1, 2=-1, 3=-1, 4=-1, 5=-1, 6=-1, 7=1, 8=-1, 9=1} 
[2023-10-25 14:26:06] [INFO   ] Receive: ACCEPT - Sender: 4, Receiver: 1, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:06] [INFO   ] Accepted Entries: {1=-1, 2=-1, 3=-1, 4=1, 5=-1, 6=-1, 7=1, 8=-1, 9=1} 
[2023-10-25 14:26:06] [INFO   ] Receive: ACCEPT - Sender: 8, Receiver: 1, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:06] [INFO   ] Accepted Entries: {1=-1, 2=-1, 3=-1, 4=1, 5=-1, 6=-1, 7=1, 8=1, 9=1} 
[2023-10-25 14:26:06] [INFO   ] Receive: ACCEPT - Sender: 1, Receiver: 1, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:06] [INFO   ] Accepted Entries: {1=1, 2=-1, 3=-1, 4=1, 5=-1, 6=-1, 7=1, 8=1, 9=1} 
[2023-10-25 14:26:06] [INFO   ] LEARN: SHUTDOWN - Value: 1 
[2023-10-25 14:26:06] [INFO   ] Receive: ACCEPT - Sender: 5, Receiver: 1, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:06] [INFO   ] Receive: ACCEPT - Sender: 6, Receiver: 1, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:06] [INFO   ] Accepted Entries: {1=1, 2=-1, 3=-1, 4=1, 5=1, 6=-1, 7=1, 8=1, 9=1} 
[2023-10-25 14:26:06] [INFO   ] LEARN: SHUTDOWN - Value: 1 
[2023-10-25 14:26:06] [INFO   ] Accepted Entries: {1=1, 2=-1, 3=-1, 4=1, 5=1, 6=1, 7=1, 8=1, 9=1} 
[2023-10-25 14:26:06] [INFO   ] LEARN: SHUTDOWN - Value: 1 
[2023-10-25 14:26:06] [INFO   ] Send: SHUTDOWN - Value: 1 
[2023-10-25 14:26:06] [INFO   ] Send: SHUTDOWN - Value: 1 
[2023-10-25 14:26:06] [INFO   ] Send: SHUTDOWN - Value: 1 
[2023-10-25 14:26:06] [INFO   ] Send: SHUTDOWN - Value: 1 
[2023-10-25 14:26:06] [INFO   ] Send: SHUTDOWN - Value: 1 
[2023-10-25 14:26:06] [INFO   ] Send: SHUTDOWN - Value: 1 
[2023-10-25 14:26:06] [INFO   ] Send: SHUTDOWN - Value: 1 
[2023-10-25 14:26:06] [INFO   ] Send: SHUTDOWN - Value: 1 
[2023-10-25 14:26:06] [INFO   ] Send: SHUTDOWN - Value: 1 
[2023-10-25 14:26:07] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 2, ID: 11, Value: 1, TS: 4 
[2023-10-25 14:26:07] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 3, ID: 11, Value: 1, TS: 4 
```

## Test scenario 4 - When M1-M9 respond after small to moderate delay (within the range of retry and wait time from server)

### How to run 

```bash
make central_registry
```

Open the second terminal, set up all acceptors

```bash 
make acceptors REPLY_MIN=100 REPLY_MAX=1050
```

Open the third terminal, run two proposers:

```bash 
make three_proposers_canonical
```

### What happens
Each acceptors now have a 5% chance of not responding to a message on time. Which means for every round of message, there is a
`1 - (1 - 0.05)^6 = 25%`chance of at least one acceptor not responding on time. The broadcast server will retry sending the message.
Since the protocol is TCP, all messages are guaranteed to be delivered in order, so the result should be transparent to the user.
The probability that the same set of failed messages will fail all 5 times is `0.25^5` so we can expect that this still works as usual.
In the log below, some messages were too slow and thus were resend as a result.

### Log results 

```bash
[2023-10-25 15:27:28] [INFO   ] Server started on port 12345 
[2023-10-25 15:27:41] [INFO   ] Receive: CONNECT - Sender: 8 
[2023-10-25 15:27:41] [INFO   ] Receive: CONNECT - Sender: 9 
[2023-10-25 15:27:41] [INFO   ] Receive: CONNECT - Sender: 6 
[2023-10-25 15:27:42] [INFO   ] Receive: CONNECT - Sender: 4 
[2023-10-25 15:27:42] [INFO   ] Receive: CONNECT - Sender: 5 
[2023-10-25 15:27:42] [INFO   ] Receive: CONNECT - Sender: 7 
[2023-10-25 15:28:01] [INFO   ] Receive: CONNECT - Sender: 1 
[2023-10-25 15:28:01] [INFO   ] Receive: CONNECT - Sender: 2 
[2023-10-25 15:28:01] [INFO   ] Receive: CONNECT - Sender: 3 
[2023-10-25 15:28:04] [INFO   ] Receive: PREPARE - Sender: 1, Receiver: 0, ID: 1, TS: -1 
[2023-10-25 15:28:04] [INFO   ] Broadcast: PREPARE - sender: 1 
[2023-10-25 15:28:04] [INFO   ] Send: INFORM - Receiver: 1 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 2, ID: 1, TS: 0 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 3, ID: 1, TS: 0 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 4, ID: 1, TS: 0 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 5, ID: 1, TS: 0 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 6, ID: 1, TS: 0 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 7, ID: 1, TS: 0 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 8, ID: 1, TS: 0 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 9, ID: 1, TS: 0 
[2023-10-25 15:28:04] [INFO   ] Receive: PREPARE - Sender: 2, Receiver: 0, ID: 2, TS: -1 
[2023-10-25 15:28:04] [INFO   ] Broadcast: PREPARE - sender: 2 
[2023-10-25 15:28:04] [INFO   ] Send: INFORM - Receiver: 2 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 1, ID: 2, TS: 1 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 3, ID: 2, TS: 1 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 4, ID: 2, TS: 1 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 5, ID: 2, TS: 1 
[2023-10-25 15:28:04] [INFO   ] Receive: PREPARE - Sender: 3, Receiver: 0, ID: 3, TS: -1 
[2023-10-25 15:28:04] [INFO   ] Broadcast: PREPARE - sender: 3 
[2023-10-25 15:28:04] [INFO   ] Send: INFORM - Receiver: 3 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 1, ID: 3, TS: 2 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 6, ID: 2, TS: 1 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 7, ID: 2, TS: 1 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 8, ID: 2, TS: 1 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 9, ID: 2, TS: 1 
[2023-10-25 15:28:04] [INFO   ] Receive: PROMISE - Sender: 2, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:04] [INFO   ] Send: PROMISE - Sender: 2, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 2, ID: 3, TS: 2 
[2023-10-25 15:28:04] [INFO   ] Receive: PROMISE - Sender: 1, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:04] [INFO   ] Send: PROMISE - Sender: 1, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 4, ID: 3, TS: 2 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 5, ID: 3, TS: 2 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 6, ID: 3, TS: 2 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 7, ID: 3, TS: 2 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 8, ID: 3, TS: 2 
[2023-10-25 15:28:04] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 9, ID: 3, TS: 2 
[2023-10-25 15:28:04] [INFO   ] Receive: PROMISE - Sender: 3, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:04] [INFO   ] Send: PROMISE - Sender: 3, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:04] [INFO   ] Receive: PROMISE - Sender: 1, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:04] [INFO   ] Send: PROMISE - Sender: 1, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:04] [INFO   ] Receive: PROMISE - Sender: 2, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:04] [INFO   ] Send: PROMISE - Sender: 2, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:05] [INFO   ] Receive: PROMISE - Sender: 3, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:05] [INFO   ] Send: PROMISE - Sender: 3, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:05] [INFO   ] Receive: PROMISE - Sender: 1, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:05] [INFO   ] Send: PROMISE - Sender: 1, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:05] [INFO   ] Receive: PROMISE - Sender: 2, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:05] [INFO   ] Send: PROMISE - Sender: 2, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:05] [INFO   ] Receive: PROMISE - Sender: 5, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:05] [INFO   ] Send: PROMISE - Sender: 5, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:05] [INFO   ] Receive: PROMISE - Sender: 3, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:05] [INFO   ] Send: PROMISE - Sender: 3, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:05] [INFO   ] Receive: PROMISE - Sender: 6, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:05] [INFO   ] Send: PROMISE - Sender: 6, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:05] [INFO   ] Receive: PROMISE - Sender: 5, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:05] [INFO   ] Send: PROMISE - Sender: 5, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:05] [INFO   ] Receive: PROMISE - Sender: 7, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:05] [INFO   ] Send: PROMISE - Sender: 7, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:05] [INFO   ] Receive: PROMISE - Sender: 8, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:05] [INFO   ] Send: PROMISE - Sender: 8, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:05] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 4, ID: 1, TS: 0 
[2023-10-25 15:28:05] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 9, ID: 1, TS: 0 
[2023-10-25 15:28:05] [INFO   ] Receive: PROMISE - Sender: 4, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:05] [INFO   ] Send: PROMISE - Sender: 4, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:05] [INFO   ] Receive: PROMISE - Sender: 7, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:05] [INFO   ] Send: PROMISE - Sender: 7, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:05] [INFO   ] Receive: PROMISE - Sender: 9, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:05] [INFO   ] Send: PROMISE - Sender: 9, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:05] [INFO   ] Resend: PREPARE - Sender: 2, Receiver: 4, ID: 2, TS: 1 
[2023-10-25 15:28:05] [INFO   ] Resend: PREPARE - Sender: 2, Receiver: 6, ID: 2, TS: 1 
[2023-10-25 15:28:05] [INFO   ] Receive: PROMISE - Sender: 4, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:05] [INFO   ] Send: PROMISE - Sender: 4, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:05] [INFO   ] Resend: PREPARE - Sender: 2, Receiver: 8, ID: 2, TS: 1 
[2023-10-25 15:28:05] [INFO   ] Resend: PREPARE - Sender: 2, Receiver: 9, ID: 2, TS: 1 
[2023-10-25 15:28:05] [INFO   ] Resend: PREPARE - Sender: 3, Receiver: 4, ID: 3, TS: 2 
[2023-10-25 15:28:05] [INFO   ] Resend: PREPARE - Sender: 3, Receiver: 5, ID: 3, TS: 2 
[2023-10-25 15:28:05] [INFO   ] Resend: PREPARE - Sender: 3, Receiver: 6, ID: 3, TS: 2 
[2023-10-25 15:28:05] [INFO   ] Resend: PREPARE - Sender: 3, Receiver: 7, ID: 3, TS: 2 
[2023-10-25 15:28:05] [INFO   ] Resend: PREPARE - Sender: 3, Receiver: 8, ID: 3, TS: 2 
[2023-10-25 15:28:05] [INFO   ] Resend: PREPARE - Sender: 3, Receiver: 9, ID: 3, TS: 2 
[2023-10-25 15:28:05] [INFO   ] Receive: PROMISE - Sender: 6, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:05] [INFO   ] Send: PROMISE - Sender: 6, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:05] [INFO   ] Receive: PROMISE - Sender: 5, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:05] [INFO   ] Send: PROMISE - Sender: 5, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:05] [INFO   ] Receive: PROPOSE - Sender: 1, Receiver: 0, ID: 1, Value: 1, TS: -1 
[2023-10-25 15:28:05] [INFO   ] Broadcast: PROPOSE - sender: 1 
[2023-10-25 15:28:05] [INFO   ] Send: INFORM - Receiver: 1 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 15:28:05] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 1, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:28:05] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 2, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:28:06] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 3, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:28:05] [INFO   ] Receive: PROMISE - Sender: 9, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:06] [INFO   ] Send: PROMISE - Sender: 9, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:06] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 4, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:28:06] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 5, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:28:06] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 6, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:28:06] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 7, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:28:06] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 8, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:28:06] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 9, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:28:06] [INFO   ] Receive: PROMISE - Sender: 4, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:06] [INFO   ] Send: PROMISE - Sender: 4, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:06] [INFO   ] Receive: PROMISE - Sender: 7, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:06] [INFO   ] Send: PROMISE - Sender: 7, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:06] [INFO   ] Receive: NAK_PROPOSE - Sender: 1, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:28:06] [INFO   ] Receive: NAK_PROPOSE - Sender: 3, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:28:06] [INFO   ] Receive: NAK_PREPARE - Sender: 5, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:06] [INFO   ] Send: NAK_PREPARE - Sender: 5, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:06] [INFO   ] Receive: PROMISE - Sender: 9, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:06] [INFO   ] Send: PROMISE - Sender: 9, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:06] [INFO   ] Receive: NAK_PROPOSE - Sender: 2, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:28:06] [INFO   ] Receive: NAK_PREPARE - Sender: 9, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:06] [INFO   ] Send: NAK_PREPARE - Sender: 9, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:06] [INFO   ] Receive: PROMISE - Sender: 6, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:06] [INFO   ] Send: PROMISE - Sender: 6, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:06] [INFO   ] Receive: NAK_PROPOSE - Sender: 5, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:28:06] [INFO   ] Receive: PROMISE - Sender: 8, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:06] [INFO   ] Send: PROMISE - Sender: 8, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:06] [INFO   ] Receive: NAK_PREPARE - Sender: 4, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:06] [INFO   ] Send: NAK_PREPARE - Sender: 4, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:28:06] [INFO   ] Receive: NAK_PREPARE - Sender: 9, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:06] [INFO   ] Send: NAK_PREPARE - Sender: 9, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:06] [INFO   ] Receive: NAK_PREPARE - Sender: 4, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:06] [INFO   ] Send: NAK_PREPARE - Sender: 4, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:06] [INFO   ] Resend: PREPARE - Sender: 3, Receiver: 8, ID: 3, TS: 2 
[2023-10-25 15:28:06] [INFO   ] Receive: NAK_PREPARE - Sender: 4, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:06] [INFO   ] Send: NAK_PREPARE - Sender: 4, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:06] [INFO   ] Receive: PROMISE - Sender: 8, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:06] [INFO   ] Receive: NAK_PREPARE - Sender: 7, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:06] [INFO   ] Send: PROMISE - Sender: 8, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:06] [INFO   ] Send: NAK_PREPARE - Sender: 7, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:07] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 4, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:28:07] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 6, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:28:07] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 7, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:28:07] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 8, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:28:07] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 9, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:28:07] [INFO   ] Receive: NAK_PREPARE - Sender: 8, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:07] [INFO   ] Send: NAK_PREPARE - Sender: 8, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:07] [INFO   ] Receive: NAK_PREPARE - Sender: 9, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:07] [INFO   ] Send: NAK_PREPARE - Sender: 9, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:07] [INFO   ] Receive: NAK_PROPOSE - Sender: 4, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:28:07] [INFO   ] Receive: NAK_PREPARE - Sender: 6, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:07] [INFO   ] Send: NAK_PREPARE - Sender: 6, Receiver: 2, ID: 2, TS: 1 
[2023-10-25 15:28:07] [INFO   ] Receive: NAK_PROPOSE - Sender: 9, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:28:07] [INFO   ] Receive: NAK_PREPARE - Sender: 8, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:07] [INFO   ] Send: NAK_PREPARE - Sender: 8, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:07] [INFO   ] Receive: NAK_PROPOSE - Sender: 9, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:28:07] [INFO   ] Receive: NAK_PROPOSE - Sender: 4, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:28:07] [INFO   ] Receive: NAK_PROPOSE - Sender: 7, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:28:07] [INFO   ] Receive: NAK_PROPOSE - Sender: 8, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:28:07] [INFO   ] Receive: NAK_PREPARE - Sender: 8, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:07] [INFO   ] Send: NAK_PREPARE - Sender: 8, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:08] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 6, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:28:08] [INFO   ] Receive: NAK_PROPOSE - Sender: 8, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:28:08] [INFO   ] Receive: NAK_PREPARE - Sender: 6, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:08] [INFO   ] Send: NAK_PREPARE - Sender: 6, Receiver: 3, ID: 3, TS: 2 
[2023-10-25 15:28:08] [INFO   ] Receive: NAK_PROPOSE - Sender: 7, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:28:08] [INFO   ] Receive: PROPOSE - Sender: 3, Receiver: 0, ID: 3, Value: 3, TS: -1 
[2023-10-25 15:28:08] [INFO   ] Broadcast: PROPOSE - sender: 3 
[2023-10-25 15:28:08] [INFO   ] Send: INFORM - Receiver: 3 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 1, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 2, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 3, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 4, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 5, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 6, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 7, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 8, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 9, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:08] [INFO   ] Receive: PROPOSE - Sender: 2, Receiver: 0, ID: 2, Value: 2, TS: -1 
[2023-10-25 15:28:08] [INFO   ] Broadcast: PROPOSE - sender: 2 
[2023-10-25 15:28:08] [INFO   ] Send: INFORM - Receiver: 2 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 1, ID: 2, Value: 2, TS: 5 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 2, ID: 2, Value: 2, TS: 5 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 3, ID: 2, Value: 2, TS: 5 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 4, ID: 2, Value: 2, TS: 5 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 5, ID: 2, Value: 2, TS: 5 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 6, ID: 2, Value: 2, TS: 5 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 7, ID: 2, Value: 2, TS: 5 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 8, ID: 2, Value: 2, TS: 5 
[2023-10-25 15:28:08] [INFO   ] Send: PROPOSE - Sender: 2, Receiver: 9, ID: 2, Value: 2, TS: 5 
[2023-10-25 15:28:08] [INFO   ] Receive: NAK_PROPOSE - Sender: 6, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:28:08] [INFO   ] Receive: ACCEPT - Sender: 1, Receiver: 3, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:08] [INFO   ] Accepted Entries: {1=3, 2=-1, 3=-1, 4=-1, 5=-1, 6=-1, 7=-1, 8=-1, 9=-1} 
[2023-10-25 15:28:08] [INFO   ] Receive: ACCEPT - Sender: 2, Receiver: 3, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:08] [INFO   ] Receive: ACCEPT - Sender: 3, Receiver: 3, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:08] [INFO   ] Accepted Entries: {1=3, 2=3, 3=-1, 4=-1, 5=-1, 6=-1, 7=-1, 8=-1, 9=-1} 
[2023-10-25 15:28:08] [INFO   ] Accepted Entries: {1=3, 2=3, 3=3, 4=-1, 5=-1, 6=-1, 7=-1, 8=-1, 9=-1} 
[2023-10-25 15:28:08] [INFO   ] Receive: NAK_PROPOSE - Sender: 1, Receiver: 2, ID: 2, Value: 2 TS: 5 
[2023-10-25 15:28:08] [INFO   ] Receive: NAK_PROPOSE - Sender: 3, Receiver: 2, ID: 2, Value: 2 TS: 5 
[2023-10-25 15:28:09] [INFO   ] Receive: NAK_PROPOSE - Sender: 2, Receiver: 2, ID: 2, Value: 2 TS: 5 
[2023-10-25 15:28:09] [INFO   ] Receive: ACCEPT - Sender: 8, Receiver: 3, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:09] [INFO   ] Accepted Entries: {1=3, 2=3, 3=3, 4=-1, 5=-1, 6=-1, 7=-1, 8=3, 9=-1} 
[2023-10-25 15:28:09] [INFO   ] Receive: ACCEPT - Sender: 4, Receiver: 3, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:09] [INFO   ] Accepted Entries: {1=3, 2=3, 3=3, 4=3, 5=-1, 6=-1, 7=-1, 8=3, 9=-1} 
[2023-10-25 15:28:09] [INFO   ] LEARN: SHUTDOWN - Value: 3 
[2023-10-25 15:28:09] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:28:09] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:28:09] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:28:09] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:28:09] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:28:09] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:28:09] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:28:09] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:28:09] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:28:09] [INFO   ] Resend: PROPOSE - Sender: 3, Receiver: 5, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:09] [INFO   ] Resend: PROPOSE - Sender: 3, Receiver: 6, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:09] [INFO   ] Resend: PROPOSE - Sender: 3, Receiver: 7, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:09] [INFO   ] Resend: PROPOSE - Sender: 3, Receiver: 9, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:28:09] [INFO   ] Resend: PROPOSE - Sender: 2, Receiver: 4, ID: 2, Value: 2, TS: 5 
[2023-10-25 15:28:09] [INFO   ] Resend: PROPOSE - Sender: 2, Receiver: 5, ID: 2, Value: 2, TS: 5 
[2023-10-25 15:28:09] [INFO   ] Resend: PROPOSE - Sender: 2, Receiver: 6, ID: 2, Value: 2, TS: 5 
[2023-10-25 15:28:09] [INFO   ] Resend: PROPOSE - Sender: 2, Receiver: 7, ID: 2, Value: 2, TS: 5 
[2023-10-25 15:28:09] [INFO   ] Resend: PROPOSE - Sender: 2, Receiver: 8, ID: 2, Value: 2, TS: 5 
[2023-10-25 15:28:09] [INFO   ] Resend: PROPOSE - Sender: 2, Receiver: 9, ID: 2, Value: 2, TS: 5 
```


## Test scenario 4 - When M1-M9 may take a long time to respond (or going offline)

### How to run

```bash
make central_registry
```

Open the second terminal, set up all acceptors

```bash 
make acceptors_with_drop_out REPLY_MIN=100 REPLY_MAX=1050
```

Open the third terminal, run two proposers:

```bash 
make three_proposers_canonical
```


### What happens

One of the acceptors is guaranteed to not respond on time. This should not affect the quorum as a consensus is still reached 
by the majority 

### Log results 
In this realization below, acceptor 9 will always timeout and thus the broadcast server automatically send NAK. 

```bash
[2023-10-25 15:36:01] [INFO   ] Server started on port 12345 
[2023-10-25 15:36:09] [INFO   ] Receive: CONNECT - Sender: 5 
[2023-10-25 15:36:09] [INFO   ] Receive: CONNECT - Sender: 9 
[2023-10-25 15:36:09] [INFO   ] Receive: CONNECT - Sender: 4 
[2023-10-25 15:36:09] [INFO   ] Receive: CONNECT - Sender: 8 
[2023-10-25 15:36:09] [INFO   ] Receive: CONNECT - Sender: 7 
[2023-10-25 15:36:09] [INFO   ] Receive: CONNECT - Sender: 6 
[2023-10-25 15:37:22] [INFO   ] Receive: CONNECT - Sender: 1 
[2023-10-25 15:37:22] [INFO   ] Receive: CONNECT - Sender: 3 
[2023-10-25 15:37:22] [INFO   ] Receive: CONNECT - Sender: 2 
[2023-10-25 15:37:25] [INFO   ] Receive: PREPARE - Sender: 1, Receiver: 0, ID: 1, TS: -1 
[2023-10-25 15:37:25] [INFO   ] Broadcast: PREPARE - sender: 1 
[2023-10-25 15:37:25] [INFO   ] Send: INFORM - Receiver: 1 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 2, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 3, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 4, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 5, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 6, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 7, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 8, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 1, Receiver: 9, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Receive: PROMISE - Sender: 1, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Send: PROMISE - Sender: 1, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Receive: PREPARE - Sender: 3, Receiver: 0, ID: 3, TS: -1 
[2023-10-25 15:37:25] [INFO   ] Broadcast: PREPARE - sender: 3 
[2023-10-25 15:37:25] [INFO   ] Send: INFORM - Receiver: 3 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 15:37:25] [INFO   ] Receive: PROMISE - Sender: 2, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Send: PROMISE - Sender: 2, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 1, ID: 3, TS: 1 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 2, ID: 3, TS: 1 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 4, ID: 3, TS: 1 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 5, ID: 3, TS: 1 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 6, ID: 3, TS: 1 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 7, ID: 3, TS: 1 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 8, ID: 3, TS: 1 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 3, Receiver: 9, ID: 3, TS: 1 
[2023-10-25 15:37:25] [INFO   ] Receive: PROMISE - Sender: 3, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Send: PROMISE - Sender: 3, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Receive: PREPARE - Sender: 2, Receiver: 0, ID: 2, TS: -1 
[2023-10-25 15:37:25] [INFO   ] Broadcast: PREPARE - sender: 2 
[2023-10-25 15:37:25] [INFO   ] Send: INFORM - Receiver: 2 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 1, ID: 2, TS: 2 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 3, ID: 2, TS: 2 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 4, ID: 2, TS: 2 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 5, ID: 2, TS: 2 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 6, ID: 2, TS: 2 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 7, ID: 2, TS: 2 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 8, ID: 2, TS: 2 
[2023-10-25 15:37:25] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 9, ID: 2, TS: 2 
[2023-10-25 15:37:25] [INFO   ] Receive: PROMISE - Sender: 3, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:25] [INFO   ] Send: PROMISE - Sender: 3, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:25] [INFO   ] Receive: PROMISE - Sender: 2, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:25] [INFO   ] Send: PROMISE - Sender: 2, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:25] [INFO   ] Receive: PROMISE - Sender: 1, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:25] [INFO   ] Send: PROMISE - Sender: 1, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:25] [INFO   ] Receive: PROMISE - Sender: 7, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Send: PROMISE - Sender: 7, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Receive: PROMISE - Sender: 4, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Receive: NAK_PREPARE - Sender: 3, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:25] [INFO   ] Send: NAK_PREPARE - Sender: 3, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:25] [INFO   ] Receive: NAK_PREPARE - Sender: 1, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:25] [INFO   ] Send: NAK_PREPARE - Sender: 1, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:25] [INFO   ] Receive: NAK_PREPARE - Sender: 2, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:25] [INFO   ] Send: NAK_PREPARE - Sender: 2, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:25] [INFO   ] Send: PROMISE - Sender: 4, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Receive: PROMISE - Sender: 8, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:25] [INFO   ] Send: PROMISE - Sender: 8, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:26] [INFO   ] Receive: PROMISE - Sender: 4, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:26] [INFO   ] Send: PROMISE - Sender: 4, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:26] [INFO   ] Receive: PROMISE - Sender: 5, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:26] [INFO   ] Send: PROMISE - Sender: 5, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:26] [INFO   ] Receive: PROMISE - Sender: 6, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:26] [INFO   ] Send: PROMISE - Sender: 6, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:26] [INFO   ] Receive: PROMISE - Sender: 8, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:26] [INFO   ] Send: PROMISE - Sender: 8, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:26] [INFO   ] Receive: NAK_PREPARE - Sender: 4, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:26] [INFO   ] Send: NAK_PREPARE - Sender: 4, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:26] [INFO   ] Receive: PROMISE - Sender: 7, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:26] [INFO   ] Send: PROMISE - Sender: 7, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:26] [INFO   ] Resend: PREPARE - Sender: 1, Receiver: 9, ID: 1, TS: 0 
[2023-10-25 15:37:26] [INFO   ] Receive: NAK_PREPARE - Sender: 7, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:26] [INFO   ] Send: NAK_PREPARE - Sender: 7, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:26] [INFO   ] Receive: PROMISE - Sender: 6, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:26] [INFO   ] Send: PROMISE - Sender: 6, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:26] [INFO   ] Resend: PREPARE - Sender: 3, Receiver: 5, ID: 3, TS: 1 
[2023-10-25 15:37:26] [INFO   ] Resend: PREPARE - Sender: 3, Receiver: 9, ID: 3, TS: 1 
[2023-10-25 15:37:26] [INFO   ] Resend: PREPARE - Sender: 2, Receiver: 5, ID: 2, TS: 2 
[2023-10-25 15:37:26] [INFO   ] Resend: PREPARE - Sender: 2, Receiver: 6, ID: 2, TS: 2 
[2023-10-25 15:37:26] [INFO   ] Resend: PREPARE - Sender: 2, Receiver: 8, ID: 2, TS: 2 
[2023-10-25 15:37:26] [INFO   ] Resend: PREPARE - Sender: 2, Receiver: 9, ID: 2, TS: 2 
[2023-10-25 15:37:26] [INFO   ] Receive: NAK_PREPARE - Sender: 8, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:26] [INFO   ] Send: NAK_PREPARE - Sender: 8, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:26] [INFO   ] Receive: NAK_PREPARE - Sender: 6, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:26] [INFO   ] Send: NAK_PREPARE - Sender: 6, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:26] [INFO   ] Receive: PROMISE - Sender: 5, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:26] [INFO   ] Send: PROMISE - Sender: 5, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:26] [INFO   ] Receive: PROMISE - Sender: 9, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:26] [INFO   ] Send: PROMISE - Sender: 9, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:26] [INFO   ] Receive: NAK_PREPARE - Sender: 5, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:26] [INFO   ] Send: NAK_PREPARE - Sender: 5, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:26] [INFO   ] Receive: PROPOSE - Sender: 1, Receiver: 0, ID: 1, Value: 1, TS: -1 
[2023-10-25 15:37:26] [INFO   ] Broadcast: PROPOSE - sender: 1 
[2023-10-25 15:37:26] [INFO   ] Send: INFORM - Receiver: 1 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 15:37:26] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 1, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:37:26] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 2, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:37:26] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 3, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:37:26] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 4, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:37:26] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 5, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:37:26] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 6, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:37:26] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 7, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:37:26] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 8, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:37:26] [INFO   ] Send: PROPOSE - Sender: 1, Receiver: 9, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:37:27] [INFO   ] Receive: NAK_PROPOSE - Sender: 3, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:37:27] [INFO   ] Receive: NAK_PROPOSE - Sender: 1, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:37:27] [INFO   ] Receive: NAK_PROPOSE - Sender: 2, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:37:27] [INFO   ] Receive: NAK_PREPARE - Sender: 5, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:27] [INFO   ] Send: NAK_PREPARE - Sender: 5, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:27] [INFO   ] Receive: NAK_PREPARE - Sender: 8, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:27] [INFO   ] Send: NAK_PREPARE - Sender: 8, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:27] [INFO   ] Resend: PREPARE - Sender: 3, Receiver: 9, ID: 3, TS: 1 
[2023-10-25 15:37:27] [INFO   ] Resend: PREPARE - Sender: 2, Receiver: 9, ID: 2, TS: 2 
[2023-10-25 15:37:27] [INFO   ] Receive: NAK_PROPOSE - Sender: 7, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:37:27] [INFO   ] Receive: NAK_PREPARE - Sender: 6, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:27] [INFO   ] Send: NAK_PREPARE - Sender: 6, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:27] [INFO   ] Receive: PROMISE - Sender: 9, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:27] [INFO   ] Send: PROMISE - Sender: 9, Receiver: 3, ID: 3, TS: 1 
[2023-10-25 15:37:27] [INFO   ] Receive: NAK_PROPOSE - Sender: 8, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:37:27] [INFO   ] Receive: NAK_PROPOSE - Sender: 4, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:37:27] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 5, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:37:27] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 6, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:37:27] [INFO   ] Receive: NAK_PROPOSE - Sender: 6, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:37:27] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 9, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:37:28] [INFO   ] Receive: NAK_PROPOSE - Sender: 6, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:37:28] [INFO   ] Receive: NAK_PREPARE - Sender: 5, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:28] [INFO   ] Send: NAK_PREPARE - Sender: 5, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:28] [INFO   ] Resend: PREPARE - Sender: 2, Receiver: 9, ID: 2, TS: 2 
[2023-10-25 15:37:28] [INFO   ] Receive: NAK_PROPOSE - Sender: 5, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:37:28] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 9, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:37:29] [INFO   ] Receive: NAK_PREPARE - Sender: 9, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:29] [INFO   ] Send: NAK_PREPARE - Sender: 9, Receiver: 2, ID: 2, TS: 2 
[2023-10-25 15:37:29] [INFO   ] Receive: NAK_PROPOSE - Sender: 5, Receiver: 1, ID: 1, Value: 1 TS: 3 
[2023-10-25 15:37:29] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 9, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:37:30] [INFO   ] Receive: PROPOSE - Sender: 3, Receiver: 0, ID: 3, Value: 3, TS: -1 
[2023-10-25 15:37:30] [INFO   ] Broadcast: PROPOSE - sender: 3 
[2023-10-25 15:37:30] [INFO   ] Send: INFORM - Receiver: 3 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 15:37:30] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 1, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:30] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 2, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:30] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 3, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:30] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 4, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:30] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 5, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:30] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 6, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:30] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 7, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:30] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 8, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:30] [INFO   ] Send: PROPOSE - Sender: 3, Receiver: 9, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:30] [INFO   ] Receive: ACCEPT - Sender: 1, Receiver: 3, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:30] [INFO   ] Receive: ACCEPT - Sender: 3, Receiver: 3, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:30] [INFO   ] Accepted Entries: {1=3, 2=-1, 3=-1, 4=-1, 5=-1, 6=-1, 7=-1, 8=-1, 9=-1} 
[2023-10-25 15:37:30] [INFO   ] Accepted Entries: {1=3, 2=-1, 3=3, 4=-1, 5=-1, 6=-1, 7=-1, 8=-1, 9=-1} 
[2023-10-25 15:37:30] [INFO   ] Receive: NAK_PREPARE - Sender: 9, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:30] [INFO   ] Send: NAK_PREPARE - Sender: 9, Receiver: 1, ID: 1, TS: 0 
[2023-10-25 15:37:30] [INFO   ] Receive: ACCEPT - Sender: 6, Receiver: 3, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:30] [INFO   ] Accepted Entries: {1=3, 2=-1, 3=3, 4=-1, 5=-1, 6=3, 7=-1, 8=-1, 9=-1} 
[2023-10-25 15:37:30] [INFO   ] Receive: PREPARE - Sender: 2, Receiver: 0, ID: 12, TS: -1 
[2023-10-25 15:37:30] [INFO   ] Broadcast: PREPARE - sender: 2 
[2023-10-25 15:37:30] [INFO   ] Send: INFORM - Receiver: 2 [1, 2, 3, 4, 5, 6, 7, 8, 9] 
[2023-10-25 15:37:30] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 1, ID: 12, TS: 5 
[2023-10-25 15:37:30] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 2, ID: 12, TS: 5 
[2023-10-25 15:37:30] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 3, ID: 12, TS: 5 
[2023-10-25 15:37:30] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 4, ID: 12, TS: 5 
[2023-10-25 15:37:30] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 5, ID: 12, TS: 5 
[2023-10-25 15:37:30] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 6, ID: 12, TS: 5 
[2023-10-25 15:37:30] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 7, ID: 12, TS: 5 
[2023-10-25 15:37:30] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 8, ID: 12, TS: 5 
[2023-10-25 15:37:30] [INFO   ] Send: PREPARE - Sender: 2, Receiver: 9, ID: 12, TS: 5 
[2023-10-25 15:37:30] [INFO   ] Receive: ACCEPT - Sender: 2, Receiver: 3, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:30] [INFO   ] Accepted Entries: {1=3, 2=3, 3=3, 4=-1, 5=-1, 6=3, 7=-1, 8=-1, 9=-1} 
[2023-10-25 15:37:30] [INFO   ] Receive: PROMISE - Sender: 1, Receiver: 2, ID: 12, aID: 3, aVal: 3, TS: 5 
[2023-10-25 15:37:30] [INFO   ] Send: PROMISE - Sender: 1, Receiver: 2, ID: 12, aID: 3, aVal: 3, TS: 5 
[2023-10-25 15:37:30] [INFO   ] Receive: PROMISE - Sender: 3, Receiver: 2, ID: 12, aID: 3, aVal: 3, TS: 5 
[2023-10-25 15:37:30] [INFO   ] Send: PROMISE - Sender: 3, Receiver: 2, ID: 12, aID: 3, aVal: 3, TS: 5 
[2023-10-25 15:37:30] [INFO   ] Receive: PROMISE - Sender: 2, Receiver: 2, ID: 12, aID: 3, aVal: 3, TS: 5 
[2023-10-25 15:37:30] [INFO   ] Send: PROMISE - Sender: 2, Receiver: 2, ID: 12, aID: 3, aVal: 3, TS: 5 
[2023-10-25 15:37:30] [INFO   ] Receive: ACCEPT - Sender: 7, Receiver: 3, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:30] [INFO   ] Accepted Entries: {1=3, 2=3, 3=3, 4=-1, 5=-1, 6=3, 7=3, 8=-1, 9=-1} 
[2023-10-25 15:37:30] [INFO   ] LEARN: SHUTDOWN - Value: 3 
[2023-10-25 15:37:30] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:37:30] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:37:30] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:37:30] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:37:30] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:37:30] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:37:30] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:37:30] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:37:30] [INFO   ] Send: SHUTDOWN - Value: 3 
[2023-10-25 15:37:30] [INFO   ] Resend: PROPOSE - Sender: 1, Receiver: 9, ID: 1, Value: 1, TS: 3 
[2023-10-25 15:37:31] [INFO   ] Resend: PROPOSE - Sender: 3, Receiver: 4, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:31] [INFO   ] Resend: PROPOSE - Sender: 3, Receiver: 5, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:31] [INFO   ] Resend: PROPOSE - Sender: 3, Receiver: 8, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:31] [INFO   ] Resend: PROPOSE - Sender: 3, Receiver: 9, ID: 3, Value: 3, TS: 4 
[2023-10-25 15:37:31] [INFO   ] Resend: PREPARE - Sender: 2, Receiver: 4, ID: 12, TS: 5 
[2023-10-25 15:37:31] [INFO   ] Resend: PREPARE - Sender: 2, Receiver: 5, ID: 12, TS: 5 
[2023-10-25 15:37:31] [INFO   ] Resend: PREPARE - Sender: 2, Receiver: 6, ID: 12, TS: 5 
[2023-10-25 15:37:31] [INFO   ] Resend: PREPARE - Sender: 2, Receiver: 7, ID: 12, TS: 5 
[2023-10-25 15:37:31] [INFO   ] Resend: PREPARE - Sender: 2, Receiver: 8, ID: 12, TS: 5 
[2023-10-25 15:37:31] [INFO   ] Resend: PREPARE - Sender: 2, Receiver: 9, ID: 12, TS: 5 

```