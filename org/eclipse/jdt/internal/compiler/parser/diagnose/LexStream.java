package org.eclipse.jdt.internal.compiler.parser.diagnose;

import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;

public class LexStream implements TerminalTokens
{
    public static final int IS_AFTER_JUMP = 1;
    public static final int LBRACE_MISSING = 2;
    private int tokenCacheIndex;
    private int tokenCacheEOFIndex;
    private Token[] tokenCache;
    private int currentIndex;
    private Scanner scanner;
    private int[] intervalStartToSkip;
    private int[] intervalEndToSkip;
    private int[] intervalFlagsToSkip;
    private int previousInterval;
    private int currentInterval;
    private boolean awaitingColonColon;
    
    public LexStream(final int size, final Scanner scanner, final int[] intervalStartToSkip, final int[] intervalEndToSkip, final int[] intervalFlagsToSkip, final int firstToken, final int init, final int eof) {
        this.currentIndex = -1;
        this.previousInterval = -1;
        this.currentInterval = -1;
        this.tokenCache = new Token[size];
        this.tokenCacheIndex = 0;
        this.tokenCacheEOFIndex = Integer.MAX_VALUE;
        this.tokenCache[0] = new Token();
        this.tokenCache[0].kind = firstToken;
        this.tokenCache[0].name = CharOperation.NO_CHAR;
        this.tokenCache[0].start = init;
        this.tokenCache[0].end = init;
        this.tokenCache[0].line = 0;
        this.intervalStartToSkip = intervalStartToSkip;
        this.intervalEndToSkip = intervalEndToSkip;
        this.intervalFlagsToSkip = intervalFlagsToSkip;
        this.awaitingColonColon = false;
        scanner.resetTo(init, eof);
        this.scanner = scanner;
    }
    
    private void readTokenFromScanner() {
        final int length = this.tokenCache.length;
        boolean tokenNotFound = true;
        while (tokenNotFound) {
            try {
                final int tokenKind = this.scanner.getNextToken();
                if (tokenKind == 83) {
                    this.awaitingColonColon = true;
                }
                else if (tokenKind == 7) {
                    this.awaitingColonColon = false;
                }
                if (tokenKind != 60) {
                    final int start = this.scanner.getCurrentTokenStartPosition();
                    final int end = this.scanner.getCurrentTokenEndPosition();
                    final int nextInterval = this.currentInterval + 1;
                    if (this.intervalStartToSkip.length == 0 || nextInterval >= this.intervalStartToSkip.length || start < this.intervalStartToSkip[nextInterval]) {
                        final Token token = new Token();
                        token.kind = tokenKind;
                        token.name = this.scanner.getCurrentTokenSource();
                        token.start = start;
                        token.end = end;
                        token.line = Util.getLineNumber(end, this.scanner.lineEnds, 0, this.scanner.linePtr);
                        if (this.currentInterval != this.previousInterval && (this.intervalFlagsToSkip[this.currentInterval] & 0x2) == 0x0) {
                            token.flags = 1;
                            if ((this.intervalFlagsToSkip[this.currentInterval] & 0x1) != 0x0) {
                                final Token token3 = token;
                                token3.flags |= 0x2;
                            }
                        }
                        this.previousInterval = this.currentInterval;
                        this.tokenCache[++this.tokenCacheIndex % length] = token;
                        tokenNotFound = false;
                    }
                    else {
                        this.scanner.resetTo(this.intervalEndToSkip[++this.currentInterval] + 1, this.scanner.eofPosition - 1);
                    }
                }
                else {
                    final int start = this.scanner.getCurrentTokenStartPosition();
                    final int end = this.scanner.getCurrentTokenEndPosition();
                    final Token token2 = new Token();
                    token2.kind = tokenKind;
                    token2.name = CharOperation.NO_CHAR;
                    token2.start = start;
                    token2.end = end;
                    token2.line = Util.getLineNumber(end, this.scanner.lineEnds, 0, this.scanner.linePtr);
                    this.tokenCache[++this.tokenCacheIndex % length] = token2;
                    this.tokenCacheEOFIndex = this.tokenCacheIndex;
                    tokenNotFound = false;
                }
            }
            catch (final InvalidInputException ex) {}
        }
    }
    
    public Token token(final int index) {
        if (index < 0) {
            final Token eofToken = new Token();
            eofToken.kind = 60;
            eofToken.name = CharOperation.NO_CHAR;
            return eofToken;
        }
        if (this.tokenCacheEOFIndex >= 0 && index > this.tokenCacheEOFIndex) {
            return this.token(this.tokenCacheEOFIndex);
        }
        final int length = this.tokenCache.length;
        if (index > this.tokenCacheIndex) {
            int tokensToRead = index - this.tokenCacheIndex;
            while (tokensToRead-- != 0) {
                this.readTokenFromScanner();
            }
        }
        else if (this.tokenCacheIndex - length >= index) {
            return null;
        }
        return this.tokenCache[index % length];
    }
    
