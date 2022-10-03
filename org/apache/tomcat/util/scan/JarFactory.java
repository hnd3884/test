package org.apache.tomcat.util.scan;

import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.io.IOException;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.Jar;
import java.net.URL;

public class JarFactory
{
    private JarFactory() {
    }
    
    public static Jar newInstance(final URL url) throws IOException {
        final String urlString = url.toString();
        if (urlString.startsWith("jar:file:")) {
            if (urlString.endsWith("!/")) {
                return (Jar)new JarFileUrlJar(url, true);
            }
            return (Jar)new JarFileUrlNestedJar(url);
        }
        else {
            if (urlString.startsWith("war:file:")) {
                final URL jarUrl = UriUtil.warToJar(url);
                return (Jar)new JarFileUrlNestedJar(jarUrl);
            }
            if (urlString.startsWith("file:")) {
                return (Jar)new JarFileUrlJar(url, false);
            }
            return (Jar)new UrlJar(url);
        }
    }
    
    public static URL getJarEntryURL(final URL baseUrl, final String entryName) throws MalformedURLException {
        String baseExternal = baseUrl.toExternalForm();
        if (baseExternal.startsWith("jar")) {
            baseExternal = baseExternal.replaceFirst("^jar:", "war:");
            baseExternal = baseExternal.replaceFirst("!/", Matcher.quoteReplacement(UriUtil.getWarSeparator()));
        }
        return new URL("jar:" + baseExternal + "!/" + entryName);
    }
}
