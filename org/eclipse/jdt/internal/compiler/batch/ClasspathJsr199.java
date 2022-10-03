package org.eclipse.jdt.internal.compiler.batch;

import java.util.Iterator;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.ArrayList;
import java.io.InputStream;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import java.io.IOException;
import java.io.File;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import java.util.List;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import java.util.HashSet;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.util.Set;

public class ClasspathJsr199 extends ClasspathLocation
{
    private static final Set<JavaFileObject.Kind> fileTypes;
    private JavaFileManager fileManager;
    private JavaFileManager.Location location;
    
    static {
        (fileTypes = new HashSet<JavaFileObject.Kind>()).add(JavaFileObject.Kind.CLASS);
    }
    
    public ClasspathJsr199(final JavaFileManager file, final JavaFileManager.Location location) {
        super(null, null);
        this.fileManager = file;
        this.location = location;
    }
    
    @Override
    public List fetchLinkedJars(final FileSystem.ClasspathSectionProblemReporter problemReporter) {
        return null;
    }
    
    @Override
    public NameEnvironmentAnswer findClass(final char[] typeName, final String qualifiedPackageName, final String qualifiedBinaryFileName) {
        return this.findClass(typeName, qualifiedPackageName, qualifiedBinaryFileName, false);
    }
    
    @Override
    public NameEnvironmentAnswer findClass(final char[] typeName, final String qualifiedPackageName, final String aQualifiedBinaryFileName, final boolean asBinaryOnly) {
        final String qualifiedBinaryFileName = (File.separatorChar == '/') ? aQualifiedBinaryFileName : aQualifiedBinaryFileName.replace(File.separatorChar, '/');
        try {
            final int lastDot = qualifiedBinaryFileName.lastIndexOf(46);
            final String className = (lastDot < 0) ? qualifiedBinaryFileName : qualifiedBinaryFileName.substring(0, lastDot);
            JavaFileObject jfo = null;
            try {
                jfo = this.fileManager.getJavaFileForInput(this.location, className, JavaFileObject.Kind.CLASS);
            }
            catch (final IOException ex) {}
            if (jfo == null) {
                return null;
            }
            Throwable t = null;
            try {
                final InputStream inputStream = jfo.openInputStream();
                try {
                    final ClassFileReader reader = ClassFileReader.read(inputStream, qualifiedBinaryFileName);
                    if (reader != null) {
                        return new NameEnvironmentAnswer(reader, this.fetchAccessRestriction(qualifiedBinaryFileName));
                    }
                    return null;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            finally {
                if (t == null) {
                    final Throwable t2;
                    t = t2;
                }
                else {
                    final Throwable t2;
                    if (t != t2) {
                        t.addSuppressed(t2);
                    }
                }
            }
        }
        catch (final ClassFormatException ex2) {}
        catch (final IOException ex3) {}
        return null;
    }
    
    @Override
    public char[][][] findTypeNames(final String aQualifiedPackageName) {
        final String qualifiedPackageName = (File.separatorChar == '/') ? aQualifiedPackageName : aQualifiedPackageName.replace(File.separatorChar, '/');
        Iterable<JavaFileObject> files = null;
        try {
            files = this.fileManager.list(this.location, qualifiedPackageName, ClasspathJsr199.fileTypes, false);
        }
        catch (final IOException ex) {}
        if (files == null) {
            return null;
        }
        final ArrayList answers = new ArrayList();
        final char[][] packageName = CharOperation.splitOn(File.separatorChar, qualifiedPackageName.toCharArray());
        for (final JavaFileObject file : files) {
            final String fileName = file.toUri().getPath();
            final int last = fileName.lastIndexOf(47);
            if (last > 0) {
                final int indexOfDot = fileName.lastIndexOf(46);
                if (indexOfDot == -1) {
                    continue;
                }
                final String typeName = fileName.substring(last + 1, indexOfDot);
                answers.add(CharOperation.arrayConcat(packageName, typeName.toCharArray()));
            }
        }
        final int size = answers.size();
        if (size != 0) {
            final char[][][] result = new char[size][][];
            answers.toArray(result);
            return result;
        }
        return null;
    }
    
    @Override
    public void initialize() throws IOException {
    }
    
    @Override
    public boolean isPackage(final String aQualifiedPackageName) {
        final String qualifiedPackageName = (File.separatorChar == '/') ? aQualifiedPackageName : aQualifiedPackageName.replace(File.separatorChar, '/');
        boolean result = false;
        try {
            Iterable<JavaFileObject> files = this.fileManager.list(this.location, qualifiedPackageName, ClasspathJsr199.fileTypes, false);
            Iterator f = files.iterator();
            if (f.hasNext()) {
                result = true;
            }
            else {
                files = this.fileManager.list(this.location, qualifiedPackageName, ClasspathJsr199.fileTypes, true);
                f = files.iterator();
                if (f.hasNext()) {
                    result = true;
                }
            }
        }
        catch (final IOException ex) {}
        return result;
    }
    
    @Override
    public void reset() {
        try {
            this.fileManager.flush();
        }
        catch (final IOException ex) {}
    }
    
    @Override
    public String toString() {
        return "Classpath for Jsr 199 JavaFileManager: " + this.location;
    }
    
    @Override
    public char[] normalizedPath() {
        if (this.normalizedPath == null) {
            this.normalizedPath = this.path.toCharArray();
        }
        return this.normalizedPath;
    }
    
    @Override
    public String getPath() {
        if (this.path == null) {
            this.path = this.location.getName();
        }
        return this.path;
    }
    
    @Override
    public int getMode() {
        return 2;
    }
    
    @Override
    public boolean hasAnnotationFileFor(final String qualifiedTypeName) {
        return false;
    }
}
