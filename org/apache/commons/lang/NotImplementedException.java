package org.apache.commons.lang;

public class NotImplementedException extends UnsupportedOperationException
{
    public NotImplementedException(final Class clazz) {
        super("Method is not implemented in class " + ((clazz == null) ? null : clazz.getName()));
    }
    
    public NotImplementedException(final String msg) {
        super(msg);
    }
}
