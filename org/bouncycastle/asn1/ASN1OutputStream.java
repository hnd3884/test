package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

public class ASN1OutputStream
{
    private OutputStream os;
    
    public ASN1OutputStream(final OutputStream os) {
        this.os = os;
    }
    
    void writeLength(final int n) throws IOException {
        if (n > 127) {
            int n2 = 1;
            int n3 = n;
            while ((n3 >>>= 8) != 0) {
                ++n2;
            }
            this.write((byte)(n2 | 0x80));
            for (int i = (n2 - 1) * 8; i >= 0; i -= 8) {
                this.write((byte)(n >> i));
            }
        }
        else {
            this.write((byte)n);
        }
    }
    
    void write(final int n) throws IOException {
        this.os.write(n);
    }
    
    void write(final byte[] array) throws IOException {
        this.os.write(array);
    }
    
    void write(final byte[] array, final int n, final int n2) throws IOException {
        this.os.write(array, n, n2);
    }
    
    void writeEncoded(final int n, final byte[] array) throws IOException {
        this.write(n);
        this.writeLength(array.length);
        this.write(array);
    }
    
    void writeTag(final int n, int i) throws IOException {
        if (i < 31) {
            this.write(n | i);
        }
        else {
            this.write(n | 0x1F);
            if (i < 128) {
                this.write(i);
            }
            else {
                final byte[] array = new byte[5];
                int length = array.length;
                array[--length] = (byte)(i & 0x7F);
                do {
                    i >>= 7;
                    array[--length] = (byte)((i & 0x7F) | 0x80);
                } while (i > 127);
                this.write(array, length, array.length - length);
            }
        }
    }
    
    void writeEncoded(final int n, final int n2, final byte[] array) throws IOException {
        this.writeTag(n, n2);
        this.writeLength(array.length);
        this.write(array);
    }
    
    protected void writeNull() throws IOException {
        this.os.write(5);
        this.os.write(0);
    }
    
    public void writeObject(final ASN1Encodable asn1Encodable) throws IOException {
        if (asn1Encodable != null) {
            asn1Encodable.toASN1Primitive().encode(this);
            return;
        }
        throw new IOException("null object detected");
    }
    
    void writeImplicitObject(final ASN1Primitive asn1Primitive) throws IOException {
        if (asn1Primitive != null) {
            asn1Primitive.encode(new ImplicitOutputStream(this.os));
            return;
        }
        throw new IOException("null object detected");
    }
    
    public void close() throws IOException {
        this.os.close();
    }
    
    public void flush() throws IOException {
        this.os.flush();
    }
    
    ASN1OutputStream getDERSubStream() {
        return new DEROutputStream(this.os);
    }
    
    ASN1OutputStream getDLSubStream() {
        return new DLOutputStream(this.os);
    }
    
    private class ImplicitOutputStream extends ASN1OutputStream
    {
        private boolean first;
        
        public ImplicitOutputStream(final OutputStream outputStream) {
            super(outputStream);
            this.first = true;
        }
        
        public void write(final int n) throws IOException {
            if (this.first) {
                this.first = false;
            }
            else {
                super.write(n);
            }
        }
    }
}
