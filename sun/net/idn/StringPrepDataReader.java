package sun.net.idn;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import sun.text.normalizer.ICUBinary;

final class StringPrepDataReader implements ICUBinary.Authenticate
{
    private DataInputStream dataInputStream;
    private byte[] unicodeVersion;
    private static final byte[] DATA_FORMAT_ID;
    private static final byte[] DATA_FORMAT_VERSION;
    
    public StringPrepDataReader(final InputStream inputStream) throws IOException {
        this.unicodeVersion = ICUBinary.readHeader(inputStream, StringPrepDataReader.DATA_FORMAT_ID, this);
        this.dataInputStream = new DataInputStream(inputStream);
    }
    
    public void read(final byte[] array, final char[] array2) throws IOException {
        this.dataInputStream.read(array);
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = this.dataInputStream.readChar();
        }
    }
    
    public byte[] getDataFormatVersion() {
        return StringPrepDataReader.DATA_FORMAT_VERSION;
    }
    
    @Override
    public boolean isDataVersionAcceptable(final byte[] array) {
        return array[0] == StringPrepDataReader.DATA_FORMAT_VERSION[0] && array[2] == StringPrepDataReader.DATA_FORMAT_VERSION[2] && array[3] == StringPrepDataReader.DATA_FORMAT_VERSION[3];
    }
    
    public int[] readIndexes(final int n) throws IOException {
        final int[] array = new int[n];
        for (int i = 0; i < n; ++i) {
            array[i] = this.dataInputStream.readInt();
        }
        return array;
    }
    
    public byte[] getUnicodeVersion() {
        return this.unicodeVersion;
    }
    
    static {
        DATA_FORMAT_ID = new byte[] { 83, 80, 82, 80 };
        DATA_FORMAT_VERSION = new byte[] { 3, 2, 5, 2 };
    }
}
