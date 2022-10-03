package org.apache.lucene.util.automaton;

public class CharacterRunAutomaton extends RunAutomaton
{
    public CharacterRunAutomaton(final Automaton a) {
        this(a, 10000);
    }
    
    public CharacterRunAutomaton(final Automaton a, final int maxDeterminizedStates) {
        super(a, 1114111, false, maxDeterminizedStates);
    }
    
    public boolean run(final String s) {
        int p = this.initial;
        for (int l = s.length(), i = 0, cp = 0; i < l; i += Character.charCount(cp)) {
            p = this.step(p, cp = s.codePointAt(i));
            if (p == -1) {
                return false;
            }
        }
        return this.accept[p];
    }
    
    public boolean run(final char[] s, final int offset, final int length) {
        int p = this.initial;
        for (int l = offset + length, i = offset, cp = 0; i < l; i += Character.charCount(cp)) {
            p = this.step(p, cp = Character.codePointAt(s, i, l));
            if (p == -1) {
                return false;
            }
        }
        return this.accept[p];
    }
}
