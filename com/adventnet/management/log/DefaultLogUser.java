package com.adventnet.management.log;

public class DefaultLogUser extends LogUser
{
    private boolean enabled;
    
    public DefaultLogUser(final String s, final int n, final LogBaseWriter logBaseWriter) {
        super(null, 3, null);
        this.enabled = true;
    }
    
    public void fail(final String s, final Throwable t) {
        if (!this.enabled) {
            return;
        }
        System.err.println(s);
        if (t != null) {
            t.printStackTrace();
        }
    }
    
    public void setSuppressModuleNameLogging(final boolean b) {
    }
    
    public boolean isSuppressModuleNameLogging() {
        return false;
    }
    
    public void log(final String s, final int n) {
        if (!this.enabled) {
            return;
        }
        System.out.println(s);
    }
    
    public void logException(final Throwable t, final int n) {
        if (!this.enabled) {
            return;
        }
        if (t != null) {
            t.printStackTrace();
        }
    }
    
    public void logStackTrace(final int n) {
    }
    
    public void flush() {
    }
    
    public void setLevel(final int n) {
    }
    
    public void setStatus(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public int getLevel() {
        return 3;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
}
