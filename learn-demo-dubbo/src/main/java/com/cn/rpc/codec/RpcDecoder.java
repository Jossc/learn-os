package com.cn.rpc.codec;

import com.cn.rpc.compress.Compressor;
import com.cn.rpc.compress.CompressorFactory;
import com.cn.rpc.constants.Constants;
import com.cn.rpc.protocol.Request;
import com.cn.rpc.protocol.RpcResponse;
import com.cn.rpc.serialization.Serialization;
import com.cn.rpc.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 解码
 *
 * @Description: RpcDecoder
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class RpcDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf byteBuf, List<Object> list) throws Exception {
        // 不到16字节的话无法解析消息头，暂不读取
        if (byteBuf.readableBytes() < Constants.HEADER_SIZE) {
            return;
        }
        // 记录当前指针位置
        byteBuf.markReaderIndex();
        //读取魔数头
        short magic = byteBuf.readShort();
        // 魔数头 不符合直接丢异常
        if (Constants.MAGIC != magic) {
            //这里需要重置 指针
            byteBuf.resetReaderIndex();
            throw new RuntimeException();
        } // 依次读取消息版本、附加信息、消息ID以及消息体长度四部分
        byte version = byteBuf.readByte();
        byte extraInfo = byteBuf.readByte();
        long messageId = byteBuf.readLong();
        int size = byteBuf.readInt();
        Object body = null;
        // 心跳消息是没有消息体的，无需读取
        if (!Constants.isHeartBeat(extraInfo)) {
            // 对于非心跳消息，没有积累到足够的数据是无法进行反序列化的
            if (byteBuf.readableBytes() < size) {
                byteBuf.resetReaderIndex();
                return;
            }
            // 读取消息体并进行反序列化
            byte[] payload = new byte[size];
            byteBuf.readBytes(payload);
            // 这里根据消息头中的extraInfo部分选择相应的序列化和压缩方式
            Serialization serialization = SerializationFactory.get(extraInfo);
            Compressor compressor = CompressorFactory.get(extraInfo);
            if (Constants.isRequest(extraInfo)) {
                // 得到消息体
                body = serialization.deserialize(compressor.unCompress(payload),
                        Request.class);
            } else {
                // 得到消息体
                body = serialization.deserialize(compressor.unCompress(payload),
                        RpcResponse.class);
            }
        }
    }
}
