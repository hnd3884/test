package sun.security.provider;

import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.Iterator;
import java.io.FileWriter;
import java.io.FileReader;
import sun.net.www.ParseUtil;
import java.util.Locale;
import java.util.HashMap;
import javax.security.auth.x500.X500Principal;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Date;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Enumeration;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import sun.security.util.ResourcesMgr;
import java.util.TreeMap;
import java.io.BufferedReader;
import java.io.Reader;
import sun.security.util.PropertyExpander;
import java.io.StreamTokenizer;
import sun.security.util.Debug;
import java.util.Map;
import java.util.Vector;

public class PolicyParser
{
    private static final String EXTDIRS_PROPERTY = "java.ext.dirs";
    private static final String OLD_EXTDIRS_EXPANSION = "${java.ext.dirs}";
    static final String EXTDIRS_EXPANSION = "${{java.ext.dirs}}";
    private Vector<GrantEntry> grantEntries;
    private Map<String, DomainEntry> domainEntries;
    private static final Debug debug;
    private StreamTokenizer st;
    private int lookahead;
    private boolean expandProp;
    private String keyStoreUrlString;
    private String keyStoreType;
    private String keyStoreProvider;
    private String storePassURL;
    
    private String expand(final String s) throws PropertyExpander.ExpandException {
        return this.expand(s, false);
    }
    
    private String expand(final String s, final boolean b) throws PropertyExpander.ExpandException {
        if (!this.expandProp) {
            return s;
        }
        return PropertyExpander.expand(s, b);
    }
    
    public PolicyParser() {
        this.expandProp = false;
        this.keyStoreUrlString = null;
        this.keyStoreType = null;
        this.keyStoreProvider = null;
        this.storePassURL = null;
        this.grantEntries = new Vector<GrantEntry>();
    }
    
    public PolicyParser(final boolean expandProp) {
        this();
        this.expandProp = expandProp;
    }
    
    public void read(Reader reader) throws ParsingException, IOException {
        if (!(reader instanceof BufferedReader)) {
            reader = new BufferedReader(reader);
        }
        (this.st = new StreamTokenizer(reader)).resetSyntax();
        this.st.wordChars(97, 122);
        this.st.wordChars(65, 90);
        this.st.wordChars(46, 46);
        this.st.wordChars(48, 57);
        this.st.wordChars(95, 95);
        this.st.wordChars(36, 36);
        this.st.wordChars(160, 255);
        this.st.whitespaceChars(0, 32);
        this.st.commentChar(47);
        this.st.quoteChar(39);
        this.st.quoteChar(34);
        this.st.lowerCaseMode(false);
        this.st.ordinaryChar(47);
        this.st.slashSlashComments(true);
        this.st.slashStarComments(true);
        this.lookahead = this.st.nextToken();
        GrantEntry grantEntry = null;
        while (this.lookahead != -1) {
            if (this.peek("grant")) {
                grantEntry = this.parseGrantEntry();
                if (grantEntry != null) {
                    this.add(grantEntry);
                }
            }
            else if (this.peek("keystore") && this.keyStoreUrlString == null) {
                this.parseKeyStoreEntry();
            }
            else if (this.peek("keystorePasswordURL") && this.storePassURL == null) {
                this.parseStorePassURL();
            }
            else if (grantEntry == null && this.keyStoreUrlString == null && this.storePassURL == null && this.peek("domain")) {
                if (this.domainEntries == null) {
                    this.domainEntries = new TreeMap<String, DomainEntry>();
                }
                final DomainEntry domainEntry = this.parseDomainEntry();
                if (domainEntry != null) {
                    final String name = domainEntry.getName();
                    if (this.domainEntries.containsKey(name)) {
                        throw new ParsingException(new MessageFormat(ResourcesMgr.getString("duplicate.keystore.domain.name")).format(new Object[] { name }));
                    }
                    this.domainEntries.put(name, domainEntry);
                }
            }
            this.match(";");
        }
        if (this.keyStoreUrlString == null && this.storePassURL != null) {
            throw new ParsingException(ResourcesMgr.getString("keystorePasswordURL.can.not.be.specified.without.also.specifying.keystore"));
        }
    }
    
