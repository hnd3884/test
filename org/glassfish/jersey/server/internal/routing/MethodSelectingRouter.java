package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import org.glassfish.jersey.message.ReaderModel;
import javax.ws.rs.core.GenericType;
import org.glassfish.jersey.internal.guava.Primitives;
import java.lang.reflect.Method;
import org.glassfish.jersey.message.WriterModel;
import java.lang.reflect.Type;
import org.glassfish.jersey.message.internal.AcceptableMediaType;
import javax.ws.rs.NotAcceptableException;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import java.util.function.Function;
import javax.ws.rs.NotSupportedException;
import java.util.IdentityHashMap;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.container.ContainerRequestContext;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.Invocable;
import org.glassfish.jersey.server.model.ResourceMethod;
import java.util.Collection;
import javax.ws.rs.core.MediaType;
import java.util.LinkedHashSet;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.glassfish.jersey.message.MessageBodyWorkers;
import java.util.Comparator;
import java.util.logging.Logger;

final class MethodSelectingRouter implements Router
{
    private static final Logger LOGGER;
    private static final Comparator<ConsumesProducesAcceptor> CONSUMES_PRODUCES_ACCEPTOR_COMPARATOR;
    private final MessageBodyWorkers workers;
    private final Map<String, List<ConsumesProducesAcceptor>> consumesProducesAcceptors;
    private final Router router;
    
    MethodSelectingRouter(final MessageBodyWorkers workers, final List<MethodRouting> methodRoutings) {
        this.workers = workers;
        this.consumesProducesAcceptors = new HashMap<String, List<ConsumesProducesAcceptor>>();
        final Set<String> httpMethods = new HashSet<String>();
        for (final MethodRouting methodRouting : methodRoutings) {
            final String httpMethod = methodRouting.method.getHttpMethod();
            httpMethods.add(httpMethod);
            List<ConsumesProducesAcceptor> httpMethodBoundAcceptors = this.consumesProducesAcceptors.get(httpMethod);
            if (httpMethodBoundAcceptors == null) {
                httpMethodBoundAcceptors = new LinkedList<ConsumesProducesAcceptor>();
                this.consumesProducesAcceptors.put(httpMethod, httpMethodBoundAcceptors);
            }
            this.addAllConsumesProducesCombinations(httpMethodBoundAcceptors, methodRouting);
        }
        for (final String httpMethod2 : httpMethods) {
            Collections.sort((List<Object>)this.consumesProducesAcceptors.get(httpMethod2), (Comparator<? super Object>)MethodSelectingRouter.CONSUMES_PRODUCES_ACCEPTOR_COMPARATOR);
        }
        if (!this.consumesProducesAcceptors.containsKey("HEAD")) {
            this.router = this.createHeadEnrichedRouter();
        }
        else {
            this.router = this.createInternalRouter();
        }
    }
    
    private Router createInternalRouter() {
        return new Router() {
            @Override
            public Continuation apply(final RequestProcessingContext requestContext) {
                return Continuation.of(requestContext, MethodSelectingRouter.this.getMethodRouter(requestContext));
            }
        };
    }
    
    @Override
    public Continuation apply(final RequestProcessingContext requestContext) {
        return this.router.apply(requestContext);
    }
    
    private void addAllConsumesProducesCombinations(final List<ConsumesProducesAcceptor> acceptors, final MethodRouting methodRouting) {
        final ResourceMethod resourceMethod = methodRouting.method;
        final Set<MediaType> effectiveInputTypes = new LinkedHashSet<MediaType>();
        final boolean consumesFromWorkers = this.fillMediaTypes(effectiveInputTypes, resourceMethod, resourceMethod.getConsumedTypes(), true);
        final Set<MediaType> effectiveOutputTypes = new LinkedHashSet<MediaType>();
        final boolean producesFromWorkers = this.fillMediaTypes(effectiveOutputTypes, resourceMethod, resourceMethod.getProducedTypes(), false);
        final Set<ConsumesProducesAcceptor> acceptorSet = new HashSet<ConsumesProducesAcceptor>();
        for (final MediaType consumes : effectiveInputTypes) {
            for (final MediaType produces : effectiveOutputTypes) {
                acceptorSet.add(new ConsumesProducesAcceptor(new CombinedMediaType.EffectiveMediaType(consumes, consumesFromWorkers), new CombinedMediaType.EffectiveMediaType(produces, producesFromWorkers), methodRouting));
            }
        }
        acceptors.addAll(acceptorSet);
    }
    
