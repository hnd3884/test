package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Pair;
import java.io.Serializable;

public class CommonToken implements WritableToken, Serializable
{
    protected static final Pair<TokenSource, CharStream> EMPTY_SOURCE;
    protected int type;
    protected int line;
    protected int charPositionInLine;
    protected int channel;
    protected Pair<TokenSource, CharStream> source;
    protected String text;
    protected int index;
    protected int start;
    protected int stop;
    
    public CommonToken(final int type) {
        this.charPositionInLine = -1;
        this.channel = 0;
        this.index = -1;
        this.type = type;
        this.source = CommonToken.EMPTY_SOURCE;
    }
    
    public CommonToken(final Pair<TokenSource, CharStream> source, final int type, final int channel, final int start, final int stop) {
        this.charPositionInLine = -1;
        this.channel = 0;
        this.index = -1;
        this.source = source;
        this.type = type;
        this.channel = channel;
        this.start = start;
        this.stop = stop;
        if (source.a != null) {
            this.line = source.a.getLine();
            this.charPositionInLine = source.a.getCharPositionInLine();
        }
    }
    
    public CommonToken(final int type, final String text) {
        this.charPositionInLine = -1;
        this.channel = 0;
        this.index = -1;
        this.type = type;
        this.channel = 0;
        this.text = text;
        this.source = CommonToken.EMPTY_SOURCE;
    }
    
    public CommonToken(final Token oldToken) {
        this.charPositionInLine = -1;
        this.channel = 0;
        this.index = -1;
        this.type = oldToken.getType();
        this.line = oldToken.getLine();
        this.index = oldToken.getTokenIndex();
        this.charPositionInLine = oldToken.getCharPositionInLine();
        this.channel = oldToken.getChannel();
        this.start = oldToken.getStartIndex();
        this.stop = oldToken.getStopIndex();
        if (oldToken instanceof CommonToken) {
            this.text = ((CommonToken)oldToken).text;
            this.source = ((CommonToken)oldToken).source;
        }
        else {
            this.text = oldToken.getText();
            this.source = new Pair<TokenSource, CharStream>(oldToken.getTokenSource(), oldToken.getInputStream());
        }
    }
    
    @Override
    public int getType() {
        return this.type;
    }
    
    @Override
    public void setLine(final int line) {
        this.line = line;
    }
    
    @Override
    public String getText() {
        if (this.text != null) {
            return this.text;
        }
        final CharStream input = this.getInputStream();
        if (input == null) {
            return null;
        }
        final int n = input.size();
        if (this.start < n && this.stop < n) {
            return input.getText(Interval.of(this.start, this.stop));
        }
        return "<EOF>";
    }
    
    @Override
    public void setText(final String text) {
        this.text = text;
    }
    
    @Override
    public int getLine() {
        return this.line;
    }
    
    @Override
    public int getCharPositionInLine() {
        return this.charPositionInLine;
    }
    
    @Override
    public void setCharPositionInLine(final int charPositionInLine) {
        this.charPositionInLine = charPositionInLine;
    }
    
    @Override
    public int getChannel() {
        return this.channel;
    }
    
    @Override
    public void setChannel(final int channel) {
        this.channel = channel;
    }
    
    @Override
    public void setType(final int type) {
        this.type = type;
    }
    
    @Override
    public int getStartIndex() {
        return this.start;
    }
    
    public void setStartIndex(final int start) {
        this.start = start;
    }
    
    @Override
    public int getStopIndex() {
        return this.stop;
    }
    
    public void setStopIndex(final int stop) {
        this.stop = stop;
    }
    
    @Override
    public int getTokenIndex() {
        return this.index;
    }
    
    @Override
    public void setTokenIndex(final int index) {
        this.index = index;
    }
    
    @Override
    public TokenSource getTokenSource() {
        return this.source.a;
    }
    
    @Override
    public CharStream getInputStream() {
        return this.source.b;
    }
    
    @Override
    public String toString() {
        String channelStr = "";
        if (this.channel > 0) {
            channelStr = ",channel=" + this.channel;
        }
        String txt = this.getText();
        if (txt != null) {
            txt = txt.replace("\n", "\\n");
            txt = txt.replace("\r", "\\r");
            txt = txt.replace("\t", "\\t");
        }
        else {
            txt = "<no text>";
        }
        return "[@" + this.getTokenIndex() + "," + this.start + ":" + this.stop + "='" + txt + "',<" + this.type + ">" + channelStr + "," + this.line + ":" + this.getCharPositionInLine() + "]";
    }
    
    static {
        EMPTY_SOURCE = new Pair<TokenSource, CharStream>(null, null);
    }
}
