package com.unboundid.ldap.sdk;

import com.unboundid.util.ByteStringBuffer;
import java.util.ArrayList;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPURL implements Serializable
{
    private static final Filter DEFAULT_FILTER;
    public static final int DEFAULT_LDAP_PORT = 389;
    public static final int DEFAULT_LDAPS_PORT = 636;
    public static final int DEFAULT_LDAPI_PORT = 0;
    private static final SearchScope DEFAULT_SCOPE;
    private static final DN DEFAULT_BASE_DN;
    private static final String[] DEFAULT_ATTRIBUTES;
    private static final long serialVersionUID = 3420786933570240493L;
    private final boolean attributesProvided;
    private final boolean baseDNProvided;
    private final boolean filterProvided;
    private final boolean portProvided;
    private final boolean scopeProvided;
    private final DN baseDN;
    private final Filter filter;
    private final int port;
    private final SearchScope scope;
    private final String host;
    private volatile String normalizedURLString;
    private final String scheme;
    private final String urlString;
    private final String[] attributes;
    
    public LDAPURL(final String urlString) throws LDAPException {
        Validator.ensureNotNull(urlString);
        this.urlString = urlString;
        final int colonPos = urlString.indexOf("://");
        if (colonPos < 0) {
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_NO_COLON_SLASHES.get());
        }
        this.scheme = StaticUtils.toLowerCase(urlString.substring(0, colonPos));
        int defaultPort;
        if (this.scheme.equals("ldap")) {
            defaultPort = 389;
        }
        else if (this.scheme.equals("ldaps")) {
            defaultPort = 636;
        }
        else {
            if (!this.scheme.equals("ldapi")) {
                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_INVALID_SCHEME.get(this.scheme));
            }
            defaultPort = 0;
        }
        final int slashPos = urlString.indexOf(47, colonPos + 3);
        if (slashPos < 0) {
            this.baseDN = LDAPURL.DEFAULT_BASE_DN;
            this.baseDNProvided = false;
            this.attributes = LDAPURL.DEFAULT_ATTRIBUTES;
            this.attributesProvided = false;
            this.scope = LDAPURL.DEFAULT_SCOPE;
            this.scopeProvided = false;
            this.filter = LDAPURL.DEFAULT_FILTER;
            this.filterProvided = false;
            final String hostPort = urlString.substring(colonPos + 3);
            final StringBuilder hostBuffer = new StringBuilder(hostPort.length());
            final int portValue = decodeHostPort(hostPort, hostBuffer);
            if (portValue < 0) {
                this.port = defaultPort;
                this.portProvided = false;
            }
            else {
                this.port = portValue;
                this.portProvided = true;
            }
            if (hostBuffer.length() == 0) {
                this.host = null;
            }
            else {
                this.host = hostBuffer.toString();
            }
            return;
        }
        final String hostPort = urlString.substring(colonPos + 3, slashPos);
        final StringBuilder hostBuffer = new StringBuilder(hostPort.length());
        final int portValue = decodeHostPort(hostPort, hostBuffer);
        if (portValue < 0) {
            this.port = defaultPort;
            this.portProvided = false;
        }
        else {
            this.port = portValue;
            this.portProvided = true;
        }
        if (hostBuffer.length() == 0) {
            this.host = null;
        }
        else {
            this.host = hostBuffer.toString();
        }
        final int questionMarkPos = urlString.indexOf(63, slashPos + 1);
        if (questionMarkPos < 0) {
            this.attributes = LDAPURL.DEFAULT_ATTRIBUTES;
            this.attributesProvided = false;
            this.scope = LDAPURL.DEFAULT_SCOPE;
            this.scopeProvided = false;
            this.filter = LDAPURL.DEFAULT_FILTER;
            this.filterProvided = false;
            this.baseDN = new DN(percentDecode(urlString.substring(slashPos + 1)));
            this.baseDNProvided = !this.baseDN.isNullDN();
            return;
        }
        this.baseDN = new DN(percentDecode(urlString.substring(slashPos + 1, questionMarkPos)));
        this.baseDNProvided = !this.baseDN.isNullDN();
        final int questionMark2Pos = urlString.indexOf(63, questionMarkPos + 1);
        if (questionMark2Pos < 0) {
            this.scope = LDAPURL.DEFAULT_SCOPE;
            this.scopeProvided = false;
            this.filter = LDAPURL.DEFAULT_FILTER;
            this.filterProvided = false;
            this.attributes = decodeAttributes(urlString.substring(questionMarkPos + 1));
            this.attributesProvided = (this.attributes.length > 0);
            return;
        }
        this.attributes = decodeAttributes(urlString.substring(questionMarkPos + 1, questionMark2Pos));
        this.attributesProvided = (this.attributes.length > 0);
        final int questionMark3Pos = urlString.indexOf(63, questionMark2Pos + 1);
        if (questionMark3Pos < 0) {
            this.filter = LDAPURL.DEFAULT_FILTER;
            this.filterProvided = false;
            final String scopeStr = StaticUtils.toLowerCase(urlString.substring(questionMark2Pos + 1));
            if (scopeStr.isEmpty()) {
                this.scope = SearchScope.BASE;
                this.scopeProvided = false;
            }
            else if (scopeStr.equals("base")) {
                this.scope = SearchScope.BASE;
                this.scopeProvided = true;
            }
            else if (scopeStr.equals("one")) {
                this.scope = SearchScope.ONE;
                this.scopeProvided = true;
            }
            else if (scopeStr.equals("sub")) {
                this.scope = SearchScope.SUB;
                this.scopeProvided = true;
            }
            else {
                if (!scopeStr.equals("subord") && !scopeStr.equals("subordinates")) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_INVALID_SCOPE.get(scopeStr));
                }
                this.scope = SearchScope.SUBORDINATE_SUBTREE;
                this.scopeProvided = true;
            }
            return;
        }
        final String scopeStr = StaticUtils.toLowerCase(urlString.substring(questionMark2Pos + 1, questionMark3Pos));
        if (scopeStr.isEmpty()) {
            this.scope = SearchScope.BASE;
            this.scopeProvided = false;
        }
        else if (scopeStr.equals("base")) {
            this.scope = SearchScope.BASE;
            this.scopeProvided = true;
        }
        else if (scopeStr.equals("one")) {
            this.scope = SearchScope.ONE;
            this.scopeProvided = true;
        }
        else if (scopeStr.equals("sub")) {
            this.scope = SearchScope.SUB;
            this.scopeProvided = true;
        }
        else {
            if (!scopeStr.equals("subord") && !scopeStr.equals("subordinates")) {
                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_INVALID_SCOPE.get(scopeStr));
            }
            this.scope = SearchScope.SUBORDINATE_SUBTREE;
            this.scopeProvided = true;
        }
        final String filterStr = percentDecode(urlString.substring(questionMark3Pos + 1));
        if (filterStr.isEmpty()) {
            this.filter = LDAPURL.DEFAULT_FILTER;
            this.filterProvided = false;
        }
        else {
            this.filter = Filter.create(filterStr);
            this.filterProvided = true;
        }
    }
    
    public LDAPURL(final String scheme, final String host, final Integer port, final DN baseDN, final String[] attributes, final SearchScope scope, final Filter filter) throws LDAPException {
        Validator.ensureNotNull(scheme);
        final StringBuilder buffer = new StringBuilder();
        this.scheme = StaticUtils.toLowerCase(scheme);
        int defaultPort;
        if (scheme.equals("ldap")) {
            defaultPort = 389;
        }
        else if (scheme.equals("ldaps")) {
            defaultPort = 636;
        }
        else {
            if (!scheme.equals("ldapi")) {
                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_INVALID_SCHEME.get(scheme));
            }
            defaultPort = 0;
        }
        buffer.append(scheme);
        buffer.append("://");
        if (host == null || host.isEmpty()) {
            this.host = null;
        }
        else {
            buffer.append(this.host = host);
        }
        if (port == null) {
            this.port = defaultPort;
            this.portProvided = false;
        }
        else {
            this.port = port;
            this.portProvided = true;
            buffer.append(':');
            buffer.append(port);
            if (port < 1 || port > 65535) {
                throw new LDAPException(ResultCode.PARAM_ERROR, LDAPMessages.ERR_LDAPURL_INVALID_PORT.get(port));
            }
        }
        buffer.append('/');
        if (baseDN == null) {
            this.baseDN = LDAPURL.DEFAULT_BASE_DN;
            this.baseDNProvided = false;
        }
        else {
            this.baseDN = baseDN;
            this.baseDNProvided = true;
            percentEncode(baseDN.toString(), buffer);
        }
        final boolean continueAppending = (attributes != null && attributes.length != 0) || scope != null || filter != null;
        if (continueAppending) {
            buffer.append('?');
        }
        if (attributes == null || attributes.length == 0) {
            this.attributes = LDAPURL.DEFAULT_ATTRIBUTES;
            this.attributesProvided = false;
        }
        else {
            this.attributes = attributes;
            this.attributesProvided = true;
            for (int i = 0; i < attributes.length; ++i) {
                if (i > 0) {
                    buffer.append(',');
                }
                buffer.append(attributes[i]);
            }
        }
        if (continueAppending) {
            buffer.append('?');
        }
        if (scope == null) {
            this.scope = LDAPURL.DEFAULT_SCOPE;
            this.scopeProvided = false;
        }
        else {
            switch (scope.intValue()) {
                case 0: {
                    this.scope = scope;
                    this.scopeProvided = true;
                    buffer.append("base");
                    break;
                }
                case 1: {
                    this.scope = scope;
                    this.scopeProvided = true;
                    buffer.append("one");
                    break;
                }
                case 2: {
                    this.scope = scope;
                    this.scopeProvided = true;
                    buffer.append("sub");
                    break;
                }
                case 3: {
                    this.scope = scope;
                    this.scopeProvided = true;
                    buffer.append("subordinates");
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.PARAM_ERROR, LDAPMessages.ERR_LDAPURL_INVALID_SCOPE_VALUE.get(scope));
                }
            }
        }
        if (continueAppending) {
            buffer.append('?');
        }
        if (filter == null) {
            this.filter = LDAPURL.DEFAULT_FILTER;
            this.filterProvided = false;
        }
        else {
            this.filter = filter;
            this.filterProvided = true;
            percentEncode(filter.toString(), buffer);
        }
        this.urlString = buffer.toString();
    }
    
    private static int decodeHostPort(final String hostPort, final StringBuilder hostBuffer) throws LDAPException {
        final int length = hostPort.length();
        if (length == 0) {
            return -1;
        }
        if (hostPort.charAt(0) == '[') {
            final int closingBracketPos = hostPort.indexOf(93);
            if (closingBracketPos < 0) {
                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_IPV6_HOST_MISSING_BRACKET.get());
            }
            hostBuffer.append(hostPort.substring(1, closingBracketPos).trim());
            if (hostBuffer.length() == 0) {
                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_IPV6_HOST_EMPTY.get());
            }
            if (closingBracketPos == length - 1) {
                return -1;
            }
            if (hostPort.charAt(closingBracketPos + 1) != ':') {
                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_IPV6_HOST_UNEXPECTED_CHAR.get(hostPort.charAt(closingBracketPos + 1)));
            }
            try {
                final int decodedPort = Integer.parseInt(hostPort.substring(closingBracketPos + 2));
                if (decodedPort >= 1 && decodedPort <= 65535) {
                    return decodedPort;
                }
                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_INVALID_PORT.get(decodedPort));
            }
            catch (final NumberFormatException nfe) {
                Debug.debugException(nfe);
                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_PORT_NOT_INT.get(hostPort), nfe);
            }
        }
        final int colonPos = hostPort.indexOf(58);
        if (colonPos < 0) {
            hostBuffer.append(hostPort);
            return -1;
        }
        try {
            final int decodedPort = Integer.parseInt(hostPort.substring(colonPos + 1));
            if (decodedPort >= 1 && decodedPort <= 65535) {
                hostBuffer.append(hostPort.substring(0, colonPos));
                return decodedPort;
            }
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_INVALID_PORT.get(decodedPort));
        }
        catch (final NumberFormatException nfe) {
            Debug.debugException(nfe);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_PORT_NOT_INT.get(hostPort), nfe);
        }
    }
    
    private static String[] decodeAttributes(final String s) throws LDAPException {
        final int length = s.length();
        if (length == 0) {
            return LDAPURL.DEFAULT_ATTRIBUTES;
        }
        final ArrayList<String> attrList = new ArrayList<String>(10);
        int startPos = 0;
        while (startPos < length) {
            final int commaPos = s.indexOf(44, startPos);
            if (commaPos < 0) {
                final String attrName = s.substring(startPos).trim();
                if (!attrName.isEmpty()) {
                    attrList.add(attrName);
                    break;
                }
                if (attrList.isEmpty()) {
                    return LDAPURL.DEFAULT_ATTRIBUTES;
                }
                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_ATTRLIST_ENDS_WITH_COMMA.get());
            }
            else {
                final String attrName = s.substring(startPos, commaPos).trim();
                if (attrName.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_ATTRLIST_EMPTY_ATTRIBUTE.get());
                }
                attrList.add(attrName);
                startPos = commaPos + 1;
                if (startPos >= length) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_ATTRLIST_ENDS_WITH_COMMA.get());
                }
                continue;
            }
        }
        final String[] attributes = new String[attrList.size()];
        attrList.toArray(attributes);
        return attributes;
    }
    
    public static String percentDecode(final String s) throws LDAPException {
        int firstPercentPos = -1;
        final int length = s.length();
        for (int i = 0; i < length; ++i) {
            if (s.charAt(i) == '%') {
                firstPercentPos = i;
                break;
            }
        }
        if (firstPercentPos < 0) {
            return s;
        }
        int pos = firstPercentPos;
        final ByteStringBuffer buffer = new ByteStringBuffer(2 * length);
        buffer.append((CharSequence)s.substring(0, firstPercentPos));
        while (pos < length) {
            final char c = s.charAt(pos++);
            if (c == '%') {
                if (pos >= length) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_HEX_STRING_TOO_SHORT.get(s));
                }
                byte b = 0;
                switch (s.charAt(pos++)) {
                    case '0': {
                        b = 0;
                        break;
                    }
                    case '1': {
                        b = 16;
                        break;
                    }
                    case '2': {
                        b = 32;
                        break;
                    }
                    case '3': {
                        b = 48;
                        break;
                    }
                    case '4': {
                        b = 64;
                        break;
                    }
                    case '5': {
                        b = 80;
                        break;
                    }
                    case '6': {
                        b = 96;
                        break;
                    }
                    case '7': {
                        b = 112;
                        break;
                    }
                    case '8': {
                        b = -128;
                        break;
                    }
                    case '9': {
                        b = -112;
                        break;
                    }
                    case 'A':
                    case 'a': {
                        b = -96;
                        break;
                    }
                    case 'B':
                    case 'b': {
                        b = -80;
                        break;
                    }
                    case 'C':
                    case 'c': {
                        b = -64;
                        break;
                    }
                    case 'D':
                    case 'd': {
                        b = -48;
                        break;
                    }
                    case 'E':
                    case 'e': {
                        b = -32;
                        break;
                    }
                    case 'F':
                    case 'f': {
                        b = -16;
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_INVALID_HEX_CHAR.get(s.charAt(pos - 1)));
                    }
                }
                if (pos >= length) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_HEX_STRING_TOO_SHORT.get(s));
                }
                switch (s.charAt(pos++)) {
                    case '0': {
                        buffer.append(b);
                        continue;
                    }
                    case '1': {
                        buffer.append((byte)(b | 0x1));
                        continue;
                    }
                    case '2': {
                        buffer.append((byte)(b | 0x2));
                        continue;
                    }
                    case '3': {
                        buffer.append((byte)(b | 0x3));
                        continue;
                    }
                    case '4': {
                        buffer.append((byte)(b | 0x4));
                        continue;
                    }
                    case '5': {
                        buffer.append((byte)(b | 0x5));
                        continue;
                    }
                    case '6': {
                        buffer.append((byte)(b | 0x6));
                        continue;
                    }
                    case '7': {
                        buffer.append((byte)(b | 0x7));
                        continue;
                    }
                    case '8': {
                        buffer.append((byte)(b | 0x8));
                        continue;
                    }
                    case '9': {
                        buffer.append((byte)(b | 0x9));
                        continue;
                    }
                    case 'A':
                    case 'a': {
                        buffer.append((byte)(b | 0xA));
                        continue;
                    }
                    case 'B':
                    case 'b': {
                        buffer.append((byte)(b | 0xB));
                        continue;
                    }
                    case 'C':
                    case 'c': {
                        buffer.append((byte)(b | 0xC));
                        continue;
                    }
                    case 'D':
                    case 'd': {
                        buffer.append((byte)(b | 0xD));
                        continue;
                    }
                    case 'E':
                    case 'e': {
                        buffer.append((byte)(b | 0xE));
                        continue;
                    }
                    case 'F':
                    case 'f': {
                        buffer.append((byte)(b | 0xF));
                        continue;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_LDAPURL_INVALID_HEX_CHAR.get(s.charAt(pos - 1)));
                    }
                }
            }
            else {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }
    
    private static void percentEncode(final String s, final StringBuilder buffer) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char c = s.charAt(i);
            switch (c) {
                case '!':
                case '$':
                case '&':
                case '\'':
                case '(':
                case ')':
                case '*':
                case '+':
                case ',':
                case '-':
                case '.':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case ';':
                case '=':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '_':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                case '~': {
                    buffer.append(c);
                    break;
                }
                default: {
                    final byte[] arr$;
                    final byte[] charBytes = arr$ = StaticUtils.getBytes(new String(new char[] { c }));
                    for (final byte b : arr$) {
                        buffer.append('%');
                        StaticUtils.toHex(b, buffer);
                    }
                    break;
                }
            }
        }
    }
    
    public String getScheme() {
        return this.scheme;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public boolean hostProvided() {
        return this.host != null;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public boolean portProvided() {
        return this.portProvided;
    }
    
    public DN getBaseDN() {
        return this.baseDN;
    }
    
    public boolean baseDNProvided() {
        return this.baseDNProvided;
    }
    
    public String[] getAttributes() {
        return this.attributes;
    }
    
    public boolean attributesProvided() {
        return this.attributesProvided;
    }
    
    public SearchScope getScope() {
        return this.scope;
    }
    
    public boolean scopeProvided() {
        return this.scopeProvided;
    }
    
    public Filter getFilter() {
        return this.filter;
    }
    
    public boolean filterProvided() {
        return this.filterProvided;
    }
    
    public SearchRequest toSearchRequest() {
        return new SearchRequest(this.baseDN.toString(), this.scope, this.filter, this.attributes);
    }
    
    @Override
    public int hashCode() {
        return this.toNormalizedString().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof LDAPURL)) {
            return false;
        }
        final LDAPURL url = (LDAPURL)o;
        return this.toNormalizedString().equals(url.toNormalizedString());
    }
    
    @Override
    public String toString() {
        return this.urlString;
    }
    
    public String toNormalizedString() {
        if (this.normalizedURLString == null) {
            final StringBuilder buffer = new StringBuilder();
            this.toNormalizedString(buffer);
            this.normalizedURLString = buffer.toString();
        }
        return this.normalizedURLString;
    }
    
    public void toNormalizedString(final StringBuilder buffer) {
        buffer.append(this.scheme);
        buffer.append("://");
        if (this.host != null) {
            if (this.host.indexOf(58) >= 0) {
                buffer.append('[');
                buffer.append(StaticUtils.toLowerCase(this.host));
                buffer.append(']');
            }
            else {
                buffer.append(StaticUtils.toLowerCase(this.host));
            }
        }
        if (!this.scheme.equals("ldapi")) {
            buffer.append(':');
            buffer.append(this.port);
        }
        buffer.append('/');
        percentEncode(this.baseDN.toNormalizedString(), buffer);
        buffer.append('?');
        for (int i = 0; i < this.attributes.length; ++i) {
            if (i > 0) {
                buffer.append(',');
            }
            buffer.append(StaticUtils.toLowerCase(this.attributes[i]));
        }
        buffer.append('?');
        switch (this.scope.intValue()) {
            case 0: {
                buffer.append("base");
                break;
            }
            case 1: {
                buffer.append("one");
                break;
            }
            case 2: {
                buffer.append("sub");
                break;
            }
            case 3: {
                buffer.append("subordinates");
                break;
            }
        }
        buffer.append('?');
        percentEncode(this.filter.toNormalizedString(), buffer);
    }
    
    static {
        DEFAULT_FILTER = Filter.createPresenceFilter("objectClass");
        DEFAULT_SCOPE = SearchScope.BASE;
        DEFAULT_BASE_DN = DN.NULL_DN;
        DEFAULT_ATTRIBUTES = StaticUtils.NO_STRINGS;
    }
}
