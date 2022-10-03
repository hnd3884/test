package sun.nio.fs;

import java.util.NoSuchElementException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.Iterator;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;

class WindowsDirectoryStream implements DirectoryStream<Path>
{
    private final WindowsPath dir;
    private final Filter<? super Path> filter;
    private final long handle;
    private final String firstName;
    private final NativeBuffer findDataBuffer;
    private final Object closeLock;
    private boolean isOpen;
    private Iterator<Path> iterator;
    
    WindowsDirectoryStream(final WindowsPath dir, final Filter<? super Path> filter) throws IOException {
        this.closeLock = new Object();
        this.isOpen = true;
        this.dir = dir;
        this.filter = filter;
        try {
            final String pathForWin32Calls = dir.getPathForWin32Calls();
            final char char1 = pathForWin32Calls.charAt(pathForWin32Calls.length() - 1);
            String s;
            if (char1 == ':' || char1 == '\\') {
                s = pathForWin32Calls + "*";
            }
            else {
                s = pathForWin32Calls + "\\*";
            }
            final WindowsNativeDispatcher.FirstFile findFirstFile = WindowsNativeDispatcher.FindFirstFile(s);
            this.handle = findFirstFile.handle();
            this.firstName = findFirstFile.name();
            this.findDataBuffer = WindowsFileAttributes.getBufferForFindData();
        }
        catch (final WindowsException ex) {
            if (ex.lastError() == 267) {
                throw new NotDirectoryException(dir.getPathForExceptionMessage());
            }
            ex.rethrowAsIOException(dir);
            throw new AssertionError();
        }
    }
    
    @Override
    public void close() throws IOException {
        synchronized (this.closeLock) {
            if (!this.isOpen) {
                return;
            }
            this.isOpen = false;
        }
        this.findDataBuffer.release();
        try {
            WindowsNativeDispatcher.FindClose(this.handle);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(this.dir);
        }
    }
    
    @Override
    public Iterator<Path> iterator() {
        if (!this.isOpen) {
            throw new IllegalStateException("Directory stream is closed");
        }
        synchronized (this) {
            if (this.iterator != null) {
                throw new IllegalStateException("Iterator already obtained");
            }
            return this.iterator = new WindowsDirectoryIterator(this.firstName);
        }
    }
    
    private class WindowsDirectoryIterator implements Iterator<Path>
    {
        private boolean atEof;
        private String first;
        private Path nextEntry;
        private String prefix;
        
        WindowsDirectoryIterator(final String first) {
            this.atEof = false;
            this.first = first;
            if (WindowsDirectoryStream.this.dir.needsSlashWhenResolving()) {
                this.prefix = WindowsDirectoryStream.this.dir.toString() + "\\";
            }
            else {
                this.prefix = WindowsDirectoryStream.this.dir.toString();
            }
        }
        
        private boolean isSelfOrParent(final String s) {
            return s.equals(".") || s.equals("..");
        }
        
        private Path acceptEntry(final String s, final BasicFileAttributes basicFileAttributes) {
            final WindowsPath fromNormalizedPath = WindowsPath.createFromNormalizedPath(WindowsDirectoryStream.this.dir.getFileSystem(), this.prefix + s, basicFileAttributes);
            try {
                if (WindowsDirectoryStream.this.filter.accept(fromNormalizedPath)) {
                    return fromNormalizedPath;
                }
            }
            catch (final IOException ex) {
                throw new DirectoryIteratorException(ex);
            }
            return null;
        }
        
        private Path readNextEntry() {
            if (this.first != null) {
                this.nextEntry = (this.isSelfOrParent(this.first) ? null : this.acceptEntry(this.first, null));
                this.first = null;
                if (this.nextEntry != null) {
                    return this.nextEntry;
                }
            }
            Path acceptEntry;
            while (true) {
                String findNextFile = null;
                final WindowsFileAttributes fromFindData;
                synchronized (WindowsDirectoryStream.this.closeLock) {
                    try {
                        if (WindowsDirectoryStream.this.isOpen) {
                            findNextFile = WindowsNativeDispatcher.FindNextFile(WindowsDirectoryStream.this.handle, WindowsDirectoryStream.this.findDataBuffer.address());
                        }
                    }
                    catch (final WindowsException ex) {
                        throw new DirectoryIteratorException(ex.asIOException(WindowsDirectoryStream.this.dir));
                    }
                    if (findNextFile == null) {
                        this.atEof = true;
                        return null;
                    }
                    if (this.isSelfOrParent(findNextFile)) {
                        continue;
                    }
                    fromFindData = WindowsFileAttributes.fromFindData(WindowsDirectoryStream.this.findDataBuffer.address());
                }
                acceptEntry = this.acceptEntry(findNextFile, fromFindData);
                if (acceptEntry != null) {
                    break;
                }
            }
            return acceptEntry;
        }
        
        @Override
        public synchronized boolean hasNext() {
            if (this.nextEntry == null && !this.atEof) {
                this.nextEntry = this.readNextEntry();
            }
            return this.nextEntry != null;
        }
        
        @Override
        public synchronized Path next() {
            Path path;
            if (this.nextEntry == null && !this.atEof) {
                path = this.readNextEntry();
            }
            else {
                path = this.nextEntry;
                this.nextEntry = null;
            }
            if (path == null) {
                throw new NoSuchElementException();
            }
            return path;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
