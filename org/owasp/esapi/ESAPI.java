package org.owasp.esapi;

import org.owasp.esapi.util.ObjFactory;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public final class ESAPI
{
    private static String securityConfigurationImplName;
    private static volatile SecurityConfiguration overrideConfig;
    
    private ESAPI() {
    }
    
    public static void clearCurrent() {
        authenticator().clearCurrent();
        httpUtilities().clearCurrent();
    }
    
    public static HttpServletRequest currentRequest() {
        return httpUtilities().getCurrentRequest();
    }
    
    public static HttpServletResponse currentResponse() {
        return httpUtilities().getCurrentResponse();
    }
    
    public static AccessController accessController() {
        return ObjFactory.make(securityConfiguration().getAccessControlImplementation(), "AccessController");
    }
    
    public static Authenticator authenticator() {
        return ObjFactory.make(securityConfiguration().getAuthenticationImplementation(), "Authenticator");
    }
    
    public static Encoder encoder() {
        return ObjFactory.make(securityConfiguration().getEncoderImplementation(), "Encoder");
    }
    
    public static Encryptor encryptor() {
        return ObjFactory.make(securityConfiguration().getEncryptionImplementation(), "Encryptor");
    }
    
    public static Executor executor() {
        return ObjFactory.make(securityConfiguration().getExecutorImplementation(), "Executor");
    }
    
    public static HTTPUtilities httpUtilities() {
        return ObjFactory.make(securityConfiguration().getHTTPUtilitiesImplementation(), "HTTPUtilities");
    }
    
    public static IntrusionDetector intrusionDetector() {
        return ObjFactory.make(securityConfiguration().getIntrusionDetectionImplementation(), "IntrusionDetector");
    }
    
    private static LogFactory logFactory() {
        return ObjFactory.make(securityConfiguration().getLogImplementation(), "LogFactory");
    }
    
    public static Logger getLogger(final Class clazz) {
        return logFactory().getLogger(clazz);
    }
    
    public static Logger getLogger(final String moduleName) {
        return logFactory().getLogger(moduleName);
    }
    
    public static Logger log() {
        return logFactory().getLogger("DefaultLogger");
    }
    
    public static Randomizer randomizer() {
        return ObjFactory.make(securityConfiguration().getRandomizerImplementation(), "Randomizer");
    }
    
    public static SecurityConfiguration securityConfiguration() {
        final SecurityConfiguration override = ESAPI.overrideConfig;
        if (override != null) {
            return override;
        }
        return ObjFactory.make(ESAPI.securityConfigurationImplName, "SecurityConfiguration");
    }
    
    public static Validator validator() {
        return ObjFactory.make(securityConfiguration().getValidationImplementation(), "Validator");
    }
    
    public static String initialize(final String impl) {
        final String oldImpl = ESAPI.securityConfigurationImplName;
        ESAPI.securityConfigurationImplName = impl;
        return oldImpl;
    }
    
    public static void override(final SecurityConfiguration config) {
        ESAPI.overrideConfig = config;
    }
    
    static {
        ESAPI.securityConfigurationImplName = System.getProperty("org.owasp.esapi.SecurityConfiguration", "org.owasp.esapi.reference.DefaultSecurityConfiguration");
        ESAPI.overrideConfig = null;
    }
}
