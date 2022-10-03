package org.apache.xerces.util;

import java.io.IOException;
import java.util.Locale;
import java.io.Serializable;

public class URI implements Serializable
{
    static final long serialVersionUID = 1601921774685357214L;
    private static final byte[] fgLookupTable;
    private static final int RESERVED_CHARACTERS = 1;
    private static final int MARK_CHARACTERS = 2;
    private static final int SCHEME_CHARACTERS = 4;
    private static final int USERINFO_CHARACTERS = 8;
    private static final int ASCII_ALPHA_CHARACTERS = 16;
    private static final int ASCII_DIGIT_CHARACTERS = 32;
    private static final int ASCII_HEX_CHARACTERS = 64;
    private static final int PATH_CHARACTERS = 128;
    private static final int MASK_ALPHA_NUMERIC = 48;
    private static final int MASK_UNRESERVED_MASK = 50;
    private static final int MASK_URI_CHARACTER = 51;
    private static final int MASK_SCHEME_CHARACTER = 52;
    private static final int MASK_USERINFO_CHARACTER = 58;
    private static final int MASK_PATH_CHARACTER = 178;
    private String m_scheme;
    private String m_userinfo;
    private String m_host;
    private int m_port;
    private String m_regAuthority;
    private String m_path;
    private String m_queryString;
    private String m_fragment;
    
    public URI() {
        this.m_scheme = null;
        this.m_userinfo = null;
        this.m_host = null;
        this.m_port = -1;
        this.m_regAuthority = null;
        this.m_path = null;
        this.m_queryString = null;
        this.m_fragment = null;
    }
    
    public URI(final URI uri) {
        this.m_scheme = null;
        this.m_userinfo = null;
        this.m_host = null;
        this.m_port = -1;
        this.m_regAuthority = null;
        this.m_path = null;
        this.m_queryString = null;
        this.m_fragment = null;
        this.initialize(uri);
    }
    
    public URI(final String s) throws MalformedURIException {
        this((URI)null, s);
    }
    
    public URI(final String s, final boolean b) throws MalformedURIException {
        this(null, s, b);
    }
    
    public URI(final URI uri, final String s) throws MalformedURIException {
        this.m_scheme = null;
        this.m_userinfo = null;
        this.m_host = null;
        this.m_port = -1;
        this.m_regAuthority = null;
        this.m_path = null;
        this.m_queryString = null;
        this.m_fragment = null;
        this.initialize(uri, s);
    }
    
    public URI(final URI uri, final String s, final boolean b) throws MalformedURIException {
        this.m_scheme = null;
        this.m_userinfo = null;
        this.m_host = null;
        this.m_port = -1;
        this.m_regAuthority = null;
        this.m_path = null;
        this.m_queryString = null;
        this.m_fragment = null;
        this.initialize(uri, s, b);
    }
    
    public URI(final String scheme, final String path) throws MalformedURIException {
        this.m_scheme = null;
        this.m_userinfo = null;
        this.m_host = null;
        this.m_port = -1;
        this.m_regAuthority = null;
        this.m_path = null;
        this.m_queryString = null;
        this.m_fragment = null;
        if (scheme == null || scheme.trim().length() == 0) {
            throw new MalformedURIException("Cannot construct URI with null/empty scheme!");
        }
        if (path == null || path.trim().length() == 0) {
            throw new MalformedURIException("Cannot construct URI with null/empty scheme-specific part!");
        }
        this.setScheme(scheme);
        this.setPath(path);
    }
    
    public URI(final String s, final String s2, final String s3, final String s4, final String s5) throws MalformedURIException {
        this(s, null, s2, -1, s3, s4, s5);
    }
    
    public URI(final String scheme, final String userinfo, final String host, final int port, final String path, final String queryString, final String fragment) throws MalformedURIException {
        this.m_scheme = null;
        this.m_userinfo = null;
        this.m_host = null;
        this.m_port = -1;
        this.m_regAuthority = null;
        this.m_path = null;
        this.m_queryString = null;
        this.m_fragment = null;
        if (scheme == null || scheme.trim().length() == 0) {
            throw new MalformedURIException("Scheme is required!");
        }
        if (host == null) {
            if (userinfo != null) {
                throw new MalformedURIException("Userinfo may not be specified if host is not specified!");
            }
            if (port != -1) {
                throw new MalformedURIException("Port may not be specified if host is not specified!");
            }
        }
        if (path != null) {
            if (path.indexOf(63) != -1 && queryString != null) {
                throw new MalformedURIException("Query string cannot be specified in path and query string!");
            }
            if (path.indexOf(35) != -1 && fragment != null) {
                throw new MalformedURIException("Fragment cannot be specified in both the path and fragment!");
            }
        }
        this.setScheme(scheme);
        this.setHost(host);
        this.setPort(port);
        this.setUserinfo(userinfo);
        this.setPath(path);
        this.setQueryString(queryString);
        this.setFragment(fragment);
    }
    
