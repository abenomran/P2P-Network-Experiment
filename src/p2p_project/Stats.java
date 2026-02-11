package p2p_project;

import java.util.HashMap;
import java.util.Map;

// for collecting analysis metrics
public class Stats {
    public static long queryForwards = 0;
    public static long hitsSent = 0;
    public static long hitsReceivedAtOrigin = 0;

    // mapping qid -> startTime
    public static final Map<Long, Long> startTime = new HashMap<>();
    // mapping qid -> hitTime
    public static final Map<Long, Long> hitTime = new HashMap<>();

    public static void reset() {
        queryForwards = 0;
        hitsSent = 0;
        hitsReceivedAtOrigin = 0;
        startTime.clear();
        hitTime.clear();
    }
}
