package sun.security.tools.policytool;

import javax.swing.UIManager;
import java.awt.event.KeyEvent;
import javax.swing.SwingUtilities;
import java.lang.reflect.Constructor;
import java.security.Permission;
import javax.security.auth.x500.X500Principal;
import java.security.cert.Certificate;
import java.security.PublicKey;
import java.util.LinkedList;
import javax.security.auth.login.LoginException;
import sun.security.util.Debug;
import sun.security.util.PolicyUtil;
import java.net.URL;
import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import sun.security.util.PropertyExpander;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.cert.CertificateException;
import java.security.KeyStoreException;
import java.io.FileNotFoundException;
import java.util.ListIterator;
import java.util.Enumeration;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.io.Reader;
import java.io.FileReader;
import java.security.KeyStore;
import sun.security.provider.PolicyParser;
import java.util.Vector;
import java.text.Collator;
import java.util.ResourceBundle;

public class PolicyTool
{
    static final ResourceBundle rb;
    static final Collator collator;
    Vector<String> warnings;
    boolean newWarning;
    boolean modified;
    private static final boolean testing = false;
    private static final Class<?>[] TWOPARAMS;
    private static final Class<?>[] ONEPARAMS;
    private static final Class<?>[] NOPARAMS;
    private static String policyFileName;
    private Vector<PolicyEntry> policyEntries;
    private PolicyParser parser;
    private KeyStore keyStore;
    private String keyStoreName;
    private String keyStoreType;
    private String keyStoreProvider;
    private String keyStorePwdURL;
    private static final String P11KEYSTORE = "PKCS11";
    private static final String NONE = "NONE";
    
    private PolicyTool() {
        this.newWarning = false;
        this.modified = false;
        this.policyEntries = null;
        this.parser = null;
        this.keyStore = null;
        this.keyStoreName = " ";
        this.keyStoreType = " ";
        this.keyStoreProvider = " ";
        this.keyStorePwdURL = " ";
        this.policyEntries = new Vector<PolicyEntry>();
        this.parser = new PolicyParser();
        this.warnings = new Vector<String>();
    }
    
    String getPolicyFileName() {
        return PolicyTool.policyFileName;
    }
    
    void setPolicyFileName(final String policyFileName) {
        PolicyTool.policyFileName = policyFileName;
    }
    
    void clearKeyStoreInfo() {
        this.keyStoreName = null;
        this.keyStoreType = null;
        this.keyStoreProvider = null;
        this.keyStorePwdURL = null;
        this.keyStore = null;
    }
    
    String getKeyStoreName() {
        return this.keyStoreName;
    }
    
    String getKeyStoreType() {
        return this.keyStoreType;
    }
    
    String getKeyStoreProvider() {
        return this.keyStoreProvider;
    }
    
    String getKeyStorePwdURL() {
        return this.keyStorePwdURL;
    }
    
