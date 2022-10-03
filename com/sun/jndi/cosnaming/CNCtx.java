package com.sun.jndi.cosnaming;

import java.security.AccessController;
import javax.naming.NameParser;
import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import javax.naming.NameNotFoundException;
import javax.naming.Reference;
import javax.naming.InvalidNameException;
import javax.naming.NotContextException;
import javax.naming.CannotProceedException;
import javax.naming.spi.NamingManager;
import javax.naming.Name;
import javax.naming.CompositeName;
import org.omg.CORBA.SystemException;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.COMM_FAILURE;
import javax.naming.CommunicationException;
import org.omg.CORBA.ORBPackage.InvalidName;
import java.util.Iterator;
import java.net.MalformedURLException;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CosNaming.NamingContextHelper;
import javax.naming.ConfigurationException;
import javax.naming.spi.ResolveResult;
import javax.naming.NamingException;
import com.sun.jndi.toolkit.corba.CorbaUtils;
import java.util.Hashtable;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CORBA.ORB;
import javax.naming.Context;

public class CNCtx implements Context
{
    private static final boolean debug = false;
    private static ORB _defaultOrb;
    ORB _orb;
    public NamingContext _nc;
    private NameComponent[] _name;
    Hashtable<String, Object> _env;
    static final CNNameParser parser;
    private static final String FED_PROP = "com.sun.jndi.cosnaming.federation";
    boolean federation;
    public static final boolean trustURLCodebase;
    OrbReuseTracker orbTracker;
    int enumCount;
    boolean isCloseCalled;
    
    private static synchronized ORB getDefaultOrb() {
        if (CNCtx._defaultOrb == null) {
            CNCtx._defaultOrb = CorbaUtils.getOrb(null, -1, new Hashtable<Object, Object>());
        }
        return CNCtx._defaultOrb;
    }
    
    CNCtx(Hashtable<?, ?> env) throws NamingException {
        this._name = null;
        this.federation = false;
        this.orbTracker = null;
        this.isCloseCalled = false;
        if (env != null) {
            env = (Hashtable)env.clone();
        }
        this._env = env;
        this.federation = "true".equals((env != null) ? env.get("com.sun.jndi.cosnaming.federation") : null);
        this.initOrbAndRootContext(env);
    }
    
    private CNCtx() {
        this._name = null;
        this.federation = false;
        this.orbTracker = null;
        this.isCloseCalled = false;
    }
    
    public static ResolveResult createUsingURL(final String s, Hashtable<?, ?> env) throws NamingException {
        final CNCtx cnCtx = new CNCtx();
        if (env != null) {
            env = (Hashtable)env.clone();
        }
        cnCtx._env = env;
        return new ResolveResult(cnCtx, CNCtx.parser.parse(cnCtx.initUsingUrl((env != null) ? ((ORB)env.get("java.naming.corba.orb")) : null, s, env)));
    }
    
    CNCtx(final ORB orb, final OrbReuseTracker orbReuseTracker, final NamingContext nc, final Hashtable<String, Object> env, final NameComponent[] name) throws NamingException {
        this._name = null;
        this.federation = false;
        this.orbTracker = null;
        this.isCloseCalled = false;
        if (orb == null || nc == null) {
            throw new ConfigurationException("Must supply ORB or NamingContext");
        }
        if (orb != null) {
            this._orb = orb;
        }
        else {
            this._orb = getDefaultOrb();
        }
        this._nc = nc;
        this._env = env;
        this._name = name;
        this.federation = "true".equals((env != null) ? env.get("com.sun.jndi.cosnaming.federation") : null);
    }
    
    NameComponent[] makeFullName(final NameComponent[] array) {
        if (this._name == null || this._name.length == 0) {
            return array;
        }
        final NameComponent[] array2 = new NameComponent[this._name.length + array.length];
        System.arraycopy(this._name, 0, array2, 0, this._name.length);
        System.arraycopy(array, 0, array2, this._name.length, array.length);
        return array2;
    }
    
