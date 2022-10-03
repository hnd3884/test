package org.apache.lucene.search.spell;

import java.io.IOException;
import java.util.Queue;
import java.util.Comparator;
import java.util.PriorityQueue;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

public class WordBreakSpellChecker
{
    private int minSuggestionFrequency;
    private int minBreakWordLength;
    private int maxCombineWordLength;
    private int maxChanges;
    private int maxEvaluations;
    public static final Term SEPARATOR_TERM;
    
    public WordBreakSpellChecker() {
        this.minSuggestionFrequency = 1;
        this.minBreakWordLength = 1;
        this.maxCombineWordLength = 20;
        this.maxChanges = 1;
        this.maxEvaluations = 1000;
    }
    
    public SuggestWord[][] suggestWordBreaks(final Term term, final int maxSuggestions, final IndexReader ir, SuggestMode suggestMode, BreakSuggestionSortMethod sortMethod) throws IOException {
        if (maxSuggestions < 1) {
            return new SuggestWord[0][0];
        }
        if (suggestMode == null) {
            suggestMode = SuggestMode.SUGGEST_WHEN_NOT_IN_INDEX;
        }
        if (sortMethod == null) {
            sortMethod = BreakSuggestionSortMethod.NUM_CHANGES_THEN_MAX_FREQUENCY;
        }
        final int queueInitialCapacity = (maxSuggestions > 10) ? 10 : maxSuggestions;
        final Comparator<SuggestWordArrayWrapper> queueComparator = (sortMethod == BreakSuggestionSortMethod.NUM_CHANGES_THEN_MAX_FREQUENCY) ? new LengthThenMaxFreqComparator() : new LengthThenSumFreqComparator();
        final Queue<SuggestWordArrayWrapper> suggestions = new PriorityQueue<SuggestWordArrayWrapper>(queueInitialCapacity, queueComparator);
        final int origFreq = ir.docFreq(term);
        if (origFreq > 0 && suggestMode == SuggestMode.SUGGEST_WHEN_NOT_IN_INDEX) {
            return new SuggestWord[0][];
        }
        int useMinSuggestionFrequency = this.minSuggestionFrequency;
        if (suggestMode == SuggestMode.SUGGEST_MORE_POPULAR) {
            useMinSuggestionFrequency = ((origFreq == 0) ? 1 : origFreq);
        }
        this.generateBreakUpSuggestions(term, ir, 1, maxSuggestions, useMinSuggestionFrequency, new SuggestWord[0], suggestions, 0, sortMethod);
        final SuggestWord[][] suggestionArray = new SuggestWord[suggestions.size()][];
        for (int i = suggestions.size() - 1; i >= 0; --i) {
            suggestionArray[i] = suggestions.remove().suggestWords;
        }
        return suggestionArray;
    }
    
    public CombineSuggestion[] suggestWordCombinations(final Term[] terms, final int maxSuggestions, final IndexReader ir, final SuggestMode suggestMode) throws IOException {
        if (maxSuggestions < 1) {
            return new CombineSuggestion[0];
        }
        int[] origFreqs = null;
        if (suggestMode != SuggestMode.SUGGEST_ALWAYS) {
            origFreqs = new int[terms.length];
            for (int i = 0; i < terms.length; ++i) {
                origFreqs[i] = ir.docFreq(terms[i]);
            }
        }
        final int queueInitialCapacity = (maxSuggestions > 10) ? 10 : maxSuggestions;
        final Comparator<CombineSuggestionWrapper> queueComparator = new CombinationsThenFreqComparator();
        final Queue<CombineSuggestionWrapper> suggestions = new PriorityQueue<CombineSuggestionWrapper>(queueInitialCapacity, queueComparator);
        int thisTimeEvaluations = 0;
        for (int j = 0; j < terms.length - 1; ++j) {
            if (!terms[j].equals((Object)WordBreakSpellChecker.SEPARATOR_TERM)) {
                final String leftTermText = terms[j].text();
                final int leftTermLength = leftTermText.codePointCount(0, leftTermText.length());
                if (leftTermLength <= this.maxCombineWordLength) {
                    int maxFreq = 0;
                    int minFreq = Integer.MAX_VALUE;
                    if (origFreqs != null) {
                        maxFreq = origFreqs[j];
                        minFreq = origFreqs[j];
                    }
                    String combinedTermText = leftTermText;
                    int combinedLength = leftTermLength;
                    for (int k = j + 1; k < terms.length && k - j <= this.maxChanges; ++k) {
                        if (terms[k].equals((Object)WordBreakSpellChecker.SEPARATOR_TERM)) {
                            break;
                        }
                        final String rightTermText = terms[k].text();
                        final int rightTermLength = rightTermText.codePointCount(0, rightTermText.length());
                        combinedTermText += rightTermText;
                        combinedLength += rightTermLength;
                        if (combinedLength > this.maxCombineWordLength) {
                            break;
                        }
                        if (origFreqs != null) {
                            maxFreq = Math.max(maxFreq, origFreqs[k]);
                            minFreq = Math.min(minFreq, origFreqs[k]);
                        }
                        final Term combinedTerm = new Term(terms[0].field(), combinedTermText);
                        final int combinedTermFreq = ir.docFreq(combinedTerm);
                        if ((suggestMode != SuggestMode.SUGGEST_MORE_POPULAR || combinedTermFreq >= maxFreq) && (suggestMode != SuggestMode.SUGGEST_WHEN_NOT_IN_INDEX || minFreq == 0) && combinedTermFreq >= this.minSuggestionFrequency) {
                            final int[] origIndexes = new int[k - j + 1];
                            origIndexes[0] = j;
                            for (int l = 1; l < origIndexes.length; ++l) {
                                origIndexes[l] = j + l;
                            }
                            final SuggestWord word = new SuggestWord();
                            word.freq = combinedTermFreq;
                            word.score = (float)(origIndexes.length - 1);
                            word.string = combinedTerm.text();
                            final CombineSuggestionWrapper suggestion = new CombineSuggestionWrapper(new CombineSuggestion(word, origIndexes), origIndexes.length - 1);
                            suggestions.offer(suggestion);
                            if (suggestions.size() > maxSuggestions) {
                                suggestions.poll();
                            }
                        }
                        if (++thisTimeEvaluations == this.maxEvaluations) {
                            break;
                        }
                    }
                }
            }
        }
        final CombineSuggestion[] combineSuggestions = new CombineSuggestion[suggestions.size()];
        for (int m = suggestions.size() - 1; m >= 0; --m) {
            combineSuggestions[m] = suggestions.remove().combineSuggestion;
        }
        return combineSuggestions;
    }
    
