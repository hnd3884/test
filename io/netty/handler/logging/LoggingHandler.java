package io.netty.handler.logging;

import io.netty.buffer.ByteBufUtil;
import io.netty.util.internal.StringUtil;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPromise;
import java.net.SocketAddress;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelDuplexHandler;

@ChannelHandler.Sharable
public class LoggingHandler extends ChannelDuplexHandler
{
    private static final LogLevel DEFAULT_LEVEL;
    protected final InternalLogger logger;
    protected final InternalLogLevel internalLevel;
    private final LogLevel level;
    private final ByteBufFormat byteBufFormat;
    
    public LoggingHandler() {
        this(LoggingHandler.DEFAULT_LEVEL);
    }
    
    public LoggingHandler(final ByteBufFormat format) {
        this(LoggingHandler.DEFAULT_LEVEL, format);
    }
    
    public LoggingHandler(final LogLevel level) {
        this(level, ByteBufFormat.HEX_DUMP);
    }
    
    public LoggingHandler(final LogLevel level, final ByteBufFormat byteBufFormat) {
        this.level = ObjectUtil.checkNotNull(level, "level");
        this.byteBufFormat = ObjectUtil.checkNotNull(byteBufFormat, "byteBufFormat");
        this.logger = InternalLoggerFactory.getInstance(this.getClass());
        this.internalLevel = level.toInternalLevel();
    }
    
    public LoggingHandler(final Class<?> clazz) {
        this(clazz, LoggingHandler.DEFAULT_LEVEL);
    }
    
    public LoggingHandler(final Class<?> clazz, final LogLevel level) {
        this(clazz, level, ByteBufFormat.HEX_DUMP);
    }
    
    public LoggingHandler(final Class<?> clazz, final LogLevel level, final ByteBufFormat byteBufFormat) {
        ObjectUtil.checkNotNull(clazz, "clazz");
        this.level = ObjectUtil.checkNotNull(level, "level");
        this.byteBufFormat = ObjectUtil.checkNotNull(byteBufFormat, "byteBufFormat");
        this.logger = InternalLoggerFactory.getInstance(clazz);
        this.internalLevel = level.toInternalLevel();
    }
    
    public LoggingHandler(final String name) {
        this(name, LoggingHandler.DEFAULT_LEVEL);
    }
    
    public LoggingHandler(final String name, final LogLevel level) {
        this(name, level, ByteBufFormat.HEX_DUMP);
    }
    
    public LoggingHandler(final String name, final LogLevel level, final ByteBufFormat byteBufFormat) {
        ObjectUtil.checkNotNull(name, "name");
        this.level = ObjectUtil.checkNotNull(level, "level");
        this.byteBufFormat = ObjectUtil.checkNotNull(byteBufFormat, "byteBufFormat");
        this.logger = InternalLoggerFactory.getInstance(name);
        this.internalLevel = level.toInternalLevel();
    }
    
    public LogLevel level() {
        return this.level;
    }
    
    public ByteBufFormat byteBufFormat() {
        return this.byteBufFormat;
    }
    
