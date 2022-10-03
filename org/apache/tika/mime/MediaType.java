package org.apache.tika.mime;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.HashSet;
import java.util.Set;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.io.Serializable;

public final class MediaType implements Comparable<MediaType>, Serializable
{
    private static final long serialVersionUID = -3831000556189036392L;
    private static final Pattern SPECIAL;
    private static final Pattern SPECIAL_OR_WHITESPACE;
    private static final String VALID_CHARS = "([^\\c\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]+)";
    private static final Pattern TYPE_PATTERN;
    private static final Pattern CHARSET_FIRST_PATTERN;
    private static final Map<String, MediaType> SIMPLE_TYPES;
    public static final MediaType OCTET_STREAM;
    public static final MediaType EMPTY;
    public static final MediaType TEXT_PLAIN;
    public static final MediaType TEXT_HTML;
    public static final MediaType APPLICATION_XML;
    public static final MediaType APPLICATION_ZIP;
    private final String string;
    private final int slash;
    private final int semicolon;
    private final Map<String, String> parameters;
    
    public MediaType(String type, String subtype, final Map<String, String> parameters) {
        type = type.trim().toLowerCase(Locale.ENGLISH);
        subtype = subtype.trim().toLowerCase(Locale.ENGLISH);
        this.slash = type.length();
        this.semicolon = this.slash + 1 + subtype.length();
        if (parameters.isEmpty()) {
            this.parameters = Collections.emptyMap();
            this.string = type + '/' + subtype;
        }
        else {
            final StringBuilder builder = new StringBuilder();
            builder.append(type);
            builder.append('/');
            builder.append(subtype);
            final SortedMap<String, String> map = new TreeMap<String, String>();
            for (final Map.Entry<String, String> entry : parameters.entrySet()) {
                final String key = entry.getKey().trim().toLowerCase(Locale.ENGLISH);
                map.put(key, entry.getValue());
            }
            for (final Map.Entry<String, String> entry : map.entrySet()) {
                builder.append("; ");
                builder.append(entry.getKey());
                builder.append("=");
                final String value = entry.getValue();
                if (MediaType.SPECIAL_OR_WHITESPACE.matcher(value).find()) {
                    builder.append('\"');
                    builder.append(MediaType.SPECIAL.matcher(value).replaceAll("\\\\$0"));
                    builder.append('\"');
                }
                else {
                    builder.append(value);
                }
            }
            this.string = builder.toString();
            this.parameters = (Map<String, String>)Collections.unmodifiableSortedMap((SortedMap<String, ?>)map);
        }
    }
    
    public MediaType(final String type, final String subtype) {
        this(type, subtype, Collections.emptyMap());
    }
    
    private MediaType(final String string, final int slash) {
        assert slash != -1;
        assert string.charAt(slash) == '/';
        assert isSimpleName(string.substring(0, slash));
        assert isSimpleName(string.substring(slash + 1));
        this.string = string;
        this.slash = slash;
        this.semicolon = string.length();
        this.parameters = Collections.emptyMap();
    }
    
    public MediaType(final MediaType type, final Map<String, String> parameters) {
        this(type.getType(), type.getSubtype(), union(type.parameters, parameters));
    }
    
    public MediaType(final MediaType type, final String name, final String value) {
        this(type, Collections.singletonMap(name, value));
    }
    
    public MediaType(final MediaType type, final Charset charset) {
        this(type, "charset", charset.name());
    }
    
    public static MediaType application(final String type) {
        return parse("application/" + type);
    }
    
    public static MediaType audio(final String type) {
        return parse("audio/" + type);
    }
    
    public static MediaType image(final String type) {
        return parse("image/" + type);
    }
    
    public static MediaType text(final String type) {
        return parse("text/" + type);
    }
    
    public static MediaType video(final String type) {
        return parse("video/" + type);
    }
    
    public static Set<MediaType> set(final MediaType... types) {
        final Set<MediaType> set = new HashSet<MediaType>();
        for (final MediaType type : types) {
            if (type != null) {
                set.add(type);
            }
        }
        return Collections.unmodifiableSet((Set<? extends MediaType>)set);
    }
    
    public static Set<MediaType> set(final String... types) {
        final Set<MediaType> set = new HashSet<MediaType>();
        for (final String type : types) {
            final MediaType mt = parse(type);
            if (mt != null) {
                set.add(mt);
            }
        }
        return Collections.unmodifiableSet((Set<? extends MediaType>)set);
    }
    
