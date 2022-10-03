package org.apache.lucene.util.automaton;

import java.util.Arrays;
import java.util.Iterator;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Comparator;
import org.apache.lucene.util.CharsRef;
import java.util.HashMap;

final class DaciukMihovAutomatonBuilder
{
    private HashMap<State, State> stateRegistry;
    private State root;
    private CharsRef previous;
    private static final Comparator<CharsRef> comparator;
    
    DaciukMihovAutomatonBuilder() {
        this.stateRegistry = new HashMap<State, State>();
        this.root = new State();
    }
    
    public void add(final CharsRef current) {
        assert this.stateRegistry != null : "Automaton already built.";
        assert DaciukMihovAutomatonBuilder.comparator.compare(this.previous, current) <= 0 : "Input must be in sorted UTF-8 order: " + (Object)this.previous + " >= " + (Object)current;
        assert this.setPrevious(current);
        int pos;
        int max;
        State state;
        State next;
        for (pos = 0, max = current.length(), state = this.root; pos < max && (next = state.lastChild(Character.codePointAt(current, pos))) != null; state = next, pos += Character.charCount(Character.codePointAt(current, pos))) {}
        if (state.hasChildren()) {
            this.replaceOrRegister(state);
        }
        this.addSuffix(state, current, pos);
    }
    
    public State complete() {
        if (this.stateRegistry == null) {
            throw new IllegalStateException();
        }
        if (this.root.hasChildren()) {
            this.replaceOrRegister(this.root);
        }
        this.stateRegistry = null;
        return this.root;
    }
    
    private static int convert(final Automaton.Builder a, final State s, final IdentityHashMap<State, Integer> visited) {
        Integer converted = visited.get(s);
        if (converted != null) {
            return converted;
        }
        converted = a.createState();
        a.setAccept(converted, s.is_final);
        visited.put(s, converted);
        int i = 0;
        final int[] labels = s.labels;
        for (final State target : s.states) {
            a.addTransition(converted, convert(a, target, visited), labels[i++]);
        }
        return converted;
    }
    
    public static Automaton build(final Collection<BytesRef> input) {
        final DaciukMihovAutomatonBuilder builder = new DaciukMihovAutomatonBuilder();
        char[] chars = new char[0];
        final CharsRef ref = new CharsRef();
        for (final BytesRef b : input) {
            chars = ArrayUtil.grow(chars, b.length);
            final int len = UnicodeUtil.UTF8toUTF16(b, chars);
            ref.chars = chars;
            ref.length = len;
            builder.add(ref);
        }
        final Automaton.Builder a = new Automaton.Builder();
        convert(a, builder.complete(), new IdentityHashMap<State, Integer>());
        return a.finish();
    }
    
    private boolean setPrevious(final CharsRef current) {
        this.previous = CharsRef.deepCopyOf(current);
        return true;
    }
    
    private void replaceOrRegister(final State state) {
        final State child = state.lastChild();
        if (child.hasChildren()) {
            this.replaceOrRegister(child);
        }
        final State registered = this.stateRegistry.get(child);
        if (registered != null) {
            state.replaceLastChild(registered);
        }
        else {
            this.stateRegistry.put(child, child);
        }
    }
    
    private void addSuffix(State state, final CharSequence current, int fromIndex) {
        int cp;
        for (int len = current.length(); fromIndex < len; fromIndex += Character.charCount(cp)) {
            cp = Character.codePointAt(current, fromIndex);
            state = state.newState(cp);
        }
        state.is_final = true;
    }
    
    static {
        comparator = CharsRef.getUTF16SortedAsUTF8Comparator();
    }
    
    private static final class State
    {
        private static final int[] NO_LABELS;
        private static final State[] NO_STATES;
        int[] labels;
        State[] states;
        boolean is_final;
        
        private State() {
            this.labels = State.NO_LABELS;
            this.states = State.NO_STATES;
        }
        
        State getState(final int label) {
            final int index = Arrays.binarySearch(this.labels, label);
            return (index >= 0) ? this.states[index] : null;
        }
        
        @Override
        public boolean equals(final Object obj) {
            final State other = (State)obj;
            return this.is_final == other.is_final && Arrays.equals(this.labels, other.labels) && referenceEquals(this.states, other.states);
        }
        
        @Override
        public int hashCode() {
            int hash = this.is_final ? 1 : 0;
            hash ^= hash * 31 + this.labels.length;
            for (final int c : this.labels) {
                hash ^= hash * 31 + c;
            }
            for (final State s : this.states) {
                hash ^= System.identityHashCode(s);
            }
            return hash;
        }
        
        boolean hasChildren() {
            return this.labels.length > 0;
        }
        
        State newState(final int label) {
            assert Arrays.binarySearch(this.labels, label) < 0 : "State already has transition labeled: " + label;
            this.labels = Arrays.copyOf(this.labels, this.labels.length + 1);
            this.states = Arrays.copyOf(this.states, this.states.length + 1);
            this.labels[this.labels.length - 1] = label;
            return this.states[this.states.length - 1] = new State();
        }
        
        State lastChild() {
            assert this.hasChildren() : "No outgoing transitions.";
            return this.states[this.states.length - 1];
        }
        
        State lastChild(final int label) {
            final int index = this.labels.length - 1;
            State s = null;
            if (index >= 0 && this.labels[index] == label) {
                s = this.states[index];
            }
            assert s == this.getState(label);
            return s;
        }
        
        void replaceLastChild(final State state) {
            assert this.hasChildren() : "No outgoing transitions.";
            this.states[this.states.length - 1] = state;
        }
        
        private static boolean referenceEquals(final Object[] a1, final Object[] a2) {
            if (a1.length != a2.length) {
                return false;
            }
            for (int i = 0; i < a1.length; ++i) {
                if (a1[i] != a2[i]) {
                    return false;
                }
            }
            return true;
        }
        
        static {
            NO_LABELS = new int[0];
            NO_STATES = new State[0];
        }
    }
}
