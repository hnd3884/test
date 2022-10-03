package org.apache.lucene.util.fst;

import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.IntsRef;
import java.io.IOException;
import org.apache.lucene.util.IntsRefBuilder;

public class Builder<T>
{
    private final NodeHash<T> dedupHash;
    final FST<T> fst;
    private final T NO_OUTPUT;
    private final int minSuffixCount1;
    private final int minSuffixCount2;
    private final boolean doShareNonSingletonNodes;
    private final int shareMaxTailLength;
    private final IntsRefBuilder lastInput;
    private final boolean doPackFST;
    private final float acceptableOverheadRatio;
    private UnCompiledNode<T>[] frontier;
    long lastFrozenNode;
    int[] reusedBytesPerArc;
    long arcCount;
    long nodeCount;
    boolean allowArrayArcs;
    BytesStore bytes;
    
    public Builder(final FST.INPUT_TYPE inputType, final Outputs<T> outputs) {
        this(inputType, 0, 0, true, true, Integer.MAX_VALUE, outputs, false, 0.0f, true, 15);
    }
    
    public Builder(final FST.INPUT_TYPE inputType, final int minSuffixCount1, final int minSuffixCount2, final boolean doShareSuffix, final boolean doShareNonSingletonNodes, final int shareMaxTailLength, final Outputs<T> outputs, final boolean doPackFST, final float acceptableOverheadRatio, final boolean allowArrayArcs, final int bytesPageBits) {
        this.lastInput = new IntsRefBuilder();
        this.reusedBytesPerArc = new int[4];
        this.minSuffixCount1 = minSuffixCount1;
        this.minSuffixCount2 = minSuffixCount2;
        this.doShareNonSingletonNodes = doShareNonSingletonNodes;
        this.shareMaxTailLength = shareMaxTailLength;
        this.doPackFST = doPackFST;
        this.acceptableOverheadRatio = acceptableOverheadRatio;
        this.allowArrayArcs = allowArrayArcs;
        this.fst = new FST<T>(inputType, outputs, doPackFST, acceptableOverheadRatio, bytesPageBits);
        this.bytes = this.fst.bytes;
        assert this.bytes != null;
        if (doShareSuffix) {
            this.dedupHash = new NodeHash<T>(this.fst, this.bytes.getReverseReader(false));
        }
        else {
            this.dedupHash = null;
        }
        this.NO_OUTPUT = outputs.getNoOutput();
        final UnCompiledNode<T>[] f = new UnCompiledNode[10];
        this.frontier = f;
        for (int idx = 0; idx < this.frontier.length; ++idx) {
            this.frontier[idx] = new UnCompiledNode<T>(this, idx);
        }
    }
    
    public long getTermCount() {
        return this.frontier[0].inputCount;
    }
    
    public long getNodeCount() {
        return 1L + this.nodeCount;
    }
    
    public long getArcCount() {
        return this.arcCount;
    }
    
    public long getMappedStateCount() {
        return (this.dedupHash == null) ? 0L : this.nodeCount;
    }
    
    private CompiledNode compileNode(final UnCompiledNode<T> nodeIn, final int tailLength) throws IOException {
        final long bytesPosStart = this.bytes.getPosition();
        long node;
        if (this.dedupHash != null && (this.doShareNonSingletonNodes || nodeIn.numArcs <= 1) && tailLength <= this.shareMaxTailLength) {
            if (nodeIn.numArcs == 0) {
                node = this.fst.addNode(this, nodeIn);
                this.lastFrozenNode = node;
            }
            else {
                node = this.dedupHash.add(this, nodeIn);
            }
        }
        else {
            node = this.fst.addNode(this, nodeIn);
        }
        assert node != -2L;
        final long bytesPosEnd = this.bytes.getPosition();
        if (bytesPosEnd != bytesPosStart) {
            assert bytesPosEnd > bytesPosStart;
            this.lastFrozenNode = node;
        }
        nodeIn.clear();
        final CompiledNode fn = new CompiledNode();
        fn.node = node;
        return fn;
    }
    
