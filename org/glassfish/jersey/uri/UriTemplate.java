package org.glassfish.jersey.uri;

import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Deque;
import java.util.ArrayDeque;
import org.glassfish.jersey.internal.guava.Preconditions;
import java.net.URI;
import java.util.regex.PatternSyntaxException;
import org.glassfish.jersey.uri.internal.UriTemplateParser;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Comparator;

public class UriTemplate
{
    private static final String[] EMPTY_VALUES;
    public static final Comparator<UriTemplate> COMPARATOR;
    private static final Pattern TEMPLATE_NAMES_PATTERN;
    public static final UriTemplate EMPTY;
    private final String template;
    private final String normalizedTemplate;
    private final PatternWithGroups pattern;
    private final boolean endsWithSlash;
    private final List<String> templateVariables;
    private final int numOfExplicitRegexes;
    private final int numOfRegexGroups;
    private final int numOfCharacters;
    
    private UriTemplate() {
        final String s = "";
        this.normalizedTemplate = s;
        this.template = s;
        this.pattern = PatternWithGroups.EMPTY;
        this.endsWithSlash = false;
        this.templateVariables = Collections.emptyList();
        final int numOfExplicitRegexes = 0;
        this.numOfRegexGroups = numOfExplicitRegexes;
        this.numOfCharacters = numOfExplicitRegexes;
        this.numOfExplicitRegexes = numOfExplicitRegexes;
    }
    
    public UriTemplate(final String template) throws PatternSyntaxException, IllegalArgumentException {
        this(new UriTemplateParser(template));
    }
    
    protected UriTemplate(final UriTemplateParser templateParser) throws PatternSyntaxException, IllegalArgumentException {
        this.template = templateParser.getTemplate();
        this.normalizedTemplate = templateParser.getNormalizedTemplate();
        this.pattern = initUriPattern(templateParser);
        this.numOfExplicitRegexes = templateParser.getNumberOfExplicitRegexes();
        this.numOfRegexGroups = templateParser.getNumberOfRegexGroups();
        this.numOfCharacters = templateParser.getNumberOfLiteralCharacters();
        this.endsWithSlash = (this.template.charAt(this.template.length() - 1) == '/');
        this.templateVariables = Collections.unmodifiableList((List<? extends String>)templateParser.getNames());
    }
    
    private static PatternWithGroups initUriPattern(final UriTemplateParser templateParser) {
        return new PatternWithGroups(templateParser.getPattern(), templateParser.getGroupIndexes());
    }
    
    public static URI resolve(final URI baseUri, final String refUri) {
        return resolve(baseUri, URI.create(refUri));
    }
    
    public static URI resolve(final URI baseUri, URI refUri) {
        Preconditions.checkNotNull(baseUri, (Object)"Input base URI parameter must not be null.");
        Preconditions.checkNotNull(refUri, (Object)"Input reference URI parameter must not be null.");
        final String refString = refUri.toString();
        if (refString.isEmpty()) {
            refUri = URI.create("#");
        }
        else if (refString.startsWith("?")) {
            String baseString = baseUri.toString();
            final int qIndex = baseString.indexOf(63);
            baseString = ((qIndex > -1) ? baseString.substring(0, qIndex) : baseString);
            return URI.create(baseString + refString);
        }
        URI result = baseUri.resolve(refUri);
        if (refString.isEmpty()) {
            final String resolvedString = result.toString();
            result = URI.create(resolvedString.substring(0, resolvedString.indexOf(35)));
        }
        return normalize(result);
    }
    
    public static URI normalize(final String uri) {
        return normalize(URI.create(uri));
    }
    
    public static URI normalize(final URI uri) {
        Preconditions.checkNotNull(uri, (Object)"Input reference URI parameter must not be null.");
        final String path = uri.getPath();
        if (path == null || path.isEmpty() || !path.contains("/.")) {
            return uri;
        }
        final String[] segments = path.split("/");
        final Deque<String> resolvedSegments = new ArrayDeque<String>(segments.length);
        for (final String segment : segments) {
            if (!segment.isEmpty()) {
                if (!".".equals(segment)) {
                    if ("..".equals(segment)) {
                        resolvedSegments.pollLast();
                    }
                    else {
                        resolvedSegments.offer(segment);
                    }
                }
            }
        }
        final StringBuilder pathBuilder = new StringBuilder();
        for (final String segment2 : resolvedSegments) {
            pathBuilder.append('/').append(segment2);
        }
        final String resultString = createURIWithStringValues(uri.getScheme(), uri.getAuthority(), null, null, null, pathBuilder.toString(), uri.getQuery(), uri.getFragment(), UriTemplate.EMPTY_VALUES, false, false);
        return URI.create(resultString);
    }
    
