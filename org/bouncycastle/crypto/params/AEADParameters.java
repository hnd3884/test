package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class AEADParameters implements CipherParameters
{
    private byte[] associatedText;
    private byte[] nonce;
    private KeyParameter key;
    private int macSize;
    
    public AEADParameters(final KeyParameter keyParameter, final int n, final byte[] array) {
        this(keyParameter, n, array, null);
    }
    
    public AEADParameters(final KeyParameter key, final int macSize, final byte[] nonce, final byte[] associatedText) {
        this.key = key;
        this.nonce = nonce;
        this.macSize = macSize;
        this.associatedText = associatedText;
    }
    
    public KeyParameter getKey() {
        return this.key;
    }
    
    public int getMacSize() {
        return this.macSize;
    }
    
    public byte[] getAssociatedText() {
        return this.associatedText;
    }
    
    public byte[] getNonce() {
        return this.nonce;
    }
}