    private void initialize(final URI uri) {
        this.m_scheme = uri.getScheme();
        this.m_userinfo = uri.getUserinfo();
        this.m_host = uri.getHost();
        this.m_port = uri.getPort();
        this.m_regAuthority = uri.getRegBasedAuthority();
        this.m_path = uri.getPath();
        this.m_queryString = uri.getQueryString();
        this.m_fragment = uri.getFragment();
    }
    
    private void initialize(final URI uri, final String s, final boolean b) throws MalformedURIException {
        final int n = (s != null) ? s.length() : 0;
        if (uri == null && n == 0) {
            if (b) {
                this.m_path = "";
                return;
            }
            throw new MalformedURIException("Cannot initialize URI with empty parameters.");
        }
        else {
            if (n == 0) {
                this.initialize(uri);
                return;
            }
            int i = 0;
            final int index = s.indexOf(58);
            if (index != -1) {
                final int n2 = index - 1;
                final int lastIndex = s.lastIndexOf(47, n2);
                final int lastIndex2 = s.lastIndexOf(63, n2);
                final int lastIndex3 = s.lastIndexOf(35, n2);
                if (index == 0 || lastIndex != -1 || lastIndex2 != -1 || lastIndex3 != -1) {
                    if (index == 0 || (uri == null && lastIndex3 != 0 && !b)) {
                        throw new MalformedURIException("No scheme found in URI.");
                    }
                }
                else {
                    this.initializeScheme(s);
                    i = this.m_scheme.length() + 1;
                    if (index == n - 1 || s.charAt(index + 1) == '#') {
                        throw new MalformedURIException("Scheme specific part cannot be empty.");
                    }
                }
            }
            else if (uri == null && s.indexOf(35) != 0 && !b) {
                throw new MalformedURIException("No scheme found in URI.");
            }
            if (i + 1 < n && s.charAt(i) == '/' && s.charAt(i + 1) == '/') {
                i += 2;
                final int n3 = i;
                while (i < n) {
                    final char char1 = s.charAt(i);
                    if (char1 == '/' || char1 == '?') {
                        break;
                    }
                    if (char1 == '#') {
                        break;
                    }
                    ++i;
                }
                if (i > n3) {
                    if (!this.initializeAuthority(s.substring(n3, i))) {
                        i = n3 - 2;
                    }
                }
                else {
                    this.m_host = "";
                }
            }
            this.initializePath(s, i);
            if (uri != null) {
                this.absolutize(uri);
            }
        }
    }
    
    private void initialize(final URI uri, final String s) throws MalformedURIException {
        final int n = (s != null) ? s.length() : 0;
        if (uri == null && n == 0) {
            throw new MalformedURIException("Cannot initialize URI with empty parameters.");
        }
        if (n == 0) {
            this.initialize(uri);
            return;
        }
        int i = 0;
        final int index = s.indexOf(58);
        if (index != -1) {
            final int n2 = index - 1;
            final int lastIndex = s.lastIndexOf(47, n2);
            final int lastIndex2 = s.lastIndexOf(63, n2);
            final int lastIndex3 = s.lastIndexOf(35, n2);
            if (index == 0 || lastIndex != -1 || lastIndex2 != -1 || lastIndex3 != -1) {
                if (index == 0 || (uri == null && lastIndex3 != 0)) {
                    throw new MalformedURIException("No scheme found in URI.");
                }
            }
            else {
                this.initializeScheme(s);
                i = this.m_scheme.length() + 1;
                if (index == n - 1 || s.charAt(index + 1) == '#') {
                    throw new MalformedURIException("Scheme specific part cannot be empty.");
                }
            }
        }
        else if (uri == null && s.indexOf(35) != 0) {
            throw new MalformedURIException("No scheme found in URI.");
        }
        if (i + 1 < n && s.charAt(i) == '/' && s.charAt(i + 1) == '/') {
            i += 2;
            final int n3 = i;
            while (i < n) {
                final char char1 = s.charAt(i);
                if (char1 == '/' || char1 == '?') {
                    break;
                }
                if (char1 == '#') {
                    break;
                }
                ++i;
            }
            if (i > n3) {
                if (!this.initializeAuthority(s.substring(n3, i))) {
                    i = n3 - 2;
                }
            }
            else {
                this.m_host = "";
            }
        }
        this.initializePath(s, i);
        if (uri != null) {
            this.absolutize(uri);
        }
    }
    
