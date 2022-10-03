package com.zoho.security.api;

import com.adventnet.iam.security.IPUtil;
import com.adventnet.iam.security.SecurityUtil;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.net.URL;
import com.adventnet.iam.security.IAMSecurityException;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class Util
{
    private static final Logger LOGGER;
    private static final Pattern DOMAIN_NAME_PATTERN;
    
    public static void validateURL(final String urlString) throws IAMSecurityException {
        validateURL(urlString, false);
    }
    
    public static void validateURL(final String urlString, final boolean allowLanAccess) throws IAMSecurityException {
        try {
            validateURL(new URL(urlString), allowLanAccess);
        }
        catch (final MalformedURLException ex) {
            Util.LOGGER.log(Level.SEVERE, "Unable to parse the URL : {0} , Exception : {1}", new Object[] { getMaskedUrl(urlString), ex.getMessage() });
            throw new IAMSecurityException("INVALID_URL");
        }
    }
    
    public static void validateURL(final URL url, final boolean allowLanAccess) throws IAMSecurityException {
        final String protocol = url.getProtocol();
        if (!"http".equals(protocol) && !"https".equals(protocol)) {
            Util.LOGGER.log(Level.SEVERE, "Unsupported url protocol \"{0}\". URL : \"{1}\"", new Object[] { protocol, getMaskedUrl(url.toString()) });
            throw new IAMSecurityException("UNSUPPORTED_URL_PROTOCOL");
        }
        final String hostName = url.getHost();
        if (SecurityUtil.isValid(hostName)) {
            final boolean isIPAddress = IPUtil.isValidIPv4(hostName);
            if (isIPAddress || isFQDN(hostName)) {
                if ((isIPAddress || !allowLanAccess) && IPUtil.isPrivateIP(hostName)) {
                    Util.LOGGER.log(Level.SEVERE, "Attempting to access LAN/Private IP via URL. ACCESS DENIED for URL {0}", getMaskedUrl(url.toString()));
                    throw new IAMSecurityException("LAN_ACCESS_DENIED");
                }
                return;
            }
        }
        Util.LOGGER.log(Level.SEVERE, "Host name \"{0}\" is not valid. URL : \"{1}\"", new Object[] { hostName, getMaskedUrl(url.toString()) });
        throw new IAMSecurityException("INVALID_DOMAIN_NAME");
    }
    
    public static boolean isFQDN(final String domainName) {
        return SecurityUtil.isValid(domainName) && domainName.length() <= 255 && (domainName.indexOf(".") > 0 && SecurityUtil.matchPattern(domainName, Util.DOMAIN_NAME_PATTERN));
    }
    
    public static String getMaskedUrl(final String urlString) {
        int index = -1;
        if (urlString != null && (index = urlString.indexOf("?")) != -1) {
            final String queryStringStrippedUrl = urlString.substring(0, index + 1);
            return queryStringStrippedUrl + "****";
        }
        return urlString;
    }
    
    static {
        LOGGER = Logger.getLogger(Util.class.getName());
        DOMAIN_NAME_PATTERN = Pattern.compile("^(?:\\p{Alnum}([\\p{Alnum}-]{0,61}\\p{Alnum})?\\.)+(\\p{Alpha}([\\p{Alnum}-]{0,22}\\p{Alpha}))$");
    }
}
