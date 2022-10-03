package org.apache.lucene.util.automaton;

import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.util.RamUsageEstimator;
import java.util.Iterator;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.InPlaceMergeSorter;
import org.apache.lucene.util.Sorter;
import java.util.BitSet;
import org.apache.lucene.util.Accountable;

public class Automaton implements Accountable
{
    private int nextState;
    private int nextTransition;
    private int curState;
    private int[] states;
    private final BitSet isAccept;
    private int[] transitions;
    private boolean deterministic;
    private final Sorter destMinMaxSorter;
    private final Sorter minMaxDestSorter;
    
    public Automaton() {
        this(2, 2);
    }
    
    public Automaton(final int numStates, final int numTransitions) {
        this.curState = -1;
        this.deterministic = true;
        this.destMinMaxSorter = new InPlaceMergeSorter() {
            private void swapOne(final int i, final int j) {
                final int x = Automaton.this.transitions[i];
                Automaton.this.transitions[i] = Automaton.this.transitions[j];
                Automaton.this.transitions[j] = x;
            }
            
            @Override
            protected void swap(final int i, final int j) {
                final int iStart = 3 * i;
                final int jStart = 3 * j;
                this.swapOne(iStart, jStart);
                this.swapOne(iStart + 1, jStart + 1);
                this.swapOne(iStart + 2, jStart + 2);
            }
            
            @Override
            protected int compare(final int i, final int j) {
                final int iStart = 3 * i;
                final int jStart = 3 * j;
                final int iDest = Automaton.this.transitions[iStart];
                final int jDest = Automaton.this.transitions[jStart];
                if (iDest < jDest) {
                    return -1;
                }
                if (iDest > jDest) {
                    return 1;
                }
                final int iMin = Automaton.this.transitions[iStart + 1];
                final int jMin = Automaton.this.transitions[jStart + 1];
                if (iMin < jMin) {
                    return -1;
                }
                if (iMin > jMin) {
                    return 1;
                }
                final int iMax = Automaton.this.transitions[iStart + 2];
                final int jMax = Automaton.this.transitions[jStart + 2];
                if (iMax < jMax) {
                    return -1;
                }
                if (iMax > jMax) {
                    return 1;
                }
                return 0;
            }
        };
        this.minMaxDestSorter = new InPlaceMergeSorter() {
            private void swapOne(final int i, final int j) {
                final int x = Automaton.this.transitions[i];
                Automaton.this.transitions[i] = Automaton.this.transitions[j];
                Automaton.this.transitions[j] = x;
            }
            
            @Override
            protected void swap(final int i, final int j) {
                final int iStart = 3 * i;
                final int jStart = 3 * j;
                this.swapOne(iStart, jStart);
                this.swapOne(iStart + 1, jStart + 1);
                this.swapOne(iStart + 2, jStart + 2);
            }
            
            @Override
            protected int compare(final int i, final int j) {
                final int iStart = 3 * i;
                final int jStart = 3 * j;
                final int iMin = Automaton.this.transitions[iStart + 1];
                final int jMin = Automaton.this.transitions[jStart + 1];
                if (iMin < jMin) {
                    return -1;
                }
                if (iMin > jMin) {
                    return 1;
                }
                final int iMax = Automaton.this.transitions[iStart + 2];
                final int jMax = Automaton.this.transitions[jStart + 2];
                if (iMax < jMax) {
                    return -1;
                }
                if (iMax > jMax) {
                    return 1;
                }
                final int iDest = Automaton.this.transitions[iStart];
                final int jDest = Automaton.this.transitions[jStart];
                if (iDest < jDest) {
                    return -1;
                }
                if (iDest > jDest) {
                    return 1;
                }
                return 0;
            }
        };
        this.states = new int[numStates * 2];
        this.isAccept = new BitSet(numStates);
        this.transitions = new int[numTransitions * 3];
    }
    
    public int createState() {
        this.growStates();
        final int state = this.nextState / 2;
        this.states[this.nextState] = -1;
        this.nextState += 2;
        return state;
    }
    
    public void setAccept(final int state, final boolean accept) {
        if (state >= this.getNumStates()) {
            throw new IllegalArgumentException("state=" + state + " is out of bounds (numStates=" + this.getNumStates() + ")");
        }
        if (accept) {
            this.isAccept.set(state);
        }
        else {
            this.isAccept.clear(state);
        }
    }
    
