package org.apache.lucene.analysis.util;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.io.FileNotFoundException;
import java.nio.file.OpenOption;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public final class FilesystemResourceLoader implements ResourceLoader
{
    private final Path baseDirectory;
    private final ResourceLoader delegate;
    
    public FilesystemResourceLoader(final Path baseDirectory) {
        this(baseDirectory, new ClasspathResourceLoader());
    }
    
    public FilesystemResourceLoader(final Path baseDirectory, final ResourceLoader delegate) {
        if (baseDirectory == null) {
            throw new NullPointerException();
        }
        if (!Files.isDirectory(baseDirectory, new LinkOption[0])) {
            throw new IllegalArgumentException(baseDirectory + " is not a directory");
        }
        if (delegate == null) {
            throw new IllegalArgumentException("delegate ResourceLoader may not be null");
        }
        this.baseDirectory = baseDirectory;
        this.delegate = delegate;
    }
    
    @Override
    public InputStream openResource(final String resource) throws IOException {
        try {
            return Files.newInputStream(this.baseDirectory.resolve(resource), new OpenOption[0]);
        }
        catch (final FileNotFoundException | NoSuchFileException fnfe) {
            return this.delegate.openResource(resource);
        }
    }
    
    @Override
    public <T> T newInstance(final String cname, final Class<T> expectedType) {
        return this.delegate.newInstance(cname, expectedType);
    }
    
    @Override
    public <T> Class<? extends T> findClass(final String cname, final Class<T> expectedType) {
        return this.delegate.findClass(cname, expectedType);
    }
}
