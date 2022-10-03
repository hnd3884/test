package org.bouncycastle.asn1.bc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class ObjectStoreIntegrityCheck extends ASN1Object implements ASN1Choice
{
    public static final int PBKD_MAC_CHECK = 0;
    private final int type;
    private final ASN1Object integrityCheck;
    
    public ObjectStoreIntegrityCheck(final PbkdMacIntegrityCheck pbkdMacIntegrityCheck) {
        this((ASN1Encodable)pbkdMacIntegrityCheck);
    }
    
    private ObjectStoreIntegrityCheck(final ASN1Encodable asn1Encodable) {
        if (asn1Encodable instanceof ASN1Sequence || asn1Encodable instanceof PbkdMacIntegrityCheck) {
            this.type = 0;
            this.integrityCheck = PbkdMacIntegrityCheck.getInstance(asn1Encodable);
            return;
        }
        throw new IllegalArgumentException("Unknown check object in integrity check.");
    }
    
    public static ObjectStoreIntegrityCheck getInstance(final Object o) {
        if (o instanceof ObjectStoreIntegrityCheck) {
            return (ObjectStoreIntegrityCheck)o;
        }
        if (o instanceof byte[]) {
            try {
                return new ObjectStoreIntegrityCheck(ASN1Primitive.fromByteArray((byte[])o));
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("Unable to parse integrity check details.");
            }
        }
        if (o != null) {
            return new ObjectStoreIntegrityCheck((ASN1Encodable)o);
        }
        return null;
    }
    
    public int getType() {
        return this.type;
    }
    
    public ASN1Object getIntegrityCheck() {
        return this.integrityCheck;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.integrityCheck.toASN1Primitive();
    }
}
