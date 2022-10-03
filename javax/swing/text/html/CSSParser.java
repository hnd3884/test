package javax.swing.text.html;

import java.io.IOException;
import java.io.Reader;

class CSSParser
{
    private static final int IDENTIFIER = 1;
    private static final int BRACKET_OPEN = 2;
    private static final int BRACKET_CLOSE = 3;
    private static final int BRACE_OPEN = 4;
    private static final int BRACE_CLOSE = 5;
    private static final int PAREN_OPEN = 6;
    private static final int PAREN_CLOSE = 7;
    private static final int END = -1;
    private static final char[] charMapping;
    private boolean didPushChar;
    private int pushedChar;
    private StringBuffer unitBuffer;
    private int[] unitStack;
    private int stackCount;
    private Reader reader;
    private boolean encounteredRuleSet;
    private CSSParserCallback callback;
    private char[] tokenBuffer;
    private int tokenBufferLength;
    private boolean readWS;
    
    CSSParser() {
        this.unitStack = new int[2];
        this.tokenBuffer = new char[80];
        this.unitBuffer = new StringBuffer();
    }
    
    void parse(final Reader reader, final CSSParserCallback callback, final boolean b) throws IOException {
        this.callback = callback;
        final int n = 0;
        this.tokenBufferLength = n;
        this.stackCount = n;
        this.reader = reader;
        this.encounteredRuleSet = false;
        try {
            if (b) {
                this.parseDeclarationBlock();
            }
            else {
                while (this.getNextStatement()) {}
            }
        }
        finally {}
    }
    
