package org.htmlparser.util;

public class FeedbackManager
{
    protected static ParserFeedback callback;
    
    public static void setParserFeedback(final ParserFeedback feedback) {
        FeedbackManager.callback = feedback;
    }
    
    public static void info(final String message) {
        FeedbackManager.callback.info(message);
    }
    
    public static void warning(final String message) {
        FeedbackManager.callback.warning(message);
    }
    
    public static void error(final String message, final ParserException e) {
        FeedbackManager.callback.error(message, e);
    }
    
    static {
        FeedbackManager.callback = new DefaultParserFeedback();
    }
}
