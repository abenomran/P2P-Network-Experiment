package p2p_project;

import peersim.config.Configuration;

import java.io.File;
import java.io.FileWriter;

import java.util.HashMap;
import java.util.Map;

// for collecting analysis metrics and saving data
public class Stats {
    public static long queryForwards = 0;
    public static long hitsSent = 0;
    public static long hitsReceivedAtOrigin = 0;

    // mapping qid -> startTime
    public static final Map<Long, Long> startTime = new HashMap<>();
    // mapping qid -> hitTime
    public static final Map<Long, Long> hitTime = new HashMap<>();

    // data collection configuration
    private static final String OUTDIR = "run_outputs";
    private static final String CFG_TAG = "stats.tag";
    private static final String CFG_PROTOCOL = "stats.protocol";

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                writeCsv();
            } catch (Exception e) {
                System.err.println("Stats write failed: " + e);
            }
        }));
    }
    
    // write data to csv
    private static void writeCsv() throws Exception {
    
        String protocol = Configuration.getString(CFG_PROTOCOL, "unknown");
        String tag = Configuration.getString(CFG_TAG, "exp");
    
        // runID = timestamp
        long run = System.currentTimeMillis();
    
        File dir = new File(OUTDIR);
        dir.mkdirs();
    
        File summaryDir = new File(OUTDIR + "/" + "summary");
        File perQueryDir = new File(OUTDIR + "/" + "query");

        summaryDir.mkdirs();
        perQueryDir.mkdirs();

        File summary = new File(summaryDir,
                protocol + "_" + tag + "_" + run + "_summary.csv");

        File perQuery = new File(perQueryDir,
                protocol + "_" + tag + "_" + run + "_perquery.csv");

    
        writeSummary(summary, protocol, tag, run);
        writePerQuery(perQuery, protocol, tag, run);
    
        System.out.println("Saved stats -> " + summary.getName());
    }

    // writes one row per sim run
    private static void writeSummary(File f, String protocol, String tag, long run) throws Exception {

        int injected = startTime.size();
        int served = hitTime.size();
    
        // average latency
        double avgLatency = 0;
        int count = 0;
    
        for (Long qid : hitTime.keySet()) {
            Long st = startTime.get(qid);
            Long ht = hitTime.get(qid);
            if (st != null && ht != null) {
                avgLatency += (ht - st);
                count++;
            }
        }
    
        if (count > 0) avgLatency /= count;
    
        // throughput estimate
        long minStart = startTime.values().stream().min(Long::compareTo).orElse(0L);
        long maxHit = hitTime.values().stream().max(Long::compareTo).orElse(0L);
        double throughput = served / (double) Math.max(1, maxHit - minStart + 1);
    
        try (FileWriter w = new FileWriter(f)) {
            w.write("protocol,tag,run,injected,served,avg_latency,throughput,forwards,hitsSent,hitsRecv\n");
            w.write(protocol + "," + tag + "," + run + "," +
                    injected + "," + served + "," +
                    avgLatency + "," + throughput + "," +
                    queryForwards + "," + hitsSent + "," + hitsReceivedAtOrigin + "\n");
        }
    }

    // writes query rows
    private static void writePerQuery(File f, String protocol, String tag, long run) throws Exception {

        try (FileWriter w = new FileWriter(f)) {
    
            w.write("protocol,tag,run,qid,start,hit,latency\n");
    
            for (Long qid : startTime.keySet()) {
    
                Long st = startTime.get(qid);
                Long ht = hitTime.get(qid);
                Long lat = (st != null && ht != null) ? (ht - st) : null;
    
                w.write(protocol + "," + tag + "," + run + "," +
                        qid + "," +
                        st + "," +
                        (ht == null ? "" : ht) + "," +
                        (lat == null ? "" : lat) + "\n");
            }
        }
    } 

    public static void reset() {
        queryForwards = 0;
        hitsSent = 0;
        hitsReceivedAtOrigin = 0;
        startTime.clear();
        hitTime.clear();
    }
}
