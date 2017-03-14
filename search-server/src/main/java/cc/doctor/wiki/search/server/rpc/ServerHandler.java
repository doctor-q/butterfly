package cc.doctor.wiki.search.server.rpc;

import cc.doctor.wiki.search.client.rpc.Message;
import cc.doctor.wiki.search.client.rpc.result.RpcResult;
import cc.doctor.wiki.utils.SerializeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by doctor on 2017/3/14.
 * register->active->read->readComplete->inactive->unregister
 */
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {
    //客户端通道注册
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    //客户端通道离开
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    //通道可用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    //通道不可用
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    //通道有数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        int readableBytes = byteBuf.readableBytes();
        byte[] bytes = new byte[readableBytes];
        byteBuf.readBytes(bytes);
        Message message = SerializeUtils.deserialize(bytes);
        OperationHandler operationHandler = OperationHandlerFactory.getOperationHandler(message.getOperation());
        RpcResult rpcResult = operationHandler.handlerOperation(message.getData());
        byte[] serialize = SerializeUtils.serialize(rpcResult);
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeBytes(serialize);
        ctx.writeAndFlush(buffer);
    }

    //读取完成
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }
}
