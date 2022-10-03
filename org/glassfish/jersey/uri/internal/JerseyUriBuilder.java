package org.glassfish.jersey.uri.internal;

import java.net.URISyntaxException;
import javax.ws.rs.core.UriBuilderException;
import java.util.List;
import java.util.Iterator;
import org.glassfish.jersey.uri.UriTemplate;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.AnnotatedElement;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.lang.reflect.Method;
import javax.ws.rs.Path;
import org.glassfish.jersey.internal.guava.InetAddresses;
import org.glassfish.jersey.uri.UriComponent;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.net.URI;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

public class JerseyUriBuilder extends UriBuilder
{
    private String scheme;
    private String ssp;
    private String authority;
    private String userInfo;
    private String host;
    private String port;
    private final StringBuilder path;
    private MultivaluedMap<String, String> matrixParams;
    private final StringBuilder query;
    private MultivaluedMap<String, String> queryParams;
    private String fragment;
    
    public JerseyUriBuilder() {
        this.path = new StringBuilder();
        this.query = new StringBuilder();
    }
    
    private JerseyUriBuilder(final JerseyUriBuilder that) {
        this.scheme = that.scheme;
        this.ssp = that.ssp;
        this.authority = that.authority;
        this.userInfo = that.userInfo;
        this.host = that.host;
        this.port = that.port;
        this.path = new StringBuilder(that.path);
        this.matrixParams = (MultivaluedMap<String, String>)((that.matrixParams == null) ? null : new MultivaluedStringMap(that.matrixParams));
        this.query = new StringBuilder(that.query);
        this.queryParams = (MultivaluedMap<String, String>)((that.queryParams == null) ? null : new MultivaluedStringMap(that.queryParams));
        this.fragment = that.fragment;
    }
    
    public JerseyUriBuilder clone() {
        return new JerseyUriBuilder(this);
    }
    
