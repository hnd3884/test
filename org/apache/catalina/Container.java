package org.apache.catalina;

import java.io.File;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.Request;
import java.beans.PropertyChangeListener;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;

public interface Container extends Lifecycle
{
    public static final String ADD_CHILD_EVENT = "addChild";
    public static final String ADD_VALVE_EVENT = "addValve";
    public static final String REMOVE_CHILD_EVENT = "removeChild";
    public static final String REMOVE_VALVE_EVENT = "removeValve";
    
    Log getLogger();
    
    String getLogName();
    
    ObjectName getObjectName();
    
    String getDomain();
    
    String getMBeanKeyProperties();
    
    Pipeline getPipeline();
    
    Cluster getCluster();
    
    void setCluster(final Cluster p0);
    
    int getBackgroundProcessorDelay();
    
    void setBackgroundProcessorDelay(final int p0);
    
    String getName();
    
    void setName(final String p0);
    
    Container getParent();
    
    void setParent(final Container p0);
    
    ClassLoader getParentClassLoader();
    
    void setParentClassLoader(final ClassLoader p0);
    
    Realm getRealm();
    
    void setRealm(final Realm p0);
    
    void backgroundProcess();
    
    void addChild(final Container p0);
    
    void addContainerListener(final ContainerListener p0);
    
    void addPropertyChangeListener(final PropertyChangeListener p0);
    
    Container findChild(final String p0);
    
    Container[] findChildren();
    
    ContainerListener[] findContainerListeners();
    
    void removeChild(final Container p0);
    
    void removeContainerListener(final ContainerListener p0);
    
    void removePropertyChangeListener(final PropertyChangeListener p0);
    
    void fireContainerEvent(final String p0, final Object p1);
    
    void logAccess(final Request p0, final Response p1, final long p2, final boolean p3);
    
    AccessLog getAccessLog();
    
    int getStartStopThreads();
    
    void setStartStopThreads(final int p0);
    
    File getCatalinaBase();
    
    File getCatalinaHome();
}
