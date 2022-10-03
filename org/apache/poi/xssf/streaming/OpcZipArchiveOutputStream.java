package org.apache.poi.xssf.streaming;

import java.io.IOException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

class OpcZipArchiveOutputStream extends ZipArchiveOutputStream
{
    private final OpcOutputStream out;
    
    OpcZipArchiveOutputStream(final OutputStream out) {
        super(out);
        this.out = new OpcOutputStream(out);
    }
    
    public void setLevel(final int level) {
        this.out.setLevel(level);
    }
    
    public void putArchiveEntry(final ArchiveEntry archiveEntry) throws IOException {
        this.out.putNextEntry(archiveEntry.getName());
    }
    
    public void closeArchiveEntry() throws IOException {
        this.out.closeEntry();
    }
    
    public void finish() throws IOException {
        this.out.finish();
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.out.write(b, off, len);
    }
    
    public void close() throws IOException {
        this.out.close();
    }
    
    public void write(final int b) throws IOException {
        this.out.write(b);
    }
    
    public void flush() throws IOException {
        this.out.flush();
    }
    
    public void write(final byte[] b) throws IOException {
        this.out.write(b);
    }
}
