package org.apache.lucene.codecs.blocktree;

import org.apache.lucene.util.fst.ByteSequenceOutputs;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.index.TermState;
import java.io.IOException;
import org.apache.lucene.util.automaton.Transition;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.RunAutomaton;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.index.TermsEnum;

final class IntersectTermsEnum extends TermsEnum
{
    final IndexInput in;
    static final Outputs<BytesRef> fstOutputs;
    IntersectTermsEnumFrame[] stack;
    private FST.Arc<BytesRef>[] arcs;
    final RunAutomaton runAutomaton;
    final Automaton automaton;
    final BytesRef commonSuffix;
    private IntersectTermsEnumFrame currentFrame;
    private Transition currentTransition;
    private final BytesRef term;
    private final FST.BytesReader fstReader;
    private final boolean allowAutoPrefixTerms;
    final FieldReader fr;
    private final int sinkState;
    private BytesRef savedStartTerm;
    private boolean useAutoPrefixTerm;
    private final Transition scratchTransition;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    public IntersectTermsEnum(final FieldReader fr, final Automaton automaton, final RunAutomaton runAutomaton, final BytesRef commonSuffix, final BytesRef startTerm, final int sinkState) throws IOException {
        this.arcs = new FST.Arc[5];
        this.term = new BytesRef();
        this.scratchTransition = new Transition();
        this.fr = fr;
        this.sinkState = sinkState;
        assert automaton != null;
        assert runAutomaton != null;
        this.runAutomaton = runAutomaton;
        this.allowAutoPrefixTerms = (sinkState != -1);
        this.automaton = automaton;
        this.commonSuffix = commonSuffix;
        this.in = fr.parent.termsIn.clone();
        this.stack = new IntersectTermsEnumFrame[5];
        for (int idx = 0; idx < this.stack.length; ++idx) {
            this.stack[idx] = new IntersectTermsEnumFrame(this, idx);
        }
        for (int arcIdx = 0; arcIdx < this.arcs.length; ++arcIdx) {
            this.arcs[arcIdx] = new FST.Arc<BytesRef>();
        }
        if (fr.index == null) {
            this.fstReader = null;
        }
        else {
            this.fstReader = fr.index.getBytesReader();
        }
        final FST.Arc<BytesRef> arc = fr.index.getFirstArc(this.arcs[0]);
        assert arc.isFinal();
        final IntersectTermsEnumFrame intersectTermsEnumFrame2;
        final IntersectTermsEnumFrame intersectTermsEnumFrame;
        final IntersectTermsEnumFrame f = intersectTermsEnumFrame = (intersectTermsEnumFrame2 = this.stack[0]);
        final long rootBlockFP = fr.rootBlockFP;
        intersectTermsEnumFrame.fpOrig = rootBlockFP;
        intersectTermsEnumFrame2.fp = rootBlockFP;
        f.prefix = 0;
        f.setState(runAutomaton.getInitialState());
        f.arc = arc;
        f.outputPrefix = arc.output;
        f.load(fr.rootCode);
        assert this.setSavedStartTerm(startTerm);
        this.currentFrame = f;
        if (startTerm != null) {
            this.seekToStartTerm(startTerm);
        }
        this.currentTransition = this.currentFrame.transition;
    }
    
    private boolean setSavedStartTerm(final BytesRef startTerm) {
        this.savedStartTerm = ((startTerm == null) ? null : BytesRef.deepCopyOf(startTerm));
        return true;
    }
    
    @Override
    public TermState termState() throws IOException {
        this.currentFrame.decodeMetaData();
        return this.currentFrame.termState.clone();
    }
    
