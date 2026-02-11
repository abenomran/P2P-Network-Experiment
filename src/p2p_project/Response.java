package p2p_project;

import java.util.List;

// if match for query is found, we send back a response to the origin

// for responses: the id of the responder is stored in a response message,
// this way, origin knows which peer holds the matching data
public class Response {
    public final long qid;
    public final long responderId;
    public final List<String> hits;
    public final int hops;

    public Response(long qid, long responderId, List<String> hits, int hops) {
        this.qid = qid;
        this.responderId = responderId;
        this.hits = hits;
        this.hops = hops;
    }
}
