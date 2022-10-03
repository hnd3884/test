package org.bouncycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

public class CertificateURL
{
    protected short type;
    protected Vector urlAndHashList;
    
    public CertificateURL(final short type, final Vector urlAndHashList) {
        if (!CertChainType.isValid(type)) {
            throw new IllegalArgumentException("'type' is not a valid CertChainType value");
        }
        if (urlAndHashList == null || urlAndHashList.isEmpty()) {
            throw new IllegalArgumentException("'urlAndHashList' must have length > 0");
        }
        this.type = type;
        this.urlAndHashList = urlAndHashList;
    }
    
    public short getType() {
        return this.type;
    }
    
    public Vector getURLAndHashList() {
        return this.urlAndHashList;
    }
    
    public void encode(final OutputStream outputStream) throws IOException {
        TlsUtils.writeUint8(this.type, outputStream);
        final ListBuffer16 listBuffer16 = new ListBuffer16();
        for (int i = 0; i < this.urlAndHashList.size(); ++i) {
            ((URLAndHash)this.urlAndHashList.elementAt(i)).encode(listBuffer16);
        }
        listBuffer16.encodeTo(outputStream);
    }
    
    public static CertificateURL parse(final TlsContext tlsContext, final InputStream inputStream) throws IOException {
        final short uint8 = TlsUtils.readUint8(inputStream);
        if (!CertChainType.isValid(uint8)) {
            throw new TlsFatalAlert((short)50);
        }
        final int uint9 = TlsUtils.readUint16(inputStream);
        if (uint9 < 1) {
            throw new TlsFatalAlert((short)50);
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(TlsUtils.readFully(uint9, inputStream));
        final Vector vector = new Vector();
        while (byteArrayInputStream.available() > 0) {
            vector.addElement(URLAndHash.parse(tlsContext, byteArrayInputStream));
        }
        return new CertificateURL(uint8, vector);
    }
    
    class ListBuffer16 extends ByteArrayOutputStream
    {
        ListBuffer16() throws IOException {
            TlsUtils.writeUint16(0, this);
        }
        
        void encodeTo(final OutputStream outputStream) throws IOException {
            final int n = this.count - 2;
            TlsUtils.checkUint16(n);
            TlsUtils.writeUint16(n, this.buf, 0);
            outputStream.write(this.buf, 0, this.count);
            this.buf = null;
        }
    }
}
