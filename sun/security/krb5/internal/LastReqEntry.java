package sun.security.krb5.internal;

import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;

public class LastReqEntry
{
    private int lrType;
    private KerberosTime lrValue;
    
    private LastReqEntry() {
    }
    
    public LastReqEntry(final int lrType, final KerberosTime lrValue) {
        this.lrType = lrType;
        this.lrValue = lrValue;
    }
    
    public LastReqEntry(final DerValue derValue) throws Asn1Exception, IOException {
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if ((derValue2.getTag() & 0x1F) != 0x0) {
            throw new Asn1Exception(906);
        }
        this.lrType = derValue2.getData().getBigInteger().intValue();
        this.lrValue = KerberosTime.parse(derValue.getData(), (byte)1, false);
        if (derValue.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(this.lrType);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), this.lrValue.asn1Encode());
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.write((byte)48, derOutputStream);
        return derOutputStream3.toByteArray();
    }
    
    public Object clone() {
        final LastReqEntry lastReqEntry = new LastReqEntry();
        lastReqEntry.lrType = this.lrType;
        lastReqEntry.lrValue = this.lrValue;
        return lastReqEntry;
    }
}
