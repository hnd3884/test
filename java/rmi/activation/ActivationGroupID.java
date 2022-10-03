package java.rmi.activation;

import java.rmi.server.UID;
import java.io.Serializable;

public class ActivationGroupID implements Serializable
{
    private ActivationSystem system;
    private UID uid;
    private static final long serialVersionUID = -1648432278909740833L;
    
    public ActivationGroupID(final ActivationSystem system) {
        this.uid = new UID();
        this.system = system;
    }
    
    public ActivationSystem getSystem() {
        return this.system;
    }
    
    @Override
    public int hashCode() {
        return this.uid.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ActivationGroupID) {
            final ActivationGroupID activationGroupID = (ActivationGroupID)o;
            return this.uid.equals(activationGroupID.uid) && this.system.equals(activationGroupID.system);
        }
        return false;
    }
}
