# K-Means++ and DBSCAN Algorithms in Java
This repository contains Java implementations of two popular clustering algorithms: K-Means++ and DBSCAN. These algorithms are widely used in data mining. These algorithms have been implemented as a part of the Assignment 2 of the CSE304 Introduction to Data Mining course at Ulsan National Institute of Science and Technology.

## K-Means++ Algorithm
### Running the K-Means++ Algorithm
To run the K-Means++ Algorithm implementation, follow these steps: <br />
Compile the Java source file:
```
javac A1_G1_t1.java 
```
Run the compiled program with the following command-line arguments:
```
java A1_G1_t1 {path_to_data} {#of_clusters}
```
{path_to_data}: Specify the path to the input dataset file. <br />
{#of_clusters}: Specify the number of clusters as an integer value (e.g., 15 or 31). <br />
Example: <br />
```
java A2_G1_t1 ./artset1.csv 15
```
If the number of clusters are not given, then run it without specifying number of clusters. <br />
Example: <br />
```
java A2_G1_t1 ./artset1.csv 
```

## DBSCAN Algorithm
### Running the DBSCAN Algorithm

Firstly, in order to run the code, you need to compile it:
```
javac A2_G1_t2.java
```

Then, you can run it with both mu and eps given, or with only one of them (mu should be integer, eps should be floating point):
```
java A2_G1_t2 ./artd-31.csv {minPts} {eps}
```


