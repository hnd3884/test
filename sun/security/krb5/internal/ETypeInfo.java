package sun.security.krb5.internal;

import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;

public class ETypeInfo
{
    private int etype;
    private String salt;
    private static final byte TAG_TYPE = 0;
    private static final byte TAG_VALUE = 1;
    
    private ETypeInfo() {
        this.salt = null;
    }
    
    public ETypeInfo(final int etype, final String salt) {
        this.salt = null;
        this.etype = etype;
        this.salt = salt;
    }
    
    public Object clone() {
        return new ETypeInfo(this.etype, this.salt);
    }
    
    public ETypeInfo(final DerValue derValue) throws Asn1Exception, IOException {
        this.salt = null;
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if ((derValue2.getTag() & 0x1F) != 0x0) {
            throw new Asn1Exception(906);
        }
        this.etype = derValue2.getData().getBigInteger().intValue();
        if (derValue.getData().available() > 0) {
            final DerValue derValue3 = derValue.getData().getDerValue();
            if ((derValue3.getTag() & 0x1F) == 0x1) {
                final byte[] octetString = derValue3.getData().getOctetString();
                if (KerberosString.MSNAME) {
                    this.salt = new String(octetString, "UTF8");
                }
                else {
                    this.salt = new String(octetString);
                }
            }
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
        if (this.salt != null) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            if (KerberosString.MSNAME) {
                derOutputStream3.putOctetString(this.salt.getBytes("UTF8"));
            }
            else {
                derOutputStream3.putOctetString(this.salt.getBytes());
            }
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream3);
        }
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write((byte)48, derOutputStream);
        return derOutputStream4.toByteArray();
    }
    
    public int getEType() {
        return this.etype;
    }
    
    public String getSalt() {
        return this.salt;
    }
}
