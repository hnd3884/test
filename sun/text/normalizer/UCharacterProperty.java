package sun.text.normalizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.util.MissingResourceException;

public final class UCharacterProperty
{
    public CharTrie m_trie_;
    public char[] m_trieIndex_;
    public char[] m_trieData_;
    public int m_trieInitialValue_;
    public VersionInfo m_unicodeVersion_;
    public static final int SRC_PROPSVEC = 2;
    public static final int SRC_COUNT = 9;
    CharTrie m_additionalTrie_;
    int[] m_additionalVectors_;
    int m_additionalColumnsCount_;
    int m_maxBlockScriptValue_;
    int m_maxJTGValue_;
    private static UCharacterProperty INSTANCE_;
    private static final String DATA_FILE_NAME_ = "/sun/text/resources/uprops.icu";
    private static final int DATA_BUFFER_SIZE_ = 25000;
    private static final int VALUE_SHIFT_ = 8;
    private static final int UNSIGNED_VALUE_MASK_AFTER_SHIFT_ = 255;
    private static final int LEAD_SURROGATE_SHIFT_ = 10;
    private static final int SURROGATE_OFFSET_ = -56613888;
    private static final int FIRST_NIBBLE_SHIFT_ = 4;
    private static final int LAST_NIBBLE_MASK_ = 15;
    private static final int AGE_SHIFT_ = 24;
    
    public void setIndexData(final CharTrie.FriendAgent friendAgent) {
        this.m_trieIndex_ = friendAgent.getPrivateIndex();
        this.m_trieData_ = friendAgent.getPrivateData();
        this.m_trieInitialValue_ = friendAgent.getPrivateInitialValue();
    }
    
    public final int getProperty(final int n) {
        Label_0045: {
            if (n >= 55296) {
                if (n <= 56319 || n >= 65536) {
                    break Label_0045;
                }
            }
            try {
                return this.m_trieData_[(this.m_trieIndex_[n >> 5] << 2) + (n & 0x1F)];
            }
            catch (final ArrayIndexOutOfBoundsException ex) {
                return this.m_trieInitialValue_;
            }
        }
        if (n <= 56319) {
            return this.m_trieData_[(this.m_trieIndex_[320 + (n >> 5)] << 2) + (n & 0x1F)];
        }
        if (n <= 1114111) {
            return this.m_trie_.getSurrogateValue(UTF16.getLeadSurrogate(n), (char)(n & 0x3FF));
        }
        return this.m_trieInitialValue_;
    }
    
    public static int getUnsignedValue(final int n) {
        return n >> 8 & 0xFF;
    }
    
    public int getAdditional(final int n, final int n2) {
        if (n2 == -1) {
            return this.getProperty(n);
        }
        if (n2 < 0 || n2 >= this.m_additionalColumnsCount_) {
            return 0;
        }
        return this.m_additionalVectors_[this.m_additionalTrie_.getCodePointValue(n) + n2];
    }
    
    public VersionInfo getAge(final int n) {
        final int n2 = this.getAdditional(n, 0) >> 24;
        return VersionInfo.getInstance(n2 >> 4 & 0xF, n2 & 0xF, 0, 0);
    }
    
    public static int getRawSupplementary(final char c, final char c2) {
        return (c << 10) + c2 - 56613888;
    }
    
    public static UCharacterProperty getInstance() {
        if (UCharacterProperty.INSTANCE_ == null) {
            try {
                UCharacterProperty.INSTANCE_ = new UCharacterProperty();
            }
            catch (final Exception ex) {
                throw new MissingResourceException(ex.getMessage(), "", "");
            }
        }
        return UCharacterProperty.INSTANCE_;
    }
    
    public static boolean isRuleWhiteSpace(final int n) {
        return n >= 9 && n <= 8233 && (n <= 13 || n == 32 || n == 133 || n == 8206 || n == 8207 || n >= 8232);
    }
    
    private UCharacterProperty() throws IOException {
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(ICUData.getRequiredStream("/sun/text/resources/uprops.icu"), 25000);
        new UCharacterPropertyReader(bufferedInputStream).read(this);
        bufferedInputStream.close();
        this.m_trie_.putIndexData(this);
    }
    
    public void upropsvec_addPropertyStarts(final UnicodeSet set) {
        if (this.m_additionalColumnsCount_ > 0) {
            final TrieIterator trieIterator = new TrieIterator(this.m_additionalTrie_);
            final RangeValueIterator.Element element = new RangeValueIterator.Element();
            while (trieIterator.next(element)) {
                set.add(element.start);
            }
        }
    }
    
    static {
        UCharacterProperty.INSTANCE_ = null;
    }
}
