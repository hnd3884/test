package org.apache.xml.serialize;

import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.xerces.dom.DOMMessageFormatter;
import java.util.Locale;
import java.util.Hashtable;

public final class HTMLdtd
{
    public static final String HTMLPublicId = "-//W3C//DTD HTML 4.01//EN";
    public static final String HTMLSystemId = "http://www.w3.org/TR/html4/strict.dtd";
    public static final String XHTMLPublicId = "-//W3C//DTD XHTML 1.0 Strict//EN";
    public static final String XHTMLSystemId = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
    private static Hashtable _byChar;
    private static Hashtable _byName;
    private static Hashtable _boolAttrs;
    private static Hashtable _elemDefs;
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
    
    public static boolean isEmptyTag(final String s) {
        return isElement(s, 17);
    }
    
    public static boolean isElementContent(final String s) {
        return isElement(s, 2);
    }
    
    public static boolean isPreserveSpace(final String s) {
        return isElement(s, 4);
    }
    
    public static boolean isOptionalClosing(final String s) {
        return isElement(s, 8);
    }
    
    public static boolean isOnlyOpening(final String s) {
        return isElement(s, 1);
    }
    
    public static boolean isClosing(final String s, final String s2) {
        if (s2.equalsIgnoreCase("HEAD")) {
            return !isElement(s, 32);
        }
        if (s2.equalsIgnoreCase("P")) {
            return isElement(s, 64);
        }
        if (s2.equalsIgnoreCase("DT") || s2.equalsIgnoreCase("DD")) {
            return isElement(s, 128);
        }
        if (s2.equalsIgnoreCase("LI") || s2.equalsIgnoreCase("OPTION")) {
            return isElement(s, 256);
        }
        if (s2.equalsIgnoreCase("THEAD") || s2.equalsIgnoreCase("TFOOT") || s2.equalsIgnoreCase("TBODY") || s2.equalsIgnoreCase("TR") || s2.equalsIgnoreCase("COLGROUP")) {
            return isElement(s, 512);
        }
        return (s2.equalsIgnoreCase("TH") || s2.equalsIgnoreCase("TD")) && isElement(s, 16384);
    }
    
    public static boolean isURI(final String s, final String s2) {
        return s2.equalsIgnoreCase("href") || s2.equalsIgnoreCase("src");
    }
    
    public static boolean isBoolean(final String s, final String s2) {
        final String[] array = HTMLdtd._boolAttrs.get(s.toUpperCase(Locale.ENGLISH));
        if (array == null) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equalsIgnoreCase(s2)) {
                return true;
            }
        }
        return false;
    }
    
    public static int charFromName(final String s) {
        initialize();
        final Integer value = HTMLdtd._byName.get(s);
        if (value != null && value instanceof Integer) {
            return value;
        }
        return -1;
    }
    
    public static String fromChar(final int n) {
        if (n > 65535) {
            return null;
        }
        initialize();
        return HTMLdtd._byChar.get(new Integer(n));
    }
    
    private static void initialize() {
        InputStream resourceAsStream = null;
        if (HTMLdtd._byName != null) {
            return;
        }
        try {
            HTMLdtd._byName = new Hashtable();
            HTMLdtd._byChar = new Hashtable();
            resourceAsStream = HTMLdtd.class.getResourceAsStream("HTMLEntities.res");
            if (resourceAsStream == null) {
                throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ResourceNotFound", new Object[] { "HTMLEntities.res" }));
            }
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream, "ASCII"));
            for (String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
                if (s.length() != 0 && s.charAt(0) != '#') {
                    int index = s.indexOf(32);
                    if (index > 1) {
                        final String substring = s.substring(0, index);
                        if (++index < s.length()) {
                            String s2 = s.substring(index);
                            final int index2 = s2.indexOf(32);
                            if (index2 > 0) {
                                s2 = s2.substring(0, index2);
                            }
                            defineEntity(substring, (char)Integer.parseInt(s2));
                        }
                    }
                }
            }
            resourceAsStream.close();
        }
        catch (final Exception ex) {
            throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ResourceNotLoaded", new Object[] { "HTMLEntities.res", ex.toString() }));
        }
        finally {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                }
                catch (final Exception ex2) {}
            }
        }
    }
    
    private static void defineEntity(final String s, final char c) {
        if (HTMLdtd._byName.get(s) == null) {
            HTMLdtd._byName.put(s, new Integer(c));
            HTMLdtd._byChar.put(new Integer(c), s);
        }
    }
    
    private static void defineElement(final String s, final int n) {
        HTMLdtd._elemDefs.put(s, new Integer(n));
    }
    
    private static void defineBoolean(final String s, final String s2) {
        defineBoolean(s, new String[] { s2 });
    }
    
    private static void defineBoolean(final String s, final String[] array) {
        HTMLdtd._boolAttrs.put(s, array);
    }
    
    private static boolean isElement(final String s, final int n) {
        final Integer n2 = HTMLdtd._elemDefs.get(s.toUpperCase(Locale.ENGLISH));
        return n2 != null && (n2 & n) == n;
    }
    
    static {
        HTMLdtd._elemDefs = new Hashtable();
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
        HTMLdtd._boolAttrs = new Hashtable();
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
