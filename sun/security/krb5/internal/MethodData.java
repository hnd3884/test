package sun.security.krb5.internal;

import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;

public class MethodData
{
    private int methodType;
    private byte[] methodData;
    
    public MethodData(final int methodType, final byte[] array) {
        this.methodData = null;
        this.methodType = methodType;
        if (array != null) {
            this.methodData = array.clone();
        }
    }
    
    public MethodData(final DerValue derValue) throws Asn1Exception, IOException {
        this.methodData = null;
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if ((derValue2.getTag() & 0x1F) != 0x0) {
            throw new Asn1Exception(906);
        }
        this.methodType = derValue2.getData().getBigInteger().intValue();
        if (derValue.getData().available() > 0) {
            final DerValue derValue3 = derValue.getData().getDerValue();
            if ((derValue3.getTag() & 0x1F) != 0x1) {
                throw new Asn1Exception(906);
            }
            this.methodData = derValue3.getData().getOctetString();
        }
        if (derValue.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(BigInteger.valueOf(this.methodType));
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        if (this.methodData != null) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            derOutputStream3.putOctetString(this.methodData);
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream3);
        }
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write((byte)48, derOutputStream);
        return derOutputStream4.toByteArray();
    }
}