    public static URI relativize(final URI baseUri, final URI refUri) {
        Preconditions.checkNotNull(baseUri, (Object)"Input base URI parameter must not be null.");
        Preconditions.checkNotNull(refUri, (Object)"Input reference URI parameter must not be null.");
        return normalize(baseUri.relativize(refUri));
    }
    
    public final String getTemplate() {
        return this.template;
    }
    
    public final PatternWithGroups getPattern() {
        return this.pattern;
    }
    
    public final boolean endsWithSlash() {
        return this.endsWithSlash;
    }
    
    public final List<String> getTemplateVariables() {
        return this.templateVariables;
    }
    
    public final boolean isTemplateVariablePresent(final String name) {
        for (final String s : this.templateVariables) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public final int getNumberOfExplicitRegexes() {
        return this.numOfExplicitRegexes;
    }
    
    public final int getNumberOfRegexGroups() {
        return this.numOfRegexGroups;
    }
    
    public final int getNumberOfExplicitCharacters() {
        return this.numOfCharacters;
    }
    
    public final int getNumberOfTemplateVariables() {
        return this.templateVariables.size();
    }
    
    public final boolean match(final CharSequence uri, final Map<String, String> templateVariableToValue) throws IllegalArgumentException {
        if (templateVariableToValue == null) {
            throw new IllegalArgumentException();
        }
        return this.pattern.match(uri, this.templateVariables, templateVariableToValue);
    }
    
    public final boolean match(final CharSequence uri, final List<String> groupValues) throws IllegalArgumentException {
        if (groupValues == null) {
            throw new IllegalArgumentException();
        }
        return this.pattern.match(uri, groupValues);
    }
    
    public final String createURI(final Map<String, String> values) {
        final StringBuilder sb = new StringBuilder();
        resolveTemplate(this.normalizedTemplate, sb, new TemplateValueStrategy() {
            @Override
            public String valueFor(final String templateVariable, final String matchedGroup) {
                return values.get(templateVariable);
            }
        });
        return sb.toString();
    }
    
    public final String createURI(final String... values) {
        return this.createURI(values, 0, values.length);
    }
    
    public final String createURI(final String[] values, final int offset, final int length) {
        final TemplateValueStrategy ns = new TemplateValueStrategy() {
            private final int lengthPlusOffset = length + offset;
            private int v = offset;
            private final Map<String, String> mapValues = new HashMap<String, String>();
            
            @Override
            public String valueFor(final String templateVariable, final String matchedGroup) {
                String tValue = this.mapValues.get(templateVariable);
                if (tValue == null && this.v < this.lengthPlusOffset) {
                    tValue = values[this.v++];
                    if (tValue != null) {
                        this.mapValues.put(templateVariable, tValue);
                    }
                }
                return tValue;
            }
        };
        final StringBuilder sb = new StringBuilder();
        resolveTemplate(this.normalizedTemplate, sb, ns);
        return sb.toString();
    }
    
    private static void resolveTemplate(final String normalizedTemplate, final StringBuilder builder, final TemplateValueStrategy valueStrategy) {
        final Matcher m = UriTemplate.TEMPLATE_NAMES_PATTERN.matcher(normalizedTemplate);
        int i = 0;
        while (m.find()) {
            builder.append(normalizedTemplate, i, m.start());
            final String variableName = m.group(1);
            final char firstChar = variableName.charAt(0);
            if (firstChar == '?' || firstChar == ';') {
                char prefix;
                char separator;
                String emptyValueAssignment;
                if (firstChar == '?') {
                    prefix = '?';
                    separator = '&';
                    emptyValueAssignment = "=";
                }
                else {
                    prefix = ';';
                    separator = ';';
                    emptyValueAssignment = "";
                }
                final int index = builder.length();
                final String[] split;
                final String[] variables = split = variableName.substring(1).split(", ?");
                for (final String variable : split) {
                    try {
                        final String value = valueStrategy.valueFor(variable, m.group());
                        if (value != null) {
                            if (index != builder.length()) {
                                builder.append(separator);
                            }
                            builder.append(variable);
                            if (value.isEmpty()) {
                                builder.append(emptyValueAssignment);
                            }
                            else {
                                builder.append('=');
                                builder.append(value);
                            }
                        }
                    }
                    catch (final IllegalArgumentException ex) {}
                }
                if (index != builder.length() && (index == 0 || builder.charAt(index - 1) != prefix)) {
                    builder.insert(index, prefix);
                }
            }
            else {
                final String value2 = valueStrategy.valueFor(variableName, m.group());
                if (value2 != null) {
                    builder.append(value2);
                }
            }
            i = m.end();
        }
        builder.append(normalizedTemplate, i, normalizedTemplate.length());
    }
    
    @Override
    public final String toString() {
        return this.pattern.toString();
    }
    
    @Override
    public final int hashCode() {
        return this.pattern.hashCode();
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (o instanceof UriTemplate) {
            final UriTemplate that = (UriTemplate)o;
            return this.pattern.equals(that.pattern);
        }
        return false;
    }
    
    public static String createURI(final String scheme, final String authority, final String userInfo, final String host, final String port, final String path, final String query, final String fragment, final Map<String, ?> values, final boolean encode, final boolean encodeSlashInPath) {
        final Map<String, String> stringValues = new HashMap<String, String>();
        for (final Map.Entry<String, ?> e : values.entrySet()) {
            if (e.getValue() != null) {
                stringValues.put(e.getKey(), e.getValue().toString());
            }
        }
        return createURIWithStringValues(scheme, authority, userInfo, host, port, path, query, fragment, stringValues, encode, encodeSlashInPath);
    }
    
    public static String createURIWithStringValues(final String scheme, final String authority, final String userInfo, final String host, final String port, final String path, final String query, final String fragment, final Map<String, ?> values, final boolean encode, final boolean encodeSlashInPath) {
        return createURIWithStringValues(scheme, authority, userInfo, host, port, path, query, fragment, UriTemplate.EMPTY_VALUES, encode, encodeSlashInPath, values);
    }
    
    public static String createURI(final String scheme, final String authority, final String userInfo, final String host, final String port, final String path, final String query, final String fragment, final Object[] values, final boolean encode, final boolean encodeSlashInPath) {
        final String[] stringValues = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            if (values[i] != null) {
                stringValues[i] = values[i].toString();
            }
        }
        return createURIWithStringValues(scheme, authority, userInfo, host, port, path, query, fragment, stringValues, encode, encodeSlashInPath);
    }
    
