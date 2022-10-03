package org.apache.lucene.util.fst;

import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.store.DataInput;
import java.io.IOException;

abstract class FSTEnum<T>
{
    protected final FST<T> fst;
    protected FST.Arc<T>[] arcs;
    protected T[] output;
    protected final T NO_OUTPUT;
    protected final FST.BytesReader fstReader;
    protected final FST.Arc<T> scratchArc;
    protected int upto;
    protected int targetLength;
    
    protected FSTEnum(final FST<T> fst) {
        this.arcs = new FST.Arc[10];
        this.output = (T[])new Object[10];
        this.scratchArc = new FST.Arc<T>();
        this.fst = fst;
        this.fstReader = fst.getBytesReader();
        this.NO_OUTPUT = fst.outputs.getNoOutput();
        fst.getFirstArc(this.getArc(0));
        this.output[0] = this.NO_OUTPUT;
    }
    
    protected abstract int getTargetLabel();
    
    protected abstract int getCurrentLabel();
    
    protected abstract void setCurrentLabel(final int p0);
    
    protected abstract void grow();
    
    protected final void rewindPrefix() throws IOException {
        if (this.upto == 0) {
            this.upto = 1;
            this.fst.readFirstTargetArc(this.getArc(0), this.getArc(1), this.fstReader);
            return;
        }
        final int currentLimit = this.upto;
        this.upto = 1;
        while (this.upto < currentLimit && this.upto <= this.targetLength + 1) {
            final int cmp = this.getCurrentLabel() - this.getTargetLabel();
            if (cmp < 0) {
                break;
            }
            if (cmp > 0) {
                final FST.Arc<T> arc = this.getArc(this.upto);
                this.fst.readFirstTargetArc(this.getArc(this.upto - 1), arc, this.fstReader);
                break;
            }
            ++this.upto;
        }
    }
    
    protected void doNext() throws IOException {
        if (this.upto == 0) {
            this.upto = 1;
            this.fst.readFirstTargetArc(this.getArc(0), this.getArc(1), this.fstReader);
        }
        else {
            while (this.arcs[this.upto].isLast()) {
                --this.upto;
                if (this.upto == 0) {
                    return;
                }
            }
            this.fst.readNextArc(this.arcs[this.upto], this.fstReader);
        }
        this.pushFirst();
    }
    
    protected void doSeekCeil() throws IOException {
        this.rewindPrefix();
        FST.Arc<T> arc = this.getArc(this.upto);
        int targetLabel = this.getTargetLabel();
        while (true) {
            if (arc.bytesPerArc != 0 && arc.label != -1) {
                final FST.BytesReader in = this.fst.getBytesReader();
                int low = arc.arcIdx;
                int high = arc.numArcs - 1;
                int mid = 0;
                boolean found = false;
                while (low <= high) {
                    mid = low + high >>> 1;
                    in.setPosition(arc.posArcsStart);
                    in.skipBytes(arc.bytesPerArc * mid + 1);
                    final int midLabel = this.fst.readLabel(in);
                    final int cmp = midLabel - targetLabel;
                    if (cmp < 0) {
                        low = mid + 1;
                    }
                    else {
                        if (cmp <= 0) {
                            found = true;
                            break;
                        }
                        high = mid - 1;
                    }
                }
                if (found) {
                    arc.arcIdx = mid - 1;
                    this.fst.readNextRealArc(arc, in);
                    assert arc.arcIdx == mid;
                    assert arc.label == targetLabel : "arc.label=" + arc.label + " vs targetLabel=" + targetLabel + " mid=" + mid;
                    this.output[this.upto] = this.fst.outputs.add(this.output[this.upto - 1], arc.output);
                    if (targetLabel == -1) {
                        return;
                    }
                    this.setCurrentLabel(arc.label);
                    this.incr();
                    arc = this.fst.readFirstTargetArc(arc, this.getArc(this.upto), this.fstReader);
                    targetLabel = this.getTargetLabel();
                }
                else if (low == arc.numArcs) {
                    arc.arcIdx = arc.numArcs - 2;
                    this.fst.readNextRealArc(arc, in);
                    assert arc.isLast();
                    --this.upto;
                    while (this.upto != 0) {
                        final FST.Arc<T> prevArc = this.getArc(this.upto);
                        if (!prevArc.isLast()) {
                            this.fst.readNextArc(prevArc, this.fstReader);
                            this.pushFirst();
                            return;
                        }
                        --this.upto;
                    }
                }
                else {
                    arc.arcIdx = ((low > high) ? low : high) - 1;
                    this.fst.readNextRealArc(arc, in);
                    assert arc.label > targetLabel;
                    this.pushFirst();
                }
            }
            else if (arc.label == targetLabel) {
                this.output[this.upto] = this.fst.outputs.add(this.output[this.upto - 1], arc.output);
                if (targetLabel == -1) {
                    return;
                }
                this.setCurrentLabel(arc.label);
                this.incr();
                arc = this.fst.readFirstTargetArc(arc, this.getArc(this.upto), this.fstReader);
                targetLabel = this.getTargetLabel();
            }
            else {
                if (arc.label > targetLabel) {
                    this.pushFirst();
                    return;
                }
                if (arc.isLast()) {
                    --this.upto;
                    while (this.upto != 0) {
                        final FST.Arc<T> prevArc2 = this.getArc(this.upto);
                        if (!prevArc2.isLast()) {
                            this.fst.readNextArc(prevArc2, this.fstReader);
                            this.pushFirst();
                            return;
                        }
                        --this.upto;
                    }
                    return;
                }
                this.fst.readNextArc(arc, this.fstReader);
            }
        }
    }
    
