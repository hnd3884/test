package sun.security.pkcs;

import sun.security.util.DerValue;
import java.io.IOException;

public class SigningCertificateInfo
{
    private byte[] ber;
    private ESSCertId[] certId;
    
    public SigningCertificateInfo(final byte[] array) throws IOException {
        this.ber = null;
        this.certId = null;
        this.parse(array);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("[\n");
        for (int i = 0; i < this.certId.length; ++i) {
            sb.append(this.certId[i].toString());
        }
        sb.append("\n]");
        return sb.toString();
    }
    
    public void parse(final byte[] array) throws IOException {
        final DerValue derValue = new DerValue(array);
        if (derValue.tag != 48) {
            throw new IOException("Bad encoding for signingCertificate");
        }
        final DerValue[] sequence = derValue.data.getSequence(1);
        this.certId = new ESSCertId[sequence.length];
        for (int i = 0; i < sequence.length; ++i) {
            this.certId[i] = new ESSCertId(sequence[i]);
        }
        if (derValue.data.available() > 0) {
            final DerValue[] sequence2 = derValue.data.getSequence(1);
            for (int j = 0; j < sequence2.length; ++j) {}
        }
    }
}
