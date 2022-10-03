package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

final class MemoryData implements Data
{
    private static final Logger LOGGER;
    private final byte[] data;
    private final int len;
    private final MIMEConfig config;
    
    MemoryData(final ByteBuffer buf, final MIMEConfig config) {
        this.data = buf.array();
        this.len = buf.limit();
        this.config = config;
    }
    
    @Override
    public int size() {
        return this.len;
    }
    
    @Override
    public byte[] read() {
        return this.data;
    }
    
    @Override
    public long writeTo(final DataFile file) {
        return file.writeTo(this.data, 0, this.len);
    }
    
    @Override
    public Data createNext(final DataHead dataHead, final ByteBuffer buf) {
        if (!this.config.isOnlyMemory() && dataHead.inMemory >= this.config.memoryThreshold) {
            try {
                final String prefix = this.config.getTempFilePrefix();
                final String suffix = this.config.getTempFileSuffix();
                final File tempFile = TempFiles.createTempFile(prefix, suffix, this.config.getTempDir());
                tempFile.deleteOnExit();
                if (MemoryData.LOGGER.isLoggable(Level.FINE)) {
                    MemoryData.LOGGER.log(Level.FINE, "Created temp file = {0}", tempFile);
                }
                tempFile.deleteOnExit();
                if (MemoryData.LOGGER.isLoggable(Level.FINE)) {
                    MemoryData.LOGGER.log(Level.FINE, "Created temp file = {0}", tempFile);
                }
                dataHead.dataFile = new DataFile(tempFile);
            }
            catch (final IOException ioe) {
                throw new MIMEParsingException(ioe);
            }
            if (dataHead.head != null) {
                for (Chunk c = dataHead.head; c != null; c = c.next) {
                    final long pointer = c.data.writeTo(dataHead.dataFile);
                    c.data = new FileData(dataHead.dataFile, pointer, this.len);
                }
            }
            return new FileData(dataHead.dataFile, buf);
        }
        return new MemoryData(buf, this.config);
    }
    
    static {
        LOGGER = Logger.getLogger(MemoryData.class.getName());
    }
}
