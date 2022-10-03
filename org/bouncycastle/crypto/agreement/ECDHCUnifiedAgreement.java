package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.crypto.params.ECDHUPublicParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ECDHUPrivateParameters;

public class ECDHCUnifiedAgreement
{
    private ECDHUPrivateParameters privParams;
    
    public void init(final CipherParameters cipherParameters) {
        this.privParams = (ECDHUPrivateParameters)cipherParameters;
    }
    
    public int getFieldSize() {
        return (this.privParams.getStaticPrivateKey().getParameters().getCurve().getFieldSize() + 7) / 8;
    }
    
    public byte[] calculateAgreement(final CipherParameters cipherParameters) {
        final ECDHUPublicParameters ecdhuPublicParameters = (ECDHUPublicParameters)cipherParameters;
        final ECDHCBasicAgreement ecdhcBasicAgreement = new ECDHCBasicAgreement();
        final ECDHCBasicAgreement ecdhcBasicAgreement2 = new ECDHCBasicAgreement();
        ecdhcBasicAgreement.init(this.privParams.getStaticPrivateKey());
        final BigInteger calculateAgreement = ecdhcBasicAgreement.calculateAgreement(ecdhuPublicParameters.getStaticPublicKey());
        ecdhcBasicAgreement2.init(this.privParams.getEphemeralPrivateKey());
        return Arrays.concatenate(BigIntegers.asUnsignedByteArray(this.getFieldSize(), ecdhcBasicAgreement2.calculateAgreement(ecdhuPublicParameters.getEphemeralPublicKey())), BigIntegers.asUnsignedByteArray(this.getFieldSize(), calculateAgreement));
    }
}
