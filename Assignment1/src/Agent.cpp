//
// Created by spl211 on 04/11/2020.
//
#include "../include/Agent.h"

//CONSTRUCTOR
Agent::Agent() {};

//----------------------------------------------------------------------------------------------//

Agent& Agent::getRef() {      //WE NEED IT FOR 'ADD AGENT' METHOD
    return  *this;
}

//---------------------------------------------------------------------------------------------//

//VIRUS SEGMENT

Virus::Virus(int nodeInd): nodeInd(nodeInd) {}  //CONSTRUCTOR

Agent* Virus::clone() const {
    return new Virus(*this);
}

void Virus::act(Session &session) {
    if(!session.hadVirus(nodeInd)){  //IS HE ALREADY INFECTED?
        session.setToHadAVirus(nodeInd);
        session.enqueueInfected(this->nodeInd);
    }
    int target = session.getNodeToInfect(this->nodeInd);
    if(target != -1){
        session.addYellowNode(target);  //NEW NODE THAT CARRY THE VIRUS , BUT NOT SICK YET !!!
        Agent* newVirus = new Virus(target);
        Agent& newRefAgent = newVirus->getRef();
        session.addAgent(newRefAgent);
        delete newVirus;
        newVirus = nullptr;
    }
}

//-----------------------------------------------------------------------------------------//

//CONTACT TRACER SEGMENT

ContactTracer::ContactTracer() {}; //CONSTRUCTOR

Agent* ContactTracer::clone() const {
    return new ContactTracer(*this);
}

void ContactTracer::act(Session &session) {
    int InfectedNodeIndex = session.dequeueInfected();
    if(InfectedNodeIndex != -1) {
        Tree* infectedNodeTree = session.runBfs(session,InfectedNodeIndex);
        int nodeToCut = infectedNodeTree->traceTree();
        session.deleteEdges(nodeToCut);
        delete infectedNodeTree;
        infectedNodeTree = nullptr;
    }
}