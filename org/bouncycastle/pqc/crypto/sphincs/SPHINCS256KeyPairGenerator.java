package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.Digest;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;

public class SPHINCS256KeyPairGenerator implements AsymmetricCipherKeyPairGenerator
{
    private SecureRandom random;
    private Digest treeDigest;
    
    public void init(final KeyGenerationParameters keyGenerationParameters) {
        this.random = keyGenerationParameters.getRandom();
        this.treeDigest = ((SPHINCS256KeyGenerationParameters)keyGenerationParameters).getTreeDigest();
    }
    
    public AsymmetricCipherKeyPair generateKeyPair() {
        final Tree.leafaddr leafaddr = new Tree.leafaddr();
        final byte[] array = new byte[1088];
        this.random.nextBytes(array);
        final byte[] array2 = new byte[1056];
        System.arraycopy(array, 32, array2, 0, 1024);
        leafaddr.level = 11;
        leafaddr.subtree = 0L;
        leafaddr.subleaf = 0L;
        Tree.treehash(new HashFunctions(this.treeDigest), array2, 1024, 5, array, leafaddr, array2, 0);
        return new AsymmetricCipherKeyPair(new SPHINCSPublicKeyParameters(array2), new SPHINCSPrivateKeyParameters(array));
    }
}
