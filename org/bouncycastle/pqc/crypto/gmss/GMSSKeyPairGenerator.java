package org.bouncycastle.pqc.crypto.gmss;

import org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;
import org.bouncycastle.pqc.crypto.gmss.util.WinternitzOTSVerify;
import org.bouncycastle.pqc.crypto.gmss.util.WinternitzOTSignature;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.util.Vector;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;

public class GMSSKeyPairGenerator implements AsymmetricCipherKeyPairGenerator
{
    private GMSSRandom gmssRandom;
    private Digest messDigestTree;
    private byte[][] currentSeeds;
    private byte[][] nextNextSeeds;
    private byte[][] currentRootSigs;
    private GMSSDigestProvider digestProvider;
    private int mdLength;
    private int numLayer;
    private boolean initialized;
    private GMSSParameters gmssPS;
    private int[] heightOfTrees;
    private int[] otsIndex;
    private int[] K;
    private GMSSKeyGenerationParameters gmssParams;
    public static final String OID = "1.3.6.1.4.1.8301.3.1.3.3";
    
    public GMSSKeyPairGenerator(final GMSSDigestProvider digestProvider) {
        this.initialized = false;
        this.digestProvider = digestProvider;
        this.messDigestTree = digestProvider.get();
        this.mdLength = this.messDigestTree.getDigestSize();
        this.gmssRandom = new GMSSRandom(this.messDigestTree);
    }
    
