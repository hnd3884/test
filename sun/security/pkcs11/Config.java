package sun.security.pkcs11;

import java.util.HashMap;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.ArrayList;
import sun.security.pkcs11.wrapper.Functions;
import java.io.File;
import java.math.BigInteger;
import sun.security.util.PropertyExpander;
import java.io.IOException;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.io.StringReader;
import java.security.ProviderException;
import java.io.InputStream;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import java.util.Set;
import java.io.StreamTokenizer;
import java.io.Reader;
import java.util.Map;

final class Config
{
    static final int ERR_HALT = 1;
    static final int ERR_IGNORE_ALL = 2;
    static final int ERR_IGNORE_LIB = 3;
    private static final boolean staticAllowSingleThreadedModules;
    private static final Map<String, Config> configMap;
    private static final boolean DEBUG = false;
    private Reader reader;
    private StreamTokenizer st;
    private Set<String> parsedKeywords;
    private String name;
    private String library;
    private String description;
    private int slotID;
    private int slotListIndex;
    private Set<Long> enabledMechanisms;
    private Set<Long> disabledMechanisms;
    private boolean showInfo;
    private TemplateManager templateManager;
    private int handleStartupErrors;
    private boolean keyStoreCompatibilityMode;
    private boolean explicitCancel;
    private int insertionCheckInterval;
    private boolean omitInitialize;
    private boolean allowSingleThreadedModules;
    private String functionList;
    private boolean nssUseSecmod;
    private String nssLibraryDirectory;
    private String nssSecmodDirectory;
    private String nssModule;
    private Secmod.DbMode nssDbMode;
    private boolean nssNetscapeDbWorkaround;
    private String nssArgs;
    private boolean nssUseSecmodTrust;
    private boolean useEcX963Encoding;
    private boolean nssOptimizeSpace;
    private static final CK_ATTRIBUTE[] CK_A0;
    
    static Config getConfig(final String s, final InputStream inputStream) {
        final Config config = Config.configMap.get(s);
        if (config != null) {
            return config;
        }
        try {
            final Config config2 = new Config(s, inputStream);
            Config.configMap.put(s, config2);
            return config2;
        }
        catch (final Exception ex) {
            throw new ProviderException("Error parsing configuration", ex);
        }
    }
    
    static Config removeConfig(final String s) {
        return Config.configMap.remove(s);
    }
    
    private static void debug(final Object o) {
    }
    
    private Config(final String s, InputStream inputStream) throws IOException {
        this.slotID = -1;
        this.slotListIndex = -1;
        this.showInfo = false;
        this.handleStartupErrors = 1;
        this.keyStoreCompatibilityMode = true;
        this.explicitCancel = true;
        this.insertionCheckInterval = 2000;
        this.omitInitialize = false;
        this.allowSingleThreadedModules = true;
        this.functionList = "C_GetFunctionList";
        this.nssDbMode = Secmod.DbMode.READ_WRITE;
        this.nssNetscapeDbWorkaround = true;
        this.nssUseSecmodTrust = false;
        this.useEcX963Encoding = false;
        this.nssOptimizeSpace = false;
        if (inputStream == null) {
            if (s.startsWith("--")) {
                this.reader = new StringReader(s.substring(2).replace("\\n", "\n"));
            }
            else {
                inputStream = new FileInputStream(expand(s));
            }
        }
        if (this.reader == null) {
            this.reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
        }
        this.parsedKeywords = new HashSet<String>();
        this.st = new StreamTokenizer(this.reader);
        this.setupTokenizer();
        this.parse();
    }
    
    String getName() {
        return this.name;
    }
    
    String getLibrary() {
        return this.library;
    }
    
    String getDescription() {
        if (this.description != null) {
            return this.description;
        }
        return "SunPKCS11-" + this.name + " using library " + this.library;
    }
    
    int getSlotID() {
        return this.slotID;
    }
    
    int getSlotListIndex() {
        if (this.slotID == -1 && this.slotListIndex == -1) {
            return 0;
        }
        return this.slotListIndex;
    }
    
