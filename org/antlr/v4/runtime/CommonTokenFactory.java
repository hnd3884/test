package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Pair;

public class CommonTokenFactory implements TokenFactory<CommonToken>
{
    public static final TokenFactory<CommonToken> DEFAULT;
    protected final boolean copyText;
    
    public CommonTokenFactory(final boolean copyText) {
        this.copyText = copyText;
    }
    
    public CommonTokenFactory() {
        this(false);
    }
    
    @Override
    public CommonToken create(final Pair<TokenSource, CharStream> source, final int type, final String text, final int channel, final int start, final int stop, final int line, final int charPositionInLine) {
        final CommonToken t = new CommonToken(source, type, channel, start, stop);
        t.setLine(line);
        t.setCharPositionInLine(charPositionInLine);
        if (text != null) {
            t.setText(text);
        }
        else if (this.copyText && source.b != null) {
            t.setText(source.b.getText(Interval.of(start, stop)));
        }
        return t;
    }
    
    @Override
    public CommonToken create(final int type, final String text) {
        return new CommonToken(type, text);
    }
    
    static {
        DEFAULT = new CommonTokenFactory();
    }
}
