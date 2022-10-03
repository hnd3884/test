package org.tukaani.xz;

import java.io.IOException;
import java.io.InputStream;

public class LZMA2Options extends FilterOptions
{
    public static final int PRESET_MIN = 0;
    public static final int PRESET_MAX = 9;
    public static final int PRESET_DEFAULT = 6;
    public static final int DICT_SIZE_MIN = 4096;
    public static final int DICT_SIZE_MAX = 805306368;
    public static final int DICT_SIZE_DEFAULT = 8388608;
    public static final int LC_LP_MAX = 4;
    public static final int LC_DEFAULT = 3;
    public static final int LP_DEFAULT = 0;
    public static final int PB_MAX = 4;
    public static final int PB_DEFAULT = 2;
    public static final int MODE_UNCOMPRESSED = 0;
    public static final int MODE_FAST = 1;
    public static final int MODE_NORMAL = 2;
    public static final int NICE_LEN_MIN = 8;
    public static final int NICE_LEN_MAX = 273;
    public static final int MF_HC4 = 4;
    public static final int MF_BT4 = 20;
    private static final int[] presetToDictSize;
    private static final int[] presetToDepthLimit;
    private int dictSize;
    private byte[] presetDict;
    private int lc;
    private int lp;
    private int pb;
    private int mode;
    private int niceLen;
    private int mf;
    private int depthLimit;
    
    public LZMA2Options() {
        this.presetDict = null;
        try {
            this.setPreset(6);
        }
        catch (final UnsupportedOptionsException ex) {
            assert false;
            throw new RuntimeException();
        }
    }
    
    public LZMA2Options(final int preset) throws UnsupportedOptionsException {
        this.presetDict = null;
        this.setPreset(preset);
    }
    
    public LZMA2Options(final int dictSize, final int n, final int n2, final int pb, final int mode, final int niceLen, final int matchFinder, final int depthLimit) throws UnsupportedOptionsException {
        this.presetDict = null;
        this.setDictSize(dictSize);
        this.setLcLp(n, n2);
        this.setPb(pb);
        this.setMode(mode);
        this.setNiceLen(niceLen);
        this.setMatchFinder(matchFinder);
        this.setDepthLimit(depthLimit);
    }
    
    public void setPreset(final int n) throws UnsupportedOptionsException {
        if (n < 0 || n > 9) {
            throw new UnsupportedOptionsException("Unsupported preset: " + n);
        }
        this.lc = 3;
        this.lp = 0;
        this.pb = 2;
        this.dictSize = LZMA2Options.presetToDictSize[n];
        if (n <= 3) {
            this.mode = 1;
            this.mf = 4;
            this.niceLen = ((n <= 1) ? 128 : 273);
            this.depthLimit = LZMA2Options.presetToDepthLimit[n];
        }
        else {
            this.mode = 2;
            this.mf = 20;
            this.niceLen = ((n == 4) ? 16 : ((n == 5) ? 32 : 64));
            this.depthLimit = 0;
        }
    }
    
    public void setDictSize(final int dictSize) throws UnsupportedOptionsException {
        if (dictSize < 4096) {
            throw new UnsupportedOptionsException("LZMA2 dictionary size must be at least 4 KiB: " + dictSize + " B");
        }
        if (dictSize > 805306368) {
            throw new UnsupportedOptionsException("LZMA2 dictionary size must not exceed 768 MiB: " + dictSize + " B");
        }
        this.dictSize = dictSize;
    }
    
    public int getDictSize() {
        return this.dictSize;
    }
    
    public void setPresetDict(final byte[] presetDict) {
        this.presetDict = presetDict;
    }
    
    public byte[] getPresetDict() {
        return this.presetDict;
    }
    
    public void setLcLp(final int lc, final int lp) throws UnsupportedOptionsException {
        if (lc < 0 || lp < 0 || lc > 4 || lp > 4 || lc + lp > 4) {
            throw new UnsupportedOptionsException("lc + lp must not exceed 4: " + lc + " + " + lp);
        }
        this.lc = lc;
        this.lp = lp;
    }
    
