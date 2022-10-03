package com.adventnet.utils;

import javax.swing.JButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.util.Locale;

public class CLIUtils
{
    public static Locale locale;
    public static boolean INTERNATIONALIZE;
    static CLIResourceBundle clirb;
    
    CLIUtils() {
    }
    
    public static void setBundleName(final String bundleName) {
        CLIResourceBundle.bundleName = bundleName;
    }
    
    public static String getBundleName() {
        return CLIResourceBundle.bundleName;
    }
    
    public static void setLocale(final Locale locale) {
        CLIUtils.locale = locale;
    }
    
    public static void setSearchPath(final String searchPath) {
        CLIResourceBundle.searchPath = searchPath;
    }
    
    public static String getString(final String s) {
        if (s == null) {
            return null;
        }
        try {
            if (!CLIUtils.INTERNATIONALIZE) {
                return s.trim();
            }
            if (CLIUtils.clirb == null) {
                CLIUtils.clirb = new CLIResourceBundle(CLIUtils.locale);
            }
            final String returnString = CLIUtils.clirb.returnString(s.trim());
            if (returnString != null && !returnString.equals("")) {
                return returnString;
            }
            return s.trim();
        }
        catch (final Throwable t) {
            return s.trim();
        }
    }
    
    public static JMenu createJMenu(final String s, final char c, final char c2) {
        String s2 = s;
        char char1 = c;
        if (CLIUtils.INTERNATIONALIZE) {
            s2 = getString(s);
            if (s2 == null || s2.equals("")) {
                s2 = s;
            }
            final String string = getString(String.valueOf(char1));
            if (string != null && !string.equals("")) {
                char1 = string.charAt(0);
            }
            else {
                char1 = c2;
            }
            if (s2.toUpperCase().indexOf(String.valueOf(char1).toUpperCase()) == -1) {
                s2 = s2 + "(" + char1 + ")";
            }
        }
        final JMenu menu = new JMenu(s2);
        menu.setMnemonic(char1);
        return menu;
    }
    
    public static JMenuItem createJMenuItem(final String s, final char c, final char c2) {
        String s2 = s;
        char char1 = c;
        if (CLIUtils.INTERNATIONALIZE) {
            s2 = getString(s);
            if (s2 == null || s2.equals("")) {
                s2 = s;
            }
            final String string = getString(String.valueOf(char1));
            if (string != null && !string.equals("")) {
                char1 = string.charAt(0);
            }
            else {
                char1 = c2;
            }
            if (s2.toUpperCase().indexOf(String.valueOf(char1).toUpperCase()) == -1) {
                s2 = s2 + "(" + char1 + ")";
            }
        }
        final JMenuItem menuItem = new JMenuItem(s2);
        menuItem.setMnemonic(char1);
        return menuItem;
    }
    
    public static JCheckBoxMenuItem createJCheckBoxMenuItem(final String s, final char c, final char c2) {
        String s2 = s;
        char char1 = c;
        if (CLIUtils.INTERNATIONALIZE) {
            s2 = getString(s);
            if (s2 == null || s2.equals("")) {
                s2 = s;
            }
            final String string = getString(String.valueOf(char1));
            if (string != null && !string.equals("")) {
                char1 = string.charAt(0);
            }
            else {
                char1 = c2;
            }
            if (s2.toUpperCase().indexOf(String.valueOf(char1).toUpperCase()) == -1) {
                s2 = s2 + "(" + char1 + ")";
            }
        }
        final JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem(s2);
        checkBoxMenuItem.setMnemonic(char1);
        return checkBoxMenuItem;
    }
    
    public static JCheckBox createJCheckBox(final String s, final char c, final char c2) {
        String s2 = s;
        char char1 = c;
        if (CLIUtils.INTERNATIONALIZE) {
            s2 = getString(s);
            if (s2 == null || s2.equals("")) {
                s2 = s;
            }
            final String string = getString(String.valueOf(char1));
            if (string != null && !string.equals("")) {
                char1 = string.charAt(0);
            }
            else {
                char1 = c2;
            }
            if (s2.toUpperCase().indexOf(String.valueOf(char1).toUpperCase()) == -1) {
                s2 = s2 + "(" + char1 + ")";
            }
        }
        final JCheckBox checkBox = new JCheckBox(s2);
        checkBox.setMnemonic(char1);
        return checkBox;
    }
    
    public static JRadioButtonMenuItem createJRadioButtonMenuItem(final String s, final char c, final char c2) {
        String s2 = s;
        char char1 = c;
        if (CLIUtils.INTERNATIONALIZE) {
            s2 = getString(s);
            if (s2 == null || s2.equals("")) {
                s2 = s;
            }
            final String string = getString(String.valueOf(char1));
            if (string != null && !string.equals("")) {
                char1 = string.charAt(0);
            }
            else {
                char1 = c2;
            }
            if (s2.toUpperCase().indexOf(String.valueOf(char1).toUpperCase()) == -1) {
                s2 = s2 + "(" + char1 + ")";
            }
        }
        final JRadioButtonMenuItem radioButtonMenuItem = new JRadioButtonMenuItem(s2);
        radioButtonMenuItem.setMnemonic(char1);
        return radioButtonMenuItem;
    }
    
    public static JButton createJButton(final String s, final char c, final char c2) {
        String s2 = s;
        char char1 = c;
        if (CLIUtils.INTERNATIONALIZE) {
            s2 = getString(s);
            if (s2 == null || s2.equals("")) {
                s2 = s;
            }
            final String string = getString(String.valueOf(char1));
            if (string != null && !string.equals("")) {
                char1 = string.charAt(0);
            }
            else {
                char1 = c2;
            }
            if (s2.toUpperCase().indexOf(String.valueOf(char1).toUpperCase()) == -1) {
                s2 = s2 + "(" + char1 + ")";
            }
        }
        final JButton button = new JButton(s2);
        button.setMnemonic(char1);
        return button;
    }
    
    static {
        CLIUtils.locale = new Locale("", "", "");
        CLIUtils.INTERNATIONALIZE = false;
        CLIUtils.clirb = null;
    }
}
