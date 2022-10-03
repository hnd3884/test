package sun.rmi.server;

import java.io.IOException;
import java.io.NotSerializableException;
import java.rmi.server.RemoteRef;
import java.io.ObjectOutput;
import sun.rmi.transport.LiveRef;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.activation.ActivationID;

public class ActivatableServerRef extends UnicastServerRef2
{
    private static final long serialVersionUID = 2002967993223003793L;
    private ActivationID id;
    
    public ActivatableServerRef(final ActivationID activationID, final int n) {
        this(activationID, n, null, null);
    }
    
    public ActivatableServerRef(final ActivationID id, final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory) {
        super(new LiveRef(n, rmiClientSocketFactory, rmiServerSocketFactory));
        this.id = id;
    }
    
    @Override
    public String getRefClass(final ObjectOutput objectOutput) {
        return "ActivatableServerRef";
    }
    
    @Override
    protected RemoteRef getClientRef() {
        return new ActivatableRef(this.id, new UnicastRef2(this.ref));
    }
    
    @Override
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        throw new NotSerializableException("ActivatableServerRef not serializable");
    }
}