    public void setLc(final int n) throws UnsupportedOptionsException {
        this.setLcLp(n, this.lp);
    }
    
    public void setLp(final int n) throws UnsupportedOptionsException {
        this.setLcLp(this.lc, n);
    }
    
    public int getLc() {
        return this.lc;
    }
    
    public int getLp() {
        return this.lp;
    }
    
    public void setPb(final int pb) throws UnsupportedOptionsException {
        if (pb < 0 || pb > 4) {
            throw new UnsupportedOptionsException("pb must not exceed 4: " + pb);
        }
        this.pb = pb;
    }
    
    public int getPb() {
        return this.pb;
    }
    
    public void setMode(final int mode) throws UnsupportedOptionsException {
        if (mode < 0 || mode > 2) {
            throw new UnsupportedOptionsException("Unsupported compression mode: " + mode);
        }
        this.mode = mode;
    }
    
    public int getMode() {
        return this.mode;
    }
    
    public void setNiceLen(final int niceLen) throws UnsupportedOptionsException {
        if (niceLen < 8) {
            throw new UnsupportedOptionsException("Minimum nice length of matches is 8 bytes: " + niceLen);
        }
        if (niceLen > 273) {
            throw new UnsupportedOptionsException("Maximum nice length of matches is 273: " + niceLen);
        }
        this.niceLen = niceLen;
    }
    
    public int getNiceLen() {
        return this.niceLen;
    }
    
    public void setMatchFinder(final int mf) throws UnsupportedOptionsException {
        if (mf != 4 && mf != 20) {
            throw new UnsupportedOptionsException("Unsupported match finder: " + mf);
        }
        this.mf = mf;
    }
    
    public int getMatchFinder() {
        return this.mf;
    }
    
    public void setDepthLimit(final int depthLimit) throws UnsupportedOptionsException {
        if (depthLimit < 0) {
            throw new UnsupportedOptionsException("Depth limit cannot be negative: " + depthLimit);
        }
        this.depthLimit = depthLimit;
    }
    
    public int getDepthLimit() {
        return this.depthLimit;
    }
    
    @Override
    public int getEncoderMemoryUsage() {
        return (this.mode == 0) ? UncompressedLZMA2OutputStream.getMemoryUsage() : LZMA2OutputStream.getMemoryUsage(this);
    }
    
    @Override
    public FinishableOutputStream getOutputStream(final FinishableOutputStream finishableOutputStream, final ArrayCache arrayCache) {
        if (this.mode == 0) {
            return new UncompressedLZMA2OutputStream(finishableOutputStream, arrayCache);
        }
        return new LZMA2OutputStream(finishableOutputStream, this, arrayCache);
    }
    
    @Override
    public int getDecoderMemoryUsage() {
        final int n = this.dictSize - 1;
        final int n2 = n | n >>> 2;
        final int n3 = n2 | n2 >>> 3;
        final int n4 = n3 | n3 >>> 4;
        final int n5 = n4 | n4 >>> 8;
        return LZMA2InputStream.getMemoryUsage((n5 | n5 >>> 16) + 1);
    }
    
    @Override
    public InputStream getInputStream(final InputStream inputStream, final ArrayCache arrayCache) throws IOException {
        return new LZMA2InputStream(inputStream, this.dictSize, this.presetDict, arrayCache);
    }
    
    @Override
    FilterEncoder getFilterEncoder() {
        return new LZMA2Encoder(this);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            assert false;
            throw new RuntimeException();
        }
    }
    
    static {
        presetToDictSize = new int[] { 262144, 1048576, 2097152, 4194304, 4194304, 8388608, 8388608, 16777216, 33554432, 67108864 };
        presetToDepthLimit = new int[] { 4, 8, 24, 48 };
    }
}
