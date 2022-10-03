package sun.net.idn;

import sun.text.normalizer.UCharacter;
import java.text.Normalizer;
import sun.text.normalizer.UTF16;
import java.text.ParseException;
import sun.text.normalizer.UCharacterIterator;
import java.io.IOException;
import sun.text.normalizer.NormalizerImpl;
import sun.text.normalizer.Trie;
import sun.text.normalizer.CharTrie;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import sun.text.normalizer.VersionInfo;

public final class StringPrep
{
    public static final int DEFAULT = 0;
    public static final int ALLOW_UNASSIGNED = 1;
    private static final int UNASSIGNED = 0;
    private static final int MAP = 1;
    private static final int PROHIBITED = 2;
    private static final int DELETE = 3;
    private static final int TYPE_LIMIT = 4;
    private static final int NORMALIZATION_ON = 1;
    private static final int CHECK_BIDI_ON = 2;
    private static final int TYPE_THRESHOLD = 65520;
    private static final int MAX_INDEX_VALUE = 16319;
    private static final int MAX_INDEX_TOP_LENGTH = 3;
    private static final int INDEX_TRIE_SIZE = 0;
    private static final int INDEX_MAPPING_DATA_SIZE = 1;
    private static final int NORM_CORRECTNS_LAST_UNI_VERSION = 2;
    private static final int ONE_UCHAR_MAPPING_INDEX_START = 3;
    private static final int TWO_UCHARS_MAPPING_INDEX_START = 4;
    private static final int THREE_UCHARS_MAPPING_INDEX_START = 5;
    private static final int FOUR_UCHARS_MAPPING_INDEX_START = 6;
    private static final int OPTIONS = 7;
    private static final int INDEX_TOP = 16;
    private static final int DATA_BUFFER_SIZE = 25000;
    private StringPrepTrieImpl sprepTrieImpl;
    private int[] indexes;
    private char[] mappingData;
    private byte[] formatVersion;
    private VersionInfo sprepUniVer;
    private VersionInfo normCorrVer;
    private boolean doNFKC;
    private boolean checkBiDi;
    
    private char getCodePointValue(final int n) {
        return this.sprepTrieImpl.sprepTrie.getCodePointValue(n);
    }
    
    private static VersionInfo getVersionInfo(final int n) {
        return VersionInfo.getInstance(n >> 24 & 0xFF, n >> 16 & 0xFF, n >> 8 & 0xFF, n & 0xFF);
    }
    
    private static VersionInfo getVersionInfo(final byte[] array) {
        if (array.length != 4) {
            return null;
        }
        return VersionInfo.getInstance(array[0], array[1], array[2], array[3]);
    }
    
