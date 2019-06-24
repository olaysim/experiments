package dk.syslab.proxy.experiment1;

import dk.orda.overlaynetwork.net.NetworkConfiguration;
import dk.orda.overlaynetwork.overlay.KademliaOverlay;
import dk.orda.overlaynetwork.overlay.OverlayFactory;
import dk.orda.overlaynetwork.overlay.OverlayNetwork;
import dk.orda.overlaynetwork.overlay.RequestOverlay;
import dk.orda.overlaynetwork.overlay.dht.Node;
import dk.orda.overlaynetwork.overlay.dht.Number160;
import dk.orda.overlaynetwork.statistics.RoutingStatistic;
import dk.orda.overlaynetwork.statistics.StatConfiguration;
import dk.orda.overlaynetwork.statistics.StatLogger;
import dk.orda.overlaynetwork.statistics.Value;
import dk.syslab.proxy.configuration.Config;
import dk.syslab.proxy.util.XmlSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class ExperimentAgent implements Runnable{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ScheduledExecutorService executor;
    private OverlayNetwork overlayNetwork;
    private Node self;
    private Config config;
    private XmlSerializer xmlSerializer;
    private boolean trucking = false;
    private Marker exp;
    private long time = Long.MAX_VALUE;
    private StatLogger statLogger;

    @Autowired
    public ExperimentAgent(Config config, XmlSerializer xmlSerializer) throws IOException {
        this.config = config;
        this.xmlSerializer = xmlSerializer;

        StatConfiguration  emptyConf = new StatConfiguration(null, null);
        statLogger = new StatLogger(emptyConf);
        statLogger.initialize("");

        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void initialize(int stage, String testid, String correlationid) {
        switch (stage) {
            case 1:
                StatConfiguration statConf = new StatConfiguration(testid, correlationid);
                statLogger.setConfig(statConf);
                statLogger.initialize("experiment2");
                NetworkConfiguration netconf = new NetworkConfiguration();
                netconf.setLocalBroadcasting(false);
                netconf.setSeedServer(true);
                netconf.setLocalSystemName(config.getName());
                overlayNetwork = OverlayFactory.createKademlia(config.getName(), netconf, null, null, statLogger);
                self = ((RequestOverlay)overlayNetwork).getSelf();
                log.info("*** Starting overlay network!");
                overlayNetwork.start();
                trucking = true;
                break;

            case 2:
                warmupConnections();
                log.info("*** Overlay network started...");
                break;
        }
    }

    public void start(long time) {
        System.out.println("NOT IMPLEMENTED");
        System.exit(-1);

        this.time = time;

        // wait for start time to arrive
        log.info("Waiting for time: " + time + " to arrive");
        log.info("system time " + System.currentTimeMillis() + ", time " + time);
        while (System.currentTimeMillis() < time) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                log.error("Failed to wait for start time", e);
            }
        }
        //        executor.schedule(this, 5, TimeUnit.SECONDS);

    }
    public void reset(String testid, String correlationid) {
        statLogger.getConfig().setTimestamp(System.currentTimeMillis());
        statLogger.getConfig().setTestId(testid);
        statLogger.getConfig().setCorrelationId(correlationid);
        overlayNetwork.resetDHT();
    }


    private void warmupConnections() {
        List<String> addresses = Arrays.asList(
            "10.42.241.1",
            "10.42.241.2",
            "10.42.241.3",
            "10.42.241.5",
            "10.42.241.7",
            "10.42.241.9",
            "10.42.241.10",
            "10.42.241.11",
            "10.42.241.12",
            "10.42.241.13",
            "10.42.241.18",
            "10.42.241.22",
            "10.42.241.24",
            "10.42.241.25",
            "10.42.241.26",
            "10.42.241.28",
            "10.42.241.29",
            "10.42.241.30",
            "10.42.241.31",
            "10.42.241.33",
            "10.42.241.201",
            "10.42.241.202",
            "10.42.241.203",
            "10.42.242.1",
            "10.42.242.2",
            "10.42.242.7"
        );
        for (String address : addresses) {
            overlayNetwork.warmupConnection(address);
        }
    }

    public void stop() {
        if (overlayNetwork != null) {
            overlayNetwork.stop();
        }
        overlayNetwork = null;
    }


    public void runtest(int testnmbr) {
        switch (testnmbr) {
            case 1:
                runtest("syslab-03"); // 1 hop
                break;
            case 2:
                runtest("syslab-05"); // 2 hop
                break;
            case 3:
                runtest("syslab-07"); // 3 hop
                break;
            case 4:
                runtest("syslab-09"); // 4 hop
                break;
            case 5:
                runtest("syslab-10"); // 5 hop
                break;
            case 6:
                runtest("syslab-11"); // 6 hop
                break;
            case 7:
                runtest("syslab-12"); // 7 hop
                break;
            case 8:
                runtest("syslab-13"); // 8 hop
                break;
//            case 9:
//                runtest("syslab-16"); // 9 hop
//                break;
            case 9:
                runtest("syslab-18"); // 9 hop
                break;
            case 10:
                runtest("syslab-22"); // 10 hop
                break;
            case 11:
                runtest("syslab-24"); // 11 hop
                break;
            case 12:
                runtest("syslab-25"); // 12 hop
                break;
            case 13:
                runtest("syslab-26"); // 13 hop
                break;
            case 14:
                runtest("syslab-28"); // 14 hop
                break;
            case 15:
                runtest("syslab-29"); // 15 hop
                break;
            case 16:
                runtest("syslab-30"); // 16 hop
                break;
            case 17:
                runtest("syslab-31"); // 17 hop
                break;
            case 18:
                runtest("syslab-33"); // 18 hop
                break;
            case 19:
                runtest("flexhouse-01"); // 19 hop
                break;
            case 20:
                runtest("flexhouse-02"); // 20 hop
                break;
            case 21:
                runtest("flexhouse-03"); // 21 hop
                break;
            case 22:
                runtest("syslab-ui01"); // 22 hop
                break;
            case 23:
                runtest("syslab-ui02"); // 23 hop
                break;
            case 24:
                runtest("syslab-ui07"); // 24 hop
                break;
        }
    }

    public void runtest(String trgt) {
        log.info("test lookup started " + System.currentTimeMillis() + ", target: " + trgt);
        log.info("I know about " + ((KademliaOverlay)overlayNetwork).getKBucketCount() + " nodes");
        statLogger.getConfig().setTimestamp(System.currentTimeMillis());

        // find node
        Number160 target = Number160.createHash(statLogger, trgt);
        Node result = overlayNetwork.findNode(target);
        if (result == null) {
            log.error(exp, "****** TEST FAILED! (skipping), target: " + trgt);
            return;
        }
        log.info("end of test run, target: " + trgt);
    }

    @Override
    public void run() {
        log.info("store value loop started " + System.currentTimeMillis());
        while (trucking) {
            // store value using unique key (number)
            Number160 skey = Number160.createHash(statLogger, config.getNumber());
            overlayNetwork.storeValue(skey, config.getNumber().getBytes(), 0);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("Experiment Agent's sleep was interrupted", e);
            }
        }
    }


    public void saveValueTest(String corrid) throws InterruptedException {
        log.info("storing value");
        String value = "syslab-33"; // remember datavalue is both key and target (so this value should be saved to syslab-3, which atm is 18 hops)
        Number160 skey = Number160.createHash(null, value);
        overlayNetwork.storeValue(skey, config.getNumber().getBytes(), 1);
        Thread.sleep(2000);
        log.info("finding value");
        byte[] bytes = overlayNetwork.findValue(skey);
        String res = new String(bytes);
        log.info("test done");
    }

    public StatLogger getStatLogger() {
        return statLogger;
    }
}
