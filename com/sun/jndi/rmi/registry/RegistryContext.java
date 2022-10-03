package com.sun.jndi.rmi.registry;

import java.security.AccessController;
import javax.naming.spi.NamingManager;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.ServerException;
import javax.naming.CommunicationException;
import java.rmi.NoSuchObjectException;
import java.rmi.UnmarshalException;
import java.rmi.MarshalException;
import java.rmi.ConnectIOException;
import java.rmi.server.ExportException;
import java.rmi.server.SocketSecurityException;
import java.rmi.UnknownHostException;
import java.rmi.StubNotFoundException;
import javax.naming.NoPermissionException;
import java.rmi.AccessException;
import javax.naming.ServiceUnavailableException;
import java.rmi.ConnectException;
import javax.naming.RefAddr;
import javax.naming.StringRefAddr;
import javax.naming.ConfigurationException;
import javax.naming.OperationNotSupportedException;
import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import java.rmi.AlreadyBoundException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.InvalidNameException;
import javax.naming.CompositeName;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.Name;
import javax.naming.NamingException;
import java.rmi.server.RMIClientSocketFactory;
import javax.naming.Reference;
import javax.naming.NameParser;
import java.rmi.registry.Registry;
import java.util.Hashtable;
import javax.naming.Referenceable;
import javax.naming.Context;

public class RegistryContext implements Context, Referenceable
{
    private Hashtable<String, Object> environment;
    private Registry registry;
    private String host;
    private int port;
    private static final NameParser nameParser;
    private static final String SOCKET_FACTORY = "com.sun.jndi.rmi.factory.socket";
    static final boolean trustURLCodebase;
    Reference reference;
    public static final String SECURITY_MGR = "java.naming.rmi.security.manager";
    
    public RegistryContext(String substring, final int port, final Hashtable<?, ?> hashtable) throws NamingException {
        this.reference = null;
        this.environment = (Hashtable<String, Object>)((hashtable == null) ? new Hashtable<String, Object>(5) : hashtable);
        if (this.environment.get("java.naming.rmi.security.manager") != null) {
            installSecurityMgr();
        }
        if (substring != null && substring.charAt(0) == '[') {
            substring = substring.substring(1, substring.length() - 1);
        }
        this.registry = getRegistry(substring, port, this.environment.get("com.sun.jndi.rmi.factory.socket"));
        this.host = substring;
        this.port = port;
    }
    
    RegistryContext(final RegistryContext registryContext) {
        this.reference = null;
        this.environment = (Hashtable)registryContext.environment.clone();
        this.registry = registryContext.registry;
        this.host = registryContext.host;
        this.port = registryContext.port;
        this.reference = registryContext.reference;
    }
    
    @Override
    protected void finalize() {
        this.close();
    }
    
    @Override
    public Object lookup(final Name name) throws NamingException {
        if (name.isEmpty()) {
            return new RegistryContext(this);
        }
        Remote lookup;
        try {
            lookup = this.registry.lookup(name.get(0));
        }
        catch (final NotBoundException ex) {
            throw new NameNotFoundException(name.get(0));
        }
        catch (final RemoteException ex2) {
            throw (NamingException)wrapRemoteException(ex2).fillInStackTrace();
        }
        return this.decodeObject(lookup, name.getPrefix(1));
    }
    
    @Override
    public Object lookup(final String s) throws NamingException {
        return this.lookup(new CompositeName(s));
    }
    
    @Override
    public void bind(final Name name, final Object o) throws NamingException {
        if (name.isEmpty()) {
            throw new InvalidNameException("RegistryContext: Cannot bind empty name");
        }
        try {
            this.registry.bind(name.get(0), this.encodeObject(o, name.getPrefix(1)));
        }
        catch (final AlreadyBoundException rootCause) {
            final NameAlreadyBoundException ex = new NameAlreadyBoundException(name.get(0));
            ex.setRootCause(rootCause);
            throw ex;
        }
        catch (final RemoteException ex2) {
            throw (NamingException)wrapRemoteException(ex2).fillInStackTrace();
        }
    }
    
    @Override
    public void bind(final String s, final Object o) throws NamingException {
        this.bind(new CompositeName(s), o);
    }
    
