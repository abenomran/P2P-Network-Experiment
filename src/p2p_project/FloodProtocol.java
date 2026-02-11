package p2p_project;

import peersim.core.Node;
import peersim.core.Linkable;
import peersim.edsim.EDSimulator;

// implements flood search as a SearchProtocol extension
public class FloodProtocol extends SearchProtocol {
    public FloodProtocol(String prefix) {
        super(prefix);
    }
    // FLOODING
    // send query to all neighbors except self and origin
    @Override
    public void forwardQuery(Node node, int pid, Query q) {
        Linkable link = (Linkable) node.getProtocol(linkPid);
        for (int i = 0; i < link.degree(); i++) {
            Node nb = link.getNeighbor(i);
            if (nb.getID() == node.getID()) continue;
            if (nb.getID() == q.senderId) continue;
            Stats.queryForwards++;
            EDSimulator.add(1, q.nextHop(node.getID()), nb, pid);
        }
    }
}
