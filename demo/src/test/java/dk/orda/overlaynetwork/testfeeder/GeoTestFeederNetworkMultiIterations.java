package dk.orda.overlaynetwork.testfeeder;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("Duplicates")
public class GeoTestFeederNetworkMultiIterations {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void EuropeanTestFeederNetwork() throws Exception {

        String OUTPUT_FOLDER = "test_iterative_14";

        // SELECT NETWORK:
        // 1 = european test feeder
        // 2 = 8500 node test feeder
        // 3 = networkX
        int network = 2;

        // run ITERATIVE network
        int iterativenetwork = 1;

        // small world bucket size
        int bucketsize = 1000;

        dk.orda.HpcTest.GeoTestFeederNetworkMultiIterations test = new dk.orda.HpcTest.GeoTestFeederNetworkMultiIterations();
        test.RunTestFeederNetwork(network, iterativenetwork, bucketsize, OUTPUT_FOLDER, 3, 10);
    }


    @Test
    public void BitMenesiaTest() {

//        Double d = new Double(5.0002);
//        long longBits = Double.doubleToLongBits(d);
//        double v = Double.longBitsToDouble(longBits);
    }


}
