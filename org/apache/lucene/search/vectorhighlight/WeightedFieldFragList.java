package org.apache.lucene.search.vectorhighlight;

import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

public class WeightedFieldFragList extends FieldFragList
{
    public WeightedFieldFragList(final int fragCharSize) {
        super(fragCharSize);
    }
    
    @Override
    public void add(final int startOffset, final int endOffset, final List<FieldPhraseList.WeightedPhraseInfo> phraseInfoList) {
        final List<WeightedFragInfo.SubInfo> tempSubInfos = new ArrayList<WeightedFragInfo.SubInfo>();
        final List<WeightedFragInfo.SubInfo> realSubInfos = new ArrayList<WeightedFragInfo.SubInfo>();
        final HashSet<String> distinctTerms = new HashSet<String>();
        int length = 0;
        for (final FieldPhraseList.WeightedPhraseInfo phraseInfo : phraseInfoList) {
            float phraseTotalBoost = 0.0f;
            for (final FieldTermStack.TermInfo ti : phraseInfo.getTermsInfos()) {
                if (distinctTerms.add(ti.getText())) {
                    phraseTotalBoost += ti.getWeight() * phraseInfo.getBoost();
                }
                ++length;
            }
            tempSubInfos.add(new WeightedFragInfo.SubInfo(phraseInfo.getText(), phraseInfo.getTermsOffsets(), phraseInfo.getSeqnum(), phraseTotalBoost));
        }
        final float norm = length * (1.0f / (float)Math.sqrt(length));
        float totalBoost = 0.0f;
        for (final WeightedFragInfo.SubInfo tempSubInfo : tempSubInfos) {
            final float subInfoBoost = tempSubInfo.getBoost() * norm;
            realSubInfos.add(new WeightedFragInfo.SubInfo(tempSubInfo.getText(), tempSubInfo.getTermsOffsets(), tempSubInfo.getSeqnum(), subInfoBoost));
            totalBoost += subInfoBoost;
        }
        this.getFragInfos().add(new WeightedFragInfo(startOffset, endOffset, realSubInfos, totalBoost));
    }
}
