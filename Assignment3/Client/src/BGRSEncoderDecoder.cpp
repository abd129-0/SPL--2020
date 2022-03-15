#include <sstream>
#include "../include/BGRSEncoderDecoder.h"

BGRSEncoderDecoder::BGRSEncoderDecoder(ConnectionHandler &handler, mutex &mutex):handler(handler),_mutex(mutex) {}


bool BGRSEncoderDecoder::encode(string toEncode) {
    vector<char> result;
    istringstream split(toEncode);
    vector<string> args;
    for(string s;split>>s;)
        args.push_back(s);
    if(args.size() > 0){
        short opcode = commandToShort(args[0]);
        if(opcode != -1) {
            if(sendOpcode(opcode)){
                for (unsigned i=1; i < args.size(); i++){
                    handler.sendLine(args[i]);
                }
                if(args.size() == 1) {
                    string empty = "";
                    handler.sendLine(empty);
                }
                return true;
            } else return false;
        } else {
            cout << "Unknown command" << endl;
        }
    }
        return true;
}
bool BGRSEncoderDecoder::sendOpcode(short opcode){
    char opByte[2];
    opByte[0]=((opcode>>8) & 0xff);
    opByte[1]=(opcode & 0xff);
    return handler.sendBytes(opByte,2);
}

short BGRSEncoderDecoder::commandToShort(string command) {
    if(command=="ADMINREG") return 1;
    if (command=="STUDENTREG") return 2;
    if(command=="LOGIN") return 3;
    if(command=="LOGOUT") return 4;
    if(command=="COURSEREG") return 5;
    if (command=="KDAMCHECK") return 6;
    if(command=="COURSESTAT") return 7;
    if(command=="STUDENTSTAT") return 8;
    if (command=="ISREGISTERED") return 9;
    if (command=="UNREGISTER") return 10;
    if(command=="MYCOURSES") return 11;
    return -1;
}
void BGRSEncoderDecoder::operator()() {
    while (!handler.ShouldTerminate()) {
        string answer;
        char receive[4];
        if (handler.getBytes(receive,4)) {
            decode(receive);
        }
        else {
            handler.terminate();
        }
    }
}

void BGRSEncoderDecoder::decode(char toDecode[]) {
    short res = (short)((toDecode[0] & 0xff) << 8);
    res += (short)(toDecode[1] & 0xff);
    short opcode = (short)((toDecode[2] & 0xff) << 8);
    opcode += (short)(toDecode[3] & 0xff);
    string args;
    handler.getLine(args);
    switch (res) {
        case 12:
            if (opcode == 4) {
                handler.terminate();
                cout << "ACK " << opcode << endl;
            } else if (((opcode > 5) & (opcode < 10)) | (opcode == 11)) {
                cout << "ACK " << opcode << endl;
                cout << args << endl;
            } else {
                cout << "ACK " << opcode << endl;
            }
            break;
        case 13: {
            cout << "ERR " << opcode << endl;
        }
        default: {
            //cout << "received: " << res << " and " << opcode << " and " << args << endl;
            break;
        }
    }
}
