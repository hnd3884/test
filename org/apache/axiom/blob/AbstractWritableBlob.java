package org.apache.axiom.blob;

import java.io.OutputStream;
import java.io.IOException;
import org.apache.axiom.ext.io.StreamCopyException;
import java.io.InputStream;

public abstract class AbstractWritableBlob implements WritableBlob
{
    public long readFrom(final InputStream in) throws StreamCopyException {
        OutputStream out;
        try {
            out = this.getOutputStream();
        }
        catch (final IOException ex) {
            throw new StreamCopyException(2, ex);
        }
        try {
            return IOUtil.copy(in, out, -1L);
        }
        finally {
            try {
                out.close();
            }
            catch (final IOException ex2) {
                throw new StreamCopyException(2, ex2);
            }
        }
    }
    
    public void writeTo(final OutputStream out) throws StreamCopyException {
        InputStream in;
        try {
            in = this.getInputStream();
        }
        catch (final IOException ex) {
            throw new StreamCopyException(1, ex);
        }
        try {
            IOUtil.copy(in, out, -1L);
        }
        finally {
            try {
                in.close();
            }
            catch (final IOException ex2) {
                throw new StreamCopyException(1, ex2);
            }
        }
    }
}
