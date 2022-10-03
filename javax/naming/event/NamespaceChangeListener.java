package javax.naming.event;

public interface NamespaceChangeListener extends NamingListener
{
    void objectAdded(final NamingEvent p0);
    
    void objectRemoved(final NamingEvent p0);
    
    void objectRenamed(final NamingEvent p0);
}
