package javax.sound.midi;

public class MetaMessage extends MidiMessage
{
    public static final int META = 255;
    private int dataLength;
    private static final long mask = 127L;
    
    public MetaMessage() {
        this(new byte[] { -1, 0 });
    }
    
    public MetaMessage(final int n, final byte[] array, final int n2) throws InvalidMidiDataException {
        super(null);
        this.dataLength = 0;
        this.setMessage(n, array, n2);
    }
    
    protected MetaMessage(final byte[] array) {
        super(array);
        this.dataLength = 0;
        if (array.length >= 3) {
            this.dataLength = array.length - 3;
            for (int n = 2; n < array.length && (array[n] & 0x80) != 0x0; ++n) {
                --this.dataLength;
            }
        }
    }
    
    public void setMessage(final int n, final byte[] array, final int dataLength) throws InvalidMidiDataException {
        if (n >= 128 || n < 0) {
            throw new InvalidMidiDataException("Invalid meta event with type " + n);
        }
        if ((dataLength > 0 && dataLength > array.length) || dataLength < 0) {
            throw new InvalidMidiDataException("length out of bounds: " + dataLength);
        }
        this.length = 2 + this.getVarIntLength(dataLength) + dataLength;
        this.dataLength = dataLength;
        (this.data = new byte[this.length])[0] = -1;
        this.data[1] = (byte)n;
        this.writeVarInt(this.data, 2, dataLength);
        if (dataLength > 0) {
            System.arraycopy(array, 0, this.data, this.length - this.dataLength, this.dataLength);
        }
    }
    
    public int getType() {
        if (this.length >= 2) {
            return this.data[1] & 0xFF;
        }
        return 0;
    }
    
    public byte[] getData() {
        final byte[] array = new byte[this.dataLength];
        System.arraycopy(this.data, this.length - this.dataLength, array, 0, this.dataLength);
        return array;
    }
    
    @Override
    public Object clone() {
        final byte[] array = new byte[this.length];
        System.arraycopy(this.data, 0, array, 0, array.length);
        return new MetaMessage(array);
    }
    
    private int getVarIntLength(long n) {
        int n2 = 0;
        do {
            n >>= 7;
            ++n2;
        } while (n > 0L);
        return n2;
    }
    
    private void writeVarInt(final byte[] array, int n, final long n2) {
        int i;
        for (i = 63; i > 0 && (n2 & 127L << i) == 0x0L; i -= 7) {}
        while (i > 0) {
            array[n++] = (byte)((n2 & 127L << i) >> i | 0x80L);
            i -= 7;
        }
        array[n] = (byte)(n2 & 0x7FL);
    }
}
