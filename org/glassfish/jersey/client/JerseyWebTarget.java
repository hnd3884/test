package org.glassfish.jersey.client;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.client.Invocation;
import java.util.Iterator;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.LinkedList;
import org.glassfish.jersey.internal.guava.Preconditions;
import javax.ws.rs.core.Link;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.client.WebTarget;

public class JerseyWebTarget implements WebTarget, Initializable<JerseyWebTarget>
{
    private final ClientConfig config;
    private final UriBuilder targetUri;
    
    JerseyWebTarget(final String uri, final JerseyClient parent) {
        this(UriBuilder.fromUri(uri), parent.getConfiguration());
    }
    
    JerseyWebTarget(final URI uri, final JerseyClient parent) {
        this(UriBuilder.fromUri(uri), parent.getConfiguration());
    }
    
    JerseyWebTarget(final UriBuilder uriBuilder, final JerseyClient parent) {
        this(uriBuilder.clone(), parent.getConfiguration());
    }
    
    JerseyWebTarget(final Link link, final JerseyClient parent) {
        this(UriBuilder.fromUri(link.getUri()), parent.getConfiguration());
    }
    
    protected JerseyWebTarget(final UriBuilder uriBuilder, final JerseyWebTarget that) {
        this(uriBuilder, that.config);
    }
    
    protected JerseyWebTarget(final UriBuilder uriBuilder, final ClientConfig clientConfig) {
        clientConfig.checkClient();
        this.targetUri = uriBuilder;
        this.config = clientConfig.snapshot();
    }
    
