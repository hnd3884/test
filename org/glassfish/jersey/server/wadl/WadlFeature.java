package org.glassfish.jersey.server.wadl;

import javax.inject.Singleton;
import org.glassfish.jersey.server.wadl.internal.WadlApplicationContextImpl;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.wadl.processor.WadlModelProcessor;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Feature;

public class WadlFeature implements Feature
{
    public boolean configure(final FeatureContext context) {
        final boolean disabled = PropertiesHelper.isProperty(context.getConfiguration().getProperty("jersey.config.server.wadl.disableWadl"));
        if (disabled) {
            return false;
        }
        context.register((Class)WadlModelProcessor.class);
        context.register((Object)new AbstractBinder() {
            protected void configure() {
                ((ClassBinding)this.bind((Class)WadlApplicationContextImpl.class).to((Class)WadlApplicationContext.class)).in((Class)Singleton.class);
            }
        });
        return true;
    }
}
