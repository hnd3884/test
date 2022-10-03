package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferShort extends DataBuffer
{
    short[] data;
    short[][] bankdata;
    
    public DataBufferShort(final int n) {
        super(StateTrackable.State.STABLE, 2, n);
        this.data = new short[n];
        (this.bankdata = new short[1][])[0] = this.data;
    }
    
    public DataBufferShort(final int n, final int n2) {
        super(StateTrackable.State.STABLE, 2, n, n2);
        this.bankdata = new short[n2][];
        for (int i = 0; i < n2; ++i) {
            this.bankdata[i] = new short[n];
        }
        this.data = this.bankdata[0];
    }
    
    public DataBufferShort(final short[] data, final int n) {
        super(StateTrackable.State.UNTRACKABLE, 2, n);
        this.data = data;
        (this.bankdata = new short[1][])[0] = this.data;
    }
    
    public DataBufferShort(final short[] data, final int n, final int n2) {
        super(StateTrackable.State.UNTRACKABLE, 2, n, 1, n2);
        this.data = data;
        (this.bankdata = new short[1][])[0] = this.data;
    }
    
    public DataBufferShort(final short[][] array, final int n) {
        super(StateTrackable.State.UNTRACKABLE, 2, n, array.length);
        this.bankdata = array.clone();
        this.data = this.bankdata[0];
    }
    
    public DataBufferShort(final short[][] array, final int n, final int[] array2) {
        super(StateTrackable.State.UNTRACKABLE, 2, n, array.length, array2);
        this.bankdata = array.clone();
        this.data = this.bankdata[0];
    }
    
    public short[] getData() {
        this.theTrackable.setUntrackable();
        return this.data;
    }
    
    public short[] getData(final int n) {
        this.theTrackable.setUntrackable();
        return this.bankdata[n];
    }
    
    public short[][] getBankData() {
        this.theTrackable.setUntrackable();
        return this.bankdata.clone();
    }
    
    @Override
    public int getElem(final int n) {
        return this.data[n + this.offset];
    }
    
    @Override
    public int getElem(final int n, final int n2) {
        return this.bankdata[n][n2 + this.offsets[n]];
    }
    
    @Override
    public void setElem(final int n, final int n2) {
        this.data[n + this.offset] = (short)n2;
        this.theTrackable.markDirty();
    }
    
    @Override
    public void setElem(final int n, final int n2, final int n3) {
        this.bankdata[n][n2 + this.offsets[n]] = (short)n3;
        this.theTrackable.markDirty();
    }
}
