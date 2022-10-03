package org.apache.lucene.util.fst;

import java.util.TreeSet;
import org.apache.lucene.util.BytesRefBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.BitSet;
import java.util.ArrayList;
import java.io.Writer;
import java.util.Comparator;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import org.apache.lucene.util.IntsRef;

public final class Util
{
    private Util() {
    }
    
    public static <T> T get(final FST<T> fst, final IntsRef input) throws IOException {
        final FST.Arc<T> arc = fst.getFirstArc(new FST.Arc<T>());
        final FST.BytesReader fstReader = fst.getBytesReader();
        T output = fst.outputs.getNoOutput();
        for (int i = 0; i < input.length; ++i) {
            if (fst.findTargetArc(input.ints[input.offset + i], arc, arc, fstReader) == null) {
                return null;
            }
            output = fst.outputs.add(output, arc.output);
        }
        if (arc.isFinal()) {
            return fst.outputs.add(output, arc.nextFinalOutput);
        }
        return null;
    }
    
    public static <T> T get(final FST<T> fst, final BytesRef input) throws IOException {
        assert fst.inputType == FST.INPUT_TYPE.BYTE1;
        final FST.BytesReader fstReader = fst.getBytesReader();
        final FST.Arc<T> arc = fst.getFirstArc(new FST.Arc<T>());
        T output = fst.outputs.getNoOutput();
        for (int i = 0; i < input.length; ++i) {
            if (fst.findTargetArc(input.bytes[i + input.offset] & 0xFF, arc, arc, fstReader) == null) {
                return null;
            }
            output = fst.outputs.add(output, arc.output);
        }
        if (arc.isFinal()) {
            return fst.outputs.add(output, arc.nextFinalOutput);
        }
        return null;
    }
    
    public static IntsRef getByOutput(final FST<Long> fst, final long targetOutput) throws IOException {
        final FST.BytesReader in = fst.getBytesReader();
        final FST.Arc<Long> arc = fst.getFirstArc(new FST.Arc<Long>());
        final FST.Arc<Long> scratchArc = new FST.Arc<Long>();
        final IntsRefBuilder result = new IntsRefBuilder();
        return getByOutput(fst, targetOutput, in, arc, scratchArc, result);
    }
    
    public static IntsRef getByOutput(final FST<Long> fst, final long targetOutput, final FST.BytesReader in, final FST.Arc<Long> arc, final FST.Arc<Long> scratchArc, final IntsRefBuilder result) throws IOException {
        long output = arc.output;
        int upto = 0;
        while (true) {
            if (arc.isFinal()) {
                final long finalOutput = output + arc.nextFinalOutput;
                if (finalOutput == targetOutput) {
                    result.setLength(upto);
                    return result.get();
                }
                if (finalOutput > targetOutput) {
                    return null;
                }
            }
            if (!FST.targetHasArcs(arc)) {
                return null;
            }
            result.grow(1 + upto);
            fst.readFirstRealTargetArc(arc.target, arc, in);
            if (arc.bytesPerArc != 0) {
                int low = 0;
                int high = arc.numArcs - 1;
                int mid = 0;
                boolean exact = false;
                while (low <= high) {
                    mid = low + high >>> 1;
                    in.setPosition(arc.posArcsStart);
                    in.skipBytes(arc.bytesPerArc * mid);
                    final byte flags = in.readByte();
                    fst.readLabel(in);
                    long minArcOutput;
                    if ((flags & 0x10) != 0x0) {
                        final long arcOutput = fst.outputs.read(in);
                        minArcOutput = output + arcOutput;
                    }
                    else {
                        minArcOutput = output;
                    }
                    if (minArcOutput == targetOutput) {
                        exact = true;
                        break;
                    }
                    if (minArcOutput < targetOutput) {
                        low = mid + 1;
                    }
                    else {
                        high = mid - 1;
                    }
                }
                if (high == -1) {
                    return null;
                }
                if (exact) {
                    arc.arcIdx = mid - 1;
                }
                else {
                    arc.arcIdx = low - 2;
                }
                fst.readNextRealArc(arc, in);
                result.setIntAt(upto++, arc.label);
                output += arc.output;
            }
            else {
                FST.Arc<Long> prevArc = null;
                while (true) {
                    final long minArcOutput2 = output + arc.output;
                    if (minArcOutput2 == targetOutput) {
                        output = minArcOutput2;
                        result.setIntAt(upto++, arc.label);
                        break;
                    }
                    if (minArcOutput2 > targetOutput) {
                        if (prevArc == null) {
                            return null;
                        }
                        arc.copyFrom(prevArc);
                        result.setIntAt(upto++, arc.label);
                        output += arc.output;
                        break;
                    }
                    else {
                        if (arc.isLast()) {
                            output = minArcOutput2;
                            result.setIntAt(upto++, arc.label);
                            break;
                        }
                        prevArc = scratchArc;
                        prevArc.copyFrom(arc);
                        fst.readNextRealArc(arc, in);
                    }
                }
            }
        }
    }
    
