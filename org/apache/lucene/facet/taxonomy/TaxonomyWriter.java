package org.apache.lucene.facet.taxonomy;

import java.util.Map;
import java.io.IOException;
import org.apache.lucene.index.TwoPhaseCommit;
import java.io.Closeable;

public interface TaxonomyWriter extends Closeable, TwoPhaseCommit
{
    int addCategory(final FacetLabel p0) throws IOException;
    
    int getParent(final int p0) throws IOException;
    
    int getSize();
    
    void setCommitData(final Map<String, String> p0);
    
    Map<String, String> getCommitData();
}