    public URI getUri() {
        this.checkNotClosed();
        try {
            return this.targetUri.build(new Object[0]);
        }
        catch (final IllegalArgumentException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }
    
    private void checkNotClosed() {
        this.config.getClient().checkNotClosed();
    }
    
    public UriBuilder getUriBuilder() {
        this.checkNotClosed();
        return this.targetUri.clone();
    }
    
    public JerseyWebTarget path(final String path) throws NullPointerException {
        this.checkNotClosed();
        Preconditions.checkNotNull((Object)path, (Object)"path is 'null'.");
        return new JerseyWebTarget(this.getUriBuilder().path(path), this);
    }
    
    public JerseyWebTarget matrixParam(final String name, final Object... values) throws NullPointerException {
        this.checkNotClosed();
        Preconditions.checkNotNull((Object)name, (Object)"Matrix parameter name must not be 'null'.");
        if (values == null || values.length == 0 || (values.length == 1 && values[0] == null)) {
            return new JerseyWebTarget(this.getUriBuilder().replaceMatrixParam(name, (Object[])null), this);
        }
        checkForNullValues(name, values);
        return new JerseyWebTarget(this.getUriBuilder().matrixParam(name, values), this);
    }
    
    public JerseyWebTarget queryParam(final String name, final Object... values) throws NullPointerException {
        this.checkNotClosed();
        return new JerseyWebTarget(setQueryParam(this.getUriBuilder(), name, values), this);
    }
    
    private static UriBuilder setQueryParam(final UriBuilder uriBuilder, final String name, final Object[] values) {
        if (values == null || values.length == 0 || (values.length == 1 && values[0] == null)) {
            return uriBuilder.replaceQueryParam(name, (Object[])null);
        }
        checkForNullValues(name, values);
        return uriBuilder.queryParam(name, values);
    }
    
    private static void checkForNullValues(final String name, final Object[] values) {
        Preconditions.checkNotNull((Object)name, (Object)"name is 'null'.");
        final List<Integer> indexes = new LinkedList<Integer>();
        for (int i = 0; i < values.length; ++i) {
            if (values[i] == null) {
                indexes.add(i);
            }
        }
        final int failedIndexCount = indexes.size();
        if (failedIndexCount > 0) {
            String valueTxt;
            String indexTxt;
            if (failedIndexCount == 1) {
                valueTxt = "value";
                indexTxt = "index";
            }
            else {
                valueTxt = "values";
                indexTxt = "indexes";
            }
            throw new NullPointerException(String.format("'null' %s detected for parameter '%s' on %s : %s", valueTxt, name, indexTxt, indexes.toString()));
        }
    }
    
    public JerseyInvocation.Builder request() {
        this.checkNotClosed();
        return new JerseyInvocation.Builder(this.getUri(), this.config.snapshot());
    }
    
    public JerseyInvocation.Builder request(final String... acceptedResponseTypes) {
        this.checkNotClosed();
        final JerseyInvocation.Builder b = new JerseyInvocation.Builder(this.getUri(), this.config.snapshot());
        b.request().accept(acceptedResponseTypes);
        return b;
    }
    
    public JerseyInvocation.Builder request(final MediaType... acceptedResponseTypes) {
        this.checkNotClosed();
        final JerseyInvocation.Builder b = new JerseyInvocation.Builder(this.getUri(), this.config.snapshot());
        b.request().accept(acceptedResponseTypes);
        return b;
    }
    
    public JerseyWebTarget resolveTemplate(final String name, final Object value) throws NullPointerException {
        return this.resolveTemplate(name, value, true);
    }
    
    public JerseyWebTarget resolveTemplate(final String name, final Object value, final boolean encodeSlashInPath) throws NullPointerException {
        this.checkNotClosed();
        Preconditions.checkNotNull((Object)name, (Object)"name is 'null'.");
        Preconditions.checkNotNull(value, (Object)"value is 'null'.");
        return new JerseyWebTarget(this.getUriBuilder().resolveTemplate(name, value, encodeSlashInPath), this);
    }
    
    public JerseyWebTarget resolveTemplateFromEncoded(final String name, final Object value) throws NullPointerException {
        this.checkNotClosed();
        Preconditions.checkNotNull((Object)name, (Object)"name is 'null'.");
        Preconditions.checkNotNull(value, (Object)"value is 'null'.");
        return new JerseyWebTarget(this.getUriBuilder().resolveTemplateFromEncoded(name, value), this);
    }
    
    public JerseyWebTarget resolveTemplates(final Map<String, Object> templateValues) throws NullPointerException {
        return this.resolveTemplates(templateValues, true);
    }
    
    public JerseyWebTarget resolveTemplates(final Map<String, Object> templateValues, final boolean encodeSlashInPath) throws NullPointerException {
        this.checkNotClosed();
        this.checkTemplateValues(templateValues);
        if (templateValues.isEmpty()) {
            return this;
        }
        return new JerseyWebTarget(this.getUriBuilder().resolveTemplates((Map)templateValues, encodeSlashInPath), this);
    }
    
    public JerseyWebTarget resolveTemplatesFromEncoded(final Map<String, Object> templateValues) throws NullPointerException {
        this.checkNotClosed();
        this.checkTemplateValues(templateValues);
        if (templateValues.isEmpty()) {
            return this;
        }
        return new JerseyWebTarget(this.getUriBuilder().resolveTemplatesFromEncoded((Map)templateValues), this);
    }
    
    private void checkTemplateValues(final Map<String, Object> templateValues) throws NullPointerException {
        Preconditions.checkNotNull((Object)templateValues, (Object)"templateValues is 'null'.");
        for (final Map.Entry entry : templateValues.entrySet()) {
            Preconditions.checkNotNull(entry.getKey(), (Object)"name is 'null'.");
            Preconditions.checkNotNull(entry.getValue(), (Object)"value is 'null'.");
        }
    }
    
    public JerseyWebTarget register(final Class<?> providerClass) {
        this.checkNotClosed();
        this.config.register(providerClass);
        return this;
    }
    
    public JerseyWebTarget register(final Object provider) {
        this.checkNotClosed();
        this.config.register(provider);
        return this;
    }
    
    public JerseyWebTarget register(final Class<?> providerClass, final int bindingPriority) {
        this.checkNotClosed();
        this.config.register(providerClass, bindingPriority);
        return this;
    }
    
    public JerseyWebTarget register(final Class<?> providerClass, final Class<?>... contracts) {
        this.checkNotClosed();
        this.config.register(providerClass, contracts);
        return this;
    }
    
    public JerseyWebTarget register(final Class<?> providerClass, final Map<Class<?>, Integer> contracts) {
        this.checkNotClosed();
        this.config.register(providerClass, contracts);
        return this;
    }
    
    public JerseyWebTarget register(final Object provider, final int bindingPriority) {
        this.checkNotClosed();
        this.config.register(provider, bindingPriority);
        return this;
    }
    
    public JerseyWebTarget register(final Object provider, final Class<?>... contracts) {
        this.checkNotClosed();
        this.config.register(provider, contracts);
        return this;
    }
    
    public JerseyWebTarget register(final Object provider, final Map<Class<?>, Integer> contracts) {
        this.checkNotClosed();
        this.config.register(provider, contracts);
        return this;
    }
    
    public JerseyWebTarget property(final String name, final Object value) {
        this.checkNotClosed();
        this.config.property(name, value);
        return this;
    }
    
    public ClientConfig getConfiguration() {
        this.checkNotClosed();
        return this.config.getConfiguration();
    }
    
    public JerseyWebTarget preInitialize() {
        this.config.preInitialize();
        return this;
    }
    
    @Override
    public String toString() {
        return "JerseyWebTarget { " + this.targetUri.toTemplate() + " }";
    }
}
