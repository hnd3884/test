package org.msgpack.template.builder;

public class TemplateBuildException extends RuntimeException
{
    public TemplateBuildException(final String reason) {
        super(reason);
    }
    
    public TemplateBuildException(final String reason, final Throwable t) {
        super(reason, t);
    }
    
    public TemplateBuildException(final Throwable t) {
        super(t);
    }
}
