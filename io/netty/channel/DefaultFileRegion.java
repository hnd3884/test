package io.netty.channel;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.ReferenceCounted;
import io.netty.util.IllegalReferenceCountException;
import java.nio.channels.WritableByteChannel;
import java.io.IOException;
import java.io.RandomAccessFile;
import io.netty.util.internal.ObjectUtil;
import java.nio.channels.FileChannel;
import java.io.File;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.AbstractReferenceCounted;

public class DefaultFileRegion extends AbstractReferenceCounted implements FileRegion
{
    private static final InternalLogger logger;
    private final File f;
    private final long position;
    private final long count;
    private long transferred;
    private FileChannel file;
    
    public DefaultFileRegion(final FileChannel file, final long position, final long count) {
        this.file = ObjectUtil.checkNotNull(file, "file");
        this.position = ObjectUtil.checkPositiveOrZero(position, "position");
        this.count = ObjectUtil.checkPositiveOrZero(count, "count");
        this.f = null;
    }
    
    public DefaultFileRegion(final File f, final long position, final long count) {
        this.f = ObjectUtil.checkNotNull(f, "f");
        this.position = ObjectUtil.checkPositiveOrZero(position, "position");
        this.count = ObjectUtil.checkPositiveOrZero(count, "count");
    }
    
    public boolean isOpen() {
        return this.file != null;
    }
    
    public void open() throws IOException {
        if (!this.isOpen() && this.refCnt() > 0) {
            this.file = new RandomAccessFile(this.f, "r").getChannel();
        }
    }
    
    @Override
    public long position() {
        return this.position;
    }
    
    @Override
    public long count() {
        return this.count;
    }
    
    @Deprecated
    @Override
    public long transfered() {
        return this.transferred;
    }
    
    @Override
    public long transferred() {
        return this.transferred;
    }
    
    @Override
    public long transferTo(final WritableByteChannel target, final long position) throws IOException {
        final long count = this.count - position;
        if (count < 0L || position < 0L) {
            throw new IllegalArgumentException("position out of range: " + position + " (expected: 0 - " + (this.count - 1L) + ')');
        }
        if (count == 0L) {
            return 0L;
        }
        if (this.refCnt() == 0) {
            throw new IllegalReferenceCountException(0);
        }
        this.open();
        final long written = this.file.transferTo(this.position + position, count, target);
        if (written > 0L) {
            this.transferred += written;
        }
        else if (written == 0L) {
            validate(this, position);
        }
        return written;
    }
    
    @Override
    protected void deallocate() {
        final FileChannel file = this.file;
        if (file == null) {
            return;
        }
        this.file = null;
        try {
            file.close();
        }
        catch (final IOException e) {
            DefaultFileRegion.logger.warn("Failed to close a file.", e);
        }
    }
    
    @Override
    public FileRegion retain() {
        super.retain();
        return this;
    }
    
    @Override
    public FileRegion retain(final int increment) {
        super.retain(increment);
        return this;
    }
    
    @Override
    public FileRegion touch() {
        return this;
    }
    
    @Override
    public FileRegion touch(final Object hint) {
        return this;
    }
    
    static void validate(final DefaultFileRegion region, final long position) throws IOException {
        final long size = region.file.size();
        final long count = region.count - position;
        if (region.position + count + position > size) {
            throw new IOException("Underlying file size " + size + " smaller then requested count " + region.count);
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(DefaultFileRegion.class);
    }
}
