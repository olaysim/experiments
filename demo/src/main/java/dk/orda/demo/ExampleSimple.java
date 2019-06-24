//package dk.orda.demo;
//
//import dk.orda.dht.dht.FutureGet;
//import dk.orda.dht.dht.PeerBuilderDHT;
//import dk.orda.dht.dht.PeerDHT;
//import dk.orda.dht.futures.FutureBootstrap;
//import dk.orda.dht.p2p.Peer;
//import dk.orda.dht.p2p.PeerBuilder;
//import dk.orda.dht.peers.Number160;
//import dk.orda.dht.storage.Data;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.net.InetAddress;
//
//public class ExampleSimple {
//    private final Logger log = LoggerFactory.getLogger(this.getClass());
//
//    final private PeerDHT peerDHT;
//
//    public ExampleSimple(int peerId) throws Exception {
//        Peer peer = new PeerBuilder(Number160.createHash(peerId)).ports(4000 + peerId).start();
//        peerDHT = new PeerBuilderDHT(peer).start();
//
//        FutureBootstrap fb = this.peerDHT.peer().bootstrap().inetAddress(InetAddress.getByName("127.0.0.1")).ports(4001).start();
//        fb.awaitUninterruptibly();
//        if(fb.isSuccess()) {
//            peerDHT.peer().discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
//        }
//    }
//
//    public String get(String name) throws ClassNotFoundException, IOException {
//        FutureGet futureGet = peerDHT.get(Number160.createHash(name)).start();
//        futureGet.awaitUninterruptibly();
//        if (futureGet.isSuccess()) {
//            return futureGet.dataMap().values().iterator().next().object().toString();
//        }
//        return "not found";
//    }
//
//    public void store(String name, String ip) throws IOException {
//        peerDHT.put(Number160.createHash(name)).data(new Data(ip)).start().awaitUninterruptibly();
//    }
//}
