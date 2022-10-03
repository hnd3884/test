package org.antlr.v4.runtime;

public class CommonTokenStream extends BufferedTokenStream
{
    protected int channel;
    
    public CommonTokenStream(final TokenSource tokenSource) {
        super(tokenSource);
        this.channel = 0;
    }
    
    public CommonTokenStream(final TokenSource tokenSource, final int channel) {
        this(tokenSource);
        this.channel = channel;
    }
    
    @Override
    protected int adjustSeekIndex(final int i) {
        return this.nextTokenOnChannel(i, this.channel);
    }
    
    @Override
    protected Token LB(final int k) {
        if (k == 0 || this.p - k < 0) {
            return null;
        }
        int i = this.p;
        for (int n = 1; n <= k; ++n) {
            i = this.previousTokenOnChannel(i - 1, this.channel);
        }
        if (i < 0) {
            return null;
        }
        return this.tokens.get(i);
    }
    
    @Override
    public Token LT(final int k) {
        this.lazyInit();
        if (k == 0) {
            return null;
        }
        if (k < 0) {
            return this.LB(-k);
        }
        int i = this.p;
        for (int n = 1; n < k; ++n) {
            if (this.sync(i + 1)) {
                i = this.nextTokenOnChannel(i + 1, this.channel);
            }
        }
        return this.tokens.get(i);
    }
    
    public int getNumberOfOnChannelTokens() {
        int n = 0;
        this.fill();
        for (int i = 0; i < this.tokens.size(); ++i) {
            final Token t = this.tokens.get(i);
            if (t.getChannel() == this.channel) {
                ++n;
            }
            if (t.getType() == -1) {
                break;
            }
        }
        return n;
    }
}
