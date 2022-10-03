package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class DERGenerator extends ASN1Generator
{
    private boolean _tagged;
    private boolean _isExplicit;
    private int _tagNo;
    
    protected DERGenerator(final OutputStream outputStream) {
        super(outputStream);
        this._tagged = false;
    }
    
    public DERGenerator(final OutputStream outputStream, final int tagNo, final boolean isExplicit) {
        super(outputStream);
        this._tagged = false;
        this._tagged = true;
        this._isExplicit = isExplicit;
        this._tagNo = tagNo;
    }
    
    private void writeLength(final OutputStream outputStream, final int n) throws IOException {
        if (n > 127) {
            int n2 = 1;
            int n3 = n;
            while ((n3 >>>= 8) != 0) {
                ++n2;
            }
            outputStream.write((byte)(n2 | 0x80));
            for (int i = (n2 - 1) * 8; i >= 0; i -= 8) {
                outputStream.write((byte)(n >> i));
            }
        }
        else {
            outputStream.write((byte)n);
        }
    }
    
    void writeDEREncoded(final OutputStream outputStream, final int n, final byte[] array) throws IOException {
        outputStream.write(n);
        this.writeLength(outputStream, array.length);
        outputStream.write(array);
    }
    
    void writeDEREncoded(final int n, final byte[] array) throws IOException {
        if (this._tagged) {
            final int n2 = this._tagNo | 0x80;
            if (this._isExplicit) {
                final int n3 = this._tagNo | 0x20 | 0x80;
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                this.writeDEREncoded(byteArrayOutputStream, n, array);
                this.writeDEREncoded(this._out, n3, byteArrayOutputStream.toByteArray());
            }
            else if ((n & 0x20) != 0x0) {
                this.writeDEREncoded(this._out, n2 | 0x20, array);
            }
            else {
                this.writeDEREncoded(this._out, n2, array);
            }
        }
        else {
            this.writeDEREncoded(this._out, n, array);
        }
    }
}