    private AsymmetricCipherKeyPair genKeyPair() {
        if (!this.initialized) {
            this.initializeDefault();
        }
        final byte[][][] array = new byte[this.numLayer][][];
        final byte[][][] array2 = new byte[this.numLayer - 1][][];
        final Treehash[][] array3 = new Treehash[this.numLayer][];
        final Treehash[][] array4 = new Treehash[this.numLayer - 1][];
        final Vector[] array5 = new Vector[this.numLayer];
        final Vector[] array6 = new Vector[this.numLayer - 1];
        final Vector[][] array7 = new Vector[this.numLayer][];
        final Vector[][] array8 = new Vector[this.numLayer - 1][];
        for (int i = 0; i < this.numLayer; ++i) {
            array[i] = new byte[this.heightOfTrees[i]][this.mdLength];
            array3[i] = new Treehash[this.heightOfTrees[i] - this.K[i]];
            if (i > 0) {
                array2[i - 1] = new byte[this.heightOfTrees[i]][this.mdLength];
                array4[i - 1] = new Treehash[this.heightOfTrees[i] - this.K[i]];
            }
            array5[i] = new Vector();
            if (i > 0) {
                array6[i - 1] = new Vector();
            }
        }
        final byte[][] array9 = new byte[this.numLayer][this.mdLength];
        final byte[][] array10 = new byte[this.numLayer - 1][this.mdLength];
        final byte[][] array11 = new byte[this.numLayer][this.mdLength];
        for (int j = 0; j < this.numLayer; ++j) {
            System.arraycopy(this.currentSeeds[j], 0, array11[j], 0, this.mdLength);
        }
        this.currentRootSigs = new byte[this.numLayer - 1][this.mdLength];
        for (int k = this.numLayer - 1; k >= 0; --k) {
            GMSSRootCalc gmssRootCalc = new GMSSRootCalc(this.heightOfTrees[k], this.K[k], this.digestProvider);
            try {
                if (k == this.numLayer - 1) {
                    gmssRootCalc = this.generateCurrentAuthpathAndRoot(null, array5[k], array11[k], k);
                }
                else {
                    gmssRootCalc = this.generateCurrentAuthpathAndRoot(array9[k + 1], array5[k], array11[k], k);
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
            for (int l = 0; l < this.heightOfTrees[k]; ++l) {
                System.arraycopy(gmssRootCalc.getAuthPath()[l], 0, array[k][l], 0, this.mdLength);
            }
            array7[k] = gmssRootCalc.getRetain();
            array3[k] = gmssRootCalc.getTreehash();
            System.arraycopy(gmssRootCalc.getRoot(), 0, array9[k], 0, this.mdLength);
        }
        for (int n = this.numLayer - 2; n >= 0; --n) {
            final GMSSRootCalc generateNextAuthpathAndRoot = this.generateNextAuthpathAndRoot(array6[n], array11[n + 1], n + 1);
            for (int n2 = 0; n2 < this.heightOfTrees[n + 1]; ++n2) {
                System.arraycopy(generateNextAuthpathAndRoot.getAuthPath()[n2], 0, array2[n][n2], 0, this.mdLength);
            }
            array8[n] = generateNextAuthpathAndRoot.getRetain();
            array4[n] = generateNextAuthpathAndRoot.getTreehash();
            System.arraycopy(generateNextAuthpathAndRoot.getRoot(), 0, array10[n], 0, this.mdLength);
            System.arraycopy(array11[n + 1], 0, this.nextNextSeeds[n], 0, this.mdLength);
        }
        return new AsymmetricCipherKeyPair(new GMSSPublicKeyParameters(array9[0], this.gmssPS), new GMSSPrivateKeyParameters(this.currentSeeds, this.nextNextSeeds, array, array2, array3, array4, array5, array6, array7, array8, array10, this.currentRootSigs, this.gmssPS, this.digestProvider));
    }
    
    private GMSSRootCalc generateCurrentAuthpathAndRoot(final byte[] array, final Vector vector, final byte[] array2, final int n) {
        final byte[] array3 = new byte[this.mdLength];
        final byte[] array4 = new byte[this.mdLength];
        final byte[] nextSeed = this.gmssRandom.nextSeed(array2);
        final GMSSRootCalc gmssRootCalc = new GMSSRootCalc(this.heightOfTrees[n], this.K[n], this.digestProvider);
        gmssRootCalc.initialize(vector);
        byte[] array5;
        if (n == this.numLayer - 1) {
            array5 = new WinternitzOTSignature(nextSeed, this.digestProvider.get(), this.otsIndex[n]).getPublicKey();
        }
        else {
            this.currentRootSigs[n] = new WinternitzOTSignature(nextSeed, this.digestProvider.get(), this.otsIndex[n]).getSignature(array);
            array5 = new WinternitzOTSVerify(this.digestProvider.get(), this.otsIndex[n]).Verify(array, this.currentRootSigs[n]);
        }
        gmssRootCalc.update(array5);
        int n2 = 3;
        int n3 = 0;
        for (int i = 1; i < 1 << this.heightOfTrees[n]; ++i) {
            if (i == n2 && n3 < this.heightOfTrees[n] - this.K[n]) {
                gmssRootCalc.initializeTreehashSeed(array2, n3);
                n2 *= 2;
                ++n3;
            }
            gmssRootCalc.update(new WinternitzOTSignature(this.gmssRandom.nextSeed(array2), this.digestProvider.get(), this.otsIndex[n]).getPublicKey());
        }
        if (gmssRootCalc.wasFinished()) {
            return gmssRootCalc;
        }
        System.err.println("Baum noch nicht fertig konstruiert!!!");
        return null;
    }
    
    private GMSSRootCalc generateNextAuthpathAndRoot(final Vector vector, final byte[] array, final int n) {
        final byte[] array2 = new byte[this.numLayer];
        final GMSSRootCalc gmssRootCalc = new GMSSRootCalc(this.heightOfTrees[n], this.K[n], this.digestProvider);
        gmssRootCalc.initialize(vector);
        int n2 = 3;
        int n3 = 0;
        for (int i = 0; i < 1 << this.heightOfTrees[n]; ++i) {
            if (i == n2 && n3 < this.heightOfTrees[n] - this.K[n]) {
                gmssRootCalc.initializeTreehashSeed(array, n3);
                n2 *= 2;
                ++n3;
            }
            gmssRootCalc.update(new WinternitzOTSignature(this.gmssRandom.nextSeed(array), this.digestProvider.get(), this.otsIndex[n]).getPublicKey());
        }
        if (gmssRootCalc.wasFinished()) {
            return gmssRootCalc;
        }
        System.err.println("N\ufffdchster Baum noch nicht fertig konstruiert!!!");
        return null;
    }
    
    public void initialize(final int n, final SecureRandom secureRandom) {
        GMSSKeyGenerationParameters gmssKeyGenerationParameters;
        if (n <= 10) {
            final int[] array = { 10 };
            gmssKeyGenerationParameters = new GMSSKeyGenerationParameters(secureRandom, new GMSSParameters(array.length, array, new int[] { 3 }, new int[] { 2 }));
        }
        else if (n <= 20) {
            final int[] array2 = { 10, 10 };
            gmssKeyGenerationParameters = new GMSSKeyGenerationParameters(secureRandom, new GMSSParameters(array2.length, array2, new int[] { 5, 4 }, new int[] { 2, 2 }));
        }
        else {
            final int[] array3 = { 10, 10, 10, 10 };
            gmssKeyGenerationParameters = new GMSSKeyGenerationParameters(secureRandom, new GMSSParameters(array3.length, array3, new int[] { 9, 9, 9, 3 }, new int[] { 2, 2, 2, 2 }));
        }
        this.initialize(gmssKeyGenerationParameters);
    }
    
    public void initialize(final KeyGenerationParameters keyGenerationParameters) {
        this.gmssParams = (GMSSKeyGenerationParameters)keyGenerationParameters;
        this.gmssPS = new GMSSParameters(this.gmssParams.getParameters().getNumOfLayers(), this.gmssParams.getParameters().getHeightOfTrees(), this.gmssParams.getParameters().getWinternitzParameter(), this.gmssParams.getParameters().getK());
        this.numLayer = this.gmssPS.getNumOfLayers();
        this.heightOfTrees = this.gmssPS.getHeightOfTrees();
        this.otsIndex = this.gmssPS.getWinternitzParameter();
        this.K = this.gmssPS.getK();
        this.currentSeeds = new byte[this.numLayer][this.mdLength];
        this.nextNextSeeds = new byte[this.numLayer - 1][this.mdLength];
        final SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < this.numLayer; ++i) {
            secureRandom.nextBytes(this.currentSeeds[i]);
            this.gmssRandom.nextSeed(this.currentSeeds[i]);
        }
        this.initialized = true;
    }
    
    private void initializeDefault() {
        final int[] array = { 10, 10, 10, 10 };
        this.initialize(new GMSSKeyGenerationParameters(new SecureRandom(), new GMSSParameters(array.length, array, new int[] { 3, 3, 3, 3 }, new int[] { 2, 2, 2, 2 })));
    }
    
    public void init(final KeyGenerationParameters keyGenerationParameters) {
        this.initialize(keyGenerationParameters);
    }
    
    public AsymmetricCipherKeyPair generateKeyPair() {
        return this.genKeyPair();
    }
}
