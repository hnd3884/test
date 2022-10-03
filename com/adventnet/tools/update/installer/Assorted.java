package com.adventnet.tools.update.installer;

import java.awt.Toolkit;
import java.awt.Dimension;
import javax.swing.JFrame;
import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Rectangle;
import javax.swing.Icon;
import java.awt.Component;
import javax.swing.JOptionPane;
import java.awt.Window;
import java.util.StringTokenizer;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Point;

public final class Assorted
{
    public static Point screenCenter;
    public static final String JRE_MSG = "You would need java compiler for this operation. \nUse JDK and start the builder again. \nPlease ensure that tools.jar is in \nthe classpath of the builder";
    
    public static byte[] getByteArray(final InputStream is) throws IOException {
        int len = 0;
        byte[] buff = new byte[512];
        while (true) {
            if (len == buff.length) {
                final byte[] newBuff = new byte[buff.length * 2];
                System.arraycopy(buff, 0, newBuff, 0, len);
                buff = newBuff;
            }
            final int r = is.read(buff, len, buff.length - len);
            if (r < 0) {
                break;
            }
            len += r;
        }
        final byte[] buffer = new byte[len];
        System.arraycopy(buff, 0, buffer, 0, len);
        return buffer;
    }
    
    public static boolean isDirOkay(final File file, final boolean create) throws Exception {
        if (!file.exists()) {
            return create && file.mkdirs();
        }
        if (!file.isDirectory()) {
            System.err.println(file.getAbsolutePath() + " not  a directory");
            return false;
        }
        if (!file.canWrite() || !file.canRead()) {
            System.err.println(file.getAbsolutePath() + " not readable/writable");
            return false;
        }
        return true;
    }
    
    public static String isFileReadable(final File file) {
        if (!file.exists()) {
            return file.getAbsolutePath() + " does not exist";
        }
        if (!file.isFile()) {
            return file.getAbsolutePath() + " is not a file";
        }
        if (!file.canRead()) {
            return file.getAbsolutePath() + " is not readable";
        }
        return null;
    }
    
    public static String genInstanceName(String clsName, final Map nameMapArg) {
        final int num = 1;
        int i = 0;
        if ((i = clsName.lastIndexOf(".")) != -1) {
            clsName = clsName.substring(i + 1);
        }
        return genUniqueName(clsName, nameMapArg);
    }
    
    public static String genUniqueName(final String in, final Map nameMapArg) {
        int num;
        String name;
        for (num = 1, name = in + num; nameMapArg.get(name) != null; name = in + num) {
            ++num;
        }
        return name;
    }
    
    public static String genClassNameOnType(String clName, final String pkgName) {
        if (pkgName != null && pkgName.trim() != "") {
            clName = pkgName + "." + clName;
        }
        return clName;
    }
    
    public static String getFullyQualifiedName(final String className, final String pkgName) {
        return genClassNameOnType(className, pkgName);
    }
    
    public static String prependBackSlashToDoubleQuote(final String valueStr) {
        final StringTokenizer st = new StringTokenizer(valueStr, "\"", true);
        String finalString = "";
        while (st.hasMoreTokens()) {
            String tmp = st.nextToken();
            if (tmp.indexOf("\"") != -1) {
                tmp = "\\" + tmp;
            }
            finalString += tmp;
        }
        return finalString;
    }
    
    public static String removeBackSlashBeforeDoubleQuote(final String valueStr) {
        final StringTokenizer st = new StringTokenizer(valueStr, "\\\"", true);
        String finalString = "";
        while (st.hasMoreTokens()) {
            String tmp = st.nextToken();
            if (tmp.indexOf("\\") != -1 && st.hasMoreTokens()) {
                tmp = st.nextToken();
                if (tmp.indexOf("\"") != -1) {
                    tmp = "\"";
                }
                else {
                    tmp = "\\" + tmp;
                }
            }
            finalString += tmp;
        }
        return finalString;
    }
    
    public static void placeTheWindow(final Window win, final String place) {
        positionTheWindow(win, place);
        win.setVisible(true);
    }
    
