package org.apache.tika.pipes.fetcher.fs;

import org.apache.tika.config.InitializableProblemHandler;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.config.Param;
import java.util.Map;
import org.apache.tika.config.Field;
import java.nio.file.Paths;
import org.apache.tika.exception.TikaException;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.tika.io.TikaInputStream;
import java.nio.file.Files;
import org.apache.tika.metadata.TikaCoreProperties;
import java.nio.file.LinkOption;
import java.io.InputStream;
import org.apache.tika.metadata.Metadata;
import java.nio.file.Path;
import org.apache.tika.config.Initializable;
import org.apache.tika.pipes.fetcher.AbstractFetcher;

public class FileSystemFetcher extends AbstractFetcher implements Initializable
{
    private Path basePath;
    
    public FileSystemFetcher() {
        this.basePath = null;
    }
    
    static boolean isDescendant(final Path root, final Path descendant) {
        return descendant.toAbsolutePath().normalize().startsWith(root.toAbsolutePath().normalize());
    }
    
    @Override
    public InputStream fetch(final String fetchKey, final Metadata metadata) throws IOException, TikaException {
        if (this.basePath == null) {
            throw new IllegalStateException("must set 'basePath' before calling fetch");
        }
        if (fetchKey.contains("\u0000")) {
            throw new IllegalArgumentException("Path must not contain \u0000. Please review the life decisions that led you to requesting a file name with this character in it.");
        }
        final Path p = this.basePath.resolve(fetchKey);
        if (!p.toRealPath(new LinkOption[0]).startsWith(this.basePath.toRealPath(new LinkOption[0]))) {
            throw new IllegalArgumentException("fetchKey must resolve to be a descendant of the 'basePath'");
        }
        metadata.set(TikaCoreProperties.SOURCE_PATH, fetchKey);
        if (Files.isRegularFile(p, new LinkOption[0])) {
            return (InputStream)TikaInputStream.get(p, metadata);
        }
        if (!Files.isDirectory(this.basePath, new LinkOption[0])) {
            throw new IOException("BasePath is not a directory: " + this.basePath);
        }
        throw new FileNotFoundException(p.toAbsolutePath().toString());
    }
    
    public Path getBasePath() {
        return this.basePath;
    }
    
    @Field
    public void setBasePath(final String basePath) {
        this.basePath = Paths.get(basePath, new String[0]);
    }
    
    @Override
    public void initialize(final Map<String, Param> params) throws TikaConfigException {
    }
    
    @Override
    public void checkInitialization(final InitializableProblemHandler problemHandler) throws TikaConfigException {
        if (this.basePath == null || this.basePath.toString().trim().length() == 0) {
            throw new TikaConfigException("'basePath' must be specified");
        }
        if (this.basePath.toString().startsWith("http://")) {
            throw new TikaConfigException("FileSystemFetcher only works with local file systems.  Please use the tika-fetcher-http module for http calls");
        }
        if (this.basePath.toString().startsWith("ftp://")) {
            throw new TikaConfigException("FileSystemFetcher only works with local file systems.  Please consider contributing an ftp fetcher module");
        }
        if (this.basePath.toString().startsWith("s3://")) {
            throw new TikaConfigException("FileSystemFetcher only works with local file systems.  Please use the tika-fetcher-s3 module");
        }
        if (this.basePath.toAbsolutePath().toString().contains("\u0000")) {
            throw new TikaConfigException("base path must not contain \u0000. Seriously, what were you thinking?");
        }
    }
}
