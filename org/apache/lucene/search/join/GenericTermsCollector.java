package org.apache.lucene.search.join;

import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.search.Collector;

interface GenericTermsCollector extends Collector
{
    BytesRefHash getCollectedTerms();
    
    float[] getScoresPerTerm();
}
