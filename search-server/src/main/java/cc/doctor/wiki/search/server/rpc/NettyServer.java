package cc.doctor.wiki.search.server.rpc;

import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.utils.PropertyUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by doctor on 2017/3/14.
 */
public class NettyServer implements Server {
    private int nettyServerPort = PropertyUtils.getProperty(GlobalConfig.NETTY_SERVER_PORT, GlobalConfig.NETTY_SERVER_PORT_DEFAULT);
    private ServerBootstrap serverBootstrap;

    private NettyServer() {
        serverBootstrap = new ServerBootstrap();
    }

    @Override
    public void start() {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(nettyServerPort);
        ChannelFuture channelFuture = serverBootstrap.group(new NioEventLoopGroup()).channel(NioServerSocketChannel.class)
                .childHandler(new ServerHandler()).bind(inetSocketAddress);
        try {
            channelFuture.sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void stop() {

    }
}
