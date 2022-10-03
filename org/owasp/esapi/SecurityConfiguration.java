package org.owasp.esapi;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.io.File;
import java.util.regex.Pattern;
import org.owasp.esapi.configuration.EsapiPropertyLoader;

public interface SecurityConfiguration extends EsapiPropertyLoader
{
    @Deprecated
    String getApplicationName();
    
    @Deprecated
    String getLogImplementation();
    
    @Deprecated
    String getAuthenticationImplementation();
    
    @Deprecated
    String getEncoderImplementation();
    
    @Deprecated
    String getAccessControlImplementation();
    
    @Deprecated
    String getIntrusionDetectionImplementation();
    
    @Deprecated
    String getRandomizerImplementation();
    
    @Deprecated
    String getEncryptionImplementation();
    
    @Deprecated
    String getValidationImplementation();
    
    Pattern getValidationPattern(final String p0);
    
    @Deprecated
    boolean getLenientDatesAccepted();
    
    @Deprecated
    String getExecutorImplementation();
    
    @Deprecated
    String getHTTPUtilitiesImplementation();
    
    @Deprecated
    byte[] getMasterKey();
    
    File getUploadDirectory();
    
    File getUploadTempDirectory();
    
    @Deprecated
    int getEncryptionKeyLength();
    
    @Deprecated
    byte[] getMasterSalt();
    
    List<String> getAllowedExecutables();
    
    List<String> getAllowedFileExtensions();
    
    @Deprecated
    int getAllowedFileUploadSize();
    
    @Deprecated
    String getPasswordParameterName();
    
    @Deprecated
    String getUsernameParameterName();
    
    @Deprecated
    String getEncryptionAlgorithm();
    
    @Deprecated
    String getCipherTransformation();
    
    @Deprecated
    String setCipherTransformation(final String p0);
    
    @Deprecated
    String getPreferredJCEProvider();
    
    @Deprecated
    boolean useMACforCipherText();
    
    @Deprecated
    boolean overwritePlainText();
    
    @Deprecated
    String getIVType();
    
    @Deprecated
    String getFixedIV();
    
    List<String> getCombinedCipherModes();
    
    List<String> getAdditionalAllowedCipherModes();
    
    @Deprecated
    String getHashAlgorithm();
    
    @Deprecated
    int getHashIterations();
    
    @Deprecated
    String getKDFPseudoRandomFunction();
    
    @Deprecated
    String getCharacterEncoding();
    
    @Deprecated
    boolean getAllowMultipleEncoding();
    
    @Deprecated
    boolean getAllowMixedEncoding();
    
    List<String> getDefaultCanonicalizationCodecs();
    
    @Deprecated
    String getDigitalSignatureAlgorithm();
    
    @Deprecated
    int getDigitalSignatureKeyLength();
    
    @Deprecated
    String getRandomAlgorithm();
    
    @Deprecated
    int getAllowedLoginAttempts();
    
    @Deprecated
    int getMaxOldPasswordHashes();
    
    @Deprecated
    boolean getDisableIntrusionDetection();
    
    Threshold getQuota(final String p0);
    
    File getResourceFile(final String p0);
    
    @Deprecated
    boolean getForceHttpOnlySession();
    
    @Deprecated
    boolean getForceSecureSession();
    
    @Deprecated
    boolean getForceHttpOnlyCookies();
    
    @Deprecated
    boolean getForceSecureCookies();
    
    @Deprecated
    int getMaxHttpHeaderSize();
    
    InputStream getResourceStream(final String p0) throws IOException;
    
    void setResourceDirectory(final String p0);
    
    @Deprecated
    String getResponseContentType();
    
    @Deprecated
    String getHttpSessionIdName();
    
    long getRememberTokenDuration();
    
    @Deprecated
    int getSessionIdleTimeoutLength();
    
    @Deprecated
    int getSessionAbsoluteTimeoutLength();
    
    @Deprecated
    boolean getLogEncodingRequired();
    
    @Deprecated
    boolean getLogApplicationName();
    
    @Deprecated
    boolean getLogServerIP();
    
    @Deprecated
    int getLogLevel();
    
    @Deprecated
    String getLogFileName();
    
    @Deprecated
    int getMaxLogFileSize();
    
    File getWorkingDirectory();
    
    public static class Threshold
    {
        public String name;
        public int count;
        public long interval;
        public List<String> actions;
        
        public Threshold(final String name, final int count, final long interval, final List<String> actions) {
            this.name = null;
            this.count = 0;
            this.interval = 0L;
            this.actions = null;
            this.name = name;
            this.count = count;
            this.interval = interval;
            this.actions = actions;
        }
    }
}
