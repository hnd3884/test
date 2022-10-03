package io.netty.handler.codec.http;

import io.netty.handler.codec.compression.ZstdEncoder;
import io.netty.handler.codec.compression.BrotliEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.ChannelHandler;
import java.util.HashMap;
import io.netty.handler.codec.compression.Zstd;
import io.netty.handler.codec.compression.StandardCompressionOptions;
import io.netty.handler.codec.compression.Brotli;
import io.netty.handler.codec.compression.CompressionOptions;
import io.netty.util.internal.ObjectUtil;
import java.util.Map;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.compression.ZstdOptions;
import io.netty.handler.codec.compression.DeflateOptions;
import io.netty.handler.codec.compression.GzipOptions;
import io.netty.handler.codec.compression.BrotliOptions;

public class HttpContentCompressor extends HttpContentEncoder
{
    private final boolean supportsCompressionOptions;
    private final BrotliOptions brotliOptions;
    private final GzipOptions gzipOptions;
    private final DeflateOptions deflateOptions;
    private final ZstdOptions zstdOptions;
    private final int compressionLevel;
    private final int windowBits;
    private final int memLevel;
    private final int contentSizeThreshold;
    private ChannelHandlerContext ctx;
    private final Map<String, CompressionEncoderFactory> factories;
    
    public HttpContentCompressor() {
        this(6);
    }
    
    @Deprecated
    public HttpContentCompressor(final int compressionLevel) {
        this(compressionLevel, 15, 8, 0);
    }
    
    @Deprecated
    public HttpContentCompressor(final int compressionLevel, final int windowBits, final int memLevel) {
        this(compressionLevel, windowBits, memLevel, 0);
    }
    
    @Deprecated
    public HttpContentCompressor(final int compressionLevel, final int windowBits, final int memLevel, final int contentSizeThreshold) {
        this.compressionLevel = ObjectUtil.checkInRange(compressionLevel, 0, 9, "compressionLevel");
        this.windowBits = ObjectUtil.checkInRange(windowBits, 9, 15, "windowBits");
        this.memLevel = ObjectUtil.checkInRange(memLevel, 1, 9, "memLevel");
        this.contentSizeThreshold = ObjectUtil.checkPositiveOrZero(contentSizeThreshold, "contentSizeThreshold");
        this.brotliOptions = null;
        this.gzipOptions = null;
        this.deflateOptions = null;
        this.zstdOptions = null;
        this.factories = null;
        this.supportsCompressionOptions = false;
    }
    
    public HttpContentCompressor(final CompressionOptions... compressionOptions) {
        this(0, compressionOptions);
    }
    
