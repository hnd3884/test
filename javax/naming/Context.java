package javax.naming;

import java.util.Hashtable;

public interface Context
{
    public static final String INITIAL_CONTEXT_FACTORY = "java.naming.factory.initial";
    public static final String OBJECT_FACTORIES = "java.naming.factory.object";
    public static final String STATE_FACTORIES = "java.naming.factory.state";
    public static final String URL_PKG_PREFIXES = "java.naming.factory.url.pkgs";
    public static final String PROVIDER_URL = "java.naming.provider.url";
    public static final String DNS_URL = "java.naming.dns.url";
    public static final String AUTHORITATIVE = "java.naming.authoritative";
    public static final String BATCHSIZE = "java.naming.batchsize";
    public static final String REFERRAL = "java.naming.referral";
    public static final String SECURITY_PROTOCOL = "java.naming.security.protocol";
    public static final String SECURITY_AUTHENTICATION = "java.naming.security.authentication";
    public static final String SECURITY_PRINCIPAL = "java.naming.security.principal";
    public static final String SECURITY_CREDENTIALS = "java.naming.security.credentials";
    public static final String LANGUAGE = "java.naming.language";
    public static final String APPLET = "java.naming.applet";
    
    Object lookup(final Name p0) throws NamingException;
    
    Object lookup(final String p0) throws NamingException;
    
    void bind(final Name p0, final Object p1) throws NamingException;
    
    void bind(final String p0, final Object p1) throws NamingException;
    
    void rebind(final Name p0, final Object p1) throws NamingException;
    
    void rebind(final String p0, final Object p1) throws NamingException;
    
    void unbind(final Name p0) throws NamingException;
    
    void unbind(final String p0) throws NamingException;
    
    void rename(final Name p0, final Name p1) throws NamingException;
    
    void rename(final String p0, final String p1) throws NamingException;
    
    NamingEnumeration<NameClassPair> list(final Name p0) throws NamingException;
    
    NamingEnumeration<NameClassPair> list(final String p0) throws NamingException;
    
    NamingEnumeration<Binding> listBindings(final Name p0) throws NamingException;
    
    NamingEnumeration<Binding> listBindings(final String p0) throws NamingException;
    
    void destroySubcontext(final Name p0) throws NamingException;
    
    void destroySubcontext(final String p0) throws NamingException;
    
    Context createSubcontext(final Name p0) throws NamingException;
    
    Context createSubcontext(final String p0) throws NamingException;
    
    Object lookupLink(final Name p0) throws NamingException;
    
    Object lookupLink(final String p0) throws NamingException;
    
    NameParser getNameParser(final Name p0) throws NamingException;
    
    NameParser getNameParser(final String p0) throws NamingException;
    
    Name composeName(final Name p0, final Name p1) throws NamingException;
    
    String composeName(final String p0, final String p1) throws NamingException;
    
    Object addToEnvironment(final String p0, final Object p1) throws NamingException;
    
    Object removeFromEnvironment(final String p0) throws NamingException;
    
    Hashtable<?, ?> getEnvironment() throws NamingException;
    
    void close() throws NamingException;
    
    String getNameInNamespace() throws NamingException;
}
