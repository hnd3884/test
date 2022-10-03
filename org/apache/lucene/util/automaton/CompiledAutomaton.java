package org.apache.lucene.util.automaton;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.lucene.index.SingleTermsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.util.BytesRef;

public class CompiledAutomaton
{
    public final AUTOMATON_TYPE type;
    public final BytesRef term;
    public final ByteRunAutomaton runAutomaton;
    public final Automaton automaton;
    public final BytesRef commonSuffixRef;
    public final Boolean finite;
    public final int sinkState;
    private Transition transition;
    
    public CompiledAutomaton(final Automaton automaton) {
        this(automaton, null, true);
    }
    
    private static int findSinkState(final Automaton automaton) {
        final int numStates = automaton.getNumStates();
        final Transition t = new Transition();
        int foundState = -1;
        for (int s = 0; s < numStates; ++s) {
            if (automaton.isAccept(s)) {
                final int count = automaton.initTransition(s, t);
                boolean isSinkState = false;
                for (int i = 0; i < count; ++i) {
                    automaton.getNextTransition(t);
                    if (t.dest == s && t.min == 0 && t.max == 255) {
                        isSinkState = true;
                        break;
                    }
                }
                if (isSinkState) {
                    foundState = s;
                    break;
                }
            }
        }
        return foundState;
    }
    
    public CompiledAutomaton(final Automaton automaton, final Boolean finite, final boolean simplify) {
        this(automaton, finite, simplify, 10000, false);
    }
    
    public CompiledAutomaton(Automaton automaton, final Boolean finite, final boolean simplify, final int maxDeterminizedStates, final boolean isBinary) {
        this.transition = new Transition();
        if (automaton.getNumStates() == 0) {
            automaton = new Automaton();
            automaton.createState();
        }
        if (simplify) {
            if (Operations.isEmpty(automaton)) {
                this.type = AUTOMATON_TYPE.NONE;
                this.term = null;
                this.commonSuffixRef = null;
                this.runAutomaton = null;
                this.automaton = null;
                this.finite = null;
                this.sinkState = -1;
                return;
            }
            boolean isTotal;
            if (isBinary) {
                isTotal = Operations.isTotal(automaton, 0, 255);
            }
            else {
                isTotal = Operations.isTotal(automaton);
            }
            if (isTotal) {
                this.type = AUTOMATON_TYPE.ALL;
                this.term = null;
                this.commonSuffixRef = null;
                this.runAutomaton = null;
                this.automaton = null;
                this.finite = null;
                this.sinkState = -1;
                return;
            }
            automaton = Operations.determinize(automaton, maxDeterminizedStates);
            final IntsRef singleton = Operations.getSingleton(automaton);
            if (singleton != null) {
                this.type = AUTOMATON_TYPE.SINGLE;
                this.commonSuffixRef = null;
                this.runAutomaton = null;
                this.automaton = null;
                this.finite = null;
                if (isBinary) {
                    this.term = StringHelper.intsRefToBytesRef(singleton);
                }
                else {
                    this.term = new BytesRef(UnicodeUtil.newString(singleton.ints, singleton.offset, singleton.length));
                }
                this.sinkState = -1;
                return;
            }
        }
        this.type = AUTOMATON_TYPE.NORMAL;
        this.term = null;
        if (finite == null) {
            this.finite = Operations.isFinite(automaton);
        }
        else {
            this.finite = finite;
        }
        Automaton binary;
        if (isBinary) {
            binary = automaton;
        }
        else {
            binary = new UTF32ToUTF8().convert(automaton);
        }
        if (this.finite) {
            this.commonSuffixRef = null;
        }
        else {
            final BytesRef suffix = Operations.getCommonSuffixBytesRef(binary, maxDeterminizedStates);
            if (suffix.length == 0) {
                this.commonSuffixRef = null;
            }
            else {
                this.commonSuffixRef = suffix;
            }
        }
        this.runAutomaton = new ByteRunAutomaton(binary, true, maxDeterminizedStates);
        this.automaton = this.runAutomaton.automaton;
        this.sinkState = findSinkState(this.automaton);
    }
    
