package dk.syslab.proxy.comm;

import dk.orda.overlaynetwork.overlay.OverlayFactory;
import dk.orda.overlaynetwork.overlay.OverlayNetwork;
import dk.orda.overlaynetwork.overlay.RequestOverlay;
import dk.orda.overlaynetwork.overlay.dht.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class AggregatorService implements Runnable{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ScheduledExecutorService executor;
    private OverlayNetwork overlayNetwork;
    private Node self;

    @Autowired
    public AggregatorService() {
        overlayNetwork = OverlayFactory.createKademlia("aggregator");
        overlayNetwork.start();
        self = ((RequestOverlay)overlayNetwork).getSelf();
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(this, 0,10, TimeUnit.SECONDS);
    }

    @Override
    public void run() {

    }
}
