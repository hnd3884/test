package io.netty.handler.codec.http2;

import io.netty.util.AsciiString;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZstdEncoder;
import io.netty.handler.codec.compression.BrotliEncoder;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.internal.ObjectUtil;
import io.netty.handler.codec.compression.StandardCompressionOptions;
import io.netty.handler.codec.compression.CompressionOptions;
import io.netty.handler.codec.compression.ZstdOptions;
import io.netty.handler.codec.compression.DeflateOptions;
import io.netty.handler.codec.compression.GzipOptions;
import io.netty.handler.codec.compression.BrotliOptions;

public class CompressorHttp2ConnectionEncoder extends DecoratingHttp2ConnectionEncoder
{
    public static final int DEFAULT_COMPRESSION_LEVEL = 6;
    public static final int DEFAULT_WINDOW_BITS = 15;
    public static final int DEFAULT_MEM_LEVEL = 8;
    private int compressionLevel;
    private int windowBits;
    private int memLevel;
    private final Http2Connection.PropertyKey propertyKey;
    private final boolean supportsCompressionOptions;
    private BrotliOptions brotliOptions;
    private GzipOptions gzipCompressionOptions;
    private DeflateOptions deflateOptions;
    private ZstdOptions zstdOptions;
    
    public CompressorHttp2ConnectionEncoder(final Http2ConnectionEncoder delegate) {
        this(delegate, new CompressionOptions[] { StandardCompressionOptions.brotli(), StandardCompressionOptions.gzip(), StandardCompressionOptions.deflate() });
    }
    
    @Deprecated
    public CompressorHttp2ConnectionEncoder(final Http2ConnectionEncoder delegate, final int compressionLevel, final int windowBits, final int memLevel) {
        super(delegate);
        this.compressionLevel = ObjectUtil.checkInRange(compressionLevel, 0, 9, "compressionLevel");
        this.windowBits = ObjectUtil.checkInRange(windowBits, 9, 15, "windowBits");
        this.memLevel = ObjectUtil.checkInRange(memLevel, 1, 9, "memLevel");
        this.propertyKey = this.connection().newKey();
        this.connection().addListener(new Http2ConnectionAdapter() {
            @Override
            public void onStreamRemoved(final Http2Stream stream) {
                final EmbeddedChannel compressor = stream.getProperty(CompressorHttp2ConnectionEncoder.this.propertyKey);
                if (compressor != null) {
                    CompressorHttp2ConnectionEncoder.this.cleanup(stream, compressor);
                }
            }
        });
        this.supportsCompressionOptions = false;
    }
    
    public CompressorHttp2ConnectionEncoder(final Http2ConnectionEncoder delegate, final CompressionOptions... compressionOptionsArgs) {
        super(delegate);
        ObjectUtil.checkNotNull(compressionOptionsArgs, "CompressionOptions");
        ObjectUtil.deepCheckNotNull("CompressionOptions", compressionOptionsArgs);
        for (final CompressionOptions compressionOptions : compressionOptionsArgs) {
            if (compressionOptions instanceof BrotliOptions) {
                this.brotliOptions = (BrotliOptions)compressionOptions;
            }
            else if (compressionOptions instanceof GzipOptions) {
                this.gzipCompressionOptions = (GzipOptions)compressionOptions;
            }
            else if (compressionOptions instanceof DeflateOptions) {
                this.deflateOptions = (DeflateOptions)compressionOptions;
            }
            else {
                if (!(compressionOptions instanceof ZstdOptions)) {
                    throw new IllegalArgumentException("Unsupported " + CompressionOptions.class.getSimpleName() + ": " + compressionOptions);
                }
                this.zstdOptions = (ZstdOptions)compressionOptions;
            }
        }
        this.supportsCompressionOptions = true;
        this.propertyKey = this.connection().newKey();
        this.connection().addListener(new Http2ConnectionAdapter() {
            @Override
            public void onStreamRemoved(final Http2Stream stream) {
                final EmbeddedChannel compressor = stream.getProperty(CompressorHttp2ConnectionEncoder.this.propertyKey);
                if (compressor != null) {
                    CompressorHttp2ConnectionEncoder.this.cleanup(stream, compressor);
                }
            }
        });
    }
    
