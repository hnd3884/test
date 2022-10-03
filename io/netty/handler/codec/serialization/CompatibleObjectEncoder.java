package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import io.netty.util.internal.ObjectUtil;
import java.io.Serializable;
import io.netty.handler.codec.MessageToByteEncoder;

public class CompatibleObjectEncoder extends MessageToByteEncoder<Serializable>
{
    private final int resetInterval;
    private int writtenObjects;
    
    public CompatibleObjectEncoder() {
        this(16);
    }
    
    public CompatibleObjectEncoder(final int resetInterval) {
        this.resetInterval = ObjectUtil.checkPositiveOrZero(resetInterval, "resetInterval");
    }
    
    protected ObjectOutputStream newObjectOutputStream(final OutputStream out) throws Exception {
        return new ObjectOutputStream(out);
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final Serializable msg, final ByteBuf out) throws Exception {
        final ObjectOutputStream oos = this.newObjectOutputStream(new ByteBufOutputStream(out));
        try {
            if (this.resetInterval != 0) {
                ++this.writtenObjects;
                if (this.writtenObjects % this.resetInterval == 0) {
                    oos.reset();
                }
            }
            oos.writeObject(msg);
            oos.flush();
        }
        finally {
            oos.close();
        }
    }
}
