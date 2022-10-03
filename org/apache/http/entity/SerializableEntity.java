package org.apache.http.entity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.http.util.Args;
import java.io.Serializable;

public class SerializableEntity extends AbstractHttpEntity
{
    private byte[] objSer;
    private Serializable objRef;
    
    public SerializableEntity(final Serializable ser, final boolean bufferize) throws IOException {
        Args.notNull(ser, "Source object");
        if (bufferize) {
            this.createBytes(ser);
        }
        else {
            this.objRef = ser;
        }
    }
    
    public SerializableEntity(final Serializable serializable) {
        Args.notNull(serializable, "Source object");
        this.objRef = serializable;
    }
    
    private void createBytes(final Serializable ser) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(ser);
        out.flush();
        this.objSer = baos.toByteArray();
    }
    
    @Override
    public InputStream getContent() throws IOException, IllegalStateException {
        if (this.objSer == null) {
            this.createBytes(this.objRef);
        }
        return new ByteArrayInputStream(this.objSer);
    }
    
    @Override
    public long getContentLength() {
        return (this.objSer == null) ? -1L : this.objSer.length;
    }
    
    @Override
    public boolean isRepeatable() {
        return true;
    }
    
    @Override
    public boolean isStreaming() {
        return this.objSer == null;
    }
    
    @Override
    public void writeTo(final OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        if (this.objSer == null) {
            final ObjectOutputStream out = new ObjectOutputStream(outStream);
            out.writeObject(this.objRef);
            out.flush();
        }
        else {
            outStream.write(this.objSer);
            outStream.flush();
        }
    }
}
