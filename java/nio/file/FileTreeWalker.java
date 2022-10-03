package java.nio.file;

import java.io.IOException;
import sun.nio.fs.BasicFileAttributesHolder;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayDeque;
import java.io.Closeable;

class FileTreeWalker implements Closeable
{
    private final boolean followLinks;
    private final LinkOption[] linkOptions;
    private final int maxDepth;
    private final ArrayDeque<DirectoryNode> stack;
    private boolean closed;
    
    FileTreeWalker(final Collection<FileVisitOption> collection, final int maxDepth) {
        this.stack = new ArrayDeque<DirectoryNode>();
        boolean followLinks = false;
        final Iterator<FileVisitOption> iterator = collection.iterator();
        while (iterator.hasNext()) {
            switch (iterator.next()) {
                case FOLLOW_LINKS: {
                    followLinks = true;
                    continue;
                }
                default: {
                    throw new AssertionError((Object)"Should not get here");
                }
            }
        }
        if (maxDepth < 0) {
            throw new IllegalArgumentException("'maxDepth' is negative");
        }
        this.followLinks = followLinks;
        this.linkOptions = (followLinks ? new LinkOption[0] : new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
        this.maxDepth = maxDepth;
    }
    
    private BasicFileAttributes getAttributes(final Path path, final boolean b) throws IOException {
        if (b && path instanceof BasicFileAttributesHolder && System.getSecurityManager() == null) {
            final BasicFileAttributes value = ((BasicFileAttributesHolder)path).get();
            if (value != null && (!this.followLinks || !value.isSymbolicLink())) {
                return value;
            }
        }
        BasicFileAttributes basicFileAttributes;
        try {
            basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class, this.linkOptions);
        }
        catch (final IOException ex) {
            if (!this.followLinks) {
                throw ex;
            }
            basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        }
        return basicFileAttributes;
    }
    
    private boolean wouldLoop(final Path path, final Object o) {
        for (final DirectoryNode directoryNode : this.stack) {
            final Object key = directoryNode.key();
            if (o != null && key != null) {
                if (o.equals(key)) {
                    return true;
                }
                continue;
            }
            else {
                try {
                    if (Files.isSameFile(path, directoryNode.directory())) {
                        return true;
                    }
                    continue;
                }
                catch (final IOException | SecurityException ex) {}
            }
        }
        return false;
    }
    
    private Event visit(final Path path, final boolean b, final boolean b2) {
        BasicFileAttributes attributes;
        try {
            attributes = this.getAttributes(path, b2);
        }
        catch (final IOException ex) {
            return new Event(EventType.ENTRY, path, ex);
        }
        catch (final SecurityException ex2) {
            if (b) {
                return null;
            }
            throw ex2;
        }
        if (this.stack.size() >= this.maxDepth || !attributes.isDirectory()) {
            return new Event(EventType.ENTRY, path, attributes);
        }
        if (this.followLinks && this.wouldLoop(path, attributes.fileKey())) {
            return new Event(EventType.ENTRY, path, new FileSystemLoopException(path.toString()));
        }
        DirectoryStream<Path> directoryStream;
        try {
            directoryStream = Files.newDirectoryStream(path);
        }
        catch (final IOException ex3) {
            return new Event(EventType.ENTRY, path, ex3);
        }
        catch (final SecurityException ex4) {
            if (b) {
                return null;
            }
            throw ex4;
        }
        this.stack.push(new DirectoryNode(path, attributes.fileKey(), directoryStream));
        return new Event(EventType.START_DIRECTORY, path, attributes);
    }
    
    Event walk(final Path path) {
        if (this.closed) {
            throw new IllegalStateException("Closed");
        }
        final Event visit = this.visit(path, false, false);
        assert visit != null;
        return visit;
    }
    
    Event next() {
        final DirectoryNode directoryNode = this.stack.peek();
        if (directoryNode == null) {
            return null;
        }
        Event visit;
        do {
            Path path = null;
            IOException cause = null;
            if (!directoryNode.skipped()) {
                final Iterator<Path> iterator = directoryNode.iterator();
                try {
                    if (iterator.hasNext()) {
                        path = iterator.next();
                    }
                }
                catch (final DirectoryIteratorException ex) {
                    cause = ex.getCause();
                }
            }
            if (path == null) {
                try {
                    directoryNode.stream().close();
                }
                catch (final IOException ex2) {
                    if (cause != null) {
                        cause = ex2;
                    }
                    else {
                        cause.addSuppressed(ex2);
                    }
                }
                this.stack.pop();
                return new Event(EventType.END_DIRECTORY, directoryNode.directory(), cause);
            }
            visit = this.visit(path, true, true);
        } while (visit == null);
        return visit;
    }
    
    void pop() {
        if (!this.stack.isEmpty()) {
            final DirectoryNode directoryNode = this.stack.pop();
            try {
                directoryNode.stream().close();
            }
            catch (final IOException ex) {}
        }
    }
    
    void skipRemainingSiblings() {
        if (!this.stack.isEmpty()) {
            this.stack.peek().skip();
        }
    }
    
    boolean isOpen() {
        return !this.closed;
    }
    
    @Override
    public void close() {
        if (!this.closed) {
            while (!this.stack.isEmpty()) {
                this.pop();
            }
            this.closed = true;
        }
    }
    
    private static class DirectoryNode
    {
        private final Path dir;
        private final Object key;
        private final DirectoryStream<Path> stream;
        private final Iterator<Path> iterator;
        private boolean skipped;
        
        DirectoryNode(final Path dir, final Object key, final DirectoryStream<Path> stream) {
            this.dir = dir;
            this.key = key;
            this.stream = stream;
            this.iterator = stream.iterator();
        }
        
        Path directory() {
            return this.dir;
        }
        
        Object key() {
            return this.key;
        }
        
        DirectoryStream<Path> stream() {
            return this.stream;
        }
        
        Iterator<Path> iterator() {
            return this.iterator;
        }
        
        void skip() {
            this.skipped = true;
        }
        
        boolean skipped() {
            return this.skipped;
        }
    }
    
    enum EventType
    {
        START_DIRECTORY, 
        END_DIRECTORY, 
        ENTRY;
    }
    
    static class Event
    {
        private final EventType type;
        private final Path file;
        private final BasicFileAttributes attrs;
        private final IOException ioe;
        
        private Event(final EventType type, final Path file, final BasicFileAttributes attrs, final IOException ioe) {
            this.type = type;
            this.file = file;
            this.attrs = attrs;
            this.ioe = ioe;
        }
        
        Event(final EventType eventType, final Path path, final BasicFileAttributes basicFileAttributes) {
            this(eventType, path, basicFileAttributes, null);
        }
        
        Event(final EventType eventType, final Path path, final IOException ex) {
            this(eventType, path, null, ex);
        }
        
        EventType type() {
            return this.type;
        }
        
        Path file() {
            return this.file;
        }
        
        BasicFileAttributes attributes() {
            return this.attrs;
        }
        
        IOException ioeException() {
            return this.ioe;
        }
    }
}
