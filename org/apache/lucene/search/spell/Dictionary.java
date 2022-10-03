package org.apache.lucene.search.spell;

import java.io.IOException;
import org.apache.lucene.search.suggest.InputIterator;

public interface Dictionary
{
    InputIterator getEntryIterator() throws IOException;
}
