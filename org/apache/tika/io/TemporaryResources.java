package org.apache.tika.io;

import org.slf4j.LoggerFactory;
import org.apache.tika.exception.TikaException;
import java.util.Iterator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import org.slf4j.Logger;
import java.io.Closeable;

public class TemporaryResources implements Closeable
{
    private static final Logger LOG;
    private final LinkedList<Closeable> resources;
    private Path tempFileDir;
    
    public TemporaryResources() {
        this.resources = new LinkedList<Closeable>();
        this.tempFileDir = null;
    }
    
    public void setTemporaryFileDirectory(final Path tempFileDir) {
        this.tempFileDir = tempFileDir;
    }
    
    public void setTemporaryFileDirectory(final File tempFileDir) {
        this.tempFileDir = ((tempFileDir == null) ? null : tempFileDir.toPath());
    }
    
    public Path createTempFile() throws IOException {
        final Path path = (this.tempFileDir == null) ? Files.createTempFile("apache-tika-", ".tmp", (FileAttribute<?>[])new FileAttribute[0]) : Files.createTempFile(this.tempFileDir, "apache-tika-", ".tmp", (FileAttribute<?>[])new FileAttribute[0]);
        this.addResource(() -> {
            try {
                Files.delete(path);
            }
            catch (final IOException e) {
                TemporaryResources.LOG.warn("delete tmp file fail, will delete it on exit");
                path.toFile().deleteOnExit();
            }
            return;
        });
        return path;
    }
    
    public File createTemporaryFile() throws IOException {
        return this.createTempFile().toFile();
    }
    
    public void addResource(final Closeable resource) {
        this.resources.addFirst(resource);
    }
    
    public <T extends Closeable> T getResource(final Class<T> klass) {
        for (final Closeable resource : this.resources) {
            if (klass.isAssignableFrom(resource.getClass())) {
                return (T)resource;
            }
        }
        return null;
    }
    
    @Override
    public void close() throws IOException {
        IOException exception = null;
        for (final Closeable resource : this.resources) {
            try {
                resource.close();
            }
            catch (final IOException e) {
                if (exception == null) {
                    exception = e;
                }
                else {
                    exception.addSuppressed(e);
                }
            }
        }
        this.resources.clear();
        if (exception != null) {
            throw exception;
        }
    }
    
    public void dispose() throws TikaException {
        try {
            this.close();
        }
        catch (final IOException e) {
            throw new TikaException("Failed to close temporary resources", e);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger((Class)TemporaryResources.class);
    }
}
