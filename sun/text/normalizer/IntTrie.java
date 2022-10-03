package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class IntTrie extends Trie
{
    private int m_initialValue_;
    private int[] m_data_;
    
    public IntTrie(final InputStream inputStream, final DataManipulate dataManipulate) throws IOException {
        super(inputStream, dataManipulate);
        if (!this.isIntTrie()) {
            throw new IllegalArgumentException("Data given does not belong to a int trie.");
        }
    }
    
    public final int getCodePointValue(final int n) {
        final int codePointOffset = this.getCodePointOffset(n);
        return (codePointOffset >= 0) ? this.m_data_[codePointOffset] : this.m_initialValue_;
    }
    
    public final int getLeadValue(final char c) {
        return this.m_data_[this.getLeadOffset(c)];
    }
    
    public final int getTrailValue(final int n, final char c) {
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
        super.unserialize(inputStream);
        this.m_data_ = new int[this.m_dataLength_];
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        for (int i = 0; i < this.m_dataLength_; ++i) {
            this.m_data_[i] = dataInputStream.readInt();
        }
        this.m_initialValue_ = this.m_data_[0];
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
    
    IntTrie(final char[] array, final int[] data_, final int initialValue_, final int n, final DataManipulate dataManipulate) {
        super(array, n, dataManipulate);
        this.m_data_ = data_;
        this.m_dataLength_ = this.m_data_.length;
        this.m_initialValue_ = initialValue_;
    }
}