    private int generateBreakUpSuggestions(final Term term, final IndexReader ir, final int numberBreaks, final int maxSuggestions, final int useMinSuggestionFrequency, final SuggestWord[] prefix, final Queue<SuggestWordArrayWrapper> suggestions, int totalEvaluations, final BreakSuggestionSortMethod sortMethod) throws IOException {
        final String termText = term.text();
        final int termLength = termText.codePointCount(0, termText.length());
        int useMinBreakWordLength = this.minBreakWordLength;
        if (useMinBreakWordLength < 1) {
            useMinBreakWordLength = 1;
        }
        if (termLength < useMinBreakWordLength * 2) {
            return 0;
        }
        int thisTimeEvaluations = 0;
        for (int i = useMinBreakWordLength; i <= termLength - useMinBreakWordLength; ++i) {
            final int end = termText.offsetByCodePoints(0, i);
            final String leftText = termText.substring(0, end);
            final String rightText = termText.substring(end);
            final SuggestWord leftWord = this.generateSuggestWord(ir, term.field(), leftText);
            if (leftWord.freq >= useMinSuggestionFrequency) {
                final SuggestWord rightWord = this.generateSuggestWord(ir, term.field(), rightText);
                if (rightWord.freq >= useMinSuggestionFrequency) {
                    final SuggestWordArrayWrapper suggestion = new SuggestWordArrayWrapper(this.newSuggestion(prefix, leftWord, rightWord));
                    suggestions.offer(suggestion);
                    if (suggestions.size() > maxSuggestions) {
                        suggestions.poll();
                    }
                }
                final int newNumberBreaks = numberBreaks + 1;
                if (newNumberBreaks <= this.maxChanges) {
                    final int evaluations = this.generateBreakUpSuggestions(new Term(term.field(), rightWord.string), ir, newNumberBreaks, maxSuggestions, useMinSuggestionFrequency, this.newPrefix(prefix, leftWord), suggestions, totalEvaluations, sortMethod);
                    totalEvaluations += evaluations;
                }
            }
            ++thisTimeEvaluations;
            if (++totalEvaluations >= this.maxEvaluations) {
                break;
            }
        }
        return thisTimeEvaluations;
    }
    
    private SuggestWord[] newPrefix(final SuggestWord[] oldPrefix, final SuggestWord append) {
        final SuggestWord[] newPrefix = new SuggestWord[oldPrefix.length + 1];
        System.arraycopy(oldPrefix, 0, newPrefix, 0, oldPrefix.length);
        newPrefix[newPrefix.length - 1] = append;
        return newPrefix;
    }
    
