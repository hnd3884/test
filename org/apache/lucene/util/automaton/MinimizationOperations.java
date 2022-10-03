package org.apache.lucene.util.automaton;

import java.util.Iterator;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.ArrayList;

public final class MinimizationOperations
{
    private MinimizationOperations() {
    }
    
    public static Automaton minimize(Automaton a, final int maxDeterminizedStates) {
        if (a.getNumStates() == 0 || (!a.isAccept(0) && a.getNumTransitions(0) == 0)) {
            return new Automaton();
        }
        a = Operations.determinize(a, maxDeterminizedStates);
        if (a.getNumTransitions(0) == 1) {
            final Transition t = new Transition();
            a.getTransition(0, 0, t);
            if (t.dest == 0 && t.min == 0 && t.max == 1114111) {
                return a;
            }
        }
        a = Operations.totalize(a);
        final int[] sigma = a.getStartPoints();
        final int sigmaLen = sigma.length;
        final int statesLen = a.getNumStates();
        final ArrayList<Integer>[][] reverse = new ArrayList[statesLen][sigmaLen];
        final HashSet<Integer>[] partition = new HashSet[statesLen];
        final ArrayList<Integer>[] splitblock = new ArrayList[statesLen];
        final int[] block = new int[statesLen];
        final StateList[][] active = new StateList[statesLen][sigmaLen];
        final StateListNode[][] active2 = new StateListNode[statesLen][sigmaLen];
        final LinkedList<IntPair> pending = new LinkedList<IntPair>();
        final BitSet pending2 = new BitSet(sigmaLen * statesLen);
        final BitSet split = new BitSet(statesLen);
        final BitSet refine = new BitSet(statesLen);
        final BitSet refine2 = new BitSet(statesLen);
        for (int q = 0; q < statesLen; ++q) {
            splitblock[q] = new ArrayList<Integer>();
            partition[q] = new HashSet<Integer>();
            for (int x = 0; x < sigmaLen; ++x) {
                active[q][x] = new StateList();
            }
        }
        for (int q = 0; q < statesLen; ++q) {
            final int j = a.isAccept(q) ? 0 : 1;
            partition[j].add(q);
            block[q] = j;
            for (int x2 = 0; x2 < sigmaLen; ++x2) {
                final ArrayList<Integer>[] r = reverse[a.step(q, sigma[x2])];
                if (r[x2] == null) {
                    r[x2] = new ArrayList<Integer>();
                }
                r[x2].add(q);
            }
        }
        for (int i = 0; i <= 1; ++i) {
            for (int x = 0; x < sigmaLen; ++x) {
                for (final int q2 : partition[i]) {
                    if (reverse[q2][x] != null) {
                        active2[q2][x] = active[i][x].add(q2);
                    }
                }
            }
        }
        for (int x3 = 0; x3 < sigmaLen; ++x3) {
            final int j = (active[0][x3].size > active[1][x3].size) ? 1 : 0;
            pending.add(new IntPair(j, x3));
            pending2.set(x3 * statesLen + j);
        }
        int k = 2;
        while (!pending.isEmpty()) {
            final IntPair ip = pending.removeFirst();
            final int p = ip.n1;
            final int x4 = ip.n2;
            pending2.clear(x4 * statesLen + p);
            for (StateListNode m = active[p][x4].first; m != null; m = m.next) {
                final ArrayList<Integer> r2 = reverse[m.q][x4];
                if (r2 != null) {
                    for (final int l : r2) {
                        if (!split.get(l)) {
                            split.set(l);
                            final int j2 = block[l];
                            splitblock[j2].add(l);
                            if (refine2.get(j2)) {
                                continue;
                            }
                            refine2.set(j2);
                            refine.set(j2);
                        }
                    }
                }
            }
            for (int j3 = refine.nextSetBit(0); j3 >= 0; j3 = refine.nextSetBit(j3 + 1)) {
                final ArrayList<Integer> sb = splitblock[j3];
                if (sb.size() < partition[j3].size()) {
                    final HashSet<Integer> b1 = partition[j3];
                    final HashSet<Integer> b2 = partition[k];
                    for (final int s : sb) {
                        b1.remove(s);
                        b2.add(s);
                        block[s] = k;
                        for (int c = 0; c < sigmaLen; ++c) {
                            final StateListNode sn = active2[s][c];
                            if (sn != null && sn.sl == active[j3][c]) {
                                sn.remove();
                                active2[s][c] = active[k][c].add(s);
                            }
                        }
                    }
                    for (int c2 = 0; c2 < sigmaLen; ++c2) {
                        final int aj = active[j3][c2].size;
                        final int ak = active[k][c2].size;
                        final int ofs = c2 * statesLen;
                        if (!pending2.get(ofs + j3) && 0 < aj && aj <= ak) {
                            pending2.set(ofs + j3);
                            pending.add(new IntPair(j3, c2));
                        }
                        else {
                            pending2.set(ofs + k);
                            pending.add(new IntPair(k, c2));
                        }
                    }
                    ++k;
                }
                refine2.clear(j3);
                for (final int s2 : sb) {
                    split.clear(s2);
                }
                sb.clear();
            }
            refine.clear();
        }
        final Automaton result = new Automaton();
        final Transition t2 = new Transition();
        final int[] stateMap = new int[statesLen];
        final int[] stateRep = new int[k];
        result.createState();
        for (int n = 0; n < k; ++n) {
            boolean isInitial = false;
            for (final int q3 : partition[n]) {
                if (q3 == 0) {
                    isInitial = true;
                    break;
                }
            }
            int newState;
            if (isInitial) {
                newState = 0;
            }
            else {
                newState = result.createState();
            }
            for (final int q4 : partition[n]) {
                result.setAccept(stateMap[q4] = newState, a.isAccept(q4));
                stateRep[newState] = q4;
            }
        }
        for (int n = 0; n < k; ++n) {
            for (int numTransitions = a.initTransition(stateRep[n], t2), l = 0; l < numTransitions; ++l) {
                a.getNextTransition(t2);
                result.addTransition(n, stateMap[t2.dest], t2.min, t2.max);
            }
        }
        result.finishState();
        return Operations.removeDeadStates(result);
    }
    
    static final class IntPair
    {
        final int n1;
        final int n2;
        
        IntPair(final int n1, final int n2) {
            this.n1 = n1;
            this.n2 = n2;
        }
    }
    
    static final class StateList
    {
        int size;
        StateListNode first;
        StateListNode last;
        
        StateListNode add(final int q) {
            return new StateListNode(q, this);
        }
    }
    
    static final class StateListNode
    {
        final int q;
        StateListNode next;
        StateListNode prev;
        final StateList sl;
        
        StateListNode(final int q, final StateList sl) {
            this.q = q;
            this.sl = sl;
            if (sl.size++ == 0) {
                sl.last = this;
                sl.first = this;
            }
            else {
                sl.last.next = this;
                this.prev = sl.last;
                sl.last = this;
            }
        }
        
        void remove() {
            final StateList sl = this.sl;
            --sl.size;
            if (this.sl.first == this) {
                this.sl.first = this.next;
            }
            else {
                this.prev.next = this.next;
            }
            if (this.sl.last == this) {
                this.sl.last = this.prev;
            }
            else {
                this.next.prev = this.prev;
            }
        }
    }
}
