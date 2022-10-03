package sun.security.x509;

import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class CertificatePolicyId
{
    private ObjectIdentifier id;
    
    public CertificatePolicyId(final ObjectIdentifier id) {
        this.id = id;
    }
    
    public CertificatePolicyId(final DerValue derValue) throws IOException {
        this.id = derValue.getOID();
    }
    
    public ObjectIdentifier getIdentifier() {
        return this.id;
    }
    
    @Override
    public String toString() {
        return "CertificatePolicyId: [" + this.id.toString() + "]\n";
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        derOutputStream.putOID(this.id);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof CertificatePolicyId && this.id.equals((Object)((CertificatePolicyId)o).getIdentifier());
    }
    
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