    public static String createURIWithStringValues(final String scheme, final String authority, final String userInfo, final String host, final String port, final String path, final String query, final String fragment, final String[] values, final boolean encode, final boolean encodeSlashInPath) {
        final Map<String, Object> mapValues = new HashMap<String, Object>();
        return createURIWithStringValues(scheme, authority, userInfo, host, port, path, query, fragment, values, encode, encodeSlashInPath, mapValues);
    }
    
    private static String createURIWithStringValues(final String scheme, final String authority, final String userInfo, final String host, final String port, final String path, final String query, final String fragment, final String[] values, final boolean encode, final boolean encodeSlashInPath, final Map<String, ?> mapValues) {
        final StringBuilder sb = new StringBuilder();
        int offset = 0;
        if (scheme != null) {
            offset = createUriComponent(UriComponent.Type.SCHEME, scheme, values, offset, false, mapValues, sb);
            sb.append(':');
        }
        boolean hasAuthority = false;
        if (notEmpty(userInfo) || notEmpty(host) || notEmpty(port)) {
            hasAuthority = true;
            sb.append("//");
            if (notEmpty(userInfo)) {
                offset = createUriComponent(UriComponent.Type.USER_INFO, userInfo, values, offset, encode, mapValues, sb);
                sb.append('@');
            }
            if (notEmpty(host)) {
                offset = createUriComponent(UriComponent.Type.HOST, host, values, offset, encode, mapValues, sb);
            }
            if (notEmpty(port)) {
                sb.append(':');
                offset = createUriComponent(UriComponent.Type.PORT, port, values, offset, false, mapValues, sb);
            }
        }
        else if (notEmpty(authority)) {
            hasAuthority = true;
            sb.append("//");
            offset = createUriComponent(UriComponent.Type.AUTHORITY, authority, values, offset, encode, mapValues, sb);
        }
        if (notEmpty(path) || notEmpty(query) || notEmpty(fragment)) {
            if (hasAuthority && (path == null || path.isEmpty() || path.charAt(0) != '/')) {
                sb.append('/');
            }
            if (notEmpty(path)) {
                final UriComponent.Type t = encodeSlashInPath ? UriComponent.Type.PATH_SEGMENT : UriComponent.Type.PATH;
                offset = createUriComponent(t, path, values, offset, encode, mapValues, sb);
            }
            if (notEmpty(query)) {
                sb.append('?');
                offset = createUriComponent(UriComponent.Type.QUERY_PARAM, query, values, offset, encode, mapValues, sb);
            }
            if (notEmpty(fragment)) {
                sb.append('#');
                createUriComponent(UriComponent.Type.FRAGMENT, fragment, values, offset, encode, mapValues, sb);
            }
        }
        return sb.toString();
    }
    
