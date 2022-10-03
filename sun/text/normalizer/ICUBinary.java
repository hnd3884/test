package sun.text.normalizer;

import java.util.Arrays;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.InputStream;

public final class ICUBinary
{
    private static final byte MAGIC1 = -38;
    private static final byte MAGIC2 = 39;
    private static final byte BIG_ENDIAN_ = 1;
    private static final byte CHAR_SET_ = 0;
    private static final byte CHAR_SIZE_ = 2;
    private static final String MAGIC_NUMBER_AUTHENTICATION_FAILED_ = "ICU data file error: Not an ICU data file";
    private static final String HEADER_AUTHENTICATION_FAILED_ = "ICU data file error: Header authentication failed, please check if you have a valid ICU data file";
    
    public static final byte[] readHeader(final InputStream inputStream, final byte[] array, final Authenticate authenticate) throws IOException {
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        final char char1 = dataInputStream.readChar();
        char c = '\u0002';
        final byte byte1 = dataInputStream.readByte();
        ++c;
        final byte byte2 = dataInputStream.readByte();
        ++c;
        if (byte1 != -38 || byte2 != 39) {
            throw new IOException("ICU data file error: Not an ICU data file");
        }
        dataInputStream.readChar();
        c += '\u0002';
        dataInputStream.readChar();
        c += '\u0002';
        final byte byte3 = dataInputStream.readByte();
        ++c;
        final byte byte4 = dataInputStream.readByte();
        ++c;
        final byte byte5 = dataInputStream.readByte();
        ++c;
        dataInputStream.readByte();
        ++c;
        final byte[] array2 = new byte[4];
        dataInputStream.readFully(array2);
        c += '\u0004';
        final byte[] array3 = new byte[4];
        dataInputStream.readFully(array3);
        c += '\u0004';
        final byte[] array4 = new byte[4];
        dataInputStream.readFully(array4);
        c += '\u0004';
        if (char1 < c) {
            throw new IOException("Internal Error: Header size error");
        }
        dataInputStream.skipBytes(char1 - c);
        if (byte3 != 1 || byte4 != 0 || byte5 != 2 || !Arrays.equals(array, array2) || (authenticate != null && !authenticate.isDataVersionAcceptable(array3))) {
            throw new IOException("ICU data file error: Header authentication failed, please check if you have a valid ICU data file");
        }
        return array4;
    }
    
    public interface Authenticate
    {
        boolean isDataVersionAcceptable(final byte[] p0);
    }
}
