package com.adventnet.iam.security;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import ua_parser.Parser;
import ua_parser.Client;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.logging.Logger;

public class UserAgent
{
    private static final Logger LOGGER;
    public static final String OTHER = "Other";
    private static final Map<String, Pattern> DEVICEPATTERNS;
    private Client client;
    private String deviceType;
    private String userAgentStr;
    static Parser userAgentParser;
    private static final Pattern MAC_EMBEDDED_BROWSER_PATTERN;
    private static final Pattern CHROMIUM_BASED_BROWSER_PATTERN;
    private static final Pattern CHROMIUM_BROWSER_VERSION_PATTERN;
    
    public UserAgent(final String userAgentString) {
        this.userAgentStr = null;
        this.userAgentStr = userAgentString;
        this.client = UserAgent.userAgentParser.parse(userAgentString);
        this.deviceType = this.getDeviceType(this.getDeviceName(), userAgentString);
    }
    
    public UserAgent(final SecurityRequestWrapper request, String userAgentString) {
        this(userAgentString = ((userAgentString == null) ? request.getHeader("User-Agent") : userAgentString));
    }
    
    public String getDeviceName() {
        return this.client.device.family;
    }
    
    public String getOsName() {
        return this.client.os.family;
    }
    
    public int getOsMajorVersion() {
        return this.getVersion(this.client.os.major);
    }
    
    public int getOsMinorVersion() {
        return this.getVersion(this.client.os.minor);
    }
    
    public String getBrowserName() {
        return this.client.userAgent.family;
    }
    
    public int getBrowserMajorVersion() {
        return this.getVersion(this.client.userAgent.major);
    }
    
    public int getBrowserMinorVersion() {
        return this.getVersion(this.client.userAgent.minor);
    }
    
    public int getBrowserPatchVersion() {
        return this.getVersion(this.client.userAgent.patch);
    }
    
    public String getDeviceType() {
        return this.deviceType;
    }
    
    private String getDeviceType(final String deviceName, final String userAgent) {
        final DeviceType[] values;
        final DeviceType[] deviceTypes = values = DeviceType.values();
        for (final DeviceType deviceType : values) {
            final Pattern deviceTypePattern = getPattern(deviceType);
            if (deviceTypePattern != null && SecurityUtil.matchPattern(deviceName.toLowerCase(), deviceTypePattern)) {
                return deviceType.getValue();
            }
        }
        if (userAgent.contains("Android")) {
            return userAgent.contains("Mobile") ? DeviceType.MOBILE.getValue() : DeviceType.TABLET.getValue();
        }
        return "Other";
    }
    
    private static Pattern getPattern(final DeviceType type) {
        return UserAgent.DEVICEPATTERNS.get(type.value);
    }
    
    boolean isAllowedBrowserAndVersion(final String browserName, final int majorVersion) {
        return browserName != null && browserName.equals(this.getBrowserName()) && this.getBrowserMajorVersion() >= majorVersion;
    }
    
    boolean isEqualsOSAndVersion(final String osName, final int majorVersion, final int minorVersion) {
        if (osName == null) {
            return false;
        }
        final boolean isMajorMatched = osName.equalsIgnoreCase(this.getOsName()) && this.getOsMajorVersion() == majorVersion;
        if (minorVersion != -1) {
            return isMajorMatched && this.getOsMinorVersion() == minorVersion;
        }
        return isMajorMatched;
    }
    
