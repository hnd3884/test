package sun.misc;

public class MessageUtils
{
    public static String subst(final String s, final String s2) {
        return subst(s, new String[] { s2 });
    }
    
    public static String subst(final String s, final String s2, final String s3) {
        return subst(s, new String[] { s2, s3 });
    }
    
    public static String subst(final String s, final String s2, final String s3, final String s4) {
        return subst(s, new String[] { s2, s3, s4 });
    }
    
    public static String subst(final String s, final String[] array) {
        final StringBuffer sb = new StringBuffer();
        for (int length = s.length(), n = 0; n >= 0 && n < length; ++n) {
            final char char1 = s.charAt(n);
            if (char1 == '%') {
                if (n != length) {
                    final int digit = Character.digit(s.charAt(n + 1), 10);
                    if (digit == -1) {
                        sb.append(s.charAt(n + 1));
                        ++n;
                    }
                    else if (digit < array.length) {
                        sb.append(array[digit]);
                        ++n;
                    }
                }
            }
            else {
                sb.append(char1);
            }
        }
        return sb.toString();
    }
    
    public static String substProp(final String s, final String s2) {
        return subst(System.getProperty(s), s2);
    }
    
    public static String substProp(final String s, final String s2, final String s3) {
        return subst(System.getProperty(s), s2, s3);
    }
    
    public static String substProp(final String s, final String s2, final String s3, final String s4) {
        return subst(System.getProperty(s), s2, s3, s4);
    }
    
    public static native void toStderr(final String p0);
    
    public static native void toStdout(final String p0);
    
    public static void err(final String s) {
        toStderr(s + "\n");
    }
    
    public static void out(final String s) {
        toStdout(s + "\n");
    }
    
    public static void where() {
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        for (int i = 1; i < stackTrace.length; ++i) {
            toStderr("\t" + stackTrace[i].toString() + "\n");
        }
    }
}
