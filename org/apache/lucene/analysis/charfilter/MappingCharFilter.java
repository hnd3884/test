package org.apache.lucene.analysis.charfilter;

import java.io.IOException;
import org.apache.lucene.util.fst.CharSequenceOutputs;
import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.util.RollingCharBuffer;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.fst.Outputs;

public class MappingCharFilter extends BaseCharFilter
{
    private final Outputs<CharsRef> outputs;
    private final FST<CharsRef> map;
    private final FST.BytesReader fstReader;
    private final RollingCharBuffer buffer;
    private final FST.Arc<CharsRef> scratchArc;
    private final Map<Character, FST.Arc<CharsRef>> cachedRootArcs;
    private CharsRef replacement;
    private int replacementPointer;
    private int inputOff;
    
    public MappingCharFilter(final NormalizeCharMap normMap, final Reader in) {
        super(in);
        this.outputs = (Outputs<CharsRef>)CharSequenceOutputs.getSingleton();
        this.buffer = new RollingCharBuffer();
        this.scratchArc = (FST.Arc<CharsRef>)new FST.Arc();
        this.buffer.reset(in);
        this.map = normMap.map;
        this.cachedRootArcs = normMap.cachedRootArcs;
        if (this.map != null) {
            this.fstReader = this.map.getBytesReader();
        }
        else {
            this.fstReader = null;
        }
    }
    
    public void reset() throws IOException {
        this.input.reset();
        this.buffer.reset(this.input);
        this.replacement = null;
        this.inputOff = 0;
    }
    
    public int read() throws IOException {
        while (this.replacement == null || this.replacementPointer >= this.replacement.length) {
            int lastMatchLen = -1;
            CharsRef lastMatch = null;
            final int firstCH = this.buffer.get(this.inputOff);
            if (firstCH != -1) {
                FST.Arc<CharsRef> arc = this.cachedRootArcs.get((char)firstCH);
                if (arc != null) {
                    if (!FST.targetHasArcs((FST.Arc)arc)) {
                        assert arc.isFinal();
                        lastMatchLen = 1;
                        lastMatch = (CharsRef)arc.output;
                    }
                    else {
                        int lookahead = 0;
                        CharsRef output = (CharsRef)arc.output;
                        while (true) {
                            ++lookahead;
                            if (arc.isFinal()) {
                                lastMatchLen = lookahead;
                                lastMatch = (CharsRef)this.outputs.add((Object)output, arc.nextFinalOutput);
                            }
                            if (!FST.targetHasArcs((FST.Arc)arc)) {
                                break;
                            }
                            final int ch = this.buffer.get(this.inputOff + lookahead);
                            if (ch == -1) {
                                break;
                            }
                            if ((arc = (FST.Arc<CharsRef>)this.map.findTargetArc(ch, (FST.Arc)arc, (FST.Arc)this.scratchArc, this.fstReader)) == null) {
                                break;
                            }
                            output = (CharsRef)this.outputs.add((Object)output, arc.output);
                        }
                    }
                }
            }
            if (lastMatch == null) {
                final int ret = this.buffer.get(this.inputOff);
                if (ret != -1) {
                    ++this.inputOff;
                    this.buffer.freeBefore(this.inputOff);
                }
                return ret;
            }
            this.inputOff += lastMatchLen;
            final int diff = lastMatchLen - lastMatch.length;
            if (diff != 0) {
                final int prevCumulativeDiff = this.getLastCumulativeDiff();
                if (diff > 0) {
                    this.addOffCorrectMap(this.inputOff - diff - prevCumulativeDiff, prevCumulativeDiff + diff);
                }
                else {
                    final int outputStart = this.inputOff - prevCumulativeDiff;
                    for (int extraIDX = 0; extraIDX < -diff; ++extraIDX) {
                        this.addOffCorrectMap(outputStart + extraIDX, prevCumulativeDiff - extraIDX - 1);
                    }
                }
            }
            this.replacement = lastMatch;
            this.replacementPointer = 0;
        }
        return this.replacement.chars[this.replacement.offset + this.replacementPointer++];
    }
    
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        int numRead = 0;
        for (int i = off; i < off + len; ++i) {
            final int c = this.read();
            if (c == -1) {
                break;
            }
            cbuf[i] = (char)c;
            ++numRead;
        }
        return (numRead == 0) ? -1 : numRead;
    }
}
