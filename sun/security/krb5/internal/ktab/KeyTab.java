package sun.security.krb5.internal.ktab;

import sun.security.krb5.internal.Krb5;
import java.util.HashMap;
import java.io.OutputStream;
import java.io.FileOutputStream;
import sun.security.krb5.internal.KerberosTime;
import java.util.Arrays;
import java.util.Comparator;
import sun.security.krb5.internal.crypto.EType;
import java.util.ArrayList;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.KrbException;
import java.util.StringTokenizer;
import sun.security.krb5.Config;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Vector;
import java.util.Map;

public class KeyTab implements KeyTabConstants
{
    private static final boolean DEBUG;
    private static String defaultTabName;
    private static Map<String, KeyTab> map;
    private boolean isMissing;
    private boolean isValid;
    private final String tabName;
    private long lastModified;
    private int kt_vno;
    private Vector<KeyTabEntry> entries;
    
    private KeyTab(final String tabName) {
        this.isMissing = false;
        this.isValid = true;
        this.kt_vno = 1282;
        this.entries = new Vector<KeyTabEntry>();
        this.tabName = tabName;
        try {
            this.lastModified = new File(this.tabName).lastModified();
            try (final KeyTabInputStream keyTabInputStream = new KeyTabInputStream(new FileInputStream(tabName))) {
                this.load(keyTabInputStream);
            }
        }
        catch (final FileNotFoundException ex) {
            this.entries.clear();
            this.isMissing = true;
        }
        catch (final Exception ex2) {
            this.entries.clear();
            this.isValid = false;
        }
    }
    
    private static synchronized KeyTab getInstance0(final String s) {
        final long lastModified = new File(s).lastModified();
        final KeyTab keyTab = KeyTab.map.get(s);
        if (keyTab != null && keyTab.isValid() && keyTab.lastModified == lastModified) {
            return keyTab;
        }
        final KeyTab keyTab2 = new KeyTab(s);
        if (keyTab2.isValid()) {
            KeyTab.map.put(s, keyTab2);
            return keyTab2;
        }
        if (keyTab != null) {
            return keyTab;
        }
        return keyTab2;
    }
    
    public static KeyTab getInstance(final String s) {
        if (s == null) {
            return getInstance();
        }
        return getInstance0(normalize(s));
    }
    
    public static KeyTab getInstance(final File file) {
        if (file == null) {
            return getInstance();
        }
        return getInstance0(file.getPath());
    }
    
    public static KeyTab getInstance() {
        return getInstance(getDefaultTabName());
    }
    
    public boolean isMissing() {
        return this.isMissing;
    }
    
    public boolean isValid() {
        return this.isValid;
    }
    
