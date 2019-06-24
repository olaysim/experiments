package dk.syslab.proxy.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import risoe.syslab.comm.shared.CommunicationException;
import risoe.syslab.comm.shared.RMIClientBase;
import risoe.syslab.comm.shared.RMITransportDetails;
import risoe.syslab.comm.typebased.interfaces.DEIFDieselGenset;
import risoe.syslab.comm.typebased.rmi.DEIFDieselGensetRMIClient;
import risoe.syslab.comm.typebased.rmi.GaiaWindTurbineRMIClient;
import risoe.syslab.comm.typebased.rmi.PVSystemRMIClient;
import risoe.syslab.comm.typebased.rmi.StandardSubstationRMIClient;
import risoe.syslab.labs.syslab.SyslabComponent;
import risoe.syslab.labs.syslab.SyslabRMI;
import risoe.syslab.model.CompositeMeasurement;
import risoe.syslab.model.devices.RMode;

import java.rmi.Naming;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//@Service
public class Aggregator implements Runnable {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ScheduledExecutorService executor;
    private OverlayNetworkComm comm;

    public Aggregator(@Autowired OverlayNetworkComm comm) {
        this.comm = comm;
//        executor = Executors.newSingleThreadScheduledExecutor();
//        executor.scheduleWithFixedDelay(this, 30,20, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        try {
            // let try and talk to producers
            log.info("There are " + comm.producers.size() + " producers.");
            for (Map.Entry<String, DerNode> entry : comm.producers.entrySet()) {
                DerNode node = entry.getValue();
                switch (entry.getKey()) {
                    case "syslab-02":
                        log.debug("Connecting to " + node.getName() + " (" + node.getDescription() + ")");
                        for (String name : Naming.list("rmi://" + node.getAddress())) {
                            log.debug(name);
                        }
//                        risoe.syslab.comm.typebased.rmi.
                        DEIFDieselGensetRMIClient client = new DEIFDieselGensetRMIClient();
                        connect(client, node.getAddress(),"genset1", "typebased_RMI_Diesel");
                        RMode rm = client.getCurrentRunningMode();
                        log.debug("Diesel running mode: " + rm.getMode() + "(" + rm.getDescription() + ")");
                        break;

                    case "syslab-03":
                        log.debug("Connecting to " + node.getName() + " (" + node.getDescription() + ")");
                        for (String name : Naming.list("rmi://" + node.getAddress())) {
                            log.debug(name);
                        }
                        GaiaWindTurbineRMIClient client1 = new GaiaWindTurbineRMIClient();
                        connect(client1, node.getAddress(),"gaia1", "typebased_RMI_WTGS");
                        CompositeMeasurement activePower = client1.getActivePower();
                        log.debug("Gaia active power: " + activePower.getValue());
                        break;

                    case "syslab-07":
                        log.debug("Connecting to " + node.getName() + " (" + node.getDescription() + ")");
                        for (String name : Naming.list("rmi://" + node.getAddress())) {
                            log.debug(name);
                        }
                        PVSystemRMIClient client2 = new PVSystemRMIClient();
                        connect(client2, node.getAddress(),"pv117", "typebased_RMI_PV");
                        CompositeMeasurement acActivePower = client2.getACActivePower();
                        log.debug("PV117 a AC ctive power: " + acActivePower.getValue());
                        break;

                    case "syslab-10":
                        log.debug("Connecting to " + node.getName() + " (" + node.getDescription() + ")");
                        for (String name : Naming.list("rmi://" + node.getAddress())) {
                            log.debug(name);
                        }
                        PVSystemRMIClient client3 = new PVSystemRMIClient();
                        connect(client3, node.getAddress(),"pv715", "typebased_RMI_PV");
                        CompositeMeasurement acActivePower1 = client3.getACActivePower();
                        log.debug("PV117 a AC ctive power: " + acActivePower1.getValue());
                        break;

                    case "syslab-12":
                        log.debug("Connecting to " + node.getName() + " (" + node.getDescription() + ")");
                        for (String name : Naming.list("rmi://" + node.getAddress())) {
                            log.debug(name);
                        }
                        break;

                    case "syslab-24":
                        log.debug("Connecting to " + node.getName() + " (" + node.getDescription() + ")");
                        for (String name : Naming.list("rmi://" + node.getAddress())) {
                            log.debug(name);
                        }
                        PVSystemRMIClient client5 = new PVSystemRMIClient();
                        connect(client5, node.getAddress(),"pv319", "typebased_RMI_PV");
                        CompositeMeasurement acActivePower2 = client5.getACActivePower();
                        log.debug("PV117 a AC ctive power: " + acActivePower2.getValue());
                        break;
                }


            }





        } catch (Exception e) {
            log.error("Aggregator error", e);
        }
    }

    private void connect(RMIClientBase client, String server, String unit, String ifname) throws CommunicationException {
        RMITransportDetails rtd = new RMITransportDetails(server, 1099, ifname, unit);
        client.setTransportDetails(rtd);
    }
}
