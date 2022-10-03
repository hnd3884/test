package org.bouncycastle.crypto.params;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.CipherParameters;

public class TweakableBlockCipherParameters implements CipherParameters
{
    private final byte[] tweak;
    private final KeyParameter key;
    
    public TweakableBlockCipherParameters(final KeyParameter key, final byte[] array) {
        this.key = key;
        this.tweak = Arrays.clone(array);
    }
    
    public KeyParameter getKey() {
        return this.key;
    }
    
    public byte[] getTweak() {
        return this.tweak;
    }
}
