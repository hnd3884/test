package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBufHolder;
import io.netty.util.ReferenceCounted;
import io.netty.handler.codec.http.HttpConstants;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.io.RandomAccessFile;
import java.io.File;
import io.netty.buffer.CompositeByteBuf;
import java.io.InputStream;
import java.io.IOException;
import io.netty.util.internal.ObjectUtil;
import io.netty.buffer.Unpooled;
import java.nio.charset.Charset;
import io.netty.buffer.ByteBuf;

public abstract class AbstractMemoryHttpData extends AbstractHttpData
{
    private ByteBuf byteBuf;
    private int chunkPosition;
    
    protected AbstractMemoryHttpData(final String name, final Charset charset, final long size) {
        super(name, charset, size);
        this.byteBuf = Unpooled.EMPTY_BUFFER;
    }
    
    @Override
    public void setContent(final ByteBuf buffer) throws IOException {
        ObjectUtil.checkNotNull(buffer, "buffer");
        final long localsize = buffer.readableBytes();
        try {
            this.checkSize(localsize);
        }
        catch (final IOException e) {
            buffer.release();
            throw e;
        }
        if (this.definedSize > 0L && this.definedSize < localsize) {
            buffer.release();
            throw new IOException("Out of size: " + localsize + " > " + this.definedSize);
        }
        if (this.byteBuf != null) {
            this.byteBuf.release();
        }
        this.byteBuf = buffer;
        this.size = localsize;
        this.setCompleted();
    }
    
    @Override
    public void setContent(final InputStream inputStream) throws IOException {
        ObjectUtil.checkNotNull(inputStream, "inputStream");
        final byte[] bytes = new byte[16384];
        final ByteBuf buffer = Unpooled.buffer();
        int written = 0;
        try {
            for (int read = inputStream.read(bytes); read > 0; read = inputStream.read(bytes)) {
                buffer.writeBytes(bytes, 0, read);
                written += read;
                this.checkSize(written);
            }
        }
        catch (final IOException e) {
            buffer.release();
            throw e;
        }
        this.size = written;
        if (this.definedSize > 0L && this.definedSize < this.size) {
            buffer.release();
            throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
        }
        if (this.byteBuf != null) {
            this.byteBuf.release();
        }
        this.byteBuf = buffer;
        this.setCompleted();
    }
    
    @Override
    public void addContent(final ByteBuf buffer, final boolean last) throws IOException {
        if (buffer != null) {
            final long localsize = buffer.readableBytes();
            try {
                this.checkSize(this.size + localsize);
            }
            catch (final IOException e) {
                buffer.release();
                throw e;
            }
            if (this.definedSize > 0L && this.definedSize < this.size + localsize) {
                buffer.release();
                throw new IOException("Out of size: " + (this.size + localsize) + " > " + this.definedSize);
            }
            this.size += localsize;
            if (this.byteBuf == null) {
                this.byteBuf = buffer;
            }
            else if (localsize == 0L) {
                buffer.release();
            }
            else if (this.byteBuf.readableBytes() == 0) {
                this.byteBuf.release();
                this.byteBuf = buffer;
            }
            else if (this.byteBuf instanceof CompositeByteBuf) {
                final CompositeByteBuf cbb = (CompositeByteBuf)this.byteBuf;
                cbb.addComponent(true, buffer);
            }
            else {
                final CompositeByteBuf cbb = Unpooled.compositeBuffer(Integer.MAX_VALUE);
                cbb.addComponents(true, this.byteBuf, buffer);
                this.byteBuf = cbb;
            }
        }
        if (last) {
            this.setCompleted();
        }
        else {
            ObjectUtil.checkNotNull(buffer, "buffer");
        }
    }
    
