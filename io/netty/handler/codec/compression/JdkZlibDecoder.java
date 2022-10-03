package io.netty.handler.codec.compression;

import java.util.zip.DataFormatException;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.zip.Checksum;
import java.util.zip.CRC32;
import io.netty.util.internal.ObjectUtil;
import java.util.zip.Inflater;

public class JdkZlibDecoder extends ZlibDecoder
{
    private static final int FHCRC = 2;
    private static final int FEXTRA = 4;
    private static final int FNAME = 8;
    private static final int FCOMMENT = 16;
    private static final int FRESERVED = 224;
    private Inflater inflater;
    private final byte[] dictionary;
    private final ByteBufChecksum crc;
    private final boolean decompressConcatenated;
    private GzipState gzipState;
    private int flags;
    private int xlen;
    private volatile boolean finished;
    private boolean decideZlibOrNone;
    
    public JdkZlibDecoder() {
        this(ZlibWrapper.ZLIB, null, false, 0);
    }
    
    public JdkZlibDecoder(final int maxAllocation) {
        this(ZlibWrapper.ZLIB, null, false, maxAllocation);
    }
    
    public JdkZlibDecoder(final byte[] dictionary) {
        this(ZlibWrapper.ZLIB, dictionary, false, 0);
    }
    
    public JdkZlibDecoder(final byte[] dictionary, final int maxAllocation) {
        this(ZlibWrapper.ZLIB, dictionary, false, maxAllocation);
    }
    
    public JdkZlibDecoder(final ZlibWrapper wrapper) {
        this(wrapper, null, false, 0);
    }
    
    public JdkZlibDecoder(final ZlibWrapper wrapper, final int maxAllocation) {
        this(wrapper, null, false, maxAllocation);
    }
    
    public JdkZlibDecoder(final ZlibWrapper wrapper, final boolean decompressConcatenated) {
        this(wrapper, null, decompressConcatenated, 0);
    }
    
    public JdkZlibDecoder(final ZlibWrapper wrapper, final boolean decompressConcatenated, final int maxAllocation) {
        this(wrapper, null, decompressConcatenated, maxAllocation);
    }
    
    public JdkZlibDecoder(final boolean decompressConcatenated) {
        this(ZlibWrapper.GZIP, null, decompressConcatenated, 0);
    }
    
    public JdkZlibDecoder(final boolean decompressConcatenated, final int maxAllocation) {
        this(ZlibWrapper.GZIP, null, decompressConcatenated, maxAllocation);
    }
    
    private JdkZlibDecoder(final ZlibWrapper wrapper, final byte[] dictionary, final boolean decompressConcatenated, final int maxAllocation) {
        super(maxAllocation);
        this.gzipState = GzipState.HEADER_START;
        this.flags = -1;
        this.xlen = -1;
        ObjectUtil.checkNotNull(wrapper, "wrapper");
        this.decompressConcatenated = decompressConcatenated;
        switch (wrapper) {
            case GZIP: {
                this.inflater = new Inflater(true);
                this.crc = ByteBufChecksum.wrapChecksum(new CRC32());
                break;
            }
            case NONE: {
                this.inflater = new Inflater(true);
                this.crc = null;
                break;
            }
            case ZLIB: {
                this.inflater = new Inflater();
                this.crc = null;
                break;
            }
            case ZLIB_OR_NONE: {
                this.decideZlibOrNone = true;
                this.crc = null;
                break;
            }
            default: {
                throw new IllegalArgumentException("Only GZIP or ZLIB is supported, but you used " + wrapper);
            }
        }
        this.dictionary = dictionary;
    }
    
    @Override
    public boolean isClosed() {
        return this.finished;
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        if (this.finished) {
            in.skipBytes(in.readableBytes());
            return;
        }
        int readableBytes = in.readableBytes();
        if (readableBytes == 0) {
            return;
        }
        if (this.decideZlibOrNone) {
            if (readableBytes < 2) {
                return;
            }
            final boolean nowrap = !looksLikeZlib(in.getShort(in.readerIndex()));
            this.inflater = new Inflater(nowrap);
            this.decideZlibOrNone = false;
        }
        if (this.crc != null && this.gzipState != GzipState.HEADER_END) {
            if (this.gzipState == GzipState.FOOTER_START) {
                if (!this.handleGzipFooter(in)) {
                    return;
                }
                assert this.gzipState == GzipState.HEADER_START;
            }
            if (!this.readGZIPHeader(in)) {
                return;
            }
            readableBytes = in.readableBytes();
            if (readableBytes == 0) {
                return;
            }
        }
        if (this.inflater.needsInput()) {
            if (in.hasArray()) {
                this.inflater.setInput(in.array(), in.arrayOffset() + in.readerIndex(), readableBytes);
            }
            else {
                final byte[] array = new byte[readableBytes];
                in.getBytes(in.readerIndex(), array);
                this.inflater.setInput(array);
            }
        }
        ByteBuf decompressed = this.prepareDecompressBuffer(ctx, null, this.inflater.getRemaining() << 1);
        try {
            boolean readFooter = false;
            while (!this.inflater.needsInput()) {
                final byte[] outArray = decompressed.array();
                final int writerIndex = decompressed.writerIndex();
                final int outIndex = decompressed.arrayOffset() + writerIndex;
                final int writable = decompressed.writableBytes();
                final int outputLength = this.inflater.inflate(outArray, outIndex, writable);
                if (outputLength > 0) {
                    decompressed.writerIndex(writerIndex + outputLength);
                    if (this.crc != null) {
                        this.crc.update(outArray, outIndex, outputLength);
                    }
                }
                else if (this.inflater.needsDictionary()) {
                    if (this.dictionary == null) {
                        throw new DecompressionException("decompression failure, unable to set dictionary as non was specified");
                    }
                    this.inflater.setDictionary(this.dictionary);
                }
                if (this.inflater.finished()) {
                    if (this.crc == null) {
                        this.finished = true;
                        break;
                    }
                    readFooter = true;
                    break;
                }
                else {
                    decompressed = this.prepareDecompressBuffer(ctx, decompressed, this.inflater.getRemaining() << 1);
                }
            }
            in.skipBytes(readableBytes - this.inflater.getRemaining());
            if (readFooter) {
                this.gzipState = GzipState.FOOTER_START;
                this.handleGzipFooter(in);
            }
        }
        catch (final DataFormatException e) {
            throw new DecompressionException("decompression failure", e);
        }
        finally {
            if (decompressed.isReadable()) {
                out.add(decompressed);
            }
            else {
                decompressed.release();
            }
        }
    }
    
