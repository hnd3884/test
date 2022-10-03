package javax.tools;

import java.util.Iterator;
import java.io.IOException;
import java.util.Set;
import java.io.Flushable;
import java.io.Closeable;

public interface JavaFileManager extends Closeable, Flushable, OptionChecker
{
    ClassLoader getClassLoader(final Location p0);
    
    Iterable<JavaFileObject> list(final Location p0, final String p1, final Set<JavaFileObject.Kind> p2, final boolean p3) throws IOException;
    
    String inferBinaryName(final Location p0, final JavaFileObject p1);
    
    boolean isSameFile(final FileObject p0, final FileObject p1);
    
    boolean handleOption(final String p0, final Iterator<String> p1);
    
    boolean hasLocation(final Location p0);
    
    JavaFileObject getJavaFileForInput(final Location p0, final String p1, final JavaFileObject.Kind p2) throws IOException;
    
    JavaFileObject getJavaFileForOutput(final Location p0, final String p1, final JavaFileObject.Kind p2, final FileObject p3) throws IOException;
    
    FileObject getFileForInput(final Location p0, final String p1, final String p2) throws IOException;
    
    FileObject getFileForOutput(final Location p0, final String p1, final String p2, final FileObject p3) throws IOException;
    
    void flush() throws IOException;
    
    void close() throws IOException;
    
    public interface Location
    {
        String getName();
        
        boolean isOutputLocation();
    }
}
