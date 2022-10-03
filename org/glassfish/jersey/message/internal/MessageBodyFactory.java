package org.glassfish.jersey.message.internal;

import java.util.Arrays;
import java.util.LinkedList;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.util.HashMap;
import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.BootstrapConfigurator;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.logging.Level;
import javax.ws.rs.ext.WriterInterceptor;
import java.io.OutputStream;
import java.io.IOException;
import javax.ws.rs.WebApplicationException;
import javax.xml.transform.Source;
import java.io.Closeable;
import javax.ws.rs.ext.ReaderInterceptor;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashSet;
import org.glassfish.jersey.internal.guava.Primitives;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;
import org.glassfish.jersey.internal.PropertiesDelegate;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.glassfish.jersey.internal.util.collection.KeyComparatorLinkedHashMap;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import org.glassfish.jersey.internal.inject.Providers;
import java.util.ArrayList;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.internal.util.collection.DataStructures;
import org.glassfish.jersey.internal.util.collection.KeyComparatorHashMap;
import javax.ws.rs.core.Configuration;
import java.util.function.Function;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.MessageBodyReader;
import java.util.Map;
import org.glassfish.jersey.message.WriterModel;
import org.glassfish.jersey.message.ReaderModel;
import java.util.List;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.message.AbstractEntityProviderModel;
import java.util.Comparator;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.internal.util.collection.KeyComparator;
import java.util.logging.Logger;
import org.glassfish.jersey.message.MessageBodyWorkers;

public class MessageBodyFactory implements MessageBodyWorkers
{
    private static final Logger LOGGER;
    public static final KeyComparator<MediaType> MEDIA_TYPE_KEY_COMPARATOR;
    private static final Comparator<AbstractEntityProviderModel<?>> WORKER_BY_TYPE_COMPARATOR;
    private InjectionManager injectionManager;
    private final Boolean legacyProviderOrdering;
    private List<ReaderModel> readers;
    private List<WriterModel> writers;
    private final Map<MediaType, List<MessageBodyReader>> readersCache;
    private final Map<MediaType, List<MessageBodyWriter>> writersCache;
    private static final int LOOKUP_CACHE_INITIAL_CAPACITY = 32;
    private static final float LOOKUP_CACHE_LOAD_FACTOR = 0.75f;
    private final Map<Class<?>, List<ReaderModel>> mbrTypeLookupCache;
    private final Map<Class<?>, List<WriterModel>> mbwTypeLookupCache;
    private final Map<Class<?>, List<MediaType>> typeToMediaTypeReadersCache;
    private final Map<Class<?>, List<MediaType>> typeToMediaTypeWritersCache;
    private final Map<ModelLookupKey, List<ReaderModel>> mbrLookupCache;
    private final Map<ModelLookupKey, List<WriterModel>> mbwLookupCache;
    private static final Function<WriterModel, MessageBodyWriter> MODEL_TO_WRITER;
    private static final Function<ReaderModel, MessageBodyReader> MODEL_TO_READER;
    
    public MessageBodyFactory(final Configuration configuration) {
        this.readersCache = new KeyComparatorHashMap<MediaType, List<MessageBodyReader>>(MessageBodyFactory.MEDIA_TYPE_KEY_COMPARATOR);
        this.writersCache = new KeyComparatorHashMap<MediaType, List<MessageBodyWriter>>(MessageBodyFactory.MEDIA_TYPE_KEY_COMPARATOR);
        this.mbrTypeLookupCache = (Map<Class<?>, List<ReaderModel>>)DataStructures.createConcurrentMap(32, 0.75f, DataStructures.DEFAULT_CONCURENCY_LEVEL);
        this.mbwTypeLookupCache = (Map<Class<?>, List<WriterModel>>)DataStructures.createConcurrentMap(32, 0.75f, DataStructures.DEFAULT_CONCURENCY_LEVEL);
        this.typeToMediaTypeReadersCache = (Map<Class<?>, List<MediaType>>)DataStructures.createConcurrentMap(32, 0.75f, DataStructures.DEFAULT_CONCURENCY_LEVEL);
        this.typeToMediaTypeWritersCache = (Map<Class<?>, List<MediaType>>)DataStructures.createConcurrentMap(32, 0.75f, DataStructures.DEFAULT_CONCURENCY_LEVEL);
        this.mbrLookupCache = (Map<ModelLookupKey, List<ReaderModel>>)DataStructures.createConcurrentMap(32, 0.75f, DataStructures.DEFAULT_CONCURENCY_LEVEL);
        this.mbwLookupCache = (Map<ModelLookupKey, List<WriterModel>>)DataStructures.createConcurrentMap(32, 0.75f, DataStructures.DEFAULT_CONCURENCY_LEVEL);
        this.legacyProviderOrdering = (configuration != null && PropertiesHelper.isProperty(configuration.getProperty("jersey.config.workers.legacyOrdering")));
    }
    
