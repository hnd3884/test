package io.netty.handler.codec.http;

import io.netty.util.internal.StringUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.net.URI;
import io.netty.util.internal.ObjectUtil;
import java.util.List;
import java.util.Map;
import java.nio.charset.Charset;

public class QueryStringDecoder
{
    private static final int DEFAULT_MAX_PARAMS = 1024;
    private final Charset charset;
    private final String uri;
    private final int maxParams;
    private final boolean semicolonIsNormalChar;
    private int pathEndIdx;
    private String path;
    private Map<String, List<String>> params;
    
    public QueryStringDecoder(final String uri) {
        this(uri, HttpConstants.DEFAULT_CHARSET);
    }
    
    public QueryStringDecoder(final String uri, final boolean hasPath) {
        this(uri, HttpConstants.DEFAULT_CHARSET, hasPath);
    }
    
    public QueryStringDecoder(final String uri, final Charset charset) {
        this(uri, charset, true);
    }
    
    public QueryStringDecoder(final String uri, final Charset charset, final boolean hasPath) {
        this(uri, charset, hasPath, 1024);
    }
    
    public QueryStringDecoder(final String uri, final Charset charset, final boolean hasPath, final int maxParams) {
        this(uri, charset, hasPath, maxParams, false);
    }
    
    public QueryStringDecoder(final String uri, final Charset charset, final boolean hasPath, final int maxParams, final boolean semicolonIsNormalChar) {
        this.uri = ObjectUtil.checkNotNull(uri, "uri");
        this.charset = ObjectUtil.checkNotNull(charset, "charset");
        this.maxParams = ObjectUtil.checkPositive(maxParams, "maxParams");
        this.semicolonIsNormalChar = semicolonIsNormalChar;
        this.pathEndIdx = (hasPath ? -1 : 0);
    }
    
    public QueryStringDecoder(final URI uri) {
        this(uri, HttpConstants.DEFAULT_CHARSET);
    }
    
    public QueryStringDecoder(final URI uri, final Charset charset) {
        this(uri, charset, 1024);
    }
    
    public QueryStringDecoder(final URI uri, final Charset charset, final int maxParams) {
        this(uri, charset, maxParams, false);
    }
    
    public QueryStringDecoder(final URI uri, final Charset charset, final int maxParams, final boolean semicolonIsNormalChar) {
        String rawPath = uri.getRawPath();
        if (rawPath == null) {
            rawPath = "";
        }
        final String rawQuery = uri.getRawQuery();
        this.uri = ((rawQuery == null) ? rawPath : (rawPath + '?' + rawQuery));
        this.charset = ObjectUtil.checkNotNull(charset, "charset");
        this.maxParams = ObjectUtil.checkPositive(maxParams, "maxParams");
        this.semicolonIsNormalChar = semicolonIsNormalChar;
        this.pathEndIdx = rawPath.length();
    }
    
    @Override
    public String toString() {
        return this.uri();
    }
    
    public String uri() {
        return this.uri;
    }
    
    public String path() {
        if (this.path == null) {
            this.path = decodeComponent(this.uri, 0, this.pathEndIdx(), this.charset, true);
        }
        return this.path;
    }
    
    public Map<String, List<String>> parameters() {
        if (this.params == null) {
            this.params = decodeParams(this.uri, this.pathEndIdx(), this.charset, this.maxParams, this.semicolonIsNormalChar);
        }
        return this.params;
    }
    
    public String rawPath() {
        return this.uri.substring(0, this.pathEndIdx());
    }
    
    public String rawQuery() {
        final int start = this.pathEndIdx() + 1;
        return (start < this.uri.length()) ? this.uri.substring(start) : "";
    }
    
    private int pathEndIdx() {
        if (this.pathEndIdx == -1) {
            this.pathEndIdx = findPathEndIndex(this.uri);
        }
        return this.pathEndIdx;
    }
    
