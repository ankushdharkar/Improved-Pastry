Names of Team Members : ANKUSH DHARKAR 

The same file is used for the failure models also 

Instead of 
project2.scala numNodes topology algorithm 

provide 
project2.scala numNodes topology algorithm connectionfailure numOfConnectionsFailing

or 
project2.scala numNodes topology algorithm nodefailure numOfNodesFailing

The algorithm takes care of the failures

Upon running the programs on the Thunder server, the results were very slow, and some even displayed broken pipe. So, I decided to run all the experiments locally. 

I adjusted the Heapsize to 2GB for running the experiments


Maximum Number of actors spawned :

FullNetwork Gossip = 5000 
FullNetwork Push-sum = 5000 
Grid2D Gossip = 5000 
Grid2D Push-sum = 5000 
Line Gossip = 5000 
Line Push-sum = 1500 
ImperfectGrid2D Gossip = 5000 
ImperfectGrid2D Push-sum = 50000 

Bonus part implemented upto half the total number in failure of nodes from 1,5,10 ... 250 


As seen from the Plots: 
- Making a Grid as imperfect gives it line-like qualities (esp since a line is a grid with 1 row only)
- A full network model converges the fastest, as it has more people it can approach in each random jump
- A line network model is also fast, because of the high probability of jumping to the neighbouring node( at least 50% ) 
- 2D grid shows the worst time for convergence since it is very restrictive

