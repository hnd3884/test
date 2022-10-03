package sun.security.krb5.internal.ccache;

import sun.security.krb5.internal.Krb5;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import sun.security.action.GetPropertyAction;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.nio.charset.StandardCharsets;
import sun.security.util.SecurityProperties;
import java.util.Collections;
import sun.security.krb5.internal.LoginOptions;
import sun.security.krb5.Asn1Exception;
import java.util.Iterator;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import sun.security.krb5.KrbException;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.Vector;
import sun.security.krb5.PrincipalName;

public class FileCredentialsCache extends CredentialsCache implements FileCCacheConstants
{
    public int version;
    public Tag tag;
    public PrincipalName primaryPrincipal;
    private Vector<Credentials> credentialsList;
    private static String dir;
    private static boolean DEBUG;
    private List<ConfigEntry> configEntries;
    
    public static synchronized FileCredentialsCache acquireInstance(final PrincipalName primaryPrincipal, final String s) {
        try {
            final FileCredentialsCache fileCredentialsCache = new FileCredentialsCache();
            if (s == null) {
                FileCredentialsCache.cacheName = getDefaultCacheName();
            }
            else {
                FileCredentialsCache.cacheName = checkValidation(s);
            }
            if (FileCredentialsCache.cacheName == null || !new File(FileCredentialsCache.cacheName).exists()) {
                return null;
            }
            if (primaryPrincipal != null) {
                fileCredentialsCache.primaryPrincipal = primaryPrincipal;
            }
            fileCredentialsCache.load(FileCredentialsCache.cacheName);
            return fileCredentialsCache;
        }
        catch (final IOException ex) {
            if (FileCredentialsCache.DEBUG) {
                ex.printStackTrace();
            }
        }
        catch (final KrbException ex2) {
            if (FileCredentialsCache.DEBUG) {
                ex2.printStackTrace();
            }
        }
        return null;
    }
    
    public static FileCredentialsCache acquireInstance() {
        return acquireInstance(null, null);
    }
    
    static synchronized FileCredentialsCache New(final PrincipalName principalName, final String s) {
        try {
            final FileCredentialsCache fileCredentialsCache = new FileCredentialsCache();
            FileCredentialsCache.cacheName = checkValidation(s);
            if (FileCredentialsCache.cacheName == null) {
                return null;
            }
            fileCredentialsCache.init(principalName, FileCredentialsCache.cacheName);
            return fileCredentialsCache;
        }
        catch (final IOException ex) {}
        catch (final KrbException ex2) {}
        return null;
    }
    
    static synchronized FileCredentialsCache New(final PrincipalName principalName) {
        try {
            final FileCredentialsCache fileCredentialsCache = new FileCredentialsCache();
            fileCredentialsCache.init(principalName, FileCredentialsCache.cacheName = getDefaultCacheName());
            return fileCredentialsCache;
        }
        catch (final IOException ex) {
            if (FileCredentialsCache.DEBUG) {
                ex.printStackTrace();
            }
        }
        catch (final KrbException ex2) {
            if (FileCredentialsCache.DEBUG) {
                ex2.printStackTrace();
            }
        }
        return null;
    }
    
    private FileCredentialsCache() {
        this.configEntries = new ArrayList<ConfigEntry>();
    }
    
    boolean exists(final String s) {
        return new File(s).exists();
    }
    
    synchronized void init(final PrincipalName primaryPrincipal, final String s) throws IOException, KrbException {
        this.primaryPrincipal = primaryPrincipal;
        try (final FileOutputStream fileOutputStream = new FileOutputStream(s);
             final CCacheOutputStream cCacheOutputStream = new CCacheOutputStream(fileOutputStream)) {
            this.version = 1283;
            cCacheOutputStream.writeHeader(this.primaryPrincipal, this.version);
        }
        this.load(s);
    }
    
