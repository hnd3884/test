package sun.text.normalizer;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.InputStream;

public abstract class Trie
{
    protected static final int LEAD_INDEX_OFFSET_ = 320;
    protected static final int INDEX_STAGE_1_SHIFT_ = 5;
    protected static final int INDEX_STAGE_2_SHIFT_ = 2;
    protected static final int DATA_BLOCK_LENGTH = 32;
    protected static final int INDEX_STAGE_3_MASK_ = 31;
    protected static final int SURROGATE_BLOCK_BITS = 5;
    protected static final int SURROGATE_BLOCK_COUNT = 32;
    protected static final int BMP_INDEX_LENGTH = 2048;
    protected static final int SURROGATE_MASK_ = 1023;
    protected char[] m_index_;
    protected DataManipulate m_dataManipulate_;
    protected int m_dataOffset_;
    protected int m_dataLength_;
    protected static final int HEADER_OPTIONS_LATIN1_IS_LINEAR_MASK_ = 512;
    protected static final int HEADER_SIGNATURE_ = 1416784229;
    private static final int HEADER_OPTIONS_SHIFT_MASK_ = 15;
    protected static final int HEADER_OPTIONS_INDEX_SHIFT_ = 4;
    protected static final int HEADER_OPTIONS_DATA_IS_32_BIT_ = 256;
    private boolean m_isLatin1Linear_;
    private int m_options_;
    
    protected Trie(final InputStream inputStream, final DataManipulate dataManipulate_) throws IOException {
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        final int int1 = dataInputStream.readInt();
        this.m_options_ = dataInputStream.readInt();
        if (!this.checkHeader(int1)) {
            throw new IllegalArgumentException("ICU data file error: Trie header authentication failed, please check if you have the most updated ICU data file");
        }
        if (dataManipulate_ != null) {
            this.m_dataManipulate_ = dataManipulate_;
        }
        else {
            this.m_dataManipulate_ = new DefaultGetFoldingOffset();
        }
        this.m_isLatin1Linear_ = ((this.m_options_ & 0x200) != 0x0);
        this.m_dataOffset_ = dataInputStream.readInt();
        this.m_dataLength_ = dataInputStream.readInt();
        this.unserialize(inputStream);
    }
    
    protected Trie(final char[] index_, final int options_, final DataManipulate dataManipulate_) {
        this.m_options_ = options_;
        if (dataManipulate_ != null) {
            this.m_dataManipulate_ = dataManipulate_;
        }
        else {
            this.m_dataManipulate_ = new DefaultGetFoldingOffset();
        }
        this.m_isLatin1Linear_ = ((this.m_options_ & 0x200) != 0x0);
        this.m_index_ = index_;
        this.m_dataOffset_ = this.m_index_.length;
    }
    
    protected abstract int getSurrogateOffset(final char p0, final char p1);
    
    protected abstract int getValue(final int p0);
    
    protected abstract int getInitialValue();
    
    protected final int getRawOffset(final int n, final char c) {
        return (this.m_index_[n + (c >> 5)] << 2) + (c & '\u001f');
    }
    
    protected final int getBMPOffset(final char c) {
        return (c >= '\ud800' && c <= '\udbff') ? this.getRawOffset(320, c) : this.getRawOffset(0, c);
    }
    
    protected final int getLeadOffset(final char c) {
        return this.getRawOffset(0, c);
    }
    
    protected final int getCodePointOffset(final int n) {
        if (n < 0) {
            return -1;
        }
        if (n < 55296) {
            return this.getRawOffset(0, (char)n);
        }
        if (n < 65536) {
            return this.getBMPOffset((char)n);
        }
        if (n <= 1114111) {
            return this.getSurrogateOffset(UTF16.getLeadSurrogate(n), (char)(n & 0x3FF));
        }
        return -1;
    }
    
    protected void unserialize(final InputStream inputStream) throws IOException {
        this.m_index_ = new char[this.m_dataOffset_];
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        for (int i = 0; i < this.m_dataOffset_; ++i) {
            this.m_index_[i] = dataInputStream.readChar();
        }
    }
    
    protected final boolean isIntTrie() {
        return (this.m_options_ & 0x100) != 0x0;
    }
    
    protected final boolean isCharTrie() {
        return (this.m_options_ & 0x100) == 0x0;
    }
    
    private final boolean checkHeader(final int n) {
        return n == 1416784229 && (this.m_options_ & 0xF) == 0x5 && (this.m_options_ >> 4 & 0xF) == 0x2;
    }
    
    private static class DefaultGetFoldingOffset implements DataManipulate
    {
        @Override
        public int getFoldingOffset(final int n) {
            return n;
        }
    }
    
    public interface DataManipulate
    {
        int getFoldingOffset(final int p0);
    }
}
