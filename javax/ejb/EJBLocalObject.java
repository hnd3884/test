package javax.ejb;

public interface EJBLocalObject
{
    EJBLocalHome getEJBLocalHome() throws EJBException;
    
    Object getPrimaryKey() throws EJBException;
    
    void remove() throws RemoveException, EJBException;
    
    boolean isIdentical(final EJBLocalObject p0) throws EJBException;
}
