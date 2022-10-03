package org.glassfish.jersey;

import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.ReaderInterceptorContext;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.inject.InjectionManagerSupplier;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.ws.rs.ext.WriterInterceptorContext;

public class InjectionManagerProvider
{
    public static InjectionManager getInjectionManager(final WriterInterceptorContext writerInterceptorContext) {
        if (!(writerInterceptorContext instanceof InjectionManagerSupplier)) {
            throw new IllegalArgumentException(LocalizationMessages.ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_FEATURE_WRITER_INTERCEPTOR_CONTEXT(writerInterceptorContext.getClass().getName()));
        }
        return ((InjectionManagerSupplier)writerInterceptorContext).getInjectionManager();
    }
    
    public static InjectionManager getInjectionManager(final ReaderInterceptorContext readerInterceptorContext) {
        if (!(readerInterceptorContext instanceof InjectionManagerSupplier)) {
            throw new IllegalArgumentException(LocalizationMessages.ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_FEATURE_READER_INTERCEPTOR_CONTEXT(readerInterceptorContext.getClass().getName()));
        }
        return ((InjectionManagerSupplier)readerInterceptorContext).getInjectionManager();
    }
    
    public static InjectionManager getInjectionManager(final FeatureContext featureContext) {
        if (!(featureContext instanceof InjectionManagerSupplier)) {
            throw new IllegalArgumentException(LocalizationMessages.ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_FEATURE_CONTEXT(featureContext.getClass().getName()));
        }
        return ((InjectionManagerSupplier)featureContext).getInjectionManager();
    }
}
