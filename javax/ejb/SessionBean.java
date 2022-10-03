package javax.ejb;

import java.rmi.RemoteException;

public interface SessionBean extends EnterpriseBean
{
    void ejbActivate() throws EJBException, RemoteException;
    
    void ejbPassivate() throws EJBException, RemoteException;
    
    void ejbRemove() throws EJBException, RemoteException;
    
    void setSessionContext(final SessionContext p0) throws EJBException, RemoteException;
}
