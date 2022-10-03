package org.apache.tika.exception;

import org.xml.sax.SAXException;

public class WriteLimitReachedException extends SAXException
{
    private static final int MAX_DEPTH = 100;
    private final int writeLimit;
    
    public WriteLimitReachedException(final int writeLimit) {
        this.writeLimit = writeLimit;
    }
    
    @Override
    public String getMessage() {
        return "Your document contained more than " + this.writeLimit + " characters, and so your requested limit has been reached. To receive the full text of the document, increase your limit. (Text up to the limit is however available).";
    }
    
    public static boolean isWriteLimitReached(final Throwable t) {
        return isWriteLimitReached(t, 0);
    }
    
    private static boolean isWriteLimitReached(final Throwable t, final int depth) {
        return t != null && depth <= 100 && (t instanceof WriteLimitReachedException || (t.getCause() != null && isWriteLimitReached(t.getCause(), depth + 1)));
    }
    
    public static void throwIfWriteLimitReached(final Exception ex) throws SAXException {
        throwIfWriteLimitReached(ex, 0);
    }
    
    private static void throwIfWriteLimitReached(final Exception ex, final int depth) throws SAXException {
        if (ex == null) {
            return;
        }
        if (depth > 100) {
            return;
        }
        if (ex instanceof WriteLimitReachedException) {
            throw (SAXException)ex;
        }
        isWriteLimitReached(ex.getCause(), depth + 1);
    }
}
