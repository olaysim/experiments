package dk.orda.overlaynetwork.testfeeder;

import dk.orda.overlaynetwork.net.NetworkConfiguration;
import dk.orda.overlaynetwork.net.rpc.SuperNode;
import dk.orda.overlaynetwork.overlay.KademliaOverlay;
import dk.orda.overlaynetwork.overlay.OverlayFactory;
import dk.orda.overlaynetwork.overlay.dht.DistributedHashTableConfiguration;
import dk.orda.overlaynetwork.overlay.dht.Node;
import dk.orda.overlaynetwork.overlay.dht.Number160;
import dk.orda.overlaynetwork.overlaygeo.GeoKademliaOverlay;
import dk.orda.overlaynetwork.overlaygeo.LatLon;
import dk.orda.overlaynetwork.statistics.StatConfiguration;
import dk.orda.overlaynetwork.statistics.StatLogger;
import dk.orda.overlaynetwork.statistics.Value;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class CreateGeoTestFeederNetwork {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void createFeederNetwork() throws Exception {

        String OUTPUT_FOLDER = "test6";

        Path lines = Paths.get("Lines.csv");
//        Path lines = Paths.get("Lines_extra_tielines.csv");
//        Path lines = Paths.get("Lines_geo_best.csv");
        Path buscoords = Paths.get("Buscoords.csv");
//        Path buscoords = Paths.get("Buscoords_geo_best.csv");
//        Path buscoords = Paths.get("Buscoords_geo_worst.csv");

        FileReader fileReader = new FileReader(buscoords.toFile());
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        NetworkConfiguration netconf = new NetworkConfiguration();
        netconf.setLocalBroadcasting(false);
        netconf.setSeedServer(false);
        DistributedHashTableConfiguration dhtConf = new DistributedHashTableConfiguration();
        dhtConf.setK(2000);
        Map<String, GeoKademliaOverlay> map = new HashMap<>();

        String corrid = "44";
        String testid = "1";
        StatConfiguration statConfiguration = new StatConfiguration(testid, corrid);

        SuperNode sn = null;
        String line;
        // read coords and create ONs
        while ((line = bufferedReader.readLine()) != null) {
            List<String> strings = Arrays.asList(line.split(","));
            StatLogger sl = new StatLogger(statConfiguration);
            sl.initialize(OUTPUT_FOLDER);
            if (sn == null) {
                double x = Double.parseDouble(strings.get(1));
                double y = Double.parseDouble(strings.get(2));
                GeoKademliaOverlay on = (GeoKademliaOverlay)OverlayFactory.createGeoKademlia(strings.get(0), x, y, netconf, dhtConf, null, sl);
                on.setInitialization(true);
                sn = on.getSuperNode();
                on.getSelf().putDict("x", strings.get(1));
                on.getSelf().putDict("y", strings.get(2));
                map.put(strings.get(0), on);
            } else {
                double x = Double.parseDouble(strings.get(1));
                double y = Double.parseDouble(strings.get(2));
                GeoKademliaOverlay on = (GeoKademliaOverlay)OverlayFactory.createGeoKademlia(strings.get(0), x, y, netconf, dhtConf, sn, sl);
                on.setInitialization(true);
                on.getSelf().putDict("x", strings.get(1));
                on.getSelf().putDict("y", strings.get(2));
                map.put(strings.get(0), on);
            }
        }
        bufferedReader.close();
        fileReader.close();

        fileReader = new FileReader(lines.toFile());
        bufferedReader = new BufferedReader(fileReader);

        // use lines.csv to updateNodes to connect nodes
        while ((line = bufferedReader.readLine()) != null) {
            List<String> strings = Arrays.asList(line.split(","));
            map.get(strings.get(1)).updateNode(map.get(strings.get(2)).getSelf());
            map.get(strings.get(2)).updateNode(map.get(strings.get(1)).getSelf());
        }

        for (Map.Entry<String, GeoKademliaOverlay> entry : map.entrySet()) {
            entry.getValue().start();
        }

        for (Map.Entry<String, GeoKademliaOverlay> entry : map.entrySet()) {
            entry.getValue().logDht();
        }

        for (Map.Entry<String, GeoKademliaOverlay> entry : map.entrySet()) {
            entry.getValue().setInitialization(false);
        }

        GeoKademliaOverlay on = map.get("1");
        GeoKademliaOverlay target = map.get("899");
        LatLon latLon = target.getSelf().tryGetLatLon();
        Node node = on.findNode(target.getSelf().getId(), latLon.getLat(), latLon.getLon());

        statConfiguration.setTestId("2");
        for (Map.Entry<String, GeoKademliaOverlay> entry : map.entrySet()) {
            entry.getValue().logDht();
        }

        for (Map.Entry<String, GeoKademliaOverlay> entry : map.entrySet()) {
            entry.getValue().getStatLogger().write();
        }

        // create id/idstr lookup table
        StatLogger sl = new StatLogger(statConfiguration);
        sl.initialize(OUTPUT_FOLDER);
        for (Map.Entry<String, GeoKademliaOverlay> entry : map.entrySet()) {
            sl.addValue(new Value(entry.getValue().getSelf().getId().toString(), 0, statConfiguration, "lookup", Arrays.asList(entry.getKey())));
        }
        sl.write();

        int i = 0;
    }
}
