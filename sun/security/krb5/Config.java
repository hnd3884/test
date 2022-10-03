package sun.security.krb5;

import sun.security.krb5.internal.Krb5;
import sun.security.util.SecurityProperties;
import sun.net.dns.ResolverConfiguration;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.StringTokenizer;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;
import java.io.File;
import java.security.PrivilegedActionException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.io.FileInputStream;
import java.security.PrivilegedExceptionAction;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
import java.io.IOException;
import sun.security.krb5.internal.crypto.EType;
import java.util.Hashtable;

public class Config
{
    public static final boolean DISABLE_REFERRALS;
    public static final int MAX_REFERRALS;
    private static Config singleton;
    private Hashtable<String, Object> stanzaTable;
    private static boolean DEBUG;
    private static final int BASE16_0 = 1;
    private static final int BASE16_1 = 16;
    private static final int BASE16_2 = 256;
    private static final int BASE16_3 = 4096;
    private final String defaultRealm;
    private final String defaultKDC;
    
    private static native String getWindowsDirectory(final boolean p0);
    
    public static synchronized Config getInstance() throws KrbException {
        if (Config.singleton == null) {
            Config.singleton = new Config();
        }
        return Config.singleton;
    }
    
    public static synchronized void refresh() throws KrbException {
        Config.singleton = new Config();
        KdcComm.initStatic();
        EType.initStatic();
        Checksum.initStatic();
        KrbAsReqBuilder.ReferralsState.initStatic();
    }
    
    private static boolean isMacosLionOrBetter() {
        if (!getProperty("os.name").contains("OS X")) {
            return false;
        }
        final String[] split = getProperty("os.version").split("\\.");
        if (!split[0].equals("10")) {
            return false;
        }
        if (split.length < 2) {
            return false;
        }
        try {
            if (Integer.parseInt(split[1]) >= 7) {
                return true;
            }
        }
        catch (final NumberFormatException ex) {}
        return false;
    }
    
    private Config() throws KrbException {
        this.stanzaTable = new Hashtable<String, Object>();
        final String property = getProperty("java.security.krb5.kdc");
        if (property != null) {
            this.defaultKDC = property.replace(':', ' ');
        }
        else {
            this.defaultKDC = null;
        }
        this.defaultRealm = getProperty("java.security.krb5.realm");
        if ((this.defaultKDC == null && this.defaultRealm != null) || (this.defaultRealm == null && this.defaultKDC != null)) {
            throw new KrbException("System property java.security.krb5.kdc and java.security.krb5.realm both must be set or neither must be set.");
        }
        try {
            final String javaFileName = this.getJavaFileName();
            if (javaFileName != null) {
                this.stanzaTable = this.parseStanzaTable(this.loadConfigFile(javaFileName));
                if (Config.DEBUG) {
                    System.out.println("Loaded from Java config");
                }
            }
            else {
                boolean b = false;
                if (isMacosLionOrBetter()) {
                    try {
                        this.stanzaTable = SCDynamicStoreConfig.getConfig();
                        if (Config.DEBUG) {
                            System.out.println("Loaded from SCDynamicStoreConfig");
                        }
                        b = true;
                    }
                    catch (final IOException ex) {}
                }
                if (!b) {
                    this.stanzaTable = this.parseStanzaTable(this.loadConfigFile(this.getNativeFileName()));
                    if (Config.DEBUG) {
                        System.out.println("Loaded from native config");
                    }
                }
            }
        }
        catch (final IOException ex2) {}
    }
    
    public String get(final String... array) {
        final Vector<String> string0 = this.getString0(array);
        if (string0 == null) {
            return null;
        }
        return string0.lastElement();
    }
    
    public Boolean getBooleanObject(final String... array) {
        final String value = this.get(array);
        if (value == null) {
            return null;
        }
        final String lowerCase = value.toLowerCase(Locale.US);
        switch (lowerCase) {
            case "yes":
            case "true": {
                return Boolean.TRUE;
            }
            case "no":
            case "false": {
                return Boolean.FALSE;
            }
            default: {
                return null;
            }
        }
    }
    
