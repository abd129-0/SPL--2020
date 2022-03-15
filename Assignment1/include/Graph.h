#ifndef GRAPH_H_
#define GRAPH_H_
#include "../include/Tree.h"

#include <vector>

class Graph{
public:
    //CONSTRUCTOR
    Graph(std::vector<std::vector<int>> matrix, std::vector<int> infectedNodes);
//-----------------------------------------------------------------------------------------------------//

    void infectNode(int nodeInd);

    bool isInfected(int nodeInd);

    std::vector<int> &getInfectedNodes();   //REFERENCE OF INFECTED NODES - DEFAULT ZERO NODES

    //----------------------------------------------------------------------------------------------------//

    Tree* bfs(const Session &session, int infectedNode);

    std::vector<std::vector<int>> &getEdges();

    std::vector<int> neighbours(int nodeIndex);

private:
    std::vector<std::vector<int>> edges;
    std::vector<int> infectedNodes;
};
#endif
