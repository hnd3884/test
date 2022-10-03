package org.apache.lucene.search.highlight;

import org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.Counter;
import java.io.IOException;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.BytesRefArray;
import org.apache.lucene.util.CharsRefBuilder;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Terms;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.TokenStream;

public final class TokenStreamFromTermVector extends TokenStream
{
    public static final AttributeFactory ATTRIBUTE_FACTORY;
    private final Terms vector;
    private final CharTermAttribute termAttribute;
    private final PositionIncrementAttribute positionIncrementAttribute;
    private final int maxStartOffset;
    private OffsetAttribute offsetAttribute;
    private PayloadAttribute payloadAttribute;
    private CharsRefBuilder termCharsBuilder;
    private BytesRefArray payloadsBytesRefArray;
    private BytesRefBuilder spareBytesRefBuilder;
    private TokenLL firstToken;
    private TokenLL incrementToken;
    private boolean initialized;
    
    public TokenStreamFromTermVector(final Terms vector, final int maxStartOffset) throws IOException {
        super(TokenStreamFromTermVector.ATTRIBUTE_FACTORY);
        this.firstToken = null;
        this.incrementToken = null;
        this.initialized = false;
        this.maxStartOffset = ((maxStartOffset < 0) ? Integer.MAX_VALUE : maxStartOffset);
        assert !this.hasAttribute((Class)PayloadAttribute.class) : "AttributeFactory shouldn't have payloads *yet*";
        if (!vector.hasPositions() && !vector.hasOffsets()) {
            throw new IllegalArgumentException("The term vector needs positions and/or offsets.");
        }
        assert vector.hasFreqs();
        this.vector = vector;
        this.termAttribute = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.positionIncrementAttribute = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
    }
    
    public Terms getTermVectorTerms() {
        return this.vector;
    }
    
    public void reset() throws IOException {
        this.incrementToken = null;
        super.reset();
    }
    
    private void init() throws IOException {
        assert !this.initialized;
        short dpEnumFlags = 24;
        if (this.vector.hasOffsets()) {
            dpEnumFlags |= 0x38;
            this.offsetAttribute = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        }
        if (this.vector.hasPayloads() && this.hasAttribute((Class)PayloadAttribute.class)) {
            dpEnumFlags |= 0x78;
            this.payloadAttribute = (PayloadAttribute)this.getAttribute((Class)PayloadAttribute.class);
            this.payloadsBytesRefArray = new BytesRefArray(Counter.newCounter());
            this.spareBytesRefBuilder = new BytesRefBuilder();
        }
        (this.termCharsBuilder = new CharsRefBuilder()).grow((int)(this.vector.size() * 7L));
        TokenLL[] positionedTokens = this.initTokensArray();
        int lastPosition = -1;
        final TermsEnum termsEnum = this.vector.iterator();
        PostingsEnum dpEnum = null;
        final CharsRefBuilder tempCharsRefBuilder = new CharsRefBuilder();
        BytesRef termBytesRef;
        while ((termBytesRef = termsEnum.next()) != null) {
            tempCharsRefBuilder.grow(termBytesRef.length);
            final int termCharsLen = UnicodeUtil.UTF8toUTF16(termBytesRef, tempCharsRefBuilder.chars());
            final int termCharsOff = this.termCharsBuilder.length();
            this.termCharsBuilder.append(tempCharsRefBuilder.chars(), 0, termCharsLen);
            dpEnum = termsEnum.postings(dpEnum, (int)dpEnumFlags);
            assert dpEnum != null;
            dpEnum.nextDoc();
            for (int freq = dpEnum.freq(), j = 0; j < freq; ++j) {
                int pos = dpEnum.nextPosition();
                final TokenLL token = new TokenLL();
                token.termCharsOff = termCharsOff;
                token.termCharsLen = (short)Math.min(termCharsLen, 32767);
                if (this.offsetAttribute != null) {
                    token.startOffset = dpEnum.startOffset();
                    if (token.startOffset > this.maxStartOffset) {
                        continue;
                    }
                    token.endOffsetInc = (short)Math.min(dpEnum.endOffset() - token.startOffset, 32767);
                    if (pos == -1) {
                        pos = token.startOffset >> 3;
                    }
                }
                if (this.payloadAttribute != null) {
                    final BytesRef payload = dpEnum.getPayload();
                    token.payloadIndex = ((payload == null) ? -1 : this.payloadsBytesRefArray.append(payload));
                }
                if (positionedTokens.length <= pos) {
                    final TokenLL[] newPositionedTokens = new TokenLL[(int)((pos + 1) * 1.5f)];
                    System.arraycopy(positionedTokens, 0, newPositionedTokens, 0, lastPosition + 1);
                    positionedTokens = newPositionedTokens;
                }
                positionedTokens[pos] = token.insertIntoSortedLinkedList(positionedTokens[pos]);
                lastPosition = Math.max(lastPosition, pos);
            }
        }
        int prevTokenPos = -1;
        TokenLL prevToken = null;
        for (int pos2 = 0; pos2 <= lastPosition; ++pos2) {
            TokenLL token2 = positionedTokens[pos2];
            if (token2 != null) {
                if (prevToken != null) {
                    assert prevToken.next == null;
                    prevToken.next = token2;
                }
                else {
                    assert this.firstToken == null;
                    this.firstToken = token2;
                }
                if (this.vector.hasPositions()) {
                    token2.positionIncrement = pos2 - prevTokenPos;
                    while (token2.next != null) {
                        token2 = token2.next;
                        token2.positionIncrement = 0;
                    }
                }
                else {
                    token2.positionIncrement = 1;
                    while (token2.next != null) {
                        prevToken = token2;
                        token2 = token2.next;
                        if (prevToken.startOffset == token2.startOffset) {
                            token2.positionIncrement = 0;
                        }
                        else {
                            token2.positionIncrement = 1;
                        }
                    }
                }
                prevTokenPos = pos2;
                prevToken = token2;
            }
        }
        this.initialized = true;
    }
    
