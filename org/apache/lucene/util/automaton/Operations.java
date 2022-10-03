package org.apache.lucene.util.automaton;

import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IntsRef;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;

public final class Operations
{
    public static final int DEFAULT_MAX_DETERMINIZED_STATES = 10000;
    
    private Operations() {
    }
    
    public static Automaton concatenate(final Automaton a1, final Automaton a2) {
        return concatenate(Arrays.asList(a1, a2));
    }
    
    public static Automaton concatenate(final List<Automaton> l) {
        final Automaton result = new Automaton();
        for (final Automaton a : l) {
            if (a.getNumStates() == 0) {
                result.finishState();
                return result;
            }
            for (int numStates = a.getNumStates(), s = 0; s < numStates; ++s) {
                result.createState();
            }
        }
        int stateOffset = 0;
        final Transition t = new Transition();
        for (int i = 0; i < l.size(); ++i) {
            final Automaton a2 = l.get(i);
            final int numStates2 = a2.getNumStates();
            final Automaton nextA = (i == l.size() - 1) ? null : l.get(i + 1);
        Label_0371:
            for (int s2 = 0; s2 < numStates2; ++s2) {
                for (int numTransitions = a2.initTransition(s2, t), j = 0; j < numTransitions; ++j) {
                    a2.getNextTransition(t);
                    result.addTransition(stateOffset + s2, stateOffset + t.dest, t.min, t.max);
                }
                if (a2.isAccept(s2)) {
                    Automaton followA = nextA;
                    int followOffset = stateOffset;
                    for (int upto = i + 1; followA != null; followA = ((upto == l.size() - 1) ? null : l.get(upto + 1)), ++upto) {
                        for (int numTransitions = followA.initTransition(0, t), k = 0; k < numTransitions; ++k) {
                            followA.getNextTransition(t);
                            result.addTransition(stateOffset + s2, followOffset + numStates2 + t.dest, t.min, t.max);
                        }
                        if (!followA.isAccept(0)) {
                            continue Label_0371;
                        }
                        followOffset += followA.getNumStates();
                    }
                    result.setAccept(stateOffset + s2, true);
                }
            }
            stateOffset += numStates2;
        }
        if (result.getNumStates() == 0) {
            result.createState();
        }
        result.finishState();
        return result;
    }
    
    public static Automaton optional(final Automaton a) {
        final Automaton result = new Automaton();
        result.createState();
        result.setAccept(0, true);
        if (a.getNumStates() > 0) {
            result.copy(a);
            result.addEpsilon(0, 1);
        }
        result.finishState();
        return result;
    }
    
    public static Automaton repeat(final Automaton a) {
        if (a.getNumStates() == 0) {
            return a;
        }
        final Automaton.Builder builder = new Automaton.Builder();
        builder.createState();
        builder.setAccept(0, true);
        builder.copy(a);
        final Transition t = new Transition();
        for (int count = a.initTransition(0, t), i = 0; i < count; ++i) {
            a.getNextTransition(t);
            builder.addTransition(0, t.dest + 1, t.min, t.max);
        }
        for (int numStates = a.getNumStates(), s = 0; s < numStates; ++s) {
            if (a.isAccept(s)) {
                for (int count = a.initTransition(0, t), j = 0; j < count; ++j) {
                    a.getNextTransition(t);
                    builder.addTransition(s + 1, t.dest + 1, t.min, t.max);
                }
            }
        }
        return builder.finish();
    }
    
    public static Automaton repeat(final Automaton a, int count) {
        if (count == 0) {
            return repeat(a);
        }
        final List<Automaton> as = new ArrayList<Automaton>();
        while (count-- > 0) {
            as.add(a);
        }
        as.add(repeat(a));
        return concatenate(as);
    }
    
