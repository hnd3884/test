package com.adventnet.management.log;

public class LogUser
{
    private String m_name;
    public int m_level;
    private boolean m_is_enabled;
    private LogBaseWriter m_log_writer;
    private boolean m_suppressModuleName;
    
    public LogUser(final String name, final int level, final LogBaseWriter log_writer) {
        this.m_name = null;
        this.m_level = 0;
        this.m_is_enabled = true;
        this.m_log_writer = null;
        this.m_suppressModuleName = false;
        this.m_name = name;
        this.m_log_writer = log_writer;
        this.m_level = level;
    }
    
    public void setSuppressModuleNameLogging(final boolean suppressModuleName) {
        this.m_suppressModuleName = suppressModuleName;
    }
    
    public boolean isSuppressModuleNameLogging() {
        return this.m_suppressModuleName;
    }
    
    public void log(final String s, final int n) {
        if (!this.m_is_enabled || n > this.m_level) {
            return;
        }
        if (this.m_suppressModuleName) {
            this.m_log_writer.log(s);
        }
        else {
            this.m_log_writer.log(this.m_name + ": " + s);
        }
    }
    
    public void logException(final Throwable t, final int n) {
        if (!this.m_is_enabled || n > this.m_level) {
            return;
        }
        this.m_log_writer.logException(t);
    }
    
    public void logStackTrace(final int n) {
        if (!this.m_is_enabled || n > this.m_level) {
            return;
        }
        this.m_log_writer.logStackTrace();
    }
    
    public void fail(final String s, final Throwable t) {
        String string;
        if (s != null) {
            string = s + ((t == null) ? "" : " at:");
        }
        else {
            string = "RUNTIME ERROR at:";
        }
        this.log(string, 1);
        if (t != null) {
            this.logException(t, 1);
        }
    }
    
    public void flush() {
        this.m_log_writer.flush();
    }
    
    public void setLevel(final int level) {
        this.m_level = level;
    }
    
    public void setStatus(final boolean is_enabled) {
        this.m_is_enabled = is_enabled;
    }
    
    public int getLevel() {
        return this.m_level;
    }
    
    public boolean isEnabled() {
        return this.m_is_enabled;
    }
    
    public void abort(final String s, final Throwable t) {
        this.fail(s, t);
    }
    
    public void assert(final boolean b, final String s) {
        if (!b) {
            this.fail(s, null);
        }
    }
    
    public void setDisplayName(final String name) {
        this.m_name = name;
    }
}
