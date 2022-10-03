package sun.misc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;

public class BASE64Decoder extends CharacterDecoder
{
    private static final char[] pem_array;
    private static final byte[] pem_convert_array;
    byte[] decode_buffer;
    
    public BASE64Decoder() {
        this.decode_buffer = new byte[4];
    }
    
    @Override
    protected int bytesPerAtom() {
        return 4;
    }
    
    @Override
    protected int bytesPerLine() {
        return 72;
    }
    
    @Override
    protected void decodeAtom(final PushbackInputStream pushbackInputStream, final OutputStream outputStream, int n) throws IOException {
        int n2 = -1;
        int n3 = -1;
        int n4 = -1;
        int n5 = -1;
        if (n < 2) {
            throw new CEFormatException("BASE64Decoder: Not enough bytes for an atom.");
        }
        int read;
        do {
            read = pushbackInputStream.read();
            if (read == -1) {
                throw new CEStreamExhausted();
            }
        } while (read == 10 || read == 13);
        this.decode_buffer[0] = (byte)read;
        if (this.readFully(pushbackInputStream, this.decode_buffer, 1, n - 1) == -1) {
            throw new CEStreamExhausted();
        }
        if (n > 3 && this.decode_buffer[3] == 61) {
            n = 3;
        }
        if (n > 2 && this.decode_buffer[2] == 61) {
            n = 2;
        }
        switch (n) {
            case 4: {
                n5 = BASE64Decoder.pem_convert_array[this.decode_buffer[3] & 0xFF];
            }
            case 3: {
                n4 = BASE64Decoder.pem_convert_array[this.decode_buffer[2] & 0xFF];
            }
            case 2: {
                n3 = BASE64Decoder.pem_convert_array[this.decode_buffer[1] & 0xFF];
                n2 = BASE64Decoder.pem_convert_array[this.decode_buffer[0] & 0xFF];
                break;
            }
        }
        switch (n) {
            case 2: {
                outputStream.write((byte)((n2 << 2 & 0xFC) | (n3 >>> 4 & 0x3)));
                break;
            }
            case 3: {
                outputStream.write((byte)((n2 << 2 & 0xFC) | (n3 >>> 4 & 0x3)));
                outputStream.write((byte)((n3 << 4 & 0xF0) | (n4 >>> 2 & 0xF)));
                break;
            }
            case 4: {
                outputStream.write((byte)((n2 << 2 & 0xFC) | (n3 >>> 4 & 0x3)));
                outputStream.write((byte)((n3 << 4 & 0xF0) | (n4 >>> 2 & 0xF)));
                outputStream.write((byte)((n4 << 6 & 0xC0) | (n5 & 0x3F)));
                break;
            }
        }
    }
    
    static {
        pem_array = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
        pem_convert_array = new byte[256];
        for (int i = 0; i < 255; ++i) {
            BASE64Decoder.pem_convert_array[i] = -1;
        }
        for (int j = 0; j < BASE64Decoder.pem_array.length; ++j) {
            BASE64Decoder.pem_convert_array[BASE64Decoder.pem_array[j]] = (byte)j;
        }
    }
}
