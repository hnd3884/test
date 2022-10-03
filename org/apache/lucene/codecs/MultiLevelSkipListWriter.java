package org.apache.lucene.codecs;

import org.apache.lucene.store.DataOutput;
import java.io.IOException;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.MathUtil;
import org.apache.lucene.store.RAMOutputStream;

public abstract class MultiLevelSkipListWriter
{
    protected int numberOfSkipLevels;
    private int skipInterval;
    private int skipMultiplier;
    private RAMOutputStream[] skipBuffer;
    
    protected MultiLevelSkipListWriter(final int skipInterval, final int skipMultiplier, final int maxSkipLevels, final int df) {
        this.skipInterval = skipInterval;
        this.skipMultiplier = skipMultiplier;
        if (df <= skipInterval) {
            this.numberOfSkipLevels = 1;
        }
        else {
            this.numberOfSkipLevels = 1 + MathUtil.log(df / skipInterval, skipMultiplier);
        }
        if (this.numberOfSkipLevels > maxSkipLevels) {
            this.numberOfSkipLevels = maxSkipLevels;
        }
    }
    
    protected MultiLevelSkipListWriter(final int skipInterval, final int maxSkipLevels, final int df) {
        this(skipInterval, skipInterval, maxSkipLevels, df);
    }
    
    protected void init() {
        this.skipBuffer = new RAMOutputStream[this.numberOfSkipLevels];
        for (int i = 0; i < this.numberOfSkipLevels; ++i) {
            this.skipBuffer[i] = new RAMOutputStream();
        }
    }
    
    protected void resetSkip() {
        if (this.skipBuffer == null) {
            this.init();
        }
        else {
            for (int i = 0; i < this.skipBuffer.length; ++i) {
                this.skipBuffer[i].reset();
            }
        }
    }
    
    protected abstract void writeSkipData(final int p0, final IndexOutput p1) throws IOException;
    
    public void bufferSkip(int df) throws IOException {
        assert df % this.skipInterval == 0;
        int numLevels;
        for (numLevels = 1, df /= this.skipInterval; df % this.skipMultiplier == 0 && numLevels < this.numberOfSkipLevels; ++numLevels, df /= this.skipMultiplier) {}
        long childPointer = 0L;
        for (int level = 0; level < numLevels; ++level) {
            this.writeSkipData(level, this.skipBuffer[level]);
            final long newChildPointer = this.skipBuffer[level].getFilePointer();
            if (level != 0) {
                this.skipBuffer[level].writeVLong(childPointer);
            }
            childPointer = newChildPointer;
        }
    }
    
    public long writeSkip(final IndexOutput output) throws IOException {
        final long skipPointer = output.getFilePointer();
        if (this.skipBuffer == null || this.skipBuffer.length == 0) {
            return skipPointer;
        }
        for (int level = this.numberOfSkipLevels - 1; level > 0; --level) {
            final long length = this.skipBuffer[level].getFilePointer();
            if (length > 0L) {
                output.writeVLong(length);
                this.skipBuffer[level].writeTo(output);
            }
        }
        this.skipBuffer[0].writeTo(output);
        return skipPointer;
    }
}
