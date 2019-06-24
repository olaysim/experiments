package dk.orda;

import dk.orda.HpcTest.GeoTestFeederNetworkMultiIterations;
import dk.orda.HpcTest.KademliaTestFeederNetworkMultiIterations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {

        if (args.length != 4) {
            //ApplicationContext ctx = SpringApplication.run(App.class, args);
            System.out.println("WRONG COMMANDLINE ARGUMENTS YA DOOFUS!");
            System.exit(-1);
        } else {
            // run hpc test
            int test = Integer.parseInt(args[0]);
            int network = Integer.parseInt(args[1]);
            int iterative = Integer.parseInt(args[2]);
            int bucketsize = Integer.parseInt(args[3]);

            if (test > 2 || test < 1 || network > 4 || network < 1 || iterative > 1 || iterative < 0) {
                System.out.println("WRONG INPUT ARGUMENTS FOR HPC TEST");
                System.exit(-1);
            } else {
                System.out.println("RUNNING HPC TEST: test " + test + ", network " + network + ", iterative " + iterative + ", bucketsize " + bucketsize);
            }

            if (test == 1) {
                GeoTestFeederNetworkMultiIterations testfeeder = new GeoTestFeederNetworkMultiIterations();
                testfeeder.RunTestFeederNetwork(network, iterative, bucketsize);
            }
            if (test == 2) {
                KademliaTestFeederNetworkMultiIterations testfeeder = new KademliaTestFeederNetworkMultiIterations();
                testfeeder.RunTestFeederNetwork(network, iterative, bucketsize);
            }
            System.out.println("\n\nTEST DONE!!!\n\n");
            System.exit(0);
        }

        System.exit(0);
    }
}