    public static Automaton repeat(final Automaton a, final int min, final int max) {
        if (min > max) {
            return Automata.makeEmpty();
        }
        Automaton b;
        if (min == 0) {
            b = Automata.makeEmptyString();
        }
        else if (min == 1) {
            b = new Automaton();
            b.copy(a);
        }
        else {
            final List<Automaton> as = new ArrayList<Automaton>();
            for (int i = 0; i < min; ++i) {
                as.add(a);
            }
            b = concatenate(as);
        }
        Set<Integer> prevAcceptStates = toSet(b, 0);
        final Automaton.Builder builder = new Automaton.Builder();
        builder.copy(b);
        for (int j = min; j < max; ++j) {
            final int numStates = builder.getNumStates();
            builder.copy(a);
            for (final int s : prevAcceptStates) {
                builder.addEpsilon(s, numStates);
            }
            prevAcceptStates = toSet(a, numStates);
        }
        return builder.finish();
    }
    
    private static Set<Integer> toSet(final Automaton a, final int offset) {
        final int numStates = a.getNumStates();
        final BitSet isAccept = a.getAcceptStates();
        final Set<Integer> result = new HashSet<Integer>();
        for (int upto = 0; upto < numStates && (upto = isAccept.nextSetBit(upto)) != -1; ++upto) {
            result.add(offset + upto);
        }
        return result;
    }
    
    public static Automaton complement(Automaton a, final int maxDeterminizedStates) {
        a = totalize(determinize(a, maxDeterminizedStates));
        for (int numStates = a.getNumStates(), p = 0; p < numStates; ++p) {
            a.setAccept(p, !a.isAccept(p));
        }
        return removeDeadStates(a);
    }
    
    public static Automaton minus(final Automaton a1, final Automaton a2, final int maxDeterminizedStates) {
        if (isEmpty(a1) || a1 == a2) {
            return Automata.makeEmpty();
        }
        if (isEmpty(a2)) {
            return a1;
        }
        return intersection(a1, complement(a2, maxDeterminizedStates));
    }
    
    public static Automaton intersection(final Automaton a1, final Automaton a2) {
        if (a1 == a2) {
            return a1;
        }
        if (a1.getNumStates() == 0) {
            return a1;
        }
        if (a2.getNumStates() == 0) {
            return a2;
        }
        final Transition[][] transitions1 = a1.getSortedTransitions();
        final Transition[][] transitions2 = a2.getSortedTransitions();
        final Automaton c = new Automaton();
        c.createState();
        final LinkedList<StatePair> worklist = new LinkedList<StatePair>();
        final HashMap<StatePair, StatePair> newstates = new HashMap<StatePair, StatePair>();
        StatePair p = new StatePair(0, 0, 0);
        worklist.add(p);
        newstates.put(p, p);
        while (worklist.size() > 0) {
            p = worklist.removeFirst();
            c.setAccept(p.s, a1.isAccept(p.s1) && a2.isAccept(p.s2));
            final Transition[] t1 = transitions1[p.s1];
            final Transition[] t2 = transitions2[p.s2];
            int n1 = 0;
            int b2 = 0;
            while (n1 < t1.length) {
                while (b2 < t2.length && t2[b2].max < t1[n1].min) {
                    ++b2;
                }
                for (int n2 = b2; n2 < t2.length && t1[n1].max >= t2[n2].min; ++n2) {
                    if (t2[n2].max >= t1[n1].min) {
                        final StatePair q = new StatePair(t1[n1].dest, t2[n2].dest);
                        StatePair r = newstates.get(q);
                        if (r == null) {
                            q.s = c.createState();
                            worklist.add(q);
                            newstates.put(q, q);
                            r = q;
                        }
                        final int min = (t1[n1].min > t2[n2].min) ? t1[n1].min : t2[n2].min;
                        final int max = (t1[n1].max < t2[n2].max) ? t1[n1].max : t2[n2].max;
                        c.addTransition(p.s, r.s, min, max);
                    }
                }
                ++n1;
            }
        }
        c.finishState();
        return removeDeadStates(c);
    }
    
