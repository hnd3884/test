package java.util.regex;

import java.util.Locale;
import java.util.HashMap;

enum UnicodeProp
{
    ALPHABETIC {
        @Override
        public boolean is(final int n) {
            return Character.isAlphabetic(n);
        }
    }, 
    LETTER {
        @Override
        public boolean is(final int n) {
            return Character.isLetter(n);
        }
    }, 
    IDEOGRAPHIC {
        @Override
        public boolean is(final int n) {
            return Character.isIdeographic(n);
        }
    }, 
    LOWERCASE {
        @Override
        public boolean is(final int n) {
            return Character.isLowerCase(n);
        }
    }, 
    UPPERCASE {
        @Override
        public boolean is(final int n) {
            return Character.isUpperCase(n);
        }
    }, 
    TITLECASE {
        @Override
        public boolean is(final int n) {
            return Character.isTitleCase(n);
        }
    }, 
    WHITE_SPACE {
        @Override
        public boolean is(final int n) {
            return (28672 >> Character.getType(n) & 0x1) != 0x0 || (n >= 9 && n <= 13) || n == 133;
        }
    }, 
    CONTROL {
        @Override
        public boolean is(final int n) {
            return Character.getType(n) == 15;
        }
    }, 
    PUNCTUATION {
        @Override
        public boolean is(final int n) {
            return (1643118592 >> Character.getType(n) & 0x1) != 0x0;
        }
    }, 
    HEX_DIGIT {
        @Override
        public boolean is(final int n) {
            return UnicodeProp$10.DIGIT.is(n) || (n >= 48 && n <= 57) || (n >= 65 && n <= 70) || (n >= 97 && n <= 102) || (n >= 65296 && n <= 65305) || (n >= 65313 && n <= 65318) || (n >= 65345 && n <= 65350);
        }
    }, 
    ASSIGNED {
        @Override
        public boolean is(final int n) {
            return Character.getType(n) != 0;
        }
    }, 
    NONCHARACTER_CODE_POINT {
        @Override
        public boolean is(final int n) {
            return (n & 0xFFFE) == 0xFFFE || (n >= 64976 && n <= 65007);
        }
    }, 
    DIGIT {
        @Override
        public boolean is(final int n) {
            return Character.isDigit(n);
        }
    }, 
    ALNUM {
        @Override
        public boolean is(final int n) {
            return UnicodeProp$14.ALPHABETIC.is(n) || UnicodeProp$14.DIGIT.is(n);
        }
    }, 
    BLANK {
        @Override
        public boolean is(final int n) {
            return Character.getType(n) == 12 || n == 9;
        }
    }, 
    GRAPH {
        @Override
        public boolean is(final int n) {
            return (585729 >> Character.getType(n) & 0x1) == 0x0;
        }
    }, 
    PRINT {
        @Override
        public boolean is(final int n) {
            return (UnicodeProp$17.GRAPH.is(n) || UnicodeProp$17.BLANK.is(n)) && !UnicodeProp$17.CONTROL.is(n);
        }
    }, 
    WORD {
        @Override
        public boolean is(final int n) {
            return UnicodeProp$18.ALPHABETIC.is(n) || (8389568 >> Character.getType(n) & 0x1) != 0x0 || UnicodeProp$18.JOIN_CONTROL.is(n);
        }
    }, 
    JOIN_CONTROL {
        @Override
        public boolean is(final int n) {
            return n == 8204 || n == 8205;
        }
    };
    
    private static final HashMap<String, String> posix;
    private static final HashMap<String, String> aliases;
    
    public static UnicodeProp forName(String upperCase) {
        upperCase = upperCase.toUpperCase(Locale.ENGLISH);
        final String s = UnicodeProp.aliases.get(upperCase);
        if (s != null) {
            upperCase = s;
        }
        try {
            return valueOf(upperCase);
        }
        catch (final IllegalArgumentException ex) {
            return null;
        }
    }
    
    public static UnicodeProp forPOSIXName(String s) {
        s = UnicodeProp.posix.get(s.toUpperCase(Locale.ENGLISH));
        if (s == null) {
            return null;
        }
        return valueOf(s);
    }
    
    public abstract boolean is(final int p0);
    
    static {
        posix = new HashMap<String, String>();
        aliases = new HashMap<String, String>();
        UnicodeProp.posix.put("ALPHA", "ALPHABETIC");
        UnicodeProp.posix.put("LOWER", "LOWERCASE");
        UnicodeProp.posix.put("UPPER", "UPPERCASE");
        UnicodeProp.posix.put("SPACE", "WHITE_SPACE");
        UnicodeProp.posix.put("PUNCT", "PUNCTUATION");
        UnicodeProp.posix.put("XDIGIT", "HEX_DIGIT");
        UnicodeProp.posix.put("ALNUM", "ALNUM");
        UnicodeProp.posix.put("CNTRL", "CONTROL");
        UnicodeProp.posix.put("DIGIT", "DIGIT");
        UnicodeProp.posix.put("BLANK", "BLANK");
        UnicodeProp.posix.put("GRAPH", "GRAPH");
        UnicodeProp.posix.put("PRINT", "PRINT");
        UnicodeProp.aliases.put("WHITESPACE", "WHITE_SPACE");
        UnicodeProp.aliases.put("HEXDIGIT", "HEX_DIGIT");
        UnicodeProp.aliases.put("NONCHARACTERCODEPOINT", "NONCHARACTER_CODE_POINT");
        UnicodeProp.aliases.put("JOINCONTROL", "JOIN_CONTROL");
    }
}
