package sun.nio.cs;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

public class CharsetMapping
{
    public static final char UNMAPPABLE_DECODING = '\ufffd';
    public static final int UNMAPPABLE_ENCODING = 65533;
    char[] b2cSB;
    char[] b2cDB1;
    char[] b2cDB2;
    int b2Min;
    int b2Max;
    int b1MinDB1;
    int b1MaxDB1;
    int b1MinDB2;
    int b1MaxDB2;
    int dbSegSize;
    char[] c2b;
    char[] c2bIndex;
    char[] b2cSupp;
    char[] c2bSupp;
    Entry[] b2cComp;
    Entry[] c2bComp;
    static Comparator<Entry> comparatorBytes;
    static Comparator<Entry> comparatorCP;
    static Comparator<Entry> comparatorComp;
    private static final int MAP_SINGLEBYTE = 1;
    private static final int MAP_DOUBLEBYTE1 = 2;
    private static final int MAP_DOUBLEBYTE2 = 3;
    private static final int MAP_SUPPLEMENT = 5;
    private static final int MAP_SUPPLEMENT_C2B = 6;
    private static final int MAP_COMPOSITE = 7;
    private static final int MAP_INDEXC2B = 8;
    int off;
    byte[] bb;
    
    public CharsetMapping() {
        this.off = 0;
    }
    
    public char decodeSingle(final int n) {
        return this.b2cSB[n];
    }
    
    public char decodeDouble(int n, int n2) {
        if (n2 >= this.b2Min && n2 < this.b2Max) {
            n2 -= this.b2Min;
            if (n >= this.b1MinDB1 && n <= this.b1MaxDB1) {
                n -= this.b1MinDB1;
                return this.b2cDB1[n * this.dbSegSize + n2];
            }
            if (n >= this.b1MinDB2 && n <= this.b1MaxDB2) {
                n -= this.b1MinDB2;
                return this.b2cDB2[n * this.dbSegSize + n2];
            }
        }
        return '\ufffd';
    }
    
    public char[] decodeSurrogate(final int n, final char[] array) {
        final int n2 = this.b2cSupp.length / 2;
        final int binarySearch = Arrays.binarySearch(this.b2cSupp, 0, n2, (char)n);
        if (binarySearch >= 0) {
            Character.toChars(this.b2cSupp[n2 + binarySearch] + 131072, array, 0);
            return array;
        }
        return null;
    }
    
    public char[] decodeComposite(final Entry entry, final char[] array) {
        final int bytes = findBytes(this.b2cComp, entry);
        if (bytes >= 0) {
            array[0] = (char)this.b2cComp[bytes].cp;
            array[1] = (char)this.b2cComp[bytes].cp2;
            return array;
        }
        return null;
    }
    
    public int encodeChar(final char c) {
        final char c2 = this.c2bIndex[c >> 8];
        if (c2 == '\uffff') {
            return 65533;
        }
        return this.c2b[c2 + (c & '\u00ff')];
    }
    
    public int encodeSurrogate(final char c, final char c2) {
        final int codePoint = Character.toCodePoint(c, c2);
        if (codePoint < 131072 || codePoint >= 196608) {
            return 65533;
        }
        final int n = this.c2bSupp.length / 2;
        final int binarySearch = Arrays.binarySearch(this.c2bSupp, 0, n, (char)codePoint);
        if (binarySearch >= 0) {
            return this.c2bSupp[n + binarySearch];
        }
        return 65533;
    }
    
    public boolean isCompositeBase(final Entry entry) {
        return entry.cp <= 12791 && entry.cp >= 230 && findCP(this.c2bComp, entry) >= 0;
    }
    
    public int encodeComposite(final Entry entry) {
        final int comp = findComp(this.c2bComp, entry);
        if (comp >= 0) {
            return this.c2bComp[comp].bs;
        }
        return 65533;
    }
    
    public static CharsetMapping get(final InputStream inputStream) {
        return AccessController.doPrivileged((PrivilegedAction<CharsetMapping>)new PrivilegedAction<CharsetMapping>() {
            @Override
            public CharsetMapping run() {
                return new CharsetMapping().load(inputStream);
            }
        });
    }
    
    static int findBytes(final Entry[] array, final Entry entry) {
        return Arrays.binarySearch(array, 0, array.length, entry, CharsetMapping.comparatorBytes);
    }
    
    static int findCP(final Entry[] array, final Entry entry) {
        return Arrays.binarySearch(array, 0, array.length, entry, CharsetMapping.comparatorCP);
    }
    
    static int findComp(final Entry[] array, final Entry entry) {
        return Arrays.binarySearch(array, 0, array.length, entry, CharsetMapping.comparatorComp);
    }
    
    private static final boolean readNBytes(final InputStream inputStream, final byte[] array, int i) throws IOException {
        int read;
        for (int n = 0; i > 0; i -= read, n += read) {
            read = inputStream.read(array, n, i);
            if (read == -1) {
                return false;
            }
        }
        return true;
    }
    
    private char[] readCharArray() {
        final int n = (this.bb[this.off++] & 0xFF) << 8 | (this.bb[this.off++] & 0xFF);
        final char[] array = new char[n];
        for (int i = 0; i < n; ++i) {
            array[i] = (char)((this.bb[this.off++] & 0xFF) << 8 | (this.bb[this.off++] & 0xFF));
        }
        return array;
    }
    
    void readSINGLEBYTE() {
        final char[] charArray = this.readCharArray();
        for (int i = 0; i < charArray.length; ++i) {
            final char c = charArray[i];
            if (c != '\ufffd') {
                this.c2b[this.c2bIndex[c >> 8] + (c & '\u00ff')] = (char)i;
            }
        }
        this.b2cSB = charArray;
    }
    
