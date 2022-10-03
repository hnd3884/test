package org.bouncycastle.jce;

import java.util.Vector;
import java.util.Hashtable;
import org.bouncycastle.asn1.x500.X500Name;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1InputStream;
import java.security.Principal;
import org.bouncycastle.asn1.x509.X509Name;

public class X509Principal extends X509Name implements Principal
{
    private static ASN1Sequence readSequence(final ASN1InputStream asn1InputStream) throws IOException {
        try {
            return ASN1Sequence.getInstance(asn1InputStream.readObject());
        }
        catch (final IllegalArgumentException ex) {
            throw new IOException("not an ASN.1 Sequence: " + ex);
        }
    }
    
    public X509Principal(final byte[] array) throws IOException {
        super(readSequence(new ASN1InputStream(array)));
    }
    
    public X509Principal(final X509Name x509Name) {
        super((ASN1Sequence)x509Name.toASN1Primitive());
    }
    
    public X509Principal(final X500Name x500Name) {
        super((ASN1Sequence)x500Name.toASN1Primitive());
    }
    
    public X509Principal(final Hashtable hashtable) {
        super(hashtable);
    }
    
    public X509Principal(final Vector vector, final Hashtable hashtable) {
        super(vector, hashtable);
    }
    
    public X509Principal(final Vector vector, final Vector vector2) {
        super(vector, vector2);
    }
    
    public X509Principal(final String s) {
        super(s);
    }
    
    public X509Principal(final boolean b, final String s) {
        super(b, s);
    }
    
    public X509Principal(final boolean b, final Hashtable hashtable, final String s) {
        super(b, hashtable, s);
    }
    
    public String getName() {
        return this.toString();
    }
    
    @Override
    public byte[] getEncoded() {
        try {
            return this.getEncoded("DER");
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex.toString());
        }
    }
}