    public static <T> TopResults<T> shortestPaths(final FST<T> fst, final FST.Arc<T> fromNode, final T startOutput, final Comparator<T> comparator, final int topN, final boolean allowEmptyString) throws IOException {
        final TopNSearcher<T> searcher = new TopNSearcher<T>(fst, topN, topN, comparator);
        searcher.addStartPaths(fromNode, startOutput, allowEmptyString, new IntsRefBuilder());
        return searcher.search();
    }
    
    public static <T> void toDot(final FST<T> fst, final Writer out, final boolean sameRank, final boolean labelStates) throws IOException {
        final String expandedNodeColor = "blue";
        final FST.Arc<T> startArc = fst.getFirstArc(new FST.Arc<T>());
        final List<FST.Arc<T>> thisLevelQueue = new ArrayList<FST.Arc<T>>();
        final List<FST.Arc<T>> nextLevelQueue = new ArrayList<FST.Arc<T>>();
        nextLevelQueue.add(startArc);
        final List<Integer> sameLevelStates = new ArrayList<Integer>();
        final BitSet seen = new BitSet();
        seen.set((int)startArc.target);
        final String stateShape = "circle";
        final String finalStateShape = "doublecircle";
        out.write("digraph FST {\n");
        out.write("  rankdir = LR; splines=true; concentrate=true; ordering=out; ranksep=2.5; \n");
        if (!labelStates) {
            out.write("  node [shape=circle, width=.2, height=.2, style=filled]\n");
        }
        emitDotState(out, "initial", "point", "white", "");
        final T NO_OUTPUT = fst.outputs.getNoOutput();
        final FST.BytesReader r = fst.getBytesReader();
        String stateColor;
        if (fst.isExpandedTarget(startArc, r)) {
            stateColor = "blue";
        }
        else {
            stateColor = null;
        }
        boolean isFinal;
        T finalOutput;
        if (startArc.isFinal()) {
            isFinal = true;
            finalOutput = ((startArc.nextFinalOutput == NO_OUTPUT) ? null : startArc.nextFinalOutput);
        }
        else {
            isFinal = false;
            finalOutput = null;
        }
        emitDotState(out, Long.toString(startArc.target), isFinal ? "doublecircle" : "circle", stateColor, (finalOutput == null) ? "" : fst.outputs.outputToString(finalOutput));
        out.write("  initial -> " + startArc.target + "\n");
        int level = 0;
        while (!nextLevelQueue.isEmpty()) {
            thisLevelQueue.addAll(nextLevelQueue);
            nextLevelQueue.clear();
            ++level;
            out.write("\n  // Transitions and states at level: " + level + "\n");
            while (!thisLevelQueue.isEmpty()) {
                final FST.Arc<T> arc = thisLevelQueue.remove(thisLevelQueue.size() - 1);
                if (FST.targetHasArcs(arc)) {
                    final long node = arc.target;
                    fst.readFirstRealTargetArc(arc.target, arc, r);
                    while (true) {
                        if (arc.target >= 0L && !seen.get((int)arc.target)) {
                            String stateColor2;
                            if (fst.isExpandedTarget(arc, r)) {
                                stateColor2 = "blue";
                            }
                            else {
                                stateColor2 = null;
                            }
                            String finalOutput2;
                            if (arc.nextFinalOutput != null && arc.nextFinalOutput != NO_OUTPUT) {
                                finalOutput2 = fst.outputs.outputToString(arc.nextFinalOutput);
                            }
                            else {
                                finalOutput2 = "";
                            }
                            emitDotState(out, Long.toString(arc.target), "circle", stateColor2, finalOutput2);
                            seen.set((int)arc.target);
                            nextLevelQueue.add(new FST.Arc<T>().copyFrom(arc));
                            sameLevelStates.add((int)arc.target);
                        }
                        String outs;
                        if (arc.output != NO_OUTPUT) {
                            outs = "/" + fst.outputs.outputToString(arc.output);
                        }
                        else {
                            outs = "";
                        }
                        if (!FST.targetHasArcs(arc) && arc.isFinal() && arc.nextFinalOutput != NO_OUTPUT) {
                            outs = outs + "/[" + fst.outputs.outputToString(arc.nextFinalOutput) + "]";
                        }
                        String arcColor;
                        if (arc.flag(4)) {
                            arcColor = "red";
                        }
                        else {
                            arcColor = "black";
                        }
                        assert arc.label != -1;
                        out.write("  " + node + " -> " + arc.target + " [label=\"" + printableLabel(arc.label) + outs + "\"" + (arc.isFinal() ? " style=\"bold\"" : "") + " color=\"" + arcColor + "\"]\n");
                        if (arc.isLast()) {
                            break;
                        }
                        fst.readNextRealArc(arc, r);
                    }
                }
            }
            if (sameRank && sameLevelStates.size() > 1) {
                out.write("  {rank=same; ");
                for (final int state : sameLevelStates) {
                    out.write(state + "; ");
                }
                out.write(" }\n");
            }
            sameLevelStates.clear();
        }
        out.write("  -1 [style=filled, color=black, shape=doublecircle, label=\"\"]\n\n");
        out.write("  {rank=sink; -1 }\n");
        out.write("}\n");
        out.flush();
    }
    
