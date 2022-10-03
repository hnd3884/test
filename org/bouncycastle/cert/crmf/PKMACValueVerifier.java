package org.bouncycastle.cert.crmf;

import java.io.OutputStream;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.Arrays;
import java.io.IOException;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.crmf.PKMACValue;

class PKMACValueVerifier
{
    private final PKMACBuilder builder;
    
    public PKMACValueVerifier(final PKMACBuilder builder) {
        this.builder = builder;
    }
    
    public boolean isValid(final PKMACValue pkmacValue, final char[] array, final SubjectPublicKeyInfo subjectPublicKeyInfo) throws CRMFException {
        this.builder.setParameters(PBMParameter.getInstance((Object)pkmacValue.getAlgId().getParameters()));
        final MacCalculator build = this.builder.build(array);
        final OutputStream outputStream = build.getOutputStream();
        try {
            outputStream.write(subjectPublicKeyInfo.getEncoded("DER"));
            outputStream.close();
        }
        catch (final IOException ex) {
            throw new CRMFException("exception encoding mac input: " + ex.getMessage(), ex);
        }
        return Arrays.areEqual(build.getMac(), pkmacValue.getValue().getBytes());
    }
}
