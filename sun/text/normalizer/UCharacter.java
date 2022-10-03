package sun.text.normalizer;

import java.io.IOException;
import java.util.MissingResourceException;

public final class UCharacter
{
    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 1114111;
    public static final int SUPPLEMENTARY_MIN_VALUE = 65536;
    private static final UCharacterProperty PROPERTY_;
    private static final char[] PROPERTY_TRIE_INDEX_;
    private static final char[] PROPERTY_TRIE_DATA_;
    private static final int PROPERTY_INITIAL_VALUE_;
    private static final UBiDiProps gBdp;
    private static final int NUMERIC_TYPE_SHIFT_ = 5;
    private static final int NUMERIC_TYPE_MASK_ = 224;
    
    public static int digit(final int n, final int n2) {
        final int property = getProperty(n);
        int n3;
        if (getNumericType(property) == 1) {
            n3 = UCharacterProperty.getUnsignedValue(property);
        }
        else {
            n3 = getEuropeanDigit(n);
        }
        return (0 <= n3 && n3 < n2) ? n3 : -1;
    }
    
    public static int getDirection(final int n) {
        return UCharacter.gBdp.getClass(n);
    }
    
    public static int getCodePoint(final char c, final char c2) {
        if (UTF16.isLeadSurrogate(c) && UTF16.isTrailSurrogate(c2)) {
            return UCharacterProperty.getRawSupplementary(c, c2);
        }
        throw new IllegalArgumentException("Illegal surrogate characters");
    }
    
    public static VersionInfo getAge(final int n) {
        if (n < 0 || n > 1114111) {
            throw new IllegalArgumentException("Codepoint out of bounds");
        }
        return UCharacter.PROPERTY_.getAge(n);
    }
    
    private static int getEuropeanDigit(final int n) {
        if ((n > 122 && n < 65313) || n < 65 || (n > 90 && n < 97) || n > 65370 || (n > 65338 && n < 65345)) {
            return -1;
        }
        if (n <= 122) {
            return n + 10 - ((n <= 90) ? 65 : 97);
        }
        if (n <= 65338) {
            return n + 10 - 65313;
        }
        return n + 10 - 65345;
    }
    
    private static int getNumericType(final int n) {
        return (n & 0xE0) >> 5;
    }
    
    private static final int getProperty(final int n) {
        Label_0042: {
            if (n >= 55296) {
                if (n <= 56319 || n >= 65536) {
                    break Label_0042;
                }
            }
            try {
                return UCharacter.PROPERTY_TRIE_DATA_[(UCharacter.PROPERTY_TRIE_INDEX_[n >> 5] << 2) + (n & 0x1F)];
            }
            catch (final ArrayIndexOutOfBoundsException ex) {
                return UCharacter.PROPERTY_INITIAL_VALUE_;
            }
        }
        if (n <= 56319) {
            return UCharacter.PROPERTY_TRIE_DATA_[(UCharacter.PROPERTY_TRIE_INDEX_[320 + (n >> 5)] << 2) + (n & 0x1F)];
        }
        if (n <= 1114111) {
            return UCharacter.PROPERTY_.m_trie_.getSurrogateValue(UTF16.getLeadSurrogate(n), (char)(n & 0x3FF));
        }
        return UCharacter.PROPERTY_INITIAL_VALUE_;
    }
    
    static {
        try {
            PROPERTY_ = UCharacterProperty.getInstance();
            PROPERTY_TRIE_INDEX_ = UCharacter.PROPERTY_.m_trieIndex_;
            PROPERTY_TRIE_DATA_ = UCharacter.PROPERTY_.m_trieData_;
            PROPERTY_INITIAL_VALUE_ = UCharacter.PROPERTY_.m_trieInitialValue_;
        }
        catch (final Exception ex) {
            throw new MissingResourceException(ex.getMessage(), "", "");
        }
        UBiDiProps gBdp2;
        try {
            gBdp2 = UBiDiProps.getSingleton();
        }
        catch (final IOException ex2) {
            gBdp2 = UBiDiProps.getDummy();
        }
        gBdp = gBdp2;
    }
    
    public interface NumericType
    {
        public static final int DECIMAL = 1;
    }
}