    protected void doSeekFloor() throws IOException {
        this.rewindPrefix();
        FST.Arc<T> arc = this.getArc(this.upto);
        int targetLabel = this.getTargetLabel();
        Label_0828: {
            Block_27: {
                while (true) {
                    if (arc.bytesPerArc != 0 && arc.label != -1) {
                        final FST.BytesReader in = this.fst.getBytesReader();
                        int low = arc.arcIdx;
                        int high = arc.numArcs - 1;
                        int mid = 0;
                        boolean found = false;
                        while (low <= high) {
                            mid = low + high >>> 1;
                            in.setPosition(arc.posArcsStart);
                            in.skipBytes(arc.bytesPerArc * mid + 1);
                            final int midLabel = this.fst.readLabel(in);
                            final int cmp = midLabel - targetLabel;
                            if (cmp < 0) {
                                low = mid + 1;
                            }
                            else {
                                if (cmp <= 0) {
                                    found = true;
                                    break;
                                }
                                high = mid - 1;
                            }
                        }
                        if (found) {
                            arc.arcIdx = mid - 1;
                            this.fst.readNextRealArc(arc, in);
                            assert arc.arcIdx == mid;
                            assert arc.label == targetLabel : "arc.label=" + arc.label + " vs targetLabel=" + targetLabel + " mid=" + mid;
                            this.output[this.upto] = this.fst.outputs.add(this.output[this.upto - 1], arc.output);
                            if (targetLabel == -1) {
                                return;
                            }
                            this.setCurrentLabel(arc.label);
                            this.incr();
                            arc = this.fst.readFirstTargetArc(arc, this.getArc(this.upto), this.fstReader);
                            targetLabel = this.getTargetLabel();
                        }
                        else {
                            if (high == -1) {
                                while (true) {
                                    this.fst.readFirstTargetArc(this.getArc(this.upto - 1), arc, this.fstReader);
                                    if (arc.label < targetLabel) {
                                        break;
                                    }
                                    --this.upto;
                                    if (this.upto == 0) {
                                        return;
                                    }
                                    targetLabel = this.getTargetLabel();
                                    arc = this.getArc(this.upto);
                                }
                                while (!arc.isLast() && this.fst.readNextArcLabel(arc, in) < targetLabel) {
                                    this.fst.readNextArc(arc, this.fstReader);
                                }
                                this.pushLast();
                                return;
                            }
                            arc.arcIdx = ((low > high) ? high : low) - 1;
                            this.fst.readNextRealArc(arc, in);
                            assert this.fst.readNextArcLabel(arc, in) > targetLabel;
                            assert arc.label < targetLabel : "arc.label=" + arc.label + " vs targetLabel=" + targetLabel;
                            this.pushLast();
                            return;
                        }
                    }
                    else if (arc.label == targetLabel) {
                        this.output[this.upto] = this.fst.outputs.add(this.output[this.upto - 1], arc.output);
                        if (targetLabel == -1) {
                            return;
                        }
                        this.setCurrentLabel(arc.label);
                        this.incr();
                        arc = this.fst.readFirstTargetArc(arc, this.getArc(this.upto), this.fstReader);
                        targetLabel = this.getTargetLabel();
                    }
                    else {
                        if (arc.label > targetLabel) {
                            break;
                        }
                        if (arc.isLast()) {
                            break Label_0828;
                        }
                        if (this.fst.readNextArcLabel(arc, this.fstReader) > targetLabel) {
                            break Block_27;
                        }
                        this.fst.readNextArc(arc, this.fstReader);
                    }
                }
                while (true) {
                    this.fst.readFirstTargetArc(this.getArc(this.upto - 1), arc, this.fstReader);
                    if (arc.label < targetLabel) {
                        break;
                    }
                    --this.upto;
                    if (this.upto == 0) {
                        return;
                    }
                    targetLabel = this.getTargetLabel();
                    arc = this.getArc(this.upto);
                }
                while (!arc.isLast() && this.fst.readNextArcLabel(arc, this.fstReader) < targetLabel) {
                    this.fst.readNextArc(arc, this.fstReader);
                }
                this.pushLast();
                return;
            }
            this.pushLast();
            return;
        }
        this.pushLast();
    }
    
