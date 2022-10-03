package org.apache.lucene.search.vectorhighlight;

import java.util.List;

public class SimpleFragmentsBuilder extends BaseFragmentsBuilder
{
    public SimpleFragmentsBuilder() {
    }
    
    public SimpleFragmentsBuilder(final String[] preTags, final String[] postTags) {
        super(preTags, postTags);
    }
    
    public SimpleFragmentsBuilder(final BoundaryScanner bs) {
        super(bs);
    }
    
    public SimpleFragmentsBuilder(final String[] preTags, final String[] postTags, final BoundaryScanner bs) {
        super(preTags, postTags, bs);
    }
    
    @Override
    public List<FieldFragList.WeightedFragInfo> getWeightedFragInfoList(final List<FieldFragList.WeightedFragInfo> src) {
        return src;
    }
}