    public StringPrep(final InputStream inputStream) throws IOException {
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 25000);
        final StringPrepDataReader stringPrepDataReader = new StringPrepDataReader(bufferedInputStream);
        this.indexes = stringPrepDataReader.readIndexes(16);
        final byte[] array = new byte[this.indexes[0]];
        stringPrepDataReader.read(array, this.mappingData = new char[this.indexes[1] / 2]);
        (this.sprepTrieImpl = new StringPrepTrieImpl()).sprepTrie = new CharTrie(new ByteArrayInputStream(array), this.sprepTrieImpl);
        this.formatVersion = stringPrepDataReader.getDataFormatVersion();
        this.doNFKC = ((this.indexes[7] & 0x1) > 0);
        this.checkBiDi = ((this.indexes[7] & 0x2) > 0);
        this.sprepUniVer = getVersionInfo(stringPrepDataReader.getUnicodeVersion());
        this.normCorrVer = getVersionInfo(this.indexes[2]);
        final VersionInfo unicodeVersion = NormalizerImpl.getUnicodeVersion();
        if (unicodeVersion.compareTo(this.sprepUniVer) < 0 && unicodeVersion.compareTo(this.normCorrVer) < 0 && (this.indexes[7] & 0x1) > 0) {
            throw new IOException("Normalization Correction version not supported");
        }
        bufferedInputStream.close();
    }
    
    private static final void getValues(final char c, final Values values) {
        values.reset();
        if (c == '\0') {
            values.type = 4;
        }
        else if (c >= '\ufff0') {
            values.type = c - '\ufff0';
        }
        else {
            values.type = 1;
            if ((c & '\u0002') > 0) {
                values.isIndex = true;
                values.value = c >> 2;
            }
            else {
                values.isIndex = false;
                values.value = c << 16 >> 16;
                values.value >>= 2;
            }
            if (c >> 2 == 16319) {
                values.type = 3;
                values.isIndex = false;
                values.value = 0;
            }
        }
    }
    
    private StringBuffer map(final UCharacterIterator uCharacterIterator, final int n) throws ParseException {
        final Values values = new Values();
        final StringBuffer sb = new StringBuffer();
        final boolean b = (n & 0x1) > 0;
        int nextCodePoint;
        while ((nextCodePoint = uCharacterIterator.nextCodePoint()) != -1) {
            getValues(this.getCodePointValue(nextCodePoint), values);
            if (values.type == 0 && !b) {
                throw new ParseException("An unassigned code point was found in the input " + uCharacterIterator.getText(), uCharacterIterator.getIndex());
            }
            if (values.type == 1) {
                if (values.isIndex) {
                    int value = values.value;
                    int n2;
                    if (value >= this.indexes[3] && value < this.indexes[4]) {
                        n2 = 1;
                    }
                    else if (value >= this.indexes[4] && value < this.indexes[5]) {
                        n2 = 2;
                    }
                    else if (value >= this.indexes[5] && value < this.indexes[6]) {
                        n2 = 3;
                    }
                    else {
                        n2 = this.mappingData[value++];
                    }
                    sb.append(this.mappingData, value, n2);
                    continue;
                }
                nextCodePoint -= values.value;
            }
            else if (values.type == 3) {
                continue;
            }
            UTF16.append(sb, nextCodePoint);
        }
        return sb;
    }
    
    private StringBuffer normalize(final StringBuffer sb) {
        return new StringBuffer(sun.text.Normalizer.normalize(sb.toString(), Normalizer.Form.NFKC, 262432));
    }
    
    public StringBuffer prepare(final UCharacterIterator uCharacterIterator, final int n) throws ParseException {
        StringBuffer sb2;
        final StringBuffer sb = sb2 = this.map(uCharacterIterator, n);
        if (this.doNFKC) {
            sb2 = this.normalize(sb);
        }
        final UCharacterIterator instance = UCharacterIterator.getInstance(sb2);
        final Values values = new Values();
        int direction = 19;
        int n2 = 19;
        int n3 = -1;
        int n4 = -1;
        boolean b = false;
        boolean b2 = false;
        int nextCodePoint;
        while ((nextCodePoint = instance.nextCodePoint()) != -1) {
            getValues(this.getCodePointValue(nextCodePoint), values);
            if (values.type == 2) {
                throw new ParseException("A prohibited code point was found in the input" + instance.getText(), values.value);
            }
            direction = UCharacter.getDirection(nextCodePoint);
            if (n2 == 19) {
                n2 = direction;
            }
            if (direction == 0) {
                b2 = true;
                n4 = instance.getIndex() - 1;
            }
            if (direction != 1 && direction != 13) {
                continue;
            }
            b = true;
            n3 = instance.getIndex() - 1;
        }
        if (this.checkBiDi) {
            if (b2 && b) {
                throw new ParseException("The input does not conform to the rules for BiDi code points." + instance.getText(), (n3 > n4) ? n3 : n4);
            }
            if (b && ((n2 != 1 && n2 != 13) || (direction != 1 && direction != 13))) {
                throw new ParseException("The input does not conform to the rules for BiDi code points." + instance.getText(), (n3 > n4) ? n3 : n4);
            }
        }
        return sb2;
    }
    
    private static final class StringPrepTrieImpl implements Trie.DataManipulate
    {
        private CharTrie sprepTrie;
        
        private StringPrepTrieImpl() {
            this.sprepTrie = null;
        }
        
        @Override
        public int getFoldingOffset(final int n) {
            return n;
        }
    }
    
    private static final class Values
    {
        boolean isIndex;
        int value;
        int type;
        
        public void reset() {
            this.isIndex = false;
            this.value = 0;
            this.type = -1;
        }
    }
}
