package io.netty.handler.codec.protobuf;

import io.netty.buffer.ByteBufUtil;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import com.google.protobuf.nano.MessageNano;
import io.netty.channel.ChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToMessageDecoder;

@ChannelHandler.Sharable
public class ProtobufDecoderNano extends MessageToMessageDecoder<ByteBuf>
{
    private final Class<? extends MessageNano> clazz;
    
    public ProtobufDecoderNano(final Class<? extends MessageNano> clazz) {
        this.clazz = ObjectUtil.checkNotNull(clazz, "You must provide a Class");
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf msg, final List<Object> out) throws Exception {
        final int length = msg.readableBytes();
        byte[] array;
        int offset;
        if (msg.hasArray()) {
            array = msg.array();
            offset = msg.arrayOffset() + msg.readerIndex();
        }
        else {
            array = ByteBufUtil.getBytes(msg, msg.readerIndex(), length, false);
            offset = 0;
        }
        final MessageNano prototype = (MessageNano)this.clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        out.add(MessageNano.mergeFrom(prototype, array, offset, length));
    }
}
