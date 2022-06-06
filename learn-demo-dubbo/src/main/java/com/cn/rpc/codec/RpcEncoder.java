package com.cn.rpc.codec;

import com.cn.rpc.compress.Compressor;
import com.cn.rpc.compress.CompressorFactory;
import com.cn.rpc.constants.Constants;
import com.cn.rpc.protocol.RpcHeader;
import com.cn.rpc.protocol.RpcMessage;
import com.cn.rpc.serialization.Serialization;
import com.cn.rpc.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码
 * @Description: RpcEncoder
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class RpcEncoder extends MessageToByteEncoder<RpcMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf byteBuf) throws Exception {
        RpcHeader header = rpcMessage.getHeader();
        // 依次序列化消息头中的魔数、版本、附加信息以及消息ID
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getExtraInfo());
        byteBuf.writeLong(header.getMessageId());
        Object content = rpcMessage.getContent();
        if (Constants.isHeartBeat(header.getExtraInfo())) {
            // 心跳消息，没有消息体，这里写入0
            byteBuf.writeInt(0);
            return;
        }
        // 按照extraInfo部分指定的序列化方式和压缩方式进行处理
        Serialization serialization = SerializationFactory.get(header.getExtraInfo());
        Compressor compressor = CompressorFactory.get(header.getExtraInfo());
        byte[] payload = compressor.compress(serialization.serialize(content));
        // 写入消息体长度
        byteBuf.writeInt(payload.length);
        // 写入消息体
        byteBuf.writeBytes(payload);
    }
}
