# Experimental P2P Search System (PeerSim)

## Overview
This project implements a toy Peer-to-Peer (P2P) search system using the PeerSim simulator. The system supports two search strategies:
- Random Walk Search
- Flooding Search

The system simulates peers sharing simulated text files and performing keyword-based search queries across the network.

The goal of this project is to evaluate and compare these two P2P search mechanisms using performance measurements including:
- Query latency (average time needed per query)
- Throughput (queries served per time unit)

## Features
- PeerSim P2P file sharing simulation
- Keyword-based search queries
- Two search protocols:
  - Random walk-based search
  - Flood-based search
- Performance metrics via stats tracking
- Configurable experiments:
  - Vary number of files per peer
  - Vary number of queries
  - Vary number of peers

## Project Structure

| File | Description |
|---|---|
| `Category.java` | File category definitions |
| `DebugController.java` | Network and file distribution setup |
| `FloodProtocol.java` | Flood search implementation |
| `Peer.java` | Peer data model (files, metadata) |
| `PeerProtocol.java` | Peer behavior and search protocol interface |
| `Query.java` | Query message structure (keyword, TTL, hops, origin, ID) |
| `QueryDriver.java` | Query generation controller |
| `RandomWalkProtocol.java` | Random walk search implementation |
| `Response.java` | Response / HIT message returned to query origin |
| `SearchProtocol.java` | Base search protocol interface |
| `Stats.java` | Metrics collection (latency, throughput, counts) |

## How the System Works
### Network Setup
1. Minimum 5 peers in the network
2. Each peer stores multiple files
3. Files are named using searchable keywords

### Query Process
1. A peer injects a keyword query
2. Query propagates using either:
    - Flooding → forward to all neighbors
    - Random Walk → forward to one random neighbor
3. If a peer has a matching file:
    - A response message is returned to the origin
4. Statistics recorded

## Performance Metrics
### Latency
Average time between Query injection time and first response received at origin peer

```
Latency = HitTime - StartTime
```
### Throughput
Number of completed queries per simulation time window
```
Throughput = CompletedQueries / TimeWindow
```

## Running the Simulation
1. Compile:
```
javac -cp "peersim-1.0.5.jar:djep-1.0.0.jar:jep-2.3.0.jar" -d . src/p2p_project/*.java
```
2. Run:

- **Random Walk** Search
```
java -cp ".:peersim-1.0.5.jar:djep-1.0.0.jar:jep-2.3.0.jar" peersim.Simulator example/p2p-search-rw.txt 
```

- **Flood** Search
```
java -cp ".:peersim-1.0.5.jar:djep-1.0.0.jar:jep-2.3.0.jar" peersim.Simulator example/p2p-search-flood.txt
```

On a Windows system, substitute `:` with `;` in the classpath string.

## Expected Behavior
### Random Walk Search
- Queries are forwarded to one randomly selected neighbor

### Flood Search
- Queries are forwarded to all neighbors (besides origin)

| Metric | Flood Search | Random Walk Search |
|---|---|---|
| Average Latency | Low | Medium / High |
| Throughput | High | Medium |
| Network Traffic | Very High | Low |
| Hit Rate | High | Medium (depends on TTL) |
| Scalability | Poor (due to traffic explosion) | Good |


## PeerSim Information
This uses version 1.0.5 of the Peersim high level P2P network simulator.

For more information and documentation visit the Peersim site at
sourceforge:
http://peersim.sourceforge.net/