    public void absolutize(final URI uri) {
        if (this.m_path.length() == 0 && this.m_scheme == null && this.m_host == null && this.m_regAuthority == null) {
            this.m_scheme = uri.getScheme();
            this.m_userinfo = uri.getUserinfo();
            this.m_host = uri.getHost();
            this.m_port = uri.getPort();
            this.m_regAuthority = uri.getRegBasedAuthority();
            this.m_path = uri.getPath();
            if (this.m_queryString == null) {
                this.m_queryString = uri.getQueryString();
                if (this.m_fragment == null) {
                    this.m_fragment = uri.getFragment();
                }
            }
            return;
        }
        if (this.m_scheme != null) {
            return;
        }
        this.m_scheme = uri.getScheme();
        if (this.m_host != null || this.m_regAuthority != null) {
            return;
        }
        this.m_userinfo = uri.getUserinfo();
        this.m_host = uri.getHost();
        this.m_port = uri.getPort();
        this.m_regAuthority = uri.getRegBasedAuthority();
        if (this.m_path.length() > 0 && this.m_path.startsWith("/")) {
            return;
        }
        String substring = "";
        final String path = uri.getPath();
        if (path != null && path.length() > 0) {
            final int lastIndex = path.lastIndexOf(47);
            if (lastIndex != -1) {
                substring = path.substring(0, lastIndex + 1);
            }
        }
        else if (this.m_path.length() > 0) {
            substring = "/";
        }
        String path2;
        int index;
        for (path2 = substring.concat(this.m_path); (index = path2.indexOf("/./")) != -1; path2 = path2.substring(0, index + 1).concat(path2.substring(index + 3))) {}
        if (path2.endsWith("/.")) {
            path2 = path2.substring(0, path2.length() - 1);
        }
        int index2 = 1;
        while ((index2 = path2.indexOf("/../", index2)) > 0) {
            final String substring2 = path2.substring(0, path2.indexOf("/../"));
            final int lastIndex2 = substring2.lastIndexOf(47);
            if (lastIndex2 != -1) {
                if (!substring2.substring(lastIndex2).equals("..")) {
                    path2 = path2.substring(0, lastIndex2 + 1).concat(path2.substring(index2 + 4));
                    index2 = lastIndex2;
                }
                else {
                    index2 += 4;
                }
            }
            else {
                index2 += 4;
            }
        }
        if (path2.endsWith("/..")) {
            final int lastIndex3 = path2.substring(0, path2.length() - 3).lastIndexOf(47);
            if (lastIndex3 != -1) {
                path2 = path2.substring(0, lastIndex3 + 1);
            }
        }
        this.m_path = path2;
    }
    
