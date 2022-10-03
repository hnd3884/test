package org.apache.commons.compress.parallel;

import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.io.OutputStream;
import java.io.File;

public class FileBasedScatterGatherBackingStore implements ScatterGatherBackingStore
{
    private final File target;
    private final OutputStream os;
    private boolean closed;
    
    public FileBasedScatterGatherBackingStore(final File target) throws FileNotFoundException {
        this.target = target;
        try {
            this.os = Files.newOutputStream(target.toPath(), new OpenOption[0]);
        }
        catch (final FileNotFoundException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw new RuntimeException(ex2);
        }
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(this.target.toPath(), new OpenOption[0]);
    }
    
    @Override
    public void closeForWriting() throws IOException {
        if (!this.closed) {
            this.os.close();
            this.closed = true;
        }
    }
    
    @Override
    public void writeOut(final byte[] data, final int offset, final int length) throws IOException {
        this.os.write(data, offset, length);
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.closeForWriting();
        }
        finally {
            if (this.target.exists() && !this.target.delete()) {
                this.target.deleteOnExit();
            }
        }
    }
}
