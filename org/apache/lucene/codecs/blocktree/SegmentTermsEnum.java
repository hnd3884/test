package org.apache.lucene.codecs.blocktree;

import org.apache.lucene.codecs.BlockTermState;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.util.fst.Util;
import java.io.PrintStream;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;
import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.index.TermsEnum;

final class SegmentTermsEnum extends TermsEnum
{
    IndexInput in;
    private SegmentTermsEnumFrame[] stack;
    private final SegmentTermsEnumFrame staticFrame;
    SegmentTermsEnumFrame currentFrame;
    boolean termExists;
    final FieldReader fr;
    private int targetBeforeCurrentLength;
    private final ByteArrayDataInput scratchReader;
    private int validIndexPrefix;
    private boolean eof;
    final BytesRefBuilder term;
    private final FST.BytesReader fstReader;
    private FST.Arc<BytesRef>[] arcs;
    
    public SegmentTermsEnum(final FieldReader fr) throws IOException {
        this.scratchReader = new ByteArrayDataInput();
        this.term = new BytesRefBuilder();
        this.arcs = new FST.Arc[1];
        this.fr = fr;
        this.stack = new SegmentTermsEnumFrame[0];
        this.staticFrame = new SegmentTermsEnumFrame(this, -1);
        if (fr.index == null) {
            this.fstReader = null;
        }
        else {
            this.fstReader = fr.index.getBytesReader();
        }
        for (int arcIdx = 0; arcIdx < this.arcs.length; ++arcIdx) {
            this.arcs[arcIdx] = new FST.Arc<BytesRef>();
        }
        this.currentFrame = this.staticFrame;
        if (fr.index != null) {
            final FST.Arc<BytesRef> arc = fr.index.getFirstArc(this.arcs[0]);
            assert arc.isFinal();
        }
        else {
            final FST.Arc<BytesRef> arc = null;
        }
        this.validIndexPrefix = 0;
    }
    
    void initIndexInput() {
        if (this.in == null) {
            this.in = this.fr.parent.termsIn.clone();
        }
    }
    
    public Stats computeBlockStats() throws IOException {
        final Stats stats = new Stats(this.fr.parent.segment, this.fr.fieldInfo.name);
        if (this.fr.index != null) {
            stats.indexNumBytes = this.fr.index.ramBytesUsed();
        }
        this.currentFrame = this.staticFrame;
        FST.Arc<BytesRef> arc;
        if (this.fr.index != null) {
            arc = this.fr.index.getFirstArc(this.arcs[0]);
            assert arc.isFinal();
        }
        else {
            arc = null;
        }
        this.currentFrame = this.pushFrame(arc, this.fr.rootCode, 0);
        this.currentFrame.fpOrig = this.currentFrame.fp;
        this.currentFrame.loadBlock();
        this.validIndexPrefix = 0;
        stats.startBlock(this.currentFrame, !this.currentFrame.isLastInFloor);
        while (true) {
            if (this.currentFrame.nextEnt == this.currentFrame.entCount) {
                stats.endBlock(this.currentFrame);
                if (!this.currentFrame.isLastInFloor) {
                    this.currentFrame.loadNextFloorBlock();
                    stats.startBlock(this.currentFrame, true);
                }
                else {
                    if (this.currentFrame.ord == 0) {
                        stats.finish();
                        this.currentFrame = this.staticFrame;
                        if (this.fr.index != null) {
                            arc = this.fr.index.getFirstArc(this.arcs[0]);
                            assert arc.isFinal();
                        }
                        else {
                            arc = null;
                        }
                        (this.currentFrame = this.pushFrame(arc, this.fr.rootCode, 0)).rewind();
                        this.currentFrame.loadBlock();
                        this.validIndexPrefix = 0;
                        this.term.clear();
                        return stats;
                    }
                    final long lastFP = this.currentFrame.fpOrig;
                    this.currentFrame = this.stack[this.currentFrame.ord - 1];
                    assert lastFP == this.currentFrame.lastSubFP;
                    continue;
                }
            }
            while (this.currentFrame.next()) {
                this.currentFrame = this.pushFrame(null, this.currentFrame.lastSubFP, this.term.length());
                this.currentFrame.fpOrig = this.currentFrame.fp;
                this.currentFrame.loadBlock();
                stats.startBlock(this.currentFrame, !this.currentFrame.isLastInFloor);
            }
            stats.term(this.term.get());
        }
    }
    