    @Override
    public String getNameInNamespace() throws NamingException {
        if (this._name == null || this._name.length == 0) {
            return "";
        }
        return CNNameParser.cosNameToInsString(this._name);
    }
    
    private static boolean isCorbaUrl(final String s) {
        return s.startsWith("iiop://") || s.startsWith("iiopname://") || s.startsWith("corbaname:");
    }
    
    private void initOrbAndRootContext(final Hashtable<?, ?> hashtable) throws NamingException {
        ORB defaultOrb = null;
        if (defaultOrb == null && hashtable != null) {
            defaultOrb = (ORB)hashtable.get("java.naming.corba.orb");
        }
        if (defaultOrb == null) {
            defaultOrb = getDefaultOrb();
        }
        String s = null;
        if (hashtable != null) {
            s = (String)hashtable.get("java.naming.provider.url");
        }
        if (s != null && !isCorbaUrl(s)) {
            this.setOrbAndRootContext(defaultOrb, this.getStringifiedIor(s));
        }
        else if (s != null) {
            final String initUsingUrl = this.initUsingUrl(defaultOrb, s, hashtable);
            if (initUsingUrl.length() > 0) {
                this._name = CNNameParser.nameToCosName(CNCtx.parser.parse(initUsingUrl));
                try {
                    this._nc = NamingContextHelper.narrow(this._nc.resolve(this._name));
                    if (this._nc == null) {
                        throw new ConfigurationException(initUsingUrl + " does not name a NamingContext");
                    }
                }
                catch (final BAD_PARAM bad_PARAM) {
                    throw new ConfigurationException(initUsingUrl + " does not name a NamingContext");
                }
                catch (final Exception ex) {
                    throw ExceptionMapper.mapException(ex, this, this._name);
                }
            }
        }
        else {
            this.setOrbAndRootContext(defaultOrb, (String)null);
        }
    }
    
    private String initUsingUrl(final ORB orb, final String s, final Hashtable<?, ?> hashtable) throws NamingException {
        if (s.startsWith("iiop://") || s.startsWith("iiopname://")) {
            return this.initUsingIiopUrl(orb, s, hashtable);
        }
        return this.initUsingCorbanameUrl(orb, s, hashtable);
    }
    
    private String initUsingIiopUrl(ORB defaultOrb, final String s, final Hashtable<?, ?> hashtable) throws NamingException {
        if (defaultOrb == null) {
            defaultOrb = getDefaultOrb();
        }
        try {
            final IiopUrl iiopUrl = new IiopUrl(s);
            NamingException ex = null;
            for (final IiopUrl.Address address : iiopUrl.getAddresses()) {
                try {
                    try {
                        this.setOrbAndRootContext(defaultOrb, defaultOrb.string_to_object("corbaloc:iiop:" + address.host + ":" + address.port + "/NameService"));
                        return iiopUrl.getStringName();
                    }
                    catch (final Exception ex2) {
                        this.setOrbAndRootContext(defaultOrb, (String)null);
                        return iiopUrl.getStringName();
                    }
                }
                catch (final NamingException ex3) {
                    ex = ex3;
                    continue;
                }
                break;
            }
            if (ex != null) {
                throw ex;
            }
            throw new ConfigurationException("Problem with URL: " + s);
        }
        catch (final MalformedURLException ex4) {
            throw new ConfigurationException(ex4.getMessage());
        }
    }
    
    private String initUsingCorbanameUrl(ORB defaultOrb, final String s, final Hashtable<?, ?> hashtable) throws NamingException {
        if (defaultOrb == null) {
            defaultOrb = getDefaultOrb();
        }
        try {
            final CorbanameUrl corbanameUrl = new CorbanameUrl(s);
            final String location = corbanameUrl.getLocation();
            corbanameUrl.getStringName();
            this.setOrbAndRootContext(defaultOrb, location);
            return corbanameUrl.getStringName();
        }
        catch (final MalformedURLException ex) {
            throw new ConfigurationException(ex.getMessage());
        }
    }
    
