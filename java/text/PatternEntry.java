package java.text;

class PatternEntry
{
    static final int RESET = -2;
    static final int UNSET = -1;
    int strength;
    String chars;
    String extension;
    
    public void appendQuotedExtension(final StringBuffer sb) {
        appendQuoted(this.extension, sb);
    }
    
    public void appendQuotedChars(final StringBuffer sb) {
        appendQuoted(this.chars, sb);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && this.chars.equals(((PatternEntry)o).chars);
    }
    
    @Override
    public int hashCode() {
        return this.chars.hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        this.addToBuffer(sb, true, false, null);
        return sb.toString();
    }
    
    final int getStrength() {
        return this.strength;
    }
    
    final String getExtension() {
        return this.extension;
    }
    
    final String getChars() {
        return this.chars;
    }
    
    void addToBuffer(final StringBuffer sb, final boolean b, final boolean b2, final PatternEntry patternEntry) {
        if (b2 && sb.length() > 0) {
            if (this.strength == 0 || patternEntry != null) {
                sb.append('\n');
            }
            else {
                sb.append(' ');
            }
        }
        if (patternEntry != null) {
            sb.append('&');
            if (b2) {
                sb.append(' ');
            }
            patternEntry.appendQuotedChars(sb);
            this.appendQuotedExtension(sb);
            if (b2) {
                sb.append(' ');
            }
        }
        switch (this.strength) {
            case 3: {
                sb.append('=');
                break;
            }
            case 2: {
                sb.append(',');
                break;
            }
            case 1: {
                sb.append(';');
                break;
            }
            case 0: {
                sb.append('<');
                break;
            }
            case -2: {
                sb.append('&');
                break;
            }
            case -1: {
                sb.append('?');
                break;
            }
        }
        if (b2) {
            sb.append(' ');
        }
        appendQuoted(this.chars, sb);
        if (b && this.extension.length() != 0) {
            sb.append('/');
            appendQuoted(this.extension, sb);
        }
    }
    
    static void appendQuoted(final String s, final StringBuffer sb) {
        int n = 0;
        final char char1 = s.charAt(0);
        if (Character.isSpaceChar(char1)) {
            n = 1;
            sb.append('\'');
        }
        else if (isSpecialChar(char1)) {
            n = 1;
            sb.append('\'');
        }
        else {
            switch (char1) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case '\u0010':
                case '@': {
                    n = 1;
                    sb.append('\'');
                    break;
                }
                case '\'': {
                    n = 1;
                    sb.append('\'');
                    break;
                }
                default: {
                    if (n != 0) {
                        n = 0;
                        sb.append('\'');
                        break;
                    }
                    break;
                }
            }
        }
        sb.append(s);
        if (n != 0) {
            sb.append('\'');
        }
    }
    
    PatternEntry(final int strength, final StringBuffer sb, final StringBuffer sb2) {
        this.strength = -1;
        this.chars = "";
        this.extension = "";
        this.strength = strength;
        this.chars = sb.toString();
        this.extension = ((sb2.length() > 0) ? sb2.toString() : "");
    }
    
    static boolean isSpecialChar(final char c) {
        return c == ' ' || (c <= '/' && c >= '\"') || (c <= '?' && c >= ':') || (c <= '`' && c >= '[') || (c <= '~' && c >= '{');
    }
    
    static class Parser
    {
        private String pattern;
        private int i;
        private StringBuffer newChars;
        private StringBuffer newExtension;
        
        public Parser(final String pattern) {
            this.newChars = new StringBuffer();
            this.newExtension = new StringBuffer();
            this.pattern = pattern;
            this.i = 0;
        }
        
        public PatternEntry next() throws ParseException {
            int n = -1;
            this.newChars.setLength(0);
            this.newExtension.setLength(0);
            int n2 = 1;
            int n3 = 0;
        Label_0546:
            while (this.i < this.pattern.length()) {
                final char char1 = this.pattern.charAt(this.i);
                if (n3 != 0) {
                    if (char1 == '\'') {
                        n3 = 0;
                    }
                    else if (this.newChars.length() == 0) {
                        this.newChars.append(char1);
                    }
                    else if (n2 != 0) {
                        this.newChars.append(char1);
                    }
                    else {
                        this.newExtension.append(char1);
                    }
                }
                else {
                    switch (char1) {
                        case '=': {
                            if (n != -1) {
                                break Label_0546;
                            }
                            n = 3;
                            break;
                        }
                        case ',': {
                            if (n != -1) {
                                break Label_0546;
                            }
                            n = 2;
                            break;
                        }
                        case ';': {
                            if (n != -1) {
                                break Label_0546;
                            }
                            n = 1;
                            break;
                        }
                        case '<': {
                            if (n != -1) {
                                break Label_0546;
                            }
                            n = 0;
                            break;
                        }
                        case '&': {
                            if (n != -1) {
                                break Label_0546;
                            }
                            n = -2;
                            break;
                        }
                        case '\t':
                        case '\n':
                        case '\f':
                        case '\r':
                        case ' ': {
                            break;
                        }
                        case '/': {
                            n2 = 0;
                            break;
                        }
                        case '\'': {
                            n3 = 1;
                            final char char2 = this.pattern.charAt(++this.i);
                            if (this.newChars.length() == 0) {
                                this.newChars.append(char2);
                                break;
                            }
                            if (n2 != 0) {
                                this.newChars.append(char2);
                                break;
                            }
                            this.newExtension.append(char2);
                            break;
                        }
                        default: {
                            if (n == -1) {
                                throw new ParseException("missing char (=,;<&) : " + this.pattern.substring(this.i, (this.i + 10 < this.pattern.length()) ? (this.i + 10) : this.pattern.length()), this.i);
                            }
                            if (PatternEntry.isSpecialChar(char1) && n3 == 0) {
                                throw new ParseException("Unquoted punctuation character : " + Integer.toString(char1, 16), this.i);
                            }
                            if (n2 != 0) {
                                this.newChars.append(char1);
                                break;
                            }
                            this.newExtension.append(char1);
                            break;
                        }
                    }
                }
                ++this.i;
            }
            if (n == -1) {
                return null;
            }
            if (this.newChars.length() == 0) {
                throw new ParseException("missing chars (=,;<&): " + this.pattern.substring(this.i, (this.i + 10 < this.pattern.length()) ? (this.i + 10) : this.pattern.length()), this.i);
            }
            return new PatternEntry(n, this.newChars, this.newExtension);
        }
    }
}