    private SegmentTermsEnumFrame getFrame(final int ord) throws IOException {
        if (ord >= this.stack.length) {
            final SegmentTermsEnumFrame[] next = new SegmentTermsEnumFrame[ArrayUtil.oversize(1 + ord, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
            System.arraycopy(this.stack, 0, next, 0, this.stack.length);
            for (int stackOrd = this.stack.length; stackOrd < next.length; ++stackOrd) {
                next[stackOrd] = new SegmentTermsEnumFrame(this, stackOrd);
            }
            this.stack = next;
        }
        assert this.stack[ord].ord == ord;
        return this.stack[ord];
    }
    
    private FST.Arc<BytesRef> getArc(final int ord) {
        if (ord >= this.arcs.length) {
            final FST.Arc<BytesRef>[] next = new FST.Arc[ArrayUtil.oversize(1 + ord, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
            System.arraycopy(this.arcs, 0, next, 0, this.arcs.length);
            for (int arcOrd = this.arcs.length; arcOrd < next.length; ++arcOrd) {
                next[arcOrd] = new FST.Arc<BytesRef>();
            }
            this.arcs = next;
        }
        return this.arcs[ord];
    }
    
    SegmentTermsEnumFrame pushFrame(final FST.Arc<BytesRef> arc, final BytesRef frameData, final int length) throws IOException {
        this.scratchReader.reset(frameData.bytes, frameData.offset, frameData.length);
        final long code = this.scratchReader.readVLong();
        final long fpSeek = code >>> 2;
        final SegmentTermsEnumFrame f = this.getFrame(1 + this.currentFrame.ord);
        f.hasTerms = ((code & 0x2L) != 0x0L);
        f.hasTermsOrig = f.hasTerms;
        f.isFloor = ((code & 0x1L) != 0x0L);
        if (f.isFloor) {
            f.setFloorData(this.scratchReader, frameData);
        }
        this.pushFrame(arc, fpSeek, length);
        return f;
    }
    
    SegmentTermsEnumFrame pushFrame(final FST.Arc<BytesRef> arc, final long fp, final int length) throws IOException {
        final SegmentTermsEnumFrame f = this.getFrame(1 + this.currentFrame.ord);
        f.arc = arc;
        if (f.fpOrig == fp && f.nextEnt != -1) {
            if (f.ord > this.targetBeforeCurrentLength) {
                f.rewind();
            }
            assert length == f.prefix;
        }
        else {
            f.nextEnt = -1;
            f.prefix = length;
            f.state.termBlockOrd = 0;
            final SegmentTermsEnumFrame segmentTermsEnumFrame = f;
            f.fp = fp;
            segmentTermsEnumFrame.fpOrig = fp;
            f.lastSubFP = -1L;
        }
        return f;
    }
    
    private boolean clearEOF() {
        this.eof = false;
        return true;
    }
    
    private boolean setEOF() {
        return this.eof = true;
    }
    
    @Override
    public boolean seekExact(final BytesRef target) throws IOException {
        if (this.fr.index == null) {
            throw new IllegalStateException("terms index was not loaded");
        }
        this.term.grow(1 + target.length);
        assert this.clearEOF();
        this.targetBeforeCurrentLength = this.currentFrame.ord;
        FST.Arc<BytesRef> arc;
        BytesRef output;
        int targetUpto;
        if (this.currentFrame != this.staticFrame) {
            arc = this.arcs[0];
            assert arc.isFinal();
            output = arc.output;
            targetUpto = 0;
            SegmentTermsEnumFrame lastFrame = this.stack[0];
            assert this.validIndexPrefix <= this.term.length();
            final int targetLimit = Math.min(target.length, this.validIndexPrefix);
            int cmp = 0;
            while (targetUpto < targetLimit) {
                cmp = (this.term.byteAt(targetUpto) & 0xFF) - (target.bytes[target.offset + targetUpto] & 0xFF);
                if (cmp != 0) {
                    break;
                }
                arc = this.arcs[1 + targetUpto];
                assert arc.label == (target.bytes[target.offset + targetUpto] & 0xFF) : "arc.label=" + (char)arc.label + " targetLabel=" + (char)(target.bytes[target.offset + targetUpto] & 0xFF);
                if (arc.output != BlockTreeTermsReader.NO_OUTPUT) {
                    output = BlockTreeTermsReader.FST_OUTPUTS.add(output, arc.output);
                }
                if (arc.isFinal()) {
                    lastFrame = this.stack[1 + lastFrame.ord];
                }
                ++targetUpto;
            }
            if (cmp == 0) {
                final int targetUptoMid = targetUpto;
                for (int targetLimit2 = Math.min(target.length, this.term.length()); targetUpto < targetLimit2; ++targetUpto) {
                    cmp = (this.term.byteAt(targetUpto) & 0xFF) - (target.bytes[target.offset + targetUpto] & 0xFF);
                    if (cmp != 0) {
                        break;
                    }
                }
                if (cmp == 0) {
                    cmp = this.term.length() - target.length;
                }
                targetUpto = targetUptoMid;
            }
            if (cmp < 0) {
                this.currentFrame = lastFrame;
            }
            else if (cmp > 0) {
                this.targetBeforeCurrentLength = lastFrame.ord;
                (this.currentFrame = lastFrame).rewind();
            }
            else {
                assert this.term.length() == target.length;
                if (this.termExists) {
                    return true;
                }
            }
        }
        else {
            this.targetBeforeCurrentLength = -1;
            arc = this.fr.index.getFirstArc(this.arcs[0]);
            assert arc.isFinal();
            assert arc.output != null;
            output = arc.output;
            this.currentFrame = this.staticFrame;
            targetUpto = 0;
            this.currentFrame = this.pushFrame(arc, BlockTreeTermsReader.FST_OUTPUTS.add(output, arc.nextFinalOutput), 0);
        }
        while (targetUpto < target.length) {
            final int targetLabel = target.bytes[target.offset + targetUpto] & 0xFF;
            final FST.Arc<BytesRef> nextArc = this.fr.index.findTargetArc(targetLabel, arc, this.getArc(1 + targetUpto), this.fstReader);
            if (nextArc == null) {
                this.validIndexPrefix = this.currentFrame.prefix;
                this.currentFrame.scanToFloorFrame(target);
                if (!this.currentFrame.hasTerms) {
                    this.termExists = false;
                    this.term.setByteAt(targetUpto, (byte)targetLabel);
                    this.term.setLength(1 + targetUpto);
                    return false;
                }
                this.currentFrame.loadBlock();
                final SeekStatus result = this.currentFrame.scanToTerm(target, true);
                return result == SeekStatus.FOUND;
            }
            else {
                arc = nextArc;
                this.term.setByteAt(targetUpto, (byte)targetLabel);
                assert arc.output != null;
                if (arc.output != BlockTreeTermsReader.NO_OUTPUT) {
                    output = BlockTreeTermsReader.FST_OUTPUTS.add(output, arc.output);
                }
                ++targetUpto;
                if (!arc.isFinal()) {
                    continue;
                }
                this.currentFrame = this.pushFrame(arc, BlockTreeTermsReader.FST_OUTPUTS.add(output, arc.nextFinalOutput), targetUpto);
            }
        }
        this.validIndexPrefix = this.currentFrame.prefix;
        this.currentFrame.scanToFloorFrame(target);
        if (!this.currentFrame.hasTerms) {
            this.termExists = false;
            this.term.setLength(targetUpto);
            return false;
        }
        this.currentFrame.loadBlock();
        final SeekStatus result2 = this.currentFrame.scanToTerm(target, true);
        return result2 == SeekStatus.FOUND;
    }
    
    @Override
    public SeekStatus seekCeil(final BytesRef target) throws IOException {
        if (this.fr.index == null) {
            throw new IllegalStateException("terms index was not loaded");
        }
        this.term.grow(1 + target.length);
        assert this.clearEOF();
        this.targetBeforeCurrentLength = this.currentFrame.ord;
        FST.Arc<BytesRef> arc;
        BytesRef output;
        int targetUpto;
        if (this.currentFrame != this.staticFrame) {
            arc = this.arcs[0];
            assert arc.isFinal();
            output = arc.output;
            targetUpto = 0;
            SegmentTermsEnumFrame lastFrame = this.stack[0];
            assert this.validIndexPrefix <= this.term.length();
            final int targetLimit = Math.min(target.length, this.validIndexPrefix);
            int cmp = 0;
            while (targetUpto < targetLimit) {
                cmp = (this.term.byteAt(targetUpto) & 0xFF) - (target.bytes[target.offset + targetUpto] & 0xFF);
                if (cmp != 0) {
                    break;
                }
                arc = this.arcs[1 + targetUpto];
                assert arc.label == (target.bytes[target.offset + targetUpto] & 0xFF) : "arc.label=" + (char)arc.label + " targetLabel=" + (char)(target.bytes[target.offset + targetUpto] & 0xFF);
                if (arc.output != BlockTreeTermsReader.NO_OUTPUT) {
                    output = BlockTreeTermsReader.FST_OUTPUTS.add(output, arc.output);
                }
                if (arc.isFinal()) {
                    lastFrame = this.stack[1 + lastFrame.ord];
                }
                ++targetUpto;
            }
            if (cmp == 0) {
                final int targetUptoMid = targetUpto;
                for (int targetLimit2 = Math.min(target.length, this.term.length()); targetUpto < targetLimit2; ++targetUpto) {
                    cmp = (this.term.byteAt(targetUpto) & 0xFF) - (target.bytes[target.offset + targetUpto] & 0xFF);
                    if (cmp != 0) {
                        break;
                    }
                }
                if (cmp == 0) {
                    cmp = this.term.length() - target.length;
                }
                targetUpto = targetUptoMid;
            }
            if (cmp < 0) {
                this.currentFrame = lastFrame;
            }
            else if (cmp > 0) {
                this.targetBeforeCurrentLength = 0;
                (this.currentFrame = lastFrame).rewind();
            }
            else {
                assert this.term.length() == target.length;
                if (this.termExists) {
                    return SeekStatus.FOUND;
                }
            }
        }
        else {
            this.targetBeforeCurrentLength = -1;
            arc = this.fr.index.getFirstArc(this.arcs[0]);
            assert arc.isFinal();
            assert arc.output != null;
            output = arc.output;
            this.currentFrame = this.staticFrame;
            targetUpto = 0;
            this.currentFrame = this.pushFrame(arc, BlockTreeTermsReader.FST_OUTPUTS.add(output, arc.nextFinalOutput), 0);
        }
        while (targetUpto < target.length) {
            final int targetLabel = target.bytes[target.offset + targetUpto] & 0xFF;
            final FST.Arc<BytesRef> nextArc = this.fr.index.findTargetArc(targetLabel, arc, this.getArc(1 + targetUpto), this.fstReader);
            if (nextArc == null) {
                this.validIndexPrefix = this.currentFrame.prefix;
                this.currentFrame.scanToFloorFrame(target);
                this.currentFrame.loadBlock();
                final SeekStatus result = this.currentFrame.scanToTerm(target, false);
                if (result != SeekStatus.END) {
                    return result;
                }
                this.term.copyBytes(target);
                this.termExists = false;
                if (this.next() != null) {
                    return SeekStatus.NOT_FOUND;
                }
                return SeekStatus.END;
            }
            else {
                this.term.setByteAt(targetUpto, (byte)targetLabel);
                arc = nextArc;
                assert arc.output != null;
                if (arc.output != BlockTreeTermsReader.NO_OUTPUT) {
                    output = BlockTreeTermsReader.FST_OUTPUTS.add(output, arc.output);
                }
                ++targetUpto;
                if (!arc.isFinal()) {
                    continue;
                }
                this.currentFrame = this.pushFrame(arc, BlockTreeTermsReader.FST_OUTPUTS.add(output, arc.nextFinalOutput), targetUpto);
            }
        }
        this.validIndexPrefix = this.currentFrame.prefix;
        this.currentFrame.scanToFloorFrame(target);
        this.currentFrame.loadBlock();
        final SeekStatus result2 = this.currentFrame.scanToTerm(target, false);
        if (result2 != SeekStatus.END) {
            return result2;
        }
        this.term.copyBytes(target);
        this.termExists = false;
        if (this.next() != null) {
            return SeekStatus.NOT_FOUND;
        }
        return SeekStatus.END;
    }
    
    private void printSeekState(final PrintStream out) throws IOException {
        if (this.currentFrame == this.staticFrame) {
            out.println("  no prior seek");
        }
        else {
            out.println("  prior seek state:");
            int ord = 0;
            boolean isSeekFrame = true;
            while (true) {
                final SegmentTermsEnumFrame f = this.getFrame(ord);
                assert f != null;
                final BytesRef prefix = new BytesRef(this.term.get().bytes, 0, f.prefix);
                if (f.nextEnt == -1) {
                    out.println("    frame " + (isSeekFrame ? "(seek)" : "(next)") + " ord=" + ord + " fp=" + f.fp + (f.isFloor ? (" (fpOrig=" + f.fpOrig + ")") : "") + " prefixLen=" + f.prefix + " prefix=" + prefix + ((f.nextEnt == -1) ? "" : (" (of " + f.entCount + ")")) + " hasTerms=" + f.hasTerms + " isFloor=" + f.isFloor + " code=" + ((f.fp << 2) + (f.hasTerms ? 2 : 0) + (long)(f.isFloor ? 1 : 0)) + " isLastInFloor=" + f.isLastInFloor + " mdUpto=" + f.metaDataUpto + " tbOrd=" + f.getTermBlockOrd());
                }
                else {
                    out.println("    frame " + (isSeekFrame ? "(seek, loaded)" : "(next, loaded)") + " ord=" + ord + " fp=" + f.fp + (f.isFloor ? (" (fpOrig=" + f.fpOrig + ")") : "") + " prefixLen=" + f.prefix + " prefix=" + prefix + " nextEnt=" + f.nextEnt + ((f.nextEnt == -1) ? "" : (" (of " + f.entCount + ")")) + " hasTerms=" + f.hasTerms + " isFloor=" + f.isFloor + " code=" + ((f.fp << 2) + (f.hasTerms ? 2 : 0) + (long)(f.isFloor ? 1 : 0)) + " lastSubFP=" + f.lastSubFP + " isLastInFloor=" + f.isLastInFloor + " mdUpto=" + f.metaDataUpto + " tbOrd=" + f.getTermBlockOrd());
                }
                if (this.fr.index != null) {
                    assert f.arc != null : "isSeekFrame=" + isSeekFrame + " f.arc=" + f.arc;
                    if (f.prefix > 0 && isSeekFrame && f.arc.label != (this.term.byteAt(f.prefix - 1) & 0xFF)) {
                        out.println("      broken seek state: arc.label=" + (char)f.arc.label + " vs term byte=" + (char)(this.term.byteAt(f.prefix - 1) & 0xFF));
                        throw new RuntimeException("seek state is broken");
                    }
                    final BytesRef output = Util.get(this.fr.index, prefix);
                    if (output == null) {
                        out.println("      broken seek state: prefix is not final in index");
                        throw new RuntimeException("seek state is broken");
                    }
                    if (isSeekFrame && !f.isFloor) {
                        final ByteArrayDataInput reader = new ByteArrayDataInput(output.bytes, output.offset, output.length);
                        final long codeOrig = reader.readVLong();
                        final long code = f.fp << 2 | (long)(f.hasTerms ? 2 : 0) | (long)(f.isFloor ? 1 : 0);
                        if (codeOrig != code) {
                            out.println("      broken seek state: output code=" + codeOrig + " doesn't match frame code=" + code);
                            throw new RuntimeException("seek state is broken");
                        }
                    }
                }
                if (f == this.currentFrame) {
                    break;
                }
                if (f.prefix == this.validIndexPrefix) {
                    isSeekFrame = false;
                }
                ++ord;
            }
        }
    }
    
    @Override
    public BytesRef next() throws IOException {
        if (this.in == null) {
            FST.Arc<BytesRef> arc;
            if (this.fr.index != null) {
                arc = this.fr.index.getFirstArc(this.arcs[0]);
                assert arc.isFinal();
            }
            else {
                arc = null;
            }
            (this.currentFrame = this.pushFrame(arc, this.fr.rootCode, 0)).loadBlock();
        }
        this.targetBeforeCurrentLength = this.currentFrame.ord;
        assert !this.eof;
        if (this.currentFrame == this.staticFrame) {
            final boolean result = this.seekExact(this.term.get());
            assert result;
        }
        while (this.currentFrame.nextEnt == this.currentFrame.entCount) {
            if (!this.currentFrame.isLastInFloor) {
                this.currentFrame.loadNextFloorBlock();
                break;
            }
            if (this.currentFrame.ord == 0) {
                assert this.setEOF();
                this.term.clear();
                this.validIndexPrefix = 0;
                this.currentFrame.rewind();
                this.termExists = false;
                return null;
            }
            else {
                final long lastFP = this.currentFrame.fpOrig;
                this.currentFrame = this.stack[this.currentFrame.ord - 1];
                if (this.currentFrame.nextEnt == -1 || this.currentFrame.lastSubFP != lastFP) {
                    this.currentFrame.scanToFloorFrame(this.term.get());
                    this.currentFrame.loadBlock();
                    this.currentFrame.scanToSubBlock(lastFP);
                }
                this.validIndexPrefix = Math.min(this.validIndexPrefix, this.currentFrame.prefix);
            }
        }
        while (this.currentFrame.next()) {
            (this.currentFrame = this.pushFrame(null, this.currentFrame.lastSubFP, this.term.length())).loadBlock();
        }
        return this.term.get();
    }
    
    @Override
    public BytesRef term() {
        assert !this.eof;
        return this.term.get();
    }
    
    @Override
    public int docFreq() throws IOException {
        assert !this.eof;
        this.currentFrame.decodeMetaData();
        return this.currentFrame.state.docFreq;
    }
    
    @Override
    public long totalTermFreq() throws IOException {
        assert !this.eof;
        this.currentFrame.decodeMetaData();
        return this.currentFrame.state.totalTermFreq;
    }
    
    @Override
    public PostingsEnum postings(final PostingsEnum reuse, final int flags) throws IOException {
        assert !this.eof;
        this.currentFrame.decodeMetaData();
        return this.fr.parent.postingsReader.postings(this.fr.fieldInfo, this.currentFrame.state, reuse, flags);
    }
    
    @Override
    public void seekExact(final BytesRef target, final TermState otherState) {
        assert this.clearEOF();
        if (target.compareTo(this.term.get()) != 0 || !this.termExists) {
            assert otherState != null && otherState instanceof BlockTermState;
            this.currentFrame = this.staticFrame;
            this.currentFrame.state.copyFrom(otherState);
            this.term.copyBytes(target);
            this.currentFrame.metaDataUpto = this.currentFrame.getTermBlockOrd();
            assert this.currentFrame.metaDataUpto > 0;
            this.validIndexPrefix = 0;
        }
    }
    
    @Override
    public TermState termState() throws IOException {
        assert !this.eof;
        this.currentFrame.decodeMetaData();
        final TermState ts = this.currentFrame.state.clone();
        return ts;
    }
    
    @Override
    public void seekExact(final long ord) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long ord() {
        throw new UnsupportedOperationException();
    }
}