    private boolean fillMediaTypes(final Set<MediaType> effectiveTypes, final ResourceMethod resourceMethod, final List<MediaType> methodTypes, final boolean inputTypes) {
        if (methodTypes.size() > 1 || !methodTypes.contains(MediaType.WILDCARD_TYPE)) {
            effectiveTypes.addAll(methodTypes);
        }
        boolean mediaTypesFromWorkers = effectiveTypes.isEmpty();
        if (mediaTypesFromWorkers) {
            final Invocable invocableMethod = resourceMethod.getInvocable();
            if (inputTypes) {
                this.fillInputTypesFromWorkers(effectiveTypes, invocableMethod);
            }
            else {
                this.fillOutputTypesFromWorkers(effectiveTypes, invocableMethod.getRawResponseType());
            }
            mediaTypesFromWorkers = !effectiveTypes.isEmpty();
            if (!mediaTypesFromWorkers) {
                if (inputTypes) {
                    effectiveTypes.addAll(this.workers.getMessageBodyReaderMediaTypesByType((Class)Object.class));
                }
                else {
                    effectiveTypes.addAll(this.workers.getMessageBodyWriterMediaTypesByType((Class)Object.class));
                }
                mediaTypesFromWorkers = true;
            }
        }
        return mediaTypesFromWorkers;
    }
    
    private void fillOutputTypesFromWorkers(final Set<MediaType> effectiveOutputTypes, final Class<?> returnEntityType) {
        effectiveOutputTypes.addAll(this.workers.getMessageBodyWriterMediaTypesByType((Class)returnEntityType));
    }
    
    private void fillInputTypesFromWorkers(final Set<MediaType> effectiveInputTypes, final Invocable invocableMethod) {
        for (final Parameter p : invocableMethod.getParameters()) {
            if (p.getSource() == Parameter.Source.ENTITY) {
                effectiveInputTypes.addAll(this.workers.getMessageBodyReaderMediaTypesByType((Class)p.getRawType()));
                break;
            }
        }
    }
    
    private Parameter getEntityParam(final Invocable invocable) {
        for (final Parameter parameter : invocable.getParameters()) {
            if (parameter.getSource() == Parameter.Source.ENTITY && !ContainerRequestContext.class.isAssignableFrom(parameter.getRawType())) {
                return parameter;
            }
        }
        return null;
    }
    
