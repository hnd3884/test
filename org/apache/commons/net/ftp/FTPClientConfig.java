package org.apache.commons.net.ftp;

import java.util.TreeMap;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Locale;
import java.text.DateFormatSymbols;
import java.util.Map;

public class FTPClientConfig
{
    public static final String SYST_UNIX = "UNIX";
    public static final String SYST_UNIX_TRIM_LEADING = "UNIX_LTRIM";
    public static final String SYST_VMS = "VMS";
    public static final String SYST_NT = "WINDOWS";
    public static final String SYST_OS2 = "OS/2";
    public static final String SYST_OS400 = "OS/400";
    public static final String SYST_AS400 = "AS/400";
    public static final String SYST_MVS = "MVS";
    public static final String SYST_L8 = "TYPE: L8";
    public static final String SYST_NETWARE = "NETWARE";
    public static final String SYST_MACOS_PETER = "MACOS PETER";
    private final String serverSystemKey;
    private String defaultDateFormatStr;
    private String recentDateFormatStr;
    private boolean lenientFutureDates;
    private String serverLanguageCode;
    private String shortMonthNames;
    private String serverTimeZoneId;
    private boolean saveUnparseableEntries;
    private static final Map<String, Object> LANGUAGE_CODE_MAP;
    
    public FTPClientConfig(final String systemKey) {
        this.defaultDateFormatStr = null;
        this.recentDateFormatStr = null;
        this.lenientFutureDates = true;
        this.serverLanguageCode = null;
        this.shortMonthNames = null;
        this.serverTimeZoneId = null;
        this.saveUnparseableEntries = false;
        this.serverSystemKey = systemKey;
    }
    
    public FTPClientConfig() {
        this("UNIX");
    }
    
    public FTPClientConfig(final String systemKey, final String defaultDateFormatStr, final String recentDateFormatStr) {
        this(systemKey);
        this.defaultDateFormatStr = defaultDateFormatStr;
        this.recentDateFormatStr = recentDateFormatStr;
    }
    
    public FTPClientConfig(final String systemKey, final String defaultDateFormatStr, final String recentDateFormatStr, final String serverLanguageCode, final String shortMonthNames, final String serverTimeZoneId) {
        this(systemKey);
        this.defaultDateFormatStr = defaultDateFormatStr;
        this.recentDateFormatStr = recentDateFormatStr;
        this.serverLanguageCode = serverLanguageCode;
        this.shortMonthNames = shortMonthNames;
        this.serverTimeZoneId = serverTimeZoneId;
    }
    
    public FTPClientConfig(final String systemKey, final String defaultDateFormatStr, final String recentDateFormatStr, final String serverLanguageCode, final String shortMonthNames, final String serverTimeZoneId, final boolean lenientFutureDates, final boolean saveUnparseableEntries) {
        this(systemKey);
        this.defaultDateFormatStr = defaultDateFormatStr;
        this.lenientFutureDates = lenientFutureDates;
        this.recentDateFormatStr = recentDateFormatStr;
        this.saveUnparseableEntries = saveUnparseableEntries;
        this.serverLanguageCode = serverLanguageCode;
        this.shortMonthNames = shortMonthNames;
        this.serverTimeZoneId = serverTimeZoneId;
    }
    
    FTPClientConfig(final String systemKey, final FTPClientConfig config) {
        this.defaultDateFormatStr = null;
        this.recentDateFormatStr = null;
        this.lenientFutureDates = true;
        this.serverLanguageCode = null;
        this.shortMonthNames = null;
        this.serverTimeZoneId = null;
        this.saveUnparseableEntries = false;
        this.serverSystemKey = systemKey;
        this.defaultDateFormatStr = config.defaultDateFormatStr;
        this.lenientFutureDates = config.lenientFutureDates;
        this.recentDateFormatStr = config.recentDateFormatStr;
        this.saveUnparseableEntries = config.saveUnparseableEntries;
        this.serverLanguageCode = config.serverLanguageCode;
        this.serverTimeZoneId = config.serverTimeZoneId;
        this.shortMonthNames = config.shortMonthNames;
    }
    
    public FTPClientConfig(final FTPClientConfig config) {
        this.defaultDateFormatStr = null;
        this.recentDateFormatStr = null;
        this.lenientFutureDates = true;
        this.serverLanguageCode = null;
        this.shortMonthNames = null;
        this.serverTimeZoneId = null;
        this.saveUnparseableEntries = false;
        this.serverSystemKey = config.serverSystemKey;
        this.defaultDateFormatStr = config.defaultDateFormatStr;
        this.lenientFutureDates = config.lenientFutureDates;
        this.recentDateFormatStr = config.recentDateFormatStr;
        this.saveUnparseableEntries = config.saveUnparseableEntries;
        this.serverLanguageCode = config.serverLanguageCode;
        this.serverTimeZoneId = config.serverTimeZoneId;
        this.shortMonthNames = config.shortMonthNames;
    }
    
