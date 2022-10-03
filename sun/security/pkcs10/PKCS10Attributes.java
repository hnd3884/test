package sun.security.pkcs10;

import java.util.Collections;
import java.util.Collection;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.util.DerInputStream;
import java.util.Hashtable;
import sun.security.util.DerEncoder;

public class PKCS10Attributes implements DerEncoder
{
    private Hashtable<String, PKCS10Attribute> map;
    
    public PKCS10Attributes() {
        this.map = new Hashtable<String, PKCS10Attribute>(3);
    }
    
    public PKCS10Attributes(final PKCS10Attribute[] array) {
        this.map = new Hashtable<String, PKCS10Attribute>(3);
        for (int i = 0; i < array.length; ++i) {
            this.map.put(array[i].getAttributeId().toString(), array[i]);
        }
    }
    
    public PKCS10Attributes(final DerInputStream derInputStream) throws IOException {
        this.map = new Hashtable<String, PKCS10Attribute>(3);
        final DerValue[] set = derInputStream.getSet(3, true);
        if (set == null) {
            throw new IOException("Illegal encoding of attributes");
        }
        for (int i = 0; i < set.length; ++i) {
            final PKCS10Attribute pkcs10Attribute = new PKCS10Attribute(set[i]);
            this.map.put(pkcs10Attribute.getAttributeId().toString(), pkcs10Attribute);
        }
    }
    
    public void encode(final OutputStream outputStream) throws IOException {
        this.derEncode(outputStream);
    }
    
    @Override
    public void derEncode(final OutputStream outputStream) throws IOException {
        final PKCS10Attribute[] array = this.map.values().toArray(new PKCS10Attribute[this.map.size()]);
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putOrderedSetOf(DerValue.createTag((byte)(-128), true, (byte)0), array);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    public void setAttribute(final String s, final Object o) {
        if (o instanceof PKCS10Attribute) {
            this.map.put(s, (PKCS10Attribute)o);
        }
    }
    
    public Object getAttribute(final String s) {
        return this.map.get(s);
    }
    
    public void deleteAttribute(final String s) {
        this.map.remove(s);
    }
    
    public Enumeration<PKCS10Attribute> getElements() {
        return this.map.elements();
    }
    
    public Collection<PKCS10Attribute> getAttributes() {
        return Collections.unmodifiableCollection((Collection<? extends PKCS10Attribute>)this.map.values());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PKCS10Attributes)) {
            return false;
        }
        final Collection<PKCS10Attribute> attributes = ((PKCS10Attributes)o).getAttributes();
        final PKCS10Attribute[] array = attributes.toArray(new PKCS10Attribute[attributes.size()]);
        final int length = array.length;
        if (length != this.map.size()) {
            return false;
        }
        for (final PKCS10Attribute pkcs10Attribute : array) {
            final String string = pkcs10Attribute.getAttributeId().toString();
            if (string == null) {
                return false;
            }
            final PKCS10Attribute pkcs10Attribute2 = this.map.get(string);
            if (pkcs10Attribute2 == null) {
                return false;
            }
            if (!pkcs10Attribute2.equals(pkcs10Attribute)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return this.map.hashCode();
    }
    
    @Override
    public String toString() {
        return this.map.size() + "\n" + this.map.toString();
    }
}
