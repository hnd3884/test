package org.apache.tomcat.util.http.parser;

import org.apache.juli.logging.LogFactory;
import java.io.IOException;
import java.io.Reader;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.res.StringManager;

public class HttpParser
{
    private static final StringManager SM;
    private static final Log LOGGER;
    private static final int ARRAY_SIZE = 128;
    private static final boolean[] IS_CONTROL;
    private static final boolean[] IS_SEPARATOR;
    private static final boolean[] IS_TOKEN;
    private static final boolean[] IS_HEX;
    private static final boolean[] IS_HTTP_PROTOCOL;
    private static final boolean[] IS_ALPHA;
    private static final boolean[] IS_NUMERIC;
    private static final boolean[] REQUEST_TARGET_ALLOW;
    private static final boolean[] IS_UNRESERVED;
    private static final boolean[] IS_SUBDELIM;
    private static final boolean[] IS_USERINFO;
    private static final boolean[] IS_RELAXABLE;
    private static final HttpParser DEFAULT;
    private final boolean[] isNotRequestTarget;
    private final boolean[] isAbsolutePathRelaxed;
    private final boolean[] isQueryRelaxed;
    
    public HttpParser(final String relaxedPathChars, final String relaxedQueryChars) {
        this.isNotRequestTarget = new boolean[128];
        this.isAbsolutePathRelaxed = new boolean[128];
        this.isQueryRelaxed = new boolean[128];
        for (int i = 0; i < 128; ++i) {
            if ((HttpParser.IS_CONTROL[i] || i == 32 || i == 34 || i == 35 || i == 60 || i == 62 || i == 92 || i == 94 || i == 96 || i == 123 || i == 124 || i == 125) && !HttpParser.REQUEST_TARGET_ALLOW[i]) {
                this.isNotRequestTarget[i] = true;
            }
            if (HttpParser.IS_USERINFO[i] || i == 64 || i == 47 || HttpParser.REQUEST_TARGET_ALLOW[i]) {
                this.isAbsolutePathRelaxed[i] = true;
            }
            if (this.isAbsolutePathRelaxed[i] || i == 63 || HttpParser.REQUEST_TARGET_ALLOW[i]) {
                this.isQueryRelaxed[i] = true;
            }
        }
        this.relax(this.isAbsolutePathRelaxed, relaxedPathChars);
        this.relax(this.isQueryRelaxed, relaxedQueryChars);
    }
    
    public boolean isNotRequestTargetRelaxed(final int c) {
        try {
            return this.isNotRequestTarget[c];
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            return true;
        }
    }
    