    public void add(final GrantEntry grantEntry) {
        this.grantEntries.addElement(grantEntry);
    }
    
    public void replace(final GrantEntry grantEntry, final GrantEntry grantEntry2) {
        this.grantEntries.setElementAt(grantEntry2, this.grantEntries.indexOf(grantEntry));
    }
    
    public boolean remove(final GrantEntry grantEntry) {
        return this.grantEntries.removeElement(grantEntry);
    }
    
    public String getKeyStoreUrl() {
        try {
            if (this.keyStoreUrlString != null && this.keyStoreUrlString.length() != 0) {
                return this.expand(this.keyStoreUrlString, true).replace(File.separatorChar, '/');
            }
        }
        catch (final PropertyExpander.ExpandException ex) {
            if (PolicyParser.debug != null) {
                PolicyParser.debug.println(ex.toString());
            }
            return null;
        }
        return null;
    }
    
    public void setKeyStoreUrl(final String keyStoreUrlString) {
        this.keyStoreUrlString = keyStoreUrlString;
    }
    
    public String getKeyStoreType() {
        return this.keyStoreType;
    }
    
    public void setKeyStoreType(final String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }
    
    public String getKeyStoreProvider() {
        return this.keyStoreProvider;
    }
    
    public void setKeyStoreProvider(final String keyStoreProvider) {
        this.keyStoreProvider = keyStoreProvider;
    }
    
    public String getStorePassURL() {
        try {
            if (this.storePassURL != null && this.storePassURL.length() != 0) {
                return this.expand(this.storePassURL, true).replace(File.separatorChar, '/');
            }
        }
        catch (final PropertyExpander.ExpandException ex) {
            if (PolicyParser.debug != null) {
                PolicyParser.debug.println(ex.toString());
            }
            return null;
        }
        return null;
    }
    
    public void setStorePassURL(final String storePassURL) {
        this.storePassURL = storePassURL;
    }
    
    public Enumeration<GrantEntry> grantElements() {
        return this.grantEntries.elements();
    }
    
    public Collection<DomainEntry> getDomainEntries() {
        return this.domainEntries.values();
    }
    
    public void write(final Writer writer) {
        final PrintWriter printWriter = new PrintWriter(new BufferedWriter(writer));
        final Enumeration<GrantEntry> grantElements = this.grantElements();
        printWriter.println("/* AUTOMATICALLY GENERATED ON " + new Date() + "*/");
        printWriter.println("/* DO NOT EDIT */");
        printWriter.println();
        if (this.keyStoreUrlString != null) {
            this.writeKeyStoreEntry(printWriter);
        }
        if (this.storePassURL != null) {
            this.writeStorePassURL(printWriter);
        }
        while (grantElements.hasMoreElements()) {
            grantElements.nextElement().write(printWriter);
            printWriter.println();
        }
        printWriter.flush();
    }
    
