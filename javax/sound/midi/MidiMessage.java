package javax.sound.midi;

public abstract class MidiMessage implements Cloneable
{
    protected byte[] data;
    protected int length;
    
    protected MidiMessage(final byte[] data) {
        this.length = 0;
        this.data = data;
        if (data != null) {
            this.length = data.length;
        }
    }
    
    protected void setMessage(final byte[] array, final int length) throws InvalidMidiDataException {
        if (length < 0 || (length > 0 && length > array.length)) {
            throw new IndexOutOfBoundsException("length out of bounds: " + length);
        }
        this.length = length;
        if (this.data == null || this.data.length < this.length) {
            this.data = new byte[this.length];
        }
        System.arraycopy(array, 0, this.data, 0, length);
    }
    
    public byte[] getMessage() {
        final byte[] array = new byte[this.length];
        System.arraycopy(this.data, 0, array, 0, this.length);
        return array;
    }
    
    public int getStatus() {
        if (this.length > 0) {
            return this.data[0] & 0xFF;
        }
        return 0;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public abstract Object clone();
}