    private TokenLL[] initTokensArray() throws IOException {
        int sumTotalTermFreq = (int)this.vector.getSumTotalTermFreq();
        if (sumTotalTermFreq == -1) {
            int size = (int)this.vector.size();
            if (size == -1) {
                size = 128;
            }
            sumTotalTermFreq = (int)(size * 2.4);
        }
        final int originalPositionEstimate = (int)(sumTotalTermFreq * 1.5);
        final int offsetLimitPositionEstimate = (int)(this.maxStartOffset / 5.0);
        return new TokenLL[Math.max(64, Math.min(originalPositionEstimate, offsetLimitPositionEstimate))];
    }
    
    public boolean incrementToken() throws IOException {
        if (this.incrementToken == null) {
            if (!this.initialized) {
                this.init();
                assert this.initialized;
            }
            this.incrementToken = this.firstToken;
            if (this.incrementToken == null) {
                return false;
            }
        }
        else {
            if (this.incrementToken.next == null) {
                return false;
            }
            this.incrementToken = this.incrementToken.next;
        }
        this.clearAttributes();
        this.termAttribute.copyBuffer(this.termCharsBuilder.chars(), this.incrementToken.termCharsOff, (int)this.incrementToken.termCharsLen);
        this.positionIncrementAttribute.setPositionIncrement(this.incrementToken.positionIncrement);
        if (this.offsetAttribute != null) {
            this.offsetAttribute.setOffset(this.incrementToken.startOffset, this.incrementToken.startOffset + this.incrementToken.endOffsetInc);
        }
        if (this.payloadAttribute != null) {
            if (this.incrementToken.payloadIndex == -1) {
                this.payloadAttribute.setPayload((BytesRef)null);
            }
            else {
                this.payloadAttribute.setPayload(this.payloadsBytesRefArray.get(this.spareBytesRefBuilder, this.incrementToken.payloadIndex));
            }
        }
        return true;
    }
    
    static {
        ATTRIBUTE_FACTORY = AttributeFactory.getStaticImplementation(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, (Class)PackedTokenAttributeImpl.class);
    }
    
    private static class TokenLL
    {
        int termCharsOff;
        short termCharsLen;
        int positionIncrement;
        int startOffset;
        short endOffsetInc;
        int payloadIndex;
        TokenLL next;
        
        TokenLL insertIntoSortedLinkedList(final TokenLL head) {
            assert this.next == null;
            if (head == null) {
                return this;
            }
            if (this.compareOffsets(head) <= 0) {
                this.next = head;
                return this;
            }
            TokenLL prev;
            for (prev = head; prev.next != null && this.compareOffsets(prev.next) > 0; prev = prev.next) {}
            this.next = prev.next;
            prev.next = this;
            return head;
        }
        
        int compareOffsets(final TokenLL tokenB) {
            int cmp = Integer.compare(this.startOffset, tokenB.startOffset);
            if (cmp == 0) {
                cmp = Short.compare(this.endOffsetInc, tokenB.endOffsetInc);
            }
            return cmp;
        }
    }
}
