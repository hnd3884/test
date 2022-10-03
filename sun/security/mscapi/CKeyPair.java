package sun.security.mscapi;

class CKeyPair
{
    private final CPrivateKey privateKey;
    private final CPublicKey publicKey;
    
    CKeyPair(final String s, final long n, final long n2, final int n3) {
        final CKey.NativeHandles nativeHandles = new CKey.NativeHandles(n, n2);
        this.privateKey = CPrivateKey.of(s, nativeHandles, n3);
        this.publicKey = CPublicKey.of(s, nativeHandles, n3);
    }
    
    public CPrivateKey getPrivate() {
        return this.privateKey;
    }
    
    public CPublicKey getPublic() {
        return this.publicKey;
    }
}
