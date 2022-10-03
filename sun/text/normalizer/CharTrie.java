package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CharTrie extends Trie
{
    private char m_initialValue_;
    private char[] m_data_;
    private FriendAgent m_friendAgent_;
    
    public CharTrie(final InputStream inputStream, final DataManipulate dataManipulate) throws IOException {
        super(inputStream, dataManipulate);
        if (!this.isCharTrie()) {
            throw new IllegalArgumentException("Data given does not belong to a char trie.");
        }
        this.m_friendAgent_ = new FriendAgent();
    }
    
    public CharTrie(final int n, final int n2, final DataManipulate dataManipulate) {
        super(new char[2080], 512, dataManipulate);
        int dataLength_;
        final int n3 = dataLength_ = 256;
        if (n2 != n) {
            dataLength_ += 32;
        }
        this.m_data_ = new char[dataLength_];
        this.m_dataLength_ = dataLength_;
        this.m_initialValue_ = (char)n;
        for (int i = 0; i < n3; ++i) {
            this.m_data_[i] = (char)n;
        }
        if (n2 != n) {
            final char c = (char)(n3 >> 2);
            for (int j = 1728; j < 1760; ++j) {
                this.m_index_[j] = c;
            }
            for (int n4 = n3 + 32, k = n3; k < n4; ++k) {
                this.m_data_[k] = (char)n2;
            }
        }
        this.m_friendAgent_ = new FriendAgent();
    }
    
    public void putIndexData(final UCharacterProperty uCharacterProperty) {
        uCharacterProperty.setIndexData(this.m_friendAgent_);
    }
    
    public final char getCodePointValue(final int n) {
        if (0 <= n && n < 55296) {
            return this.m_data_[(this.m_index_[n >> 5] << 2) + (n & 0x1F)];
        }
        final int codePointOffset = this.getCodePointOffset(n);
        return (codePointOffset >= 0) ? this.m_data_[codePointOffset] : this.m_initialValue_;
    }
    
    public final char getLeadValue(final char c) {
        return this.m_data_[this.getLeadOffset(c)];
    }
    
    public final char getSurrogateValue(final char c, final char c2) {
        final int surrogateOffset = this.getSurrogateOffset(c, c2);
        if (surrogateOffset > 0) {
            return this.m_data_[surrogateOffset];
        }
        return this.m_initialValue_;
    }
    
    public final char getTrailValue(final int n, final char c) {
        if (this.m_dataManipulate_ == null) {
            throw new NullPointerException("The field DataManipulate in this Trie is null");
        }
        final int foldingOffset = this.m_dataManipulate_.getFoldingOffset(n);
        if (foldingOffset > 0) {
            return this.m_data_[this.getRawOffset(foldingOffset, (char)(c & '\u03ff'))];
        }
        return this.m_initialValue_;
    }
    
    @Override
    protected final void unserialize(final InputStream inputStream) throws IOException {
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        final int n = this.m_dataOffset_ + this.m_dataLength_;
        this.m_index_ = new char[n];
        for (int i = 0; i < n; ++i) {
            this.m_index_[i] = dataInputStream.readChar();
        }
        this.m_data_ = this.m_index_;
        this.m_initialValue_ = this.m_data_[this.m_dataOffset_];
    }
    
    @Override
    protected final int getSurrogateOffset(final char c, final char c2) {
        if (this.m_dataManipulate_ == null) {
            throw new NullPointerException("The field DataManipulate in this Trie is null");
        }
        final int foldingOffset = this.m_dataManipulate_.getFoldingOffset(this.getLeadValue(c));
        if (foldingOffset > 0) {
            return this.getRawOffset(foldingOffset, (char)(c2 & '\u03ff'));
        }
        return -1;
    }
    
    @Override
    protected final int getValue(final int n) {
        return this.m_data_[n];
    }
    
    @Override
    protected final int getInitialValue() {
        return this.m_initialValue_;
    }
    
    public class FriendAgent
    {
        public char[] getPrivateIndex() {
            return CharTrie.this.m_index_;
        }
        
        public char[] getPrivateData() {
            return CharTrie.this.m_data_;
        }
        
        public int getPrivateInitialValue() {
            return CharTrie.this.m_initialValue_;
        }
    }
}
