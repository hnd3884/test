package com.sun.xml.internal.messaging.saaj.util;

import java.io.IOException;
import java.io.Serializable;

public class JaxmURI implements Serializable
{
    private static final String RESERVED_CHARACTERS = ";/?:@&=+$,";
    private static final String MARK_CHARACTERS = "-_.!~*'() ";
    private static final String SCHEME_CHARACTERS = "+-.";
    private static final String USERINFO_CHARACTERS = ";:&=+$,";
    private String m_scheme;
    private String m_userinfo;
    private String m_host;
    private int m_port;
    private String m_path;
    private String m_queryString;
    private String m_fragment;
    private static boolean DEBUG;
    
    public JaxmURI() {
        this.m_scheme = null;
        this.m_userinfo = null;
        this.m_host = null;
        this.m_port = -1;
        this.m_path = null;
        this.m_queryString = null;
        this.m_fragment = null;
    }
    
    public JaxmURI(final JaxmURI p_other) {
        this.m_scheme = null;
        this.m_userinfo = null;
        this.m_host = null;
        this.m_port = -1;
        this.m_path = null;
        this.m_queryString = null;
        this.m_fragment = null;
        this.initialize(p_other);
    }
    
    public JaxmURI(final String p_uriSpec) throws MalformedURIException {
        this((JaxmURI)null, p_uriSpec);
    }
    
    public JaxmURI(final JaxmURI p_base, final String p_uriSpec) throws MalformedURIException {
        this.m_scheme = null;
        this.m_userinfo = null;
        this.m_host = null;
        this.m_port = -1;
        this.m_path = null;
        this.m_queryString = null;
        this.m_fragment = null;
        this.initialize(p_base, p_uriSpec);
    }
    
