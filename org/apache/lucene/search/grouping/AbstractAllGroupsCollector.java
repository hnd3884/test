package org.apache.lucene.search.grouping;

import java.io.IOException;
import org.apache.lucene.search.Scorer;
import java.util.Collection;
import org.apache.lucene.search.SimpleCollector;

public abstract class AbstractAllGroupsCollector<GROUP_VALUE_TYPE> extends SimpleCollector
{
    public int getGroupCount() {
        return this.getGroups().size();
    }
    
    public abstract Collection<GROUP_VALUE_TYPE> getGroups();
    
    public void setScorer(final Scorer scorer) throws IOException {
    }
    
    public boolean needsScores() {
        return false;
    }
}
