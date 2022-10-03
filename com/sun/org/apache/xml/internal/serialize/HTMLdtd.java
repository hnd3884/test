package com.sun.org.apache.xml.internal.serialize;

import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class HTMLdtd
{
    public static final String HTMLPublicId = "-//W3C//DTD HTML 4.01//EN";
    public static final String HTMLSystemId = "http://www.w3.org/TR/html4/strict.dtd";
    public static final String XHTMLPublicId = "-//W3C//DTD XHTML 1.0 Strict//EN";
    public static final String XHTMLSystemId = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
    private static Map<Integer, String> _byChar;
    private static Map<String, Integer> _byName;
    private static final Map<String, String[]> _boolAttrs;
    private static final Map<String, Integer> _elemDefs;
    private static final String ENTITIES_RESOURCE = "HTMLEntities.res";
    private static final int ONLY_OPENING = 1;
    private static final int ELEM_CONTENT = 2;
    private static final int PRESERVE = 4;
    private static final int OPT_CLOSING = 8;
    private static final int EMPTY = 17;
    private static final int ALLOWED_HEAD = 32;
    private static final int CLOSE_P = 64;
    private static final int CLOSE_DD_DT = 128;
    private static final int CLOSE_SELF = 256;
    private static final int CLOSE_TABLE = 512;
    private static final int CLOSE_TH_TD = 16384;
    
    public static boolean isEmptyTag(final String tagName) {
        return isElement(tagName, 17);
    }
    
    public static boolean isElementContent(final String tagName) {
        return isElement(tagName, 2);
    }
    
    public static boolean isPreserveSpace(final String tagName) {
        return isElement(tagName, 4);
    }
    
    public static boolean isOptionalClosing(final String tagName) {
        return isElement(tagName, 8);
    }
    
    public static boolean isOnlyOpening(final String tagName) {
        return isElement(tagName, 1);
    }
    
    public static boolean isClosing(final String tagName, final String openTag) {
        if (openTag.equalsIgnoreCase("HEAD")) {
            return !isElement(tagName, 32);
        }
        if (openTag.equalsIgnoreCase("P")) {
            return isElement(tagName, 64);
        }
        if (openTag.equalsIgnoreCase("DT") || openTag.equalsIgnoreCase("DD")) {
            return isElement(tagName, 128);
        }
        if (openTag.equalsIgnoreCase("LI") || openTag.equalsIgnoreCase("OPTION")) {
            return isElement(tagName, 256);
        }
        if (openTag.equalsIgnoreCase("THEAD") || openTag.equalsIgnoreCase("TFOOT") || openTag.equalsIgnoreCase("TBODY") || openTag.equalsIgnoreCase("TR") || openTag.equalsIgnoreCase("COLGROUP")) {
            return isElement(tagName, 512);
        }
        return (openTag.equalsIgnoreCase("TH") || openTag.equalsIgnoreCase("TD")) && isElement(tagName, 16384);
    }
    
    public static boolean isURI(final String tagName, final String attrName) {
        return attrName.equalsIgnoreCase("href") || attrName.equalsIgnoreCase("src");
    }
    
    public static boolean isBoolean(final String tagName, final String attrName) {
        final String[] attrNames = HTMLdtd._boolAttrs.get(tagName.toUpperCase(Locale.ENGLISH));
        if (attrNames == null) {
            return false;
        }
        for (int i = 0; i < attrNames.length; ++i) {
            if (attrNames[i].equalsIgnoreCase(attrName)) {
                return true;
            }
        }
        return false;
    }
    
    public static int charFromName(final String name) {
        initialize();
        final Object value = HTMLdtd._byName.get(name);
        if (value != null && value instanceof Integer) {
            return (int)value;
        }
        return -1;
    }
    
    public static String fromChar(final int value) {
        if (value > 65535) {
            return null;
        }
        initialize();
        final String name = HTMLdtd._byChar.get(value);
        return name;
    }
    
    private static void initialize() {
        InputStream is = null;
        BufferedReader reader = null;
        if (HTMLdtd._byName != null) {
            return;
        }
        try {
            HTMLdtd._byName = new HashMap<String, Integer>();
            HTMLdtd._byChar = new HashMap<Integer, String>();
            is = HTMLdtd.class.getResourceAsStream("HTMLEntities.res");
            if (is == null) {
                throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ResourceNotFound", new Object[] { "HTMLEntities.res" }));
            }
            reader = new BufferedReader(new InputStreamReader(is, "ASCII"));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.length() != 0 && line.charAt(0) != '#') {
                    int index = line.indexOf(32);
                    if (index > 1) {
                        final String name = line.substring(0, index);
                        if (++index < line.length()) {
                            String value = line.substring(index);
                            index = value.indexOf(32);
                            if (index > 0) {
                                value = value.substring(0, index);
                            }
                            final int code = Integer.parseInt(value);
                            defineEntity(name, (char)code);
                        }
                    }
                }
            }
            is.close();
        }
        catch (final Exception except) {
            throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ResourceNotLoaded", new Object[] { "HTMLEntities.res", except.toString() }));
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    private static void defineEntity(final String name, final char value) {
        if (HTMLdtd._byName.get(name) == null) {
            HTMLdtd._byName.put(name, new Integer(value));
            HTMLdtd._byChar.put(new Integer(value), name);
        }
    }
    
    private static void defineElement(final String name, final int flags) {
        HTMLdtd._elemDefs.put(name, flags);
    }
    
    private static void defineBoolean(final String tagName, final String attrName) {
        defineBoolean(tagName, new String[] { attrName });
    }
    
    private static void defineBoolean(final String tagName, final String[] attrNames) {
        HTMLdtd._boolAttrs.put(tagName, attrNames);
    }
    
    private static boolean isElement(final String name, final int flag) {
        final Integer flags = HTMLdtd._elemDefs.get(name.toUpperCase(Locale.ENGLISH));
        return flags != null && (flags & flag) == flag;
    }
    
    static {
        _elemDefs = new HashMap<String, Integer>();
        defineElement("ADDRESS", 64);
        defineElement("AREA", 17);
        defineElement("BASE", 49);
        defineElement("BASEFONT", 17);
        defineElement("BLOCKQUOTE", 64);
        defineElement("BODY", 8);
        defineElement("BR", 17);
        defineElement("COL", 17);
        defineElement("COLGROUP", 522);
        defineElement("DD", 137);
        defineElement("DIV", 64);
        defineElement("DL", 66);
        defineElement("DT", 137);
        defineElement("FIELDSET", 64);
        defineElement("FORM", 64);
        defineElement("FRAME", 25);
        defineElement("H1", 64);
        defineElement("H2", 64);
        defineElement("H3", 64);
        defineElement("H4", 64);
        defineElement("H5", 64);
        defineElement("H6", 64);
        defineElement("HEAD", 10);
        defineElement("HR", 81);
        defineElement("HTML", 10);
        defineElement("IMG", 17);
        defineElement("INPUT", 17);
        defineElement("ISINDEX", 49);
        defineElement("LI", 265);
        defineElement("LINK", 49);
        defineElement("MAP", 32);
        defineElement("META", 49);
        defineElement("OL", 66);
        defineElement("OPTGROUP", 2);
        defineElement("OPTION", 265);
        defineElement("P", 328);
        defineElement("PARAM", 17);
        defineElement("PRE", 68);
        defineElement("SCRIPT", 36);
        defineElement("NOSCRIPT", 36);
        defineElement("SELECT", 2);
        defineElement("STYLE", 36);
        defineElement("TABLE", 66);
        defineElement("TBODY", 522);
        defineElement("TD", 16392);
        defineElement("TEXTAREA", 4);
        defineElement("TFOOT", 522);
        defineElement("TH", 16392);
        defineElement("THEAD", 522);
        defineElement("TITLE", 32);
        defineElement("TR", 522);
        defineElement("UL", 66);
        _boolAttrs = new HashMap<String, String[]>();
        defineBoolean("AREA", "href");
        defineBoolean("BUTTON", "disabled");
        defineBoolean("DIR", "compact");
        defineBoolean("DL", "compact");
        defineBoolean("FRAME", "noresize");
        defineBoolean("HR", "noshade");
        defineBoolean("IMAGE", "ismap");
        defineBoolean("INPUT", new String[] { "defaultchecked", "checked", "readonly", "disabled" });
        defineBoolean("LINK", "link");
        defineBoolean("MENU", "compact");
        defineBoolean("OBJECT", "declare");
        defineBoolean("OL", "compact");
        defineBoolean("OPTGROUP", "disabled");
        defineBoolean("OPTION", new String[] { "default-selected", "selected", "disabled" });
        defineBoolean("SCRIPT", "defer");
        defineBoolean("SELECT", new String[] { "multiple", "disabled" });
        defineBoolean("STYLE", "disabled");
        defineBoolean("TD", "nowrap");
        defineBoolean("TH", "nowrap");
        defineBoolean("TEXTAREA", new String[] { "disabled", "readonly" });
        defineBoolean("UL", "compact");
        initialize();
    }
}
