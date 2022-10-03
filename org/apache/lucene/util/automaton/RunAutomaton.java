package org.apache.lucene.util.automaton;

import java.util.Arrays;

public abstract class RunAutomaton
{
    final Automaton automaton;
    final int maxInterval;
    final int size;
    final boolean[] accept;
    final int initial;
    final int[] transitions;
    final int[] points;
    final int[] classmap;
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append("initial state: ").append(this.initial).append("\n");
        for (int i = 0; i < this.size; ++i) {
            b.append("state " + i);
            if (this.accept[i]) {
                b.append(" [accept]:\n");
            }
            else {
                b.append(" [reject]:\n");
            }
            for (int j = 0; j < this.points.length; ++j) {
                final int k = this.transitions[i * this.points.length + j];
                if (k != -1) {
                    final int min = this.points[j];
                    int max;
                    if (j + 1 < this.points.length) {
                        max = this.points[j + 1] - 1;
                    }
                    else {
                        max = this.maxInterval;
                    }
                    b.append(" ");
                    Automaton.appendCharString(min, b);
                    if (min != max) {
                        b.append("-");
                        Automaton.appendCharString(max, b);
                    }
                    b.append(" -> ").append(k).append("\n");
                }
            }
        }
        return b.toString();
    }
    
    public final int getSize() {
        return this.size;
    }
    
    public final boolean isAccept(final int state) {
        return this.accept[state];
    }
    
    public final int getInitialState() {
        return this.initial;
    }
    
    public final int[] getCharIntervals() {
        return this.points.clone();
    }
    
    final int getCharClass(final int c) {
        return Operations.findIndex(c, this.points);
    }
    
    public RunAutomaton(final Automaton a, final int maxInterval, final boolean tableize) {
        this(a, maxInterval, tableize, 10000);
    }
    
    public RunAutomaton(Automaton a, final int maxInterval, final boolean tableize, final int maxDeterminizedStates) {
        this.maxInterval = maxInterval;
        a = Operations.determinize(a, maxDeterminizedStates);
        this.automaton = a;
        this.points = a.getStartPoints();
        this.initial = 0;
        this.size = Math.max(1, a.getNumStates());
        this.accept = new boolean[this.size];
        Arrays.fill(this.transitions = new int[this.size * this.points.length], -1);
        for (int n = 0; n < this.size; ++n) {
            this.accept[n] = a.isAccept(n);
            for (int c = 0; c < this.points.length; ++c) {
                final int dest = a.step(n, this.points[c]);
                assert dest < this.size;
                this.transitions[n * this.points.length + c] = dest;
            }
        }
        if (tableize) {
            this.classmap = new int[maxInterval + 1];
            int i = 0;
            for (int j = 0; j <= maxInterval; ++j) {
                if (i + 1 < this.points.length && j == this.points[i + 1]) {
                    ++i;
                }
                this.classmap[j] = i;
            }
        }
        else {
            this.classmap = null;
        }
    }
    
    public final int step(final int state, final int c) {
        if (this.classmap == null) {
            return this.transitions[state * this.points.length + this.getCharClass(c)];
        }
        return this.transitions[state * this.points.length + this.classmap[c]];
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.initial;
        result = 31 * result + this.maxInterval;
        result = 31 * result + this.points.length;
        result = 31 * result + this.size;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final RunAutomaton other = (RunAutomaton)obj;
        return this.initial == other.initial && this.maxInterval == other.maxInterval && this.size == other.size && Arrays.equals(this.points, other.points) && Arrays.equals(this.accept, other.accept) && Arrays.equals(this.transitions, other.transitions);
    }
}
