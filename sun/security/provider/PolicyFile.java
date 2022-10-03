package sun.security.provider;

import sun.misc.SharedSecrets;
import java.util.HashMap;
import java.util.Random;
import java.util.Map;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import java.util.Set;
import javax.security.auth.Subject;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.security.Principal;
import java.security.Permissions;
import java.security.PermissionCollection;
import sun.misc.JavaSecurityProtectionDomainAccess;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.security.KeyStoreException;
import java.util.StringTokenizer;
import java.security.AllPermission;
import java.net.NetPermission;
import java.net.SocketPermission;
import java.io.FilePermission;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.security.UnresolvedPermission;
import java.util.List;
import java.net.MalformedURLException;
import java.util.PropertyPermission;
import java.security.Permission;
import sun.security.util.SecurityConstants;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.security.KeyStore;
import java.io.IOException;
import java.text.MessageFormat;
import sun.security.util.ResourcesMgr;
import java.io.Reader;
import java.io.InputStreamReader;
import sun.security.util.PolicyUtil;
import java.net.URI;
import sun.net.www.ParseUtil;
import java.io.File;
import sun.security.util.PropertyExpander;
import java.security.AccessController;
import java.security.Security;
import java.security.PrivilegedAction;
import java.net.URL;
import sun.security.util.Debug;
import java.security.Policy;

public class PolicyFile extends Policy
{
    private static final Debug debug;
    private static final String NONE = "NONE";
    private static final String P11KEYSTORE = "PKCS11";
    private static final String SELF = "${{self}}";
    private static final String X500PRINCIPAL = "javax.security.auth.x500.X500Principal";
    private static final String POLICY = "java.security.policy";
    private static final String SECURITY_MANAGER = "java.security.manager";
    private static final String POLICY_URL = "policy.url.";
    private static final String AUTH_POLICY = "java.security.auth.policy";
    private static final String AUTH_POLICY_URL = "auth.policy.url.";
    private static final int DEFAULT_CACHE_SIZE = 1;
    private volatile PolicyInfo policyInfo;
    private boolean constructed;
    private boolean expandProperties;
    private boolean ignoreIdentityScope;
    private boolean allowSystemProperties;
    private boolean notUtf8;
    private URL url;
    private static final Class[] PARAMS0;
    private static final Class[] PARAMS1;
    private static final Class[] PARAMS2;
    
    public PolicyFile() {
        this.constructed = false;
        this.expandProperties = true;
        this.ignoreIdentityScope = true;
        this.allowSystemProperties = true;
        this.notUtf8 = false;
        this.init(null);
    }
    
    public PolicyFile(final URL url) {
        this.constructed = false;
        this.expandProperties = true;
        this.ignoreIdentityScope = true;
        this.allowSystemProperties = true;
        this.notUtf8 = false;
        this.init(this.url = url);
    }
    
