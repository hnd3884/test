package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Strings;
import org.bouncycastle.util.Arrays;

public class BasicTlsPSKIdentity implements TlsPSKIdentity
{
    protected byte[] identity;
    protected byte[] psk;
    
    public BasicTlsPSKIdentity(final byte[] array, final byte[] array2) {
        this.identity = Arrays.clone(array);
        this.psk = Arrays.clone(array2);
    }
    
    public BasicTlsPSKIdentity(final String s, final byte[] array) {
        this.identity = Strings.toUTF8ByteArray(s);
        this.psk = Arrays.clone(array);
    }
    
    public void skipIdentityHint() {
    }
    
    public void notifyIdentityHint(final byte[] array) {
    }
    
    public byte[] getPSKIdentity() {
        return this.identity;
    }
    
    public byte[] getPSK() {
        return this.psk;
    }
}
