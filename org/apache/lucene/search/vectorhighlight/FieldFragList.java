package org.apache.lucene.search.vectorhighlight;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public abstract class FieldFragList
{
    private List<WeightedFragInfo> fragInfos;
    
    public FieldFragList(final int fragCharSize) {
        this.fragInfos = new ArrayList<WeightedFragInfo>();
    }
    
    public abstract void add(final int p0, final int p1, final List<FieldPhraseList.WeightedPhraseInfo> p2);
    
    public List<WeightedFragInfo> getFragInfos() {
        return this.fragInfos;
    }
    
    public static class WeightedFragInfo
    {
        private List<SubInfo> subInfos;
        private float totalBoost;
        private int startOffset;
        private int endOffset;
        
        public WeightedFragInfo(final int startOffset, final int endOffset, final List<SubInfo> subInfos, final float totalBoost) {
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.totalBoost = totalBoost;
            this.subInfos = subInfos;
        }
        
        public List<SubInfo> getSubInfos() {
            return this.subInfos;
        }
        
        public float getTotalBoost() {
            return this.totalBoost;
        }
        
        public int getStartOffset() {
            return this.startOffset;
        }
        
        public int getEndOffset() {
            return this.endOffset;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("subInfos=(");
            for (final SubInfo si : this.subInfos) {
                sb.append(si.toString());
            }
            sb.append(")/").append(this.totalBoost).append('(').append(this.startOffset).append(',').append(this.endOffset).append(')');
            return sb.toString();
        }
        
        public static class SubInfo
        {
            private final String text;
            private final List<FieldPhraseList.WeightedPhraseInfo.Toffs> termsOffsets;
            private final int seqnum;
            private final float boost;
            
            public SubInfo(final String text, final List<FieldPhraseList.WeightedPhraseInfo.Toffs> termsOffsets, final int seqnum, final float boost) {
                this.text = text;
                this.termsOffsets = termsOffsets;
                this.seqnum = seqnum;
                this.boost = boost;
            }
            
            public List<FieldPhraseList.WeightedPhraseInfo.Toffs> getTermsOffsets() {
                return this.termsOffsets;
            }
            
            public int getSeqnum() {
                return this.seqnum;
            }
            
            public String getText() {
                return this.text;
            }
            
            public float getBoost() {
                return this.boost;
            }
            
            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder();
                sb.append(this.text).append('(');
                for (final FieldPhraseList.WeightedPhraseInfo.Toffs to : this.termsOffsets) {
                    sb.append(to.toString());
                }
                sb.append(')');
                return sb.toString();
            }
        }
    }
}
