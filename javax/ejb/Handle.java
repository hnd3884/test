package javax.ejb;

import java.rmi.RemoteException;
import java.io.Serializable;

public interface Handle extends Serializable
{
    EJBObject getEJBObject() throws RemoteException;
}