    public Transition[][] getSortedTransitions() {
        final int numStates = this.getNumStates();
        final Transition[][] transitions = new Transition[numStates][];
        for (int s = 0; s < numStates; ++s) {
            final int numTransitions = this.getNumTransitions(s);
            transitions[s] = new Transition[numTransitions];
            for (int t = 0; t < numTransitions; ++t) {
                final Transition transition = new Transition();
                this.getTransition(s, t, transition);
                transitions[s][t] = transition;
            }
        }
        return transitions;
    }
    
    BitSet getAcceptStates() {
        return this.isAccept;
    }
    
    public boolean isAccept(final int state) {
        return this.isAccept.get(state);
    }
    
    public void addTransition(final int source, final int dest, final int label) {
        this.addTransition(source, dest, label, label);
    }
    
    public void addTransition(final int source, final int dest, final int min, final int max) {
        assert this.nextTransition % 3 == 0;
        if (source >= this.nextState / 2) {
            throw new IllegalArgumentException("source=" + source + " is out of bounds (maxState is " + (this.nextState / 2 - 1) + ")");
        }
        if (dest >= this.nextState / 2) {
            throw new IllegalArgumentException("dest=" + dest + " is out of bounds (max state is " + (this.nextState / 2 - 1) + ")");
        }
        this.growTransitions();
        if (this.curState != source) {
            if (this.curState != -1) {
                this.finishCurrentState();
            }
            this.curState = source;
            if (this.states[2 * this.curState] != -1) {
                throw new IllegalStateException("from state (" + source + ") already had transitions added");
            }
            assert this.states[2 * this.curState + 1] == 0;
            this.states[2 * this.curState] = this.nextTransition;
        }
        this.transitions[this.nextTransition++] = dest;
        this.transitions[this.nextTransition++] = min;
        this.transitions[this.nextTransition++] = max;
        final int[] states = this.states;
        final int n = 2 * this.curState + 1;
        ++states[n];
    }
    
    public void addEpsilon(final int source, final int dest) {
        final Transition t = new Transition();
        for (int count = this.initTransition(dest, t), i = 0; i < count; ++i) {
            this.getNextTransition(t);
            this.addTransition(source, t.dest, t.min, t.max);
        }
        if (this.isAccept(dest)) {
            this.setAccept(source, true);
        }
    }
    
    public void copy(final Automaton other) {
        final int stateOffset = this.getNumStates();
        this.states = ArrayUtil.grow(this.states, this.nextState + other.nextState);
        System.arraycopy(other.states, 0, this.states, this.nextState, other.nextState);
        for (int i = 0; i < other.nextState; i += 2) {
            if (this.states[this.nextState + i] != -1) {
                final int[] states = this.states;
                final int n = this.nextState + i;
                states[n] += this.nextTransition;
            }
        }
        this.nextState += other.nextState;
        final int otherNumStates = other.getNumStates();
        final BitSet otherAcceptStates = other.getAcceptStates();
        for (int state = 0; state < otherNumStates && (state = otherAcceptStates.nextSetBit(state)) != -1; ++state) {
            this.setAccept(stateOffset + state, true);
        }
        this.transitions = ArrayUtil.grow(this.transitions, this.nextTransition + other.nextTransition);
        System.arraycopy(other.transitions, 0, this.transitions, this.nextTransition, other.nextTransition);
        for (int j = 0; j < other.nextTransition; j += 3) {
            final int[] transitions = this.transitions;
            final int n2 = this.nextTransition + j;
            transitions[n2] += stateOffset;
        }
        this.nextTransition += other.nextTransition;
        if (!other.deterministic) {
            this.deterministic = false;
        }
    }
    