    private void setOrbAndRootContext(final ORB orb, final String s) throws NamingException {
        this._orb = orb;
        try {
            org.omg.CORBA.Object object;
            if (s != null) {
                object = this._orb.string_to_object(s);
            }
            else {
                object = this._orb.resolve_initial_references("NameService");
            }
            this._nc = NamingContextHelper.narrow(object);
            if (this._nc == null) {
                if (s != null) {
                    throw new ConfigurationException("Cannot convert IOR to a NamingContext: " + s);
                }
                throw new ConfigurationException("ORB.resolve_initial_references(\"NameService\") does not return a NamingContext");
            }
        }
        catch (final InvalidName rootCause) {
            final ConfigurationException ex = new ConfigurationException("COS Name Service not registered with ORB under the name 'NameService'");
            ex.setRootCause(rootCause);
            throw ex;
        }
        catch (final COMM_FAILURE rootCause2) {
            final CommunicationException ex2 = new CommunicationException("Cannot connect to ORB");
            ex2.setRootCause(rootCause2);
            throw ex2;
        }
        catch (final BAD_PARAM rootCause3) {
            final ConfigurationException ex3 = new ConfigurationException("Invalid URL or IOR: " + s);
            ex3.setRootCause(rootCause3);
            throw ex3;
        }
        catch (final INV_OBJREF rootCause4) {
            final ConfigurationException ex4 = new ConfigurationException("Invalid object reference: " + s);
            ex4.setRootCause(rootCause4);
            throw ex4;
        }
    }
    
    private void setOrbAndRootContext(final ORB orb, final org.omg.CORBA.Object object) throws NamingException {
        this._orb = orb;
        try {
            this._nc = NamingContextHelper.narrow(object);
            if (this._nc == null) {
                throw new ConfigurationException("Cannot convert object reference to NamingContext: " + object);
            }
        }
        catch (final COMM_FAILURE rootCause) {
            final CommunicationException ex = new CommunicationException("Cannot connect to ORB");
            ex.setRootCause(rootCause);
            throw ex;
        }
    }
    
    private String getStringifiedIor(final String s) throws NamingException {
        if (s.startsWith("IOR:") || s.startsWith("corbaloc:")) {
            return s;
        }
        InputStream openStream = null;
        try {
            openStream = new URL(s).openStream();
            if (openStream != null) {
                String line;
                while ((line = new BufferedReader(new InputStreamReader(openStream, "8859_1")).readLine()) != null) {
                    if (line.startsWith("IOR:")) {
                        return line;
                    }
                }
            }
        }
        catch (final IOException rootCause) {
            final ConfigurationException ex = new ConfigurationException("Invalid URL: " + s);
            ex.setRootCause(rootCause);
            throw ex;
        }
        finally {
            try {
                if (openStream != null) {
                    openStream.close();
                }
            }
            catch (final IOException rootCause2) {
                final ConfigurationException ex2 = new ConfigurationException("Invalid URL: " + s);
                ex2.setRootCause(rootCause2);
                throw ex2;
            }
        }
        throw new ConfigurationException(s + " does not contain an IOR");
    }
    
    Object callResolve(final NameComponent[] array) throws NamingException {
        try {
            final org.omg.CORBA.Object resolve = this._nc.resolve(array);
            try {
                final NamingContext narrow = NamingContextHelper.narrow(resolve);
                if (narrow != null) {
                    return new CNCtx(this._orb, this.orbTracker, narrow, this._env, this.makeFullName(array));
                }
                return resolve;
            }
            catch (final SystemException ex) {
                return resolve;
            }
        }
        catch (final Exception ex2) {
            throw ExceptionMapper.mapException(ex2, this, array);
        }
    }
    
    @Override
    public Object lookup(final String s) throws NamingException {
        return this.lookup(new CompositeName(s));
    }
    
