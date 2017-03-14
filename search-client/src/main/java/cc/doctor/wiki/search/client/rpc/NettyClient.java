package cc.doctor.wiki.search.client.rpc;

import cc.doctor.wiki.exceptions.rpc.RpcException;
import cc.doctor.wiki.search.client.rpc.result.RpcResult;
import cc.doctor.wiki.utils.PropertyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by doctor on 2017/3/14.
 */
public class NettyClient {
    private Bootstrap clientBootstrap;
    private String serverAddress = PropertyUtils.getProperty("netty.server.address", "127.0.0.1:1218");
    private ClientHandler clientHandler;

    public NettyClient() {
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
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException ignored) {
        }
    }

    public RpcResult sendMessage(Message message) {
        byte[] bytes = message.messageBytes();
        clientHandler.sendMessage(bytes);
        return null;
    }

    public String getHost() {
        return "localhost";
    }

    public class ClientHandler extends ChannelInboundHandlerAdapter {
        ChannelHandlerContext ctx;

        public void sendMessage(byte[] bytes) {
            if (ctx != null) {
                ByteBuf buffer = ctx.alloc().buffer();
                buffer.writeBytes(bytes);
                ChannelFuture channelFuture = ctx.writeAndFlush(bytes);
                if (channelFuture.isSuccess()) {
                    throw new RpcException("Send message error.");
                }
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        System.out.println(future.get());
                    }
                });
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            //发送消息
            this.ctx = ctx;
        }
    }
}
