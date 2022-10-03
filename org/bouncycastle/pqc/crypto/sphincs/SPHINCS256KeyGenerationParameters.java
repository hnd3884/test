package org.bouncycastle.pqc.crypto.sphincs;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class SPHINCS256KeyGenerationParameters extends KeyGenerationParameters
{
    private final Digest treeDigest;
    
    public SPHINCS256KeyGenerationParameters(final SecureRandom secureRandom, final Digest treeDigest) {
        super(secureRandom, 8448);
        this.treeDigest = treeDigest;
    }
    
    public Digest getTreeDigest() {
        return this.treeDigest;
    }
}
