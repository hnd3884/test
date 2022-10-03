package sun.text.normalizer;

public class TrieIterator implements RangeValueIterator
{
    private static final int BMP_INDEX_LENGTH_ = 2048;
    private static final int LEAD_SURROGATE_MIN_VALUE_ = 55296;
    private static final int TRAIL_SURROGATE_MIN_VALUE_ = 56320;
    private static final int TRAIL_SURROGATE_COUNT_ = 1024;
    private static final int TRAIL_SURROGATE_INDEX_BLOCK_LENGTH_ = 32;
    private static final int DATA_BLOCK_LENGTH_ = 32;
    private Trie m_trie_;
    private int m_initialValue_;
    private int m_currentCodepoint_;
    private int m_nextCodepoint_;
    private int m_nextValue_;
    private int m_nextIndex_;
    private int m_nextBlock_;
    private int m_nextBlockIndex_;
    private int m_nextTrailIndexOffset_;
    
    public TrieIterator(final Trie trie_) {
        if (trie_ == null) {
            throw new IllegalArgumentException("Argument trie cannot be null");
        }
        this.m_trie_ = trie_;
        this.m_initialValue_ = this.extract(this.m_trie_.getInitialValue());
        this.reset();
    }
    
    @Override
    public final boolean next(final Element element) {
        if (this.m_nextCodepoint_ > 1114111) {
            return false;
        }
        if (this.m_nextCodepoint_ < 65536 && this.calculateNextBMPElement(element)) {
            return true;
        }
        this.calculateNextSupplementaryElement(element);
        return true;
    }
    
    @Override
    public final void reset() {
        this.m_currentCodepoint_ = 0;
        this.m_nextCodepoint_ = 0;
        this.m_nextIndex_ = 0;
        this.m_nextBlock_ = this.m_trie_.m_index_[0] << 2;
        if (this.m_nextBlock_ == 0) {
            this.m_nextValue_ = this.m_initialValue_;
        }
        else {
            this.m_nextValue_ = this.extract(this.m_trie_.getValue(this.m_nextBlock_));
        }
        this.m_nextBlockIndex_ = 0;
        this.m_nextTrailIndexOffset_ = 32;
    }
    
    protected int extract(final int n) {
        return n;
    }
    
    private final void setResult(final Element element, final int start, final int limit, final int value) {
        element.start = start;
        element.limit = limit;
        element.value = value;
    }
    
    private final boolean calculateNextBMPElement(final Element element) {
        final int nextBlock_ = this.m_nextBlock_;
        final int nextValue_ = this.m_nextValue_;
        this.m_currentCodepoint_ = this.m_nextCodepoint_;
        ++this.m_nextCodepoint_;
        ++this.m_nextBlockIndex_;
        if (!this.checkBlockDetail(nextValue_)) {
            this.setResult(element, this.m_currentCodepoint_, this.m_nextCodepoint_, nextValue_);
            return true;
        }
        while (this.m_nextCodepoint_ < 65536) {
            ++this.m_nextIndex_;
            if (this.m_nextCodepoint_ == 55296) {
                this.m_nextIndex_ = 2048;
            }
            else if (this.m_nextCodepoint_ == 56320) {
                this.m_nextIndex_ = this.m_nextCodepoint_ >> 5;
            }
            this.m_nextBlockIndex_ = 0;
            if (!this.checkBlock(nextBlock_, nextValue_)) {
                this.setResult(element, this.m_currentCodepoint_, this.m_nextCodepoint_, nextValue_);
                return true;
            }
        }
        --this.m_nextCodepoint_;
        --this.m_nextBlockIndex_;
        return false;
    }
    
