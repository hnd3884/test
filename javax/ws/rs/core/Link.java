package javax.ws.rs.core;

import java.util.Iterator;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.XmlAnyAttribute;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import javax.ws.rs.ext.RuntimeDelegate;
import java.util.Map;
import java.util.List;
import java.net.URI;

public abstract class Link
{
    public static final String TITLE = "title";
    public static final String REL = "rel";
    public static final String TYPE = "type";
    
    public abstract URI getUri();
    
    public abstract UriBuilder getUriBuilder();
    
    public abstract String getRel();
    
    public abstract List<String> getRels();
    
    public abstract String getTitle();
    
    public abstract String getType();
    
    public abstract Map<String, String> getParams();
    
    @Override
    public abstract String toString();
    
    public static Link valueOf(final String value) {
        final Builder b = RuntimeDelegate.getInstance().createLinkBuilder();
        b.link(value);
        return b.build(new Object[0]);
    }
    
    public static Builder fromUri(final URI uri) {
        final Builder b = RuntimeDelegate.getInstance().createLinkBuilder();
        b.uri(uri);
        return b;
    }
    
    public static Builder fromUri(final String uri) {
        final Builder b = RuntimeDelegate.getInstance().createLinkBuilder();
        b.uri(uri);
        return b;
    }
    
    public static Builder fromUriBuilder(final UriBuilder uriBuilder) {
        final Builder b = RuntimeDelegate.getInstance().createLinkBuilder();
        b.uriBuilder(uriBuilder);
        return b;
    }
    
    public static Builder fromLink(final Link link) {
        final Builder b = RuntimeDelegate.getInstance().createLinkBuilder();
        b.link(link);
        return b;
    }
    
    public static Builder fromPath(final String path) {
        return fromUriBuilder(UriBuilder.fromPath(path));
    }
    
    public static Builder fromResource(final Class<?> resource) {
        return fromUriBuilder(UriBuilder.fromResource(resource));
    }
    
    public static Builder fromMethod(final Class<?> resource, final String method) {
        return fromUriBuilder(UriBuilder.fromMethod(resource, method));
    }
    
    public static class JaxbLink
    {
        private URI uri;
        private Map<QName, Object> params;
        
        public JaxbLink() {
        }
        
        public JaxbLink(final URI uri) {
            this.uri = uri;
        }
        
        public JaxbLink(final URI uri, final Map<QName, Object> params) {
            this.uri = uri;
            this.params = params;
        }
        
        @XmlAttribute(name = "href")
        public URI getUri() {
            return this.uri;
        }
        
        @XmlAnyAttribute
        public Map<QName, Object> getParams() {
            if (this.params == null) {
                this.params = new HashMap<QName, Object>();
            }
            return this.params;
        }
        
        void setUri(final URI uri) {
            this.uri = uri;
        }
        
        void setParams(final Map<QName, Object> params) {
            this.params = params;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof JaxbLink)) {
                return false;
            }
            final JaxbLink jaxbLink = (JaxbLink)o;
            Label_0054: {
                if (this.uri != null) {
                    if (this.uri.equals(jaxbLink.uri)) {
                        break Label_0054;
                    }
                }
                else if (jaxbLink.uri == null) {
                    break Label_0054;
                }
                return false;
            }
            if (this.params == jaxbLink.params) {
                return true;
            }
            if (this.params == null) {
                return jaxbLink.params.isEmpty();
            }
            if (jaxbLink.params == null) {
                return this.params.isEmpty();
            }
            return this.params.equals(jaxbLink.params);
        }
        
        @Override
        public int hashCode() {
            int result = (this.uri != null) ? this.uri.hashCode() : 0;
            result = 31 * result + ((this.params != null && !this.params.isEmpty()) ? this.params.hashCode() : 0);
            return result;
        }
    }
    
    public static class JaxbAdapter extends XmlAdapter<JaxbLink, Link>
    {
        @Override
        public Link unmarshal(final JaxbLink v) {
            final Builder lb = Link.fromUri(v.getUri());
            for (final Map.Entry<QName, Object> e : v.getParams().entrySet()) {
                lb.param(e.getKey().getLocalPart(), e.getValue().toString());
            }
            return lb.build(new Object[0]);
        }
        
        @Override
        public JaxbLink marshal(final Link v) {
            final JaxbLink jl = new JaxbLink(v.getUri());
            for (final Map.Entry<String, String> e : v.getParams().entrySet()) {
                final String name = e.getKey();
                jl.getParams().put(new QName("", name), e.getValue());
            }
            return jl;
        }
    }
    
    public interface Builder
    {
        Builder link(final Link p0);
        
        Builder link(final String p0);
        
        Builder uri(final URI p0);
        
        Builder uri(final String p0);
        
        Builder baseUri(final URI p0);
        
        Builder baseUri(final String p0);
        
        Builder uriBuilder(final UriBuilder p0);
        
        Builder rel(final String p0);
        
        Builder title(final String p0);
        
        Builder type(final String p0);
        
        Builder param(final String p0, final String p1);
        
        Link build(final Object... p0);
        
        Link buildRelativized(final URI p0, final Object... p1);
    }
}
