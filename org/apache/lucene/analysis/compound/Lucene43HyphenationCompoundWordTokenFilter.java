package org.apache.lucene.analysis.compound;

import org.apache.lucene.analysis.compound.hyphenation.Hyphenation;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.compound.hyphenation.HyphenationTree;

@Deprecated
public class Lucene43HyphenationCompoundWordTokenFilter extends Lucene43CompoundWordTokenFilterBase
{
    private HyphenationTree hyphenator;
    
    public Lucene43HyphenationCompoundWordTokenFilter(final TokenStream input, final HyphenationTree hyphenator, final CharArraySet dictionary) {
        this(input, hyphenator, dictionary, 5, 2, 15, false);
    }
    
    public Lucene43HyphenationCompoundWordTokenFilter(final TokenStream input, final HyphenationTree hyphenator, final CharArraySet dictionary, final int minWordSize, final int minSubwordSize, final int maxSubwordSize, final boolean onlyLongestMatch) {
        super(input, dictionary, minWordSize, minSubwordSize, maxSubwordSize, onlyLongestMatch);
        this.hyphenator = hyphenator;
    }
    
    public Lucene43HyphenationCompoundWordTokenFilter(final TokenStream input, final HyphenationTree hyphenator, final int minWordSize, final int minSubwordSize, final int maxSubwordSize) {
        this(input, hyphenator, null, minWordSize, minSubwordSize, maxSubwordSize, false);
    }
    
    public Lucene43HyphenationCompoundWordTokenFilter(final TokenStream input, final HyphenationTree hyphenator) {
        this(input, hyphenator, 5, 2, 15);
    }
    
    public static HyphenationTree getHyphenationTree(final String hyphenationFilename) throws IOException {
        return getHyphenationTree(new InputSource(hyphenationFilename));
    }
    
    public static HyphenationTree getHyphenationTree(final InputSource hyphenationSource) throws IOException {
        final HyphenationTree tree = new HyphenationTree();
        tree.loadPatterns(hyphenationSource);
        return tree;
    }
    
    @Override
    protected void decompose() {
        final Hyphenation hyphens = this.hyphenator.hyphenate(this.termAtt.buffer(), 0, this.termAtt.length(), 1, 1);
        if (hyphens == null) {
            return;
        }
        final int[] hyp = hyphens.getHyphenationPoints();
        for (int i = 0; i < hyp.length; ++i) {
            final int remaining = hyp.length - i;
            final int start = hyp[i];
            CompoundToken longestMatchToken = null;
            for (int j = 1; j < remaining; ++j) {
                final int partLength = hyp[i + j] - start;
                if (partLength > this.maxSubwordSize) {
                    break;
                }
                if (partLength >= this.minSubwordSize) {
                    if (this.dictionary == null || this.dictionary.contains(this.termAtt.buffer(), start, partLength)) {
                        if (this.onlyLongestMatch) {
                            if (longestMatchToken != null) {
                                if (longestMatchToken.txt.length() < partLength) {
                                    longestMatchToken = new CompoundToken(start, partLength);
                                }
                            }
                            else {
                                longestMatchToken = new CompoundToken(start, partLength);
                            }
                        }
                        else {
                            this.tokens.add(new CompoundToken(start, partLength));
                        }
                    }
                    else if (this.dictionary.contains(this.termAtt.buffer(), start, partLength - 1)) {
                        if (this.onlyLongestMatch) {
                            if (longestMatchToken != null) {
                                if (longestMatchToken.txt.length() < partLength - 1) {
                                    longestMatchToken = new CompoundToken(start, partLength - 1);
                                }
                            }
                            else {
                                longestMatchToken = new CompoundToken(start, partLength - 1);
                            }
                        }
                        else {
                            this.tokens.add(new CompoundToken(start, partLength - 1));
                        }
                    }
                }
            }
            if (this.onlyLongestMatch && longestMatchToken != null) {
                this.tokens.add(longestMatchToken);
            }
        }
    }
}
