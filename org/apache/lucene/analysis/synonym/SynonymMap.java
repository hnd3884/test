package org.apache.lucene.analysis.synonym;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.text.ParseException;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Set;
import org.apache.lucene.util.fst.Util;
import org.apache.lucene.util.IntsRefBuilder;
import java.util.Comparator;
import java.util.Arrays;
import java.util.HashSet;
import org.apache.lucene.store.ByteArrayDataOutput;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.ByteSequenceOutputs;
import org.apache.lucene.util.CharsRefBuilder;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.CharsRef;
import java.util.HashMap;
import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.fst.FST;

public class SynonymMap
{
    public static final char WORD_SEPARATOR = '\0';
    public final FST<BytesRef> fst;
    public final BytesRefHash words;
    public final int maxHorizontalContext;
    
    public SynonymMap(final FST<BytesRef> fst, final BytesRefHash words, final int maxHorizontalContext) {
        this.fst = fst;
        this.words = words;
        this.maxHorizontalContext = maxHorizontalContext;
    }
    
    public static class Builder
    {
        private final HashMap<CharsRef, MapEntry> workingSet;
        private final BytesRefHash words;
        private final BytesRefBuilder utf8Scratch;
        private int maxHorizontalContext;
        private final boolean dedup;
        
        public Builder(final boolean dedup) {
            this.workingSet = new HashMap<CharsRef, MapEntry>();
            this.words = new BytesRefHash();
            this.utf8Scratch = new BytesRefBuilder();
            this.dedup = dedup;
        }
        
        public static CharsRef join(final String[] words, final CharsRefBuilder reuse) {
            int upto = 0;
            char[] buffer = reuse.chars();
            for (final String word : words) {
                final int wordLen = word.length();
                final int needed = (0 == upto) ? wordLen : (1 + upto + wordLen);
                if (needed > buffer.length) {
                    reuse.grow(needed);
                    buffer = reuse.chars();
                }
                if (upto > 0) {
                    buffer[upto++] = '\0';
                }
                word.getChars(0, wordLen, buffer, upto);
                upto += wordLen;
            }
            reuse.setLength(upto);
            return reuse.get();
        }
        
        private boolean hasHoles(final CharsRef chars) {
            for (int end = chars.offset + chars.length, idx = chars.offset + 1; idx < end; ++idx) {
                if (chars.chars[idx] == '\0' && chars.chars[idx - 1] == '\0') {
                    return true;
                }
            }
            return chars.chars[chars.offset] == '\0' || chars.chars[chars.offset + chars.length - 1] == '\0';
        }
        
        private void add(final CharsRef input, final int numInputWords, final CharsRef output, final int numOutputWords, final boolean includeOrig) {
            if (numInputWords <= 0) {
                throw new IllegalArgumentException("numInputWords must be > 0 (got " + numInputWords + ")");
            }
            if (input.length <= 0) {
                throw new IllegalArgumentException("input.length must be > 0 (got " + input.length + ")");
            }
            if (numOutputWords <= 0) {
                throw new IllegalArgumentException("numOutputWords must be > 0 (got " + numOutputWords + ")");
            }
            if (output.length <= 0) {
                throw new IllegalArgumentException("output.length must be > 0 (got " + output.length + ")");
            }
            assert !this.hasHoles(input) : "input has holes: " + input;
            assert !this.hasHoles(output) : "output has holes: " + output;
            this.utf8Scratch.copyChars(output.chars, output.offset, output.length);
            int ord = this.words.add(this.utf8Scratch.get());
            if (ord < 0) {
                ord = -ord - 1;
            }
            MapEntry e = this.workingSet.get(input);
            if (e == null) {
                e = new MapEntry();
                this.workingSet.put(CharsRef.deepCopyOf(input), e);
            }
            e.ords.add(ord);
            final MapEntry mapEntry = e;
            mapEntry.includeOrig |= includeOrig;
            this.maxHorizontalContext = Math.max(this.maxHorizontalContext, numInputWords);
            this.maxHorizontalContext = Math.max(this.maxHorizontalContext, numOutputWords);
        }
        
        private int countWords(final CharsRef chars) {
            int wordCount = 1;
            int upto = chars.offset;
            final int limit = chars.offset + chars.length;
            while (upto < limit) {
                if (chars.chars[upto++] == '\0') {
                    ++wordCount;
                }
            }
            return wordCount;
        }
        
        public void add(final CharsRef input, final CharsRef output, final boolean includeOrig) {
            this.add(input, this.countWords(input), output, this.countWords(output), includeOrig);
        }
        
