package org.apache.lucene.util.automaton;

import java.util.Iterator;
import java.util.ArrayList;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.util.BytesRef;
import java.util.Collection;

public final class Automata
{
    private Automata() {
    }
    
    public static Automaton makeEmpty() {
        final Automaton a = new Automaton();
        a.finishState();
        return a;
    }
    
    public static Automaton makeEmptyString() {
        final Automaton a = new Automaton();
        a.createState();
        a.setAccept(0, true);
        return a;
    }
    
    public static Automaton makeAnyString() {
        final Automaton a = new Automaton();
        final int s = a.createState();
        a.setAccept(s, true);
        a.addTransition(s, s, 0, 1114111);
        a.finishState();
        return a;
    }
    
    public static Automaton makeAnyBinary() {
        final Automaton a = new Automaton();
        final int s = a.createState();
        a.setAccept(s, true);
        a.addTransition(s, s, 0, 255);
        a.finishState();
        return a;
    }
    
    public static Automaton makeAnyChar() {
        return makeCharRange(0, 1114111);
    }
    
    public static int appendAnyChar(final Automaton a, final int state) {
        final int newState = a.createState();
        a.addTransition(state, newState, 0, 1114111);
        return newState;
    }
    
    public static Automaton makeChar(final int c) {
        return makeCharRange(c, c);
    }
    
    public static int appendChar(final Automaton a, final int state, final int c) {
        final int newState = a.createState();
        a.addTransition(state, newState, c, c);
        return newState;
    }
    
    public static Automaton makeCharRange(final int min, final int max) {
        if (min > max) {
            return makeEmpty();
        }
        final Automaton a = new Automaton();
        final int s1 = a.createState();
        final int s2 = a.createState();
        a.setAccept(s2, true);
        a.addTransition(s1, s2, min, max);
        a.finishState();
        return a;
    }
    
    private static int anyOfRightLength(final Automaton.Builder builder, final String x, final int n) {
        final int s = builder.createState();
        if (x.length() == n) {
            builder.setAccept(s, true);
        }
        else {
            builder.addTransition(s, anyOfRightLength(builder, x, n + 1), 48, 57);
        }
        return s;
    }
    
    private static int atLeast(final Automaton.Builder builder, final String x, final int n, final Collection<Integer> initials, final boolean zeros) {
        final int s = builder.createState();
        if (x.length() == n) {
            builder.setAccept(s, true);
        }
        else {
            if (zeros) {
                initials.add(s);
            }
            final char c = x.charAt(n);
            builder.addTransition(s, atLeast(builder, x, n + 1, initials, zeros && c == '0'), c);
            if (c < '9') {
                builder.addTransition(s, anyOfRightLength(builder, x, n + 1), (char)(c + '\u0001'), 57);
            }
        }
        return s;
    }
    
    private static int atMost(final Automaton.Builder builder, final String x, final int n) {
        final int s = builder.createState();
        if (x.length() == n) {
            builder.setAccept(s, true);
        }
        else {
            final char c = x.charAt(n);
            builder.addTransition(s, atMost(builder, x, (char)n + '\u0001'), c);
            if (c > '0') {
                builder.addTransition(s, anyOfRightLength(builder, x, n + 1), 48, (char)(c - '\u0001'));
            }
        }
        return s;
    }
    
    private static int between(final Automaton.Builder builder, final String x, final String y, final int n, final Collection<Integer> initials, final boolean zeros) {
        final int s = builder.createState();
        if (x.length() == n) {
            builder.setAccept(s, true);
        }
        else {
            if (zeros) {
                initials.add(s);
            }
            final char cx = x.charAt(n);
            final char cy = y.charAt(n);
            if (cx == cy) {
                builder.addTransition(s, between(builder, x, y, n + 1, initials, zeros && cx == '0'), cx);
            }
            else {
                builder.addTransition(s, atLeast(builder, x, n + 1, initials, zeros && cx == '0'), cx);
                builder.addTransition(s, atMost(builder, y, n + 1), cy);
                if (cx + '\u0001' < cy) {
                    builder.addTransition(s, anyOfRightLength(builder, x, n + 1), (char)(cx + '\u0001'), (char)(cy - '\u0001'));
                }
            }
        }
        return s;
    }
    
