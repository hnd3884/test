package org.apache.catalina;

import java.util.Iterator;
import javax.servlet.http.HttpSession;
import java.security.Principal;

public interface Session
{
    public static final String SESSION_CREATED_EVENT = "createSession";
    public static final String SESSION_DESTROYED_EVENT = "destroySession";
    public static final String SESSION_ACTIVATED_EVENT = "activateSession";
    public static final String SESSION_PASSIVATED_EVENT = "passivateSession";
    
    String getAuthType();
    
    void setAuthType(final String p0);
    
    long getCreationTime();
    
    long getCreationTimeInternal();
    
    void setCreationTime(final long p0);
    
    String getId();
    
    String getIdInternal();
    
    void setId(final String p0);
    
    void setId(final String p0, final boolean p1);
    
    long getThisAccessedTime();
    
    long getThisAccessedTimeInternal();
    
    long getLastAccessedTime();
    
    long getLastAccessedTimeInternal();
    
    long getIdleTime();
    
    long getIdleTimeInternal();
    
    Manager getManager();
    
    void setManager(final Manager p0);
    
    int getMaxInactiveInterval();
    
    void setMaxInactiveInterval(final int p0);
    
    void setNew(final boolean p0);
    
    Principal getPrincipal();
    
    void setPrincipal(final Principal p0);
    
    HttpSession getSession();
    
    void setValid(final boolean p0);
    
    boolean isValid();
    
    void access();
    
    void addSessionListener(final SessionListener p0);
    
    void endAccess();
    
    void expire();
    
    Object getNote(final String p0);
    
    Iterator<String> getNoteNames();
    
    void recycle();
    
    void removeNote(final String p0);
    
    void removeSessionListener(final SessionListener p0);
    
    void setNote(final String p0, final Object p1);
    
    void tellChangedSessionId(final String p0, final String p1, final boolean p2, final boolean p3);
    
    boolean isAttributeDistributable(final String p0, final Object p1);
}
