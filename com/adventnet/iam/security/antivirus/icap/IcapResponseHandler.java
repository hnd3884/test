package com.adventnet.iam.security.antivirus.icap;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ExceptionEvent;
import java.util.concurrent.CountDownLatch;
import ch.mimo.netty.handler.codec.icap.IcapResponse;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class IcapResponseHandler extends SimpleChannelUpstreamHandler
{
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        if (!(e.getMessage() instanceof IcapResponse)) {
            return;
        }
        if (!(ctx.getChannel().getAttachment() instanceof CountDownLatch)) {
            throw new IcapClientException("Invalid Attachement");
        }
        final CountDownLatch latch = (CountDownLatch)ctx.getChannel().getAttachment();
        ctx.getChannel().setAttachment(e.getMessage());
        latch.countDown();
    }
    
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        if (ctx.getChannel().getAttachment() instanceof CountDownLatch) {
            final CountDownLatch latch = (CountDownLatch)ctx.getChannel().getAttachment();
            ctx.getChannel().setAttachment((Object)e);
            latch.countDown();
        }
        ctx.sendUpstream((ChannelEvent)e);
    }
}
