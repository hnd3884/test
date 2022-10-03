package org.apache.catalina;

import java.io.IOException;
import java.beans.PropertyChangeListener;

public interface Manager
{
    Context getContext();
    
    void setContext(final Context p0);
    
    SessionIdGenerator getSessionIdGenerator();
    
    void setSessionIdGenerator(final SessionIdGenerator p0);
    
    long getSessionCounter();
    
    void setSessionCounter(final long p0);
    
    int getMaxActive();
    
    void setMaxActive(final int p0);
    
    int getActiveSessions();
    
    long getExpiredSessions();
    
    void setExpiredSessions(final long p0);
    
    int getRejectedSessions();
    
    int getSessionMaxAliveTime();
    
    void setSessionMaxAliveTime(final int p0);
    
    int getSessionAverageAliveTime();
    
    int getSessionCreateRate();
    
    int getSessionExpireRate();
    
    void add(final Session p0);
    
    void addPropertyChangeListener(final PropertyChangeListener p0);
    
    void changeSessionId(final Session p0);
    
    void changeSessionId(final Session p0, final String p1);
    
    Session createEmptySession();
    
    Session createSession(final String p0);
    
    Session findSession(final String p0) throws IOException;
    
    Session[] findSessions();
    
    void load() throws ClassNotFoundException, IOException;
    
    void remove(final Session p0);
    
    void remove(final Session p0, final boolean p1);
    
    void removePropertyChangeListener(final PropertyChangeListener p0);
    
    void unload() throws IOException;
    
    void backgroundProcess();
    
    boolean willAttributeDistribute(final String p0, final Object p1);
}
