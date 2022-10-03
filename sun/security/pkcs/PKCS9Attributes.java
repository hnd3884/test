package sun.security.pkcs;

import sun.security.util.DerEncoder;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.ObjectIdentifier;
import java.util.Hashtable;

public class PKCS9Attributes
{
    private final Hashtable<ObjectIdentifier, PKCS9Attribute> attributes;
    private final Hashtable<ObjectIdentifier, ObjectIdentifier> permittedAttributes;
    private final byte[] derEncoding;
    private boolean ignoreUnsupportedAttributes;
    
    public PKCS9Attributes(final ObjectIdentifier[] array, final DerInputStream derInputStream) throws IOException {
        this.attributes = new Hashtable<ObjectIdentifier, PKCS9Attribute>(3);
        this.ignoreUnsupportedAttributes = false;
        if (array != null) {
            this.permittedAttributes = new Hashtable<ObjectIdentifier, ObjectIdentifier>(array.length);
            for (int i = 0; i < array.length; ++i) {
                this.permittedAttributes.put(array[i], array[i]);
            }
        }
        else {
            this.permittedAttributes = null;
        }
        this.derEncoding = this.decode(derInputStream);
    }
    
    public PKCS9Attributes(final DerInputStream derInputStream) throws IOException {
        this(derInputStream, false);
    }
    
    public PKCS9Attributes(final DerInputStream derInputStream, final boolean ignoreUnsupportedAttributes) throws IOException {
        this.attributes = new Hashtable<ObjectIdentifier, PKCS9Attribute>(3);
        this.ignoreUnsupportedAttributes = false;
        this.ignoreUnsupportedAttributes = ignoreUnsupportedAttributes;
        this.derEncoding = this.decode(derInputStream);
        this.permittedAttributes = null;
    }
    
    public PKCS9Attributes(final PKCS9Attribute[] array) throws IllegalArgumentException, IOException {
        this.attributes = new Hashtable<ObjectIdentifier, PKCS9Attribute>(3);
        this.ignoreUnsupportedAttributes = false;
        for (int i = 0; i < array.length; ++i) {
            final ObjectIdentifier oid = array[i].getOID();
            if (this.attributes.containsKey(oid)) {
                throw new IllegalArgumentException("PKCSAttribute " + array[i].getOID() + " duplicated while constructing PKCS9Attributes.");
            }
            this.attributes.put(oid, array[i]);
        }
        this.derEncoding = this.generateDerEncoding();
        this.permittedAttributes = null;
    }
    
    private byte[] decode(final DerInputStream derInputStream) throws IOException {
        final byte[] byteArray = derInputStream.getDerValue().toByteArray();
        byteArray[0] = 49;
        final DerValue[] set = new DerInputStream(byteArray).getSet(3, true);
        boolean b = true;
        for (int i = 0; i < set.length; ++i) {
            PKCS9Attribute pkcs9Attribute;
            try {
                pkcs9Attribute = new PKCS9Attribute(set[i]);
            }
            catch (final ParsingException ex) {
                if (this.ignoreUnsupportedAttributes) {
                    b = false;
                    continue;
                }
                throw ex;
            }
            final ObjectIdentifier oid = pkcs9Attribute.getOID();
            if (this.attributes.get(oid) != null) {
                throw new IOException("Duplicate PKCS9 attribute: " + oid);
            }
            if (this.permittedAttributes != null && !this.permittedAttributes.containsKey(oid)) {
                throw new IOException("Attribute " + oid + " not permitted in this attribute set");
            }
            this.attributes.put(oid, pkcs9Attribute);
        }
        return b ? byteArray : this.generateDerEncoding();
    }
    
    public void encode(final byte b, final OutputStream outputStream) throws IOException {
        outputStream.write(b);
        outputStream.write(this.derEncoding, 1, this.derEncoding.length - 1);
    }
    
    private byte[] generateDerEncoding() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putOrderedSetOf((byte)49, castToDerEncoder(this.attributes.values().toArray()));
        return derOutputStream.toByteArray();
    }
    
    public byte[] getDerEncoding() throws IOException {
        return this.derEncoding.clone();
    }
    
    public PKCS9Attribute getAttribute(final ObjectIdentifier objectIdentifier) {
        return this.attributes.get(objectIdentifier);
    }
    
    public PKCS9Attribute getAttribute(final String s) {
        return this.attributes.get(PKCS9Attribute.getOID(s));
    }
    
    public PKCS9Attribute[] getAttributes() {
        final PKCS9Attribute[] array = new PKCS9Attribute[this.attributes.size()];
        for (int n = 0, n2 = 1; n2 < PKCS9Attribute.PKCS9_OIDS.length && n < array.length; ++n2) {
            array[n] = this.getAttribute(PKCS9Attribute.PKCS9_OIDS[n2]);
            if (array[n] != null) {
                ++n;
            }
        }
        return array;
    }
    
    public Object getAttributeValue(final ObjectIdentifier objectIdentifier) throws IOException {
        try {
            return this.getAttribute(objectIdentifier).getValue();
        }
        catch (final NullPointerException ex) {
            throw new IOException("No value found for attribute " + objectIdentifier);
        }
    }
    
    public Object getAttributeValue(final String s) throws IOException {
        final ObjectIdentifier oid = PKCS9Attribute.getOID(s);
        if (oid == null) {
            throw new IOException("Attribute name " + s + " not recognized or not supported.");
        }
        return this.getAttributeValue(oid);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(200);
        sb.append("PKCS9 Attributes: [\n\t");
        int n = 1;
        for (int i = 1; i < PKCS9Attribute.PKCS9_OIDS.length; ++i) {
            final PKCS9Attribute attribute = this.getAttribute(PKCS9Attribute.PKCS9_OIDS[i]);
            if (attribute != null) {
                if (n != 0) {
                    n = 0;
                }
                else {
                    sb.append(";\n\t");
                }
                sb.append(attribute.toString());
            }
        }
        sb.append("\n\t] (end PKCS9 Attributes)");
        return sb.toString();
    }
    
    static DerEncoder[] castToDerEncoder(final Object[] array) {
        final DerEncoder[] array2 = new DerEncoder[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = (DerEncoder)array[i];
        }
        return array2;
    }
}
