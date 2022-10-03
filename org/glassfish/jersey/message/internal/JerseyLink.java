package org.glassfish.jersey.message.internal;

import org.glassfish.jersey.uri.UriTemplate;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import javax.ws.rs.core.UriBuilder;
import java.util.Map;
import java.net.URI;
import javax.ws.rs.core.Link;

public final class JerseyLink extends Link
{
    private final URI uri;
    private final Map<String, String> params;
    
    private JerseyLink(final URI uri, final Map<String, String> params) {
        this.uri = uri;
        this.params = params;
    }
    
    public URI getUri() {
        return this.uri;
    }
    
    public UriBuilder getUriBuilder() {
        return new JerseyUriBuilder().uri(this.uri);
    }
    
    public String getRel() {
        return this.params.get("rel");
    }
    
    public List<String> getRels() {
        final String rels = this.params.get("rel");
        return (rels == null) ? Collections.emptyList() : Arrays.asList(rels.split(" +"));
    }
    
    public String getTitle() {
        return this.params.get("title");
    }
    
    public String getType() {
        return this.params.get("type");
    }
    
    public Map<String, String> getParams() {
        return this.params;
    }
    
    public String toString() {
        return LinkProvider.stringfy(this);
    }
    
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Link) {
            final Link otherLink = (Link)other;
            return this.uri.equals(otherLink.getUri()) && this.params.equals(otherLink.getParams());
        }
        return false;
    }
    
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + ((this.uri != null) ? this.uri.hashCode() : 0);
        hash = 89 * hash + ((this.params != null) ? this.params.hashCode() : 0);
        return hash;
    }
    
    public static class Builder implements Link.Builder
    {
        private UriBuilder uriBuilder;
        private URI baseUri;
        private Map<String, String> params;
        
        public Builder() {
            this.uriBuilder = new JerseyUriBuilder();
            this.baseUri = null;
            this.params = new HashMap<String, String>();
        }
        
        public Builder link(final Link link) {
            this.uriBuilder.uri(link.getUri());
            this.params.clear();
            this.params.putAll(link.getParams());
            return this;
        }
        
        public Builder link(final String link) {
            LinkProvider.initBuilder(this, link);
            return this;
        }
        
        public Builder uri(final URI uri) {
            this.uriBuilder = UriBuilder.fromUri(uri);
            return this;
        }
        
        public Builder uri(final String uri) {
            this.uriBuilder = UriBuilder.fromUri(uri);
            return this;
        }
        
        public Builder uriBuilder(final UriBuilder uriBuilder) {
            this.uriBuilder = UriBuilder.fromUri(uriBuilder.toTemplate());
            return this;
        }
        
        public Link.Builder baseUri(final URI uri) {
            this.baseUri = uri;
            return (Link.Builder)this;
        }
        
        public Link.Builder baseUri(final String uri) {
            this.baseUri = URI.create(uri);
            return (Link.Builder)this;
        }
        
        public Builder rel(final String rel) {
            final String rels = this.params.get("rel");
            this.param("rel", (rels == null) ? rel : (rels + " " + rel));
            return this;
        }
        
        public Builder title(final String title) {
            this.param("title", title);
            return this;
        }
        
        public Builder type(final String type) {
            this.param("type", type);
            return this;
        }
        
        public Builder param(final String name, final String value) {
            if (name == null || value == null) {
                throw new IllegalArgumentException("Link parameter name or value is null");
            }
            this.params.put(name, value);
            return this;
        }
        
        public JerseyLink build(final Object... values) {
            final URI linkUri = this.resolveLinkUri(values);
            return new JerseyLink(linkUri, Collections.unmodifiableMap((Map<?, ?>)new HashMap<Object, Object>(this.params)), null);
        }
        
        public Link buildRelativized(final URI uri, final Object... values) {
            final URI linkUri = UriTemplate.relativize(uri, this.resolveLinkUri(values));
            return new JerseyLink(linkUri, Collections.unmodifiableMap((Map<?, ?>)new HashMap<Object, Object>(this.params)), null);
        }
        
        private URI resolveLinkUri(final Object[] values) {
            final URI linkUri = this.uriBuilder.build(values);
            if (this.baseUri == null || linkUri.isAbsolute()) {
                return UriTemplate.normalize(linkUri);
            }
            return UriTemplate.resolve(this.baseUri, linkUri);
        }
    }
}