    boolean getShowInfo() {
        return SunPKCS11.debug != null || this.showInfo;
    }
    
    TemplateManager getTemplateManager() {
        if (this.templateManager == null) {
            this.templateManager = new TemplateManager();
        }
        return this.templateManager;
    }
    
    boolean isEnabled(final long n) {
        if (this.enabledMechanisms != null) {
            return this.enabledMechanisms.contains(n);
        }
        return this.disabledMechanisms == null || !this.disabledMechanisms.contains(n);
    }
    
    int getHandleStartupErrors() {
        return this.handleStartupErrors;
    }
    
    boolean getKeyStoreCompatibilityMode() {
        return this.keyStoreCompatibilityMode;
    }
    
    boolean getExplicitCancel() {
        return this.explicitCancel;
    }
    
    int getInsertionCheckInterval() {
        return this.insertionCheckInterval;
    }
    
    boolean getOmitInitialize() {
        return this.omitInitialize;
    }
    
    boolean getAllowSingleThreadedModules() {
        return Config.staticAllowSingleThreadedModules && this.allowSingleThreadedModules;
    }
    
    String getFunctionList() {
        return this.functionList;
    }
    
    boolean getNssUseSecmod() {
        return this.nssUseSecmod;
    }
    
    String getNssLibraryDirectory() {
        return this.nssLibraryDirectory;
    }
    
    String getNssSecmodDirectory() {
        return this.nssSecmodDirectory;
    }
    
    String getNssModule() {
        return this.nssModule;
    }
    
    Secmod.DbMode getNssDbMode() {
        return this.nssDbMode;
    }
    
    public boolean getNssNetscapeDbWorkaround() {
        return this.nssUseSecmod && this.nssNetscapeDbWorkaround;
    }
    
    String getNssArgs() {
        return this.nssArgs;
    }
    
    boolean getNssUseSecmodTrust() {
        return this.nssUseSecmodTrust;
    }
    
    boolean getUseEcX963Encoding() {
        return this.useEcX963Encoding;
    }
    
    boolean getNssOptimizeSpace() {
        return this.nssOptimizeSpace;
    }
    