    private void initializeScheme(final String s) throws MalformedURIException {
        int length;
        int i;
        for (length = s.length(), i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == ':' || char1 == '/' || char1 == '?') {
                break;
            }
            if (char1 == '#') {
                break;
            }
        }
        final String substring = s.substring(0, i);
        if (substring.length() == 0) {
            throw new MalformedURIException("No scheme found in URI.");
        }
        this.setScheme(substring);
    }
    
    private boolean initializeAuthority(final String regAuthority) {
        int i = 0;
        final int n = 0;
        final int length = regAuthority.length();
        String substring = null;
        if (regAuthority.indexOf(64, n) != -1) {
            while (i < length && regAuthority.charAt(i) != '@') {
                ++i;
            }
            substring = regAuthority.substring(n, i);
            ++i;
        }
        final int n2 = i;
        boolean b = false;
        if (i < length) {
            if (regAuthority.charAt(n2) == '[') {
                final int index = regAuthority.indexOf(93, n2);
                i = ((index != -1) ? index : length);
                if (i + 1 < length && regAuthority.charAt(i + 1) == ':') {
                    ++i;
                    b = true;
                }
                else {
                    i = length;
                }
            }
            else {
                final int lastIndex = regAuthority.lastIndexOf(58, length);
                i = ((lastIndex > n2) ? lastIndex : length);
                b = (i != length);
            }
        }
        final String substring2 = regAuthority.substring(n2, i);
        int int1 = -1;
        if (substring2.length() > 0 && b) {
            int n3;
            for (n3 = ++i; i < length; ++i) {}
            final String substring3 = regAuthority.substring(n3, i);
            if (substring3.length() > 0) {
                try {
                    int1 = Integer.parseInt(substring3);
                    if (int1 == -1) {
                        --int1;
                    }
                }
                catch (final NumberFormatException ex) {
                    int1 = -2;
                }
            }
        }
        if (this.isValidServerBasedAuthority(substring2, int1, substring)) {
            this.m_host = substring2;
            this.m_port = int1;
            this.m_userinfo = substring;
            return true;
        }
        if (this.isValidRegistryBasedAuthority(regAuthority)) {
            this.m_regAuthority = regAuthority;
            return true;
        }
        return false;
    }
    
    private boolean isValidServerBasedAuthority(final String s, final int n, final String s2) {
        if (!isWellFormedAddress(s)) {
            return false;
        }
        if (n < -1 || n > 65535) {
            return false;
        }
        if (s2 != null) {
            for (int i = 0, length = s2.length(); i < length; ++i) {
                final char char1 = s2.charAt(i);
                if (char1 == '%') {
                    if (i + 2 >= length || !isHex(s2.charAt(i + 1)) || !isHex(s2.charAt(i + 2))) {
                        return false;
                    }
                    i += 2;
                }
                else if (!isUserinfoCharacter(char1)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean isValidRegistryBasedAuthority(final String s) {
        for (int i = 0, length = s.length(); i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '%') {
                if (i + 2 >= length || !isHex(s.charAt(i + 1)) || !isHex(s.charAt(i + 2))) {
                    return false;
                }
                i += 2;
            }
            else if (!isPathCharacter(char1)) {
                return false;
            }
        }
        return true;
    }
    
    private void initializePath(final String s, final int n) throws MalformedURIException {
        if (s == null) {
            throw new MalformedURIException("Cannot initialize path from null string!");
        }
        int i = n;
        final int length = s.length();
        char c = '\0';
        if (n < length) {
            if (this.getScheme() == null || s.charAt(n) == '/') {
                while (i < length) {
                    c = s.charAt(i);
                    if (c == '%') {
                        if (i + 2 >= length || !isHex(s.charAt(i + 1)) || !isHex(s.charAt(i + 2))) {
                            throw new MalformedURIException("Path contains invalid escape sequence!");
                        }
                        i += 2;
                    }
                    else if (!isPathCharacter(c)) {
                        if (c == '?') {
                            break;
                        }
                        if (c == '#') {
                            break;
                        }
                        throw new MalformedURIException("Path contains invalid character: " + c);
                    }
                    ++i;
                }
            }
            else {
                while (i < length) {
                    c = s.charAt(i);
                    if (c == '?') {
                        break;
                    }
                    if (c == '#') {
                        break;
                    }
                    if (c == '%') {
                        if (i + 2 >= length || !isHex(s.charAt(i + 1)) || !isHex(s.charAt(i + 2))) {
                            throw new MalformedURIException("Opaque part contains invalid escape sequence!");
                        }
                        i += 2;
                    }
                    else if (!isURICharacter(c)) {
                        throw new MalformedURIException("Opaque part contains invalid character: " + c);
                    }
                    ++i;
                }
            }
        }
        this.m_path = s.substring(n, i);
        if (c == '?') {
            int n2;
            for (n2 = ++i; i < length; ++i) {
                c = s.charAt(i);
                if (c == '#') {
                    break;
                }
                if (c == '%') {
                    if (i + 2 >= length || !isHex(s.charAt(i + 1)) || !isHex(s.charAt(i + 2))) {
                        throw new MalformedURIException("Query string contains invalid escape sequence!");
                    }
                    i += 2;
                }
                else if (!isURICharacter(c)) {
                    throw new MalformedURIException("Query string contains invalid character: " + c);
                }
            }
            this.m_queryString = s.substring(n2, i);
        }
        if (c == '#') {
            int n3;
            for (n3 = ++i; i < length; ++i) {
                final char char1 = s.charAt(i);
                if (char1 == '%') {
                    if (i + 2 >= length || !isHex(s.charAt(i + 1)) || !isHex(s.charAt(i + 2))) {
                        throw new MalformedURIException("Fragment contains invalid escape sequence!");
                    }
                    i += 2;
                }
                else if (!isURICharacter(char1)) {
                    throw new MalformedURIException("Fragment contains invalid character: " + char1);
                }
            }
            this.m_fragment = s.substring(n3, i);
        }
    }
    
    public String getScheme() {
        return this.m_scheme;
    }
    
    public String getSchemeSpecificPart() {
        final StringBuffer sb = new StringBuffer();
        if (this.m_host != null || this.m_regAuthority != null) {
            sb.append("//");
            if (this.m_host != null) {
                if (this.m_userinfo != null) {
                    sb.append(this.m_userinfo);
                    sb.append('@');
                }
                sb.append(this.m_host);
                if (this.m_port != -1) {
                    sb.append(':');
                    sb.append(this.m_port);
                }
            }
            else {
                sb.append(this.m_regAuthority);
            }
        }
        if (this.m_path != null) {
            sb.append(this.m_path);
        }
        if (this.m_queryString != null) {
            sb.append('?');
            sb.append(this.m_queryString);
        }
        if (this.m_fragment != null) {
            sb.append('#');
            sb.append(this.m_fragment);
        }
        return sb.toString();
    }
    
    public String getUserinfo() {
        return this.m_userinfo;
    }
    
    public String getHost() {
        return this.m_host;
    }
    
    public int getPort() {
        return this.m_port;
    }
    
    public String getRegBasedAuthority() {
        return this.m_regAuthority;
    }
    
    public String getAuthority() {
        final StringBuffer sb = new StringBuffer();
        if (this.m_host != null || this.m_regAuthority != null) {
            sb.append("//");
            if (this.m_host != null) {
                if (this.m_userinfo != null) {
                    sb.append(this.m_userinfo);
                    sb.append('@');
                }
                sb.append(this.m_host);
                if (this.m_port != -1) {
                    sb.append(':');
                    sb.append(this.m_port);
                }
            }
            else {
                sb.append(this.m_regAuthority);
            }
        }
        return sb.toString();
    }
    
    public String getPath(final boolean b, final boolean b2) {
        final StringBuffer sb = new StringBuffer(this.m_path);
        if (b && this.m_queryString != null) {
            sb.append('?');
            sb.append(this.m_queryString);
        }
        if (b2 && this.m_fragment != null) {
            sb.append('#');
            sb.append(this.m_fragment);
        }
        return sb.toString();
    }
    
    public String getPath() {
        return this.m_path;
    }
    
    public String getQueryString() {
        return this.m_queryString;
    }
    
    public String getFragment() {
        return this.m_fragment;
    }
    
    public void setScheme(final String s) throws MalformedURIException {
        if (s == null) {
            throw new MalformedURIException("Cannot set scheme from null string!");
        }
        if (!isConformantSchemeName(s)) {
            throw new MalformedURIException("The scheme is not conformant.");
        }
        this.m_scheme = s.toLowerCase(Locale.ENGLISH);
    }
    
    public void setUserinfo(final String userinfo) throws MalformedURIException {
        if (userinfo == null) {
            this.m_userinfo = null;
            return;
        }
        if (this.m_host == null) {
            throw new MalformedURIException("Userinfo cannot be set when host is null!");
        }
        for (int i = 0, length = userinfo.length(); i < length; ++i) {
            final char char1 = userinfo.charAt(i);
            if (char1 == '%') {
                if (i + 2 >= length || !isHex(userinfo.charAt(i + 1)) || !isHex(userinfo.charAt(i + 2))) {
                    throw new MalformedURIException("Userinfo contains invalid escape sequence!");
                }
            }
            else if (!isUserinfoCharacter(char1)) {
                throw new MalformedURIException("Userinfo contains invalid character:" + char1);
            }
        }
        this.m_userinfo = userinfo;
    }
    
    public void setHost(final String s) throws MalformedURIException {
        if (s == null || s.length() == 0) {
            if (s != null) {
                this.m_regAuthority = null;
            }
            this.m_host = s;
            this.m_userinfo = null;
            this.m_port = -1;
            return;
        }
        if (!isWellFormedAddress(s)) {
            throw new MalformedURIException("Host is not a well formed address!");
        }
        this.m_host = s;
        this.m_regAuthority = null;
    }
    
    public void setPort(final int port) throws MalformedURIException {
        if (port >= 0 && port <= 65535) {
            if (this.m_host == null) {
                throw new MalformedURIException("Port cannot be set when host is null!");
            }
        }
        else if (port != -1) {
            throw new MalformedURIException("Invalid port number!");
        }
        this.m_port = port;
    }
    
    public void setRegBasedAuthority(final String regAuthority) throws MalformedURIException {
        if (regAuthority == null) {
            this.m_regAuthority = null;
            return;
        }
        if (regAuthority.length() < 1 || !this.isValidRegistryBasedAuthority(regAuthority) || regAuthority.indexOf(47) != -1) {
            throw new MalformedURIException("Registry based authority is not well formed.");
        }
        this.m_regAuthority = regAuthority;
        this.m_host = null;
        this.m_userinfo = null;
        this.m_port = -1;
    }
    
    public void setPath(final String s) throws MalformedURIException {
        if (s == null) {
            this.m_path = null;
            this.m_queryString = null;
            this.m_fragment = null;
        }
        else {
            this.initializePath(s, 0);
        }
    }
    
    public void appendPath(final String path) throws MalformedURIException {
        if (path == null || path.trim().length() == 0) {
            return;
        }
        if (!isURIString(path)) {
            throw new MalformedURIException("Path contains invalid character!");
        }
        if (this.m_path == null || this.m_path.trim().length() == 0) {
            if (path.startsWith("/")) {
                this.m_path = path;
            }
            else {
                this.m_path = "/" + path;
            }
        }
        else if (this.m_path.endsWith("/")) {
            if (path.startsWith("/")) {
                this.m_path = this.m_path.concat(path.substring(1));
            }
            else {
                this.m_path = this.m_path.concat(path);
            }
        }
        else if (path.startsWith("/")) {
            this.m_path = this.m_path.concat(path);
        }
        else {
            this.m_path = this.m_path.concat("/" + path);
        }
    }
    
    public void setQueryString(final String queryString) throws MalformedURIException {
        if (queryString == null) {
            this.m_queryString = null;
        }
        else {
            if (!this.isGenericURI()) {
                throw new MalformedURIException("Query string can only be set for a generic URI!");
            }
            if (this.getPath() == null) {
                throw new MalformedURIException("Query string cannot be set when path is null!");
            }
            if (!isURIString(queryString)) {
                throw new MalformedURIException("Query string contains invalid character!");
            }
            this.m_queryString = queryString;
        }
    }
    
    public void setFragment(final String fragment) throws MalformedURIException {
        if (fragment == null) {
            this.m_fragment = null;
        }
        else {
            if (!this.isGenericURI()) {
                throw new MalformedURIException("Fragment can only be set for a generic URI!");
            }
            if (this.getPath() == null) {
                throw new MalformedURIException("Fragment cannot be set when path is null!");
            }
            if (!isURIString(fragment)) {
                throw new MalformedURIException("Fragment contains invalid character!");
            }
            this.m_fragment = fragment;
        }
    }
    
    public boolean equals(final Object o) {
        if (o instanceof URI) {
            final URI uri = (URI)o;
            if (((this.m_scheme == null && uri.m_scheme == null) || (this.m_scheme != null && uri.m_scheme != null && this.m_scheme.equals(uri.m_scheme))) && ((this.m_userinfo == null && uri.m_userinfo == null) || (this.m_userinfo != null && uri.m_userinfo != null && this.m_userinfo.equals(uri.m_userinfo))) && ((this.m_host == null && uri.m_host == null) || (this.m_host != null && uri.m_host != null && this.m_host.equals(uri.m_host))) && this.m_port == uri.m_port && ((this.m_path == null && uri.m_path == null) || (this.m_path != null && uri.m_path != null && this.m_path.equals(uri.m_path))) && ((this.m_queryString == null && uri.m_queryString == null) || (this.m_queryString != null && uri.m_queryString != null && this.m_queryString.equals(uri.m_queryString))) && ((this.m_fragment == null && uri.m_fragment == null) || (this.m_fragment != null && uri.m_fragment != null && this.m_fragment.equals(uri.m_fragment)))) {
                return true;
            }
        }
        return false;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.m_scheme != null) {
            sb.append(this.m_scheme);
            sb.append(':');
        }
        sb.append(this.getSchemeSpecificPart());
        return sb.toString();
    }
    
    public boolean isGenericURI() {
        return this.m_host != null;
    }
    
    public boolean isAbsoluteURI() {
        return this.m_scheme != null;
    }
    
    public static boolean isConformantSchemeName(final String s) {
        if (s == null || s.trim().length() == 0) {
            return false;
        }
        if (!isAlpha(s.charAt(0))) {
            return false;
        }
        for (int length = s.length(), i = 1; i < length; ++i) {
            if (!isSchemeCharacter(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isWellFormedAddress(final String s) {
        if (s == null) {
            return false;
        }
        final int length = s.length();
        if (length == 0) {
            return false;
        }
        if (s.startsWith("[")) {
            return isWellFormedIPv6Reference(s);
        }
        if (s.startsWith(".") || s.startsWith("-") || s.endsWith("-")) {
            return false;
        }
        int n = s.lastIndexOf(46);
        if (s.endsWith(".")) {
            n = s.substring(0, n).lastIndexOf(46);
        }
        if (n + 1 < length && isDigit(s.charAt(n + 1))) {
            return isWellFormedIPv4Address(s);
        }
        if (length > 255) {
            return false;
        }
        int n2 = 0;
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '.') {
                if (!isAlphanum(s.charAt(i - 1))) {
                    return false;
                }
                if (i + 1 < length && !isAlphanum(s.charAt(i + 1))) {
                    return false;
                }
                n2 = 0;
            }
            else {
                if (!isAlphanum(char1) && char1 != '-') {
                    return false;
                }
                if (++n2 > 63) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean isWellFormedIPv4Address(final String s) {
        final int length = s.length();
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '.') {
                if ((i > 0 && !isDigit(s.charAt(i - 1))) || (i + 1 < length && !isDigit(s.charAt(i + 1)))) {
                    return false;
                }
                n2 = 0;
                if (++n > 3) {
                    return false;
                }
            }
            else {
                if (!isDigit(char1)) {
                    return false;
                }
                if (++n2 > 3) {
                    return false;
                }
                if (n2 == 3) {
                    final char char2 = s.charAt(i - 2);
                    final char char3 = s.charAt(i - 1);
                    if (char2 >= '2' && (char2 != '2' || (char3 >= '5' && (char3 != '5' || char1 > '5')))) {
                        return false;
                    }
                }
            }
        }
        return n == 3;
    }
    
    public static boolean isWellFormedIPv6Reference(final String s) {
        final int length = s.length();
        final int n = 1;
        final int n2 = length - 1;
        if (length <= 2 || s.charAt(0) != '[' || s.charAt(n2) != ']') {
            return false;
        }
        final int[] array = { 0 };
        int scanHexSequence = scanHexSequence(s, n, n2, array);
        if (scanHexSequence == -1) {
            return false;
        }
        if (scanHexSequence == n2) {
            return array[0] == 8;
        }
        if (scanHexSequence + 1 >= n2 || s.charAt(scanHexSequence) != ':') {
            return false;
        }
        if (s.charAt(scanHexSequence + 1) != ':') {
            return array[0] == 6 && isWellFormedIPv4Address(s.substring(scanHexSequence + 1, n2));
        }
        if (++array[0] > 8) {
            return false;
        }
        scanHexSequence += 2;
        if (scanHexSequence == n2) {
            return true;
        }
        final int n3 = array[0];
        final int scanHexSequence2 = scanHexSequence(s, scanHexSequence, n2, array);
        return scanHexSequence2 == n2 || (scanHexSequence2 != -1 && isWellFormedIPv4Address(s.substring((array[0] > n3) ? (scanHexSequence2 + 1) : scanHexSequence2, n2)));
    }
    
    private static int scanHexSequence(final String s, int i, final int n, final int[] array) {
        int n2 = 0;
        final int n3 = i;
        while (i < n) {
            final char char1 = s.charAt(i);
            if (char1 == ':') {
                if (n2 > 0 && ++array[0] > 8) {
                    return -1;
                }
                if (n2 == 0 || (i + 1 < n && s.charAt(i + 1) == ':')) {
                    return i;
                }
                n2 = 0;
            }
            else if (!isHex(char1)) {
                if (char1 == '.' && n2 < 4 && n2 > 0 && array[0] <= 6) {
                    final int n4 = i - n2 - 1;
                    return (n4 >= n3) ? n4 : (n4 + 1);
                }
                return -1;
            }
            else if (++n2 > 4) {
                return -1;
            }
            ++i;
        }
        return (n2 > 0 && ++array[0] <= 8) ? n : -1;
    }
    
    private static boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }
    
    private static boolean isHex(final char c) {
        return c <= 'f' && (URI.fgLookupTable[c] & 0x40) != 0x0;
    }
    
    private static boolean isAlpha(final char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    
    private static boolean isAlphanum(final char c) {
        return c <= 'z' && (URI.fgLookupTable[c] & 0x30) != 0x0;
    }
    
    private static boolean isReservedCharacter(final char c) {
        return c <= ']' && (URI.fgLookupTable[c] & 0x1) != 0x0;
    }
    
    private static boolean isUnreservedCharacter(final char c) {
        return c <= '~' && (URI.fgLookupTable[c] & 0x32) != 0x0;
    }
    
    private static boolean isURICharacter(final char c) {
        return c <= '~' && (URI.fgLookupTable[c] & 0x33) != 0x0;
    }
    
    private static boolean isSchemeCharacter(final char c) {
        return c <= 'z' && (URI.fgLookupTable[c] & 0x34) != 0x0;
    }
    
    private static boolean isUserinfoCharacter(final char c) {
        return c <= 'z' && (URI.fgLookupTable[c] & 0x3A) != 0x0;
    }
    
    private static boolean isPathCharacter(final char c) {
        return c <= '~' && (URI.fgLookupTable[c] & 0xB2) != 0x0;
    }
    
    private static boolean isURIString(final String s) {
        if (s == null) {
            return false;
        }
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '%') {
                if (i + 2 >= length || !isHex(s.charAt(i + 1)) || !isHex(s.charAt(i + 2))) {
                    return false;
                }
                i += 2;
            }
            else if (!isURICharacter(char1)) {
                return false;
            }
        }
        return true;
    }
    
    static {
        fgLookupTable = new byte[128];
        for (int i = 48; i <= 57; ++i) {
            final byte[] fgLookupTable2 = URI.fgLookupTable;
            final int n = i;
            fgLookupTable2[n] |= 0x60;
        }
        for (int j = 65; j <= 70; ++j) {
            final byte[] fgLookupTable3 = URI.fgLookupTable;
            final int n2 = j;
            fgLookupTable3[n2] |= 0x50;
            final byte[] fgLookupTable4 = URI.fgLookupTable;
            final int n3 = j + 32;
            fgLookupTable4[n3] |= 0x50;
        }
        for (int k = 71; k <= 90; ++k) {
            final byte[] fgLookupTable5 = URI.fgLookupTable;
            final int n4 = k;
            fgLookupTable5[n4] |= 0x10;
            final byte[] fgLookupTable6 = URI.fgLookupTable;
            final int n5 = k + 32;
            fgLookupTable6[n5] |= 0x10;
        }
        final byte[] fgLookupTable7 = URI.fgLookupTable;
        final int n6 = 59;
        fgLookupTable7[n6] |= 0x1;
        final byte[] fgLookupTable8 = URI.fgLookupTable;
        final int n7 = 47;
        fgLookupTable8[n7] |= 0x1;
        final byte[] fgLookupTable9 = URI.fgLookupTable;
        final int n8 = 63;
        fgLookupTable9[n8] |= 0x1;
        final byte[] fgLookupTable10 = URI.fgLookupTable;
        final int n9 = 58;
        fgLookupTable10[n9] |= 0x1;
        final byte[] fgLookupTable11 = URI.fgLookupTable;
        final int n10 = 64;
        fgLookupTable11[n10] |= 0x1;
        final byte[] fgLookupTable12 = URI.fgLookupTable;
        final int n11 = 38;
        fgLookupTable12[n11] |= 0x1;
        final byte[] fgLookupTable13 = URI.fgLookupTable;
        final int n12 = 61;
        fgLookupTable13[n12] |= 0x1;
        final byte[] fgLookupTable14 = URI.fgLookupTable;
        final int n13 = 43;
        fgLookupTable14[n13] |= 0x1;
        final byte[] fgLookupTable15 = URI.fgLookupTable;
        final int n14 = 36;
        fgLookupTable15[n14] |= 0x1;
        final byte[] fgLookupTable16 = URI.fgLookupTable;
        final int n15 = 44;
        fgLookupTable16[n15] |= 0x1;
        final byte[] fgLookupTable17 = URI.fgLookupTable;
        final int n16 = 91;
        fgLookupTable17[n16] |= 0x1;
        final byte[] fgLookupTable18 = URI.fgLookupTable;
        final int n17 = 93;
        fgLookupTable18[n17] |= 0x1;
        final byte[] fgLookupTable19 = URI.fgLookupTable;
        final int n18 = 45;
        fgLookupTable19[n18] |= 0x2;
        final byte[] fgLookupTable20 = URI.fgLookupTable;
        final int n19 = 95;
        fgLookupTable20[n19] |= 0x2;
        final byte[] fgLookupTable21 = URI.fgLookupTable;
        final int n20 = 46;
        fgLookupTable21[n20] |= 0x2;
        final byte[] fgLookupTable22 = URI.fgLookupTable;
        final int n21 = 33;
        fgLookupTable22[n21] |= 0x2;
        final byte[] fgLookupTable23 = URI.fgLookupTable;
        final int n22 = 126;
        fgLookupTable23[n22] |= 0x2;
        final byte[] fgLookupTable24 = URI.fgLookupTable;
        final int n23 = 42;
        fgLookupTable24[n23] |= 0x2;
        final byte[] fgLookupTable25 = URI.fgLookupTable;
        final int n24 = 39;
        fgLookupTable25[n24] |= 0x2;
        final byte[] fgLookupTable26 = URI.fgLookupTable;
        final int n25 = 40;
        fgLookupTable26[n25] |= 0x2;
        final byte[] fgLookupTable27 = URI.fgLookupTable;
        final int n26 = 41;
        fgLookupTable27[n26] |= 0x2;
        final byte[] fgLookupTable28 = URI.fgLookupTable;
        final int n27 = 43;
        fgLookupTable28[n27] |= 0x4;
        final byte[] fgLookupTable29 = URI.fgLookupTable;
        final int n28 = 45;
        fgLookupTable29[n28] |= 0x4;
        final byte[] fgLookupTable30 = URI.fgLookupTable;
        final int n29 = 46;
        fgLookupTable30[n29] |= 0x4;
        final byte[] fgLookupTable31 = URI.fgLookupTable;
        final int n30 = 59;
        fgLookupTable31[n30] |= 0x8;
        final byte[] fgLookupTable32 = URI.fgLookupTable;
        final int n31 = 58;
        fgLookupTable32[n31] |= 0x8;
        final byte[] fgLookupTable33 = URI.fgLookupTable;
        final int n32 = 38;
        fgLookupTable33[n32] |= 0x8;
        final byte[] fgLookupTable34 = URI.fgLookupTable;
        final int n33 = 61;
        fgLookupTable34[n33] |= 0x8;
        final byte[] fgLookupTable35 = URI.fgLookupTable;
        final int n34 = 43;
        fgLookupTable35[n34] |= 0x8;
        final byte[] fgLookupTable36 = URI.fgLookupTable;
        final int n35 = 36;
        fgLookupTable36[n35] |= 0x8;
        final byte[] fgLookupTable37 = URI.fgLookupTable;
        final int n36 = 44;
        fgLookupTable37[n36] |= 0x8;
        final byte[] fgLookupTable38 = URI.fgLookupTable;
        final int n37 = 59;
        fgLookupTable38[n37] |= (byte)128;
        final byte[] fgLookupTable39 = URI.fgLookupTable;
        final int n38 = 47;
        fgLookupTable39[n38] |= (byte)128;
        final byte[] fgLookupTable40 = URI.fgLookupTable;
        final int n39 = 58;
        fgLookupTable40[n39] |= (byte)128;
        final byte[] fgLookupTable41 = URI.fgLookupTable;
        final int n40 = 64;
        fgLookupTable41[n40] |= (byte)128;
        final byte[] fgLookupTable42 = URI.fgLookupTable;
        final int n41 = 38;
        fgLookupTable42[n41] |= (byte)128;
        final byte[] fgLookupTable43 = URI.fgLookupTable;
        final int n42 = 61;
        fgLookupTable43[n42] |= (byte)128;
        final byte[] fgLookupTable44 = URI.fgLookupTable;
        final int n43 = 43;
        fgLookupTable44[n43] |= (byte)128;
        final byte[] fgLookupTable45 = URI.fgLookupTable;
        final int n44 = 36;
        fgLookupTable45[n44] |= (byte)128;
        final byte[] fgLookupTable46 = URI.fgLookupTable;
        final int n45 = 44;
        fgLookupTable46[n45] |= (byte)128;
    }
    
    public static class MalformedURIException extends IOException
    {
        static final long serialVersionUID = -6695054834342951930L;
        
        public MalformedURIException() {
        }
        
        public MalformedURIException(final String s) {
            super(s);
        }
    }
}
