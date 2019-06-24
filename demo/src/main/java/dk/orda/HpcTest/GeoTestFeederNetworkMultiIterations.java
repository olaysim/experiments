package dk.orda.HpcTest;

import dk.orda.overlaynetwork.net.NetworkConfiguration;
import dk.orda.overlaynetwork.net.rpc.SuperNode;
import dk.orda.overlaynetwork.overlay.OverlayFactory;
import dk.orda.overlaynetwork.overlay.dht.DistributedHashTableConfiguration;
import dk.orda.overlaynetwork.overlay.dht.Node;
import dk.orda.overlaynetwork.overlaygeo.GeoDistributedHashTableImpl;
import dk.orda.overlaynetwork.overlaygeo.GeoKademliaOverlay;
import dk.orda.overlaynetwork.overlaygeo.LatLon;
import dk.orda.overlaynetwork.overlaygeo.SmallWorldBucket;
import dk.orda.overlaynetwork.statistics.StatConfiguration;
import dk.orda.overlaynetwork.statistics.StatLogger;
import dk.orda.overlaynetwork.statistics.dijkstra.Dijkstra;
import dk.orda.overlaynetwork.statistics.dijkstra.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.text.Format;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

@SuppressWarnings("Duplicates")
public class GeoTestFeederNetworkMultiIterations {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    Format df = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm").withZone(ZoneId.systemDefault()).toFormat();

    public void RunTestFeederNetwork(int network, int iterativenetwork, int bucketsize) throws Exception {

        String OUTPUT_FOLDER = "test_iterative_13";

        RunTestFeederNetwork(network, iterativenetwork, bucketsize, OUTPUT_FOLDER, 3, 200);
    }


    static class Misses {
        public double missesSum = 0;
        public double missesCount = 0;
        public double bsizeSum = 0;
        public double bsizeCount = 0;

        public double getResult() {
            return missesSum / missesCount;
        }