        public SynonymMap build() throws IOException {
            final ByteSequenceOutputs outputs = ByteSequenceOutputs.getSingleton();
            final org.apache.lucene.util.fst.Builder<BytesRef> builder = (org.apache.lucene.util.fst.Builder<BytesRef>)new org.apache.lucene.util.fst.Builder(FST.INPUT_TYPE.BYTE4, (Outputs)outputs);
            final BytesRefBuilder scratch = new BytesRefBuilder();
            final ByteArrayDataOutput scratchOutput = new ByteArrayDataOutput();
            Set<Integer> dedupSet;
            if (this.dedup) {
                dedupSet = new HashSet<Integer>();
            }
            else {
                dedupSet = null;
            }
            final byte[] spare = new byte[5];
            final Set<CharsRef> keys = this.workingSet.keySet();
            final CharsRef[] sortedKeys = keys.toArray(new CharsRef[keys.size()]);
            Arrays.sort(sortedKeys, CharsRef.getUTF16SortedAsUTF8Comparator());
            final IntsRefBuilder scratchIntsRef = new IntsRefBuilder();
            for (int keyIdx = 0; keyIdx < sortedKeys.length; ++keyIdx) {
                final CharsRef input = sortedKeys[keyIdx];
                final MapEntry output = this.workingSet.get(input);
                final int numEntries = output.ords.size();
                final int estimatedSize = 5 + numEntries * 5;
                scratch.grow(estimatedSize);
                scratchOutput.reset(scratch.bytes());
                int count = 0;
                for (int i = 0; i < numEntries; ++i) {
                    if (dedupSet != null) {
                        final Integer ent = output.ords.get(i);
                        if (dedupSet.contains(ent)) {
                            continue;
                        }
                        dedupSet.add(ent);
                    }
                    scratchOutput.writeVInt((int)output.ords.get(i));
                    ++count;
                }
                final int pos = scratchOutput.getPosition();
                scratchOutput.writeVInt(count << 1 | (output.includeOrig ? 0 : 1));
                final int pos2 = scratchOutput.getPosition();
                final int vIntLen = pos2 - pos;
                System.arraycopy(scratch.bytes(), pos, spare, 0, vIntLen);
                System.arraycopy(scratch.bytes(), 0, scratch.bytes(), vIntLen, pos);
                System.arraycopy(spare, 0, scratch.bytes(), 0, vIntLen);
                if (dedupSet != null) {
                    dedupSet.clear();
                }
                scratch.setLength(scratchOutput.getPosition());
                builder.add(Util.toUTF32((CharSequence)input, scratchIntsRef), (Object)scratch.toBytesRef());
            }
            final FST<BytesRef> fst = (FST<BytesRef>)builder.finish();
            return new SynonymMap(fst, this.words, this.maxHorizontalContext);
        }
        
        private static class MapEntry
        {
            boolean includeOrig;
            ArrayList<Integer> ords;
            
            private MapEntry() {
                this.ords = new ArrayList<Integer>();
            }
        }
    }
    
    public abstract static class Parser extends Builder
    {
        private final Analyzer analyzer;
        
        public Parser(final boolean dedup, final Analyzer analyzer) {
            super(dedup);
            this.analyzer = analyzer;
        }
        
        public abstract void parse(final Reader p0) throws IOException, ParseException;
        
        public CharsRef analyze(final String text, final CharsRefBuilder reuse) throws IOException {
            try (final TokenStream ts = this.analyzer.tokenStream("", text)) {
                final CharTermAttribute termAtt = (CharTermAttribute)ts.addAttribute((Class)CharTermAttribute.class);
                final PositionIncrementAttribute posIncAtt = (PositionIncrementAttribute)ts.addAttribute((Class)PositionIncrementAttribute.class);
                ts.reset();
                reuse.clear();
                while (ts.incrementToken()) {
                    final int length = termAtt.length();
                    if (length == 0) {
                        throw new IllegalArgumentException("term: " + text + " analyzed to a zero-length token");
                    }
                    if (posIncAtt.getPositionIncrement() != 1) {
                        throw new IllegalArgumentException("term: " + text + " analyzed to a token with posinc != 1");
                    }
                    reuse.grow(reuse.length() + length + 1);
                    int end = reuse.length();
                    if (reuse.length() > 0) {
                        reuse.setCharAt(end++, '\0');
                        reuse.setLength(reuse.length() + 1);
                    }
                    System.arraycopy(termAtt.buffer(), 0, reuse.chars(), end, length);
                    reuse.setLength(reuse.length() + length);
                }
                ts.end();
            }
            if (reuse.length() == 0) {
                throw new IllegalArgumentException("term: " + text + " was completely eliminated by analyzer");
            }
            return reuse.get();
        }
    }
}
