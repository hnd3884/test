package org.bouncycastle.pkcs.bc;

import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.crypto.io.CipherOutputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.BlockCipher;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.ExtendedDigest;

public class BcPKCS12PBEOutputEncryptorBuilder
{
    private ExtendedDigest digest;
    private BufferedBlockCipher engine;
    private ASN1ObjectIdentifier algorithm;
    private SecureRandom random;
    private int iterationCount;
    
    public BcPKCS12PBEOutputEncryptorBuilder(final ASN1ObjectIdentifier asn1ObjectIdentifier, final BlockCipher blockCipher) {
        this(asn1ObjectIdentifier, blockCipher, (ExtendedDigest)new SHA1Digest());
    }
    
    public BcPKCS12PBEOutputEncryptorBuilder(final ASN1ObjectIdentifier algorithm, final BlockCipher blockCipher, final ExtendedDigest digest) {
        this.iterationCount = 1024;
        this.algorithm = algorithm;
        this.engine = (BufferedBlockCipher)new PaddedBufferedBlockCipher(blockCipher, (BlockCipherPadding)new PKCS7Padding());
        this.digest = digest;
    }
    
    public BcPKCS12PBEOutputEncryptorBuilder setIterationCount(final int iterationCount) {
        this.iterationCount = iterationCount;
        return this;
    }
    
    public OutputEncryptor build(final char[] array) {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        final byte[] array2 = new byte[20];
        this.random.nextBytes(array2);
        final PKCS12PBEParams pkcs12PBEParams = new PKCS12PBEParams(array2, this.iterationCount);
        this.engine.init(true, PKCS12PBEUtils.createCipherParameters(this.algorithm, this.digest, this.engine.getBlockSize(), pkcs12PBEParams, array));
        return new OutputEncryptor() {
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return new AlgorithmIdentifier(BcPKCS12PBEOutputEncryptorBuilder.this.algorithm, (ASN1Encodable)pkcs12PBEParams);
            }
            
            public OutputStream getOutputStream(final OutputStream outputStream) {
                return (OutputStream)new CipherOutputStream(outputStream, BcPKCS12PBEOutputEncryptorBuilder.this.engine);
            }
            
            public GenericKey getKey() {
                return new GenericKey(new AlgorithmIdentifier(BcPKCS12PBEOutputEncryptorBuilder.this.algorithm, (ASN1Encodable)pkcs12PBEParams), PKCS12ParametersGenerator.PKCS12PasswordToBytes(array));
            }
        };
    }
}
