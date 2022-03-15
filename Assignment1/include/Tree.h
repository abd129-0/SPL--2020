#ifndef TREE_H_
#define TREE_H_

#include <vector>

class Session;

class Tree{
public:
     Tree(int rootLabel); //CONSTRUCTOR

     Tree(const Tree& other);   //COPY CONSTRUCTOR

     virtual ~Tree(); //DESTRUCTOR

     Tree & operator=(const Tree &other); //COPY ASSIGNMNET

     Tree(Tree &&other); //MOVE CONSTRUCTOR

     Tree & operator=(Tree &&other); //MOVE ASSIGNMENT

    void addChild(const Tree& child);

    void addChild(Tree* child);

    int getNodeIndex();

    static Tree* createTree(const Session& session, int rootLabel);

    virtual int traceTree()=0;

    std::vector<Tree *> getChildren();

    virtual Tree* copy() const=0;

    int numOfChildren();


protected:
    int node;                            //ROOT
    std::vector<Tree*> children;
};

//----------------------------------------------------------------------------------------//

//CYCLE TREE

class CycleTree: public Tree{
public:
    CycleTree(int rootLabel, int currCycle);
    virtual int traceTree();
    virtual Tree* copy() const;

private:
    int currCycle;
};

//----------------------------------------------------------------------------------------//

//MAX RANK TREE

class MaxRankTree: public Tree{
public:
    MaxRankTree(int rootLabel);
    virtual int traceTree();
    virtual Tree* copy() const;
};

//------------------------------------------------------------------------------------------//

//ROOT TREE

class RootTree: public Tree{
public:
    RootTree(int rootLabel);
    virtual int traceTree();
    virtual Tree* copy() const;
};

#endif
