package org.apache.lucene.search.vectorhighlight;

import java.util.Comparator;
import java.util.Collections;
import java.util.List;

public class ScoreOrderFragmentsBuilder extends BaseFragmentsBuilder
{
    public ScoreOrderFragmentsBuilder() {
    }
    
    public ScoreOrderFragmentsBuilder(final String[] preTags, final String[] postTags) {
        super(preTags, postTags);
    }
    
    public ScoreOrderFragmentsBuilder(final BoundaryScanner bs) {
        super(bs);
    }
    
    public ScoreOrderFragmentsBuilder(final String[] preTags, final String[] postTags, final BoundaryScanner bs) {
        super(preTags, postTags, bs);
    }
    
    @Override
    public List<FieldFragList.WeightedFragInfo> getWeightedFragInfoList(final List<FieldFragList.WeightedFragInfo> src) {
        Collections.sort(src, new ScoreComparator());
        return src;
    }
    
    public static class ScoreComparator implements Comparator<FieldFragList.WeightedFragInfo>
    {
        @Override
        public int compare(final FieldFragList.WeightedFragInfo o1, final FieldFragList.WeightedFragInfo o2) {
            if (o1.getTotalBoost() > o2.getTotalBoost()) {
                return -1;
            }
            if (o1.getTotalBoost() < o2.getTotalBoost()) {
                return 1;
            }
            if (o1.getStartOffset() < o2.getStartOffset()) {
                return -1;
            }
            if (o1.getStartOffset() > o2.getStartOffset()) {
                return 1;
            }
            return 0;
        }
    }
}