    public static boolean sameLanguage(final Automaton a1, final Automaton a2) {
        return a1 == a2 || (subsetOf(a2, a1) && subsetOf(a1, a2));
    }
    
    public static boolean hasDeadStates(final Automaton a) {
        final BitSet liveStates = getLiveStates(a);
        final int numLive = liveStates.cardinality();
        final int numStates = a.getNumStates();
        assert numLive <= numStates : "numLive=" + numLive + " numStates=" + numStates + " " + liveStates;
        return numLive < numStates;
    }
    
    public static boolean hasDeadStatesFromInitial(final Automaton a) {
        final BitSet reachableFromInitial = getLiveStatesFromInitial(a);
        final BitSet reachableFromAccept = getLiveStatesToAccept(a);
        reachableFromInitial.andNot(reachableFromAccept);
        return !reachableFromInitial.isEmpty();
    }
    
    public static boolean hasDeadStatesToAccept(final Automaton a) {
        final BitSet reachableFromInitial = getLiveStatesFromInitial(a);
        final BitSet reachableFromAccept = getLiveStatesToAccept(a);
        reachableFromAccept.andNot(reachableFromInitial);
        return !reachableFromAccept.isEmpty();
    }
    
    public static boolean subsetOf(final Automaton a1, final Automaton a2) {
        if (!a1.isDeterministic()) {
            throw new IllegalArgumentException("a1 must be deterministic");
        }
        if (!a2.isDeterministic()) {
            throw new IllegalArgumentException("a2 must be deterministic");
        }
        assert !hasDeadStatesFromInitial(a1);
        assert !hasDeadStatesFromInitial(a2);
        if (a1.getNumStates() == 0) {
            return true;
        }
        if (a2.getNumStates() == 0) {
            return isEmpty(a1);
        }
        final Transition[][] transitions1 = a1.getSortedTransitions();
        final Transition[][] transitions2 = a2.getSortedTransitions();
        final LinkedList<StatePair> worklist = new LinkedList<StatePair>();
        final HashSet<StatePair> visited = new HashSet<StatePair>();
        StatePair p = new StatePair(0, 0);
        worklist.add(p);
        visited.add(p);
        while (worklist.size() > 0) {
            p = worklist.removeFirst();
            if (a1.isAccept(p.s1) && !a2.isAccept(p.s2)) {
                return false;
            }
            final Transition[] t1 = transitions1[p.s1];
            final Transition[] t2 = transitions2[p.s2];
            int n1 = 0;
            int b2 = 0;
            while (n1 < t1.length) {
                while (b2 < t2.length && t2[b2].max < t1[n1].min) {
                    ++b2;
                }
                int min1 = t1[n1].min;
                int max1 = t1[n1].max;
                for (int n2 = b2; n2 < t2.length && t1[n1].max >= t2[n2].min; ++n2) {
                    if (t2[n2].min > min1) {
                        return false;
                    }
                    if (t2[n2].max < 1114111) {
                        min1 = t2[n2].max + 1;
                    }
                    else {
                        min1 = 1114111;
                        max1 = 0;
                    }
                    final StatePair q = new StatePair(t1[n1].dest, t2[n2].dest);
                    if (!visited.contains(q)) {
                        worklist.add(q);
                        visited.add(q);
                    }
                }
                if (min1 <= max1) {
                    return false;
                }
                ++n1;
            }
        }
        return true;
    }
    
    public static Automaton union(final Automaton a1, final Automaton a2) {
        return union(Arrays.asList(a1, a2));
    }
    
    public static Automaton union(final Collection<Automaton> l) {
        final Automaton result = new Automaton();
        result.createState();
        for (final Automaton a : l) {
            result.copy(a);
        }
        int stateOffset = 1;
        for (final Automaton a2 : l) {
            if (a2.getNumStates() == 0) {
                continue;
            }
            result.addEpsilon(0, stateOffset);
            stateOffset += a2.getNumStates();
        }
        result.finishState();
        return removeDeadStates(result);
    }
    
