package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Vector;
import java.util.Hashtable;

public class ExtensionsGenerator
{
    private Hashtable extensions;
    private Vector extOrdering;
    
    public ExtensionsGenerator() {
        this.extensions = new Hashtable();
        this.extOrdering = new Vector();
    }
    
    public void reset() {
        this.extensions = new Hashtable();
        this.extOrdering = new Vector();
    }
    
    public void addExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final ASN1Encodable asn1Encodable) throws IOException {
        this.addExtension(asn1ObjectIdentifier, b, asn1Encodable.toASN1Primitive().getEncoded("DER"));
    }
    
    public void addExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final byte[] array) {
        if (this.extensions.containsKey(asn1ObjectIdentifier)) {
            throw new IllegalArgumentException("extension " + asn1ObjectIdentifier + " already added");
        }
        this.extOrdering.addElement(asn1ObjectIdentifier);
        this.extensions.put(asn1ObjectIdentifier, new Extension(asn1ObjectIdentifier, b, new DEROctetString(array)));
    }
    
    public void addExtension(final Extension extension) {
        if (this.extensions.containsKey(extension.getExtnId())) {
            throw new IllegalArgumentException("extension " + extension.getExtnId() + " already added");
        }
        this.extOrdering.addElement(extension.getExtnId());
        this.extensions.put(extension.getExtnId(), extension);
    }
    
    public boolean isEmpty() {
        return this.extOrdering.isEmpty();
    }
    
    public Extensions generate() {
        final Extension[] array = new Extension[this.extOrdering.size()];
        for (int i = 0; i != this.extOrdering.size(); ++i) {
            array[i] = (Extension)this.extensions.get(this.extOrdering.elementAt(i));
        }
        return new Extensions(array);
    }
}