    private static void emitDotState(final Writer out, final String name, final String shape, final String color, final String label) throws IOException {
        out.write("  " + name + " [" + ((shape != null) ? ("shape=" + shape) : "") + " " + ((color != null) ? ("color=" + color) : "") + " " + ((label != null) ? ("label=\"" + label + "\"") : "label=\"\"") + " " + "]\n");
    }
    
    private static String printableLabel(final int label) {
        if (label >= 32 && label <= 125 && label != 34 && label != 92) {
            return Character.toString((char)label);
        }
        return "0x" + Integer.toHexString(label);
    }
    
    public static IntsRef toUTF16(final CharSequence s, final IntsRefBuilder scratch) {
        final int charLimit = s.length();
        scratch.setLength(charLimit);
        scratch.grow(charLimit);
        for (int idx = 0; idx < charLimit; ++idx) {
            scratch.setIntAt(idx, s.charAt(idx));
        }
        return scratch.get();
    }
    
    public static IntsRef toUTF32(final CharSequence s, final IntsRefBuilder scratch) {
        int charIdx = 0;
        int intIdx = 0;
        int utf32;
        for (int charLimit = s.length(); charIdx < charLimit; charIdx += Character.charCount(utf32), ++intIdx) {
            scratch.grow(intIdx + 1);
            utf32 = Character.codePointAt(s, charIdx);
            scratch.setIntAt(intIdx, utf32);
        }
        scratch.setLength(intIdx);
        return scratch.get();
    }
    
    public static IntsRef toUTF32(final char[] s, final int offset, final int length, final IntsRefBuilder scratch) {
        int charIdx = offset;
        int intIdx = 0;
        int utf32;
        for (int charLimit = offset + length; charIdx < charLimit; charIdx += Character.charCount(utf32), ++intIdx) {
            scratch.grow(intIdx + 1);
            utf32 = Character.codePointAt(s, charIdx, charLimit);
            scratch.setIntAt(intIdx, utf32);
        }
        scratch.setLength(intIdx);
        return scratch.get();
    }
    
    public static IntsRef toIntsRef(final BytesRef input, final IntsRefBuilder scratch) {
        scratch.clear();
        for (int i = 0; i < input.length; ++i) {
            scratch.append(input.bytes[i + input.offset] & 0xFF);
        }
        return scratch.get();
    }
    
    public static BytesRef toBytesRef(final IntsRef input, final BytesRefBuilder scratch) {
        scratch.grow(input.length);
        for (int i = 0; i < input.length; ++i) {
            final int value = input.ints[i + input.offset];
            assert value >= -128 && value <= 255 : "value " + value + " doesn't fit into byte";
            scratch.setByteAt(i, (byte)value);
        }
        scratch.setLength(input.length);
        return scratch.get();
    }
    