    private void finishCurrentState() {
        final int numTransitions = this.states[2 * this.curState + 1];
        assert numTransitions > 0;
        final int offset = this.states[2 * this.curState];
        final int start = offset / 3;
        this.destMinMaxSorter.sort(start, start + numTransitions);
        int upto = 0;
        int min = -1;
        int max = -1;
        int dest = -1;
        for (int i = 0; i < numTransitions; ++i) {
            final int tDest = this.transitions[offset + 3 * i];
            final int tMin = this.transitions[offset + 3 * i + 1];
            final int tMax = this.transitions[offset + 3 * i + 2];
            if (dest == tDest) {
                if (tMin <= max + 1) {
                    if (tMax > max) {
                        max = tMax;
                    }
                }
                else {
                    if (dest != -1) {
                        this.transitions[offset + 3 * upto] = dest;
                        this.transitions[offset + 3 * upto + 1] = min;
                        this.transitions[offset + 3 * upto + 2] = max;
                        ++upto;
                    }
                    min = tMin;
                    max = tMax;
                }
            }
            else {
                if (dest != -1) {
                    this.transitions[offset + 3 * upto] = dest;
                    this.transitions[offset + 3 * upto + 1] = min;
                    this.transitions[offset + 3 * upto + 2] = max;
                    ++upto;
                }
                dest = tDest;
                min = tMin;
                max = tMax;
            }
        }
        if (dest != -1) {
            this.transitions[offset + 3 * upto] = dest;
            this.transitions[offset + 3 * upto + 1] = min;
            this.transitions[offset + 3 * upto + 2] = max;
            ++upto;
        }
        this.nextTransition -= (numTransitions - upto) * 3;
        this.states[2 * this.curState + 1] = upto;
        this.minMaxDestSorter.sort(start, start + upto);
        if (this.deterministic && upto > 1) {
            int lastMax = this.transitions[offset + 2];
            for (int j = 1; j < upto; ++j) {
                min = this.transitions[offset + 3 * j + 1];
                if (min <= lastMax) {
                    this.deterministic = false;
                    break;
                }
                lastMax = this.transitions[offset + 3 * j + 2];
            }
        }
    }
    
    public boolean isDeterministic() {
        return this.deterministic;
    }
    
    public void finishState() {
        if (this.curState != -1) {
            this.finishCurrentState();
            this.curState = -1;
        }
    }
    
    public int getNumStates() {
        return this.nextState / 2;
    }
    
    public int getNumTransitions() {
        return this.nextTransition / 3;
    }
    
    public int getNumTransitions(final int state) {
        assert state >= 0;
        final int count = this.states[2 * state + 1];
        if (count == -1) {
            return 0;
        }
        return count;
    }
    
    private void growStates() {
        if (this.nextState + 2 >= this.states.length) {
            this.states = ArrayUtil.grow(this.states, this.nextState + 2);
        }
    }
    
    private void growTransitions() {
        if (this.nextTransition + 3 >= this.transitions.length) {
            this.transitions = ArrayUtil.grow(this.transitions, this.nextTransition + 3);
        }
    }
    
    public int initTransition(final int state, final Transition t) {
        assert state < this.nextState / 2 : "state=" + state + " nextState=" + this.nextState;
        t.source = state;
        t.transitionUpto = this.states[2 * state];
        return this.getNumTransitions(state);
    }
    
    public void getNextTransition(final Transition t) {
        assert t.transitionUpto + 3 - this.states[2 * t.source] <= 3 * this.states[2 * t.source + 1];
        assert this.transitionSorted(t);
        t.dest = this.transitions[t.transitionUpto++];
        t.min = this.transitions[t.transitionUpto++];
        t.max = this.transitions[t.transitionUpto++];
    }
    
    private boolean transitionSorted(final Transition t) {
        final int upto = t.transitionUpto;
        if (upto == this.states[2 * t.source]) {
            return true;
        }
        final int nextDest = this.transitions[upto];
        final int nextMin = this.transitions[upto + 1];
        final int nextMax = this.transitions[upto + 2];
        return nextMin > t.min || (nextMin >= t.min && (nextMax > t.max || (nextMax >= t.max && (nextDest > t.dest || (nextDest < t.dest && false)))));
    }
    
    public void getTransition(final int state, final int index, final Transition t) {
        int i = this.states[2 * state] + 3 * index;
        t.source = state;
        t.dest = this.transitions[i++];
        t.min = this.transitions[i++];
        t.max = this.transitions[i++];
    }
    
