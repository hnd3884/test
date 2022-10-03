package sun.text.normalizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

final class NormalizerDataReader implements ICUBinary.Authenticate
{
    private DataInputStream dataInputStream;
    private byte[] unicodeVersion;
    private static final byte[] DATA_FORMAT_ID;
    private static final byte[] DATA_FORMAT_VERSION;
    
    protected NormalizerDataReader(final InputStream inputStream) throws IOException {
        this.unicodeVersion = ICUBinary.readHeader(inputStream, NormalizerDataReader.DATA_FORMAT_ID, this);
        this.dataInputStream = new DataInputStream(inputStream);
    }
    
    protected int[] readIndexes(final int n) throws IOException {
        final int[] array = new int[n];
        for (int i = 0; i < n; ++i) {
            array[i] = this.dataInputStream.readInt();
        }
        return array;
    }
    
    protected void read(final byte[] array, final byte[] array2, final byte[] array3, final char[] array4, final char[] array5) throws IOException {
        this.dataInputStream.readFully(array);
        for (int i = 0; i < array4.length; ++i) {
            array4[i] = this.dataInputStream.readChar();
        }
        for (int j = 0; j < array5.length; ++j) {
            array5[j] = this.dataInputStream.readChar();
        }
        this.dataInputStream.readFully(array2);
        this.dataInputStream.readFully(array3);
    }
    
    public byte[] getDataFormatVersion() {
        return NormalizerDataReader.DATA_FORMAT_VERSION;
    }
    
    @Override
    public boolean isDataVersionAcceptable(final byte[] array) {
        return array[0] == NormalizerDataReader.DATA_FORMAT_VERSION[0] && array[2] == NormalizerDataReader.DATA_FORMAT_VERSION[2] && array[3] == NormalizerDataReader.DATA_FORMAT_VERSION[3];
    }
    
    public byte[] getUnicodeVersion() {
        return this.unicodeVersion;
    }
    
    static {
        DATA_FORMAT_ID = new byte[] { 78, 111, 114, 109 };
        DATA_FORMAT_VERSION = new byte[] { 2, 2, 5, 2 };
    }
}
