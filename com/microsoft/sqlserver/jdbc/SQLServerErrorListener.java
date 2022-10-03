package com.microsoft.sqlserver.jdbc;

import java.util.logging.Level;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import java.util.logging.Logger;
import org.antlr.v4.runtime.BaseErrorListener;

class SQLServerErrorListener extends BaseErrorListener
{
    private static final Logger logger;
    
    public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line, final int charPositionInLine, final String msg, final RecognitionException e) {
        if (SQLServerErrorListener.logger.isLoggable(Level.FINE)) {
            SQLServerErrorListener.logger.fine("Error occured during token parsing: " + msg);
            SQLServerErrorListener.logger.fine("line " + line + ":" + charPositionInLine + " token recognition error at: " + offendingSymbol.toString());
        }
    }
    
    static {
        logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerFMTQuery");
    }
}
