package javax.ejb;

import java.rmi.RemoteException;
import java.io.Serializable;

public interface HomeHandle extends Serializable
{
    EJBHome getEJBHome() throws RemoteException;
}