    private void parseKeyStoreEntry() throws ParsingException, IOException {
        this.match("keystore");
        this.keyStoreUrlString = this.match("quoted string");
        if (!this.peek(",")) {
            return;
        }
        this.match(",");
        if (!this.peek("\"")) {
            throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("expected.keystore.type"));
        }
        this.keyStoreType = this.match("quoted string");
        if (!this.peek(",")) {
            return;
        }
        this.match(",");
        if (this.peek("\"")) {
            this.keyStoreProvider = this.match("quoted string");
            return;
        }
        throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("expected.keystore.provider"));
    }
    
    private void parseStorePassURL() throws ParsingException, IOException {
        this.match("keyStorePasswordURL");
        this.storePassURL = this.match("quoted string");
    }
    
    private void writeKeyStoreEntry(final PrintWriter printWriter) {
        printWriter.print("keystore \"");
        printWriter.print(this.keyStoreUrlString);
        printWriter.print('\"');
        if (this.keyStoreType != null && this.keyStoreType.length() > 0) {
            printWriter.print(", \"" + this.keyStoreType + "\"");
        }
        if (this.keyStoreProvider != null && this.keyStoreProvider.length() > 0) {
            printWriter.print(", \"" + this.keyStoreProvider + "\"");
        }
        printWriter.println(";");
        printWriter.println();
    }
    
    private void writeStorePassURL(final PrintWriter printWriter) {
        printWriter.print("keystorePasswordURL \"");
        printWriter.print(this.storePassURL);
        printWriter.print('\"');
        printWriter.println(";");
        printWriter.println();
    }
    
    private GrantEntry parseGrantEntry() throws ParsingException, IOException {
        final GrantEntry grantEntry = new GrantEntry();
        LinkedList<PrincipalEntry> principals = null;
        boolean b = false;
        this.match("grant");
        while (!this.peek("{")) {
            if (this.peekAndMatch("Codebase")) {
                if (grantEntry.codeBase != null) {
                    throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("multiple.Codebase.expressions"));
                }
                grantEntry.codeBase = this.match("quoted string");
                this.peekAndMatch(",");
            }
            else if (this.peekAndMatch("SignedBy")) {
                if (grantEntry.signedBy != null) {
                    throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("multiple.SignedBy.expressions"));
                }
                grantEntry.signedBy = this.match("quoted string");
                final StringTokenizer stringTokenizer = new StringTokenizer(grantEntry.signedBy, ",", true);
                int n = 0;
                int n2 = 0;
                while (stringTokenizer.hasMoreTokens()) {
                    final String trim = stringTokenizer.nextToken().trim();
                    if (trim.equals(",")) {
                        ++n2;
                    }
                    else {
                        if (trim.length() <= 0) {
                            continue;
                        }
                        ++n;
                    }
                }
                if (n <= n2) {
                    throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("SignedBy.has.empty.alias"));
                }
                this.peekAndMatch(",");
            }
            else {
                if (!this.peekAndMatch("Principal")) {
                    throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("expected.codeBase.or.SignedBy.or.Principal"));
                }
                if (principals == null) {
                    principals = new LinkedList<PrincipalEntry>();
                }
                String match;
                String s;
                if (this.peek("\"")) {
                    match = "PolicyParser.REPLACE_NAME";
                    s = this.match("principal type");
                }
                else {
                    if (this.peek("*")) {
                        this.match("*");
                        match = "WILDCARD_PRINCIPAL_CLASS";
                    }
                    else {
                        match = this.match("principal type");
                    }
                    if (this.peek("*")) {
                        this.match("*");
                        s = "WILDCARD_PRINCIPAL_NAME";
                    }
                    else {
                        s = this.match("quoted string");
                    }
                    if (match.equals("WILDCARD_PRINCIPAL_CLASS") && !s.equals("WILDCARD_PRINCIPAL_NAME")) {
                        if (PolicyParser.debug != null) {
                            PolicyParser.debug.println("disallowing principal that has WILDCARD class but no WILDCARD name");
                        }
                        throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name"));
                    }
                }
                try {
                    s = this.expand(s);
                    if (match.equals("javax.security.auth.x500.X500Principal") && !s.equals("WILDCARD_PRINCIPAL_NAME")) {
                        s = new X500Principal(new X500Principal(s).toString()).getName();
                    }
                    principals.add(new PrincipalEntry(match, s));
                }
                catch (final PropertyExpander.ExpandException ex) {
                    if (PolicyParser.debug != null) {
                        PolicyParser.debug.println("principal name expansion failed: " + s);
                    }
                    b = true;
                }
                this.peekAndMatch(",");
            }
        }
        if (principals != null) {
            grantEntry.principals = principals;
        }
        this.match("{");
        while (!this.peek("}")) {
            if (!this.peek("Permission")) {
                throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("expected.permission.entry"));
            }
            try {
                grantEntry.add(this.parsePermissionEntry());
            }
            catch (final PropertyExpander.ExpandException ex2) {
                if (PolicyParser.debug != null) {
                    PolicyParser.debug.println(ex2.toString());
                }
                this.skipEntry();
            }
            this.match(";");
        }
        this.match("}");
        try {
            if (grantEntry.signedBy != null) {
                grantEntry.signedBy = this.expand(grantEntry.signedBy);
            }
            if (grantEntry.codeBase != null) {
                if (grantEntry.codeBase.equals("${java.ext.dirs}")) {
                    grantEntry.codeBase = "${{java.ext.dirs}}";
                }
                final int index;
                if ((index = grantEntry.codeBase.indexOf("${{java.ext.dirs}}")) < 0) {
                    grantEntry.codeBase = this.expand(grantEntry.codeBase, true).replace(File.separatorChar, '/');
                }
                else {
                    final String[] extDirs = parseExtDirs(grantEntry.codeBase, index);
                    if (extDirs != null && extDirs.length > 0) {
                        for (int i = 0; i < extDirs.length; ++i) {
                            final GrantEntry grantEntry2 = (GrantEntry)grantEntry.clone();
                            grantEntry2.codeBase = extDirs[i];
                            this.add(grantEntry2);
                            if (PolicyParser.debug != null) {
                                PolicyParser.debug.println("creating policy entry for expanded java.ext.dirs path:\n\t\t" + extDirs[i]);
                            }
                        }
                    }
                    b = true;
                }
            }
        }
        catch (final PropertyExpander.ExpandException ex3) {
            if (PolicyParser.debug != null) {
                PolicyParser.debug.println(ex3.toString());
            }
            return null;
        }
        return b ? null : grantEntry;
    }
    
    private PermissionEntry parsePermissionEntry() throws ParsingException, IOException, PropertyExpander.ExpandException {
        final PermissionEntry permissionEntry = new PermissionEntry();
        this.match("Permission");
        permissionEntry.permission = this.match("permission type");
        if (this.peek("\"")) {
            permissionEntry.name = this.expand(this.match("quoted string"));
        }
        if (!this.peek(",")) {
            return permissionEntry;
        }
        this.match(",");
        if (this.peek("\"")) {
            permissionEntry.action = this.expand(this.match("quoted string"));
            if (!this.peek(",")) {
                return permissionEntry;
            }
            this.match(",");
        }
        if (this.peekAndMatch("SignedBy")) {
            permissionEntry.signedBy = this.expand(this.match("quoted string"));
        }
        return permissionEntry;
    }
    
    private DomainEntry parseDomainEntry() throws ParsingException, IOException {
        final boolean b = false;
        Map<String, String> map = new HashMap<String, String>();
        this.match("domain");
        final String match = this.match("domain name");
        while (!this.peek("{")) {
            map = this.parseProperties("{");
        }
        this.match("{");
        final DomainEntry domainEntry = new DomainEntry(match, map);
        while (!this.peek("}")) {
            this.match("keystore");
            final String match2 = this.match("keystore name");
            if (!this.peek("}")) {
                map = this.parseProperties(";");
            }
            this.match(";");
            domainEntry.add(new KeyStoreEntry(match2, map));
        }
        this.match("}");
        return b ? null : domainEntry;
    }
    
    private Map<String, String> parseProperties(final String s) throws ParsingException, IOException {
        final HashMap hashMap = new HashMap();
        while (!this.peek(s)) {
            final String match = this.match("property name");
            this.match("=");
            String expand;
            try {
                expand = this.expand(this.match("quoted string"));
            }
            catch (final PropertyExpander.ExpandException ex) {
                throw new IOException(ex.getLocalizedMessage());
            }
            hashMap.put(match.toLowerCase(Locale.ENGLISH), expand);
        }
        return hashMap;
    }
    
    static String[] parseExtDirs(final String s, final int n) {
        final String property = System.getProperty("java.ext.dirs");
        final String s2 = (n > 0) ? s.substring(0, n) : "file:";
        final int n2 = n + "${{java.ext.dirs}}".length();
        final String s3 = (n2 < s.length()) ? s.substring(n2) : null;
        String[] array = null;
        if (property != null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(property, File.pathSeparator);
            final int countTokens = stringTokenizer.countTokens();
            array = new String[countTokens];
            for (int i = 0; i < countTokens; ++i) {
                array[i] = ParseUtil.encodePath(new File(stringTokenizer.nextToken()).getAbsolutePath());
                if (!array[i].startsWith("/")) {
                    array[i] = "/" + array[i];
                }
                array[i] = s2 + array[i] + ((s3 == null) ? (array[i].endsWith("/") ? "*" : "/*") : s3);
            }
        }
        return array;
    }
    
    private boolean peekAndMatch(final String s) throws ParsingException, IOException {
        if (this.peek(s)) {
            this.match(s);
            return true;
        }
        return false;
    }
    
    private boolean peek(final String s) {
        boolean b = false;
        switch (this.lookahead) {
            case -3: {
                if (s.equalsIgnoreCase(this.st.sval)) {
                    b = true;
                    break;
                }
                break;
            }
            case 44: {
                if (s.equalsIgnoreCase(",")) {
                    b = true;
                    break;
                }
                break;
            }
            case 123: {
                if (s.equalsIgnoreCase("{")) {
                    b = true;
                    break;
                }
                break;
            }
            case 125: {
                if (s.equalsIgnoreCase("}")) {
                    b = true;
                    break;
                }
                break;
            }
            case 34: {
                if (s.equalsIgnoreCase("\"")) {
                    b = true;
                    break;
                }
                break;
            }
            case 42: {
                if (s.equalsIgnoreCase("*")) {
                    b = true;
                    break;
                }
                break;
            }
            case 59: {
                if (s.equalsIgnoreCase(";")) {
                    b = true;
                    break;
                }
                break;
            }
        }
        return b;
    }
    
    private String match(final String s) throws ParsingException, IOException {
        String s2 = null;
        switch (this.lookahead) {
            case -2: {
                throw new ParsingException(this.st.lineno(), s, ResourcesMgr.getString("number.") + String.valueOf(this.st.nval));
            }
            case -1: {
                throw new ParsingException(new MessageFormat(ResourcesMgr.getString("expected.expect.read.end.of.file.")).format(new Object[] { s }));
            }
            case -3: {
                if (s.equalsIgnoreCase(this.st.sval)) {
                    this.lookahead = this.st.nextToken();
                    break;
                }
                if (s.equalsIgnoreCase("permission type")) {
                    s2 = this.st.sval;
                    this.lookahead = this.st.nextToken();
                    break;
                }
                if (s.equalsIgnoreCase("principal type")) {
                    s2 = this.st.sval;
                    this.lookahead = this.st.nextToken();
                    break;
                }
                if (s.equalsIgnoreCase("domain name") || s.equalsIgnoreCase("keystore name") || s.equalsIgnoreCase("property name")) {
                    s2 = this.st.sval;
                    this.lookahead = this.st.nextToken();
                    break;
                }
                throw new ParsingException(this.st.lineno(), s, this.st.sval);
            }
            case 34: {
                if (s.equalsIgnoreCase("quoted string")) {
                    s2 = this.st.sval;
                    this.lookahead = this.st.nextToken();
                    break;
                }
                if (s.equalsIgnoreCase("permission type")) {
                    s2 = this.st.sval;
                    this.lookahead = this.st.nextToken();
                    break;
                }
                if (s.equalsIgnoreCase("principal type")) {
                    s2 = this.st.sval;
                    this.lookahead = this.st.nextToken();
                    break;
                }
                throw new ParsingException(this.st.lineno(), s, this.st.sval);
            }
            case 44: {
                if (s.equalsIgnoreCase(",")) {
                    this.lookahead = this.st.nextToken();
                    break;
                }
                throw new ParsingException(this.st.lineno(), s, ",");
            }
            case 123: {
                if (s.equalsIgnoreCase("{")) {
                    this.lookahead = this.st.nextToken();
                    break;
                }
                throw new ParsingException(this.st.lineno(), s, "{");
            }
            case 125: {
                if (s.equalsIgnoreCase("}")) {
                    this.lookahead = this.st.nextToken();
                    break;
                }
                throw new ParsingException(this.st.lineno(), s, "}");
            }
            case 59: {
                if (s.equalsIgnoreCase(";")) {
                    this.lookahead = this.st.nextToken();
                    break;
                }
                throw new ParsingException(this.st.lineno(), s, ";");
            }
            case 42: {
                if (s.equalsIgnoreCase("*")) {
                    this.lookahead = this.st.nextToken();
                    break;
                }
                throw new ParsingException(this.st.lineno(), s, "*");
            }
            case 61: {
                if (s.equalsIgnoreCase("=")) {
                    this.lookahead = this.st.nextToken();
                    break;
                }
                throw new ParsingException(this.st.lineno(), s, "=");
            }
            default: {
                throw new ParsingException(this.st.lineno(), s, new String(new char[] { (char)this.lookahead }));
            }
        }
        return s2;
    }
    
    private void skipEntry() throws ParsingException, IOException {
        while (this.lookahead != 59) {
            switch (this.lookahead) {
                case -2: {
                    throw new ParsingException(this.st.lineno(), ";", ResourcesMgr.getString("number.") + String.valueOf(this.st.nval));
                }
                case -1: {
                    throw new ParsingException(ResourcesMgr.getString("expected.read.end.of.file."));
                }
                default: {
                    this.lookahead = this.st.nextToken();
                    continue;
                }
            }
        }
    }
    
    public static void main(final String[] array) throws Exception {
        try (final FileReader fileReader = new FileReader(array[0]);
             final FileWriter fileWriter = new FileWriter(array[1])) {
            final PolicyParser policyParser = new PolicyParser(true);
            policyParser.read(fileReader);
            policyParser.write(fileWriter);
        }
    }
    
    static {
        debug = Debug.getInstance("parser", "\t[Policy Parser]");
    }
    
    public static class GrantEntry
    {
        public String signedBy;
        public String codeBase;
        public LinkedList<PrincipalEntry> principals;
        public Vector<PermissionEntry> permissionEntries;
        
        public GrantEntry() {
            this.principals = new LinkedList<PrincipalEntry>();
            this.permissionEntries = new Vector<PermissionEntry>();
        }
        
        public GrantEntry(final String signedBy, final String codeBase) {
            this.codeBase = codeBase;
            this.signedBy = signedBy;
            this.principals = new LinkedList<PrincipalEntry>();
            this.permissionEntries = new Vector<PermissionEntry>();
        }
        
        public void add(final PermissionEntry permissionEntry) {
            this.permissionEntries.addElement(permissionEntry);
        }
        
        public boolean remove(final PrincipalEntry principalEntry) {
            return this.principals.remove(principalEntry);
        }
        
        public boolean remove(final PermissionEntry permissionEntry) {
            return this.permissionEntries.removeElement(permissionEntry);
        }
        
        public boolean contains(final PrincipalEntry principalEntry) {
            return this.principals.contains(principalEntry);
        }
        
        public boolean contains(final PermissionEntry permissionEntry) {
            return this.permissionEntries.contains(permissionEntry);
        }
        
        public Enumeration<PermissionEntry> permissionElements() {
            return this.permissionEntries.elements();
        }
        
        public void write(final PrintWriter printWriter) {
            printWriter.print("grant");
            if (this.signedBy != null) {
                printWriter.print(" signedBy \"");
                printWriter.print(this.signedBy);
                printWriter.print('\"');
                if (this.codeBase != null) {
                    printWriter.print(", ");
                }
            }
            if (this.codeBase != null) {
                printWriter.print(" codeBase \"");
                printWriter.print(this.codeBase);
                printWriter.print('\"');
                if (this.principals != null && this.principals.size() > 0) {
                    printWriter.print(",\n");
                }
            }
            if (this.principals != null && this.principals.size() > 0) {
                final Iterator<Object> iterator = this.principals.iterator();
                while (iterator.hasNext()) {
                    printWriter.print("      ");
                    iterator.next().write(printWriter);
                    if (iterator.hasNext()) {
                        printWriter.print(",\n");
                    }
                }
            }
            printWriter.println(" {");
            final Enumeration<PermissionEntry> elements = this.permissionEntries.elements();
            while (elements.hasMoreElements()) {
                final PermissionEntry permissionEntry = elements.nextElement();
                printWriter.write("  ");
                permissionEntry.write(printWriter);
            }
            printWriter.println("};");
        }
        
        public Object clone() {
            final GrantEntry grantEntry = new GrantEntry();
            grantEntry.codeBase = this.codeBase;
            grantEntry.signedBy = this.signedBy;
            grantEntry.principals = new LinkedList<PrincipalEntry>(this.principals);
            grantEntry.permissionEntries = new Vector<PermissionEntry>(this.permissionEntries);
            return grantEntry;
        }
    }
    
    public static class PrincipalEntry implements Principal
    {
        public static final String WILDCARD_CLASS = "WILDCARD_PRINCIPAL_CLASS";
        public static final String WILDCARD_NAME = "WILDCARD_PRINCIPAL_NAME";
        public static final String REPLACE_NAME = "PolicyParser.REPLACE_NAME";
        String principalClass;
        String principalName;
        
        public PrincipalEntry(final String principalClass, final String principalName) {
            if (principalClass == null || principalName == null) {
                throw new NullPointerException(ResourcesMgr.getString("null.principalClass.or.principalName"));
            }
            this.principalClass = principalClass;
            this.principalName = principalName;
        }
        
        boolean isWildcardName() {
            return this.principalName.equals("WILDCARD_PRINCIPAL_NAME");
        }
        
        boolean isWildcardClass() {
            return this.principalClass.equals("WILDCARD_PRINCIPAL_CLASS");
        }
        
        boolean isReplaceName() {
            return this.principalClass.equals("PolicyParser.REPLACE_NAME");
        }
        
        public String getPrincipalClass() {
            return this.principalClass;
        }
        
        public String getPrincipalName() {
            return this.principalName;
        }
        
        public String getDisplayClass() {
            if (this.isWildcardClass()) {
                return "*";
            }
            if (this.isReplaceName()) {
                return "";
            }
            return this.principalClass;
        }
        
        public String getDisplayName() {
            return this.getDisplayName(false);
        }
        
        public String getDisplayName(final boolean b) {
            if (this.isWildcardName()) {
                return "*";
            }
            if (b) {
                return "\"" + this.principalName + "\"";
            }
            return this.principalName;
        }
        
        @Override
        public String getName() {
            return this.principalName;
        }
        
        @Override
        public String toString() {
            if (!this.isReplaceName()) {
                return this.getDisplayClass() + "/" + this.getDisplayName();
            }
            return this.getDisplayName();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof PrincipalEntry)) {
                return false;
            }
            final PrincipalEntry principalEntry = (PrincipalEntry)o;
            return this.principalClass.equals(principalEntry.principalClass) && this.principalName.equals(principalEntry.principalName);
        }
        
        @Override
        public int hashCode() {
            return this.principalClass.hashCode();
        }
        
        public void write(final PrintWriter printWriter) {
            printWriter.print("principal " + this.getDisplayClass() + " " + this.getDisplayName(true));
        }
    }
    
    public static class PermissionEntry
    {
        public String permission;
        public String name;
        public String action;
        public String signedBy;
        
        public PermissionEntry() {
        }
        
        public PermissionEntry(final String permission, final String name, final String action) {
            this.permission = permission;
            this.name = name;
            this.action = action;
        }
        
        @Override
        public int hashCode() {
            int hashCode = this.permission.hashCode();
            if (this.name != null) {
                hashCode ^= this.name.hashCode();
            }
            if (this.action != null) {
                hashCode ^= this.action.hashCode();
            }
            return hashCode;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof PermissionEntry)) {
                return false;
            }
            final PermissionEntry permissionEntry = (PermissionEntry)o;
            if (this.permission == null) {
                if (permissionEntry.permission != null) {
                    return false;
                }
            }
            else if (!this.permission.equals(permissionEntry.permission)) {
                return false;
            }
            if (this.name == null) {
                if (permissionEntry.name != null) {
                    return false;
                }
            }
            else if (!this.name.equals(permissionEntry.name)) {
                return false;
            }
            if (this.action == null) {
                if (permissionEntry.action != null) {
                    return false;
                }
            }
            else if (!this.action.equals(permissionEntry.action)) {
                return false;
            }
            if (this.signedBy == null) {
                if (permissionEntry.signedBy != null) {
                    return false;
                }
            }
            else if (!this.signedBy.equals(permissionEntry.signedBy)) {
                return false;
            }
            return true;
        }
        
        public void write(final PrintWriter printWriter) {
            printWriter.print("permission ");
            printWriter.print(this.permission);
            if (this.name != null) {
                printWriter.print(" \"");
                printWriter.print(this.name.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\\\""));
                printWriter.print('\"');
            }
            if (this.action != null) {
                printWriter.print(", \"");
                printWriter.print(this.action);
                printWriter.print('\"');
            }
            if (this.signedBy != null) {
                printWriter.print(", signedBy \"");
                printWriter.print(this.signedBy);
                printWriter.print('\"');
            }
            printWriter.println(";");
        }
    }
    
    static class DomainEntry
    {
        private final String name;
        private final Map<String, String> properties;
        private final Map<String, KeyStoreEntry> entries;
        
        DomainEntry(final String name, final Map<String, String> properties) {
            this.name = name;
            this.properties = properties;
            this.entries = new HashMap<String, KeyStoreEntry>();
        }
        
        String getName() {
            return this.name;
        }
        
        Map<String, String> getProperties() {
            return this.properties;
        }
        
        Collection<KeyStoreEntry> getEntries() {
            return this.entries.values();
        }
        
        void add(final KeyStoreEntry keyStoreEntry) throws ParsingException {
            final String name = keyStoreEntry.getName();
            if (!this.entries.containsKey(name)) {
                this.entries.put(name, keyStoreEntry);
                return;
            }
            throw new ParsingException(new MessageFormat(ResourcesMgr.getString("duplicate.keystore.name")).format(new Object[] { name }));
        }
        
        @Override
        public String toString() {
            final StringBuilder append = new StringBuilder("\ndomain ").append(this.name);
            if (this.properties != null) {
                for (final Map.Entry entry : this.properties.entrySet()) {
                    append.append("\n        ").append((String)entry.getKey()).append('=').append((String)entry.getValue());
                }
            }
            append.append(" {\n");
            if (this.entries != null) {
                final Iterator<KeyStoreEntry> iterator2 = this.entries.values().iterator();
                while (iterator2.hasNext()) {
                    append.append(iterator2.next()).append("\n");
                }
            }
            append.append("}");
            return append.toString();
        }
    }
    
    static class KeyStoreEntry
    {
        private final String name;
        private final Map<String, String> properties;
        
        KeyStoreEntry(final String name, final Map<String, String> properties) {
            this.name = name;
            this.properties = properties;
        }
        
        String getName() {
            return this.name;
        }
        
        Map<String, String> getProperties() {
            return this.properties;
        }
        
        @Override
        public String toString() {
            final StringBuilder append = new StringBuilder("\n    keystore ").append(this.name);
            if (this.properties != null) {
                for (final Map.Entry entry : this.properties.entrySet()) {
                    append.append("\n        ").append((String)entry.getKey()).append('=').append((String)entry.getValue());
                }
            }
            append.append(";");
            return append.toString();
        }
    }
    
    public static class ParsingException extends GeneralSecurityException
    {
        private static final long serialVersionUID = -4330692689482574072L;
        private String i18nMessage;
        
        public ParsingException(final String i18nMessage) {
            super(i18nMessage);
            this.i18nMessage = i18nMessage;
        }
        
        public ParsingException(final int n, final String s) {
            super("line " + n + ": " + s);
            this.i18nMessage = new MessageFormat(ResourcesMgr.getString("line.number.msg")).format(new Object[] { new Integer(n), s });
        }
        
        public ParsingException(final int n, final String s, final String s2) {
            super("line " + n + ": expected [" + s + "], found [" + s2 + "]");
            this.i18nMessage = new MessageFormat(ResourcesMgr.getString("line.number.expected.expect.found.actual.")).format(new Object[] { new Integer(n), s, s2 });
        }
        
        @Override
        public String getLocalizedMessage() {
            return this.i18nMessage;
        }
    }
}
