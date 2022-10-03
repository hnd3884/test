package org.owasp.esapi.reference;

import java.util.Hashtable;
import java.util.HashMap;
import org.owasp.esapi.ESAPI;
import java.util.regex.PatternSyntaxException;
import java.util.ArrayList;
import java.net.URL;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Collections;
import org.apache.commons.lang.text.StrTokenizer;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import org.owasp.esapi.errors.ConfigurationException;
import org.owasp.esapi.configuration.EsapiPropertyManager;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.Properties;
import org.owasp.esapi.SecurityConfiguration;

public class DefaultSecurityConfiguration implements SecurityConfiguration
{
    private static volatile SecurityConfiguration instance;
    private Properties properties;
    private String cipherXformFromESAPIProp;
    private String cipherXformCurrent;
    public static final String DEFAULT_RESOURCE_FILE = "ESAPI.properties";
    public static final String REMEMBER_TOKEN_DURATION = "Authenticator.RememberTokenDuration";
    public static final String IDLE_TIMEOUT_DURATION = "Authenticator.IdleTimeoutDuration";
    public static final String ABSOLUTE_TIMEOUT_DURATION = "Authenticator.AbsoluteTimeoutDuration";
    public static final String ALLOWED_LOGIN_ATTEMPTS = "Authenticator.AllowedLoginAttempts";
    public static final String USERNAME_PARAMETER_NAME = "Authenticator.UsernameParameterName";
    public static final String PASSWORD_PARAMETER_NAME = "Authenticator.PasswordParameterName";
    public static final String MAX_OLD_PASSWORD_HASHES = "Authenticator.MaxOldPasswordHashes";
    public static final String ALLOW_MULTIPLE_ENCODING = "Encoder.AllowMultipleEncoding";
    public static final String ALLOW_MIXED_ENCODING = "Encoder.AllowMixedEncoding";
    public static final String CANONICALIZATION_CODECS = "Encoder.DefaultCodecList";
    public static final String DISABLE_INTRUSION_DETECTION = "IntrusionDetector.Disable";
    public static final String MASTER_KEY = "Encryptor.MasterKey";
    public static final String MASTER_SALT = "Encryptor.MasterSalt";
    public static final String KEY_LENGTH = "Encryptor.EncryptionKeyLength";
    public static final String ENCRYPTION_ALGORITHM = "Encryptor.EncryptionAlgorithm";
    public static final String HASH_ALGORITHM = "Encryptor.HashAlgorithm";
    public static final String HASH_ITERATIONS = "Encryptor.HashIterations";
    public static final String CHARACTER_ENCODING = "Encryptor.CharacterEncoding";
    public static final String RANDOM_ALGORITHM = "Encryptor.RandomAlgorithm";
    public static final String DIGITAL_SIGNATURE_ALGORITHM = "Encryptor.DigitalSignatureAlgorithm";
    public static final String DIGITAL_SIGNATURE_KEY_LENGTH = "Encryptor.DigitalSignatureKeyLength";
    public static final String PREFERRED_JCE_PROVIDER = "Encryptor.PreferredJCEProvider";
    public static final String CIPHER_TRANSFORMATION_IMPLEMENTATION = "Encryptor.CipherTransformation";
    public static final String CIPHERTEXT_USE_MAC = "Encryptor.CipherText.useMAC";
    public static final String PLAINTEXT_OVERWRITE = "Encryptor.PlainText.overwrite";
    public static final String IV_TYPE = "Encryptor.ChooseIVMethod";
    public static final String FIXED_IV = "Encryptor.fixedIV";
    public static final String COMBINED_CIPHER_MODES = "Encryptor.cipher_modes.combined_modes";
    public static final String ADDITIONAL_ALLOWED_CIPHER_MODES = "Encryptor.cipher_modes.additional_allowed";
    public static final String KDF_PRF_ALG = "Encryptor.KDF.PRF";
    public static final String PRINT_PROPERTIES_WHEN_LOADED = "ESAPI.printProperties";
    public static final String WORKING_DIRECTORY = "Executor.WorkingDirectory";
    public static final String APPROVED_EXECUTABLES = "Executor.ApprovedExecutables";
    public static final String FORCE_HTTPONLYSESSION = "HttpUtilities.ForceHttpOnlySession";
    public static final String FORCE_SECURESESSION = "HttpUtilities.SecureSession";
    public static final String FORCE_HTTPONLYCOOKIES = "HttpUtilities.ForceHttpOnlyCookies";
    public static final String FORCE_SECURECOOKIES = "HttpUtilities.ForceSecureCookies";
    public static final String MAX_HTTP_HEADER_SIZE = "HttpUtilities.MaxHeaderSize";
    public static final String UPLOAD_DIRECTORY = "HttpUtilities.UploadDir";
    public static final String UPLOAD_TEMP_DIRECTORY = "HttpUtilities.UploadTempDir";
    public static final String APPROVED_UPLOAD_EXTENSIONS = "HttpUtilities.ApprovedUploadExtensions";
    public static final String MAX_UPLOAD_FILE_BYTES = "HttpUtilities.MaxUploadFileBytes";
    public static final String RESPONSE_CONTENT_TYPE = "HttpUtilities.ResponseContentType";
    public static final String HTTP_SESSION_ID_NAME = "HttpUtilities.HttpSessionIdName";
    public static final String APPLICATION_NAME = "Logger.ApplicationName";
    public static final String LOG_LEVEL = "Logger.LogLevel";
    public static final String LOG_FILE_NAME = "Logger.LogFileName";
    public static final String MAX_LOG_FILE_SIZE = "Logger.MaxLogFileSize";
    public static final String LOG_ENCODING_REQUIRED = "Logger.LogEncodingRequired";
    public static final String LOG_APPLICATION_NAME = "Logger.LogApplicationName";
    public static final String LOG_SERVER_IP = "Logger.LogServerIP";
    public static final String VALIDATION_PROPERTIES = "Validator.ConfigurationFile";
    public static final String VALIDATION_PROPERTIES_MULTIVALUED = "Validator.ConfigurationFile.MultiValued";
    public static final String ACCEPT_LENIENT_DATES = "Validator.AcceptLenientDates";
    public static final int DEFAULT_MAX_LOG_FILE_SIZE = 10000000;
    protected final int MAX_REDIRECT_LOCATION = 1000;
    @Deprecated
    protected final int MAX_FILE_NAME_LENGTH = 1000;
    public static final String LOG_IMPLEMENTATION = "ESAPI.Logger";
    public static final String AUTHENTICATION_IMPLEMENTATION = "ESAPI.Authenticator";
    public static final String ENCODER_IMPLEMENTATION = "ESAPI.Encoder";
    public static final String ACCESS_CONTROL_IMPLEMENTATION = "ESAPI.AccessControl";
    public static final String ENCRYPTION_IMPLEMENTATION = "ESAPI.Encryptor";
    public static final String INTRUSION_DETECTION_IMPLEMENTATION = "ESAPI.IntrusionDetector";
    public static final String RANDOMIZER_IMPLEMENTATION = "ESAPI.Randomizer";
    public static final String EXECUTOR_IMPLEMENTATION = "ESAPI.Executor";
    public static final String VALIDATOR_IMPLEMENTATION = "ESAPI.Validator";
    public static final String HTTP_UTILITIES_IMPLEMENTATION = "ESAPI.HTTPUtilities";
    public static final String DEFAULT_LOG_IMPLEMENTATION = "org.owasp.esapi.reference.JavaLogFactory";
    public static final String DEFAULT_AUTHENTICATION_IMPLEMENTATION = "org.owasp.esapi.reference.FileBasedAuthenticator";
    public static final String DEFAULT_ENCODER_IMPLEMENTATION = "org.owasp.esapi.reference.DefaultEncoder";
    public static final String DEFAULT_ACCESS_CONTROL_IMPLEMENTATION = "org.owasp.esapi.reference.accesscontrol.DefaultAccessController";
    public static final String DEFAULT_ENCRYPTION_IMPLEMENTATION = "org.owasp.esapi.reference.crypto.JavaEncryptor";
    public static final String DEFAULT_INTRUSION_DETECTION_IMPLEMENTATION = "org.owasp.esapi.reference.DefaultIntrusionDetector";
    public static final String DEFAULT_RANDOMIZER_IMPLEMENTATION = "org.owasp.esapi.reference.DefaultRandomizer";
    public static final String DEFAULT_EXECUTOR_IMPLEMENTATION = "org.owasp.esapi.reference.DefaultExecutor";
    public static final String DEFAULT_HTTP_UTILITIES_IMPLEMENTATION = "org.owasp.esapi.reference.DefaultHTTPUtilities";
    public static final String DEFAULT_VALIDATOR_IMPLEMENTATION = "org.owasp.esapi.reference.DefaultValidator";
    private static final Map<String, Pattern> patternCache;
    private static final String userHome;
    private static String customDirectory;
    private String resourceDirectory;
    private final String resourceFile;
    private EsapiPropertyManager esapiPropertyManager;
    
