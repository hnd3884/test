package org.apache.xmlbeans.impl.common;

public class NullLogger extends XBLogger
{
    @Override
    public void initialize(final String cat) {
    }
    
    @Override
    protected void _log(final int level, final Object obj1) {
    }
    
    @Override
    protected void _log(final int level, final Object obj1, final Throwable exception) {
    }
    
    @Override
    public void log(final int level, final Object... objs) {
    }
    
    @Override
    public boolean check(final int level) {
        return false;
    }
}