    public static <T> FST.Arc<T> readCeilArc(final int label, final FST<T> fst, final FST.Arc<T> follow, final FST.Arc<T> arc, final FST.BytesReader in) throws IOException {
        if (label == -1) {
            if (follow.isFinal()) {
                if (follow.target <= 0L) {
                    arc.flags = 2;
                }
                else {
                    arc.flags = 0;
                    arc.nextArc = follow.target;
                    arc.node = follow.target;
                }
                arc.output = follow.nextFinalOutput;
                arc.label = -1;
                return arc;
            }
            return null;
        }
        else {
            if (!FST.targetHasArcs(follow)) {
                return null;
            }
            fst.readFirstTargetArc(follow, arc, in);
            if (arc.bytesPerArc == 0 || arc.label == -1) {
                fst.readFirstRealTargetArc(follow.target, arc, in);
                while (arc.label < label) {
                    if (arc.isLast()) {
                        return null;
                    }
                    fst.readNextRealArc(arc, in);
                }
                return arc;
            }
            int low = arc.arcIdx;
            int high = arc.numArcs - 1;
            int mid = 0;
            while (low <= high) {
                mid = low + high >>> 1;
                in.setPosition(arc.posArcsStart);
                in.skipBytes(arc.bytesPerArc * mid + 1);
                final int midLabel = fst.readLabel(in);
                final int cmp = midLabel - label;
                if (cmp < 0) {
                    low = mid + 1;
                }
                else {
                    if (cmp <= 0) {
                        arc.arcIdx = mid - 1;
                        return fst.readNextRealArc(arc, in);
                    }
                    high = mid - 1;
                }
            }
            if (low == arc.numArcs) {
                return null;
            }
            arc.arcIdx = ((low > high) ? high : low);
            return fst.readNextRealArc(arc, in);
        }
    }
    
    public static class FSTPath<T>
    {
        public FST.Arc<T> arc;
        public T cost;
        public final IntsRefBuilder input;
        public final float boost;
        public final CharSequence context;
        
        public FSTPath(final T cost, final FST.Arc<T> arc, final IntsRefBuilder input) {
            this(cost, (FST.Arc<Object>)arc, input, 0.0f, null);
        }
        
        public FSTPath(final T cost, final FST.Arc<T> arc, final IntsRefBuilder input, final float boost, final CharSequence context) {
            this.arc = new FST.Arc<T>().copyFrom(arc);
            this.cost = cost;
            this.input = input;
            this.boost = boost;
            this.context = context;
        }
        
        public FSTPath<T> newPath(final T cost, final IntsRefBuilder input) {
            return new FSTPath<T>(cost, this.arc, input, this.boost, this.context);
        }
        
        @Override
        public String toString() {
            return "input=" + this.input.get() + " cost=" + this.cost + "context=" + (Object)this.context + "boost=" + this.boost;
        }
    }
    
    private static class TieBreakByInputComparator<T> implements Comparator<FSTPath<T>>
    {
        private final Comparator<T> comparator;
        
        public TieBreakByInputComparator(final Comparator<T> comparator) {
            this.comparator = comparator;
        }
        
        @Override
        public int compare(final FSTPath<T> a, final FSTPath<T> b) {
            final int cmp = this.comparator.compare(a.cost, b.cost);
            if (cmp == 0) {
                return a.input.get().compareTo(b.input.get());
            }
            return cmp;
        }
    }
    
    public static class TopNSearcher<T>
    {
        private final FST<T> fst;
        private final FST.BytesReader bytesReader;
        private final int topN;
        private final int maxQueueDepth;
        private final FST.Arc<T> scratchArc;
        private final Comparator<T> comparator;
        private final Comparator<FSTPath<T>> pathComparator;
        TreeSet<FSTPath<T>> queue;
        
        public TopNSearcher(final FST<T> fst, final int topN, final int maxQueueDepth, final Comparator<T> comparator) {
            this(fst, topN, maxQueueDepth, comparator, (Comparator)new TieBreakByInputComparator(comparator));
        }
        
        public TopNSearcher(final FST<T> fst, final int topN, final int maxQueueDepth, final Comparator<T> comparator, final Comparator<FSTPath<T>> pathComparator) {
            this.scratchArc = new FST.Arc<T>();
            this.queue = null;
            this.fst = fst;
            this.bytesReader = fst.getBytesReader();
            this.topN = topN;
            this.maxQueueDepth = maxQueueDepth;
            this.comparator = comparator;
            this.pathComparator = pathComparator;
            this.queue = new TreeSet<FSTPath<T>>(pathComparator);
        }
        
        protected void addIfCompetitive(final FSTPath<T> path) {
            assert this.queue != null;
            final T cost = this.fst.outputs.add(path.cost, path.arc.output);
            if (this.queue.size() == this.maxQueueDepth) {
                final FSTPath<T> bottom = this.queue.last();
                final int comp = this.pathComparator.compare(path, bottom);
                if (comp > 0) {
                    return;
                }
                if (comp == 0) {
                    path.input.append(path.arc.label);
                    final int cmp = bottom.input.get().compareTo(path.input.get());
                    path.input.setLength(path.input.length() - 1);
                    assert cmp != 0;
                    if (cmp < 0) {
                        return;
                    }
                }
            }
            final IntsRefBuilder newInput = new IntsRefBuilder();
            newInput.copyInts(path.input.get());
            newInput.append(path.arc.label);
            this.queue.add(path.newPath(cost, newInput));
            if (this.queue.size() == this.maxQueueDepth + 1) {
                this.queue.pollLast();
            }
        }
        
