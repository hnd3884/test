package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;

public final class UBiDiProps
{
    private static UBiDiProps gBdp;
    private static UBiDiProps gBdpDummy;
    private int[] indexes;
    private int[] mirrors;
    private byte[] jgArray;
    private CharTrie trie;
    private static final String DATA_FILE_NAME = "/sun/text/resources/ubidi.icu";
    private static final byte[] FMT;
    private static final int IX_INDEX_TOP = 0;
    private static final int IX_MIRROR_LENGTH = 3;
    private static final int IX_JG_START = 4;
    private static final int IX_JG_LIMIT = 5;
    private static final int IX_TOP = 16;
    private static final int CLASS_MASK = 31;
    
    public UBiDiProps() throws IOException {
        final InputStream stream = ICUData.getStream("/sun/text/resources/ubidi.icu");
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(stream, 4096);
        this.readData(bufferedInputStream);
        bufferedInputStream.close();
        stream.close();
    }
    
    private void readData(final InputStream inputStream) throws IOException {
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        ICUBinary.readHeader(dataInputStream, UBiDiProps.FMT, new IsAcceptable());
        final int int1 = dataInputStream.readInt();
        if (int1 < 0) {
            throw new IOException("indexes[0] too small in /sun/text/resources/ubidi.icu");
        }
        (this.indexes = new int[int1])[0] = int1;
        for (int i = 1; i < int1; ++i) {
            this.indexes[i] = dataInputStream.readInt();
        }
        this.trie = new CharTrie(dataInputStream, null);
        final int n = this.indexes[3];
        if (n > 0) {
            this.mirrors = new int[n];
            for (int j = 0; j < n; ++j) {
                this.mirrors[j] = dataInputStream.readInt();
            }
        }
        final int n2 = this.indexes[5] - this.indexes[4];
        this.jgArray = new byte[n2];
        for (int k = 0; k < n2; ++k) {
            this.jgArray[k] = dataInputStream.readByte();
        }
    }
    
    public static final synchronized UBiDiProps getSingleton() throws IOException {
        if (UBiDiProps.gBdp == null) {
            UBiDiProps.gBdp = new UBiDiProps();
        }
        return UBiDiProps.gBdp;
    }
    
    private UBiDiProps(final boolean b) {
        (this.indexes = new int[16])[0] = 16;
        this.trie = new CharTrie(0, 0, null);
    }
    
    public static final synchronized UBiDiProps getDummy() {
        if (UBiDiProps.gBdpDummy == null) {
            UBiDiProps.gBdpDummy = new UBiDiProps(true);
        }
        return UBiDiProps.gBdpDummy;
    }
    
    public final int getClass(final int n) {
        return getClassFromProps(this.trie.getCodePointValue(n));
    }
    
    private static final int getClassFromProps(final int n) {
        return n & 0x1F;
    }
    
    static {
        UBiDiProps.gBdp = null;
        UBiDiProps.gBdpDummy = null;
        FMT = new byte[] { 66, 105, 68, 105 };
    }
    
    private final class IsAcceptable implements ICUBinary.Authenticate
    {
        @Override
        public boolean isDataVersionAcceptable(final byte[] array) {
            return array[0] == 1 && array[2] == 5 && array[3] == 2;
        }
    }
}
