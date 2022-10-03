package org.bouncycastle.pqc.crypto.gmss;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.pqc.crypto.gmss.util.WinternitzOTSVerify;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.SecureRandom;
import org.bouncycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.bouncycastle.pqc.crypto.gmss.util.WinternitzOTSignature;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.gmss.util.GMSSUtil;
import org.bouncycastle.pqc.crypto.MessageSigner;

public class GMSSSigner implements MessageSigner
{
    private GMSSUtil gmssUtil;
    private byte[] pubKeyBytes;
    private Digest messDigestTrees;
    private int mdLength;
    private int numLayer;
    private Digest messDigestOTS;
    private WinternitzOTSignature ots;
    private GMSSDigestProvider digestProvider;
    private int[] index;
    private byte[][][] currentAuthPaths;
    private byte[][] subtreeRootSig;
    private GMSSParameters gmssPS;
    private GMSSRandom gmssRandom;
    GMSSKeyParameters key;
    private SecureRandom random;
    
    public GMSSSigner(final GMSSDigestProvider digestProvider) {
        this.gmssUtil = new GMSSUtil();
        this.digestProvider = digestProvider;
        this.messDigestTrees = digestProvider.get();
        this.messDigestOTS = this.messDigestTrees;
        this.mdLength = this.messDigestTrees.getDigestSize();
        this.gmssRandom = new GMSSRandom(this.messDigestTrees);
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        if (b) {
            if (cipherParameters instanceof ParametersWithRandom) {
                final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
                this.random = parametersWithRandom.getRandom();
                this.key = (GMSSPrivateKeyParameters)parametersWithRandom.getParameters();
                this.initSign();
            }
            else {
                this.random = new SecureRandom();
                this.key = (GMSSPrivateKeyParameters)cipherParameters;
                this.initSign();
            }
        }
        else {
            this.key = (GMSSPublicKeyParameters)cipherParameters;
            this.initVerify();
        }
    }
    
    private void initSign() {
        this.messDigestTrees.reset();
        final GMSSPrivateKeyParameters gmssPrivateKeyParameters = (GMSSPrivateKeyParameters)this.key;
        if (gmssPrivateKeyParameters.isUsed()) {
            throw new IllegalStateException("Private key already used");
        }
        if (gmssPrivateKeyParameters.getIndex(0) >= gmssPrivateKeyParameters.getNumLeafs(0)) {
            throw new IllegalStateException("No more signatures can be generated");
        }
        this.gmssPS = gmssPrivateKeyParameters.getParameters();
        this.numLayer = this.gmssPS.getNumOfLayers();
        final byte[] array = gmssPrivateKeyParameters.getCurrentSeeds()[this.numLayer - 1];
        final byte[] array2 = new byte[this.mdLength];
        final byte[] array3 = new byte[this.mdLength];
        System.arraycopy(array, 0, array3, 0, this.mdLength);
        this.ots = new WinternitzOTSignature(this.gmssRandom.nextSeed(array3), this.digestProvider.get(), this.gmssPS.getWinternitzParameter()[this.numLayer - 1]);
        final byte[][][] currentAuthPaths = gmssPrivateKeyParameters.getCurrentAuthPaths();
        this.currentAuthPaths = new byte[this.numLayer][][];
        for (int i = 0; i < this.numLayer; ++i) {
            this.currentAuthPaths[i] = new byte[currentAuthPaths[i].length][this.mdLength];
            for (int j = 0; j < currentAuthPaths[i].length; ++j) {
                System.arraycopy(currentAuthPaths[i][j], 0, this.currentAuthPaths[i][j], 0, this.mdLength);
            }
        }
        this.index = new int[this.numLayer];
        System.arraycopy(gmssPrivateKeyParameters.getIndex(), 0, this.index, 0, this.numLayer);
        this.subtreeRootSig = new byte[this.numLayer - 1][];
        for (int k = 0; k < this.numLayer - 1; ++k) {
            final byte[] subtreeRootSig = gmssPrivateKeyParameters.getSubtreeRootSig(k);
            System.arraycopy(subtreeRootSig, 0, this.subtreeRootSig[k] = new byte[subtreeRootSig.length], 0, subtreeRootSig.length);
        }
        gmssPrivateKeyParameters.markUsed();
    }
    
