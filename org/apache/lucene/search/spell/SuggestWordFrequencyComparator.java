package org.apache.lucene.search.spell;

import java.util.Comparator;

public class SuggestWordFrequencyComparator implements Comparator<SuggestWord>
{
    @Override
    public int compare(final SuggestWord first, final SuggestWord second) {
        if (first.freq > second.freq) {
            return 1;
        }
        if (first.freq < second.freq) {
            return -1;
        }
        if (first.score > second.score) {
            return 1;
        }
        if (first.score < second.score) {
            return -1;
        }
        return second.string.compareTo(first.string);
    }
}
