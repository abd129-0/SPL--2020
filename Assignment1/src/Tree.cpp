#include "../include/Tree.h"
#include <iostream>
#include <queue>
using namespace std;
#include "../include/Session.h"
//CONSTRUCTOR
 Tree::Tree(int rootLabel): node(rootLabel), children(std::vector<Tree*>()){}

 //COPY CONSTRUCTOR
 Tree::Tree(const Tree &other) : node(other.node), children(std::vector<Tree*>()) {
    for(unsigned i = 0; i < other.children.size(); i++){
        Tree* child = other.children[i]->copy();
        addChild(child);
    }
}
 //DESTRUCTOR
 Tree::~Tree()  {
    for(unsigned i = 0; i < children.size(); i++){
        if(children[i]){
            delete children[i];
            children[i] = nullptr;
        }
    }
    children.clear();         //METHOD CLEAR OF VECTOR
}

//COPY ASSIGNMENT
Tree & Tree::operator=(const Tree &other) {
    if(this == &other){
        return *this;
    }
    for(unsigned i = 0; i < children.size(); i++){
        if(children[i]){
            delete children[i];
            children[i] = nullptr;
        }
    }
    for(unsigned i = 0; i < other.children.size(); i++){
        children.push_back(other.children[i]->copy());      //COPY THE CHILD WITH Different ADDRESS
    }
    return *this;
}

//MOVE CONSTRUCTOR
Tree::Tree(Tree &&other) : node(other.node), children(other.children) {
    for(unsigned i = 0; i < other.children.size(); i++){
        other.children[i] = nullptr;
    }
    other.node = 0;
}

//MOVE ASSIGNMENT
Tree& Tree::operator=(Tree &&other) {
    if(this == &other){
        return (*this);
    }
    else{
        for(unsigned i = 0; i < children.size(); i++){
            if(children[i]){
                delete children[i];
                children[i] = nullptr;
            }
        }
        node = other.node;
        children = std::move(other.children);     //MOVE METHOD OF VECTOR
    }
    return *this;
}

//CYCLE TREE CONSTRUCTOR
CycleTree::CycleTree(int rootLabel, int currCycle): Tree(rootLabel), currCycle(currCycle)  {};

//ROOT TREE CONSTRUCTOR
RootTree::RootTree(int rootLabel): Tree(rootLabel) {};

//MAX RANK TREE CONSTRUCTOR
MaxRankTree::MaxRankTree(int rootLabel): Tree(rootLabel) {};

 Tree* Tree::createTree(const Session &session, int rootLabel) {
     Tree* newTree;
    if(session.getTreeType() == MaxRank){
        newTree = new MaxRankTree(rootLabel);
    }
    else if(session.getTreeType() == Cycle){
        newTree = new CycleTree(rootLabel,session.getCycle());
    }
    else{
        newTree = new RootTree(rootLabel);
    }
     return newTree;
}

void Tree::addChild(Tree *child) {
    children.push_back(child);
}

void Tree::addChild(const Tree &child) {
    Tree* newChild = child.copy();
    children.push_back(newChild);
}

int Tree::getNodeIndex() {
    return node;
}

Tree* CycleTree::copy() const {
    return new CycleTree(*this);
}

Tree* MaxRankTree::copy() const {
    return new MaxRankTree(*this);
}

Tree* RootTree::copy() const {
    return new RootTree(*this);
}

int Tree::numOfChildren() {
    return this->children.size();
}

int CycleTree::traceTree() {
    int currCycle = this->currCycle;
    int result = this->node;
    Tree* myself = this;
    while(currCycle > 0) {
        if(myself->numOfChildren() != 0){
            myself = myself->getChildren()[0];
            result = myself->getNodeIndex();
        }
        currCycle = currCycle - 1;
    }
    return result;
}

int RootTree::traceTree() {
    return this->node;
}

std::vector<Tree*> Tree::getChildren() {
    return children;
}

int MaxRankTree::traceTree() {
    int needed = node;
    std::queue<Tree*> family;
    family.push(this);
    int childrenSize = children.size();
    while (!family.empty()){
        Tree* temp = family.front();
        family.pop();
        for (int i = 0; i < temp->numOfChildren();i++){
            family.push(temp->getChildren()[i]);
            if (temp->numOfChildren() > childrenSize){    //JUST BIGGER NOT EQUAL SO WE ALWAYS GET THE MOST LEFT CHILD
                needed = temp->getNodeIndex();
                childrenSize = temp->numOfChildren();
            }
        }
    }
    return needed;
}
