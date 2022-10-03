package javax.ejb;

public interface SessionContext extends EJBContext
{
    EJBLocalObject getEJBLocalObject() throws IllegalStateException;
    
    EJBObject getEJBObject() throws IllegalStateException;
}
