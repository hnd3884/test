package org.apache.poi.openxml4j.opc.internal;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public final class MemoryPackagePartOutputStream extends OutputStream
{
    private MemoryPackagePart _part;
    private ByteArrayOutputStream _buff;
    
    public MemoryPackagePartOutputStream(final MemoryPackagePart part) {
        this._part = part;
        this._buff = new ByteArrayOutputStream();
    }
    
    @Override
    public void write(final int b) {
        this._buff.write(b);
    }
    
    @Override
    public void close() throws IOException {
        this.flush();
    }
    
    @Override
    public void flush() throws IOException {
        this._buff.flush();
        if (this._part.data != null) {
            final byte[] newArray = new byte[this._part.data.length + this._buff.size()];
            System.arraycopy(this._part.data, 0, newArray, 0, this._part.data.length);
            final byte[] buffArr = this._buff.toByteArray();
            System.arraycopy(buffArr, 0, newArray, this._part.data.length, buffArr.length);
            this._part.data = newArray;
        }
        else {
            this._part.data = this._buff.toByteArray();
        }
        this._buff.reset();
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) {
        this._buff.write(b, off, len);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this._buff.write(b);
    }
}
