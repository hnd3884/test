package org.bouncycastle.pqc.crypto.xmss;

import java.util.ArrayList;
import java.util.List;

public class XMSSReducedSignature implements XMSSStoreableObjectInterface
{
    private final XMSSParameters params;
    private final WOTSPlusSignature wotsPlusSignature;
    private final List<XMSSNode> authPath;
    
    protected XMSSReducedSignature(final Builder builder) {
        this.params = builder.params;
        if (this.params == null) {
            throw new NullPointerException("params == null");
        }
        final int digestSize = this.params.getDigestSize();
        final int len = this.params.getWOTSPlus().getParams().getLen();
        final int height = this.params.getHeight();
        final byte[] access$100 = builder.reducedSignature;
        if (access$100 != null) {
            if (access$100.length != len * digestSize + height * digestSize) {
                throw new IllegalArgumentException("signature has wrong size");
            }
            int n = 0;
            final byte[][] array = new byte[len][];
            for (int i = 0; i < array.length; ++i) {
                array[i] = XMSSUtil.extractBytesAtOffset(access$100, n, digestSize);
                n += digestSize;
            }
            this.wotsPlusSignature = new WOTSPlusSignature(this.params.getWOTSPlus().getParams(), array);
            final ArrayList authPath = new ArrayList();
            for (int j = 0; j < height; ++j) {
                authPath.add(new XMSSNode(j, XMSSUtil.extractBytesAtOffset(access$100, n, digestSize)));
                n += digestSize;
            }
            this.authPath = authPath;
        }
        else {
            final WOTSPlusSignature access$101 = builder.wotsPlusSignature;
            if (access$101 != null) {
                this.wotsPlusSignature = access$101;
            }
            else {
                this.wotsPlusSignature = new WOTSPlusSignature(this.params.getWOTSPlus().getParams(), new byte[len][digestSize]);
            }
            final List access$102 = builder.authPath;
            if (access$102 != null) {
                if (access$102.size() != height) {
                    throw new IllegalArgumentException("size of authPath needs to be equal to height of tree");
                }
                this.authPath = access$102;
            }
            else {
                this.authPath = new ArrayList<XMSSNode>();
            }
        }
    }
    
    public byte[] toByteArray() {
        final int digestSize = this.params.getDigestSize();
        final byte[] array = new byte[this.params.getWOTSPlus().getParams().getLen() * digestSize + this.params.getHeight() * digestSize];
        int n = 0;
        final byte[][] byteArray = this.wotsPlusSignature.toByteArray();
        for (int i = 0; i < byteArray.length; ++i) {
            XMSSUtil.copyBytesAtOffset(array, byteArray[i], n);
            n += digestSize;
        }
        for (int j = 0; j < this.authPath.size(); ++j) {
            XMSSUtil.copyBytesAtOffset(array, this.authPath.get(j).getValue(), n);
            n += digestSize;
        }
        return array;
    }
    
    public XMSSParameters getParams() {
        return this.params;
    }
    
    public WOTSPlusSignature getWOTSPlusSignature() {
        return this.wotsPlusSignature;
    }
    
    public List<XMSSNode> getAuthPath() {
        return this.authPath;
    }
    
    public static class Builder
    {
        private final XMSSParameters params;
        private WOTSPlusSignature wotsPlusSignature;
        private List<XMSSNode> authPath;
        private byte[] reducedSignature;
        
        public Builder(final XMSSParameters params) {
            this.wotsPlusSignature = null;
            this.authPath = null;
            this.reducedSignature = null;
            this.params = params;
        }
        
        public Builder withWOTSPlusSignature(final WOTSPlusSignature wotsPlusSignature) {
            this.wotsPlusSignature = wotsPlusSignature;
            return this;
        }
        
        public Builder withAuthPath(final List<XMSSNode> authPath) {
            this.authPath = authPath;
            return this;
        }
        
        public Builder withReducedSignature(final byte[] array) {
            this.reducedSignature = XMSSUtil.cloneArray(array);
            return this;
        }
        
        public XMSSReducedSignature build() {
            return new XMSSReducedSignature(this);
        }
    }
}
