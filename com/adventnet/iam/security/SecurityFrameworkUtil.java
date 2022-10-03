package com.adventnet.iam.security;

import java.util.logging.Level;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import java.util.List;
import java.io.IOException;
import java.io.FileNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.logging.Logger;

public class SecurityFrameworkUtil
{
    public static final Logger LOGGER;
    private static Boolean isWAFInstrumentLoaded;
    
    public static boolean isFileBasedLoadingEnabled(final SecurityFilterProperties sFConfig) {
        return sFConfig.isFileBasedLoadingEnabled();
    }
    
    public static String getSecurityConfigurationDirectory() {
        return SecurityUtil.getSecurityConfigurationDir();
    }
    
    public static boolean isAuthenticationProviderConfigured(final SecurityFilterProperties sFConfig) {
        return sFConfig.isAuthenticationProviderConfigured();
    }
    
    public static Map<String, String> getSystemConfigPropFromIAM(final SecurityFilterProperties sFConfig) {
        return sFConfig.getAuthenticationProvider().getRequestComponentBlackListPatterns();
    }
    
    public static String getInputStreamContentforScanning(final HttpServletRequest request) {
        return ((SecurityRequestWrapper)request).getOriginalInputStreamContent();
    }
    
    public static String getFileContentforScanning(final UploadedFileItem file) throws FileNotFoundException, IOException {
        String fileContent = null;
        fileContent = SecurityUtil.getFileAsString(file.getUploadedFileForValidation());
        return fileContent;
    }
    
    public static List<UploadedFileItem> getMultiPartFiles(final HttpServletRequest request) {
        return ((SecurityRequestWrapper)request).getMultipartFiles();
    }
    
    public static boolean isValid(final String value) {
        return value != null && !"".equals(value.trim());
    }
    
    public static DocumentBuilder getDocumentBuilder() {
        return SecurityUtil.getDocumentBuilder();
    }
    
    public static List<Element> getChildNodesByTagName(final Element root, final String directive) {
        return RuleSetParser.getChildNodesByTagName(root, directive);
    }
    
    public static HttpURLConnection getURLConnection(final String urlString, final String postParams, String method) throws Exception {
        method = method.toUpperCase();
        final URL urlObj = new URL(urlString);
        final HttpURLConnection connection = (HttpURLConnection)urlObj.openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        if ("POST".equals(method) && postParams != null) {
            connection.setDoOutput(true);
            final OutputStream os = connection.getOutputStream();
            os.write(postParams.getBytes());
            os.flush();
            os.close();
        }
        return connection;
    }
    
    public static String readFile(final String filename) throws Exception {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            final StringBuilder sb = new StringBuilder();
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line);
            }
            return sb.toString();
        }
        catch (final Exception e) {
            SecurityFrameworkUtil.LOGGER.log(Level.WARNING, " Exception occurred while reading the file  : {0}.", e.getMessage());
            throw e;
        }
        finally {
            try {
                if (br != null) {
                    br.close();
                }
            }
            catch (final IOException e2) {
                SecurityFrameworkUtil.LOGGER.log(Level.WARNING, " Exception occurred in finally while reading the file  : {0}.", e2.getMessage());
                throw e2;
            }
        }
    }
    
    public static boolean isWAFInstrumentLoaded() {
        if (SecurityFrameworkUtil.isWAFInstrumentLoaded == null) {
            try {
                Class.forName("com.zoho.security.instrumentation.WAFInstrumentClass", false, SecurityFrameworkUtil.class.getClassLoader());
                SecurityFrameworkUtil.isWAFInstrumentLoaded = true;
            }
            catch (final ClassNotFoundException cnfe) {
                SecurityFrameworkUtil.isWAFInstrumentLoaded = false;
            }
        }
        return SecurityFrameworkUtil.isWAFInstrumentLoaded;
    }
    
    static {
        LOGGER = Logger.getLogger(SecurityFrameworkUtil.class.getName());
    }
}
