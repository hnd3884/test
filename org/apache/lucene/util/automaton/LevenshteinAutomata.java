package org.apache.lucene.util.automaton;

import org.apache.lucene.util.UnicodeUtil;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class LevenshteinAutomata
{
    public static final int MAXIMUM_SUPPORTED_DISTANCE = 2;
    final int[] word;
    final int[] alphabet;
    final int alphaMax;
    final int[] rangeLower;
    final int[] rangeUpper;
    int numRanges;
    ParametricDescription[] descriptions;
    
    public LevenshteinAutomata(final String input, final boolean withTranspositions) {
        this(codePoints(input), 1114111, withTranspositions);
    }
    
    public LevenshteinAutomata(final int[] word, final int alphaMax, final boolean withTranspositions) {
        this.numRanges = 0;
        this.word = word;
        this.alphaMax = alphaMax;
        final SortedSet<Integer> set = new TreeSet<Integer>();
        for (int i = 0; i < word.length; ++i) {
            final int v = word[i];
            if (v > alphaMax) {
                throw new IllegalArgumentException("alphaMax exceeded by symbol " + v + " in word");
            }
            set.add(v);
        }
        this.alphabet = new int[set.size()];
        final Iterator<Integer> iterator = set.iterator();
        for (int j = 0; j < this.alphabet.length; ++j) {
            this.alphabet[j] = iterator.next();
        }
        this.rangeLower = new int[this.alphabet.length + 2];
        this.rangeUpper = new int[this.alphabet.length + 2];
        int lower = 0;
        for (int k = 0; k < this.alphabet.length; ++k) {
            final int higher = this.alphabet[k];
            if (higher > lower) {
                this.rangeLower[this.numRanges] = lower;
                this.rangeUpper[this.numRanges] = higher - 1;
                ++this.numRanges;
            }
            lower = higher + 1;
        }
        if (lower <= alphaMax) {
            this.rangeLower[this.numRanges] = lower;
            this.rangeUpper[this.numRanges] = alphaMax;
            ++this.numRanges;
        }
        this.descriptions = new ParametricDescription[] { null, withTranspositions ? new Lev1TParametricDescription(word.length) : new Lev1ParametricDescription(word.length), withTranspositions ? new Lev2TParametricDescription(word.length) : new Lev2ParametricDescription(word.length) };
    }
    
    private static int[] codePoints(final String input) {
        final int length = Character.codePointCount(input, 0, input.length());
        final int[] word = new int[length];
        int i = 0;
        int j = 0;
        for (int cp = 0; i < input.length(); i += Character.charCount(cp)) {
            cp = (word[j++] = input.codePointAt(i));
        }
        return word;
    }
    
    public Automaton toAutomaton(final int n) {
        return this.toAutomaton(n, "");
    }
    
    public Automaton toAutomaton(final int n, final String prefix) {
        assert prefix != null;
        if (n == 0) {
            return Automata.makeString(prefix + UnicodeUtil.newString(this.word, 0, this.word.length));
        }
        if (n >= this.descriptions.length) {
            return null;
        }
        final int range = 2 * n + 1;
        final ParametricDescription description = this.descriptions[n];
        final int numStates = description.size();
        final Automaton a = new Automaton();
        int lastState;
        if (prefix != null) {
            lastState = a.createState();
            for (int i = 0, cp = 0; i < prefix.length(); i += Character.charCount(cp)) {
                final int state = a.createState();
                cp = prefix.codePointAt(i);
                a.addTransition(lastState, state, cp, cp);
                lastState = state;
            }
        }
        else {
            lastState = a.createState();
        }
        final int stateOffset = lastState;
        a.setAccept(lastState, description.isAccept(0));
        for (int j = 1; j < numStates; ++j) {
            final int state = a.createState();
            a.setAccept(state, description.isAccept(j));
        }
        for (int k = 0; k < numStates; ++k) {
            final int xpos = description.getPosition(k);
            if (xpos >= 0) {
                final int end = xpos + Math.min(this.word.length - xpos, range);
                for (int x = 0; x < this.alphabet.length; ++x) {
                    final int ch = this.alphabet[x];
                    final int cvec = this.getVector(ch, xpos, end);
                    final int dest = description.transition(k, xpos, cvec);
                    if (dest >= 0) {
                        a.addTransition(stateOffset + k, stateOffset + dest, ch);
                    }
                }
                final int dest2 = description.transition(k, xpos, 0);
                if (dest2 >= 0) {
                    for (int r = 0; r < this.numRanges; ++r) {
                        a.addTransition(stateOffset + k, stateOffset + dest2, this.rangeLower[r], this.rangeUpper[r]);
                    }
                }
            }
        }
        a.finishState();
        assert a.isDeterministic();
        return a;
    }
    
    int getVector(final int x, final int pos, final int end) {
        int vector = 0;
        for (int i = pos; i < end; ++i) {
            vector <<= 1;
            if (this.word[i] == x) {
                vector |= 0x1;
            }
        }
        return vector;
    }
    
    abstract static class ParametricDescription
    {
        protected final int w;
        protected final int n;
        private final int[] minErrors;
        private static final long[] MASKS;
        
        ParametricDescription(final int w, final int n, final int[] minErrors) {
            this.w = w;
            this.n = n;
            this.minErrors = minErrors;
        }
        
        int size() {
            return this.minErrors.length * (this.w + 1);
        }
        
        boolean isAccept(final int absState) {
            final int state = absState / (this.w + 1);
            final int offset = absState % (this.w + 1);
            assert offset >= 0;
            return this.w - offset + this.minErrors[state] <= this.n;
        }
        
        int getPosition(final int absState) {
            return absState % (this.w + 1);
        }
        
        abstract int transition(final int p0, final int p1, final int p2);
        
        protected int unpack(final long[] data, final int index, final int bitsPerValue) {
            final long bitLoc = bitsPerValue * index;
            final int dataLoc = (int)(bitLoc >> 6);
            final int bitStart = (int)(bitLoc & 0x3FL);
            if (bitStart + bitsPerValue <= 64) {
                return (int)(data[dataLoc] >> bitStart & ParametricDescription.MASKS[bitsPerValue - 1]);
            }
            final int part = 64 - bitStart;
            return (int)((data[dataLoc] >> bitStart & ParametricDescription.MASKS[part - 1]) + ((data[1 + dataLoc] & ParametricDescription.MASKS[bitsPerValue - part - 1]) << part));
        }
        
        static {
            MASKS = new long[] { 1L, 3L, 7L, 15L, 31L, 63L, 127L, 255L, 511L, 1023L, 2047L, 4095L, 8191L, 16383L, 32767L, 65535L, 131071L, 262143L, 524287L, 1048575L, 2097151L, 4194303L, 8388607L, 16777215L, 33554431L, 67108863L, 134217727L, 268435455L, 536870911L, 1073741823L, 2147483647L, 4294967295L, 8589934591L, 17179869183L, 34359738367L, 68719476735L, 137438953471L, 274877906943L, 549755813887L, 1099511627775L, 2199023255551L, 4398046511103L, 8796093022207L, 17592186044415L, 35184372088831L, 70368744177663L, 140737488355327L, 281474976710655L, 562949953421311L, 1125899906842623L, 2251799813685247L, 4503599627370495L, 9007199254740991L, 18014398509481983L, 36028797018963967L, 72057594037927935L, 144115188075855871L, 288230376151711743L, 576460752303423487L, 1152921504606846975L, 2305843009213693951L, 4611686018427387903L, Long.MAX_VALUE };
        }
    }
}
