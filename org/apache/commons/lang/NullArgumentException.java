package org.apache.commons.lang;

public class NullArgumentException extends IllegalArgumentException
{
    public NullArgumentException(final String argName) {
        super(argName + " must not be null.");
    }
}
