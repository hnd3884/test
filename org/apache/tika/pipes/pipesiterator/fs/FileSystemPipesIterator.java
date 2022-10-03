package org.apache.tika.pipes.pipesiterator.fs;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.pipes.emitter.EmitKey;
import org.apache.tika.pipes.fetcher.FetchKey;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.tika.pipes.FetchEmitTuple;
import org.apache.tika.pipes.HandlerConfig;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.config.InitializableProblemHandler;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import org.apache.tika.config.Field;
import java.nio.file.Paths;
import java.nio.file.Path;
import org.apache.tika.config.Initializable;
import org.apache.tika.pipes.pipesiterator.PipesIterator;

public class FileSystemPipesIterator extends PipesIterator implements Initializable
{
    private Path basePath;
    
    public FileSystemPipesIterator() {
    }
    
    public FileSystemPipesIterator(final Path basePath) {
        this.basePath = basePath;
    }
    
    @Field
    public void setBasePath(final String basePath) {
        this.basePath = Paths.get(basePath, new String[0]);
    }
    
    @Override
    protected void enqueue() throws InterruptedException, IOException, TimeoutException {
        if (!Files.isDirectory(this.basePath, new LinkOption[0])) {
            throw new IllegalArgumentException("\"basePath\" directory does not exist: " + this.basePath.toAbsolutePath());
        }
        try {
            Files.walkFileTree(this.basePath, new FSFileVisitor(this.getFetcherName(), this.getEmitterName()));
        }
        catch (final IOException e) {
            final Throwable cause = e.getCause();
            if (cause != null && cause instanceof TimeoutException) {
                throw (TimeoutException)cause;
            }
            throw e;
        }
    }
    
    @Override
    public void checkInitialization(final InitializableProblemHandler problemHandler) throws TikaConfigException {
        TikaConfig.mustNotBeEmpty("basePath", this.basePath);
        TikaConfig.mustNotBeEmpty("fetcherName", this.getFetcherName());
        TikaConfig.mustNotBeEmpty("emitterName", this.getFetcherName());
    }
    
    private class FSFileVisitor implements FileVisitor<Path>
    {
        private final String fetcherName;
        private final String emitterName;
        
        private FSFileVisitor(final String fetcherName, final String emitterName) {
            this.fetcherName = fetcherName;
            this.emitterName = emitterName;
        }
        
        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }
        
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            final String relPath = FileSystemPipesIterator.this.basePath.relativize(file).toString();
            try {
                PipesIterator.this.tryToAdd(new FetchEmitTuple(relPath, new FetchKey(this.fetcherName, relPath), new EmitKey(this.emitterName, relPath), new Metadata(), PipesIterator.this.getHandlerConfig(), FileSystemPipesIterator.this.getOnParseException()));
            }
            catch (final TimeoutException e) {
                throw new IOException(e);
            }
            catch (final InterruptedException e2) {
                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }
        
        @Override
        public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
        
        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }
}
