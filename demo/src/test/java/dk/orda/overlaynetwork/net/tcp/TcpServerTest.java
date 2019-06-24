package dk.orda.overlaynetwork.net.tcp;

import com.google.protobuf.ByteString;
import dk.orda.overlaynetwork.net.Address;
import dk.orda.overlaynetwork.net.Port;
import dk.orda.overlaynetwork.net.rpc.MessageType;
import dk.orda.overlaynetwork.net.rpc.client.MapOrNodesFuture;
import dk.orda.overlaynetwork.net.rpc.client.ValueOrNodesFuture;
import dk.orda.overlaynetwork.net.rpc.protobuf.RpcMessages;
import dk.orda.overlaynetwork.overlay.OverlayFactory;
import dk.orda.overlaynetwork.overlay.OverlayNetwork;
import dk.orda.overlaynetwork.overlay.RequestOverlay;
import dk.orda.overlaynetwork.overlay.dht.Number160;
import io.netty.channel.Channel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;

public class TcpServerTest {

    @Test
    public void runTest() throws Exception {
//        SslContext sslClientContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
//        SelfSignedCertificate ssc = new SelfSignedCertificate("OverlayNetwork");
//        SslContext sslServerContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();

        OverlayNetwork overlayNetwork = OverlayFactory.createKademlia("101");
        ((RequestOverlay)overlayNetwork).getSelf().getPeer().setAddress(new Address("127.0.0.1", 7070));
        overlayNetwork.start();

        Address address = new Address("127.0.0.1", 7070);
//        TcpServer server = new TcpServer(address, null, 0, 0, null);
//        server.start();

        TcpClient client = new TcpClient(null);
        Channel ch = client.connect(new InetSocketAddress("127.0.0.1", 7070));

        String prod = Number160.createHash(null, "producer").toString();
        String id = Number160.createHash(null, "101").toString();
        String exid = Number160.createHash(null,"102").toString();
        byte[] test = {1,2,3,4,5,6};

        UUID uuid = UUID.randomUUID();
        RpcMessages.AddValue add = RpcMessages.AddValue.newBuilder()
            .setHeader(RpcMessages.Header.newBuilder()
                .setCorrelationId(uuid.toString())
                .setTargetId(id)
                .build())
            .setKey(prod)
            .setIndex("test")
            .setValue(ByteString.copyFrom(test))
            .build();
        ValueOrNodesFuture f22 = client.sendMessage(ch, add, MessageType.ADD_VALUE, uuid);
        RpcMessages.ValueOrNodes r22 = f22.get();

        RpcMessages.FindMap findMap = RpcMessages.FindMap.newBuilder()
            .setHeader(RpcMessages.Header.newBuilder()
                .setCorrelationId(uuid.toString())
                .setTargetId(id)
                .build())
            .setKey(prod)
            .build();

        MapOrNodesFuture fm1 = client.sendMessageForMap(ch, findMap, MessageType.FIND_MAP, uuid);
        RpcMessages.MapOrNodes rm1 = fm1.get();
        Map<String, ByteString> bs1 = rm1.getValue();

        // test find node
        RpcMessages.FindNode findNode = RpcMessages.FindNode.newBuilder()
            .setHeader(RpcMessages.Header.newBuilder()
                .setCorrelationId(uuid.toString())
                .setTargetId("0xbb49ccdaf2be3fd80dc0f6c6ffa9b2f5a534b4ca")
                .build())
            .build();

        ValueOrNodesFuture f = client.sendMessage(ch, findNode, MessageType.FIND_NODE, uuid);
//        for(int i = 0; i < 10; i++) {
//            Thread.sleep(10000);
//        }
        RpcMessages.ValueOrNodes r = f.get();

        // test find value (and decoder selector)
        RpcMessages.FindValue findValue = RpcMessages.FindValue.newBuilder()
            .setHeader(RpcMessages.Header.newBuilder()
                .setCorrelationId(uuid.toString())
                .setTargetId("test 2")
                .build())
            .build();

        ValueOrNodesFuture f2 = client.sendMessage(ch, findValue, MessageType.FIND_VALUE, uuid);
        RpcMessages.ValueOrNodes r2 = f2.get();


        // run a couple of more times, and mix it up
        ValueOrNodesFuture f3 = client.sendMessage(ch, findNode, MessageType.FIND_NODE, uuid);
        RpcMessages.ValueOrNodes r3 = f3.get();
//        RpcMessages.ValueOrNodes r2 = f2.get();
        ValueOrNodesFuture f4 = client.sendMessage(ch, findNode, MessageType.FIND_NODE, uuid);
//        RpcMessages.ValueOrNodes r3 = f3.get();
        RpcMessages.ValueOrNodes r4 = f4.get();
        ValueOrNodesFuture f5 = client.sendMessage(ch, findNode, MessageType.FIND_NODE, uuid);
        RpcMessages.ValueOrNodes r5 = f5.get();
//        f4 = client.sendMessage(ch, findNode, Serialization.PROTO_FIND_NODE);

        int i = 0;
    }
}