    private boolean handleGzipFooter(final ByteBuf in) {
        if (this.readGZIPFooter(in) && !(this.finished = !this.decompressConcatenated)) {
            this.inflater.reset();
            this.crc.reset();
            this.gzipState = GzipState.HEADER_START;
            return true;
        }
        return false;
    }
    
    @Override
    protected void decompressionBufferExhausted(final ByteBuf buffer) {
        this.finished = true;
    }
    
    @Override
    protected void handlerRemoved0(final ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved0(ctx);
        if (this.inflater != null) {
            this.inflater.end();
        }
    }
    
    private boolean readGZIPHeader(final ByteBuf in) {
        switch (this.gzipState) {
            case HEADER_START: {
                if (in.readableBytes() < 10) {
                    return false;
                }
                final int magic0 = in.readByte();
                final int magic2 = in.readByte();
                if (magic0 != 31) {
                    throw new DecompressionException("Input is not in the GZIP format");
                }
                this.crc.update(magic0);
                this.crc.update(magic2);
                final int method = in.readUnsignedByte();
                if (method != 8) {
                    throw new DecompressionException("Unsupported compression method " + method + " in the GZIP header");
                }
                this.crc.update(method);
                this.flags = in.readUnsignedByte();
                this.crc.update(this.flags);
                if ((this.flags & 0xE0) != 0x0) {
                    throw new DecompressionException("Reserved flags are set in the GZIP header");
                }
                this.crc.update(in, in.readerIndex(), 4);
                in.skipBytes(4);
                this.crc.update(in.readUnsignedByte());
                this.crc.update(in.readUnsignedByte());
                this.gzipState = GzipState.FLG_READ;
            }
            case FLG_READ: {
                if ((this.flags & 0x4) != 0x0) {
                    if (in.readableBytes() < 2) {
                        return false;
                    }
                    final int xlen1 = in.readUnsignedByte();
                    final int xlen2 = in.readUnsignedByte();
                    this.crc.update(xlen1);
                    this.crc.update(xlen2);
                    this.xlen |= (xlen1 << 8 | xlen2);
                }
                this.gzipState = GzipState.XLEN_READ;
            }
            case XLEN_READ: {
                if (this.xlen != -1) {
                    if (in.readableBytes() < this.xlen) {
                        return false;
                    }
                    this.crc.update(in, in.readerIndex(), this.xlen);
                    in.skipBytes(this.xlen);
                }
                this.gzipState = GzipState.SKIP_FNAME;
            }
            case SKIP_FNAME: {
                if (!this.skipIfNeeded(in, 8)) {
                    return false;
                }
                this.gzipState = GzipState.SKIP_COMMENT;
            }
            case SKIP_COMMENT: {
                if (!this.skipIfNeeded(in, 16)) {
                    return false;
                }
                this.gzipState = GzipState.PROCESS_FHCRC;
            }
            case PROCESS_FHCRC: {
                if ((this.flags & 0x2) != 0x0 && !this.verifyCrc(in)) {
                    return false;
                }
                this.crc.reset();
                this.gzipState = GzipState.HEADER_END;
                return true;
            }
            case HEADER_END: {
                return true;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    private boolean skipIfNeeded(final ByteBuf in, final int flagMask) {
        if ((this.flags & flagMask) != 0x0) {
            while (in.isReadable()) {
                final int b = in.readUnsignedByte();
                this.crc.update(b);
                if (b == 0) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    private boolean readGZIPFooter(final ByteBuf in) {
        if (in.readableBytes() < 8) {
            return false;
        }
        final boolean enoughData = this.verifyCrc(in);
        assert enoughData;
        int dataLength = 0;
        for (int i = 0; i < 4; ++i) {
            dataLength |= in.readUnsignedByte() << i * 8;
        }
        final int readLength = this.inflater.getTotalOut();
        if (dataLength != readLength) {
            throw new DecompressionException("Number of bytes mismatch. Expected: " + dataLength + ", Got: " + readLength);
        }
        return true;
    }
    
    private boolean verifyCrc(final ByteBuf in) {
        if (in.readableBytes() < 4) {
            return false;
        }
        long crcValue = 0L;
        for (int i = 0; i < 4; ++i) {
            crcValue |= (long)in.readUnsignedByte() << i * 8;
        }
        final long readCrc = this.crc.getValue();
        if (crcValue != readCrc) {
            throw new DecompressionException("CRC value mismatch. Expected: " + crcValue + ", Got: " + readCrc);
        }
        return true;
    }
    
    private static boolean looksLikeZlib(final short cmf_flg) {
        return (cmf_flg & 0x7800) == 0x7800 && cmf_flg % 31 == 0;
    }
    
    private enum GzipState
    {
        HEADER_START, 
        HEADER_END, 
        FLG_READ, 
        XLEN_READ, 
        SKIP_FNAME, 
        SKIP_COMMENT, 
        PROCESS_FHCRC, 
        FOOTER_START;
    }
}
