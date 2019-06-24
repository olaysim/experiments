package dk.orda.geo;

import dk.orda.overlaynetwork.overlay.dht.Node;
import dk.orda.overlaynetwork.overlay.dht.Number160;
import dk.orda.overlaynetwork.overlaygeo.GeoDistributedHashTableImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class ComparatorTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void ComparatorEqualsTest() {
//        "x" -> "391016.001"
//        "y" -> "392829.07"

        Node a = new Node(Number160.createHash(null, "A"), "A");
        Node b = new Node(Number160.createHash(null, "B"), "A");

        a.putDict("x", "391016.001");
        a.putDict("y", "392829.07");
        b.putDict("x", "391016.001");
        b.putDict("y", "392829.07");

        double lat = 390960.503;
        double lon = 392792.29;

        Comparator<Node> comparator = GeoDistributedHashTableImpl.getGeoNodeComparator(lat, lon);

        for (int i = 0; i < 20000000; i++) {
            int res = comparator.compare(a, b);
            if (res != 0) {
                System.out.println("HEY");
            }
        }


        int bp = 0;
    }
}
