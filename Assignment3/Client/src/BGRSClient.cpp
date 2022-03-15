#include <iostream>
#include <thread>
#include <mutex>
#include "../include/connectionHandler.h"
#include "../include/readKeyboard.h"
using namespace std;

int main (int argc, char *argv[]) {
    if (argc < 3) {
        cerr << "Usage: " << argv[0] << " host port" << endl << endl;
        return -1;
    }
    cout << "connected to server" << endl;
    string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler handler(host, port);
    if (!handler.connect()) {
        cerr << "Cannot connect to " << host << ":" << port << endl;
        return 1;
    }

    mutex mutex;
    BGRSEncoderDecoder receiveSocket(handler, mutex);
    readKeyboard sendSocket(handler, mutex);

    //reads from keyboard, encodes what it reads, sends that it encoded
    thread sendSocketThread(&readKeyboard::operator(), sendSocket);
    //receives from server, decodes, prints
    thread receiveSocketThread(&BGRSEncoderDecoder::operator(), receiveSocket);

    receiveSocketThread.join();
    sendSocketThread.join();
    return  0;
}