    public String getAll(final String... array) {
        final Vector<String> string0 = this.getString0(array);
        if (string0 == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        int n = 1;
        final Iterator<String> iterator = string0.iterator();
        while (iterator.hasNext()) {
            final String replaceAll = iterator.next().replaceAll("[\\s,]+", " ");
            if (n != 0) {
                sb.append(replaceAll);
                n = 0;
            }
            else {
                sb.append(' ').append(replaceAll);
            }
        }
        return sb.toString();
    }
    
    public boolean exists(final String... array) {
        return this.get0(array) != null;
    }
    
    private Vector<String> getString0(final String... array) {
        try {
            return (Vector)this.get0(array);
        }
        catch (final ClassCastException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    private Object get0(final String... array) {
        Hashtable<String, Object> hashtable = this.stanzaTable;
        try {
            for (int length = array.length, i = 0; i < length; ++i) {
                hashtable = ((Hashtable<K, Hashtable<String, Object>>)hashtable).get(array[i]);
                if (hashtable == null) {
                    return null;
                }
            }
            return hashtable;
        }
        catch (final ClassCastException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    public static int duration(final String s) throws KrbException {
        if (s.isEmpty()) {
            throw new KrbException("Duration cannot be empty");
        }
        if (s.matches("\\d+")) {
            return Integer.parseInt(s);
        }
        final Matcher matcher = Pattern.compile("(\\d+):(\\d+)(:(\\d+))?").matcher(s);
        if (matcher.matches()) {
            final int int1 = Integer.parseInt(matcher.group(1));
            final int int2 = Integer.parseInt(matcher.group(2));
            if (int2 >= 60) {
                throw new KrbException("Illegal duration format " + s);
            }
            int n = int1 * 3600 + int2 * 60;
            if (matcher.group(4) != null) {
                final int int3 = Integer.parseInt(matcher.group(4));
                if (int3 >= 60) {
                    throw new KrbException("Illegal duration format " + s);
                }
                n += int3;
            }
            return n;
        }
        else {
            final Matcher matcher2 = Pattern.compile("((\\d+)d)?\\s*((\\d+)h)?\\s*((\\d+)m)?\\s*((\\d+)s)?", 2).matcher(s);
            if (matcher2.matches()) {
                int n2 = 0;
                if (matcher2.group(2) != null) {
                    n2 += 86400 * Integer.parseInt(matcher2.group(2));
                }
                if (matcher2.group(4) != null) {
                    n2 += 3600 * Integer.parseInt(matcher2.group(4));
                }
                if (matcher2.group(6) != null) {
                    n2 += 60 * Integer.parseInt(matcher2.group(6));
                }
                if (matcher2.group(8) != null) {
                    n2 += Integer.parseInt(matcher2.group(8));
                }
                return n2;
            }
            throw new KrbException("Illegal duration format " + s);
        }
    }
    
    public int getIntValue(final String... array) {
        final String value = this.get(array);
        int intValue = Integer.MIN_VALUE;
        if (value != null) {
            try {
                intValue = this.parseIntValue(value);
            }
            catch (final NumberFormatException ex) {
                if (Config.DEBUG) {
                    System.out.println("Exception in getting value of " + Arrays.toString(array) + " " + ex.getMessage());
                    System.out.println("Setting " + Arrays.toString(array) + " to minimum value");
                }
                intValue = Integer.MIN_VALUE;
            }
        }
        return intValue;
    }
    
    public boolean getBooleanValue(final String... array) {
        final String value = this.get(array);
        return value != null && value.equalsIgnoreCase("true");
    }
    
    private int parseIntValue(final String s) throws NumberFormatException {
        int int1 = 0;
        if (s.startsWith("+")) {
            return Integer.parseInt(s.substring(1));
        }
        if (s.startsWith("0x")) {
            final char[] charArray = s.substring(2).toCharArray();
            if (charArray.length > 8) {
                throw new NumberFormatException();
            }
            for (int i = 0; i < charArray.length; ++i) {
                final int n = charArray.length - i - 1;
                switch (charArray[i]) {
                    case '0': {
                        int1 += 0;
                        break;
                    }
                    case '1': {
                        int1 += 1 * this.getBase(n);
                        break;
                    }
                    case '2': {
                        int1 += 2 * this.getBase(n);
                        break;
                    }
                    case '3': {
                        int1 += 3 * this.getBase(n);
                        break;
                    }
                    case '4': {
                        int1 += 4 * this.getBase(n);
                        break;
                    }
                    case '5': {
                        int1 += 5 * this.getBase(n);
                        break;
                    }
                    case '6': {
                        int1 += 6 * this.getBase(n);
                        break;
                    }
                    case '7': {
                        int1 += 7 * this.getBase(n);
                        break;
                    }
                    case '8': {
                        int1 += 8 * this.getBase(n);
                        break;
                    }
                    case '9': {
                        int1 += 9 * this.getBase(n);
                        break;
                    }
                    case 'A':
                    case 'a': {
                        int1 += 10 * this.getBase(n);
                        break;
                    }
                    case 'B':
                    case 'b': {
                        int1 += 11 * this.getBase(n);
                        break;
                    }
                    case 'C':
                    case 'c': {
                        int1 += 12 * this.getBase(n);
                        break;
                    }
                    case 'D':
                    case 'd': {
                        int1 += 13 * this.getBase(n);
                        break;
                    }
                    case 'E':
                    case 'e': {
                        int1 += 14 * this.getBase(n);
                        break;
                    }
                    case 'F':
                    case 'f': {
                        int1 += 15 * this.getBase(n);
                        break;
                    }
                    default: {
                        throw new NumberFormatException("Invalid numerical format");
                    }
                }
            }
            if (int1 < 0) {
                throw new NumberFormatException("Data overflow.");
            }
        }
        else {
            int1 = Integer.parseInt(s);
        }
        return int1;
    }
    
    private int getBase(final int n) {
        int n2 = 16;
        switch (n) {
            case 0: {
                n2 = 1;
                break;
            }
            case 1: {
                n2 = 16;
                break;
            }
            case 2: {
                n2 = 256;
                break;
            }
            case 3: {
                n2 = 4096;
                break;
            }
            default: {
                for (int i = 1; i < n; ++i) {
                    n2 *= 16;
                }
                break;
            }
        }
        return n2;
    }
    
    private List<String> loadConfigFile(final String s) throws IOException, KrbException {
        try {
            final ArrayList list = new ArrayList();
            try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)new PrivilegedExceptionAction<FileInputStream>() {
                @Override
                public FileInputStream run() throws IOException {
                    return new FileInputStream(s);
                }
            })))) {
                String s2 = null;
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    final String trim = line.trim();
                    if (!trim.isEmpty() && !trim.startsWith("#")) {
                        if (trim.startsWith(";")) {
                            continue;
                        }
                        if (trim.startsWith("[")) {
                            if (!trim.endsWith("]")) {
                                throw new KrbException("Illegal config content:" + trim);
                            }
                            if (s2 != null) {
                                list.add(s2);
                                list.add("}");
                            }
                            final String trim2 = trim.substring(1, trim.length() - 1).trim();
                            if (trim2.isEmpty()) {
                                throw new KrbException("Illegal config content:" + trim);
                            }
                            s2 = trim2 + " = {";
                        }
                        else if (trim.startsWith("{")) {
                            if (s2 == null) {
                                throw new KrbException("Config file should not start with \"{\"");
                            }
                            s2 += " {";
                            if (trim.length() <= 1) {
                                continue;
                            }
                            list.add(s2);
                            s2 = trim.substring(1).trim();
                        }
                        else {
                            if (s2 == null) {
                                continue;
                            }
                            list.add(s2);
                            s2 = trim;
                        }
                    }
                }
                if (s2 != null) {
                    list.add(s2);
                    list.add("}");
                }
            }
            return list;
        }
        catch (final PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    private Hashtable<String, Object> parseStanzaTable(final List<String> list) throws KrbException {
        Hashtable<?, ?> stanzaTable = this.stanzaTable;
        for (final String s : list) {
            if (s.equals("}")) {
                stanzaTable = (Hashtable<?, ?>)stanzaTable.remove(" PARENT ");
                if (stanzaTable == null) {
                    throw new KrbException("Unmatched close brace");
                }
                continue;
            }
            else {
                final int index = s.indexOf(61);
                if (index < 0) {
                    throw new KrbException("Illegal config content:" + s);
                }
                String s2 = s.substring(0, index).trim();
                final String trimmed = trimmed(s.substring(index + 1));
                if (trimmed.equals("{")) {
                    if (stanzaTable == this.stanzaTable) {
                        s2 = s2.toLowerCase(Locale.US);
                    }
                    final Hashtable hashtable = new Hashtable();
                    stanzaTable.put(s2, hashtable);
                    hashtable.put(" PARENT ", stanzaTable);
                    stanzaTable = hashtable;
                }
                else {
                    Vector<?> vector;
                    if (stanzaTable.containsKey(s2)) {
                        if (!(stanzaTable.get(s2) instanceof Vector)) {
                            throw new KrbException("Key " + s2 + "used for both value and section");
                        }
                        vector = (Vector<?>)stanzaTable.get(s2);
                    }
                    else {
                        vector = new Vector<Object>();
                        stanzaTable.put(s2, (Hashtable<?, ?>)vector);
                    }
                    vector.add(trimmed);
                }
            }
        }
        if (stanzaTable != this.stanzaTable) {
            throw new KrbException("Not closed");
        }
        return (Hashtable<String, Object>)stanzaTable;
    }
    
    private String getJavaFileName() {
        String s = getProperty("java.security.krb5.conf");
        if (s == null) {
            s = getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "krb5.conf";
            if (!this.fileExists(s)) {
                s = null;
            }
        }
        if (Config.DEBUG) {
            System.out.println("Java config name: " + s);
        }
        return s;
    }
    
    private String getNativeFileName() {
        String macosConfigFile = null;
        final String property = getProperty("os.name");
        if (property.startsWith("Windows")) {
            try {
                Credentials.ensureLoaded();
            }
            catch (final Exception ex) {}
            if (Credentials.alreadyLoaded) {
                final String windowsDirectory = getWindowsDirectory(false);
                if (windowsDirectory != null) {
                    String s;
                    if (windowsDirectory.endsWith("\\")) {
                        s = windowsDirectory + "krb5.ini";
                    }
                    else {
                        s = windowsDirectory + "\\krb5.ini";
                    }
                    if (this.fileExists(s)) {
                        macosConfigFile = s;
                    }
                }
                if (macosConfigFile == null) {
                    final String windowsDirectory2 = getWindowsDirectory(true);
                    if (windowsDirectory2 != null) {
                        String s2;
                        if (windowsDirectory2.endsWith("\\")) {
                            s2 = windowsDirectory2 + "krb5.ini";
                        }
                        else {
                            s2 = windowsDirectory2 + "\\krb5.ini";
                        }
                        macosConfigFile = s2;
                    }
                }
            }
            if (macosConfigFile == null) {
                macosConfigFile = "c:\\winnt\\krb5.ini";
            }
        }
        else if (property.startsWith("SunOS")) {
            macosConfigFile = "/etc/krb5/krb5.conf";
        }
        else if (property.contains("OS X")) {
            macosConfigFile = this.findMacosConfigFile();
        }
        else {
            macosConfigFile = "/etc/krb5.conf";
        }
        if (Config.DEBUG) {
            System.out.println("Native config name: " + macosConfigFile);
        }
        return macosConfigFile;
    }
    
    private static String getProperty(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction(s));
    }
    