    public void initialize(final InjectionManager injectionManager) {
        this.injectionManager = injectionManager;
        this.readers = new ArrayList<ReaderModel>();
        final Set<MessageBodyReader> customMbrs = Providers.getCustomProviders(injectionManager, MessageBodyReader.class);
        final Set<MessageBodyReader> mbrs = Providers.getProviders(injectionManager, MessageBodyReader.class);
        addReaders(this.readers, customMbrs, true);
        mbrs.removeAll(customMbrs);
        addReaders(this.readers, mbrs, false);
        if (this.legacyProviderOrdering) {
            this.readers.sort((Comparator<? super ReaderModel>)new LegacyWorkerComparator((Class)MessageBodyReader.class));
            for (final ReaderModel model : this.readers) {
                for (final MediaType mt : model.declaredTypes()) {
                    List<MessageBodyReader> readerList = this.readersCache.get(mt);
                    if (readerList == null) {
                        readerList = new ArrayList<MessageBodyReader>();
                        this.readersCache.put(mt, readerList);
                    }
                    readerList.add(model.provider());
                }
            }
        }
        this.writers = new ArrayList<WriterModel>();
        final Set<MessageBodyWriter> customMbws = Providers.getCustomProviders(injectionManager, MessageBodyWriter.class);
        final Set<MessageBodyWriter> mbws = Providers.getProviders(injectionManager, MessageBodyWriter.class);
        addWriters(this.writers, customMbws, true);
        mbws.removeAll(customMbws);
        addWriters(this.writers, mbws, false);
        if (this.legacyProviderOrdering) {
            this.writers.sort((Comparator<? super WriterModel>)new LegacyWorkerComparator((Class)MessageBodyWriter.class));
            for (final AbstractEntityProviderModel<MessageBodyWriter> model2 : this.writers) {
                for (final MediaType mt2 : model2.declaredTypes()) {
                    List<MessageBodyWriter> writerList = this.writersCache.get(mt2);
                    if (writerList == null) {
                        writerList = new ArrayList<MessageBodyWriter>();
                        this.writersCache.put(mt2, writerList);
                    }
                    writerList.add(model2.provider());
                }
            }
        }
    }
    
    private static void addReaders(final List<ReaderModel> models, final Set<MessageBodyReader> readers, final boolean custom) {
        for (final MessageBodyReader provider : readers) {
            final List<MediaType> values = MediaTypes.createFrom(provider.getClass().getAnnotation(Consumes.class));
            models.add(new ReaderModel(provider, values, custom));
        }
    }
    
    private static void addWriters(final List<WriterModel> models, final Set<MessageBodyWriter> writers, final boolean custom) {
        for (final MessageBodyWriter provider : writers) {
            final List<MediaType> values = MediaTypes.createFrom(provider.getClass().getAnnotation(Produces.class));
            models.add(new WriterModel(provider, values, custom));
        }
    }
    
    @Override
    public Map<MediaType, List<MessageBodyReader>> getReaders(final MediaType mediaType) {
        final Map<MediaType, List<MessageBodyReader>> subSet = new KeyComparatorLinkedHashMap<MediaType, List<MessageBodyReader>>(MessageBodyFactory.MEDIA_TYPE_KEY_COMPARATOR);
        getCompatibleProvidersMap(mediaType, this.readers, subSet);
        return subSet;
    }
    
    @Override
    public Map<MediaType, List<MessageBodyWriter>> getWriters(final MediaType mediaType) {
        final Map<MediaType, List<MessageBodyWriter>> subSet = new KeyComparatorLinkedHashMap<MediaType, List<MessageBodyWriter>>(MessageBodyFactory.MEDIA_TYPE_KEY_COMPARATOR);
        getCompatibleProvidersMap(mediaType, this.writers, subSet);
        return subSet;
    }
    
    @Override
    public String readersToString(final Map<MediaType, List<MessageBodyReader>> readers) {
        return this.toString(readers);
    }
    
    @Override
    public String writersToString(final Map<MediaType, List<MessageBodyWriter>> writers) {
        return this.toString(writers);
    }
    
