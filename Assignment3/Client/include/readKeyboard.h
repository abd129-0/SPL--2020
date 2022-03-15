#ifndef BOOST_ECHO_CLIENT_READKEYBOARD_H
#define BOOST_ECHO_CLIENT_READKEYBOARD_H
#include "connectionHandler.h"
#include "BGRSEncoderDecoder.h"
#include <mutex>


class readKeyboard{
public:
    readKeyboard(ConnectionHandler &_handler, mutex &mutex);
     void operator()();
private:
    BGRSEncoderDecoder encdec;
    ConnectionHandler &handler;
    mutex & _mutex;

};
#endif //BOOST_ECHO_CLIENT_READKEYBOARD_H
