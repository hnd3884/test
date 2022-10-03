package com.adventnet.client.themes.web;

import com.adventnet.iam.xss.IAMEncoder;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import com.adventnet.client.properties.ClientProperties;
import com.adventnet.client.util.web.WebClientUtil;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.Criteria;
import java.util.Arrays;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.client.cache.StaticCache;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import java.util.logging.Logger;
import com.adventnet.client.util.web.WebConstants;

public class ThemesAPI implements WebConstants
{
    private static Logger logger;
    private static HashMap<String, String> httpstyleSnippet;
    private static HashMap<String, String> httpsstyleSnippet;
    private static String THEMES_DIR_PH;
    
    public static Row getThemeForAccount(final long accountId) throws Exception {
        String themeName = (String)StaticCache.getFromCache("SELTHEME_" + accountId);
        if (themeName == null) {
            try {
                final Row selectedThemeRow = new Row("ACUserClientState");
                selectedThemeRow.set(1, (Object)new Long(accountId));
                final DataObject selectedThemeDO = LookUpUtil.getCachedUserPersistence().get("ACUserClientState", selectedThemeRow);
                themeName = (String)selectedThemeDO.getValue("ACUserClientState", 2, (Row)null);
                if (themeName == null) {
                    themeName = getDefaultTheme();
                    StaticCache.addToCache("SELTHEME_" + accountId, themeName);
                }
            }
            catch (final DataAccessException dae) {
                ThemesAPI.logger.log(Level.SEVERE, "Error !!! while fetching theme for account id '" + new Long(accountId) + "' and context ");
                throw new Exception("Error !!! while fetching theme for account id '" + new Long(accountId) + "' and context ", (Throwable)dae);
            }
        }
        return getThemeRow(themeName);
    }
    
    public static Row getThemeRow(final Object themeName) throws Exception {
        final String key = "R_" + themeName;
        Row r = (Row)StaticCache.getFromCache(key);
        if (r == null) {
            try {
                final Row themeRow = new Row("Theme");
                if (themeName instanceof Long) {
                    themeRow.set(1, themeName);
                }
                else if (themeName instanceof String) {
                    themeRow.set(2, themeName);
                }
                final DataObject themeDO = LookUpUtil.getCachedUserPersistence().get("Theme", themeRow);
                r = themeDO.getFirstRow("Theme");
                StaticCache.addToCache(key, r, Arrays.asList("Theme"));
            }
            catch (final DataAccessException dae) {
                throw new DataAccessException("The given theme '" + themeName + "' is not configured.", (Throwable)dae);
            }
        }
        return r;
    }
    
    public static String getDefaultTheme() throws Exception {
        final String key = "DEFTHEME_";
        Row r = (Row)StaticCache.getFromCache(key);
        if (r == null) {
            final DataObject defaultThemeDO = LookUpUtil.getUserPersistence().get("DefaultTheme", (Criteria)null);
            r = defaultThemeDO.getRow("DefaultTheme");
            StaticCache.addToCache(key, r, Arrays.asList("DefaultTheme"));
        }
        return getThemeName(r.get(1));
    }
    
    public static String getThemeName(final Object themeNameNo) throws Exception {
        final Row themerow = getThemeRow(themeNameNo);
        return (String)themerow.get(2);
    }
    
    public static Long getThemeNameNo(final Object themeName) throws Exception {
        final Row themerow = getThemeRow(themeName);
        return (Long)themerow.get(1);
    }
    
    public static String getThemeValue(final String themeName, final String themeAttribute) {
        String attributeValue = null;
        try {
            final Row mainRow = new Row("ThemeAttributesMapping");
            mainRow.set(1, (Object)getThemeNameNo(themeName));
            mainRow.set(2, (Object)themeAttribute);
            final DataObject themeDO = LookUpUtil.getUserPersistence().get("ThemeAttributesMapping", mainRow);
            if (themeDO.containsTable("ThemeAttributesMapping")) {
                attributeValue = (String)themeDO.getFirstValue("ThemeAttributesMapping", 3);
            }
            else {
                attributeValue = themeAttribute;
            }
        }
        catch (final Exception e) {
            ThemesAPI.logger.log(Level.SEVERE, "Error !!! while fetching '{0}' attribute value of '{1}' theme.", new String[] { themeAttribute, themeName });
            throw new RuntimeException("Exception while fetching '" + themeAttribute + "' attribute value of '" + themeAttribute + "' theme.", e);
        }
        return attributeValue;
    }
    
    @Deprecated
    public static String handlePath(final String str, final HttpServletRequest request, final String themeDir) {
        return handlePath(str, request.getContextPath(), themeDir);
    }
    
    public static String handlePath(final String filePath, final String contextPath, final String themeDir) {
        if (filePath == null) {
            return null;
        }
        if (filePath.charAt(0) == '/') {
            return contextPath + filePath;
        }
        return themeDir + "/" + filePath;
    }
    
    public static DataObject getThemesForContext() {
        try {
            return LookUpUtil.getUserPersistence().get("Theme", (Criteria)null);
        }
        catch (final Exception e) {
            ThemesAPI.logger.log(Level.SEVERE, "Error !!! while fetching Theme DO for context {0}");
            throw new RuntimeException("Exception while fetching Theme DO for context", e);
        }
    }
    
