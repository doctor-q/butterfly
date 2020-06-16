package cc.doctor.search.server.rpc;

import cc.doctor.search.common.utils.NetworkUtils;
import cc.doctor.search.server.common.config.GlobalConfig;
import cc.doctor.search.server.common.config.Settings;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

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
        InetSocketAddress inetSocketAddress = new InetSocketAddress(Settings.settings.getInt(GlobalConfig.NETTY_SERVER_PORT));
        ChannelFuture channelFuture = serverBootstrap.group(new NioEventLoopGroup()).channel(NioServerSocketChannel.class)
                .childHandler(new ServerHandler()).bind(inetSocketAddress);
        try {
            channelFuture.sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
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
        return Settings.settings.getInt(GlobalConfig.NETTY_SERVER_PORT);
    }
}
