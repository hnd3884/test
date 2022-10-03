package org.bouncycastle.crypto.tls;

public interface TlsPSKIdentity
{
    void skipIdentityHint();
    
    void notifyIdentityHint(final byte[] p0);
    
    byte[] getPSKIdentity();
    
    byte[] getPSK();
}
