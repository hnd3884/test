package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferByte extends DataBuffer
{
    byte[] data;
    byte[][] bankdata;
    
    public DataBufferByte(final int n) {
        super(StateTrackable.State.STABLE, 0, n);
        this.data = new byte[n];
        (this.bankdata = new byte[1][])[0] = this.data;
    }
    
    public DataBufferByte(final int n, final int n2) {
        super(StateTrackable.State.STABLE, 0, n, n2);
        this.bankdata = new byte[n2][];
        for (int i = 0; i < n2; ++i) {
            this.bankdata[i] = new byte[n];
        }
        this.data = this.bankdata[0];
    }
    
    public DataBufferByte(final byte[] data, final int n) {
        super(StateTrackable.State.UNTRACKABLE, 0, n);
        this.data = data;
        (this.bankdata = new byte[1][])[0] = this.data;
    }
    
    public DataBufferByte(final byte[] data, final int n, final int n2) {
        super(StateTrackable.State.UNTRACKABLE, 0, n, 1, n2);
        this.data = data;
        (this.bankdata = new byte[1][])[0] = this.data;
    }
    
    public DataBufferByte(final byte[][] array, final int n) {
        super(StateTrackable.State.UNTRACKABLE, 0, n, array.length);
        this.bankdata = array.clone();
        this.data = this.bankdata[0];
    }
    
    public DataBufferByte(final byte[][] array, final int n, final int[] array2) {
        super(StateTrackable.State.UNTRACKABLE, 0, n, array.length, array2);
        this.bankdata = array.clone();
        this.data = this.bankdata[0];
    }
    
    public byte[] getData() {
        this.theTrackable.setUntrackable();
        return this.data;
    }
    
    public byte[] getData(final int n) {
        this.theTrackable.setUntrackable();
        return this.bankdata[n];
    }
    
    public byte[][] getBankData() {
        this.theTrackable.setUntrackable();
        return this.bankdata.clone();
    }
    
    @Override
    public int getElem(final int n) {
        return this.data[n + this.offset] & 0xFF;
    }
    
    @Override
    public int getElem(final int n, final int n2) {
        return this.bankdata[n][n2 + this.offsets[n]] & 0xFF;
    }
    
    @Override
    public void setElem(final int n, final int n2) {
        this.data[n + this.offset] = (byte)n2;
        this.theTrackable.markDirty();
    }
    
    @Override
    public void setElem(final int n, final int n2, final int n3) {
        this.bankdata[n][n2 + this.offsets[n]] = (byte)n3;
        this.theTrackable.markDirty();
    }
}
