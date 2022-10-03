package org.antlr.v4.runtime;

public class ConsoleErrorListener extends BaseErrorListener
{
    public static final ConsoleErrorListener INSTANCE;
    
    @Override
    public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line, final int charPositionInLine, final String msg, final RecognitionException e) {
        System.err.println("line " + line + ":" + charPositionInLine + " " + msg);
    }
    
    static {
        INSTANCE = new ConsoleErrorListener();
    }
}