    public boolean isAtleastAllowedBrowserAndVersion(final int major, final int minor, final int patch) {
        if (this.getBrowserMajorVersion() != major) {
            return this.getBrowserMajorVersion() > major;
        }
        if (this.getBrowserMinorVersion() != minor) {
            return this.getBrowserMinorVersion() > minor;
        }
        return this.getBrowserPatchVersion() >= patch;
    }
    
    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder(100);
        out.append("{ browser: { name :  ");
        out.append(this.getBrowserName());
        out.append(',').append("version : ").append(this.getBrowserMajorVersion());
        out.append('/').append(this.getBrowserMinorVersion());
        out.append("},").append("os : {");
        out.append("name : ").append(this.getOsName());
        out.append(",version:").append(this.getOsMajorVersion());
        out.append('/').append(this.getOsMinorVersion()).append("},");
        out.append("device:{ ").append("name : ").append(this.getDeviceName());
        out.append(',').append("type : ").append(this.getDeviceType());
        out.append('}');
        return out.toString();
    }
    
    private int getVersion(final String val) {
        int version = -1;
        try {
            version = Integer.valueOf(val);
        }
        catch (final NumberFormatException e) {
            UserAgent.LOGGER.fine("Invalid number : " + val + " Reason : " + e.getMessage());
        }
        return version;
    }
    
    String getUserAgentString() {
        return this.userAgentStr;
    }
    
    public boolean isSameSiteNoneIncompatibleClient() {
        return this.hasWebKitSameSiteBug() || this.dropsUnrecognizedSameSiteCookies();
    }
    
    private boolean hasWebKitSameSiteBug() {
        return this.isEqualsOSAndVersion("iOS", 12, -1) || (this.isEqualsOSAndVersion("Mac OS X", 10, 14) && ("Safari".equalsIgnoreCase(this.getBrowserName()) || this.isMacEmbeddedBrowser()));
    }
    
    private boolean isMacEmbeddedBrowser() {
        return UserAgent.MAC_EMBEDDED_BROWSER_PATTERN.matcher(this.userAgentStr).find();
    }
    
    private boolean dropsUnrecognizedSameSiteCookies() {
        return ("UC Browser".equalsIgnoreCase(this.getBrowserName()) && !this.isAtleastAllowedBrowserAndVersion(12, 13, 2)) || (this.isChromiumBased() && this.isChromiumVersionAtLeast(51) && !this.isChromiumVersionAtLeast(67));
    }
    
    private boolean isChromiumBased() {
        return UserAgent.CHROMIUM_BASED_BROWSER_PATTERN.matcher(this.userAgentStr).find();
    }
    
    private boolean isChromiumVersionAtLeast(final int major) {
        final Matcher matcher = UserAgent.CHROMIUM_BROWSER_VERSION_PATTERN.matcher(this.userAgentStr);
        int version = -1;
        if (matcher.find()) {
            version = this.getInt(matcher.group(1));
        }
        return version >= major;
    }
    
    public int getInt(final String str) {
        if (str != null) {
            try {
                return Integer.parseInt(str);
            }
            catch (final NumberFormatException ex) {
                UserAgent.LOGGER.log(Level.FINE, "HTTPCookie Class Number Format Exception", ex);
            }
        }
        return -1;
    }
    
    static {
        LOGGER = Logger.getLogger(UserAgent.class.getName());
        DEVICEPATTERNS = new HashMap<String, Pattern>(3);
        UserAgent.userAgentParser = null;
        MAC_EMBEDDED_BROWSER_PATTERN = Pattern.compile("^Mozilla\\/[\\.\\d]+ \\(Macintosh;.*Mac OS X [_\\d]+\\) AppleWebKit\\/[\\.\\d]+ \\(KHTML, like Gecko\\)$");
        CHROMIUM_BASED_BROWSER_PATTERN = Pattern.compile("Chrom(e|ium)");
        CHROMIUM_BROWSER_VERSION_PATTERN = Pattern.compile("Chrom[^ \\/]+\\/(\\d+)[\\.\\d]* ");
        final String confDirPath = SecurityUtil.getSecurityConfigurationDir();
        final String userAgentRegexFilePath = confDirPath + File.separator + "regexes.yaml";
        try {
            UserAgent.userAgentParser = new Parser((InputStream)new FileInputStream(userAgentRegexFilePath));
            UserAgent.LOGGER.log(Level.INFO, " regexes.yaml is being loaded from {0}", userAgentRegexFilePath);
        }
        catch (final FileNotFoundException e) {
            throw new RuntimeException("File \"regexes.yaml\" doesnot exist at " + confDirPath + " it must be fetched from the security conf location ");
        }
        Pattern pattern = Pattern.compile(".*(iphone|ipod|android|mobile|blackberry|opera mini|opera mobi|skyfire|maemo|windows phone|palm|iemobile|symbian|symbianos|fennec|mobile|sonyericsson|micromax|motorola|nokia|samsung|karbonn|htc|spice|lg-lg225|lg-a130|lg-cb630|alcatel|mtc540|acer_e101|amoi|ot-s686|ot-s692|asus-j206|asus-j501|asus-m303|asus-v80|asus-z801|cdm-8910|gt-i8350|gt-i8700|gt-i8710|gt-i8750|gt-i9300|gt-i9505|gt-s7530|le070|omnia 7|benq|(?:m|b|id)[ _]?bot[ _]?[0-9]+|power bot|generic smartphone|generic feature phone).*");
        UserAgent.DEVICEPATTERNS.put(DeviceType.MOBILE.value, pattern);
        pattern = Pattern.compile(".*(ipad|iprod|android 3|sch-i800|tablet|playbook|tablet|kindle|gt-p1000|sgh-t849|sgh-i987|shw-m180s|shw-m180l|sph-p100|a511|dell streak|silk|a101it|a70bht|nook|mid7015|ideos s7|sc-01c|m[ _]bot[ _]tab).*");
        UserAgent.DEVICEPATTERNS.put(DeviceType.TABLET.value, pattern);
        pattern = Pattern.compile(".*(googletv|smarttv|nettv|hbbtv|TV|playstation|nitendo|kylo|libnup|lg|sansui).*");
        UserAgent.DEVICEPATTERNS.put(DeviceType.TV.value, pattern);
        pattern = Pattern.compile(".*(desktop|mac).*");
        UserAgent.DEVICEPATTERNS.put(DeviceType.DESKTOP.value, pattern);
        pattern = Pattern.compile(".*(bot|spider).*");
        UserAgent.DEVICEPATTERNS.put(DeviceType.BOT.value, pattern);
    }
    
    public enum DeviceType
    {
        MOBILE("MOBILE"), 
        TABLET("TABLET"), 
        TV("TV"), 
        DESKTOP("DESKTOP"), 
        BOT("BOT");
        
        private final String value;
        
        private DeviceType(final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
}
