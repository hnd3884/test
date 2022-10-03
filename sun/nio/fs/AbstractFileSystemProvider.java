package sun.nio.fs;

import java.util.Map;
import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

abstract class AbstractFileSystemProvider extends FileSystemProvider
{
    protected AbstractFileSystemProvider() {
    }
    
    private static String[] split(final String s) {
        final String[] array = new String[2];
        int index = s.indexOf(58);
        if (index == -1) {
            array[0] = "basic";
            array[1] = s;
        }
        else {
            array[0] = s.substring(0, index++);
            array[1] = ((index == s.length()) ? "" : s.substring(index));
        }
        return array;
    }
    
    abstract DynamicFileAttributeView getFileAttributeView(final Path p0, final String p1, final LinkOption... p2);
    
    @Override
    public final void setAttribute(final Path path, final String s, final Object o, final LinkOption... array) throws IOException {
        final String[] split = split(s);
        if (split[0].length() == 0) {
            throw new IllegalArgumentException(s);
        }
        final DynamicFileAttributeView fileAttributeView = this.getFileAttributeView(path, split[0], array);
        if (fileAttributeView == null) {
            throw new UnsupportedOperationException("View '" + split[0] + "' not available");
        }
        fileAttributeView.setAttribute(split[1], o);
    }
    
    @Override
    public final Map<String, Object> readAttributes(final Path path, final String s, final LinkOption... array) throws IOException {
        final String[] split = split(s);
        if (split[0].length() == 0) {
            throw new IllegalArgumentException(s);
        }
        final DynamicFileAttributeView fileAttributeView = this.getFileAttributeView(path, split[0], array);
        if (fileAttributeView == null) {
            throw new UnsupportedOperationException("View '" + split[0] + "' not available");
        }
        return fileAttributeView.readAttributes(split[1].split(","));
    }
    
    abstract boolean implDelete(final Path p0, final boolean p1) throws IOException;
    
    @Override
    public final void delete(final Path path) throws IOException {
        this.implDelete(path, true);
    }
    
    @Override
    public final boolean deleteIfExists(final Path path) throws IOException {
        return this.implDelete(path, false);
    }
}
