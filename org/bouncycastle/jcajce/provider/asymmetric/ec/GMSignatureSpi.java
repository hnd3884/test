package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.CryptoException;
import java.security.SignatureException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import java.security.PublicKey;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.spec.SM2ParameterSpec;
import java.security.AlgorithmParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import java.security.SignatureSpi;

public class GMSignatureSpi extends SignatureSpi
{
    private final JcaJceHelper helper;
    private AlgorithmParameters engineParams;
    private SM2ParameterSpec paramSpec;
    private final SM2Signer signer;
    
    GMSignatureSpi(final SM2Signer signer) {
        this.helper = new BCJcaJceHelper();
        this.signer = signer;
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        CipherParameters generatePublicKeyParameter = ECUtils.generatePublicKeyParameter(publicKey);
        if (this.paramSpec != null) {
            generatePublicKeyParameter = new ParametersWithID(generatePublicKeyParameter, this.paramSpec.getID());
        }
        this.signer.init(false, generatePublicKeyParameter);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        CipherParameters generatePrivateKeyParameter = ECUtil.generatePrivateKeyParameter(privateKey);
        if (this.appRandom != null) {
            generatePrivateKeyParameter = new ParametersWithRandom(generatePrivateKeyParameter, this.appRandom);
        }
        if (this.paramSpec != null) {
            this.signer.init(true, new ParametersWithID(generatePrivateKeyParameter, this.paramSpec.getID()));
        }
        else {
            this.signer.init(true, generatePrivateKeyParameter);
        }
    }
    
    @Override
    protected void engineUpdate(final byte b) throws SignatureException {
        this.signer.update(b);
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) throws SignatureException {
        this.signer.update(array, n, n2);
    }
    
    @Override
    protected byte[] engineSign() throws SignatureException {
        try {
            return this.signer.generateSignature();
        }
        catch (final CryptoException ex) {
            throw new SignatureException("unable to create signature: " + ex.getMessage());
        }
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws SignatureException {
        return this.signer.verifySignature(array);
    }
    
    @Override
    protected void engineSetParameter(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec instanceof SM2ParameterSpec) {
            this.paramSpec = (SM2ParameterSpec)algorithmParameterSpec;
            return;
        }
        throw new InvalidAlgorithmParameterException("only SM2ParameterSpec supported");
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.paramSpec != null) {
            try {
                (this.engineParams = this.helper.createAlgorithmParameters("PSS")).init(this.paramSpec);
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex.toString());
            }
        }
        return this.engineParams;
    }
    
    @Override
    protected void engineSetParameter(final String s, final Object o) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }
    
    @Override
    protected Object engineGetParameter(final String s) {
        throw new UnsupportedOperationException("engineGetParameter unsupported");
    }
    
    public static class sm3WithSM2 extends GMSignatureSpi
    {
        public sm3WithSM2() {
            super(new SM2Signer());
        }
    }
}