    private List<Router> getMethodRouter(final RequestProcessingContext context) {
        final ContainerRequest request = context.request();
        final List<ConsumesProducesAcceptor> acceptors = this.consumesProducesAcceptors.get(request.getMethod());
        if (acceptors == null) {
            throw new NotAllowedException(Response.status(Response.Status.METHOD_NOT_ALLOWED).allow((Set)this.consumesProducesAcceptors.keySet()).build());
        }
        final List<ConsumesProducesAcceptor> satisfyingAcceptors = new LinkedList<ConsumesProducesAcceptor>();
        final Set<ResourceMethod> differentInvokableMethods = Collections.newSetFromMap(new IdentityHashMap<ResourceMethod, Boolean>());
        for (final ConsumesProducesAcceptor cpi : acceptors) {
            if (cpi.isConsumable(request)) {
                satisfyingAcceptors.add(cpi);
                differentInvokableMethods.add(cpi.methodRouting.method);
            }
        }
        if (satisfyingAcceptors.isEmpty()) {
            throw new NotSupportedException();
        }
        final List<AcceptableMediaType> acceptableMediaTypes = request.getQualifiedAcceptableMediaTypes();
        final MediaType requestContentType = request.getMediaType();
        final MediaType effectiveContentType = (requestContentType == null) ? MediaType.WILDCARD_TYPE : requestContentType;
        final MethodSelector methodSelector = this.selectMethod(acceptableMediaTypes, satisfyingAcceptors, effectiveContentType, differentInvokableMethods.size() == 1);
        if (methodSelector.selected != null) {
            final RequestSpecificConsumesProducesAcceptor selected = methodSelector.selected;
            if (methodSelector.sameFitnessAcceptors != null) {
                this.reportMethodSelectionAmbiguity(acceptableMediaTypes, methodSelector.selected, methodSelector.sameFitnessAcceptors);
            }
            context.push(new Function<ContainerResponse, ContainerResponse>() {
                @Override
                public ContainerResponse apply(final ContainerResponse responseContext) {
                    if (responseContext.getMediaType() == null && (responseContext.hasEntity() || "HEAD".equals(request.getMethod()))) {
                        MediaType effectiveResponseType = MethodSelectingRouter.this.determineResponseMediaType(responseContext.getEntityClass(), responseContext.getEntityType(), methodSelector.selected, acceptableMediaTypes);
                        if (MediaTypes.isWildcard(effectiveResponseType)) {
                            if (!effectiveResponseType.isWildcardType() && !"application".equalsIgnoreCase(effectiveResponseType.getType())) {
                                throw new NotAcceptableException();
                            }
                            effectiveResponseType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
                        }
                        responseContext.setMediaType(effectiveResponseType);
                    }
                    return responseContext;
                }
            });
            return selected.methodRouting.routers;
        }
        throw new NotAcceptableException();
    }
    
    private MediaType determineResponseMediaType(final Class<?> entityClass, final Type entityType, final RequestSpecificConsumesProducesAcceptor selectedMethod, final List<AcceptableMediaType> acceptableMediaTypes) {
        if (usePreSelectedMediaType(selectedMethod, acceptableMediaTypes)) {
            return selectedMethod.produces.combinedType;
        }
        final ResourceMethod resourceMethod = selectedMethod.methodRouting.method;
        final Invocable invocable = resourceMethod.getInvocable();
        final Class<?> responseEntityClass = (entityClass == null) ? invocable.getRawRoutingResponseType() : entityClass;
        final Method handlingMethod = invocable.getHandlingMethod();
        final List<MediaType> methodProducesTypes = resourceMethod.getProducedTypes().isEmpty() ? Collections.singletonList(MediaType.WILDCARD_TYPE) : resourceMethod.getProducedTypes();
        final List<WriterModel> writersForEntityType = this.workers.getWritersModelsForType((Class)responseEntityClass);
        CombinedMediaType selected = null;
        for (final MediaType acceptableMediaType : acceptableMediaTypes) {
            for (final MediaType methodProducesType : methodProducesTypes) {
                if (!acceptableMediaType.isCompatible(methodProducesType)) {
                    continue;
                }
                for (final WriterModel model : writersForEntityType) {
                    for (final MediaType writerProduces : model.declaredTypes()) {
                        if (writerProduces.isCompatible(acceptableMediaType)) {
                            if (!methodProducesType.isCompatible(writerProduces)) {
                                continue;
                            }
                            final CombinedMediaType.EffectiveMediaType effectiveProduces = new CombinedMediaType.EffectiveMediaType(MediaTypes.mostSpecific(methodProducesType, writerProduces), false);
                            final CombinedMediaType candidate = CombinedMediaType.create(acceptableMediaType, effectiveProduces);
                            if (candidate == CombinedMediaType.NO_MATCH || (selected != null && CombinedMediaType.COMPARATOR.compare(candidate, selected) >= 0) || !model.isWriteable((Class)responseEntityClass, entityType, handlingMethod.getDeclaredAnnotations(), candidate.combinedType)) {
                                continue;
                            }
                            selected = candidate;
                        }
                    }
                }
            }
        }
        if (selected != null) {
            return selected.combinedType;
        }
        return selectedMethod.produces.combinedType;
    }
    
