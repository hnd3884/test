package javax.tools;

import java.util.Iterator;
import java.io.IOException;
import java.util.Set;

public class ForwardingJavaFileManager<M extends JavaFileManager> implements JavaFileManager
{
    protected final M fileManager;
    
    protected ForwardingJavaFileManager(final M fileManager) {
        fileManager.getClass();
        this.fileManager = fileManager;
    }
    
    @Override
    public ClassLoader getClassLoader(final Location location) {
        return this.fileManager.getClassLoader(location);
    }
    
    @Override
    public Iterable<JavaFileObject> list(final Location location, final String s, final Set<JavaFileObject.Kind> set, final boolean b) throws IOException {
        return this.fileManager.list(location, s, set, b);
    }
    
    @Override
    public String inferBinaryName(final Location location, final JavaFileObject javaFileObject) {
        return this.fileManager.inferBinaryName(location, javaFileObject);
    }
    
    @Override
    public boolean isSameFile(final FileObject fileObject, final FileObject fileObject2) {
        return this.fileManager.isSameFile(fileObject, fileObject2);
    }
    
    @Override
    public boolean handleOption(final String s, final Iterator<String> iterator) {
        return this.fileManager.handleOption(s, iterator);
    }
    
    @Override
    public boolean hasLocation(final Location location) {
        return this.fileManager.hasLocation(location);
    }
    
    @Override
    public int isSupportedOption(final String s) {
        return this.fileManager.isSupportedOption(s);
    }
    
    @Override
    public JavaFileObject getJavaFileForInput(final Location location, final String s, final JavaFileObject.Kind kind) throws IOException {
        return this.fileManager.getJavaFileForInput(location, s, kind);
    }
    
    @Override
    public JavaFileObject getJavaFileForOutput(final Location location, final String s, final JavaFileObject.Kind kind, final FileObject fileObject) throws IOException {
        return this.fileManager.getJavaFileForOutput(location, s, kind, fileObject);
    }
    
    @Override
    public FileObject getFileForInput(final Location location, final String s, final String s2) throws IOException {
        return this.fileManager.getFileForInput(location, s, s2);
    }
    
    @Override
    public FileObject getFileForOutput(final Location location, final String s, final String s2, final FileObject fileObject) throws IOException {
        return this.fileManager.getFileForOutput(location, s, s2, fileObject);
    }
    
    @Override
    public void flush() throws IOException {
        this.fileManager.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.fileManager.close();
    }
}
