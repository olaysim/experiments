package dk.orda.seedserver;

import dk.orda.overlaynetwork.net.rpc.MessageType;
import dk.orda.overlaynetwork.net.rpc.Protocol;
import dk.orda.overlaynetwork.net.rpc.client.SeedsOrStatusFuture;
import dk.orda.overlaynetwork.net.rpc.protobuf.RpcMessages;
import dk.orda.overlaynetwork.net.tcp.TcpClient;
import io.netty.channel.Channel;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;

public class FakeInitializeSeedServerTest {
    @Test
    public void runInitialization() throws Exception {

        TcpClient client = new TcpClient();
        Channel ch = client.connect(new InetSocketAddress("seed.syslab.dk", 5007), Protocol.SEEDS);

        UUID uuid = UUID.randomUUID();

        RpcMessages.GetSeeds getSeedsSyslab01 = RpcMessages.GetSeeds.newBuilder()
            .setCorrelationId(uuid.toString())
            .setAddress("10.42.245.4")
            .setId("syslab-01")
            .setPort(1234)
            .build();

        RpcMessages.GetSeeds getSeedsSyslab02 = RpcMessages.GetSeeds.newBuilder()
            .setCorrelationId(uuid.toString())
            .setAddress("10.42.245.4")
            .setId("syslab-02")
            .setPort(4321)
            .build();

//        SeedsOrStatusFuture sosf1 = client.sendSeedRequest(ch, getSeedsSyslab01, MessageType.GET_SEEDS, uuid);
//        RpcMessages.SeedsOrStatus result1 = sosf1.get();

        SeedsOrStatusFuture sosf2 = client.sendSeedRequest(ch, getSeedsSyslab02, MessageType.GET_SEEDS, uuid);
        RpcMessages.SeedsOrStatus result2 = sosf2.get();

//        SeedsOrStatusFuture sosf3 = client.sendSeedRequest(ch, getSeedsSyslab01, MessageType.GET_SEEDS, uuid);
//        RpcMessages.SeedsOrStatus result3 = sosf3.get();
//        List<RpcMessages.Peer> peersList = result3.getPeersList();

        client.close(ch);
        client.shutdown();
        int i = 0;
    }
}
