package org.apache.lucene.index;

class AbortingException extends Exception
{
    private AbortingException(final Throwable cause) {
        super(cause);
        assert !(cause instanceof AbortingException);
    }
    
    public static AbortingException wrap(final Throwable t) {
        if (t instanceof AbortingException) {
            return (AbortingException)t;
        }
        return new AbortingException(t);
    }
}
