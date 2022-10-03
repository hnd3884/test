package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.io.OutputStream;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.pkcs.MacData;

class MacDataGenerator
{
    private PKCS12MacCalculatorBuilder builder;
    
    MacDataGenerator(final PKCS12MacCalculatorBuilder builder) {
        this.builder = builder;
    }
    
    public MacData build(final char[] array, final byte[] array2) throws PKCSException {
        MacCalculator build;
        try {
            build = this.builder.build(array);
            final OutputStream outputStream = build.getOutputStream();
            outputStream.write(array2);
            outputStream.close();
        }
        catch (final Exception ex) {
            throw new PKCSException("unable to process data: " + ex.getMessage(), ex);
        }
        final AlgorithmIdentifier algorithmIdentifier = build.getAlgorithmIdentifier();
        final DigestInfo digestInfo = new DigestInfo(this.builder.getDigestAlgorithmIdentifier(), build.getMac());
        final PKCS12PBEParams instance = PKCS12PBEParams.getInstance((Object)algorithmIdentifier.getParameters());
        return new MacData(digestInfo, instance.getIV(), instance.getIterations().intValue());
    }
}