    private String findMacosConfigFile() {
        final String string = getProperty("user.home") + "/Library/Preferences/edu.mit.Kerberos";
        if (this.fileExists(string)) {
            return string;
        }
        if (this.fileExists("/Library/Preferences/edu.mit.Kerberos")) {
            return "/Library/Preferences/edu.mit.Kerberos";
        }
        return "/etc/krb5.conf";
    }
    
    private static String trimmed(String s) {
        s = s.trim();
        if (s.length() >= 2 && ((s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"') || (s.charAt(0) == '\'' && s.charAt(s.length() - 1) == '\''))) {
            s = s.substring(1, s.length() - 1).trim();
        }
        return s;
    }
    
    public void listTable() {
        System.out.println(this);
    }
    
    public int[] defaultEtype(final String s) throws KrbException {
        final String value = this.get("libdefaults", s);
        int[] builtInDefaults;
        if (value == null) {
            if (Config.DEBUG) {
                System.out.println("Using builtin default etypes for " + s);
            }
            builtInDefaults = EType.getBuiltInDefaults();
        }
        else {
            String s2 = " ";
            for (int i = 0; i < value.length(); ++i) {
                if (value.substring(i, i + 1).equals(",")) {
                    s2 = ",";
                    break;
                }
            }
            final StringTokenizer stringTokenizer = new StringTokenizer(value, s2);
            final int countTokens = stringTokenizer.countTokens();
            final ArrayList list = new ArrayList<Integer>(countTokens);
            for (int j = 0; j < countTokens; ++j) {
                final int type = getType(stringTokenizer.nextToken());
                if (type != -1 && EType.isSupported(type)) {
                    list.add(type);
                }
            }
            if (list.isEmpty()) {
                throw new KrbException("no supported default etypes for " + s);
            }
            builtInDefaults = new int[list.size()];
            for (int k = 0; k < builtInDefaults.length; ++k) {
                builtInDefaults[k] = list.get(k);
            }
        }
        if (Config.DEBUG) {
            System.out.print("default etypes for " + s + ":");
            for (int l = 0; l < builtInDefaults.length; ++l) {
                System.out.print(" " + builtInDefaults[l]);
            }
            System.out.println(".");
        }
        return builtInDefaults;
    }
    
    public static int getType(final String s) {
        int n = -1;
        if (s == null) {
            return n;
        }
        if (s.startsWith("d") || s.startsWith("D")) {
            if (s.equalsIgnoreCase("des-cbc-crc")) {
                n = 1;
            }
            else if (s.equalsIgnoreCase("des-cbc-md5")) {
                n = 3;
            }
            else if (s.equalsIgnoreCase("des-mac")) {
                n = 4;
            }
            else if (s.equalsIgnoreCase("des-mac-k")) {
                n = 5;
            }
            else if (s.equalsIgnoreCase("des-cbc-md4")) {
                n = 2;
            }
            else if (s.equalsIgnoreCase("des3-cbc-sha1") || s.equalsIgnoreCase("des3-hmac-sha1") || s.equalsIgnoreCase("des3-cbc-sha1-kd") || s.equalsIgnoreCase("des3-cbc-hmac-sha1-kd")) {
                n = 16;
            }
        }
        else if (s.startsWith("a") || s.startsWith("A")) {
            if (s.equalsIgnoreCase("aes128-cts") || s.equalsIgnoreCase("aes128-cts-hmac-sha1-96")) {
                n = 17;
            }
            else if (s.equalsIgnoreCase("aes256-cts") || s.equalsIgnoreCase("aes256-cts-hmac-sha1-96")) {
                n = 18;
            }
            else if (s.equalsIgnoreCase("arcfour-hmac") || s.equalsIgnoreCase("arcfour-hmac-md5")) {
                n = 23;
            }
        }
        else if (s.equalsIgnoreCase("rc4-hmac")) {
            n = 23;
        }
        else if (s.equalsIgnoreCase("CRC32")) {
            n = 1;
        }
        else if (s.startsWith("r") || s.startsWith("R")) {
            if (s.equalsIgnoreCase("rsa-md5")) {
                n = 7;
            }
            else if (s.equalsIgnoreCase("rsa-md5-des")) {
                n = 8;
            }
        }
        else if (s.equalsIgnoreCase("hmac-sha1-des3-kd")) {
            n = 12;
        }
        else if (s.equalsIgnoreCase("hmac-sha1-96-aes128")) {
            n = 15;
        }
        else if (s.equalsIgnoreCase("hmac-sha1-96-aes256")) {
            n = 16;
        }
        else if (s.equalsIgnoreCase("hmac-md5-rc4") || s.equalsIgnoreCase("hmac-md5-arcfour") || s.equalsIgnoreCase("hmac-md5-enc")) {
            n = -138;
        }
        else if (s.equalsIgnoreCase("NULL")) {
            n = 0;
        }
        return n;
    }
    
    public void resetDefaultRealm(final String s) {
        if (Config.DEBUG) {
            System.out.println(">>> Config try resetting default kdc " + s);
        }
    }
    
    public boolean useAddresses() {
        final String value = this.get("libdefaults", "no_addresses");
        boolean b = value != null && value.equalsIgnoreCase("false");
        if (!b) {
            final String value2 = this.get("libdefaults", "noaddresses");
            b = (value2 != null && value2.equalsIgnoreCase("false"));
        }
        return b;
    }
    
    private boolean useDNS(final String s, final boolean b) {
        final Boolean booleanObject = this.getBooleanObject("libdefaults", s);
        if (booleanObject != null) {
            return booleanObject;
        }
        final Boolean booleanObject2 = this.getBooleanObject("libdefaults", "dns_fallback");
        if (booleanObject2 != null) {
            return booleanObject2;
        }
        return b;
    }
    
    private boolean useDNS_KDC() {
        return this.useDNS("dns_lookup_kdc", true);
    }
    
    private boolean useDNS_Realm() {
        return this.useDNS("dns_lookup_realm", false);
    }
    
    public String getDefaultRealm() throws KrbException {
        if (this.defaultRealm != null) {
            return this.defaultRealm;
        }
        Throwable t = null;
        String s = this.get("libdefaults", "default_realm");
        if (s == null && this.useDNS_Realm()) {
            try {
                s = this.getRealmFromDNS();
            }
            catch (final KrbException ex) {
                t = ex;
            }
        }
        if (s == null) {
            s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                @Override
                public String run() {
                    if (System.getProperty("os.name").startsWith("Windows")) {
                        return System.getenv("USERDNSDOMAIN");
                    }
                    return null;
                }
            });
        }
        if (s == null) {
            final KrbException ex2 = new KrbException("Cannot locate default realm");
            if (t != null) {
                ex2.initCause(t);
            }
            throw ex2;
        }
        return s;
    }
    