    protected boolean doSeekExact() throws IOException {
        this.rewindPrefix();
        FST.Arc<T> arc = this.getArc(this.upto - 1);
        int targetLabel = this.getTargetLabel();
        final FST.BytesReader fstReader = this.fst.getBytesReader();
        while (true) {
            final FST.Arc<T> nextArc = this.fst.findTargetArc(targetLabel, arc, this.getArc(this.upto), fstReader);
            if (nextArc == null) {
                this.fst.readFirstTargetArc(arc, this.getArc(this.upto), fstReader);
                return false;
            }
            this.output[this.upto] = this.fst.outputs.add(this.output[this.upto - 1], nextArc.output);
            if (targetLabel == -1) {
                return true;
            }
            this.setCurrentLabel(targetLabel);
            this.incr();
            targetLabel = this.getTargetLabel();
            arc = nextArc;
        }
    }
    
    private void incr() {
        ++this.upto;
        this.grow();
        if (this.arcs.length <= this.upto) {
            final FST.Arc<T>[] newArcs = new FST.Arc[ArrayUtil.oversize(1 + this.upto, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
            System.arraycopy(this.arcs, 0, newArcs, 0, this.arcs.length);
            this.arcs = newArcs;
        }
        if (this.output.length <= this.upto) {
            final T[] newOutput = (T[])new Object[ArrayUtil.oversize(1 + this.upto, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
            System.arraycopy(this.output, 0, newOutput, 0, this.output.length);
            this.output = newOutput;
        }
    }
    
    private void pushFirst() throws IOException {
        FST.Arc<T> arc = this.arcs[this.upto];
        assert arc != null;
        while (true) {
            this.output[this.upto] = this.fst.outputs.add(this.output[this.upto - 1], arc.output);
            if (arc.label == -1) {
                break;
            }
            this.setCurrentLabel(arc.label);
            this.incr();
            final FST.Arc<T> nextArc = this.getArc(this.upto);
            this.fst.readFirstTargetArc(arc, nextArc, this.fstReader);
            arc = nextArc;
        }
    }
    
    private void pushLast() throws IOException {
        FST.Arc<T> arc = this.arcs[this.upto];
        assert arc != null;
        while (true) {
            this.setCurrentLabel(arc.label);
            this.output[this.upto] = this.fst.outputs.add(this.output[this.upto - 1], arc.output);
            if (arc.label == -1) {
                break;
            }
            this.incr();
            arc = this.fst.readLastTargetArc(arc, this.getArc(this.upto), this.fstReader);
        }
    }
    
    private FST.Arc<T> getArc(final int idx) {
        if (this.arcs[idx] == null) {
            this.arcs[idx] = new FST.Arc<T>();
        }
        return this.arcs[idx];
    }
}
