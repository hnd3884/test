package sun.security.x509;

import java.util.Collections;
import java.util.List;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.util.DerInputStream;
import java.util.Vector;

public class CertificatePolicySet
{
    private final Vector<CertificatePolicyId> ids;
    
    public CertificatePolicySet(final Vector<CertificatePolicyId> ids) {
        this.ids = ids;
    }
    
    public CertificatePolicySet(final DerInputStream derInputStream) throws IOException {
        this.ids = new Vector<CertificatePolicyId>();
        final DerValue[] sequence = derInputStream.getSequence(5);
        for (int i = 0; i < sequence.length; ++i) {
            this.ids.addElement(new CertificatePolicyId(sequence[i]));
        }
    }
    
    @Override
    public String toString() {
        return "CertificatePolicySet:[\n" + this.ids.toString() + "]\n";
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        for (int i = 0; i < this.ids.size(); ++i) {
            this.ids.elementAt(i).encode(derOutputStream2);
        }
        derOutputStream.write((byte)48, derOutputStream2);
    }
    
    public List<CertificatePolicyId> getCertPolicyIds() {
        return Collections.unmodifiableList((List<? extends CertificatePolicyId>)this.ids);
    }
}
