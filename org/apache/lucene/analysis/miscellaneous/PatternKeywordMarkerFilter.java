package org.apache.lucene.analysis.miscellaneous;

import java.util.regex.Pattern;
import org.apache.lucene.analysis.TokenStream;
import java.util.regex.Matcher;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class PatternKeywordMarkerFilter extends KeywordMarkerFilter
{
    private final CharTermAttribute termAtt;
    private final Matcher matcher;
    
    public PatternKeywordMarkerFilter(final TokenStream in, final Pattern pattern) {
        super(in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.matcher = pattern.matcher("");
    }
    
    @Override
    protected boolean isKeyword() {
        this.matcher.reset((CharSequence)this.termAtt);
        return this.matcher.matches();
    }
}
