package cc.doctor.wiki.search.server.rpc;

import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.utils.NetworkUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

import static cc.doctor.wiki.search.server.common.config.Settings.settings;

/**
 * Created by doctor on 2017/3/14.
 */
public class NettyServer implements Server {
    private ServerBootstrap serverBootstrap;

    public NettyServer() {
        serverBootstrap = new ServerBootstrap();
    }

    @Override
    public void start() {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(settings.getInt(GlobalConfig.NETTY_SERVER_PORT));
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

    @Override
    public String getHost() {
        return NetworkUtils.getOneUnLoopHost().getHostAddress();
    }

    @Override
    public int getPort() {
        return settings.getInt(GlobalConfig.NETTY_SERVER_PORT);
    }
}
