package org.bouncycastle.pqc.jcajce.provider.sphincs;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;
import java.security.KeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA512tDigest;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.pqc.jcajce.spec.SPHINCS256KeyGenParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import java.security.SecureRandom;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256KeyPairGenerator;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256KeyGenerationParameters;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.security.KeyPairGenerator;

public class Sphincs256KeyPairGeneratorSpi extends KeyPairGenerator
{
    ASN1ObjectIdentifier treeDigest;
    SPHINCS256KeyGenerationParameters param;
    SPHINCS256KeyPairGenerator engine;
    SecureRandom random;
    boolean initialised;
    
    public Sphincs256KeyPairGeneratorSpi() {
        super("SPHINCS256");
        this.treeDigest = NISTObjectIdentifiers.id_sha512_256;
        this.engine = new SPHINCS256KeyPairGenerator();
        this.random = new SecureRandom();
        this.initialised = false;
    }
    
    @Override
    public void initialize(final int n, final SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof SPHINCS256KeyGenParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a SPHINCS256KeyGenParameterSpec");
        }
        final SPHINCS256KeyGenParameterSpec sphincs256KeyGenParameterSpec = (SPHINCS256KeyGenParameterSpec)algorithmParameterSpec;
        if (sphincs256KeyGenParameterSpec.getTreeDigest().equals("SHA512-256")) {
            this.treeDigest = NISTObjectIdentifiers.id_sha512_256;
            this.param = new SPHINCS256KeyGenerationParameters(secureRandom, new SHA512tDigest(256));
        }
        else if (sphincs256KeyGenParameterSpec.getTreeDigest().equals("SHA3-256")) {
            this.treeDigest = NISTObjectIdentifiers.id_sha3_256;
            this.param = new SPHINCS256KeyGenerationParameters(secureRandom, new SHA3Digest(256));
        }
        this.engine.init(this.param);
        this.initialised = true;
    }
    
    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = new SPHINCS256KeyGenerationParameters(this.random, new SHA512tDigest(256));
            this.engine.init(this.param);
            this.initialised = true;
        }
        final AsymmetricCipherKeyPair generateKeyPair = this.engine.generateKeyPair();
        return new KeyPair(new BCSphincs256PublicKey(this.treeDigest, (SPHINCSPublicKeyParameters)generateKeyPair.getPublic()), new BCSphincs256PrivateKey(this.treeDigest, (SPHINCSPrivateKeyParameters)generateKeyPair.getPrivate()));
    }
}
