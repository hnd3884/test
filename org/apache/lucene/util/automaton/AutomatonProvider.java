package org.apache.lucene.util.automaton;

import java.io.IOException;

public interface AutomatonProvider
{
    Automaton getAutomaton(final String p0) throws IOException;
}
