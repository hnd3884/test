package org.bouncycastle.cms;

import java.io.FilterInputStream;
import java.io.IOException;
import org.bouncycastle.util.io.Streams;
import java.io.BufferedInputStream;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class CMSTypedStream
{
    private static final int BUF_SIZ = 32768;
    private final ASN1ObjectIdentifier _oid;
    protected InputStream _in;
    
    public CMSTypedStream(final InputStream inputStream) {
        this(PKCSObjectIdentifiers.data.getId(), inputStream, 32768);
    }
    
    public CMSTypedStream(final String s, final InputStream inputStream) {
        this(new ASN1ObjectIdentifier(s), inputStream, 32768);
    }
    
    public CMSTypedStream(final String s, final InputStream inputStream, final int n) {
        this(new ASN1ObjectIdentifier(s), inputStream, n);
    }
    
    public CMSTypedStream(final ASN1ObjectIdentifier asn1ObjectIdentifier, final InputStream inputStream) {
        this(asn1ObjectIdentifier, inputStream, 32768);
    }
    
    public CMSTypedStream(final ASN1ObjectIdentifier oid, final InputStream inputStream, final int n) {
        this._oid = oid;
        this._in = new FullReaderStream(new BufferedInputStream(inputStream, n));
    }
    
    protected CMSTypedStream(final ASN1ObjectIdentifier oid) {
        this._oid = oid;
    }
    
    public ASN1ObjectIdentifier getContentType() {
        return this._oid;
    }
    
    public InputStream getContentStream() {
        return this._in;
    }
    
    public void drain() throws IOException {
        Streams.drain(this._in);
        this._in.close();
    }
    
    private static class FullReaderStream extends FilterInputStream
    {
        FullReaderStream(final InputStream inputStream) {
            super(inputStream);
        }
        
        @Override
        public int read(final byte[] array, final int n, final int n2) throws IOException {
            final int fully = Streams.readFully(super.in, array, n, n2);
            return (fully > 0) ? fully : -1;
        }
    }
}
