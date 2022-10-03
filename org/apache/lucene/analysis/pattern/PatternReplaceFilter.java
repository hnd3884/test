package org.apache.lucene.analysis.pattern;

import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.TokenStream;
import java.util.regex.Matcher;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class PatternReplaceFilter extends TokenFilter
{
    private final String replacement;
    private final boolean all;
    private final CharTermAttribute termAtt;
    private final Matcher m;
    
    public PatternReplaceFilter(final TokenStream in, final Pattern p, final String replacement, final boolean all) {
        super(in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.replacement = ((null == replacement) ? "" : replacement);
        this.all = all;
        this.m = p.matcher((CharSequence)this.termAtt);
    }
    
    public boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        this.m.reset();
        if (this.m.find()) {
            final String transformed = this.all ? this.m.replaceAll(this.replacement) : this.m.replaceFirst(this.replacement);
            this.termAtt.setEmpty().append(transformed);
        }
        return true;
    }
}