    public static String getTheme() {
        Row themeRow = null;
        try {
            themeRow = getThemeForAccount(WebClientUtil.getAccountId());
        }
        catch (final Exception e) {
            ThemesAPI.logger.log(Level.FINE, e.getMessage() + " \n{0}", e.getCause());
        }
        if (themeRow == null) {
            return null;
        }
        return (String)themeRow.get(2);
    }
    
    @Deprecated
    public static String getThemeDirForRequest(final HttpServletRequest request) throws Exception {
        return getThemeDirForRequest(request.getContextPath());
    }
    
    public static String getThemeDirForRequest(final String contextPath) throws Exception {
        final long accountId = WebClientUtil.getAccountId();
        String themeDir = (String)StaticCache.getFromCache("THEME_DIR_" + accountId);
        if (themeDir == null) {
            final Row themeRow = getThemeForAccount(accountId);
            if (ClientProperties.cssHost != null && ClientProperties.cssVersion != null) {
                if (ClientProperties.prependversion) {
                    themeDir = ClientProperties.cssHost + "/" + ClientProperties.cssVersion + themeRow.get(4);
                }
                else {
                    themeDir = ClientProperties.cssHost + "/" + themeRow.get(4) + ClientProperties.cssVersion;
                }
            }
            else {
                themeDir = contextPath + "/" + themeRow.get(4);
            }
            StaticCache.addToCache("THEME_DIR_" + accountId, themeDir);
        }
        return themeDir;
    }
    
    public static String getStyleSnippet(final HttpServletRequest request) throws Exception {
        final String scheme = request.getScheme();
        HashMap<String, String> styleSnippet = null;
        if ("http".equals(scheme)) {
            styleSnippet = ThemesAPI.httpstyleSnippet;
        }
        else {
            styleSnippet = ThemesAPI.httpsstyleSnippet;
        }
        final String themeDir = getThemeDirForRequest(request);
        if (styleSnippet != null && themeDir != null) {
            final String styleSnippetStr = styleSnippet.get(themeDir);
            if (styleSnippetStr != null) {
                return styleSnippetStr;
            }
        }
        final String cssListFiles = System.getProperty("numberofcsslistfiles");
        int files = 0;
        if (cssListFiles != null) {
            files = Integer.parseInt(cssListFiles);
        }
        final StringBuilder snippetBuf = new StringBuilder();
        if (ClientProperties.useCompression) {
            if (cssListFiles != null) {
                for (int i = 0; i < files; ++i) {
                    snippetBuf.append("<link href='");
                    if (ClientProperties.useApache) {
                        snippetBuf.append(scheme + "://" + ClientProperties.cssHost);
                    }
                    if (ClientProperties.prependversion) {
                        snippetBuf.append("/" + ClientProperties.cssVersion).append("/").append(themeDir);
                    }
                    else {
                        snippetBuf.append(themeDir).append("/").append(ClientProperties.cssVersion);
                    }
                    snippetBuf.append("/styles/").append("client").append(i).append(".css' rel='stylesheet' type='text/css'/>\n");
                }
            }
            else {
                snippetBuf.append("<link href='");
                if (ClientProperties.useApache) {
                    snippetBuf.append(scheme + "://" + ClientProperties.cssHost);
                }
                if (ClientProperties.prependversion) {
                    snippetBuf.append("/" + ClientProperties.cssVersion).append('/').append(themeDir);
                }
                else {
                    snippetBuf.append(themeDir).append('/').append(ClientProperties.cssVersion);
                }
                snippetBuf.append("/styles/client.css' rel='stylesheet' type='text/css'/>\n");
            }
        }
        else {
            for (int i = 0; i < files; ++i) {
                final String cssFileList = request.getSession().getServletContext().getRealPath("/fileslist/csslist" + i + ".txt");
                try (final BufferedReader bf = new BufferedReader(new FileReader(cssFileList))) {
                    String input = null;
                    while ((input = bf.readLine()) != null) {
                        if (!input.trim().startsWith("#")) {
                            if (input.contains(ThemesAPI.THEMES_DIR_PH)) {
                                input = input.replace(ThemesAPI.THEMES_DIR_PH, themeDir);
                            }
                            snippetBuf.append("<link href='").append(IAMEncoder.encodeHTMLAttribute(input));
                            snippetBuf.append("' rel='stylesheet' type='text/css'/>\n");
                        }
                    }
                }
            }
        }
        final String styleSnippetStr2 = snippetBuf.toString();
        styleSnippet.put(themeDir, styleSnippetStr2);
        if ("http".equals(scheme)) {
            ThemesAPI.httpstyleSnippet = styleSnippet;
        }
        else {
            ThemesAPI.httpsstyleSnippet = styleSnippet;
        }
        return styleSnippetStr2;
    }
    
    static {
        ThemesAPI.logger = Logger.getLogger(ThemesAPI.class.getName());
        ThemesAPI.httpstyleSnippet = new HashMap<String, String>();
        ThemesAPI.httpsstyleSnippet = new HashMap<String, String>();
        ThemesAPI.THEMES_DIR_PH = "${THEME_DIR}";
    }
}