    private IntersectTermsEnumFrame getFrame(final int ord) throws IOException {
        if (ord >= this.stack.length) {
            final IntersectTermsEnumFrame[] next = new IntersectTermsEnumFrame[ArrayUtil.oversize(1 + ord, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
            System.arraycopy(this.stack, 0, next, 0, this.stack.length);
            for (int stackOrd = this.stack.length; stackOrd < next.length; ++stackOrd) {
                next[stackOrd] = new IntersectTermsEnumFrame(this, stackOrd);
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
    
    private IntersectTermsEnumFrame pushFrame(final int state) throws IOException {
        assert this.currentFrame != null;
        final IntersectTermsEnumFrame frame;
        final IntersectTermsEnumFrame intersectTermsEnumFrame;
        final IntersectTermsEnumFrame f = intersectTermsEnumFrame = (frame = this.getFrame((this.currentFrame == null) ? 0 : (1 + this.currentFrame.ord)));
        final long lastSubFP = this.currentFrame.lastSubFP;
        intersectTermsEnumFrame.fpOrig = lastSubFP;
        frame.fp = lastSubFP;
        f.prefix = this.currentFrame.prefix + this.currentFrame.suffix;
        f.setState(state);
        FST.Arc<BytesRef> arc = this.currentFrame.arc;
        int idx = this.currentFrame.prefix;
        assert this.currentFrame.suffix > 0;
        BytesRef output = this.currentFrame.outputPrefix;
        while (idx < f.prefix) {
            final int target = this.term.bytes[idx] & 0xFF;
            arc = this.fr.index.findTargetArc(target, arc, this.getArc(1 + idx), this.fstReader);
            assert arc != null;
            output = IntersectTermsEnum.fstOutputs.add(output, arc.output);
            ++idx;
        }
        f.arc = arc;
        f.outputPrefix = output;
        assert arc.isFinal();
        f.load(IntersectTermsEnum.fstOutputs.add(output, arc.nextFinalOutput));
        return f;
    }
    
    @Override
    public BytesRef term() {
        return this.term;
    }
    
    @Override
    public int docFreq() throws IOException {
        this.currentFrame.decodeMetaData();
        return this.currentFrame.termState.docFreq;
    }
    
    @Override
    public long totalTermFreq() throws IOException {
        this.currentFrame.decodeMetaData();
        return this.currentFrame.termState.totalTermFreq;
    }
    
    @Override
    public PostingsEnum postings(final PostingsEnum reuse, final int flags) throws IOException {
        this.currentFrame.decodeMetaData();
        return this.fr.parent.postingsReader.postings(this.fr.fieldInfo, this.currentFrame.termState, reuse, flags);
    }
    
    private int getState() {
        int state = this.currentFrame.state;
        for (int idx = 0; idx < this.currentFrame.suffix; ++idx) {
            state = this.runAutomaton.step(state, this.currentFrame.suffixBytes[this.currentFrame.startBytePos + idx] & 0xFF);
            assert state != -1;
        }
        return state;
    }
    
    private void seekToStartTerm(final BytesRef target) throws IOException {
        assert this.currentFrame.ord == 0;
        if (this.term.length < target.length) {
            this.term.bytes = ArrayUtil.grow(this.term.bytes, target.length);
        }
        final FST.Arc<BytesRef> arc = this.arcs[0];
        assert arc == this.currentFrame.arc;
        for (int idx = 0; idx <= target.length; ++idx) {
            while (true) {
                final int savNextEnt = this.currentFrame.nextEnt;
                final int savePos = this.currentFrame.suffixesReader.getPosition();
                final int saveStartBytePos = this.currentFrame.startBytePos;
                final int saveSuffix = this.currentFrame.suffix;
                final long saveLastSubFP = this.currentFrame.lastSubFP;
                final int saveTermBlockOrd = this.currentFrame.termState.termBlockOrd;
                final boolean saveIsAutoPrefixTerm = this.currentFrame.isAutoPrefixTerm;
                final boolean isSubBlock = this.currentFrame.next();
                this.term.length = this.currentFrame.prefix + this.currentFrame.suffix;
                if (this.term.bytes.length < this.term.length) {
                    this.term.bytes = ArrayUtil.grow(this.term.bytes, this.term.length);
                }
                System.arraycopy(this.currentFrame.suffixBytes, this.currentFrame.startBytePos, this.term.bytes, this.currentFrame.prefix, this.currentFrame.suffix);
                if (isSubBlock && StringHelper.startsWith(target, this.term)) {
                    this.currentFrame = this.pushFrame(this.getState());
                    break;
                }
                final int cmp = this.term.compareTo(target);
                if (cmp < 0) {
                    if (this.currentFrame.nextEnt != this.currentFrame.entCount) {
                        continue;
                    }
                    if (this.currentFrame.isLastInFloor) {
                        return;
                    }
                    this.currentFrame.loadNextFloorBlock();
                }
                else if (cmp == 0) {
                    if (!this.allowAutoPrefixTerms && this.currentFrame.isAutoPrefixTerm) {
                        continue;
                    }
                    return;
                }
                else {
                    if (this.allowAutoPrefixTerms || !this.currentFrame.isAutoPrefixTerm) {
                        this.currentFrame.nextEnt = savNextEnt;
                        this.currentFrame.lastSubFP = saveLastSubFP;
                        this.currentFrame.startBytePos = saveStartBytePos;
                        this.currentFrame.suffix = saveSuffix;
                        this.currentFrame.suffixesReader.setPosition(savePos);
                        this.currentFrame.termState.termBlockOrd = saveTermBlockOrd;
                        this.currentFrame.isAutoPrefixTerm = saveIsAutoPrefixTerm;
                        System.arraycopy(this.currentFrame.suffixBytes, this.currentFrame.startBytePos, this.term.bytes, this.currentFrame.prefix, this.currentFrame.suffix);
                        this.term.length = this.currentFrame.prefix + this.currentFrame.suffix;
                        return;
                    }
                    continue;
                }
            }
        }
        assert false;
    }
    
    private boolean popPushNext() throws IOException {
        while (this.currentFrame.nextEnt == this.currentFrame.entCount) {
            if (!this.currentFrame.isLastInFloor) {
                this.currentFrame.loadNextFloorBlock();
                break;
            }
            if (this.currentFrame.ord == 0) {
                throw NoMoreTermsException.INSTANCE;
            }
            final long lastFP = this.currentFrame.fpOrig;
            this.currentFrame = this.stack[this.currentFrame.ord - 1];
            this.currentTransition = this.currentFrame.transition;
            assert this.currentFrame.lastSubFP == lastFP;
        }
        return this.currentFrame.next();
    }
    
    private boolean skipPastLastAutoPrefixTerm() throws IOException {
        assert this.currentFrame.isAutoPrefixTerm;
        this.useAutoPrefixTerm = false;
        this.currentFrame.termState.isRealTerm = true;
        final int floorSuffixLeadEnd = this.currentFrame.floorSuffixLeadEnd;
        boolean isSubBlock = false;
        Label_0568: {
            if (floorSuffixLeadEnd == -1) {
                final int prefix = this.currentFrame.prefix;
                final int suffix = this.currentFrame.suffix;
                if (suffix == 0) {
                    if (this.currentFrame.ord == 0) {
                        throw NoMoreTermsException.INSTANCE;
                    }
                    this.currentFrame = this.stack[this.currentFrame.ord - 1];
                    this.currentTransition = this.currentFrame.transition;
                    return this.popPushNext();
                }
                else {
                    while (true) {
                        if (this.currentFrame.nextEnt == this.currentFrame.entCount) {
                            if (!this.currentFrame.isLastInFloor) {
                                this.currentFrame.loadNextFloorBlock();
                            }
                            else {
                                if (this.currentFrame.ord == 0) {
                                    throw NoMoreTermsException.INSTANCE;
                                }
                                this.currentFrame = this.stack[this.currentFrame.ord - 1];
                                this.currentTransition = this.currentFrame.transition;
                                return this.popPushNext();
                            }
                        }
                        isSubBlock = this.currentFrame.next();
                        for (int i = 0; i < suffix; ++i) {
                            if (this.term.bytes[prefix + i] != this.currentFrame.suffixBytes[this.currentFrame.startBytePos + i]) {
                                break Label_0568;
                            }
                        }
                    }
                }
            }
            else {
                int prefix = this.currentFrame.prefix;
                int suffix = this.currentFrame.suffix;
                if (this.currentFrame.floorSuffixLeadStart == -1) {
                    ++suffix;
                }
                if (suffix == 0) {
                    if (this.currentFrame.ord == 0) {
                        throw NoMoreTermsException.INSTANCE;
                    }
                    this.currentFrame = this.stack[this.currentFrame.ord - 1];
                    this.currentTransition = this.currentFrame.transition;
                    prefix = this.currentFrame.prefix;
                    suffix = this.term.length - this.currentFrame.prefix;
                }
                do {
                    if (this.currentFrame.nextEnt == this.currentFrame.entCount) {
                        if (!this.currentFrame.isLastInFloor) {
                            this.currentFrame.loadNextFloorBlock();
                        }
                        else {
                            if (this.currentFrame.ord == 0) {
                                throw NoMoreTermsException.INSTANCE;
                            }
                            this.currentFrame = this.stack[this.currentFrame.ord - 1];
                            this.currentTransition = this.currentFrame.transition;
                            return this.popPushNext();
                        }
                    }
                    isSubBlock = this.currentFrame.next();
                    for (int i = 0; i < suffix - 1; ++i) {
                        if (this.term.bytes[prefix + i] != this.currentFrame.suffixBytes[this.currentFrame.startBytePos + i]) {
                            break Label_0568;
                        }
                    }
                } while (this.currentFrame.suffix < suffix || (this.currentFrame.suffixBytes[this.currentFrame.startBytePos + suffix - 1] & 0xFF) <= floorSuffixLeadEnd);
            }
        }
        return isSubBlock;
    }
    
    @Override
    public BytesRef next() throws IOException {
        try {
            return this._next();
        }
        catch (final NoMoreTermsException eoi) {
            this.currentFrame = null;
            return null;
        }
    }
    
    private BytesRef _next() throws IOException {
        boolean isSubBlock;
        if (this.useAutoPrefixTerm) {
            isSubBlock = this.skipPastLastAutoPrefixTerm();
            assert !this.useAutoPrefixTerm;
        }
        else {
            isSubBlock = this.popPushNext();
        }
    Label_0038:
        while (IntersectTermsEnum.$assertionsDisabled || this.currentFrame.transition == this.currentTransition) {
            int lastState;
            int state;
            if (this.currentFrame.suffix != 0) {
                final byte[] suffixBytes = this.currentFrame.suffixBytes;
                final int label = suffixBytes[this.currentFrame.startBytePos] & 0xFF;
                if (label < this.currentTransition.min) {
                    final int minTrans = this.currentTransition.min;
                    while (this.currentFrame.nextEnt < this.currentFrame.entCount) {
                        isSubBlock = this.currentFrame.next();
                        if ((suffixBytes[this.currentFrame.startBytePos] & 0xFF) >= minTrans) {
                            continue Label_0038;
                        }
                    }
                    isSubBlock = this.popPushNext();
                    continue;
                }
                while (label > this.currentTransition.max) {
                    if (this.currentFrame.transitionIndex >= this.currentFrame.transitionCount - 1) {
                        if (this.currentFrame.ord == 0) {
                            this.currentFrame = null;
                            return null;
                        }
                        this.currentFrame = this.stack[this.currentFrame.ord - 1];
                        this.currentTransition = this.currentFrame.transition;
                        isSubBlock = this.popPushNext();
                        continue Label_0038;
                    }
                    else {
                        final IntersectTermsEnumFrame currentFrame = this.currentFrame;
                        ++currentFrame.transitionIndex;
                        this.automaton.getNextTransition(this.currentTransition);
                        if (label < this.currentTransition.min) {
                            final int minTrans = this.currentTransition.min;
                            while (this.currentFrame.nextEnt < this.currentFrame.entCount) {
                                isSubBlock = this.currentFrame.next();
                                if ((suffixBytes[this.currentFrame.startBytePos] & 0xFF) >= minTrans) {
                                    continue Label_0038;
                                }
                            }
                            isSubBlock = this.popPushNext();
                            continue Label_0038;
                        }
                        continue;
                    }
                }
                if (this.commonSuffix != null && !isSubBlock) {
                    final int termLen = this.currentFrame.prefix + this.currentFrame.suffix;
                    if (termLen < this.commonSuffix.length) {
                        isSubBlock = this.popPushNext();
                        continue;
                    }
                    final byte[] commonSuffixBytes = this.commonSuffix.bytes;
                    final int lenInPrefix = this.commonSuffix.length - this.currentFrame.suffix;
                    assert this.commonSuffix.offset == 0;
                    int commonSuffixBytesPos = 0;
                    int suffixBytesPos;
                    if (lenInPrefix > 0) {
                        final byte[] termBytes = this.term.bytes;
                        int termBytesPos = this.currentFrame.prefix - lenInPrefix;
                        assert termBytesPos >= 0;
                        final int termBytesPosEnd = this.currentFrame.prefix;
                        while (termBytesPos < termBytesPosEnd) {
                            if (termBytes[termBytesPos++] != commonSuffixBytes[commonSuffixBytesPos++]) {
                                isSubBlock = this.popPushNext();
                                continue Label_0038;
                            }
                        }
                        suffixBytesPos = this.currentFrame.startBytePos;
                    }
                    else {
                        suffixBytesPos = this.currentFrame.startBytePos + this.currentFrame.suffix - this.commonSuffix.length;
                    }
                    final int commonSuffixBytesPosEnd = this.commonSuffix.length;
                    while (commonSuffixBytesPos < commonSuffixBytesPosEnd) {
                        if (suffixBytes[suffixBytesPos++] != commonSuffixBytes[commonSuffixBytesPos++]) {
                            isSubBlock = this.popPushNext();
                            continue Label_0038;
                        }
                    }
                }
                lastState = this.currentFrame.state;
                state = this.currentTransition.dest;
                for (int end = this.currentFrame.startBytePos + this.currentFrame.suffix, idx = this.currentFrame.startBytePos + 1; idx < end; ++idx) {
                    lastState = state;
                    state = this.runAutomaton.step(state, suffixBytes[idx] & 0xFF);
                    if (state == -1) {
                        isSubBlock = this.popPushNext();
                        continue Label_0038;
                    }
                }
            }
            else {
                state = this.currentFrame.state;
                lastState = this.currentFrame.lastState;
            }
            if (isSubBlock) {
                this.copyTerm();
                this.currentFrame = this.pushFrame(state);
                this.currentTransition = this.currentFrame.transition;
                this.currentFrame.lastState = lastState;
            }
            else if (this.currentFrame.isAutoPrefixTerm) {
                if (this.allowAutoPrefixTerms) {
                    if (this.currentFrame.floorSuffixLeadEnd == -1) {
                        this.useAutoPrefixTerm = (state == this.sinkState);
                    }
                    else if (this.currentFrame.floorSuffixLeadStart == -1) {
                        if (this.automaton.isAccept(state)) {
                            this.useAutoPrefixTerm = this.acceptsSuffixRange(state, 0, this.currentFrame.floorSuffixLeadEnd);
                        }
                    }
                    else {
                        this.useAutoPrefixTerm = this.acceptsSuffixRange(lastState, this.currentFrame.floorSuffixLeadStart, this.currentFrame.floorSuffixLeadEnd);
                    }
                    if (this.useAutoPrefixTerm) {
                        this.copyTerm();
                        this.currentFrame.termState.isRealTerm = false;
                        return this.term;
                    }
                }
            }
            else if (this.runAutomaton.isAccept(state)) {
                this.copyTerm();
                assert this.term.compareTo(this.savedStartTerm) > 0 : "saveStartTerm=" + this.savedStartTerm.utf8ToString() + " term=" + this.term.utf8ToString();
                return this.term;
            }
            isSubBlock = this.popPushNext();
        }
        throw new AssertionError();
    }
    
    private boolean acceptsSuffixRange(final int state, final int start, final int end) {
        for (int count = this.automaton.initTransition(state, this.scratchTransition), i = 0; i < count; ++i) {
            this.automaton.getNextTransition(this.scratchTransition);
            if (start >= this.scratchTransition.min && end <= this.scratchTransition.max && this.scratchTransition.dest == this.sinkState) {
                return true;
            }
        }
        return false;
    }
    
    static String brToString(final BytesRef b) {
        try {
            return b.utf8ToString() + " " + b;
        }
        catch (final Throwable t) {
            return b.toString();
        }
    }
    
    private void copyTerm() {
        final int len = this.currentFrame.prefix + this.currentFrame.suffix;
        if (this.term.bytes.length < len) {
            this.term.bytes = ArrayUtil.grow(this.term.bytes, len);
        }
        System.arraycopy(this.currentFrame.suffixBytes, this.currentFrame.startBytePos, this.term.bytes, this.currentFrame.prefix, this.currentFrame.suffix);
        this.term.length = len;
    }
    
    @Override
    public boolean seekExact(final BytesRef text) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void seekExact(final long ord) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long ord() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public SeekStatus seekCeil(final BytesRef text) {
        throw new UnsupportedOperationException();
    }
    
    static {
        fstOutputs = ByteSequenceOutputs.getSingleton();
    }
    
    private static final class NoMoreTermsException extends RuntimeException
    {
        public static final NoMoreTermsException INSTANCE;
        
        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
        
        static {
            INSTANCE = new NoMoreTermsException();
        }
    }
}
