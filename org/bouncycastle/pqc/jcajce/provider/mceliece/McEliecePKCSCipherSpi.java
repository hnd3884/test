package org.bouncycastle.pqc.jcajce.provider.mceliece;

import org.bouncycastle.pqc.crypto.mceliece.McElieceKeyParameters;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.PrivateKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCipher;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.provider.util.AsymmetricBlockCipher;

public class McEliecePKCSCipherSpi extends AsymmetricBlockCipher implements PKCSObjectIdentifiers, X509ObjectIdentifiers
{
    private McElieceCipher cipher;
    
    public McEliecePKCSCipherSpi(final McElieceCipher cipher) {
        this.cipher = cipher;
    }
    
    @Override
    protected void initCipherEncrypt(final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.cipher.init(true, new ParametersWithRandom(McElieceKeysToParams.generatePublicKeyParameter((PublicKey)key), secureRandom));
        this.maxPlainTextSize = this.cipher.maxPlainTextSize;
        this.cipherTextSize = this.cipher.cipherTextSize;
    }
    
    @Override
    protected void initCipherDecrypt(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.cipher.init(false, McElieceKeysToParams.generatePrivateKeyParameter((PrivateKey)key));
        this.maxPlainTextSize = this.cipher.maxPlainTextSize;
        this.cipherTextSize = this.cipher.cipherTextSize;
    }
    
    @Override
    protected byte[] messageEncrypt(final byte[] array) throws IllegalBlockSizeException, BadPaddingException {
        byte[] messageEncrypt = null;
        try {
            messageEncrypt = this.cipher.messageEncrypt(array);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return messageEncrypt;
    }
    
    @Override
    protected byte[] messageDecrypt(final byte[] array) throws IllegalBlockSizeException, BadPaddingException {
        byte[] messageDecrypt = null;
        try {
            messageDecrypt = this.cipher.messageDecrypt(array);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return messageDecrypt;
    }
    
    @Override
    public String getName() {
        return "McEliecePKCS";
    }
    
    @Override
    public int getKeySize(final Key key) throws InvalidKeyException {
        McElieceKeyParameters mcElieceKeyParameters;
        if (key instanceof PublicKey) {
            mcElieceKeyParameters = (McElieceKeyParameters)McElieceKeysToParams.generatePublicKeyParameter((PublicKey)key);
        }
        else {
            mcElieceKeyParameters = (McElieceKeyParameters)McElieceKeysToParams.generatePrivateKeyParameter((PrivateKey)key);
        }
        return this.cipher.getKeySize(mcElieceKeyParameters);
    }
    
    public static class McEliecePKCS extends McEliecePKCSCipherSpi
    {
        public McEliecePKCS() {
            super(new McElieceCipher());
        }
    }
}
