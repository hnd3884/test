package org.apache.catalina.realm;

import org.apache.juli.logging.LogFactory;
import java.io.InputStream;
import java.io.IOException;
import org.apache.catalina.LifecycleException;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.digester.RuleSet;
import java.util.List;
import java.util.ArrayList;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.apache.tomcat.util.digester.Digester;
import org.apache.juli.logging.Log;

public class MemoryRealm extends RealmBase
{
    private static final Log log;
    private static Digester digester;
    @Deprecated
    protected static final String name = "MemoryRealm";
    private String pathname;
    private final Map<String, GenericPrincipal> principals;
    
    public MemoryRealm() {
        this.pathname = "conf/tomcat-users.xml";
        this.principals = new HashMap<String, GenericPrincipal>();
    }
    
    public String getPathname() {
        return this.pathname;
    }
    
    public void setPathname(final String pathname) {
        this.pathname = pathname;
    }
    
    @Override
    public Principal authenticate(final String username, final String credentials) {
        if (username == null || credentials == null) {
            if (MemoryRealm.log.isDebugEnabled()) {
                MemoryRealm.log.debug((Object)MemoryRealm.sm.getString("memoryRealm.authenticateFailure", new Object[] { username }));
            }
            return null;
        }
        final GenericPrincipal principal = this.principals.get(username);
        if (principal == null || principal.getPassword() == null) {
            this.getCredentialHandler().mutate(credentials);
            if (MemoryRealm.log.isDebugEnabled()) {
                MemoryRealm.log.debug((Object)MemoryRealm.sm.getString("memoryRealm.authenticateFailure", new Object[] { username }));
            }
            return null;
        }
        final boolean validated = this.getCredentialHandler().matches(credentials, principal.getPassword());
        if (validated) {
            if (MemoryRealm.log.isDebugEnabled()) {
                MemoryRealm.log.debug((Object)MemoryRealm.sm.getString("memoryRealm.authenticateSuccess", new Object[] { username }));
            }
            return principal;
        }
        if (MemoryRealm.log.isDebugEnabled()) {
            MemoryRealm.log.debug((Object)MemoryRealm.sm.getString("memoryRealm.authenticateFailure", new Object[] { username }));
        }
        return null;
    }
    
    void addUser(final String username, final String password, String roles) {
        final ArrayList<String> list = new ArrayList<String>();
        roles += ",";
        while (true) {
            final int comma = roles.indexOf(44);
            if (comma < 0) {
                break;
            }
            final String role = roles.substring(0, comma).trim();
            list.add(role);
            roles = roles.substring(comma + 1);
        }
        final GenericPrincipal principal = new GenericPrincipal(username, password, list);
        this.principals.put(username, principal);
    }
    
    protected synchronized Digester getDigester() {
        if (MemoryRealm.digester == null) {
            (MemoryRealm.digester = new Digester()).setValidating(false);
            try {
                MemoryRealm.digester.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
            }
            catch (final Exception e) {
                MemoryRealm.log.warn((Object)MemoryRealm.sm.getString("memoryRealm.xmlFeatureEncoding"), (Throwable)e);
            }
            MemoryRealm.digester.addRuleSet((RuleSet)new MemoryRuleSet());
        }
        return MemoryRealm.digester;
    }
    
    @Deprecated
    @Override
    protected String getName() {
        return "MemoryRealm";
    }
    
    @Override
    protected String getPassword(final String username) {
        final GenericPrincipal principal = this.principals.get(username);
        if (principal != null) {
            return principal.getPassword();
        }
        return null;
    }
    
    @Override
    protected Principal getPrincipal(final String username) {
        return this.principals.get(username);
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        final String pathName = this.getPathname();
        try (final InputStream is = ConfigFileLoader.getInputStream(pathName)) {
            if (MemoryRealm.log.isDebugEnabled()) {
                MemoryRealm.log.debug((Object)MemoryRealm.sm.getString("memoryRealm.loadPath", new Object[] { pathName }));
            }
            final Digester digester = this.getDigester();
            try {
                synchronized (digester) {
                    digester.push((Object)this);
                    digester.parse(is);
                }
            }
            catch (final Exception e) {
                throw new LifecycleException(MemoryRealm.sm.getString("memoryRealm.readXml"), e);
            }
            finally {
                digester.reset();
            }
        }
        catch (final IOException ioe) {
            throw new LifecycleException(MemoryRealm.sm.getString("memoryRealm.loadExist", new Object[] { pathName }), ioe);
        }
        super.startInternal();
    }
    
    static {
        log = LogFactory.getLog((Class)MemoryRealm.class);
        MemoryRealm.digester = null;
    }
}
