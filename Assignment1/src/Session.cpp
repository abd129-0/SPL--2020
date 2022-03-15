#include <iostream>
#include "../include/Session.h"
#include "../include/json.hpp"
#include <vector>
#include "../include/Agent.h"
#include <fstream>
//-------------------------------------------------------------------------------------------------------------
using json = nlohmann::json;
using namespace std;
#define JSON_PATH "output.json"

//CONSTRUCTOR
Session::Session(const std::string &path) : g(std::vector<std::vector<int>>(), std::vector<int>()), treeType(),
agents(std::vector<Agent*>()), yellowNodes(std::vector<int>()), hadAVirus(std::vector<int>()), cycle(-1){
    ifstream i(path);
    json j;
    i >> j;
    if (j["tree"] == "M") {          //initialise TREE TYPE
        treeType = MaxRank;
    } else if (j["tree"] == "R") {
        treeType = Root;
    } else {
        treeType = Cycle;
    }
    for (auto &elem : j["agents"]) {      //initialise AGENTS
        if (elem[0] == "V") {
            Agent *virus = new Virus(elem[1]);
            agents.push_back(virus);
            yellowNodes.push_back(elem[1]);
        } else {
            Agent *contactTracer = new ContactTracer();
            agents.push_back(contactTracer);
        }
    }
    for (auto &elem : j["graph"]) {     //initialise GRAPH
        this->g.getEdges().push_back(elem);
    }
};

//COPY CONSTRUCTOR
Session::Session(const Session &other) : g(other.g), treeType(other.treeType), agents(std::vector<Agent*>()), yellowNodes(other.yellowNodes)
, hadAVirus(other.hadAVirus), cycle(other.cycle){
    for (unsigned i = 0; i < other.agents.size(); i++) {
        agents.push_back(other.agents[i]->clone());
    }
}
//----------------------------------------------------------------------------------------
//COPY ASSIGNMENT

Session &Session::operator=(const Session &other) {
    if (this == &other) {
        return *this;
    } else {
        g = Graph(other.g);
        cycle = other.cycle;
        treeType = other.treeType;
        yellowNodes = other.yellowNodes;
        hadAVirus = other.hadAVirus;
        for (unsigned i = 0; i < agents.size(); i++) {
            if (agents[i]) {
                delete agents[i];
                agents[i] = nullptr;
            }
        }
        for (unsigned i = 0; i < other.agents.size(); i++) {
            agents.push_back(other.agents[i]->clone());
        }
    }
    return *this;
}
//----------------------------------------------------------------------------------------
//MOVE CONSTRUCTOR

Session::Session(Session &&other) : g(other.g), treeType(other.treeType), agents(other.agents), yellowNodes(other.yellowNodes), hadAVirus(other.hadAVirus), cycle(other.cycle) {
    other.treeType = MaxRank; //default
    other.cycle = 0;
    for (unsigned i = 0; i < other.agents.size(); i++) {
        other.agents[i] = nullptr;
    }
}
//----------------------------------------------------------------------------------------
//MOVE ASSIGNMENT

Session &Session::operator=(Session &&other) {
    if (this == &other) {
        return *this;
    }
    else {
        for (unsigned i = 0; i < agents.size(); i++) {
            if (agents[i]) {
                delete agents[i];
                agents[i] = nullptr;
            }
        }
        cycle = other.cycle;
        treeType = other.treeType;
        g = other.g;
        yellowNodes = other.yellowNodes;
        hadAVirus = other.hadAVirus;
        agents = std::move(other.agents);
    }
    return *this;
}

//----------------------------------------------------------------------------------------
//DESTRUCTOR

Session::~Session() {
    for (unsigned i = 0; i < agents.size(); i++) {
        if (agents[i]) {
            delete agents[i];
            agents[i] = nullptr;
        }
    }
}

void Session::enqueueInfected(int index) {
    g.getInfectedNodes().push_back(index);
}

int Session::dequeueInfected() {
    if (g.getInfectedNodes().size() == 0) {
        return -1;
    }
    int toReturn = g.getInfectedNodes().front();
    g.getInfectedNodes().erase(g.getInfectedNodes().begin());
    return toReturn;
}

