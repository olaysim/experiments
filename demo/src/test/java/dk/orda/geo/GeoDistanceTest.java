package dk.orda.geo;

import dk.orda.overlaynetwork.overlaygeo.DistanceCalculator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dk.orda.overlaynetwork.overlaygeo.DistanceCalculator.distance;

public class GeoDistanceTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void GeoDistanceCalculatorTest() throws Exception {
        double mile = DistanceCalculator.distance(32.9697, -96.80322, 29.46786, -98.53506, "M");
        double km = DistanceCalculator.distance(32.9697, -96.80322, 29.46786, -98.53506, "K");
        double nautical = DistanceCalculator.distance(32.9697, -96.80322, 29.46786, -98.53506, "N");

        log.info(mile + " Miles");
        log.info(km  + " Kilometers");
        log.info(nautical + " Nautical Miles");

        int i = 0;
    }

    @Test
    public void PythagoreanDistanceTest() throws Exception {
        double a = DistanceCalculator.pythagoreanDistance(1, 2, 1, 2); // 0.0
        double b = DistanceCalculator.pythagoreanDistance(4, 4, 1, 1); // 4.242640687119285
        double c = DistanceCalculator.pythagoreanDistance(5, 3, 2, 3); // 3.0

        int i = 0;
    }
}
