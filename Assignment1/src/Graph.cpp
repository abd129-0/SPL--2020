#include <iostream>
#include "../include/Graph.h"//
#include <vector>
#include <algorithm>
#include <queue>
#include <iostream>
using namespace std;

Graph::Graph(std::vector<std::vector<int>> matrix, std::vector<int> infectedNodes): edges(matrix), infectedNodes(infectedNodes){};

void Graph::infectNode(int nodeInd) {
    infectedNodes.push_back(nodeInd);
}

bool Graph::isInfected(int nodeInd) {
    for(unsigned i = 0; i < infectedNodes.size(); i++){
        if(infectedNodes[i] == nodeInd){
            return true;
        }
    }
    return false;
}

std::vector<int>& Graph::getInfectedNodes() {
    return infectedNodes;
}

std::vector<std::vector<int>>& Graph::getEdges() {
    return edges;
}

std::vector<int> Graph::neighbours(int nodeIndex) {
    std::vector<int> neighbours;
    for(unsigned i = 0; i < edges.size(); i++){
        if(edges[nodeIndex][i] == 1){
            neighbours.push_back(i);
        }
    }
    return neighbours;
}

Tree* Graph::bfs(const Session &session, int infectedNode) {
    bool visited[edges.size()];
    for(unsigned i = 0; i < edges.size();  i++){
        visited[i] = false;
    }
    std::queue<Tree*> queue;
    Tree* result = Tree::createTree(session,infectedNode);
    queue.push(result);
    visited[infectedNode] = true;
    while(!queue.empty())
    {
        Tree* tmp = queue.front();
        queue.pop();
        std::vector<int> neighbours = this->neighbours(tmp->getNodeIndex());
        for(unsigned i = 0; i < neighbours.size(); i++){
            if(!visited[neighbours[i]]){
                Tree* child = Tree::createTree(session, neighbours[i]);
                visited[neighbours[i]] = true;
                queue.push(child);
                tmp->addChild(child);
            }
        }
    }
    return result;
}

