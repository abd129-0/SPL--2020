CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_thread -pthread

all: bin/BGRSclient

bin/BGRSclient: bin/BGRSEncoderDecoder.o bin/connectionHandler.o bin/readKeyboard.o bin/BGRSClient.o
	@echo 'Building target: main'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/BGRSclient bin/connectionHandler.o bin/BGRSEncoderDecoder.o bin/readKeyboard.o bin/BGRSClient.o $(LDFLAGS)
	@echo 'Finished building target: main'
	@echo ' '

bin/BGRSClient.o: src/BGRSClient.cpp
	g++ $(CFLAGS) -o bin/BGRSClient.o src/BGRSClient.cpp

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/BGRSEncoderDecoder.o: src/BGRSEncoderDecoder.cpp
	g++ $(CFLAGS) -o bin/BGRSEncoderDecoder.o src/BGRSEncoderDecoder.cpp

bin/readKeyboard.o: src/readKeyboard.cpp
	g++ $(CFLAGS) -o bin/readKeyboard.o src/readKeyboard.cpp

.PHONY: clean
clean:
	rm -f bin/*