void Session::addAgent(const Agent &agent) {
    Agent *newAgent = agent.clone();
    agents.push_back(newAgent);
}

int Session::getNodeToInfect(int index) {
    for (unsigned i = 0; i < g.getEdges().size(); i++) {
        if (g.getEdges()[index][i] == 1) {
            if (!isYellow(i)) {                             //NOT HAD A VIRUS YET
                return i;
            }
        }
    }
    return -1;  // ALL THE NEIGHBOURS ARE INFECTED
}

Tree *Session::runBfs(const Session &session, int index) {
    return g.bfs(session, index);
}

void Session::setGraph(const Graph &graph) {
    this->g = graph;
}

Graph &Session::getGraph() {
    return g;
}

TreeType Session::getTreeType() const {
    return treeType;
}

int Session::getCycle() const {
    return cycle;
}

void Session::setCycle(int newCycle) {
    cycle = newCycle;
}

void Session::deleteEdges(int nodeToCut) {
    for (unsigned i = 0; i < this->g.getEdges().size(); i++) {
        g.getEdges()[nodeToCut][i] = 0;
        g.getEdges()[i][nodeToCut] = 0;
    }
}

bool Session::toContinue() {
    bool toContinue = false;    //FIND A REASON TO CONTINUE
    for (unsigned i = 0; i < (this->g.getEdges().size()) && (!toContinue); i++) {
        if (isWhiteNode(i)) {
            toContinue = checkColoredSiplings(i);
        } else {     //THE NODE IS COLORED
            toContinue = checkWhiteSiplings(i);
        }
    }
    return toContinue;
}

bool Session::checkColoredSiplings(int index) {
    bool color = false;
    std::vector<int> neighbours = this->g.neighbours(index);
    if (neighbours.size() == 0) {
        return color;
    }
    else {
        for (unsigned i = 0; i < neighbours.size(); i++) {   //CHECK COLOR OF NEIGHBOURS
            color = isColoredNode(neighbours.back());
            neighbours.pop_back();
            if (color) {
                return true;
            }
        }
    }
    return false;
}

bool Session::checkWhiteSiplings(int index) {
    bool white = false;
    std::vector<int> neighbours = this->g.neighbours(index);
    if (neighbours.size() == 0) {
        return white;
    } else {
        for (unsigned i = 0; i < neighbours.size() &&(!white); i++) {
            white = isWhiteNode(neighbours[i]);
        }
    }
    return white;
}

bool Session::isWhiteNode(int index) {
    bool white = true;
    if (this->g.isInfected(index)) {
        white = false;
    }
    if (white) {
        if (this->isYellow(index)) {
            white = false;
        }
    }
    return white;
}

bool Session::isColoredNode(int index) {
    bool result = isYellow(index);;
    if (result) {
        return true;
    }
    result = this->g.isInfected(index);
    return result;
}

bool Session::isYellow(int index) {
   for (unsigned i =0;i<yellowNodes.size();i++){
       if (yellowNodes[i] == index)
           return true;
   }
   return false;
}

void Session::addYellowNode(int index) {
    this->yellowNodes.push_back(index);
}

void Session::setToHadAVirus(int index) {
    hadAVirus.push_back(index);
}

bool Session::hadVirus(int index) {
    for (unsigned i =0; i<hadAVirus.size(); i++){
        if (hadAVirus[i] == index)
            return true;
    }
    return false;
}

void Session::simulate() {
    int currSize = agents.size();
    while (toContinue()) {
        this->cycle = this->cycle + 1;  //DEFAULT IS ZERO
        for (int i = 0; i < currSize; i++) {  //LET AGENTS ACT
            agents[i]->act(*this);
        }
        currSize = this->agents.size();  //UPDATE SIZE - IF WE ADD NEW AGENTS
    }
    json j;
    std::ofstream i("output.json");
    j["infected"] = this->yellowNodes;
    j["graph"] = this->g.getEdges();
    i << j;
}