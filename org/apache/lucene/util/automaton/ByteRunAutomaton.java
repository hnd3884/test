package org.apache.lucene.util.automaton;

public class ByteRunAutomaton extends RunAutomaton
{
    public ByteRunAutomaton(final Automaton a) {
        this(a, false, 10000);
    }
    
    public ByteRunAutomaton(final Automaton a, final boolean isBinary, final int maxDeterminizedStates) {
        super(isBinary ? a : new UTF32ToUTF8().convert(a), 256, true, maxDeterminizedStates);
    }
    
    public boolean run(final byte[] s, final int offset, final int length) {
        int p = this.initial;
        for (int l = offset + length, i = offset; i < l; ++i) {
            p = this.step(p, s[i] & 0xFF);
            if (p == -1) {
                return false;
            }
        }
        return this.accept[p];
    }
}
