package org.glassfish.jersey.jackson.internal;

import javax.inject.Singleton;
import org.glassfish.jersey.message.filtering.spi.ObjectGraphTransformer;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import org.glassfish.jersey.message.filtering.spi.ObjectProvider;
import javax.ws.rs.core.GenericType;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Feature;

public final class JacksonFilteringFeature implements Feature
{
    public boolean configure(final FeatureContext context) {
        final Configuration config = context.getConfiguration();
        if (!config.isRegistered((Class)Binder.class)) {
            context.register((Object)new Binder());
            return true;
        }
        return false;
    }
    
    private static final class Binder extends AbstractBinder
    {
        protected void configure() {
            ((ClassBinding)((ClassBinding)this.bindAsContract((Class)JacksonObjectProvider.class).to((GenericType)new GenericType<ObjectProvider<FilterProvider>>() {})).to((GenericType)new GenericType<ObjectGraphTransformer<FilterProvider>>() {})).in((Class)Singleton.class);
        }
    }
}
