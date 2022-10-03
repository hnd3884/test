package org.apache.tomcat.dbcp.dbcp2;

import java.util.HashSet;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.Properties;
import java.sql.ResultSet;
import java.util.Set;
import java.util.ResourceBundle;

public final class Utils
{
    private static final ResourceBundle messages;
    public static final boolean IS_SECURITY_ENABLED;
    public static final String DISCONNECTION_SQL_CODE_PREFIX = "08";
    public static final Set<String> DISCONNECTION_SQL_CODES;
    static final ResultSet[] EMPTY_RESULT_SET_ARRAY;
    static final String[] EMPTY_STRING_ARRAY;
    
    public static char[] clone(final char[] value) {
        return (char[])((value == null) ? null : ((char[])value.clone()));
    }
    
    public static Properties cloneWithoutCredentials(final Properties properties) {
        if (properties != null) {
            final Properties temp = (Properties)properties.clone();
            temp.remove("user");
            temp.remove("password");
            return temp;
        }
        return properties;
    }
    
    public static void closeQuietly(final AutoCloseable autoCloseable) {
        if (autoCloseable != null) {
            try {
                autoCloseable.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    public static String getMessage(final String key) {
        return getMessage(key, (Object[])null);
    }
    
    public static String getMessage(final String key, final Object... args) {
        final String msg = Utils.messages.getString(key);
        if (args == null || args.length == 0) {
            return msg;
        }
        final MessageFormat mf = new MessageFormat(msg);
        return mf.format(args, new StringBuffer(), null).toString();
    }
    
    public static char[] toCharArray(final String value) {
        return (char[])((value != null) ? value.toCharArray() : null);
    }
    
    public static String toString(final char[] value) {
        return (value == null) ? null : String.valueOf(value);
    }
    
    private Utils() {
    }
    
    static {
        messages = ResourceBundle.getBundle(Utils.class.getPackage().getName() + ".LocalStrings");
        IS_SECURITY_ENABLED = (System.getSecurityManager() != null);
        EMPTY_RESULT_SET_ARRAY = new ResultSet[0];
        EMPTY_STRING_ARRAY = new String[0];
        (DISCONNECTION_SQL_CODES = new HashSet<String>()).add("57P01");
        Utils.DISCONNECTION_SQL_CODES.add("57P02");
        Utils.DISCONNECTION_SQL_CODES.add("57P03");
        Utils.DISCONNECTION_SQL_CODES.add("01002");
        Utils.DISCONNECTION_SQL_CODES.add("JZ0C0");
        Utils.DISCONNECTION_SQL_CODES.add("JZ0C1");
    }
}
