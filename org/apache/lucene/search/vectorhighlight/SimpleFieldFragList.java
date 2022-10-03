package org.apache.lucene.search.vectorhighlight;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class SimpleFieldFragList extends FieldFragList
{
    public SimpleFieldFragList(final int fragCharSize) {
        super(fragCharSize);
    }
    
    @Override
    public void add(final int startOffset, final int endOffset, final List<FieldPhraseList.WeightedPhraseInfo> phraseInfoList) {
        float totalBoost = 0.0f;
        final List<WeightedFragInfo.SubInfo> subInfos = new ArrayList<WeightedFragInfo.SubInfo>();
        for (final FieldPhraseList.WeightedPhraseInfo phraseInfo : phraseInfoList) {
            subInfos.add(new WeightedFragInfo.SubInfo(phraseInfo.getText(), phraseInfo.getTermsOffsets(), phraseInfo.getSeqnum(), phraseInfo.getBoost()));
            totalBoost += phraseInfo.getBoost();
        }
        this.getFragInfos().add(new WeightedFragInfo(startOffset, endOffset, subInfos, totalBoost));
    }
}
