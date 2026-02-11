package p2p_project;

import peersim.core.Node;
import peersim.core.Linkable;
import peersim.core.CommonState;
import peersim.edsim.EDSimulator;

// implements random walk as a SearchProtocol extension
public class RandomWalkProtocol extends SearchProtocol {
    public RandomWalkProtocol(String prefix) {
        super(prefix);
    }
    // RANDOM WALK
    // forward query to one randomly selected neighbor
    @Override
    public void forwardQuery(Node node, int pid, Query q) {
        Linkable link = (Linkable) node.getProtocol(linkPid);
        Node next = pickNeighbor(link, node.getID(), q.senderId);
        if (next == null) return;
        Stats.queryForwards++;
        EDSimulator.add(1, q.nextHop(node.getID()), next, pid);
    }

    // helper to select a random neighbor that is not self or origin
    private Node pickNeighbor(Linkable link, long selfId, long senderId) {
        if (link.degree() == 0) return null;

        int start = CommonState.r.nextInt(link.degree());
        for (int i = 0; i < link.degree(); i++) {
            int idx = (start + i) % link.degree();
            Node nb = link.getNeighbor(idx);
            if (nb.getID() == selfId) continue;
            if (nb.getID() == senderId) continue;
            return nb;
        }
        return null;
    }
}
