package org.owasp.esapi.codecs;

public class MySQLCodec extends Codec
{
    public static final int MYSQL_MODE = 0;
    public static final int ANSI_MODE = 1;
    private Mode mode;
    
    @Deprecated
    public MySQLCodec(final int mode) {
        this.mode = Mode.findByKey(mode);
    }
    
    public MySQLCodec(final Mode mode) {
        this.mode = mode;
    }
    
    @Override
    public String encodeCharacter(final char[] immune, final Character c) {
        final char ch = c;
        if (Codec.containsCharacter(ch, immune)) {
            return "" + ch;
        }
        final String hex = Codec.getHexForNonAlphanumeric(ch);
        if (hex == null) {
            return "" + ch;
        }
        switch (this.mode) {
            case ANSI: {
                return this.encodeCharacterANSI(c);
            }
            case STANDARD: {
                return this.encodeCharacterMySQL(c);
            }
            default: {
                return null;
            }
        }
    }
    
    private String encodeCharacterANSI(final Character c) {
        if (c == '\\') {
            return "\\\\";
        }
        if (c == '\'') {
            return "''";
        }
        if (c == '\"') {
            return "";
        }
        return "" + c;
    }
    
    private String encodeCharacterMySQL(final Character c) {
        final char ch = c;
        if (ch == '\0') {
            return "\\0";
        }
        if (ch == '\b') {
            return "\\b";
        }
        if (ch == '\t') {
            return "\\t";
        }
        if (ch == '\n') {
            return "\\n";
        }
        if (ch == '\r') {
            return "\\r";
        }
        if (ch == '\u001a') {
            return "\\Z";
        }
        if (ch == '\"') {
            return "\\\"";
        }
        if (ch == '%') {
            return "\\%";
        }
        if (ch == '\'') {
            return "\\'";
        }
        if (ch == '\\') {
            return "\\\\";
        }
        if (ch == '_') {
            return "\\_";
        }
        return "\\" + c;
    }
    
    @Override
    public Character decodeCharacter(final PushbackString input) {
        switch (this.mode) {
            case ANSI: {
                return this.decodeCharacterANSI(input);
            }
            case STANDARD: {
                return this.decodeCharacterMySQL(input);
            }
            default: {
                return null;
            }
        }
    }
    
    private Character decodeCharacterANSI(final PushbackString input) {
        input.mark();
        final Character first = input.next();
        if (first == null) {
            input.reset();
            return null;
        }
        if (first != '\'') {
            input.reset();
            return null;
        }
        final Character second = input.next();
        if (second == null) {
            input.reset();
            return null;
        }
        if (second != '\'') {
            input.reset();
            return null;
        }
        return '\'';
    }
    
    private Character decodeCharacterMySQL(final PushbackString input) {
        input.mark();
        final Character first = input.next();
        if (first == null) {
            input.reset();
            return null;
        }
        if (first != '\\') {
            input.reset();
            return null;
        }
        final Character second = input.next();
        if (second == null) {
            input.reset();
            return null;
        }
        if (second == '0') {
            return '\0';
        }
        if (second == 'b') {
            return '\b';
        }
        if (second == 't') {
            return '\t';
        }
        if (second == 'n') {
            return '\n';
        }
        if (second == 'r') {
            return '\r';
        }
        if (second == 'z') {
            return '\u001a';
        }
        if (second == '\"') {
            return '\"';
        }
        if (second == '%') {
            return '%';
        }
        if (second == '\'') {
            return '\'';
        }
        if (second == '\\') {
            return '\\';
        }
        if (second == '_') {
            return '_';
        }
        return second;
    }
    
    public enum Mode
    {
        ANSI(1), 
        STANDARD(0);
        
        private int key;
        
        private Mode(final int key) {
            this.key = key;
        }
        
        static Mode findByKey(final int key) {
            for (final Mode m : values()) {
                if (m.key == key) {
                    return m;
                }
            }
            return null;
        }
    }
}
