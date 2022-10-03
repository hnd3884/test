package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.annotation.processing.FilerException;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.tools.JavaFileObject;
import javax.lang.model.element.Element;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import java.net.URI;
import java.util.HashSet;
import javax.tools.JavaFileManager;
import javax.annotation.processing.Filer;

public class BatchFilerImpl implements Filer
{
    protected final BaseAnnotationProcessorManager _dispatchManager;
    protected final BaseProcessingEnvImpl _env;
    protected final JavaFileManager _fileManager;
    protected final HashSet<URI> _createdFiles;
    
    public BatchFilerImpl(final BaseAnnotationProcessorManager dispatchManager, final BatchProcessingEnvImpl env) {
        this._dispatchManager = dispatchManager;
        this._fileManager = env._fileManager;
        this._env = env;
        this._createdFiles = new HashSet<URI>();
    }
    
    public void addNewUnit(final ICompilationUnit unit) {
        this._env.addNewUnit(unit);
    }
    
    public void addNewClassFile(final ReferenceBinding binding) {
        this._env.addNewClassFile(binding);
    }
    
    @Override
    public JavaFileObject createClassFile(final CharSequence name, final Element... originatingElements) throws IOException {
        final JavaFileObject jfo = this._fileManager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, name.toString(), JavaFileObject.Kind.CLASS, null);
        final URI uri = jfo.toUri();
        if (this._createdFiles.contains(uri)) {
            throw new FilerException("Class file already created : " + (Object)name);
        }
        this._createdFiles.add(uri);
        return new HookedJavaFileObject(jfo, jfo.getName(), name.toString(), this);
    }
    
    @Override
    public FileObject createResource(final JavaFileManager.Location location, final CharSequence pkg, final CharSequence relativeName, final Element... originatingElements) throws IOException {
        validateName(relativeName);
        final FileObject fo = this._fileManager.getFileForOutput(location, pkg.toString(), relativeName.toString(), null);
        final URI uri = fo.toUri();
        if (this._createdFiles.contains(uri)) {
            throw new FilerException("Resource already created : " + location + '/' + (Object)pkg + '/' + (Object)relativeName);
        }
        this._createdFiles.add(uri);
        return fo;
    }
    
    private static void validateName(final CharSequence relativeName) {
        final int length = relativeName.length();
        if (length == 0) {
            throw new IllegalArgumentException("relative path cannot be empty");
        }
        String path = relativeName.toString();
        if (path.indexOf(92) != -1) {
            path = path.replace('\\', '/');
        }
        if (path.charAt(0) == '/') {
            throw new IllegalArgumentException("relative path is absolute");
        }
        boolean hasDot = false;
        for (int i = 0; i < length; ++i) {
            switch (path.charAt(i)) {
                case '/': {
                    if (hasDot) {
                        throw new IllegalArgumentException("relative name " + (Object)relativeName + " is not relative");
                    }
                    break;
                }
                case '.': {
                    hasDot = true;
                    break;
                }
                default: {
                    hasDot = false;
                    break;
                }
            }
        }
        if (hasDot) {
            throw new IllegalArgumentException("relative name " + (Object)relativeName + " is not relative");
        }
    }
    
    @Override
    public JavaFileObject createSourceFile(final CharSequence name, final Element... originatingElements) throws IOException {
        final JavaFileObject jfo = this._fileManager.getJavaFileForOutput(StandardLocation.SOURCE_OUTPUT, name.toString(), JavaFileObject.Kind.SOURCE, null);
        final URI uri = jfo.toUri();
        if (this._createdFiles.contains(uri)) {
            throw new FilerException("Source file already created : " + (Object)name);
        }
        this._createdFiles.add(uri);
        return new HookedJavaFileObject(jfo, jfo.getName(), name.toString(), this);
    }
    
    @Override
    public FileObject getResource(final JavaFileManager.Location location, final CharSequence pkg, final CharSequence relativeName) throws IOException {
        validateName(relativeName);
        final FileObject fo = this._fileManager.getFileForInput(location, pkg.toString(), relativeName.toString());
        if (fo == null) {
            throw new FileNotFoundException("Resource does not exist : " + location + '/' + (Object)pkg + '/' + (Object)relativeName);
        }
        final URI uri = fo.toUri();
        if (this._createdFiles.contains(uri)) {
            throw new FilerException("Resource already created : " + location + '/' + (Object)pkg + '/' + (Object)relativeName);
        }
        this._createdFiles.add(uri);
        return fo;
    }
}
