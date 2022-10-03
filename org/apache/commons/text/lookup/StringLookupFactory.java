package org.apache.commons.text.lookup;

import java.util.Map;

public final class StringLookupFactory
{
    public static final StringLookupFactory INSTANCE;
    public static final String KEY_BASE64_DECODER = "base64Decoder";
    public static final String KEY_BASE64_ENCODER = "base64Encoder";
    public static final String KEY_CONST = "const";
    public static final String KEY_DATE = "date";
    public static final String KEY_DNS = "dns";
    public static final String KEY_ENV = "env";
    public static final String KEY_FILE = "file";
    public static final String KEY_JAVA = "java";
    public static final String KEY_LOCALHOST = "localhost";
    public static final String KEY_PROPERTIES = "properties";
    public static final String KEY_RESOURCE_BUNDLE = "resourceBundle";
    public static final String KEY_SCRIPT = "script";
    public static final String KEY_SYS = "sys";
    public static final String KEY_URL = "url";
    public static final String KEY_URL_DECODER = "urlDecoder";
    public static final String KEY_URL_ENCODER = "urlEncoder";
    public static final String KEY_XML = "xml";
    
    public static void clear() {
        ConstantStringLookup.clear();
    }
    
    private StringLookupFactory() {
    }
    
    public void addDefaultStringLookups(final Map<String, StringLookup> stringLookupMap) {
        if (stringLookupMap != null) {
            stringLookupMap.put("base64", Base64DecoderStringLookup.INSTANCE);
            for (final DefaultStringLookup stringLookup : DefaultStringLookup.values()) {
                stringLookupMap.put(InterpolatorStringLookup.toKey(stringLookup.getKey()), stringLookup.getStringLookup());
            }
        }
    }
    
    public StringLookup base64DecoderStringLookup() {
        return Base64DecoderStringLookup.INSTANCE;
    }
    
    public StringLookup base64EncoderStringLookup() {
        return Base64EncoderStringLookup.INSTANCE;
    }
    
    @Deprecated
    public StringLookup base64StringLookup() {
        return Base64DecoderStringLookup.INSTANCE;
    }
    
    public StringLookup constantStringLookup() {
        return ConstantStringLookup.INSTANCE;
    }
    
    public StringLookup dateStringLookup() {
        return DateStringLookup.INSTANCE;
    }
    
    public StringLookup environmentVariableStringLookup() {
        return EnvironmentVariableStringLookup.INSTANCE;
    }
    
    public StringLookup fileStringLookup() {
        return FileStringLookup.INSTANCE;
    }
    
    public StringLookup interpolatorStringLookup() {
        return InterpolatorStringLookup.INSTANCE;
    }
    
    public StringLookup interpolatorStringLookup(final Map<String, StringLookup> stringLookupMap, final StringLookup defaultStringLookup, final boolean addDefaultLookups) {
        return new InterpolatorStringLookup(stringLookupMap, defaultStringLookup, addDefaultLookups);
    }
    
    public <V> StringLookup interpolatorStringLookup(final Map<String, V> map) {
        return new InterpolatorStringLookup((Map<String, V>)map);
    }
    
    public StringLookup interpolatorStringLookup(final StringLookup defaultStringLookup) {
        return new InterpolatorStringLookup(defaultStringLookup);
    }
    
    public StringLookup javaPlatformStringLookup() {
        return JavaPlatformStringLookup.INSTANCE;
    }
    
    public StringLookup localHostStringLookup() {
        return LocalHostStringLookup.INSTANCE;
    }
    
    public StringLookup dnsStringLookup() {
        return DnsStringLookup.INSTANCE;
    }
    
    public <V> StringLookup mapStringLookup(final Map<String, V> map) {
        return MapStringLookup.on(map);
    }
    
    public StringLookup nullStringLookup() {
        return NullStringLookup.INSTANCE;
    }
    
    public StringLookup propertiesStringLookup() {
        return PropertiesStringLookup.INSTANCE;
    }
    
    public StringLookup resourceBundleStringLookup() {
        return ResourceBundleStringLookup.INSTANCE;
    }
    
    public StringLookup resourceBundleStringLookup(final String bundleName) {
        return new ResourceBundleStringLookup(bundleName);
    }
    
    public StringLookup scriptStringLookup() {
        return ScriptStringLookup.INSTANCE;
    }
    
    public StringLookup systemPropertyStringLookup() {
        return SystemPropertyStringLookup.INSTANCE;
    }
    
    public StringLookup urlDecoderStringLookup() {
        return UrlDecoderStringLookup.INSTANCE;
    }
    
    public StringLookup urlEncoderStringLookup() {
        return UrlEncoderStringLookup.INSTANCE;
    }
    
    public StringLookup urlStringLookup() {
        return UrlStringLookup.INSTANCE;
    }
    
    public StringLookup xmlStringLookup() {
        return XmlStringLookup.INSTANCE;
    }
    
    static {
        INSTANCE = new StringLookupFactory();
    }
}
