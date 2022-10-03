package org.bouncycastle.pqc.crypto.xmss;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public final class XMSSMTSignature implements XMSSStoreableObjectInterface
{
    private final XMSSMTParameters params;
    private final long index;
    private final byte[] random;
    private final List<XMSSReducedSignature> reducedSignatures;
    
    private XMSSMTSignature(final Builder builder) {
        this.params = builder.params;
        if (this.params == null) {
            throw new NullPointerException("params == null");
        }
        final int digestSize = this.params.getDigestSize();
        final byte[] access$100 = builder.signature;
        if (access$100 != null) {
            final int len = this.params.getWOTSPlus().getParams().getLen();
            final int n = (int)Math.ceil(this.params.getHeight() / 8.0);
            final int n2 = digestSize;
            final int n3 = (this.params.getHeight() / this.params.getLayers() + len) * digestSize;
            if (access$100.length != n + n2 + n3 * this.params.getLayers()) {
                throw new IllegalArgumentException("signature has wrong size");
            }
            final int n4 = 0;
            this.index = XMSSUtil.bytesToXBigEndian(access$100, n4, n);
            if (!XMSSUtil.isIndexValid(this.params.getHeight(), this.index)) {
                throw new IllegalArgumentException("index out of bounds");
            }
            final int n5 = n4 + n;
            this.random = XMSSUtil.extractBytesAtOffset(access$100, n5, n2);
            int i = n5 + n2;
            this.reducedSignatures = new ArrayList<XMSSReducedSignature>();
            while (i < access$100.length) {
                this.reducedSignatures.add(new XMSSReducedSignature.Builder(this.params.getXMSSParameters()).withReducedSignature(XMSSUtil.extractBytesAtOffset(access$100, i, n3)).build());
                i += n3;
            }
        }
        else {
            this.index = builder.index;
            final byte[] access$101 = builder.random;
            if (access$101 != null) {
                if (access$101.length != digestSize) {
                    throw new IllegalArgumentException("size of random needs to be equal to size of digest");
                }
                this.random = access$101;
            }
            else {
                this.random = new byte[digestSize];
            }
            final List access$102 = builder.reducedSignatures;
            if (access$102 != null) {
                this.reducedSignatures = access$102;
            }
            else {
                this.reducedSignatures = new ArrayList<XMSSReducedSignature>();
            }
        }
    }
    
    public byte[] toByteArray() {
        final int digestSize = this.params.getDigestSize();
        final int len = this.params.getWOTSPlus().getParams().getLen();
        final int n = (int)Math.ceil(this.params.getHeight() / 8.0);
        final int n2 = digestSize;
        final int n3 = (this.params.getHeight() / this.params.getLayers() + len) * digestSize;
        final byte[] array = new byte[n + n2 + n3 * this.params.getLayers()];
        final int n4 = 0;
        XMSSUtil.copyBytesAtOffset(array, XMSSUtil.toBytesBigEndian(this.index, n), n4);
        final int n5 = n4 + n;
        XMSSUtil.copyBytesAtOffset(array, this.random, n5);
        int n6 = n5 + n2;
        final Iterator<XMSSReducedSignature> iterator = this.reducedSignatures.iterator();
        while (iterator.hasNext()) {
            XMSSUtil.copyBytesAtOffset(array, iterator.next().toByteArray(), n6);
            n6 += n3;
        }
        return array;
    }
    
    public long getIndex() {
        return this.index;
    }
    
    public byte[] getRandom() {
        return XMSSUtil.cloneArray(this.random);
    }
    
    public List<XMSSReducedSignature> getReducedSignatures() {
        return this.reducedSignatures;
    }
    
    public static class Builder
    {
        private final XMSSMTParameters params;
        private long index;
        private byte[] random;
        private List<XMSSReducedSignature> reducedSignatures;
        private byte[] signature;
        
        public Builder(final XMSSMTParameters params) {
            this.index = 0L;
            this.random = null;
            this.reducedSignatures = null;
            this.signature = null;
            this.params = params;
        }
        
        public Builder withIndex(final long index) {
            this.index = index;
            return this;
        }
        
        public Builder withRandom(final byte[] array) {
            this.random = XMSSUtil.cloneArray(array);
            return this;
        }
        
        public Builder withReducedSignatures(final List<XMSSReducedSignature> reducedSignatures) {
            this.reducedSignatures = reducedSignatures;
            return this;
        }
        
        public Builder withSignature(final byte[] signature) {
            this.signature = signature;
            return this;
        }
        
        public XMSSMTSignature build() {
            return new XMSSMTSignature(this, null);
        }
    }
}
