# All Targets
all: bin/cTrace

bin/cTrace: bin/main.o bin/agent.o bin/session.o bin/graph.o bin/tree.o
	@echo 'Building target: main'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/cTrace bin/main.o bin/agent.o bin/graph.o bin/tree.o bin/session.o
	@echo 'Finished building target: main'
	@echo ' '

bin/main.o: src/main.cpp
	g++ -g -Wall -Weffc++ -std=c++11  -c -Iinclude -o bin/main.o src/main.cpp

bin/agent.o: src/Agent.cpp
	g++ -g -Wall -Weffc++ -std=c++11  -c -Iinclude -o bin/agent.o src/Agent.cpp

bin/session.o: src/Session.cpp
	g++ -g -Wall -Weffc++ -std=c++11  -c -Iinclude -o bin/session.o src/Session.cpp

bin/graph.o: src/Graph.cpp
	g++ -g -Wall -Weffc++ -std=c++11  -c -Iinclude -o bin/graph.o src/Graph.cpp

bin/tree.o: src/Tree.cpp
	g++ -g -Wall -Weffc++ -std=c++11  -c -Iinclude -o bin/tree.o src/Tree.cpp
clean:
		rm -f bin/*
