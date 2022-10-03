package sun.security.tools;

import java.net.MalformedURLException;
import java.io.IOException;
import java.util.StringTokenizer;
import java.net.URL;
import java.io.File;

public class PathList
{
    public static String appendPath(final String s, final String s2) {
        if (s == null || s.length() == 0) {
            return s2;
        }
        if (s2 == null || s2.length() == 0) {
            return s;
        }
        return s + File.pathSeparator + s2;
    }
    
    public static URL[] pathToURLs(final String s) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s, File.pathSeparator);
        URL[] array = new URL[stringTokenizer.countTokens()];
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            final URL fileToURL = fileToURL(new File(stringTokenizer.nextToken()));
            if (fileToURL != null) {
                array[n++] = fileToURL;
            }
        }
        if (array.length != n) {
            final URL[] array2 = new URL[n];
            System.arraycopy(array, 0, array2, 0, n);
            array = array2;
        }
        return array;
    }
    
    private static URL fileToURL(final File file) {
        String s;
        try {
            s = file.getCanonicalPath();
        }
        catch (final IOException ex) {
            s = file.getAbsolutePath();
        }
        String s2 = s.replace(File.separatorChar, '/');
        if (!s2.startsWith("/")) {
            s2 = "/" + s2;
        }
        if (!file.isFile()) {
            s2 += "/";
        }
        try {
            return new URL("file", "", s2);
        }
        catch (final MalformedURLException ex2) {
            throw new IllegalArgumentException("file");
        }
    }
}