    synchronized void load(final String s) throws IOException, KrbException {
        try (final FileInputStream fileInputStream = new FileInputStream(s);
             final CCacheInputStream cCacheInputStream = new CCacheInputStream(fileInputStream)) {
            this.version = cCacheInputStream.readVersion();
            if (this.version == 1284) {
                this.tag = cCacheInputStream.readTag();
            }
            else {
                this.tag = null;
                if (this.version == 1281 || this.version == 1282) {
                    cCacheInputStream.setNativeByteOrder();
                }
            }
            final PrincipalName principal = cCacheInputStream.readPrincipal(this.version);
            if (this.primaryPrincipal != null) {
                if (!this.primaryPrincipal.match(principal)) {
                    throw new IOException("Primary principals don't match.");
                }
            }
            else {
                this.primaryPrincipal = principal;
            }
            this.credentialsList = new Vector<Credentials>();
            while (cCacheInputStream.available() > 0) {
                final Object cred = cCacheInputStream.readCred(this.version);
                if (cred != null) {
                    if (cred instanceof Credentials) {
                        this.credentialsList.addElement((Credentials)cred);
                    }
                    else {
                        this.addConfigEntry((ConfigEntry)cred);
                    }
                }
            }
        }
    }
    
    @Override
    public synchronized void update(final Credentials credentials) {
        if (this.credentialsList != null) {
            if (this.credentialsList.isEmpty()) {
                this.credentialsList.addElement(credentials);
            }
            else {
                boolean b = false;
                for (int i = 0; i < this.credentialsList.size(); ++i) {
                    final Credentials credentials2 = this.credentialsList.elementAt(i);
                    if (this.match(credentials.sname.getNameStrings(), credentials2.sname.getNameStrings()) && credentials.sname.getRealmString().equalsIgnoreCase(credentials2.sname.getRealmString())) {
                        b = true;
                        if (credentials.endtime.getTime() >= credentials2.endtime.getTime()) {
                            if (FileCredentialsCache.DEBUG) {
                                System.out.println(" >>> FileCredentialsCache Ticket matched, overwrite the old one.");
                            }
                            this.credentialsList.removeElementAt(i);
                            this.credentialsList.addElement(credentials);
                        }
                    }
                }
                if (!b) {
                    if (FileCredentialsCache.DEBUG) {
                        System.out.println(" >>> FileCredentialsCache Ticket not exactly matched, add new one into cache.");
                    }
                    this.credentialsList.addElement(credentials);
                }
            }
        }
    }
    
    @Override
    public synchronized PrincipalName getPrimaryPrincipal() {
        return this.primaryPrincipal;
    }
    
    @Override
    public synchronized void save() throws IOException, Asn1Exception {
        try (final FileOutputStream fileOutputStream = new FileOutputStream(FileCredentialsCache.cacheName);
             final CCacheOutputStream cCacheOutputStream = new CCacheOutputStream(fileOutputStream)) {
            cCacheOutputStream.writeHeader(this.primaryPrincipal, this.version);
            final Credentials[] credsList;
            if ((credsList = this.getCredsList()) != null) {
                for (int i = 0; i < credsList.length; ++i) {
                    cCacheOutputStream.addCreds(credsList[i]);
                }
            }
            final Iterator<ConfigEntry> iterator = this.getConfigEntries().iterator();
            while (iterator.hasNext()) {
                cCacheOutputStream.addConfigEntry(this.primaryPrincipal, iterator.next());
            }
        }
    }
    
