package com.google.api.client.googleapis;

import java.util.regex.Matcher;
import java.util.Properties;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.io.InputStream;
import com.google.api.client.util.SecurityUtils;
import java.security.KeyStore;
import com.google.common.annotations.VisibleForTesting;
import java.util.regex.Pattern;

public final class GoogleUtils
{
    public static final String VERSION;
    public static final Integer MAJOR_VERSION;
    public static final Integer MINOR_VERSION;
    public static final Integer BUGFIX_VERSION;
    @VisibleForTesting
    static final Pattern VERSION_PATTERN;
    static KeyStore certTrustStore;
    
    public static synchronized KeyStore getCertificateTrustStore() throws IOException, GeneralSecurityException {
        if (GoogleUtils.certTrustStore == null) {
            GoogleUtils.certTrustStore = SecurityUtils.getPkcs12KeyStore();
            final InputStream keyStoreStream = GoogleUtils.class.getResourceAsStream("google.p12");
            SecurityUtils.loadKeyStore(GoogleUtils.certTrustStore, keyStoreStream, "notasecret");
        }
        return GoogleUtils.certTrustStore;
    }
    
    private static String getVersion() {
        String version = null;
        try (final InputStream inputStream = GoogleUtils.class.getResourceAsStream("google-api-client.properties")) {
            if (inputStream != null) {
                final Properties properties = new Properties();
                properties.load(inputStream);
                version = properties.getProperty("google-api-client.version");
            }
        }
        catch (final IOException ex) {}
        return (version == null) ? "unknown-version" : version;
    }
    
    private GoogleUtils() {
    }
    
    static {
        VERSION = getVersion();
        VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(-SNAPSHOT)?");
        final Matcher versionMatcher = GoogleUtils.VERSION_PATTERN.matcher(GoogleUtils.VERSION);
        versionMatcher.find();
        MAJOR_VERSION = Integer.parseInt(versionMatcher.group(1));
        MINOR_VERSION = Integer.parseInt(versionMatcher.group(2));
        BUGFIX_VERSION = Integer.parseInt(versionMatcher.group(3));
    }
}
