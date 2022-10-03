package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.SkeinParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.SkeinEngine;
import org.bouncycastle.crypto.Mac;

public class SkeinMac implements Mac
{
    public static final int SKEIN_256 = 256;
    public static final int SKEIN_512 = 512;
    public static final int SKEIN_1024 = 1024;
    private SkeinEngine engine;
    
    public SkeinMac(final int n, final int n2) {
        this.engine = new SkeinEngine(n, n2);
    }
    
    public SkeinMac(final SkeinMac skeinMac) {
        this.engine = new SkeinEngine(skeinMac.engine);
    }
    
    public String getAlgorithmName() {
        return "Skein-MAC-" + this.engine.getBlockSize() * 8 + "-" + this.engine.getOutputSize() * 8;
    }
    
    public void init(final CipherParameters cipherParameters) throws IllegalArgumentException {
        SkeinParameters build;
        if (cipherParameters instanceof SkeinParameters) {
            build = (SkeinParameters)cipherParameters;
        }
        else {
            if (!(cipherParameters instanceof KeyParameter)) {
                throw new IllegalArgumentException("Invalid parameter passed to Skein MAC init - " + cipherParameters.getClass().getName());
            }
            build = new SkeinParameters.Builder().setKey(((KeyParameter)cipherParameters).getKey()).build();
        }
        if (build.getKey() == null) {
            throw new IllegalArgumentException("Skein MAC requires a key parameter.");
        }
        this.engine.init(build);
    }
    
    public int getMacSize() {
        return this.engine.getOutputSize();
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
