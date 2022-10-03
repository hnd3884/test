package org.bouncycastle.pqc.crypto.xmss;

final class WOTSPlusSignature
{
    private byte[][] signature;
    
    protected WOTSPlusSignature(final WOTSPlusParameters wotsPlusParameters, final byte[][] array) {
        if (wotsPlusParameters == null) {
            throw new NullPointerException("params == null");
        }
        if (array == null) {
            throw new NullPointerException("signature == null");
        }
        if (XMSSUtil.hasNullPointer(array)) {
            throw new NullPointerException("signature byte array == null");
        }
        if (array.length != wotsPlusParameters.getLen()) {
            throw new IllegalArgumentException("wrong signature size");
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i].length != wotsPlusParameters.getDigestSize()) {
                throw new IllegalArgumentException("wrong signature format");
            }
        }
        this.signature = XMSSUtil.cloneArray(array);
    }
    
    public byte[][] toByteArray() {
        return XMSSUtil.cloneArray(this.signature);
    }
}
