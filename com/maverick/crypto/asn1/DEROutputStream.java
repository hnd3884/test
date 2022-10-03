package com.maverick.crypto.asn1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class DEROutputStream extends FilterOutputStream implements DERTags
{
    public DEROutputStream(final OutputStream outputStream) {
        super(outputStream);
    }
    
    private void b(final int n) throws IOException {
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
    
    void b(final int n, final byte[] array) throws IOException {
        this.write(n);
        this.b(array.length);
        this.write(array);
    }
    
    protected void writeNull() throws IOException {
        this.write(5);
        this.write(0);
    }
    
    public void writeObject(final Object o) throws IOException {
        if (o == null) {
            this.writeNull();
        }
        else if (o instanceof DERObject) {
            ((DERObject)o).encode(this);
        }
        else {
            if (!(o instanceof DEREncodable)) {
                throw new IOException("object not DEREncodable");
            }
            ((DEREncodable)o).getDERObject().encode(this);
        }
    }
}
