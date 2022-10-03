package org.apache.catalina.valves.rewrite;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import org.apache.tomcat.util.res.StringManager;

public class QuotedStringTokenizer
{
    protected static final StringManager sm;
    private Iterator<String> tokenIterator;
    private int tokenCount;
    private int returnedTokens;
    
    public QuotedStringTokenizer(final String text) {
        this.returnedTokens = 0;
        List<String> tokens;
        if (text != null) {
            tokens = this.tokenizeText(text);
        }
        else {
            tokens = Collections.emptyList();
        }
        this.tokenCount = tokens.size();
        this.tokenIterator = tokens.iterator();
    }
    
    private List<String> tokenizeText(final String inputText) {
        final List<String> tokens = new ArrayList<String>();
        int pos = 0;
        final int length = inputText.length();
        WordMode currentMode = WordMode.SPACES;
        final StringBuilder currentToken = new StringBuilder();
        while (pos < length) {
            final char currentChar = inputText.charAt(pos);
            switch (currentMode) {
                case SPACES: {
                    currentMode = this.handleSpaces(currentToken, currentChar);
                    break;
                }
                case QUOTED: {
                    currentMode = this.handleQuoted(tokens, currentToken, currentChar);
                    break;
                }
                case ESCAPED: {
                    currentToken.append(currentChar);
                    currentMode = WordMode.QUOTED;
                    break;
                }
                case SIMPLE: {
                    currentMode = this.handleSimple(tokens, currentToken, currentChar);
                    break;
                }
                case COMMENT: {
                    if (currentChar == '\r' || currentChar == '\n') {
                        currentMode = WordMode.SPACES;
                        break;
                    }
                    break;
                }
                default: {
                    throw new IllegalStateException(QuotedStringTokenizer.sm.getString("quotedStringTokenizer.tokenizeError", new Object[] { inputText, pos, currentMode }));
                }
            }
            ++pos;
        }
        final String possibleLastToken = currentToken.toString();
        if (!possibleLastToken.isEmpty()) {
            tokens.add(possibleLastToken);
        }
        return tokens;
    }
    
    private WordMode handleSimple(final List<String> tokens, final StringBuilder currentToken, final char currentChar) {
        if (Character.isWhitespace(currentChar)) {
            tokens.add(currentToken.toString());
            currentToken.setLength(0);
            return WordMode.SPACES;
        }
        currentToken.append(currentChar);
        return WordMode.SIMPLE;
    }
    
    private WordMode handleQuoted(final List<String> tokens, final StringBuilder currentToken, final char currentChar) {
        if (currentChar == '\"') {
            tokens.add(currentToken.toString());
            currentToken.setLength(0);
            return WordMode.SPACES;
        }
        if (currentChar == '\\') {
            return WordMode.ESCAPED;
        }
        currentToken.append(currentChar);
        return WordMode.QUOTED;
    }
    
    private WordMode handleSpaces(final StringBuilder currentToken, final char currentChar) {
        if (Character.isWhitespace(currentChar)) {
            return WordMode.SPACES;
        }
        if (currentChar == '\"') {
            return WordMode.QUOTED;
        }
        if (currentChar == '#') {
            return WordMode.COMMENT;
        }
        currentToken.append(currentChar);
        return WordMode.SIMPLE;
    }
    
    public boolean hasMoreTokens() {
        return this.tokenIterator.hasNext();
    }
    
    public String nextToken() {
        ++this.returnedTokens;
        return this.tokenIterator.next();
    }
    
    public int countTokens() {
        return this.tokenCount - this.returnedTokens;
    }
    
    static {
        sm = StringManager.getManager((Class)QuotedStringTokenizer.class);
    }
    
    enum WordMode
    {
        SPACES, 
        QUOTED, 
        ESCAPED, 
        SIMPLE, 
        COMMENT;
    }
}