    private void freezeTail(final int prefixLenPlus1) throws IOException {
        for (int downTo = Math.max(1, prefixLenPlus1), idx = this.lastInput.length(); idx >= downTo; --idx) {
            boolean doPrune = false;
            boolean doCompile = false;
            final UnCompiledNode<T> node = this.frontier[idx];
            final UnCompiledNode<T> parent = this.frontier[idx - 1];
            if (node.inputCount < this.minSuffixCount1) {
                doPrune = true;
                doCompile = true;
            }
            else if (idx > prefixLenPlus1) {
                doPrune = (parent.inputCount < this.minSuffixCount2 || (this.minSuffixCount2 == 1 && parent.inputCount == 1L && idx > 1));
                doCompile = true;
            }
            else {
                doCompile = (this.minSuffixCount2 == 0);
            }
            if (node.inputCount < this.minSuffixCount2 || (this.minSuffixCount2 == 1 && node.inputCount == 1L && idx > 1)) {
                for (int arcIdx = 0; arcIdx < node.numArcs; ++arcIdx) {
                    final UnCompiledNode<T> target = (UnCompiledNode<T>)node.arcs[arcIdx].target;
                    target.clear();
                }
                node.numArcs = 0;
            }
            if (doPrune) {
                node.clear();
                parent.deleteLast(this.lastInput.intAt(idx - 1), node);
            }
            else {
                if (this.minSuffixCount2 != 0) {
                    this.compileAllTargets(node, this.lastInput.length() - idx);
                }
                final T nextFinalOutput = node.output;
                final boolean isFinal = node.isFinal || node.numArcs == 0;
                if (doCompile) {
                    parent.replaceLast(this.lastInput.intAt(idx - 1), this.compileNode(node, 1 + this.lastInput.length() - idx), nextFinalOutput, isFinal);
                }
                else {
                    parent.replaceLast(this.lastInput.intAt(idx - 1), node, nextFinalOutput, isFinal);
                    this.frontier[idx] = new UnCompiledNode<T>(this, idx);
                }
            }
        }
    }
    