    @Override
    public Object lookup(final Name name) throws NamingException {
        if (this._nc == null) {
            throw new ConfigurationException("Context does not have a corresponding NamingContext");
        }
        if (name.size() == 0) {
            return this;
        }
        final NameComponent[] nameToCosName = CNNameParser.nameToCosName(name);
        Object o;
        try {
            o = this.callResolve(nameToCosName);
            try {
                if (CorbaUtils.isObjectFactoryTrusted(o)) {
                    o = NamingManager.getObjectInstance(o, name, this, this._env);
                }
            }
            catch (final NamingException ex) {
                throw ex;
            }
            catch (final Exception rootCause) {
                final NamingException ex2 = new NamingException("problem generating object using object factory");
                ex2.setRootCause(rootCause);
                throw ex2;
            }
        }
        catch (final CannotProceedException ex3) {
            return getContinuationContext(ex3).lookup(ex3.getRemainingName());
        }
        return o;
    }
    
    private void callBindOrRebind(final NameComponent[] array, final Name name, Object o, final boolean b) throws NamingException {
        if (this._nc == null) {
            throw new ConfigurationException("Context does not have a corresponding NamingContext");
        }
        try {
            o = NamingManager.getStateToBind(o, name, this, this._env);
            if (o instanceof CNCtx) {
                o = ((CNCtx)o)._nc;
            }
            if (o instanceof NamingContext) {
                final NamingContext narrow = NamingContextHelper.narrow((org.omg.CORBA.Object)o);
                if (b) {
                    this._nc.rebind_context(array, narrow);
                }
                else {
                    this._nc.bind_context(array, narrow);
                }
            }
            else {
                if (!(o instanceof org.omg.CORBA.Object)) {
                    throw new IllegalArgumentException("Only instances of org.omg.CORBA.Object can be bound");
                }
                if (b) {
                    this._nc.rebind(array, (org.omg.CORBA.Object)o);
                }
                else {
                    this._nc.bind(array, (org.omg.CORBA.Object)o);
                }
            }
        }
        catch (final BAD_PARAM rootCause) {
            final NotContextException ex = new NotContextException(name.toString());
            ex.setRootCause(rootCause);
            throw ex;
        }
        catch (final Exception ex2) {
            throw ExceptionMapper.mapException(ex2, this, array);
        }
    }
    
    @Override
    public void bind(final Name name, final Object o) throws NamingException {
        if (name.size() == 0) {
            throw new InvalidNameException("Name is empty");
        }
        final NameComponent[] nameToCosName = CNNameParser.nameToCosName(name);
        try {
            this.callBindOrRebind(nameToCosName, name, o, false);
        }
        catch (final CannotProceedException ex) {
            getContinuationContext(ex).bind(ex.getRemainingName(), o);
        }
    }
    
    private static Context getContinuationContext(final CannotProceedException ex) throws NamingException {
        try {
            return NamingManager.getContinuationContext(ex);
        }
        catch (final CannotProceedException ex2) {
            final Object resolvedObj = ex2.getResolvedObj();
            if (resolvedObj instanceof Reference && ((Reference)resolvedObj).get("nns").getContent() instanceof Context) {
                final NameNotFoundException ex3 = new NameNotFoundException("No object reference bound for specified name");
                ex3.setRootCause(ex.getRootCause());
                ex3.setRemainingName(ex.getRemainingName());
                throw ex3;
            }
            throw ex2;
        }
    }
    
    @Override
    public void bind(final String s, final Object o) throws NamingException {
        this.bind(new CompositeName(s), o);
    }
    
    @Override
    public void rebind(final Name name, final Object o) throws NamingException {
        if (name.size() == 0) {
            throw new InvalidNameException("Name is empty");
        }
        final NameComponent[] nameToCosName = CNNameParser.nameToCosName(name);
        try {
            this.callBindOrRebind(nameToCosName, name, o, true);
        }
        catch (final CannotProceedException ex) {
            getContinuationContext(ex).rebind(ex.getRemainingName(), o);
        }
    }
    
    @Override
    public void rebind(final String s, final Object o) throws NamingException {
        this.rebind(new CompositeName(s), o);
    }
    
