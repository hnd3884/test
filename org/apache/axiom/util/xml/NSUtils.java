package org.apache.axiom.util.xml;

public final class NSUtils
{
    private static final char[] prefixChars;
    
    private NSUtils() {
    }
    
    public static String generatePrefix(final String namespaceURI) {
        final char[] prefix = new char[7];
        prefix[0] = 'n';
        prefix[1] = 's';
        int hash = namespaceURI.hashCode() & Integer.MAX_VALUE;
        for (int i = prefix.length - 1; i >= 2; --i) {
            prefix[i] = NSUtils.prefixChars[hash % 62];
            hash /= 62;
        }
        return new String(prefix);
    }
    
    static {
        prefixChars = new char[62];
        for (int i = 0; i < 10; ++i) {
            NSUtils.prefixChars[i] = (char)(48 + i);
        }
        for (int i = 0; i < 26; ++i) {
            NSUtils.prefixChars[i + 10] = (char)(97 + i);
        }
        for (int i = 0; i < 26; ++i) {
            NSUtils.prefixChars[i + 36] = (char)(65 + i);
        }
    }
}
