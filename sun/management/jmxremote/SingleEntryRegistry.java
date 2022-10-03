package sun.management.jmxremote;

import sun.misc.ObjectInputFilter;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.Remote;
import sun.rmi.registry.RegistryImpl;

public class SingleEntryRegistry extends RegistryImpl
{
    private final String name;
    private final Remote object;
    private static final long serialVersionUID = -4897238949499730950L;
    
    SingleEntryRegistry(final int n, final String name, final Remote object) throws RemoteException {
        super(n, null, null, SingleEntryRegistry::singleRegistryFilter);
        this.name = name;
        this.object = object;
    }
    
    SingleEntryRegistry(final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory, final String name, final Remote object) throws RemoteException {
        super(n, rmiClientSocketFactory, rmiServerSocketFactory, SingleEntryRegistry::singleRegistryFilter);
        this.name = name;
        this.object = object;
    }
    
    @Override
    public String[] list() {
        return new String[] { this.name };
    }
    
    @Override
    public Remote lookup(final String s) throws NotBoundException {
        if (s.equals(this.name)) {
            return this.object;
        }
        throw new NotBoundException("Not bound: \"" + s + "\" (only bound name is \"" + this.name + "\")");
    }
    
    @Override
    public void bind(final String s, final Remote remote) throws AccessException {
        throw new AccessException("Cannot modify this registry");
    }
    
    @Override
    public void rebind(final String s, final Remote remote) throws AccessException {
        throw new AccessException("Cannot modify this registry");
    }
    
    @Override
    public void unbind(final String s) throws AccessException {
        throw new AccessException("Cannot modify this registry");
    }
    
    private static ObjectInputFilter.Status singleRegistryFilter(final ObjectInputFilter.FilterInfo filterInfo) {
        return (filterInfo.serialClass() != null || filterInfo.depth() > 2L || filterInfo.references() > 4L || filterInfo.arrayLength() >= 0L) ? ObjectInputFilter.Status.REJECTED : ObjectInputFilter.Status.ALLOWED;
    }
}
