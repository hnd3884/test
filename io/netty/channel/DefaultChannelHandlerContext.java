package io.netty.channel;

import io.netty.util.concurrent.EventExecutor;

final class DefaultChannelHandlerContext extends AbstractChannelHandlerContext
{
    private final ChannelHandler handler;
    
    DefaultChannelHandlerContext(final DefaultChannelPipeline pipeline, final EventExecutor executor, final String name, final ChannelHandler handler) {
        super(pipeline, executor, name, handler.getClass());
        this.handler = handler;
    }
    
    @Override
    public ChannelHandler handler() {
        return this.handler;
    }
}
