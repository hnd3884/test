package java.rmi.dgc;

import java.rmi.RemoteException;
import java.rmi.server.ObjID;
import java.rmi.Remote;

public interface DGC extends Remote
{
    Lease dirty(final ObjID[] p0, final long p1, final Lease p2) throws RemoteException;
    
    void clean(final ObjID[] p0, final long p1, final VMID p2, final boolean p3) throws RemoteException;
}
