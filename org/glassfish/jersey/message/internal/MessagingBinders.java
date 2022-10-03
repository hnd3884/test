package org.glassfish.jersey.message.internal;

import org.glassfish.jersey.internal.inject.InstanceBinding;
import java.util.HashSet;
import java.util.Set;
import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.ServiceFinderBinder;
import org.glassfish.jersey.spi.HeaderDelegateProvider;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.inject.Singleton;
import javax.ws.rs.ext.MessageBodyReader;
import org.glassfish.jersey.internal.inject.ClassBinding;
import javax.ws.rs.RuntimeType;
import java.util.Map;
import org.glassfish.jersey.internal.inject.AbstractBinder;

public final class MessagingBinders
{
    private MessagingBinders() {
    }
    
    public static class MessageBodyProviders extends AbstractBinder
    {
        private final Map<String, Object> applicationProperties;
        private final RuntimeType runtimeType;
        
        public MessageBodyProviders(final Map<String, Object> applicationProperties, final RuntimeType runtimeType) {
            this.applicationProperties = applicationProperties;
            this.runtimeType = runtimeType;
        }
        
        @Override
        protected void configure() {
            this.bindSingletonWorker(ByteArrayProvider.class);
            this.bindSingletonWorker(DataSourceProvider.class);
            this.bindSingletonWorker(FileProvider.class);
            this.bindSingletonWorker(FormMultivaluedMapProvider.class);
            this.bindSingletonWorker(FormProvider.class);
            this.bindSingletonWorker(InputStreamProvider.class);
            this.bindSingletonWorker(BasicTypesMessageProvider.class);
            this.bindSingletonWorker(ReaderProvider.class);
            this.bindSingletonWorker(RenderedImageProvider.class);
            this.bindSingletonWorker(StringMessageProvider.class);
            this.bind(SourceProvider.StreamSourceReader.class).to((Class<? super Object>)MessageBodyReader.class).in(Singleton.class);
            this.bind(SourceProvider.SaxSourceReader.class).to((Class<? super Object>)MessageBodyReader.class).in(Singleton.class);
            this.bind(SourceProvider.DomSourceReader.class).to((Class<? super Object>)MessageBodyReader.class).in(Singleton.class);
            this.bind(StreamingOutputProvider.class).to((Class<? super Object>)MessageBodyWriter.class).in(Singleton.class);
            this.bind(SourceProvider.SourceWriter.class).to((Class<? super Object>)MessageBodyWriter.class).in(Singleton.class);
            this.install(new ServiceFinderBinder<Object>(HeaderDelegateProvider.class, this.applicationProperties, this.runtimeType));
        }
        
        private <T extends MessageBodyReader & MessageBodyWriter> void bindSingletonWorker(final Class<T> worker) {
            this.bind(worker).to((Class<? super Object>)MessageBodyReader.class).to(MessageBodyWriter.class).in(Singleton.class);
        }
    }
    
    public static class HeaderDelegateProviders extends AbstractBinder
    {
        private final Set<HeaderDelegateProvider> providers;
        
        public HeaderDelegateProviders() {
            final Set<HeaderDelegateProvider> providers = new HashSet<HeaderDelegateProvider>();
            providers.add(new CacheControlProvider());
            providers.add(new CookieProvider());
            providers.add(new DateProvider());
            providers.add(new EntityTagProvider());
            providers.add(new LinkProvider());
            providers.add(new LocaleProvider());
            providers.add(new MediaTypeProvider());
            providers.add(new NewCookieProvider());
            providers.add(new StringHeaderProvider());
            providers.add(new UriProvider());
            this.providers = providers;
        }
        
        @Override
        protected void configure() {
            this.providers.forEach(provider -> {
                final InstanceBinding instanceBinding = this.bind(provider).to((Class<? super Object>)HeaderDelegateProvider.class);
            });
        }
        
        public Set<HeaderDelegateProvider> getHeaderDelegateProviders() {
            return this.providers;
        }
    }
}
