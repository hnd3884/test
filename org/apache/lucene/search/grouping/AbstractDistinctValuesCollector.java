package org.apache.lucene.search.grouping;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import org.apache.lucene.search.SimpleCollector;

public abstract class AbstractDistinctValuesCollector<GC extends GroupCount<?>> extends SimpleCollector
{
    public abstract List<GC> getGroups();
    
    public boolean needsScores() {
        return false;
    }
    
    public abstract static class GroupCount<GROUP_VALUE_TYPE>
    {
        public final GROUP_VALUE_TYPE groupValue;
        public final Set<GROUP_VALUE_TYPE> uniqueValues;
        
        public GroupCount(final GROUP_VALUE_TYPE groupValue) {
            this.groupValue = groupValue;
            this.uniqueValues = new HashSet<GROUP_VALUE_TYPE>();
        }
    }
}
