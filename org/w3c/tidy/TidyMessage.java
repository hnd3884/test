package org.w3c.tidy;

public final class TidyMessage
{
    private int line;
    private int column;
    private Level level;
    private String message;
    private int errorCode;
    
    public TidyMessage(final int errorCode, final int line, final int column, final Level level, final String message) {
        this.errorCode = errorCode;
        this.line = line;
        this.column = column;
        this.level = level;
        this.message = message;
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    public int getColumn() {
        return this.column;
    }
    
    public Level getLevel() {
        return this.level;
    }
    
    public int getLine() {
        return this.line;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public static final class Level implements Comparable
    {
        public static final Level SUMMARY;
        public static final Level INFO;
        public static final Level WARNING;
        public static final Level ERROR;
        private short code;
        
        private Level(final int n) {
            this.code = (short)n;
        }
        
        public short getCode() {
            return this.code;
        }
        
        public static Level fromCode(final int n) {
            switch (n) {
                case 0: {
                    return Level.SUMMARY;
                }
                case 1: {
                    return Level.INFO;
                }
                case 2: {
                    return Level.WARNING;
                }
                case 3: {
                    return Level.ERROR;
                }
                default: {
                    return null;
                }
            }
        }
        
        public int compareTo(final Object o) {
            return this.code - ((Level)o).code;
        }
        
        public boolean equals(final Object o) {
            return o instanceof Level && this.code == ((Level)o).code;
        }
        
        public String toString() {
            switch (this.code) {
                case 0: {
                    return "SUMMARY";
                }
                case 1: {
                    return "INFO";
                }
                case 2: {
                    return "WARNING";
                }
                case 3: {
                    return "ERROR";
                }
                default: {
                    return "?";
                }
            }
        }
        
        public int hashCode() {
            return super.hashCode();
        }
        
        static {
            SUMMARY = new Level(0);
            INFO = new Level(1);
            WARNING = new Level(2);
            ERROR = new Level(3);
        }
    }
}
