package com.adventnet.tools.prevalent;

import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.Font;
import java.util.Locale;

public class ToolsUtils
{
    public static Locale locale;
    public static boolean INTERNATIONALIZE;
    static Font default_font;
    static ToolsResourceBundle lrb;
    
    public static void setBundleName(final String bundleName) {
        final ToolsResourceBundle lrb = ToolsUtils.lrb;
        ToolsResourceBundle.bundleName = bundleName;
    }
    
    public static void setFont(final Font font) {
        ToolsUtils.default_font = font;
    }
    
    public static Font getFont() {
        if (ToolsUtils.default_font == null) {
            return ToolsUtils.default_font = new Font("Helvetica", 0, 12);
        }
        return ToolsUtils.default_font;
    }
    
    public static void setLocale(final Locale loc) {
        ToolsUtils.locale = loc;
    }
    
    public static void setSearchPath(final String path) {
        final ToolsResourceBundle lrb = ToolsUtils.lrb;
        ToolsResourceBundle.searchPath = path;
    }
    
    public static String getString(final String key) {
        if (key == null) {
            return null;
        }
        try {
            final String inter = System.getProperty("tools.i18N");
            if (inter != null) {
                final Boolean b = new Boolean(inter);
                ToolsUtils.INTERNATIONALIZE = b;
            }
            if (!ToolsUtils.INTERNATIONALIZE) {
                return key.trim();
            }
            final String language = System.getProperty("tools.language");
            final String country = System.getProperty("tools.country");
            if (language != null && country != null) {
                setLocale(new Locale(language, country));
            }
            final String fileName = System.getProperty("tools.propname");
            final String path = System.getProperty("tools.proppath");
            if (fileName != null) {
                setBundleName(fileName);
            }
            if (path != null) {
                setSearchPath(path);
            }
            if (ToolsUtils.lrb == null) {
                ToolsUtils.lrb = new ToolsResourceBundle(ToolsUtils.locale);
            }
            final String ret = ToolsUtils.lrb.returnString(key.trim());
            if (ret != null && !ret.equals("")) {
                return ret;
            }
            return key.trim();
        }
        catch (final Throwable th) {
            return key.trim();
        }
    }
    
    public static JMenu createJMenu(final String name, final String mnemonic, final char defaultMnemonicChar) {
        String newname = "";
        newname = getString(name);
        if (newname == null || newname.equals("")) {
            newname = name;
        }
        final String s = getString(mnemonic);
        char c;
        if (s != null && !s.equals("")) {
            c = s.charAt(0);
        }
        else {
            c = defaultMnemonicChar;
        }
        if (newname.indexOf(c) == -1) {
            newname = newname + "(" + c + ")";
        }
        final JMenu m = new JMenu(newname);
        m.setMnemonic(c);
        return m;
    }
    
    public static JMenuItem createJMenuItem(final String name, final String mnemonic, final char defaultMnemonicChar) {
        String newname = "";
        newname = getString(name);
        if (newname == null || newname.equals("")) {
            newname = name;
        }
        final String s = getString(mnemonic);
        char c;
        if (s != null && !s.equals("")) {
            c = s.charAt(0);
        }
        else {
            c = defaultMnemonicChar;
        }
        if (newname.indexOf(c) == -1) {
            newname = newname + "(" + c + ")";
        }
        final JMenuItem m = new JMenuItem(newname);
        m.setMnemonic(c);
        return m;
    }
    
    static {
        ToolsUtils.locale = new Locale("en", "US");
        ToolsUtils.INTERNATIONALIZE = true;
        ToolsUtils.default_font = null;
        ToolsUtils.lrb = null;
    }
}
