package jdk.jfr.internal.dcmd;

import java.util.Formatter;

final class DCmdException extends Exception
{
    private static final long serialVersionUID = -3792411099340016465L;
    
    public DCmdException(final String s, final Object... array) {
        super(format(s, array));
    }
    
    public DCmdException(final Throwable t, final String s, final Object... array) {
        super(format(s, array), t);
    }
    
    private static String format(final String s, final Object... array) {
        try (final Formatter formatter = new Formatter()) {
            return formatter.format(s, array).toString();
        }
    }
}
