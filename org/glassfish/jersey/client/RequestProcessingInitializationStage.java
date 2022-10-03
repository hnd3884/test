package org.glassfish.jersey.client;

import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.model.internal.RankedComparator;
import java.util.List;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.internal.util.collection.Ref;
import javax.inject.Provider;
import java.util.function.Function;

public class RequestProcessingInitializationStage implements Function<ClientRequest, ClientRequest>
{
    private final Provider<Ref<ClientRequest>> requestRefProvider;
    private final MessageBodyWorkers workersProvider;
    private final Iterable<WriterInterceptor> writerInterceptors;
    private final Iterable<ReaderInterceptor> readerInterceptors;
    
    public RequestProcessingInitializationStage(final Provider<Ref<ClientRequest>> requestRefProvider, final MessageBodyWorkers workersProvider, final InjectionManager injectionManager) {
        this.requestRefProvider = requestRefProvider;
        this.workersProvider = workersProvider;
        this.writerInterceptors = (Iterable<WriterInterceptor>)Collections.unmodifiableList((List<?>)StreamSupport.stream(Providers.getAllProviders(injectionManager, (Class)WriterInterceptor.class, new RankedComparator()).spliterator(), false).collect((Collector<? super Object, ?, List<? extends T>>)Collectors.toList()));
        this.readerInterceptors = (Iterable<ReaderInterceptor>)Collections.unmodifiableList((List<?>)StreamSupport.stream(Providers.getAllProviders(injectionManager, (Class)ReaderInterceptor.class, new RankedComparator()).spliterator(), false).collect((Collector<? super Object, ?, List<? extends T>>)Collectors.toList()));
    }
    
    @Override
    public ClientRequest apply(final ClientRequest requestContext) {
        ((Ref)this.requestRefProvider.get()).set((Object)requestContext);
        requestContext.setWorkers(this.workersProvider);
        requestContext.setWriterInterceptors(this.writerInterceptors);
        requestContext.setReaderInterceptors(this.readerInterceptors);
        return requestContext;
    }
}
