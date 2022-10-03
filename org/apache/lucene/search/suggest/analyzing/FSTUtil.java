package org.apache.lucene.search.suggest.analyzing;

import java.io.IOException;
import org.apache.lucene.util.fst.Util;
import org.apache.lucene.util.automaton.Transition;
import org.apache.lucene.util.IntsRefBuilder;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.automaton.Automaton;

public class FSTUtil
{
    private FSTUtil() {
    }
    
    public static <T> List<Path<T>> intersectPrefixPaths(final Automaton a, final FST<T> fst) throws IOException {
        assert a.isDeterministic();
        final List<Path<T>> queue = new ArrayList<Path<T>>();
        final List<Path<T>> endNodes = new ArrayList<Path<T>>();
        if (a.getNumStates() == 0) {
            return endNodes;
        }
        queue.add(new Path<T>(0, (FST.Arc<T>)fst.getFirstArc(new FST.Arc()), (T)fst.outputs.getNoOutput(), new IntsRefBuilder()));
        final FST.Arc<T> scratchArc = (FST.Arc<T>)new FST.Arc();
        final FST.BytesReader fstReader = fst.getBytesReader();
        final Transition t = new Transition();
        while (queue.size() != 0) {
            final Path<T> path = queue.remove(queue.size() - 1);
            if (a.isAccept(path.state)) {
                endNodes.add(path);
            }
            else {
                final IntsRefBuilder currentInput = path.input;
                for (int count = a.initTransition(path.state, t), i = 0; i < count; ++i) {
                    a.getNextTransition(t);
                    final int min = t.min;
                    final int max = t.max;
                    if (min == max) {
                        final FST.Arc<T> nextArc = (FST.Arc<T>)fst.findTargetArc(t.min, (FST.Arc)path.fstNode, (FST.Arc)scratchArc, fstReader);
                        if (nextArc != null) {
                            final IntsRefBuilder newInput = new IntsRefBuilder();
                            newInput.copyInts(currentInput.get());
                            newInput.append(t.min);
                            queue.add(new Path<T>(t.dest, (FST.Arc<T>)new FST.Arc().copyFrom((FST.Arc)nextArc), (T)fst.outputs.add((Object)path.output, nextArc.output), newInput));
                        }
                    }
                    else {
                        FST.Arc<T> nextArc = (FST.Arc<T>)Util.readCeilArc(min, (FST)fst, (FST.Arc)path.fstNode, (FST.Arc)scratchArc, fstReader);
                        while (nextArc != null && nextArc.label <= max) {
                            assert nextArc.label <= max;
                            assert nextArc.label >= min : nextArc.label + " " + min;
                            final IntsRefBuilder newInput = new IntsRefBuilder();
                            newInput.copyInts(currentInput.get());
                            newInput.append(nextArc.label);
                            queue.add(new Path<T>(t.dest, (FST.Arc<T>)new FST.Arc().copyFrom((FST.Arc)nextArc), (T)fst.outputs.add((Object)path.output, nextArc.output), newInput));
                            final int label = nextArc.label;
                            nextArc = (FST.Arc<T>)(nextArc.isLast() ? null : fst.readNextRealArc((FST.Arc)nextArc, fstReader));
                            assert label < nextArc.label : "last: " + label + " next: " + nextArc.label;
                        }
                    }
                }
            }
        }
        return endNodes;
    }
    
    public static final class Path<T>
    {
        public final int state;
        public final FST.Arc<T> fstNode;
        public final T output;
        public final IntsRefBuilder input;
        
        public Path(final int state, final FST.Arc<T> fstNode, final T output, final IntsRefBuilder input) {
            this.state = state;
            this.fstNode = fstNode;
            this.output = output;
            this.input = input;
        }
    }
}
