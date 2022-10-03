package org.owasp.esapi;

public interface Logger
{
    public static final EventType SECURITY_SUCCESS = new EventType("SECURITY SUCCESS", true);
    public static final EventType SECURITY_FAILURE = new EventType("SECURITY FAILURE", false);
    public static final EventType SECURITY_AUDIT = new EventType("SECURITY AUDIT", null);
    public static final EventType EVENT_SUCCESS = new EventType("EVENT SUCCESS", true);
    public static final EventType EVENT_FAILURE = new EventType("EVENT FAILURE", false);
    public static final EventType EVENT_UNSPECIFIED = new EventType("EVENT UNSPECIFIED", null);
    public static final int OFF = Integer.MAX_VALUE;
    public static final int FATAL = 1000;
    public static final int ERROR = 800;
    public static final int WARNING = 600;
    public static final int INFO = 400;
    public static final int DEBUG = 200;
    public static final int TRACE = 100;
    public static final int ALL = Integer.MIN_VALUE;
    
    void setLevel(final int p0);
    
    int getESAPILevel();
    
    void fatal(final EventType p0, final String p1);
    
    void fatal(final EventType p0, final String p1, final Throwable p2);
    
    boolean isFatalEnabled();
    
    void error(final EventType p0, final String p1);
    
    void error(final EventType p0, final String p1, final Throwable p2);
    
    boolean isErrorEnabled();
    
    void warning(final EventType p0, final String p1);
    
    void warning(final EventType p0, final String p1, final Throwable p2);
    
    boolean isWarningEnabled();
    
    void info(final EventType p0, final String p1);
    
    void info(final EventType p0, final String p1, final Throwable p2);
    
    boolean isInfoEnabled();
    
    void debug(final EventType p0, final String p1);
    
    void debug(final EventType p0, final String p1, final Throwable p2);
    
    boolean isDebugEnabled();
    
    void trace(final EventType p0, final String p1);
    
    void trace(final EventType p0, final String p1, final Throwable p2);
    
    boolean isTraceEnabled();
    
    void always(final EventType p0, final String p1);
    
    void always(final EventType p0, final String p1, final Throwable p2);
    
    public static class EventType
    {
        private String type;
        private Boolean success;
        
        public EventType(final String name, final Boolean newSuccess) {
            this.success = null;
            this.type = name;
            this.success = newSuccess;
        }
        
        public Boolean isSuccess() {
            return this.success;
        }
        
        @Override
        public String toString() {
            return this.type;
        }
    }
}
