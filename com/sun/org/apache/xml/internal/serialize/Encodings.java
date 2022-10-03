package com.sun.org.apache.xml.internal.serialize;

import java.util.concurrent.ConcurrentHashMap;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import java.util.Map;

class Encodings
{
    static final int DEFAULT_LAST_PRINTABLE = 127;
    static final int LAST_PRINTABLE_UNICODE = 65535;
    static final String[] UNICODE_ENCODINGS;
    static final String DEFAULT_ENCODING = "UTF8";
    private static final Map<String, EncodingInfo> _encodings;
    static final String JIS_DANGER_CHARS = "\\~\u007f¢£¥¬\u2014\u2015\u2016\u2026\u203e\u203e\u2225\u222f\u301c\uff3c\uff5e\uffe0\uffe1\uffe2\uffe3";
    
    static EncodingInfo getEncodingInfo(String encoding, final boolean allowJavaNames) throws UnsupportedEncodingException {
        EncodingInfo eInfo = null;
        if (encoding == null) {
            if ((eInfo = Encodings._encodings.get("UTF8")) != null) {
                return eInfo;
            }
            eInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping("UTF8"), "UTF8", 65535);
            Encodings._encodings.put("UTF8", eInfo);
            return eInfo;
        }
        else {
            encoding = encoding.toUpperCase(Locale.ENGLISH);
            final String jName = EncodingMap.getIANA2JavaMapping(encoding);
            if (jName == null) {
                if (!allowJavaNames) {
                    throw new UnsupportedEncodingException(encoding);
                }
                EncodingInfo.testJavaEncodingName(encoding);
                if ((eInfo = Encodings._encodings.get(encoding)) != null) {
                    return eInfo;
                }
                int i;
                for (i = 0; i < Encodings.UNICODE_ENCODINGS.length; ++i) {
                    if (Encodings.UNICODE_ENCODINGS[i].equalsIgnoreCase(encoding)) {
                        eInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping(encoding), encoding, 65535);
                        break;
                    }
                }
                if (i == Encodings.UNICODE_ENCODINGS.length) {
                    eInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping(encoding), encoding, 127);
                }
                Encodings._encodings.put(encoding, eInfo);
                return eInfo;
            }
            else {
                if ((eInfo = Encodings._encodings.get(jName)) != null) {
                    return eInfo;
                }
                int i;
                for (i = 0; i < Encodings.UNICODE_ENCODINGS.length; ++i) {
                    if (Encodings.UNICODE_ENCODINGS[i].equalsIgnoreCase(jName)) {
                        eInfo = new EncodingInfo(encoding, jName, 65535);
                        break;
                    }
                }
                if (i == Encodings.UNICODE_ENCODINGS.length) {
                    eInfo = new EncodingInfo(encoding, jName, 127);
                }
                Encodings._encodings.put(jName, eInfo);
                return eInfo;
            }
        }
    }
    
    static {
        UNICODE_ENCODINGS = new String[] { "Unicode", "UnicodeBig", "UnicodeLittle", "GB2312", "UTF8", "UTF-16" };
        _encodings = new ConcurrentHashMap<String, EncodingInfo>();
    }
}
