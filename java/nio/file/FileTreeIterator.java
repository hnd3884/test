package java.nio.file;

import java.util.NoSuchElementException;
import java.io.UncheckedIOException;
import java.io.IOException;
import java.util.Collection;
import java.util.Arrays;
import java.io.Closeable;
import java.util.Iterator;

class FileTreeIterator implements Iterator<FileTreeWalker.Event>, Closeable
{
    private final FileTreeWalker walker;
    private FileTreeWalker.Event next;
    
    FileTreeIterator(final Path path, final int n, final FileVisitOption... array) throws IOException {
        this.walker = new FileTreeWalker(Arrays.asList(array), n);
        this.next = this.walker.walk(path);
        assert this.next.type() == FileTreeWalker.EventType.START_DIRECTORY;
        final IOException ioeException = this.next.ioeException();
        if (ioeException != null) {
            throw ioeException;
        }
    }
    
    private void fetchNextIfNeeded() {
        if (this.next == null) {
            for (FileTreeWalker.Event next = this.walker.next(); next != null; next = this.walker.next()) {
                final IOException ioeException = next.ioeException();
                if (ioeException != null) {
                    throw new UncheckedIOException(ioeException);
                }
                if (next.type() != FileTreeWalker.EventType.END_DIRECTORY) {
                    this.next = next;
                    return;
                }
            }
        }
    }
    
    @Override
    public boolean hasNext() {
        if (!this.walker.isOpen()) {
            throw new IllegalStateException();
        }
        this.fetchNextIfNeeded();
        return this.next != null;
    }
    
    @Override
    public FileTreeWalker.Event next() {
        if (!this.walker.isOpen()) {
            throw new IllegalStateException();
        }
        this.fetchNextIfNeeded();
        if (this.next == null) {
            throw new NoSuchElementException();
        }
        final FileTreeWalker.Event next = this.next;
        this.next = null;
        return next;
    }
    
    @Override
    public void close() {
        this.walker.close();
    }
}
