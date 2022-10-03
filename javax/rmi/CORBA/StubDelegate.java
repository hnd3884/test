package javax.rmi.CORBA;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import org.omg.CORBA.ORB;

public interface StubDelegate
{
    int hashCode(final Stub p0);
    
    boolean equals(final Stub p0, final Object p1);
    
    String toString(final Stub p0);
    
    void connect(final Stub p0, final ORB p1) throws RemoteException;
    
    void readObject(final Stub p0, final ObjectInputStream p1) throws IOException, ClassNotFoundException;
    
    void writeObject(final Stub p0, final ObjectOutputStream p1) throws IOException;
}
