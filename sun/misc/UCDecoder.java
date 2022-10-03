package sun.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.ByteArrayOutputStream;

public class UCDecoder extends CharacterDecoder
{
    private static final byte[] map_array;
    private int sequence;
    private byte[] tmp;
    private CRC16 crc;
    private ByteArrayOutputStream lineAndSeq;
    
    public UCDecoder() {
        this.tmp = new byte[2];
        this.crc = new CRC16();
        this.lineAndSeq = new ByteArrayOutputStream(2);
    }
    
    @Override
    protected int bytesPerAtom() {
        return 2;
    }
    
    @Override
    protected int bytesPerLine() {
        return 48;
    }
    
    @Override
    protected void decodeAtom(final PushbackInputStream pushbackInputStream, final OutputStream outputStream, final int n) throws IOException {
        int n2 = -1;
        int n3 = -1;
        int n4 = -1;
        final byte[] array = new byte[3];
        if (pushbackInputStream.read(array) != 3) {
            throw new CEStreamExhausted();
        }
        for (int n5 = 0; n5 < 64 && (n2 == -1 || n3 == -1 || n4 == -1); ++n5) {
            if (array[0] == UCDecoder.map_array[n5]) {
                n2 = (byte)n5;
            }
            if (array[1] == UCDecoder.map_array[n5]) {
                n3 = (byte)n5;
            }
            if (array[2] == UCDecoder.map_array[n5]) {
                n4 = (byte)n5;
            }
        }
        final byte b = (byte)(((n2 & 0x38) << 2) + (n3 & 0x1F));
        final byte b2 = (byte)(((n2 & 0x7) << 5) + (n4 & 0x1F));
        int n6 = 0;
        int n7 = 0;
        for (int i = 1; i < 256; i *= 2) {
            if ((b & i) != 0x0) {
                ++n6;
            }
            if ((b2 & i) != 0x0) {
                ++n7;
            }
        }
        final int n8 = (n3 & 0x20) / 32;
        final int n9 = (n4 & 0x20) / 32;
        if ((n6 & 0x1) != n8) {
            throw new CEFormatException("UCDecoder: High byte parity error.");
        }
        if ((n7 & 0x1) != n9) {
            throw new CEFormatException("UCDecoder: Low byte parity error.");
        }
        outputStream.write(b);
        this.crc.update(b);
        if (n == 2) {
            outputStream.write(b2);
            this.crc.update(b2);
        }
    }
    
    @Override
    protected void decodeBufferPrefix(final PushbackInputStream pushbackInputStream, final OutputStream outputStream) {
        this.sequence = 0;
    }
    
    @Override
    protected int decodeLinePrefix(final PushbackInputStream pushbackInputStream, final OutputStream outputStream) throws IOException {
        this.crc.value = 0;
        while (pushbackInputStream.read(this.tmp, 0, 1) != -1) {
            if (this.tmp[0] == 42) {
                this.lineAndSeq.reset();
                this.decodeAtom(pushbackInputStream, this.lineAndSeq, 2);
                final byte[] byteArray = this.lineAndSeq.toByteArray();
                final int n = byteArray[0] & 0xFF;
                if ((byteArray[1] & 0xFF) != this.sequence) {
                    throw new CEFormatException("UCDecoder: Out of sequence line.");
                }
                this.sequence = (this.sequence + 1 & 0xFF);
                return n;
            }
        }
        throw new CEStreamExhausted();
    }
    
    @Override
    protected void decodeLineSuffix(final PushbackInputStream pushbackInputStream, final OutputStream outputStream) throws IOException {
        final int value = this.crc.value;
        this.lineAndSeq.reset();
        this.decodeAtom(pushbackInputStream, this.lineAndSeq, 2);
        final byte[] byteArray = this.lineAndSeq.toByteArray();
        if ((byteArray[0] << 8 & 0xFF00) + (byteArray[1] & 0xFF) != value) {
            throw new CEFormatException("UCDecoder: CRC check failed.");
        }
    }
    
    static {
        map_array = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 40, 41 };
    }
}