    private void callUnbind(final NameComponent[] array) throws NamingException {
        if (this._nc == null) {
            throw new ConfigurationException("Context does not have a corresponding NamingContext");
        }
        try {
            this._nc.unbind(array);
        }
        catch (final NotFound notFound) {
            if (!this.leafNotFound(notFound, array[array.length - 1])) {
                throw ExceptionMapper.mapException(notFound, this, array);
            }
        }
        catch (final Exception ex) {
            throw ExceptionMapper.mapException(ex, this, array);
        }
    }
    
    private boolean leafNotFound(final NotFound notFound, final NameComponent nameComponent) {
        final NameComponent nameComponent2;
        return notFound.why.value() == 0 && notFound.rest_of_name.length == 1 && (nameComponent2 = notFound.rest_of_name[0]).id.equals(nameComponent.id) && (nameComponent2.kind == nameComponent.kind || (nameComponent2.kind != null && nameComponent2.kind.equals(nameComponent.kind)));
    }
    
    @Override
    public void unbind(final String s) throws NamingException {
        this.unbind(new CompositeName(s));
    }
    
    @Override
    public void unbind(final Name name) throws NamingException {
        if (name.size() == 0) {
            throw new InvalidNameException("Name is empty");
        }
        final NameComponent[] nameToCosName = CNNameParser.nameToCosName(name);
        try {
            this.callUnbind(nameToCosName);
        }
        catch (final CannotProceedException ex) {
            getContinuationContext(ex).unbind(ex.getRemainingName());
        }
    }
    
    @Override
    public void rename(final String s, final String s2) throws NamingException {
        this.rename(new CompositeName(s), new CompositeName(s2));
    }
    
    @Override
    public void rename(final Name name, final Name name2) throws NamingException {
        if (this._nc == null) {
            throw new ConfigurationException("Context does not have a corresponding NamingContext");
        }
        if (name.size() == 0 || name2.size() == 0) {
            throw new InvalidNameException("One or both names empty");
        }
        this.bind(name2, this.lookup(name));
        this.unbind(name);
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final String s) throws NamingException {
        return this.list(new CompositeName(s));
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final Name name) throws NamingException {
        return (NamingEnumeration<NameClassPair>)this.listBindings(name);
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final String s) throws NamingException {
        return this.listBindings(new CompositeName(s));
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final Name name) throws NamingException {
        if (this._nc == null) {
            throw new ConfigurationException("Context does not have a corresponding NamingContext");
        }
        if (name.size() > 0) {
            try {
                final Object lookup = this.lookup(name);
                if (lookup instanceof CNCtx) {
                    return new CNBindingEnumeration((CNCtx)lookup, true, this._env);
                }
                throw new NotContextException(name.toString());
            }
            catch (final NamingException ex) {
                throw ex;
            }
            catch (final BAD_PARAM rootCause) {
                final NotContextException ex2 = new NotContextException(name.toString());
                ex2.setRootCause(rootCause);
                throw ex2;
            }
        }
        return new CNBindingEnumeration(this, false, this._env);
    }
    
    private void callDestroy(final NamingContext namingContext) throws NamingException {
        if (this._nc == null) {
            throw new ConfigurationException("Context does not have a corresponding NamingContext");
        }
        try {
            namingContext.destroy();
        }
        catch (final Exception ex) {
            throw ExceptionMapper.mapException(ex, this, null);
        }
    }
    
    @Override
    public void destroySubcontext(final String s) throws NamingException {
        this.destroySubcontext(new CompositeName(s));
    }
    
