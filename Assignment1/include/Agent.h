#ifndef AGENT_H_
#define AGENT_H_

#include <vector>
#include "../include/Session.h"

class Agent{
public:
    Agent();
    virtual ~Agent()=default; //CLION FORCES US TO CREATE ONE

    virtual void act(Session& session)=0;

    virtual Agent* clone() const=0;

     Agent& getRef();

};


class ContactTracer: public Agent{
public:
      ContactTracer();
      virtual Agent* clone() const ;
    virtual void act(Session& session);
};


class Virus: public Agent{
public:
      Virus(int nodeInd);  //CONSTRUCTOR TO CREATE A NEW AGENT OF TYPE VIRUS
        virtual Agent* clone() const ;
        virtual void act(Session& session);
private:
    const int nodeInd;
};

#endif