    public String getServerSystemKey() {
        return this.serverSystemKey;
    }
    
    public String getDefaultDateFormatStr() {
        return this.defaultDateFormatStr;
    }
    
    public String getRecentDateFormatStr() {
        return this.recentDateFormatStr;
    }
    
    public String getServerTimeZoneId() {
        return this.serverTimeZoneId;
    }
    
    public String getShortMonthNames() {
        return this.shortMonthNames;
    }
    
    public String getServerLanguageCode() {
        return this.serverLanguageCode;
    }
    
    public boolean isLenientFutureDates() {
        return this.lenientFutureDates;
    }
    
    public void setDefaultDateFormatStr(final String defaultDateFormatStr) {
        this.defaultDateFormatStr = defaultDateFormatStr;
    }
    
    public void setRecentDateFormatStr(final String recentDateFormatStr) {
        this.recentDateFormatStr = recentDateFormatStr;
    }
    
    public void setLenientFutureDates(final boolean lenientFutureDates) {
        this.lenientFutureDates = lenientFutureDates;
    }
    
    public void setServerTimeZoneId(final String serverTimeZoneId) {
        this.serverTimeZoneId = serverTimeZoneId;
    }
    
    public void setShortMonthNames(final String shortMonthNames) {
        this.shortMonthNames = shortMonthNames;
    }
    
    public void setServerLanguageCode(final String serverLanguageCode) {
        this.serverLanguageCode = serverLanguageCode;
    }
    
    public static DateFormatSymbols lookupDateFormatSymbols(final String languageCode) {
        final Object lang = FTPClientConfig.LANGUAGE_CODE_MAP.get(languageCode);
        if (lang != null) {
            if (lang instanceof Locale) {
                return new DateFormatSymbols((Locale)lang);
            }
            if (lang instanceof String) {
                return getDateFormatSymbols((String)lang);
            }
        }
        return new DateFormatSymbols(Locale.US);
    }
    
    public static DateFormatSymbols getDateFormatSymbols(final String shortmonths) {
        final String[] months = splitShortMonthString(shortmonths);
        final DateFormatSymbols dfs = new DateFormatSymbols(Locale.US);
        dfs.setShortMonths(months);
        return dfs;
    }
    
    private static String[] splitShortMonthString(final String shortmonths) {
        final StringTokenizer st = new StringTokenizer(shortmonths, "|");
        final int monthcnt = st.countTokens();
        if (12 != monthcnt) {
            throw new IllegalArgumentException("expecting a pipe-delimited string containing 12 tokens");
        }
        final String[] months = new String[13];
        int pos = 0;
        while (st.hasMoreTokens()) {
            months[pos++] = st.nextToken();
        }
        months[pos] = "";
        return months;
    }
    
    public static Collection<String> getSupportedLanguageCodes() {
        return FTPClientConfig.LANGUAGE_CODE_MAP.keySet();
    }
    
    public void setUnparseableEntries(final boolean saveUnparseable) {
        this.saveUnparseableEntries = saveUnparseable;
    }
    
    public boolean getUnparseableEntries() {
        return this.saveUnparseableEntries;
    }
    
    static {
        (LANGUAGE_CODE_MAP = new TreeMap<String, Object>()).put("en", Locale.ENGLISH);
        FTPClientConfig.LANGUAGE_CODE_MAP.put("de", Locale.GERMAN);
        FTPClientConfig.LANGUAGE_CODE_MAP.put("it", Locale.ITALIAN);
        FTPClientConfig.LANGUAGE_CODE_MAP.put("es", new Locale("es", "", ""));
        FTPClientConfig.LANGUAGE_CODE_MAP.put("pt", new Locale("pt", "", ""));
        FTPClientConfig.LANGUAGE_CODE_MAP.put("da", new Locale("da", "", ""));
        FTPClientConfig.LANGUAGE_CODE_MAP.put("sv", new Locale("sv", "", ""));
        FTPClientConfig.LANGUAGE_CODE_MAP.put("no", new Locale("no", "", ""));
        FTPClientConfig.LANGUAGE_CODE_MAP.put("nl", new Locale("nl", "", ""));
        FTPClientConfig.LANGUAGE_CODE_MAP.put("ro", new Locale("ro", "", ""));
        FTPClientConfig.LANGUAGE_CODE_MAP.put("sq", new Locale("sq", "", ""));
        FTPClientConfig.LANGUAGE_CODE_MAP.put("sh", new Locale("sh", "", ""));
        FTPClientConfig.LANGUAGE_CODE_MAP.put("sk", new Locale("sk", "", ""));
        FTPClientConfig.LANGUAGE_CODE_MAP.put("sl", new Locale("sl", "", ""));
        FTPClientConfig.LANGUAGE_CODE_MAP.put("fr", "jan|f\u00e9v|mar|avr|mai|jun|jui|ao\u00fb|sep|oct|nov|d\u00e9c");
    }
}