    private static Map<String, List<String>> decodeParams(final String s, int from, final Charset charset, int paramsLimit, final boolean semicolonIsNormalChar) {
        final int len = s.length();
        if (from >= len) {
            return Collections.emptyMap();
        }
        if (s.charAt(from) == '?') {
            ++from;
        }
        final Map<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        int nameStart = from;
        int valueStart = -1;
        int i = 0;
    Label_0188:
        for (i = from; i < len; ++i) {
            switch (s.charAt(i)) {
                case '=': {
                    if (nameStart == i) {
                        nameStart = i + 1;
                        break;
                    }
                    if (valueStart < nameStart) {
                        valueStart = i + 1;
                        break;
                    }
                    break;
                }
                case ';': {
                    if (semicolonIsNormalChar) {
                        break;
                    }
                }
                case '&': {
                    if (addParam(s, nameStart, valueStart, i, params, charset) && --paramsLimit == 0) {
                        return params;
                    }
                    nameStart = i + 1;
                    break;
                }
                case '#': {
                    break Label_0188;
                }
            }
        }
        addParam(s, nameStart, valueStart, i, params, charset);
        return params;
    }
    
    private static boolean addParam(final String s, final int nameStart, int valueStart, final int valueEnd, final Map<String, List<String>> params, final Charset charset) {
        if (nameStart >= valueEnd) {
            return false;
        }
        if (valueStart <= nameStart) {
            valueStart = valueEnd + 1;
        }
        final String name = decodeComponent(s, nameStart, valueStart - 1, charset, false);
        final String value = decodeComponent(s, valueStart, valueEnd, charset, false);
        List<String> values = params.get(name);
        if (values == null) {
            values = new ArrayList<String>(1);
            params.put(name, values);
        }
        values.add(value);
        return true;
    }
    
    public static String decodeComponent(final String s) {
        return decodeComponent(s, HttpConstants.DEFAULT_CHARSET);
    }
    
    public static String decodeComponent(final String s, final Charset charset) {
        if (s == null) {
            return "";
        }
        return decodeComponent(s, 0, s.length(), charset, false);
    }
    
    private static String decodeComponent(final String s, final int from, final int toExcluded, final Charset charset, final boolean isPath) {
        final int len = toExcluded - from;
        if (len <= 0) {
            return "";
        }
        int firstEscaped = -1;
        for (int i = from; i < toExcluded; ++i) {
            final char c = s.charAt(i);
            if (c == '%' || (c == '+' && !isPath)) {
                firstEscaped = i;
                break;
            }
        }
        if (firstEscaped == -1) {
            return s.substring(from, toExcluded);
        }
        final int decodedCapacity = (toExcluded - firstEscaped) / 3;
        final byte[] buf = PlatformDependent.allocateUninitializedArray(decodedCapacity);
        final StringBuilder strBuf = new StringBuilder(len);
        strBuf.append(s, from, firstEscaped);
    Label_0273:
        for (int j = firstEscaped; j < toExcluded; ++j) {
            final char c2 = s.charAt(j);
            if (c2 == '%') {
                int bufIdx = 0;
                while (j + 3 <= toExcluded) {
                    buf[bufIdx++] = StringUtil.decodeHexByte(s, j + 1);
                    j += 3;
                    if (j >= toExcluded || s.charAt(j) != '%') {
                        --j;
                        strBuf.append(new String(buf, 0, bufIdx, charset));
                        continue Label_0273;
                    }
                }
                throw new IllegalArgumentException("unterminated escape sequence at index " + j + " of: " + s);
            }
            strBuf.append((c2 != '+' || isPath) ? c2 : ' ');
        }
        return strBuf.toString();
    }
    
    private static int findPathEndIndex(final String uri) {
        final int len = uri.length();
        for (int i = 0; i < len; ++i) {
            final char c = uri.charAt(i);
            if (c == '?' || c == '#') {
                return i;
            }
        }
        return len;
    }
}
