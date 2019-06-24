package dk.syslab.proxy.experiment1;

import dk.orda.overlaynetwork.statistics.StatLogger;
import dk.syslab.supv.client.SupvApi;
import dk.syslab.supv.client.SupvClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ExperimentController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private ExperimentAgent experimentAgent;
    private SupvApi api;
    private static final String apiHost = "syslab-v02.syslab.dk:9080";
    private static final String token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJvcmRhIiwiaWF0IjoxNTIzNDg0MDAwLCJleHAiOjE1MzIxMjQwMDAsImlzcyI6InJlc3QiLCJhZG1pbiI6ZmFsc2V9.p57Mm8RhZvj4cPOT6uvPATfuC7eHcG5xs4cafGXakCuoq6briQMj5U7nomXT03PEQRRpg2cXE_brbtiRfVICRyGp4aC4ZJHeLvWxqpPiTB8_5WK2C5e5K4dwEzKhTv7Juw9jl3CBV78e9_t3M844sZz69Y0empR-lqn3Zy3kKSygfTbinCxF2AzoJB7EbF4GqY8SaRV0prAGIjyEt113cuDx5HTPBq6GYcvpXRCqfU-ShJ5-BJ72k7b4X0zxdiCWgs_EVrQZWNgh87dK0R4VtT6H7T-ir4p9Dbm-UdUd12OA7QrjgvhaTbzJgEyaCTq-C4-QUTGNvRKtQ2CNM6GSkg";
    List<String> nodes;


    public ExperimentController(ExperimentAgent experimentAgent) {
        this.experimentAgent = experimentAgent;
        this.api = SupvClient.getApi();

        this.nodes = Arrays.asList(
            "syslab-01",
            "syslab-02",
            "syslab-03",
            "syslab-05",
            "syslab-07",
            "syslab-09",
            "syslab-10",
            "syslab-11",
            "syslab-12",
            "syslab-13",
            "syslab-18",
            "syslab-22",
            "syslab-24",
            "syslab-25",
            "syslab-26",
            "syslab-28",
            "syslab-29",
            "syslab-30",
            "syslab-31",
            "syslab-33",
            "flexhouse-01",
            "flexhouse-02",
            "flexhouse-03",
            "syslab-ui01",
            "syslab-ui02",
            "syslab-ui07");
    }

    public void runexperiment(int expnmbr) {
        try {
            String corrid = UUID.randomUUID().toString();

            // initialize overlay network
            log.info("Initializing nodes 1");
            api.nodes().sendMessage(apiHost, nodes, "syslabproxy", "init 1 1 " + corrid + "\n", token);
            experimentAgent.initialize(1, "1", corrid);
            Thread.sleep(10000);

            log.info("Initializing nodes 2");
            api.nodes().sendMessage(apiHost, nodes, "syslabproxy", "init 2 1 " + corrid + "\n", token);
            experimentAgent.initialize(2, "1", corrid);
            Thread.sleep(6000);

            log.info("Running experiment " + expnmbr);
            switch (expnmbr) {
                case 1:
                    runhelper(1, corrid);
                    runhelper(2, corrid);
                    runhelper(3, corrid);
                    runhelper(4, corrid);
                    runhelper(5, corrid);
                    runhelper(6, corrid);
                    runhelper(7, corrid);
                    runhelper(8, corrid);
                    runhelper(9, corrid);
                    runhelper(10, corrid);
                    runhelper(11, corrid);
                    runhelper(12, corrid);
                    runhelper(13, corrid);
                    runhelper(14, corrid);
                    runhelper(15, corrid);
                    runhelper(16, corrid);
                    runhelper(17, corrid);
                    runhelper(18, corrid);
                    runhelper(19, corrid);
                    runhelper(20, corrid);
                    runhelper(21, corrid);
                    runhelper(22, corrid);
                    runhelper(23, corrid);
                    runhelper(24, corrid);

                    log.info("Writing experiment data");
                    experimentAgent.getStatLogger().write();
                    api.nodes().sendMessage(apiHost, nodes, "syslabproxy", "write\n", token);
                    break;
                case 2:
                    runhelper(24, corrid);
                    break;

                case 3:
                    experimentAgent.saveValueTest(corrid);
                    // reset overlay network
                    api.nodes().sendMessage(apiHost, nodes, "syslabproxy", "reset 1 " + corrid + "\n", token);
                    experimentAgent.reset("1", corrid);
                    break;
            }
        } catch (Exception e) {
            log.error("Exception while running experiment: ", e);
        }
    }

    private void runhelper(int nr, String correlationid) throws InterruptedException, IOException, URISyntaxException {
        // run test
        experimentAgent.runtest(nr);
        Thread.sleep(2000);

        // stop overlay network
//        api.nodes().sendMessage(apiHost, nodes, "syslabproxy", "stop\n", token);
//        api.nodes().signalProgram(apiHost, nodes, "syslabproxy", "SIGHUP", token);
//        experimentAgent.stop();
//        Thread.sleep(2000);

        // reset overlay network
        api.nodes().sendMessage(apiHost, nodes, "syslabproxy", "reset " + nr+1 + " " + correlationid + "\n", token);
        experimentAgent.reset(String.valueOf(nr+1), correlationid);
        Thread.sleep(3000);
    }
}
