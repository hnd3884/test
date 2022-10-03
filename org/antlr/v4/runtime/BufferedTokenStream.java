package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class BufferedTokenStream implements TokenStream
{
    protected TokenSource tokenSource;
    protected List<Token> tokens;
    protected int p;
    protected boolean fetchedEOF;
    
    public BufferedTokenStream(final TokenSource tokenSource) {
        this.tokens = new ArrayList<Token>(100);
        this.p = -1;
        if (tokenSource == null) {
            throw new NullPointerException("tokenSource cannot be null");
        }
        this.tokenSource = tokenSource;
    }
    
    @Override
    public TokenSource getTokenSource() {
        return this.tokenSource;
    }
    
    @Override
    public int index() {
        return this.p;
    }
    
    @Override
    public int mark() {
        return 0;
    }
    
    @Override
    public void release(final int marker) {
    }
    
    public void reset() {
        this.seek(0);
    }
    
    @Override
    public void seek(final int index) {
        this.lazyInit();
        this.p = this.adjustSeekIndex(index);
    }
    
    @Override
    public int size() {
        return this.tokens.size();
    }
    
    @Override
    public void consume() {
        boolean skipEofCheck;
        if (this.p >= 0) {
            if (this.fetchedEOF) {
                skipEofCheck = (this.p < this.tokens.size() - 1);
            }
            else {
                skipEofCheck = (this.p < this.tokens.size());
            }
        }
        else {
            skipEofCheck = false;
        }
        if (!skipEofCheck && this.LA(1) == -1) {
            throw new IllegalStateException("cannot consume EOF");
        }
        if (this.sync(this.p + 1)) {
            this.p = this.adjustSeekIndex(this.p + 1);
        }
    }
    
    protected boolean sync(final int i) {
        assert i >= 0;
        final int n = i - this.tokens.size() + 1;
        if (n > 0) {
            final int fetched = this.fetch(n);
            return fetched >= n;
        }
        return true;
    }
    
    protected int fetch(final int n) {
        if (this.fetchedEOF) {
            return 0;
        }
        for (int i = 0; i < n; ++i) {
            final Token t = this.tokenSource.nextToken();
            if (t instanceof WritableToken) {
                ((WritableToken)t).setTokenIndex(this.tokens.size());
            }
            this.tokens.add(t);
            if (t.getType() == -1) {
                this.fetchedEOF = true;
                return i + 1;
            }
        }
        return n;
    }
    
    @Override
    public Token get(final int i) {
        if (i < 0 || i >= this.tokens.size()) {
            throw new IndexOutOfBoundsException("token index " + i + " out of range 0.." + (this.tokens.size() - 1));
        }
        return this.tokens.get(i);
    }
    
    public List<Token> get(final int start, int stop) {
        if (start < 0 || stop < 0) {
            return null;
        }
        this.lazyInit();
        final List<Token> subset = new ArrayList<Token>();
        if (stop >= this.tokens.size()) {
            stop = this.tokens.size() - 1;
        }
        for (int i = start; i <= stop; ++i) {
            final Token t = this.tokens.get(i);
            if (t.getType() == -1) {
                break;
            }
            subset.add(t);
        }
        return subset;
    }
    
    @Override
    public int LA(final int i) {
        return this.LT(i).getType();
    }
    
    protected Token LB(final int k) {
        if (this.p - k < 0) {
            return null;
        }
        return this.tokens.get(this.p - k);
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
        final int i = this.p + k - 1;
        this.sync(i);
        if (i >= this.tokens.size()) {
            return this.tokens.get(this.tokens.size() - 1);
        }
        return this.tokens.get(i);
    }
    
    protected int adjustSeekIndex(final int i) {
        return i;
    }
    
    protected final void lazyInit() {
        if (this.p == -1) {
            this.setup();
        }
    }
    
    protected void setup() {
        this.sync(0);
        this.p = this.adjustSeekIndex(0);
    }
    
    public void setTokenSource(final TokenSource tokenSource) {
        this.tokenSource = tokenSource;
        this.tokens.clear();
        this.p = -1;
    }
    
    public List<Token> getTokens() {
        return this.tokens;
    }
    
    public List<Token> getTokens(final int start, final int stop) {
        return this.getTokens(start, stop, null);
    }
    
    public List<Token> getTokens(final int start, final int stop, final Set<Integer> types) {
        this.lazyInit();
        if (start < 0 || stop >= this.tokens.size() || stop < 0 || start >= this.tokens.size()) {
            throw new IndexOutOfBoundsException("start " + start + " or stop " + stop + " not in 0.." + (this.tokens.size() - 1));
        }
        if (start > stop) {
            return null;
        }
        List<Token> filteredTokens = new ArrayList<Token>();
        for (int i = start; i <= stop; ++i) {
            final Token t = this.tokens.get(i);
            if (types == null || types.contains(t.getType())) {
                filteredTokens.add(t);
            }
        }
        if (filteredTokens.isEmpty()) {
            filteredTokens = null;
        }
        return filteredTokens;
    }
    
    public List<Token> getTokens(final int start, final int stop, final int ttype) {
        final HashSet<Integer> s = new HashSet<Integer>(ttype);
        s.add(ttype);
        return this.getTokens(start, stop, s);
    }
    
    protected int nextTokenOnChannel(int i, final int channel) {
        this.sync(i);
        if (i >= this.size()) {
            return this.size() - 1;
        }
        for (Token token = this.tokens.get(i); token.getChannel() != channel; token = this.tokens.get(i)) {
            if (token.getType() == -1) {
                return i;
            }
            ++i;
            this.sync(i);
        }
        return i;
    }
    
    protected int previousTokenOnChannel(int i, final int channel) {
        this.sync(i);
        if (i >= this.size()) {
            return this.size() - 1;
        }
        while (i >= 0) {
            final Token token = this.tokens.get(i);
            if (token.getType() == -1 || token.getChannel() == channel) {
                return i;
            }
            --i;
        }
        return i;
    }
    
    public List<Token> getHiddenTokensToRight(final int tokenIndex, final int channel) {
        this.lazyInit();
        if (tokenIndex < 0 || tokenIndex >= this.tokens.size()) {
            throw new IndexOutOfBoundsException(tokenIndex + " not in 0.." + (this.tokens.size() - 1));
        }
        final int nextOnChannel = this.nextTokenOnChannel(tokenIndex + 1, 0);
        final int from = tokenIndex + 1;
        int to;
        if (nextOnChannel == -1) {
            to = this.size() - 1;
        }
        else {
            to = nextOnChannel;
        }
        return this.filterForChannel(from, to, channel);
    }
    
    public List<Token> getHiddenTokensToRight(final int tokenIndex) {
        return this.getHiddenTokensToRight(tokenIndex, -1);
    }
    
    public List<Token> getHiddenTokensToLeft(final int tokenIndex, final int channel) {
        this.lazyInit();
        if (tokenIndex < 0 || tokenIndex >= this.tokens.size()) {
            throw new IndexOutOfBoundsException(tokenIndex + " not in 0.." + (this.tokens.size() - 1));
        }
        if (tokenIndex == 0) {
            return null;
        }
        final int prevOnChannel = this.previousTokenOnChannel(tokenIndex - 1, 0);
        if (prevOnChannel == tokenIndex - 1) {
            return null;
        }
        final int from = prevOnChannel + 1;
        final int to = tokenIndex - 1;
        return this.filterForChannel(from, to, channel);
    }
    
    public List<Token> getHiddenTokensToLeft(final int tokenIndex) {
        return this.getHiddenTokensToLeft(tokenIndex, -1);
    }
    
    protected List<Token> filterForChannel(final int from, final int to, final int channel) {
        final List<Token> hidden = new ArrayList<Token>();
        for (int i = from; i <= to; ++i) {
            final Token t = this.tokens.get(i);
            if (channel == -1) {
                if (t.getChannel() != 0) {
                    hidden.add(t);
                }
            }
            else if (t.getChannel() == channel) {
                hidden.add(t);
            }
        }
        if (hidden.size() == 0) {
            return null;
        }
        return hidden;
    }
    
    @Override
    public String getSourceName() {
        return this.tokenSource.getSourceName();
    }
    
    @Override
    public String getText() {
        this.lazyInit();
        this.fill();
        return this.getText(Interval.of(0, this.size() - 1));
    }
    
    @Override
    public String getText(final Interval interval) {
        final int start = interval.a;
        int stop = interval.b;
        if (start < 0 || stop < 0) {
            return "";
        }
        this.lazyInit();
        if (stop >= this.tokens.size()) {
            stop = this.tokens.size() - 1;
        }
        final StringBuilder buf = new StringBuilder();
        for (int i = start; i <= stop; ++i) {
            final Token t = this.tokens.get(i);
            if (t.getType() == -1) {
                break;
            }
            buf.append(t.getText());
        }
        return buf.toString();
    }
    
    @Override
    public String getText(final RuleContext ctx) {
        return this.getText(ctx.getSourceInterval());
    }
    
    @Override
    public String getText(final Token start, final Token stop) {
        if (start != null && stop != null) {
            return this.getText(Interval.of(start.getTokenIndex(), stop.getTokenIndex()));
        }
        return "";
    }
    
    public void fill() {
        this.lazyInit();
        final int blockSize = 1000;
        int fetched;
        do {
            fetched = this.fetch(1000);
        } while (fetched >= 1000);
    }
}
