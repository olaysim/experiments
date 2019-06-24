package dk.syslab.proxy.comm;

import dk.orda.overlaynetwork.statistics.StatLogger;
import dk.syslab.proxy.experiment1.ExperimentAgent;
import dk.syslab.proxy.experiment1.ExperimentController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SupervisorReceiver implements Runnable {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private ExecutorService executorService;
    private Marker exp;
    private Scanner scanner;

    @Autowired
    ExperimentAgent experimentAgent;

    @Autowired
    ExperimentController controller;


    public SupervisorReceiver() {
        exp = MarkerFactory.getMarker("EXP");
        this.scanner = new Scanner(System.in);
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this);
    }

    @Override
    public void run() {
        while (true) {
            String input;
            input = scanner.next();

            if (input == null || input.isEmpty()) {
                log.error("Failed to read from System.in");
                continue;
            }

            if (input.equalsIgnoreCase("init")) {
                String stageStr = scanner.next();
                int stage = Integer.parseInt(stageStr);
                String testid = scanner.next();
                String correlationid = scanner.next();
                experimentAgent.initialize(stage, testid, correlationid);
            }

            if (input.equalsIgnoreCase("exp")) {
                String expnmbrStr = scanner.next();
                int expnmbr = Integer.parseInt(expnmbrStr);
                controller.runexperiment(expnmbr);
            }

            if (input.equalsIgnoreCase("start")) {
                String timeStr = scanner.next();
                long time = Long.parseLong(timeStr);
                log.info(exp, String.valueOf(System.currentTimeMillis()));
                experimentAgent.start(time);
            }
            if (input.equalsIgnoreCase("stop")) {
                log.info(exp, "*** Stopping overlay network!");
                log.info(exp, String.valueOf(System.currentTimeMillis()));
                experimentAgent.stop();
            }

            if (input.equalsIgnoreCase("test")) {
                String stageStr = scanner.next();
                int testnmbr = Integer.parseInt(stageStr);
                experimentAgent.runtest(testnmbr);
            }

            if (input.equalsIgnoreCase("write")) {
                log.info(exp, "*** writing out data");
                log.info(exp, String.valueOf(System.currentTimeMillis()));
                experimentAgent.getStatLogger().write();
            }

            if (input.equalsIgnoreCase("reset")) {
                String testid = scanner.next();
                String correlationid = scanner.next();
                log.info(exp, "*** resetting Distributed Hash Table");
                experimentAgent.reset(testid, correlationid);
            }
        }
    }
}
