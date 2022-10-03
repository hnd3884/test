package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class X962Parameters extends ASN1Object implements ASN1Choice
{
    private ASN1Primitive params;
    
    public static X962Parameters getInstance(final Object o) {
        if (o == null || o instanceof X962Parameters) {
            return (X962Parameters)o;
        }
        if (o instanceof ASN1Primitive) {
            return new X962Parameters((ASN1Primitive)o);
        }
        if (o instanceof byte[]) {
            try {
                return new X962Parameters(ASN1Primitive.fromByteArray((byte[])o));
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("unable to parse encoded data: " + ex.getMessage());
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance()");
    }
    
    public static X962Parameters getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public X962Parameters(final X9ECParameters x9ECParameters) {
        this.params = null;
        this.params = x9ECParameters.toASN1Primitive();
    }
    
    public X962Parameters(final ASN1ObjectIdentifier params) {
        this.params = null;
        this.params = params;
    }
    
    public X962Parameters(final ASN1Null params) {
        this.params = null;
        this.params = params;
    }
    
    @Deprecated
    public X962Parameters(final ASN1Primitive params) {
        this.params = null;
        this.params = params;
    }
    
    public boolean isNamedCurve() {
        return this.params instanceof ASN1ObjectIdentifier;
    }
    
    public boolean isImplicitlyCA() {
        return this.params instanceof ASN1Null;
    }
    
    public ASN1Primitive getParameters() {
        return this.params;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.params;
    }
}
