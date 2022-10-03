package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import org.bouncycastle.asn1.cms.ecc.ECCCMSSharedInfo;
import org.bouncycastle.util.Pack;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

class RFC5753KeyMaterialGenerator implements KeyMaterialGenerator
{
    public byte[] generateKDFMaterial(final AlgorithmIdentifier algorithmIdentifier, final int n, final byte[] array) {
        final ECCCMSSharedInfo ecccmsSharedInfo = new ECCCMSSharedInfo(algorithmIdentifier, array, Pack.intToBigEndian(n));
        try {
            return ecccmsSharedInfo.getEncoded("DER");
        }
        catch (final IOException ex) {
            throw new IllegalStateException("Unable to create KDF material: " + ex);
        }
    }
}
