package org.bouncycastle.jcajce.provider.symmetric.util;

import org.bouncycastle.crypto.CipherParameters;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKey;
import java.security.spec.KeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class PBESecretKeyFactory extends BaseSecretKeyFactory implements PBE
{
    private boolean forCipher;
    private int scheme;
    private int digest;
    private int keySize;
    private int ivSize;
    
    public PBESecretKeyFactory(final String s, final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean forCipher, final int scheme, final int digest, final int keySize, final int ivSize) {
        super(s, asn1ObjectIdentifier);
        this.forCipher = forCipher;
        this.scheme = scheme;
        this.digest = digest;
        this.keySize = keySize;
        this.ivSize = ivSize;
    }
    
    @Override
    protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
        if (!(keySpec instanceof PBEKeySpec)) {
            throw new InvalidKeySpecException("Invalid KeySpec");
        }
        final PBEKeySpec pbeKeySpec = (PBEKeySpec)keySpec;
        if (pbeKeySpec.getSalt() == null) {
            return new BCPBEKey(this.algName, this.algOid, this.scheme, this.digest, this.keySize, this.ivSize, pbeKeySpec, null);
        }
        CipherParameters cipherParameters;
        if (this.forCipher) {
            cipherParameters = Util.makePBEParameters(pbeKeySpec, this.scheme, this.digest, this.keySize, this.ivSize);
        }
        else {
            cipherParameters = Util.makePBEMacParameters(pbeKeySpec, this.scheme, this.digest, this.keySize);
        }
        return new BCPBEKey(this.algName, this.algOid, this.scheme, this.digest, this.keySize, this.ivSize, pbeKeySpec, cipherParameters);
    }
}
