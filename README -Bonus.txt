The same file for main project can beused for the failure models also 

Instead of 
project2.scala numNodes topology algorithm 

provide 
project2.scala numNodes topology algorithm connectionfailure numOfConnectionsFailing

or 
project2.scala numNodes topology algorithm nodefailure numOfNodesFailing

The algorithm takes care of the failures

Upon running the programs on the Thunder server, the results were very slow, and some even displayed broken pipe. So, I decided to run all the experiments locally. 

I adjusted the Heapsize to 2GB for running the experiments

Bonus part implemented upto half the total number in failure of nodes from 1,5,10 ... 250 

As seen from the Failure of Nodes Plot: 
- Full network suffers very much from as the number of failure in nodes increase.
- A line network model shows no great deffernce, but, actually, it gives a very wrong value of convergence quickly, as their are no alternate paths to the neighbours of neighbours. Hence, it "seems" to reach a covergence quickly on node failures. But that is a wrong output of s/w provided.
Other models differ but not with so much of difference as compared that of line-model