    public static SecurityConfiguration getInstance() {
        if (DefaultSecurityConfiguration.instance == null) {
            synchronized (DefaultSecurityConfiguration.class) {
                if (DefaultSecurityConfiguration.instance == null) {
                    DefaultSecurityConfiguration.instance = new DefaultSecurityConfiguration();
                }
            }
        }
        return DefaultSecurityConfiguration.instance;
    }
    
    DefaultSecurityConfiguration(final String resourceFile) {
        this.properties = null;
        this.cipherXformFromESAPIProp = null;
        this.cipherXformCurrent = null;
        this.resourceDirectory = ".esapi";
        this.resourceFile = resourceFile;
        this.esapiPropertyManager = new EsapiPropertyManager();
        try {
            this.loadConfiguration();
            this.setCipherXProperties();
        }
        catch (final IOException e) {
            this.logSpecial("Failed to load security configuration", e);
            throw new ConfigurationException("Failed to load security configuration", e);
        }
    }
    
    public DefaultSecurityConfiguration(final Properties properties) {
        this.properties = null;
        this.cipherXformFromESAPIProp = null;
        this.cipherXformCurrent = null;
        this.resourceDirectory = ".esapi";
        this.resourceFile = "ESAPI.properties";
        this.properties = properties;
        this.setCipherXProperties();
    }
    
