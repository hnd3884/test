package org.apache.lucene.search.vectorhighlight;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class SingleFragListBuilder implements FragListBuilder
{
    @Override
    public FieldFragList createFieldFragList(final FieldPhraseList fieldPhraseList, final int fragCharSize) {
        final FieldFragList ffl = new SimpleFieldFragList(fragCharSize);
        final List<FieldPhraseList.WeightedPhraseInfo> wpil = new ArrayList<FieldPhraseList.WeightedPhraseInfo>();
        final Iterator<FieldPhraseList.WeightedPhraseInfo> ite = fieldPhraseList.phraseList.iterator();
        FieldPhraseList.WeightedPhraseInfo phraseInfo = null;
        while (true) {
            while (ite.hasNext()) {
                phraseInfo = ite.next();
                if (phraseInfo == null) {
                    if (wpil.size() > 0) {
                        ffl.add(0, Integer.MAX_VALUE, wpil);
                    }
                    return ffl;
                }
                wpil.add(phraseInfo);
            }
            continue;
        }
    }
}
