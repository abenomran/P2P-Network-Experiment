package p2p_project;

import java.util.List;
import java.util.Map;

// data class representing peer state
// contains: peer category, list of its files, and its keyword index

// note: peer id is not stored in the class itself, but instead tracked by PeerSim
public class Peer {
    public final Category category;
    public final List<String> files;
    public final Map<String, List<String>> index;

    public Peer(Category category, List<String> files, Map<String, List<String>> index) {
        this.category = category;
        this.files = files;
        this.index = index;
    }
}