    @Override
    public void rebind(final Name name, final Object o) throws NamingException {
        if (name.isEmpty()) {
            throw new InvalidNameException("RegistryContext: Cannot rebind empty name");
        }
        try {
            this.registry.rebind(name.get(0), this.encodeObject(o, name.getPrefix(1)));
        }
        catch (final RemoteException ex) {
            throw (NamingException)wrapRemoteException(ex).fillInStackTrace();
        }
    }
    
    @Override
    public void rebind(final String s, final Object o) throws NamingException {
        this.rebind(new CompositeName(s), o);
    }
    
    @Override
    public void unbind(final Name name) throws NamingException {
        if (name.isEmpty()) {
            throw new InvalidNameException("RegistryContext: Cannot unbind empty name");
        }
        try {
            this.registry.unbind(name.get(0));
        }
        catch (final NotBoundException ex) {}
        catch (final RemoteException ex2) {
            throw (NamingException)wrapRemoteException(ex2).fillInStackTrace();
        }
    }
    
    @Override
    public void unbind(final String s) throws NamingException {
        this.unbind(new CompositeName(s));
    }
    
    @Override
    public void rename(final Name name, final Name name2) throws NamingException {
        this.bind(name2, this.lookup(name));
        this.unbind(name);
    }
    
    @Override
    public void rename(final String s, final String s2) throws NamingException {
        this.rename(new CompositeName(s), new CompositeName(s2));
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final Name name) throws NamingException {
        if (!name.isEmpty()) {
            throw new InvalidNameException("RegistryContext: can only list \"\"");
        }
        try {
            return new NameClassPairEnumeration(this.registry.list());
        }
        catch (final RemoteException ex) {
            throw (NamingException)wrapRemoteException(ex).fillInStackTrace();
        }
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final String s) throws NamingException {
        return this.list(new CompositeName(s));
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final Name name) throws NamingException {
        if (!name.isEmpty()) {
            throw new InvalidNameException("RegistryContext: can only list \"\"");
        }
        try {
            return new BindingEnumeration(this, this.registry.list());
        }
        catch (final RemoteException ex) {
            throw (NamingException)wrapRemoteException(ex).fillInStackTrace();
        }
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final String s) throws NamingException {
        return this.listBindings(new CompositeName(s));
    }
    
    @Override
    public void destroySubcontext(final Name name) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public void destroySubcontext(final String s) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public Context createSubcontext(final Name name) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public Context createSubcontext(final String s) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public Object lookupLink(final Name name) throws NamingException {
        return this.lookup(name);
    }
    
    @Override
    public Object lookupLink(final String s) throws NamingException {
        return this.lookup(s);
    }
    
    @Override
    public NameParser getNameParser(final Name name) throws NamingException {
        return RegistryContext.nameParser;
    }
    
    @Override
    public NameParser getNameParser(final String s) throws NamingException {
        return RegistryContext.nameParser;
    }
    
    @Override
    public Name composeName(final Name name, final Name name2) throws NamingException {
        return ((Name)name2.clone()).addAll(name);
    }
    
    @Override
    public String composeName(final String s, final String s2) throws NamingException {
        return this.composeName(new CompositeName(s), new CompositeName(s2)).toString();
    }
    
    @Override
    public Object removeFromEnvironment(final String s) throws NamingException {
        return this.environment.remove(s);
    }
    
    @Override
    public Object addToEnvironment(final String s, final Object o) throws NamingException {
        if (s.equals("java.naming.rmi.security.manager")) {
            installSecurityMgr();
        }
        return this.environment.put(s, o);
    }
    
    @Override
    public Hashtable<String, Object> getEnvironment() throws NamingException {
        return (Hashtable)this.environment.clone();
    }
    
    @Override
    public void close() {
        this.environment = null;
        this.registry = null;
    }
    
    @Override
    public String getNameInNamespace() {
        return "";
    }
    
