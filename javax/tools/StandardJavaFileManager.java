package javax.tools;

import java.io.IOException;
import java.io.File;

public interface StandardJavaFileManager extends JavaFileManager
{
    boolean isSameFile(final FileObject p0, final FileObject p1);
    
    Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(final Iterable<? extends File> p0);
    
    Iterable<? extends JavaFileObject> getJavaFileObjects(final File... p0);
    
    Iterable<? extends JavaFileObject> getJavaFileObjectsFromStrings(final Iterable<String> p0);
    
    Iterable<? extends JavaFileObject> getJavaFileObjects(final String... p0);
    
    void setLocation(final Location p0, final Iterable<? extends File> p1) throws IOException;
    
    Iterable<? extends File> getLocation(final Location p0);
}