    public static Automaton determinize(final Automaton a, final int maxDeterminizedStates) {
        if (a.isDeterministic()) {
            return a;
        }
        if (a.getNumStates() <= 1) {
            return a;
        }
        final Automaton.Builder b = new Automaton.Builder();
        final SortedIntSet.FrozenIntSet initialset = new SortedIntSet.FrozenIntSet(0, 0);
        b.createState();
        final LinkedList<SortedIntSet.FrozenIntSet> worklist = new LinkedList<SortedIntSet.FrozenIntSet>();
        final Map<SortedIntSet.FrozenIntSet, Integer> newstate = new HashMap<SortedIntSet.FrozenIntSet, Integer>();
        worklist.add(initialset);
        b.setAccept(0, a.isAccept(0));
        newstate.put(initialset, 0);
        final PointTransitionSet points = new PointTransitionSet();
        final SortedIntSet statesSet = new SortedIntSet(5);
        final Transition t = new Transition();
        while (worklist.size() > 0) {
            final SortedIntSet.FrozenIntSet s = worklist.removeFirst();
            for (int i = 0; i < s.values.length; ++i) {
                final int s2 = s.values[i];
                final int numTransitions = a.getNumTransitions(s2);
                a.initTransition(s2, t);
                for (int j = 0; j < numTransitions; ++j) {
                    a.getNextTransition(t);
                    points.add(t);
                }
            }
            if (points.count == 0) {
                continue;
            }
            points.sort();
            int lastPoint = -1;
            int accCount = 0;
            final int r = s.state;
            for (int k = 0; k < points.count; ++k) {
                final int point = points.points[k].point;
                if (statesSet.upto > 0) {
                    assert lastPoint != -1;
                    statesSet.computeHash();
                    Integer q = newstate.get(statesSet);
                    if (q == null) {
                        q = b.createState();
                        if (q >= maxDeterminizedStates) {
                            throw new TooComplexToDeterminizeException(a, maxDeterminizedStates);
                        }
                        final SortedIntSet.FrozenIntSet p = statesSet.freeze(q);
                        worklist.add(p);
                        b.setAccept(q, accCount > 0);
                        newstate.put(p, q);
                    }
                    else {
                        assert accCount > 0 == b.isAccept(q) : "accCount=" + accCount + " vs existing accept=" + b.isAccept(q) + " states=" + statesSet;
                    }
                    b.addTransition(r, q, lastPoint, point - 1);
                }
                int[] transitions = points.points[k].ends.transitions;
                for (int limit = points.points[k].ends.next, l = 0; l < limit; l += 3) {
                    final int dest = transitions[l];
                    statesSet.decr(dest);
                    accCount -= (a.isAccept(dest) ? 1 : 0);
                }
                points.points[k].ends.next = 0;
                transitions = points.points[k].starts.transitions;
                for (int limit = points.points[k].starts.next, l = 0; l < limit; l += 3) {
                    final int dest = transitions[l];
                    statesSet.incr(dest);
                    accCount += (a.isAccept(dest) ? 1 : 0);
                }
                lastPoint = point;
                points.points[k].starts.next = 0;
            }
            points.reset();
            assert statesSet.upto == 0 : "upto=" + statesSet.upto;
        }
        final Automaton result = b.finish();
        assert result.isDeterministic();
        return result;
    }
    
    public static boolean isEmpty(final Automaton a) {
        if (a.getNumStates() == 0) {
            return true;
        }
        if (!a.isAccept(0) && a.getNumTransitions(0) == 0) {
            return true;
        }
        if (a.isAccept(0)) {
            return false;
        }
        final LinkedList<Integer> workList = new LinkedList<Integer>();
        final BitSet seen = new BitSet(a.getNumStates());
        workList.add(0);
        seen.set(0);
        final Transition t = new Transition();
        while (!workList.isEmpty()) {
            final int state = workList.removeFirst();
            if (a.isAccept(state)) {
                return false;
            }
            for (int count = a.initTransition(state, t), i = 0; i < count; ++i) {
                a.getNextTransition(t);
                if (!seen.get(t.dest)) {
                    workList.add(t.dest);
                    seen.set(t.dest);
                }
            }
        }
        return true;
    }
    