    private static boolean notEmpty(final String string) {
        return string != null && !string.isEmpty();
    }
    
    private static int createUriComponent(final UriComponent.Type componentType, String template, final String[] values, final int valueOffset, final boolean encode, final Map<String, ?> _mapValues, final StringBuilder b) {
        final Map<String, Object> mapValues = (Map<String, Object>)_mapValues;
        if (template.indexOf(123) == -1) {
            b.append(template);
            return valueOffset;
        }
        template = new UriTemplateParser(template).getNormalizedTemplate();
        class ValuesFromArrayStrategy implements TemplateValueStrategy
        {
            private int offset = valueOffset;
            
            @Override
            public String valueFor(final String templateVariable, final String matchedGroup) {
                Object value = mapValues.get(templateVariable);
                if (value == null && this.offset < values.length) {
                    value = values[this.offset++];
                    mapValues.put(templateVariable, value);
                }
                if (value == null) {
                    throw new IllegalArgumentException(String.format("The template variable '%s' has no value", templateVariable));
                }
                if (encode) {
                    return UriComponent.encode(value.toString(), componentType);
                }
                return UriComponent.contextualEncode(value.toString(), componentType);
            }
        }
        final ValuesFromArrayStrategy cs = new ValuesFromArrayStrategy();
        resolveTemplate(template, b, cs);
        return cs.offset;
    }
    
    public static String resolveTemplateValues(final UriComponent.Type type, String template, final boolean encode, final Map<String, ?> _mapValues) {
        if (template == null || template.isEmpty() || template.indexOf(123) == -1) {
            return template;
        }
        final Map<String, Object> mapValues = (Map<String, Object>)_mapValues;
        template = new UriTemplateParser(template).getNormalizedTemplate();
        final StringBuilder sb = new StringBuilder();
        resolveTemplate(template, sb, new TemplateValueStrategy() {
            @Override
            public String valueFor(final String templateVariable, final String matchedGroup) {
                Object value = mapValues.get(templateVariable);
                if (value != null) {
                    if (encode) {
                        value = UriComponent.encode(value.toString(), type);
                    }
                    else {
                        value = UriComponent.contextualEncode(value.toString(), type);
                    }
                    return value.toString();
                }
                if (mapValues.containsKey(templateVariable)) {
                    throw new IllegalArgumentException(String.format("The value associated of the template value map for key '%s' is 'null'.", templateVariable));
                }
                return matchedGroup;
            }
        });
        return sb.toString();
    }
    
    static {
        EMPTY_VALUES = new String[0];
        COMPARATOR = new Comparator<UriTemplate>() {
            @Override
            public int compare(final UriTemplate o1, final UriTemplate o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                }
                if (o1 == null) {
                    return 1;
                }
                if (o2 == null) {
                    return -1;
                }
                if (o1 == UriTemplate.EMPTY && o2 == UriTemplate.EMPTY) {
                    return 0;
                }
                if (o1 == UriTemplate.EMPTY) {
                    return 1;
                }
                if (o2 == UriTemplate.EMPTY) {
                    return -1;
                }
                int i = o2.getNumberOfExplicitCharacters() - o1.getNumberOfExplicitCharacters();
                if (i != 0) {
                    return i;
                }
                i = o2.getNumberOfTemplateVariables() - o1.getNumberOfTemplateVariables();
                if (i != 0) {
                    return i;
                }
                i = o2.getNumberOfExplicitRegexes() - o1.getNumberOfExplicitRegexes();
                if (i != 0) {
                    return i;
                }
                return o2.pattern.getRegex().compareTo(o1.pattern.getRegex());
            }
        };
        TEMPLATE_NAMES_PATTERN = Pattern.compile("\\{([\\w\\?;][-\\w\\.,]*)\\}");
        EMPTY = new UriTemplate();
    }
    
    private interface TemplateValueStrategy
    {
        String valueFor(final String p0, final String p1);
    }
}
