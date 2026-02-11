package p2p_project;

import peersim.core.Node;

// interface for search protocols 
// in this project, I have RandomWalk and Flood
public interface PeerProtocol {
    void setLinkPid(int linkPid); // tells the protocol where to find its neighbors
    Peer getPeer(); // get peer data
    void setPeer(Peer p); // attach peer data

    // forwarding rule differs per protocol
    void forwardQuery(Node node, int pid, Query q);
}
