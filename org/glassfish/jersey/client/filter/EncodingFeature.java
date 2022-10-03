package org.glassfish.jersey.client.filter;

import javax.ws.rs.core.FeatureContext;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.spi.ContentEncoder;
import javax.ws.rs.core.Feature;

public class EncodingFeature implements Feature
{
    private final String useEncoding;
    private final Class<?>[] encodingProviders;
    
    public EncodingFeature(final Class<?>... encodingProviders) {
        this((String)null, encodingProviders);
    }
    
    public EncodingFeature(final String useEncoding, final Class<?>... encoders) {
        this.useEncoding = useEncoding;
        Providers.ensureContract((Class)ContentEncoder.class, (Class[])encoders);
        this.encodingProviders = encoders;
    }
    
    public boolean configure(final FeatureContext context) {
        if (this.useEncoding != null && !context.getConfiguration().getProperties().containsKey("jersey.config.client.useEncoding")) {
            context.property("jersey.config.client.useEncoding", (Object)this.useEncoding);
        }
        for (final Class<?> provider : this.encodingProviders) {
            context.register((Class)provider);
        }
        final boolean enable = this.useEncoding != null || this.encodingProviders.length > 0;
        if (enable) {
            context.register((Class)EncodingFilter.class);
        }
        return enable;
    }
}
