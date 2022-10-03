package com.sun.xml.internal.org.jvnet.mimepull;

import java.util.logging.Level;
import java.io.File;
import java.util.logging.Logger;

public class MIMEConfig
{
    private static final int DEFAULT_CHUNK_SIZE = 8192;
    private static final long DEFAULT_MEMORY_THRESHOLD = 1048576L;
    private static final String DEFAULT_FILE_PREFIX = "MIME";
    private static final Logger LOGGER;
    boolean parseEagerly;
    int chunkSize;
    long memoryThreshold;
    File tempDir;
    String prefix;
    String suffix;
    
    private MIMEConfig(final boolean parseEagerly, final int chunkSize, final long inMemoryThreshold, final String dir, final String prefix, final String suffix) {
        this.parseEagerly = parseEagerly;
        this.chunkSize = chunkSize;
        this.memoryThreshold = inMemoryThreshold;
        this.prefix = prefix;
        this.suffix = suffix;
        this.setDir(dir);
    }
    
    public MIMEConfig() {
        this(false, 8192, 1048576L, null, "MIME", null);
    }
    
    boolean isParseEagerly() {
        return this.parseEagerly;
    }
    
    public void setParseEagerly(final boolean parseEagerly) {
        this.parseEagerly = parseEagerly;
    }
    
    int getChunkSize() {
        return this.chunkSize;
    }
    
    void setChunkSize(final int chunkSize) {
        this.chunkSize = chunkSize;
    }
    
    long getMemoryThreshold() {
        return this.memoryThreshold;
    }
    
    public void setMemoryThreshold(final long memoryThreshold) {
        this.memoryThreshold = memoryThreshold;
    }
    
    boolean isOnlyMemory() {
        return this.memoryThreshold == -1L;
    }
    
    File getTempDir() {
        return this.tempDir;
    }
    
    String getTempFilePrefix() {
        return this.prefix;
    }
    
    String getTempFileSuffix() {
        return this.suffix;
    }
    
    public final void setDir(final String dir) {
        if (this.tempDir == null && dir != null && !dir.equals("")) {
            this.tempDir = new File(dir);
        }
    }
    
    public void validate() {
        if (!this.isOnlyMemory()) {
            try {
                final File tempFile = (this.tempDir == null) ? File.createTempFile(this.prefix, this.suffix) : File.createTempFile(this.prefix, this.suffix, this.tempDir);
                final boolean deleted = tempFile.delete();
                if (!deleted && MIMEConfig.LOGGER.isLoggable(Level.INFO)) {
                    MIMEConfig.LOGGER.log(Level.INFO, "File {0} was not deleted", tempFile.getAbsolutePath());
                }
            }
            catch (final Exception ioe) {
                this.memoryThreshold = -1L;
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(MIMEConfig.class.getName());
    }
}