    private static boolean suffixIsZeros(final BytesRef br, final int len) {
        for (int i = len; i < br.length; ++i) {
            if (br.bytes[br.offset + i] != 0) {
                return false;
            }
        }
        return true;
    }
    
    public static Automaton makeBinaryInterval(BytesRef min, boolean minInclusive, final BytesRef max, final boolean maxInclusive) {
        if (min == null && !minInclusive) {
            throw new IllegalArgumentException("minInclusive must be true when min is null (open ended)");
        }
        if (max == null && !maxInclusive) {
            throw new IllegalArgumentException("maxInclusive must be true when max is null (open ended)");
        }
        if (min == null) {
            min = new BytesRef();
            minInclusive = true;
        }
        int cmp;
        if (max != null) {
            cmp = min.compareTo(max);
        }
        else {
            cmp = -1;
            if (min.length == 0 && minInclusive) {
                return makeAnyBinary();
            }
        }
        if (cmp == 0) {
            if (!minInclusive || !maxInclusive) {
                return makeEmpty();
            }
            return makeBinary(min);
        }
        else {
            if (cmp > 0) {
                return makeEmpty();
            }
            if (max != null && StringHelper.startsWith(max, min) && suffixIsZeros(max, min.length)) {
                int maxLength = max.length;
                assert maxLength > min.length;
                if (!maxInclusive) {
                    --maxLength;
                }
                if (maxLength != min.length) {
                    final Automaton a = new Automaton();
                    int lastState = a.createState();
                    for (int i = 0; i < min.length; ++i) {
                        final int state = a.createState();
                        final int label = min.bytes[min.offset + i] & 0xFF;
                        a.addTransition(lastState, state, label);
                        lastState = state;
                    }
                    if (minInclusive) {
                        a.setAccept(lastState, true);
                    }
                    for (int i = min.length; i < maxLength; ++i) {
                        final int state = a.createState();
                        a.addTransition(lastState, state, 0);
                        a.setAccept(state, true);
                        lastState = state;
                    }
                    a.finishState();
                    return a;
                }
                if (!minInclusive) {
                    return makeEmpty();
                }
                return makeBinary(min);
            }
            else {
                final Automaton a2 = new Automaton();
                final int startState = a2.createState();
                final int sinkState = a2.createState();
                a2.setAccept(sinkState, true);
                a2.addTransition(sinkState, sinkState, 0, 255);
                boolean equalPrefix = true;
                int lastState2 = startState;
                int firstMaxState = -1;
                int sharedPrefixLength = 0;
                for (int j = 0; j < min.length; ++j) {
                    final int minLabel = min.bytes[min.offset + j] & 0xFF;
                    int maxLabel;
                    if (max != null && equalPrefix && j < max.length) {
                        maxLabel = (max.bytes[max.offset + j] & 0xFF);
                    }
                    else {
                        maxLabel = -1;
                    }
                    int nextState;
                    if (minInclusive && j == min.length - 1 && (!equalPrefix || minLabel != maxLabel)) {
                        nextState = sinkState;
                    }
                    else {
                        nextState = a2.createState();
                    }
                    if (equalPrefix) {
                        if (minLabel == maxLabel) {
                            a2.addTransition(lastState2, nextState, minLabel);
                        }
                        else if (max == null) {
                            equalPrefix = false;
                            sharedPrefixLength = 0;
                            a2.addTransition(lastState2, sinkState, minLabel + 1, 255);
                            a2.addTransition(lastState2, nextState, minLabel);
                        }
                        else {
                            assert maxLabel > minLabel;
                            a2.addTransition(lastState2, nextState, minLabel);
                            if (maxLabel > minLabel + 1) {
                                a2.addTransition(lastState2, sinkState, minLabel + 1, maxLabel - 1);
                            }
                            if (maxInclusive || j < max.length - 1) {
                                firstMaxState = a2.createState();
                                if (j < max.length - 1) {
                                    a2.setAccept(firstMaxState, true);
                                }
                                a2.addTransition(lastState2, firstMaxState, maxLabel);
                            }
                            equalPrefix = false;
                            sharedPrefixLength = j;
                        }
                    }
                    else {
                        a2.addTransition(lastState2, nextState, minLabel);
                        if (minLabel < 255) {
                            a2.addTransition(lastState2, sinkState, minLabel + 1, 255);
                        }
                    }
                    lastState2 = nextState;
                }
                if (!equalPrefix && lastState2 != sinkState && lastState2 != startState) {
                    a2.addTransition(lastState2, sinkState, 0, 255);
                }
                if (minInclusive) {
                    a2.setAccept(lastState2, true);
                }
                if (max != null) {
                    if (firstMaxState == -1) {
                        sharedPrefixLength = min.length;
                    }
                    else {
                        lastState2 = firstMaxState;
                        ++sharedPrefixLength;
                    }
                    for (int j = sharedPrefixLength; j < max.length; ++j) {
                        final int maxLabel2 = max.bytes[max.offset + j] & 0xFF;
                        if (maxLabel2 > 0) {
                            a2.addTransition(lastState2, sinkState, 0, maxLabel2 - 1);
                        }
                        if (maxInclusive || j < max.length - 1) {
                            final int nextState2 = a2.createState();
                            if (j < max.length - 1) {
                                a2.setAccept(nextState2, true);
                            }
                            a2.addTransition(lastState2, nextState2, maxLabel2);
                            lastState2 = nextState2;
                        }
                    }
                    if (maxInclusive) {
                        a2.setAccept(lastState2, true);
                    }
                }
                a2.finishState();
                assert a2.isDeterministic() : a2.toDot();
                return a2;
            }
        }
    }
    
