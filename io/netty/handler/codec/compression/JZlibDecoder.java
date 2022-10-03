package io.netty.handler.codec.compression;

import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import com.jcraft.jzlib.JZlib;
import io.netty.util.internal.ObjectUtil;
import com.jcraft.jzlib.Inflater;

public class JZlibDecoder extends ZlibDecoder
{
    private final Inflater z;
    private byte[] dictionary;
    private volatile boolean finished;
    
    public JZlibDecoder() {
        this(ZlibWrapper.ZLIB, 0);
    }
    
    public JZlibDecoder(final int maxAllocation) {
        this(ZlibWrapper.ZLIB, maxAllocation);
    }
    
    public JZlibDecoder(final ZlibWrapper wrapper) {
        this(wrapper, 0);
    }
    
    public JZlibDecoder(final ZlibWrapper wrapper, final int maxAllocation) {
        super(maxAllocation);
        this.z = new Inflater();
        ObjectUtil.checkNotNull(wrapper, "wrapper");
        final int resultCode = this.z.init(ZlibUtil.convertWrapperType(wrapper));
        if (resultCode != 0) {
            ZlibUtil.fail(this.z, "initialization failure", resultCode);
        }
    }
    
    public JZlibDecoder(final byte[] dictionary) {
        this(dictionary, 0);
    }
    
    public JZlibDecoder(final byte[] dictionary, final int maxAllocation) {
        super(maxAllocation);
        this.z = new Inflater();
        this.dictionary = ObjectUtil.checkNotNull(dictionary, "dictionary");
        final int resultCode = this.z.inflateInit(JZlib.W_ZLIB);
        if (resultCode != 0) {
            ZlibUtil.fail(this.z, "initialization failure", resultCode);
        }
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
        final int inputLength = in.readableBytes();
        if (inputLength == 0) {
            return;
        }
        try {
            this.z.avail_in = inputLength;
            if (in.hasArray()) {
                this.z.next_in = in.array();
                this.z.next_in_index = in.arrayOffset() + in.readerIndex();
            }
            else {
                final byte[] array = new byte[inputLength];
                in.getBytes(in.readerIndex(), array);
                this.z.next_in = array;
                this.z.next_in_index = 0;
            }
            final int oldNextInIndex = this.z.next_in_index;
            ByteBuf decompressed = this.prepareDecompressBuffer(ctx, null, inputLength << 1);
            try {
            Label_0392:
                while (true) {
                    decompressed = this.prepareDecompressBuffer(ctx, decompressed, this.z.avail_in << 1);
                    this.z.avail_out = decompressed.writableBytes();
                    this.z.next_out = decompressed.array();
                    this.z.next_out_index = decompressed.arrayOffset() + decompressed.writerIndex();
                    final int oldNextOutIndex = this.z.next_out_index;
                    int resultCode = this.z.inflate(2);
                    final int outputLength = this.z.next_out_index - oldNextOutIndex;
                    if (outputLength > 0) {
                        decompressed.writerIndex(decompressed.writerIndex() + outputLength);
                    }
                    switch (resultCode) {
                        case 2: {
                            if (this.dictionary == null) {
                                ZlibUtil.fail(this.z, "decompression failure", resultCode);
                                continue;
                            }
                            resultCode = this.z.inflateSetDictionary(this.dictionary, this.dictionary.length);
                            if (resultCode != 0) {
                                ZlibUtil.fail(this.z, "failed to set the dictionary", resultCode);
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            this.finished = true;
                            this.z.inflateEnd();
                            break Label_0392;
                        }
                        case 0: {
                            continue;
                        }
                        case -5: {
                            if (this.z.avail_in <= 0) {
                                break Label_0392;
                            }
                            continue;
                        }
                        default: {
                            ZlibUtil.fail(this.z, "decompression failure", resultCode);
                            continue;
                        }
                    }
                }
            }
            finally {
                in.skipBytes(this.z.next_in_index - oldNextInIndex);
                if (decompressed.isReadable()) {
                    out.add(decompressed);
                }
                else {
                    decompressed.release();
                }
            }
        }
        finally {
            this.z.next_in = null;
            this.z.next_out = null;
        }
    }
    
    @Override
    protected void decompressionBufferExhausted(final ByteBuf buffer) {
        this.finished = true;
    }
}