    private static boolean usePreSelectedMediaType(final RequestSpecificConsumesProducesAcceptor selectedMethod, final List<AcceptableMediaType> acceptableMediaTypes) {
        return (!selectedMethod.producesFromProviders && selectedMethod.methodRouting.method.getProducedTypes().size() == 1) || (acceptableMediaTypes.size() == 1 && !MediaTypes.isWildcard((MediaType)acceptableMediaTypes.get(0)));
    }
    
    private boolean isWriteable(final RequestSpecificConsumesProducesAcceptor candidate) {
        final Invocable invocable = candidate.methodRouting.method.getInvocable();
        final Class<?> responseType = Primitives.wrap((Class)invocable.getRawRoutingResponseType());
        if (Response.class.isAssignableFrom(responseType) || Void.class.isAssignableFrom(responseType)) {
            return true;
        }
        final Type genericType = invocable.getRoutingResponseType();
        final Type genericReturnType = (genericType instanceof GenericType) ? ((GenericType)genericType).getType() : genericType;
        for (final WriterModel model : this.workers.getWritersModelsForType((Class)responseType)) {
            if (model.isWriteable((Class)responseType, genericReturnType, invocable.getHandlingMethod().getDeclaredAnnotations(), candidate.produces.combinedType)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isReadable(final RequestSpecificConsumesProducesAcceptor candidate) {
        final Invocable invocable = candidate.methodRouting.method.getInvocable();
        final Method handlingMethod = invocable.getHandlingMethod();
        final Parameter entityParam = this.getEntityParam(invocable);
        if (entityParam == null) {
            return true;
        }
        final Class<?> entityType = entityParam.getRawType();
        for (final ReaderModel model : this.workers.getReaderModelsForType((Class)entityType)) {
            if (model.isReadable((Class)entityType, entityParam.getType(), handlingMethod.getDeclaredAnnotations(), candidate.consumes.combinedType)) {
                return true;
            }
        }
        return false;
    }
    
    private MethodSelector selectMethod(final List<AcceptableMediaType> acceptableMediaTypes, final List<ConsumesProducesAcceptor> satisfyingAcceptors, final MediaType effectiveContentType, final boolean singleInvokableMethod) {
        final MethodSelector method = new MethodSelector(null);
        final MethodSelector alternative = new MethodSelector(null);
        for (final MediaType acceptableMediaType : acceptableMediaTypes) {
            for (final ConsumesProducesAcceptor satisfiable : satisfyingAcceptors) {
                final CombinedMediaType produces = CombinedMediaType.create(acceptableMediaType, satisfiable.produces);
                if (produces != CombinedMediaType.NO_MATCH) {
                    final CombinedMediaType consumes = CombinedMediaType.create(effectiveContentType, satisfiable.consumes);
                    final RequestSpecificConsumesProducesAcceptor candidate = new RequestSpecificConsumesProducesAcceptor(consumes, produces, satisfiable.produces.isDerived(), satisfiable.methodRouting);
                    if (singleInvokableMethod) {
                        return new MethodSelector(candidate);
                    }
                    if (candidate.compareTo(method.selected) >= 0) {
                        continue;
                    }
                    if (method.selected == null || candidate.methodRouting.method != method.selected.methodRouting.method) {
                        if (this.isReadable(candidate) && this.isWriteable(candidate)) {
                            method.consider(candidate);
                        }
                        else {
                            alternative.consider(candidate);
                        }
                    }
                    else {
                        method.consider(candidate);
                    }
                }
            }
        }
        return (method.selected != null) ? method : alternative;
    }
    
    private void reportMethodSelectionAmbiguity(final List<AcceptableMediaType> acceptableTypes, final RequestSpecificConsumesProducesAcceptor selected, final List<RequestSpecificConsumesProducesAcceptor> sameFitnessAcceptors) {
        if (MethodSelectingRouter.LOGGER.isLoggable(Level.WARNING)) {
            final StringBuilder msgBuilder = new StringBuilder(LocalizationMessages.AMBIGUOUS_RESOURCE_METHOD(acceptableTypes)).append('\n');
            msgBuilder.append('\t').append(selected.methodRouting.method).append('\n');
            final Set<ResourceMethod> reportedMethods = new HashSet<ResourceMethod>();
            reportedMethods.add(selected.methodRouting.method);
            for (final RequestSpecificConsumesProducesAcceptor i : sameFitnessAcceptors) {
                if (!reportedMethods.contains(i.methodRouting.method)) {
                    msgBuilder.append('\t').append(i.methodRouting.method).append('\n');
                }
                reportedMethods.add(i.methodRouting.method);
            }
            MethodSelectingRouter.LOGGER.log(Level.WARNING, msgBuilder.toString());
        }
    }
    
    private Router createHeadEnrichedRouter() {
        return new Router() {
            @Override
            public Continuation apply(final RequestProcessingContext context) {
                final ContainerRequest request = context.request();
                if ("HEAD".equals(request.getMethod())) {
                    request.setMethodWithoutException("GET");
                    context.push(new Function<ContainerResponse, ContainerResponse>() {
                        @Override
                        public ContainerResponse apply(final ContainerResponse responseContext) {
                            responseContext.getRequestContext().setMethodWithoutException("HEAD");
                            return responseContext;
                        }
                    });
                }
                return Continuation.of(context, MethodSelectingRouter.this.getMethodRouter(context));
            }
        };
    }
    
    static {
        LOGGER = Logger.getLogger(MethodSelectingRouter.class.getName());
        CONSUMES_PRODUCES_ACCEPTOR_COMPARATOR = new Comparator<ConsumesProducesAcceptor>() {
            @Override
            public int compare(final ConsumesProducesAcceptor o1, final ConsumesProducesAcceptor o2) {
                final ResourceMethod model1 = o1.methodRouting.method;
                final ResourceMethod model2 = o2.methodRouting.method;
                int compared = this.compare(model1.getConsumedTypes(), model2.getConsumedTypes());
                if (compared != 0) {
                    return compared;
                }
                compared = this.compare(model1.getProducedTypes(), model2.getProducedTypes());
                if (compared != 0) {
                    return compared;
                }
                compared = MediaTypes.PARTIAL_ORDER_COMPARATOR.compare(o1.consumes.getMediaType(), o2.consumes.getMediaType());
                if (compared != 0) {
                    return compared;
                }
                return MediaTypes.PARTIAL_ORDER_COMPARATOR.compare(o1.produces.getMediaType(), o2.produces.getMediaType());
            }
            
            private int compare(List<MediaType> mediaTypeList1, List<MediaType> mediaTypeList2) {
                mediaTypeList1 = (mediaTypeList1.isEmpty() ? MediaTypes.WILDCARD_TYPE_SINGLETON_LIST : mediaTypeList1);
                mediaTypeList2 = (mediaTypeList2.isEmpty() ? MediaTypes.WILDCARD_TYPE_SINGLETON_LIST : mediaTypeList2);
                return MediaTypes.MEDIA_TYPE_LIST_COMPARATOR.compare(mediaTypeList1, mediaTypeList2);
            }
        };
    }
    
    private static class ConsumesProducesAcceptor
    {
        final CombinedMediaType.EffectiveMediaType consumes;
        final CombinedMediaType.EffectiveMediaType produces;
        final MethodRouting methodRouting;
        
        private ConsumesProducesAcceptor(final CombinedMediaType.EffectiveMediaType consumes, final CombinedMediaType.EffectiveMediaType produces, final MethodRouting methodRouting) {
            this.methodRouting = methodRouting;
            this.consumes = consumes;
            this.produces = produces;
        }
        
        boolean isConsumable(final ContainerRequest requestContext) {
            final MediaType contentType = requestContext.getMediaType();
            return contentType == null || this.consumes.getMediaType().isCompatible(contentType);
        }
        
        @Override
        public String toString() {
            return String.format("%s->%s:%s", this.consumes.getMediaType(), this.produces.getMediaType(), this.methodRouting);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ConsumesProducesAcceptor)) {
                return false;
            }
            final ConsumesProducesAcceptor that = (ConsumesProducesAcceptor)o;
            Label_0054: {
                if (this.consumes != null) {
                    if (this.consumes.equals(that.consumes)) {
                        break Label_0054;
                    }
                }
                else if (that.consumes == null) {
                    break Label_0054;
                }
                return false;
            }
            Label_0087: {
                if (this.methodRouting != null) {
                    if (this.methodRouting.equals(that.methodRouting)) {
                        break Label_0087;
                    }
                }
                else if (that.methodRouting == null) {
                    break Label_0087;
                }
                return false;
            }
            if (this.produces != null) {
                if (this.produces.equals(that.produces)) {
                    return true;
                }
            }
            else if (that.produces == null) {
                return true;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int result = (this.consumes != null) ? this.consumes.hashCode() : 0;
            result = 31 * result + ((this.produces != null) ? this.produces.hashCode() : 0);
            result = 31 * result + ((this.methodRouting != null) ? this.methodRouting.hashCode() : 0);
            return result;
        }
    }
    
    private static final class RequestSpecificConsumesProducesAcceptor implements Comparable
    {
        final CombinedMediaType consumes;
        final CombinedMediaType produces;
        final MethodRouting methodRouting;
        final boolean producesFromProviders;
        
        RequestSpecificConsumesProducesAcceptor(final CombinedMediaType consumes, final CombinedMediaType produces, final boolean producesFromProviders, final MethodRouting methodRouting) {
            this.methodRouting = methodRouting;
            this.consumes = consumes;
            this.produces = produces;
            this.producesFromProviders = producesFromProviders;
        }
        
        @Override
        public String toString() {
            return String.format("%s->%s:%s", this.consumes, this.produces, this.methodRouting);
        }
        
        @Override
        public int compareTo(final Object o) {
            if (o == null) {
                return -1;
            }
            if (!(o instanceof RequestSpecificConsumesProducesAcceptor)) {
                return -1;
            }
            final RequestSpecificConsumesProducesAcceptor other = (RequestSpecificConsumesProducesAcceptor)o;
            final int consumedComparison = CombinedMediaType.COMPARATOR.compare(this.consumes, other.consumes);
            return (consumedComparison != 0) ? consumedComparison : CombinedMediaType.COMPARATOR.compare(this.produces, other.produces);
        }
    }
    
    private static class MethodSelector
    {
        RequestSpecificConsumesProducesAcceptor selected;
        List<RequestSpecificConsumesProducesAcceptor> sameFitnessAcceptors;
        
        MethodSelector(final RequestSpecificConsumesProducesAcceptor i) {
            this.selected = i;
            this.sameFitnessAcceptors = null;
        }
        
        void consider(final RequestSpecificConsumesProducesAcceptor i) {
            final int theLessTheBetter = i.compareTo(this.selected);
            if (theLessTheBetter < 0) {
                this.selected = i;
                this.sameFitnessAcceptors = null;
            }
            else if (theLessTheBetter == 0 && this.selected.methodRouting != i.methodRouting) {
                this.getSameFitnessList().add(i);
            }
        }
        
        List<RequestSpecificConsumesProducesAcceptor> getSameFitnessList() {
            if (this.sameFitnessAcceptors == null) {
                this.sameFitnessAcceptors = new LinkedList<RequestSpecificConsumesProducesAcceptor>();
            }
            return this.sameFitnessAcceptors;
        }
    }
}
