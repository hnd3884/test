package org.apache.lucene.search.grouping.term;

import org.apache.lucene.util.BytesRef;

class GroupedFacetHit
{
    final BytesRef groupValue;
    final BytesRef facetValue;
    
    GroupedFacetHit(final BytesRef groupValue, final BytesRef facetValue) {
        this.groupValue = groupValue;
        this.facetValue = facetValue;
    }
}
