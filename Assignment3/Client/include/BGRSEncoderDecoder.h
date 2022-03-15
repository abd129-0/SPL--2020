#ifndef BOOST_ECHO_CLIENT_BGRSENCODERDecoder_H
#define BOOST_ECHO_CLIENT_BGRSENCODERDecoder_H

#include <string>
#include <vector>
#include <iostream>
#include <codecvt>
#include <locale>
#include <mutex>
#include "connectionHandler.h"

using namespace std;

class BGRSEncoderDecoder {
public:
    BGRSEncoderDecoder(ConnectionHandler &handler, mutex &mutex);
    //encode
    bool encode(string toEncode);
    
    //decode
    void decode(char* toDecode);
    void operator()();
private:
    bool sendOpcode(short opcode);
    short commandToShort(string str);
    ConnectionHandler &handler;
    mutex & _mutex;
};
#endif //BOOST_ECHO_CLIENT_BGRSENCODERDecoder_H
