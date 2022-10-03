package org.apache.lucene.search.highlight;

import java.util.Iterator;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer;

public class Highlighter
{
    public static final int DEFAULT_MAX_CHARS_TO_ANALYZE = 51200;
    private int maxDocCharsToAnalyze;
    private Formatter formatter;
    private Encoder encoder;
    private Fragmenter textFragmenter;
    private Scorer fragmentScorer;
    
    public Highlighter(final Scorer fragmentScorer) {
        this(new SimpleHTMLFormatter(), fragmentScorer);
    }
    
    public Highlighter(final Formatter formatter, final Scorer fragmentScorer) {
        this(formatter, new DefaultEncoder(), fragmentScorer);
    }
    
    public Highlighter(final Formatter formatter, final Encoder encoder, final Scorer fragmentScorer) {
        this.maxDocCharsToAnalyze = 51200;
        this.textFragmenter = new SimpleFragmenter();
        this.fragmentScorer = null;
        this.formatter = formatter;
        this.encoder = encoder;
        this.fragmentScorer = fragmentScorer;
    }
    
    public final String getBestFragment(final Analyzer analyzer, final String fieldName, final String text) throws IOException, InvalidTokenOffsetsException {
        final TokenStream tokenStream = analyzer.tokenStream(fieldName, text);
        return this.getBestFragment(tokenStream, text);
    }
    
    public final String getBestFragment(final TokenStream tokenStream, final String text) throws IOException, InvalidTokenOffsetsException {
        final String[] results = this.getBestFragments(tokenStream, text, 1);
        if (results.length > 0) {
            return results[0];
        }
        return null;
    }
    
    public final String[] getBestFragments(final Analyzer analyzer, final String fieldName, final String text, final int maxNumFragments) throws IOException, InvalidTokenOffsetsException {
        final TokenStream tokenStream = analyzer.tokenStream(fieldName, text);
        return this.getBestFragments(tokenStream, text, maxNumFragments);
    }
    
    public final String[] getBestFragments(final TokenStream tokenStream, final String text, int maxNumFragments) throws IOException, InvalidTokenOffsetsException {
        maxNumFragments = Math.max(1, maxNumFragments);
        final TextFragment[] frag = this.getBestTextFragments(tokenStream, text, true, maxNumFragments);
        final ArrayList<String> fragTexts = new ArrayList<String>();
        for (int i = 0; i < frag.length; ++i) {
            if (frag[i] != null && frag[i].getScore() > 0.0f) {
                fragTexts.add(frag[i].toString());
            }
        }
        return fragTexts.toArray(new String[0]);
    }
    