    void readINDEXC2B() {
        final char[] charArray = this.readCharArray();
        for (int i = charArray.length - 1; i >= 0; --i) {
            if (this.c2b == null && charArray[i] != -1) {
                Arrays.fill(this.c2b = new char[charArray[i] + '\u0100'], '\ufffd');
                break;
            }
        }
        this.c2bIndex = charArray;
    }
    
    char[] readDB(final int n, final int n2, final int n3) {
        final char[] charArray = this.readCharArray();
        for (int i = 0; i < charArray.length; ++i) {
            final char c = charArray[i];
            if (c != '\ufffd') {
                this.c2b[this.c2bIndex[c >> 8] + (c & '\u00ff')] = (char)((i / n3 + n) * 256 + (i % n3 + n2));
            }
        }
        return charArray;
    }
    
    void readDOUBLEBYTE1() {
        this.b1MinDB1 = ((this.bb[this.off++] & 0xFF) << 8 | (this.bb[this.off++] & 0xFF));
        this.b1MaxDB1 = ((this.bb[this.off++] & 0xFF) << 8 | (this.bb[this.off++] & 0xFF));
        this.b2Min = ((this.bb[this.off++] & 0xFF) << 8 | (this.bb[this.off++] & 0xFF));
        this.b2Max = ((this.bb[this.off++] & 0xFF) << 8 | (this.bb[this.off++] & 0xFF));
        this.dbSegSize = this.b2Max - this.b2Min + 1;
        this.b2cDB1 = this.readDB(this.b1MinDB1, this.b2Min, this.dbSegSize);
    }
    
    void readDOUBLEBYTE2() {
        this.b1MinDB2 = ((this.bb[this.off++] & 0xFF) << 8 | (this.bb[this.off++] & 0xFF));
        this.b1MaxDB2 = ((this.bb[this.off++] & 0xFF) << 8 | (this.bb[this.off++] & 0xFF));
        this.b2Min = ((this.bb[this.off++] & 0xFF) << 8 | (this.bb[this.off++] & 0xFF));
        this.b2Max = ((this.bb[this.off++] & 0xFF) << 8 | (this.bb[this.off++] & 0xFF));
        this.dbSegSize = this.b2Max - this.b2Min + 1;
        this.b2cDB2 = this.readDB(this.b1MinDB2, this.b2Min, this.dbSegSize);
    }
    
    void readCOMPOSITE() {
        final char[] charArray = this.readCharArray();
        final int n = charArray.length / 3;
        this.b2cComp = new Entry[n];
        this.c2bComp = new Entry[n];
        int i = 0;
        int n2 = 0;
        while (i < n) {
            final Entry entry = new Entry();
            entry.bs = charArray[n2++];
            entry.cp = charArray[n2++];
            entry.cp2 = charArray[n2++];
            this.b2cComp[i] = entry;
            this.c2bComp[i] = entry;
            ++i;
        }
        Arrays.sort(this.c2bComp, 0, this.c2bComp.length, CharsetMapping.comparatorComp);
    }
    
    CharsetMapping load(final InputStream inputStream) {
        try {
            final int n = (inputStream.read() & 0xFF) << 24 | (inputStream.read() & 0xFF) << 16 | (inputStream.read() & 0xFF) << 8 | (inputStream.read() & 0xFF);
            this.bb = new byte[n];
            this.off = 0;
            if (!readNBytes(inputStream, this.bb, n)) {
                throw new RuntimeException("Corrupted data file");
            }
            inputStream.close();
            while (this.off < n) {
                switch ((this.bb[this.off++] & 0xFF) << 8 | (this.bb[this.off++] & 0xFF)) {
                    case 8: {
                        this.readINDEXC2B();
                        continue;
                    }
                    case 1: {
                        this.readSINGLEBYTE();
                        continue;
                    }
                    case 2: {
                        this.readDOUBLEBYTE1();
                        continue;
                    }
                    case 3: {
                        this.readDOUBLEBYTE2();
                        continue;
                    }
                    case 5: {
                        this.b2cSupp = this.readCharArray();
                        continue;
                    }
                    case 6: {
                        this.c2bSupp = this.readCharArray();
                        continue;
                    }
                    case 7: {
                        this.readCOMPOSITE();
                        continue;
                    }
                    default: {
                        throw new RuntimeException("Corrupted data file");
                    }
                }
            }
            this.bb = null;
            return this;
        }
        catch (final IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    static {
        CharsetMapping.comparatorBytes = new Comparator<Entry>() {
            @Override
            public int compare(final Entry entry, final Entry entry2) {
                return entry.bs - entry2.bs;
            }
            
            @Override
            public boolean equals(final Object o) {
                return this == o;
            }
        };
        CharsetMapping.comparatorCP = new Comparator<Entry>() {
            @Override
            public int compare(final Entry entry, final Entry entry2) {
                return entry.cp - entry2.cp;
            }
            
            @Override
            public boolean equals(final Object o) {
                return this == o;
            }
        };
        CharsetMapping.comparatorComp = new Comparator<Entry>() {
            @Override
            public int compare(final Entry entry, final Entry entry2) {
                int n = entry.cp - entry2.cp;
                if (n == 0) {
                    n = entry.cp2 - entry2.cp2;
                }
                return n;
            }
            
            @Override
            public boolean equals(final Object o) {
                return this == o;
            }
        };
    }
    
    public static class Entry
    {
        public int bs;
        public int cp;
        public int cp2;
    }
}
