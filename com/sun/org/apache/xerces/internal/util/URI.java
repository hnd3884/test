package com.sun.org.apache.xerces.internal.util;

import java.io.IOException;
import java.util.Objects;
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
    private static boolean DEBUG;
    
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
    
    public URI(final URI p_other) {
        this.m_scheme = null;
        this.m_userinfo = null;
        this.m_host = null;
        this.m_port = -1;
        this.m_regAuthority = null;
        this.m_path = null;
        this.m_queryString = null;
        this.m_fragment = null;
        this.initialize(p_other);
    }
    
    public URI(final String p_uriSpec) throws MalformedURIException {
        this((URI)null, p_uriSpec);
    }
    
    public URI(final String p_uriSpec, final boolean allowNonAbsoluteURI) throws MalformedURIException {
        this(null, p_uriSpec, allowNonAbsoluteURI);
    }
    
    public URI(final URI p_base, final String p_uriSpec) throws MalformedURIException {
        this.m_scheme = null;
        this.m_userinfo = null;
        this.m_host = null;
        this.m_port = -1;
        this.m_regAuthority = null;
        this.m_path = null;
        this.m_queryString = null;
        this.m_fragment = null;
        this.initialize(p_base, p_uriSpec);
    }
    
    public URI(final URI p_base, final String p_uriSpec, final boolean allowNonAbsoluteURI) throws MalformedURIException {
        this.m_scheme = null;
        this.m_userinfo = null;
        this.m_host = null;
        this.m_port = -1;
        this.m_regAuthority = null;
        this.m_path = null;
        this.m_queryString = null;
        this.m_fragment = null;
        this.initialize(p_base, p_uriSpec, allowNonAbsoluteURI);
    }
    
    public URI(final String p_scheme, final String p_schemeSpecificPart) throws MalformedURIException {
        this.m_scheme = null;
        this.m_userinfo = null;
        this.m_host = null;
        this.m_port = -1;
        this.m_regAuthority = null;
        this.m_path = null;
        this.m_queryString = null;
        this.m_fragment = null;
        if (p_scheme == null || p_scheme.trim().length() == 0) {
            throw new MalformedURIException("Cannot construct URI with null/empty scheme!");
        }
        if (p_schemeSpecificPart == null || p_schemeSpecificPart.trim().length() == 0) {
            throw new MalformedURIException("Cannot construct URI with null/empty scheme-specific part!");
        }
        this.setScheme(p_scheme);
        this.setPath(p_schemeSpecificPart);
    }
    
    public URI(final String p_scheme, final String p_host, final String p_path, final String p_queryString, final String p_fragment) throws MalformedURIException {
        this(p_scheme, null, p_host, -1, p_path, p_queryString, p_fragment);
    }
    
    public URI(final String p_scheme, final String p_userinfo, final String p_host, final int p_port, final String p_path, final String p_queryString, final String p_fragment) throws MalformedURIException {
        this.m_scheme = null;
        this.m_userinfo = null;
        this.m_host = null;
        this.m_port = -1;
        this.m_regAuthority = null;
        this.m_path = null;
        this.m_queryString = null;
        this.m_fragment = null;
        if (p_scheme == null || p_scheme.trim().length() == 0) {
            throw new MalformedURIException("Scheme is required!");
        }
        if (p_host == null) {
            if (p_userinfo != null) {
                throw new MalformedURIException("Userinfo may not be specified if host is not specified!");
            }
            if (p_port != -1) {
                throw new MalformedURIException("Port may not be specified if host is not specified!");
            }
        }
        if (p_path != null) {
            if (p_path.indexOf(63) != -1 && p_queryString != null) {
                throw new MalformedURIException("Query string cannot be specified in path and query string!");
            }
            if (p_path.indexOf(35) != -1 && p_fragment != null) {
                throw new MalformedURIException("Fragment cannot be specified in both the path and fragment!");
            }
        }
        this.setScheme(p_scheme);
        this.setHost(p_host);
        this.setPort(p_port);
        this.setUserinfo(p_userinfo);
        this.setPath(p_path);
        this.setQueryString(p_queryString);
        this.setFragment(p_fragment);
    }
    
    private void initialize(final URI p_other) {
        this.m_scheme = p_other.getScheme();
        this.m_userinfo = p_other.getUserinfo();
        this.m_host = p_other.getHost();
        this.m_port = p_other.getPort();
        this.m_regAuthority = p_other.getRegBasedAuthority();
        this.m_path = p_other.getPath();
        this.m_queryString = p_other.getQueryString();
        this.m_fragment = p_other.getFragment();
    }
    
    private void initialize(final URI p_base, final String p_uriSpec, final boolean allowNonAbsoluteURI) throws MalformedURIException {
        final String uriSpec = p_uriSpec;
        final int uriSpecLen = (uriSpec != null) ? uriSpec.length() : 0;
        if (p_base == null && uriSpecLen == 0) {
            if (allowNonAbsoluteURI) {
                this.m_path = "";
                return;
            }
            throw new MalformedURIException("Cannot initialize URI with empty parameters.");
        }
        else {
            if (uriSpecLen == 0) {
                this.initialize(p_base);
                return;
            }
            int index = 0;
            final int colonIdx = uriSpec.indexOf(58);
            if (colonIdx != -1) {
                final int searchFrom = colonIdx - 1;
                final int slashIdx = uriSpec.lastIndexOf(47, searchFrom);
                final int queryIdx = uriSpec.lastIndexOf(63, searchFrom);
                final int fragmentIdx = uriSpec.lastIndexOf(35, searchFrom);
                if (colonIdx == 0 || slashIdx != -1 || queryIdx != -1 || fragmentIdx != -1) {
                    if (colonIdx == 0 || (p_base == null && fragmentIdx != 0 && !allowNonAbsoluteURI)) {
                        throw new MalformedURIException("No scheme found in URI.");
                    }
                }
                else {
                    this.initializeScheme(uriSpec);
                    index = this.m_scheme.length() + 1;
                    if (colonIdx == uriSpecLen - 1 || uriSpec.charAt(colonIdx + 1) == '#') {
                        throw new MalformedURIException("Scheme specific part cannot be empty.");
                    }
                }
            }
            else if (p_base == null && uriSpec.indexOf(35) != 0 && !allowNonAbsoluteURI) {
                throw new MalformedURIException("No scheme found in URI.");
            }
            if (index + 1 < uriSpecLen && uriSpec.charAt(index) == '/' && uriSpec.charAt(index + 1) == '/') {
                index += 2;
                final int startPos = index;
                char testChar = '\0';
                while (index < uriSpecLen) {
                    testChar = uriSpec.charAt(index);
                    if (testChar == '/' || testChar == '?') {
                        break;
                    }
                    if (testChar == '#') {
                        break;
                    }
                    ++index;
                }
                if (index > startPos) {
                    if (!this.initializeAuthority(uriSpec.substring(startPos, index))) {
                        index = startPos - 2;
                    }
                }
                else {
                    this.m_host = "";
                }
            }
            this.initializePath(uriSpec, index);
            if (p_base != null) {
                this.absolutize(p_base);
            }
        }
    }
    
    private void initialize(final URI p_base, final String p_uriSpec) throws MalformedURIException {
        final String uriSpec = p_uriSpec;
        final int uriSpecLen = (uriSpec != null) ? uriSpec.length() : 0;
        if (p_base == null && uriSpecLen == 0) {
            throw new MalformedURIException("Cannot initialize URI with empty parameters.");
        }
        if (uriSpecLen == 0) {
            this.initialize(p_base);
            return;
        }
        int index = 0;
        final int colonIdx = uriSpec.indexOf(58);
        if (colonIdx != -1) {
            final int searchFrom = colonIdx - 1;
            final int slashIdx = uriSpec.lastIndexOf(47, searchFrom);
            final int queryIdx = uriSpec.lastIndexOf(63, searchFrom);
            final int fragmentIdx = uriSpec.lastIndexOf(35, searchFrom);
            if (colonIdx == 0 || slashIdx != -1 || queryIdx != -1 || fragmentIdx != -1) {
                if (colonIdx == 0 || (p_base == null && fragmentIdx != 0)) {
                    throw new MalformedURIException("No scheme found in URI.");
                }
            }
            else {
                this.initializeScheme(uriSpec);
                index = this.m_scheme.length() + 1;
                if (colonIdx == uriSpecLen - 1 || uriSpec.charAt(colonIdx + 1) == '#') {
                    throw new MalformedURIException("Scheme specific part cannot be empty.");
                }
            }
        }
        else if (p_base == null && uriSpec.indexOf(35) != 0) {
            throw new MalformedURIException("No scheme found in URI.");
        }
        if (index + 1 < uriSpecLen && uriSpec.charAt(index) == '/' && uriSpec.charAt(index + 1) == '/') {
            index += 2;
            final int startPos = index;
            char testChar = '\0';
            while (index < uriSpecLen) {
                testChar = uriSpec.charAt(index);
                if (testChar == '/' || testChar == '?') {
                    break;
                }
                if (testChar == '#') {
                    break;
                }
                ++index;
            }
            if (index > startPos) {
                if (!this.initializeAuthority(uriSpec.substring(startPos, index))) {
                    index = startPos - 2;
                }
            }
            else {
                if (index >= uriSpecLen) {
                    throw new MalformedURIException("Expected authority.");
                }
                this.m_host = "";
            }
        }
        this.initializePath(uriSpec, index);
        if (p_base != null) {
            this.absolutize(p_base);
        }
    }
    
    public void absolutize(final URI p_base) {
        if (this.m_path.length() == 0 && this.m_scheme == null && this.m_host == null && this.m_regAuthority == null) {
            this.m_scheme = p_base.getScheme();
            this.m_userinfo = p_base.getUserinfo();
            this.m_host = p_base.getHost();
            this.m_port = p_base.getPort();
            this.m_regAuthority = p_base.getRegBasedAuthority();
            this.m_path = p_base.getPath();
            if (this.m_queryString == null) {
                this.m_queryString = p_base.getQueryString();
                if (this.m_fragment == null) {
                    this.m_fragment = p_base.getFragment();
                }
            }
            return;
        }
        if (this.m_scheme != null) {
            return;
        }
        this.m_scheme = p_base.getScheme();
        if (this.m_host != null || this.m_regAuthority != null) {
            return;
        }
        this.m_userinfo = p_base.getUserinfo();
        this.m_host = p_base.getHost();
        this.m_port = p_base.getPort();
        this.m_regAuthority = p_base.getRegBasedAuthority();
        if (this.m_path.length() > 0 && this.m_path.startsWith("/")) {
            return;
        }
        String path = "";
        final String basePath = p_base.getPath();
        if (basePath != null && basePath.length() > 0) {
            final int lastSlash = basePath.lastIndexOf(47);
            if (lastSlash != -1) {
                path = basePath.substring(0, lastSlash + 1);
            }
        }
        else if (this.m_path.length() > 0) {
            path = "/";
        }
        path = path.concat(this.m_path);
        for (int index = -1; (index = path.indexOf("/./")) != -1; path = path.substring(0, index + 1).concat(path.substring(index + 3))) {}
        if (path.endsWith("/.")) {
            path = path.substring(0, path.length() - 1);
        }
        int index = 1;
        int segIndex = -1;
        String tempString = null;
        while ((index = path.indexOf("/../", index)) > 0) {
            tempString = path.substring(0, path.indexOf("/../"));
            segIndex = tempString.lastIndexOf(47);
            if (segIndex != -1) {
                if (!tempString.substring(segIndex).equals("..")) {
                    path = path.substring(0, segIndex + 1).concat(path.substring(index + 4));
                    index = segIndex;
                }
                else {
                    index += 4;
                }
            }
            else {
                index += 4;
            }
        }
        if (path.endsWith("/..")) {
            tempString = path.substring(0, path.length() - 3);
            segIndex = tempString.lastIndexOf(47);
            if (segIndex != -1) {
                path = path.substring(0, segIndex + 1);
            }
        }
        this.m_path = path;
    }
    
    private void initializeScheme(final String p_uriSpec) throws MalformedURIException {
        final int uriSpecLen = p_uriSpec.length();
        int index = 0;
        String scheme = null;
        char testChar = '\0';
        while (index < uriSpecLen) {
            testChar = p_uriSpec.charAt(index);
            if (testChar == ':' || testChar == '/' || testChar == '?') {
                break;
            }
            if (testChar == '#') {
                break;
            }
            ++index;
        }
        scheme = p_uriSpec.substring(0, index);
        if (scheme.length() == 0) {
            throw new MalformedURIException("No scheme found in URI.");
        }
        this.setScheme(scheme);
    }
    
    private boolean initializeAuthority(final String p_uriSpec) {
        int index = 0;
        int start = 0;
        final int end = p_uriSpec.length();
        char testChar = '\0';
        String userinfo = null;
        if (p_uriSpec.indexOf(64, start) != -1) {
            while (index < end) {
                testChar = p_uriSpec.charAt(index);
                if (testChar == '@') {
                    break;
                }
                ++index;
            }
            userinfo = p_uriSpec.substring(start, index);
            ++index;
        }
        String host = null;
        start = index;
        boolean hasPort = false;
        if (index < end) {
            if (p_uriSpec.charAt(start) == '[') {
                final int bracketIndex = p_uriSpec.indexOf(93, start);
                index = ((bracketIndex != -1) ? bracketIndex : end);
                if (index + 1 < end && p_uriSpec.charAt(index + 1) == ':') {
                    ++index;
                    hasPort = true;
                }
                else {
                    index = end;
                }
            }
            else {
                final int colonIndex = p_uriSpec.lastIndexOf(58, end);
                index = ((colonIndex > start) ? colonIndex : end);
                hasPort = (index != end);
            }
        }
        host = p_uriSpec.substring(start, index);
        int port = -1;
        if (host.length() > 0 && hasPort) {
            for (start = ++index; index < end; ++index) {}
            final String portStr = p_uriSpec.substring(start, index);
            if (portStr.length() > 0) {
                try {
                    port = Integer.parseInt(portStr);
                    if (port == -1) {
                        --port;
                    }
                }
                catch (final NumberFormatException nfe) {
                    port = -2;
                }
            }
        }
        if (this.isValidServerBasedAuthority(host, port, userinfo)) {
            this.m_host = host;
            this.m_port = port;
            this.m_userinfo = userinfo;
            return true;
        }
        if (this.isValidRegistryBasedAuthority(p_uriSpec)) {
            this.m_regAuthority = p_uriSpec;
            return true;
        }
        return false;
    }
    
    private boolean isValidServerBasedAuthority(final String host, final int port, final String userinfo) {
        if (!isWellFormedAddress(host)) {
            return false;
        }
        if (port < -1 || port > 65535) {
            return false;
        }
        if (userinfo != null) {
            int index = 0;
            final int end = userinfo.length();
            char testChar = '\0';
            while (index < end) {
                testChar = userinfo.charAt(index);
                if (testChar == '%') {
                    if (index + 2 >= end || !isHex(userinfo.charAt(index + 1)) || !isHex(userinfo.charAt(index + 2))) {
                        return false;
                    }
                    index += 2;
                }
                else if (!isUserinfoCharacter(testChar)) {
                    return false;
                }
                ++index;
            }
        }
        return true;
    }
    
    private boolean isValidRegistryBasedAuthority(final String authority) {
        for (int index = 0, end = authority.length(); index < end; ++index) {
            final char testChar = authority.charAt(index);
            if (testChar == '%') {
                if (index + 2 >= end || !isHex(authority.charAt(index + 1)) || !isHex(authority.charAt(index + 2))) {
                    return false;
                }
                index += 2;
            }
            else if (!isPathCharacter(testChar)) {
                return false;
            }
        }
        return true;
    }
    
    private void initializePath(final String p_uriSpec, final int p_nStartIndex) throws MalformedURIException {
        if (p_uriSpec == null) {
            throw new MalformedURIException("Cannot initialize path from null string!");
        }
        int index = p_nStartIndex;
        int start = p_nStartIndex;
        final int end = p_uriSpec.length();
        char testChar = '\0';
        if (start < end) {
            if (this.getScheme() == null || p_uriSpec.charAt(start) == '/') {
                while (index < end) {
                    testChar = p_uriSpec.charAt(index);
                    if (testChar == '%') {
                        if (index + 2 >= end || !isHex(p_uriSpec.charAt(index + 1)) || !isHex(p_uriSpec.charAt(index + 2))) {
                            throw new MalformedURIException("Path contains invalid escape sequence!");
                        }
                        index += 2;
                    }
                    else if (!isPathCharacter(testChar)) {
                        if (testChar == '?') {
                            break;
                        }
                        if (testChar == '#') {
                            break;
                        }
                        throw new MalformedURIException("Path contains invalid character: " + testChar);
                    }
                    ++index;
                }
            }
            else {
                while (index < end) {
                    testChar = p_uriSpec.charAt(index);
                    if (testChar == '?') {
                        break;
                    }
                    if (testChar == '#') {
                        break;
                    }
                    if (testChar == '%') {
                        if (index + 2 >= end || !isHex(p_uriSpec.charAt(index + 1)) || !isHex(p_uriSpec.charAt(index + 2))) {
                            throw new MalformedURIException("Opaque part contains invalid escape sequence!");
                        }
                        index += 2;
                    }
                    else if (!isURICharacter(testChar)) {
                        throw new MalformedURIException("Opaque part contains invalid character: " + testChar);
                    }
                    ++index;
                }
            }
        }
        this.m_path = p_uriSpec.substring(start, index);
        if (testChar == '?') {
            for (start = ++index; index < end; ++index) {
                testChar = p_uriSpec.charAt(index);
                if (testChar == '#') {
                    break;
                }
                if (testChar == '%') {
                    if (index + 2 >= end || !isHex(p_uriSpec.charAt(index + 1)) || !isHex(p_uriSpec.charAt(index + 2))) {
                        throw new MalformedURIException("Query string contains invalid escape sequence!");
                    }
                    index += 2;
                }
                else if (!isURICharacter(testChar)) {
                    throw new MalformedURIException("Query string contains invalid character: " + testChar);
                }
            }
            this.m_queryString = p_uriSpec.substring(start, index);
        }
        if (testChar == '#') {
            for (start = ++index; index < end; ++index) {
                testChar = p_uriSpec.charAt(index);
                if (testChar == '%') {
                    if (index + 2 >= end || !isHex(p_uriSpec.charAt(index + 1)) || !isHex(p_uriSpec.charAt(index + 2))) {
                        throw new MalformedURIException("Fragment contains invalid escape sequence!");
                    }
                    index += 2;
                }
                else if (!isURICharacter(testChar)) {
                    throw new MalformedURIException("Fragment contains invalid character: " + testChar);
                }
            }
            this.m_fragment = p_uriSpec.substring(start, index);
        }
    }
    
    public String getScheme() {
        return this.m_scheme;
    }
    
    public String getSchemeSpecificPart() {
        final StringBuilder schemespec = new StringBuilder();
        if (this.m_host != null || this.m_regAuthority != null) {
            schemespec.append("//");
            if (this.m_host != null) {
                if (this.m_userinfo != null) {
                    schemespec.append(this.m_userinfo);
                    schemespec.append('@');
                }
                schemespec.append(this.m_host);
                if (this.m_port != -1) {
                    schemespec.append(':');
                    schemespec.append(this.m_port);
                }
            }
            else {
                schemespec.append(this.m_regAuthority);
            }
        }
        if (this.m_path != null) {
            schemespec.append(this.m_path);
        }
        if (this.m_queryString != null) {
            schemespec.append('?');
            schemespec.append(this.m_queryString);
        }
        if (this.m_fragment != null) {
            schemespec.append('#');
            schemespec.append(this.m_fragment);
        }
        return schemespec.toString();
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
        final StringBuilder authority = new StringBuilder();
        if (this.m_host != null || this.m_regAuthority != null) {
            authority.append("//");
            if (this.m_host != null) {
                if (this.m_userinfo != null) {
                    authority.append(this.m_userinfo);
                    authority.append('@');
                }
                authority.append(this.m_host);
                if (this.m_port != -1) {
                    authority.append(':');
                    authority.append(this.m_port);
                }
            }
            else {
                authority.append(this.m_regAuthority);
            }
        }
        return authority.toString();
    }
    
    public String getPath(final boolean p_includeQueryString, final boolean p_includeFragment) {
        final StringBuilder pathString = new StringBuilder(this.m_path);
        if (p_includeQueryString && this.m_queryString != null) {
            pathString.append('?');
            pathString.append(this.m_queryString);
        }
        if (p_includeFragment && this.m_fragment != null) {
            pathString.append('#');
            pathString.append(this.m_fragment);
        }
        return pathString.toString();
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
    
    public void setScheme(final String p_scheme) throws MalformedURIException {
        if (p_scheme == null) {
            throw new MalformedURIException("Cannot set scheme from null string!");
        }
        if (!isConformantSchemeName(p_scheme)) {
            throw new MalformedURIException("The scheme is not conformant.");
        }
        this.m_scheme = p_scheme.toLowerCase();
    }
    
    public void setUserinfo(final String p_userinfo) throws MalformedURIException {
        if (p_userinfo == null) {
            this.m_userinfo = null;
            return;
        }
        if (this.m_host == null) {
            throw new MalformedURIException("Userinfo cannot be set when host is null!");
        }
        int index = 0;
        final int end = p_userinfo.length();
        char testChar = '\0';
        while (index < end) {
            testChar = p_userinfo.charAt(index);
            if (testChar == '%') {
                if (index + 2 >= end || !isHex(p_userinfo.charAt(index + 1)) || !isHex(p_userinfo.charAt(index + 2))) {
                    throw new MalformedURIException("Userinfo contains invalid escape sequence!");
                }
            }
            else if (!isUserinfoCharacter(testChar)) {
                throw new MalformedURIException("Userinfo contains invalid character:" + testChar);
            }
            ++index;
        }
        this.m_userinfo = p_userinfo;
    }
    
    public void setHost(final String p_host) throws MalformedURIException {
        if (p_host == null || p_host.length() == 0) {
            if (p_host != null) {
                this.m_regAuthority = null;
            }
            this.m_host = p_host;
            this.m_userinfo = null;
            this.m_port = -1;
            return;
        }
        if (!isWellFormedAddress(p_host)) {
            throw new MalformedURIException("Host is not a well formed address!");
        }
        this.m_host = p_host;
        this.m_regAuthority = null;
    }
    
    public void setPort(final int p_port) throws MalformedURIException {
        if (p_port >= 0 && p_port <= 65535) {
            if (this.m_host == null) {
                throw new MalformedURIException("Port cannot be set when host is null!");
            }
        }
        else if (p_port != -1) {
            throw new MalformedURIException("Invalid port number!");
        }
        this.m_port = p_port;
    }
    
    public void setRegBasedAuthority(final String authority) throws MalformedURIException {
        if (authority == null) {
            this.m_regAuthority = null;
            return;
        }
        if (authority.length() < 1 || !this.isValidRegistryBasedAuthority(authority) || authority.indexOf(47) != -1) {
            throw new MalformedURIException("Registry based authority is not well formed.");
        }
        this.m_regAuthority = authority;
        this.m_host = null;
        this.m_userinfo = null;
        this.m_port = -1;
    }
    
    public void setPath(final String p_path) throws MalformedURIException {
        if (p_path == null) {
            this.m_path = null;
            this.m_queryString = null;
            this.m_fragment = null;
        }
        else {
            this.initializePath(p_path, 0);
        }
    }
    
    public void appendPath(final String p_addToPath) throws MalformedURIException {
        if (p_addToPath == null || p_addToPath.trim().length() == 0) {
            return;
        }
        if (!isURIString(p_addToPath)) {
            throw new MalformedURIException("Path contains invalid character!");
        }
        if (this.m_path == null || this.m_path.trim().length() == 0) {
            if (p_addToPath.startsWith("/")) {
                this.m_path = p_addToPath;
            }
            else {
                this.m_path = "/" + p_addToPath;
            }
        }
        else if (this.m_path.endsWith("/")) {
            if (p_addToPath.startsWith("/")) {
                this.m_path = this.m_path.concat(p_addToPath.substring(1));
            }
            else {
                this.m_path = this.m_path.concat(p_addToPath);
            }
        }
        else if (p_addToPath.startsWith("/")) {
            this.m_path = this.m_path.concat(p_addToPath);
        }
        else {
            this.m_path = this.m_path.concat("/" + p_addToPath);
        }
    }
    
    public void setQueryString(final String p_queryString) throws MalformedURIException {
        if (p_queryString == null) {
            this.m_queryString = null;
        }
        else {
            if (!this.isGenericURI()) {
                throw new MalformedURIException("Query string can only be set for a generic URI!");
            }
            if (this.getPath() == null) {
                throw new MalformedURIException("Query string cannot be set when path is null!");
            }
            if (!isURIString(p_queryString)) {
                throw new MalformedURIException("Query string contains invalid character!");
            }
            this.m_queryString = p_queryString;
        }
    }
    
    public void setFragment(final String p_fragment) throws MalformedURIException {
        if (p_fragment == null) {
            this.m_fragment = null;
        }
        else {
            if (!this.isGenericURI()) {
                throw new MalformedURIException("Fragment can only be set for a generic URI!");
            }
            if (this.getPath() == null) {
                throw new MalformedURIException("Fragment cannot be set when path is null!");
            }
            if (!isURIString(p_fragment)) {
                throw new MalformedURIException("Fragment contains invalid character!");
            }
            this.m_fragment = p_fragment;
        }
    }
    
    @Override
    public boolean equals(final Object p_test) {
        if (p_test instanceof URI) {
            final URI testURI = (URI)p_test;
            if (((this.m_scheme == null && testURI.m_scheme == null) || (this.m_scheme != null && testURI.m_scheme != null && this.m_scheme.equals(testURI.m_scheme))) && ((this.m_userinfo == null && testURI.m_userinfo == null) || (this.m_userinfo != null && testURI.m_userinfo != null && this.m_userinfo.equals(testURI.m_userinfo))) && ((this.m_host == null && testURI.m_host == null) || (this.m_host != null && testURI.m_host != null && this.m_host.equals(testURI.m_host))) && this.m_port == testURI.m_port && ((this.m_path == null && testURI.m_path == null) || (this.m_path != null && testURI.m_path != null && this.m_path.equals(testURI.m_path))) && ((this.m_queryString == null && testURI.m_queryString == null) || (this.m_queryString != null && testURI.m_queryString != null && this.m_queryString.equals(testURI.m_queryString))) && ((this.m_fragment == null && testURI.m_fragment == null) || (this.m_fragment != null && testURI.m_fragment != null && this.m_fragment.equals(testURI.m_fragment)))) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.m_scheme);
        hash = 47 * hash + Objects.hashCode(this.m_userinfo);
        hash = 47 * hash + Objects.hashCode(this.m_host);
        hash = 47 * hash + this.m_port;
        hash = 47 * hash + Objects.hashCode(this.m_path);
        hash = 47 * hash + Objects.hashCode(this.m_queryString);
        hash = 47 * hash + Objects.hashCode(this.m_fragment);
        return hash;
    }
    
    @Override
    public String toString() {
        final StringBuilder uriSpecString = new StringBuilder();
        if (this.m_scheme != null) {
            uriSpecString.append(this.m_scheme);
            uriSpecString.append(':');
        }
        uriSpecString.append(this.getSchemeSpecificPart());
        return uriSpecString.toString();
    }
    
    public boolean isGenericURI() {
        return this.m_host != null;
    }
    
    public boolean isAbsoluteURI() {
        return this.m_scheme != null;
    }
    
    public static boolean isConformantSchemeName(final String p_scheme) {
        if (p_scheme == null || p_scheme.trim().length() == 0) {
            return false;
        }
        if (!isAlpha(p_scheme.charAt(0))) {
            return false;
        }
        for (int schemeLength = p_scheme.length(), i = 1; i < schemeLength; ++i) {
            final char testChar = p_scheme.charAt(i);
            if (!isSchemeCharacter(testChar)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isWellFormedAddress(final String address) {
        if (address == null) {
            return false;
        }
        final int addrLength = address.length();
        if (addrLength == 0) {
            return false;
        }
        if (address.startsWith("[")) {
            return isWellFormedIPv6Reference(address);
        }
        if (address.startsWith(".") || address.startsWith("-") || address.endsWith("-")) {
            return false;
        }
        int index = address.lastIndexOf(46);
        if (address.endsWith(".")) {
            index = address.substring(0, index).lastIndexOf(46);
        }
        if (index + 1 < addrLength && isDigit(address.charAt(index + 1))) {
            return isWellFormedIPv4Address(address);
        }
        if (addrLength > 255) {
            return false;
        }
        int labelCharCount = 0;
        for (int i = 0; i < addrLength; ++i) {
            final char testChar = address.charAt(i);
            if (testChar == '.') {
                if (!isAlphanum(address.charAt(i - 1))) {
                    return false;
                }
                if (i + 1 < addrLength && !isAlphanum(address.charAt(i + 1))) {
                    return false;
                }
                labelCharCount = 0;
            }
            else {
                if (!isAlphanum(testChar) && testChar != '-') {
                    return false;
                }
                if (++labelCharCount > 63) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean isWellFormedIPv4Address(final String address) {
        final int addrLength = address.length();
        int numDots = 0;
        int numDigits = 0;
        for (int i = 0; i < addrLength; ++i) {
            final char testChar = address.charAt(i);
            if (testChar == '.') {
                if ((i > 0 && !isDigit(address.charAt(i - 1))) || (i + 1 < addrLength && !isDigit(address.charAt(i + 1)))) {
                    return false;
                }
                numDigits = 0;
                if (++numDots > 3) {
                    return false;
                }
            }
            else {
                if (!isDigit(testChar)) {
                    return false;
                }
                if (++numDigits > 3) {
                    return false;
                }
                if (numDigits == 3) {
                    final char first = address.charAt(i - 2);
                    final char second = address.charAt(i - 1);
                    if (first >= '2' && (first != '2' || (second >= '5' && (second != '5' || testChar > '5')))) {
                        return false;
                    }
                }
            }
        }
        return numDots == 3;
    }
    
    public static boolean isWellFormedIPv6Reference(final String address) {
        final int addrLength = address.length();
        int index = 1;
        final int end = addrLength - 1;
        if (addrLength <= 2 || address.charAt(0) != '[' || address.charAt(end) != ']') {
            return false;
        }
        final int[] counter = { 0 };
        index = scanHexSequence(address, index, end, counter);
        if (index == -1) {
            return false;
        }
        if (index == end) {
            return counter[0] == 8;
        }
        if (index + 1 >= end || address.charAt(index) != ':') {
            return false;
        }
        if (address.charAt(index + 1) != ':') {
            return counter[0] == 6 && isWellFormedIPv4Address(address.substring(index + 1, end));
        }
        if (++counter[0] > 8) {
            return false;
        }
        index += 2;
        if (index == end) {
            return true;
        }
        final int prevCount = counter[0];
        index = scanHexSequence(address, index, end, counter);
        return index == end || (index != -1 && isWellFormedIPv4Address(address.substring((counter[0] > prevCount) ? (index + 1) : index, end)));
    }
    
    private static int scanHexSequence(final String address, int index, final int end, final int[] counter) {
        int numDigits = 0;
        final int start = index;
        while (index < end) {
            final char testChar = address.charAt(index);
            if (testChar == ':') {
                if (numDigits > 0 && ++counter[0] > 8) {
                    return -1;
                }
                if (numDigits == 0 || (index + 1 < end && address.charAt(index + 1) == ':')) {
                    return index;
                }
                numDigits = 0;
            }
            else if (!isHex(testChar)) {
                if (testChar == '.' && numDigits < 4 && numDigits > 0 && counter[0] <= 6) {
                    final int back = index - numDigits - 1;
                    return (back >= start) ? back : (back + 1);
                }
                return -1;
            }
            else if (++numDigits > 4) {
                return -1;
            }
            ++index;
        }
        return (numDigits > 0 && ++counter[0] <= 8) ? end : -1;
    }
    
    private static boolean isDigit(final char p_char) {
        return p_char >= '0' && p_char <= '9';
    }
    
    private static boolean isHex(final char p_char) {
        return p_char <= 'f' && (URI.fgLookupTable[p_char] & 0x40) != 0x0;
    }
    
    private static boolean isAlpha(final char p_char) {
        return (p_char >= 'a' && p_char <= 'z') || (p_char >= 'A' && p_char <= 'Z');
    }
    
    private static boolean isAlphanum(final char p_char) {
        return p_char <= 'z' && (URI.fgLookupTable[p_char] & 0x30) != 0x0;
    }
    
    private static boolean isReservedCharacter(final char p_char) {
        return p_char <= ']' && (URI.fgLookupTable[p_char] & 0x1) != 0x0;
    }
    
    private static boolean isUnreservedCharacter(final char p_char) {
        return p_char <= '~' && (URI.fgLookupTable[p_char] & 0x32) != 0x0;
    }
    
    private static boolean isURICharacter(final char p_char) {
        return p_char <= '~' && (URI.fgLookupTable[p_char] & 0x33) != 0x0;
    }
    
    private static boolean isSchemeCharacter(final char p_char) {
        return p_char <= 'z' && (URI.fgLookupTable[p_char] & 0x34) != 0x0;
    }
    
    private static boolean isUserinfoCharacter(final char p_char) {
        return p_char <= 'z' && (URI.fgLookupTable[p_char] & 0x3A) != 0x0;
    }
    
    private static boolean isPathCharacter(final char p_char) {
        return p_char <= '~' && (URI.fgLookupTable[p_char] & 0xB2) != 0x0;
    }
    
    private static boolean isURIString(final String p_uric) {
        if (p_uric == null) {
            return false;
        }
        final int end = p_uric.length();
        char testChar = '\0';
        for (int i = 0; i < end; ++i) {
            testChar = p_uric.charAt(i);
            if (testChar == '%') {
                if (i + 2 >= end || !isHex(p_uric.charAt(i + 1)) || !isHex(p_uric.charAt(i + 2))) {
                    return false;
                }
                i += 2;
            }
            else if (!isURICharacter(testChar)) {
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
        for (int i = 65; i <= 70; ++i) {
            final byte[] fgLookupTable3 = URI.fgLookupTable;
            final int n2 = i;
            fgLookupTable3[n2] |= 0x50;
            final byte[] fgLookupTable4 = URI.fgLookupTable;
            final int n3 = i + 32;
            fgLookupTable4[n3] |= 0x50;
        }
        for (int i = 71; i <= 90; ++i) {
            final byte[] fgLookupTable5 = URI.fgLookupTable;
            final int n4 = i;
            fgLookupTable5[n4] |= 0x10;
            final byte[] fgLookupTable6 = URI.fgLookupTable;
            final int n5 = i + 32;
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
        URI.DEBUG = false;
    }
    
    public static class MalformedURIException extends IOException
    {
        static final long serialVersionUID = -6695054834342951930L;
        
        public MalformedURIException() {
        }
        
        public MalformedURIException(final String p_msg) {
            super(p_msg);
        }
    }
}
