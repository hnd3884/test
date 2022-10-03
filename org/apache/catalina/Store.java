package org.apache.catalina;

import java.beans.PropertyChangeListener;
import java.io.IOException;

public interface Store
{
    Manager getManager();
    
    void setManager(final Manager p0);
    
    int getSize() throws IOException;
    
    void addPropertyChangeListener(final PropertyChangeListener p0);
    
    String[] keys() throws IOException;
    
    Session load(final String p0) throws ClassNotFoundException, IOException;
    
    void remove(final String p0) throws IOException;
    
    void clear() throws IOException;
    
    void removePropertyChangeListener(final PropertyChangeListener p0);
    
    void save(final Session p0) throws IOException;
}