    @Override
    public ChannelFuture writeData(final ChannelHandlerContext ctx, final int streamId, final ByteBuf data, int padding, final boolean endOfStream, final ChannelPromise promise) {
        final Http2Stream stream = this.connection().stream(streamId);
        final EmbeddedChannel channel = (stream == null) ? null : stream.getProperty(this.propertyKey);
        if (channel == null) {
            return super.writeData(ctx, streamId, data, padding, endOfStream, promise);
        }
        try {
            channel.writeOutbound(data);
            ByteBuf buf = nextReadableBuf(channel);
            if (buf == null) {
                if (endOfStream) {
                    if (channel.finish()) {
                        buf = nextReadableBuf(channel);
                    }
                    return super.writeData(ctx, streamId, (buf == null) ? Unpooled.EMPTY_BUFFER : buf, padding, true, promise);
                }
                promise.setSuccess();
                return promise;
            }
            else {
                final PromiseCombiner combiner = new PromiseCombiner(ctx.executor());
                while (true) {
                    ByteBuf nextBuf = nextReadableBuf(channel);
                    boolean compressedEndOfStream = nextBuf == null && endOfStream;
                    if (compressedEndOfStream && channel.finish()) {
                        nextBuf = nextReadableBuf(channel);
                        compressedEndOfStream = (nextBuf == null);
                    }
                    final ChannelPromise bufPromise = ctx.newPromise();
                    combiner.add(bufPromise);
                    super.writeData(ctx, streamId, buf, padding, compressedEndOfStream, bufPromise);
                    if (nextBuf == null) {
                        break;
                    }
                    padding = 0;
                    buf = nextBuf;
                }
                combiner.finish(promise);
            }
        }
        catch (final Throwable cause) {
            promise.tryFailure(cause);
        }
        finally {
            if (endOfStream) {
                this.cleanup(stream, channel);
            }
        }
        return promise;
    }
    
    @Override
    public ChannelFuture writeHeaders(final ChannelHandlerContext ctx, final int streamId, final Http2Headers headers, final int padding, final boolean endStream, final ChannelPromise promise) {
        try {
            final EmbeddedChannel compressor = this.newCompressor(ctx, headers, endStream);
            final ChannelFuture future = super.writeHeaders(ctx, streamId, headers, padding, endStream, promise);
            this.bindCompressorToStream(compressor, streamId);
            return future;
        }
        catch (final Throwable e) {
            promise.tryFailure(e);
            return promise;
        }
    }
    
    @Override
    public ChannelFuture writeHeaders(final ChannelHandlerContext ctx, final int streamId, final Http2Headers headers, final int streamDependency, final short weight, final boolean exclusive, final int padding, final boolean endOfStream, final ChannelPromise promise) {
        try {
            final EmbeddedChannel compressor = this.newCompressor(ctx, headers, endOfStream);
            final ChannelFuture future = super.writeHeaders(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endOfStream, promise);
            this.bindCompressorToStream(compressor, streamId);
            return future;
        }
        catch (final Throwable e) {
            promise.tryFailure(e);
            return promise;
        }
    }
    
    protected EmbeddedChannel newContentCompressor(final ChannelHandlerContext ctx, final CharSequence contentEncoding) throws Http2Exception {
        if (HttpHeaderValues.GZIP.contentEqualsIgnoreCase(contentEncoding) || HttpHeaderValues.X_GZIP.contentEqualsIgnoreCase(contentEncoding)) {
            return this.newCompressionChannel(ctx, ZlibWrapper.GZIP);
        }
        if (HttpHeaderValues.DEFLATE.contentEqualsIgnoreCase(contentEncoding) || HttpHeaderValues.X_DEFLATE.contentEqualsIgnoreCase(contentEncoding)) {
            return this.newCompressionChannel(ctx, ZlibWrapper.ZLIB);
        }
        if (this.brotliOptions != null && HttpHeaderValues.BR.contentEqualsIgnoreCase(contentEncoding)) {
            return new EmbeddedChannel(ctx.channel().id(), ctx.channel().metadata().hasDisconnect(), ctx.channel().config(), new ChannelHandler[] { new BrotliEncoder(this.brotliOptions.parameters()) });
        }
        if (this.zstdOptions != null && HttpHeaderValues.ZSTD.contentEqualsIgnoreCase(contentEncoding)) {
            return new EmbeddedChannel(ctx.channel().id(), ctx.channel().metadata().hasDisconnect(), ctx.channel().config(), new ChannelHandler[] { new ZstdEncoder(this.zstdOptions.compressionLevel(), this.zstdOptions.blockSize(), this.zstdOptions.maxEncodeSize()) });
        }
        return null;
    }
    