    private boolean getNextStatement() throws IOException {
        this.unitBuffer.setLength(0);
        final int nextToken = this.nextToken('\0');
        switch (nextToken) {
            case 1: {
                if (this.tokenBufferLength > 0) {
                    if (this.tokenBuffer[0] == '@') {
                        this.parseAtRule();
                    }
                    else {
                        this.encounteredRuleSet = true;
                        this.parseRuleSet();
                    }
                }
                return true;
            }
            case 2:
            case 4:
            case 6: {
                this.parseTillClosed(nextToken);
                return true;
            }
            case 3:
            case 5:
            case 7: {
                throw new RuntimeException("Unexpected top level block close");
            }
            case -1: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    private void parseAtRule() throws IOException {
        int i = 0;
        final boolean b = this.tokenBufferLength == 7 && this.tokenBuffer[0] == '@' && this.tokenBuffer[1] == 'i' && this.tokenBuffer[2] == 'm' && this.tokenBuffer[3] == 'p' && this.tokenBuffer[4] == 'o' && this.tokenBuffer[5] == 'r' && this.tokenBuffer[6] == 't';
        this.unitBuffer.setLength(0);
        while (i == 0) {
            final int nextToken = this.nextToken(';');
            switch (nextToken) {
                case 1: {
                    if (this.tokenBufferLength > 0 && this.tokenBuffer[this.tokenBufferLength - 1] == ';') {
                        --this.tokenBufferLength;
                        i = 1;
                    }
                    if (this.tokenBufferLength > 0) {
                        if (this.unitBuffer.length() > 0 && this.readWS) {
                            this.unitBuffer.append(' ');
                        }
                        this.unitBuffer.append(this.tokenBuffer, 0, this.tokenBufferLength);
                        continue;
                    }
                    continue;
                }
                case 4: {
                    if (this.unitBuffer.length() > 0 && this.readWS) {
                        this.unitBuffer.append(' ');
                    }
                    this.unitBuffer.append(CSSParser.charMapping[nextToken]);
                    this.parseTillClosed(nextToken);
                    i = 1;
                    final int ws = this.readWS();
                    if (ws == -1 || ws == 59) {
                        continue;
                    }
                    this.pushChar(ws);
                    continue;
                }
                case 2:
                case 6: {
                    this.unitBuffer.append(CSSParser.charMapping[nextToken]);
                    this.parseTillClosed(nextToken);
                    continue;
                }
                case 3:
                case 5:
                case 7: {
                    throw new RuntimeException("Unexpected close in @ rule");
                }
                case -1: {
                    i = 1;
                    continue;
                }
            }
        }
        if (b && !this.encounteredRuleSet) {
            this.callback.handleImport(this.unitBuffer.toString());
        }
    }
    
    private void parseRuleSet() throws IOException {
        if (this.parseSelectors()) {
            this.callback.startRule();
            this.parseDeclarationBlock();
            this.callback.endRule();
        }
    }
    
    private boolean parseSelectors() throws IOException {
        if (this.tokenBufferLength > 0) {
            this.callback.handleSelector(new String(this.tokenBuffer, 0, this.tokenBufferLength));
        }
        this.unitBuffer.setLength(0);
        while (true) {
            final int nextToken;
            if ((nextToken = this.nextToken('\0')) == 1) {
                if (this.tokenBufferLength <= 0) {
                    continue;
                }
                this.callback.handleSelector(new String(this.tokenBuffer, 0, this.tokenBufferLength));
            }
            else {
                switch (nextToken) {
                    case 4: {
                        return true;
                    }
                    case 2:
                    case 6: {
                        this.parseTillClosed(nextToken);
                        this.unitBuffer.setLength(0);
                        continue;
                    }
                    case 3:
                    case 5:
                    case 7: {
                        throw new RuntimeException("Unexpected block close in selector");
                    }
                    case -1: {
                        return false;
                    }
                }
            }
        }
    }
    
    private void parseDeclarationBlock() throws IOException {
        while (true) {
            switch (this.parseDeclaration()) {
                case -1:
                case 5: {
                    return;
                }
                case 3:
                case 7: {
                    throw new RuntimeException("Unexpected close in declaration block");
                }
                default: {
                    continue;
                }
            }
        }
    }
    
    private int parseDeclaration() throws IOException {
        final int identifiers;
        if ((identifiers = this.parseIdentifiers(':', false)) != 1) {
            return identifiers;
        }
        for (int i = this.unitBuffer.length() - 1; i >= 0; --i) {
            this.unitBuffer.setCharAt(i, Character.toLowerCase(this.unitBuffer.charAt(i)));
        }
        this.callback.handleProperty(this.unitBuffer.toString());
        final int identifiers2 = this.parseIdentifiers(';', true);
        this.callback.handleValue(this.unitBuffer.toString());
        return identifiers2;
    }
    
    private int parseIdentifiers(final char c, final boolean b) throws IOException {
        this.unitBuffer.setLength(0);
        while (true) {
            final int nextToken = this.nextToken(c);
            switch (nextToken) {
                case 1: {
                    if (this.tokenBufferLength <= 0) {
                        continue;
                    }
                    if (this.tokenBuffer[this.tokenBufferLength - 1] == c) {
                        if (--this.tokenBufferLength > 0) {
                            if (this.readWS && this.unitBuffer.length() > 0) {
                                this.unitBuffer.append(' ');
                            }
                            this.unitBuffer.append(this.tokenBuffer, 0, this.tokenBufferLength);
                        }
                        return 1;
                    }
                    if (this.readWS && this.unitBuffer.length() > 0) {
                        this.unitBuffer.append(' ');
                    }
                    this.unitBuffer.append(this.tokenBuffer, 0, this.tokenBufferLength);
                    continue;
                }
                case 2:
                case 4:
                case 6: {
                    final int length = this.unitBuffer.length();
                    if (b) {
                        this.unitBuffer.append(CSSParser.charMapping[nextToken]);
                    }
                    this.parseTillClosed(nextToken);
                    if (!b) {
                        this.unitBuffer.setLength(length);
                        continue;
                    }
                    continue;
                }
                case -1:
                case 3:
                case 5:
                case 7: {
                    return nextToken;
                }
            }
        }
    }
    
    private void parseTillClosed(final int n) throws IOException {
        int i = 0;
        this.startBlock(n);
        while (i == 0) {
            final int nextToken = this.nextToken('\0');
            switch (nextToken) {
                case 1: {
                    if (this.unitBuffer.length() > 0 && this.readWS) {
                        this.unitBuffer.append(' ');
                    }
                    if (this.tokenBufferLength > 0) {
                        this.unitBuffer.append(this.tokenBuffer, 0, this.tokenBufferLength);
                        continue;
                    }
                    continue;
                }
                case 2:
                case 4:
                case 6: {
                    if (this.unitBuffer.length() > 0 && this.readWS) {
                        this.unitBuffer.append(' ');
                    }
                    this.unitBuffer.append(CSSParser.charMapping[nextToken]);
                    this.startBlock(nextToken);
                    continue;
                }
                case 3:
                case 5:
                case 7: {
                    if (this.unitBuffer.length() > 0 && this.readWS) {
                        this.unitBuffer.append(' ');
                    }
                    this.unitBuffer.append(CSSParser.charMapping[nextToken]);
                    this.endBlock(nextToken);
                    if (!this.inBlock()) {
                        i = 1;
                        continue;
                    }
                    continue;
                }
                case -1: {
                    throw new RuntimeException("Unclosed block");
                }
            }
        }
    }
    
    private int nextToken(final char c) throws IOException {
        this.readWS = false;
        final int ws = this.readWS();
        switch (ws) {
            case 39: {
                this.readTill('\'');
                if (this.tokenBufferLength > 0) {
                    --this.tokenBufferLength;
                }
                return 1;
            }
            case 34: {
                this.readTill('\"');
                if (this.tokenBufferLength > 0) {
                    --this.tokenBufferLength;
                }
                return 1;
            }
            case 91: {
                return 2;
            }
            case 93: {
                return 3;
            }
            case 123: {
                return 4;
            }
            case 125: {
                return 5;
            }
            case 40: {
                return 6;
            }
            case 41: {
                return 7;
            }
            case -1: {
                return -1;
            }
            default: {
                this.pushChar(ws);
                this.getIdentifier(c);
                return 1;
            }
        }
    }
    
    private boolean getIdentifier(final char c) throws IOException {
        int n = 0;
        int i = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        this.tokenBufferLength = 0;
        while (i == 0) {
            final int char1 = this.readChar();
            int n5 = 0;
            switch (char1) {
                case 92: {
                    n5 = 1;
                    break;
                }
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    n5 = 2;
                    n4 = char1 - 48;
                    break;
                }
                case 97:
                case 98:
                case 99:
                case 100:
                case 101:
                case 102: {
                    n5 = 2;
                    n4 = char1 - 97 + 10;
                    break;
                }
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70: {
                    n5 = 2;
                    n4 = char1 - 65 + 10;
                    break;
                }
                case 9:
                case 10:
                case 13:
                case 32:
                case 34:
                case 39:
                case 40:
                case 41:
                case 91:
                case 93:
                case 123:
                case 125: {
                    n5 = 3;
                    break;
                }
                case 47: {
                    n5 = 4;
                    break;
                }
                case -1: {
                    i = 1;
                    n5 = 0;
                    break;
                }
                default: {
                    n5 = 0;
                    break;
                }
            }
            if (n != 0) {
                if (n5 == 2) {
                    n3 = n3 * 16 + n4;
                    if (++n2 != 4) {
                        continue;
                    }
                    n = 0;
                    this.append((char)n3);
                }
                else {
                    n = 0;
                    if (n2 > 0) {
                        this.append((char)n3);
                        this.pushChar(char1);
                    }
                    else {
                        if (i != 0) {
                            continue;
                        }
                        this.append((char)char1);
                    }
                }
            }
            else {
                if (i != 0) {
                    continue;
                }
                if (n5 == 1) {
                    n = 1;
                    n2 = (n3 = 0);
                }
                else if (n5 == 3) {
                    i = 1;
                    this.pushChar(char1);
                }
                else if (n5 == 4) {
                    final int char2 = this.readChar();
                    if (char2 == 42) {
                        i = 1;
                        this.readComment();
                        this.readWS = true;
                    }
                    else {
                        this.append('/');
                        if (char2 == -1) {
                            i = 1;
                        }
                        else {
                            this.pushChar(char2);
                        }
                    }
                }
                else {
                    this.append((char)char1);
                    if (char1 != c) {
                        continue;
                    }
                    i = 1;
                }
            }
        }
        return this.tokenBufferLength > 0;
    }
    
    private void readTill(final char c) throws IOException {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int i = 0;
        int n4 = 0;
        this.tokenBufferLength = 0;
        while (i == 0) {
            final int char1 = this.readChar();
            int n5 = 0;
            switch (char1) {
                case 92: {
                    n5 = 1;
                    break;
                }
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    n5 = 2;
                    n4 = char1 - 48;
                    break;
                }
                case 97:
                case 98:
                case 99:
                case 100:
                case 101:
                case 102: {
                    n5 = 2;
                    n4 = char1 - 97 + 10;
                    break;
                }
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70: {
                    n5 = 2;
                    n4 = char1 - 65 + 10;
                    break;
                }
                case -1: {
                    throw new RuntimeException("Unclosed " + c);
                }
                default: {
                    n5 = 0;
                    break;
                }
            }
            if (n != 0) {
                if (n5 == 2) {
                    n3 = n3 * 16 + n4;
                    if (++n2 != 4) {
                        continue;
                    }
                    n = 0;
                    this.append((char)n3);
                }
                else if (n2 > 0) {
                    this.append((char)n3);
                    if (n5 == 1) {
                        n = 1;
                        n2 = (n3 = 0);
                    }
                    else {
                        if (char1 == c) {
                            i = 1;
                        }
                        this.append((char)char1);
                        n = 0;
                    }
                }
                else {
                    this.append((char)char1);
                    n = 0;
                }
            }
            else if (n5 == 1) {
                n = 1;
                n2 = (n3 = 0);
            }
            else {
                if (char1 == c) {
                    i = 1;
                }
                this.append((char)char1);
            }
        }
    }
    
