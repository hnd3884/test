package org.bouncycastle.crypto.util;

import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.util.Arrays;

public final class DERMacData
{
    private final byte[] macData;
    
    private DERMacData(final byte[] macData) {
        this.macData = macData;
    }
    
    public byte[] getMacData() {
        return Arrays.clone(this.macData);
    }
    
    public static final class Builder
    {
        private final Type type;
        private ASN1OctetString idU;
        private ASN1OctetString idV;
        private ASN1OctetString ephemDataU;
        private ASN1OctetString ephemDataV;
        private byte[] text;
        
        public Builder(final Type type, final byte[] array, final byte[] array2, final byte[] array3, final byte[] array4) {
            this.type = type;
            this.idU = DerUtil.getOctetString(array);
            this.idV = DerUtil.getOctetString(array2);
            this.ephemDataU = DerUtil.getOctetString(array3);
            this.ephemDataV = DerUtil.getOctetString(array4);
        }
        
        public Builder withText(final byte[] array) {
            this.text = DerUtil.toByteArray(new DERTaggedObject(false, 0, DerUtil.getOctetString(array)));
            return this;
        }
        
        public DERMacData build() {
            switch (this.type) {
                case UNILATERALU:
                case BILATERALU: {
                    return new DERMacData(this.concatenate(this.type.getHeader(), DerUtil.toByteArray(this.idU), DerUtil.toByteArray(this.idV), DerUtil.toByteArray(this.ephemDataU), DerUtil.toByteArray(this.ephemDataV), this.text), null);
                }
                case UNILATERALV:
                case BILATERALV: {
                    return new DERMacData(this.concatenate(this.type.getHeader(), DerUtil.toByteArray(this.idV), DerUtil.toByteArray(this.idU), DerUtil.toByteArray(this.ephemDataV), DerUtil.toByteArray(this.ephemDataU), this.text), null);
                }
                default: {
                    throw new IllegalStateException("Unknown type encountered in build");
                }
            }
        }
        
        private byte[] concatenate(final byte[] array, final byte[] array2, final byte[] array3, final byte[] array4, final byte[] array5, final byte[] array6) {
            return Arrays.concatenate(Arrays.concatenate(array, array2, array3), Arrays.concatenate(array4, array5, array6));
        }
    }
    
    public enum Type
    {
        UNILATERALU("KC_1_U"), 
        UNILATERALV("KC_1_V"), 
        BILATERALU("KC_2_U"), 
        BILATERALV("KC_2_V");
        
        private final String enc;
        
        private Type(final String enc) {
            this.enc = enc;
        }
        
        public byte[] getHeader() {
            return Strings.toByteArray(this.enc);
        }
    }
}
