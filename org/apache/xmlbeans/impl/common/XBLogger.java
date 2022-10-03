package org.apache.xmlbeans.impl.common;

public abstract class XBLogger
{
    public static final int DEBUG = 1;
    public static final int INFO = 3;
    public static final int WARN = 5;
    public static final int ERROR = 7;
    public static final int FATAL = 9;
    protected static final String[] LEVEL_STRINGS_SHORT;
    protected static final String[] LEVEL_STRINGS;
    
    XBLogger() {
    }
    
    public abstract void initialize(final String p0);
    
    protected abstract void _log(final int p0, final Object p1);
    
    protected abstract void _log(final int p0, final Object p1, final Throwable p2);
    
    public abstract boolean check(final int p0);
    
    public void log(final int level, final Object... objs) {
        if (!this.check(level)) {
            return;
        }
        final StringBuilder sb = new StringBuilder(32);
        Throwable lastEx = null;
        for (int i = 0; i < objs.length; ++i) {
            if (i == objs.length - 1 && objs[i] instanceof Throwable) {
                lastEx = (Throwable)objs[i];
            }
            else {
                sb.append(objs[i]);
            }
        }
        String msg = sb.toString();
        msg = msg.replaceAll("[\r\n]+", " ");
        if (lastEx == null) {
            this._log(level, msg);
        }
        else {
            this._log(level, msg, lastEx);
        }
    }
    
    static {
        LEVEL_STRINGS_SHORT = new String[] { "?", "D", "?", "I", "?", "W", "?", "E", "?", "F", "?" };
        LEVEL_STRINGS = new String[] { "?0?", "DEBUG", "?2?", "INFO", "?4?", "WARN", "?6?", "ERROR", "?8?", "FATAL", "?10+?" };
    }
}
