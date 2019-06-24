package dk.orda.overlaynetwork.testfeeder;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("Duplicates")
public class KademliaTestFeederNetworkMultiIterations {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void EuropeanTestFeederNetwork() throws Exception {

        String OUTPUT_FOLDER = "test_iterative_12";

        // SELECT NETWORK:
        // 1 = european test feeder
        // 2 = 8500 node test feeder
        // 3 = networkX
        int network = 1;

        // run ITERATIVE network
        int iterativenetwork = 1;

        // kademlia bucket size
        int bucketsize = 20;

        dk.orda.HpcTest.KademliaTestFeederNetworkMultiIterations test = new dk.orda.HpcTest.KademliaTestFeederNetworkMultiIterations();
        test.RunTestFeederNetwork(network, iterativenetwork, bucketsize, OUTPUT_FOLDER, 3, 10);
    }
}
