package org.glassfish.jersey.client.filter;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.ArrayList;
import java.util.Collection;
import java.lang.reflect.Type;
import org.glassfish.jersey.spi.ContentEncoder;
import java.util.TreeSet;
import java.io.IOException;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import java.util.logging.Logger;
import javax.ws.rs.client.ClientRequestContext;
import java.util.List;
import javax.inject.Inject;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.ws.rs.client.ClientRequestFilter;

public final class EncodingFilter implements ClientRequestFilter
{
    @Inject
    private InjectionManager injectionManager;
    private volatile List<Object> supportedEncodings;
    
    public EncodingFilter() {
        this.supportedEncodings = null;
    }
    
    public void filter(final ClientRequestContext request) throws IOException {
        if (this.getSupportedEncodings().isEmpty()) {
            return;
        }
        request.getHeaders().addAll((Object)"Accept-Encoding", (List)this.getSupportedEncodings());
        final String useEncoding = (String)request.getConfiguration().getProperty("jersey.config.client.useEncoding");
        if (useEncoding != null) {
            if (!this.getSupportedEncodings().contains(useEncoding)) {
                Logger.getLogger(this.getClass().getName()).warning(LocalizationMessages.USE_ENCODING_IGNORED("jersey.config.client.useEncoding", useEncoding, this.getSupportedEncodings()));
            }
            else if (request.hasEntity() && request.getHeaders().getFirst((Object)"Content-Encoding") == null) {
                request.getHeaders().putSingle((Object)"Content-Encoding", (Object)useEncoding);
            }
        }
    }
    
    List<Object> getSupportedEncodings() {
        if (this.supportedEncodings == null) {
            final SortedSet<String> se = new TreeSet<String>();
            final List<ContentEncoder> encoders = this.injectionManager.getAllInstances((Type)ContentEncoder.class);
            for (final ContentEncoder encoder : encoders) {
                se.addAll((Collection<?>)encoder.getSupportedEncodings());
            }
            this.supportedEncodings = new ArrayList<Object>(se);
        }
        return this.supportedEncodings;
    }
}
