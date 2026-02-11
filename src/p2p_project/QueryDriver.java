package p2p_project;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

// PeerSim control component responsible for injecting the initial query
// message into the network to start the search experiment

public class QueryDriver implements Control {

    private static final String PAR_PID = "protocol";
    private final int pid;
    private boolean done = false;

    // reads protocol id from PeerSim config file
    // in this project's case it would be either Random Walk or Flood
    public QueryDriver(String prefix) {
        this.pid = Configuration.getPid(prefix + "." + PAR_PID);
    }

    // injects initial query
    @Override
    public boolean execute() {
        // makes sure it only runs once
        if (done) return false;
        done = true;

        // send query to node 1
        System.out.println("Query start time=" + peersim.core.CommonState.getTime());
        Node target = Network.get(0);
        Query msg = new Query(1L, 0L, 0L, "league", 3, 0);

        // inject into simulator
        EDSimulator.add(0, msg, target, pid);
        System.out.println("\nDriver injected QUERY '" + msg.keyword + "' to node " + target.getID() + " (origin)\n");

        return false;
    }
}
