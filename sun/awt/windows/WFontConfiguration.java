package sun.awt.windows;

import sun.awt.FontDescriptor;
import java.nio.charset.Charset;
import java.util.Hashtable;
import sun.font.SunFontManager;
import java.util.HashMap;
import sun.awt.FontConfiguration;

public final class WFontConfiguration extends FontConfiguration
{
    private boolean useCompatibilityFallbacks;
    private static HashMap subsetCharsetMap;
    private static HashMap subsetEncodingMap;
    private static String textInputCharset;
    
    public WFontConfiguration(final SunFontManager sunFontManager) {
        super(sunFontManager);
        this.useCompatibilityFallbacks = "windows-1252".equals(WFontConfiguration.encoding);
        this.initTables(WFontConfiguration.encoding);
    }
    
    public WFontConfiguration(final SunFontManager sunFontManager, final boolean b, final boolean b2) {
        super(sunFontManager, b, b2);
        this.useCompatibilityFallbacks = "windows-1252".equals(WFontConfiguration.encoding);
    }
    
    @Override
    protected void initReorderMap() {
        if (WFontConfiguration.encoding.equalsIgnoreCase("windows-31j")) {
            (WFontConfiguration.localeMap = new Hashtable()).put("dialoginput.plain.japanese", "MS Mincho");
            WFontConfiguration.localeMap.put("dialoginput.bold.japanese", "MS Mincho");
            WFontConfiguration.localeMap.put("dialoginput.italic.japanese", "MS Mincho");
            WFontConfiguration.localeMap.put("dialoginput.bolditalic.japanese", "MS Mincho");
        }
        (this.reorderMap = new HashMap()).put("UTF-8.hi", "devanagari");
        this.reorderMap.put("windows-1255", "hebrew");
        this.reorderMap.put("x-windows-874", "thai");
        this.reorderMap.put("windows-31j", "japanese");
        this.reorderMap.put("x-windows-949", "korean");
        this.reorderMap.put("GBK", "chinese-ms936");
        this.reorderMap.put("GB18030", "chinese-gb18030");
        this.reorderMap.put("x-windows-950", "chinese-ms950");
        this.reorderMap.put("x-MS950-HKSCS", this.split("chinese-ms950,chinese-hkscs"));
    }
    
    @Override
    protected void setOsNameAndVersion() {
        super.setOsNameAndVersion();
        if (WFontConfiguration.osName.startsWith("Windows")) {
            final int index = WFontConfiguration.osName.indexOf(32);
            if (index == -1) {
                WFontConfiguration.osName = null;
            }
            else {
                final int index2 = WFontConfiguration.osName.indexOf(32, index + 1);
                if (index2 == -1) {
                    WFontConfiguration.osName = WFontConfiguration.osName.substring(index + 1);
                }
                else {
                    WFontConfiguration.osName = WFontConfiguration.osName.substring(index + 1, index2);
                }
            }
            WFontConfiguration.osVersion = null;
        }
    }
    
    @Override
    public String getFallbackFamilyName(final String s, final String s2) {
        if (this.useCompatibilityFallbacks) {
            final String compatibilityFamilyName = this.getCompatibilityFamilyName(s);
            if (compatibilityFamilyName != null) {
                return compatibilityFamilyName;
            }
        }
        return s2;
    }
    
    @Override
    protected String makeAWTFontName(final String s, final String s2) {
        String s3 = WFontConfiguration.subsetCharsetMap.get(s2);
        if (s3 == null) {
            s3 = "DEFAULT_CHARSET";
        }
        return s + "," + s3;
    }
    
    @Override
    protected String getEncoding(final String s, final String s2) {
        String s3 = WFontConfiguration.subsetEncodingMap.get(s2);
        if (s3 == null) {
            s3 = "default";
        }
        return s3;
    }
    
    @Override
    protected Charset getDefaultFontCharset(final String s) {
        return new WDefaultFontCharset(s);
    }
    
    public String getFaceNameFromComponentFontName(final String s) {
        return s;
    }
    
    @Override
    protected String getFileNameFromComponentFontName(final String s) {
        return this.getFileNameFromPlatformName(s);
    }
    
    public String getTextComponentFontName(final String s, final int n) {
        final FontDescriptor[] fontDescriptors = this.getFontDescriptors(s, n);
        String s2 = this.findFontWithCharset(fontDescriptors, WFontConfiguration.textInputCharset);
        if (s2 == null) {
            s2 = this.findFontWithCharset(fontDescriptors, "DEFAULT_CHARSET");
        }
        return s2;
    }
    
    private String findFontWithCharset(final FontDescriptor[] array, final String s) {
        String s2 = null;
        for (int i = 0; i < array.length; ++i) {
            final String nativeName = array[i].getNativeName();
            if (nativeName.endsWith(s)) {
                s2 = nativeName;
            }
        }
        return s2;
    }
    
