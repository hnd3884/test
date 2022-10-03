package sun.security.krb5.internal;

import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;

public class PAEncTSEnc
{
    public KerberosTime pATimeStamp;
    public Integer pAUSec;
    
    public PAEncTSEnc(final KerberosTime paTimeStamp, final Integer pauSec) {
        this.pATimeStamp = paTimeStamp;
        this.pAUSec = pauSec;
    }
    
    public PAEncTSEnc() {
        final KerberosTime now = KerberosTime.now();
        this.pATimeStamp = now;
        this.pAUSec = new Integer(now.getMicroSeconds());
    }
    
    public PAEncTSEnc(final DerValue derValue) throws Asn1Exception, IOException {
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        this.pATimeStamp = KerberosTime.parse(derValue.getData(), (byte)0, false);
        if (derValue.getData().available() > 0) {
            final DerValue derValue2 = derValue.getData().getDerValue();
            if ((derValue2.getTag() & 0x1F) != 0x1) {
                throw new Asn1Exception(906);
            }
            this.pAUSec = new Integer(derValue2.getData().getBigInteger().intValue());
        }
        if (derValue.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), this.pATimeStamp.asn1Encode());
        if (this.pAUSec != null) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            derOutputStream3.putInteger(BigInteger.valueOf(this.pAUSec));
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream3);
        }
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write((byte)48, derOutputStream);
        return derOutputStream4.toByteArray();
    }
}
