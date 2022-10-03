package org.eclipse.jdt.internal.compiler.apt.util;

import java.net.URISyntaxException;
import java.net.URI;
import java.io.Writer;
import java.io.Reader;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import javax.tools.FileObject;
import org.eclipse.jdt.internal.compiler.util.Util;
import javax.lang.model.element.NestingKind;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipFile;
import java.io.File;
import javax.tools.JavaFileObject;

public class ArchiveFileObject implements JavaFileObject
{
    private String entryName;
    private File file;
    private ZipFile zipFile;
    private Charset charset;
    
    public ArchiveFileObject(final File file, final String entryName, final Charset charset) {
        this.entryName = entryName;
        this.file = file;
        this.charset = charset;
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (this.zipFile != null) {
            try {
                this.zipFile.close();
            }
            catch (final IOException ex) {}
        }
        super.finalize();
    }
    
    @Override
    public Modifier getAccessLevel() {
        if (this.getKind() != Kind.CLASS) {
            return null;
        }
        ClassFileReader reader = null;
        try {
            Throwable t = null;
            try {
                final ZipFile zip = new ZipFile(this.file);
                try {
                    reader = ClassFileReader.read(zip, this.entryName);
                }
                finally {
                    if (zip != null) {
                        zip.close();
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
    public Kind getKind() {
        final String name = this.entryName.toLowerCase();
        if (name.endsWith(Kind.CLASS.extension)) {
            return Kind.CLASS;
        }
        if (name.endsWith(Kind.SOURCE.extension)) {
            return Kind.SOURCE;
        }
        if (name.endsWith(Kind.HTML.extension)) {
            return Kind.HTML;
        }
        return Kind.OTHER;
    }
    
    @Override
    public NestingKind getNestingKind() {
        switch (this.getKind()) {
            case SOURCE: {
                return NestingKind.TOP_LEVEL;
            }
            case CLASS: {
                ClassFileReader reader = null;
                try {
                    Throwable t = null;
                    try {
                        final ZipFile zip = new ZipFile(this.file);
                        try {
                            reader = ClassFileReader.read(zip, this.entryName);
                        }
                        finally {
                            if (zip != null) {
                                zip.close();
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
    public boolean isNameCompatible(final String simpleName, final Kind kind) {
        return this.entryName.endsWith(String.valueOf(simpleName) + kind.extension);
    }
    
    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ArchiveFileObject)) {
            return false;
        }
        final ArchiveFileObject archiveFileObject = (ArchiveFileObject)o;
        return archiveFileObject.toUri().equals(this.toUri());
    }
    
    @Override
    public int hashCode() {
        return this.toUri().hashCode();
    }
    
    @Override
    public CharSequence getCharContent(final boolean ignoreEncodingErrors) throws IOException {
        if (this.getKind() == Kind.SOURCE) {
            Throwable t = null;
            try {
                final ZipFile zipFile2 = new ZipFile(this.file);
                try {
                    final ZipEntry zipEntry = zipFile2.getEntry(this.entryName);
                    return org.eclipse.jdt.internal.compiler.apt.util.Util.getCharContents(this, ignoreEncodingErrors, Util.getZipEntryByteContent(zipEntry, zipFile2), this.charset.name());
                }
                finally {
                    if (zipFile2 != null) {
                        zipFile2.close();
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
        return null;
    }
    
    @Override
    public long getLastModified() {
        try {
            Throwable t = null;
            try {
                final ZipFile zip = new ZipFile(this.file);
                try {
                    final ZipEntry zipEntry = zip.getEntry(this.entryName);
                    return zipEntry.getTime();
                }
                finally {
                    if (zip != null) {
                        zip.close();
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
        catch (final IOException ex) {
            return 0L;
        }
    }
    
    @Override
    public String getName() {
        return this.entryName;
    }
    
    @Override
    public InputStream openInputStream() throws IOException {
        if (this.zipFile == null) {
            this.zipFile = new ZipFile(this.file);
        }
        final ZipEntry zipEntry = this.zipFile.getEntry(this.entryName);
        return this.zipFile.getInputStream(zipEntry);
    }
    
    @Override
    public OutputStream openOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Reader openReader(final boolean ignoreEncodingErrors) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Writer openWriter() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public URI toUri() {
        try {
            return new URI("jar:" + this.file.toURI().getPath() + "!" + this.entryName);
        }
        catch (final URISyntaxException ex) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.file.getAbsolutePath()) + "[" + this.entryName + "]";
    }
}