    @Override
    public void destroySubcontext(final Name name) throws NamingException {
        if (this._nc == null) {
            throw new ConfigurationException("Context does not have a corresponding NamingContext");
        }
        NamingContext namingContext = this._nc;
        final NameComponent[] nameToCosName = CNNameParser.nameToCosName(name);
        if (name.size() > 0) {
            try {
                final CNCtx cnCtx = (CNCtx)this.callResolve(nameToCosName);
                namingContext = cnCtx._nc;
                cnCtx.close();
            }
            catch (final ClassCastException ex) {
                throw new NotContextException(name.toString());
            }
            catch (final CannotProceedException ex2) {
                getContinuationContext(ex2).destroySubcontext(ex2.getRemainingName());
                return;
            }
            catch (final NameNotFoundException ex3) {
                if (ex3.getRootCause() instanceof NotFound && this.leafNotFound((NotFound)ex3.getRootCause(), nameToCosName[nameToCosName.length - 1])) {
                    return;
                }
                throw ex3;
            }
            catch (final NamingException ex4) {
                throw ex4;
            }
        }
        this.callDestroy(namingContext);
        this.callUnbind(nameToCosName);
    }
    
    private Context callBindNewContext(final NameComponent[] array) throws NamingException {
        if (this._nc == null) {
            throw new ConfigurationException("Context does not have a corresponding NamingContext");
        }
        try {
            return new CNCtx(this._orb, this.orbTracker, this._nc.bind_new_context(array), this._env, this.makeFullName(array));
        }
        catch (final Exception ex) {
            throw ExceptionMapper.mapException(ex, this, array);
        }
    }
    
    @Override
    public Context createSubcontext(final String s) throws NamingException {
        return this.createSubcontext(new CompositeName(s));
    }
    
    @Override
    public Context createSubcontext(final Name name) throws NamingException {
        if (name.size() == 0) {
            throw new InvalidNameException("Name is empty");
        }
        final NameComponent[] nameToCosName = CNNameParser.nameToCosName(name);
        try {
            return this.callBindNewContext(nameToCosName);
        }
        catch (final CannotProceedException ex) {
            return getContinuationContext(ex).createSubcontext(ex.getRemainingName());
        }
    }
    
    @Override
    public Object lookupLink(final String s) throws NamingException {
        return this.lookupLink(new CompositeName(s));
    }
    
    @Override
    public Object lookupLink(final Name name) throws NamingException {
        return this.lookup(name);
    }
    
    @Override
    public NameParser getNameParser(final String s) throws NamingException {
        return CNCtx.parser;
    }
    
    @Override
    public NameParser getNameParser(final Name name) throws NamingException {
        return CNCtx.parser;
    }
    
    @Override
    public Hashtable<String, Object> getEnvironment() throws NamingException {
        if (this._env == null) {
            return new Hashtable<String, Object>(5, 0.75f);
        }
        return (Hashtable)this._env.clone();
    }
    
    @Override
    public String composeName(final String s, final String s2) throws NamingException {
        return this.composeName(new CompositeName(s), new CompositeName(s2)).toString();
    }
    
    @Override
    public Name composeName(final Name name, final Name name2) throws NamingException {
        return ((Name)name2.clone()).addAll(name);
    }
    
    @Override
    public Object addToEnvironment(final String s, final Object o) throws NamingException {
        if (this._env == null) {
            this._env = new Hashtable<String, Object>(7, 0.75f);
        }
        else {
            this._env = (Hashtable)this._env.clone();
        }
        return this._env.put(s, o);
    }
    
    @Override
    public Object removeFromEnvironment(final String s) throws NamingException {
        if (this._env != null && this._env.get(s) != null) {
            this._env = (Hashtable)this._env.clone();
            return this._env.remove(s);
        }
        return null;
    }
    
    public synchronized void incEnumCount() {
        ++this.enumCount;
    }
    
    public synchronized void decEnumCount() throws NamingException {
        --this.enumCount;
        if (this.enumCount == 0 && this.isCloseCalled) {
            this.close();
        }
    }
    
    @Override
    public synchronized void close() throws NamingException {
        if (this.enumCount > 0) {
            this.isCloseCalled = true;
        }
    }
    
    @Override
    protected void finalize() {
        try {
            this.close();
        }
        catch (final NamingException ex) {}
    }
    
    static {
        parser = new CNNameParser();
        trustURLCodebase = "true".equalsIgnoreCase(AccessController.doPrivileged(() -> System.getProperty("com.sun.jndi.cosnaming.object.trustURLCodebase", "false")));
    }
}