    public int getToken() {
        return this.currentIndex = this.next(this.currentIndex);
    }
    
    public int previous(final int tokenIndex) {
        return (tokenIndex > 0) ? (tokenIndex - 1) : 0;
    }
    
    public int next(final int tokenIndex) {
        return (tokenIndex < this.tokenCacheEOFIndex) ? (tokenIndex + 1) : this.tokenCacheEOFIndex;
    }
    
    public boolean afterEol(final int i) {
        return i < 1 || this.line(i - 1) < this.line(i);
    }
    
    public void reset() {
        this.currentIndex = -1;
    }
    
    public void reset(final int i) {
        this.currentIndex = this.previous(i);
    }
    
    public int badtoken() {
        return 0;
    }
    
    public int kind(final int tokenIndex) {
        return this.token(tokenIndex).kind;
    }
    
    public char[] name(final int tokenIndex) {
        return this.token(tokenIndex).name;
    }
    
    public int line(final int tokenIndex) {
        return this.token(tokenIndex).line;
    }
    
    public int start(final int tokenIndex) {
        return this.token(tokenIndex).start;
    }
    
    public int end(final int tokenIndex) {
        return this.token(tokenIndex).end;
    }
    
    public int flags(final int tokenIndex) {
        return this.token(tokenIndex).flags;
    }
    
    public boolean isInsideStream(final int index) {
        return (this.tokenCacheEOFIndex < 0 || index <= this.tokenCacheEOFIndex) && (index > this.tokenCacheIndex || this.tokenCacheIndex - this.tokenCache.length < index);
    }
    
    @Override
    public String toString() {
        final StringBuffer res = new StringBuffer();
        final String source = new String(this.scanner.source);
        if (this.currentIndex < 0) {
            int previousEnd = -1;
            for (int i = 0; i < this.intervalStartToSkip.length; ++i) {
                final int intervalStart = this.intervalStartToSkip[i];
                final int intervalEnd = this.intervalEndToSkip[i];
                res.append(source.substring(previousEnd + 1, intervalStart));
                res.append('<');
                res.append('@');
                res.append(source.substring(intervalStart, intervalEnd + 1));
                res.append('@');
                res.append('>');
                previousEnd = intervalEnd;
            }
            res.append(source.substring(previousEnd + 1));
        }
        else {
            final Token token = this.token(this.currentIndex);
            final int curtokKind = token.kind;
            final int curtokStart = token.start;
            final int curtokEnd = token.end;
            int previousEnd2 = -1;
            for (int j = 0; j < this.intervalStartToSkip.length; ++j) {
                final int intervalStart2 = this.intervalStartToSkip[j];
                final int intervalEnd2 = this.intervalEndToSkip[j];
                if (curtokStart >= previousEnd2 && curtokEnd <= intervalStart2) {
                    res.append(source.substring(previousEnd2 + 1, curtokStart));
                    res.append('<');
                    res.append('#');
                    res.append(source.substring(curtokStart, curtokEnd + 1));
                    res.append('#');
                    res.append('>');
                    res.append(source.substring(curtokEnd + 1, intervalStart2));
                }
                else {
                    res.append(source.substring(previousEnd2 + 1, intervalStart2));
                }
                res.append('<');
                res.append('@');
                res.append(source.substring(intervalStart2, intervalEnd2 + 1));
                res.append('@');
                res.append('>');
                previousEnd2 = intervalEnd2;
            }
            if (curtokStart >= previousEnd2) {
                res.append(source.substring(previousEnd2 + 1, curtokStart));
                res.append('<');
                res.append('#');
                if (curtokKind == 60) {
                    res.append("EOF#>");
                }
                else {
                    res.append(source.substring(curtokStart, curtokEnd + 1));
                    res.append('#');
                    res.append('>');
                    res.append(source.substring(curtokEnd + 1));
                }
            }
            else {
                res.append(source.substring(previousEnd2 + 1));
            }
        }
        return res.toString();
    }
    
    public boolean awaitingColonColon() {
        return this.awaitingColonColon;
    }
    
    public static class Token
    {
        int kind;
        char[] name;
        int start;
        int end;
        int line;
        int flags;
        
        @Override
        public String toString() {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(this.name).append('[').append(this.kind).append(']');
            buffer.append('{').append(this.start).append(',').append(this.end).append('}').append(this.line);
            return buffer.toString();
        }
    }
}
