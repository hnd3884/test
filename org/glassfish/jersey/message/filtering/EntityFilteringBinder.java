package org.glassfish.jersey.message.filtering;

import org.glassfish.jersey.message.filtering.spi.ObjectGraphTransformer;
import org.glassfish.jersey.message.filtering.spi.ObjectGraph;
import javax.ws.rs.core.GenericType;
import org.glassfish.jersey.message.filtering.spi.ObjectProvider;
import org.glassfish.jersey.message.filtering.spi.EntityGraphProvider;
import javax.inject.Singleton;
import org.glassfish.jersey.message.filtering.spi.EntityInspector;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.internal.inject.AbstractBinder;

final class EntityFilteringBinder extends AbstractBinder
{
    protected void configure() {
        ((ClassBinding)this.bind((Class)EntityInspectorImpl.class).to((Class)EntityInspector.class)).in((Class)Singleton.class);
        ((ClassBinding)this.bind((Class)EntityGraphProviderImpl.class).to((Class)EntityGraphProvider.class)).in((Class)Singleton.class);
        ((ClassBinding)((ClassBinding)((ClassBinding)((ClassBinding)((ClassBinding)((ClassBinding)this.bindAsContract((Class)ObjectGraphProvider.class).to((Class)ObjectProvider.class)).to((GenericType)new GenericType<ObjectProvider<Object>>() {})).to((GenericType)new GenericType<ObjectProvider<ObjectGraph>>() {})).to((Class)ObjectGraphTransformer.class)).to((GenericType)new GenericType<ObjectGraphTransformer<Object>>() {})).to((GenericType)new GenericType<ObjectGraphTransformer<ObjectGraph>>() {})).in((Class)Singleton.class);
    }
}
