package p2p_project;

import java.util.List;

import peersim.config.Configuration;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.core.CommonState;
import peersim.edsim.EDSimulator;
import peersim.core.Network;

import java.util.HashSet;
import java.util.Set;

public abstract class SearchProtocol implements EDProtocol, PeerProtocol {
    protected static final String PAR_STEP = "step";
    protected final long step;

    protected Peer self;
    protected Set<Long> seen = new HashSet<>();
    protected int linkPid = -1; // config sets this

    protected SearchProtocol(String prefix) {
        this.step = Configuration.getLong(prefix + "." + PAR_STEP);
    }
    
    // reset per-node state so cloned protocol instances do not share memory
    @Override
    public Object clone() {
        SearchProtocol c;
        try {
            c = (SearchProtocol) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        // per-node state must not be shared
        c.seen = new HashSet<>();
        c.self = null; // each node will set its own Peer later
        c.linkPid = -1; // set again by DebugController
        return c;
    }

    @Override
    public void processEvent(Node node, int pid, Object event) {
        // handle response
        // updates stats for metrics
        // print information
        if (event instanceof Response) {
            Response h = (Response) event;

            Stats.hitsReceivedAtOrigin++;
            if (!Stats.hitTime.containsKey(h.qid)) {
                Stats.hitTime.put(h.qid, CommonState.getTime());
            }

            System.out.println("ORIGIN node " + node.getID()
                    + " received HIT for qid=" + h.qid
                    + " from=" + h.responderId
                    + " hops=" + h.hops
                    + " hits=" + h.hits
                    + " time=" + CommonState.getTime());

            System.out.println("STATS: forwards=" + Stats.queryForwards
                    + " hitsSent=" + Stats.hitsSent
                    + " hitsRecvAtOrigin=" + Stats.hitsReceivedAtOrigin);
            return;
        }

        // ignore unknown events
        if (!(event instanceof Query)) return;
        Query q = (Query) event;

        // duplicate check to prevent processing the same query multiple times
        if (seen.contains(q.qid)) return;
        seen.add(q.qid);

        // search peer's local files for match
        java.util.List<String> hits = searchLocal(q.keyword);

        System.out.println("Node " + node.getID()
                + " got QUERY qid=" + q.qid
                + " kw='" + q.keyword + "'"
                + " ttl=" + q.ttl
                + " hops=" + q.hops
                + " from=" + q.senderId
                + " hits=" + hits);

        // if found, send response to origin + update stats
        if (!hits.isEmpty()) {
            // sends response directly to origin based on origin ID
            // doesn't need to re-traverse backwards
            Node origin = Network.get((int) q.originId);
            Response hm = new Response(q.qid, node.getID(), hits, q.hops);

            Stats.hitsSent++;
            EDSimulator.add(1, hm, origin, pid);
            System.out.println("Node " + node.getID() + " sending HIT back to origin " + q.originId);
            return;
        }

        // check TTL to kill query if necessary
        if (q.ttl <= 0) return;

        // based on search algorithm override
        forwardQuery(node, pid, q);
    }

    public void setLinkPid(int linkPid) { this.linkPid = linkPid; }

    public void setPeer(Peer p) { this.self = p; }
 
    public Peer getPeer() { return self; }

    // search keyword in peer's local index
    public List<String> searchLocal(String keyword) {
        if (self == null) return java.util.Collections.emptyList();
        return self.index.getOrDefault(keyword, java.util.Collections.emptyList());
    }
}
