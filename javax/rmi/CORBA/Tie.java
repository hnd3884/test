package javax.rmi.CORBA;

import java.rmi.Remote;
import org.omg.CORBA.ORB;
import java.rmi.NoSuchObjectException;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.InvokeHandler;

public interface Tie extends InvokeHandler
{
    org.omg.CORBA.Object thisObject();
    
    void deactivate() throws NoSuchObjectException;
    
    ORB orb();
    
    void orb(final ORB p0);
    
    void setTarget(final Remote p0);
    
    Remote getTarget();
}