    public byte[] generateSignature(final byte[] array) {
        final byte[] array2 = new byte[this.mdLength];
        final byte[] signature = this.ots.getSignature(array);
        final byte[] concatenateArray = this.gmssUtil.concatenateArray(this.currentAuthPaths[this.numLayer - 1]);
        final byte[] intToBytesLittleEndian = this.gmssUtil.intToBytesLittleEndian(this.index[this.numLayer - 1]);
        final byte[] array3 = new byte[intToBytesLittleEndian.length + signature.length + concatenateArray.length];
        System.arraycopy(intToBytesLittleEndian, 0, array3, 0, intToBytesLittleEndian.length);
        System.arraycopy(signature, 0, array3, intToBytesLittleEndian.length, signature.length);
        System.arraycopy(concatenateArray, 0, array3, intToBytesLittleEndian.length + signature.length, concatenateArray.length);
        byte[] array4 = new byte[0];
        for (int i = this.numLayer - 1 - 1; i >= 0; --i) {
            final byte[] concatenateArray2 = this.gmssUtil.concatenateArray(this.currentAuthPaths[i]);
            final byte[] intToBytesLittleEndian2 = this.gmssUtil.intToBytesLittleEndian(this.index[i]);
            final byte[] array5 = new byte[array4.length];
            System.arraycopy(array4, 0, array5, 0, array4.length);
            array4 = new byte[array5.length + intToBytesLittleEndian2.length + this.subtreeRootSig[i].length + concatenateArray2.length];
            System.arraycopy(array5, 0, array4, 0, array5.length);
            System.arraycopy(intToBytesLittleEndian2, 0, array4, array5.length, intToBytesLittleEndian2.length);
            System.arraycopy(this.subtreeRootSig[i], 0, array4, array5.length + intToBytesLittleEndian2.length, this.subtreeRootSig[i].length);
            System.arraycopy(concatenateArray2, 0, array4, array5.length + intToBytesLittleEndian2.length + this.subtreeRootSig[i].length, concatenateArray2.length);
        }
        final byte[] array6 = new byte[array3.length + array4.length];
        System.arraycopy(array3, 0, array6, 0, array3.length);
        System.arraycopy(array4, 0, array6, array3.length, array4.length);
        return array6;
    }
    
    private void initVerify() {
        this.messDigestTrees.reset();
        final GMSSPublicKeyParameters gmssPublicKeyParameters = (GMSSPublicKeyParameters)this.key;
        this.pubKeyBytes = gmssPublicKeyParameters.getPublicKey();
        this.gmssPS = gmssPublicKeyParameters.getParameters();
        this.numLayer = this.gmssPS.getNumOfLayers();
    }
    
    public boolean verifySignature(byte[] array, final byte[] array2) {
        boolean b = false;
        this.messDigestOTS.reset();
        byte[] array3 = array;
        int n = 0;
        for (int i = this.numLayer - 1; i >= 0; --i) {
            final WinternitzOTSVerify winternitzOTSVerify = new WinternitzOTSVerify(this.digestProvider.get(), this.gmssPS.getWinternitzParameter()[i]);
            final int signatureLength = winternitzOTSVerify.getSignatureLength();
            array = array3;
            final int bytesToIntLittleEndian = this.gmssUtil.bytesToIntLittleEndian(array2, n);
            n += 4;
            final byte[] array4 = new byte[signatureLength];
            System.arraycopy(array2, n, array4, 0, signatureLength);
            n += signatureLength;
            final byte[] verify = winternitzOTSVerify.Verify(array, array4);
            if (verify == null) {
                System.err.println("OTS Public Key is null in GMSSSignature.verify");
                return false;
            }
            final byte[][] array5 = new byte[this.gmssPS.getHeightOfTrees()[i]][this.mdLength];
            for (int j = 0; j < array5.length; ++j) {
                System.arraycopy(array2, n, array5[j], 0, this.mdLength);
                n += this.mdLength;
            }
            final byte[] array6 = new byte[this.mdLength];
            array3 = verify;
            int n2 = (1 << array5.length) + bytesToIntLittleEndian;
            for (int k = 0; k < array5.length; ++k) {
                final byte[] array7 = new byte[this.mdLength << 1];
                if (n2 % 2 == 0) {
                    System.arraycopy(array3, 0, array7, 0, this.mdLength);
                    System.arraycopy(array5[k], 0, array7, this.mdLength, this.mdLength);
                    n2 /= 2;
                }
                else {
                    System.arraycopy(array5[k], 0, array7, 0, this.mdLength);
                    System.arraycopy(array3, 0, array7, this.mdLength, array3.length);
                    n2 = (n2 - 1) / 2;
                }
                this.messDigestTrees.update(array7, 0, array7.length);
                array3 = new byte[this.messDigestTrees.getDigestSize()];
                this.messDigestTrees.doFinal(array3, 0);
            }
        }
        if (Arrays.areEqual(this.pubKeyBytes, array3)) {
            b = true;
        }
        return b;
    }
}