    @Override
    public void channelRegistered(final ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(ctx, "REGISTERED"));
        }
        ctx.fireChannelRegistered();
    }
    
    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(ctx, "UNREGISTERED"));
        }
        ctx.fireChannelUnregistered();
    }
    
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(ctx, "ACTIVE"));
        }
        ctx.fireChannelActive();
    }
    
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(ctx, "INACTIVE"));
        }
        ctx.fireChannelInactive();
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(ctx, "EXCEPTION", cause), cause);
        }
        ctx.fireExceptionCaught(cause);
    }
    
    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(ctx, "USER_EVENT", evt));
        }
        ctx.fireUserEventTriggered(evt);
    }
    
    @Override
    public void bind(final ChannelHandlerContext ctx, final SocketAddress localAddress, final ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(ctx, "BIND", localAddress));
        }
        ctx.bind(localAddress, promise);
    }
    
    @Override
    public void connect(final ChannelHandlerContext ctx, final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(ctx, "CONNECT", remoteAddress, localAddress));
        }
        ctx.connect(remoteAddress, localAddress, promise);
    }
    
    @Override
    public void disconnect(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(ctx, "DISCONNECT"));
        }
        ctx.disconnect(promise);
    }
    
    @Override
    public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(ctx, "CLOSE"));
        }
        ctx.close(promise);
    }
    
    @Override
    public void deregister(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(ctx, "DEREGISTER"));
        }
        ctx.deregister(promise);
    }
    
    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(ctx, "READ COMPLETE"));
        }
        ctx.fireChannelReadComplete();
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(ctx, "READ", msg));
        }
        ctx.fireChannelRead(msg);
    }
    
    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(ctx, "WRITE", msg));
        }
        ctx.write(msg, promise);
    }
    
    @Override
    public void channelWritabilityChanged(final ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(ctx, "WRITABILITY CHANGED"));
        }
        ctx.fireChannelWritabilityChanged();
    }
    
    @Override
    public void flush(final ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, this.format(ctx, "FLUSH"));
        }
        ctx.flush();
    }
    
    protected String format(final ChannelHandlerContext ctx, final String eventName) {
        final String chStr = ctx.channel().toString();
        return new StringBuilder(chStr.length() + 1 + eventName.length()).append(chStr).append(' ').append(eventName).toString();
    }
    
    protected String format(final ChannelHandlerContext ctx, final String eventName, final Object arg) {
        if (arg instanceof ByteBuf) {
            return this.formatByteBuf(ctx, eventName, (ByteBuf)arg);
        }
        if (arg instanceof ByteBufHolder) {
            return this.formatByteBufHolder(ctx, eventName, (ByteBufHolder)arg);
        }
        return formatSimple(ctx, eventName, arg);
    }
    
    protected String format(final ChannelHandlerContext ctx, final String eventName, final Object firstArg, final Object secondArg) {
        if (secondArg == null) {
            return formatSimple(ctx, eventName, firstArg);
        }
        final String chStr = ctx.channel().toString();
        final String arg1Str = String.valueOf(firstArg);
        final String arg2Str = secondArg.toString();
        final StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 2 + arg1Str.length() + 2 + arg2Str.length());
        buf.append(chStr).append(' ').append(eventName).append(": ").append(arg1Str).append(", ").append(arg2Str);
        return buf.toString();
    }
    
    private String formatByteBuf(final ChannelHandlerContext ctx, final String eventName, final ByteBuf msg) {
        final String chStr = ctx.channel().toString();
        final int length = msg.readableBytes();
        if (length == 0) {
            final StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 4);
            buf.append(chStr).append(' ').append(eventName).append(": 0B");
            return buf.toString();
        }
        int outputLength = chStr.length() + 1 + eventName.length() + 2 + 10 + 1;
        if (this.byteBufFormat == ByteBufFormat.HEX_DUMP) {
            final int rows = length / 16 + ((length % 15 != 0) ? 1 : 0) + 4;
            final int hexDumpLength = 2 + rows * 80;
            outputLength += hexDumpLength;
        }
        final StringBuilder buf2 = new StringBuilder(outputLength);
        buf2.append(chStr).append(' ').append(eventName).append(": ").append(length).append('B');
        if (this.byteBufFormat == ByteBufFormat.HEX_DUMP) {
            buf2.append(StringUtil.NEWLINE);
            ByteBufUtil.appendPrettyHexDump(buf2, msg);
        }
        return buf2.toString();
    }
    
    private String formatByteBufHolder(final ChannelHandlerContext ctx, final String eventName, final ByteBufHolder msg) {
        final String chStr = ctx.channel().toString();
        final String msgStr = msg.toString();
        final ByteBuf content = msg.content();
        final int length = content.readableBytes();
        if (length == 0) {
            final StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 2 + msgStr.length() + 4);
            buf.append(chStr).append(' ').append(eventName).append(", ").append(msgStr).append(", 0B");
            return buf.toString();
        }
        int outputLength = chStr.length() + 1 + eventName.length() + 2 + msgStr.length() + 2 + 10 + 1;
        if (this.byteBufFormat == ByteBufFormat.HEX_DUMP) {
            final int rows = length / 16 + ((length % 15 != 0) ? 1 : 0) + 4;
            final int hexDumpLength = 2 + rows * 80;
            outputLength += hexDumpLength;
        }
        final StringBuilder buf2 = new StringBuilder(outputLength);
        buf2.append(chStr).append(' ').append(eventName).append(": ").append(msgStr).append(", ").append(length).append('B');
        if (this.byteBufFormat == ByteBufFormat.HEX_DUMP) {
            buf2.append(StringUtil.NEWLINE);
            ByteBufUtil.appendPrettyHexDump(buf2, content);
        }
        return buf2.toString();
    }
    
    private static String formatSimple(final ChannelHandlerContext ctx, final String eventName, final Object msg) {
        final String chStr = ctx.channel().toString();
        final String msgStr = String.valueOf(msg);
        final StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 2 + msgStr.length());
        return buf.append(chStr).append(' ').append(eventName).append(": ").append(msgStr).toString();
    }
    
    static {
        DEFAULT_LEVEL = LogLevel.DEBUG;
    }
}
