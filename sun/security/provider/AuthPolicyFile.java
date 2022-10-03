package sun.security.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.security.cert.X509Certificate;
import java.util.Set;
import java.security.Principal;
import javax.security.auth.PrivateCredentialPermission;
import java.security.AllPermission;
import java.security.Permissions;
import java.util.LinkedList;
import java.security.PermissionCollection;
import java.security.KeyStoreException;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.security.UnresolvedPermission;
import javax.security.auth.Subject;
import java.security.CodeSource;
import java.util.Enumeration;
import java.io.Reader;
import java.io.InputStreamReader;
import java.security.cert.Certificate;
import java.io.File;
import sun.security.util.PropertyExpander;
import java.security.Security;
import java.io.InputStream;
import java.io.BufferedInputStream;
import sun.security.util.PolicyUtil;
import java.net.MalformedURLException;
import java.security.KeyStore;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Permission;
import javax.security.auth.AuthPermission;
import java.util.Hashtable;
import java.util.Vector;
import sun.security.util.Debug;
import java.util.ResourceBundle;
import javax.security.auth.Policy;

@Deprecated
public class AuthPolicyFile extends Policy
{
    static final ResourceBundle rb;
    private static final Debug debug;
    private static final String AUTH_POLICY = "java.security.auth.policy";
    private static final String SECURITY_MANAGER = "java.security.manager";
    private static final String AUTH_POLICY_URL = "auth.policy.url.";
    private Vector<PolicyEntry> policyEntries;
    private Hashtable<Object, Object> aliasMapping;
    private boolean initialized;
    private boolean expandProperties;
    private boolean ignoreIdentityScope;
    private static final Class<?>[] PARAMS;
    
    public AuthPolicyFile() {
        this.initialized = false;
        this.expandProperties = true;
        this.ignoreIdentityScope = true;
        String s = System.getProperty("java.security.auth.policy");
        if (s == null) {
            s = System.getProperty("java.security.manager");
        }
        if (s != null) {
            this.init();
        }
    }
    
    private synchronized void init() {
        if (this.initialized) {
            return;
        }
        this.policyEntries = new Vector<PolicyEntry>();
        this.aliasMapping = new Hashtable<Object, Object>(11);
        this.initPolicyFile();
        this.initialized = true;
    }
    
