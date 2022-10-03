package com.adventnet.webclient.util;

import java.util.Hashtable;
import java.util.Properties;
import com.adventnet.webclient.ClientException;
import javax.servlet.http.HttpServletRequest;

public class WebClientUtil
{
    private static WebClientUtil clientUtil;
    
    public static WebClientUtil getInstance() {
        if (WebClientUtil.clientUtil == null) {
            WebClientUtil.clientUtil = new WebClientUtil();
        }
        return WebClientUtil.clientUtil;
    }
    
    public boolean isAuthenticated(final HttpServletRequest request) throws ClientException {
        final boolean isAuthenticated = false;
        return isAuthenticated;
    }
    
    public String getContentType(String language, final String country) {
        String contentType = "ISO-8859-1";
        if (language == null) {
            return contentType;
        }
        language = language.trim();
        final Properties charsetTable = loadProperties();
        if (!language.equals("zh")) {
            contentType = charsetTable.getProperty(language);
            if (contentType == null) {
                contentType = "ISO-8859-1";
            }
            return contentType;
        }
        if (country.equals("CN")) {
            return "GB2312";
        }
        return "Big5";
    }
    
    private static Properties loadProperties() {
        final Properties table = new Properties();
        ((Hashtable<String, String>)table).put("sq", "ISO-8859-2");
        ((Hashtable<String, String>)table).put("ar", "ISO-8859-6");
        ((Hashtable<String, String>)table).put("bg", "ISO-8859-5");
        ((Hashtable<String, String>)table).put("be", "ISO-8859-5");
        ((Hashtable<String, String>)table).put("ca", "ISO-8859-1");
        ((Hashtable<String, String>)table).put("hr", "ISO-8859-2");
        ((Hashtable<String, String>)table).put("cs", "ISO-8859-2");
        ((Hashtable<String, String>)table).put("da", "ISO-8859-1");
        ((Hashtable<String, String>)table).put("nl", "ISO-8859-1");
        ((Hashtable<String, String>)table).put("en", "ISO-8859-1");
        ((Hashtable<String, String>)table).put("et", "ISO-8859-1");
        ((Hashtable<String, String>)table).put("fi", "ISO-8859-1");
        ((Hashtable<String, String>)table).put("fr", "ISO-8859-1");
        ((Hashtable<String, String>)table).put("de", "ISO-8859-1");
        ((Hashtable<String, String>)table).put("el", "ISO-8859-7");
        ((Hashtable<String, String>)table).put("he", "ISO-8859-8");
        ((Hashtable<String, String>)table).put("hu", "ISO-8859-2");
        ((Hashtable<String, String>)table).put("is", "ISO-8859-1");
        ((Hashtable<String, String>)table).put("it", "ISO-8859-1");
        ((Hashtable<String, String>)table).put("ja", "Shift_JIS,ISO-2022-JP,EUC-JP");
        ((Hashtable<String, String>)table).put("ko", "EUC-KR");
        ((Hashtable<String, String>)table).put("lv", "ISO-8859-2");
        ((Hashtable<String, String>)table).put("lt", "ISO-8859-2");
        ((Hashtable<String, String>)table).put("mk", "ISO-8859-5");
        ((Hashtable<String, String>)table).put("no", "ISO-8859-1");
        ((Hashtable<String, String>)table).put("pl", "ISO-8859-2");
        ((Hashtable<String, String>)table).put("pt", "ISO-8859-1");
        ((Hashtable<String, String>)table).put("ro", "ISO-8859-2");
        ((Hashtable<String, String>)table).put("ru", "ISO-8859-5,KOI8-R");
        ((Hashtable<String, String>)table).put("sr", "ISO-8859-KOI8-R");
        ((Hashtable<String, String>)table).put("sh", "ISO-8859-5,ISO-8859-2,KOI8-R");
        ((Hashtable<String, String>)table).put("sk", "ISO-8859-2");
        ((Hashtable<String, String>)table).put("sl", "ISO-8859-2");
        ((Hashtable<String, String>)table).put("es", "ISO-8859-1");
        ((Hashtable<String, String>)table).put("sv", "ISO-8859-1");
        ((Hashtable<String, String>)table).put("tr", "ISO-8859-9");
        ((Hashtable<String, String>)table).put("uk", "ISO-8859-5,KOI8-R");
        return table;
    }
}