    private static String expand(final String s) throws IOException {
        try {
            return PropertyExpander.expand(s);
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    private void setupTokenizer() {
        this.st.resetSyntax();
        this.st.wordChars(97, 122);
        this.st.wordChars(65, 90);
        this.st.wordChars(48, 57);
        this.st.wordChars(58, 58);
        this.st.wordChars(46, 46);
        this.st.wordChars(95, 95);
        this.st.wordChars(45, 45);
        this.st.wordChars(47, 47);
        this.st.wordChars(92, 92);
        this.st.wordChars(36, 36);
        this.st.wordChars(123, 123);
        this.st.wordChars(125, 125);
        this.st.wordChars(42, 42);
        this.st.wordChars(43, 43);
        this.st.wordChars(126, 126);
        this.st.whitespaceChars(0, 32);
        this.st.commentChar(35);
        this.st.eolIsSignificant(true);
        this.st.quoteChar(34);
    }
    
    private ConfigurationException excToken(final String s) {
        return new ConfigurationException(s + " " + this.st);
    }
    
    private ConfigurationException excLine(final String s) {
        return new ConfigurationException(s + ", line " + this.st.lineno());
    }
    
    private void parse() throws IOException {
        while (true) {
            final int nextToken = this.nextToken();
            if (nextToken == -1) {
                this.reader.close();
                this.reader = null;
                this.st = null;
                this.parsedKeywords = null;
                if (this.name == null) {
                    throw new ConfigurationException("name must be specified");
                }
                if (!this.nssUseSecmod) {
                    if (this.library == null) {
                        throw new ConfigurationException("library must be specified");
                    }
                }
                else {
                    if (this.library != null) {
                        throw new ConfigurationException("library must not be specified in NSS mode");
                    }
                    if (this.slotID != -1 || this.slotListIndex != -1) {
                        throw new ConfigurationException("slot and slotListIndex must not be specified in NSS mode");
                    }
                    if (this.nssArgs != null) {
                        throw new ConfigurationException("nssArgs must not be specified in NSS mode");
                    }
                    if (this.nssUseSecmodTrust) {
                        throw new ConfigurationException("nssUseSecmodTrust is an internal option and must not be specified in NSS mode");
                    }
                }
            }
            else {
                if (nextToken == 10) {
                    continue;
                }
                if (nextToken != -3) {
                    throw this.excToken("Unexpected token:");
                }
                final String sval = this.st.sval;
                if (sval.equals("name")) {
                    this.name = this.parseStringEntry(sval);
                }
                else if (sval.equals("library")) {
                    this.library = this.parseLibrary(sval);
                }
                else if (sval.equals("description")) {
                    this.parseDescription(sval);
                }
                else if (sval.equals("slot")) {
                    this.parseSlotID(sval);
                }
                else if (sval.equals("slotListIndex")) {
                    this.parseSlotListIndex(sval);
                }
                else if (sval.equals("enabledMechanisms")) {
                    this.parseEnabledMechanisms(sval);
                }
                else if (sval.equals("disabledMechanisms")) {
                    this.parseDisabledMechanisms(sval);
                }
                else if (sval.equals("attributes")) {
                    this.parseAttributes(sval);
                }
                else if (sval.equals("handleStartupErrors")) {
                    this.parseHandleStartupErrors(sval);
                }
                else if (sval.endsWith("insertionCheckInterval")) {
                    this.insertionCheckInterval = this.parseIntegerEntry(sval);
                    if (this.insertionCheckInterval < 100) {
                        throw this.excLine(sval + " must be at least 100 ms");
                    }
                }
                else if (sval.equals("showInfo")) {
                    this.showInfo = this.parseBooleanEntry(sval);
                }
                else if (sval.equals("keyStoreCompatibilityMode")) {
                    this.keyStoreCompatibilityMode = this.parseBooleanEntry(sval);
                }
                else if (sval.equals("explicitCancel")) {
                    this.explicitCancel = this.parseBooleanEntry(sval);
                }
                else if (sval.equals("omitInitialize")) {
                    this.omitInitialize = this.parseBooleanEntry(sval);
                }
                else if (sval.equals("allowSingleThreadedModules")) {
                    this.allowSingleThreadedModules = this.parseBooleanEntry(sval);
                }
                else if (sval.equals("functionList")) {
                    this.functionList = this.parseStringEntry(sval);
                }
                else if (sval.equals("nssUseSecmod")) {
                    this.nssUseSecmod = this.parseBooleanEntry(sval);
                }
                else if (sval.equals("nssLibraryDirectory")) {
                    this.nssLibraryDirectory = this.parseLibrary(sval);
                    this.nssUseSecmod = true;
                }
                else if (sval.equals("nssSecmodDirectory")) {
                    this.nssSecmodDirectory = expand(this.parseStringEntry(sval));
                    this.nssUseSecmod = true;
                }
                else if (sval.equals("nssModule")) {
                    this.nssModule = this.parseStringEntry(sval);
                    this.nssUseSecmod = true;
                }
                else if (sval.equals("nssDbMode")) {
                    final String stringEntry = this.parseStringEntry(sval);
                    if (stringEntry.equals("readWrite")) {
                        this.nssDbMode = Secmod.DbMode.READ_WRITE;
                    }
                    else if (stringEntry.equals("readOnly")) {
                        this.nssDbMode = Secmod.DbMode.READ_ONLY;
                    }
                    else {
                        if (!stringEntry.equals("noDb")) {
                            throw this.excToken("nssDbMode must be one of readWrite, readOnly, and noDb:");
                        }
                        this.nssDbMode = Secmod.DbMode.NO_DB;
                    }
                    this.nssUseSecmod = true;
                }
                else if (sval.equals("nssNetscapeDbWorkaround")) {
                    this.nssNetscapeDbWorkaround = this.parseBooleanEntry(sval);
                    this.nssUseSecmod = true;
                }
                else if (sval.equals("nssArgs")) {
                    this.parseNSSArgs(sval);
                }
                else if (sval.equals("nssUseSecmodTrust")) {
                    this.nssUseSecmodTrust = this.parseBooleanEntry(sval);
                }
                else if (sval.equals("useEcX963Encoding")) {
                    this.useEcX963Encoding = this.parseBooleanEntry(sval);
                }
                else {
                    if (!sval.equals("nssOptimizeSpace")) {
                        throw new ConfigurationException("Unknown keyword '" + sval + "', line " + this.st.lineno());
                    }
                    this.nssOptimizeSpace = this.parseBooleanEntry(sval);
                }
                this.parsedKeywords.add(sval);
            }
        }
    }
    
    private int nextToken() throws IOException {
        final int nextToken = this.st.nextToken();
        debug(this.st);
        return nextToken;
    }
    
    private void parseEquals() throws IOException {
        if (this.nextToken() != 61) {
            throw this.excToken("Expected '=', read");
        }
    }
    
    private void parseOpenBraces() throws IOException {
        int nextToken;
        do {
            nextToken = this.nextToken();
        } while (nextToken == 10);
        if (nextToken == -3 && this.st.sval.equals("{")) {
            return;
        }
        throw this.excToken("Expected '{', read");
    }
    
    private boolean isCloseBraces(final int n) {
        return n == -3 && this.st.sval.equals("}");
    }
    
    private String parseWord() throws IOException {
        if (this.nextToken() != -3) {
            throw this.excToken("Unexpected value:");
        }
        return this.st.sval;
    }
    
    private String parseStringEntry(final String s) throws IOException {
        this.checkDup(s);
        this.parseEquals();
        final int nextToken = this.nextToken();
        if (nextToken != -3 && nextToken != 34) {
            throw this.excToken("Unexpected value:");
        }
        final String sval = this.st.sval;
        debug(s + ": " + sval);
        return sval;
    }
    
    private boolean parseBooleanEntry(final String s) throws IOException {
        this.checkDup(s);
        this.parseEquals();
        final boolean boolean1 = this.parseBoolean();
        debug(s + ": " + boolean1);
        return boolean1;
    }
    
    private int parseIntegerEntry(final String s) throws IOException {
        this.checkDup(s);
        this.parseEquals();
        final int decodeNumber = this.decodeNumber(this.parseWord());
        debug(s + ": " + decodeNumber);
        return decodeNumber;
    }
    
    private boolean parseBoolean() throws IOException {
        final String word = this.parseWord();
        switch (word) {
            case "true": {
                return true;
            }
            case "false": {
                return false;
            }
            default: {
                throw this.excToken("Expected boolean value, read:");
            }
        }
    }
    
    private String parseLine() throws IOException {
        String s = null;
        while (true) {
            final int nextToken = this.nextToken();
            if (nextToken != 10 && nextToken != -1) {
                if (nextToken != -3 && nextToken != 34) {
                    throw this.excToken("Unexpected value");
                }
                if (s == null) {
                    s = this.st.sval;
                }
                else {
                    s = s + " " + this.st.sval;
                }
            }
            else {
                if (s == null) {
                    throw this.excToken("Unexpected empty line");
                }
                return s;
            }
        }
    }
    
    private int decodeNumber(final String s) throws IOException {
        try {
            if (s.startsWith("0x") || s.startsWith("0X")) {
                return Integer.parseInt(s.substring(2), 16);
            }
            return Integer.parseInt(s);
        }
        catch (final NumberFormatException ex) {
            throw this.excToken("Expected number, read");
        }
    }
    
    private static boolean isNumber(final String s) {
        if (s.length() == 0) {
            return false;
        }
        final char char1 = s.charAt(0);
        return char1 >= '0' && char1 <= '9';
    }
    
    private void parseComma() throws IOException {
        if (this.nextToken() != 44) {
            throw this.excToken("Expected ',', read");
        }
    }
    
    private static boolean isByteArray(final String s) {
        return s.startsWith("0h");
    }
    
    private byte[] decodeByteArray(String substring) throws IOException {
        if (!substring.startsWith("0h")) {
            throw this.excToken("Expected byte array value, read");
        }
        substring = substring.substring(2);
        try {
            return new BigInteger(substring, 16).toByteArray();
        }
        catch (final NumberFormatException ex) {
            throw this.excToken("Expected byte array value, read");
        }
    }
    
    private void checkDup(final String s) throws IOException {
        if (this.parsedKeywords.contains(s)) {
            throw this.excLine(s + " must only be specified once");
        }
    }
    
    private String parseLibrary(final String s) throws IOException {
        this.checkDup(s);
        this.parseEquals();
        String s2 = expand(this.parseLine());
        final int index = s2.indexOf("/$ISA/");
        if (index != -1) {
            final String property = System.getProperty("os.name", "");
            final String property2 = System.getProperty("os.arch", "");
            final String substring = s2.substring(0, index);
            final String substring2 = s2.substring(index + 5);
            if (property.equals("SunOS") && property2.equals("sparcv9")) {
                s2 = substring + "/sparcv9" + substring2;
            }
            else if (property.equals("SunOS") && property2.equals("amd64")) {
                s2 = substring + "/amd64" + substring2;
            }
            else {
                s2 = substring + substring2;
            }
        }
        debug(s + ": " + s2);
        if (!new File(s2).isAbsolute()) {
            throw new ConfigurationException("Absolute path required for library value: " + s2);
        }
        return s2;
    }
    
    private void parseDescription(final String s) throws IOException {
        this.checkDup(s);
        this.parseEquals();
        this.description = this.parseLine();
        debug("description: " + this.description);
    }
    
    private void parseSlotID(final String s) throws IOException {
        if (this.slotID >= 0) {
            throw this.excLine("Duplicate slot definition");
        }
        if (this.slotListIndex >= 0) {
            throw this.excLine("Only one of slot and slotListIndex must be specified");
        }
        this.parseEquals();
        this.slotID = this.decodeNumber(this.parseWord());
        debug("slot: " + this.slotID);
    }
    
    private void parseSlotListIndex(final String s) throws IOException {
        if (this.slotListIndex >= 0) {
            throw this.excLine("Duplicate slotListIndex definition");
        }
        if (this.slotID >= 0) {
            throw this.excLine("Only one of slot and slotListIndex must be specified");
        }
        this.parseEquals();
        this.slotListIndex = this.decodeNumber(this.parseWord());
        debug("slotListIndex: " + this.slotListIndex);
    }
    
    private void parseEnabledMechanisms(final String s) throws IOException {
        this.enabledMechanisms = this.parseMechanisms(s);
    }
    
    private void parseDisabledMechanisms(final String s) throws IOException {
        this.disabledMechanisms = this.parseMechanisms(s);
    }
    
    private Set<Long> parseMechanisms(final String s) throws IOException {
        this.checkDup(s);
        final HashSet set = new HashSet();
        this.parseEquals();
        this.parseOpenBraces();
        while (true) {
            final int nextToken = this.nextToken();
            if (this.isCloseBraces(nextToken)) {
                return set;
            }
            if (nextToken == 10) {
                continue;
            }
            if (nextToken != -3) {
                throw this.excToken("Expected mechanism, read");
            }
            set.add(this.parseMechanism(this.st.sval));
        }
    }
    
    private long parseMechanism(final String s) throws IOException {
        if (isNumber(s)) {
            return this.decodeNumber(s);
        }
        try {
            return Functions.getMechanismId(s);
        }
        catch (final IllegalArgumentException ex) {
            throw this.excLine("Unknown mechanism: " + s);
        }
    }
    
    private void parseAttributes(final String s) throws IOException {
        if (this.templateManager == null) {
            this.templateManager = new TemplateManager();
        }
        final int nextToken = this.nextToken();
        if (nextToken == 61) {
            final String word = this.parseWord();
            if (!word.equals("compatibility")) {
                throw this.excLine("Expected 'compatibility', read " + word);
            }
            this.setCompatibilityAttributes();
        }
        else {
            if (nextToken != 40) {
                throw this.excToken("Expected '(' or '=', read");
            }
            final String operation = this.parseOperation();
            this.parseComma();
            final long objectClass = this.parseObjectClass();
            this.parseComma();
            final long keyAlgorithm = this.parseKeyAlgorithm();
            if (this.nextToken() != 41) {
                throw this.excToken("Expected ')', read");
            }
            this.parseEquals();
            this.parseOpenBraces();
            final ArrayList list = new ArrayList();
            while (true) {
                final int nextToken2 = this.nextToken();
                if (this.isCloseBraces(nextToken2)) {
                    this.templateManager.addTemplate(operation, objectClass, keyAlgorithm, (CK_ATTRIBUTE[])list.toArray(Config.CK_A0));
                    return;
                }
                if (nextToken2 == 10) {
                    continue;
                }
                if (nextToken2 != -3) {
                    throw this.excToken("Expected mechanism, read");
                }
                final long decodeAttributeName = this.decodeAttributeName(this.st.sval);
                this.parseEquals();
                list.add(this.decodeAttributeValue(decodeAttributeName, this.parseWord()));
            }
        }
    }
    
    private void setCompatibilityAttributes() {
        this.templateManager.addTemplate("*", 4L, 2147483426L, new CK_ATTRIBUTE[] { CK_ATTRIBUTE.TOKEN_FALSE, CK_ATTRIBUTE.SENSITIVE_FALSE, CK_ATTRIBUTE.EXTRACTABLE_TRUE, CK_ATTRIBUTE.ENCRYPT_TRUE, CK_ATTRIBUTE.DECRYPT_TRUE, CK_ATTRIBUTE.WRAP_TRUE, CK_ATTRIBUTE.UNWRAP_TRUE });
        this.templateManager.addTemplate("*", 4L, 16L, new CK_ATTRIBUTE[] { CK_ATTRIBUTE.SIGN_TRUE, CK_ATTRIBUTE.VERIFY_TRUE, CK_ATTRIBUTE.ENCRYPT_NULL, CK_ATTRIBUTE.DECRYPT_NULL, CK_ATTRIBUTE.WRAP_NULL, CK_ATTRIBUTE.UNWRAP_NULL, CK_ATTRIBUTE.DERIVE_TRUE });
        this.templateManager.addTemplate("*", 3L, 2147483426L, new CK_ATTRIBUTE[] { CK_ATTRIBUTE.TOKEN_FALSE, CK_ATTRIBUTE.SENSITIVE_FALSE, CK_ATTRIBUTE.EXTRACTABLE_TRUE });
        this.templateManager.addTemplate("*", 2L, 2147483426L, new CK_ATTRIBUTE[] { CK_ATTRIBUTE.TOKEN_FALSE });
        this.templateManager.addTemplate("*", 3L, 0L, new CK_ATTRIBUTE[] { CK_ATTRIBUTE.DECRYPT_TRUE, CK_ATTRIBUTE.SIGN_TRUE, CK_ATTRIBUTE.SIGN_RECOVER_TRUE, CK_ATTRIBUTE.UNWRAP_TRUE });
        this.templateManager.addTemplate("*", 2L, 0L, new CK_ATTRIBUTE[] { CK_ATTRIBUTE.ENCRYPT_TRUE, CK_ATTRIBUTE.VERIFY_TRUE, CK_ATTRIBUTE.VERIFY_RECOVER_TRUE, CK_ATTRIBUTE.WRAP_TRUE });
        this.templateManager.addTemplate("*", 3L, 1L, new CK_ATTRIBUTE[] { CK_ATTRIBUTE.SIGN_TRUE });
        this.templateManager.addTemplate("*", 2L, 1L, new CK_ATTRIBUTE[] { CK_ATTRIBUTE.VERIFY_TRUE });
        this.templateManager.addTemplate("*", 3L, 2L, new CK_ATTRIBUTE[] { CK_ATTRIBUTE.DERIVE_TRUE });
        this.templateManager.addTemplate("*", 3L, 3L, new CK_ATTRIBUTE[] { CK_ATTRIBUTE.SIGN_TRUE, CK_ATTRIBUTE.DERIVE_TRUE });
        this.templateManager.addTemplate("*", 2L, 3L, new CK_ATTRIBUTE[] { CK_ATTRIBUTE.VERIFY_TRUE });
    }
    
    private String parseOperation() throws IOException {
        final String word;
        final String s = word = this.parseWord();
        switch (word) {
            case "*": {
                return "*";
            }
            case "generate": {
                return "generate";
            }
            case "import": {
                return "import";
            }
            default: {
                throw this.excLine("Unknown operation " + s);
            }
        }
    }
    
    private long parseObjectClass() throws IOException {
        final String word = this.parseWord();
        try {
            return Functions.getObjectClassId(word);
        }
        catch (final IllegalArgumentException ex) {
            throw this.excLine("Unknown object class " + word);
        }
    }
    
    private long parseKeyAlgorithm() throws IOException {
        final String word = this.parseWord();
        if (isNumber(word)) {
            return this.decodeNumber(word);
        }
        try {
            return Functions.getKeyId(word);
        }
        catch (final IllegalArgumentException ex) {
            throw this.excLine("Unknown key algorithm " + word);
        }
    }
    
    private long decodeAttributeName(final String s) throws IOException {
        if (isNumber(s)) {
            return this.decodeNumber(s);
        }
        try {
            return Functions.getAttributeId(s);
        }
        catch (final IllegalArgumentException ex) {
            throw this.excLine("Unknown attribute name " + s);
        }
    }
    
    private CK_ATTRIBUTE decodeAttributeValue(final long n, final String s) throws IOException {
        if (s.equals("null")) {
            return new CK_ATTRIBUTE(n);
        }
        if (s.equals("true")) {
            return new CK_ATTRIBUTE(n, true);
        }
        if (s.equals("false")) {
            return new CK_ATTRIBUTE(n, false);
        }
        if (isByteArray(s)) {
            return new CK_ATTRIBUTE(n, this.decodeByteArray(s));
        }
        if (isNumber(s)) {
            return new CK_ATTRIBUTE(n, (Object)this.decodeNumber(s));
        }
        throw this.excLine("Unknown attribute value " + s);
    }
    
    private void parseNSSArgs(final String s) throws IOException {
        this.checkDup(s);
        this.parseEquals();
        if (this.nextToken() != 34) {
            throw this.excToken("Expected quoted string");
        }
        this.nssArgs = expand(this.st.sval);
        debug("nssArgs: " + this.nssArgs);
    }
    
    private void parseHandleStartupErrors(final String s) throws IOException {
        this.checkDup(s);
        this.parseEquals();
        final String word = this.parseWord();
        if (word.equals("ignoreAll")) {
            this.handleStartupErrors = 2;
        }
        else if (word.equals("ignoreMissingLibrary")) {
            this.handleStartupErrors = 3;
        }
        else {
            if (!word.equals("halt")) {
                throw this.excToken("Invalid value for handleStartupErrors:");
            }
            this.handleStartupErrors = 1;
        }
        debug("handleStartupErrors: " + this.handleStartupErrors);
    }
    
    static {
        if ("false".equalsIgnoreCase(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.security.pkcs11.allowSingleThreadedModules")))) {
            staticAllowSingleThreadedModules = false;
        }
        else {
            staticAllowSingleThreadedModules = true;
        }
        configMap = new HashMap<String, Config>();
        CK_A0 = new CK_ATTRIBUTE[0];
    }
}
