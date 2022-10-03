package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.crypto.params.DHUPublicParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.DHUPrivateParameters;

public class DHUnifiedAgreement
{
    private DHUPrivateParameters privParams;
    
    public void init(final CipherParameters cipherParameters) {
        this.privParams = (DHUPrivateParameters)cipherParameters;
    }
    
    public int getFieldSize() {
        return (this.privParams.getStaticPrivateKey().getParameters().getP().bitLength() + 7) / 8;
    }
    
    public byte[] calculateAgreement(final CipherParameters cipherParameters) {
        final DHUPublicParameters dhuPublicParameters = (DHUPublicParameters)cipherParameters;
        final DHBasicAgreement dhBasicAgreement = new DHBasicAgreement();
        final DHBasicAgreement dhBasicAgreement2 = new DHBasicAgreement();
        dhBasicAgreement.init(this.privParams.getStaticPrivateKey());
        final BigInteger calculateAgreement = dhBasicAgreement.calculateAgreement(dhuPublicParameters.getStaticPublicKey());
        dhBasicAgreement2.init(this.privParams.getEphemeralPrivateKey());
        return Arrays.concatenate(BigIntegers.asUnsignedByteArray(this.getFieldSize(), dhBasicAgreement2.calculateAgreement(dhuPublicParameters.getEphemeralPublicKey())), BigIntegers.asUnsignedByteArray(this.getFieldSize(), calculateAgreement));
    }
}
