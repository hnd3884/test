package javax.ejb;

import java.rmi.RemoteException;

public interface SessionSynchronization
{
    void afterBegin() throws EJBException, RemoteException;
    
    void afterCompletion(final boolean p0) throws EJBException, RemoteException;
    
    void beforeCompletion() throws EJBException, RemoteException;
}
