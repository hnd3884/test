package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.util.Pack;

public final class XMSSSignature extends XMSSReducedSignature implements XMSSStoreableObjectInterface
{
    private final int index;
    private final byte[] random;
    
    private XMSSSignature(final Builder builder) {
        super(builder);
        this.index = builder.index;
        final int digestSize = this.getParams().getDigestSize();
        final byte[] access$100 = builder.random;
        if (access$100 != null) {
            if (access$100.length != digestSize) {
                throw new IllegalArgumentException("size of random needs to be equal to size of digest");
            }
            this.random = access$100;
        }
        else {
            this.random = new byte[digestSize];
        }
    }
    
    @Override
    public byte[] toByteArray() {
        final int digestSize = this.getParams().getDigestSize();
        final int n = 4;
        final int n2 = digestSize;
        final byte[] array = new byte[n + n2 + this.getParams().getWOTSPlus().getParams().getLen() * digestSize + this.getParams().getHeight() * digestSize];
        final int n3 = 0;
        Pack.intToBigEndian(this.index, array, n3);
        final int n4 = n3 + n;
        XMSSUtil.copyBytesAtOffset(array, this.random, n4);
        int n5 = n4 + n2;
        final byte[][] byteArray = this.getWOTSPlusSignature().toByteArray();
        for (int i = 0; i < byteArray.length; ++i) {
            XMSSUtil.copyBytesAtOffset(array, byteArray[i], n5);
            n5 += digestSize;
        }
        for (int j = 0; j < this.getAuthPath().size(); ++j) {
            XMSSUtil.copyBytesAtOffset(array, this.getAuthPath().get(j).getValue(), n5);
            n5 += digestSize;
        }
        return array;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public byte[] getRandom() {
        return XMSSUtil.cloneArray(this.random);
    }
    
    public static class Builder extends XMSSReducedSignature.Builder
    {
        private final XMSSParameters params;
        private int index;
        private byte[] random;
        
        public Builder(final XMSSParameters params) {
            super(params);
            this.index = 0;
            this.random = null;
            this.params = params;
        }
        
        public Builder withIndex(final int index) {
            this.index = index;
            return this;
        }
        
        public Builder withRandom(final byte[] array) {
            this.random = XMSSUtil.cloneArray(array);
            return this;
        }
        
        public Builder withSignature(final byte[] array) {
            if (array == null) {
                throw new NullPointerException("signature == null");
            }
            final int digestSize = this.params.getDigestSize();
            final int len = this.params.getWOTSPlus().getParams().getLen();
            final int height = this.params.getHeight();
            final int n = 4;
            final int n2 = digestSize;
            final int n3 = len * digestSize;
            final int n4 = height * digestSize;
            final int n5 = 0;
            this.index = Pack.bigEndianToInt(array, n5);
            final int n6 = n5 + n;
            this.random = XMSSUtil.extractBytesAtOffset(array, n6, n2);
            this.withReducedSignature(XMSSUtil.extractBytesAtOffset(array, n6 + n2, n3 + n4));
            return this;
        }
        
        @Override
        public XMSSSignature build() {
            return new XMSSSignature(this, null);
        }
    }
}
