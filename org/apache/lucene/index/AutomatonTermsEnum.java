package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.util.automaton.CompiledAutomaton;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.automaton.Transition;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.automaton.ByteRunAutomaton;

public class AutomatonTermsEnum extends FilteredTermsEnum
{
    private final ByteRunAutomaton runAutomaton;
    private final BytesRef commonSuffixRef;
    private final boolean finite;
    private final Automaton automaton;
    private final long[] visited;
    private long curGen;
    private final BytesRefBuilder seekBytesRef;
    private boolean linear;
    private final BytesRef linearUpperBound;
    private Transition transition;
    private final IntsRefBuilder savedStates;
    
    public AutomatonTermsEnum(final TermsEnum tenum, final CompiledAutomaton compiled) {
        super(tenum);
        this.seekBytesRef = new BytesRefBuilder();
        this.linear = false;
        this.linearUpperBound = new BytesRef(10);
        this.transition = new Transition();
        this.savedStates = new IntsRefBuilder();
        this.finite = compiled.finite;
        this.runAutomaton = compiled.runAutomaton;
        assert this.runAutomaton != null;
        this.commonSuffixRef = compiled.commonSuffixRef;
        this.automaton = compiled.automaton;
        this.visited = new long[this.runAutomaton.getSize()];
    }
    
    @Override
    protected AcceptStatus accept(final BytesRef term) {
        if (this.commonSuffixRef != null && !StringHelper.endsWith(term, this.commonSuffixRef)) {
            return (this.linear && term.compareTo(this.linearUpperBound) < 0) ? AcceptStatus.NO : AcceptStatus.NO_AND_SEEK;
        }
        if (this.runAutomaton.run(term.bytes, term.offset, term.length)) {
            return this.linear ? AcceptStatus.YES : AcceptStatus.YES_AND_SEEK;
        }
        return (this.linear && term.compareTo(this.linearUpperBound) < 0) ? AcceptStatus.NO : AcceptStatus.NO_AND_SEEK;
    }
    
    @Override
    protected BytesRef nextSeekTerm(final BytesRef term) throws IOException {
        if (term == null) {
            assert this.seekBytesRef.length() == 0;
            if (this.runAutomaton.isAccept(this.runAutomaton.getInitialState())) {
                return this.seekBytesRef.get();
            }
        }
        else {
            this.seekBytesRef.copyBytes(term);
        }
        if (this.nextString()) {
            return this.seekBytesRef.get();
        }
        return null;
    }
    
    private void setLinear(final int position) {
        assert !this.linear;
        int state = this.runAutomaton.getInitialState();
        assert state == 0;
        int maxInterval = 255;
        for (int i = 0; i < position; ++i) {
            state = this.runAutomaton.step(state, this.seekBytesRef.byteAt(i) & 0xFF);
            assert state >= 0 : "state=" + state;
        }
        final int numTransitions = this.automaton.getNumTransitions(state);
        this.automaton.initTransition(state, this.transition);
        for (int j = 0; j < numTransitions; ++j) {
            this.automaton.getNextTransition(this.transition);
            if (this.transition.min <= (this.seekBytesRef.byteAt(position) & 0xFF) && (this.seekBytesRef.byteAt(position) & 0xFF) <= this.transition.max) {
                maxInterval = this.transition.max;
                break;
            }
        }
        if (maxInterval != 255) {
            ++maxInterval;
        }
        final int length = position + 1;
        if (this.linearUpperBound.bytes.length < length) {
            this.linearUpperBound.bytes = new byte[length];
        }
        System.arraycopy(this.seekBytesRef.bytes(), 0, this.linearUpperBound.bytes, 0, position);
        this.linearUpperBound.bytes[position] = (byte)maxInterval;
        this.linearUpperBound.length = length;
        this.linear = true;
    }
    
    private boolean nextString() {
        int pos = 0;
        this.savedStates.grow(this.seekBytesRef.length() + 1);
        this.savedStates.setIntAt(0, this.runAutomaton.getInitialState());
        while (true) {
            ++this.curGen;
            this.linear = false;
            int state = this.savedStates.intAt(pos);
            while (pos < this.seekBytesRef.length()) {
                this.visited[state] = this.curGen;
                final int nextState = this.runAutomaton.step(state, this.seekBytesRef.byteAt(pos) & 0xFF);
                if (nextState == -1) {
                    break;
                }
                this.savedStates.setIntAt(pos + 1, nextState);
                if (!this.finite && !this.linear && this.visited[nextState] == this.curGen) {
                    this.setLinear(pos);
                }
                state = nextState;
                ++pos;
            }
            if (this.nextString(state, pos)) {
                return true;
            }
            if ((pos = this.backtrack(pos)) < 0) {
                return false;
            }
            final int newState = this.runAutomaton.step(this.savedStates.intAt(pos), this.seekBytesRef.byteAt(pos) & 0xFF);
            if (newState >= 0 && this.runAutomaton.isAccept(newState)) {
                return true;
            }
            if (this.finite) {
                continue;
            }
            pos = 0;
        }
    }
    
    private boolean nextString(int state, final int position) {
        int c = 0;
        if (position < this.seekBytesRef.length()) {
            c = (this.seekBytesRef.byteAt(position) & 0xFF);
            if (c++ == 255) {
                return false;
            }
        }
        this.seekBytesRef.setLength(position);
        this.visited[state] = this.curGen;
        final int numTransitions = this.automaton.getNumTransitions(state);
        this.automaton.initTransition(state, this.transition);
        for (int i = 0; i < numTransitions; ++i) {
            this.automaton.getNextTransition(this.transition);
            if (this.transition.max >= c) {
                final int nextChar = Math.max(c, this.transition.min);
                this.seekBytesRef.grow(this.seekBytesRef.length() + 1);
                this.seekBytesRef.append((byte)nextChar);
                state = this.transition.dest;
                while (this.visited[state] != this.curGen && !this.runAutomaton.isAccept(state)) {
                    this.visited[state] = this.curGen;
                    this.automaton.initTransition(state, this.transition);
                    this.automaton.getNextTransition(this.transition);
                    state = this.transition.dest;
                    this.seekBytesRef.grow(this.seekBytesRef.length() + 1);
                    this.seekBytesRef.append((byte)this.transition.min);
                    if (!this.finite && !this.linear && this.visited[state] == this.curGen) {
                        this.setLinear(this.seekBytesRef.length() - 1);
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    private int backtrack(int position) {
        while (position-- > 0) {
            int nextChar = this.seekBytesRef.byteAt(position) & 0xFF;
            if (nextChar++ != 255) {
                this.seekBytesRef.setByteAt(position, (byte)nextChar);
                this.seekBytesRef.setLength(position + 1);
                return position;
            }
        }
        return -1;
    }
}
