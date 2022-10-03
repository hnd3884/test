package org.apache.lucene.analysis.snowball;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.tartarus.snowball.SnowballProgram;
import org.apache.lucene.analysis.TokenFilter;

public final class SnowballFilter extends TokenFilter
{
    private final SnowballProgram stemmer;
    private final CharTermAttribute termAtt;
    private final KeywordAttribute keywordAttr;
    
    public SnowballFilter(final TokenStream input, final SnowballProgram stemmer) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.keywordAttr = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
        this.stemmer = stemmer;
    }
    
    public SnowballFilter(final TokenStream in, final String name) {
        super(in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.keywordAttr = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
        try {
            final Class<? extends SnowballProgram> stemClass = Class.forName("org.tartarus.snowball.ext." + name + "Stemmer").asSubclass(SnowballProgram.class);
            this.stemmer = (SnowballProgram)stemClass.newInstance();
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Invalid stemmer class specified: " + name, e);
        }
    }
    
    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (!this.keywordAttr.isKeyword()) {
                final char[] termBuffer = this.termAtt.buffer();
                final int length = this.termAtt.length();
                this.stemmer.setCurrent(termBuffer, length);
                this.stemmer.stem();
                final char[] finalTerm = this.stemmer.getCurrentBuffer();
                final int newLength = this.stemmer.getCurrentBufferLength();
                if (finalTerm != termBuffer) {
                    this.termAtt.copyBuffer(finalTerm, 0, newLength);
                }
                else {
                    this.termAtt.setLength(newLength);
                }
            }
            return true;
        }
        return false;
    }
}
