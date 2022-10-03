package org.apache.lucene.util.fst;

import org.apache.lucene.util.packed.PackedInts;
import java.io.IOException;
import org.apache.lucene.util.packed.PagedGrowableWriter;

final class NodeHash<T>
{
    private PagedGrowableWriter table;
    private long count;
    private long mask;
    private final FST<T> fst;
    private final FST.Arc<T> scratchArc;
    private final FST.BytesReader in;
    
    public NodeHash(final FST<T> fst, final FST.BytesReader in) {
        this.scratchArc = new FST.Arc<T>();
        this.table = new PagedGrowableWriter(16L, 134217728, 8, 0.0f);
        this.mask = 15L;
        this.fst = fst;
        this.in = in;
    }
    
    private boolean nodesEqual(final Builder.UnCompiledNode<T> node, final long address) throws IOException {
        this.fst.readFirstRealTargetArc(address, this.scratchArc, this.in);
        if (this.scratchArc.bytesPerArc != 0 && node.numArcs != this.scratchArc.numArcs) {
            return false;
        }
        for (int arcUpto = 0; arcUpto < node.numArcs; ++arcUpto) {
            final Builder.Arc<T> arc = node.arcs[arcUpto];
            if (arc.label != this.scratchArc.label || !arc.output.equals(this.scratchArc.output) || ((Builder.CompiledNode)arc.target).node != this.scratchArc.target || !arc.nextFinalOutput.equals(this.scratchArc.nextFinalOutput) || arc.isFinal != this.scratchArc.isFinal()) {
                return false;
            }
            if (this.scratchArc.isLast()) {
                return arcUpto == node.numArcs - 1;
            }
            this.fst.readNextRealArc(this.scratchArc, this.in);
        }
        return false;
    }
    
    private long hash(final Builder.UnCompiledNode<T> node) {
        final int PRIME = 31;
        long h = 0L;
        for (int arcIdx = 0; arcIdx < node.numArcs; ++arcIdx) {
            final Builder.Arc<T> arc = node.arcs[arcIdx];
            h = 31L * h + arc.label;
            final long n = ((Builder.CompiledNode)arc.target).node;
            h = 31L * h + (int)(n ^ n >> 32);
            h = 31L * h + arc.output.hashCode();
            h = 31L * h + arc.nextFinalOutput.hashCode();
            if (arc.isFinal) {
                h += 17L;
            }
        }
        return h & Long.MAX_VALUE;
    }
    
    private long hash(final long node) throws IOException {
        final int PRIME = 31;
        long h = 0L;
        this.fst.readFirstRealTargetArc(node, this.scratchArc, this.in);
        while (true) {
            h = 31L * h + this.scratchArc.label;
            h = 31L * h + (int)(this.scratchArc.target ^ this.scratchArc.target >> 32);
            h = 31L * h + this.scratchArc.output.hashCode();
            h = 31L * h + this.scratchArc.nextFinalOutput.hashCode();
            if (this.scratchArc.isFinal()) {
                h += 17L;
            }
            if (this.scratchArc.isLast()) {
                break;
            }
            this.fst.readNextRealArc(this.scratchArc, this.in);
        }
        return h & Long.MAX_VALUE;
    }
    
    public long add(final Builder<T> builder, final Builder.UnCompiledNode<T> nodeIn) throws IOException {
        final long h = this.hash(nodeIn);
        long pos = h & this.mask;
        int c = 0;
        while (true) {
            final long v = this.table.get(pos);
            if (v == 0L) {
                final long node = this.fst.addNode(builder, nodeIn);
                assert this.hash(node) == h : "frozenHash=" + this.hash(node) + " vs h=" + h;
                ++this.count;
                this.table.set(pos, node);
                if (this.count > 2L * this.table.size() / 3L) {
                    this.rehash();
                }
                return node;
            }
            else {
                if (this.nodesEqual(nodeIn, v)) {
                    return v;
                }
                pos = (pos + ++c & this.mask);
            }
        }
    }
    
    private void addNew(final long address) throws IOException {
        long pos = this.hash(address) & this.mask;
        for (int c = 0; this.table.get(pos) != 0L; pos = (pos + ++c & this.mask)) {}
        this.table.set(pos, address);
    }
    
    private void rehash() throws IOException {
        final PagedGrowableWriter oldTable = this.table;
        this.table = new PagedGrowableWriter(2L * oldTable.size(), 1073741824, PackedInts.bitsRequired(this.count), 0.0f);
        this.mask = this.table.size() - 1L;
        for (long idx = 0L; idx < oldTable.size(); ++idx) {
            final long address = oldTable.get(idx);
            if (address != 0L) {
                this.addNew(address);
            }
        }
    }
}