    public boolean isAbsolutePathRelaxed(final int c) {
        try {
            return this.isAbsolutePathRelaxed[c];
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }
    
    public boolean isQueryRelaxed(final int c) {
        try {
            return this.isQueryRelaxed[c];
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }
    
    public static String unquote(final String input) {
        if (input == null || input.length() < 2) {
            return input;
        }
        int start;
        int end;
        if (input.charAt(0) == '\"') {
            start = 1;
            end = input.length() - 1;
        }
        else {
            start = 0;
            end = input.length();
        }
        final StringBuilder result = new StringBuilder();
        for (int i = start; i < end; ++i) {
            final char c = input.charAt(i);
            if (input.charAt(i) == '\\') {
                ++i;
                result.append(input.charAt(i));
            }
            else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    public static boolean isToken(final int c) {
        try {
            return HttpParser.IS_TOKEN[c];
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }
    
    public static boolean isHex(final int c) {
        try {
            return HttpParser.IS_HEX[c];
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }
    
    public static boolean isNotRequestTarget(final int c) {
        return HttpParser.DEFAULT.isNotRequestTargetRelaxed(c);
    }
    
    public static boolean isHttpProtocol(final int c) {
        try {
            return HttpParser.IS_HTTP_PROTOCOL[c];
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }
    
    public static boolean isAlpha(final int c) {
        try {
            return HttpParser.IS_ALPHA[c];
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }
    
    public static boolean isNumeric(final int c) {
        try {
            return HttpParser.IS_NUMERIC[c];
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }
    
    public static boolean isUserInfo(final int c) {
        try {
            return HttpParser.IS_USERINFO[c];
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }
    
    private static boolean isRelaxable(final int c) {
        try {
            return HttpParser.IS_RELAXABLE[c];
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }
    
    public static boolean isAbsolutePath(final int c) {
        return HttpParser.DEFAULT.isAbsolutePathRelaxed(c);
    }
    
    public static boolean isQuery(final int c) {
        return HttpParser.DEFAULT.isQueryRelaxed(c);
    }
    
    public static boolean isControl(final int c) {
        try {
            return HttpParser.IS_CONTROL[c];
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }
    
    static int skipLws(final Reader input) throws IOException {
        input.mark(1);
        int c;
        for (c = input.read(); c == 32 || c == 9 || c == 10 || c == 13; c = input.read()) {
            input.mark(1);
        }
        input.reset();
        return c;
    }
    
    static SkipResult skipConstant(final Reader input, final String constant) throws IOException {
        final int len = constant.length();
        skipLws(input);
        input.mark(len);
        int c = input.read();
        for (int i = 0; i < len; ++i) {
            if (i == 0 && c == -1) {
                return SkipResult.EOF;
            }
            if (c != constant.charAt(i)) {
                input.reset();
                return SkipResult.NOT_FOUND;
            }
            if (i != len - 1) {
                c = input.read();
            }
        }
        return SkipResult.FOUND;
    }
    
    static String readToken(final Reader input) throws IOException {
        final StringBuilder result = new StringBuilder();
        skipLws(input);
        input.mark(1);
        int c;
        for (c = input.read(); c != -1 && isToken(c); c = input.read()) {
            result.append((char)c);
            input.mark(1);
        }
        input.reset();
        if (c != -1 && result.length() == 0) {
            return null;
        }
        return result.toString();
    }
    
    static String readDigits(final Reader input) throws IOException {
        final StringBuilder result = new StringBuilder();
        skipLws(input);
        input.mark(1);
        for (int c = input.read(); c != -1 && isNumeric(c); c = input.read()) {
            result.append((char)c);
            input.mark(1);
        }
        input.reset();
        return result.toString();
    }
    
    static long readLong(final Reader input) throws IOException {
        final String digits = readDigits(input);
        if (digits.length() == 0) {
            return -1L;
        }
        return Long.parseLong(digits);
    }
    
    static String readQuotedString(final Reader input, final boolean returnQuoted) throws IOException {
        skipLws(input);
        int c = input.read();
        if (c != 34) {
            return null;
        }
        final StringBuilder result = new StringBuilder();
        if (returnQuoted) {
            result.append('\"');
        }
        for (c = input.read(); c != 34; c = input.read()) {
            if (c == -1) {
                return null;
            }
            if (c == 92) {
                c = input.read();
                if (returnQuoted) {
                    result.append('\\');
                }
                result.append((char)c);
            }
            else {
                result.append((char)c);
            }
        }
        if (returnQuoted) {
            result.append('\"');
        }
        return result.toString();
    }
    
    static String readTokenOrQuotedString(final Reader input, final boolean returnQuoted) throws IOException {
        final int c = skipLws(input);
        if (c == 34) {
            return readQuotedString(input, returnQuoted);
        }
        return readToken(input);
    }
    
    static String readQuotedToken(final Reader input) throws IOException {
        final StringBuilder result = new StringBuilder();
        boolean quoted = false;
        skipLws(input);
        input.mark(1);
        int c = input.read();
        if (c == 34) {
            quoted = true;
        }
        else {
            if (c == -1 || !isToken(c)) {
                return null;
            }
            result.append((char)c);
        }
        input.mark(1);
        for (c = input.read(); c != -1 && isToken(c); c = input.read()) {
            result.append((char)c);
            input.mark(1);
        }
        if (quoted) {
            if (c != 34) {
                return null;
            }
        }
        else {
            input.reset();
        }
        if (c != -1 && result.length() == 0) {
            return null;
        }
        return result.toString();
    }
    
    static String readLhex(final Reader input) throws IOException {
        final StringBuilder result = new StringBuilder();
        boolean quoted = false;
        skipLws(input);
        input.mark(1);
        int c = input.read();
        if (c == 34) {
            quoted = true;
        }
        else {
            if (c == -1 || !isHex(c)) {
                return null;
            }
            if (65 <= c && c <= 70) {
                c += 32;
            }
            result.append((char)c);
        }
        input.mark(1);
        for (c = input.read(); c != -1 && isHex(c); c = input.read()) {
            if (65 <= c && c <= 70) {
                c += 32;
            }
            result.append((char)c);
            input.mark(1);
        }
        if (quoted) {
            if (c != 34) {
                return null;
            }
        }
        else {
            input.reset();
        }
        if (c != -1 && result.length() == 0) {
            return null;
        }
        return result.toString();
    }
    
    static double readWeight(final Reader input, final char delimiter) throws IOException {
        skipLws(input);
        int c = input.read();
        if (c == -1 || c == delimiter) {
            return 1.0;
        }
        if (c != 113) {
            skipUntil(input, c, delimiter);
            return 0.0;
        }
        skipLws(input);
        c = input.read();
        if (c != 61) {
            skipUntil(input, c, delimiter);
            return 0.0;
        }
        skipLws(input);
        c = input.read();
        final StringBuilder value = new StringBuilder(5);
        int decimalPlacesRead = -1;
        if (c != 48 && c != 49) {
            skipUntil(input, c, delimiter);
            return 0.0;
        }
        value.append((char)c);
        c = input.read();
        while (true) {
            if (decimalPlacesRead == -1 && c == 46) {
                value.append('.');
                decimalPlacesRead = 0;
            }
            else {
                if (decimalPlacesRead <= -1 || c < 48 || c > 57) {
                    break;
                }
                if (decimalPlacesRead < 3) {
                    value.append((char)c);
                    ++decimalPlacesRead;
                }
            }
            c = input.read();
        }
        if (c == 9 || c == 32) {
            skipLws(input);
            c = input.read();
        }
        if (c != delimiter && c != -1) {
            skipUntil(input, c, delimiter);
            return 0.0;
        }
        final double result = Double.parseDouble(value.toString());
        if (result > 1.0) {
            return 0.0;
        }
        return result;
    }
    
    static int readHostIPv4(final Reader reader, final boolean inIPv6) throws IOException {
        int octet = -1;
        int octetCount = 1;
        int pos = 0;
        reader.mark(1);
        while (true) {
            final int c = reader.read();
            if (c == 46) {
                if (octet > -1 && octet < 256) {
                    ++octetCount;
                    octet = -1;
                }
                else {
                    if (inIPv6 || octet == -1) {
                        throw new IllegalArgumentException(HttpParser.SM.getString("http.invalidOctet", new Object[] { Integer.toString(octet) }));
                    }
                    reader.reset();
                    return readHostDomainName(reader);
                }
            }
            else if (isNumeric(c)) {
                if (octet == -1) {
                    octet = c - 48;
                }
                else if (octet == 0) {
                    if (inIPv6) {
                        throw new IllegalArgumentException(HttpParser.SM.getString("http.invalidLeadingZero"));
                    }
                    reader.reset();
                    return readHostDomainName(reader);
                }
                else {
                    octet = octet * 10 + c - 48;
                }
            }
            else {
                if (c != 58) {
                    if (c == -1) {
                        if (inIPv6) {
                            throw new IllegalArgumentException(HttpParser.SM.getString("http.noClosingBracket"));
                        }
                        pos = -1;
                    }
                    else if (c == 93) {
                        if (!inIPv6) {
                            throw new IllegalArgumentException(HttpParser.SM.getString("http.closingBracket"));
                        }
                        ++pos;
                    }
                    else {
                        if (!inIPv6 && (isAlpha(c) || c == 45)) {
                            reader.reset();
                            return readHostDomainName(reader);
                        }
                        throw new IllegalArgumentException(HttpParser.SM.getString("http.illegalCharacterIpv4", new Object[] { Character.toString((char)c) }));
                    }
                }
                if (octetCount != 4 || octet < 0 || octet > 255) {
                    reader.reset();
                    return readHostDomainName(reader);
                }
                return pos;
            }
            ++pos;
        }
    }
    
    static int readHostIPv6(final Reader reader) throws IOException {
        int c = reader.read();
        if (c != 91) {
            throw new IllegalArgumentException(HttpParser.SM.getString("http.noOpeningBracket"));
        }
        int h16Count = 0;
        int h16Size = 0;
        int pos = 1;
        boolean parsedDoubleColon = false;
        int precedingColonsCount = 0;
        while (true) {
            c = reader.read();
            if (h16Count == 0 && precedingColonsCount == 1 && c != 58) {
                throw new IllegalArgumentException(HttpParser.SM.getString("http.singleColonStart"));
            }
            if (isHex(c)) {
                if (h16Size == 0) {
                    precedingColonsCount = 0;
                    ++h16Count;
                }
                if (++h16Size > 4) {
                    throw new IllegalArgumentException(HttpParser.SM.getString("http.invalidHextet"));
                }
            }
            else if (c == 58) {
                if (precedingColonsCount >= 2) {
                    throw new IllegalArgumentException(HttpParser.SM.getString("http.tooManyColons"));
                }
                if (precedingColonsCount == 1) {
                    if (parsedDoubleColon) {
                        throw new IllegalArgumentException(HttpParser.SM.getString("http.tooManyDoubleColons"));
                    }
                    parsedDoubleColon = true;
                    ++h16Count;
                }
                ++precedingColonsCount;
                reader.mark(4);
                h16Size = 0;
            }
            else {
                if (c == 93) {
                    if (precedingColonsCount == 1) {
                        throw new IllegalArgumentException(HttpParser.SM.getString("http.singleColonEnd"));
                    }
                    ++pos;
                }
                else {
                    if (c != 46) {
                        throw new IllegalArgumentException(HttpParser.SM.getString("http.illegalCharacterIpv6", new Object[] { Character.toString((char)c) }));
                    }
                    if (h16Count != 7 && (h16Count >= 7 || !parsedDoubleColon)) {
                        throw new IllegalArgumentException(HttpParser.SM.getString("http.invalidIpv4Location"));
                    }
                    reader.reset();
                    pos -= h16Size;
                    pos += readHostIPv4(reader, true);
                    ++h16Count;
                }
                if (h16Count > 8) {
                    throw new IllegalArgumentException(HttpParser.SM.getString("http.tooManyHextets", new Object[] { Integer.toString(h16Count) }));
                }
                if (h16Count != 8 && !parsedDoubleColon) {
                    throw new IllegalArgumentException(HttpParser.SM.getString("http.tooFewHextets", new Object[] { Integer.toString(h16Count) }));
                }
                c = reader.read();
                if (c == 58) {
                    return pos;
                }
                if (c == -1) {
                    return -1;
                }
                throw new IllegalArgumentException(HttpParser.SM.getString("http.illegalAfterIpv6", new Object[] { Character.toString((char)c) }));
            }
            ++pos;
        }
    }
    
    static int readHostDomainName(final Reader reader) throws IOException {
        DomainParseState state;
        int pos;
        for (state = DomainParseState.NEW, pos = 0; state.mayContinue(); state = state.next(reader.read()), ++pos) {}
        if (DomainParseState.COLON == state) {
            return pos - 1;
        }
        return -1;
    }
    
    static SkipResult skipUntil(final Reader input, int c, final char target) throws IOException {
        while (c != -1 && c != target) {
            c = input.read();
        }
        if (c == -1) {
            return SkipResult.EOF;
        }
        return SkipResult.FOUND;
    }
    
    private void relax(final boolean[] flags, final String relaxedChars) {
        if (relaxedChars != null && relaxedChars.length() > 0) {
            final char[] charArray;
            final char[] chars = charArray = relaxedChars.toCharArray();
            for (final char c : charArray) {
                if (isRelaxable(c)) {
                    flags[c] = true;
                    this.isNotRequestTarget[c] = false;
                }
            }
        }
    }
    
    static {
        SM = StringManager.getManager((Class)HttpParser.class);
        LOGGER = LogFactory.getLog((Class)HttpParser.class);
        IS_CONTROL = new boolean[128];
        IS_SEPARATOR = new boolean[128];
        IS_TOKEN = new boolean[128];
        IS_HEX = new boolean[128];
        IS_HTTP_PROTOCOL = new boolean[128];
        IS_ALPHA = new boolean[128];
        IS_NUMERIC = new boolean[128];
        REQUEST_TARGET_ALLOW = new boolean[128];
        IS_UNRESERVED = new boolean[128];
        IS_SUBDELIM = new boolean[128];
        IS_USERINFO = new boolean[128];
        IS_RELAXABLE = new boolean[128];
        for (int i = 0; i < 128; ++i) {
            if (i < 32 || i == 127) {
                HttpParser.IS_CONTROL[i] = true;
            }
            if (i == 40 || i == 41 || i == 60 || i == 62 || i == 64 || i == 44 || i == 59 || i == 58 || i == 92 || i == 34 || i == 47 || i == 91 || i == 93 || i == 63 || i == 61 || i == 123 || i == 125 || i == 32 || i == 9) {
                HttpParser.IS_SEPARATOR[i] = true;
            }
            if (!HttpParser.IS_CONTROL[i] && !HttpParser.IS_SEPARATOR[i] && i < 128) {
                HttpParser.IS_TOKEN[i] = true;
            }
            if ((i >= 48 && i <= 57) || (i >= 97 && i <= 102) || (i >= 65 && i <= 70)) {
                HttpParser.IS_HEX[i] = true;
            }
            if (i == 72 || i == 84 || i == 80 || i == 47 || i == 46 || (i >= 48 && i <= 57)) {
                HttpParser.IS_HTTP_PROTOCOL[i] = true;
            }
            if (i >= 48 && i <= 57) {
                HttpParser.IS_NUMERIC[i] = true;
            }
            if ((i >= 97 && i <= 122) || (i >= 65 && i <= 90)) {
                HttpParser.IS_ALPHA[i] = true;
            }
            if (HttpParser.IS_ALPHA[i] || HttpParser.IS_NUMERIC[i] || i == 45 || i == 46 || i == 95 || i == 126) {
                HttpParser.IS_UNRESERVED[i] = true;
            }
            if (i == 33 || i == 36 || i == 38 || i == 39 || i == 40 || i == 41 || i == 42 || i == 43 || i == 44 || i == 59 || i == 61) {
                HttpParser.IS_SUBDELIM[i] = true;
            }
            if (HttpParser.IS_UNRESERVED[i] || i == 37 || HttpParser.IS_SUBDELIM[i] || i == 58) {
                HttpParser.IS_USERINFO[i] = true;
            }
            if (i == 34 || i == 60 || i == 62 || i == 91 || i == 92 || i == 93 || i == 94 || i == 96 || i == 123 || i == 124 || i == 125) {
                HttpParser.IS_RELAXABLE[i] = true;
            }
        }
        final String prop = System.getProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow");
        if (prop != null) {
            for (int j = 0; j < prop.length(); ++j) {
                final char c = prop.charAt(j);
                if (c == '{' || c == '}' || c == '|') {
                    HttpParser.REQUEST_TARGET_ALLOW[c] = true;
                }
                else {
                    HttpParser.LOGGER.warn((Object)HttpParser.SM.getString("http.invalidRequestTargetCharacter", new Object[] { c }));
                }
            }
        }
        DEFAULT = new HttpParser(null, null);
    }
    
    private enum DomainParseState
    {
        NEW(true, false, false, false, false, "http.invalidCharacterDomain.atStart"), 
        ALPHA(true, true, true, true, true, "http.invalidCharacterDomain.afterLetter"), 
        NUMERIC(true, true, true, true, true, "http.invalidCharacterDomain.afterNumber"), 
        PERIOD(true, false, false, true, false, "http.invalidCharacterDomain.afterPeriod"), 
        HYPHEN(true, true, false, false, false, "http.invalidCharacterDomain.afterHyphen"), 
        COLON(false, false, false, false, false, "http.invalidCharacterDomain.afterColon"), 
        END(false, false, false, false, false, "http.invalidCharacterDomain.atEnd"), 
        UNDERSCORE(true, true, false, true, true, "http.invalidCharacterDomain.afterUnderscore");
        
        private final boolean mayContinue;
        private final boolean allowsHyphen;
        private final boolean allowsPeriod;
        private final boolean allowsEnd;
        private final String errorMsg;
        private final boolean allowUnderscore;
        
        private DomainParseState(final boolean mayContinue, final boolean allowsHyphen, final boolean allowsPeriod, final boolean allowsEnd, final boolean allowUnderscore, final String errorMsg) {
            this.mayContinue = mayContinue;
            this.allowsHyphen = allowsHyphen;
            this.allowsPeriod = allowsPeriod;
            this.allowsEnd = allowsEnd;
            this.errorMsg = errorMsg;
            this.allowUnderscore = allowUnderscore;
        }
        
        public boolean mayContinue() {
            return this.mayContinue;
        }
        
        public DomainParseState next(final int c) {
            if (c == -1) {
                if (this.allowsEnd) {
                    return DomainParseState.END;
                }
                throw new IllegalArgumentException(HttpParser.SM.getString("http.invalidSegmentEndState", new Object[] { this.name() }));
            }
            else {
                if (HttpParser.isAlpha(c)) {
                    return DomainParseState.ALPHA;
                }
                if (HttpParser.isNumeric(c)) {
                    return DomainParseState.NUMERIC;
                }
                if (c == 46) {
                    if (this.allowsPeriod) {
                        return DomainParseState.PERIOD;
                    }
                    throw new IllegalArgumentException(HttpParser.SM.getString(this.errorMsg, new Object[] { Character.toString((char)c) }));
                }
                else if (c == 95) {
                    if (this.allowUnderscore) {
                        return DomainParseState.UNDERSCORE;
                    }
                    throw new IllegalArgumentException(HttpParser.SM.getString(this.errorMsg, new Object[] { Character.toString((char)c) }));
                }
                else if (c == 58) {
                    if (this.allowsEnd) {
                        return DomainParseState.COLON;
                    }
                    throw new IllegalArgumentException(HttpParser.SM.getString(this.errorMsg, new Object[] { Character.toString((char)c) }));
                }
                else {
                    if (c != 45) {
                        throw new IllegalArgumentException(HttpParser.SM.getString("http.illegalCharacterDomain", new Object[] { Character.toString((char)c) }));
                    }
                    if (this.allowsHyphen) {
                        return DomainParseState.HYPHEN;
                    }
                    throw new IllegalArgumentException(HttpParser.SM.getString(this.errorMsg, new Object[] { Character.toString((char)c) }));
                }
            }
        }
    }
}
