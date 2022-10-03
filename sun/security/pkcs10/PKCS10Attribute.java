package sun.security.pkcs10;

import java.io.OutputStream;
import java.io.IOException;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.util.DerEncoder;

public class PKCS10Attribute implements DerEncoder
{
    protected ObjectIdentifier attributeId;
    protected Object attributeValue;
    
    public PKCS10Attribute(final DerValue derValue) throws IOException {
        this.attributeId = null;
        this.attributeValue = null;
        final PKCS9Attribute pkcs9Attribute = new PKCS9Attribute(derValue);
        this.attributeId = pkcs9Attribute.getOID();
        this.attributeValue = pkcs9Attribute.getValue();
    }
    
    public PKCS10Attribute(final ObjectIdentifier attributeId, final Object attributeValue) {
        this.attributeId = null;
        this.attributeValue = null;
        this.attributeId = attributeId;
        this.attributeValue = attributeValue;
    }
    
    public PKCS10Attribute(final PKCS9Attribute pkcs9Attribute) {
        this.attributeId = null;
        this.attributeValue = null;
        this.attributeId = pkcs9Attribute.getOID();
        this.attributeValue = pkcs9Attribute.getValue();
    }
    
    @Override
    public void derEncode(final OutputStream outputStream) throws IOException {
        new PKCS9Attribute(this.attributeId, this.attributeValue).derEncode(outputStream);
    }
    
    public ObjectIdentifier getAttributeId() {
        return this.attributeId;
    }
    
    public Object getAttributeValue() {
        return this.attributeValue;
    }
    
    @Override
    public String toString() {
        return this.attributeValue.toString();
    }
}