    private void init(final URL url) {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                PolicyFile.this.expandProperties = "true".equalsIgnoreCase(Security.getProperty("policy.expandProperties"));
                PolicyFile.this.ignoreIdentityScope = "true".equalsIgnoreCase(Security.getProperty("policy.ignoreIdentityScope"));
                PolicyFile.this.allowSystemProperties = "true".equalsIgnoreCase(Security.getProperty("policy.allowSystemProperty"));
                PolicyFile.this.notUtf8 = "false".equalsIgnoreCase(System.getProperty("sun.security.policy.utf8"));
                return System.getProperty("sun.security.policy.numcaches");
            }
        });
        int int1;
        if (s != null) {
            try {
                int1 = Integer.parseInt(s);
            }
            catch (final NumberFormatException ex) {
                int1 = 1;
            }
        }
        else {
            int1 = 1;
        }
        final PolicyInfo policyInfo = new PolicyInfo(int1);
        this.initPolicyFile(policyInfo, url);
        this.policyInfo = policyInfo;
    }
    
    private void initPolicyFile(final PolicyInfo policyInfo, final URL url) {
        if (url != null) {
            if (PolicyFile.debug != null) {
                PolicyFile.debug.println("reading " + url);
            }
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    if (!PolicyFile.this.init(url, policyInfo)) {
                        PolicyFile.this.initStaticPolicy(policyInfo);
                    }
                    return null;
                }
            });
        }
        else {
            if (!this.initPolicyFile("java.security.policy", "policy.url.", policyInfo)) {
                this.initStaticPolicy(policyInfo);
            }
            this.initPolicyFile("java.security.auth.policy", "auth.policy.url.", policyInfo);
        }
    }
    
    private boolean initPolicyFile(final String s, final String s2, final PolicyInfo policyInfo) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                boolean b = false;
                if (PolicyFile.this.allowSystemProperties) {
                    String s = System.getProperty(s);
                    if (s != null) {
                        boolean b2 = false;
                        if (s.startsWith("=")) {
                            b2 = true;
                            s = s.substring(1);
                        }
                        try {
                            final String expand = PropertyExpander.expand(s);
                            final File file = new File(expand);
                            URL fileToEncodedURL;
                            if (file.exists()) {
                                fileToEncodedURL = ParseUtil.fileToEncodedURL(new File(file.getCanonicalPath()));
                            }
                            else {
                                fileToEncodedURL = new URL(expand);
                            }
                            if (PolicyFile.debug != null) {
                                PolicyFile.debug.println("reading " + fileToEncodedURL);
                            }
                            if (PolicyFile.this.init(fileToEncodedURL, policyInfo)) {
                                b = true;
                            }
                        }
                        catch (final Exception ex) {
                            if (PolicyFile.debug != null) {
                                PolicyFile.debug.println("caught exception: " + ex);
                            }
                        }
                        if (b2) {
                            if (PolicyFile.debug != null) {
                                PolicyFile.debug.println("overriding other policies!");
                            }
                            return b;
                        }
                    }
                }
                String property;
                for (int n = 1; (property = Security.getProperty(s2 + n)) != null; ++n) {
                    try {
                        final String replace = PropertyExpander.expand(property).replace(File.separatorChar, '/');
                        URL url;
                        if (property.startsWith("file:${java.home}/") || property.startsWith("file:${user.home}/")) {
                            url = new File(replace.substring(5)).toURI().toURL();
                        }
                        else {
                            url = new URI(replace).toURL();
                        }
                        if (PolicyFile.debug != null) {
                            PolicyFile.debug.println("reading " + url);
                        }
                        if (PolicyFile.this.init(url, policyInfo)) {
                            b = true;
                        }
                    }
                    catch (final Exception ex2) {
                        if (PolicyFile.debug != null) {
                            PolicyFile.debug.println("Debug info only. Error reading policy " + ex2);
                            ex2.printStackTrace();
                        }
                    }
                }
                return b;
            }
        });
    }
    
    private boolean init(final URL url, final PolicyInfo policyInfo) {
        boolean b = false;
        final PolicyParser policyParser = new PolicyParser(this.expandProperties);
        InputStreamReader inputStreamReader = null;
        try {
            if (this.notUtf8) {
                inputStreamReader = new InputStreamReader(PolicyUtil.getInputStream(url));
            }
            else {
                inputStreamReader = new InputStreamReader(PolicyUtil.getInputStream(url), "UTF-8");
            }
            policyParser.read(inputStreamReader);
            KeyStore keyStore = null;
            try {
                keyStore = PolicyUtil.getKeyStore(url, policyParser.getKeyStoreUrl(), policyParser.getKeyStoreType(), policyParser.getKeyStoreProvider(), policyParser.getStorePassURL(), PolicyFile.debug);
            }
            catch (final Exception ex) {
                if (PolicyFile.debug != null) {
                    PolicyFile.debug.println("Debug info only. Ignoring exception.");
                    ex.printStackTrace();
                }
            }
            final Enumeration<PolicyParser.GrantEntry> grantElements = policyParser.grantElements();
            while (grantElements.hasMoreElements()) {
                this.addGrantEntry(grantElements.nextElement(), keyStore, policyInfo);
            }
        }
        catch (final PolicyParser.ParsingException ex2) {
            System.err.println(new MessageFormat(ResourcesMgr.getString("java.security.policy.error.parsing.policy.message")).format(new Object[] { url, ex2.getLocalizedMessage() }));
            if (PolicyFile.debug != null) {
                ex2.printStackTrace();
            }
        }
        catch (final Exception ex3) {
            if (PolicyFile.debug != null) {
                PolicyFile.debug.println("error parsing " + url);
                PolicyFile.debug.println(ex3.toString());
                ex3.printStackTrace();
            }
        }
        finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                    b = true;
                }
                catch (final IOException ex4) {}
            }
            else {
                b = true;
            }
        }
        return b;
    }
    
    private void initStaticPolicy(final PolicyInfo policyInfo) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                final PolicyEntry policyEntry = new PolicyEntry(new CodeSource(null, (Certificate[])null));
                policyEntry.add(SecurityConstants.LOCAL_LISTEN_PERMISSION);
                policyEntry.add(new PropertyPermission("java.version", "read"));
                policyEntry.add(new PropertyPermission("java.vendor", "read"));
                policyEntry.add(new PropertyPermission("java.vendor.url", "read"));
                policyEntry.add(new PropertyPermission("java.class.version", "read"));
                policyEntry.add(new PropertyPermission("os.name", "read"));
                policyEntry.add(new PropertyPermission("os.version", "read"));
                policyEntry.add(new PropertyPermission("os.arch", "read"));
                policyEntry.add(new PropertyPermission("file.separator", "read"));
                policyEntry.add(new PropertyPermission("path.separator", "read"));
                policyEntry.add(new PropertyPermission("line.separator", "read"));
                policyEntry.add(new PropertyPermission("java.specification.version", "read"));
                policyEntry.add(new PropertyPermission("java.specification.vendor", "read"));
                policyEntry.add(new PropertyPermission("java.specification.name", "read"));
                policyEntry.add(new PropertyPermission("java.vm.specification.version", "read"));
                policyEntry.add(new PropertyPermission("java.vm.specification.vendor", "read"));
                policyEntry.add(new PropertyPermission("java.vm.specification.name", "read"));
                policyEntry.add(new PropertyPermission("java.vm.version", "read"));
                policyEntry.add(new PropertyPermission("java.vm.vendor", "read"));
                policyEntry.add(new PropertyPermission("java.vm.name", "read"));
                policyInfo.policyEntries.add(policyEntry);
                final String[] extDirs = PolicyParser.parseExtDirs("${{java.ext.dirs}}", 0);
                if (extDirs != null && extDirs.length > 0) {
                    for (int i = 0; i < extDirs.length; ++i) {
                        try {
                            final PolicyEntry policyEntry2 = new PolicyEntry(PolicyFile.this.canonicalizeCodebase(new CodeSource(new URL(extDirs[i]), (Certificate[])null), false));
                            policyEntry2.add(SecurityConstants.ALL_PERMISSION);
                            policyInfo.policyEntries.add(policyEntry2);
                        }
                        catch (final Exception ex) {}
                    }
                }
                return null;
            }
        });
    }
    
    private CodeSource getCodeSource(final PolicyParser.GrantEntry grantEntry, final KeyStore keyStore, final PolicyInfo policyInfo) throws MalformedURLException {
        Certificate[] certificates = null;
        if (grantEntry.signedBy != null) {
            certificates = this.getCertificates(keyStore, grantEntry.signedBy, policyInfo);
            if (certificates == null) {
                if (PolicyFile.debug != null) {
                    PolicyFile.debug.println("  -- No certs for alias '" + grantEntry.signedBy + "' - ignoring entry");
                }
                return null;
            }
        }
        URL url;
        if (grantEntry.codeBase != null) {
            url = new URL(grantEntry.codeBase);
        }
        else {
            url = null;
        }
        return this.canonicalizeCodebase(new CodeSource(url, certificates), false);
    }
    
    private void addGrantEntry(final PolicyParser.GrantEntry grantEntry, final KeyStore keyStore, final PolicyInfo policyInfo) {
        if (PolicyFile.debug != null) {
            PolicyFile.debug.println("Adding policy entry: ");
            PolicyFile.debug.println("  signedBy " + grantEntry.signedBy);
            PolicyFile.debug.println("  codeBase " + grantEntry.codeBase);
            if (grantEntry.principals != null) {
                final Iterator<Object> iterator = grantEntry.principals.iterator();
                while (iterator.hasNext()) {
                    PolicyFile.debug.println("  " + iterator.next().toString());
                }
            }
        }
        try {
            final CodeSource codeSource = this.getCodeSource(grantEntry, keyStore, policyInfo);
            if (codeSource == null) {
                return;
            }
            if (!this.replacePrincipals(grantEntry.principals, keyStore)) {
                return;
            }
            final PolicyEntry policyEntry = new PolicyEntry(codeSource, grantEntry.principals);
            final Enumeration<PolicyParser.PermissionEntry> permissionElements = grantEntry.permissionElements();
            while (permissionElements.hasMoreElements()) {
                final PolicyParser.PermissionEntry permissionEntry = permissionElements.nextElement();
                try {
                    this.expandPermissionName(permissionEntry, keyStore);
                    if (permissionEntry.permission.equals("javax.security.auth.PrivateCredentialPermission") && permissionEntry.name.endsWith(" self")) {
                        permissionEntry.name = permissionEntry.name.substring(0, permissionEntry.name.indexOf("self")) + "${{self}}";
                    }
                    Permission instance;
                    if (permissionEntry.name != null && permissionEntry.name.indexOf("${{self}}") != -1) {
                        Certificate[] certificates;
                        if (permissionEntry.signedBy != null) {
                            certificates = this.getCertificates(keyStore, permissionEntry.signedBy, policyInfo);
                        }
                        else {
                            certificates = null;
                        }
                        instance = new SelfPermission(permissionEntry.permission, permissionEntry.name, permissionEntry.action, certificates);
                    }
                    else {
                        instance = getInstance(permissionEntry.permission, permissionEntry.name, permissionEntry.action);
                    }
                    policyEntry.add(instance);
                    if (PolicyFile.debug == null) {
                        continue;
                    }
                    PolicyFile.debug.println("  " + instance);
                }
                catch (final ClassNotFoundException ex) {
                    Certificate[] certificates2;
                    if (permissionEntry.signedBy != null) {
                        certificates2 = this.getCertificates(keyStore, permissionEntry.signedBy, policyInfo);
                    }
                    else {
                        certificates2 = null;
                    }
                    if (certificates2 == null && permissionEntry.signedBy != null) {
                        continue;
                    }
                    final UnresolvedPermission unresolvedPermission = new UnresolvedPermission(permissionEntry.permission, permissionEntry.name, permissionEntry.action, certificates2);
                    policyEntry.add(unresolvedPermission);
                    if (PolicyFile.debug == null) {
                        continue;
                    }
                    PolicyFile.debug.println("  " + unresolvedPermission);
                }
                catch (final InvocationTargetException ex2) {
                    System.err.println(new MessageFormat(ResourcesMgr.getString("java.security.policy.error.adding.Permission.perm.message")).format(new Object[] { permissionEntry.permission, ex2.getTargetException().toString() }));
                }
                catch (final Exception ex3) {
                    System.err.println(new MessageFormat(ResourcesMgr.getString("java.security.policy.error.adding.Permission.perm.message")).format(new Object[] { permissionEntry.permission, ex3.toString() }));
                }
            }
            policyInfo.policyEntries.add(policyEntry);
        }
        catch (final Exception ex4) {
            System.err.println(new MessageFormat(ResourcesMgr.getString("java.security.policy.error.adding.Entry.message")).format(new Object[] { ex4.toString() }));
        }
        if (PolicyFile.debug != null) {
            PolicyFile.debug.println();
        }
    }
    
    private static final Permission getInstance(final String s, final String s2, final String s3) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final Class<?> forName = Class.forName(s, false, null);
        final Permission knownInstance = getKnownInstance(forName, s2, s3);
        if (knownInstance != null) {
            return knownInstance;
        }
        if (!Permission.class.isAssignableFrom(forName)) {
            throw new ClassCastException(s + " is not a Permission");
        }
        if (s2 == null && s3 == null) {
            try {
                return (Permission)forName.getConstructor((Class<?>[])PolicyFile.PARAMS0).newInstance(new Object[0]);
            }
            catch (final NoSuchMethodException ex) {
                try {
                    return (Permission)forName.getConstructor((Class<?>[])PolicyFile.PARAMS1).newInstance(s2);
                }
                catch (final NoSuchMethodException ex2) {
                    return (Permission)forName.getConstructor((Class<?>[])PolicyFile.PARAMS2).newInstance(s2, s3);
                }
            }
        }
        if (s2 != null && s3 == null) {
            try {
                return (Permission)forName.getConstructor((Class<?>[])PolicyFile.PARAMS1).newInstance(s2);
            }
            catch (final NoSuchMethodException ex3) {
                return (Permission)forName.getConstructor((Class<?>[])PolicyFile.PARAMS2).newInstance(s2, s3);
            }
        }
        return (Permission)forName.getConstructor((Class<?>[])PolicyFile.PARAMS2).newInstance(s2, s3);
    }
    
    private static final Permission getKnownInstance(final Class<?> clazz, final String s, final String s2) {
        if (clazz.equals(FilePermission.class)) {
            return new FilePermission(s, s2);
        }
        if (clazz.equals(SocketPermission.class)) {
            return new SocketPermission(s, s2);
        }
        if (clazz.equals(RuntimePermission.class)) {
            return new RuntimePermission(s, s2);
        }
        if (clazz.equals(PropertyPermission.class)) {
            return new PropertyPermission(s, s2);
        }
        if (clazz.equals(NetPermission.class)) {
            return new NetPermission(s, s2);
        }
        if (clazz.equals(AllPermission.class)) {
            return SecurityConstants.ALL_PERMISSION;
        }
        return null;
    }
    
    private Certificate[] getCertificates(final KeyStore keyStore, final String s, final PolicyInfo policyInfo) {
        List<Certificate> list = null;
        final StringTokenizer stringTokenizer = new StringTokenizer(s, ",");
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            final String trim = stringTokenizer.nextToken().trim();
            ++n;
            Certificate certificate = null;
            synchronized (policyInfo.aliasMapping) {
                certificate = policyInfo.aliasMapping.get(trim);
                if (certificate == null && keyStore != null) {
                    try {
                        certificate = keyStore.getCertificate(trim);
                    }
                    catch (final KeyStoreException ex) {}
                    if (certificate != null) {
                        policyInfo.aliasMapping.put(trim, certificate);
                        policyInfo.aliasMapping.put(certificate, trim);
                    }
                }
            }
            if (certificate != null) {
                if (list == null) {
                    list = new ArrayList<Certificate>();
                }
                list.add(certificate);
            }
        }
        if (list != null && n == list.size()) {
            final Certificate[] array = new Certificate[list.size()];
            list.toArray(array);
            return array;
        }
        return null;
    }
    
    @Override
    public void refresh() {
        this.init(this.url);
    }
    
    @Override
    public boolean implies(final ProtectionDomain protectionDomain, final Permission permission) {
        final JavaSecurityProtectionDomainAccess.ProtectionDomainCache pdMapping = this.policyInfo.getPdMapping();
        final PermissionCollection value = pdMapping.get(protectionDomain);
        if (value != null) {
            return value.implies(permission);
        }
        final PermissionCollection permissions = this.getPermissions(protectionDomain);
        if (permissions == null) {
            return false;
        }
        pdMapping.put(protectionDomain, permissions);
        return permissions.implies(permission);
    }
    
    @Override
    public PermissionCollection getPermissions(final ProtectionDomain protectionDomain) {
        final Permissions permissions = new Permissions();
        if (protectionDomain == null) {
            return permissions;
        }
        this.getPermissions(permissions, protectionDomain);
        final PermissionCollection permissions2 = protectionDomain.getPermissions();
        if (permissions2 != null) {
            synchronized (permissions2) {
                final Enumeration<Permission> elements = permissions2.elements();
                while (elements.hasMoreElements()) {
                    permissions.add(elements.nextElement());
                }
            }
        }
        return permissions;
    }
    
    @Override
    public PermissionCollection getPermissions(final CodeSource codeSource) {
        return this.getPermissions(new Permissions(), codeSource);
    }
    
    private PermissionCollection getPermissions(final Permissions permissions, final ProtectionDomain protectionDomain) {
        if (PolicyFile.debug != null) {
            PolicyFile.debug.println("getPermissions:\n\t" + this.printPD(protectionDomain));
        }
        final CodeSource codeSource = protectionDomain.getCodeSource();
        if (codeSource == null) {
            return permissions;
        }
        return this.getPermissions(permissions, AccessController.doPrivileged((PrivilegedAction<CodeSource>)new PrivilegedAction<CodeSource>() {
            @Override
            public CodeSource run() {
                return PolicyFile.this.canonicalizeCodebase(codeSource, true);
            }
        }), protectionDomain.getPrincipals());
    }
    
    private PermissionCollection getPermissions(final Permissions permissions, final CodeSource codeSource) {
        if (codeSource == null) {
            return permissions;
        }
        return this.getPermissions(permissions, AccessController.doPrivileged((PrivilegedAction<CodeSource>)new PrivilegedAction<CodeSource>() {
            @Override
            public CodeSource run() {
                return PolicyFile.this.canonicalizeCodebase(codeSource, true);
            }
        }), null);
    }
    
    private Permissions getPermissions(final Permissions permissions, final CodeSource codeSource, final Principal[] array) {
        final PolicyInfo policyInfo = this.policyInfo;
        final Iterator<PolicyEntry> iterator = policyInfo.policyEntries.iterator();
        while (iterator.hasNext()) {
            this.addPermissions(permissions, codeSource, array, iterator.next());
        }
        synchronized (policyInfo.identityPolicyEntries) {
            final Iterator<PolicyEntry> iterator2 = policyInfo.identityPolicyEntries.iterator();
            while (iterator2.hasNext()) {
                this.addPermissions(permissions, codeSource, array, iterator2.next());
            }
        }
        if (!this.ignoreIdentityScope) {
            final Certificate[] certificates = codeSource.getCertificates();
            if (certificates != null) {
                for (int i = 0; i < certificates.length; ++i) {
                    if (policyInfo.aliasMapping.get(certificates[i]) == null && this.checkForTrustedIdentity(certificates[i], policyInfo)) {
                        permissions.add(SecurityConstants.ALL_PERMISSION);
                    }
                }
            }
        }
        return permissions;
    }
    
    private void addPermissions(final Permissions permissions, final CodeSource codeSource, final Principal[] array, final PolicyEntry policyEntry) {
        if (PolicyFile.debug != null) {
            PolicyFile.debug.println("evaluate codesources:\n\tPolicy CodeSource: " + policyEntry.getCodeSource() + "\n\tActive CodeSource: " + codeSource);
        }
        if (!AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return new Boolean(policyEntry.getCodeSource().implies(codeSource));
            }
        })) {
            if (PolicyFile.debug != null) {
                PolicyFile.debug.println("evaluation (codesource) failed");
            }
            return;
        }
        final List<PolicyParser.PrincipalEntry> principals = policyEntry.getPrincipals();
        if (PolicyFile.debug != null) {
            final ArrayList list = new ArrayList();
            if (array != null) {
                for (int i = 0; i < array.length; ++i) {
                    list.add(new PolicyParser.PrincipalEntry(array[i].getClass().getName(), array[i].getName()));
                }
            }
            PolicyFile.debug.println("evaluate principals:\n\tPolicy Principals: " + principals + "\n\tActive Principals: " + list);
        }
        if (principals == null || principals.isEmpty()) {
            this.addPerms(permissions, array, policyEntry);
            if (PolicyFile.debug != null) {
                PolicyFile.debug.println("evaluation (codesource/principals) passed");
            }
            return;
        }
        if (array == null || array.length == 0) {
            if (PolicyFile.debug != null) {
                PolicyFile.debug.println("evaluation (principals) failed");
            }
            return;
        }
        for (final PolicyParser.PrincipalEntry principalEntry : principals) {
            if (principalEntry.isWildcardClass()) {
                continue;
            }
            if (principalEntry.isWildcardName()) {
                if (wildcardPrincipalNameImplies(principalEntry.principalClass, array)) {
                    continue;
                }
                if (PolicyFile.debug != null) {
                    PolicyFile.debug.println("evaluation (principal name wildcard) failed");
                }
                return;
            }
            else {
                final Subject subject = new Subject(true, new HashSet<Principal>(Arrays.asList(array)), Collections.EMPTY_SET, Collections.EMPTY_SET);
                try {
                    final Class<?> forName = Class.forName(principalEntry.principalClass, false, Thread.currentThread().getContextClassLoader());
                    if (!Principal.class.isAssignableFrom(forName)) {
                        throw new ClassCastException(principalEntry.principalClass + " is not a Principal");
                    }
                    final Principal principal = (Principal)forName.getConstructor((Class<?>[])PolicyFile.PARAMS1).newInstance(principalEntry.principalName);
                    if (PolicyFile.debug != null) {
                        PolicyFile.debug.println("found Principal " + principal.getClass().getName());
                    }
                    if (!principal.implies(subject)) {
                        if (PolicyFile.debug != null) {
                            PolicyFile.debug.println("evaluation (principal implies) failed");
                        }
                        return;
                    }
                    continue;
                }
                catch (final Exception ex) {
                    if (PolicyFile.debug != null) {
                        ex.printStackTrace();
                    }
                    if (!principalEntry.implies(subject)) {
                        if (PolicyFile.debug != null) {
                            PolicyFile.debug.println("evaluation (default principal implies) failed");
                        }
                        return;
                    }
                    continue;
                }
            }
        }
        if (PolicyFile.debug != null) {
            PolicyFile.debug.println("evaluation (codesource/principals) passed");
        }
        this.addPerms(permissions, array, policyEntry);
    }
    
    private static boolean wildcardPrincipalNameImplies(final String s, final Principal[] array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (s.equals(array[i].getClass().getName())) {
                return true;
            }
        }
        return false;
    }
    
    private void addPerms(final Permissions permissions, final Principal[] array, final PolicyEntry policyEntry) {
        for (int i = 0; i < policyEntry.permissions.size(); ++i) {
            final Permission permission = policyEntry.permissions.get(i);
            if (PolicyFile.debug != null) {
                PolicyFile.debug.println("  granting " + permission);
            }
            if (permission instanceof SelfPermission) {
                this.expandSelf((SelfPermission)permission, policyEntry.getPrincipals(), array, permissions);
            }
            else {
                permissions.add(permission);
            }
        }
    }
    
    private void expandSelf(final SelfPermission selfPermission, final List<PolicyParser.PrincipalEntry> list, final Principal[] array, final Permissions permissions) {
        if (list == null || list.isEmpty()) {
            if (PolicyFile.debug != null) {
                PolicyFile.debug.println("Ignoring permission " + selfPermission.getSelfType() + " with target name (" + selfPermission.getSelfName() + ").  No Principal(s) specified in the grant clause.  SELF-based target names are only valid in the context of a Principal-based grant entry.");
            }
            return;
        }
        int n = 0;
        final StringBuilder sb = new StringBuilder();
        int index;
        while ((index = selfPermission.getSelfName().indexOf("${{self}}", n)) != -1) {
            sb.append(selfPermission.getSelfName().substring(n, index));
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                final String[][] principalInfo = this.getPrincipalInfo((PolicyParser.PrincipalEntry)iterator.next(), array);
                for (int i = 0; i < principalInfo.length; ++i) {
                    if (i != 0) {
                        sb.append(", ");
                    }
                    sb.append(principalInfo[i][0] + " \"" + principalInfo[i][1] + "\"");
                }
                if (iterator.hasNext()) {
                    sb.append(", ");
                }
            }
            n = index + "${{self}}".length();
        }
        sb.append(selfPermission.getSelfName().substring(n));
        if (PolicyFile.debug != null) {
            PolicyFile.debug.println("  expanded:\n\t" + selfPermission.getSelfName() + "\n  into:\n\t" + sb.toString());
        }
        try {
            permissions.add(getInstance(selfPermission.getSelfType(), sb.toString(), selfPermission.getSelfActions()));
        }
        catch (final ClassNotFoundException ex) {
            Class<? extends Permission> class1 = null;
            synchronized (permissions) {
                final Enumeration<Permission> elements = permissions.elements();
                while (elements.hasMoreElements()) {
                    final Permission permission = elements.nextElement();
                    if (permission.getClass().getName().equals(selfPermission.getSelfType())) {
                        class1 = permission.getClass();
                        break;
                    }
                }
            }
            if (class1 == null) {
                permissions.add(new UnresolvedPermission(selfPermission.getSelfType(), sb.toString(), selfPermission.getSelfActions(), selfPermission.getCerts()));
            }
            else {
                try {
                    if (selfPermission.getSelfActions() == null) {
                        try {
                            permissions.add((Permission)class1.getConstructor((Class<?>[])PolicyFile.PARAMS1).newInstance(sb.toString()));
                        }
                        catch (final NoSuchMethodException ex2) {
                            permissions.add((Permission)class1.getConstructor((Class<?>[])PolicyFile.PARAMS2).newInstance(sb.toString(), selfPermission.getSelfActions()));
                        }
                    }
                    else {
                        permissions.add((Permission)class1.getConstructor((Class<?>[])PolicyFile.PARAMS2).newInstance(sb.toString(), selfPermission.getSelfActions()));
                    }
                }
                catch (final Exception ex3) {
                    if (PolicyFile.debug != null) {
                        PolicyFile.debug.println("self entry expansion  instantiation failed: " + ex3.toString());
                    }
                }
            }
        }
        catch (final Exception ex4) {
            if (PolicyFile.debug != null) {
                PolicyFile.debug.println(ex4.toString());
            }
        }
    }
    
    private String[][] getPrincipalInfo(final PolicyParser.PrincipalEntry principalEntry, final Principal[] array) {
        if (!principalEntry.isWildcardClass() && !principalEntry.isWildcardName()) {
            final String[][] array2 = new String[1][2];
            array2[0][0] = principalEntry.principalClass;
            array2[0][1] = principalEntry.principalName;
            return array2;
        }
        if (!principalEntry.isWildcardClass() && principalEntry.isWildcardName()) {
            final ArrayList list = new ArrayList();
            for (int i = 0; i < array.length; ++i) {
                if (principalEntry.principalClass.equals(array[i].getClass().getName())) {
                    list.add(array[i]);
                }
            }
            final String[][] array3 = new String[list.size()][2];
            int n = 0;
            for (final Principal principal : list) {
                array3[n][0] = principal.getClass().getName();
                array3[n][1] = principal.getName();
                ++n;
            }
            return array3;
        }
        final String[][] array4 = new String[array.length][2];
        for (int j = 0; j < array.length; ++j) {
            array4[j][0] = array[j].getClass().getName();
            array4[j][1] = array[j].getName();
        }
        return array4;
    }
    
    protected Certificate[] getSignerCertificates(final CodeSource codeSource) {
        final Certificate[] certificates;
        if ((certificates = codeSource.getCertificates()) == null) {
            return null;
        }
        for (int i = 0; i < certificates.length; ++i) {
            if (!(certificates[i] instanceof X509Certificate)) {
                return codeSource.getCertificates();
            }
        }
        int j = 0;
        int n = 0;
        while (j < certificates.length) {
            ++n;
            while (j + 1 < certificates.length && ((X509Certificate)certificates[j]).getIssuerDN().equals(((X509Certificate)certificates[j + 1]).getSubjectDN())) {
                ++j;
            }
            ++j;
        }
        if (n == certificates.length) {
            return certificates;
        }
        final ArrayList list = new ArrayList();
        for (int k = 0; k < certificates.length; ++k) {
            list.add(certificates[k]);
            while (k + 1 < certificates.length && ((X509Certificate)certificates[k]).getIssuerDN().equals(((X509Certificate)certificates[k + 1]).getSubjectDN())) {
                ++k;
            }
        }
        final Certificate[] array = new Certificate[list.size()];
        list.toArray(array);
        return array;
    }
    
    private CodeSource canonicalizeCodebase(final CodeSource codeSource, final boolean b) {
        String decode = null;
        CodeSource codeSource2 = codeSource;
        URL location = codeSource.getLocation();
        if (location != null) {
            if (location.getProtocol().equals("jar")) {
                final String file = location.getFile();
                final int index = file.indexOf("!/");
                if (index != -1) {
                    try {
                        location = new URL(file.substring(0, index));
                    }
                    catch (final MalformedURLException ex) {}
                }
            }
            if (location.getProtocol().equals("file")) {
                final String host = location.getHost();
                if (host == null || host.equals("") || host.equals("~") || host.equalsIgnoreCase("localhost")) {
                    decode = ParseUtil.decode(location.getFile().replace('/', File.separatorChar));
                }
            }
        }
        if (decode != null) {
            try {
                final URL fileToEncodedURL = ParseUtil.fileToEncodedURL(new File(canonPath(decode)));
                if (b) {
                    codeSource2 = new CodeSource(fileToEncodedURL, this.getSignerCertificates(codeSource));
                }
                else {
                    codeSource2 = new CodeSource(fileToEncodedURL, codeSource.getCertificates());
                }
            }
            catch (final IOException ex2) {
                if (b) {
                    codeSource2 = new CodeSource(codeSource.getLocation(), this.getSignerCertificates(codeSource));
                }
            }
        }
        else if (b) {
            codeSource2 = new CodeSource(codeSource.getLocation(), this.getSignerCertificates(codeSource));
        }
        return codeSource2;
    }
    
    private static String canonPath(String s) throws IOException {
        if (s.endsWith("*")) {
            s = s.substring(0, s.length() - 1) + "-";
            s = new File(s).getCanonicalPath();
            return s.substring(0, s.length() - 1) + "*";
        }
        return new File(s).getCanonicalPath();
    }
    
    private String printPD(final ProtectionDomain protectionDomain) {
        final Principal[] principals = protectionDomain.getPrincipals();
        String string = "<no principals>";
        if (principals != null && principals.length > 0) {
            final StringBuilder sb = new StringBuilder("(principals ");
            for (int i = 0; i < principals.length; ++i) {
                sb.append(principals[i].getClass().getName() + " \"" + principals[i].getName() + "\"");
                if (i < principals.length - 1) {
                    sb.append(", ");
                }
                else {
                    sb.append(")");
                }
            }
            string = sb.toString();
        }
        return "PD CodeSource: " + protectionDomain.getCodeSource() + "\n\tPD ClassLoader: " + protectionDomain.getClassLoader() + "\n\tPD Principals: " + string;
    }
    
    private boolean replacePrincipals(final List<PolicyParser.PrincipalEntry> list, final KeyStore keyStore) {
        if (list == null || list.isEmpty() || keyStore == null) {
            return true;
        }
        for (final PolicyParser.PrincipalEntry principalEntry : list) {
            if (principalEntry.isReplaceName()) {
                final String dn;
                if ((dn = this.getDN(principalEntry.principalName, keyStore)) == null) {
                    return false;
                }
                if (PolicyFile.debug != null) {
                    PolicyFile.debug.println("  Replacing \"" + principalEntry.principalName + "\" with " + "javax.security.auth.x500.X500Principal" + "/\"" + dn + "\"");
                }
                principalEntry.principalClass = "javax.security.auth.x500.X500Principal";
                principalEntry.principalName = dn;
            }
        }
        return true;
    }
    
    private void expandPermissionName(final PolicyParser.PermissionEntry permissionEntry, final KeyStore keyStore) throws Exception {
        if (permissionEntry.name == null || permissionEntry.name.indexOf("${{", 0) == -1) {
            return;
        }
        int n = 0;
        final StringBuilder sb = new StringBuilder();
        int index;
        while ((index = permissionEntry.name.indexOf("${{", n)) != -1) {
            final int index2 = permissionEntry.name.indexOf("}}", index);
            if (index2 < 1) {
                break;
            }
            sb.append(permissionEntry.name.substring(n, index));
            String s2;
            final String s = s2 = permissionEntry.name.substring(index + 3, index2);
            final int index3;
            if ((index3 = s.indexOf(":")) != -1) {
                s2 = s.substring(0, index3);
            }
            if (s2.equalsIgnoreCase("self")) {
                sb.append(permissionEntry.name.substring(index, index2 + 2));
                n = index2 + 2;
            }
            else {
                if (!s2.equalsIgnoreCase("alias")) {
                    throw new Exception(new MessageFormat(ResourcesMgr.getString("substitution.value.prefix.unsupported")).format(new Object[] { s2 }));
                }
                if (index3 == -1) {
                    throw new Exception(new MessageFormat(ResourcesMgr.getString("alias.name.not.provided.pe.name.")).format(new Object[] { permissionEntry.name }));
                }
                final String dn;
                if ((dn = this.getDN(s.substring(index3 + 1), keyStore)) == null) {
                    throw new Exception(new MessageFormat(ResourcesMgr.getString("unable.to.perform.substitution.on.alias.suffix")).format(new Object[] { s.substring(index3 + 1) }));
                }
                sb.append("javax.security.auth.x500.X500Principal \"" + dn + "\"");
                n = index2 + 2;
            }
        }
        sb.append(permissionEntry.name.substring(n));
        if (PolicyFile.debug != null) {
            PolicyFile.debug.println("  Permission name expanded from:\n\t" + permissionEntry.name + "\nto\n\t" + sb.toString());
        }
        permissionEntry.name = sb.toString();
    }
    
    private String getDN(final String s, final KeyStore keyStore) {
        Certificate certificate;
        try {
            certificate = keyStore.getCertificate(s);
        }
        catch (final Exception ex) {
            if (PolicyFile.debug != null) {
                PolicyFile.debug.println("  Error retrieving certificate for '" + s + "': " + ex.toString());
            }
            return null;
        }
        if (certificate == null || !(certificate instanceof X509Certificate)) {
            if (PolicyFile.debug != null) {
                PolicyFile.debug.println("  -- No certificate for '" + s + "' - ignoring entry");
            }
            return null;
        }
        return new X500Principal(((X509Certificate)certificate).getSubjectX500Principal().toString()).getName();
    }
    
    private boolean checkForTrustedIdentity(final Certificate certificate, final PolicyInfo policyInfo) {
        return false;
    }
    
    static {
        debug = Debug.getInstance("policy");
        PARAMS0 = new Class[0];
        PARAMS1 = new Class[] { String.class };
        PARAMS2 = new Class[] { String.class, String.class };
    }
    
    private static class PolicyEntry
    {
        private final CodeSource codesource;
        final List<Permission> permissions;
        private final List<PolicyParser.PrincipalEntry> principals;
        
        PolicyEntry(final CodeSource codesource, final List<PolicyParser.PrincipalEntry> principals) {
            this.codesource = codesource;
            this.permissions = new ArrayList<Permission>();
            this.principals = principals;
        }
        
        PolicyEntry(final CodeSource codeSource) {
            this(codeSource, null);
        }
        
        List<PolicyParser.PrincipalEntry> getPrincipals() {
            return this.principals;
        }
        
        void add(final Permission permission) {
            this.permissions.add(permission);
        }
        
        CodeSource getCodeSource() {
            return this.codesource;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(ResourcesMgr.getString("LPARAM"));
            sb.append(this.getCodeSource());
            sb.append("\n");
            for (int i = 0; i < this.permissions.size(); ++i) {
                final Permission permission = this.permissions.get(i);
                sb.append(ResourcesMgr.getString("SPACE"));
                sb.append(ResourcesMgr.getString("SPACE"));
                sb.append(permission);
                sb.append(ResourcesMgr.getString("NEWLINE"));
            }
            sb.append(ResourcesMgr.getString("RPARAM"));
            sb.append(ResourcesMgr.getString("NEWLINE"));
            return sb.toString();
        }
    }
    
    private static class SelfPermission extends Permission
    {
        private static final long serialVersionUID = -8315562579967246806L;
        private String type;
        private String name;
        private String actions;
        private Certificate[] certs;
        
        public SelfPermission(final String type, final String name, final String actions, final Certificate[] array) {
            super(type);
            if (type == null) {
                throw new NullPointerException(ResourcesMgr.getString("type.can.t.be.null"));
            }
            this.type = type;
            this.name = name;
            this.actions = actions;
            if (array != null) {
                for (int i = 0; i < array.length; ++i) {
                    if (!(array[i] instanceof X509Certificate)) {
                        this.certs = array.clone();
                        break;
                    }
                }
                if (this.certs == null) {
                    int j = 0;
                    int n = 0;
                    while (j < array.length) {
                        ++n;
                        while (j + 1 < array.length && ((X509Certificate)array[j]).getIssuerDN().equals(((X509Certificate)array[j + 1]).getSubjectDN())) {
                            ++j;
                        }
                        ++j;
                    }
                    if (n == array.length) {
                        this.certs = array.clone();
                    }
                    if (this.certs == null) {
                        final ArrayList list = new ArrayList();
                        for (int k = 0; k < array.length; ++k) {
                            list.add(array[k]);
                            while (k + 1 < array.length && ((X509Certificate)array[k]).getIssuerDN().equals(((X509Certificate)array[k + 1]).getSubjectDN())) {
                                ++k;
                            }
                        }
                        list.toArray(this.certs = new Certificate[list.size()]);
                    }
                }
            }
        }
        
        @Override
        public boolean implies(final Permission permission) {
            return false;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof SelfPermission)) {
                return false;
            }
            final SelfPermission selfPermission = (SelfPermission)o;
            if (!this.type.equals(selfPermission.type) || !this.name.equals(selfPermission.name) || !this.actions.equals(selfPermission.actions)) {
                return false;
            }
            if (this.certs.length != selfPermission.certs.length) {
                return false;
            }
            for (int i = 0; i < this.certs.length; ++i) {
                boolean b = false;
                for (int j = 0; j < selfPermission.certs.length; ++j) {
                    if (this.certs[i].equals(selfPermission.certs[j])) {
                        b = true;
                        break;
                    }
                }
                if (!b) {
                    return false;
                }
            }
            for (int k = 0; k < selfPermission.certs.length; ++k) {
                boolean b2 = false;
                for (int l = 0; l < this.certs.length; ++l) {
                    if (selfPermission.certs[k].equals(this.certs[l])) {
                        b2 = true;
                        break;
                    }
                }
                if (!b2) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = this.type.hashCode();
            if (this.name != null) {
                hashCode ^= this.name.hashCode();
            }
            if (this.actions != null) {
                hashCode ^= this.actions.hashCode();
            }
            return hashCode;
        }
        
        @Override
        public String getActions() {
            return "";
        }
        
        public String getSelfType() {
            return this.type;
        }
        
        public String getSelfName() {
            return this.name;
        }
        
        public String getSelfActions() {
            return this.actions;
        }
        
        public Certificate[] getCerts() {
            return this.certs;
        }
        
        @Override
        public String toString() {
            return "(SelfPermission " + this.type + " " + this.name + " " + this.actions + ")";
        }
    }
    
    private static class PolicyInfo
    {
        private static final boolean verbose = false;
        final List<PolicyEntry> policyEntries;
        final List<PolicyEntry> identityPolicyEntries;
        final Map<Object, Object> aliasMapping;
        private final JavaSecurityProtectionDomainAccess.ProtectionDomainCache[] pdMapping;
        private Random random;
        
        PolicyInfo(final int n) {
            this.policyEntries = new ArrayList<PolicyEntry>();
            this.identityPolicyEntries = Collections.synchronizedList(new ArrayList<PolicyEntry>(2));
            this.aliasMapping = Collections.synchronizedMap(new HashMap<Object, Object>(11));
            this.pdMapping = new JavaSecurityProtectionDomainAccess.ProtectionDomainCache[n];
            final JavaSecurityProtectionDomainAccess javaSecurityProtectionDomainAccess = SharedSecrets.getJavaSecurityProtectionDomainAccess();
            for (int i = 0; i < n; ++i) {
                this.pdMapping[i] = javaSecurityProtectionDomainAccess.getProtectionDomainCache();
            }
            if (n > 1) {
                this.random = new Random();
            }
        }
        
        JavaSecurityProtectionDomainAccess.ProtectionDomainCache getPdMapping() {
            if (this.pdMapping.length == 1) {
                return this.pdMapping[0];
            }
            return this.pdMapping[Math.abs(this.random.nextInt() % this.pdMapping.length)];
        }
    }
}