    private void append(final char c) {
        if (this.tokenBufferLength == this.tokenBuffer.length) {
            final char[] tokenBuffer = new char[this.tokenBuffer.length * 2];
            System.arraycopy(this.tokenBuffer, 0, tokenBuffer, 0, this.tokenBuffer.length);
            this.tokenBuffer = tokenBuffer;
        }
        this.tokenBuffer[this.tokenBufferLength++] = c;
    }
    
    private void readComment() throws IOException {
        while (true) {
            switch (this.readChar()) {
                case -1: {
                    throw new RuntimeException("Unclosed comment");
                }
                case 42: {
                    final int char1 = this.readChar();
                    if (char1 == 47) {
                        return;
                    }
                    if (char1 == -1) {
                        throw new RuntimeException("Unclosed comment");
                    }
                    this.pushChar(char1);
                    continue;
                }
                default: {
                    continue;
                }
            }
        }
    }
    
    private void startBlock(final int n) {
        if (this.stackCount == this.unitStack.length) {
            final int[] unitStack = new int[this.stackCount * 2];
            System.arraycopy(this.unitStack, 0, unitStack, 0, this.stackCount);
            this.unitStack = unitStack;
        }
        this.unitStack[this.stackCount++] = n;
    }
    
    private void endBlock(final int n) {
        int n2 = 0;
        switch (n) {
            case 3: {
                n2 = 2;
                break;
            }
            case 5: {
                n2 = 4;
                break;
            }
            case 7: {
                n2 = 6;
                break;
            }
            default: {
                n2 = -1;
                break;
            }
        }
        if (this.stackCount > 0 && this.unitStack[this.stackCount - 1] == n2) {
            --this.stackCount;
            return;
        }
        throw new RuntimeException("Unmatched block");
    }
    
    private boolean inBlock() {
        return this.stackCount > 0;
    }
    
    private int readWS() throws IOException {
        int char1;
        while ((char1 = this.readChar()) != -1 && Character.isWhitespace((char)char1)) {
            this.readWS = true;
        }
        return char1;
    }
    
    private int readChar() throws IOException {
        if (this.didPushChar) {
            this.didPushChar = false;
            return this.pushedChar;
        }
        return this.reader.read();
    }
    
    private void pushChar(final int pushedChar) {
        if (this.didPushChar) {
            throw new RuntimeException("Can not handle look ahead of more than one character");
        }
        this.didPushChar = true;
        this.pushedChar = pushedChar;
    }
    
    static {
        charMapping = new char[] { '\0', '\0', '[', ']', '{', '}', '(', ')', '\0' };
    }
    
    interface CSSParserCallback
    {
        void handleImport(final String p0);
        
        void handleSelector(final String p0);
        
        void startRule();
        
        void handleProperty(final String p0);
        
        void handleValue(final String p0);
        
        void endRule();
    }
}