    static void appendCharString(final int c, final StringBuilder b) {
        if (c >= 33 && c <= 126 && c != 92 && c != 34) {
            b.appendCodePoint(c);
        }
        else {
            b.append("\\\\U");
            final String s = Integer.toHexString(c);
            if (c < 16) {
                b.append("0000000").append(s);
            }
            else if (c < 256) {
                b.append("000000").append(s);
            }
            else if (c < 4096) {
                b.append("00000").append(s);
            }
            else if (c < 65536) {
                b.append("0000").append(s);
            }
            else if (c < 1048576) {
                b.append("000").append(s);
            }
            else if (c < 16777216) {
                b.append("00").append(s);
            }
            else if (c < 268435456) {
                b.append("0").append(s);
            }
            else {
                b.append(s);
            }
        }
    }
    
    public String toDot() {
        final StringBuilder b = new StringBuilder();
        b.append("digraph Automaton {\n");
        b.append("  rankdir = LR\n");
        final int numStates = this.getNumStates();
        if (numStates > 0) {
            b.append("  initial [shape=plaintext,label=\"0\"]\n");
            b.append("  initial -> 0\n");
        }
        final Transition t = new Transition();
        for (int state = 0; state < numStates; ++state) {
            b.append("  ");
            b.append(state);
            if (this.isAccept(state)) {
                b.append(" [shape=doublecircle,label=\"" + state + "\"]\n");
            }
            else {
                b.append(" [shape=circle,label=\"" + state + "\"]\n");
            }
            for (int numTransitions = this.initTransition(state, t), i = 0; i < numTransitions; ++i) {
                this.getNextTransition(t);
                assert t.max >= t.min;
                b.append("  ");
                b.append(state);
                b.append(" -> ");
                b.append(t.dest);
                b.append(" [label=\"");
                appendCharString(t.min, b);
                if (t.max != t.min) {
                    b.append('-');
                    appendCharString(t.max, b);
                }
                b.append("\"]\n");
            }
        }
        b.append('}');
        return b.toString();
    }
    
    int[] getStartPoints() {
        final Set<Integer> pointset = new HashSet<Integer>();
        pointset.add(0);
        for (int s = 0; s < this.nextState; s += 2) {
            for (int trans = this.states[s], limit = trans + 3 * this.states[s + 1]; trans < limit; trans += 3) {
                final int min = this.transitions[trans + 1];
                final int max = this.transitions[trans + 2];
                pointset.add(min);
                if (max < 1114111) {
                    pointset.add(max + 1);
                }
            }
        }
        final int[] points = new int[pointset.size()];
        int n = 0;
        for (final Integer m : pointset) {
            points[n++] = m;
        }
        Arrays.sort(points);
        return points;
    }
    
    public int step(final int state, final int label) {
        assert state >= 0;
        assert label >= 0;
        for (int trans = this.states[2 * state], limit = trans + 3 * this.states[2 * state + 1]; trans < limit; trans += 3) {
            final int dest = this.transitions[trans];
            final int min = this.transitions[trans + 1];
            final int max = this.transitions[trans + 2];
            if (min <= label && label <= max) {
                return dest;
            }
        }
        return -1;
    }
    
