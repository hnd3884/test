package io.netty.handler.codec.compression;

import io.netty.util.internal.ObjectUtil;
import com.aayushatharva.brotli4j.encoder.Encoder;

public final class BrotliOptions implements CompressionOptions
{
    private final Encoder.Parameters parameters;
    static final BrotliOptions DEFAULT;
    
    BrotliOptions(final Encoder.Parameters parameters) {
        if (!Brotli.isAvailable()) {
            throw new IllegalStateException("Brotli is not available", Brotli.cause());
        }
        this.parameters = ObjectUtil.checkNotNull(parameters, "Parameters");
    }
    
    public Encoder.Parameters parameters() {
        return this.parameters;
    }
    
    static {
        DEFAULT = new BrotliOptions(new Encoder.Parameters().setQuality(4).setMode(Encoder.Mode.TEXT));
    }
}
