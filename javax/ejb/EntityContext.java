package javax.ejb;

public interface EntityContext extends EJBContext
{
    EJBLocalObject getEJBLocalObject() throws IllegalStateException;
    
    EJBObject getEJBObject() throws IllegalStateException;
    
    Object getPrimaryKey() throws IllegalStateException;
}