    public static void positionTheWindow(final Window win, final String place) {
        if (Assorted.screenCenter == null) {
            return;
        }
        if (place.equals("BottomRight")) {
            win.setLocation(2 * Assorted.screenCenter.x - win.getSize().width, 2 * Assorted.screenCenter.y - win.getSize().height - 15);
        }
        else if (place.equals("TopRight")) {
            win.setLocation(2 * Assorted.screenCenter.x - win.getSize().width, 15);
        }
        else if (place.equals("Center")) {
            win.setLocation(Assorted.screenCenter.x - win.getSize().width / 2, Assorted.screenCenter.y - win.getSize().height / 2);
        }
        else if (place.equals("TopCenter")) {
            win.setLocation(Assorted.screenCenter.x - win.getSize().width / 2, 15);
        }
        else {
            win.setLocation(Assorted.screenCenter.x - win.getSize().width, 2 * Assorted.screenCenter.y - win.getSize().height - 15);
        }
    }
    
    public static void showJREMessage() {
        final Object[] options = { "OK", "CANCEL" };
        JOptionPane.showOptionDialog(null, "You would need java compiler for this operation. \nUse JDK and start the builder again. \nPlease ensure that tools.jar is in \nthe classpath of the builder", "Warning", -1, 2, null, options, options[0]);
    }
    
    public static String rectToString(final Rectangle lrect) {
        final String sx = new Integer(lrect.x).toString();
        final String sy = new Integer(lrect.y).toString();
        final String sw = new Integer(lrect.width).toString();
        final String sh = new Integer(lrect.height).toString();
        final String laystr = sx + "," + sy + "," + sw + "," + sh;
        return laystr;
    }
    
    public static String stringLayoutConstraints(final String[] arr) {
        if (arr != null) {
            String tmp = "";
            for (int j = 0; j < arr.length; ++j) {
                tmp = tmp + arr[j] + " ";
            }
            return tmp;
        }
        return null;
    }
    
    public static DefaultMutableTreeNode getNodeTillString(final DefaultMutableTreeNode node, final String str) {
        DefaultMutableTreeNode gotNode = null;
        if (node.toString().equals(str)) {
            return node;
        }
        if (node.isLeaf()) {
            return null;
        }
        final Enumeration enum1 = node.children();
        int i = 0;
        while (enum1.hasMoreElements()) {
            final DefaultMutableTreeNode newNode = enum1.nextElement();
            gotNode = getNodeTillString(newNode, str);
            if (gotNode != null) {
                return gotNode;
            }
            ++i;
        }
        return null;
    }
    
    public static String arrToSingleStr(final String[] arr) {
        String s = "";
        for (int i = 0; i < arr.length; ++i) {
            if (i > 0) {
                s += ',';
            }
            s += arr[i];
        }
        return s;
    }
    
    public static String[] singleStrToArr(final String ss) {
        final StringTokenizer st = new StringTokenizer(ss, ",");
        final int count = st.countTokens();
        if (count <= 0) {
            return null;
        }
        final String[] arr = new String[count];
        int i = 0;
        while (st.hasMoreTokens()) {
            final String str = st.nextToken();
            arr[i] = str;
            ++i;
        }
        return arr;
    }
    
    public static String[] stringToArray(final String concatenatedString, final String delimitor) {
        final StringTokenizer strTok = new StringTokenizer(concatenatedString, delimitor);
        final String[] value = new String[strTok.countTokens()];
        for (int i = 0; i < value.length; ++i) {
            value[i] = strTok.nextToken();
        }
        return value;
    }
    
    public static boolean isValidIdentifier(final String s) {
        return isValidIdentifier(s, true);
    }
    