    @Override
    public void setContent(final File file) throws IOException {
        ObjectUtil.checkNotNull(file, "file");
        final long newsize = file.length();
        if (newsize > 2147483647L) {
            throw new IllegalArgumentException("File too big to be loaded in memory");
        }
        this.checkSize(newsize);
        final RandomAccessFile accessFile = new RandomAccessFile(file, "r");
        ByteBuffer byteBuffer;
        try {
            final FileChannel fileChannel = accessFile.getChannel();
            try {
                final byte[] array = new byte[(int)newsize];
                byteBuffer = ByteBuffer.wrap(array);
                for (int read = 0; read < newsize; read += fileChannel.read(byteBuffer)) {}
            }
            finally {
                fileChannel.close();
            }
        }
        finally {
            accessFile.close();
        }
        byteBuffer.flip();
        if (this.byteBuf != null) {
            this.byteBuf.release();
        }
        this.byteBuf = Unpooled.wrappedBuffer(Integer.MAX_VALUE, byteBuffer);
        this.size = newsize;
        this.setCompleted();
    }
    
    @Override
    public void delete() {
        if (this.byteBuf != null) {
            this.byteBuf.release();
            this.byteBuf = null;
        }
    }
    
    @Override
    public byte[] get() {
        if (this.byteBuf == null) {
            return Unpooled.EMPTY_BUFFER.array();
        }
        final byte[] array = new byte[this.byteBuf.readableBytes()];
        this.byteBuf.getBytes(this.byteBuf.readerIndex(), array);
        return array;
    }
    
    @Override
    public String getString() {
        return this.getString(HttpConstants.DEFAULT_CHARSET);
    }
    
    @Override
    public String getString(Charset encoding) {
        if (this.byteBuf == null) {
            return "";
        }
        if (encoding == null) {
            encoding = HttpConstants.DEFAULT_CHARSET;
        }
        return this.byteBuf.toString(encoding);
    }
    
    @Override
    public ByteBuf getByteBuf() {
        return this.byteBuf;
    }
    
    @Override
    public ByteBuf getChunk(final int length) throws IOException {
        if (this.byteBuf == null || length == 0 || this.byteBuf.readableBytes() == 0) {
            this.chunkPosition = 0;
            return Unpooled.EMPTY_BUFFER;
        }
        final int sizeLeft = this.byteBuf.readableBytes() - this.chunkPosition;
        if (sizeLeft == 0) {
            this.chunkPosition = 0;
            return Unpooled.EMPTY_BUFFER;
        }
        int sliceLength;
        if (sizeLeft < (sliceLength = length)) {
            sliceLength = sizeLeft;
        }
        final ByteBuf chunk = this.byteBuf.retainedSlice(this.chunkPosition, sliceLength);
        this.chunkPosition += sliceLength;
        return chunk;
    }
    
    @Override
    public boolean isInMemory() {
        return true;
    }
    
    @Override
    public boolean renameTo(final File dest) throws IOException {
        ObjectUtil.checkNotNull(dest, "dest");
        if (this.byteBuf != null) {
            final int length = this.byteBuf.readableBytes();
            long written = 0L;
            final RandomAccessFile accessFile = new RandomAccessFile(dest, "rw");
            try {
                final FileChannel fileChannel = accessFile.getChannel();
                try {
                    if (this.byteBuf.nioBufferCount() == 1) {
                        for (ByteBuffer byteBuffer = this.byteBuf.nioBuffer(); written < length; written += fileChannel.write(byteBuffer)) {}
                    }
                    else {
                        for (ByteBuffer[] byteBuffers = this.byteBuf.nioBuffers(); written < length; written += fileChannel.write(byteBuffers)) {}
                    }
                    fileChannel.force(false);
                }
                finally {
                    fileChannel.close();
                }
            }
            finally {
                accessFile.close();
            }
            return written == length;
        }
        if (!dest.createNewFile()) {
            throw new IOException("file exists already: " + dest);
        }
        return true;
    }
    
    @Override
    public File getFile() throws IOException {
        throw new IOException("Not represented by a file");
    }
    
    @Override
    public HttpData touch() {
        return this.touch(null);
    }
    
    @Override
    public HttpData touch(final Object hint) {
        if (this.byteBuf != null) {
            this.byteBuf.touch(hint);
        }
        return this;
    }
}
