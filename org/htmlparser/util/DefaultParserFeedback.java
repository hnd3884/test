package org.htmlparser.util;

import java.io.Serializable;

public class DefaultParserFeedback implements ParserFeedback, Serializable
{
    public static final int QUIET = 0;
    public static final int NORMAL = 1;
    public static final int DEBUG = 2;
    protected int mMode;
    
    public DefaultParserFeedback(final int mode) {
        if (mode < 0 || mode > 2) {
            throw new IllegalArgumentException("illegal mode (" + mode + "), must be one of: QUIET, NORMAL, DEBUG");
        }
        this.mMode = mode;
    }
    
    public DefaultParserFeedback() {
        this(1);
    }
    
    public void info(final String message) {
        if (0 != this.mMode) {
            System.out.println("INFO: " + message);
        }
    }
    
    public void warning(final String message) {
        if (0 != this.mMode) {
            System.out.println("WARNING: " + message);
        }
    }
    
    public void error(final String message, final ParserException exception) {
        if (0 != this.mMode) {
            System.out.println("ERROR: " + message);
            if (2 == this.mMode && null != exception) {
                exception.printStackTrace();
            }
        }
    }
}
