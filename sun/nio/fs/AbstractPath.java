package sun.nio.fs;

import java.io.IOException;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.io.File;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.nio.file.Path;

abstract class AbstractPath implements Path
{
    protected AbstractPath() {
    }
    
    @Override
    public final boolean startsWith(final String s) {
        return this.startsWith(this.getFileSystem().getPath(s, new String[0]));
    }
    
    @Override
    public final boolean endsWith(final String s) {
        return this.endsWith(this.getFileSystem().getPath(s, new String[0]));
    }
    
    @Override
    public final Path resolve(final String s) {
        return this.resolve(this.getFileSystem().getPath(s, new String[0]));
    }
    
    @Override
    public final Path resolveSibling(final Path path) {
        if (path == null) {
            throw new NullPointerException();
        }
        final Path parent = this.getParent();
        return (parent == null) ? path : parent.resolve(path);
    }
    
    @Override
    public final Path resolveSibling(final String s) {
        return this.resolveSibling(this.getFileSystem().getPath(s, new String[0]));
    }
    
    @Override
    public final Iterator<Path> iterator() {
        return new Iterator<Path>() {
            private int i = 0;
            
            @Override
            public boolean hasNext() {
                return this.i < AbstractPath.this.getNameCount();
            }
            
            @Override
            public Path next() {
                if (this.i < AbstractPath.this.getNameCount()) {
                    final Path name = AbstractPath.this.getName(this.i);
                    ++this.i;
                    return name;
                }
                throw new NoSuchElementException();
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    @Override
    public final File toFile() {
        return new File(this.toString());
    }
    
    @Override
    public final WatchKey register(final WatchService watchService, final WatchEvent.Kind<?>... array) throws IOException {
        return this.register(watchService, array, new WatchEvent.Modifier[0]);
    }
}
