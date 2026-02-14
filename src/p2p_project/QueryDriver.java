package p2p_project;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

// PeerSim control component responsible for injecting the initial query
// message into the network to start the search experiment

public class QueryDriver implements Control {

    private static final String PID = "protocol";
    private final int pid;
    private boolean done = false;

    // config keys
    private static final String NUM_QUERIES = "numQueries";
    private static final String TTL = "ttl";
    private static final String ORIGIN = "origin";
    private static final String GAP = "gap"; // optional: ticks between injections
    private static final String START_QID = "startQid"; // optional: base qid

    // loaded from config
    private final int numQueries;
    private final int ttl;
    private final long originId;
    private final int gap;
    private final long startQid;

    // reads protocol id from PeerSim config file
    // in this project's case it would be either Random Walk or Flood
    public QueryDriver(String prefix) {
        this.pid = Configuration.getPid(prefix + "." + PID);
        this.numQueries = Configuration.getInt(prefix + "." + NUM_QUERIES, 10);
        this.ttl = Configuration.getInt(prefix + "." + TTL, 3);
        this.originId = Configuration.getLong(prefix + "." + ORIGIN, 0L);
        this.gap = Configuration.getInt(prefix + "." + GAP, 0);
        this.startQid = Configuration.getLong(prefix + "." + START_QID, 1L);

    }

    // injects initial query
    @Override
    public boolean execute() {
        // makes sure it only runs once
        if (done) return false;
        done = true;

        // // send query to node 1
        // System.out.println("Query start time=" + peersim.core.CommonState.getTime());
        // Node target = Network.get(0);
        // Query msg = new Query(1L, 0L, 0L, "league", 3, 0);

        // // inject into simulator
        // EDSimulator.add(0, msg, target, pid);
        // System.out.println("\nDriver injected QUERY '" + msg.keyword + "' to node " + target.getID() + " (origin)\n");

        String[] keywords = new String[] {
            "league","team","coach","soccer","nba",
            "java","linux","cloud","film","actor"
        };
    
        Node origin = Network.get((int) originId);

        System.out.println("Query batch start time=" + peersim.core.CommonState.getTime());

        for (int i = 0; i < numQueries; i++) {
            long qid = startQid + i;
            String kw = keywords[i % keywords.length];
        
            // send query to node origin
            Query msg = new Query(qid, originId, originId, kw, ttl, 0);
        
            Stats.startTime.put(qid, peersim.core.CommonState.getTime());
        
            // inject into simulator
            EDSimulator.add((long) i * gap, msg, origin, pid);
        }

        return false;
    }
}
