package javax.sound.midi;

public class ShortMessage extends MidiMessage
{
    public static final int MIDI_TIME_CODE = 241;
    public static final int SONG_POSITION_POINTER = 242;
    public static final int SONG_SELECT = 243;
    public static final int TUNE_REQUEST = 246;
    public static final int END_OF_EXCLUSIVE = 247;
    public static final int TIMING_CLOCK = 248;
    public static final int START = 250;
    public static final int CONTINUE = 251;
    public static final int STOP = 252;
    public static final int ACTIVE_SENSING = 254;
    public static final int SYSTEM_RESET = 255;
    public static final int NOTE_OFF = 128;
    public static final int NOTE_ON = 144;
    public static final int POLY_PRESSURE = 160;
    public static final int CONTROL_CHANGE = 176;
    public static final int PROGRAM_CHANGE = 192;
    public static final int CHANNEL_PRESSURE = 208;
    public static final int PITCH_BEND = 224;
    
    public ShortMessage() {
        this(new byte[3]);
        this.data[0] = -112;
        this.data[1] = 64;
        this.data[2] = 127;
        this.length = 3;
    }
    
    public ShortMessage(final int message) throws InvalidMidiDataException {
        super(null);
        this.setMessage(message);
    }
    
    public ShortMessage(final int n, final int n2, final int n3) throws InvalidMidiDataException {
        super(null);
        this.setMessage(n, n2, n3);
    }
    
    public ShortMessage(final int n, final int n2, final int n3, final int n4) throws InvalidMidiDataException {
        super(null);
        this.setMessage(n, n2, n3, n4);
    }
    
    protected ShortMessage(final byte[] array) {
        super(array);
    }
    
    public void setMessage(final int n) throws InvalidMidiDataException {
        final int dataLength = this.getDataLength(n);
        if (dataLength != 0) {
            throw new InvalidMidiDataException("Status byte; " + n + " requires " + dataLength + " data bytes");
        }
        this.setMessage(n, 0, 0);
    }
    
    public void setMessage(final int n, final int n2, final int n3) throws InvalidMidiDataException {
        final int dataLength = this.getDataLength(n);
        if (dataLength > 0) {
            if (n2 < 0 || n2 > 127) {
                throw new InvalidMidiDataException("data1 out of range: " + n2);
            }
            if (dataLength > 1 && (n3 < 0 || n3 > 127)) {
                throw new InvalidMidiDataException("data2 out of range: " + n3);
            }
        }
        this.length = dataLength + 1;
        if (this.data == null || this.data.length < this.length) {
            this.data = new byte[3];
        }
        this.data[0] = (byte)(n & 0xFF);
        if (this.length > 1) {
            this.data[1] = (byte)(n2 & 0xFF);
            if (this.length > 2) {
                this.data[2] = (byte)(n3 & 0xFF);
            }
        }
    }
    
    public void setMessage(final int n, final int n2, final int n3, final int n4) throws InvalidMidiDataException {
        if (n >= 240 || n < 128) {
            throw new InvalidMidiDataException("command out of range: 0x" + Integer.toHexString(n));
        }
        if ((n2 & 0xFFFFFFF0) != 0x0) {
            throw new InvalidMidiDataException("channel out of range: " + n2);
        }
        this.setMessage((n & 0xF0) | (n2 & 0xF), n3, n4);
    }
    
    public int getChannel() {
        return this.getStatus() & 0xF;
    }
    
    public int getCommand() {
        return this.getStatus() & 0xF0;
    }
    
    public int getData1() {
        if (this.length > 1) {
            return this.data[1] & 0xFF;
        }
        return 0;
    }
    
    public int getData2() {
        if (this.length > 2) {
            return this.data[2] & 0xFF;
        }
        return 0;
    }
    
    @Override
    public Object clone() {
        final byte[] array = new byte[this.length];
        System.arraycopy(this.data, 0, array, 0, array.length);
        return new ShortMessage(array);
    }
    
    protected final int getDataLength(final int n) throws InvalidMidiDataException {
        switch (n) {
            case 246:
            case 247:
            case 248:
            case 249:
            case 250:
            case 251:
            case 252:
            case 253:
            case 254:
            case 255: {
                return 0;
            }
            case 241:
            case 243: {
                return 1;
            }
            case 242: {
                return 2;
            }
            default: {
                switch (n & 0xF0) {
                    case 128:
                    case 144:
                    case 160:
                    case 176:
                    case 224: {
                        return 2;
                    }
                    case 192:
                    case 208: {
                        return 1;
                    }
                    default: {
                        throw new InvalidMidiDataException("Invalid status byte: " + n);
                    }
                }
                break;
            }
        }
    }
}