    public JerseyUriBuilder uri(final URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("uri"));
        }
        if (uri.getRawFragment() != null) {
            this.fragment = uri.getRawFragment();
        }
        if (uri.isOpaque()) {
            this.scheme = uri.getScheme();
            this.ssp = uri.getRawSchemeSpecificPart();
            return this;
        }
        if (uri.getScheme() == null) {
            if (this.ssp != null && uri.getRawSchemeSpecificPart() != null) {
                this.ssp = uri.getRawSchemeSpecificPart();
                return this;
            }
        }
        else {
            this.scheme = uri.getScheme();
        }
        this.ssp = null;
        if (uri.getRawAuthority() != null) {
            if (uri.getRawUserInfo() == null && uri.getHost() == null && uri.getPort() == -1) {
                this.authority = uri.getRawAuthority();
                this.userInfo = null;
                this.host = null;
                this.port = null;
            }
            else {
                this.authority = null;
                if (uri.getRawUserInfo() != null) {
                    this.userInfo = uri.getRawUserInfo();
                }
                if (uri.getHost() != null) {
                    this.host = uri.getHost();
                }
                if (uri.getPort() != -1) {
                    this.port = String.valueOf(uri.getPort());
                }
            }
        }
        if (uri.getRawPath() != null && !uri.getRawPath().isEmpty()) {
            this.path.setLength(0);
            this.path.append(uri.getRawPath());
        }
        if (uri.getRawQuery() != null && !uri.getRawQuery().isEmpty()) {
            this.query.setLength(0);
            this.query.append(uri.getRawQuery());
        }
        return this;
    }
    
    public JerseyUriBuilder uri(final String uriTemplate) {
        if (uriTemplate == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("uriTemplate"));
        }
        UriParser parser = new UriParser(uriTemplate);
        parser.parse();
        final String parsedScheme = parser.getScheme();
        if (parsedScheme != null) {
            this.scheme(parsedScheme);
        }
        else if (this.ssp != null) {
            this.ssp = null;
            parser = new UriParser(this.scheme + ":" + uriTemplate);
            parser.parse();
        }
        this.schemeSpecificPart(parser);
        final String parserFragment = parser.getFragment();
        if (parserFragment != null) {
            this.fragment(parserFragment);
        }
        return this;
    }
    
    private void schemeSpecificPart(final UriParser parser) {
        if (parser.isOpaque()) {
            if (parser.getSsp() != null) {
                final String authority = null;
                this.port = authority;
                this.host = authority;
                this.authority = authority;
                this.path.setLength(0);
                this.query.setLength(0);
                this.ssp = parser.getSsp();
            }
            return;
        }
        this.ssp = null;
        if (parser.getAuthority() != null) {
            if (parser.getUserInfo() == null && parser.getHost() == null && parser.getPort() == null) {
                this.authority = this.encode(parser.getAuthority(), UriComponent.Type.AUTHORITY);
                this.userInfo = null;
                this.host = null;
                this.port = null;
            }
            else {
                this.authority = null;
                if (parser.getUserInfo() != null) {
                    this.userInfo(parser.getUserInfo());
                }
                if (parser.getHost() != null) {
                    this.host(parser.getHost());
                }
                if (parser.getPort() != null) {
                    this.port = parser.getPort();
                }
            }
        }
        if (parser.getPath() != null) {
            this.path.setLength(0);
            this.path(parser.getPath());
        }
        if (parser.getQuery() != null) {
            this.query.setLength(0);
            this.query.append(parser.getQuery());
        }
    }
    
    public JerseyUriBuilder scheme(final String scheme) {
        if (scheme != null) {
            UriComponent.validate(this.scheme = scheme, UriComponent.Type.SCHEME, true);
        }
        else {
            this.scheme = null;
        }
        return this;
    }
    
    public JerseyUriBuilder schemeSpecificPart(final String ssp) {
        if (ssp == null) {
            throw new IllegalArgumentException(LocalizationMessages.URI_BUILDER_SCHEME_PART_NULL());
        }
        final UriParser parser = new UriParser((this.scheme != null) ? (this.scheme + ":" + ssp) : ssp);
        parser.parse();
        if (parser.getScheme() != null && !parser.getScheme().equals(this.scheme)) {
            throw new IllegalStateException(LocalizationMessages.URI_BUILDER_SCHEME_PART_UNEXPECTED_COMPONENT(ssp, parser.getScheme()));
        }
        if (parser.getFragment() != null) {
            throw new IllegalStateException(LocalizationMessages.URI_BUILDER_URI_PART_FRAGMENT(ssp, parser.getFragment()));
        }
        this.schemeSpecificPart(parser);
        return this;
    }
    
    public JerseyUriBuilder userInfo(final String ui) {
        this.checkSsp();
        this.userInfo = ((ui != null) ? this.encode(ui, UriComponent.Type.USER_INFO) : null);
        return this;
    }
    
    public JerseyUriBuilder host(final String host) {
        this.checkSsp();
        if (host != null) {
            if (host.isEmpty()) {
                throw new IllegalArgumentException(LocalizationMessages.INVALID_HOST());
            }
            if (InetAddresses.isMappedIPv4Address(host) || InetAddresses.isUriInetAddress(host)) {
                this.host = host;
            }
            else {
                this.host = this.encode(host, UriComponent.Type.HOST);
            }
        }
        else {
            this.host = null;
        }
        return this;
    }
    
    public JerseyUriBuilder port(final int port) {
        this.checkSsp();
        if (port < -1) {
            throw new IllegalArgumentException(LocalizationMessages.INVALID_PORT());
        }
        this.port = ((port == -1) ? null : String.valueOf(port));
        return this;
    }
    
    public JerseyUriBuilder replacePath(final String path) {
        this.checkSsp();
        this.path.setLength(0);
        if (path != null) {
            this.appendPath(path);
        }
        return this;
    }
    
    public JerseyUriBuilder path(final String path) {
        this.checkSsp();
        this.appendPath(path);
        return this;
    }
    
    public UriBuilder path(final Class resource) throws IllegalArgumentException {
        this.checkSsp();
        if (resource == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("resource"));
        }
        final Path p = Path.class.cast(resource.getAnnotation(Path.class));
        if (p == null) {
            throw new IllegalArgumentException(LocalizationMessages.URI_BUILDER_CLASS_PATH_ANNOTATION_MISSING(resource));
        }
        this.appendPath(p);
        return this;
    }
    
    public JerseyUriBuilder path(final Class resource, final String methodName) {
        this.checkSsp();
        if (resource == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("resource"));
        }
        if (methodName == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("methodName"));
        }
        final Method[] methods = AccessController.doPrivileged(ReflectionHelper.getMethodsPA(resource));
        Method found = null;
        for (final Method m : methods) {
            if (methodName.equals(m.getName())) {
                if (found == null || found.isSynthetic()) {
                    found = m;
                }
                else if (!m.isSynthetic()) {
                    throw new IllegalArgumentException();
                }
            }
        }
        if (found == null) {
            throw new IllegalArgumentException(LocalizationMessages.URI_BUILDER_METHODNAME_NOT_SPECIFIED(methodName, resource));
        }
        this.appendPath(this.getPath(found));
        return this;
    }
    
    public JerseyUriBuilder path(final Method method) {
        this.checkSsp();
        if (method == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("method"));
        }
        this.appendPath(this.getPath(method));
        return this;
    }
    
    private Path getPath(final AnnotatedElement ae) {
        final Path p = ae.getAnnotation(Path.class);
        if (p == null) {
            throw new IllegalArgumentException(LocalizationMessages.URI_BUILDER_ANNOTATEDELEMENT_PATH_ANNOTATION_MISSING(ae));
        }
        return p;
    }
    
    public JerseyUriBuilder segment(final String... segments) throws IllegalArgumentException {
        this.checkSsp();
        if (segments == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("segments"));
        }
        for (final String segment : segments) {
            this.appendPath(segment, true);
        }
        return this;
    }
    
    public JerseyUriBuilder replaceMatrix(final String matrix) {
        this.checkSsp();
        final boolean trailingSlash = this.path.charAt(this.path.length() - 1) == '/';
        final int slashIndex = trailingSlash ? this.path.lastIndexOf("/", this.path.length() - 2) : this.path.lastIndexOf("/");
        final int i = this.path.indexOf(";", slashIndex);
        if (i != -1) {
            this.path.setLength(i + 1);
        }
        else if (matrix != null) {
            this.path.append(';');
        }
        if (matrix != null) {
            this.path.append(this.encode(matrix, UriComponent.Type.PATH));
        }
        else if (i != -1) {
            this.path.setLength(i);
            if (trailingSlash) {
                this.path.append("/");
            }
        }
        return this;
    }
    
    public JerseyUriBuilder matrixParam(String name, final Object... values) {
        this.checkSsp();
        if (name == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("name"));
        }
        if (values == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("value"));
        }
        if (values.length == 0) {
            return this;
        }
        name = this.encode(name, UriComponent.Type.MATRIX_PARAM);
        if (this.matrixParams == null) {
            for (final Object value : values) {
                this.path.append(';').append(name);
                if (value == null) {
                    throw new IllegalArgumentException(LocalizationMessages.MATRIX_PARAM_NULL());
                }
                final String stringValue = value.toString();
                if (!stringValue.isEmpty()) {
                    this.path.append('=').append(this.encode(stringValue, UriComponent.Type.MATRIX_PARAM));
                }
            }
        }
        else {
            for (final Object value : values) {
                if (value == null) {
                    throw new IllegalArgumentException(LocalizationMessages.MATRIX_PARAM_NULL());
                }
                this.matrixParams.add((Object)name, (Object)this.encode(value.toString(), UriComponent.Type.MATRIX_PARAM));
            }
        }
        return this;
    }
    
    public JerseyUriBuilder replaceMatrixParam(String name, final Object... values) {
        this.checkSsp();
        if (name == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("name"));
        }
        if (this.matrixParams == null) {
            int i = this.path.lastIndexOf("/");
            if (i == -1) {
                i = 0;
            }
            this.matrixParams = UriComponent.decodeMatrix(this.path.substring(i), false);
            i = this.path.indexOf(";", i);
            if (i != -1) {
                this.path.setLength(i);
            }
        }
        name = this.encode(name, UriComponent.Type.MATRIX_PARAM);
        this.matrixParams.remove((Object)name);
        if (values != null) {
            for (final Object value : values) {
                if (value == null) {
                    throw new IllegalArgumentException(LocalizationMessages.MATRIX_PARAM_NULL());
                }
                this.matrixParams.add((Object)name, (Object)this.encode(value.toString(), UriComponent.Type.MATRIX_PARAM));
            }
        }
        return this;
    }
    
    public JerseyUriBuilder replaceQuery(final String query) {
        this.checkSsp();
        this.query.setLength(0);
        if (query != null) {
            this.query.append(this.encode(query, UriComponent.Type.QUERY));
        }
        return this;
    }
    
    public JerseyUriBuilder queryParam(String name, final Object... values) {
        this.checkSsp();
        if (name == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("name"));
        }
        if (values == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("values"));
        }
        if (values.length == 0) {
            return this;
        }
        name = this.encode(name, UriComponent.Type.QUERY_PARAM);
        if (this.queryParams == null) {
            for (final Object value : values) {
                if (this.query.length() > 0) {
                    this.query.append('&');
                }
                this.query.append(name);
                if (value == null) {
                    throw new IllegalArgumentException(LocalizationMessages.QUERY_PARAM_NULL());
                }
                this.query.append('=').append(this.encode(value.toString(), UriComponent.Type.QUERY_PARAM));
            }
        }
        else {
            for (final Object value : values) {
                if (value == null) {
                    throw new IllegalArgumentException(LocalizationMessages.QUERY_PARAM_NULL());
                }
                this.queryParams.add((Object)name, (Object)this.encode(value.toString(), UriComponent.Type.QUERY_PARAM));
            }
        }
        return this;
    }
    
    public JerseyUriBuilder replaceQueryParam(String name, final Object... values) {
        this.checkSsp();
        if (this.queryParams == null) {
            this.queryParams = UriComponent.decodeQuery(this.query.toString(), false, false);
            this.query.setLength(0);
        }
        name = this.encode(name, UriComponent.Type.QUERY_PARAM);
        this.queryParams.remove((Object)name);
        if (values == null) {
            return this;
        }
        for (final Object value : values) {
            if (value == null) {
                throw new IllegalArgumentException(LocalizationMessages.QUERY_PARAM_NULL());
            }
            this.queryParams.add((Object)name, (Object)this.encode(value.toString(), UriComponent.Type.QUERY_PARAM));
        }
        return this;
    }
    
    public JerseyUriBuilder resolveTemplate(final String name, final Object value) throws IllegalArgumentException {
        this.resolveTemplate(name, value, true, true);
        return this;
    }
    
    public JerseyUriBuilder resolveTemplate(final String name, final Object value, final boolean encodeSlashInPath) {
        this.resolveTemplate(name, value, true, encodeSlashInPath);
        return this;
    }
    
    public JerseyUriBuilder resolveTemplateFromEncoded(final String name, final Object value) {
        this.resolveTemplate(name, value, false, false);
        return this;
    }
    
    private JerseyUriBuilder resolveTemplate(final String name, final Object value, final boolean encode, final boolean encodeSlashInPath) {
        if (name == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("name"));
        }
        if (value == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("value"));
        }
        final Map<String, Object> templateValues = new HashMap<String, Object>();
        templateValues.put(name, value);
        this.resolveTemplates(templateValues, encode, encodeSlashInPath);
        return this;
    }
    
    public JerseyUriBuilder resolveTemplates(final Map<String, Object> templateValues) throws IllegalArgumentException {
        this.resolveTemplates(templateValues, true, true);
        return this;
    }
    
    public JerseyUriBuilder resolveTemplates(final Map<String, Object> templateValues, final boolean encodeSlashInPath) throws IllegalArgumentException {
        this.resolveTemplates(templateValues, true, encodeSlashInPath);
        return this;
    }
    
    public JerseyUriBuilder resolveTemplatesFromEncoded(final Map<String, Object> templateValues) {
        this.resolveTemplates(templateValues, false, false);
        return this;
    }
    
    private JerseyUriBuilder resolveTemplates(final Map<String, Object> templateValues, final boolean encode, final boolean encodeSlashInPath) {
        if (templateValues == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("templateValues"));
        }
        for (final Map.Entry entry : templateValues.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                throw new IllegalArgumentException(LocalizationMessages.TEMPLATE_PARAM_NULL());
            }
        }
        this.scheme = UriTemplate.resolveTemplateValues(UriComponent.Type.SCHEME, this.scheme, false, templateValues);
        this.userInfo = UriTemplate.resolveTemplateValues(UriComponent.Type.USER_INFO, this.userInfo, encode, templateValues);
        this.host = UriTemplate.resolveTemplateValues(UriComponent.Type.HOST, this.host, encode, templateValues);
        this.port = UriTemplate.resolveTemplateValues(UriComponent.Type.PORT, this.port, false, templateValues);
        this.authority = UriTemplate.resolveTemplateValues(UriComponent.Type.AUTHORITY, this.authority, encode, templateValues);
        final UriComponent.Type pathComponent = encodeSlashInPath ? UriComponent.Type.PATH_SEGMENT : UriComponent.Type.PATH;
        final String newPath = UriTemplate.resolveTemplateValues(pathComponent, this.path.toString(), encode, templateValues);
        this.path.setLength(0);
        this.path.append(newPath);
        final String newQuery = UriTemplate.resolveTemplateValues(UriComponent.Type.QUERY_PARAM, this.query.toString(), encode, templateValues);
        this.query.setLength(0);
        this.query.append(newQuery);
        this.fragment = UriTemplate.resolveTemplateValues(UriComponent.Type.FRAGMENT, this.fragment, encode, templateValues);
        return this;
    }
    
    public JerseyUriBuilder fragment(final String fragment) {
        this.fragment = ((fragment != null) ? this.encode(fragment, UriComponent.Type.FRAGMENT) : null);
        return this;
    }
    
    private void checkSsp() {
        if (this.ssp != null) {
            throw new IllegalArgumentException(LocalizationMessages.URI_BUILDER_SCHEMA_PART_OPAQUE());
        }
    }
    
    private void appendPath(final Path path) {
        if (path == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("path"));
        }
        this.appendPath(path.value());
    }
    
    private void appendPath(final String path) {
        this.appendPath(path, false);
    }
    
    private void appendPath(String segments, final boolean isSegment) {
        if (segments == null) {
            throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("segments"));
        }
        if (segments.isEmpty()) {
            return;
        }
        this.encodeMatrix();
        segments = this.encode(segments, isSegment ? UriComponent.Type.PATH_SEGMENT : UriComponent.Type.PATH);
        final boolean pathEndsInSlash = this.path.length() > 0 && this.path.charAt(this.path.length() - 1) == '/';
        final boolean segmentStartsWithSlash = segments.charAt(0) == '/';
        if (this.path.length() > 0 && !pathEndsInSlash && !segmentStartsWithSlash) {
            this.path.append('/');
        }
        else if (pathEndsInSlash && segmentStartsWithSlash) {
            segments = segments.substring(1);
            if (segments.isEmpty()) {
                return;
            }
        }
        this.path.append(segments);
    }
    
    private void encodeMatrix() {
        if (this.matrixParams == null || this.matrixParams.isEmpty()) {
            return;
        }
        for (final Map.Entry<String, List<String>> e : this.matrixParams.entrySet()) {
            final String name = e.getKey();
            for (final String value : e.getValue()) {
                this.path.append(';').append(name);
                if (!value.isEmpty()) {
                    this.path.append('=').append(value);
                }
            }
        }
        this.matrixParams = null;
    }
    
    private void encodeQuery() {
        if (this.queryParams == null || this.queryParams.isEmpty()) {
            return;
        }
        for (final Map.Entry<String, List<String>> e : this.queryParams.entrySet()) {
            final String name = e.getKey();
            for (final String value : e.getValue()) {
                if (this.query.length() > 0) {
                    this.query.append('&');
                }
                this.query.append(name).append('=').append(value);
            }
        }
        this.queryParams = null;
    }
    
    private String encode(final String s, final UriComponent.Type type) {
        return UriComponent.contextualEncode(s, type, true);
    }
    
    public URI buildFromMap(final Map<String, ?> values) {
        return this._buildFromMap(true, true, values);
    }
    
    public URI buildFromMap(final Map<String, ?> values, final boolean encodeSlashInPath) {
        return this._buildFromMap(true, encodeSlashInPath, values);
    }
    
    public URI buildFromEncodedMap(final Map<String, ?> values) throws IllegalArgumentException, UriBuilderException {
        return this._buildFromMap(false, false, values);
    }
    
    private URI _buildFromMap(final boolean encode, final boolean encodeSlashInPath, final Map<String, ?> values) {
        if (this.ssp != null) {
            throw new IllegalArgumentException(LocalizationMessages.URI_BUILDER_SCHEMA_PART_OPAQUE());
        }
        this.encodeMatrix();
        this.encodeQuery();
        final String uri = UriTemplate.createURI(this.scheme, this.authority, this.userInfo, this.host, this.port, this.path.toString(), this.query.toString(), this.fragment, values, encode, encodeSlashInPath);
        return this.createURI(uri);
    }
    
    public URI build(final Object... values) {
        return this._build(true, true, values);
    }
    
    public URI build(final Object[] values, final boolean encodeSlashInPath) {
        return this._build(true, encodeSlashInPath, values);
    }
    
    public URI buildFromEncoded(final Object... values) {
        return this._build(false, false, values);
    }
    
    public String toTemplate() {
        this.encodeMatrix();
        this.encodeQuery();
        final StringBuilder sb = new StringBuilder();
        if (this.scheme != null) {
            sb.append(this.scheme).append(':');
        }
        if (this.ssp != null) {
            sb.append(this.ssp);
        }
        else {
            boolean hasAuthority = false;
            if (this.userInfo != null || this.host != null || this.port != null) {
                hasAuthority = true;
                sb.append("//");
                if (this.userInfo != null && !this.userInfo.isEmpty()) {
                    sb.append(this.userInfo).append('@');
                }
                if (this.host != null) {
                    sb.append(this.host);
                }
                if (this.port != null) {
                    sb.append(':').append(this.port);
                }
            }
            else if (this.authority != null) {
                hasAuthority = true;
                sb.append("//").append(this.authority);
            }
            if (this.path.length() > 0) {
                if (hasAuthority && this.path.charAt(0) != '/') {
                    sb.append("/");
                }
                sb.append((CharSequence)this.path);
            }
            else if (hasAuthority && (this.query.length() > 0 || (this.fragment != null && !this.fragment.isEmpty()))) {
                sb.append("/");
            }
            if (this.query.length() > 0) {
                sb.append('?').append((CharSequence)this.query);
            }
        }
        if (this.fragment != null && !this.fragment.isEmpty()) {
            sb.append('#').append(this.fragment);
        }
        return sb.toString();
    }
    
    private URI _build(final boolean encode, final boolean encodeSlashInPath, final Object... values) {
        if (this.ssp == null) {
            this.encodeMatrix();
            this.encodeQuery();
            final String uri = UriTemplate.createURI(this.scheme, this.authority, this.userInfo, this.host, this.port, this.path.toString(), this.query.toString(), this.fragment, values, encode, encodeSlashInPath);
            return this.createURI(uri);
        }
        if (values == null || values.length == 0) {
            return this.createURI(this.create());
        }
        throw new IllegalArgumentException(LocalizationMessages.URI_BUILDER_SCHEMA_PART_OPAQUE());
    }
    
    private String create() {
        return UriComponent.encodeTemplateNames(this.toTemplate());
    }
    
    private URI createURI(final String uri) {
        try {
            return new URI(uri);
        }
        catch (final URISyntaxException ex) {
            throw new UriBuilderException((Throwable)ex);
        }
    }
    
    public String toString() {
        return this.toTemplate();
    }
    
    public boolean isAbsolute() {
        return this.scheme != null;
    }
}
