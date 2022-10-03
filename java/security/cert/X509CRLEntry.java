package java.security.cert;

import sun.security.x509.X509CRLEntryImpl;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;

public abstract class X509CRLEntry implements X509Extension
{
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof X509CRLEntry)) {
            return false;
        }
        try {
            final byte[] encoded = this.getEncoded();
            final byte[] encoded2 = ((X509CRLEntry)o).getEncoded();
            if (encoded.length != encoded2.length) {
                return false;
            }
            for (int i = 0; i < encoded.length; ++i) {
                if (encoded[i] != encoded2[i]) {
                    return false;
                }
            }
        }
        catch (final CRLException ex) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        try {
            final byte[] encoded = this.getEncoded();
            for (byte b = 1; b < encoded.length; ++b) {
                n += encoded[b] * b;
            }
        }
        catch (final CRLException ex) {
            return n;
        }
        return n;
    }
    
    public abstract byte[] getEncoded() throws CRLException;
    
    public abstract BigInteger getSerialNumber();
    
    public X500Principal getCertificateIssuer() {
        return null;
    }
    
    public abstract Date getRevocationDate();
    
    public abstract boolean hasExtensions();
    
    @Override
    public abstract String toString();
    
    public CRLReason getRevocationReason() {
        if (!this.hasExtensions()) {
            return null;
        }
        return X509CRLEntryImpl.getRevocationReason(this);
    }
}