    public void add(final IntsRef input, T output) throws IOException {
        if (output.equals(this.NO_OUTPUT)) {
            output = this.NO_OUTPUT;
        }
        assert input.compareTo(this.lastInput.get()) >= 0 : "inputs are added out of order lastInput=" + this.lastInput.get() + " vs input=" + input;
        assert this.validOutput(output);
        if (input.length == 0) {
            final UnCompiledNode<T> unCompiledNode = this.frontier[0];
            ++unCompiledNode.inputCount;
            this.frontier[0].isFinal = true;
            this.fst.setEmptyOutput(output);
            return;
        }
        int pos1 = 0;
        int pos2 = input.offset;
        final int pos1Stop = Math.min(this.lastInput.length(), input.length);
        while (true) {
            final UnCompiledNode<T> unCompiledNode2 = this.frontier[pos1];
            ++unCompiledNode2.inputCount;
            if (pos1 >= pos1Stop || this.lastInput.intAt(pos1) != input.ints[pos2]) {
                break;
            }
            ++pos1;
            ++pos2;
        }
        final int prefixLenPlus1 = pos1 + 1;
        if (this.frontier.length < input.length + 1) {
            final UnCompiledNode<T>[] next = new UnCompiledNode[ArrayUtil.oversize(input.length + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
            System.arraycopy(this.frontier, 0, next, 0, this.frontier.length);
            for (int idx = this.frontier.length; idx < next.length; ++idx) {
                next[idx] = new UnCompiledNode<T>(this, idx);
            }
            this.frontier = next;
        }
        this.freezeTail(prefixLenPlus1);
        for (int idx2 = prefixLenPlus1; idx2 <= input.length; ++idx2) {
            this.frontier[idx2 - 1].addArc(input.ints[input.offset + idx2 - 1], this.frontier[idx2]);
            final UnCompiledNode<T> unCompiledNode3 = this.frontier[idx2];
            ++unCompiledNode3.inputCount;
        }
        final UnCompiledNode<T> lastNode = this.frontier[input.length];
        if (this.lastInput.length() != input.length || prefixLenPlus1 != input.length + 1) {
            lastNode.isFinal = true;
            lastNode.output = this.NO_OUTPUT;
        }
        for (int idx = 1; idx < prefixLenPlus1; ++idx) {
            final UnCompiledNode<T> node = this.frontier[idx];
            final UnCompiledNode<T> parentNode = this.frontier[idx - 1];
            final T lastOutput = parentNode.getLastOutput(input.ints[input.offset + idx - 1]);
            assert this.validOutput(lastOutput);
            T commonOutputPrefix;
            if (lastOutput != this.NO_OUTPUT) {
                commonOutputPrefix = this.fst.outputs.common(output, lastOutput);
                assert this.validOutput(commonOutputPrefix);
                final T wordSuffix = this.fst.outputs.subtract(lastOutput, commonOutputPrefix);
                assert this.validOutput(wordSuffix);
                parentNode.setLastOutput(input.ints[input.offset + idx - 1], commonOutputPrefix);
                node.prependOutput(wordSuffix);
            }
            else {
                final T wordSuffix = commonOutputPrefix = this.NO_OUTPUT;
            }
            output = this.fst.outputs.subtract(output, commonOutputPrefix);
            assert this.validOutput(output);
        }
        if (this.lastInput.length() == input.length && prefixLenPlus1 == 1 + input.length) {
            lastNode.output = this.fst.outputs.merge(lastNode.output, output);
        }
        else {
            this.frontier[prefixLenPlus1 - 1].setLastOutput(input.ints[input.offset + prefixLenPlus1 - 1], output);
        }
        this.lastInput.copyInts(input);
    }
    
    private boolean validOutput(final T output) {
        return output == this.NO_OUTPUT || !output.equals(this.NO_OUTPUT);
    }
    
    public FST<T> finish() throws IOException {
        final UnCompiledNode<T> root = this.frontier[0];
        this.freezeTail(0);
        if (root.inputCount < this.minSuffixCount1 || root.inputCount < this.minSuffixCount2 || root.numArcs == 0) {
            if (this.fst.emptyOutput == null) {
                return null;
            }
            if (this.minSuffixCount1 > 0 || this.minSuffixCount2 > 0) {
                return null;
            }
        }
        else if (this.minSuffixCount2 != 0) {
            this.compileAllTargets(root, this.lastInput.length());
        }
        this.fst.finish(this.compileNode(root, this.lastInput.length()).node);
        if (this.doPackFST) {
            return this.fst.pack(this, 3, Math.max(10, (int)(this.getNodeCount() / 4L)), this.acceptableOverheadRatio);
        }
        return this.fst;
    }
    
    private void compileAllTargets(final UnCompiledNode<T> node, final int tailLength) throws IOException {
        for (int arcIdx = 0; arcIdx < node.numArcs; ++arcIdx) {
            final Arc<T> arc = node.arcs[arcIdx];
            if (!arc.target.isCompiled()) {
                final UnCompiledNode<T> n = (UnCompiledNode<T>)arc.target;
                if (n.numArcs == 0) {
                    final Arc<T> arc2 = arc;
                    final UnCompiledNode<T> unCompiledNode = n;
                    final boolean b = true;
                    unCompiledNode.isFinal = b;
                    arc2.isFinal = b;
                }
                arc.target = this.compileNode(n, tailLength - 1);
            }
        }
    }
    
    public long fstRamBytesUsed() {
        return this.fst.ramBytesUsed();
    }
    
    public static class Arc<T>
    {
        public int label;
        public Node target;
        public boolean isFinal;
        public T output;
        public T nextFinalOutput;
    }
    
    static final class CompiledNode implements Node
    {
        long node;
        
        @Override
        public boolean isCompiled() {
            return true;
        }
    }
    
    public static final class UnCompiledNode<T> implements Node
    {
        final Builder<T> owner;
        public int numArcs;
        public Arc<T>[] arcs;
        public T output;
        public boolean isFinal;
        public long inputCount;
        public final int depth;
        
        public UnCompiledNode(final Builder<T> owner, final int depth) {
            this.owner = owner;
            (this.arcs = new Arc[1])[0] = new Arc<T>();
            this.output = (T)((Builder<Object>)owner).NO_OUTPUT;
            this.depth = depth;
        }
        
        @Override
        public boolean isCompiled() {
            return false;
        }
        
        public void clear() {
            this.numArcs = 0;
            this.isFinal = false;
            this.output = (T)((Builder<Object>)this.owner).NO_OUTPUT;
            this.inputCount = 0L;
        }
        
        public T getLastOutput(final int labelToMatch) {
            assert this.numArcs > 0;
            assert this.arcs[this.numArcs - 1].label == labelToMatch;
            return this.arcs[this.numArcs - 1].output;
        }
        
        public void addArc(final int label, final Node target) {
            assert label >= 0;
            assert label > this.arcs[this.numArcs - 1].label : "arc[-1].label=" + this.arcs[this.numArcs - 1].label + " new label=" + label + " numArcs=" + this.numArcs;
            if (this.numArcs == this.arcs.length) {
                final Arc<T>[] newArcs = new Arc[ArrayUtil.oversize(this.numArcs + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                System.arraycopy(this.arcs, 0, newArcs, 0, this.arcs.length);
                for (int arcIdx = this.numArcs; arcIdx < newArcs.length; ++arcIdx) {
                    newArcs[arcIdx] = new Arc<T>();
                }
                this.arcs = newArcs;
            }
            final Arc<T> arc = this.arcs[this.numArcs++];
            arc.label = label;
            arc.target = target;
            final Arc<T> arc2 = arc;
            final Arc<T> arc3 = arc;
            final Object access$000 = ((Builder<Object>)this.owner).NO_OUTPUT;
            arc3.nextFinalOutput = (T)access$000;
            arc2.output = (T)access$000;
            arc.isFinal = false;
        }
        
        public void replaceLast(final int labelToMatch, final Node target, final T nextFinalOutput, final boolean isFinal) {
            assert this.numArcs > 0;
            final Arc<T> arc = this.arcs[this.numArcs - 1];
            assert arc.label == labelToMatch : "arc.label=" + arc.label + " vs " + labelToMatch;
            arc.target = target;
            arc.nextFinalOutput = nextFinalOutput;
            arc.isFinal = isFinal;
        }
        
        public void deleteLast(final int label, final Node target) {
            assert this.numArcs > 0;
            assert label == this.arcs[this.numArcs - 1].label;
            assert target == this.arcs[this.numArcs - 1].target;
            --this.numArcs;
        }
        
        public void setLastOutput(final int labelToMatch, final T newOutput) {
            assert ((Builder<Object>)this.owner).validOutput(newOutput);
            assert this.numArcs > 0;
            final Arc<T> arc = this.arcs[this.numArcs - 1];
            assert arc.label == labelToMatch;
            arc.output = newOutput;
        }
        
        public void prependOutput(final T outputPrefix) {
            assert ((Builder<Object>)this.owner).validOutput(outputPrefix);
            for (int arcIdx = 0; arcIdx < this.numArcs; ++arcIdx) {
                this.arcs[arcIdx].output = this.owner.fst.outputs.add(outputPrefix, this.arcs[arcIdx].output);
                assert ((Builder<Object>)this.owner).validOutput(this.arcs[arcIdx].output);
            }
            if (this.isFinal) {
                this.output = this.owner.fst.outputs.add(outputPrefix, this.output);
                assert ((Builder<Object>)this.owner).validOutput(this.output);
            }
        }
    }
    
    interface Node
    {
        boolean isCompiled();
    }
}