    boolean match(final String[] array, final String[] array2) {
        if (array.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (!array[i].equalsIgnoreCase(array2[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public synchronized Credentials[] getCredsList() {
        if (this.credentialsList == null || this.credentialsList.isEmpty()) {
            return null;
        }
        final Credentials[] array = new Credentials[this.credentialsList.size()];
        for (int i = 0; i < this.credentialsList.size(); ++i) {
            array[i] = this.credentialsList.elementAt(i);
        }
        return array;
    }
    
    @Override
    public Credentials getCreds(final LoginOptions loginOptions, final PrincipalName principalName) {
        if (loginOptions == null) {
            return this.getCreds(principalName);
        }
        final Credentials[] credsList = this.getCredsList();
        if (credsList == null) {
            return null;
        }
        for (int i = 0; i < credsList.length; ++i) {
            if (principalName.match(credsList[i].sname) && credsList[i].flags.match(loginOptions)) {
                return credsList[i];
            }
        }
        return null;
    }
    
    @Override
    public void addConfigEntry(final ConfigEntry configEntry) {
        this.configEntries.add(configEntry);
    }
    
    @Override
    public List<ConfigEntry> getConfigEntries() {
        return Collections.unmodifiableList((List<? extends ConfigEntry>)this.configEntries);
    }
    
    @Override
    public Credentials getCreds(final PrincipalName principalName) {
        final Credentials[] credsList = this.getCredsList();
        if (credsList == null) {
            return null;
        }
        for (int i = 0; i < credsList.length; ++i) {
            if (principalName.match(credsList[i].sname)) {
                return credsList[i];
            }
        }
        return null;
    }
    
    @Override
    public sun.security.krb5.Credentials getInitialCreds() {
        final Credentials defaultCreds = this.getDefaultCreds();
        if (defaultCreds == null) {
            return null;
        }
        final sun.security.krb5.Credentials setKrbCreds = defaultCreds.setKrbCreds();
        final ConfigEntry configEntry = this.getConfigEntry("proxy_impersonator");
        if (configEntry == null) {
            if (FileCredentialsCache.DEBUG) {
                System.out.println("get normal credential");
            }
            return setKrbCreds;
        }
        String privilegedGetOverridable = SecurityProperties.privilegedGetOverridable("jdk.security.krb5.default.initiate.credential");
        if (privilegedGetOverridable == null) {
            privilegedGetOverridable = "always-impersonate";
        }
        final String s = privilegedGetOverridable;
        boolean b = false;
        switch (s) {
            case "no-impersonate": {
                if (FileCredentialsCache.DEBUG) {
                    System.out.println("get normal credential");
                }
                return setKrbCreds;
            }
            case "try-impersonate": {
                b = false;
                break;
            }
            case "always-impersonate": {
                b = true;
                break;
            }
            default: {
                throw new RuntimeException("Invalid jdk.security.krb5.default.initiate.credential");
            }
        }
        try {
            final PrincipalName principalName = new PrincipalName(new String(configEntry.getData(), StandardCharsets.UTF_8));
            if (!setKrbCreds.getClient().equals(principalName)) {
                if (FileCredentialsCache.DEBUG) {
                    System.out.println("proxy_impersonator does not match service name");
                }
                return b ? null : setKrbCreds;
            }
            final PrincipalName primaryPrincipal = this.getPrimaryPrincipal();
            Credentials credentials = null;
            for (final Credentials credentials2 : this.getCredsList()) {
                if (credentials2.getClientPrincipal().equals(primaryPrincipal) && credentials2.getServicePrincipal().equals(principalName)) {
                    credentials = credentials2;
                    break;
                }
            }
            if (credentials == null) {
                if (FileCredentialsCache.DEBUG) {
                    System.out.println("Cannot find evidence ticket in ccache");
                }
                return b ? null : setKrbCreds;
            }
            if (FileCredentialsCache.DEBUG) {
                System.out.println("Get proxied credential");
            }
            return setKrbCreds.setProxy(credentials.setKrbCreds());
        }
        catch (final KrbException ex) {
            if (FileCredentialsCache.DEBUG) {
                System.out.println("Impersonation with ccache failed");
            }
            return b ? null : setKrbCreds;
        }
    }
    
    @Override
    public Credentials getDefaultCreds() {
        final Credentials[] credsList = this.getCredsList();
        if (credsList == null) {
            return null;
        }
        for (int i = credsList.length - 1; i >= 0; --i) {
            if (credsList[i].sname.toString().startsWith("krbtgt") && credsList[i].sname.getNameStrings()[1].equals(credsList[i].sname.getRealm().toString())) {
                return credsList[i];
            }
        }
        return null;
    }
    
    public static String getDefaultCacheName() {
        final String s = "krb5cc";
        final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                String s = System.getenv("KRB5CCNAME");
                if (s != null && s.length() >= 5 && s.regionMatches(true, 0, "FILE:", 0, 5)) {
                    s = s.substring(5);
                }
                return s;
            }
        });
        if (s2 != null) {
            if (FileCredentialsCache.DEBUG) {
                System.out.println(">>>KinitOptions cache name is " + s2);
            }
            return s2;
        }
        final String s3 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("os.name"));
        if (s3 != null && !s3.startsWith("Windows")) {
            try {
                final Class<?> forName = Class.forName("com.sun.security.auth.module.UnixSystem");
                final String string = File.separator + "tmp" + File.separator + s + "_" + (long)forName.getMethod("getUid", (Class[])new Class[0]).invoke(forName.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]), new Object[0]);
                if (FileCredentialsCache.DEBUG) {
                    System.out.println(">>>KinitOptions cache name is " + string);
                }
                return string;
            }
            catch (final Exception ex) {
                if (FileCredentialsCache.DEBUG) {
                    System.out.println("Exception in obtaining uid for Unix platforms Using user's home directory");
                    ex.printStackTrace();
                }
            }
        }
        final String s4 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("user.name"));
        String s5 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("user.home"));
        if (s5 == null) {
            s5 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("user.dir"));
        }
        String s6;
        if (s4 != null) {
            s6 = s5 + File.separator + s + "_" + s4;
        }
        else {
            s6 = s5 + File.separator + s;
        }
        if (FileCredentialsCache.DEBUG) {
            System.out.println(">>>KinitOptions cache name is " + s6);
        }
        return s6;
    }
    
    public static String checkValidation(final String s) {
        if (s == null) {
            return null;
        }
        String canonicalPath;
        try {
            canonicalPath = new File(s).getCanonicalPath();
            final File file = new File(canonicalPath);
            if (!file.exists() && !new File(file.getParent()).isDirectory()) {
                canonicalPath = null;
                goto Label_0057;
            }
        }
        catch (final IOException ex) {
            canonicalPath = null;
        }
        return canonicalPath;
    }
    
    private static String exec(final String s) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s);
        final Vector vector = new Vector();
        while (stringTokenizer.hasMoreTokens()) {
            vector.addElement(stringTokenizer.nextToken());
        }
        final String[] array = new String[vector.size()];
        vector.copyInto(array);
        try {
            final Process process = AccessController.doPrivileged((PrivilegedAction<Process>)new PrivilegedAction<Process>() {
                @Override
                public Process run() {
                    try {
                        return Runtime.getRuntime().exec(array);
                    }
                    catch (final IOException ex) {
                        if (FileCredentialsCache.DEBUG) {
                            ex.printStackTrace();
                        }
                        return null;
                    }
                }
            });
            if (process == null) {
                return null;
            }
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "8859_1"));
            String s2;
            if (array.length == 1 && array[0].equals("/usr/bin/env")) {
                while ((s2 = bufferedReader.readLine()) != null) {
                    if (s2.length() >= 11 && s2.substring(0, 11).equalsIgnoreCase("KRB5CCNAME=")) {
                        s2 = s2.substring(11);
                        break;
                    }
                }
            }
            else {
                s2 = bufferedReader.readLine();
            }
            bufferedReader.close();
            return s2;
        }
        catch (final Exception ex) {
            if (FileCredentialsCache.DEBUG) {
                ex.printStackTrace();
            }
            return null;
        }
    }
    
    static {
        FileCredentialsCache.DEBUG = Krb5.DEBUG;
    }
}
