package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public final class XMSSPublicKeyParameters extends AsymmetricKeyParameter implements XMSSStoreableObjectInterface
{
    private final XMSSParameters params;
    private final byte[] root;
    private final byte[] publicSeed;
    
    private XMSSPublicKeyParameters(final Builder builder) {
        super(false);
        this.params = builder.params;
        if (this.params == null) {
            throw new NullPointerException("params == null");
        }
        final int digestSize = this.params.getDigestSize();
        final byte[] access$100 = builder.publicKey;
        if (access$100 != null) {
            final int n = digestSize;
            final int n2 = digestSize;
            if (access$100.length != n + n2) {
                throw new IllegalArgumentException("public key has wrong size");
            }
            final int n3 = 0;
            this.root = XMSSUtil.extractBytesAtOffset(access$100, n3, n);
            this.publicSeed = XMSSUtil.extractBytesAtOffset(access$100, n3 + n, n2);
        }
        else {
            final byte[] access$101 = builder.root;
            if (access$101 != null) {
                if (access$101.length != digestSize) {
                    throw new IllegalArgumentException("length of root must be equal to length of digest");
                }
                this.root = access$101;
            }
            else {
                this.root = new byte[digestSize];
            }
            final byte[] access$102 = builder.publicSeed;
            if (access$102 != null) {
                if (access$102.length != digestSize) {
                    throw new IllegalArgumentException("length of publicSeed must be equal to length of digest");
                }
                this.publicSeed = access$102;
            }
            else {
                this.publicSeed = new byte[digestSize];
            }
        }
    }
    
    public byte[] toByteArray() {
        final int digestSize;
        final byte[] array = new byte[digestSize + (digestSize = this.params.getDigestSize())];
        final int n = 0;
        XMSSUtil.copyBytesAtOffset(array, this.root, n);
        XMSSUtil.copyBytesAtOffset(array, this.publicSeed, n + digestSize);
        return array;
    }
    
    public byte[] getRoot() {
        return XMSSUtil.cloneArray(this.root);
    }
    
    public byte[] getPublicSeed() {
        return XMSSUtil.cloneArray(this.publicSeed);
    }
    
    public XMSSParameters getParameters() {
        return this.params;
    }
    
    public static class Builder
    {
        private final XMSSParameters params;
        private byte[] root;
        private byte[] publicSeed;
        private byte[] publicKey;
        
        public Builder(final XMSSParameters params) {
            this.root = null;
            this.publicSeed = null;
            this.publicKey = null;
            this.params = params;
        }
        
        public Builder withRoot(final byte[] array) {
            this.root = XMSSUtil.cloneArray(array);
            return this;
        }
        
        public Builder withPublicSeed(final byte[] array) {
            this.publicSeed = XMSSUtil.cloneArray(array);
            return this;
        }
        
        public Builder withPublicKey(final byte[] array) {
            this.publicKey = XMSSUtil.cloneArray(array);
            return this;
        }
        
        public XMSSPublicKeyParameters build() {
            return new XMSSPublicKeyParameters(this, null);
        }
    }
}
