package org.apache.lucene.util.automaton;

import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.IntsRefBuilder;
import java.util.BitSet;
import org.apache.lucene.util.IntsRef;

public class FiniteStringsIterator
{
    private static final IntsRef EMPTY;
    private final Automaton a;
    private final BitSet pathStates;
    private final IntsRefBuilder string;
    private PathNode[] nodes;
    private boolean emitEmptyString;
    
    public FiniteStringsIterator(final Automaton a) {
        this.a = a;
        this.nodes = new PathNode[16];
        for (int i = 0, end = this.nodes.length; i < end; ++i) {
            this.nodes[i] = new PathNode();
        }
        this.string = new IntsRefBuilder();
        this.pathStates = new BitSet(a.getNumStates());
        this.string.setLength(0);
        this.emitEmptyString = a.isAccept(0);
        if (a.getNumTransitions(0) > 0) {
            this.pathStates.set(0);
            this.nodes[0].resetState(a, 0);
            this.string.append(0);
        }
    }
    
    public IntsRef next() {
        if (this.emitEmptyString) {
            this.emitEmptyString = false;
            return FiniteStringsIterator.EMPTY;
        }
        int depth = this.string.length();
        while (depth > 0) {
            final PathNode node = this.nodes[depth - 1];
            final int label = node.nextLabel(this.a);
            if (label != -1) {
                this.string.setIntAt(depth - 1, label);
                final int to = node.to;
                if (this.a.getNumTransitions(to) != 0) {
                    if (this.pathStates.get(to)) {
                        throw new IllegalArgumentException("automaton has cycles");
                    }
                    this.pathStates.set(to);
                    this.growStack(depth);
                    this.nodes[depth].resetState(this.a, to);
                    ++depth;
                    this.string.setLength(depth);
                    this.string.grow(depth);
                }
                else {
                    if (this.a.isAccept(to)) {
                        return this.string.get();
                    }
                    continue;
                }
            }
            else {
                final int state = node.state;
                assert this.pathStates.get(state);
                this.pathStates.clear(state);
                --depth;
                this.string.setLength(depth);
                if (this.a.isAccept(state)) {
                    return this.string.get();
                }
                continue;
            }
        }
        return null;
    }
    
    private void growStack(final int depth) {
        if (this.nodes.length == depth) {
            final PathNode[] newNodes = new PathNode[ArrayUtil.oversize(this.nodes.length + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
            System.arraycopy(this.nodes, 0, newNodes, 0, this.nodes.length);
            for (int i = depth, end = newNodes.length; i < end; ++i) {
                newNodes[i] = new PathNode();
            }
            this.nodes = newNodes;
        }
    }
    
    static {
        EMPTY = new IntsRef();
    }
    
    private static class PathNode
    {
        public int state;
        public int to;
        public int transition;
        public int label;
        private final Transition t;
        
        private PathNode() {
            this.t = new Transition();
        }
        
        public void resetState(final Automaton a, final int state) {
            assert a.getNumTransitions(state) != 0;
            a.getTransition(this.state = state, this.transition = 0, this.t);
            this.label = this.t.min;
            this.to = this.t.dest;
        }
        
        public int nextLabel(final Automaton a) {
            if (this.label > this.t.max) {
                ++this.transition;
                if (this.transition >= a.getNumTransitions(this.state)) {
                    return this.label = -1;
                }
                a.getTransition(this.state, this.transition, this.t);
                this.label = this.t.min;
                this.to = this.t.dest;
            }
            return this.label++;
        }
    }
}
