package dk.orda.overlaynetwork.net.tcp;

import com.google.protobuf.ByteString;
import dk.orda.demo.DerMap;
import dk.orda.demo.DerNode;
import dk.orda.demo.XmlSerializer;
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
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RpcTest {

    @Test
    public void runTest() throws Exception {

        OverlayNetwork overlayNetwork = OverlayFactory.createKademlia("101");
        overlayNetwork.start();

        TcpClient client = new TcpClient();
//        TcpServer server = new TcpServer(new Address(7070), (RequestOverlay)overlayNetwork);
//        server.start();



        while (true) {
            try {
                // connect
//                Channel ch = client.connect(new InetSocketAddress("10.42.241.11", 52611));
                Channel ch = client.connect(new InetSocketAddress("10.42.241.13", 53294)); // <-- manual port found on node manually, manual, man
//                Channel ch = client.connect(new InetSocketAddress("127.0.0.1", 7070));

                String prod = Number160.createHash(null, "producer").toString();
                String id = Number160.createHash(null, "101").toString();
                String exid = Number160.createHash(null, "102").toString();
                byte[] test = {1,2,3,4,5,6};

                UUID uuid = UUID.randomUUID();
//                RpcMessages.AddValue add = RpcMessages.AddValue.newBuilder()
//                    .setId(id)
//                    .setKey(prod)
//                    .setIndex("test")
//                    .setValue(ByteString.copyFrom(test))
//                    .build();
//                ValueOrNodesFuture f22 = client.sendMessage(ch, add, MessageType.ADD_VALUE, uuid);
//                RpcMessages.ValueOrNodes r22 = f22.get();


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

                // find node
                RpcMessages.FindNode findNode = RpcMessages.FindNode.newBuilder()
                    .setHeader(RpcMessages.Header.newBuilder()
                        .setCorrelationId(uuid.toString())
                        .setTargetId("0xbb49ccdaf2be3fd80dc0f6c6ffa9b2f5a534b4ca")
                        .build())
                    .build();
                ValueOrNodesFuture f1 = client.sendMessage(ch, findNode, MessageType.FIND_NODE, uuid);
                RpcMessages.ValueOrNodes r1 = f1.get();

                // store value
                byte[] value = createDerMap();

                RpcMessages.StoreValue storeValue = RpcMessages.StoreValue.newBuilder()
                    .setHeader(RpcMessages.Header.newBuilder()
                        .setCorrelationId(uuid.toString())
                        .setTargetId("0xbb49ccdaf2be3fd80dc0f6c6ffa9b2f5a534b4ca")
                        .build())
                    .setKey("0xbb49ccdaf2be3fd80dc0f6c6ffa9b2f5a534b4ca")
                    .setValue(ByteString.copyFrom(value))
                    .build();
                ValueOrNodesFuture f2 = client.sendMessage(ch, storeValue, MessageType.STORE_VALUE, uuid);

                RpcMessages.ValueOrNodes r2 = f2.get(4000, TimeUnit.MILLISECONDS);
                ByteString bs = r2.getValue();
                if (bs != null && !bs.isEmpty()) {
                    String result = bs.toStringUtf8();
                }

                // close
                client.close(ch);
            }
            catch (Exception e) {
                Exception a = e;
                e.printStackTrace();
            }
        }
    }


    private byte[] createDerMap() throws Exception {
        DerMap derMap = new DerMap();
        XmlSerializer xmlSerializer = createXmlSerializer(createJaxb2Marshaller());

        DerNode node = new DerNode();
        node.setAddress("10.42.241.1");
        node.setPort(7070);
        node.setCategory("test");
        node.setName("syslab-01");
        node.setDescription("syslab-01");
        derMap.put(node.getName(), node);

        node = new DerNode();
        node.setAddress("10.42.241.2");
        node.setPort(7070);
        node.setCategory("test");
        node.setName("syslab-02");
        node.setDescription("syslab-02");
        derMap.put(node.getName(), node);

        node = new DerNode();
        node.setAddress("10.42.241.3");
        node.setPort(7070);
        node.setCategory("test");
        node.setName("syslab-03");
        node.setDescription("syslab-03");
        derMap.put(node.getName(), node);

        node = new DerNode();
        node.setAddress("10.42.241.4");
        node.setPort(7070);
        node.setCategory("test");
        node.setName("syslab-04");
        node.setDescription("syslab-04");
        derMap.put(node.getName(), node);

        node = new DerNode();
        node.setAddress("10.42.241.5");
        node.setPort(7070);
        node.setCategory("test");
        node.setName("syslab-05");
        node.setDescription("syslab-05");
        derMap.put(node.getName(), node);

        node = new DerNode();
        node.setAddress("10.42.241.6");
        node.setPort(7070);
        node.setCategory("test");
        node.setName("syslab-06");
        node.setDescription("syslab-06");
        derMap.put(node.getName(), node);

        node = new DerNode();
        node.setAddress("10.42.241.7");
        node.setPort(7070);
        node.setCategory("test");
        node.setName("syslab-07");
        node.setDescription("syslab-07");
        derMap.put(node.getName(), node);

        node = new DerNode();
        node.setAddress("10.42.241.8");
        node.setPort(7070);
        node.setCategory("test");
        node.setName("syslab-08");
        node.setDescription("syslab-08");
        derMap.put(node.getName(), node);

        node = new DerNode();
        node.setAddress("10.42.241.9");
        node.setPort(7070);
        node.setCategory("test");
        node.setName("syslab-09");
        node.setDescription("syslab-09");
        derMap.put(node.getName(), node);

        node = new DerNode();
        node.setAddress("10.42.241.10");
        node.setPort(7070);
        node.setCategory("test");
        node.setName("syslab-10");
        node.setDescription("syslab-10");
        derMap.put(node.getName(), node);

        node = new DerNode();
        node.setAddress("10.42.241.11");
        node.setPort(7070);
        node.setCategory("test");
        node.setName("syslab-11");
        node.setDescription("syslab-11");
        derMap.put(node.getName(), node);

        String xml = xmlSerializer.objectToXmlString(derMap);
        return xml.getBytes();
    }

    private XmlSerializer createXmlSerializer(Jaxb2Marshaller jaxb2Marshaller){
        XmlSerializer xmlSerializer = new XmlSerializer();
        xmlSerializer.setMarshaller(jaxb2Marshaller);
        xmlSerializer.setUnmarshaller(jaxb2Marshaller);
        return xmlSerializer;
    }
    private Jaxb2Marshaller createJaxb2Marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setPackagesToScan("dk.orda");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("jaxb.formatted.output", true);
        jaxb2Marshaller.setMarshallerProperties(map);
        return jaxb2Marshaller;
    }
}