    private SuggestWord[] newSuggestion(final SuggestWord[] prefix, final SuggestWord append1, final SuggestWord append2) {
        final SuggestWord[] newSuggestion = new SuggestWord[prefix.length + 2];
        final int score = prefix.length + 1;
        for (int i = 0; i < prefix.length; ++i) {
            final SuggestWord word = new SuggestWord();
            word.string = prefix[i].string;
            word.freq = prefix[i].freq;
            word.score = (float)score;
            newSuggestion[i] = word;
        }
        append1.score = (float)score;
        append2.score = (float)score;
        newSuggestion[newSuggestion.length - 2] = append1;
        newSuggestion[newSuggestion.length - 1] = append2;
        return newSuggestion;
    }
    
    private SuggestWord generateSuggestWord(final IndexReader ir, final String fieldname, final String text) throws IOException {
        final Term term = new Term(fieldname, text);
        final int freq = ir.docFreq(term);
        final SuggestWord word = new SuggestWord();
        word.freq = freq;
        word.score = 1.0f;
        word.string = text;
        return word;
    }
    
    public int getMinSuggestionFrequency() {
        return this.minSuggestionFrequency;
    }
    
    public int getMaxCombineWordLength() {
        return this.maxCombineWordLength;
    }
    
    public int getMinBreakWordLength() {
        return this.minBreakWordLength;
    }
    
    public int getMaxChanges() {
        return this.maxChanges;
    }
    
    public int getMaxEvaluations() {
        return this.maxEvaluations;
    }
    
    public void setMinSuggestionFrequency(final int minSuggestionFrequency) {
        this.minSuggestionFrequency = minSuggestionFrequency;
    }
    
    public void setMaxCombineWordLength(final int maxCombineWordLength) {
        this.maxCombineWordLength = maxCombineWordLength;
    }
    
    public void setMinBreakWordLength(final int minBreakWordLength) {
        this.minBreakWordLength = minBreakWordLength;
    }
    
    public void setMaxChanges(final int maxChanges) {
        this.maxChanges = maxChanges;
    }
    
    public void setMaxEvaluations(final int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
    }
    
    static {
        SEPARATOR_TERM = new Term("", "");
    }
    
    public enum BreakSuggestionSortMethod
    {
        NUM_CHANGES_THEN_SUMMED_FREQUENCY, 
        NUM_CHANGES_THEN_MAX_FREQUENCY;
    }
    
    private class LengthThenMaxFreqComparator implements Comparator<SuggestWordArrayWrapper>
    {
        @Override
        public int compare(final SuggestWordArrayWrapper o1, final SuggestWordArrayWrapper o2) {
            if (o1.suggestWords.length != o2.suggestWords.length) {
                return o2.suggestWords.length - o1.suggestWords.length;
            }
            if (o1.freqMax != o2.freqMax) {
                return o1.freqMax - o2.freqMax;
            }
            return 0;
        }
    }
    
    private class LengthThenSumFreqComparator implements Comparator<SuggestWordArrayWrapper>
    {
        @Override
        public int compare(final SuggestWordArrayWrapper o1, final SuggestWordArrayWrapper o2) {
            if (o1.suggestWords.length != o2.suggestWords.length) {
                return o2.suggestWords.length - o1.suggestWords.length;
            }
            if (o1.freqSum != o2.freqSum) {
                return o1.freqSum - o2.freqSum;
            }
            return 0;
        }
    }
    
    private class CombinationsThenFreqComparator implements Comparator<CombineSuggestionWrapper>
    {
        @Override
        public int compare(final CombineSuggestionWrapper o1, final CombineSuggestionWrapper o2) {
            if (o1.numCombinations != o2.numCombinations) {
                return o2.numCombinations - o1.numCombinations;
            }
            if (o1.combineSuggestion.suggestion.freq != o2.combineSuggestion.suggestion.freq) {
                return o1.combineSuggestion.suggestion.freq - o2.combineSuggestion.suggestion.freq;
            }
            return 0;
        }
    }
    
    private class SuggestWordArrayWrapper
    {
        final SuggestWord[] suggestWords;
        final int freqMax;
        final int freqSum;
        
        SuggestWordArrayWrapper(final SuggestWord[] suggestWords) {
            this.suggestWords = suggestWords;
            int aFreqSum = 0;
            int aFreqMax = 0;
            for (final SuggestWord sw : suggestWords) {
                aFreqSum += sw.freq;
                aFreqMax = Math.max(aFreqMax, sw.freq);
            }
            this.freqSum = aFreqSum;
            this.freqMax = aFreqMax;
        }
    }
    
    private class CombineSuggestionWrapper
    {
        final CombineSuggestion combineSuggestion;
        final int numCombinations;
        
        CombineSuggestionWrapper(final CombineSuggestion combineSuggestion, final int numCombinations) {
            this.combineSuggestion = combineSuggestion;
            this.numCombinations = numCombinations;
        }
    }
}