    void openPolicy(final String policyFileName) throws FileNotFoundException, PolicyParser.ParsingException, KeyStoreException, CertificateException, InstantiationException, MalformedURLException, IOException, NoSuchAlgorithmException, IllegalAccessException, NoSuchMethodException, UnrecoverableKeyException, NoSuchProviderException, ClassNotFoundException, PropertyExpander.ExpandException, InvocationTargetException {
        this.newWarning = false;
        this.policyEntries = new Vector<PolicyEntry>();
        this.parser = new PolicyParser();
        this.warnings = new Vector<String>();
        this.setPolicyFileName(null);
        this.clearKeyStoreInfo();
        if (policyFileName == null) {
            this.modified = false;
            return;
        }
        this.setPolicyFileName(policyFileName);
        this.parser.read(new FileReader(policyFileName));
        this.openKeyStore(this.parser.getKeyStoreUrl(), this.parser.getKeyStoreType(), this.parser.getKeyStoreProvider(), this.parser.getStorePassURL());
        final Enumeration<PolicyParser.GrantEntry> grantElements = this.parser.grantElements();
        while (grantElements.hasMoreElements()) {
            final PolicyParser.GrantEntry grantEntry = grantElements.nextElement();
            if (grantEntry.signedBy != null) {
                final String[] signers = this.parseSigners(grantEntry.signedBy);
                for (int i = 0; i < signers.length; ++i) {
                    if (this.getPublicKeyAlias(signers[i]) == null) {
                        this.newWarning = true;
                        this.warnings.addElement(new MessageFormat(getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured.")).format(new Object[] { signers[i] }));
                    }
                }
            }
            final ListIterator<PolicyParser.PrincipalEntry> listIterator = grantEntry.principals.listIterator(0);
            while (listIterator.hasNext()) {
                final PolicyParser.PrincipalEntry principalEntry = listIterator.next();
                try {
                    this.verifyPrincipal(principalEntry.getPrincipalClass(), principalEntry.getPrincipalName());
                }
                catch (final ClassNotFoundException ex) {
                    this.newWarning = true;
                    this.warnings.addElement(new MessageFormat(getMessage("Warning.Class.not.found.class")).format(new Object[] { principalEntry.getPrincipalClass() }));
                }
            }
            final Enumeration<PolicyParser.PermissionEntry> permissionElements = grantEntry.permissionElements();
            while (permissionElements.hasMoreElements()) {
                final PolicyParser.PermissionEntry permissionEntry = permissionElements.nextElement();
                try {
                    this.verifyPermission(permissionEntry.permission, permissionEntry.name, permissionEntry.action);
                }
                catch (final ClassNotFoundException ex2) {
                    this.newWarning = true;
                    this.warnings.addElement(new MessageFormat(getMessage("Warning.Class.not.found.class")).format(new Object[] { permissionEntry.permission }));
                }
                catch (final InvocationTargetException ex3) {
                    this.newWarning = true;
                    this.warnings.addElement(new MessageFormat(getMessage("Warning.Invalid.argument.s.for.constructor.arg")).format(new Object[] { permissionEntry.permission }));
                }
                if (permissionEntry.signedBy != null) {
                    final String[] signers2 = this.parseSigners(permissionEntry.signedBy);
                    for (int j = 0; j < signers2.length; ++j) {
                        if (this.getPublicKeyAlias(signers2[j]) == null) {
                            this.newWarning = true;
                            this.warnings.addElement(new MessageFormat(getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured.")).format(new Object[] { signers2[j] }));
                        }
                    }
                }
            }
            this.policyEntries.addElement(new PolicyEntry(this, grantEntry));
        }
        this.modified = false;
    }
    
    void savePolicy(final String s) throws FileNotFoundException, IOException {
        this.parser.setKeyStoreUrl(this.keyStoreName);
        this.parser.setKeyStoreType(this.keyStoreType);
        this.parser.setKeyStoreProvider(this.keyStoreProvider);
        this.parser.setStorePassURL(this.keyStorePwdURL);
        this.parser.write(new FileWriter(s));
        this.modified = false;
    }
    
    void openKeyStore(String replace, String defaultType, final String keyStoreProvider, String replace2) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, IOException, CertificateException, NoSuchProviderException, PropertyExpander.ExpandException {
        if (replace == null && defaultType == null && keyStoreProvider == null && replace2 == null) {
            this.keyStoreName = null;
            this.keyStoreType = null;
            this.keyStoreProvider = null;
            this.keyStorePwdURL = null;
            return;
        }
        URL url = null;
        if (PolicyTool.policyFileName != null) {
            url = new URL("file:" + new File(PolicyTool.policyFileName).getCanonicalPath());
        }
        if (replace != null && replace.length() > 0) {
            replace = PropertyExpander.expand(replace).replace(File.separatorChar, '/');
        }
        if (defaultType == null || defaultType.length() == 0) {
            defaultType = KeyStore.getDefaultType();
        }
        if (replace2 != null && replace2.length() > 0) {
            replace2 = PropertyExpander.expand(replace2).replace(File.separatorChar, '/');
        }
        try {
            this.keyStore = PolicyUtil.getKeyStore(url, replace, defaultType, keyStoreProvider, replace2, null);
        }
        catch (final IOException ex) {
            final String s = "no password provided, and no callback handler available for retrieving password";
            final Throwable cause = ex.getCause();
            if (cause != null && cause instanceof LoginException && s.equals(cause.getMessage())) {
                throw new IOException(s);
            }
            throw ex;
        }
        this.keyStoreName = replace;
        this.keyStoreType = defaultType;
        this.keyStoreProvider = keyStoreProvider;
        this.keyStorePwdURL = replace2;
    }
    
    boolean addEntry(final PolicyEntry policyEntry, final int n) {
        if (n < 0) {
            this.policyEntries.addElement(policyEntry);
            this.parser.add(policyEntry.getGrantEntry());
        }
        else {
            this.parser.replace(this.policyEntries.elementAt(n).getGrantEntry(), policyEntry.getGrantEntry());
            this.policyEntries.setElementAt(policyEntry, n);
        }
        return true;
    }
    
    boolean addPrinEntry(final PolicyEntry policyEntry, final PolicyParser.PrincipalEntry principalEntry, final int n) {
        final PolicyParser.GrantEntry grantEntry = policyEntry.getGrantEntry();
        if (grantEntry.contains(principalEntry)) {
            return false;
        }
        final LinkedList<PolicyParser.PrincipalEntry> principals = grantEntry.principals;
        if (n != -1) {
            principals.set(n, principalEntry);
        }
        else {
            principals.add(principalEntry);
        }
        return this.modified = true;
    }
    
    boolean addPermEntry(final PolicyEntry policyEntry, final PolicyParser.PermissionEntry permissionEntry, final int n) {
        final PolicyParser.GrantEntry grantEntry = policyEntry.getGrantEntry();
        if (grantEntry.contains(permissionEntry)) {
            return false;
        }
        final Vector<PolicyParser.PermissionEntry> permissionEntries = grantEntry.permissionEntries;
        if (n != -1) {
            permissionEntries.setElementAt(permissionEntry, n);
        }
        else {
            permissionEntries.addElement(permissionEntry);
        }
        return this.modified = true;
    }
    
    boolean removePermEntry(final PolicyEntry policyEntry, final PolicyParser.PermissionEntry permissionEntry) {
        return this.modified = policyEntry.getGrantEntry().remove(permissionEntry);
    }
    
    boolean removeEntry(final PolicyEntry policyEntry) {
        this.parser.remove(policyEntry.getGrantEntry());
        this.modified = true;
        return this.policyEntries.removeElement(policyEntry);
    }
    
    PolicyEntry[] getEntry() {
        if (this.policyEntries.size() > 0) {
            final PolicyEntry[] array = new PolicyEntry[this.policyEntries.size()];
            for (int i = 0; i < this.policyEntries.size(); ++i) {
                array[i] = this.policyEntries.elementAt(i);
            }
            return array;
        }
        return null;
    }
    
    PublicKey getPublicKeyAlias(final String s) throws KeyStoreException {
        if (this.keyStore == null) {
            return null;
        }
        final Certificate certificate = this.keyStore.getCertificate(s);
        if (certificate == null) {
            return null;
        }
        return certificate.getPublicKey();
    }
    
    String[] getPublicKeyAlias() throws KeyStoreException {
        int n = 0;
        String[] array = null;
        if (this.keyStore == null) {
            return null;
        }
        final Enumeration<String> aliases = this.keyStore.aliases();
        while (aliases.hasMoreElements()) {
            aliases.nextElement();
            ++n;
        }
        if (n > 0) {
            array = new String[n];
            int n2 = 0;
            final Enumeration<String> aliases2 = this.keyStore.aliases();
            while (aliases2.hasMoreElements()) {
                array[n2] = new String(aliases2.nextElement());
                ++n2;
            }
        }
        return array;
    }
    
    String[] parseSigners(final String s) {
        int n = 1;
        int n2 = 0;
        int i = 0;
        int n3 = 0;
        while (i >= 0) {
            i = s.indexOf(44, n2);
            if (i >= 0) {
                ++n;
                n2 = i + 1;
            }
        }
        final String[] array = new String[n];
        int j = 0;
        int n4 = 0;
        while (j >= 0) {
            if ((j = s.indexOf(44, n4)) >= 0) {
                array[n3] = s.substring(n4, j).trim();
                ++n3;
                n4 = j + 1;
            }
            else {
                array[n3] = s.substring(n4).trim();
            }
        }
        return array;
    }
    
    void verifyPrincipal(final String s, final String s2) throws ClassNotFoundException, InstantiationException {
        if (s.equals("WILDCARD_PRINCIPAL_CLASS") || s.equals("PolicyParser.REPLACE_NAME")) {
            return;
        }
        final Class<?> forName = Class.forName("java.security.Principal");
        final Class<?> forName2 = Class.forName(s, true, Thread.currentThread().getContextClassLoader());
        if (!forName.isAssignableFrom(forName2)) {
            throw new InstantiationException(new MessageFormat(getMessage("Illegal.Principal.Type.type")).format(new Object[] { s }));
        }
        if ("javax.security.auth.x500.X500Principal".equals(forName2.getName())) {
            final X500Principal x500Principal = new X500Principal(s2);
        }
    }
    
    void verifyPermission(final String s, final String s2, final String s3) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final Class<?> forName = Class.forName(s, true, Thread.currentThread().getContextClassLoader());
        Constructor<?> constructor = null;
        final Vector vector = new Vector(2);
        if (s2 != null) {
            vector.add(s2);
        }
        if (s3 != null) {
            vector.add(s3);
        }
        switch (vector.size()) {
            case 0: {
                try {
                    constructor = forName.getConstructor(PolicyTool.NOPARAMS);
                    break;
                }
                catch (final NoSuchMethodException ex) {
                    vector.add(null);
                }
            }
            case 1: {
                try {
                    constructor = forName.getConstructor(PolicyTool.ONEPARAMS);
                    break;
                }
                catch (final NoSuchMethodException ex2) {
                    vector.add(null);
                }
            }
            case 2: {
                constructor = forName.getConstructor(PolicyTool.TWOPARAMS);
                break;
            }
        }
        final Permission permission = (Permission)constructor.newInstance(vector.toArray());
    }
    
    static void parseArgs(final String[] array) {
        for (int n = 0; n < array.length && array[n].startsWith("-"); ++n) {
            final String s = array[n];
            if (PolicyTool.collator.compare(s, "-file") == 0) {
                if (++n == array.length) {
                    usage();
                }
                PolicyTool.policyFileName = array[n];
            }
            else {
                System.err.println(new MessageFormat(getMessage("Illegal.option.option")).format(new Object[] { s }));
                usage();
            }
        }
    }
    
    static void usage() {
        System.out.println(getMessage("Usage.policytool.options."));
        System.out.println();
        System.out.println(getMessage(".file.file.policy.file.location"));
        System.out.println();
        System.exit(1);
    }
    
    public static void main(final String[] array) {
        parseArgs(array);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ToolWindow(new PolicyTool(null)).displayToolWindow(array);
            }
        });
    }
    
