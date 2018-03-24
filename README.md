# Algorithms for HeteroDom

## introduction

This project is the implement of algorithms for solving the **HeterDom** problem over directed trees.

A heterogeneous (ρ1, ρ2)-dominating team, where ρ1 ≤ ρ2, over a directed network G = (V,E) as a pair (D1,D2) of node
sets which satisfy the following condition: Any u ∈ V is either within ρ1 steps away from some node in D1, or is within
 ρ2 steps away from some node in D2. And the **HeterDom** problem is to find such teams over a graph.
 
## Notes for using and testing
* This project is implemented by java, using IntelliJ IDE. So it is recommended to install java of the latest releaed version
and IntelliJ IDE in your machines.

* The main entry of running is `/src/TreeReader.java`.

* The java files in `/src/algorithms` are the implements of algorithms. _ComputeDom.java_,
_DynamicProg.java_, _BlueToRed.java_, _RedToBlue.java_ are the implements for the algorithm 
**ComputeDom**, **DP**, **D1ToD2**, **D2ToD1** respectively. You can choose one or more more algorithms to run in a single 
running by modifying the file `/src/AlgorithmController.java`.

* Part of datasets used in our experiments are given in `/dataset`
