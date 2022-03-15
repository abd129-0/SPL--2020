#include "../include/readKeyboard.h"
#include "../include/connectionHandler.h"
using namespace std;

readKeyboard::readKeyboard(ConnectionHandler &_handler, mutex &mutex):encdec(BGRSEncoderDecoder(_handler, mutex)),handler(_handler), _mutex(mutex){}

void readKeyboard::operator()()  {
    while (!handler.ShouldTerminate()){
        short buffer_size=1024;
        char buffer[buffer_size];
        cin.getline(buffer , buffer_size);
        string line(buffer);
        if(!encdec.encode(line)){
            handler.terminate();
            break;
        }
        if(line == "LOGOUT"){
            _mutex.lock();
            string answer;
            char receive[4];
            if (handler.getBytes(receive,4)) {
                encdec.decode(receive);
            }
            else {
                handler.terminate();
            }
        }
    }
}