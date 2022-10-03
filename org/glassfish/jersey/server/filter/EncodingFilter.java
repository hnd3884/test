package org.glassfish.jersey.server.filter;

import org.glassfish.jersey.message.internal.HttpHeaderReader;
import java.util.Collection;
import java.lang.reflect.Type;
import java.io.IOException;
import java.util.Iterator;
import javax.ws.rs.NotAcceptableException;
import java.util.TreeSet;
import java.util.Collections;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerRequestContext;
import org.glassfish.jersey.spi.ContentEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import java.util.SortedSet;
import javax.inject.Inject;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.annotation.Priority;
import javax.ws.rs.container.ContainerResponseFilter;

@Priority(3000)
public final class EncodingFilter implements ContainerResponseFilter
{
    private static final String IDENTITY_ENCODING = "identity";
    @Inject
    private InjectionManager injectionManager;
    private volatile SortedSet<String> supportedEncodings;
    
    public EncodingFilter() {
        this.supportedEncodings = null;
    }
    
    @SafeVarargs
    public static void enableFor(final ResourceConfig rc, final Class<? extends ContentEncoder>... encoders) {
        rc.registerClasses((Class<?>[])encoders).registerClasses(EncodingFilter.class);
    }
    
    public void filter(final ContainerRequestContext request, final ContainerResponseContext response) throws IOException {
        if (!response.hasEntity()) {
            return;
        }
        final List<String> varyHeader = (List<String>)response.getStringHeaders().get((Object)"Vary");
        if (varyHeader == null || !varyHeader.contains("Accept-Encoding")) {
            response.getHeaders().add((Object)"Vary", (Object)"Accept-Encoding");
        }
        if (response.getHeaders().getFirst((Object)"Content-Encoding") != null) {
            return;
        }
        final List<String> acceptEncoding = (List<String>)request.getHeaders().get((Object)"Accept-Encoding");
        if (acceptEncoding == null || acceptEncoding.isEmpty()) {
            return;
        }
        final List<ContentEncoding> encodings = new ArrayList<ContentEncoding>();
        for (final String input : acceptEncoding) {
            final String[] split;
            final String[] tokens = split = input.split(",");
            for (final String token : split) {
                try {
                    final ContentEncoding encoding = ContentEncoding.fromString(token);
                    encodings.add(encoding);
                }
                catch (final ParseException e) {
                    Logger.getLogger(EncodingFilter.class.getName()).log(Level.WARNING, e.getLocalizedMessage(), e);
                }
            }
        }
        Collections.sort(encodings);
        encodings.add(new ContentEncoding("identity", -1));
        final SortedSet<String> acceptedEncodings = new TreeSet<String>(this.getSupportedEncodings());
        boolean anyRemaining = false;
        String contentEncoding = null;
        for (final ContentEncoding encoding2 : encodings) {
            if (encoding2.q == 0) {
                if ("*".equals(encoding2.name)) {
                    break;
                }
                acceptedEncodings.remove(encoding2.name);
            }
            else if ("*".equals(encoding2.name)) {
                anyRemaining = true;
            }
            else {
                if (acceptedEncodings.contains(encoding2.name)) {
                    contentEncoding = encoding2.name;
                    break;
                }
                continue;
            }
        }
        if (contentEncoding == null) {
            if (!anyRemaining || acceptedEncodings.isEmpty()) {
                throw new NotAcceptableException();
            }
            contentEncoding = acceptedEncodings.first();
        }
        if (!"identity".equals(contentEncoding)) {
            response.getHeaders().putSingle((Object)"Content-Encoding", (Object)contentEncoding);
        }
    }
    
    SortedSet<String> getSupportedEncodings() {
        if (this.supportedEncodings == null) {
            final SortedSet<String> se = new TreeSet<String>();
            final List<ContentEncoder> encoders = this.injectionManager.getAllInstances((Type)ContentEncoder.class);
            for (final ContentEncoder encoder : encoders) {
                se.addAll((Collection<?>)encoder.getSupportedEncodings());
            }
            se.add("identity");
            this.supportedEncodings = se;
        }
        return this.supportedEncodings;
    }
    
    private static class ContentEncoding implements Comparable<ContentEncoding>
    {
        public final String name;
        public final int q;
        
        public ContentEncoding(final String encoding, final int q) {
            this.name = encoding;
            this.q = q;
        }
        
        public static ContentEncoding fromString(final String input) throws ParseException {
            final HttpHeaderReader reader = HttpHeaderReader.newInstance(input);
            reader.hasNext();
            return new ContentEncoding(reader.nextToken().toString(), HttpHeaderReader.readQualityFactorParameter(reader));
        }
        
        @Override
        public int hashCode() {
            return 41 * this.name.hashCode() + this.q;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj == this || (obj != null && obj instanceof ContentEncoding && this.name.equals(((ContentEncoding)obj).name) && this.q == ((ContentEncoding)obj).q);
        }
        
        @Override
        public int compareTo(final ContentEncoding o) {
            return Integer.compare(o.q, this.q);
        }
    }
}
