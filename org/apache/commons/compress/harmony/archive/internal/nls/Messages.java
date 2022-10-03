package org.apache.commons.compress.harmony.archive.internal.nls;

import java.security.AccessController;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages
{
    private static ResourceBundle bundle;
    
    public static String getString(final String msg) {
        if (Messages.bundle == null) {
            return msg;
        }
        try {
            return Messages.bundle.getString(msg);
        }
        catch (final MissingResourceException e) {
            return "Missing message: " + msg;
        }
    }
    
    public static String getString(final String msg, final Object arg) {
        return getString(msg, new Object[] { arg });
    }
    
    public static String getString(final String msg, final int arg) {
        return getString(msg, new Object[] { Integer.toString(arg) });
    }
    
    public static String getString(final String msg, final char arg) {
        return getString(msg, new Object[] { String.valueOf(arg) });
    }
    
    public static String getString(final String msg, final Object arg1, final Object arg2) {
        return getString(msg, new Object[] { arg1, arg2 });
    }
    
    public static String getString(final String msg, final Object[] args) {
        String format = msg;
        if (Messages.bundle != null) {
            try {
                format = Messages.bundle.getString(msg);
            }
            catch (final MissingResourceException ex) {}
        }
        return format(format, args);
    }
    
    public static String format(final String format, final Object[] args) {
        final StringBuilder answer = new StringBuilder(format.length() + args.length * 20);
        final String[] argStrings = new String[args.length];
        for (int i = 0; i < args.length; ++i) {
            if (args[i] == null) {
                argStrings[i] = "<null>";
            }
            else {
                argStrings[i] = args[i].toString();
            }
        }
        int lastI = 0;
        for (int j = format.indexOf(123, 0); j >= 0; j = format.indexOf(123, lastI)) {
            if (j != 0 && format.charAt(j - 1) == '\\') {
                if (j != 1) {
                    answer.append(format.substring(lastI, j - 1));
                }
                answer.append('{');
                lastI = j + 1;
            }
            else if (j > format.length() - 3) {
                answer.append(format.substring(lastI));
                lastI = format.length();
            }
            else {
                final int argnum = (byte)Character.digit(format.charAt(j + 1), 10);
                if (argnum < 0 || format.charAt(j + 2) != '}') {
                    answer.append(format.substring(lastI, j + 1));
                    lastI = j + 1;
                }
                else {
                    answer.append(format.substring(lastI, j));
                    if (argnum >= argStrings.length) {
                        answer.append("<missing argument>");
                    }
                    else {
                        answer.append(argStrings[argnum]);
                    }
                    lastI = j + 3;
                }
            }
        }
        if (lastI < format.length()) {
            answer.append(format.substring(lastI));
        }
        return answer.toString();
    }
    
    public static ResourceBundle setLocale(final Locale locale, final String resource) {
        try {
            final ClassLoader loader = null;
            return AccessController.doPrivileged(() -> ResourceBundle.getBundle(resource, locale, (loader != null) ? loader : ClassLoader.getSystemClassLoader()));
        }
        catch (final MissingResourceException ex) {
            return null;
        }
    }
    
    static {
        Messages.bundle = null;
        try {
            Messages.bundle = setLocale(Locale.getDefault(), "org.apache.commons.compress.harmony.archive.internal.nls.messages");
        }
        catch (final Throwable e) {
            e.printStackTrace();
        }
    }
}
