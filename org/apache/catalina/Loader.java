package org.apache.catalina;

import java.beans.PropertyChangeListener;

public interface Loader
{
    void backgroundProcess();
    
    ClassLoader getClassLoader();
    
    Context getContext();
    
    void setContext(final Context p0);
    
    boolean getDelegate();
    
    void setDelegate(final boolean p0);
    
    @Deprecated
    boolean getReloadable();
    
    @Deprecated
    void setReloadable(final boolean p0);
    
    void addPropertyChangeListener(final PropertyChangeListener p0);
    
    boolean modified();
    
    void removePropertyChangeListener(final PropertyChangeListener p0);
}
