package org.apache.lucene.analysis.synonym;

import java.util.Arrays;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.CharsRef;
import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.CharsRefBuilder;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class SynonymFilter extends TokenFilter
{
    public static final String TYPE_SYNONYM = "SYNONYM";
    private final SynonymMap synonyms;
    private final boolean ignoreCase;
    private final int rollBufferSize;
    private int captureCount;
    private final CharTermAttribute termAtt;
    private final PositionIncrementAttribute posIncrAtt;
    private final PositionLengthAttribute posLenAtt;
    private final TypeAttribute typeAtt;
    private final OffsetAttribute offsetAtt;
    private int inputSkipCount;
    private final PendingInput[] futureInputs;
    private final ByteArrayDataInput bytesReader;
    private final PendingOutputs[] futureOutputs;
    private int nextWrite;
    private int nextRead;
    private boolean finished;
    private final FST.Arc<BytesRef> scratchArc;
    private final FST<BytesRef> fst;
    private final FST.BytesReader fstReader;
    private final BytesRef scratchBytes;
    private final CharsRefBuilder scratchChars;
    private int lastStartOffset;
    private int lastEndOffset;
    
    public SynonymFilter(final TokenStream input, final SynonymMap synonyms, final boolean ignoreCase) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.posLenAtt = (PositionLengthAttribute)this.addAttribute((Class)PositionLengthAttribute.class);
        this.typeAtt = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.bytesReader = new ByteArrayDataInput();
        this.scratchBytes = new BytesRef();
        this.scratchChars = new CharsRefBuilder();
        this.synonyms = synonyms;
        this.ignoreCase = ignoreCase;
        this.fst = synonyms.fst;
        if (this.fst == null) {
            throw new IllegalArgumentException("fst must be non-null");
        }
        this.fstReader = this.fst.getBytesReader();
        this.rollBufferSize = 1 + synonyms.maxHorizontalContext;
        this.futureInputs = new PendingInput[this.rollBufferSize];
        this.futureOutputs = new PendingOutputs[this.rollBufferSize];
        for (int pos = 0; pos < this.rollBufferSize; ++pos) {
            this.futureInputs[pos] = new PendingInput();
            this.futureOutputs[pos] = new PendingOutputs();
        }
        this.scratchArc = (FST.Arc<BytesRef>)new FST.Arc();
    }
    
    private void capture() {
        ++this.captureCount;
        final PendingInput input = this.futureInputs[this.nextWrite];
        input.state = this.captureState();
        input.consumed = false;
        input.term.copyChars(this.termAtt.buffer(), 0, this.termAtt.length());
        this.nextWrite = this.rollIncr(this.nextWrite);
        assert this.nextWrite != this.nextRead;
    }
    
    private void parse() throws IOException {
        assert this.inputSkipCount == 0;
        int curNextRead = this.nextRead;
        BytesRef matchOutput = null;
        int matchInputLength = 0;
        int matchEndOffset = -1;
        BytesRef pendingOutput = (BytesRef)this.fst.outputs.getNoOutput();
        this.fst.getFirstArc((FST.Arc)this.scratchArc);
        assert this.scratchArc.output == this.fst.outputs.getNoOutput();
        int tokenCount = 0;
    Label_0526:
        while (true) {
            int inputEndOffset = 0;
            char[] buffer;
            int bufferLen;
            if (curNextRead == this.nextWrite) {
                if (this.finished) {
                    break;
                }
                assert this.futureInputs[this.nextWrite].consumed;
                if (!this.input.incrementToken()) {
                    this.finished = true;
                    break;
                }
                buffer = this.termAtt.buffer();
                bufferLen = this.termAtt.length();
                final PendingInput pendingInput;
                final PendingInput input = pendingInput = this.futureInputs[this.nextWrite];
                final int startOffset = this.offsetAtt.startOffset();
                pendingInput.startOffset = startOffset;
                this.lastStartOffset = startOffset;
                final PendingInput pendingInput2 = input;
                final int endOffset = this.offsetAtt.endOffset();
                pendingInput2.endOffset = endOffset;
                this.lastEndOffset = endOffset;
                inputEndOffset = input.endOffset;
                if (this.nextRead != this.nextWrite) {
                    this.capture();
                }
                else {
                    input.consumed = false;
                }
            }
            else {
                buffer = this.futureInputs[curNextRead].term.chars();
                bufferLen = this.futureInputs[curNextRead].term.length();
                inputEndOffset = this.futureInputs[curNextRead].endOffset;
            }
            ++tokenCount;
            int codePoint;
            for (int bufUpto = 0; bufUpto < bufferLen; bufUpto += Character.charCount(codePoint)) {
                codePoint = Character.codePointAt(buffer, bufUpto, bufferLen);
                if (this.fst.findTargetArc(this.ignoreCase ? Character.toLowerCase(codePoint) : codePoint, (FST.Arc)this.scratchArc, (FST.Arc)this.scratchArc, this.fstReader) == null) {
                    break Label_0526;
                }
                pendingOutput = (BytesRef)this.fst.outputs.add((Object)pendingOutput, this.scratchArc.output);
            }
            if (this.scratchArc.isFinal()) {
                matchOutput = (BytesRef)this.fst.outputs.add((Object)pendingOutput, this.scratchArc.nextFinalOutput);
                matchInputLength = tokenCount;
                matchEndOffset = inputEndOffset;
            }
            if (this.fst.findTargetArc(0, (FST.Arc)this.scratchArc, (FST.Arc)this.scratchArc, this.fstReader) == null) {
                break;
            }
            pendingOutput = (BytesRef)this.fst.outputs.add((Object)pendingOutput, this.scratchArc.output);
            if (this.nextRead == this.nextWrite) {
                this.capture();
            }
            curNextRead = this.rollIncr(curNextRead);
        }
        if (this.nextRead == this.nextWrite && !this.finished) {
            this.nextWrite = this.rollIncr(this.nextWrite);
        }
        if (matchOutput != null) {
            this.addOutput(matchOutput, this.inputSkipCount = matchInputLength, matchEndOffset);
        }
        else if (this.nextRead != this.nextWrite) {
            this.inputSkipCount = 1;
        }
        else {
            assert this.finished;
        }
    }
    
    private void addOutput(final BytesRef bytes, final int matchInputLength, final int matchEndOffset) {
        this.bytesReader.reset(bytes.bytes, bytes.offset, bytes.length);
        final int code = this.bytesReader.readVInt();
        final boolean keepOrig = (code & 0x1) == 0x0;
        for (int count = code >>> 1, outputIDX = 0; outputIDX < count; ++outputIDX) {
            this.synonyms.words.get(this.bytesReader.readVInt(), this.scratchBytes);
            this.scratchChars.copyUTF8Bytes(this.scratchBytes);
            int lastStart = 0;
            final int chEnd = lastStart + this.scratchChars.length();
            int outputUpto = this.nextRead;
            for (int chIDX = lastStart; chIDX <= chEnd; ++chIDX) {
                if (chIDX == chEnd || this.scratchChars.charAt(chIDX) == '\0') {
                    final int outputLen = chIDX - lastStart;
                    assert outputLen > 0 : "output contains empty string: " + this.scratchChars;
                    int endOffset;
                    int posLen;
                    if (chIDX == chEnd && lastStart == 0) {
                        endOffset = matchEndOffset;
                        posLen = (keepOrig ? matchInputLength : 1);
                    }
                    else {
                        endOffset = -1;
                        posLen = 1;
                    }
                    this.futureOutputs[outputUpto].add(this.scratchChars.chars(), lastStart, outputLen, endOffset, posLen);
                    lastStart = 1 + chIDX;
                    outputUpto = this.rollIncr(outputUpto);
                    assert this.futureOutputs[outputUpto].posIncr == 1 : "outputUpto=" + outputUpto + " vs nextWrite=" + this.nextWrite;
                }
            }
        }
        int upto = this.nextRead;
        for (int idx = 0; idx < matchInputLength; ++idx) {
            final PendingInput pendingInput = this.futureInputs[upto];
            pendingInput.keepOrig |= keepOrig;
            this.futureInputs[upto].matched = true;
            upto = this.rollIncr(upto);
        }
    }
    
    private int rollIncr(int count) {
        if (++count == this.rollBufferSize) {
            return 0;
        }
        return count;
    }
    
    int getCaptureCount() {
        return this.captureCount;
    }
    
    public boolean incrementToken() throws IOException {
        while (true) {
            if (this.inputSkipCount != 0) {
                final PendingInput input = this.futureInputs[this.nextRead];
                final PendingOutputs outputs = this.futureOutputs[this.nextRead];
                if (!input.consumed && (input.keepOrig || !input.matched)) {
                    if (input.state != null) {
                        this.restoreState(input.state);
                    }
                    else {
                        assert this.inputSkipCount == 1 : "inputSkipCount=" + this.inputSkipCount + " nextRead=" + this.nextRead;
                    }
                    input.reset();
                    if (outputs.count > 0) {
                        outputs.posIncr = 0;
                    }
                    else {
                        this.nextRead = this.rollIncr(this.nextRead);
                        --this.inputSkipCount;
                    }
                    return true;
                }
                if (outputs.upto < outputs.count) {
                    input.reset();
                    final int posIncr = outputs.posIncr;
                    final CharsRef output = outputs.pullNext();
                    this.clearAttributes();
                    this.termAtt.copyBuffer(output.chars, output.offset, output.length);
                    this.typeAtt.setType("SYNONYM");
                    int endOffset = outputs.getLastEndOffset();
                    if (endOffset == -1) {
                        endOffset = input.endOffset;
                    }
                    this.offsetAtt.setOffset(input.startOffset, endOffset);
                    this.posIncrAtt.setPositionIncrement(posIncr);
                    this.posLenAtt.setPositionLength(outputs.getLastPosLength());
                    if (outputs.count == 0) {
                        this.nextRead = this.rollIncr(this.nextRead);
                        --this.inputSkipCount;
                    }
                    return true;
                }
                input.reset();
                this.nextRead = this.rollIncr(this.nextRead);
                --this.inputSkipCount;
            }
            else if (this.finished && this.nextRead == this.nextWrite) {
                final PendingOutputs outputs2 = this.futureOutputs[this.nextRead];
                if (outputs2.upto < outputs2.count) {
                    final int posIncr2 = outputs2.posIncr;
                    final CharsRef output2 = outputs2.pullNext();
                    this.futureInputs[this.nextRead].reset();
                    if (outputs2.count == 0) {
                        final int rollIncr = this.rollIncr(this.nextRead);
                        this.nextRead = rollIncr;
                        this.nextWrite = rollIncr;
                    }
                    this.clearAttributes();
                    this.offsetAtt.setOffset(this.lastStartOffset, this.lastEndOffset);
                    this.termAtt.copyBuffer(output2.chars, output2.offset, output2.length);
                    this.typeAtt.setType("SYNONYM");
                    this.posIncrAtt.setPositionIncrement(posIncr2);
                    return true;
                }
                return false;
            }
            else {
                this.parse();
            }
        }
    }
    
    public void reset() throws IOException {
        super.reset();
        this.captureCount = 0;
        this.finished = false;
        this.inputSkipCount = 0;
        final int n = 0;
        this.nextWrite = n;
        this.nextRead = n;
        for (final PendingInput input : this.futureInputs) {
            input.reset();
        }
        for (final PendingOutputs output : this.futureOutputs) {
            output.reset();
        }
    }
    
    private static class PendingInput
    {
        final CharsRefBuilder term;
        AttributeSource.State state;
        boolean keepOrig;
        boolean matched;
        boolean consumed;
        int startOffset;
        int endOffset;
        
        private PendingInput() {
            this.term = new CharsRefBuilder();
            this.consumed = true;
        }
        
        public void reset() {
            this.state = null;
            this.consumed = true;
            this.keepOrig = false;
            this.matched = false;
        }
    }
    
    private static class PendingOutputs
    {
        CharsRefBuilder[] outputs;
        int[] endOffsets;
        int[] posLengths;
        int upto;
        int count;
        int posIncr;
        int lastEndOffset;
        int lastPosLength;
        
        public PendingOutputs() {
            this.posIncr = 1;
            this.outputs = new CharsRefBuilder[1];
            this.endOffsets = new int[1];
            this.posLengths = new int[1];
        }
        
        public void reset() {
            final int n = 0;
            this.count = n;
            this.upto = n;
            this.posIncr = 1;
        }
        
        public CharsRef pullNext() {
            assert this.upto < this.count;
            this.lastEndOffset = this.endOffsets[this.upto];
            this.lastPosLength = this.posLengths[this.upto];
            final CharsRefBuilder result = this.outputs[this.upto++];
            this.posIncr = 0;
            if (this.upto == this.count) {
                this.reset();
            }
            return result.get();
        }
        
        public int getLastEndOffset() {
            return this.lastEndOffset;
        }
        
        public int getLastPosLength() {
            return this.lastPosLength;
        }
        
        public void add(final char[] output, final int offset, final int len, final int endOffset, final int posLength) {
            if (this.count == this.outputs.length) {
                this.outputs = Arrays.copyOf(this.outputs, ArrayUtil.oversize(1 + this.count, RamUsageEstimator.NUM_BYTES_OBJECT_REF));
            }
            if (this.count == this.endOffsets.length) {
                final int[] next = new int[ArrayUtil.oversize(1 + this.count, 4)];
                System.arraycopy(this.endOffsets, 0, next, 0, this.count);
                this.endOffsets = next;
            }
            if (this.count == this.posLengths.length) {
                final int[] next = new int[ArrayUtil.oversize(1 + this.count, 4)];
                System.arraycopy(this.posLengths, 0, next, 0, this.count);
                this.posLengths = next;
            }
            if (this.outputs[this.count] == null) {
                this.outputs[this.count] = new CharsRefBuilder();
            }
            this.outputs[this.count].copyChars(output, offset, len);
            this.endOffsets[this.count] = endOffset;
            this.posLengths[this.count] = posLength;
            ++this.count;
        }
    }
}