    @Override
    public Reference getReference() throws NamingException {
        if (this.reference != null) {
            return (Reference)this.reference.clone();
        }
        if (this.host == null || this.host.equals("localhost")) {
            throw new ConfigurationException("Cannot create a reference for an RMI registry whose host was unspecified or specified as \"localhost\"");
        }
        final String s = "rmi://";
        String string = (this.host.indexOf(":") > -1) ? (s + "[" + this.host + "]") : (s + this.host);
        if (this.port > 0) {
            string = string + ":" + Integer.toString(this.port);
        }
        return new Reference(RegistryContext.class.getName(), new StringRefAddr("URL", string), RegistryContextFactory.class.getName(), null);
    }
    
    public static NamingException wrapRemoteException(final RemoteException rootCause) {
        NamingException wrapRemoteException;
        if (rootCause instanceof ConnectException) {
            wrapRemoteException = new ServiceUnavailableException();
        }
        else if (rootCause instanceof AccessException) {
            wrapRemoteException = new NoPermissionException();
        }
        else if (rootCause instanceof StubNotFoundException || rootCause instanceof UnknownHostException || rootCause instanceof SocketSecurityException) {
            wrapRemoteException = new ConfigurationException();
        }
        else if (rootCause instanceof ExportException || rootCause instanceof ConnectIOException || rootCause instanceof MarshalException || rootCause instanceof UnmarshalException || rootCause instanceof NoSuchObjectException) {
            wrapRemoteException = new CommunicationException();
        }
        else if (rootCause instanceof ServerException && rootCause.detail instanceof RemoteException) {
            wrapRemoteException = wrapRemoteException((RemoteException)rootCause.detail);
        }
        else {
            wrapRemoteException = new NamingException();
        }
        wrapRemoteException.setRootCause(rootCause);
        return wrapRemoteException;
    }
    
    private static Registry getRegistry(final String s, final int n, final RMIClientSocketFactory rmiClientSocketFactory) throws NamingException {
        try {
            if (rmiClientSocketFactory == null) {
                return LocateRegistry.getRegistry(s, n);
            }
            return LocateRegistry.getRegistry(s, n, rmiClientSocketFactory);
        }
        catch (final RemoteException ex) {
            throw (NamingException)wrapRemoteException(ex).fillInStackTrace();
        }
    }
    
    private static void installSecurityMgr() {
        try {
            System.setSecurityManager(new RMISecurityManager());
        }
        catch (final Exception ex) {}
    }
    
    private Remote encodeObject(Object stateToBind, final Name name) throws NamingException, RemoteException {
        stateToBind = NamingManager.getStateToBind(stateToBind, name, this, this.environment);
        if (stateToBind instanceof Remote) {
            return (Remote)stateToBind;
        }
        if (stateToBind instanceof Reference) {
            return new ReferenceWrapper((Reference)stateToBind);
        }
        if (stateToBind instanceof Referenceable) {
            return new ReferenceWrapper(((Referenceable)stateToBind).getReference());
        }
        throw new IllegalArgumentException("RegistryContext: object to bind must be Remote, Reference, or Referenceable");
    }
    
    private Object decodeObject(final Remote remote, final Name name) throws NamingException {
        try {
            final Object o = (remote instanceof RemoteReference) ? ((RemoteReference)remote).getReference() : remote;
            Reference reference = null;
            if (o instanceof Reference) {
                reference = (Reference)o;
            }
            else if (o instanceof Referenceable) {
                reference = ((Referenceable)o).getReference();
            }
            if (reference != null && reference.getFactoryClassLocation() != null && !RegistryContext.trustURLCodebase) {
                throw new ConfigurationException("The object factory is untrusted. Set the system property 'com.sun.jndi.rmi.object.trustURLCodebase' to 'true'.");
            }
            return NamingManager.getObjectInstance(o, name, this, this.environment);
        }
        catch (final NamingException ex) {
            throw ex;
        }
        catch (final RemoteException ex2) {
            throw (NamingException)wrapRemoteException(ex2).fillInStackTrace();
        }
        catch (final Exception rootCause) {
            final NamingException ex3 = new NamingException();
            ex3.setRootCause(rootCause);
            throw ex3;
        }
    }
    
    static {
        nameParser = new AtomicNameParser();
        trustURLCodebase = "true".equalsIgnoreCase(AccessController.doPrivileged(() -> System.getProperty("com.sun.jndi.rmi.object.trustURLCodebase", "false")));
    }
}
