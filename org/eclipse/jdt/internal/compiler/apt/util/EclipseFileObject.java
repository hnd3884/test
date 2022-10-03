package org.eclipse.jdt.internal.compiler.apt.util;

import java.io.FileWriter;
import java.io.Writer;
import java.io.FileReader;
import java.io.Reader;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.tools.FileObject;
import org.eclipse.jdt.internal.compiler.util.Util;
import javax.lang.model.element.NestingKind;
import java.io.IOException;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;
import java.net.URI;
import java.nio.charset.Charset;
import java.io.File;
import javax.tools.SimpleJavaFileObject;

public class EclipseFileObject extends SimpleJavaFileObject
{
    private File f;
    private Charset charset;
    private boolean parentsExist;
    
    public EclipseFileObject(final String className, final URI uri, final JavaFileObject.Kind kind, final Charset charset) {
        super(uri, kind);
        this.f = new File(this.uri);
        this.charset = charset;
        this.parentsExist = false;
    }
    
    @Override
    public Modifier getAccessLevel() {
        if (this.getKind() != JavaFileObject.Kind.CLASS) {
            return null;
        }
        ClassFileReader reader = null;
        try {
            reader = ClassFileReader.read(this.f);
        }
        catch (final ClassFormatException ex) {}
        catch (final IOException ex2) {}
        if (reader == null) {
            return null;
        }
        final int accessFlags = reader.accessFlags();
        if ((accessFlags & 0x1) != 0x0) {
            return Modifier.PUBLIC;
        }
        if ((accessFlags & 0x400) != 0x0) {
            return Modifier.ABSTRACT;
        }
        if ((accessFlags & 0x10) != 0x0) {
            return Modifier.FINAL;
        }
        return null;
    }
    
    @Override
    public NestingKind getNestingKind() {
        switch (this.kind) {
            case SOURCE: {
                return NestingKind.TOP_LEVEL;
            }
            case CLASS: {
                ClassFileReader reader = null;
                try {
                    reader = ClassFileReader.read(this.f);
                }
                catch (final ClassFormatException ex) {}
                catch (final IOException ex2) {}
                if (reader == null) {
                    return null;
                }
                if (reader.isAnonymous()) {
                    return NestingKind.ANONYMOUS;
                }
                if (reader.isLocal()) {
                    return NestingKind.LOCAL;
                }
                if (reader.isMember()) {
                    return NestingKind.MEMBER;
                }
                return NestingKind.TOP_LEVEL;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public boolean delete() {
        return this.f.delete();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof EclipseFileObject)) {
            return false;
        }
        final EclipseFileObject eclipseFileObject = (EclipseFileObject)o;
        return eclipseFileObject.toUri().equals(this.uri);
    }
    
    @Override
    public CharSequence getCharContent(final boolean ignoreEncodingErrors) throws IOException {
        return org.eclipse.jdt.internal.compiler.apt.util.Util.getCharContents(this, ignoreEncodingErrors, Util.getFileByteContent(this.f), this.charset.name());
    }
    
    @Override
    public long getLastModified() {
        return this.f.lastModified();
    }
    
    @Override
    public String getName() {
        return this.f.getPath();
    }
    
    @Override
    public int hashCode() {
        return this.f.hashCode();
    }
    
    @Override
    public InputStream openInputStream() throws IOException {
        return new FileInputStream(this.f);
    }
    
    @Override
    public OutputStream openOutputStream() throws IOException {
        this.ensureParentDirectoriesExist();
        return new FileOutputStream(this.f);
    }
    
    @Override
    public Reader openReader(final boolean ignoreEncodingErrors) throws IOException {
        return new FileReader(this.f);
    }
    
    @Override
    public Writer openWriter() throws IOException {
        this.ensureParentDirectoriesExist();
        return new FileWriter(this.f);
    }
    
    @Override
    public String toString() {
        return this.f.getAbsolutePath();
    }
    
    private void ensureParentDirectoriesExist() throws IOException {
        if (!this.parentsExist) {
            final File parent = this.f.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs() && (!parent.exists() || !parent.isDirectory())) {
                throw new IOException("Unable to create parent directories for " + this.f);
            }
            this.parentsExist = true;
        }
    }
}
