package org.glassfish.jersey.jaxb.internal;

import javax.inject.Singleton;
import javax.ws.rs.ext.ParamConverterProvider;
import org.glassfish.jersey.internal.inject.ClassBinding;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import org.glassfish.jersey.internal.inject.AbstractBinder;

@ConstrainedTo(RuntimeType.SERVER)
public class JaxbParamConverterBinder extends AbstractBinder
{
    protected void configure() {
        ((ClassBinding)this.bind((Class)JaxbStringReaderProvider.RootElementProvider.class).to((Class)ParamConverterProvider.class)).in((Class)Singleton.class);
    }
}
