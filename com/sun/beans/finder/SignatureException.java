package com.sun.beans.finder;

final class SignatureException extends RuntimeException
{
    SignatureException(final Throwable t) {
        super(t);
    }
    
    NoSuchMethodException toNoSuchMethodException(final String s) {
        final Throwable cause = this.getCause();
        if (cause instanceof NoSuchMethodException) {
            return (NoSuchMethodException)cause;
        }
        final NoSuchMethodException ex = new NoSuchMethodException(s);
        ex.initCause(cause);
        return ex;
    }
}
