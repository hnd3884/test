package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.params.SkeinParameters;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.crypto.ExtendedDigest;

public class SkeinDigest implements ExtendedDigest, Memoable
{
    public static final int SKEIN_256 = 256;
    public static final int SKEIN_512 = 512;
    public static final int SKEIN_1024 = 1024;
    private SkeinEngine engine;
    
    public SkeinDigest(final int n, final int n2) {
        this.engine = new SkeinEngine(n, n2);
        this.init(null);
    }
    
    public SkeinDigest(final SkeinDigest skeinDigest) {
        this.engine = new SkeinEngine(skeinDigest.engine);
    }
    
    public void reset(final Memoable memoable) {
        this.engine.reset(((SkeinDigest)memoable).engine);
    }
    
    public Memoable copy() {
        return new SkeinDigest(this);
    }
    
    public String getAlgorithmName() {
        return "Skein-" + this.engine.getBlockSize() * 8 + "-" + this.engine.getOutputSize() * 8;
    }
    
    public int getDigestSize() {
        return this.engine.getOutputSize();
    }
    
    public int getByteLength() {
        return this.engine.getBlockSize();
    }
    
    public void init(final SkeinParameters skeinParameters) {
        this.engine.init(skeinParameters);
    }
    
    public void reset() {
        this.engine.reset();
    }
    
    public void update(final byte b) {
        this.engine.update(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.engine.update(array, n, n2);
    }
    
    public int doFinal(final byte[] array, final int n) {
        return this.engine.doFinal(array, n);
    }
}