    public static Automaton makeDecimalInterval(final int min, final int max, final int digits) throws IllegalArgumentException {
        String x = Integer.toString(min);
        String y = Integer.toString(max);
        if (min > max || (digits > 0 && y.length() > digits)) {
            throw new IllegalArgumentException();
        }
        int d;
        if (digits > 0) {
            d = digits;
        }
        else {
            d = y.length();
        }
        final StringBuilder bx = new StringBuilder();
        for (int i = x.length(); i < d; ++i) {
            bx.append('0');
        }
        bx.append(x);
        x = bx.toString();
        final StringBuilder by = new StringBuilder();
        for (int j = y.length(); j < d; ++j) {
            by.append('0');
        }
        by.append(y);
        y = by.toString();
        final Automaton.Builder builder = new Automaton.Builder();
        if (digits <= 0) {
            builder.createState();
        }
        final Collection<Integer> initials = new ArrayList<Integer>();
        between(builder, x, y, 0, initials, digits <= 0);
        final Automaton a1 = builder.finish();
        if (digits <= 0) {
            a1.addTransition(0, 0, 48);
            for (final int p : initials) {
                a1.addEpsilon(0, p);
            }
            a1.finishState();
        }
        return a1;
    }
    
    public static Automaton makeString(final String s) {
        final Automaton a = new Automaton();
        int lastState = a.createState();
        for (int i = 0, cp = 0; i < s.length(); i += Character.charCount(cp)) {
            final int state = a.createState();
            cp = s.codePointAt(i);
            a.addTransition(lastState, state, cp);
            lastState = state;
        }
        a.setAccept(lastState, true);
        a.finishState();
        assert a.isDeterministic();
        assert !Operations.hasDeadStates(a);
        return a;
    }
    
    public static Automaton makeBinary(final BytesRef term) {
        final Automaton a = new Automaton();
        int lastState = a.createState();
        for (int i = 0; i < term.length; ++i) {
            final int state = a.createState();
            final int label = term.bytes[term.offset + i] & 0xFF;
            a.addTransition(lastState, state, label);
            lastState = state;
        }
        a.setAccept(lastState, true);
        a.finishState();
        assert a.isDeterministic();
        assert !Operations.hasDeadStates(a);
        return a;
    }
    
    public static Automaton makeString(final int[] word, final int offset, final int length) {
        final Automaton a = new Automaton();
        a.createState();
        int s = 0;
        for (int i = offset; i < offset + length; ++i) {
            final int s2 = a.createState();
            a.addTransition(s, s2, word[i]);
            s = s2;
        }
        a.setAccept(s, true);
        a.finishState();
        return a;
    }
    
    public static Automaton makeStringUnion(final Collection<BytesRef> utf8Strings) {
        if (utf8Strings.isEmpty()) {
            return makeEmpty();
        }
        return DaciukMihovAutomatonBuilder.build(utf8Strings);
    }
}
