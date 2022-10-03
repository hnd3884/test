package com.google.zxing.aztec;

import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DetectorResult;

public final class AztecDetectorResult extends DetectorResult
{
    private final boolean compact;
    private final int nbDatablocks;
    private final int nbLayers;
    
    public AztecDetectorResult(final BitMatrix bits, final ResultPoint[] points, final boolean compact, final int nbDatablocks, final int nbLayers) {
        super(bits, points);
        this.compact = compact;
        this.nbDatablocks = nbDatablocks;
        this.nbLayers = nbLayers;
    }
    
    public int getNbLayers() {
        return this.nbLayers;
    }
    
    public int getNbDatablocks() {
        return this.nbDatablocks;
    }
    
    public boolean isCompact() {
        return this.compact;
    }
}
