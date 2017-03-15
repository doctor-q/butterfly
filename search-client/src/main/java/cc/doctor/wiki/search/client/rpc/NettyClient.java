package cc.doctor.wiki.search.client.rpc;

import cc.doctor.wiki.exceptions.rpc.RpcException;
import cc.doctor.wiki.search.client.rpc.result.RpcResult;
import cc.doctor.wiki.utils.NetworkUtils;
import cc.doctor.wiki.utils.PropertyUtils;
import cc.doctor.wiki.utils.SerializeUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by doctor on 2017/3/14.
 */
public class NettyClient {
    private Bootstrap clientBootstrap;
    private String serverAddress = PropertyUtils.getProperty("netty.server.address", "127.0.0.1:1218");
    private ClientHandler clientHandler;
    private Map<Long, ResponseFuture<RpcResult>> responseFutureMap = new ConcurrentHashMap<>();

    public NettyClient() {
        init();
    }

    public NettyClient(String serverAddress) {
        this.serverAddress = serverAddress;
        init();
    }

    public void init() {
        clientBootstrap = new Bootstrap().group(new NioEventLoopGroup()).channel(NioSocketChannel.class);
        String[] split = serverAddress.split(":");
        InetSocketAddress serverSocketAddress = new InetSocketAddress(split[0], Integer.parseInt(split[1]));
        clientHandler = new ClientHandler();
        clientBootstrap.remoteAddress(serverSocketAddress).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(clientHandler);
            }
        });
        try {
            ChannelFuture channelFuture = clientBootstrap.connect().sync();
//            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException ignored) {
        }
    }

    public RpcResult sendMessage(Message message) {
        if (message.getTimestamp() == 0) {
            message.timestamp(new Date().getTime());
        }
        return clientHandler.sendMessageSync(message, 1000);
    }

    public String getHost() {
        InetAddress oneUnLoopHost = NetworkUtils.getOneUnLoopHost();
        if (oneUnLoopHost != null) {
            return oneUnLoopHost.getHostAddress();
        }
        return null;
    }

    public void release() {
    }

    public class ClientHandler extends ChannelInboundHandlerAdapter {
        ChannelHandlerContext ctx;
        CountDownLatch ctxCountDown = new CountDownLatch(1);

        /**
         * 同步发送消息,等待消息收到后返回给客户端
         * @param message 发送的消息
         * @param timeout
         */
        public RpcResult sendMessageSync(Message message, long timeout) {
            try {
                ctxCountDown.await();
            } catch (InterruptedException ignored) {
            }
            final long responseId = message.getTimestamp();
            final ResponseFuture<RpcResult> responseFuture = new ResponseFuture<>();
            responseFutureMap.put(responseId, responseFuture);
            byte[] bytes = message.messageBytes();
            ByteBuf buffer = ctx.alloc().buffer();
            buffer.writeBytes(bytes);
            ChannelFuture channelFuture = ctx.writeAndFlush(buffer);
            if (channelFuture.isSuccess()) {
                throw new RpcException("Send message error.");
            }
            responseFuture.await(timeout);
            return responseFuture.getData();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            //发送消息
            this.ctx = ctx;
            ctxCountDown.countDown();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            RpcResult rpcResult = SerializeUtils.deserialize(bytes);
            ResponseFuture<RpcResult> responseFuture = responseFutureMap.get(rpcResult.getTimestamp());
            responseFuture.setData(rpcResult);
        }
    }
}