    public static boolean isValidIdentifier(final String s, final boolean showError) {
        String str = s;
        str = s.trim();
        final char[] c = s.toCharArray();
        if (c.length == 0) {
            if (showError) {
                JOptionPane.showMessageDialog(new JFrame(), "    Invalid Field Name", "Rename error", 0);
            }
            return false;
        }
        if (!Character.isJavaIdentifierStart(c[0])) {
            if (showError) {
                JOptionPane.showMessageDialog(new JFrame(), "    Invalid Field Name", "Rename error", 0);
            }
            return false;
        }
        for (int i = 1; i < c.length; ++i) {
            if (!Character.isJavaIdentifierPart(c[i])) {
                if (showError) {
                    JOptionPane.showMessageDialog(new JFrame(), "    Invalid Field Name", "Rename error", 0);
                }
                return false;
            }
        }
        return true;
    }
    
    public static boolean isValidPackageName(final String s) {
        String str = s;
        str = s.trim();
        final char[] c = s.toCharArray();
        final int j = c.length;
        if (j == 0 || c[j - 1] == '.' || c[0] == '.') {
            return false;
        }
        for (int i = 0; i < c.length; ++i) {
            if (c[i] == '.') {
                if (i + 1 < c.length && c[i + 1] == '.') {
                    return false;
                }
                if (!Character.isJavaIdentifierStart(c[i + 1])) {
                    return false;
                }
                if (!Character.isJavaIdentifierPart(c[i + 1])) {
                    return false;
                }
            }
            else if (c[i] == ' ') {
                return false;
            }
        }
        return true;
    }
    
    public static Rectangle stringToRect(final String name) {
        final StringTokenizer tokens = new StringTokenizer(name, ",");
        int x = 0;
        int y = 0;
        int w = 1;
        int h = 1;
        final int num = tokens.countTokens();
        Rectangle r = null;
        if (num < 4 || num > 4) {
            return null;
        }
        try {
            for (int i = 0; i < num; ++i) {
                switch (i) {
                    case 0: {
                        x = PureUtils.atoi(tokens.nextToken());
                        break;
                    }
                    case 1: {
                        y = PureUtils.atoi(tokens.nextToken());
                        break;
                    }
                    case 2: {
                        w = PureUtils.atoi(tokens.nextToken());
                        break;
                    }
                    case 3: {
                        h = PureUtils.atoi(tokens.nextToken());
                        break;
                    }
                    default: {
                        return null;
                    }
                }
            }
            r = new Rectangle(x, y, w, h);
        }
        catch (final NumberFormatException ex) {}
        return r;
    }
    
    public static Dimension stringToDim(final String name) {
        final StringTokenizer tokens = new StringTokenizer(name, ",");
        int w = 1;
        int h = 1;
        final int num = tokens.countTokens();
        Dimension r = null;
        if (num < 2 || num > 2) {
            return null;
        }
        try {
            for (int i = 0; i < num; ++i) {
                switch (i) {
                    case 0: {
                        w = PureUtils.atoi(tokens.nextToken());
                        break;
                    }
                    case 1: {
                        h = PureUtils.atoi(tokens.nextToken());
                        break;
                    }
                    default: {
                        return null;
                    }
                }
            }
            r = new Dimension(w, h);
        }
        catch (final NumberFormatException ex) {}
        return r;
    }
    
    public static boolean isValidFileName(final String fileName) {
        return isValidFileName(fileName, true);
    }
    
    public static boolean isValidFileName(final String fileName, final boolean showError) {
        final File f = new File(fileName);
        if (f == null) {
            if (showError) {
                JOptionPane.showMessageDialog(new JFrame(), "Invalid File Name", "Filename error", 0);
            }
            return false;
        }
        if (f.isDirectory()) {
            if (showError) {
                JOptionPane.showMessageDialog(new JFrame(), "Invalid File Name", "Filename error", 0);
            }
            return false;
        }
        final String baseName = GeneralUtility.getBaseName(f.getAbsolutePath(), ".");
        if (!isValidIdentifier(baseName, false)) {
            if (showError) {
                JOptionPane.showMessageDialog(new JFrame(), "Invalid File Name", "Filename error", 0);
            }
            return false;
        }
        return true;
    }
    
    static {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Assorted.screenCenter = new Point(screenSize.width / 2, screenSize.height / 2);
    }
}
