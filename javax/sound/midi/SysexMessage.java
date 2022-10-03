package javax.sound.midi;

public class SysexMessage extends MidiMessage
{
    public static final int SYSTEM_EXCLUSIVE = 240;
    public static final int SPECIAL_SYSTEM_EXCLUSIVE = 247;
    
    public SysexMessage() {
        this(new byte[2]);
        this.data[0] = -16;
        this.data[1] = -9;
    }
    
    public SysexMessage(final byte[] array, final int n) throws InvalidMidiDataException {
        super(null);
        this.setMessage(array, n);
    }
    
    public SysexMessage(final int n, final byte[] array, final int n2) throws InvalidMidiDataException {
        super(null);
        this.setMessage(n, array, n2);
    }
    
    protected SysexMessage(final byte[] array) {
        super(array);
    }
    
    public void setMessage(final byte[] array, final int n) throws InvalidMidiDataException {
        final int n2 = array[0] & 0xFF;
        if (n2 != 240 && n2 != 247) {
            throw new InvalidMidiDataException("Invalid status byte for sysex message: 0x" + Integer.toHexString(n2));
        }
        super.setMessage(array, n);
    }
    
    public void setMessage(final int n, final byte[] array, final int n2) throws InvalidMidiDataException {
        if (n != 240 && n != 247) {
            throw new InvalidMidiDataException("Invalid status byte for sysex message: 0x" + Integer.toHexString(n));
        }
        if (n2 < 0 || n2 > array.length) {
            throw new IndexOutOfBoundsException("length out of bounds: " + n2);
        }
        this.length = n2 + 1;
        if (this.data == null || this.data.length < this.length) {
            this.data = new byte[this.length];
        }
        this.data[0] = (byte)(n & 0xFF);
        if (n2 > 0) {
            System.arraycopy(array, 0, this.data, 1, n2);
        }
    }
    
    public byte[] getData() {
        final byte[] array = new byte[this.length - 1];
        System.arraycopy(this.data, 1, array, 0, this.length - 1);
        return array;
    }
    
    @Override
    public Object clone() {
        final byte[] array = new byte[this.length];
        System.arraycopy(this.data, 0, array, 0, array.length);
        return new SysexMessage(array);
    }
}