    public static boolean isTotal(final Automaton a) {
        return isTotal(a, 0, 1114111);
    }
    
    public static boolean isTotal(final Automaton a, final int minAlphabet, final int maxAlphabet) {
        if (a.isAccept(0) && a.getNumTransitions(0) == 1) {
            final Transition t = new Transition();
            a.getTransition(0, 0, t);
            return t.dest == 0 && t.min == minAlphabet && t.max == maxAlphabet;
        }
        return false;
    }
    
    public static boolean run(final Automaton a, final String s) {
        assert a.isDeterministic();
        int state = 0;
        for (int i = 0, cp = 0; i < s.length(); i += Character.charCount(cp)) {
            final int nextState = a.step(state, cp = s.codePointAt(i));
            if (nextState == -1) {
                return false;
            }
            state = nextState;
        }
        return a.isAccept(state);
    }
    
    public static boolean run(final Automaton a, final IntsRef s) {
        assert a.isDeterministic();
        int state = 0;
        for (int i = 0; i < s.length; ++i) {
            final int nextState = a.step(state, s.ints[s.offset + i]);
            if (nextState == -1) {
                return false;
            }
            state = nextState;
        }
        return a.isAccept(state);
    }
    
    private static BitSet getLiveStates(final Automaton a) {
        final BitSet live = getLiveStatesFromInitial(a);
        live.and(getLiveStatesToAccept(a));
        return live;
    }
    
    private static BitSet getLiveStatesFromInitial(final Automaton a) {
        final int numStates = a.getNumStates();
        final BitSet live = new BitSet(numStates);
        if (numStates == 0) {
            return live;
        }
        final LinkedList<Integer> workList = new LinkedList<Integer>();
        live.set(0);
        workList.add(0);
        final Transition t = new Transition();
        while (!workList.isEmpty()) {
            final int s = workList.removeFirst();
            for (int count = a.initTransition(s, t), i = 0; i < count; ++i) {
                a.getNextTransition(t);
                if (!live.get(t.dest)) {
                    live.set(t.dest);
                    workList.add(t.dest);
                }
            }
        }
        return live;
    }
    
    private static BitSet getLiveStatesToAccept(final Automaton a) {
        final Automaton.Builder builder = new Automaton.Builder();
        final Transition t = new Transition();
        final int numStates = a.getNumStates();
        for (int s = 0; s < numStates; ++s) {
            builder.createState();
        }
        for (int s = 0; s < numStates; ++s) {
            for (int count = a.initTransition(s, t), i = 0; i < count; ++i) {
                a.getNextTransition(t);
                builder.addTransition(t.dest, s, t.min, t.max);
            }
        }
        final Automaton a2 = builder.finish();
        final LinkedList<Integer> workList = new LinkedList<Integer>();
        final BitSet live = new BitSet(numStates);
        final BitSet acceptBits = a.getAcceptStates();
        for (int s2 = 0; s2 < numStates && (s2 = acceptBits.nextSetBit(s2)) != -1; ++s2) {
            live.set(s2);
            workList.add(s2);
        }
        while (!workList.isEmpty()) {
            final int s2 = workList.removeFirst();
            for (int count2 = a2.initTransition(s2, t), j = 0; j < count2; ++j) {
                a2.getNextTransition(t);
                if (!live.get(t.dest)) {
                    live.set(t.dest);
                    workList.add(t.dest);
                }
            }
        }
        return live;
    }
    
