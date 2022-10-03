package org.owasp.validator.html.util;

import java.util.StringTokenizer;
import java.util.Stack;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.MalformedURLException;
import java.io.File;
import java.net.URL;

public class URIUtils
{
    private static final String FILE_PROTOCOL_PREFIX = "file:///";
    private static final char HREF_PATH_SEP = '/';
    private static final String URL_PATH_SEP_STR = "/";
    private static final String CURRENT_DIR_OP = ".";
    private static final String PARENT_DIR_OP = "..";
    
    @SuppressFBWarnings(value = { "SECURITY" }, justification = "The 2x Path Traversal warnings related to the use of new File(href) are not vulnerabilities as no data is read or written.")
    public static String resolveAsString(final String href, final String documentBase) {
        try {
            new URL(href);
            return href;
        }
        catch (final MalformedURLException ex) {
            String absolute = null;
            if (documentBase != null && documentBase.length() > 0) {
                final int idx = documentBase.lastIndexOf(47);
                if (idx == documentBase.length() - 1) {
                    absolute = documentBase + href;
                }
                else {
                    absolute = documentBase + '/' + href;
                }
            }
            else {
                absolute = href;
            }
            try {
                if (absolute.indexOf("./") >= 0) {
                    absolute = normalize(absolute);
                }
                new URL(absolute);
                return absolute;
            }
            catch (final MalformedURLException muex) {
                final int idx2 = absolute.indexOf(58);
                if (idx2 >= 0) {
                    final String scheme = absolute.substring(0, idx2);
                    final String error = "unknown protocol: " + scheme;
                    if (error.equals(muex.getMessage())) {
                        return absolute;
                    }
                }
                String fileURL = absolute;
                File iFile = new File(href);
                final boolean exists = iFile.exists();
                fileURL = createFileURL(iFile.getAbsolutePath());
                if (!iFile.isAbsolute()) {
                    iFile = new File(absolute);
                    if (iFile.exists() || !exists) {
                        fileURL = createFileURL(iFile.getAbsolutePath());
                    }
                }
                try {
                    new URL(fileURL);
                    return fileURL;
                }
                catch (final MalformedURLException ex2) {
                    return absolute;
                }
            }
        }
    }
    
    public static String normalize(final String absoluteURL) throws MalformedURLException {
        if (absoluteURL == null) {
            return absoluteURL;
        }
        if (absoluteURL.indexOf(46) < 0) {
            return absoluteURL;
        }
        final Stack<String> tokens = new Stack<String>();
        final StringTokenizer st = new StringTokenizer(absoluteURL, "/", true);
        String last = null;
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            if ("/".equals(token)) {
                if ("/".equals(last)) {
                    tokens.push("");
                }
            }
            else if ("..".equals(token)) {
                if (tokens.empty()) {
                    throw new MalformedURLException("invalid absolute URL: " + absoluteURL);
                }
                tokens.pop();
            }
            else if (!".".equals(token)) {
                tokens.push(token);
            }
            last = token;
        }
        final StringBuffer buffer = new StringBuffer(absoluteURL.length());
        for (int i = 0; i < tokens.size(); ++i) {
            if (i > 0) {
                buffer.append('/');
            }
            buffer.append(tokens.elementAt(i).toString());
        }
        return buffer.toString();
    }
    
    private static String createFileURL(final String filename) {
        if (filename == null) {
            return "file:///";
        }
        final int size = filename.length() + "file:///".length();
        final StringBuffer sb = new StringBuffer(size);
        sb.append("file:///");
        final char[] chars = filename.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            final char ch = chars[i];
            switch (ch) {
                case '\\': {
                    sb.append('/');
                    break;
                }
                default: {
                    sb.append(ch);
                    break;
                }
            }
        }
        return sb.toString();
    }
}
