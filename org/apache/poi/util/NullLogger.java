package org.apache.poi.util;

@Internal
public class NullLogger implements POILogger
{
    @Override
    public void initialize(final String cat) {
    }
    
    @Override
    public void _log(final int level, final Object obj1) {
    }
    
    @Override
    public void _log(final int level, final Object obj1, final Throwable exception) {
    }
    
    @Override
    public void log(final int level, final Object... objs) {
    }
    
    @Override
    public boolean check(final int level) {
        return false;
    }
}
