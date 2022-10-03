package io.netty.handler.codec.smtp;

import io.netty.buffer.Unpooled;
import java.util.Iterator;
import java.util.RandomAccess;
import io.netty.buffer.ByteBufUtil;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToMessageEncoder;

public final class SmtpRequestEncoder extends MessageToMessageEncoder<Object>
{
    private static final int CRLF_SHORT = 3338;
    private static final byte SP = 32;
    private static final ByteBuf DOT_CRLF_BUFFER;
    private boolean contentExpected;
    
    @Override
    public boolean acceptOutboundMessage(final Object msg) throws Exception {
        return msg instanceof SmtpRequest || msg instanceof SmtpContent;
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object msg, final List<Object> out) throws Exception {
        if (msg instanceof SmtpRequest) {
            final SmtpRequest req = (SmtpRequest)msg;
            if (this.contentExpected) {
                if (!req.command().equals(SmtpCommand.RSET)) {
                    throw new IllegalStateException("SmtpContent expected");
                }
                this.contentExpected = false;
            }
            boolean release = true;
            final ByteBuf buffer = ctx.alloc().buffer();
            try {
                req.command().encode(buffer);
                final boolean notEmpty = req.command() != SmtpCommand.EMPTY;
                writeParameters(req.parameters(), buffer, notEmpty);
                ByteBufUtil.writeShortBE(buffer, 3338);
                out.add(buffer);
                release = false;
                if (req.command().isContentExpected()) {
                    this.contentExpected = true;
                }
            }
            finally {
                if (release) {
                    buffer.release();
                }
            }
        }
        if (msg instanceof SmtpContent) {
            if (!this.contentExpected) {
                throw new IllegalStateException("No SmtpContent expected");
            }
            final ByteBuf content = ((SmtpContent)msg).content();
            out.add(content.retain());
            if (msg instanceof LastSmtpContent) {
                out.add(SmtpRequestEncoder.DOT_CRLF_BUFFER.retainedDuplicate());
                this.contentExpected = false;
            }
        }
    }
    
    private static void writeParameters(final List<CharSequence> parameters, final ByteBuf out, final boolean commandNotEmpty) {
        if (parameters.isEmpty()) {
            return;
        }
        if (commandNotEmpty) {
            out.writeByte(32);
        }
        if (parameters instanceof RandomAccess) {
            final int sizeMinusOne = parameters.size() - 1;
            for (int i = 0; i < sizeMinusOne; ++i) {
                ByteBufUtil.writeAscii(out, parameters.get(i));
                out.writeByte(32);
            }
            ByteBufUtil.writeAscii(out, parameters.get(sizeMinusOne));
        }
        else {
            final Iterator<CharSequence> params = parameters.iterator();
            while (true) {
                ByteBufUtil.writeAscii(out, params.next());
                if (!params.hasNext()) {
                    break;
                }
                out.writeByte(32);
            }
        }
    }
    
    static {
        DOT_CRLF_BUFFER = Unpooled.unreleasableBuffer(Unpooled.directBuffer(3).writeByte(46).writeByte(13).writeByte(10));
    }
}
