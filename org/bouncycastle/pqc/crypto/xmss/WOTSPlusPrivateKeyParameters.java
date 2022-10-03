package org.bouncycastle.pqc.crypto.xmss;

final class WOTSPlusPrivateKeyParameters
{
    private final byte[][] privateKey;
    
    protected WOTSPlusPrivateKeyParameters(final WOTSPlusParameters wotsPlusParameters, final byte[][] array) {
        if (wotsPlusParameters == null) {
            throw new NullPointerException("params == null");
        }
        if (array == null) {
            throw new NullPointerException("privateKey == null");
        }
        if (XMSSUtil.hasNullPointer(array)) {
            throw new NullPointerException("privateKey byte array == null");
        }
        if (array.length != wotsPlusParameters.getLen()) {
            throw new IllegalArgumentException("wrong privateKey format");
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i].length != wotsPlusParameters.getDigestSize()) {
                throw new IllegalArgumentException("wrong privateKey format");
            }
        }
        this.privateKey = XMSSUtil.cloneArray(array);
    }
    
    protected byte[][] toByteArray() {
        return XMSSUtil.cloneArray(this.privateKey);
    }
}