    private final void calculateNextSupplementaryElement(final Element element) {
        final int nextValue_ = this.m_nextValue_;
        final int nextBlock_ = this.m_nextBlock_;
        ++this.m_nextCodepoint_;
        ++this.m_nextBlockIndex_;
        if (UTF16.getTrailSurrogate(this.m_nextCodepoint_) != '\udc00') {
            if (!this.checkNullNextTrailIndex() && !this.checkBlockDetail(nextValue_)) {
                this.setResult(element, this.m_currentCodepoint_, this.m_nextCodepoint_, nextValue_);
                this.m_currentCodepoint_ = this.m_nextCodepoint_;
                return;
            }
            ++this.m_nextIndex_;
            ++this.m_nextTrailIndexOffset_;
            if (!this.checkTrailBlock(nextBlock_, nextValue_)) {
                this.setResult(element, this.m_currentCodepoint_, this.m_nextCodepoint_, nextValue_);
                this.m_currentCodepoint_ = this.m_nextCodepoint_;
                return;
            }
        }
        int i = UTF16.getLeadSurrogate(this.m_nextCodepoint_);
        while (i < 56320) {
            final int n = this.m_trie_.m_index_[i >> 5] << 2;
            if (n == this.m_trie_.m_dataOffset_) {
                if (nextValue_ != this.m_initialValue_) {
                    this.m_nextValue_ = this.m_initialValue_;
                    this.m_nextBlock_ = 0;
                    this.m_nextBlockIndex_ = 0;
                    this.setResult(element, this.m_currentCodepoint_, this.m_nextCodepoint_, nextValue_);
                    this.m_currentCodepoint_ = this.m_nextCodepoint_;
                    return;
                }
                i += 32;
                this.m_nextCodepoint_ = UCharacterProperty.getRawSupplementary((char)i, '\udc00');
            }
            else {
                if (this.m_trie_.m_dataManipulate_ == null) {
                    throw new NullPointerException("The field DataManipulate in this Trie is null");
                }
                this.m_nextIndex_ = this.m_trie_.m_dataManipulate_.getFoldingOffset(this.m_trie_.getValue(n + (i & 0x1F)));
                if (this.m_nextIndex_ <= 0) {
                    if (nextValue_ != this.m_initialValue_) {
                        this.m_nextValue_ = this.m_initialValue_;
                        this.m_nextBlock_ = 0;
                        this.m_nextBlockIndex_ = 0;
                        this.setResult(element, this.m_currentCodepoint_, this.m_nextCodepoint_, nextValue_);
                        this.m_currentCodepoint_ = this.m_nextCodepoint_;
                        return;
                    }
                    this.m_nextCodepoint_ += 1024;
                }
                else {
                    this.m_nextTrailIndexOffset_ = 0;
                    if (!this.checkTrailBlock(nextBlock_, nextValue_)) {
                        this.setResult(element, this.m_currentCodepoint_, this.m_nextCodepoint_, nextValue_);
                        this.m_currentCodepoint_ = this.m_nextCodepoint_;
                        return;
                    }
                }
                ++i;
            }
        }
        this.setResult(element, this.m_currentCodepoint_, 1114112, nextValue_);
    }
    
    private final boolean checkBlockDetail(final int n) {
        while (this.m_nextBlockIndex_ < 32) {
            this.m_nextValue_ = this.extract(this.m_trie_.getValue(this.m_nextBlock_ + this.m_nextBlockIndex_));
            if (this.m_nextValue_ != n) {
                return false;
            }
            ++this.m_nextBlockIndex_;
            ++this.m_nextCodepoint_;
        }
        return true;
    }
    
    private final boolean checkBlock(final int n, final int n2) {
        this.m_nextBlock_ = this.m_trie_.m_index_[this.m_nextIndex_] << 2;
        if (this.m_nextBlock_ == n && this.m_nextCodepoint_ - this.m_currentCodepoint_ >= 32) {
            this.m_nextCodepoint_ += 32;
        }
        else if (this.m_nextBlock_ == 0) {
            if (n2 != this.m_initialValue_) {
                this.m_nextValue_ = this.m_initialValue_;
                this.m_nextBlockIndex_ = 0;
                return false;
            }
            this.m_nextCodepoint_ += 32;
        }
        else if (!this.checkBlockDetail(n2)) {
            return false;
        }
        return true;
    }
    
    private final boolean checkTrailBlock(final int n, final int n2) {
        while (this.m_nextTrailIndexOffset_ < 32) {
            this.m_nextBlockIndex_ = 0;
            if (!this.checkBlock(n, n2)) {
                return false;
            }
            ++this.m_nextTrailIndexOffset_;
            ++this.m_nextIndex_;
        }
        return true;
    }
    
    private final boolean checkNullNextTrailIndex() {
        if (this.m_nextIndex_ > 0) {
            return false;
        }
        this.m_nextCodepoint_ += 1023;
        final char leadSurrogate = UTF16.getLeadSurrogate(this.m_nextCodepoint_);
        final int n = this.m_trie_.m_index_[leadSurrogate >> 5] << 2;
        if (this.m_trie_.m_dataManipulate_ == null) {
            throw new NullPointerException("The field DataManipulate in this Trie is null");
        }
        this.m_nextIndex_ = this.m_trie_.m_dataManipulate_.getFoldingOffset(this.m_trie_.getValue(n + (leadSurrogate & '\u001f')));
        --this.m_nextIndex_;
        this.m_nextBlockIndex_ = 32;
        return true;
    }
}