    @Override
    public long ramBytesUsed() {
        return RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + RamUsageEstimator.sizeOf(this.states) + RamUsageEstimator.sizeOf(this.transitions) + RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + this.isAccept.size() / 8 + RamUsageEstimator.NUM_BYTES_OBJECT_REF + 2 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + 12L + 1L;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    public static class Builder
    {
        private int nextState;
        private final BitSet isAccept;
        private int[] transitions;
        private int nextTransition;
        private final Sorter sorter;
        
        public Builder() {
            this(16, 16);
        }
        
        public Builder(final int numStates, final int numTransitions) {
            this.nextState = 0;
            this.nextTransition = 0;
            this.sorter = new InPlaceMergeSorter() {
                private void swapOne(final int i, final int j) {
                    final int x = Builder.this.transitions[i];
                    Builder.this.transitions[i] = Builder.this.transitions[j];
                    Builder.this.transitions[j] = x;
                }
                
                @Override
                protected void swap(final int i, final int j) {
                    final int iStart = 4 * i;
                    final int jStart = 4 * j;
                    this.swapOne(iStart, jStart);
                    this.swapOne(iStart + 1, jStart + 1);
                    this.swapOne(iStart + 2, jStart + 2);
                    this.swapOne(iStart + 3, jStart + 3);
                }
                
                @Override
                protected int compare(final int i, final int j) {
                    final int iStart = 4 * i;
                    final int jStart = 4 * j;
                    final int iSrc = Builder.this.transitions[iStart];
                    final int jSrc = Builder.this.transitions[jStart];
                    if (iSrc < jSrc) {
                        return -1;
                    }
                    if (iSrc > jSrc) {
                        return 1;
                    }
                    final int iMin = Builder.this.transitions[iStart + 2];
                    final int jMin = Builder.this.transitions[jStart + 2];
                    if (iMin < jMin) {
                        return -1;
                    }
                    if (iMin > jMin) {
                        return 1;
                    }
                    final int iMax = Builder.this.transitions[iStart + 3];
                    final int jMax = Builder.this.transitions[jStart + 3];
                    if (iMax < jMax) {
                        return -1;
                    }
                    if (iMax > jMax) {
                        return 1;
                    }
                    final int iDest = Builder.this.transitions[iStart + 1];
                    final int jDest = Builder.this.transitions[jStart + 1];
                    if (iDest < jDest) {
                        return -1;
                    }
                    if (iDest > jDest) {
                        return 1;
                    }
                    return 0;
                }
            };
            this.isAccept = new BitSet(numStates);
            this.transitions = new int[numTransitions * 4];
        }
        
        public void addTransition(final int source, final int dest, final int label) {
            this.addTransition(source, dest, label, label);
        }
        
        public void addTransition(final int source, final int dest, final int min, final int max) {
            if (this.transitions.length < this.nextTransition + 4) {
                this.transitions = ArrayUtil.grow(this.transitions, this.nextTransition + 4);
            }
            this.transitions[this.nextTransition++] = source;
            this.transitions[this.nextTransition++] = dest;
            this.transitions[this.nextTransition++] = min;
            this.transitions[this.nextTransition++] = max;
        }
        
        public void addEpsilon(final int source, final int dest) {
            for (int upto = 0; upto < this.nextTransition; upto += 4) {
                if (this.transitions[upto] == dest) {
                    this.addTransition(source, this.transitions[upto + 1], this.transitions[upto + 2], this.transitions[upto + 3]);
                }
            }
            if (this.isAccept(dest)) {
                this.setAccept(source, true);
            }
        }
        
        public Automaton finish() {
            final int numStates = this.nextState;
            final int numTransitions = this.nextTransition / 4;
            final Automaton a = new Automaton(numStates, numTransitions);
            for (int state = 0; state < numStates; ++state) {
                a.createState();
                a.setAccept(state, this.isAccept(state));
            }
            this.sorter.sort(0, numTransitions);
            for (int upto = 0; upto < this.nextTransition; upto += 4) {
                a.addTransition(this.transitions[upto], this.transitions[upto + 1], this.transitions[upto + 2], this.transitions[upto + 3]);
            }
            a.finishState();
            return a;
        }
        
        public int createState() {
            return this.nextState++;
        }
        
        public void setAccept(final int state, final boolean accept) {
            if (state >= this.getNumStates()) {
                throw new IllegalArgumentException("state=" + state + " is out of bounds (numStates=" + this.getNumStates() + ")");
            }
            this.isAccept.set(state, accept);
        }
        
        public boolean isAccept(final int state) {
            return this.isAccept.get(state);
        }
        
        public int getNumStates() {
            return this.nextState;
        }
        
        public void copy(final Automaton other) {
            final int offset = this.getNumStates();
            final int otherNumStates = other.getNumStates();
            this.copyStates(other);
            final Transition t = new Transition();
            for (int s = 0; s < otherNumStates; ++s) {
                for (int count = other.initTransition(s, t), i = 0; i < count; ++i) {
                    other.getNextTransition(t);
                    this.addTransition(offset + s, offset + t.dest, t.min, t.max);
                }
            }
        }
        
        public void copyStates(final Automaton other) {
            for (int otherNumStates = other.getNumStates(), s = 0; s < otherNumStates; ++s) {
                final int newState = this.createState();
                this.setAccept(newState, other.isAccept(s));
            }
        }
    }
}
