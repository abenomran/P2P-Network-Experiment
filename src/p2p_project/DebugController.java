package p2p_project;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.core.CommonState;
import peersim.core.Linkable;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * PeerSim control component responsible for initializing peer states
 * it also prints helpful debug information
 * 
 * - assigns categories to peers and generates their corresponding files
 * - maps keywords to matching files for fast local lookup
 * - generate files deterministically using the simulation random seed
 * - prints neighbor lists when simulation starts
 * - prints peer state for debugging
*/
public class DebugController implements Control {

    private static final String PAR_PID = "protocol";
    private final int pid;
    private final int linkPid;

    // this is a ratio of how many files are assigned to a peer based on their given category
    // it's set to 80%
    // example: MUSIC peer with 10 files -> 8 music files + 2 random-category files.
    // this avoids strict category isolation and better simulates realistic peer storage
    private final int filesPerPeer;
    private final int inCategory;

    // reads protocol id from PeerSim config file
    // in this project's case it would be either Random Walk or Flood
    public DebugController(String prefix) {
        this.pid = Configuration.getPid(prefix + "." + PAR_PID);
        this.linkPid = Configuration.getPid(prefix + ".linkable");
        this.filesPerPeer = Configuration.getInt(prefix + ".filesPerPeer", 10);
        this.inCategory = (int) (this.filesPerPeer * 0.8);
    }

    // initialize peers and print debug information
    @Override
    public boolean execute() {
        long t = CommonState.getTime();
        Random random = CommonState.r; // results reproducible because random.seed set in config

        // print neighbor list of every peer at time = 0
        if (t == 0) {
            System.out.println("\n=== NEIGHBOR LISTS (time 0) ===");
            for (int i = 0; i < Network.size(); i++) {
                Node n = Network.get(i);
                Linkable l = (Linkable) n.getProtocol(linkPid);
        
                StringBuilder sb = new StringBuilder();
                sb.append("Node ").append(n.getID()).append(" degree=").append(l.degree()).append(" neighbors=[");
        
                for (int j = 0; j < l.degree(); j++) {
                    sb.append(l.getNeighbor(j).getID());
                    if (j + 1 < l.degree()) sb.append(", ");
                }
                sb.append("]");
        
                System.out.println(sb.toString());
            }
            System.out.println("=== END NEIGHBORS ===\n");
        }
        
        // loop through all peers
        for (int i = 0; i < Network.size(); i++) {
            Node n = Network.get(i); // current peer node
            PeerProtocol proto = (PeerProtocol) n.getProtocol(pid); // search protocol being used
            proto.setLinkPid(linkPid); // give protocol access to neighbors

            // assign category and generate files at the peer node
            if (proto.getPeer() == null) {
                Category category = Category.fromIndex(n.getID());
                List<String> files = genFiles(category, random);
                // build keyword index 
                // keyword -> files containing keyword
                Map<String, List<String>> index = buildIndex(files);

                proto.setPeer(new Peer(category, files, index)); // attach peer data
            }

            // get peer and print debug info
            Peer p = proto.getPeer();
            List<String> sample = p.files.subList(0, Math.min(3, p.files.size()));
            System.out.println("Node " + n.getID()
                    + " category=" + p.category
                    + " sample=" + sample
                    + " time=" + t);
        }
        return false;
    }

    // helper to generate files for a given peer based on its category
    private List<String> genFiles(Category category, Random random) {
        List<String> out = new ArrayList<>(filesPerPeer);

        for (int i = 0; i < inCategory; i++) {
            String keyword = pickKeyword(category, random);
            out.add(makeFilename(category, keyword, random));
        }
        for (int i = 0; i < filesPerPeer - inCategory; i++) {
            Category other = pickOther(category, random);
            String keyword = pickKeyword(other, random);
            out.add(makeFilename(other, keyword, random));
        }
        return out;
    }

    // helper to pick a random category other than the given category
    private static Category pickOther(Category category, Random random) {
        Category[] vals = Category.values();
        Category other;
        do {
            other = vals[random.nextInt(vals.length)];
        } while (other == category);
        return other;
    }

    // helper to create .txt file names based on category, keyword, and a random integer
    // e.g. a MUSIC peer file may be named music_guitar_123.txt
    private static String makeFilename(Category category, String keyword, Random random) {
        return category.name().toLowerCase() + "_" + keyword + "_" + random.nextInt(1000) + ".txt";
    }

    // helper that returns random (preset) keywords based on category
    private static String pickKeyword(Category category, Random random) {
        String[] pool;
        switch (category) {
            case MUSIC: pool = new String[]{"guitar","piano","drum","song","album","jazz","rock","concert"}; break;
            case SPORTS: pool = new String[]{"soccer","nba","tennis","baseball","stats","team","coach","league"}; break;
            case MOVIES: pool = new String[]{"cinema","actor","director","trailer","drama","comedy","scene","film"}; break;
            case TECH: pool = new String[]{"java","linux","ai","network","database","cloud","security","api"}; break;
            default: pool = new String[]{"rpg","fps","mario","chess","strategy","quest","level","puzzle"}; break;
        }
        return pool[random.nextInt(pool.length)];
    }

    // helper that builds the search index
    // example:
    //    guitar → [file1, file2]
    //    piano → [file3]
    private Map<String, List<String>> buildIndex(List<String> files) {
        Map<String, List<String>> map = new HashMap<>();
    
        for (String f : files) {
            String keyword = extractKeyword(f);
            map.computeIfAbsent(keyword, k -> new ArrayList<>()).add(f);
        }
    
        return map;
    }

    // helper that extracts keywords based on filename
    // based on format: category_keyword_id.txt
    private String extractKeyword(String filename) {
        String[] parts = filename.split("_");
        if (parts.length >= 2) return parts[1];
        return filename;
    }
}
