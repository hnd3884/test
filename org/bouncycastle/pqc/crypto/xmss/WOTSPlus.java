package org.bouncycastle.pqc.crypto.xmss;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

final class WOTSPlus
{
    private final WOTSPlusParameters params;
    private final KeyedHashFunctions khf;
    private byte[] secretKeySeed;
    private byte[] publicSeed;
    
    protected WOTSPlus(final WOTSPlusParameters params) {
        if (params == null) {
            throw new NullPointerException("params == null");
        }
        this.params = params;
        final int digestSize = params.getDigestSize();
        this.khf = new KeyedHashFunctions(params.getDigest(), digestSize);
        this.secretKeySeed = new byte[digestSize];
        this.publicSeed = new byte[digestSize];
    }
    
    void importKeys(final byte[] secretKeySeed, final byte[] publicSeed) {
        if (secretKeySeed == null) {
            throw new NullPointerException("secretKeySeed == null");
        }
        if (secretKeySeed.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of secretKeySeed needs to be equal to size of digest");
        }
        if (publicSeed == null) {
            throw new NullPointerException("publicSeed == null");
        }
        if (publicSeed.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of publicSeed needs to be equal to size of digest");
        }
        this.secretKeySeed = secretKeySeed;
        this.publicSeed = publicSeed;
    }
    
    protected WOTSPlusSignature sign(final byte[] array, OTSHashAddress otsHashAddress) {
        if (array == null) {
            throw new NullPointerException("messageDigest == null");
        }
        if (array.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        }
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        final List<Integer> convertToBaseW = this.convertToBaseW(array, this.params.getWinternitzParameter(), this.params.getLen1());
        int n = 0;
        for (int i = 0; i < this.params.getLen1(); ++i) {
            n += this.params.getWinternitzParameter() - 1 - convertToBaseW.get(i);
        }
        convertToBaseW.addAll(this.convertToBaseW(XMSSUtil.toBytesBigEndian(n << 8 - this.params.getLen2() * XMSSUtil.log2(this.params.getWinternitzParameter()) % 8, (int)Math.ceil(this.params.getLen2() * XMSSUtil.log2(this.params.getWinternitzParameter()) / 8.0)), this.params.getWinternitzParameter(), this.params.getLen2()));
        final byte[][] array2 = new byte[this.params.getLen()][];
        for (int j = 0; j < this.params.getLen(); ++j) {
            otsHashAddress = (OTSHashAddress)new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(otsHashAddress.getOTSAddress()).withChainAddress(j).withHashAddress(otsHashAddress.getHashAddress()).withKeyAndMask(otsHashAddress.getKeyAndMask()).build();
            array2[j] = this.chain(this.expandSecretKeySeed(j), 0, convertToBaseW.get(j), otsHashAddress);
        }
        return new WOTSPlusSignature(this.params, array2);
    }
    
    protected boolean verifySignature(final byte[] array, final WOTSPlusSignature wotsPlusSignature, final OTSHashAddress otsHashAddress) {
        if (array == null) {
            throw new NullPointerException("messageDigest == null");
        }
        if (array.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        }
        if (wotsPlusSignature == null) {
            throw new NullPointerException("signature == null");
        }
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        return XMSSUtil.areEqual(this.getPublicKeyFromSignature(array, wotsPlusSignature, otsHashAddress).toByteArray(), this.getPublicKey(otsHashAddress).toByteArray());
    }
    
    protected WOTSPlusPublicKeyParameters getPublicKeyFromSignature(final byte[] array, final WOTSPlusSignature wotsPlusSignature, OTSHashAddress otsHashAddress) {
        if (array == null) {
            throw new NullPointerException("messageDigest == null");
        }
        if (array.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        }
        if (wotsPlusSignature == null) {
            throw new NullPointerException("signature == null");
        }
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        final List<Integer> convertToBaseW = this.convertToBaseW(array, this.params.getWinternitzParameter(), this.params.getLen1());
        int n = 0;
        for (int i = 0; i < this.params.getLen1(); ++i) {
            n += this.params.getWinternitzParameter() - 1 - convertToBaseW.get(i);
        }
        convertToBaseW.addAll(this.convertToBaseW(XMSSUtil.toBytesBigEndian(n << 8 - this.params.getLen2() * XMSSUtil.log2(this.params.getWinternitzParameter()) % 8, (int)Math.ceil(this.params.getLen2() * XMSSUtil.log2(this.params.getWinternitzParameter()) / 8.0)), this.params.getWinternitzParameter(), this.params.getLen2()));
        final byte[][] array2 = new byte[this.params.getLen()][];
        for (int j = 0; j < this.params.getLen(); ++j) {
            otsHashAddress = (OTSHashAddress)new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(otsHashAddress.getOTSAddress()).withChainAddress(j).withHashAddress(otsHashAddress.getHashAddress()).withKeyAndMask(otsHashAddress.getKeyAndMask()).build();
            array2[j] = this.chain(wotsPlusSignature.toByteArray()[j], convertToBaseW.get(j), this.params.getWinternitzParameter() - 1 - convertToBaseW.get(j), otsHashAddress);
        }
        return new WOTSPlusPublicKeyParameters(this.params, array2);
    }
    