    protected CharSequence getTargetContentEncoding(final CharSequence contentEncoding) throws Http2Exception {
        return contentEncoding;
    }
    
    private EmbeddedChannel newCompressionChannel(final ChannelHandlerContext ctx, final ZlibWrapper wrapper) {
        if (!this.supportsCompressionOptions) {
            return new EmbeddedChannel(ctx.channel().id(), ctx.channel().metadata().hasDisconnect(), ctx.channel().config(), new ChannelHandler[] { ZlibCodecFactory.newZlibEncoder(wrapper, this.compressionLevel, this.windowBits, this.memLevel) });
        }
        if (wrapper == ZlibWrapper.GZIP && this.gzipCompressionOptions != null) {
            return new EmbeddedChannel(ctx.channel().id(), ctx.channel().metadata().hasDisconnect(), ctx.channel().config(), new ChannelHandler[] { ZlibCodecFactory.newZlibEncoder(wrapper, this.gzipCompressionOptions.compressionLevel(), this.gzipCompressionOptions.windowBits(), this.gzipCompressionOptions.memLevel()) });
        }
        if (wrapper == ZlibWrapper.ZLIB && this.deflateOptions != null) {
            return new EmbeddedChannel(ctx.channel().id(), ctx.channel().metadata().hasDisconnect(), ctx.channel().config(), new ChannelHandler[] { ZlibCodecFactory.newZlibEncoder(wrapper, this.deflateOptions.compressionLevel(), this.deflateOptions.windowBits(), this.deflateOptions.memLevel()) });
        }
        throw new IllegalArgumentException("Unsupported ZlibWrapper: " + wrapper);
    }
    
    private EmbeddedChannel newCompressor(final ChannelHandlerContext ctx, final Http2Headers headers, final boolean endOfStream) throws Http2Exception {
        if (endOfStream) {
            return null;
        }
        CharSequence encoding = ((Headers<AsciiString, CharSequence, T>)headers).get(HttpHeaderNames.CONTENT_ENCODING);
        if (encoding == null) {
            encoding = HttpHeaderValues.IDENTITY;
        }
        final EmbeddedChannel compressor = this.newContentCompressor(ctx, encoding);
        if (compressor != null) {
            final CharSequence targetContentEncoding = this.getTargetContentEncoding(encoding);
            if (HttpHeaderValues.IDENTITY.contentEqualsIgnoreCase(targetContentEncoding)) {
                ((Headers<AsciiString, V, T>)headers).remove(HttpHeaderNames.CONTENT_ENCODING);
            }
            else {
                ((Headers<AsciiString, CharSequence, Headers>)headers).set(HttpHeaderNames.CONTENT_ENCODING, targetContentEncoding);
            }
            ((Headers<AsciiString, V, T>)headers).remove(HttpHeaderNames.CONTENT_LENGTH);
        }
        return compressor;
    }
    
    private void bindCompressorToStream(final EmbeddedChannel compressor, final int streamId) {
        if (compressor != null) {
            final Http2Stream stream = this.connection().stream(streamId);
            if (stream != null) {
                stream.setProperty(this.propertyKey, compressor);
            }
        }
    }
    
    void cleanup(final Http2Stream stream, final EmbeddedChannel compressor) {
        compressor.finishAndReleaseAll();
        stream.removeProperty(this.propertyKey);
    }
    
    private static ByteBuf nextReadableBuf(final EmbeddedChannel compressor) {
        while (true) {
            final ByteBuf buf = compressor.readOutbound();
            if (buf == null) {
                return null;
            }
            if (buf.isReadable()) {
                return buf;
            }
            buf.release();
        }
    }
}
