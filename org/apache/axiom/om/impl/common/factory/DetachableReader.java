package org.apache.axiom.om.impl.common.factory;

import java.io.Writer;
import org.apache.axiom.blob.MemoryBlob;
import java.io.InputStreamReader;
import org.apache.axiom.om.OMException;
import java.io.OutputStreamWriter;
import org.apache.axiom.blob.Blobs;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import org.apache.axiom.om.impl.builder.Detachable;
import java.io.Reader;

final class DetachableReader extends Reader implements Detachable
{
    private static final Charset UTF8;
    private Reader target;
    
    static {
        UTF8 = Charset.forName("UTF-8");
    }
    
    DetachableReader(final Reader target) {
        this.target = target;
    }
    
    @Override
    public int read(final CharBuffer target) throws IOException {
        return target.read(target);
    }
    
    @Override
    public int read() throws IOException {
        return this.target.read();
    }
    
    @Override
    public int read(final char[] cbuf) throws IOException {
        return this.target.read(cbuf);
    }
    
    @Override
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        return this.target.read(cbuf, off, len);
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return this.target.skip(n);
    }
    
    @Override
    public boolean ready() throws IOException {
        return this.target.ready();
    }
    
    @Override
    public void close() throws IOException {
        this.target.close();
    }
    
    public void detach() {
        final MemoryBlob blob = Blobs.createMemoryBlob();
        final Writer out = new OutputStreamWriter(blob.getOutputStream(), DetachableReader.UTF8);
        final char[] buffer = new char[2048];
        try {
            int c;
            while ((c = this.target.read(buffer)) != -1) {
                out.write(buffer, 0, c);
            }
            out.close();
        }
        catch (final IOException ex) {
            throw new OMException((Throwable)ex);
        }
        this.target = new InputStreamReader(blob.readOnce(), DetachableReader.UTF8);
    }
}
