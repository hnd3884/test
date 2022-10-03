package java.beans.beancontext;

import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.beans.Visibility;
import java.beans.DesignMode;
import java.util.Collection;

public interface BeanContext extends BeanContextChild, Collection, DesignMode, Visibility
{
    public static final Object globalHierarchyLock = new Object();
    
    Object instantiateChild(final String p0) throws IOException, ClassNotFoundException;
    
    InputStream getResourceAsStream(final String p0, final BeanContextChild p1) throws IllegalArgumentException;
    
    URL getResource(final String p0, final BeanContextChild p1) throws IllegalArgumentException;
    
    void addBeanContextMembershipListener(final BeanContextMembershipListener p0);
    
    void removeBeanContextMembershipListener(final BeanContextMembershipListener p0);
}