    static String splitToWords(final String s) {
        return s.replaceAll("([A-Z])", " $1");
    }
    
    static String getMessage(final String s) {
        return removeMnemonicAmpersand(PolicyTool.rb.getString(s));
    }
    
    static int getMnemonicInt(final String s) {
        return findMnemonicInt(PolicyTool.rb.getString(s));
    }
    
    static int getDisplayedMnemonicIndex(final String s) {
        return findMnemonicIndex(PolicyTool.rb.getString(s));
    }
    
    private static int findMnemonicInt(final String s) {
        for (int i = 0; i < s.length() - 1; ++i) {
            if (s.charAt(i) == '&') {
                if (s.charAt(i + 1) != '&') {
                    return KeyEvent.getExtendedKeyCodeForChar(s.charAt(i + 1));
                }
                ++i;
            }
        }
        return 0;
    }
    
    private static int findMnemonicIndex(final String s) {
        for (int i = 0; i < s.length() - 1; ++i) {
            if (s.charAt(i) == '&') {
                if (s.charAt(i + 1) != '&') {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }
    
    private static String removeMnemonicAmpersand(final String s) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 != '&' || i == s.length() - 1 || s.charAt(i + 1) == '&') {
                sb.append(char1);
            }
        }
        return sb.toString();
    }
    
    static {
        rb = ResourceBundle.getBundle("sun.security.tools.policytool.Resources");
        (collator = Collator.getInstance()).setStrength(0);
        if (System.getProperty("apple.laf.useScreenMenuBar") == null) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        System.setProperty("apple.awt.application.name", getMessage("Policy.Tool"));
        if (System.getProperty("swing.defaultlaf") == null) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (final Exception ex) {}
        }
        TWOPARAMS = new Class[] { String.class, String.class };
        ONEPARAMS = new Class[] { String.class };
        NOPARAMS = new Class[0];
        PolicyTool.policyFileName = null;
    }
}