    public static Automaton removeDeadStates(final Automaton a) {
        final int numStates = a.getNumStates();
        final BitSet liveSet = getLiveStates(a);
        final int[] map = new int[numStates];
        final Automaton result = new Automaton();
        for (int i = 0; i < numStates; ++i) {
            if (liveSet.get(i)) {
                result.setAccept(map[i] = result.createState(), a.isAccept(i));
            }
        }
        final Transition t = new Transition();
        for (int j = 0; j < numStates; ++j) {
            if (liveSet.get(j)) {
                for (int numTransitions = a.initTransition(j, t), k = 0; k < numTransitions; ++k) {
                    a.getNextTransition(t);
                    if (liveSet.get(t.dest)) {
                        result.addTransition(map[j], map[t.dest], t.min, t.max);
                    }
                }
            }
        }
        result.finishState();
        assert !hasDeadStates(result);
        return result;
    }
    
    static int findIndex(final int c, final int[] points) {
        int a = 0;
        int b = points.length;
        while (b - a > 1) {
            final int d = a + b >>> 1;
            if (points[d] > c) {
                b = d;
            }
            else {
                if (points[d] >= c) {
                    return d;
                }
                a = d;
            }
        }
        return a;
    }
    
    public static boolean isFinite(final Automaton a) {
        return a.getNumStates() == 0 || isFinite(new Transition(), a, 0, new BitSet(a.getNumStates()), new BitSet(a.getNumStates()));
    }
    
    private static boolean isFinite(final Transition scratch, final Automaton a, final int state, final BitSet path, final BitSet visited) {
        path.set(state);
        for (int numTransitions = a.initTransition(state, scratch), t = 0; t < numTransitions; ++t) {
            a.getTransition(state, t, scratch);
            if (path.get(scratch.dest) || (!visited.get(scratch.dest) && !isFinite(scratch, a, scratch.dest, path, visited))) {
                return false;
            }
        }
        path.clear(state);
        visited.set(state);
        return true;
    }
    
    public static String getCommonPrefix(final Automaton a) {
        if (!a.isDeterministic()) {
            throw new IllegalArgumentException("input automaton must be deterministic");
        }
        final StringBuilder b = new StringBuilder();
        final HashSet<Integer> visited = new HashSet<Integer>();
        int s = 0;
        final Transition t = new Transition();
        boolean done;
        do {
            done = true;
            visited.add(s);
            if (!a.isAccept(s) && a.getNumTransitions(s) == 1) {
                a.getTransition(s, 0, t);
                if (t.min != t.max || visited.contains(t.dest)) {
                    continue;
                }
                b.appendCodePoint(t.min);
                s = t.dest;
                done = false;
            }
        } while (!done);
        return b.toString();
    }
    
    public static BytesRef getCommonPrefixBytesRef(final Automaton a) {
        final BytesRefBuilder builder = new BytesRefBuilder();
        final HashSet<Integer> visited = new HashSet<Integer>();
        int s = 0;
        final Transition t = new Transition();
        boolean done;
        do {
            done = true;
            visited.add(s);
            if (!a.isAccept(s) && a.getNumTransitions(s) == 1) {
                a.getTransition(s, 0, t);
                if (t.min != t.max || visited.contains(t.dest)) {
                    continue;
                }
                builder.append((byte)t.min);
                s = t.dest;
                done = false;
            }
        } while (!done);
        return builder.get();
    }
    
    public static IntsRef getSingleton(final Automaton a) {
        if (!a.isDeterministic()) {
            throw new IllegalArgumentException("input automaton must be deterministic");
        }
        final IntsRefBuilder builder = new IntsRefBuilder();
        final HashSet<Integer> visited = new HashSet<Integer>();
        int s = 0;
        final Transition t = new Transition();
        while (true) {
            visited.add(s);
            if (!a.isAccept(s)) {
                if (a.getNumTransitions(s) != 1) {
                    break;
                }
                a.getTransition(s, 0, t);
                if (t.min != t.max || visited.contains(t.dest)) {
                    break;
                }
                builder.append(t.min);
                s = t.dest;
            }
            else {
                if (a.getNumTransitions(s) == 0) {
                    return builder.get();
                }
                break;
            }
        }
        return null;
    }
    
