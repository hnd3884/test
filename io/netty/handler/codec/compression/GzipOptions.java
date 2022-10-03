package io.netty.handler.codec.compression;

public final class GzipOptions extends DeflateOptions
{
    static final GzipOptions DEFAULT;
    
    GzipOptions(final int compressionLevel, final int windowBits, final int memLevel) {
        super(compressionLevel, windowBits, memLevel);
    }
    
    static {
        DEFAULT = new GzipOptions(6, 15, 8);
    }
}