    private static String getDefaultTabName() {
        if (KeyTab.defaultTabName != null) {
            return KeyTab.defaultTabName;
        }
        String defaultTabName = null;
        try {
            final String value = Config.getInstance().get("libdefaults", "default_keytab_name");
            if (value != null) {
                final StringTokenizer stringTokenizer = new StringTokenizer(value, " ");
                while (stringTokenizer.hasMoreTokens()) {
                    defaultTabName = normalize(stringTokenizer.nextToken());
                    if (new File(defaultTabName).exists()) {
                        break;
                    }
                }
            }
        }
        catch (final KrbException ex) {
            defaultTabName = null;
        }
        if (defaultTabName == null) {
            String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("user.home"));
            if (s == null) {
                s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("user.dir"));
            }
            defaultTabName = s + File.separator + "krb5.keytab";
        }
        return KeyTab.defaultTabName = defaultTabName;
    }
    
    public static String normalize(final String s) {
        String s2;
        if (s.length() >= 5 && s.substring(0, 5).equalsIgnoreCase("FILE:")) {
            s2 = s.substring(5);
        }
        else if (s.length() >= 9 && s.substring(0, 9).equalsIgnoreCase("ANY:FILE:")) {
            s2 = s.substring(9);
        }
        else if (s.length() >= 7 && s.substring(0, 7).equalsIgnoreCase("SRVTAB:")) {
            s2 = s.substring(7);
        }
        else {
            s2 = s;
        }
        return s2;
    }
    
    private void load(final KeyTabInputStream keyTabInputStream) throws IOException, RealmException {
        this.entries.clear();
        this.kt_vno = keyTabInputStream.readVersion();
        if (this.kt_vno == 1281) {
            keyTabInputStream.setNativeByteOrder();
        }
        while (keyTabInputStream.available() > 0) {
            final int entryLength = keyTabInputStream.readEntryLength();
            final KeyTabEntry entry = keyTabInputStream.readEntry(entryLength, this.kt_vno);
            if (KeyTab.DEBUG) {
                System.out.println(">>> KeyTab: load() entry length: " + entryLength + "; type: " + ((entry != null) ? entry.keyType : 0));
            }
            if (entry != null) {
                this.entries.addElement(entry);
            }
        }
    }
    
    public PrincipalName getOneName() {
        final int size = this.entries.size();
        return (size > 0) ? this.entries.elementAt(size - 1).service : null;
    }
    
    public EncryptionKey[] readServiceKeys(final PrincipalName principalName) {
        final int size = this.entries.size();
        final ArrayList list = new ArrayList<EncryptionKey>(size);
        if (KeyTab.DEBUG) {
            System.out.println("Looking for keys for: " + principalName);
        }
        for (int i = size - 1; i >= 0; --i) {
            final KeyTabEntry keyTabEntry = this.entries.elementAt(i);
            if (keyTabEntry.service.match(principalName)) {
                if (EType.isSupported(keyTabEntry.keyType)) {
                    list.add(new EncryptionKey(keyTabEntry.keyblock, keyTabEntry.keyType, new Integer(keyTabEntry.keyVersion)));
                    if (KeyTab.DEBUG) {
                        System.out.println("Added key: " + keyTabEntry.keyType + "version: " + keyTabEntry.keyVersion);
                    }
                }
                else if (KeyTab.DEBUG) {
                    System.out.println("Found unsupported keytype (" + keyTabEntry.keyType + ") for " + principalName);
                }
            }
        }
        final EncryptionKey[] array = list.toArray(new EncryptionKey[list.size()]);
        Arrays.sort(array, new Comparator<EncryptionKey>() {
            @Override
            public int compare(final EncryptionKey encryptionKey, final EncryptionKey encryptionKey2) {
                return encryptionKey2.getKeyVersionNumber() - encryptionKey.getKeyVersionNumber();
            }
        });
        return array;
    }
    
    public boolean findServiceEntry(final PrincipalName principalName) {
        for (int i = 0; i < this.entries.size(); ++i) {
            final KeyTabEntry keyTabEntry = this.entries.elementAt(i);
            if (keyTabEntry.service.match(principalName)) {
                if (EType.isSupported(keyTabEntry.keyType)) {
                    return true;
                }
                if (KeyTab.DEBUG) {
                    System.out.println("Found unsupported keytype (" + keyTabEntry.keyType + ") for " + principalName);
                }
            }
        }
        return false;
    }
    
    public String tabName() {
        return this.tabName;
    }
    
    public void addEntry(final PrincipalName principalName, final char[] array, final int n, final boolean b) throws KrbException {
        this.addEntry(principalName, principalName.getSalt(), array, n, b);
    }
    
    public void addEntry(final PrincipalName principalName, final String s, final char[] array, int n, final boolean b) throws KrbException {
        final EncryptionKey[] acquireSecretKeys = EncryptionKey.acquireSecretKeys(array, s);
        int keyVersion = 0;
        for (int i = this.entries.size() - 1; i >= 0; --i) {
            final KeyTabEntry keyTabEntry = this.entries.get(i);
            if (keyTabEntry.service.match(principalName)) {
                if (keyTabEntry.keyVersion > keyVersion) {
                    keyVersion = keyTabEntry.keyVersion;
                }
                if (!b || keyTabEntry.keyVersion == n) {
                    this.entries.removeElementAt(i);
                }
            }
        }
        if (n == -1) {
            n = keyVersion + 1;
        }
        for (int n2 = 0; acquireSecretKeys != null && n2 < acquireSecretKeys.length; ++n2) {
            this.entries.addElement(new KeyTabEntry(principalName, principalName.getRealm(), new KerberosTime(System.currentTimeMillis()), n, acquireSecretKeys[n2].getEType(), acquireSecretKeys[n2].getBytes()));
        }
    }
    
    public KeyTabEntry[] getEntries() {
        final KeyTabEntry[] array = new KeyTabEntry[this.entries.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.entries.elementAt(i);
        }
        return array;
    }
    
    public static synchronized KeyTab create() throws IOException, RealmException {
        return create(getDefaultTabName());
    }
    
    public static synchronized KeyTab create(final String s) throws IOException, RealmException {
        try (final KeyTabOutputStream keyTabOutputStream = new KeyTabOutputStream(new FileOutputStream(s))) {
            keyTabOutputStream.writeVersion(1282);
        }
        return new KeyTab(s);
    }
    
    public synchronized void save() throws IOException {
        try (final KeyTabOutputStream keyTabOutputStream = new KeyTabOutputStream(new FileOutputStream(this.tabName))) {
            keyTabOutputStream.writeVersion(this.kt_vno);
            for (int i = 0; i < this.entries.size(); ++i) {
                keyTabOutputStream.writeEntry(this.entries.elementAt(i));
            }
        }
    }
    
    public int deleteEntries(final PrincipalName principalName, final int n, final int n2) {
        int n3 = 0;
        final HashMap hashMap = new HashMap();
        for (int i = this.entries.size() - 1; i >= 0; --i) {
            final KeyTabEntry keyTabEntry = this.entries.get(i);
            if (principalName.match(keyTabEntry.getService()) && (n == -1 || keyTabEntry.keyType == n)) {
                if (n2 == -2) {
                    if (hashMap.containsKey(keyTabEntry.keyType)) {
                        if (keyTabEntry.keyVersion > (int)hashMap.get(keyTabEntry.keyType)) {
                            hashMap.put(keyTabEntry.keyType, keyTabEntry.keyVersion);
                        }
                    }
                    else {
                        hashMap.put(keyTabEntry.keyType, keyTabEntry.keyVersion);
                    }
                }
                else if (n2 == -1 || keyTabEntry.keyVersion == n2) {
                    this.entries.removeElementAt(i);
                    ++n3;
                }
            }
        }
        if (n2 == -2) {
            for (int j = this.entries.size() - 1; j >= 0; --j) {
                final KeyTabEntry keyTabEntry2 = this.entries.get(j);
                if (principalName.match(keyTabEntry2.getService()) && (n == -1 || keyTabEntry2.keyType == n) && keyTabEntry2.keyVersion != (int)hashMap.get(keyTabEntry2.keyType)) {
                    this.entries.removeElementAt(j);
                    ++n3;
                }
            }
        }
        return n3;
    }
    
    public synchronized void createVersion(final File file) throws IOException {
        try (final KeyTabOutputStream keyTabOutputStream = new KeyTabOutputStream(new FileOutputStream(file))) {
            keyTabOutputStream.write16(1282);
        }
    }
    
    static {
        DEBUG = Krb5.DEBUG;
        KeyTab.defaultTabName = null;
        KeyTab.map = new HashMap<String, KeyTab>();
    }
}