    public static BytesRef getCommonSuffixBytesRef(final Automaton a, final int maxDeterminizedStates) {
        final Automaton r = determinize(reverse(a), maxDeterminizedStates);
        final BytesRef ref = getCommonPrefixBytesRef(r);
        reverseBytes(ref);
        return ref;
    }
    
    private static void reverseBytes(final BytesRef ref) {
        if (ref.length <= 1) {
            return;
        }
        for (int num = ref.length >> 1, i = ref.offset; i < ref.offset + num; ++i) {
            final byte b = ref.bytes[i];
            ref.bytes[i] = ref.bytes[ref.offset * 2 + ref.length - i - 1];
            ref.bytes[ref.offset * 2 + ref.length - i - 1] = b;
        }
    }
    
    public static Automaton reverse(final Automaton a) {
        return reverse(a, null);
    }
    
    static Automaton reverse(final Automaton a, final Set<Integer> initialStates) {
        if (isEmpty(a)) {
            return new Automaton();
        }
        final int numStates = a.getNumStates();
        final Automaton.Builder builder = new Automaton.Builder();
        builder.createState();
        for (int s = 0; s < numStates; ++s) {
            builder.createState();
        }
        builder.setAccept(1, true);
        final Transition t = new Transition();
        for (int s2 = 0; s2 < numStates; ++s2) {
            final int numTransitions = a.getNumTransitions(s2);
            a.initTransition(s2, t);
            for (int i = 0; i < numTransitions; ++i) {
                a.getNextTransition(t);
                builder.addTransition(t.dest + 1, s2 + 1, t.min, t.max);
            }
        }
        final Automaton result = builder.finish();
        int s3 = 0;
        for (BitSet acceptStates = a.getAcceptStates(); s3 < numStates && (s3 = acceptStates.nextSetBit(s3)) != -1; ++s3) {
            result.addEpsilon(0, s3 + 1);
            if (initialStates != null) {
                initialStates.add(s3 + 1);
            }
        }
        result.finishState();
        return result;
    }
    
    static Automaton totalize(final Automaton a) {
        final Automaton result = new Automaton();
        final int numStates = a.getNumStates();
        for (int i = 0; i < numStates; ++i) {
            result.createState();
            result.setAccept(i, a.isAccept(i));
        }
        final int deadState = result.createState();
        result.addTransition(deadState, deadState, 0, 1114111);
        final Transition t = new Transition();
        for (int j = 0; j < numStates; ++j) {
            int maxi = 0;
            for (int count = a.initTransition(j, t), k = 0; k < count; ++k) {
                a.getNextTransition(t);
                result.addTransition(j, t.dest, t.min, t.max);
                if (t.min > maxi) {
                    result.addTransition(j, deadState, maxi, t.min - 1);
                }
                if (t.max + 1 > maxi) {
                    maxi = t.max + 1;
                }
            }
            if (maxi <= 1114111) {
                result.addTransition(j, deadState, maxi, 1114111);
            }
        }
        result.finishState();
        return result;
    }
    
    public static int[] topoSortStates(final Automaton a) {
        if (a.getNumStates() == 0) {
            return new int[0];
        }
        final int numStates = a.getNumStates();
        int[] states = new int[numStates];
        final BitSet visited = new BitSet(numStates);
        final int upto = topoSortStatesRecurse(a, visited, states, 0, 0);
        if (upto < states.length) {
            final int[] newStates = new int[upto];
            System.arraycopy(states, 0, newStates, 0, upto);
            states = newStates;
        }
        for (int i = 0; i < states.length / 2; ++i) {
            final int s = states[i];
            states[i] = states[states.length - 1 - i];
            states[states.length - 1 - i] = s;
        }
        return states;
    }
    