    private <T> String toString(final Map<MediaType, List<T>> set) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        for (final Map.Entry<MediaType, List<T>> e : set.entrySet()) {
            pw.append(e.getKey().toString()).println(" ->");
            for (final T t : e.getValue()) {
                pw.append("  ").println(t.getClass().getName());
            }
        }
        pw.flush();
        return sw.toString();
    }
    
    @Override
    public <T> MessageBodyReader<T> getMessageBodyReader(final Class<T> c, final Type t, final Annotation[] as, final MediaType mediaType) {
        return this.getMessageBodyReader(c, t, as, mediaType, null);
    }
    
    @Override
    public <T> MessageBodyReader<T> getMessageBodyReader(final Class<T> c, final Type t, final Annotation[] as, final MediaType mediaType, final PropertiesDelegate propertiesDelegate) {
        MessageBodyReader<T> p = null;
        if (this.legacyProviderOrdering) {
            if (mediaType != null) {
                p = this._getMessageBodyReader(c, t, as, mediaType, mediaType, propertiesDelegate);
                if (p == null) {
                    p = this._getMessageBodyReader(c, t, as, mediaType, MediaTypes.getTypeWildCart(mediaType), propertiesDelegate);
                }
            }
            if (p == null) {
                p = this._getMessageBodyReader(c, t, as, mediaType, MediaType.WILDCARD_TYPE, propertiesDelegate);
            }
        }
        else {
            p = this._getMessageBodyReader(c, t, as, mediaType, this.readers, propertiesDelegate);
        }
        return p;
    }
    
    @Override
    public List<MediaType> getMessageBodyReaderMediaTypes(final Class<?> type, final Type genericType, final Annotation[] annotations) {
        final Set<MediaType> readableMediaTypes = new LinkedHashSet<MediaType>();
        for (final ReaderModel model : this.readers) {
            boolean readableWorker = false;
            for (final MediaType mt : model.declaredTypes()) {
                if (model.isReadable(type, genericType, annotations, mt)) {
                    readableMediaTypes.add(mt);
                    readableWorker = true;
                }
                if (!readableMediaTypes.contains(MediaType.WILDCARD_TYPE) && readableWorker && model.declaredTypes().contains(MediaType.WILDCARD_TYPE)) {
                    readableMediaTypes.add(MediaType.WILDCARD_TYPE);
                }
            }
        }
        final List<MediaType> mtl = new ArrayList<MediaType>(readableMediaTypes);
        mtl.sort(MediaTypes.PARTIAL_ORDER_COMPARATOR);
        return mtl;
    }
    
    private <T> boolean isCompatible(final AbstractEntityProviderModel<T> model, final Class c, final MediaType mediaType) {
        if (model.providedType().equals(Object.class) || model.providedType().isAssignableFrom(c) || c.isAssignableFrom(model.providedType())) {
            for (final MediaType mt : model.declaredTypes()) {
                if (mediaType == null) {
                    return true;
                }
                if (mediaType.isCompatible(mt)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private <T> MessageBodyReader<T> _getMessageBodyReader(final Class<T> c, final Type t, final Annotation[] as, final MediaType mediaType, final List<ReaderModel> models, final PropertiesDelegate propertiesDelegate) {
        final MediaType lookupType = (mediaType == null || mediaType.getParameters().isEmpty()) ? mediaType : new MediaType(mediaType.getType(), mediaType.getSubtype());
        final ModelLookupKey lookupKey = new ModelLookupKey((Class)c, lookupType);
        List<ReaderModel> readers = this.mbrLookupCache.get(lookupKey);
        if (readers == null) {
            readers = new ArrayList<ReaderModel>();
            for (final ReaderModel model : models) {
                if (this.isCompatible((AbstractEntityProviderModel<Object>)model, c, mediaType)) {
                    readers.add(model);
                }
            }
            readers.sort((Comparator<? super ReaderModel>)new WorkerComparator((Class)c, mediaType));
            this.mbrLookupCache.put(lookupKey, readers);
        }
        if (readers.isEmpty()) {
            return null;
        }
        final TracingLogger tracingLogger = TracingLogger.getInstance(propertiesDelegate);
        MessageBodyReader<T> selected = null;
        final Iterator<ReaderModel> iterator = readers.iterator();
        while (iterator.hasNext()) {
            final ReaderModel model2 = iterator.next();
            if (model2.isReadable(c, t, as, mediaType)) {
                selected = (MessageBodyReader<T>)model2.provider();
                tracingLogger.log(MsgTraceEvent.MBR_SELECTED, selected);
                break;
            }
            tracingLogger.log(MsgTraceEvent.MBR_NOT_READABLE, ((AbstractEntityProviderModel<Object>)model2).provider());
        }
        if (tracingLogger.isLogEnabled(MsgTraceEvent.MBR_SKIPPED)) {
            while (iterator.hasNext()) {
                final ReaderModel model2 = iterator.next();
                tracingLogger.log(MsgTraceEvent.MBR_SKIPPED, ((AbstractEntityProviderModel<Object>)model2).provider());
            }
        }
        return selected;
    }
    
    private <T> MessageBodyReader<T> _getMessageBodyReader(final Class<T> c, final Type t, final Annotation[] as, final MediaType mediaType, final MediaType lookup, final PropertiesDelegate propertiesDelegate) {
        final List<MessageBodyReader> readers = this.readersCache.get(lookup);
        if (readers == null) {
            return null;
        }
        final TracingLogger tracingLogger = TracingLogger.getInstance(propertiesDelegate);
        MessageBodyReader<T> selected = null;
        final Iterator<MessageBodyReader> iterator = readers.iterator();
        while (iterator.hasNext()) {
            final MessageBodyReader p = iterator.next();
            if (isReadable((MessageBodyReader<?>)p, c, t, as, mediaType)) {
                selected = (MessageBodyReader<T>)p;
                tracingLogger.log(MsgTraceEvent.MBR_SELECTED, selected);
                break;
            }
            tracingLogger.log(MsgTraceEvent.MBR_NOT_READABLE, p);
        }
        if (tracingLogger.isLogEnabled(MsgTraceEvent.MBR_SKIPPED)) {
            while (iterator.hasNext()) {
                final MessageBodyReader p = iterator.next();
                tracingLogger.log(MsgTraceEvent.MBR_SKIPPED, p);
            }
        }
        return selected;
    }
    
    @Override
    public <T> MessageBodyWriter<T> getMessageBodyWriter(final Class<T> c, final Type t, final Annotation[] as, final MediaType mediaType) {
        return this.getMessageBodyWriter(c, t, as, mediaType, null);
    }
    
    @Override
    public <T> MessageBodyWriter<T> getMessageBodyWriter(final Class<T> c, final Type t, final Annotation[] as, final MediaType mediaType, final PropertiesDelegate propertiesDelegate) {
        MessageBodyWriter<T> p = null;
        if (this.legacyProviderOrdering) {
            if (mediaType != null) {
                p = this._getMessageBodyWriter(c, t, as, mediaType, mediaType, propertiesDelegate);
                if (p == null) {
                    p = this._getMessageBodyWriter(c, t, as, mediaType, MediaTypes.getTypeWildCart(mediaType), propertiesDelegate);
                }
            }
            if (p == null) {
                p = this._getMessageBodyWriter(c, t, as, mediaType, MediaType.WILDCARD_TYPE, propertiesDelegate);
            }
        }
        else {
            p = this._getMessageBodyWriter(c, t, as, mediaType, this.writers, propertiesDelegate);
        }
        return p;
    }
    
    private <T> MessageBodyWriter<T> _getMessageBodyWriter(final Class<T> c, final Type t, final Annotation[] as, final MediaType mediaType, final List<WriterModel> models, final PropertiesDelegate propertiesDelegate) {
        final MediaType lookupType = (mediaType == null || mediaType.getParameters().isEmpty()) ? mediaType : new MediaType(mediaType.getType(), mediaType.getSubtype());
        final ModelLookupKey lookupKey = new ModelLookupKey((Class)c, lookupType);
        List<WriterModel> writers = this.mbwLookupCache.get(lookupKey);
        if (writers == null) {
            writers = new ArrayList<WriterModel>();
            for (final WriterModel model : models) {
                if (this.isCompatible((AbstractEntityProviderModel<Object>)model, c, mediaType)) {
                    writers.add(model);
                }
            }
            writers.sort((Comparator<? super WriterModel>)new WorkerComparator((Class)c, mediaType));
            this.mbwLookupCache.put(lookupKey, writers);
        }
        if (writers.isEmpty()) {
            return null;
        }
        final TracingLogger tracingLogger = TracingLogger.getInstance(propertiesDelegate);
        MessageBodyWriter<T> selected = null;
        final Iterator<WriterModel> iterator = writers.iterator();
        while (iterator.hasNext()) {
            final WriterModel model2 = iterator.next();
            if (model2.isWriteable(c, t, as, mediaType)) {
                selected = (MessageBodyWriter<T>)model2.provider();
                tracingLogger.log(MsgTraceEvent.MBW_SELECTED, selected);
                break;
            }
            tracingLogger.log(MsgTraceEvent.MBW_NOT_WRITEABLE, ((AbstractEntityProviderModel<Object>)model2).provider());
        }
        if (tracingLogger.isLogEnabled(MsgTraceEvent.MBW_SKIPPED)) {
            while (iterator.hasNext()) {
                final WriterModel model2 = iterator.next();
                tracingLogger.log(MsgTraceEvent.MBW_SKIPPED, ((AbstractEntityProviderModel<Object>)model2).provider());
            }
        }
        return selected;
    }
    
    private <T> MessageBodyWriter<T> _getMessageBodyWriter(final Class<T> c, final Type t, final Annotation[] as, final MediaType mediaType, final MediaType lookup, final PropertiesDelegate propertiesDelegate) {
        final List<MessageBodyWriter> writers = this.writersCache.get(lookup);
        if (writers == null) {
            return null;
        }
        final TracingLogger tracingLogger = TracingLogger.getInstance(propertiesDelegate);
        MessageBodyWriter<T> selected = null;
        final Iterator<MessageBodyWriter> iterator = writers.iterator();
        while (iterator.hasNext()) {
            final MessageBodyWriter p = iterator.next();
            if (isWriteable((MessageBodyWriter<?>)p, c, t, as, mediaType)) {
                selected = (MessageBodyWriter<T>)p;
                tracingLogger.log(MsgTraceEvent.MBW_SELECTED, selected);
                break;
            }
            tracingLogger.log(MsgTraceEvent.MBW_NOT_WRITEABLE, p);
        }
        if (tracingLogger.isLogEnabled(MsgTraceEvent.MBW_SKIPPED)) {
            while (iterator.hasNext()) {
                final MessageBodyWriter p = iterator.next();
                tracingLogger.log(MsgTraceEvent.MBW_SKIPPED, p);
            }
        }
        return selected;
    }
    
    private static <T> void getCompatibleProvidersMap(final MediaType mediaType, final List<? extends AbstractEntityProviderModel<T>> set, final Map<MediaType, List<T>> subSet) {
        if (mediaType.isWildcardType()) {
            getCompatibleProvidersList(mediaType, (List<? extends AbstractEntityProviderModel<Object>>)set, (Map<MediaType, List<Object>>)subSet);
        }
        else if (mediaType.isWildcardSubtype()) {
            getCompatibleProvidersList(mediaType, (List<? extends AbstractEntityProviderModel<Object>>)set, (Map<MediaType, List<Object>>)subSet);
            getCompatibleProvidersList(MediaType.WILDCARD_TYPE, (List<? extends AbstractEntityProviderModel<Object>>)set, (Map<MediaType, List<Object>>)subSet);
        }
        else {
            getCompatibleProvidersList(mediaType, (List<? extends AbstractEntityProviderModel<Object>>)set, (Map<MediaType, List<Object>>)subSet);
            getCompatibleProvidersList(MediaTypes.getTypeWildCart(mediaType), (List<? extends AbstractEntityProviderModel<Object>>)set, (Map<MediaType, List<Object>>)subSet);
            getCompatibleProvidersList(MediaType.WILDCARD_TYPE, (List<? extends AbstractEntityProviderModel<Object>>)set, (Map<MediaType, List<Object>>)subSet);
        }
    }
    
    private static <T> void getCompatibleProvidersList(final MediaType mediaType, final List<? extends AbstractEntityProviderModel<T>> set, final Map<MediaType, List<T>> subSet) {
        final List<T> providers = set.stream().filter(model -> model.declaredTypes().contains(mediaType)).map((Function<? super Object, ?>)AbstractEntityProviderModel::provider).collect((Collector<? super Object, ?, List<T>>)Collectors.toList());
        if (!providers.isEmpty()) {
            subSet.put(mediaType, Collections.unmodifiableList((List<? extends T>)providers));
        }
    }
    
    @Override
    public List<MediaType> getMessageBodyWriterMediaTypes(final Class<?> c, final Type t, final Annotation[] as) {
        final Set<MediaType> writeableMediaTypes = new LinkedHashSet<MediaType>();
        for (final WriterModel model : this.writers) {
            boolean writeableWorker = false;
            for (final MediaType mt : model.declaredTypes()) {
                if (model.isWriteable(c, t, as, mt)) {
                    writeableMediaTypes.add(mt);
                    writeableWorker = true;
                }
                if (!writeableMediaTypes.contains(MediaType.WILDCARD_TYPE) && writeableWorker && model.declaredTypes().contains(MediaType.WILDCARD_TYPE)) {
                    writeableMediaTypes.add(MediaType.WILDCARD_TYPE);
                }
            }
        }
        final List<MediaType> mtl = new ArrayList<MediaType>(writeableMediaTypes);
        mtl.sort(MediaTypes.PARTIAL_ORDER_COMPARATOR);
        return mtl;
    }
    
    @Override
    public List<MessageBodyWriter> getMessageBodyWritersForType(final Class<?> type) {
        return this.getWritersModelsForType(type).stream().map((Function<? super Object, ?>)MessageBodyFactory.MODEL_TO_WRITER).collect((Collector<? super Object, ?, List<MessageBodyWriter>>)Collectors.toList());
    }
    
    @Override
    public List<WriterModel> getWritersModelsForType(final Class<?> type) {
        final List<WriterModel> writerModels = this.mbwTypeLookupCache.get(type);
        if (writerModels != null) {
            return writerModels;
        }
        return this.processMessageBodyWritersForType(type);
    }
    
    private List<WriterModel> processMessageBodyWritersForType(final Class<?> clazz) {
        final List<WriterModel> suitableWriters = new ArrayList<WriterModel>();
        if (Response.class.isAssignableFrom(clazz)) {
            suitableWriters.addAll(this.writers);
        }
        else {
            final Class<?> wrapped = Primitives.wrap(clazz);
            for (final WriterModel model : this.writers) {
                if (model.providedType() == null || model.providedType() == clazz || model.providedType().isAssignableFrom(wrapped)) {
                    suitableWriters.add(model);
                }
            }
        }
        suitableWriters.sort(MessageBodyFactory.WORKER_BY_TYPE_COMPARATOR);
        this.mbwTypeLookupCache.put(clazz, suitableWriters);
        this.typeToMediaTypeWritersCache.put(clazz, getMessageBodyWorkersMediaTypesByType((List<? extends AbstractEntityProviderModel<Object>>)suitableWriters));
        return suitableWriters;
    }
    
    @Override
    public List<MediaType> getMessageBodyWriterMediaTypesByType(final Class<?> type) {
        if (!this.typeToMediaTypeWritersCache.containsKey(type)) {
            this.processMessageBodyWritersForType(type);
        }
        return this.typeToMediaTypeWritersCache.get(type);
    }
    
    @Override
    public List<MediaType> getMessageBodyReaderMediaTypesByType(final Class<?> type) {
        if (!this.typeToMediaTypeReadersCache.containsKey(type)) {
            this.processMessageBodyReadersForType(type);
        }
        return this.typeToMediaTypeReadersCache.get(type);
    }
    
    private static <T> List<MediaType> getMessageBodyWorkersMediaTypesByType(final List<? extends AbstractEntityProviderModel<T>> workerModels) {
        final Set<MediaType> mediaTypeSet = new HashSet<MediaType>();
        for (final AbstractEntityProviderModel<T> model : workerModels) {
            mediaTypeSet.addAll(model.declaredTypes());
        }
        final List<MediaType> mediaTypes = new ArrayList<MediaType>(mediaTypeSet);
        mediaTypes.sort(MediaTypes.PARTIAL_ORDER_COMPARATOR);
        return mediaTypes;
    }
    
    @Override
    public List<MessageBodyReader> getMessageBodyReadersForType(final Class<?> type) {
        return this.getReaderModelsForType(type).stream().map((Function<? super Object, ?>)MessageBodyFactory.MODEL_TO_READER).collect((Collector<? super Object, ?, List<MessageBodyReader>>)Collectors.toList());
    }
    
    @Override
    public List<ReaderModel> getReaderModelsForType(final Class<?> type) {
        if (!this.mbrTypeLookupCache.containsKey(type)) {
            this.processMessageBodyReadersForType(type);
        }
        return this.mbrTypeLookupCache.get(type);
    }
    
    private List<ReaderModel> processMessageBodyReadersForType(final Class<?> clazz) {
        final List<ReaderModel> suitableReaders = new ArrayList<ReaderModel>();
        final Class<?> wrapped = Primitives.wrap(clazz);
        for (final ReaderModel reader : this.readers) {
            if (reader.providedType() == null || reader.providedType() == clazz || reader.providedType().isAssignableFrom(wrapped)) {
                suitableReaders.add(reader);
            }
        }
        suitableReaders.sort(MessageBodyFactory.WORKER_BY_TYPE_COMPARATOR);
        this.mbrTypeLookupCache.put(clazz, suitableReaders);
        this.typeToMediaTypeReadersCache.put(clazz, getMessageBodyWorkersMediaTypesByType((List<? extends AbstractEntityProviderModel<Object>>)suitableReaders));
        return suitableReaders;
    }
    
    @Override
    public MediaType getMessageBodyWriterMediaType(final Class<?> c, final Type t, final Annotation[] as, final List<MediaType> acceptableMediaTypes) {
        for (final MediaType acceptable : acceptableMediaTypes) {
            for (final WriterModel model : this.writers) {
                for (final MediaType mt : model.declaredTypes()) {
                    if (mt.isCompatible(acceptable) && model.isWriteable(c, t, as, acceptable)) {
                        return MediaTypes.mostSpecific(mt, acceptable);
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public Object readFrom(final Class<?> rawType, final Type type, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final PropertiesDelegate propertiesDelegate, final InputStream entityStream, final Iterable<ReaderInterceptor> readerInterceptors, final boolean translateNce) throws WebApplicationException, IOException {
        final ReaderInterceptorExecutor executor = new ReaderInterceptorExecutor(rawType, type, annotations, mediaType, httpHeaders, propertiesDelegate, entityStream, this, readerInterceptors, translateNce, this.injectionManager);
        final TracingLogger tracingLogger = TracingLogger.getInstance(propertiesDelegate);
        final long timestamp = tracingLogger.timestamp(MsgTraceEvent.RI_SUMMARY);
        try {
            final Object instance = executor.proceed();
            if (!(instance instanceof Closeable) && !(instance instanceof Source)) {
                final InputStream stream = executor.getInputStream();
                if (stream != entityStream && stream != null) {
                    ReaderWriter.safelyClose(stream);
                }
            }
            return instance;
        }
        finally {
            tracingLogger.logDuration(MsgTraceEvent.RI_SUMMARY, timestamp, executor.getProcessedCount());
        }
    }
    
    @Override
    public OutputStream writeTo(final Object t, final Class<?> rawType, final Type type, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final PropertiesDelegate propertiesDelegate, final OutputStream entityStream, final Iterable<WriterInterceptor> writerInterceptors) throws IOException, WebApplicationException {
        final WriterInterceptorExecutor executor = new WriterInterceptorExecutor(t, rawType, type, annotations, mediaType, httpHeaders, propertiesDelegate, entityStream, this, writerInterceptors, this.injectionManager);
        final TracingLogger tracingLogger = TracingLogger.getInstance(propertiesDelegate);
        final long timestamp = tracingLogger.timestamp(MsgTraceEvent.WI_SUMMARY);
        try {
            executor.proceed();
        }
        finally {
            tracingLogger.logDuration(MsgTraceEvent.WI_SUMMARY, timestamp, executor.getProcessedCount());
        }
        return executor.getOutputStream();
    }
    
    public static boolean isWriteable(final MessageBodyWriter<?> provider, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        try {
            return provider.isWriteable((Class)type, genericType, annotations, mediaType);
        }
        catch (final Exception ex) {
            if (MessageBodyFactory.LOGGER.isLoggable(Level.FINE)) {
                MessageBodyFactory.LOGGER.log(Level.FINE, LocalizationMessages.ERROR_MBW_ISWRITABLE(provider.getClass().getName()), ex);
            }
            return false;
        }
    }
    
    public static boolean isReadable(final MessageBodyReader<?> provider, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        try {
            return provider.isReadable((Class)type, genericType, annotations, mediaType);
        }
        catch (final Exception ex) {
            if (MessageBodyFactory.LOGGER.isLoggable(Level.FINE)) {
                MessageBodyFactory.LOGGER.log(Level.FINE, LocalizationMessages.ERROR_MBR_ISREADABLE(provider.getClass().getName()), ex);
            }
            return false;
        }
    }
    
    static {
        LOGGER = Logger.getLogger(MessageBodyFactory.class.getName());
        MEDIA_TYPE_KEY_COMPARATOR = new KeyComparator<MediaType>() {
            private static final long serialVersionUID = 1616819828630827763L;
            
            @Override
            public boolean equals(final MediaType mt1, final MediaType mt2) {
                return mt1.isCompatible(mt2);
            }
            
            @Override
            public int hash(final MediaType mt) {
                return mt.getType().toLowerCase().hashCode() + mt.getSubtype().toLowerCase().hashCode();
            }
        };
        WORKER_BY_TYPE_COMPARATOR = new Comparator<AbstractEntityProviderModel<?>>() {
            @Override
            public int compare(final AbstractEntityProviderModel<?> o1, final AbstractEntityProviderModel<?> o2) {
                final Class<?> o1ProviderClassParam = o1.providedType();
                final Class<?> o2ProviderClassParam = o2.providedType();
                if (o1ProviderClassParam == o2ProviderClassParam) {
                    return this.compare(o2.declaredTypes(), o1.declaredTypes());
                }
                if (o1ProviderClassParam.isAssignableFrom(o2ProviderClassParam)) {
                    return 1;
                }
                if (o2ProviderClassParam.isAssignableFrom(o1ProviderClassParam)) {
                    return -1;
                }
                return 0;
            }
            
            private int compare(List<MediaType> mediaTypeList1, List<MediaType> mediaTypeList2) {
                mediaTypeList1 = (mediaTypeList1.isEmpty() ? MediaTypes.WILDCARD_TYPE_SINGLETON_LIST : mediaTypeList1);
                mediaTypeList2 = (mediaTypeList2.isEmpty() ? MediaTypes.WILDCARD_TYPE_SINGLETON_LIST : mediaTypeList2);
                return MediaTypes.MEDIA_TYPE_LIST_COMPARATOR.compare(mediaTypeList2, mediaTypeList1);
            }
        };
        MODEL_TO_WRITER = AbstractEntityProviderModel::provider;
        MODEL_TO_READER = AbstractEntityProviderModel::provider;
    }
    
    public static class MessageBodyWorkersConfigurator implements BootstrapConfigurator
    {
        private MessageBodyFactory messageBodyFactory;
        
        @Override
        public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
            this.messageBodyFactory = new MessageBodyFactory(bootstrapBag.getConfiguration());
            final InstanceBinding<MessageBodyFactory> binding = Bindings.service(this.messageBodyFactory).to((Class<? super Object>)MessageBodyWorkers.class);
            injectionManager.register(binding);
        }
        
        @Override
        public void postInit(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
            this.messageBodyFactory.initialize(injectionManager);
            bootstrapBag.setMessageBodyWorkers(this.messageBodyFactory);
        }
    }
    
    private static class DeclarationDistanceComparator<T> implements Comparator<T>
    {
        private final Class<T> declared;
        private final Map<Class, Integer> distanceMap;
        
        DeclarationDistanceComparator(final Class<T> declared) {
            this.distanceMap = new HashMap<Class, Integer>();
            this.declared = declared;
        }
        
        @Override
        public int compare(final T o1, final T o2) {
            final int d1 = this.getDistance(o1);
            final int d2 = this.getDistance(o2);
            return d2 - d1;
        }
        
        private int getDistance(final T t) {
            Integer distance = this.distanceMap.get(t.getClass());
            if (distance != null) {
                return distance;
            }
            final ReflectionHelper.DeclaringClassInterfacePair p = ReflectionHelper.getClass(t.getClass(), this.declared);
            final Class[] as = ReflectionHelper.getParameterizedClassArguments(p);
            Class a = (as != null) ? as[0] : null;
            distance = 0;
            while (a != null && a != Object.class) {
                ++distance;
                a = a.getSuperclass();
            }
            this.distanceMap.put(t.getClass(), distance);
            return distance;
        }
    }
    
    private static class WorkerComparator<T> implements Comparator<AbstractEntityProviderModel<T>>
    {
        final Class wantedType;
        final MediaType wantedMediaType;
        
        private WorkerComparator(final Class wantedType, final MediaType wantedMediaType) {
            this.wantedType = wantedType;
            this.wantedMediaType = wantedMediaType;
        }
        
        @Override
        public int compare(final AbstractEntityProviderModel<T> modelA, final AbstractEntityProviderModel<T> modelB) {
            final int distance = this.compareTypeDistances(modelA.providedType(), modelB.providedType());
            if (distance != 0) {
                return distance;
            }
            final int mediaTypeComparison = this.getMediaTypeDistance(this.wantedMediaType, modelA.declaredTypes()) - this.getMediaTypeDistance(this.wantedMediaType, modelB.declaredTypes());
            if (mediaTypeComparison != 0) {
                return mediaTypeComparison;
            }
            if (modelA.isCustom() ^ modelB.isCustom()) {
                return modelA.isCustom() ? -1 : 1;
            }
            return 0;
        }
        
        private int getMediaTypeDistance(final MediaType wanted, final List<MediaType> mtl) {
            if (wanted == null) {
                return 0;
            }
            int distance = 2;
            for (final MediaType mt : mtl) {
                if (MediaTypes.typeEqual(wanted, mt)) {
                    return 0;
                }
                if (distance <= 1 || !MediaTypes.typeEqual(MediaTypes.getTypeWildCart(wanted), mt)) {
                    continue;
                }
                distance = 1;
            }
            return distance;
        }
        
        private int compareTypeDistances(final Class<?> providerClassParam1, final Class<?> providerClassParam2) {
            return this.getTypeDistance(providerClassParam1) - this.getTypeDistance(providerClassParam2);
        }
        
        private int getTypeDistance(final Class<?> classParam) {
            Class<?> tmp1 = this.wantedType;
            Class<?> tmp2 = classParam;
            final Iterator<Class<?>> it1 = this.getClassHierarchyIterator(tmp1);
            final Iterator<Class<?>> it2 = this.getClassHierarchyIterator(tmp2);
            int distance = 0;
            while (!this.wantedType.equals(tmp2) && !classParam.equals(tmp1)) {
                ++distance;
                if (!this.wantedType.equals(tmp2)) {
                    tmp2 = (it2.hasNext() ? it2.next() : null);
                }
                if (!classParam.equals(tmp1)) {
                    tmp1 = (it1.hasNext() ? it1.next() : null);
                }
                if (tmp2 == null && tmp1 == null) {
                    return Integer.MAX_VALUE;
                }
            }
            return distance;
        }
        
        private Iterator<Class<?>> getClassHierarchyIterator(final Class<?> classParam) {
            if (classParam == null) {
                return Collections.emptyList().iterator();
            }
            final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
            final LinkedList<Class<?>> unprocessed = new LinkedList<Class<?>>();
            boolean objectFound = false;
            unprocessed.add(classParam);
            while (!unprocessed.isEmpty()) {
                final Class<?> clazz = unprocessed.removeFirst();
                if (Object.class.equals(clazz)) {
                    objectFound = true;
                }
                else {
                    classes.add(clazz);
                }
                unprocessed.addAll(Arrays.asList(clazz.getInterfaces()));
                final Class<?> superclazz = clazz.getSuperclass();
                if (superclazz != null) {
                    unprocessed.add(superclazz);
                }
            }
            if (objectFound) {
                classes.add(Object.class);
            }
            return classes.iterator();
        }
    }
    
    private static class LegacyWorkerComparator<T> implements Comparator<AbstractEntityProviderModel<T>>
    {
        final DeclarationDistanceComparator<T> distanceComparator;
        
        private LegacyWorkerComparator(final Class<T> type) {
            this.distanceComparator = new DeclarationDistanceComparator<T>(type);
        }
        
        @Override
        public int compare(final AbstractEntityProviderModel<T> modelA, final AbstractEntityProviderModel<T> modelB) {
            if (modelA.isCustom() ^ modelB.isCustom()) {
                return modelA.isCustom() ? -1 : 1;
            }
            final MediaType mtA = modelA.declaredTypes().get(0);
            final MediaType mtB = modelB.declaredTypes().get(0);
            final int mediaTypeComparison = MediaTypes.PARTIAL_ORDER_COMPARATOR.compare(mtA, mtB);
            if (mediaTypeComparison != 0 && !mtA.isCompatible(mtB)) {
                return mediaTypeComparison;
            }
            return this.distanceComparator.compare(modelA.provider(), modelB.provider());
        }
    }
    
    private static class ModelLookupKey
    {
        final Class<?> clazz;
        final MediaType mediaType;
        
        private ModelLookupKey(final Class<?> clazz, final MediaType mediaType) {
            this.clazz = clazz;
            this.mediaType = mediaType;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final ModelLookupKey that = (ModelLookupKey)o;
            if (this.clazz != null) {
                if (!this.clazz.equals(that.clazz)) {
                    return false;
                }
            }
            else if (that.clazz != null) {
                return false;
            }
            if (this.mediaType != null) {
                if (!this.mediaType.equals((Object)that.mediaType)) {
                    return false;
                }
            }
            else if (that.mediaType != null) {
                return false;
            }
            return true;
            b = false;
            return b;
        }
        
        @Override
        public int hashCode() {
            int result = (this.clazz != null) ? this.clazz.hashCode() : 0;
            result = 31 * result + ((this.mediaType != null) ? this.mediaType.hashCode() : 0);
            return result;
        }
    }
}
