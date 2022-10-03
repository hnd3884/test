package org.apache.xml.serialize;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import org.apache.xerces.util.EncodingMap;
import java.util.Hashtable;

public class Encodings
{
    static final int DEFAULT_LAST_PRINTABLE = 127;
    static final int LAST_PRINTABLE_UNICODE = 65535;
    static final String[] UNICODE_ENCODINGS;
    static final String DEFAULT_ENCODING = "UTF8";
    static Hashtable _encodings;
    static final String JIS_DANGER_CHARS = "\\~\u007f¢£¥¬\u2014\u2015\u2016\u2026\u203e\u203e\u2225\u222f\u301c\uff3c\uff5e\uffe0\uffe1\uffe2\uffe3";
    
    static EncodingInfo getEncodingInfo(String upperCase, final boolean b) throws UnsupportedEncodingException {
        if (upperCase == null) {
            final EncodingInfo encodingInfo;
            if ((encodingInfo = Encodings._encodings.get("UTF8")) != null) {
                return encodingInfo;
            }
            final EncodingInfo encodingInfo2 = new EncodingInfo(EncodingMap.getJava2IANAMapping("UTF8"), "UTF8", 65535);
            Encodings._encodings.put("UTF8", encodingInfo2);
            return encodingInfo2;
        }
        else {
            upperCase = upperCase.toUpperCase(Locale.ENGLISH);
            final String iana2JavaMapping = EncodingMap.getIANA2JavaMapping(upperCase);
            if (iana2JavaMapping == null) {
                if (!b) {
                    throw new UnsupportedEncodingException(upperCase);
                }
                EncodingInfo.testJavaEncodingName(upperCase);
                EncodingInfo encodingInfo3;
                if ((encodingInfo3 = Encodings._encodings.get(upperCase)) != null) {
                    return encodingInfo3;
                }
                int i;
                for (i = 0; i < Encodings.UNICODE_ENCODINGS.length; ++i) {
                    if (Encodings.UNICODE_ENCODINGS[i].equalsIgnoreCase(upperCase)) {
                        encodingInfo3 = new EncodingInfo(EncodingMap.getJava2IANAMapping(upperCase), upperCase, 65535);
                        break;
                    }
                }
                if (i == Encodings.UNICODE_ENCODINGS.length) {
                    encodingInfo3 = new EncodingInfo(EncodingMap.getJava2IANAMapping(upperCase), upperCase, 127);
                }
                Encodings._encodings.put(upperCase, encodingInfo3);
                return encodingInfo3;
            }
            else {
                EncodingInfo encodingInfo4;
                if ((encodingInfo4 = Encodings._encodings.get(iana2JavaMapping)) != null) {
                    return encodingInfo4;
                }
                int j;
                for (j = 0; j < Encodings.UNICODE_ENCODINGS.length; ++j) {
                    if (Encodings.UNICODE_ENCODINGS[j].equalsIgnoreCase(iana2JavaMapping)) {
                        encodingInfo4 = new EncodingInfo(upperCase, iana2JavaMapping, 65535);
                        break;
                    }
                }
                if (j == Encodings.UNICODE_ENCODINGS.length) {
                    encodingInfo4 = new EncodingInfo(upperCase, iana2JavaMapping, 127);
                }
                Encodings._encodings.put(iana2JavaMapping, encodingInfo4);
                return encodingInfo4;
            }
        }
    }
    
    static {
        UNICODE_ENCODINGS = new String[] { "Unicode", "UnicodeBig", "UnicodeLittle", "GB2312", "UTF8", "UTF-16" };
        Encodings._encodings = new Hashtable();
    }
}
