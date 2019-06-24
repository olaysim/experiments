package dk.orda.demo;

import dk.orda.overlaynetwork.overlay.dht.DistributedHashTableFactory;
import dk.orda.overlaynetwork.overlay.dht.DistributedHashTableImpl;
import dk.orda.overlaynetwork.overlay.dht.Node;
import dk.orda.overlaynetwork.overlay.dht.Number160;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.SortedSet;

public class DHTTest {
    private static final Logger log = LoggerFactory.getLogger(DHTTest.class);

    private DistributedHashTableImpl createDistributedHashTable() {
        Number160 id = Number160.createHash(null, 1);
        DistributedHashTableImpl dht = (DistributedHashTableImpl) DistributedHashTableFactory.createDistributedHashTable(id, id.toString(), null);

        Node node1 = new Node(Number160.createHash(null, "2"));
        Node node2 = new Node(Number160.createHash(null, "3"));
        Node node3 = new Node(Number160.createHash(null, "4"));
        Node node4 = new Node(Number160.createHash(null, "5"));
        Node node5 = new Node(Number160.createHash(null, "6"));
        Node node6 = new Node(Number160.createHash(null, "7"));
        Node node7 = new Node(Number160.createHash(null, "8"));
        Node node8 = new Node(Number160.createHash(null, "9"));
        Node node9 = new Node(Number160.createHash(null, "10"));
        Node node10 = new Node(Number160.createHash(null, "11"));


        dht.updateNode(node1);
        dht.updateNode(node2);
        dht.updateNode(node3);
        dht.updateNode(node4);
        //dht.updateNode(node5);
        dht.updateNode(node6);
        dht.updateNode(node7);
        dht.updateNode(node8);
        dht.updateNode(node9);
        dht.updateNode(node10);

        return dht;
    }

    @Test
    public void showBucketAndNeighbours() throws Exception {
        DistributedHashTableImpl dht = createDistributedHashTable();
        Node rnode = new Node(Number160.createHash(null, "5"));
        dht.updateNode(rnode);

        // show buckets
        int j = 0;
        for (Map<Number160, Node> map : dht.getBuckets()) {
            System.out.print("Bucket " + j + ": ");
            for (Number160 n : map.keySet()) {
                System.out.print(n.toString() + " ");
            }
            System.out.print("\n");
            j++;
        }

        SortedSet<Node> nset = dht.getClosestNodes();
        System.out.print("\nNodes close to self: ");
        for (Node n : nset) {
            System.out.print(n.getId().toString() + " ");
        }
        System.out.print("\n");


        SortedSet<Node> nset2 = dht.getClosestNodes(rnode.getId());
        System.out.print("\nNodes close to (" + rnode.getId().toString() + "): ");
        for (Node n : nset2) {
            System.out.print(n.getId().toString() + " ");
        }
        System.out.print("\n");



           int i = 0;
    }


//    @Test
//    public void findNode() throws Exception {
//        RoutingProtocol protocol = new RoutingProtocol();
//        Node result = protocol.iterativeFindNode3(NetworkLayer.DUMMY.dht3.getNodeId(), NetworkLayer.DUMMY.dht1);
//
//        int i = 0;
//    }

//    @Test
//    public void storeAndFindValue() throws Exception {
//        RoutingProtocol protocol = new RoutingProtocol();
//        Number160 key = Number160.createHash(25);
//        byte[] val = "This is a test".getBytes();
//        protocol.iterativeStore(key, val, NetworkLayer.DUMMY.dht1);
//
//        byte[] res = protocol.iterativeFindValue(key, NetworkLayer.DUMMY.dht1);
//        System.out.println("value: " + new String(res));
//
//        int i = 0;
//    }

}
