package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.util.Arrays;
import java.io.IOException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public final class XMSSMTPrivateKeyParameters extends AsymmetricKeyParameter implements XMSSStoreableObjectInterface
{
    private final XMSSMTParameters params;
    private final long index;
    private final byte[] secretKeySeed;
    private final byte[] secretKeyPRF;
    private final byte[] publicSeed;
    private final byte[] root;
    private final BDSStateMap bdsState;
    
    private XMSSMTPrivateKeyParameters(final Builder builder) {
        super(true);
        this.params = builder.params;
        if (this.params == null) {
            throw new NullPointerException("params == null");
        }
        final int digestSize = this.params.getDigestSize();
        final byte[] access$100 = builder.privateKey;
        if (access$100 != null) {
            if (builder.xmss == null) {
                throw new NullPointerException("xmss == null");
            }
            final int height = this.params.getHeight();
            final int n = (height + 7) / 8;
            final int n2 = digestSize;
            final int n3 = digestSize;
            final int n4 = digestSize;
            final int n5 = digestSize;
            final int n6 = 0;
            this.index = XMSSUtil.bytesToXBigEndian(access$100, n6, n);
            if (!XMSSUtil.isIndexValid(height, this.index)) {
                throw new IllegalArgumentException("index out of bounds");
            }
            final int n7 = n6 + n;
            this.secretKeySeed = XMSSUtil.extractBytesAtOffset(access$100, n7, n2);
            final int n8 = n7 + n2;
            this.secretKeyPRF = XMSSUtil.extractBytesAtOffset(access$100, n8, n3);
            final int n9 = n8 + n3;
            this.publicSeed = XMSSUtil.extractBytesAtOffset(access$100, n9, n4);
            final int n10 = n9 + n4;
            this.root = XMSSUtil.extractBytesAtOffset(access$100, n10, n5);
            final int n11 = n10 + n5;
            final byte[] bytesAtOffset = XMSSUtil.extractBytesAtOffset(access$100, n11, access$100.length - n11);
            BDSStateMap bdsState = null;
            try {
                bdsState = (BDSStateMap)XMSSUtil.deserialize(bytesAtOffset);
            }
            catch (final IOException ex) {
                ex.printStackTrace();
            }
            catch (final ClassNotFoundException ex2) {
                ex2.printStackTrace();
            }
            bdsState.setXMSS(builder.xmss);
            this.bdsState = bdsState;
        }
        else {
            this.index = builder.index;
            final byte[] access$101 = builder.secretKeySeed;
            if (access$101 != null) {
                if (access$101.length != digestSize) {
                    throw new IllegalArgumentException("size of secretKeySeed needs to be equal size of digest");
                }
                this.secretKeySeed = access$101;
            }
            else {
                this.secretKeySeed = new byte[digestSize];
            }
            final byte[] access$102 = builder.secretKeyPRF;
            if (access$102 != null) {
                if (access$102.length != digestSize) {
                    throw new IllegalArgumentException("size of secretKeyPRF needs to be equal size of digest");
                }
                this.secretKeyPRF = access$102;
            }
            else {
                this.secretKeyPRF = new byte[digestSize];
            }
            final byte[] access$103 = builder.publicSeed;
            if (access$103 != null) {
                if (access$103.length != digestSize) {
                    throw new IllegalArgumentException("size of publicSeed needs to be equal size of digest");
                }
                this.publicSeed = access$103;
            }
            else {
                this.publicSeed = new byte[digestSize];
            }
            final byte[] access$104 = builder.root;
            if (access$104 != null) {
                if (access$104.length != digestSize) {
                    throw new IllegalArgumentException("size of root needs to be equal size of digest");
                }
                this.root = access$104;
            }
            else {
                this.root = new byte[digestSize];
            }
            final BDSStateMap access$105 = builder.bdsState;
            if (access$105 != null) {
                this.bdsState = access$105;
            }
            else if (XMSSUtil.isIndexValid(this.params.getHeight(), builder.index) && access$103 != null && access$101 != null) {
                this.bdsState = new BDSStateMap(this.params, builder.index, access$103, access$101);
            }
            else {
                this.bdsState = new BDSStateMap();
            }
        }
    }
    
    public byte[] toByteArray() {
        final int digestSize = this.params.getDigestSize();
        final int n = (this.params.getHeight() + 7) / 8;
        final int n2 = digestSize;
        final int n3 = digestSize;
        final int n4 = digestSize;
        final byte[] array = new byte[n + n2 + n3 + n4 + digestSize];
        final int n5 = 0;
        XMSSUtil.copyBytesAtOffset(array, XMSSUtil.toBytesBigEndian(this.index, n), n5);
        final int n6 = n5 + n;
        XMSSUtil.copyBytesAtOffset(array, this.secretKeySeed, n6);
        final int n7 = n6 + n2;
        XMSSUtil.copyBytesAtOffset(array, this.secretKeyPRF, n7);
        final int n8 = n7 + n3;
        XMSSUtil.copyBytesAtOffset(array, this.publicSeed, n8);
        XMSSUtil.copyBytesAtOffset(array, this.root, n8 + n4);
        byte[] serialize;
        try {
            serialize = XMSSUtil.serialize(this.bdsState);
        }
        catch (final IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("error serializing bds state");
        }
        return Arrays.concatenate(array, serialize);
    }
    
    public long getIndex() {
        return this.index;
    }
    
    public byte[] getSecretKeySeed() {
        return XMSSUtil.cloneArray(this.secretKeySeed);
    }
    
    public byte[] getSecretKeyPRF() {
        return XMSSUtil.cloneArray(this.secretKeyPRF);
    }
    
    public byte[] getPublicSeed() {
        return XMSSUtil.cloneArray(this.publicSeed);
    }
    
    public byte[] getRoot() {
        return XMSSUtil.cloneArray(this.root);
    }
    
    BDSStateMap getBDSState() {
        return this.bdsState;
    }
    
    public XMSSMTParameters getParameters() {
        return this.params;
    }
    
    public XMSSMTPrivateKeyParameters getNextKey() {
        return new Builder(this.params).withIndex(this.index + 1L).withSecretKeySeed(this.secretKeySeed).withSecretKeyPRF(this.secretKeyPRF).withPublicSeed(this.publicSeed).withRoot(this.root).withBDSState(new BDSStateMap(this.bdsState, this.params, this.getIndex(), this.publicSeed, this.secretKeySeed)).build();
    }
    
    public static class Builder
    {
        private final XMSSMTParameters params;
        private long index;
        private byte[] secretKeySeed;
        private byte[] secretKeyPRF;
        private byte[] publicSeed;
        private byte[] root;
        private BDSStateMap bdsState;
        private byte[] privateKey;
        private XMSSParameters xmss;
        
        public Builder(final XMSSMTParameters params) {
            this.index = 0L;
            this.secretKeySeed = null;
            this.secretKeyPRF = null;
            this.publicSeed = null;
            this.root = null;
            this.bdsState = null;
            this.privateKey = null;
            this.xmss = null;
            this.params = params;
        }
        
        public Builder withIndex(final long index) {
            this.index = index;
            return this;
        }
        
        public Builder withSecretKeySeed(final byte[] array) {
            this.secretKeySeed = XMSSUtil.cloneArray(array);
            return this;
        }
        
        public Builder withSecretKeyPRF(final byte[] array) {
            this.secretKeyPRF = XMSSUtil.cloneArray(array);
            return this;
        }
        
        public Builder withPublicSeed(final byte[] array) {
            this.publicSeed = XMSSUtil.cloneArray(array);
            return this;
        }
        
        public Builder withRoot(final byte[] array) {
            this.root = XMSSUtil.cloneArray(array);
            return this;
        }
        
        public Builder withBDSState(final BDSStateMap bdsState) {
            this.bdsState = bdsState;
            return this;
        }
        
        public Builder withPrivateKey(final byte[] array, final XMSSParameters xmss) {
            this.privateKey = XMSSUtil.cloneArray(array);
            this.xmss = xmss;
            return this;
        }
        
        public XMSSMTPrivateKeyParameters build() {
            return new XMSSMTPrivateKeyParameters(this, null);
        }
    }
}
