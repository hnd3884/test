package org.bouncycastle.cert.crmf;

import java.io.OutputStream;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.asn1.DERBitString;
import java.io.IOException;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

class PKMACValueGenerator
{
    private PKMACBuilder builder;
    
    public PKMACValueGenerator(final PKMACBuilder builder) {
        this.builder = builder;
    }
    
    public PKMACValue generate(final char[] array, final SubjectPublicKeyInfo subjectPublicKeyInfo) throws CRMFException {
        final MacCalculator build = this.builder.build(array);
        final OutputStream outputStream = build.getOutputStream();
        try {
            outputStream.write(subjectPublicKeyInfo.getEncoded("DER"));
            outputStream.close();
        }
        catch (final IOException ex) {
            throw new CRMFException("exception encoding mac input: " + ex.getMessage(), ex);
        }
        return new PKMACValue(build.getAlgorithmIdentifier(), new DERBitString(build.getMac()));
    }
}
