package dk.orda.overlaynetwork.overlay;

import dk.orda.overlaynetwork.net.NetworkConfiguration;
import dk.orda.overlaynetwork.overlay.dht.DistributedHashTableConfiguration;
import dk.orda.overlaynetwork.overlay.dht.Node;
import dk.orda.overlaynetwork.overlay.dht.Number160;
import dk.orda.overlaynetwork.statistics.RoutingStatistic;
import dk.orda.overlaynetwork.statistics.StatConfiguration;
import dk.orda.overlaynetwork.statistics.StatLogger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OverlayNetworkTest {

    private OverlayNetwork createOverlayNetwork(String id) {

        OverlayNetwork on = OverlayFactory.createKademlia(id);
        KademliaOverlay kad = (KademliaOverlay)on;
        return on;
    }

    @Test
    public void testVirtualNetworkWithFindNode() throws Exception {
        NetworkConfiguration netconf = new NetworkConfiguration();
        netconf.setLocalBroadcasting(false);
        netconf.setSeedServer(false);

        String corrid = "42";
        String testid = "1";

        StatLogger sl1 = new StatLogger(new StatConfiguration(testid, corrid));
        sl1.initialize("test1");
        KademliaOverlay on1 = (KademliaOverlay)OverlayFactory.createKademlia("1", netconf, null, null, sl1);
        StatLogger sl2 = new StatLogger(new StatConfiguration(testid, corrid));
        sl2.initialize("test1");
        KademliaOverlay on2 = (KademliaOverlay)OverlayFactory.createKademlia("2", netconf, null, on1.getSuperNode(), sl2);
        StatLogger sl3 = new StatLogger(new StatConfiguration(testid, corrid));
        sl3.initialize("test1");
        KademliaOverlay on3 = (KademliaOverlay)OverlayFactory.createKademlia("3", netconf, null, on1.getSuperNode(), sl3);
        StatLogger sl4 = new StatLogger(new StatConfiguration(testid, corrid));
        sl4.initialize("test1");
        KademliaOverlay on4 = (KademliaOverlay)OverlayFactory.createKademlia("4", netconf, null, on1.getSuperNode(), sl4);
        StatLogger sl5 = new StatLogger(new StatConfiguration(testid, corrid));
        sl5.initialize("test1");
        KademliaOverlay on5 = (KademliaOverlay)OverlayFactory.createKademlia("5", netconf, null, on1.getSuperNode(), sl5);
        StatLogger sl6 = new StatLogger(new StatConfiguration(testid, corrid));
        sl6.initialize("test1");
        KademliaOverlay on6 = (KademliaOverlay)OverlayFactory.createKademlia("6", netconf, null, on1.getSuperNode(), sl6);

        on1.logDht();
        on2.logDht();
        on3.logDht();
        on4.logDht();
        on5.logDht();
        on6.logDht();

        on1.updateNode(on2.getSelf());
        on2.updateNode(on3.getSelf());
        on3.updateNode(on4.getSelf());
        on4.updateNode(on5.getSelf());
        on5.updateNode(on6.getSelf());
        on6.updateNode(on1.getSelf());

        on1.start();
        on2.start();
        on3.start();
        on4.start();
        on5.start();
        on6.start();

        Number160 key = Number160.createHash(null, "6");
        Node result = on1.findNode(key);

        on1.logDht();
        on2.logDht();
        on3.logDht();
        on4.logDht();
        on5.logDht();
        on6.logDht();

        sl1.write();
        sl2.write();
        sl3.write();
        sl4.write();
        sl5.write();
        sl6.write();

        int i = 0;
    }

    @Test
    public void testMassiveAmountsOfVirtualNetworkWithFindNode() throws Exception {
        int massivenumber = 1000; // 8.000 er max for 10gb laptop

        NetworkConfiguration netconf = new NetworkConfiguration();
        netconf.setLocalBroadcasting(false);
        netconf.setSeedServer(false);
        DistributedHashTableConfiguration dhtConf = new DistributedHashTableConfiguration();
        dhtConf.setK(massivenumber+5);

        List<KademliaOverlay> list = new ArrayList<>(massivenumber);
        KademliaOverlay on1 = (KademliaOverlay)OverlayFactory.createKademlia("1", netconf, dhtConf, null, null);
        list.add(on1);
        for (int i = 2; i <= massivenumber; i++) {
            KademliaOverlay on = (KademliaOverlay)OverlayFactory.createKademlia(String.valueOf(i), netconf, null, on1.getSuperNode(), null);
            list.add(on);
        }

//        System.out.println("NOW SLEEPING - ATTACH PROFILER");
//        Thread.sleep(20000);
//        System.out.println("RESUMING....");

        for (int i = 0; i < list.size()-1; i++) {
            list.get(i).updateNode(list.get(i+1).getSelf());
        }

        for (KademliaOverlay overlay : list) {
            overlay.start();
        }


        Number160 key = Number160.createHash(null, String.valueOf(massivenumber));
        Node result = on1.findNode(key);


//        while (true) {
//            Thread.sleep(10000);
//        }

        int i = 0;
    }


    @Test
    public void testStoreAndFindValue() throws Exception {

        KademliaOverlay on1 = (KademliaOverlay)createOverlayNetwork("1");
        KademliaOverlay on2 = (KademliaOverlay)createOverlayNetwork("2");
        KademliaOverlay on3 = (KademliaOverlay)createOverlayNetwork("3");
        KademliaOverlay on4 = (KademliaOverlay)createOverlayNetwork("4");
        on1.start();
        on2.start();
        on3.start();
        on4.start();

        System.out.println("on1 " + on1.getSelf().getId().toString());
        System.out.println("on2 " + on2.getSelf().getId().toString());
        System.out.println("on3 " + on3.getSelf().getId().toString());
        System.out.println("on4 " + on4.getSelf().getId().toString());

//        Thread.sleep(500);

        on1.updateNode(on2.getSelf());
        on2.updateNode(on3.getSelf());
        on3.updateNode(on4.getSelf());
        on4.updateNode(on1.getSelf());

        Number160 key = Number160.createHash(null, 25);
        byte[] val = "This is a test".getBytes();

        Node n = on1.findNode(on3.getSelf().getId());

        on1.storeValue(key, val, 0);
        Thread.sleep(2000);

        byte[] res = on1.findValue(key);
        System.out.println("value: " + new String(res));

        int i = 0;
    }

}