    private byte[] chain(final byte[] array, final int n, final int n2, OTSHashAddress otsHashAddress) {
        final int digestSize = this.params.getDigestSize();
        if (array == null) {
            throw new NullPointerException("startHash == null");
        }
        if (array.length != digestSize) {
            throw new IllegalArgumentException("startHash needs to be " + digestSize + "bytes");
        }
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        if (otsHashAddress.toByteArray() == null) {
            throw new NullPointerException("otsHashAddress byte array == null");
        }
        if (n + n2 > this.params.getWinternitzParameter() - 1) {
            throw new IllegalArgumentException("max chain length must not be greater than w");
        }
        if (n2 == 0) {
            return array;
        }
        final byte[] chain = this.chain(array, n, n2 - 1, otsHashAddress);
        otsHashAddress = (OTSHashAddress)new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(otsHashAddress.getOTSAddress()).withChainAddress(otsHashAddress.getChainAddress()).withHashAddress(n + n2 - 1).withKeyAndMask(0).build();
        final byte[] prf = this.khf.PRF(this.publicSeed, otsHashAddress.toByteArray());
        otsHashAddress = (OTSHashAddress)new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(otsHashAddress.getOTSAddress()).withChainAddress(otsHashAddress.getChainAddress()).withHashAddress(otsHashAddress.getHashAddress()).withKeyAndMask(1).build();
        final byte[] prf2 = this.khf.PRF(this.publicSeed, otsHashAddress.toByteArray());
        final byte[] array2 = new byte[digestSize];
        for (int i = 0; i < digestSize; ++i) {
            array2[i] = (byte)(chain[i] ^ prf2[i]);
        }
        return this.khf.F(prf, array2);
    }
    
    private List<Integer> convertToBaseW(final byte[] array, final int n, final int n2) {
        if (array == null) {
            throw new NullPointerException("msg == null");
        }
        if (n != 4 && n != 16) {
            throw new IllegalArgumentException("w needs to be 4 or 16");
        }
        final int log2 = XMSSUtil.log2(n);
        if (n2 > 8 * array.length / log2) {
            throw new IllegalArgumentException("outLength too big");
        }
        final ArrayList list = new ArrayList();
        for (int i = 0; i < array.length; ++i) {
            for (int j = 8 - log2; j >= 0; j -= log2) {
                list.add(array[i] >> j & n - 1);
                if (list.size() == n2) {
                    return list;
                }
            }
        }
        return list;
    }
    
    protected byte[] getWOTSPlusSecretKey(final byte[] array, OTSHashAddress otsHashAddress) {
        otsHashAddress = (OTSHashAddress)new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(otsHashAddress.getOTSAddress()).build();
        return this.khf.PRF(array, otsHashAddress.toByteArray());
    }
    
    private byte[] expandSecretKeySeed(final int n) {
        if (n < 0 || n >= this.params.getLen()) {
            throw new IllegalArgumentException("index out of bounds");
        }
        return this.khf.PRF(this.secretKeySeed, XMSSUtil.toBytesBigEndian(n, 32));
    }
    
    protected WOTSPlusParameters getParams() {
        return this.params;
    }
    
    protected KeyedHashFunctions getKhf() {
        return this.khf;
    }
    
    protected byte[] getSecretKeySeed() {
        return XMSSUtil.cloneArray(this.getSecretKeySeed());
    }
    
    protected byte[] getPublicSeed() {
        return XMSSUtil.cloneArray(this.publicSeed);
    }
    
    protected WOTSPlusPrivateKeyParameters getPrivateKey() {
        final byte[][] array = new byte[this.params.getLen()][];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.expandSecretKeySeed(i);
        }
        return new WOTSPlusPrivateKeyParameters(this.params, array);
    }
    
    protected WOTSPlusPublicKeyParameters getPublicKey(OTSHashAddress otsHashAddress) {
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        final byte[][] array = new byte[this.params.getLen()][];
        for (int i = 0; i < this.params.getLen(); ++i) {
            otsHashAddress = (OTSHashAddress)new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(otsHashAddress.getOTSAddress()).withChainAddress(i).withHashAddress(otsHashAddress.getHashAddress()).withKeyAndMask(otsHashAddress.getKeyAndMask()).build();
            array[i] = this.chain(this.expandSecretKeySeed(i), 0, this.params.getWinternitzParameter() - 1, otsHashAddress);
        }
        return new WOTSPlusPublicKeyParameters(this.params, array);
    }
}