    public DefaultSecurityConfiguration() {
        this("ESAPI.properties");
    }
    
    private void setCipherXProperties() {
        this.cipherXformFromESAPIProp = this.getESAPIProperty("Encryptor.CipherTransformation", "AES/CBC/PKCS5Padding");
        this.cipherXformCurrent = this.cipherXformFromESAPIProp;
    }
    
    @Override
    public String getApplicationName() {
        return this.getESAPIProperty("Logger.ApplicationName", "DefaultName");
    }
    
    @Override
    public String getLogImplementation() {
        return this.getESAPIProperty("ESAPI.Logger", "org.owasp.esapi.reference.JavaLogFactory");
    }
    
    @Override
    public String getAuthenticationImplementation() {
        return this.getESAPIProperty("ESAPI.Authenticator", "org.owasp.esapi.reference.FileBasedAuthenticator");
    }
    
    @Override
    public String getEncoderImplementation() {
        return this.getESAPIProperty("ESAPI.Encoder", "org.owasp.esapi.reference.DefaultEncoder");
    }
    
    @Override
    public String getAccessControlImplementation() {
        return this.getESAPIProperty("ESAPI.AccessControl", "org.owasp.esapi.reference.accesscontrol.DefaultAccessController");
    }
    
    @Override
    public String getEncryptionImplementation() {
        return this.getESAPIProperty("ESAPI.Encryptor", "org.owasp.esapi.reference.crypto.JavaEncryptor");
    }
    
    @Override
    public String getIntrusionDetectionImplementation() {
        return this.getESAPIProperty("ESAPI.IntrusionDetector", "org.owasp.esapi.reference.DefaultIntrusionDetector");
    }
    
    @Override
    public String getRandomizerImplementation() {
        return this.getESAPIProperty("ESAPI.Randomizer", "org.owasp.esapi.reference.DefaultRandomizer");
    }
    
    @Override
    public String getExecutorImplementation() {
        return this.getESAPIProperty("ESAPI.Executor", "org.owasp.esapi.reference.DefaultExecutor");
    }
    
    @Override
    public String getHTTPUtilitiesImplementation() {
        return this.getESAPIProperty("ESAPI.HTTPUtilities", "org.owasp.esapi.reference.DefaultHTTPUtilities");
    }
    
    @Override
    public String getValidationImplementation() {
        return this.getESAPIProperty("ESAPI.Validator", "org.owasp.esapi.reference.DefaultValidator");
    }
    
    @Override
    public byte[] getMasterKey() {
        final byte[] key = this.getESAPIPropertyEncoded("Encryptor.MasterKey", null);
        if (key == null || key.length == 0) {
            throw new ConfigurationException("Property 'Encryptor.MasterKey' missing or empty in ESAPI.properties file.");
        }
        return key;
    }
    
    @Override
    public void setResourceDirectory(final String dir) {
        this.resourceDirectory = dir;
        this.logSpecial("Reset resource directory to: " + dir, null);
        try {
            this.loadConfiguration();
        }
        catch (final IOException e) {
            this.logSpecial("Failed to load security configuration from " + dir, e);
        }
    }
    
    @Override
    public int getEncryptionKeyLength() {
        return this.getESAPIProperty("Encryptor.EncryptionKeyLength", 128);
    }
    
    @Override
    public byte[] getMasterSalt() {
        final byte[] salt = this.getESAPIPropertyEncoded("Encryptor.MasterSalt", null);
        if (salt == null || salt.length == 0) {
            throw new ConfigurationException("Property 'Encryptor.MasterSalt' missing or empty in ESAPI.properties file.");
        }
        return salt;
    }
    
    @Override
    public List<String> getAllowedExecutables() {
        final String def = "";
        final String[] exList = this.getESAPIProperty("Executor.ApprovedExecutables", def).split(",");
        return Arrays.asList(exList);
    }
    
    @Override
    public List<String> getAllowedFileExtensions() {
        final String def = ".zip,.pdf,.tar,.gz,.xls,.properties,.txt,.xml";
        final String[] extList = this.getESAPIProperty("HttpUtilities.ApprovedUploadExtensions", def).split(",");
        return Arrays.asList(extList);
    }
    
