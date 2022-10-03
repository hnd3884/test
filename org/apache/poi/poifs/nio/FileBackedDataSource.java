package org.apache.poi.poifs.nio;

import org.apache.poi.util.POILogFactory;
import java.util.Iterator;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.Channels;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import org.apache.poi.util.IOUtils;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import org.apache.poi.util.POILogger;

public class FileBackedDataSource extends DataSource
{
    private static final POILogger logger;
    private FileChannel channel;
    private boolean writable;
    private RandomAccessFile srcFile;
    private List<ByteBuffer> buffersToClean;
    
    public FileBackedDataSource(final File file) throws FileNotFoundException {
        this(newSrcFile(file, "r"), true);
    }
    
    public FileBackedDataSource(final File file, final boolean readOnly) throws FileNotFoundException {
        this(newSrcFile(file, readOnly ? "r" : "rw"), readOnly);
    }
    
    public FileBackedDataSource(final RandomAccessFile srcFile, final boolean readOnly) {
        this(srcFile.getChannel(), readOnly);
        this.srcFile = srcFile;
    }
    
    public FileBackedDataSource(final FileChannel channel, final boolean readOnly) {
        this.buffersToClean = new ArrayList<ByteBuffer>();
        this.channel = channel;
        this.writable = !readOnly;
    }
    
    public boolean isWriteable() {
        return this.writable;
    }
    
    public FileChannel getChannel() {
        return this.channel;
    }
    
    @Override
    public ByteBuffer read(final int length, final long position) throws IOException {
        if (position >= this.size()) {
            throw new IndexOutOfBoundsException("Position " + position + " past the end of the file");
        }
        ByteBuffer dst;
        if (this.writable) {
            dst = this.channel.map(FileChannel.MapMode.READ_WRITE, position, length);
            this.buffersToClean.add(dst);
        }
        else {
            this.channel.position(position);
            dst = ByteBuffer.allocate(length);
            final int worked = IOUtils.readFully(this.channel, dst);
            if (worked == -1) {
                throw new IndexOutOfBoundsException("Position " + position + " past the end of the file");
            }
        }
        dst.position(0);
        return dst;
    }
    
    @Override
    public void write(final ByteBuffer src, final long position) throws IOException {
        this.channel.write(src, position);
    }
    
    @Override
    public void copyTo(final OutputStream stream) throws IOException {
        try (final WritableByteChannel out = Channels.newChannel(stream)) {
            this.channel.transferTo(0L, this.channel.size(), out);
        }
    }
    
    @Override
    public long size() throws IOException {
        return this.channel.size();
    }
    
    @Override
    public void close() throws IOException {
        for (final ByteBuffer buffer : this.buffersToClean) {
            unmap(buffer);
        }
        this.buffersToClean.clear();
        if (this.srcFile != null) {
            this.srcFile.close();
        }
        else {
            this.channel.close();
        }
    }
    
    private static RandomAccessFile newSrcFile(final File file, final String mode) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.toString());
        }
        return new RandomAccessFile(file, mode);
    }
    
    private static void unmap(final ByteBuffer buffer) {
        if (buffer.getClass().getName().endsWith("HeapByteBuffer")) {
            return;
        }
        if (CleanerUtil.UNMAP_SUPPORTED) {
            try {
                CleanerUtil.getCleaner().freeBuffer(buffer);
            }
            catch (final IOException e) {
                FileBackedDataSource.logger.log(5, "Failed to unmap the buffer", e);
            }
        }
        else {
            FileBackedDataSource.logger.log(1, CleanerUtil.UNMAP_NOT_SUPPORTED_REASON);
        }
    }
    
    static {
        logger = POILogFactory.getLogger(FileBackedDataSource.class);
    }
}
