package java.awt.image;

import sun.awt.image.SunWritableRaster;
import sun.java2d.StateTrackable;
import sun.java2d.StateTrackableDelegate;

public abstract class DataBuffer
{
    public static final int TYPE_BYTE = 0;
    public static final int TYPE_USHORT = 1;
    public static final int TYPE_SHORT = 2;
    public static final int TYPE_INT = 3;
    public static final int TYPE_FLOAT = 4;
    public static final int TYPE_DOUBLE = 5;
    public static final int TYPE_UNDEFINED = 32;
    protected int dataType;
    protected int banks;
    protected int offset;
    protected int size;
    protected int[] offsets;
    StateTrackableDelegate theTrackable;
    private static final int[] dataTypeSize;
    
    public static int getDataTypeSize(final int n) {
        if (n < 0 || n > 5) {
            throw new IllegalArgumentException("Unknown data type " + n);
        }
        return DataBuffer.dataTypeSize[n];
    }
    
    protected DataBuffer(final int n, final int n2) {
        this(StateTrackable.State.UNTRACKABLE, n, n2);
    }
    
    DataBuffer(final StateTrackable.State state, final int dataType, final int size) {
        this.theTrackable = StateTrackableDelegate.createInstance(state);
        this.dataType = dataType;
        this.banks = 1;
        this.size = size;
        this.offset = 0;
        this.offsets = new int[1];
    }
    
    protected DataBuffer(final int n, final int n2, final int n3) {
        this(StateTrackable.State.UNTRACKABLE, n, n2, n3);
    }
    
    DataBuffer(final StateTrackable.State state, final int dataType, final int size, final int banks) {
        this.theTrackable = StateTrackableDelegate.createInstance(state);
        this.dataType = dataType;
        this.banks = banks;
        this.size = size;
        this.offset = 0;
        this.offsets = new int[this.banks];
    }
    
    protected DataBuffer(final int n, final int n2, final int n3, final int n4) {
        this(StateTrackable.State.UNTRACKABLE, n, n2, n3, n4);
    }
    
    DataBuffer(final StateTrackable.State state, final int dataType, final int size, final int banks, final int offset) {
        this.theTrackable = StateTrackableDelegate.createInstance(state);
        this.dataType = dataType;
        this.banks = banks;
        this.size = size;
        this.offset = offset;
        this.offsets = new int[banks];
        for (int i = 0; i < banks; ++i) {
            this.offsets[i] = offset;
        }
    }
    
    protected DataBuffer(final int n, final int n2, final int n3, final int[] array) {
        this(StateTrackable.State.UNTRACKABLE, n, n2, n3, array);
    }
    
    DataBuffer(final StateTrackable.State state, final int dataType, final int size, final int banks, final int[] array) {
        if (banks != array.length) {
            throw new ArrayIndexOutOfBoundsException("Number of banks does not match number of bank offsets");
        }
        this.theTrackable = StateTrackableDelegate.createInstance(state);
        this.dataType = dataType;
        this.banks = banks;
        this.size = size;
        this.offset = array[0];
        this.offsets = array.clone();
    }
    
    public int getDataType() {
        return this.dataType;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public int getOffset() {
        return this.offset;
    }
    
    public int[] getOffsets() {
        return this.offsets.clone();
    }
    
    public int getNumBanks() {
        return this.banks;
    }
    
    public int getElem(final int n) {
        return this.getElem(0, n);
    }
    
    public abstract int getElem(final int p0, final int p1);
    
    public void setElem(final int n, final int n2) {
        this.setElem(0, n, n2);
    }
    
    public abstract void setElem(final int p0, final int p1, final int p2);
    
    public float getElemFloat(final int n) {
        return (float)this.getElem(n);
    }
    
    public float getElemFloat(final int n, final int n2) {
        return (float)this.getElem(n, n2);
    }
    
    public void setElemFloat(final int n, final float n2) {
        this.setElem(n, (int)n2);
    }
    
    public void setElemFloat(final int n, final int n2, final float n3) {
        this.setElem(n, n2, (int)n3);
    }
    
    public double getElemDouble(final int n) {
        return this.getElem(n);
    }
    
    public double getElemDouble(final int n, final int n2) {
        return this.getElem(n, n2);
    }
    
    public void setElemDouble(final int n, final double n2) {
        this.setElem(n, (int)n2);
    }
    
    public void setElemDouble(final int n, final int n2, final double n3) {
        this.setElem(n, n2, (int)n3);
    }
    
    static int[] toIntArray(final Object o) {
        if (o instanceof int[]) {
            return (int[])o;
        }
        if (o == null) {
            return null;
        }
        if (o instanceof short[]) {
            final short[] array = (short[])o;
            final int[] array2 = new int[array.length];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = (array[i] & 0xFFFF);
            }
            return array2;
        }
        if (o instanceof byte[]) {
            final byte[] array3 = (byte[])o;
            final int[] array4 = new int[array3.length];
            for (int j = 0; j < array3.length; ++j) {
                array4[j] = (0xFF & array3[j]);
            }
            return array4;
        }
        return null;
    }
    
    static {
        dataTypeSize = new int[] { 8, 16, 16, 32, 32, 64 };
        SunWritableRaster.setDataStealer(new SunWritableRaster.DataStealer() {
            @Override
            public byte[] getData(final DataBufferByte dataBufferByte, final int n) {
                return dataBufferByte.bankdata[n];
            }
            
            @Override
            public short[] getData(final DataBufferUShort dataBufferUShort, final int n) {
                return dataBufferUShort.bankdata[n];
            }
            
            @Override
            public int[] getData(final DataBufferInt dataBufferInt, final int n) {
                return dataBufferInt.bankdata[n];
            }
            
            @Override
            public StateTrackableDelegate getTrackable(final DataBuffer dataBuffer) {
                return dataBuffer.theTrackable;
            }
            
            @Override
            public void setTrackable(final DataBuffer dataBuffer, final StateTrackableDelegate theTrackable) {
                dataBuffer.theTrackable = theTrackable;
            }
        });
    }
}
