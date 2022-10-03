package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.ByteSequenceOutputs;
import org.apache.lucene.util.CharsRefBuilder;
import java.util.ArrayList;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.BytesRefHash;
import java.io.IOException;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class StemmerOverrideFilter extends TokenFilter
{
    private final StemmerOverrideMap stemmerOverrideMap;
    private final CharTermAttribute termAtt;
    private final KeywordAttribute keywordAtt;
    private final FST.BytesReader fstReader;
    private final FST.Arc<BytesRef> scratchArc;
    private char[] spare;
    
    public StemmerOverrideFilter(final TokenStream input, final StemmerOverrideMap stemmerOverrideMap) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.keywordAtt = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
        this.scratchArc = (FST.Arc<BytesRef>)new FST.Arc();
        this.spare = new char[0];
        this.stemmerOverrideMap = stemmerOverrideMap;
        this.fstReader = stemmerOverrideMap.getBytesReader();
    }
    
    public boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        if (this.fstReader == null) {
            return true;
        }
        if (!this.keywordAtt.isKeyword()) {
            final BytesRef stem = this.stemmerOverrideMap.get(this.termAtt.buffer(), this.termAtt.length(), this.scratchArc, this.fstReader);
            if (stem != null) {
                this.spare = ArrayUtil.grow(this.termAtt.buffer(), stem.length);
                final int length = UnicodeUtil.UTF8toUTF16(stem, this.spare);
                if (this.spare != this.termAtt.buffer()) {
                    this.termAtt.copyBuffer(this.spare, 0, length);
                }
                else {
                    this.termAtt.setLength(length);
                }
                this.keywordAtt.setKeyword(true);
            }
        }
        return true;
    }
    
    public static final class StemmerOverrideMap
    {
        private final FST<BytesRef> fst;
        private final boolean ignoreCase;
        
        public StemmerOverrideMap(final FST<BytesRef> fst, final boolean ignoreCase) {
            this.fst = fst;
            this.ignoreCase = ignoreCase;
        }
        
        public FST.BytesReader getBytesReader() {
            if (this.fst == null) {
                return null;
            }
            return this.fst.getBytesReader();
        }
        
        public BytesRef get(final char[] buffer, final int bufferLen, final FST.Arc<BytesRef> scratchArc, final FST.BytesReader fstReader) throws IOException {
            BytesRef pendingOutput = (BytesRef)this.fst.outputs.getNoOutput();
            BytesRef matchOutput = null;
            int bufUpto = 0;
            this.fst.getFirstArc((FST.Arc)scratchArc);
            while (bufUpto < bufferLen) {
                final int codePoint = Character.codePointAt(buffer, bufUpto, bufferLen);
                if (this.fst.findTargetArc(this.ignoreCase ? Character.toLowerCase(codePoint) : codePoint, (FST.Arc)scratchArc, (FST.Arc)scratchArc, fstReader) == null) {
                    return null;
                }
                pendingOutput = (BytesRef)this.fst.outputs.add((Object)pendingOutput, scratchArc.output);
                bufUpto += Character.charCount(codePoint);
            }
            if (scratchArc.isFinal()) {
                matchOutput = (BytesRef)this.fst.outputs.add((Object)pendingOutput, scratchArc.nextFinalOutput);
            }
            return matchOutput;
        }
    }
    
    public static class Builder
    {
        private final BytesRefHash hash;
        private final BytesRefBuilder spare;
        private final ArrayList<CharSequence> outputValues;
        private final boolean ignoreCase;
        private final CharsRefBuilder charsSpare;
        
        public Builder() {
            this(false);
        }
        
        public Builder(final boolean ignoreCase) {
            this.hash = new BytesRefHash();
            this.spare = new BytesRefBuilder();
            this.outputValues = new ArrayList<CharSequence>();
            this.charsSpare = new CharsRefBuilder();
            this.ignoreCase = ignoreCase;
        }
        
        public boolean add(final CharSequence input, final CharSequence output) {
            final int length = input.length();
            if (this.ignoreCase) {
                this.charsSpare.grow(length);
                final char[] buffer = this.charsSpare.chars();
                for (int i = 0; i < length; i += Character.toChars(Character.toLowerCase(Character.codePointAt(input, i)), buffer, i)) {}
                this.spare.copyChars(buffer, 0, length);
            }
            else {
                this.spare.copyChars(input, 0, length);
            }
            if (this.hash.add(this.spare.get()) >= 0) {
                this.outputValues.add(output);
                return true;
            }
            return false;
        }
        
        public StemmerOverrideMap build() throws IOException {
            final ByteSequenceOutputs outputs = ByteSequenceOutputs.getSingleton();
            final org.apache.lucene.util.fst.Builder<BytesRef> builder = (org.apache.lucene.util.fst.Builder<BytesRef>)new org.apache.lucene.util.fst.Builder(FST.INPUT_TYPE.BYTE4, (Outputs)outputs);
            final int[] sort = this.hash.sort(BytesRef.getUTF8SortedAsUnicodeComparator());
            final IntsRefBuilder intsSpare = new IntsRefBuilder();
            final int size = this.hash.size();
            final BytesRef spare = new BytesRef();
            for (final int id : sort) {
                final BytesRef bytesRef = this.hash.get(id, spare);
                intsSpare.copyUTF8Bytes(bytesRef);
                builder.add(intsSpare.get(), (Object)new BytesRef((CharSequence)this.outputValues.get(id)));
            }
            return new StemmerOverrideMap((FST<BytesRef>)builder.finish(), this.ignoreCase);
        }
    }
}
