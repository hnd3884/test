package org.apache.lucene.search.vectorhighlight;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public abstract class BaseFragListBuilder implements FragListBuilder
{
    public static final int MARGIN_DEFAULT = 6;
    public static final int MIN_FRAG_CHAR_SIZE_FACTOR = 3;
    final int margin;
    final int minFragCharSize;
    
    public BaseFragListBuilder(final int margin) {
        if (margin < 0) {
            throw new IllegalArgumentException("margin(" + margin + ") is too small. It must be 0 or higher.");
        }
        this.margin = margin;
        this.minFragCharSize = Math.max(1, margin * 3);
    }
    
    public BaseFragListBuilder() {
        this(6);
    }
    
    protected FieldFragList createFieldFragList(final FieldPhraseList fieldPhraseList, final FieldFragList fieldFragList, final int fragCharSize) {
        if (fragCharSize < this.minFragCharSize) {
            throw new IllegalArgumentException("fragCharSize(" + fragCharSize + ") is too small. It must be " + this.minFragCharSize + " or higher.");
        }
        final List<FieldPhraseList.WeightedPhraseInfo> wpil = new ArrayList<FieldPhraseList.WeightedPhraseInfo>();
        final IteratorQueue<FieldPhraseList.WeightedPhraseInfo> queue = new IteratorQueue<FieldPhraseList.WeightedPhraseInfo>(fieldPhraseList.getPhraseList().iterator());
        FieldPhraseList.WeightedPhraseInfo phraseInfo = null;
        int startOffset = 0;
        while ((phraseInfo = queue.top()) != null) {
            if (phraseInfo.getStartOffset() < startOffset) {
                queue.removeTop();
            }
            else {
                wpil.clear();
                final int currentPhraseStartOffset = phraseInfo.getStartOffset();
                int currentPhraseEndOffset = phraseInfo.getEndOffset();
                int spanStart = Math.max(currentPhraseStartOffset - this.margin, startOffset);
                int spanEnd = Math.max(currentPhraseEndOffset, spanStart + fragCharSize);
                if (this.acceptPhrase(queue.removeTop(), currentPhraseEndOffset - currentPhraseStartOffset, fragCharSize)) {
                    wpil.add(phraseInfo);
                }
                while ((phraseInfo = queue.top()) != null && phraseInfo.getEndOffset() <= spanEnd) {
                    currentPhraseEndOffset = phraseInfo.getEndOffset();
                    if (this.acceptPhrase(queue.removeTop(), currentPhraseEndOffset - currentPhraseStartOffset, fragCharSize)) {
                        wpil.add(phraseInfo);
                    }
                }
                if (wpil.isEmpty()) {
                    continue;
                }
                final int matchLen = currentPhraseEndOffset - currentPhraseStartOffset;
                final int newMargin = Math.max(0, (fragCharSize - matchLen) / 2);
                spanStart = currentPhraseStartOffset - newMargin;
                if (spanStart < startOffset) {
                    spanStart = startOffset;
                }
                spanEnd = (startOffset = spanStart + Math.max(matchLen, fragCharSize));
                fieldFragList.add(spanStart, spanEnd, wpil);
            }
        }
        return fieldFragList;
    }
    
    protected boolean acceptPhrase(final FieldPhraseList.WeightedPhraseInfo info, final int matchLength, final int fragCharSize) {
        return info.getTermsOffsets().size() <= 1 || matchLength <= fragCharSize;
    }
    
    private static final class IteratorQueue<T>
    {
        private final Iterator<T> iter;
        private T top;
        
        public IteratorQueue(final Iterator<T> iter) {
            this.iter = iter;
            final T removeTop = this.removeTop();
            assert removeTop == null;
        }
        
        public T top() {
            return this.top;
        }
        
        public T removeTop() {
            final T currentTop = this.top;
            if (this.iter.hasNext()) {
                this.top = this.iter.next();
            }
            else {
                this.top = null;
            }
            return currentTop;
        }
    }
}
