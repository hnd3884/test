package javax.ejb;

import java.rmi.RemoteException;

public interface EntityBean extends EnterpriseBean
{
    void ejbActivate() throws EJBException, RemoteException;
    
    void ejbLoad() throws EJBException, RemoteException;
    
    void ejbPassivate() throws EJBException, RemoteException;
    
    void ejbRemove() throws RemoveException, EJBException, RemoteException;
    
    void ejbStore() throws EJBException, RemoteException;
    
    void setEntityContext(final EntityContext p0) throws EJBException, RemoteException;
    
    void unsetEntityContext() throws EJBException, RemoteException;
}
