import apache.rocketmq.v2.MessagingServiceGrpc;
import apache.rocketmq.v2.QueryRouteRequest;
import apache.rocketmq.v2.QueryRouteResponse;
import io.grpc.ManagedChannel;
import io.grpc.NameResolver;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GrpcTest {
    public void runTest() throws SSLException {
        QueryRouteRequest request = QueryRouteRequest.newBuilder().build();

        final SslContextBuilder builder = GrpcSslContexts.forClient();
        builder.trustManager(InsecureTrustManagerFactory.INSTANCE);
        SslContext sslContext = builder.build();

        final NettyChannelBuilder channelBuilder =
                NettyChannelBuilder.forAddress("47.97.252.220", 8080)
                        .usePlaintext()
                        .sslContext(sslContext);
        final List<InetSocketAddress> socketAddresses = Collections.singletonList(new InetSocketAddress("47.97.252.220", 8080));
        if (null != socketAddresses) {
            final IpNameResolverFactory ipNameResolverFactory = new IpNameResolverFactory(socketAddresses);
            channelBuilder.nameResolverFactory(ipNameResolverFactory);
        }
        ManagedChannel channel = channelBuilder.build();

//        ManagedChannel channel = ManagedChannelBuilder.forTarget("47.97.252.220:8080")
//                .usePlaintext()
//                .build();

        MessagingServiceGrpc.MessagingServiceBlockingStub blockingStub = MessagingServiceGrpc.newBlockingStub(channel);
        QueryRouteResponse response = blockingStub.queryRoute(request);

        // assert result
        String ret = response.toString().replace("\n","").replaceAll(" ", "");
        System.out.println(ret);
        if(!ret.equals("status{code:CLIENT_ID_REQUIREDmessage:\"clientidcannotbeempty\"}")){
            System.exit(0);
        }
    }
    public static void main(String[] args) throws SSLException, ExecutionException, InterruptedException {
        GrpcTest grpcTest = new GrpcTest();
        for(int i = 0; i < 1000; i++){
            grpcTest.runTest();
            Thread.sleep(1000);
        }
        System.exit(0);
    }
}