    public HttpContentCompressor(final int contentSizeThreshold, final CompressionOptions... compressionOptions) {
        this.contentSizeThreshold = ObjectUtil.checkPositiveOrZero(contentSizeThreshold, "contentSizeThreshold");
        BrotliOptions brotliOptions = null;
        GzipOptions gzipOptions = null;
        DeflateOptions deflateOptions = null;
        ZstdOptions zstdOptions = null;
        if (compressionOptions == null || compressionOptions.length == 0) {
            brotliOptions = (Brotli.isAvailable() ? StandardCompressionOptions.brotli() : null);
            gzipOptions = StandardCompressionOptions.gzip();
            deflateOptions = StandardCompressionOptions.deflate();
            zstdOptions = (Zstd.isAvailable() ? StandardCompressionOptions.zstd() : null);
        }
        else {
            ObjectUtil.deepCheckNotNull("compressionOptions", compressionOptions);
            for (final CompressionOptions compressionOption : compressionOptions) {
                if (compressionOption instanceof BrotliOptions) {
                    brotliOptions = (BrotliOptions)compressionOption;
                }
                else if (compressionOption instanceof GzipOptions) {
                    gzipOptions = (GzipOptions)compressionOption;
                }
                else if (compressionOption instanceof DeflateOptions) {
                    deflateOptions = (DeflateOptions)compressionOption;
                }
                else {
                    if (!(compressionOption instanceof ZstdOptions)) {
                        throw new IllegalArgumentException("Unsupported " + CompressionOptions.class.getSimpleName() + ": " + compressionOption);
                    }
                    zstdOptions = (ZstdOptions)compressionOption;
                }
            }
        }
        this.gzipOptions = gzipOptions;
        this.deflateOptions = deflateOptions;
        this.brotliOptions = brotliOptions;
        this.zstdOptions = zstdOptions;
        this.factories = new HashMap<String, CompressionEncoderFactory>();
        if (this.gzipOptions != null) {
            this.factories.put("gzip", new GzipEncoderFactory());
        }
        if (this.deflateOptions != null) {
            this.factories.put("deflate", new DeflateEncoderFactory());
        }
        if (this.brotliOptions != null) {
            this.factories.put("br", new BrEncoderFactory());
        }
        if (this.zstdOptions != null) {
            this.factories.put("zstd", new ZstdEncoderFactory());
        }
        this.compressionLevel = -1;
        this.windowBits = -1;
        this.memLevel = -1;
        this.supportsCompressionOptions = true;
    }
    
    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }
    
    @Override
    protected Result beginEncode(final HttpResponse httpResponse, final String acceptEncoding) throws Exception {
        if (this.contentSizeThreshold > 0 && httpResponse instanceof HttpContent && ((HttpContent)httpResponse).content().readableBytes() < this.contentSizeThreshold) {
            return null;
        }
        final String contentEncoding = httpResponse.headers().get(HttpHeaderNames.CONTENT_ENCODING);
        if (contentEncoding != null) {
            return null;
        }
        if (this.supportsCompressionOptions) {
            final String targetContentEncoding = this.determineEncoding(acceptEncoding);
            if (targetContentEncoding == null) {
                return null;
            }
            final CompressionEncoderFactory encoderFactory = this.factories.get(targetContentEncoding);
            if (encoderFactory == null) {
                throw new Error();
            }
            return new Result(targetContentEncoding, new EmbeddedChannel(this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx.channel().config(), new ChannelHandler[] { encoderFactory.createEncoder() }));
        }
        else {
            final ZlibWrapper wrapper = this.determineWrapper(acceptEncoding);
            if (wrapper == null) {
                return null;
            }
            String targetContentEncoding2 = null;
            switch (wrapper) {
                case GZIP: {
                    targetContentEncoding2 = "gzip";
                    break;
                }
                case ZLIB: {
                    targetContentEncoding2 = "deflate";
                    break;
                }
                default: {
                    throw new Error();
                }
            }
            return new Result(targetContentEncoding2, new EmbeddedChannel(this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx.channel().config(), new ChannelHandler[] { ZlibCodecFactory.newZlibEncoder(wrapper, this.compressionLevel, this.windowBits, this.memLevel) }));
        }
    }
    
    protected String determineEncoding(final String acceptEncoding) {
        float starQ = -1.0f;
        float brQ = -1.0f;
        float zstdQ = -1.0f;
        float gzipQ = -1.0f;
        float deflateQ = -1.0f;
        for (final String encoding : acceptEncoding.split(",")) {
            float q = 1.0f;
            final int equalsPos = encoding.indexOf(61);
            if (equalsPos != -1) {
                try {
                    q = Float.parseFloat(encoding.substring(equalsPos + 1));
                }
                catch (final NumberFormatException e) {
                    q = 0.0f;
                }
            }
            if (encoding.contains("*")) {
                starQ = q;
            }
            else if (encoding.contains("br") && q > brQ) {
                brQ = q;
            }
            else if (encoding.contains("zstd") && q > zstdQ) {
                zstdQ = q;
            }
            else if (encoding.contains("gzip") && q > gzipQ) {
                gzipQ = q;
            }
            else if (encoding.contains("deflate") && q > deflateQ) {
                deflateQ = q;
            }
        }
        if (brQ > 0.0f || zstdQ > 0.0f || gzipQ > 0.0f || deflateQ > 0.0f) {
            if (brQ != -1.0f && brQ >= zstdQ && this.brotliOptions != null) {
                return "br";
            }
            if (zstdQ != -1.0f && zstdQ >= gzipQ && this.zstdOptions != null) {
                return "zstd";
            }
            if (gzipQ != -1.0f && gzipQ >= deflateQ && this.gzipOptions != null) {
                return "gzip";
            }
            if (deflateQ != -1.0f && this.deflateOptions != null) {
                return "deflate";
            }
        }
        if (starQ > 0.0f) {
            if (brQ == -1.0f && this.brotliOptions != null) {
                return "br";
            }
            if (zstdQ == -1.0f && this.zstdOptions != null) {
                return "zstd";
            }
            if (gzipQ == -1.0f && this.gzipOptions != null) {
                return "gzip";
            }
            if (deflateQ == -1.0f && this.deflateOptions != null) {
                return "deflate";
            }
        }
        return null;
    }
    
    @Deprecated
    protected ZlibWrapper determineWrapper(final String acceptEncoding) {
        float starQ = -1.0f;
        float gzipQ = -1.0f;
        float deflateQ = -1.0f;
        for (final String encoding : acceptEncoding.split(",")) {
            float q = 1.0f;
            final int equalsPos = encoding.indexOf(61);
            if (equalsPos != -1) {
                try {
                    q = Float.parseFloat(encoding.substring(equalsPos + 1));
                }
                catch (final NumberFormatException e) {
                    q = 0.0f;
                }
            }
            if (encoding.contains("*")) {
                starQ = q;
            }
            else if (encoding.contains("gzip") && q > gzipQ) {
                gzipQ = q;
            }
            else if (encoding.contains("deflate") && q > deflateQ) {
                deflateQ = q;
            }
        }
        if (gzipQ <= 0.0f && deflateQ <= 0.0f) {
            if (starQ > 0.0f) {
                if (gzipQ == -1.0f) {
                    return ZlibWrapper.GZIP;
                }
                if (deflateQ == -1.0f) {
                    return ZlibWrapper.ZLIB;
                }
            }
            return null;
        }
        if (gzipQ >= deflateQ) {
            return ZlibWrapper.GZIP;
        }
        return ZlibWrapper.ZLIB;
    }
    
    private final class GzipEncoderFactory implements CompressionEncoderFactory
    {
        @Override
        public MessageToByteEncoder<ByteBuf> createEncoder() {
            return ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP, HttpContentCompressor.this.gzipOptions.compressionLevel(), HttpContentCompressor.this.gzipOptions.windowBits(), HttpContentCompressor.this.gzipOptions.memLevel());
        }
    }
    
    private final class DeflateEncoderFactory implements CompressionEncoderFactory
    {
        @Override
        public MessageToByteEncoder<ByteBuf> createEncoder() {
            return ZlibCodecFactory.newZlibEncoder(ZlibWrapper.ZLIB, HttpContentCompressor.this.deflateOptions.compressionLevel(), HttpContentCompressor.this.deflateOptions.windowBits(), HttpContentCompressor.this.deflateOptions.memLevel());
        }
    }
    
    private final class BrEncoderFactory implements CompressionEncoderFactory
    {
        @Override
        public MessageToByteEncoder<ByteBuf> createEncoder() {
            return new BrotliEncoder(HttpContentCompressor.this.brotliOptions.parameters());
        }
    }
    
    private final class ZstdEncoderFactory implements CompressionEncoderFactory
    {
        @Override
        public MessageToByteEncoder<ByteBuf> createEncoder() {
            return new ZstdEncoder(HttpContentCompressor.this.zstdOptions.compressionLevel(), HttpContentCompressor.this.zstdOptions.blockSize(), HttpContentCompressor.this.zstdOptions.maxEncodeSize());
        }
    }
}