    public static MediaType parse(final String string) {
        if (string == null) {
            return null;
        }
        synchronized (MediaType.SIMPLE_TYPES) {
            MediaType type = MediaType.SIMPLE_TYPES.get(string);
            if (type == null) {
                final int slash = string.indexOf(47);
                if (slash == -1) {
                    return null;
                }
                if (MediaType.SIMPLE_TYPES.size() < 10000 && isSimpleName(string.substring(0, slash)) && isSimpleName(string.substring(slash + 1))) {
                    type = new MediaType(string, slash);
                    MediaType.SIMPLE_TYPES.put(string, type);
                }
            }
            if (type != null) {
                return type;
            }
        }
        Matcher matcher = MediaType.TYPE_PATTERN.matcher(string);
        if (matcher.matches()) {
            return new MediaType(matcher.group(1), matcher.group(2), parseParameters(matcher.group(3)));
        }
        matcher = MediaType.CHARSET_FIRST_PATTERN.matcher(string);
        if (matcher.matches()) {
            return new MediaType(matcher.group(2), matcher.group(3), parseParameters(matcher.group(1)));
        }
        return null;
    }
    
    private static boolean isSimpleName(final String name) {
        for (int i = 0; i < name.length(); ++i) {
            final char c = name.charAt(i);
            if (c != '-' && c != '+' && c != '.' && c != '_' && ('0' > c || c > '9') && ('a' > c || c > 'z')) {
                return false;
            }
        }
        return name.length() > 0;
    }
    
    private static Map<String, String> parseParameters(String string) {
        if (string.length() == 0) {
            return Collections.emptyMap();
        }
        final Map<String, String> parameters = new HashMap<String, String>();
        while (string.length() > 0) {
            String key = string;
            String value = "";
            final int semicolon = string.indexOf(59);
            if (semicolon != -1) {
                key = string.substring(0, semicolon);
                string = string.substring(semicolon + 1);
            }
            else {
                string = "";
            }
            final int equals = key.indexOf(61);
            if (equals != -1) {
                value = key.substring(equals + 1);
                key = key.substring(0, equals);
            }
            key = key.trim();
            if (key.length() > 0) {
                parameters.put(key, unquote(value.trim()));
            }
        }
        return parameters;
    }
    
    private static String unquote(String s) {
        while (s.startsWith("\"") || s.startsWith("'")) {
            s = s.substring(1);
        }
        while (s.endsWith("\"") || s.endsWith("'")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
    
    private static Map<String, String> union(final Map<String, String> a, final Map<String, String> b) {
        if (a.isEmpty()) {
            return b;
        }
        if (b.isEmpty()) {
            return a;
        }
        final Map<String, String> union = new HashMap<String, String>();
        union.putAll(a);
        union.putAll(b);
        return union;
    }
    
    public MediaType getBaseType() {
        if (this.parameters.isEmpty()) {
            return this;
        }
        return parse(this.string.substring(0, this.semicolon));
    }
    
    public String getType() {
        return this.string.substring(0, this.slash);
    }
    
    public String getSubtype() {
        return this.string.substring(this.slash + 1, this.semicolon);
    }
    
    public boolean hasParameters() {
        return !this.parameters.isEmpty();
    }
    
    public Map<String, String> getParameters() {
        return this.parameters;
    }
    
    @Override
    public String toString() {
        return this.string;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object instanceof MediaType) {
            final MediaType that = (MediaType)object;
            return this.string.equals(that.string);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.string.hashCode();
    }
    
    @Override
    public int compareTo(final MediaType that) {
        return this.string.compareTo(that.string);
    }
    
    static {
        SPECIAL = Pattern.compile("[\\(\\)<>@,;:\\\\\"/\\[\\]\\?=]");
        SPECIAL_OR_WHITESPACE = Pattern.compile("[\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]");
        TYPE_PATTERN = Pattern.compile("(?s)\\s*([^\\c\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]+)\\s*/\\s*([^\\c\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]+)\\s*($|;.*)");
        CHARSET_FIRST_PATTERN = Pattern.compile("(?is)\\s*(charset\\s*=\\s*[^\\c;\\s]+)\\s*;\\s*([^\\c\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]+)\\s*/\\s*([^\\c\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]+)\\s*");
        SIMPLE_TYPES = new HashMap<String, MediaType>();
        OCTET_STREAM = parse("application/octet-stream");
        EMPTY = parse("application/x-empty");
        TEXT_PLAIN = parse("text/plain");
        TEXT_HTML = parse("text/html");
        APPLICATION_XML = parse("application/xml");
        APPLICATION_ZIP = parse("application/zip");
    }
}
