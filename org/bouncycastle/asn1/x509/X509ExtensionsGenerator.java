package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Vector;
import java.util.Hashtable;

public class X509ExtensionsGenerator
{
    private Hashtable extensions;
    private Vector extOrdering;
    
    public X509ExtensionsGenerator() {
        this.extensions = new Hashtable();
        this.extOrdering = new Vector();
    }
    
    public void reset() {
        this.extensions = new Hashtable();
        this.extOrdering = new Vector();
    }
    
    public void addExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final ASN1Encodable asn1Encodable) {
        try {
            this.addExtension(asn1ObjectIdentifier, b, asn1Encodable.toASN1Primitive().getEncoded("DER"));
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("error encoding value: " + ex);
        }
    }
    
    public void addExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final byte[] array) {
        if (this.extensions.containsKey(asn1ObjectIdentifier)) {
            throw new IllegalArgumentException("extension " + asn1ObjectIdentifier + " already added");
        }
        this.extOrdering.addElement(asn1ObjectIdentifier);
        this.extensions.put(asn1ObjectIdentifier, new X509Extension(b, new DEROctetString(array)));
    }
    
    public boolean isEmpty() {
        return this.extOrdering.isEmpty();
    }
    
    public X509Extensions generate() {
        return new X509Extensions(this.extOrdering, this.extensions);
    }
}
