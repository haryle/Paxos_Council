package utils;

import utils.helpers.Message;

import static org.junit.jupiter.api.Assertions.*;

class AcceptorTest {
    Acceptor acceptor;

    int from = 2;
    int to = 4;

    int lowID = 3;
    int highID = 5;
    int lowIDValue = 11;
    int highIDValue = 12;

    Message prepareLowID = Message.prepare(from, to, lowID);

    Message prepareHighID = Message.prepare(from, to, highID);

    Message proposeLowID = Message.propose(from, to, lowID, lowIDValue);

    Message proposeHighID = Message.propose(from, to, highID, highIDValue);

    Message promiseNoID = Message.promise(to, from, lowID, -1);

    Message promiseWithID = Message.promise(to, from, highID, lowID, lowIDValue, -1);

    Message reject = Message
}