    public final TextFragment[] getBestTextFragments(TokenStream tokenStream, final String text, final boolean mergeContiguousFragments, final int maxNumFragments) throws IOException, InvalidTokenOffsetsException {
        final ArrayList<TextFragment> docFrags = new ArrayList<TextFragment>();
        final StringBuilder newText = new StringBuilder();
        final CharTermAttribute termAtt = (CharTermAttribute)tokenStream.addAttribute((Class)CharTermAttribute.class);
        final OffsetAttribute offsetAtt = (OffsetAttribute)tokenStream.addAttribute((Class)OffsetAttribute.class);
        TextFragment currentFrag = new TextFragment(newText, newText.length(), docFrags.size());
        if (this.fragmentScorer instanceof QueryScorer) {
            ((QueryScorer)this.fragmentScorer).setMaxDocCharsToAnalyze(this.maxDocCharsToAnalyze);
        }
        final TokenStream newStream = this.fragmentScorer.init(tokenStream);
        if (newStream != null) {
            tokenStream = newStream;
        }
        this.fragmentScorer.startFragment(currentFrag);
        docFrags.add(currentFrag);
        final FragmentQueue fragQueue = new FragmentQueue(maxNumFragments);
        try {
            int lastEndOffset = 0;
            this.textFragmenter.start(text, tokenStream);
            final TokenGroup tokenGroup = new TokenGroup(tokenStream);
            tokenStream.reset();
            for (boolean next = tokenStream.incrementToken(); next && offsetAtt.startOffset() < this.maxDocCharsToAnalyze; next = tokenStream.incrementToken()) {
                if (offsetAtt.endOffset() > text.length() || offsetAtt.startOffset() > text.length()) {
                    throw new InvalidTokenOffsetsException("Token " + termAtt.toString() + " exceeds length of provided text sized " + text.length());
                }
                if (tokenGroup.getNumTokens() > 0 && tokenGroup.isDistinct()) {
                    final int startOffset = tokenGroup.getStartOffset();
                    final int endOffset = tokenGroup.getEndOffset();
                    final String tokenText = text.substring(startOffset, endOffset);
                    final String markedUpText = this.formatter.highlightTerm(this.encoder.encodeText(tokenText), tokenGroup);
                    if (startOffset > lastEndOffset) {
                        newText.append(this.encoder.encodeText(text.substring(lastEndOffset, startOffset)));
                    }
                    newText.append(markedUpText);
                    lastEndOffset = Math.max(endOffset, lastEndOffset);
                    tokenGroup.clear();
                    if (this.textFragmenter.isNewFragment()) {
                        currentFrag.setScore(this.fragmentScorer.getFragmentScore());
                        currentFrag.textEndPos = newText.length();
                        currentFrag = new TextFragment(newText, newText.length(), docFrags.size());
                        this.fragmentScorer.startFragment(currentFrag);
                        docFrags.add(currentFrag);
                    }
                }
                tokenGroup.addToken(this.fragmentScorer.getTokenScore());
            }
            currentFrag.setScore(this.fragmentScorer.getFragmentScore());
            if (tokenGroup.getNumTokens() > 0) {
                final int startOffset = tokenGroup.getStartOffset();
                final int endOffset = tokenGroup.getEndOffset();
                final String tokenText = text.substring(startOffset, endOffset);
                final String markedUpText2 = this.formatter.highlightTerm(this.encoder.encodeText(tokenText), tokenGroup);
                if (startOffset > lastEndOffset) {
                    newText.append(this.encoder.encodeText(text.substring(lastEndOffset, startOffset)));
                }
                newText.append(markedUpText2);
                lastEndOffset = Math.max(lastEndOffset, endOffset);
            }
            if (lastEndOffset < text.length() && text.length() <= this.maxDocCharsToAnalyze) {
                newText.append(this.encoder.encodeText(text.substring(lastEndOffset)));
            }
            currentFrag.textEndPos = newText.length();
            final Iterator<TextFragment> i = docFrags.iterator();
            while (i.hasNext()) {
                currentFrag = i.next();
                fragQueue.insertWithOverflow((Object)currentFrag);
            }
            TextFragment[] frag = new TextFragment[fragQueue.size()];
            for (int j = frag.length - 1; j >= 0; --j) {
                frag[j] = (TextFragment)fragQueue.pop();
            }
            if (mergeContiguousFragments) {
                this.mergeContiguousFragments(frag);
                final ArrayList<TextFragment> fragTexts = new ArrayList<TextFragment>();
                for (int k = 0; k < frag.length; ++k) {
                    if (frag[k] != null && frag[k].getScore() > 0.0f) {
                        fragTexts.add(frag[k]);
                    }
                }
                frag = fragTexts.toArray(new TextFragment[0]);
            }
            return frag;
        }
        finally {
            if (tokenStream != null) {
                try {
                    tokenStream.end();
                    tokenStream.close();
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    private void mergeContiguousFragments(final TextFragment[] frag) {
        if (frag.length > 1) {
            boolean mergingStillBeingDone;
            do {
                mergingStillBeingDone = false;
                for (int i = 0; i < frag.length; ++i) {
                    if (frag[i] != null) {
                        for (int x = 0; x < frag.length; ++x) {
                            if (frag[x] != null) {
                                if (frag[i] == null) {
                                    break;
                                }
                                TextFragment frag2 = null;
                                TextFragment frag3 = null;
                                int frag1Num = 0;
                                int frag2Num = 0;
                                if (frag[i].follows(frag[x])) {
                                    frag2 = frag[x];
                                    frag1Num = x;
                                    frag3 = frag[i];
                                    frag2Num = i;
                                }
                                else if (frag[x].follows(frag[i])) {
                                    frag2 = frag[i];
                                    frag1Num = i;
                                    frag3 = frag[x];
                                    frag2Num = x;
                                }
                                if (frag2 != null) {
                                    int bestScoringFragNum;
                                    int worstScoringFragNum;
                                    if (frag2.getScore() > frag3.getScore()) {
                                        bestScoringFragNum = frag1Num;
                                        worstScoringFragNum = frag2Num;
                                    }
                                    else {
                                        bestScoringFragNum = frag2Num;
                                        worstScoringFragNum = frag1Num;
                                    }
                                    frag2.merge(frag3);
                                    frag[worstScoringFragNum] = null;
                                    mergingStillBeingDone = true;
                                    frag[bestScoringFragNum] = frag2;
                                }
                            }
                        }
                    }
                }
            } while (mergingStillBeingDone);
        }
    }
    
    public final String getBestFragments(final TokenStream tokenStream, final String text, final int maxNumFragments, final String separator) throws IOException, InvalidTokenOffsetsException {
        final String[] sections = this.getBestFragments(tokenStream, text, maxNumFragments);
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < sections.length; ++i) {
            if (i > 0) {
                result.append(separator);
            }
            result.append(sections[i]);
        }
        return result.toString();
    }
    
    public int getMaxDocCharsToAnalyze() {
        return this.maxDocCharsToAnalyze;
    }
    
    public void setMaxDocCharsToAnalyze(final int maxDocCharsToAnalyze) {
        this.maxDocCharsToAnalyze = maxDocCharsToAnalyze;
    }
    
    public Fragmenter getTextFragmenter() {
        return this.textFragmenter;
    }
    
    public void setTextFragmenter(final Fragmenter fragmenter) {
        this.textFragmenter = fragmenter;
    }
    
    public Scorer getFragmentScorer() {
        return this.fragmentScorer;
    }
    
    public void setFragmentScorer(final Scorer scorer) {
        this.fragmentScorer = scorer;
    }
    
    public Encoder getEncoder() {
        return this.encoder;
    }
    
    public void setEncoder(final Encoder encoder) {
        this.encoder = encoder;
    }
}
