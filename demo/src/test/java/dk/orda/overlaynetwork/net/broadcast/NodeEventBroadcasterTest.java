package dk.orda.overlaynetwork.net.broadcast;

import dk.orda.overlaynetwork.net.NetworkConfiguration;
import dk.orda.overlaynetwork.overlay.dht.Node;
import dk.orda.overlaynetwork.overlay.dht.Number160;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
public class NodeEventBroadcasterTest {

    private NodeEvent createNodeEvent() {
        NodeEvent event = new NodeEvent("127.0.0.1", 8080, Number160.createHash(null, 1));
        return event;
    }

    @Test
    public void runTest() throws Exception {
//        NodeEventBroadcaster broadcaster = new NodeEventBroadcaster(new InetSocketAddress("255.255.255.255", NetworkConfiguration.BROADCAST_PORT), 1, createNodeEvent());
//        broadcaster.start();
//
//        NodeEventMonitor monitor = new NodeEventMonitor(new InetSocketAddress(NetworkConfiguration.BROADCAST_PORT));
//        monitor.bind();
//
//        TimeUnit.MINUTES.sleep(10);
//        broadcaster.stop();
//        monitor.stop();
//
//        int i = 0;
    }

    @Test
    public void nodeEventListenerTest() throws Exception {

//        NodeEventBroadcaster broadcaster = new NodeEventBroadcaster(new InetSocketAddress("255.255.255.255", NetworkConfiguration.BROADCAST_PORT), 1, createNodeEvent());
//        broadcaster.start();
//
//        NodeEventMonitor monitor = new NodeEventMonitor(new InetSocketAddress(NetworkConfiguration.BROADCAST_PORT));
//        monitor.bind();
//        NodeEventListener listener = new NodeEventListener() {
//            @Override
//            public void NodeEventReceived(NodeEvent msg) {
//                String res = "LISTENER: Got broadcast from: " + msg.getIpAddress() + ":" + msg.getPort() + ", nodeid: " + msg.getNodeId().toString();
//                System.out.println(res);
//            }
//        };
//        monitor.addNodeEventListener(listener);
//
//        TimeUnit.MINUTES.sleep(10);
//        broadcaster.stop();
//        monitor.stop();
//
//        int i = 0;
    }

}