        public double getbSize() {
            return bsizeSum / bsizeCount;
        }
    }
    public void RunTestFeederNetwork(int network, int iterativenetwork, int bucketsize, String OUTPUT_FOLDER, int ITERATIONS, int SUBITERATIONS) throws Exception {

//        ExecutorService threadPool = Executors.newFixedThreadPool(500);
        Random rand = new Random();
        StatConfiguration scout = new StatConfiguration();
        scout.setCorrelationId("333");
        StatLogger slout = new StatLogger(scout);
        slout.initialize(OUTPUT_FOLDER);

        // results
        List<Double> res_subtotals = new ArrayList<>();
        List<List<Integer>> res_steps = new ArrayList<>();
        List<List<Integer>> res_spp = new ArrayList<>();
        List<List<Double>> res_factor = new ArrayList<>();
        List<List<Double>> res_misses = new ArrayList<>();
        List<List<Double>> res_bsize = new ArrayList<>();
        List<List<Double>> res_bcsize = new ArrayList<>();

        Path lines = null;
        Path buscoords = null;
        if (network == 1) {
            lines = Paths.get("Lines.csv");
            buscoords = Paths.get("Buscoords.csv");
        }
        if (network == 2) {
            lines = Paths.get("8500_tue_lines.csv");
            buscoords = Paths.get("8500_tue_buscoords.csv");
        }
        if (network == 3) {
            lines = Paths.get("netx2_lines_2000.csv");
            buscoords = Paths.get("netx2_buscoords_2000.csv");
        }
        if (network == 4) {
            lines = Paths.get("netx2_lines_10000.csv");
            buscoords = Paths.get("netx2_buscoords_10000.csv");
        }

        FileReader fileReader = new FileReader(buscoords.toFile());
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        NetworkConfiguration netconf = new NetworkConfiguration();
        netconf.setLocalBroadcasting(false);
        netconf.setSeedServer(false);
        DistributedHashTableConfiguration dhtConf = new DistributedHashTableConfiguration();
        dhtConf.setK(200000);
        dhtConf.setA(3);
        Map<String, GeoKademliaOverlay> map = new HashMap<>();

        String corrid = "50";
        StatConfiguration statConfiguration = new StatConfiguration("0", corrid);

        Graph graph = new Graph();
        Map<String, dk.orda.overlaynetwork.statistics.dijkstra.Node> graphNodes = new HashMap<>();

        SuperNode sn = null;
        String line;
        // read coords and create ONs (and graph for dijkstra)
        while ((line = bufferedReader.readLine()) != null) {
            List<String> strings = Arrays.asList(line.split(","));
            StatLogger sl = new StatLogger(statConfiguration);
            sl.initialize(OUTPUT_FOLDER);
            if (sn == null) {
                double x = Double.parseDouble(strings.get(1));
                double y = Double.parseDouble(strings.get(2));
                String name = strings.get(0).trim();
                GeoKademliaOverlay on = (GeoKademliaOverlay) OverlayFactory.createGeoKademlia(name, x, y, netconf, dhtConf, null, sl);
                on.setInitialization(true);
                sn = on.getSuperNode();
                on.getSelf().putDict("x", strings.get(1));
                on.getSelf().putDict("y", strings.get(2));
                map.put(name, on);
                SmallWorldBucket swbucket = ((GeoDistributedHashTableImpl) on.getDHT()).getSmallWorldBucket();
                swbucket.setSizeClose(bucketsize);
                swbucket.setSizeHub(bucketsize);
                swbucket.setSizeRandom(bucketsize);
                swbucket.setSizePassive(bucketsize * 5);
            } else {
                double x = Double.parseDouble(strings.get(1));
                double y = Double.parseDouble(strings.get(2));
                String name = strings.get(0).trim();
                GeoKademliaOverlay on = (GeoKademliaOverlay) OverlayFactory.createGeoKademlia(name, x, y, netconf, dhtConf, sn, sl);
                on.setInitialization(true);
                on.getSelf().putDict("x", strings.get(1));
                on.getSelf().putDict("y", strings.get(2));
                map.put(name, on);
                SmallWorldBucket swbucket = ((GeoDistributedHashTableImpl) on.getDHT()).getSmallWorldBucket();
                swbucket.setSizeClose(bucketsize);
                swbucket.setSizeHub(bucketsize);
                swbucket.setSizeRandom(bucketsize);
                swbucket.setSizePassive(bucketsize * 5);
            }

            // add to dijkstra graph
            String name = strings.get(0).trim();
            dk.orda.overlaynetwork.statistics.dijkstra.Node n = new dk.orda.overlaynetwork.statistics.dijkstra.Node(name);
            graphNodes.put(name, n);
            graph.addNode(n);
        }
        bufferedReader.close();
        fileReader.close();

        fileReader = new FileReader(lines.toFile());
        bufferedReader = new BufferedReader(fileReader);

        // use lines.csv to updateNodes to connect nodes
        while ((line = bufferedReader.readLine()) != null) {
            List<String> strings = Arrays.asList(line.split(","));

            // European low level voltage test feeder + networkX
            if (network == 1 || network == 3 || network == 2 || network == 4) {
                String bus1 = strings.get(1).trim();
                String bus2 = strings.get(2).trim();
                map.get(bus1).updateNode(map.get(bus2).getSelf());
                map.get(bus2).updateNode(map.get(bus1).getSelf());
                graphNodes.get(bus1).addDestination(graphNodes.get(bus2));
                graphNodes.get(bus2).addDestination(graphNodes.get(bus1));
            }

            // 8500 test feeder network
//                if (network == 2) {
//                    String bus1 = strings.get(1).trim();
//                    String bus2 = strings.get(3).trim();
//                    if (map.get(bus1) == null) {
//                    }// log.warn("Unable to find " + bus1 + " in lines.csv"); }
//                    else if (map.get(bus2) == null) {
//                    }// log.warn("Unable to find " + bus2 + " in lines.csv"); }
//                    else {
//                        map.get(bus1).updateNode(map.get(bus2).getSelf());
//                        map.get(bus2).updateNode(map.get(bus1).getSelf());
//                        graphNodes.get(bus1).addDestination(graphNodes.get(bus2));
//                        graphNodes.get(bus2).addDestination(graphNodes.get(bus1));
//                    }
//                }
        }

//        for (Map.Entry<String, GeoKademliaOverlay> entry : map.entrySet()) {
//            entry.getValue().start();
//        }
//
//        for (Map.Entry<String, GeoKademliaOverlay> entry : map.entrySet()) {
//            entry.getValue().logDht();
//        }
//
//        for (Map.Entry<String, GeoKademliaOverlay> entry : map.entrySet()) {
//            entry.getValue().setInitialization(false);
//        }

        for (Map.Entry<String, GeoKademliaOverlay> entry : map.entrySet()) {
            entry.getValue().saveDhtState();
        }



        for (int testitr = 1; testitr <= ITERATIONS; testitr++) {
            Double sub_subtotals = 0.0;
            List<Integer> sub_steps = new ArrayList<>();
            List<Integer> sub_spp = new ArrayList<>();
            List<Double> sub_factor = new ArrayList<>();
            List<Double> sub_misses = new ArrayList<>();
            List<Double> sub_bsize = new ArrayList<>();
            List<Double> sub_bcsize = new ArrayList<>();
            res_steps.add(sub_steps);
            res_spp.add(sub_spp);
            res_factor.add(sub_factor);
            res_misses.add(sub_misses);
            res_bsize.add(sub_bsize);
            res_bcsize.add(sub_bcsize);


            String testid = String.valueOf(testitr);
            statConfiguration.setTestId(testid);


            // DEBUG AREA

////            GeoKademliaOverlay ona = map.get("348");
////            GeoKademliaOverlay targeta = map.get("293");
////            GeoKademliaOverlay ona = map.get("722");
////            GeoKademliaOverlay targeta = map.get("881");
//            GeoKademliaOverlay ona = map.get("303");
//            GeoKademliaOverlay targeta = map.get("216");
//            LatLon latLona = targeta.getSelf().tryGetLatLon();
//
////            for (int i=0; i < 99999; i++) {
//                Node nodea = ona.findNode(targeta.getSelf().getId(), latLona.getLat(), latLona.getLon());
//                Node nodeb = ona.findNode(targeta.getSelf().getId(), latLona.getLat(), latLona.getLon());
////                if (nodea != null) {
////                    for (Map.Entry<String, GeoKademliaOverlay> entry : map.entrySet()) {
////                        entry.getValue().stop();
////                    }
////                    System.out.println("NOT THIS TIME " + testitr);
////                    continue;
////                }
////            }
//            int breakpoint = 0;
//            Thread.sleep(999999999);






            // European low level voltage test feeder
            int start = 0;
            if (network == 1) {
                start = rand.nextInt(905) + 1;
            }

            // 8500 test feeder network
            GeoKademliaOverlay startOn = null;
            if (network == 2) {
                start = rand.nextInt(2533) + 1;
                int itr = 1;
                for (String s : map.keySet()) {
                    if (itr == start) {
                        startOn = map.get(s);
                    }
                    itr++;
                }
            }

            // networkx 2000
            if (network == 3) {
                start = rand.nextInt(1999) + 1;
            }

            // networkx 20.000
            if (network == 4) {
//                start = rand.nextInt(19999) + 1;
                start = rand.nextInt(9999) + 1;
            }


            GeoKademliaOverlay on = null;
            if (network == 1 || network == 3 || network == 4) {
                on = map.get(String.valueOf(start));
            }
            if (network == 2) {
                on = startOn;
            }

            HashMap<String, dk.orda.overlaynetwork.statistics.dijkstra.Node> graphNodes1 = new HashMap<>();
            Graph graph1 = graph.cloneWithHelperNodeMap(graphNodes1);
            graph1 = Dijkstra.calculateShortestPathFromSource(graph1, graphNodes1.get(on.getSelf().getIdStr()));


            // SUB ITERATIONS
            for (int subitr = 0; subitr < SUBITERATIONS; subitr++) {

                // European low level voltage test feeder
                int end = -1;
                if (network == 1) {
                    while (end == start || end == -1) {
                        end = rand.nextInt(905) + 1;
                    }
                }

                // 8500 test feeder network
                GeoKademliaOverlay endOn = null;
                if (network == 2) {
                    while (end == start || end == -1) {
                        end = rand.nextInt(2533) + 1;
                    }
                    int itr = 1;
                    for (String s : map.keySet()) {
                        if (itr == end) {
                            endOn = map.get(s);
                        }
                        itr++;
                    }
                }

                // networkx 2000
                if (network == 3) {
                    while (end == start || end == -1) {
                        end = rand.nextInt(1999) + 1;
                    }
                }

                // networkx 20.000
                if (network == 4) {
                    while (end == start || end == -1) {
//                        end = rand.nextInt(19999) + 1;
                        end = rand.nextInt(9999) + 1;
                    }
                }


                GeoKademliaOverlay target = null;
                if (network == 1 || network == 3 || network == 4) {
                    target = map.get(String.valueOf(end));
                }
                if (network == 2) {
                    target = endOn;
                }
                LatLon latLon = target.getSelf().tryGetLatLon();

                int spp = graphNodes1.get(target.getSelf().getIdStr()).getDistance() < Integer.MAX_VALUE ? graphNodes1.get(target.getSelf().getIdStr()).getDistance() : 0;
                log.info("Shortest path from " + on.getSelf().getIdStr() + " to " + target.getSelf().getIdStr() + ": " + spp);

                log.info("Running test: " + testitr + " sub iteration: " + (subitr+1) + " start: " + start + " end: " + end);
                Node node = on.findNodeLocal(target.getSelf(), latLon.getLat(), latLon.getLon());
                if (node == null) {
                    log.error("miss on active node");
                }

                List<String> routing = on.getSuperNode().getStatLogger().getLines().get("routing");

                sub_bcsize.add((double) on.getKBucketCount());

                try {
                    String[] routing_split = routing.get(routing.size() - 1).split(";");
                    boolean valid = routing_split[45].equalsIgnoreCase("1.0");
                    int steps = Integer.parseInt(routing_split[38]);
                    double factor = 0.0;
                    if (steps > 0) factor = (double) steps / (double) spp;
                    if (valid) sub_subtotals += factor;
                    if (valid) res_steps.get(testitr - 1).add(steps);
                    else res_steps.get(testitr - 1).add(-1);
                    if (valid) res_spp.get(testitr - 1).add(spp);
                    else res_spp.get(testitr - 1).add(-1);
                    if (valid) res_factor.get(testitr - 1).add(factor);
                    else res_factor.get(testitr - 1).add(-1.0);
                } catch (Exception e) {
                    log.error("Failed to read stats from search query", e);
                }

                // ITERATIVE NETWORK
                // don't run findnode on current active ON
                // don't exercise every node, only ?0% of the nodes
                final Misses misses = new Misses();
                if (iterativenetwork == 1) {
                    ExecutorService threadPool = Executors.newCachedThreadPool();
//                    List<Callable<String>> tasks = new ArrayList<>();
//                    int tcount = 0;

                    for (Map.Entry<String, GeoKademliaOverlay> entry : map.entrySet()) {
//                        if (!entry.getKey().equalsIgnoreCase(on.getSelf().getIdStr())) {
                        if (!entry.getKey().equalsIgnoreCase(on.getSelf().getIdStr()) && rand.nextDouble() <= 0.25) {
                            // European low level voltage test feeder
                            int itrend = -1;
                            if (network == 1) {
                                itrend = rand.nextInt(905) + 1;
                            }
                            // 8500 test feeder network
                            GeoKademliaOverlay itrEndOn = null;
                            if (network == 2) {
                                itrend = rand.nextInt(2533) + 1;
                                int itritr = 1;
                                for (String s : map.keySet()) {
                                    if (itritr == itrend) {
                                        itrEndOn = map.get(s);
                                    }
                                    itritr++;
                                }
                            }
                            // networkx 2000
                            if (network == 3) {
                                itrend = rand.nextInt(1999) + 1;
                            }

                            // networkx 20.000
                            if (network == 4) {
//                                itrend = rand.nextInt(19999) + 1;
                                itrend = rand.nextInt(9999) + 1;
                            }

                            GeoKademliaOverlay itrTarget = null;
                            if (network == 1 || network == 3 || network == 4) {
                                itrTarget = map.get(String.valueOf(itrend));
                            }
                            if (network == 2) {
                                itrTarget = itrEndOn;
                            }
                            final LatLon itrLatLon = itrTarget.getSelf().tryGetLatLon();

                            final GeoKademliaOverlay finalItrTarget = itrTarget;
                            threadPool.submit(() -> {
//                            tasks.add(() -> {
                                try {
                                    Node res = entry.getValue().findNodeLocal(finalItrTarget.getSelf(), itrLatLon.getLat(), itrLatLon.getLon());
                                    if (res == null) {
//                                        log.warn("miss null");
                                        misses.missesSum += 1;
                                    }
                                } catch (Exception e) {
//                                    log.error("miss excep");
                                    misses.missesSum += 1;
                                }
                                misses.missesCount += 1;
                                misses.bsizeSum += entry.getValue().getKBucketCount();
                                misses.bsizeCount += 1;
                                return null;
                            });
//                            tcount++;
//                            if (tcount > 1000) {
//                                threadPool.shutdown();
//                                threadPool.awaitTermination(2, TimeUnit.MINUTES);
//                                try {
//                                    Thread.sleep(1000);
//                                } catch (Exception ignore) {}
//                                tcount = 0;
//                                threadPool = Executors.newCachedThreadPool();
//                            }
                        }
                    }
//                    if (!threadPool.isShutdown()) {
//                        threadPool.shutdown();
//                        threadPool.awaitTermination(4, TimeUnit.MINUTES);
//                        try {
//                            Thread.sleep(1000);
//                        } catch (Exception ignore) {}
//                    }
//                    threadPool.invokeAll(tasks, 2, TimeUnit.MINUTES);
                    threadPool.shutdown();
                    threadPool.awaitTermination(5, TimeUnit.MINUTES);
                }
                sub_misses.add(misses.getResult());
                sub_bsize.add(misses.getbSize());

            } // END SUBITERATION
            res_subtotals.add(sub_subtotals);
            sub_subtotals = new Double(0.0);

//            for (Map.Entry<String, GeoKademliaOverlay> entry : map.entrySet()) {
//                entry.getValue().stop();
//            }
            for (Map.Entry<String, GeoKademliaOverlay> entry : map.entrySet()) {
                entry.getValue().reloadDhtState();
            }
        }
//        slout.write();
        Path path = Paths.get("data", OUTPUT_FOLDER);
        Files.createDirectories(path);
        Path file_subtotal = Paths.get("data", OUTPUT_FOLDER, "geo_results_subtotal_1" + network + iterativenetwork + "-" + bucketsize + "_" + df.format(Instant.now()) + ".csv");
        Path file_steps    = Paths.get("data", OUTPUT_FOLDER, "geo_results_steps_1"    + network + iterativenetwork + "-" + bucketsize + "_" + df.format(Instant.now()) + ".csv");
        Path file_spp      = Paths.get("data", OUTPUT_FOLDER, "geo_results_spp_1"      + network + iterativenetwork + "-" + bucketsize + "_" + df.format(Instant.now()) + ".csv");
        Path file_factor   = Paths.get("data", OUTPUT_FOLDER, "geo_results_factor_1"   + network + iterativenetwork + "-" + bucketsize + "_" + df.format(Instant.now()) + ".csv");
        Path file_misses   = Paths.get("data", OUTPUT_FOLDER, "geo_results_misses_1"   + network + iterativenetwork + "-" + bucketsize + "_" + df.format(Instant.now()) + ".csv");
        Path file_bsize    = Paths.get("data", OUTPUT_FOLDER, "geo_results_bsize_1"    + network + iterativenetwork + "-" + bucketsize + "_" + df.format(Instant.now()) + ".csv");
        Path file_bcsize   = Paths.get("data", OUTPUT_FOLDER, "geo_results_bcsize_1"   + network + iterativenetwork + "-" + bucketsize + "_" + df.format(Instant.now()) + ".csv");
        OpenOption[] openOptions = new OpenOption[] {StandardOpenOption.CREATE, StandardOpenOption.APPEND};
        BufferedWriter writer_subtotal = Files.newBufferedWriter(file_subtotal, Charset.forName("UTF-8"), openOptions);
        BufferedWriter writer_steps = Files.newBufferedWriter(file_steps, Charset.forName("UTF-8"), openOptions);
        BufferedWriter writer_spp = Files.newBufferedWriter(file_spp, Charset.forName("UTF-8"), openOptions);
        BufferedWriter writer_factor = Files.newBufferedWriter(file_factor, Charset.forName("UTF-8"), openOptions);
        BufferedWriter writer_misses = Files.newBufferedWriter(file_misses, Charset.forName("UTF-8"), openOptions);
        BufferedWriter writer_bsize = Files.newBufferedWriter(file_bsize, Charset.forName("UTF-8"), openOptions);
        BufferedWriter writer_bcsize = Files.newBufferedWriter(file_bcsize, Charset.forName("UTF-8"), openOptions);
        try {
            for (int testitr = 0; testitr < ITERATIONS; testitr++) {
                StringBuffer strb_subtotal = new StringBuffer();
                StringBuffer strb_steps = new StringBuffer();
                StringBuffer strb_spp = new StringBuffer();
                StringBuffer strb_factor = new StringBuffer();
                StringBuffer strb_misses = new StringBuffer();
                StringBuffer strb_bsize = new StringBuffer();
                StringBuffer strb_bcsize = new StringBuffer();

                // subtotal
                strb_subtotal.append((testitr+1) + ";" + res_subtotals.get(testitr)/res_steps.get(testitr).size());

                // steps
                strb_steps.append((testitr+1) + ";");
                for (int i = 0; i < res_steps.get(testitr).size(); i++) {
                    int value = res_steps.get(testitr).get(i);
//                    if (value == -1) strb_spp.append(";");
//                    else strb_spp.append(value + ";");
                    strb_steps.append(value);
                    if (i < res_steps.get(testitr).size()-1) strb_steps.append(";");
                }

                // spp
                strb_spp.append((testitr+1) + ";");
                for (int i = 0; i < res_spp.get(testitr).size(); i++) {
                    int value = res_spp.get(testitr).get(i);
//                    if (value == -1) strb_spp.append(";");
//                    else strb_spp.append(value + ";");
                    strb_spp.append(value);
                    if (i < res_spp.get(testitr).size()-1) strb_spp.append(";");
                }

                // factor
                strb_factor.append((testitr+1) + ";");
                for (int i = 0; i < res_factor.get(testitr).size(); i++) {
                    double value = res_factor.get(testitr).get(i);
//                    if (value == -1.0) strb_factor.append(";");
//                    else strb_factor.append(value + ";");
                    strb_factor.append(value);
                    if (i < res_factor.get(testitr).size()-1) strb_factor.append(";");
                }

                // misses
                strb_misses.append((testitr+1) + ";");
                for (int i = 0; i < res_misses.get(testitr).size(); i++) {
                    double value = res_misses.get(testitr).get(i);
//                    if (value == -1.0) strb_misses.append("0.0;");
//                    else strb_misses.append(value + ";");
                    strb_misses.append(value);
                    if (i < res_misses.get(testitr).size()-1) strb_misses.append(";");
                }

                // bsize
                strb_bsize.append((testitr+1) + ";");
                for (int i = 0; i < res_bsize.get(testitr).size(); i++) {
                    double value = res_bsize.get(testitr).get(i);
//                    if (value == -1.0) strb_bsize.append(";");
//                    else strb_bsize.append(value + ";");
                    strb_bsize.append(value);
                    if (i < res_bsize.get(testitr).size()-1) strb_bsize.append(";");
                }

                // bcsize
                strb_bcsize.append((testitr+1) + ";");
                for (int i = 0; i < res_bcsize.get(testitr).size(); i++) {
                    double value = res_bcsize.get(testitr).get(i);
//                    if (value == -1.0) strb_bcsize.append(";");
//                    else strb_bcsize.append(value + ";");
                    strb_bcsize.append(value);
                    if (i < res_bcsize.get(testitr).size()-1) strb_bcsize.append(";");
                }

                writer_subtotal.write(strb_subtotal.toString() + '\n');
                writer_steps.write(strb_steps.toString() + '\n');
                writer_spp.write(strb_spp.toString() + '\n');
                writer_factor.write(strb_factor.toString() + '\n');
                writer_misses.write(strb_misses.toString() + '\n');
                writer_bsize.write(strb_bsize.toString() + '\n');
                writer_bcsize.write(strb_bcsize.toString() + '\n');
                int h = 0;
            }
        } catch (IOException e) {
            log.error("Unable to write experiment data to file", e);
        } finally {
            writer_subtotal.close();
            writer_steps.close();
            writer_spp.close();
            writer_factor.close();
            writer_misses.close();
            writer_bsize.close();
            writer_bcsize.close();
        }
    }
}
