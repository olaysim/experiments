package dk.orda.geo;

import dk.orda.overlaynetwork.overlay.dht.Node;
import dk.orda.overlaynetwork.overlay.dht.Number160;
import dk.orda.overlaynetwork.overlaygeo.SmallWorldBucket;
import org.apache.commons.math3.util.Precision;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.text.Format;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class SmallWorldBucketTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    Format df = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm").withZone(ZoneId.systemDefault()).toFormat();

    @Test
    public void TestEqualityWhenMarkingPassive() {
        SmallWorldBucket bucket = new SmallWorldBucket(Number160.createHash(null, "1"), 0D, 0D);

        Node nodea = new Node(Number160.createHash(null, "100"), "100");
        Node nodeb = new Node(Number160.createHash(null, "100"), "100");
        Node nodec = new Node(Number160.createHash(null, "200"), "200");

        bucket.markPassive(nodea);
        bucket.markPassive(nodeb);
        bucket.markPassive(nodec);

        int a = 0;
    }

    @Test
    public void AddNode() throws InterruptedException {
        SmallWorldBucket bucket = new SmallWorldBucket(Number160.createHash(null, "1"), 0D, 0D);

        Node nodea = new Node(Number160.createHash(null, "100"), "100");
        Node nodeb = new Node(Number160.createHash(null, "100"), "100");
        Node nodec = new Node(Number160.createHash(null, "200"), "200");

        nodea.putDict("x", "30.0");
        nodea.putDict("y", "30.0");
        nodeb.putDict("x", "30.0");
        nodeb.putDict("y", "30.0");
        nodec.putDict("x", "10.0");
        nodec.putDict("y", "10.0");


        // add close node
        bucket.addCloseNode(nodea);
        bucket.addCloseNode(nodeb);
        bucket.addCloseNode(nodec);

        // add random node
        Thread.sleep(500); // by waiting 0.5 sec p should be pretty close to the target rate all through this test
        bucket.addRandomNode(nodea); // if decreased to something like 10ms, you should start to see "no add"
        Thread.sleep(500);
        bucket.addRandomNode(nodeb);
        Thread.sleep(1000);
        bucket.addRandomNode(nodec); // test this one

        int a = 0;
    }

    @Test
    public void TestCloseFormula() throws IOException {

        double avg = 15000.0;

        // sysout for excel
        Path path = Paths.get("data", "TEST", "closeformulatest");
        Files.createDirectories(path);
        OpenOption[] openOptions = new OpenOption[] {StandardOpenOption.CREATE, StandardOpenOption.APPEND};

        Path file = Paths.get("data", "TEST", "closeformulatest", "phi_20000_itr_" + df.format(Instant.now()) + ".csv");
        try (BufferedWriter writer = Files.newBufferedWriter(file, Charset.forName("UTF-8"), openOptions)) {
            System.out.println("Writing PHI 0.5 to 3.0");
            StringBuffer strb = new StringBuffer();
            for (double i = 0.0; i <= 20000.0; i++) {
                double x1 = calcP(i, avg, 0.5);
                double x2 = calcP(i, avg, 1.0);
                double x3 = calcP(i, avg, 2.0);
                double x4 = calcP(i, avg, 3.0);
                double y1 = Precision.round(x1, 10);
                double y2 = Precision.round(x2, 10);
                double y3 = Precision.round(x3, 10);
                double y4 = Precision.round(x4, 10);
                String replaceDotWithComma1 = "" + y1;
                String replaceDotWithComma2 = "" + y2;
                String replaceDotWithComma3 = "" + y3;
                String replaceDotWithComma4 = "" + y4;
                strb.append(i + ";" +
                        replaceDotWithComma1.replace(".", ",") + ";" +
                        replaceDotWithComma2.replace(".", ",") + ";" +
                        replaceDotWithComma3.replace(".", ",") + ";" +
                        replaceDotWithComma4.replace(".", ",") + ";" + "\n");
            }
                writer.write(strb.toString());
        } catch (IOException e) {
            log.error("Unable to write experiment data to file", e);
        }
    }

    private double calcP(double d, double avg, double phi) {
        return Math.exp(-Math.pow(d / avg, phi));
    }




    double r = 0.2;
    double targetR = 0.001;
    double rate = 0.001;
    @Test
    public void testRateCode2() {
        for (double i = 2000.0; i > 100.0; i -= 100.0) {
            cr(i);
        }
        for (double i = 100.0; i < 2000.0; i += 100.0) {
            cr(i);
        }
        int j = 0;
    }

    private void cr(double time) {
        double part1 = r * (1.0 / time);
        double part2 = (1.0 - r) * rate;
        rate = part1 + part2;
        double p = Math.pow(rate/targetR, -1);
//        System.out.println(System.currentTimeMillis() + ": p1 " + Precision.round(part1, 5) + "\t\tp2 " + Precision.round(part2, 5) + "\t\trate " + Precision.round(rate, 5) + "\t\t p " + Precision.round(p, 3));
//        System.out.println(Precision.round(time, 2) + " " + p);

        String strp = ("" + Precision.round(p, 10)).replace(".", ",");
        System.out.println(strp);
//        String itrp = ("" + Precision.round(time, 2)).replace(".", ",");
//        System.out.println(itrp);
    }


}
