package dk.syslab.proxy.comm;

import dk.orda.overlaynetwork.overlay.OverlayFactory;
import dk.orda.overlaynetwork.overlay.OverlayNetwork;
import dk.orda.overlaynetwork.overlay.RequestOverlay;
import dk.orda.overlaynetwork.overlay.dht.Node;
import dk.orda.overlaynetwork.overlay.dht.Number160;
import dk.orda.overlaynetwork.statistics.RoutingStatistic;
import dk.syslab.proxy.configuration.Config;
import dk.syslab.proxy.util.XmlSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//@Service
public class OverlayNetworkComm implements Runnable{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ScheduledExecutorService executor;
    private OverlayNetwork overlayNetwork;
    private Node self;
    private Config config;
    private XmlSerializer xmlSerializer;


    public Map<String, DerNode> producers = new Hashtable<>();
    public Map<String, DerNode> consumers = new Hashtable<>();
    public Map<String, DerNode> substations = new Hashtable<>();

    @Autowired
    public OverlayNetworkComm(Config config, XmlSerializer xmlSerializer) {
        this.config = config;
        this.xmlSerializer = xmlSerializer;
        overlayNetwork = OverlayFactory.createKademlia(config.getName());
        overlayNetwork.start();
        self = ((RequestOverlay)overlayNetwork).getSelf();
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(this, 10,10, TimeUnit.SECONDS);



    }

    @Override
    public void run() {

        try {
            // get DER categories
//            log.info("Getting DER information...");
//            RoutingStatistic producerStats = new RoutingStatistic();
//            RoutingStatistic consumerStats = new RoutingStatistic();
//            RoutingStatistic substationStats = new RoutingStatistic();
            Number160 producerkey = Number160.createHash(null, "producer");
            Number160 consumerkey = Number160.createHash(null, "consumer");
            Number160 substationkey = Number160.createHash(null, "substation");
            Map<String, byte[]> producersPre = overlayNetwork.findMap(producerkey);
            Map<String, byte[]> consumersPre = overlayNetwork.findMap(consumerkey);
            Map<String, byte[]> substationsPre = overlayNetwork.findMap(substationkey);
//            log.debug("Producer statistics: " + consumerStats.toString());
//            log.debug("Consumer statistics: " + consumerStats.toString());
//            log.debug("Substation statistics: " + consumerStats.toString());

            if (producersPre != null) {
                for (Map.Entry<String, byte[]> entry : producersPre.entrySet()) {
                    DerNode der = (DerNode) xmlSerializer.xmlStringToObject(new String(entry.getValue()));
                    producers.put(entry.getKey(), der);
                }
            }
            if (consumersPre != null) {
                for (Map.Entry<String, byte[]> entry : consumersPre.entrySet()) {
                    DerNode der = (DerNode) xmlSerializer.xmlStringToObject(new String(entry.getValue()));
                    consumers.put(entry.getKey(), der);
                }
            }
            if (substationsPre != null) {
                for (Map.Entry<String, byte[]> entry : substationsPre.entrySet()) {
                    DerNode der = (DerNode) xmlSerializer.xmlStringToObject(new String(entry.getValue()));
                    substations.put(entry.getKey(), der);
                }
            }

            // print to console
//            log.info("Producers: " + producers.size() + ", Consumers: " + consumers.size() + ", Substations: " + substations.size());
//            for (Map.Entry<String, DerNode> entry : producers.entrySet()) {
//                log.info(entry.getValue().toString());
//            }
//            for (Map.Entry<String, DerNode> entry : consumers.entrySet()) {
//                log.info(entry.getValue().toString());
//            }
//            for (Map.Entry<String, DerNode> entry : substations.entrySet()) {
//                log.info(entry.getValue().toString());
//            }


            // update with own details
//            log.info("Updating ON with own details...");
            for (String cat : config.getCategories()) {
//                RoutingStatistic categoryStats = new RoutingStatistic();
                DerNode node = new DerNode();
                node.setAddress(self.getPeer().getAddress().getAddress());
                node.setPort(self.getPeer().getAddress().getPort().getPort());
                node.setCategory(cat);
                node.setName(config.getName());
                node.setDescription(config.getDescription());

                String xml = xmlSerializer.objectToXmlString(node);
                byte[] b = xml.getBytes();
                if (cat.equalsIgnoreCase("producer"))
                    overlayNetwork.storeValueInMap(producerkey, node.getName(), b);
                if (cat.equalsIgnoreCase("consumer"))
                    overlayNetwork.storeValueInMap(consumerkey, node.getName(), b);
                if (cat.equalsIgnoreCase("substation"))
                    overlayNetwork.storeValueInMap(substationkey, node.getName(), b);

//                log.debug("Store category statistics: " + categoryStats.toString());
            }

        } catch (IOException e) {
            log.error("Unable to deserialize der node list", e);
        }
    }
}
