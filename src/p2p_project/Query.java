package p2p_project;

// origin creates a query, which will traverse the network

// for requests
public class Query {
    public final long qid;
    public final long originId;
    public final long senderId;
    public final String keyword;
    public final int ttl; // time to live (i.e. how far query can travel)
    public final int hops;

    public Query(long qid, long originId, long senderId, String keyword, int ttl, int hops) {
        this.qid = qid;
        this.originId = originId;
        this.senderId = senderId;
        this.keyword = keyword;
        this.ttl = ttl;
        this.hops = hops;
    }

    // forwards query to next peer based on newSenderId param passed by forwarding algorithm
    // updates ttl and hops accordingly
    public Query nextHop(long newSenderId) {
        return new Query(qid, originId, newSenderId, keyword, ttl - 1, hops + 1);
    }
}