        public void addStartPaths(final FST.Arc<T> node, final T startOutput, final boolean allowEmptyString, final IntsRefBuilder input) throws IOException {
            this.addStartPaths(node, startOutput, allowEmptyString, input, 0.0f, null);
        }
        
        public void addStartPaths(final FST.Arc<T> node, T startOutput, final boolean allowEmptyString, final IntsRefBuilder input, final float boost, final CharSequence context) throws IOException {
            if (startOutput.equals(this.fst.outputs.getNoOutput())) {
                startOutput = this.fst.outputs.getNoOutput();
            }
            final FSTPath<T> path = new FSTPath<T>(startOutput, node, input, boost, context);
            this.fst.readFirstTargetArc(node, path.arc, this.bytesReader);
            while (true) {
                if (allowEmptyString || path.arc.label != -1) {
                    this.addIfCompetitive(path);
                }
                if (path.arc.isLast()) {
                    break;
                }
                this.fst.readNextArc(path.arc, this.bytesReader);
            }
        }
        
        public TopResults<T> search() throws IOException {
            final List<Result<T>> results = new ArrayList<Result<T>>();
            final FST.BytesReader fstReader = this.fst.getBytesReader();
            final T NO_OUTPUT = this.fst.outputs.getNoOutput();
            int rejectCount = 0;
            while (results.size() < this.topN) {
                if (this.queue == null) {
                    break;
                }
                final FSTPath<T> path = this.queue.pollFirst();
                if (path == null) {
                    break;
                }
                if (path.arc.label == -1) {
                    path.input.setLength(path.input.length() - 1);
                    results.add(new Result<T>(path.input.get(), path.cost));
                }
                else {
                    if (results.size() == this.topN - 1 && this.maxQueueDepth == this.topN) {
                        this.queue = null;
                    }
                    while (true) {
                        this.fst.readFirstTargetArc(path.arc, path.arc, fstReader);
                        boolean foundZero = false;
                        while (true) {
                            if (this.comparator.compare(NO_OUTPUT, path.arc.output) == 0) {
                                if (this.queue == null) {
                                    foundZero = true;
                                    break;
                                }
                                if (!foundZero) {
                                    this.scratchArc.copyFrom(path.arc);
                                    foundZero = true;
                                }
                                else {
                                    this.addIfCompetitive(path);
                                }
                            }
                            else if (this.queue != null) {
                                this.addIfCompetitive(path);
                            }
                            if (path.arc.isLast()) {
                                break;
                            }
                            this.fst.readNextArc(path.arc, fstReader);
                        }
                        assert foundZero;
                        if (this.queue != null) {
                            path.arc.copyFrom(this.scratchArc);
                        }
                        if (path.arc.label == -1) {
                            path.cost = this.fst.outputs.add(path.cost, path.arc.output);
                            if (this.acceptResult(path)) {
                                results.add(new Result<T>(path.input.get(), path.cost));
                            }
                            else {
                                ++rejectCount;
                            }
                            break;
                        }
                        path.input.append(path.arc.label);
                        path.cost = this.fst.outputs.add(path.cost, path.arc.output);
                    }
                }
            }
            return new TopResults<T>(rejectCount + this.topN <= this.maxQueueDepth, results);
        }
        
        protected boolean acceptResult(final FSTPath<T> path) {
            return this.acceptResult(path.input.get(), path.cost);
        }
        
        protected boolean acceptResult(final IntsRef input, final T output) {
            return true;
        }
    }
    
    public static final class Result<T>
    {
        public final IntsRef input;
        public final T output;
        
        public Result(final IntsRef input, final T output) {
            this.input = input;
            this.output = output;
        }
    }
    
    public static final class TopResults<T> implements Iterable<Result<T>>
    {
        public final boolean isComplete;
        public final List<Result<T>> topN;
        
        TopResults(final boolean isComplete, final List<Result<T>> topN) {
            this.topN = topN;
            this.isComplete = isComplete;
        }
        
        @Override
        public Iterator<Result<T>> iterator() {
            return this.topN.iterator();
        }
    }
}