    @Override
    public int getAllowedFileUploadSize() {
        return this.getESAPIProperty("HttpUtilities.MaxUploadFileBytes", 5000000);
    }
    
    private Properties loadPropertiesFromStream(final InputStream is, final String name) throws IOException {
        final Properties config = new Properties();
        try {
            config.load(is);
            this.logSpecial("Loaded '" + name + "' properties file", null);
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (final Exception ex) {}
            }
        }
        return config;
    }
    
    protected void loadConfiguration() throws IOException {
        try {
            this.logSpecial("Attempting to load " + this.resourceFile + " via file I/O.");
            this.properties = this.loadPropertiesFromStream(this.getResourceStream(this.resourceFile), this.resourceFile);
        }
        catch (final Exception iae) {
            this.logSpecial("Loading " + this.resourceFile + " via file I/O failed. Exception was: " + iae);
            this.logSpecial("Attempting to load " + this.resourceFile + " via the classpath.");
            try {
                this.properties = this.loadConfigurationFromClasspath(this.resourceFile);
            }
            catch (final Exception e) {
                this.logSpecial(this.resourceFile + " could not be loaded by any means. Fail.", e);
                throw new ConfigurationException(this.resourceFile + " could not be loaded by any means. Fail.", e);
            }
        }
        if (this.properties != null) {
            final boolean multivalued = this.getESAPIProperty("Validator.ConfigurationFile.MultiValued", false);
            final String validationPropValue = this.getESAPIProperty("Validator.ConfigurationFile", "validation.properties");
            Iterator<String> validationPropFileNames;
            if (multivalued) {
                validationPropFileNames = (Iterator<String>)StrTokenizer.getCSVInstance(validationPropValue);
            }
            else {
                validationPropFileNames = Collections.singletonList(validationPropValue).iterator();
            }
            DefaultSecurityConfiguration.patternCache.clear();
            while (validationPropFileNames.hasNext()) {
                final String validationPropFileName = validationPropFileNames.next();
                Properties validationProperties = null;
                try {
                    this.logSpecial("Attempting to load " + validationPropFileName + " via file I/O.");
                    validationProperties = this.loadPropertiesFromStream(this.getResourceStream(validationPropFileName), validationPropFileName);
                }
                catch (final Exception iae2) {
                    this.logSpecial("Loading " + validationPropFileName + " via file I/O failed.");
                    this.logSpecial("Attempting to load " + validationPropFileName + " via the classpath.");
                    try {
                        validationProperties = this.loadConfigurationFromClasspath(validationPropFileName);
                    }
                    catch (final Exception e2) {
                        this.logSpecial(validationPropFileName + " could not be loaded by any means. fail.", e2);
                    }
                }
                if (validationProperties != null) {
                    for (final String key : ((Hashtable<Object, V>)validationProperties).keySet()) {
                        final String value = validationProperties.getProperty(key);
                        ((Hashtable<String, String>)this.properties).put(key, value);
                    }
                }
                if (this.shouldPrintProperties()) {}
            }
        }
    }
    
    @Override
    public InputStream getResourceStream(final String filename) throws IOException {
        if (filename == null) {
            return null;
        }
        try {
            final File f = this.getResourceFile(filename);
            if (f != null && f.exists()) {
                return new FileInputStream(f);
            }
        }
        catch (final Exception ex) {}
        throw new FileNotFoundException();
    }
    
    @Override
    public File getResourceFile(final String filename) {
        this.logSpecial("Attempting to load " + filename + " as resource file via file I/O.");
        if (filename == null) {
            this.logSpecial("Failed to load properties via FileIO. Filename is null.");
            return null;
        }
        File f = null;
        f = new File(DefaultSecurityConfiguration.customDirectory, filename);
        if (DefaultSecurityConfiguration.customDirectory != null && f.canRead()) {
            this.logSpecial("Found in 'org.owasp.esapi.resources' directory: " + f.getAbsolutePath());
            return f;
        }
        this.logSpecial("Not found in 'org.owasp.esapi.resources' directory or file not readable: " + f.getAbsolutePath());
        URL fileUrl = ClassLoader.getSystemResource(this.resourceDirectory + "/" + filename);
        if (fileUrl == null) {
            fileUrl = ClassLoader.getSystemResource("esapi/" + filename);
        }
        if (fileUrl != null) {
            final String fileLocation = fileUrl.getFile();
            f = new File(fileLocation);
            if (f.exists()) {
                this.logSpecial("Found in SystemResource Directory/resourceDirectory: " + f.getAbsolutePath());
                return f;
            }
            this.logSpecial("Not found in SystemResource Directory/resourceDirectory (this should never happen): " + f.getAbsolutePath());
        }
        else {
            this.logSpecial("Not found in SystemResource Directory/resourceDirectory: " + this.resourceDirectory + File.separator + filename);
        }
        String homeDir = DefaultSecurityConfiguration.userHome;
        if (homeDir == null) {
            homeDir = "";
        }
        f = new File(homeDir + "/.esapi", filename);
        if (f.canRead()) {
            this.logSpecial("[Compatibility] Found in 'user.home' directory: " + f.getAbsolutePath());
            return f;
        }
        f = new File(homeDir + "/esapi", filename);
        if (f.canRead()) {
            this.logSpecial("Found in 'user.home' directory: " + f.getAbsolutePath());
            return f;
        }
        this.logSpecial("Not found in 'user.home' (" + homeDir + ") directory: " + f.getAbsolutePath());
        return null;
    }
    
    private Properties loadConfigurationFromClasspath(final String fileName) throws IllegalArgumentException {
        Properties result = null;
        InputStream in = null;
        final ClassLoader[] loaders = { Thread.currentThread().getContextClassLoader(), ClassLoader.getSystemClassLoader(), this.getClass().getClassLoader() };
        final String[] classLoaderNames = { "current thread context class loader", "system class loader", "class loader for DefaultSecurityConfiguration class" };
        ClassLoader currentLoader = null;
        for (int i = 0; i < loaders.length; ++i) {
            if (loaders[i] != null) {
                currentLoader = loaders[i];
                try {
                    String currentClasspathSearchLocation = "/ (root)";
                    in = loaders[i].getResourceAsStream(fileName);
                    if (in == null) {
                        currentClasspathSearchLocation = this.resourceDirectory + "/";
                        in = currentLoader.getResourceAsStream(this.resourceDirectory + "/" + fileName);
                    }
                    if (in == null) {
                        currentClasspathSearchLocation = ".esapi/";
                        in = currentLoader.getResourceAsStream(".esapi/" + fileName);
                    }
                    if (in == null) {
                        currentClasspathSearchLocation = "esapi/";
                        in = currentLoader.getResourceAsStream("esapi/" + fileName);
                    }
                    if (in == null) {
                        currentClasspathSearchLocation = "src/main/resources/";
                        in = currentLoader.getResourceAsStream("src/main/resources/" + fileName);
                    }
                    if (in != null) {
                        result = new Properties();
                        result.load(in);
                        this.logSpecial("SUCCESSFULLY LOADED " + fileName + " via the CLASSPATH from '" + currentClasspathSearchLocation + "' using " + classLoaderNames[i] + "!");
                    }
                }
                catch (final Exception e) {
                    result = null;
                }
                finally {
                    try {
                        in.close();
                    }
                    catch (final Exception ex) {}
                }
            }
        }
        if (result == null) {
            throw new IllegalArgumentException("Failed to load " + this.resourceFile + " as a classloader resource.");
        }
        return result;
    }
    
    private void logSpecial(final String message, final Throwable e) {
        final StringBuffer msg = new StringBuffer(message);
        if (e != null) {
            msg.append(" Exception was: ").append(e.toString());
        }
        System.out.println(msg.toString());
    }
    
    private void logSpecial(final String message) {
        System.out.println(message);
    }
    
    @Override
    public String getPasswordParameterName() {
        return this.getESAPIProperty("Authenticator.PasswordParameterName", "password");
    }
    
    @Override
    public String getUsernameParameterName() {
        return this.getESAPIProperty("Authenticator.UsernameParameterName", "username");
    }
    
    @Override
    public String getEncryptionAlgorithm() {
        return this.getESAPIProperty("Encryptor.EncryptionAlgorithm", "AES");
    }
    
    @Override
    public String getCipherTransformation() {
        assert this.cipherXformCurrent != null : "Current cipher transformation is null";
        return this.cipherXformCurrent;
    }
    
    @Override
    public String setCipherTransformation(final String cipherXform) {
        final String previous = this.getCipherTransformation();
        if (cipherXform == null) {
            this.cipherXformCurrent = this.cipherXformFromESAPIProp;
        }
        else {
            assert !cipherXform.trim().equals("") : "Cipher transformation cannot be just white space or empty string";
            this.cipherXformCurrent = cipherXform;
        }
        return previous;
    }
    
    @Override
    public boolean useMACforCipherText() {
        return this.getESAPIProperty("Encryptor.CipherText.useMAC", true);
    }
    
    @Override
    public boolean overwritePlainText() {
        return this.getESAPIProperty("Encryptor.PlainText.overwrite", true);
    }
    
    @Override
    public String getIVType() {
        final String value = this.getESAPIProperty("Encryptor.ChooseIVMethod", "random");
        if (value.equalsIgnoreCase("fixed") || value.equalsIgnoreCase("random")) {
            return value;
        }
        if (value.equalsIgnoreCase("specified")) {
            throw new ConfigurationException("'Encryptor.ChooseIVMethod=specified' is not yet implemented. Use 'fixed' or 'random'");
        }
        throw new ConfigurationException(value + " is illegal value for " + "Encryptor.ChooseIVMethod" + ". Use 'random' (preferred) or 'fixed'.");
    }
    
    @Override
    public String getFixedIV() {
        if (!this.getIVType().equalsIgnoreCase("fixed")) {
            throw new ConfigurationException("IV type not 'fixed' (set to '" + this.getIVType() + "'), so no fixed IV applicable.");
        }
        final String ivAsHex = this.getESAPIProperty("Encryptor.fixedIV", "");
        if (ivAsHex == null || ivAsHex.trim().equals("")) {
            throw new ConfigurationException("Fixed IV requires property Encryptor.fixedIV to be set, but it is not.");
        }
        return ivAsHex;
    }
    
    @Override
    public String getHashAlgorithm() {
        return this.getESAPIProperty("Encryptor.HashAlgorithm", "SHA-512");
    }
    
    @Override
    public int getHashIterations() {
        return this.getESAPIProperty("Encryptor.HashIterations", 1024);
    }
    
    @Override
    public String getKDFPseudoRandomFunction() {
        return this.getESAPIProperty("Encryptor.KDF.PRF", "HmacSHA256");
    }
    
    @Override
    public String getCharacterEncoding() {
        return this.getESAPIProperty("Encryptor.CharacterEncoding", "UTF-8");
    }
    
    @Override
    public boolean getAllowMultipleEncoding() {
        return this.getESAPIProperty("Encoder.AllowMultipleEncoding", false);
    }
    
    @Override
    public boolean getAllowMixedEncoding() {
        return this.getESAPIProperty("Encoder.AllowMixedEncoding", false);
    }
    
    @Override
    public List<String> getDefaultCanonicalizationCodecs() {
        final List<String> def = new ArrayList<String>();
        def.add("org.owasp.esapi.codecs.HTMLEntityCodec");
        def.add("org.owasp.esapi.codecs.PercentCodec");
        def.add("org.owasp.esapi.codecs.JavaScriptCodec");
        return this.getESAPIProperty("Encoder.DefaultCodecList", def);
    }
    
    @Override
    public String getDigitalSignatureAlgorithm() {
        return this.getESAPIProperty("Encryptor.DigitalSignatureAlgorithm", "SHAwithDSA");
    }
    
    @Override
    public int getDigitalSignatureKeyLength() {
        return this.getESAPIProperty("Encryptor.DigitalSignatureKeyLength", 1024);
    }
    
    @Override
    public String getRandomAlgorithm() {
        return this.getESAPIProperty("Encryptor.RandomAlgorithm", "SHA1PRNG");
    }
    
    @Override
    public int getAllowedLoginAttempts() {
        return this.getESAPIProperty("Authenticator.AllowedLoginAttempts", 5);
    }
    
    @Override
    public int getMaxOldPasswordHashes() {
        return this.getESAPIProperty("Authenticator.MaxOldPasswordHashes", 12);
    }
    
    @Override
    public File getUploadDirectory() {
        final String dir = this.getESAPIProperty("HttpUtilities.UploadDir", "UploadDir");
        return new File(dir);
    }
    
    @Override
    public File getUploadTempDirectory() {
        final String dir = this.getESAPIProperty("HttpUtilities.UploadTempDir", System.getProperty("java.io.tmpdir", "UploadTempDir"));
        return new File(dir);
    }
    
    @Override
    public boolean getDisableIntrusionDetection() {
        final String value = this.properties.getProperty("IntrusionDetector.Disable");
        return "true".equalsIgnoreCase(value);
    }
    
    @Override
    public Threshold getQuota(final String eventName) {
        final int count = this.getESAPIProperty("IntrusionDetector." + eventName + ".count", 0);
        final int interval = this.getESAPIProperty("IntrusionDetector." + eventName + ".interval", 0);
        List<String> actions = new ArrayList<String>();
        final String actionString = this.getESAPIProperty("IntrusionDetector." + eventName + ".actions", "");
        if (actionString != null) {
            final String[] actionList = actionString.split(",");
            actions = Arrays.asList(actionList);
        }
        if (count > 0 && interval > 0 && actions.size() > 0) {
            return new Threshold(eventName, count, interval, actions);
        }
        return null;
    }
    
    @Override
    public int getLogLevel() {
        final String level = this.getESAPIProperty("Logger.LogLevel", "WARNING");
        if (level.equalsIgnoreCase("OFF")) {
            return Integer.MAX_VALUE;
        }
        if (level.equalsIgnoreCase("FATAL")) {
            return 1000;
        }
        if (level.equalsIgnoreCase("ERROR")) {
            return 800;
        }
        if (level.equalsIgnoreCase("WARNING")) {
            return 600;
        }
        if (level.equalsIgnoreCase("INFO")) {
            return 400;
        }
        if (level.equalsIgnoreCase("DEBUG")) {
            return 200;
        }
        if (level.equalsIgnoreCase("TRACE")) {
            return 100;
        }
        if (level.equalsIgnoreCase("ALL")) {
            return Integer.MIN_VALUE;
        }
        this.logSpecial("The LOG-LEVEL property in the ESAPI properties file has the unrecognized value: " + level + ". Using default: WARNING", null);
        return 600;
    }
    
    @Override
    public String getLogFileName() {
        return this.getESAPIProperty("Logger.LogFileName", "ESAPI_logging_file");
    }
    
    @Override
    public int getMaxLogFileSize() {
        return this.getESAPIProperty("Logger.MaxLogFileSize", 10000000);
    }
    
    @Override
    public boolean getLogEncodingRequired() {
        return this.getESAPIProperty("Logger.LogEncodingRequired", false);
    }
    
    @Override
    public boolean getLogApplicationName() {
        return this.getESAPIProperty("Logger.LogApplicationName", true);
    }
    
    @Override
    public boolean getLogServerIP() {
        return this.getESAPIProperty("Logger.LogServerIP", true);
    }
    
    @Override
    public boolean getForceHttpOnlySession() {
        return this.getESAPIProperty("HttpUtilities.ForceHttpOnlySession", true);
    }
    
    @Override
    public boolean getForceSecureSession() {
        return this.getESAPIProperty("HttpUtilities.SecureSession", true);
    }
    
    @Override
    public boolean getForceHttpOnlyCookies() {
        return this.getESAPIProperty("HttpUtilities.ForceHttpOnlyCookies", true);
    }
    
    @Override
    public boolean getForceSecureCookies() {
        return this.getESAPIProperty("HttpUtilities.ForceSecureCookies", true);
    }
    
    @Override
    public int getMaxHttpHeaderSize() {
        return this.getESAPIProperty("HttpUtilities.MaxHeaderSize", 4096);
    }
    
    @Override
    public String getResponseContentType() {
        return this.getESAPIProperty("HttpUtilities.ResponseContentType", "text/html; charset=UTF-8");
    }
    
    @Override
    public String getHttpSessionIdName() {
        return this.getESAPIProperty("HttpUtilities.HttpSessionIdName", "JSESSIONID");
    }
    
    @Override
    public long getRememberTokenDuration() {
        final int days = this.getESAPIProperty("Authenticator.RememberTokenDuration", 14);
        return 86400000 * days;
    }
    
    @Override
    public int getSessionIdleTimeoutLength() {
        final int minutes = this.getESAPIProperty("Authenticator.IdleTimeoutDuration", 20);
        return 60000 * minutes;
    }
    
    @Override
    public int getSessionAbsoluteTimeoutLength() {
        final int minutes = this.getESAPIProperty("Authenticator.AbsoluteTimeoutDuration", 20);
        return 60000 * minutes;
    }
    
    @Override
    public Pattern getValidationPattern(final String key) {
        final String value = this.getESAPIProperty("Validator." + key, "");
        final Pattern p = DefaultSecurityConfiguration.patternCache.get(value);
        if (p != null) {
            return p;
        }
        if (value == null || value.equals("")) {
            return null;
        }
        try {
            final Pattern q = Pattern.compile(value);
            DefaultSecurityConfiguration.patternCache.put(value, q);
            return q;
        }
        catch (final PatternSyntaxException e) {
            this.logSpecial("SecurityConfiguration for " + key + " not a valid regex in ESAPI.properties. Returning null", null);
            return null;
        }
    }
    
    @Override
    public File getWorkingDirectory() {
        final String dir = this.getESAPIProperty("Executor.WorkingDirectory", System.getProperty("user.dir"));
        if (dir != null) {
            return new File(dir);
        }
        return null;
    }
    
    @Override
    public String getPreferredJCEProvider() {
        return this.properties.getProperty("Encryptor.PreferredJCEProvider");
    }
    
    @Override
    public List<String> getCombinedCipherModes() {
        final List<String> empty = new ArrayList<String>();
        return this.getESAPIProperty("Encryptor.cipher_modes.combined_modes", empty);
    }
    
    @Override
    public List<String> getAdditionalAllowedCipherModes() {
        final List<String> empty = new ArrayList<String>();
        return this.getESAPIProperty("Encryptor.cipher_modes.additional_allowed", empty);
    }
    
    @Override
    public boolean getLenientDatesAccepted() {
        return this.getESAPIProperty("Validator.AcceptLenientDates", false);
    }
    
    protected String getESAPIProperty(final String key, final String def) {
        final String value = this.properties.getProperty(key);
        if (value == null) {
            return def;
        }
        return value;
    }
    
    protected boolean getESAPIProperty(final String key, final boolean def) {
        final String property = this.properties.getProperty(key);
        if (property == null) {
            this.logSpecial("SecurityConfiguration for " + key + " not found in ESAPI.properties. Using default: " + def, null);
            return def;
        }
        if (property.equalsIgnoreCase("true") || property.equalsIgnoreCase("yes")) {
            return true;
        }
        if (property.equalsIgnoreCase("false") || property.equalsIgnoreCase("no")) {
            return false;
        }
        this.logSpecial("SecurityConfiguration for " + key + " not either \"true\" or \"false\" in ESAPI.properties. Using default: " + def, null);
        return def;
    }
    
    protected byte[] getESAPIPropertyEncoded(final String key, final byte[] def) {
        final String property = this.properties.getProperty(key);
        if (property == null) {
            this.logSpecial("SecurityConfiguration for " + key + " not found in ESAPI.properties. Using default: " + def, null);
            return def;
        }
        try {
            return ESAPI.encoder().decodeFromBase64(property);
        }
        catch (final IOException e) {
            this.logSpecial("SecurityConfiguration for " + key + " not properly Base64 encoded in ESAPI.properties. Using default: " + def, null);
            return null;
        }
    }
    
    protected int getESAPIProperty(final String key, final int def) {
        final String property = this.properties.getProperty(key);
        if (property == null) {
            this.logSpecial("SecurityConfiguration for " + key + " not found in ESAPI.properties. Using default: " + def, null);
            return def;
        }
        try {
            return Integer.parseInt(property);
        }
        catch (final NumberFormatException e) {
            this.logSpecial("SecurityConfiguration for " + key + " not an integer in ESAPI.properties. Using default: " + def, null);
            return def;
        }
    }
    
    protected List<String> getESAPIProperty(final String key, final List<String> def) {
        final String property = this.properties.getProperty(key);
        if (property == null) {
            this.logSpecial("SecurityConfiguration for " + key + " not found in ESAPI.properties. Using default: " + def, null);
            return def;
        }
        final String[] parts = property.split(",");
        return Arrays.asList(parts);
    }
    
    @Override
    public int getIntProp(final String propertyName) throws ConfigurationException {
        try {
            return this.esapiPropertyManager.getIntProp(propertyName);
        }
        catch (final ConfigurationException ex) {
            final String property = this.properties.getProperty(propertyName);
            try {
                return Integer.parseInt(property);
            }
            catch (final NumberFormatException e) {
                throw new ConfigurationException("SecurityConfiguration for " + propertyName + " has incorrect type");
            }
        }
    }
    
    @Override
    public byte[] getByteArrayProp(final String propertyName) throws ConfigurationException {
        try {
            return this.esapiPropertyManager.getByteArrayProp(propertyName);
        }
        catch (final ConfigurationException ex) {
            final String property = this.properties.getProperty(propertyName);
            if (property == null) {
                throw new ConfigurationException("SecurityConfiguration for " + propertyName + " not found in ESAPI.properties");
            }
            try {
                return ESAPI.encoder().decodeFromBase64(property);
            }
            catch (final IOException e) {
                throw new ConfigurationException("SecurityConfiguration for " + propertyName + " has incorrect type");
            }
        }
    }
    
    @Override
    public Boolean getBooleanProp(final String propertyName) throws ConfigurationException {
        try {
            return this.esapiPropertyManager.getBooleanProp(propertyName);
        }
        catch (final ConfigurationException ex) {
            final String property = this.properties.getProperty(propertyName);
            if (property == null) {
                throw new ConfigurationException("SecurityConfiguration for " + propertyName + " not found in ESAPI.properties");
            }
            if (property.equalsIgnoreCase("true") || property.equalsIgnoreCase("yes")) {
                return true;
            }
            if (property.equalsIgnoreCase("false") || property.equalsIgnoreCase("no")) {
                return false;
            }
            throw new ConfigurationException("SecurityConfiguration for " + propertyName + " has incorrect type");
        }
    }
    
    @Override
    public String getStringProp(final String propertyName) throws ConfigurationException {
        try {
            return this.esapiPropertyManager.getStringProp(propertyName);
        }
        catch (final ConfigurationException ex) {
            final String property = this.properties.getProperty(propertyName);
            if (property == null) {
                throw new ConfigurationException("SecurityConfiguration for " + propertyName + " not found in ESAPI.properties");
            }
            return property;
        }
    }
    
    protected boolean shouldPrintProperties() {
        return this.getESAPIProperty("ESAPI.printProperties", false);
    }
    
    protected Properties getESAPIProperties() {
        return this.properties;
    }
    
    static {
        DefaultSecurityConfiguration.instance = null;
        patternCache = new HashMap<String, Pattern>();
        userHome = System.getProperty("user.home");
        DefaultSecurityConfiguration.customDirectory = System.getProperty("org.owasp.esapi.resources");
    }
}