    public String getKDCList(String defaultRealm) throws KrbException {
        if (defaultRealm == null) {
            defaultRealm = this.getDefaultRealm();
        }
        if (defaultRealm.equalsIgnoreCase(this.defaultRealm)) {
            return this.defaultKDC;
        }
        Throwable t = null;
        String s = this.getAll("realms", defaultRealm, "kdc");
        if (s == null && this.useDNS_KDC()) {
            try {
                s = this.getKDCFromDNS(defaultRealm);
            }
            catch (final KrbException ex) {
                t = ex;
            }
        }
        if (s == null) {
            s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                @Override
                public String run() {
                    if (System.getProperty("os.name").startsWith("Windows")) {
                        String s = System.getenv("LOGONSERVER");
                        if (s != null && s.startsWith("\\\\")) {
                            s = s.substring(2);
                        }
                        return s;
                    }
                    return null;
                }
            });
        }
        if (s != null) {
            return s;
        }
        if (this.defaultKDC != null) {
            return this.defaultKDC;
        }
        final KrbException ex2 = new KrbException("Cannot locate KDC");
        if (t != null) {
            ex2.initCause(t);
        }
        throw ex2;
    }
    
    private String getRealmFromDNS() throws KrbException {
        String s = null;
        String canonicalHostName;
        try {
            canonicalHostName = InetAddress.getLocalHost().getCanonicalHostName();
        }
        catch (final UnknownHostException ex) {
            final KrbException ex2 = new KrbException(60, "Unable to locate Kerberos realm: " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        final String mapHostToRealm = PrincipalName.mapHostToRealm(canonicalHostName);
        if (mapHostToRealm == null) {
            final Iterator<String> iterator = ResolverConfiguration.open().searchlist().iterator();
            while (iterator.hasNext()) {
                s = checkRealm(iterator.next());
                if (s != null) {
                    break;
                }
            }
        }
        else {
            s = checkRealm(mapHostToRealm);
        }
        if (s == null) {
            throw new KrbException(60, "Unable to locate Kerberos realm");
        }
        return s;
    }
    
    private static String checkRealm(final String s) {
        if (Config.DEBUG) {
            System.out.println("getRealmFromDNS: trying " + s);
        }
        String[] kerberosService = null;
        for (String realmComponent = s; kerberosService == null && realmComponent != null; kerberosService = KrbServiceLocator.getKerberosService(realmComponent), realmComponent = Realm.parseRealmComponent(realmComponent)) {}
        if (kerberosService != null) {
            for (int i = 0; i < kerberosService.length; ++i) {
                if (kerberosService[i].equalsIgnoreCase(s)) {
                    return kerberosService[i];
                }
            }
        }
        return null;
    }
    
    private String getKDCFromDNS(final String s) throws KrbException {
        String string = "";
        if (Config.DEBUG) {
            System.out.println("getKDCFromDNS using UDP");
        }
        String[] array = KrbServiceLocator.getKerberosService(s, "_udp");
        if (array == null) {
            if (Config.DEBUG) {
                System.out.println("getKDCFromDNS using TCP");
            }
            array = KrbServiceLocator.getKerberosService(s, "_tcp");
        }
        if (array == null) {
            throw new KrbException(60, "Unable to locate KDC for realm " + s);
        }
        if (array.length == 0) {
            return null;
        }
        for (int i = 0; i < array.length; ++i) {
            string = string + array[i].trim() + " ";
        }
        final String trim = string.trim();
        if (trim.equals("")) {
            return null;
        }
        return trim;
    }
    
    private boolean fileExists(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new FileExistsAction(s));
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        toStringInternal("", this.stanzaTable, sb);
        return sb.toString();
    }
    
    private static void toStringInternal(final String s, final Object o, final StringBuffer sb) {
        if (o instanceof String) {
            sb.append(o).append('\n');
        }
        else if (o instanceof Hashtable) {
            final Hashtable hashtable = (Hashtable)o;
            sb.append("{\n");
            for (final Object next : hashtable.keySet()) {
                sb.append(s).append("    ").append(next).append(" = ");
                toStringInternal(s + "    ", hashtable.get(next), sb);
            }
            sb.append(s).append("}\n");
        }
        else if (o instanceof Vector) {
            final Vector vector = (Vector)o;
            sb.append("[");
            int n = 1;
            for (final Object o2 : vector.toArray()) {
                if (n == 0) {
                    sb.append(",");
                }
                sb.append(o2);
                n = 0;
            }
            sb.append("]\n");
        }
    }
    
    static {
        final String privilegedGetOverridable = SecurityProperties.privilegedGetOverridable("sun.security.krb5.disableReferrals");
        if (privilegedGetOverridable != null) {
            DISABLE_REFERRALS = "true".equalsIgnoreCase(privilegedGetOverridable);
        }
        else {
            DISABLE_REFERRALS = false;
        }
        int int1 = 5;
        final String privilegedGetOverridable2 = SecurityProperties.privilegedGetOverridable("sun.security.krb5.maxReferrals");
        try {
            int1 = Integer.parseInt(privilegedGetOverridable2);
        }
        catch (final NumberFormatException ex) {}
        MAX_REFERRALS = int1;
        Config.singleton = null;
        Config.DEBUG = Krb5.DEBUG;
    }
    
    static class FileExistsAction implements PrivilegedAction<Boolean>
    {
        private String fileName;
        
        public FileExistsAction(final String fileName) {
            this.fileName = fileName;
        }
        
        @Override
        public Boolean run() {
            return new File(this.fileName).exists();
        }
    }
}