    private void initTables(final String s) {
        WFontConfiguration.subsetCharsetMap.put("alphabetic", "ANSI_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("alphabetic/1252", "ANSI_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("alphabetic/default", "DEFAULT_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("arabic", "ARABIC_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("chinese-ms936", "GB2312_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("chinese-gb18030", "GB2312_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("chinese-ms950", "CHINESEBIG5_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("chinese-hkscs", "CHINESEBIG5_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("cyrillic", "RUSSIAN_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("devanagari", "DEFAULT_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("dingbats", "SYMBOL_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("greek", "GREEK_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("hebrew", "HEBREW_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("japanese", "SHIFTJIS_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("korean", "HANGEUL_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("latin", "ANSI_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("symbol", "SYMBOL_CHARSET");
        WFontConfiguration.subsetCharsetMap.put("thai", "THAI_CHARSET");
        WFontConfiguration.subsetEncodingMap.put("alphabetic", "default");
        WFontConfiguration.subsetEncodingMap.put("alphabetic/1252", "windows-1252");
        WFontConfiguration.subsetEncodingMap.put("alphabetic/default", s);
        WFontConfiguration.subsetEncodingMap.put("arabic", "windows-1256");
        WFontConfiguration.subsetEncodingMap.put("chinese-ms936", "GBK");
        WFontConfiguration.subsetEncodingMap.put("chinese-gb18030", "GB18030");
        if ("x-MS950-HKSCS".equals(s)) {
            WFontConfiguration.subsetEncodingMap.put("chinese-ms950", "x-MS950-HKSCS");
        }
        else {
            WFontConfiguration.subsetEncodingMap.put("chinese-ms950", "x-windows-950");
        }
        WFontConfiguration.subsetEncodingMap.put("chinese-hkscs", "sun.awt.HKSCS");
        WFontConfiguration.subsetEncodingMap.put("cyrillic", "windows-1251");
        WFontConfiguration.subsetEncodingMap.put("devanagari", "UTF-16LE");
        WFontConfiguration.subsetEncodingMap.put("dingbats", "sun.awt.windows.WingDings");
        WFontConfiguration.subsetEncodingMap.put("greek", "windows-1253");
        WFontConfiguration.subsetEncodingMap.put("hebrew", "windows-1255");
        WFontConfiguration.subsetEncodingMap.put("japanese", "windows-31j");
        WFontConfiguration.subsetEncodingMap.put("korean", "x-windows-949");
        WFontConfiguration.subsetEncodingMap.put("latin", "windows-1252");
        WFontConfiguration.subsetEncodingMap.put("symbol", "sun.awt.Symbol");
        WFontConfiguration.subsetEncodingMap.put("thai", "x-windows-874");
        if ("windows-1256".equals(s)) {
            WFontConfiguration.textInputCharset = "ARABIC_CHARSET";
        }
        else if ("GBK".equals(s)) {
            WFontConfiguration.textInputCharset = "GB2312_CHARSET";
        }
        else if ("GB18030".equals(s)) {
            WFontConfiguration.textInputCharset = "GB2312_CHARSET";
        }
        else if ("x-windows-950".equals(s)) {
            WFontConfiguration.textInputCharset = "CHINESEBIG5_CHARSET";
        }
        else if ("x-MS950-HKSCS".equals(s)) {
            WFontConfiguration.textInputCharset = "CHINESEBIG5_CHARSET";
        }
        else if ("windows-1251".equals(s)) {
            WFontConfiguration.textInputCharset = "RUSSIAN_CHARSET";
        }
        else if ("UTF-8".equals(s)) {
            WFontConfiguration.textInputCharset = "DEFAULT_CHARSET";
        }
        else if ("windows-1253".equals(s)) {
            WFontConfiguration.textInputCharset = "GREEK_CHARSET";
        }
        else if ("windows-1255".equals(s)) {
            WFontConfiguration.textInputCharset = "HEBREW_CHARSET";
        }
        else if ("windows-31j".equals(s)) {
            WFontConfiguration.textInputCharset = "SHIFTJIS_CHARSET";
        }
        else if ("x-windows-949".equals(s)) {
            WFontConfiguration.textInputCharset = "HANGEUL_CHARSET";
        }
        else if ("x-windows-874".equals(s)) {
            WFontConfiguration.textInputCharset = "THAI_CHARSET";
        }
        else {
            WFontConfiguration.textInputCharset = "DEFAULT_CHARSET";
        }
    }
    
    static {
        WFontConfiguration.subsetCharsetMap = new HashMap();
        WFontConfiguration.subsetEncodingMap = new HashMap();
    }
}