    private static int topoSortStatesRecurse(final Automaton a, final BitSet visited, final int[] states, int upto, final int state) {
        final Transition t = new Transition();
        for (int count = a.initTransition(state, t), i = 0; i < count; ++i) {
            a.getNextTransition(t);
            if (!visited.get(t.dest)) {
                visited.set(t.dest);
                upto = topoSortStatesRecurse(a, visited, states, upto, t.dest);
            }
        }
        states[upto] = state;
        return ++upto;
    }
    
    private static final class TransitionList
    {
        int[] transitions;
        int next;
        
        private TransitionList() {
            this.transitions = new int[3];
        }
        
        public void add(final Transition t) {
            if (this.transitions.length < this.next + 3) {
                this.transitions = ArrayUtil.grow(this.transitions, this.next + 3);
            }
            this.transitions[this.next] = t.dest;
            this.transitions[this.next + 1] = t.min;
            this.transitions[this.next + 2] = t.max;
            this.next += 3;
        }
    }
    
    private static final class PointTransitions implements Comparable<PointTransitions>
    {
        int point;
        final TransitionList ends;
        final TransitionList starts;
        
        private PointTransitions() {
            this.ends = new TransitionList();
            this.starts = new TransitionList();
        }
        
        @Override
        public int compareTo(final PointTransitions other) {
            return this.point - other.point;
        }
        
        public void reset(final int point) {
            this.point = point;
            this.ends.next = 0;
            this.starts.next = 0;
        }
        
        @Override
        public boolean equals(final Object other) {
            return ((PointTransitions)other).point == this.point;
        }
        
        @Override
        public int hashCode() {
            return this.point;
        }
    }
    
    private static final class PointTransitionSet
    {
        int count;
        PointTransitions[] points;
        private static final int HASHMAP_CUTOVER = 30;
        private final HashMap<Integer, PointTransitions> map;
        private boolean useHash;
        
        private PointTransitionSet() {
            this.points = new PointTransitions[5];
            this.map = new HashMap<Integer, PointTransitions>();
            this.useHash = false;
        }
        
        private PointTransitions next(final int point) {
            if (this.count == this.points.length) {
                final PointTransitions[] newArray = new PointTransitions[ArrayUtil.oversize(1 + this.count, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                System.arraycopy(this.points, 0, newArray, 0, this.count);
                this.points = newArray;
            }
            PointTransitions points0 = this.points[this.count];
            if (points0 == null) {
                final PointTransitions[] points2 = this.points;
                final int count = this.count;
                final PointTransitions pointTransitions = new PointTransitions();
                points2[count] = pointTransitions;
                points0 = pointTransitions;
            }
            points0.reset(point);
            ++this.count;
            return points0;
        }
        
        private PointTransitions find(final int point) {
            if (this.useHash) {
                final Integer pi = point;
                PointTransitions p = this.map.get(pi);
                if (p == null) {
                    p = this.next(point);
                    this.map.put(pi, p);
                }
                return p;
            }
            for (int i = 0; i < this.count; ++i) {
                if (this.points[i].point == point) {
                    return this.points[i];
                }
            }
            final PointTransitions p2 = this.next(point);
            if (this.count == 30) {
                assert this.map.size() == 0;
                for (int j = 0; j < this.count; ++j) {
                    this.map.put(this.points[j].point, this.points[j]);
                }
                this.useHash = true;
            }
            return p2;
        }
        
        public void reset() {
            if (this.useHash) {
                this.map.clear();
                this.useHash = false;
            }
            this.count = 0;
        }
        
        public void sort() {
            if (this.count > 1) {
                ArrayUtil.timSort(this.points, 0, this.count);
            }
        }
        
        public void add(final Transition t) {
            this.find(t.min).starts.add(t);
            this.find(1 + t.max).ends.add(t);
        }
        
        @Override
        public String toString() {
            final StringBuilder s = new StringBuilder();
            for (int i = 0; i < this.count; ++i) {
                if (i > 0) {
                    s.append(' ');
                }
                s.append(this.points[i].point).append(':').append(this.points[i].starts.next / 3).append(',').append(this.points[i].ends.next / 3);
            }
            return s.toString();
        }
    }
}
