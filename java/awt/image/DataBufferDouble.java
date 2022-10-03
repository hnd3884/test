package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferDouble extends DataBuffer
{
    double[][] bankdata;
    double[] data;
    
    public DataBufferDouble(final int n) {
        super(StateTrackable.State.STABLE, 5, n);
        this.data = new double[n];
        (this.bankdata = new double[1][])[0] = this.data;
    }
    
    public DataBufferDouble(final int n, final int n2) {
        super(StateTrackable.State.STABLE, 5, n, n2);
        this.bankdata = new double[n2][];
        for (int i = 0; i < n2; ++i) {
            this.bankdata[i] = new double[n];
        }
        this.data = this.bankdata[0];
    }
    
    public DataBufferDouble(final double[] data, final int n) {
        super(StateTrackable.State.UNTRACKABLE, 5, n);
        this.data = data;
        (this.bankdata = new double[1][])[0] = this.data;
    }
    
    public DataBufferDouble(final double[] data, final int n, final int n2) {
        super(StateTrackable.State.UNTRACKABLE, 5, n, 1, n2);
        this.data = data;
        (this.bankdata = new double[1][])[0] = this.data;
    }
    
    public DataBufferDouble(final double[][] array, final int n) {
        super(StateTrackable.State.UNTRACKABLE, 5, n, array.length);
        this.bankdata = array.clone();
        this.data = this.bankdata[0];
    }
    
    public DataBufferDouble(final double[][] array, final int n, final int[] array2) {
        super(StateTrackable.State.UNTRACKABLE, 5, n, array.length, array2);
        this.bankdata = array.clone();
        this.data = this.bankdata[0];
    }
    
    public double[] getData() {
        this.theTrackable.setUntrackable();
        return this.data;
    }
    
    public double[] getData(final int n) {
        this.theTrackable.setUntrackable();
        return this.bankdata[n];
    }
    
    public double[][] getBankData() {
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
        this.data[n + this.offset] = n2;
        this.theTrackable.markDirty();
    }
    
    @Override
    public void setElem(final int n, final int n2, final int n3) {
        this.bankdata[n][n2 + this.offsets[n]] = n3;
        this.theTrackable.markDirty();
    }
    
    @Override
    public float getElemFloat(final int n) {
        return (float)this.data[n + this.offset];
    }
    
    @Override
    public float getElemFloat(final int n, final int n2) {
        return (float)this.bankdata[n][n2 + this.offsets[n]];
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
        this.data[n + this.offset] = n2;
        this.theTrackable.markDirty();
    }
    
    @Override
    public void setElemDouble(final int n, final int n2, final double n3) {
        this.bankdata[n][n2 + this.offsets[n]] = n3;
        this.theTrackable.markDirty();
    }
}
