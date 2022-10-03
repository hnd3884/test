package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferUShort extends DataBuffer
{
    short[] data;
    short[][] bankdata;
    
    public DataBufferUShort(final int n) {
        super(StateTrackable.State.STABLE, 1, n);
        this.data = new short[n];
        (this.bankdata = new short[1][])[0] = this.data;
    }
    
    public DataBufferUShort(final int n, final int n2) {
        super(StateTrackable.State.STABLE, 1, n, n2);
        this.bankdata = new short[n2][];
        for (int i = 0; i < n2; ++i) {
            this.bankdata[i] = new short[n];
        }
        this.data = this.bankdata[0];
    }
    
    public DataBufferUShort(final short[] data, final int n) {
        super(StateTrackable.State.UNTRACKABLE, 1, n);
        if (data == null) {
            throw new NullPointerException("dataArray is null");
        }
        this.data = data;
        (this.bankdata = new short[1][])[0] = this.data;
    }
    
    public DataBufferUShort(final short[] data, final int n, final int n2) {
        super(StateTrackable.State.UNTRACKABLE, 1, n, 1, n2);
        if (data == null) {
            throw new NullPointerException("dataArray is null");
        }
        if (n + n2 > data.length) {
            throw new IllegalArgumentException("Length of dataArray is less  than size+offset.");
        }
        this.data = data;
        (this.bankdata = new short[1][])[0] = this.data;
    }
    
    public DataBufferUShort(final short[][] array, final int n) {
        super(StateTrackable.State.UNTRACKABLE, 1, n, array.length);
        if (array == null) {
            throw new NullPointerException("dataArray is null");
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null) {
                throw new NullPointerException("dataArray[" + i + "] is null");
            }
        }
        this.bankdata = array.clone();
        this.data = this.bankdata[0];
    }
    
    public DataBufferUShort(final short[][] array, final int n, final int[] array2) {
        super(StateTrackable.State.UNTRACKABLE, 1, n, array.length, array2);
        if (array == null) {
            throw new NullPointerException("dataArray is null");
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null) {
                throw new NullPointerException("dataArray[" + i + "] is null");
            }
            if (n + array2[i] > array[i].length) {
                throw new IllegalArgumentException("Length of dataArray[" + i + "] is less than size+offsets[" + i + "].");
            }
        }
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
        return this.data[n + this.offset] & 0xFFFF;
    }
    
    @Override
    public int getElem(final int n, final int n2) {
        return this.bankdata[n][n2 + this.offsets[n]] & 0xFFFF;
    }
    
    @Override
    public void setElem(final int n, final int n2) {
        this.data[n + this.offset] = (short)(n2 & 0xFFFF);
        this.theTrackable.markDirty();
    }
    
    @Override
    public void setElem(final int n, final int n2, final int n3) {
        this.bankdata[n][n2 + this.offsets[n]] = (short)(n3 & 0xFFFF);
        this.theTrackable.markDirty();
    }
}
