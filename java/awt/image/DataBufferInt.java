package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferInt extends DataBuffer
{
    int[] data;
    int[][] bankdata;
    
    public DataBufferInt(final int n) {
        super(StateTrackable.State.STABLE, 3, n);
        this.data = new int[n];
        (this.bankdata = new int[1][])[0] = this.data;
    }
    
    public DataBufferInt(final int n, final int n2) {
        super(StateTrackable.State.STABLE, 3, n, n2);
        this.bankdata = new int[n2][];
        for (int i = 0; i < n2; ++i) {
            this.bankdata[i] = new int[n];
        }
        this.data = this.bankdata[0];
    }
    
    public DataBufferInt(final int[] data, final int n) {
        super(StateTrackable.State.UNTRACKABLE, 3, n);
        this.data = data;
        (this.bankdata = new int[1][])[0] = this.data;
    }
    
    public DataBufferInt(final int[] data, final int n, final int n2) {
        super(StateTrackable.State.UNTRACKABLE, 3, n, 1, n2);
        this.data = data;
        (this.bankdata = new int[1][])[0] = this.data;
    }
    
    public DataBufferInt(final int[][] array, final int n) {
        super(StateTrackable.State.UNTRACKABLE, 3, n, array.length);
        this.bankdata = array.clone();
        this.data = this.bankdata[0];
    }
    
    public DataBufferInt(final int[][] array, final int n, final int[] array2) {
        super(StateTrackable.State.UNTRACKABLE, 3, n, array.length, array2);
        this.bankdata = array.clone();
        this.data = this.bankdata[0];
    }
    
    public int[] getData() {
        this.theTrackable.setUntrackable();
        return this.data;
    }
    
    public int[] getData(final int n) {
        this.theTrackable.setUntrackable();
        return this.bankdata[n];
    }
    
    public int[][] getBankData() {
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
        this.data[n + this.offset] = n2;
        this.theTrackable.markDirty();
    }
    
    @Override
    public void setElem(final int n, final int n2, final int n3) {
        this.bankdata[n][n2 + this.offsets[n]] = n3;
        this.theTrackable.markDirty();
    }
}
