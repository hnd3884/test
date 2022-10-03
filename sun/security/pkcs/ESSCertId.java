package sun.security.pkcs;

import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.x509.SerialNumber;
import sun.security.x509.GeneralNames;
import sun.misc.HexDumpEncoder;

class ESSCertId
{
    private static volatile HexDumpEncoder hexDumper;
    private byte[] certHash;
    private GeneralNames issuer;
    private SerialNumber serialNumber;
    
    ESSCertId(final DerValue derValue) throws IOException {
        this.certHash = derValue.data.getDerValue().toByteArray();
        if (derValue.data.available() > 0) {
            final DerValue derValue2 = derValue.data.getDerValue();
            this.issuer = new GeneralNames(derValue2.data.getDerValue());
            this.serialNumber = new SerialNumber(derValue2.data.getDerValue());
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("[\n\tCertificate hash (SHA-1):\n");
        if (ESSCertId.hexDumper == null) {
            ESSCertId.hexDumper = new HexDumpEncoder();
        }
        sb.append(ESSCertId.hexDumper.encode(this.certHash));
        if (this.issuer != null && this.serialNumber != null) {
            sb.append("\n\tIssuer: " + this.issuer + "\n");
            sb.append("\t" + this.serialNumber);
        }
        sb.append("\n]");
        return sb.toString();
    }
}
