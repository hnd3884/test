package sun.misc;

import java.io.IOException;
import java.io.OutputStream;

public class UCEncoder extends CharacterEncoder
{
    private static final byte[] map_array;
    private int sequence;
    private byte[] tmp;
    private CRC16 crc;
    
    public UCEncoder() {
        this.tmp = new byte[2];
        this.crc = new CRC16();
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
    protected void encodeAtom(final OutputStream outputStream, final byte[] array, final int n, final int n2) throws IOException {
        final byte b = array[n];
        byte b2;
        if (n2 == 2) {
            b2 = array[n + 1];
        }
        else {
            b2 = 0;
        }
        this.crc.update(b);
        if (n2 == 2) {
            this.crc.update(b2);
        }
        outputStream.write(UCEncoder.map_array[(b >>> 2 & 0x38) + (b2 >>> 5 & 0x7)]);
        int n3 = 0;
        int n4 = 0;
        for (int i = 1; i < 256; i *= 2) {
            if ((b & i) != 0x0) {
                ++n3;
            }
            if ((b2 & i) != 0x0) {
                ++n4;
            }
        }
        final int n5 = (n3 & 0x1) * 32;
        final int n6 = (n4 & 0x1) * 32;
        outputStream.write(UCEncoder.map_array[(b & 0x1F) + n5]);
        outputStream.write(UCEncoder.map_array[(b2 & 0x1F) + n6]);
    }
    
    @Override
    protected void encodeLinePrefix(final OutputStream outputStream, final int n) throws IOException {
        outputStream.write(42);
        this.crc.value = 0;
        this.tmp[0] = (byte)n;
        this.tmp[1] = (byte)this.sequence;
        this.sequence = (this.sequence + 1 & 0xFF);
        this.encodeAtom(outputStream, this.tmp, 0, 2);
    }
    
    @Override
    protected void encodeLineSuffix(final OutputStream outputStream) throws IOException {
        this.tmp[0] = (byte)(this.crc.value >>> 8 & 0xFF);
        this.tmp[1] = (byte)(this.crc.value & 0xFF);
        this.encodeAtom(outputStream, this.tmp, 0, 2);
        super.pStream.println();
    }
    
    @Override
    protected void encodeBufferPrefix(final OutputStream outputStream) throws IOException {
        this.sequence = 0;
        super.encodeBufferPrefix(outputStream);
    }
    
    static {
        map_array = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 40, 41 };
    }
}
