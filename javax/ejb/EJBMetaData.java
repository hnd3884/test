package javax.ejb;

public interface EJBMetaData
{
    EJBHome getEJBHome();
    
    Class getHomeInterfaceClass();
    
    Class getPrimaryKeyClass();
    
    Class getRemoteInterfaceClass();
    
    boolean isSession();
    
    boolean isStatelessSession();
}
