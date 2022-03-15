#ifndef SESSION_H_
#define SESSION_H_

#include <vector>
#include <string>
#include "../include/Graph.h"

class Agent;

enum TreeType{
  Cycle,
  MaxRank,
  Root
};

class Session{
public:
    //RULE OF 5
    Session(const std::string& path); //CONSTRUCTOR

    Session(const Session &other); //COPY CONSTRUCTOR

    Session& operator=(const Session& other); //COPY ASSIGNMENT

    Session(Session&& other);  //MOVE CONSTRUCTOR

    Session& operator=(Session&& other); //MOVE ASSIGNMNET

    virtual ~Session(); //DESTRUCTOR

    //-----------------------------------------------------------------------------------------------//
    
    void simulate();

    //-----------------------------------------------------------------------------------------------//

    //TERMINATE

    bool toContinue();

    bool isWhiteNode(int index);

    bool isColoredNode(int index);

    bool isYellow(int index);

    bool checkColoredSiplings(int index);

    bool checkWhiteSiplings(int index);

    //-----------------------------------------------------------------------------------------------//

    void setGraph(const Graph& graph);

    //----------------------------------------------------------------------------------------------//

    //METHODS WE USE IN AGENTS

    void addAgent(const Agent& agent);

    void enqueueInfected(int);

    int dequeueInfected();

    void deleteEdges(int nodeToCut);

    Graph& getGraph();

    int getNodeToInfect(int index);

    Tree* runBfs(const Session& session,int index);

    //---------------------------------------------------------------------------------------------//

    TreeType getTreeType() const;

    void addYellowNode(int index);

    int getCycle() const;

    void setCycle(int newCycle);

    void setToHadAVirus(int index);

    bool hadVirus(int index);

private:
    Graph g;
    TreeType treeType;
    std::vector<Agent*> agents;
    std::vector<int> yellowNodes;  //NODES THAT CARRY THE VIRUS BUT NOT INFECTED YET - DEFAULT:VIRUS AGENTS
    std::vector<int> hadAVirus; //KEEPS ALL THE NODES THAT ALREADY HAD A VIRUS - DEFAULT: NONE
    int cycle;
};

#endif