    public JaxmURI(final String p_scheme, final String p_schemeSpecificPart) throws MalformedURIException {
        this.m_scheme = null;
        this.m_userinfo = null;
        this.m_host = null;
        this.m_port = -1;
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
    
    public JaxmURI(final String p_scheme, final String p_host, final String p_path, final String p_queryString, final String p_fragment) throws MalformedURIException {
        this(p_scheme, null, p_host, -1, p_path, p_queryString, p_fragment);
    }
    
    public JaxmURI(final String p_scheme, final String p_userinfo, final String p_host, final int p_port, final String p_path, final String p_queryString, final String p_fragment) throws MalformedURIException {
        this.m_scheme = null;
        this.m_userinfo = null;
        this.m_host = null;
        this.m_port = -1;
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
    
    private void initialize(final JaxmURI p_other) {
        this.m_scheme = p_other.getScheme();
        this.m_userinfo = p_other.getUserinfo();
        this.m_host = p_other.getHost();
        this.m_port = p_other.getPort();
        this.m_path = p_other.getPath();
        this.m_queryString = p_other.getQueryString();
        this.m_fragment = p_other.getFragment();
    }
    
    private void initialize(final JaxmURI p_base, final String p_uriSpec) throws MalformedURIException {
        if (p_base == null && (p_uriSpec == null || p_uriSpec.trim().length() == 0)) {
            throw new MalformedURIException("Cannot initialize URI with empty parameters.");
        }
        if (p_uriSpec == null || p_uriSpec.trim().length() == 0) {
            this.initialize(p_base);
            return;
        }
        final String uriSpec = p_uriSpec.trim();
        final int uriSpecLen = uriSpec.length();
        int index = 0;
        final int colonIdx = uriSpec.indexOf(58);
        final int slashIdx = uriSpec.indexOf(47);
        if (colonIdx < 2 || (colonIdx > slashIdx && slashIdx != -1)) {
            final int fragmentIdx = uriSpec.indexOf(35);
            if (p_base == null && fragmentIdx != 0) {
                throw new MalformedURIException("No scheme found in URI.");
            }
        }
        else {
            this.initializeScheme(uriSpec);
            index = this.m_scheme.length() + 1;
        }
        if (index + 1 < uriSpecLen && uriSpec.substring(index).startsWith("//")) {
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
                this.initializeAuthority(uriSpec.substring(startPos, index));
            }
            else {
                this.m_host = "";
            }
        }
        this.initializePath(uriSpec.substring(index));
        if (p_base != null) {
            if (this.m_path.length() == 0 && this.m_scheme == null && this.m_host == null) {
                this.m_scheme = p_base.getScheme();
                this.m_userinfo = p_base.getUserinfo();
                this.m_host = p_base.getHost();
                this.m_port = p_base.getPort();
                this.m_path = p_base.getPath();
                if (this.m_queryString == null) {
                    this.m_queryString = p_base.getQueryString();
                }
                return;
            }
            if (this.m_scheme != null) {
                return;
            }
            this.m_scheme = p_base.getScheme();
            if (this.m_host != null) {
                return;
            }
            this.m_userinfo = p_base.getUserinfo();
            this.m_host = p_base.getHost();
            this.m_port = p_base.getPort();
            if (this.m_path.length() > 0 && this.m_path.startsWith("/")) {
                return;
            }
            String path = "";
            final String basePath = p_base.getPath();
            if (basePath != null) {
                final int lastSlash = basePath.lastIndexOf(47);
                if (lastSlash != -1) {
                    path = basePath.substring(0, lastSlash + 1);
                }
            }
            for (path = path.concat(this.m_path), index = -1; (index = path.indexOf("/./")) != -1; path = path.substring(0, index + 1).concat(path.substring(index + 3))) {}
            if (path.endsWith("/.")) {
                path = path.substring(0, path.length() - 1);
            }
            index = 1;
            int segIndex = -1;
            String tempString = null;
            while ((index = path.indexOf("/../", index)) > 0) {
                tempString = path.substring(0, path.indexOf("/../"));
                segIndex = tempString.lastIndexOf(47);
                if (segIndex != -1) {
                    if (!tempString.substring(segIndex++).equals("..")) {
                        path = path.substring(0, segIndex).concat(path.substring(index + 4));
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
    
    private void initializeAuthority(final String p_uriSpec) throws MalformedURIException {
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
        while (index < end) {
            testChar = p_uriSpec.charAt(index);
            if (testChar == ':') {
                break;
            }
            ++index;
        }
        host = p_uriSpec.substring(start, index);
        int port = -1;
        if (host.length() > 0 && testChar == ':') {
            for (start = ++index; index < end; ++index) {}
            final String portStr = p_uriSpec.substring(start, index);
            if (portStr.length() > 0) {
                for (int i = 0; i < portStr.length(); ++i) {
                    if (!isDigit(portStr.charAt(i))) {
                        throw new MalformedURIException(portStr + " is invalid. Port should only contain digits!");
                    }
                }
                try {
                    port = Integer.parseInt(portStr);
                }
                catch (final NumberFormatException ex) {}
            }
        }
        this.setHost(host);
        this.setPort(port);
        this.setUserinfo(userinfo);
    }
    
    private void initializePath(final String p_uriSpec) throws MalformedURIException {
        if (p_uriSpec == null) {
            throw new MalformedURIException("Cannot initialize path from null string!");
        }
        int index = 0;
        int start = 0;
        final int end = p_uriSpec.length();
        char testChar = '\0';
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
                    throw new MalformedURIException("Path contains invalid escape sequence!");
                }
            }
            else if (!isReservedCharacter(testChar) && !isUnreservedCharacter(testChar)) {
                throw new MalformedURIException("Path contains invalid character: " + testChar);
            }
            ++index;
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
                }
                else if (!isReservedCharacter(testChar) && !isUnreservedCharacter(testChar)) {
                    throw new MalformedURIException("Query string contains invalid character:" + testChar);
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
                }
                else if (!isReservedCharacter(testChar) && !isUnreservedCharacter(testChar)) {
                    throw new MalformedURIException("Fragment contains invalid character:" + testChar);
                }
            }
            this.m_fragment = p_uriSpec.substring(start, index);
        }
    }
    
    public String getScheme() {
        return this.m_scheme;
    }
    
    public String getSchemeSpecificPart() {
        final StringBuffer schemespec = new StringBuffer();
        if (this.m_userinfo != null || this.m_host != null || this.m_port != -1) {
            schemespec.append("//");
        }
        if (this.m_userinfo != null) {
            schemespec.append(this.m_userinfo);
            schemespec.append('@');
        }
        if (this.m_host != null) {
            schemespec.append(this.m_host);
        }
        if (this.m_port != -1) {
            schemespec.append(':');
            schemespec.append(this.m_port);
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
    
    public String getPath(final boolean p_includeQueryString, final boolean p_includeFragment) {
        final StringBuffer pathString = new StringBuffer(this.m_path);
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
        }
        else {
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
                else if (!isUnreservedCharacter(testChar) && ";:&=+$,".indexOf(testChar) == -1) {
                    throw new MalformedURIException("Userinfo contains invalid character:" + testChar);
                }
                ++index;
            }
        }
        this.m_userinfo = p_userinfo;
    }
    
    public void setHost(final String p_host) throws MalformedURIException {
        if (p_host == null || p_host.trim().length() == 0) {
            this.m_host = p_host;
            this.m_userinfo = null;
            this.m_port = -1;
        }
        else if (!isWellFormedAddress(p_host)) {
            throw new MalformedURIException("Host is not a well formed address!");
        }
        this.m_host = p_host;
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
    
    public void setPath(final String p_path) throws MalformedURIException {
        if (p_path == null) {
            this.m_path = null;
            this.m_queryString = null;
            this.m_fragment = null;
        }
        else {
            this.initializePath(p_path);
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
        if (p_test instanceof JaxmURI) {
            final JaxmURI testURI = (JaxmURI)p_test;
            if (((this.m_scheme == null && testURI.m_scheme == null) || (this.m_scheme != null && testURI.m_scheme != null && this.m_scheme.equals(testURI.m_scheme))) && ((this.m_userinfo == null && testURI.m_userinfo == null) || (this.m_userinfo != null && testURI.m_userinfo != null && this.m_userinfo.equals(testURI.m_userinfo))) && ((this.m_host == null && testURI.m_host == null) || (this.m_host != null && testURI.m_host != null && this.m_host.equals(testURI.m_host))) && this.m_port == testURI.m_port && ((this.m_path == null && testURI.m_path == null) || (this.m_path != null && testURI.m_path != null && this.m_path.equals(testURI.m_path))) && ((this.m_queryString == null && testURI.m_queryString == null) || (this.m_queryString != null && testURI.m_queryString != null && this.m_queryString.equals(testURI.m_queryString))) && ((this.m_fragment == null && testURI.m_fragment == null) || (this.m_fragment != null && testURI.m_fragment != null && this.m_fragment.equals(testURI.m_fragment)))) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return 153214;
    }
    
    @Override
    public String toString() {
        final StringBuffer uriSpecString = new StringBuffer();
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
    
    public static boolean isConformantSchemeName(final String p_scheme) {
        if (p_scheme == null || p_scheme.trim().length() == 0) {
            return false;
        }
        if (!isAlpha(p_scheme.charAt(0))) {
            return false;
        }
        for (int i = 1; i < p_scheme.length(); ++i) {
            final char testChar = p_scheme.charAt(i);
            if (!isAlphanum(testChar) && "+-.".indexOf(testChar) == -1) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isWellFormedAddress(final String p_address) {
        if (p_address == null) {
            return false;
        }
        final String address = p_address.trim();
        final int addrLength = address.length();
        if (addrLength == 0 || addrLength > 255) {
            return false;
        }
        if (address.startsWith(".") || address.startsWith("-")) {
            return false;
        }
        int index = address.lastIndexOf(46);
        if (address.endsWith(".")) {
            index = address.substring(0, index).lastIndexOf(46);
        }
        if (index + 1 < addrLength && isDigit(p_address.charAt(index + 1))) {
            int numDots = 0;
            for (int i = 0; i < addrLength; ++i) {
                final char testChar = address.charAt(i);
                if (testChar == '.') {
                    if (!isDigit(address.charAt(i - 1)) || (i + 1 < addrLength && !isDigit(address.charAt(i + 1)))) {
                        return false;
                    }
                    ++numDots;
                }
                else if (!isDigit(testChar)) {
                    return false;
                }
            }
            if (numDots != 3) {
                return false;
            }
        }
        else {
            for (int j = 0; j < addrLength; ++j) {
                final char testChar = address.charAt(j);
                if (testChar == '.') {
                    if (!isAlphanum(address.charAt(j - 1))) {
                        return false;
                    }
                    if (j + 1 < addrLength && !isAlphanum(address.charAt(j + 1))) {
                        return false;
                    }
                }
                else if (!isAlphanum(testChar) && testChar != '-') {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static boolean isDigit(final char p_char) {
        return p_char >= '0' && p_char <= '9';
    }
    
    private static boolean isHex(final char p_char) {
        return isDigit(p_char) || (p_char >= 'a' && p_char <= 'f') || (p_char >= 'A' && p_char <= 'F');
    }
    
    private static boolean isAlpha(final char p_char) {
        return (p_char >= 'a' && p_char <= 'z') || (p_char >= 'A' && p_char <= 'Z');
    }
    
    private static boolean isAlphanum(final char p_char) {
        return isAlpha(p_char) || isDigit(p_char);
    }
    
    private static boolean isReservedCharacter(final char p_char) {
        return ";/?:@&=+$,".indexOf(p_char) != -1;
    }
    
    private static boolean isUnreservedCharacter(final char p_char) {
        return isAlphanum(p_char) || "-_.!~*'() ".indexOf(p_char) != -1;
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
            else if (!isReservedCharacter(testChar)) {
                if (!isUnreservedCharacter(testChar)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    static {
        JaxmURI.DEBUG = false;
    }
    
    public static class MalformedURIException extends IOException
    {
        public MalformedURIException() {
        }
        
        public MalformedURIException(final String p_msg) {
            super(p_msg);
        }
    }
}
