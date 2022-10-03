package sun.security.krb5.internal;

import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;

public class AuthorizationDataEntry implements Cloneable
{
    public int adType;
    public byte[] adData;
    
    private AuthorizationDataEntry() {
    }
    
    public AuthorizationDataEntry(final int adType, final byte[] adData) {
        this.adType = adType;
        this.adData = adData;
    }
    
    public Object clone() {
        final AuthorizationDataEntry authorizationDataEntry = new AuthorizationDataEntry();
        authorizationDataEntry.adType = this.adType;
        if (this.adData != null) {
            authorizationDataEntry.adData = new byte[this.adData.length];
            System.arraycopy(this.adData, 0, authorizationDataEntry.adData, 0, this.adData.length);
        }
        return authorizationDataEntry;
    }
    
    public AuthorizationDataEntry(final DerValue derValue) throws Asn1Exception, IOException {
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if ((derValue2.getTag() & 0x1F) != 0x0) {
            throw new Asn1Exception(906);
        }
        this.adType = derValue2.getData().getBigInteger().intValue();
        final DerValue derValue3 = derValue.getData().getDerValue();
        if ((derValue3.getTag() & 0x1F) != 0x1) {
            throw new Asn1Exception(906);
        }
        this.adData = derValue3.getData().getOctetString();
        if (derValue.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(this.adType);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.putOctetString(this.adData);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream3);
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write((byte)48, derOutputStream);
        return derOutputStream4.toByteArray();
    }
    
    public void writeEntry(final CCacheOutputStream cCacheOutputStream) throws IOException {
        cCacheOutputStream.write16(this.adType);
        cCacheOutputStream.write32(this.adData.length);
        cCacheOutputStream.write(this.adData, 0, this.adData.length);
    }
    
    @Override
    public String toString() {
        return "adType=" + this.adType + " adData.length=" + this.adData.length;
    }
}
