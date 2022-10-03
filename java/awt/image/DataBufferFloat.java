package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferFloat extends DataBuffer
{
    float[][] bankdata;
    float[] data;
    
    public DataBufferFloat(final int n) {
        super(StateTrackable.State.STABLE, 4, n);
        this.data = new float[n];
        (this.bankdata = new float[1][])[0] = this.data;
    }
    
    public DataBufferFloat(final int n, final int n2) {
        super(StateTrackable.State.STABLE, 4, n, n2);
        this.bankdata = new float[n2][];
        for (int i = 0; i < n2; ++i) {
            this.bankdata[i] = new float[n];
        }
        this.data = this.bankdata[0];
    }
    
    public DataBufferFloat(final float[] data, final int n) {
        super(StateTrackable.State.UNTRACKABLE, 4, n);
        this.data = data;
        (this.bankdata = new float[1][])[0] = this.data;
    }
    
    public DataBufferFloat(final float[] data, final int n, final int n2) {
        super(StateTrackable.State.UNTRACKABLE, 4, n, 1, n2);
        this.data = data;
        (this.bankdata = new float[1][])[0] = this.data;
    }
    
    public DataBufferFloat(final float[][] array, final int n) {
        super(StateTrackable.State.UNTRACKABLE, 4, n, array.length);
        this.bankdata = array.clone();
        this.data = this.bankdata[0];
    }
    
    public DataBufferFloat(final float[][] array, final int n, final int[] array2) {
        super(StateTrackable.State.UNTRACKABLE, 4, n, array.length, array2);
        this.bankdata = array.clone();
        this.data = this.bankdata[0];
    }
    
    public float[] getData() {
        this.theTrackable.setUntrackable();
        return this.data;
    }
    
    public float[] getData(final int n) {
        this.theTrackable.setUntrackable();
        return this.bankdata[n];
    }
    
    public float[][] getBankData() {
        this.theTrackable.setUntrackable();
        return this.bankdata.clone();
    }
    
    @Override
    public int getElem(final int n) {
        return (int)this.data[n + this.offset];
    }
    
    @Override
    public int getElem(final int n, final int n2) {
        return (int)this.bankdata[n][n2 + this.offsets[n]];
    }
    
    @Override
    public void setElem(final int n, final int n2) {
        this.data[n + this.offset] = (float)n2;
        this.theTrackable.markDirty();
    }
    
    @Override
    public void setElem(final int n, final int n2, final int n3) {
        this.bankdata[n][n2 + this.offsets[n]] = (float)n3;
        this.theTrackable.markDirty();
    }
    
    @Override
    public float getElemFloat(final int n) {
        return this.data[n + this.offset];
    }
    
    @Override
    public float getElemFloat(final int n, final int n2) {
        return this.bankdata[n][n2 + this.offsets[n]];
    }
    
    @Override
    public void setElemFloat(final int n, final float n2) {
        this.data[n + this.offset] = n2;
        this.theTrackable.markDirty();
    }
    
    @Override
    public void setElemFloat(final int n, final int n2, final float n3) {
        this.bankdata[n][n2 + this.offsets[n]] = n3;
        this.theTrackable.markDirty();
    }
    
    @Override
    public double getElemDouble(final int n) {
        return this.data[n + this.offset];
    }
    
    @Override
    public double getElemDouble(final int n, final int n2) {
        return this.bankdata[n][n2 + this.offsets[n]];
    }
    
    @Override
    public void setElemDouble(final int n, final double n2) {
        this.data[n + this.offset] = (float)n2;
        this.theTrackable.markDirty();
    }
    
    @Override
    public void setElemDouble(final int n, final int n2, final double n3) {
        this.bankdata[n][n2 + this.offsets[n]] = (float)n3;
        this.theTrackable.markDirty();
    }
}
