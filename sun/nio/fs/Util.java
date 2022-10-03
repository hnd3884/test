package sun.nio.fs;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.nio.file.LinkOption;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.nio.charset.Charset;

class Util
{
    private static final Charset jnuEncoding;
    
    private Util() {
    }
    
    static Charset jnuEncoding() {
        return Util.jnuEncoding;
    }
    
    static byte[] toBytes(final String s) {
        return s.getBytes(Util.jnuEncoding);
    }
    
    static String toString(final byte[] array) {
        return new String(array, Util.jnuEncoding);
    }
    
    static String[] split(final String s, final char c) {
        int n = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == c) {
                ++n;
            }
        }
        final String[] array = new String[n + 1];
        int n2 = 0;
        int n3 = 0;
        for (int j = 0; j < s.length(); ++j) {
            if (s.charAt(j) == c) {
                array[n2++] = s.substring(n3, j);
                n3 = j + 1;
            }
        }
        array[n2] = s.substring(n3, s.length());
        return array;
    }
    
    @SafeVarargs
    static <E> Set<E> newSet(final E... array) {
        final HashSet set = new HashSet();
        for (int length = array.length, i = 0; i < length; ++i) {
            set.add(array[i]);
        }
        return set;
    }
    
    @SafeVarargs
    static <E> Set<E> newSet(final Set<E> set, final E... array) {
        final HashSet set2 = new HashSet((Collection<? extends E>)set);
        for (int length = array.length, i = 0; i < length; ++i) {
            set2.add(array[i]);
        }
        return set2;
    }
    
    static boolean followLinks(final LinkOption... array) {
        boolean b = true;
        final int length = array.length;
        int i = 0;
        while (i < length) {
            final LinkOption linkOption = array[i];
            if (linkOption == LinkOption.NOFOLLOW_LINKS) {
                b = false;
                ++i;
            }
            else {
                if (linkOption == null) {
                    throw new NullPointerException();
                }
                throw new AssertionError((Object)"Should not get here");
            }
        }
        return b;
    }
    
    static {
        jnuEncoding = Charset.forName(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.jnu.encoding")));
    }
}
