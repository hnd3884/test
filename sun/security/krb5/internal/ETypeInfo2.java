package sun.security.krb5.internal;

import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;

public class ETypeInfo2
{
    private int etype;
    private String saltStr;
    private byte[] s2kparams;
    private static final byte TAG_TYPE = 0;
    private static final byte TAG_VALUE1 = 1;
    private static final byte TAG_VALUE2 = 2;
    
    private ETypeInfo2() {
        this.saltStr = null;
        this.s2kparams = null;
    }
    
    public ETypeInfo2(final int etype, final String saltStr, final byte[] array) {
        this.saltStr = null;
        this.s2kparams = null;
        this.etype = etype;
        this.saltStr = saltStr;
        if (array != null) {
            this.s2kparams = array.clone();
        }
    }
    
    public Object clone() {
        final ETypeInfo2 eTypeInfo2 = new ETypeInfo2();
        eTypeInfo2.etype = this.etype;
        eTypeInfo2.saltStr = this.saltStr;
        if (this.s2kparams != null) {
            eTypeInfo2.s2kparams = new byte[this.s2kparams.length];
            System.arraycopy(this.s2kparams, 0, eTypeInfo2.s2kparams, 0, this.s2kparams.length);
        }
        return eTypeInfo2;
    }
    
    public ETypeInfo2(final DerValue derValue) throws Asn1Exception, IOException {
        this.saltStr = null;
        this.s2kparams = null;
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if ((derValue2.getTag() & 0x1F) != 0x0) {
            throw new Asn1Exception(906);
        }
        this.etype = derValue2.getData().getBigInteger().intValue();
        if (derValue.getData().available() > 0 && (derValue.getData().peekByte() & 0x1F) == 0x1) {
            this.saltStr = new KerberosString(derValue.getData().getDerValue().getData().getDerValue()).toString();
        }
        if (derValue.getData().available() > 0 && (derValue.getData().peekByte() & 0x1F) == 0x2) {
            this.s2kparams = derValue.getData().getDerValue().getData().getOctetString();
        }
        if (derValue.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(this.etype);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        if (this.saltStr != null) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            derOutputStream3.putDerValue(new KerberosString(this.saltStr).toDerValue());
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream3);
        }
        if (this.s2kparams != null) {
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            derOutputStream4.putOctetString(this.s2kparams);
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)2), derOutputStream4);
        }
        final DerOutputStream derOutputStream5 = new DerOutputStream();
        derOutputStream5.write((byte)48, derOutputStream);
        return derOutputStream5.toByteArray();
    }
    
    public int getEType() {
        return this.etype;
    }
    
    public String getSalt() {
        return this.saltStr;
    }
    
    public byte[] getParams() {
        return (byte[])((this.s2kparams == null) ? null : ((byte[])this.s2kparams.clone()));
    }
}
