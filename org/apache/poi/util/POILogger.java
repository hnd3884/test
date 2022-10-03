package org.apache.poi.util;

@Internal
public interface POILogger
{
    public static final int DEBUG = 1;
    public static final int INFO = 3;
    public static final int WARN = 5;
    public static final int ERROR = 7;
    public static final int FATAL = 9;
    
    void initialize(final String p0);
    
    @Internal
    void _log(final int p0, final Object p1);
    
    @Internal
    void _log(final int p0, final Object p1, final Throwable p2);
    
    boolean check(final int p0);
    
    default void log(final int level, final Object... objs) {
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
}
