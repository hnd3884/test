package org.apache.catalina.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Deprecated
public class MIME2Java
{
    private static final Map<String, String> s_enchash;
    private static final Map<String, String> s_revhash;
    
    private MIME2Java() {
    }
    
    public static String convert(final String mimeCharsetName) {
        return MIME2Java.s_enchash.get(mimeCharsetName.toUpperCase(Locale.ENGLISH));
    }
    
    public static String reverse(final String encoding) {
        return MIME2Java.s_revhash.get(encoding.toUpperCase(Locale.ENGLISH));
    }
    
    static {
        (s_enchash = new HashMap<String, String>()).put("UTF-8", "UTF8");
        MIME2Java.s_enchash.put("US-ASCII", "8859_1");
        MIME2Java.s_enchash.put("ISO-8859-1", "8859_1");
        MIME2Java.s_enchash.put("ISO-8859-2", "8859_2");
        MIME2Java.s_enchash.put("ISO-8859-3", "8859_3");
        MIME2Java.s_enchash.put("ISO-8859-4", "8859_4");
        MIME2Java.s_enchash.put("ISO-8859-5", "8859_5");
        MIME2Java.s_enchash.put("ISO-8859-6", "8859_6");
        MIME2Java.s_enchash.put("ISO-8859-7", "8859_7");
        MIME2Java.s_enchash.put("ISO-8859-8", "8859_8");
        MIME2Java.s_enchash.put("ISO-8859-9", "8859_9");
        MIME2Java.s_enchash.put("ISO-2022-JP", "JIS");
        MIME2Java.s_enchash.put("SHIFT_JIS", "SJIS");
        MIME2Java.s_enchash.put("EUC-JP", "EUCJIS");
        MIME2Java.s_enchash.put("GB2312", "GB2312");
        MIME2Java.s_enchash.put("BIG5", "Big5");
        MIME2Java.s_enchash.put("EUC-KR", "KSC5601");
        MIME2Java.s_enchash.put("ISO-2022-KR", "ISO2022KR");
        MIME2Java.s_enchash.put("KOI8-R", "KOI8_R");
        MIME2Java.s_enchash.put("EBCDIC-CP-US", "CP037");
        MIME2Java.s_enchash.put("EBCDIC-CP-CA", "CP037");
        MIME2Java.s_enchash.put("EBCDIC-CP-NL", "CP037");
        MIME2Java.s_enchash.put("EBCDIC-CP-DK", "CP277");
        MIME2Java.s_enchash.put("EBCDIC-CP-NO", "CP277");
        MIME2Java.s_enchash.put("EBCDIC-CP-FI", "CP278");
        MIME2Java.s_enchash.put("EBCDIC-CP-SE", "CP278");
        MIME2Java.s_enchash.put("EBCDIC-CP-IT", "CP280");
        MIME2Java.s_enchash.put("EBCDIC-CP-ES", "CP284");
        MIME2Java.s_enchash.put("EBCDIC-CP-GB", "CP285");
        MIME2Java.s_enchash.put("EBCDIC-CP-FR", "CP297");
        MIME2Java.s_enchash.put("EBCDIC-CP-AR1", "CP420");
        MIME2Java.s_enchash.put("EBCDIC-CP-HE", "CP424");
        MIME2Java.s_enchash.put("EBCDIC-CP-CH", "CP500");
        MIME2Java.s_enchash.put("EBCDIC-CP-ROECE", "CP870");
        MIME2Java.s_enchash.put("EBCDIC-CP-YU", "CP870");
        MIME2Java.s_enchash.put("EBCDIC-CP-IS", "CP871");
        MIME2Java.s_enchash.put("EBCDIC-CP-AR2", "CP918");
        (s_revhash = new HashMap<String, String>()).put("UTF8", "UTF-8");
        MIME2Java.s_revhash.put("8859_1", "ISO-8859-1");
        MIME2Java.s_revhash.put("8859_2", "ISO-8859-2");
        MIME2Java.s_revhash.put("8859_3", "ISO-8859-3");
        MIME2Java.s_revhash.put("8859_4", "ISO-8859-4");
        MIME2Java.s_revhash.put("8859_5", "ISO-8859-5");
        MIME2Java.s_revhash.put("8859_6", "ISO-8859-6");
        MIME2Java.s_revhash.put("8859_7", "ISO-8859-7");
        MIME2Java.s_revhash.put("8859_8", "ISO-8859-8");
        MIME2Java.s_revhash.put("8859_9", "ISO-8859-9");
        MIME2Java.s_revhash.put("JIS", "ISO-2022-JP");
        MIME2Java.s_revhash.put("SJIS", "Shift_JIS");
        MIME2Java.s_revhash.put("EUCJIS", "EUC-JP");
        MIME2Java.s_revhash.put("GB2312", "GB2312");
        MIME2Java.s_revhash.put("BIG5", "Big5");
        MIME2Java.s_revhash.put("KSC5601", "EUC-KR");
        MIME2Java.s_revhash.put("ISO2022KR", "ISO-2022-KR");
        MIME2Java.s_revhash.put("KOI8_R", "KOI8-R");
        MIME2Java.s_revhash.put("CP037", "EBCDIC-CP-US");
        MIME2Java.s_revhash.put("CP037", "EBCDIC-CP-CA");
        MIME2Java.s_revhash.put("CP037", "EBCDIC-CP-NL");
        MIME2Java.s_revhash.put("CP277", "EBCDIC-CP-DK");
        MIME2Java.s_revhash.put("CP277", "EBCDIC-CP-NO");
        MIME2Java.s_revhash.put("CP278", "EBCDIC-CP-FI");
        MIME2Java.s_revhash.put("CP278", "EBCDIC-CP-SE");
        MIME2Java.s_revhash.put("CP280", "EBCDIC-CP-IT");
        MIME2Java.s_revhash.put("CP284", "EBCDIC-CP-ES");
        MIME2Java.s_revhash.put("CP285", "EBCDIC-CP-GB");
        MIME2Java.s_revhash.put("CP297", "EBCDIC-CP-FR");
        MIME2Java.s_revhash.put("CP420", "EBCDIC-CP-AR1");
        MIME2Java.s_revhash.put("CP424", "EBCDIC-CP-HE");
        MIME2Java.s_revhash.put("CP500", "EBCDIC-CP-CH");
        MIME2Java.s_revhash.put("CP870", "EBCDIC-CP-ROECE");
        MIME2Java.s_revhash.put("CP870", "EBCDIC-CP-YU");
        MIME2Java.s_revhash.put("CP871", "EBCDIC-CP-IS");
        MIME2Java.s_revhash.put("CP918", "EBCDIC-CP-AR2");
    }
}
