package org.postgresql.shaded.com.ongres.scram.common.exception;

public class ScramParseException extends ScramException
{
    public ScramParseException(final String detail) {
        super(detail);
    }
    
    public ScramParseException(final String detail, final Throwable ex) {
        super(detail, ex);
    }
}