    @Override
    public synchronized void refresh() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new AuthPermission("refreshPolicy"));
        }
        this.initialized = false;
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                AuthPolicyFile.this.init();
                return null;
            }
        });
    }
    
    private KeyStore initKeyStore(final URL url, final String s, final String s2) {
        if (s != null) {
            try {
                URL url2;
                try {
                    url2 = new URL(s);
                }
                catch (final MalformedURLException ex) {
                    url2 = new URL(url, s);
                }
                if (AuthPolicyFile.debug != null) {
                    AuthPolicyFile.debug.println("reading keystore" + url2);
                }
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(PolicyUtil.getInputStream(url2));
                KeyStore keyStore;
                if (s2 != null) {
                    keyStore = KeyStore.getInstance(s2);
                }
                else {
                    keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                }
                keyStore.load(bufferedInputStream, null);
                bufferedInputStream.close();
                return keyStore;
            }
            catch (final Exception ex2) {
                if (AuthPolicyFile.debug != null) {
                    AuthPolicyFile.debug.println("Debug info only. No keystore.");
                    ex2.printStackTrace();
                }
                return null;
            }
        }
        return null;
    }
    
    private void initPolicyFile() {
        final String property = Security.getProperty("policy.expandProperties");
        if (property != null) {
            this.expandProperties = property.equalsIgnoreCase("true");
        }
        final String property2 = Security.getProperty("policy.ignoreIdentityScope");
        if (property2 != null) {
            this.ignoreIdentityScope = property2.equalsIgnoreCase("true");
        }
        final String property3 = Security.getProperty("policy.allowSystemProperty");
        if (property3 != null && property3.equalsIgnoreCase("true")) {
            String s = System.getProperty("java.security.auth.policy");
            if (s != null) {
                boolean b = false;
                if (s.startsWith("=")) {
                    b = true;
                    s = s.substring(1);
                }
                try {
                    final String expand = PropertyExpander.expand(s);
                    final File file = new File(expand);
                    URL url;
                    if (file.exists()) {
                        url = new URL("file:" + file.getCanonicalPath());
                    }
                    else {
                        url = new URL(expand);
                    }
                    if (AuthPolicyFile.debug != null) {
                        AuthPolicyFile.debug.println("reading " + url);
                    }
                    this.init(url);
                }
                catch (final Exception ex) {
                    if (AuthPolicyFile.debug != null) {
                        AuthPolicyFile.debug.println("caught exception: " + ex);
                    }
                }
                if (b) {
                    if (AuthPolicyFile.debug != null) {
                        AuthPolicyFile.debug.println("overriding other policies!");
                    }
                    return;
                }
            }
        }
        int n = 1;
        boolean b2 = false;
        String property4;
        while ((property4 = Security.getProperty("auth.policy.url." + n)) != null) {
            try {
                final String replace = PropertyExpander.expand(property4).replace(File.separatorChar, '/');
                if (AuthPolicyFile.debug != null) {
                    AuthPolicyFile.debug.println("reading " + replace);
                }
                this.init(new URL(replace));
                b2 = true;
            }
            catch (final Exception ex2) {
                if (AuthPolicyFile.debug != null) {
                    AuthPolicyFile.debug.println("Debug info only. Error reading policy " + ex2);
                    ex2.printStackTrace();
                }
            }
            ++n;
        }
        if (!b2) {}
    }
    
    private boolean checkForTrustedIdentity(final Certificate certificate) {
        return false;
    }
    
    private void init(final URL url) {
        final PolicyParser policyParser = new PolicyParser(this.expandProperties);
        try (final InputStreamReader inputStreamReader = new InputStreamReader(PolicyUtil.getInputStream(url))) {
            policyParser.read(inputStreamReader);
            final KeyStore initKeyStore = this.initKeyStore(url, policyParser.getKeyStoreUrl(), policyParser.getKeyStoreType());
            final Enumeration<PolicyParser.GrantEntry> grantElements = policyParser.grantElements();
            while (grantElements.hasMoreElements()) {
                this.addGrantEntry(grantElements.nextElement(), initKeyStore);
            }
        }
        catch (final PolicyParser.ParsingException ex) {
            System.err.println("java.security.auth.policy" + AuthPolicyFile.rb.getString(".error.parsing.") + url);
            System.err.println("java.security.auth.policy" + AuthPolicyFile.rb.getString("COLON") + ex.getMessage());
            if (AuthPolicyFile.debug != null) {
                ex.printStackTrace();
            }
        }
        catch (final Exception ex2) {
            if (AuthPolicyFile.debug != null) {
                AuthPolicyFile.debug.println("error parsing " + url);
                AuthPolicyFile.debug.println(ex2.toString());
                ex2.printStackTrace();
            }
        }
    }
    
    CodeSource getCodeSource(final PolicyParser.GrantEntry grantEntry, final KeyStore keyStore) throws MalformedURLException {
        Certificate[] certificates = null;
        if (grantEntry.signedBy != null) {
            certificates = this.getCertificates(keyStore, grantEntry.signedBy);
            if (certificates == null) {
                if (AuthPolicyFile.debug != null) {
                    AuthPolicyFile.debug.println(" no certs for alias " + grantEntry.signedBy + ", ignoring.");
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
        if (grantEntry.principals == null || grantEntry.principals.size() == 0) {
            return this.canonicalizeCodebase(new CodeSource(url, certificates), false);
        }
        return this.canonicalizeCodebase(new SubjectCodeSource(null, grantEntry.principals, url, certificates), false);
    }
    
    private void addGrantEntry(final PolicyParser.GrantEntry grantEntry, final KeyStore keyStore) {
        if (AuthPolicyFile.debug != null) {
            AuthPolicyFile.debug.println("Adding policy entry: ");
            AuthPolicyFile.debug.println("  signedBy " + grantEntry.signedBy);
            AuthPolicyFile.debug.println("  codeBase " + grantEntry.codeBase);
            if (grantEntry.principals != null) {
                for (final PolicyParser.PrincipalEntry principalEntry : grantEntry.principals) {
                    AuthPolicyFile.debug.println("  " + principalEntry.getPrincipalClass() + " " + principalEntry.getPrincipalName());
                }
            }
            AuthPolicyFile.debug.println();
        }
        try {
            final CodeSource codeSource = this.getCodeSource(grantEntry, keyStore);
            if (codeSource == null) {
                return;
            }
            final PolicyEntry policyEntry = new PolicyEntry(codeSource);
            final Enumeration<PolicyParser.PermissionEntry> permissionElements = grantEntry.permissionElements();
            while (permissionElements.hasMoreElements()) {
                final PolicyParser.PermissionEntry permissionEntry = permissionElements.nextElement();
                try {
                    Permission permission;
                    if (permissionEntry.permission.equals("javax.security.auth.PrivateCredentialPermission") && permissionEntry.name.endsWith(" self")) {
                        permission = getInstance(permissionEntry.permission, permissionEntry.name + " \"self\"", permissionEntry.action);
                    }
                    else {
                        permission = getInstance(permissionEntry.permission, permissionEntry.name, permissionEntry.action);
                    }
                    policyEntry.add(permission);
                    if (AuthPolicyFile.debug == null) {
                        continue;
                    }
                    AuthPolicyFile.debug.println("  " + permission);
                }
                catch (final ClassNotFoundException ex) {
                    Certificate[] certificates;
                    if (permissionEntry.signedBy != null) {
                        certificates = this.getCertificates(keyStore, permissionEntry.signedBy);
                    }
                    else {
                        certificates = null;
                    }
                    if (certificates == null && permissionEntry.signedBy != null) {
                        continue;
                    }
                    final UnresolvedPermission unresolvedPermission = new UnresolvedPermission(permissionEntry.permission, permissionEntry.name, permissionEntry.action, certificates);
                    policyEntry.add(unresolvedPermission);
                    if (AuthPolicyFile.debug == null) {
                        continue;
                    }
                    AuthPolicyFile.debug.println("  " + unresolvedPermission);
                }
                catch (final InvocationTargetException ex2) {
                    System.err.println("java.security.auth.policy" + AuthPolicyFile.rb.getString(".error.adding.Permission.") + permissionEntry.permission + AuthPolicyFile.rb.getString("SPACE") + ex2.getTargetException());
                }
                catch (final Exception ex3) {
                    System.err.println("java.security.auth.policy" + AuthPolicyFile.rb.getString(".error.adding.Permission.") + permissionEntry.permission + AuthPolicyFile.rb.getString("SPACE") + ex3);
                }
            }
            this.policyEntries.addElement(policyEntry);
        }
        catch (final Exception ex4) {
            System.err.println("java.security.auth.policy" + AuthPolicyFile.rb.getString(".error.adding.Entry.") + grantEntry + AuthPolicyFile.rb.getString("SPACE") + ex4);
        }
        if (AuthPolicyFile.debug != null) {
            AuthPolicyFile.debug.println();
        }
    }
    
    private static final Permission getInstance(final String s, final String s2, final String s3) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return (Permission)Class.forName(s).getConstructor(AuthPolicyFile.PARAMS).newInstance(s2, s3);
    }
    
    Certificate[] getCertificates(final KeyStore keyStore, final String s) {
        Vector<Certificate> vector = null;
        final StringTokenizer stringTokenizer = new StringTokenizer(s, ",");
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            final String trim = stringTokenizer.nextToken().trim();
            ++n;
            Certificate certificate = this.aliasMapping.get(trim);
            if (certificate == null && keyStore != null) {
                try {
                    certificate = keyStore.getCertificate(trim);
                }
                catch (final KeyStoreException ex) {}
                if (certificate != null) {
                    this.aliasMapping.put(trim, certificate);
                    this.aliasMapping.put(certificate, trim);
                }
            }
            if (certificate != null) {
                if (vector == null) {
                    vector = new Vector<Certificate>();
                }
                vector.addElement(certificate);
            }
        }
        if (vector != null && n == vector.size()) {
            final Certificate[] array = new Certificate[vector.size()];
            vector.copyInto(array);
            return array;
        }
        return null;
    }
    
    private final synchronized Enumeration<PolicyEntry> elements() {
        return this.policyEntries.elements();
    }
    
    @Override
    public PermissionCollection getPermissions(final Subject subject, final CodeSource codeSource) {
        return AccessController.doPrivileged((PrivilegedAction<PermissionCollection>)new PrivilegedAction<PermissionCollection>() {
            @Override
            public PermissionCollection run() {
                final SubjectCodeSource subjectCodeSource = new SubjectCodeSource(subject, null, (codeSource == null) ? null : codeSource.getLocation(), (Certificate[])((codeSource == null) ? null : codeSource.getCertificates()));
                if (AuthPolicyFile.this.initialized) {
                    return AuthPolicyFile.this.getPermissions(new Permissions(), subjectCodeSource);
                }
                return new PolicyPermissions(AuthPolicyFile.this, subjectCodeSource);
            }
        });
    }
    
    PermissionCollection getPermissions(final CodeSource codeSource) {
        if (this.initialized) {
            return this.getPermissions(new Permissions(), codeSource);
        }
        return new PolicyPermissions(this, codeSource);
    }
    
    Permissions getPermissions(final Permissions permissions, final CodeSource codeSource) {
        if (!this.initialized) {
            this.init();
        }
        final CodeSource[] array = { null };
        array[0] = this.canonicalizeCodebase(codeSource, true);
        if (AuthPolicyFile.debug != null) {
            AuthPolicyFile.debug.println("evaluate(" + array[0] + ")\n");
        }
        for (int i = 0; i < this.policyEntries.size(); ++i) {
            final PolicyEntry policyEntry = this.policyEntries.elementAt(i);
            if (AuthPolicyFile.debug != null) {
                AuthPolicyFile.debug.println("PolicyFile CodeSource implies: " + policyEntry.codesource.toString() + "\n\n\t" + array[0].toString() + "\n\n");
            }
            if (policyEntry.codesource.implies(array[0])) {
                for (int j = 0; j < policyEntry.permissions.size(); ++j) {
                    final Permission permission = policyEntry.permissions.elementAt(j);
                    if (AuthPolicyFile.debug != null) {
                        AuthPolicyFile.debug.println("  granting " + permission);
                    }
                    if (!this.addSelfPermissions(permission, policyEntry.codesource, array[0], permissions)) {
                        permissions.add(permission);
                    }
                }
            }
        }
        if (!this.ignoreIdentityScope) {
            final Certificate[] certificates = array[0].getCertificates();
            if (certificates != null) {
                for (int k = 0; k < certificates.length; ++k) {
                    if (this.aliasMapping.get(certificates[k]) == null && this.checkForTrustedIdentity(certificates[k])) {
                        permissions.add(new AllPermission());
                    }
                }
            }
        }
        return permissions;
    }
    
    private boolean addSelfPermissions(final Permission permission, final CodeSource codeSource, final CodeSource codeSource2, final Permissions permissions) {
        if (!(permission instanceof PrivateCredentialPermission)) {
            return false;
        }
        if (!(codeSource instanceof SubjectCodeSource)) {
            return false;
        }
        final PrivateCredentialPermission privateCredentialPermission = (PrivateCredentialPermission)permission;
        final SubjectCodeSource subjectCodeSource = (SubjectCodeSource)codeSource;
        final String[][] principals = privateCredentialPermission.getPrincipals();
        if (principals.length <= 0 || !principals[0][0].equalsIgnoreCase("self") || !principals[0][1].equalsIgnoreCase("self")) {
            return false;
        }
        if (subjectCodeSource.getPrincipals() == null) {
            return true;
        }
        final Iterator<Object> iterator = subjectCodeSource.getPrincipals().iterator();
        while (iterator.hasNext()) {
            final String[][] principalInfo = this.getPrincipalInfo(iterator.next(), codeSource2);
            for (int i = 0; i < principalInfo.length; ++i) {
                final PrivateCredentialPermission privateCredentialPermission2 = new PrivateCredentialPermission(privateCredentialPermission.getCredentialClass() + " " + principalInfo[i][0] + " \"" + principalInfo[i][1] + "\"", "read");
                if (AuthPolicyFile.debug != null) {
                    AuthPolicyFile.debug.println("adding SELF permission: " + privateCredentialPermission2.toString());
                }
                permissions.add(privateCredentialPermission2);
            }
        }
        return true;
    }
    
    private String[][] getPrincipalInfo(final PolicyParser.PrincipalEntry principalEntry, final CodeSource codeSource) {
        if (!principalEntry.getPrincipalClass().equals("WILDCARD_PRINCIPAL_CLASS") && !principalEntry.getPrincipalName().equals("WILDCARD_PRINCIPAL_NAME")) {
            final String[][] array = new String[1][2];
            array[0][0] = principalEntry.getPrincipalClass();
            array[0][1] = principalEntry.getPrincipalName();
            return array;
        }
        if (principalEntry.getPrincipalClass().equals("WILDCARD_PRINCIPAL_CLASS") || !principalEntry.getPrincipalName().equals("WILDCARD_PRINCIPAL_NAME")) {
            final Set<Principal> principals = ((SubjectCodeSource)codeSource).getSubject().getPrincipals();
            final String[][] array2 = new String[principals.size()][2];
            int n = 0;
            for (final Principal principal : principals) {
                array2[n][0] = principal.getClass().getName();
                array2[n][1] = principal.getName();
                ++n;
            }
            return array2;
        }
        final SubjectCodeSource subjectCodeSource = (SubjectCodeSource)codeSource;
        Set<Principal> principals2 = null;
        try {
            principals2 = subjectCodeSource.getSubject().getPrincipals(Class.forName(principalEntry.getPrincipalClass(), false, ClassLoader.getSystemClassLoader()));
        }
        catch (final Exception ex) {
            if (AuthPolicyFile.debug != null) {
                AuthPolicyFile.debug.println("problem finding Principal Class when expanding SELF permission: " + ex.toString());
            }
        }
        if (principals2 == null) {
            return new String[0][0];
        }
        final String[][] array3 = new String[principals2.size()][2];
        int n2 = 0;
        for (final Principal principal2 : principals2) {
            array3[n2][0] = principal2.getClass().getName();
            array3[n2][1] = principal2.getName();
            ++n2;
        }
        return array3;
    }
    
    Certificate[] getSignerCertificates(final CodeSource codeSource) {
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
        CodeSource codeSource2 = codeSource;
        if (codeSource.getLocation() != null && codeSource.getLocation().getProtocol().equalsIgnoreCase("file")) {
            try {
                final String replace = codeSource.getLocation().getFile().replace('/', File.separatorChar);
                String s2;
                if (replace.endsWith("*")) {
                    String s = replace.substring(0, replace.length() - 1);
                    boolean b2 = false;
                    if (s.endsWith(File.separator)) {
                        b2 = true;
                    }
                    if (s.equals("")) {
                        s = System.getProperty("user.dir");
                    }
                    final File file = new File(s);
                    final String canonicalPath = file.getCanonicalPath();
                    final StringBuffer sb = new StringBuffer(canonicalPath);
                    if (!canonicalPath.endsWith(File.separator) && (b2 || file.isDirectory())) {
                        sb.append(File.separatorChar);
                    }
                    sb.append('*');
                    s2 = sb.toString();
                }
                else {
                    s2 = new File(replace).getCanonicalPath();
                }
                final URL url = new File(s2).toURL();
                if (codeSource instanceof SubjectCodeSource) {
                    final SubjectCodeSource subjectCodeSource = (SubjectCodeSource)codeSource;
                    if (b) {
                        codeSource2 = new SubjectCodeSource(subjectCodeSource.getSubject(), subjectCodeSource.getPrincipals(), url, this.getSignerCertificates(subjectCodeSource));
                    }
                    else {
                        codeSource2 = new SubjectCodeSource(subjectCodeSource.getSubject(), subjectCodeSource.getPrincipals(), url, subjectCodeSource.getCertificates());
                    }
                }
                else if (b) {
                    codeSource2 = new CodeSource(url, this.getSignerCertificates(codeSource));
                }
                else {
                    codeSource2 = new CodeSource(url, codeSource.getCertificates());
                }
            }
            catch (final IOException ex) {
                if (b) {
                    if (!(codeSource instanceof SubjectCodeSource)) {
                        codeSource2 = new CodeSource(codeSource.getLocation(), this.getSignerCertificates(codeSource));
                    }
                    else {
                        final SubjectCodeSource subjectCodeSource2 = (SubjectCodeSource)codeSource;
                        codeSource2 = new SubjectCodeSource(subjectCodeSource2.getSubject(), subjectCodeSource2.getPrincipals(), subjectCodeSource2.getLocation(), this.getSignerCertificates(subjectCodeSource2));
                    }
                }
            }
        }
        else if (b) {
            if (!(codeSource instanceof SubjectCodeSource)) {
                codeSource2 = new CodeSource(codeSource.getLocation(), this.getSignerCertificates(codeSource));
            }
            else {
                final SubjectCodeSource subjectCodeSource3 = (SubjectCodeSource)codeSource;
                codeSource2 = new SubjectCodeSource(subjectCodeSource3.getSubject(), subjectCodeSource3.getPrincipals(), subjectCodeSource3.getLocation(), this.getSignerCertificates(subjectCodeSource3));
            }
        }
        return codeSource2;
    }
    
    static {
        rb = AccessController.doPrivileged((PrivilegedAction<ResourceBundle>)new PrivilegedAction<ResourceBundle>() {
            @Override
            public ResourceBundle run() {
                return ResourceBundle.getBundle("sun.security.util.AuthResources");
            }
        });
        debug = Debug.getInstance("policy", "\t[Auth Policy]");
        PARAMS = new Class[] { String.class, String.class };
    }
    
    private static class PolicyEntry
    {
        CodeSource codesource;
        Vector<Permission> permissions;
        
        PolicyEntry(final CodeSource codesource) {
            this.codesource = codesource;
            this.permissions = new Vector<Permission>();
        }
        
        void add(final Permission permission) {
            this.permissions.addElement(permission);
        }
        
        CodeSource getCodeSource() {
            return this.codesource;
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append(AuthPolicyFile.rb.getString("LPARAM"));
            sb.append(this.getCodeSource());
            sb.append("\n");
            for (int i = 0; i < this.permissions.size(); ++i) {
                final Permission permission = this.permissions.elementAt(i);
                sb.append(AuthPolicyFile.rb.getString("SPACE"));
                sb.append(AuthPolicyFile.rb.getString("SPACE"));
                sb.append(permission);
                sb.append(AuthPolicyFile.rb.getString("NEWLINE"));
            }
            sb.append(AuthPolicyFile.rb.getString("RPARAM"));
            sb.append(AuthPolicyFile.rb.getString("NEWLINE"));
            return sb.toString();
        }
    }
}
