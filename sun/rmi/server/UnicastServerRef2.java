package sun.rmi.server;

import java.rmi.server.RemoteRef;
import java.io.ObjectOutput;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import sun.misc.ObjectInputFilter;
import sun.rmi.transport.LiveRef;

public class UnicastServerRef2 extends UnicastServerRef
{
    private static final long serialVersionUID = -2289703812660767614L;
    
    public UnicastServerRef2() {
    }
    
    public UnicastServerRef2(final LiveRef liveRef) {
        super(liveRef);
    }
    
    public UnicastServerRef2(final LiveRef liveRef, final ObjectInputFilter objectInputFilter) {
        super(liveRef, objectInputFilter);
    }
    
    public UnicastServerRef2(final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory) {
        super(new LiveRef(n, rmiClientSocketFactory, rmiServerSocketFactory));
    }
    
    public UnicastServerRef2(final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory, final ObjectInputFilter objectInputFilter) {
        super(new LiveRef(n, rmiClientSocketFactory, rmiServerSocketFactory), objectInputFilter);
    }
    
    @Override
    public String getRefClass(final ObjectOutput objectOutput) {
        return "UnicastServerRef2";
    }
    
    @Override
    protected RemoteRef getClientRef() {
        return new UnicastRef2(this.ref);
    }
}
