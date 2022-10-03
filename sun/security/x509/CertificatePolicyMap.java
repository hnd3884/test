package sun.security.x509;

import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;

public class CertificatePolicyMap
{
    private CertificatePolicyId issuerDomain;
    private CertificatePolicyId subjectDomain;
    
    public CertificatePolicyMap(final CertificatePolicyId issuerDomain, final CertificatePolicyId subjectDomain) {
        this.issuerDomain = issuerDomain;
        this.subjectDomain = subjectDomain;
    }
    
    public CertificatePolicyMap(final DerValue derValue) throws IOException {
        if (derValue.tag != 48) {
            throw new IOException("Invalid encoding for CertificatePolicyMap");
        }
        this.issuerDomain = new CertificatePolicyId(derValue.data.getDerValue());
        this.subjectDomain = new CertificatePolicyId(derValue.data.getDerValue());
    }
    
    public CertificatePolicyId getIssuerIdentifier() {
        return this.issuerDomain;
    }
    
    public CertificatePolicyId getSubjectIdentifier() {
        return this.subjectDomain;
    }
    
    @Override
    public String toString() {
        return "CertificatePolicyMap: [\nIssuerDomain:" + this.issuerDomain.toString() + "SubjectDomain:" + this.subjectDomain.toString() + "]\n";
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        this.issuerDomain.encode(derOutputStream2);
        this.subjectDomain.encode(derOutputStream2);
        derOutputStream.write((byte)48, derOutputStream2);
    }
}
