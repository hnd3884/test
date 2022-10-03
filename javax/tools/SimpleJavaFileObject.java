package javax.tools;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.StringReader;
import java.io.CharArrayReader;
import java.nio.CharBuffer;
import java.io.Reader;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class SimpleJavaFileObject implements JavaFileObject
{
    protected final URI uri;
    protected final Kind kind;
    
    protected SimpleJavaFileObject(final URI uri, final Kind kind) {
        uri.getClass();
        kind.getClass();
        if (uri.getPath() == null) {
            throw new IllegalArgumentException("URI must have a path: " + uri);
        }
        this.uri = uri;
        this.kind = kind;
    }
    
    @Override
    public URI toUri() {
        return this.uri;
    }
    
    @Override
    public String getName() {
        return this.toUri().getPath();
    }
    
    @Override
    public InputStream openInputStream() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public OutputStream openOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Reader openReader(final boolean b) throws IOException {
        final CharSequence charContent = this.getCharContent(b);
        if (charContent == null) {
            throw new UnsupportedOperationException();
        }
        if (charContent instanceof CharBuffer) {
            final CharBuffer charBuffer = (CharBuffer)charContent;
            if (charBuffer.hasArray()) {
                return new CharArrayReader(charBuffer.array());
            }
        }
        return new StringReader(charContent.toString());
    }
    
    @Override
    public CharSequence getCharContent(final boolean b) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Writer openWriter() throws IOException {
        return new OutputStreamWriter(this.openOutputStream());
    }
    
    @Override
    public long getLastModified() {
        return 0L;
    }
    
    @Override
    public boolean delete() {
        return false;
    }
    
    @Override
    public Kind getKind() {
        return this.kind;
    }
    
    @Override
    public boolean isNameCompatible(final String s, final Kind kind) {
        final String string = s + kind.extension;
        return kind.equals(this.getKind()) && (string.equals(this.toUri().getPath()) || this.toUri().getPath().endsWith("/" + string));
    }
    
    @Override
    public NestingKind getNestingKind() {
        return null;
    }
    
    @Override
    public Modifier getAccessLevel() {
        return null;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[" + this.toUri() + "]";
    }
}
