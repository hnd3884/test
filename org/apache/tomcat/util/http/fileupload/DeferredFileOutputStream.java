package org.apache.tomcat.util.http.fileupload;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.OutputStream;

public class DeferredFileOutputStream extends ThresholdingOutputStream
{
    private ByteArrayOutputStream memoryOutputStream;
    private OutputStream currentOutputStream;
    private File outputFile;
    private final String prefix;
    private final String suffix;
    private final File directory;
    
    public DeferredFileOutputStream(final int threshold, final File outputFile) {
        this(threshold, outputFile, null, null, null, 1024);
    }
    
    private DeferredFileOutputStream(final int threshold, final File outputFile, final String prefix, final String suffix, final File directory, final int initialBufferSize) {
        super(threshold);
        this.outputFile = outputFile;
        this.prefix = prefix;
        this.suffix = suffix;
        this.directory = directory;
        this.memoryOutputStream = new ByteArrayOutputStream(initialBufferSize);
        this.currentOutputStream = this.memoryOutputStream;
    }
    
    @Override
    protected OutputStream getStream() throws IOException {
        return this.currentOutputStream;
    }
    
    @Override
    protected void thresholdReached() throws IOException {
        if (this.prefix != null) {
            this.outputFile = File.createTempFile(this.prefix, this.suffix, this.directory);
        }
        FileUtils.forceMkdirParent(this.outputFile);
        final FileOutputStream fos = new FileOutputStream(this.outputFile);
        try {
            this.memoryOutputStream.writeTo(fos);
        }
        catch (final IOException e) {
            fos.close();
            throw e;
        }
        this.currentOutputStream = fos;
        this.memoryOutputStream = null;
    }
    
    public boolean isInMemory() {
        return !this.isThresholdExceeded();
    }
    
    public byte[] getData() {
        if (this.memoryOutputStream != null) {
            return this.memoryOutputStream.toByteArray();
        }
        return null;
    }
    
    public File getFile() {
        return this.outputFile;
    }
    
    @Override
    public void close() throws IOException {
        super.close();
    }
}