    private BytesRef addTail(int state, final BytesRefBuilder term, int idx, final int leadLabel) {
        int maxIndex = -1;
        for (int numTransitions = this.automaton.initTransition(state, this.transition), i = 0; i < numTransitions; ++i) {
            this.automaton.getNextTransition(this.transition);
            if (this.transition.min >= leadLabel) {
                break;
            }
            maxIndex = i;
        }
        assert maxIndex != -1;
        this.automaton.getTransition(state, maxIndex, this.transition);
        int floorLabel;
        if (this.transition.max > leadLabel - 1) {
            floorLabel = leadLabel - 1;
        }
        else {
            floorLabel = this.transition.max;
        }
        term.grow(1 + idx);
        term.setByteAt(idx, (byte)floorLabel);
        state = this.transition.dest;
        ++idx;
        while (true) {
            final int numTransitions = this.automaton.getNumTransitions(state);
            if (numTransitions == 0) {
                break;
            }
            this.automaton.getTransition(state, numTransitions - 1, this.transition);
            term.grow(1 + idx);
            term.setByteAt(idx, (byte)this.transition.max);
            state = this.transition.dest;
            ++idx;
        }
        assert this.runAutomaton.isAccept(state);
        term.setLength(idx);
        return term.get();
    }
    
    public TermsEnum getTermsEnum(final Terms terms) throws IOException {
        switch (this.type) {
            case NONE: {
                return TermsEnum.EMPTY;
            }
            case ALL: {
                return terms.iterator();
            }
            case SINGLE: {
                return new SingleTermsEnum(terms.iterator(), this.term);
            }
            case NORMAL: {
                return terms.intersect(this, null);
            }
            default: {
                throw new RuntimeException("unhandled case");
            }
        }
    }
    
    public BytesRef floor(final BytesRef input, final BytesRefBuilder output) {
        int state = this.runAutomaton.getInitialState();
        if (input.length == 0) {
            if (this.runAutomaton.isAccept(state)) {
                output.clear();
                return output.get();
            }
            return null;
        }
        else {
            final List<Integer> stack = new ArrayList<Integer>();
            int idx = 0;
            int label;
            while (true) {
                label = (input.bytes[input.offset + idx] & 0xFF);
                int nextState = this.runAutomaton.step(state, label);
                if (idx == input.length - 1) {
                    if (nextState != -1 && this.runAutomaton.isAccept(nextState)) {
                        output.grow(1 + idx);
                        output.setByteAt(idx, (byte)label);
                        output.setLength(input.length);
                        return output.get();
                    }
                    nextState = -1;
                }
                if (nextState == -1) {
                    break;
                }
                output.grow(1 + idx);
                output.setByteAt(idx, (byte)label);
                stack.add(state);
                state = nextState;
                ++idx;
            }
            while (true) {
                final int numTransitions = this.automaton.getNumTransitions(state);
                if (numTransitions == 0) {
                    assert this.runAutomaton.isAccept(state);
                    output.setLength(idx);
                    return output.get();
                }
                else {
                    this.automaton.getTransition(state, 0, this.transition);
                    if (label - 1 >= this.transition.min) {
                        return this.addTail(state, output, idx, label);
                    }
                    if (this.runAutomaton.isAccept(state)) {
                        output.setLength(idx);
                        return output.get();
                    }
                    if (stack.size() == 0) {
                        return null;
                    }
                    state = stack.remove(stack.size() - 1);
                    --idx;
                    label = (input.bytes[input.offset + idx] & 0xFF);
                }
            }
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.runAutomaton == null) ? 0 : this.runAutomaton.hashCode());
        result = 31 * result + ((this.term == null) ? 0 : this.term.hashCode());
        result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
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
        final CompiledAutomaton other = (CompiledAutomaton)obj;
        if (this.type != other.type) {
            return false;
        }
        if (this.type == AUTOMATON_TYPE.SINGLE) {
            if (!this.term.equals(other.term)) {
                return false;
            }
        }
        else if (this.type == AUTOMATON_TYPE.NORMAL && !this.runAutomaton.equals(other.runAutomaton)) {
            return false;
        }
        return true;
    }
    
    public enum AUTOMATON_TYPE
    {
        NONE, 
        ALL, 
        SINGLE, 
        NORMAL;
    }
}
