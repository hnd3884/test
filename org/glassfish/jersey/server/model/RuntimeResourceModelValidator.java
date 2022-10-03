package org.glassfish.jersey.server.model;

import java.util.Arrays;
import java.util.LinkedList;
import org.glassfish.jersey.message.internal.MediaTypes;
import java.util.Iterator;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.Severity;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.Collection;
import java.util.ArrayList;
import javax.ws.rs.core.MediaType;
import java.util.List;
import org.glassfish.jersey.message.MessageBodyWorkers;

public class RuntimeResourceModelValidator extends AbstractResourceModelVisitor
{
    private final MessageBodyWorkers workers;
    private static final List<MediaType> StarTypeList;
    
    public RuntimeResourceModelValidator(final MessageBodyWorkers workers) {
        this.workers = workers;
    }
    
    @Override
    public void visitRuntimeResource(final RuntimeResource runtimeResource) {
        this.checkMethods(runtimeResource);
    }
    
    private void checkMethods(final RuntimeResource resource) {
        final List<ResourceMethod> resourceMethods = new ArrayList<ResourceMethod>(resource.getResourceMethods());
        resourceMethods.addAll(resource.getResourceLocators());
        if (resourceMethods.size() >= 2) {
            for (final ResourceMethod m1 : resourceMethods.subList(0, resourceMethods.size() - 1)) {
                for (final ResourceMethod m2 : resourceMethods.subList(resourceMethods.indexOf(m1) + 1, resourceMethods.size())) {
                    if (m1.getHttpMethod() == null && m2.getHttpMethod() == null) {
                        Errors.error((Object)this, LocalizationMessages.AMBIGUOUS_SRLS_PATH_PATTERN(resource.getFullPathRegex()), Severity.FATAL);
                    }
                    else {
                        if (m1.getHttpMethod() == null || m2.getHttpMethod() == null || !this.sameHttpMethod(m1, m2)) {
                            continue;
                        }
                        this.checkIntersectingMediaTypes(resource, m1.getHttpMethod(), m1, m2);
                    }
                }
            }
        }
    }
    
    private void checkIntersectingMediaTypes(final RuntimeResource runtimeResource, final String httpMethod, final ResourceMethod m1, final ResourceMethod m2) {
        final List<MediaType> inputTypes1 = this.getEffectiveInputTypes(m1);
        final List<MediaType> inputTypes2 = this.getEffectiveInputTypes(m2);
        final List<MediaType> outputTypes1 = this.getEffectiveOutputTypes(m1);
        final List<MediaType> outputTypes2 = this.getEffectiveOutputTypes(m2);
        boolean consumesOnlyIntersects = false;
        boolean consumesFails;
        if (m1.getConsumedTypes().isEmpty() || m2.getConsumedTypes().isEmpty()) {
            consumesFails = inputTypes1.equals(inputTypes2);
            if (!consumesFails) {
                consumesOnlyIntersects = MediaTypes.intersect((List)inputTypes1, (List)inputTypes2);
            }
        }
        else {
            consumesFails = MediaTypes.intersect((List)inputTypes1, (List)inputTypes2);
        }
        boolean producesOnlyIntersects = false;
        boolean producesFails;
        if (m1.getProducedTypes().isEmpty() || m2.getProducedTypes().isEmpty()) {
            producesFails = outputTypes1.equals(outputTypes2);
            if (!producesFails) {
                producesOnlyIntersects = MediaTypes.intersect((List)outputTypes1, (List)outputTypes2);
            }
        }
        else {
            producesFails = MediaTypes.intersect((List)outputTypes1, (List)outputTypes2);
        }
        if (consumesFails && producesFails) {
            Errors.fatal((Object)runtimeResource, LocalizationMessages.AMBIGUOUS_FATAL_RMS(httpMethod, m1.getInvocable().getHandlingMethod(), m2.getInvocable().getHandlingMethod(), runtimeResource.getRegex()));
        }
        else if ((producesFails && consumesOnlyIntersects) || (consumesFails && producesOnlyIntersects) || (consumesOnlyIntersects && producesOnlyIntersects)) {
            if (m1.getInvocable().requiresEntity()) {
                Errors.hint((Object)runtimeResource, LocalizationMessages.AMBIGUOUS_RMS_IN(httpMethod, m1.getInvocable().getHandlingMethod(), m2.getInvocable().getHandlingMethod(), runtimeResource.getRegex()));
            }
            else {
                Errors.hint((Object)runtimeResource, LocalizationMessages.AMBIGUOUS_RMS_OUT(httpMethod, m1.getInvocable().getHandlingMethod(), m2.getInvocable().getHandlingMethod(), runtimeResource.getRegex()));
            }
        }
    }
    
    private List<MediaType> getEffectiveInputTypes(final ResourceMethod resourceMethod) {
        if (!resourceMethod.getConsumedTypes().isEmpty()) {
            return resourceMethod.getConsumedTypes();
        }
        final List<MediaType> result = new LinkedList<MediaType>();
        if (this.workers != null) {
            for (final Parameter p : resourceMethod.getInvocable().getParameters()) {
                if (p.getSource() == Parameter.Source.ENTITY) {
                    result.addAll(this.workers.getMessageBodyReaderMediaTypes((Class)p.getRawType(), p.getType(), p.getDeclaredAnnotations()));
                }
            }
        }
        return result.isEmpty() ? RuntimeResourceModelValidator.StarTypeList : result;
    }
    
    private List<MediaType> getEffectiveOutputTypes(final ResourceMethod resourceMethod) {
        if (!resourceMethod.getProducedTypes().isEmpty()) {
            return resourceMethod.getProducedTypes();
        }
        final List<MediaType> result = new LinkedList<MediaType>();
        if (this.workers != null) {
            final Invocable invocable = resourceMethod.getInvocable();
            result.addAll(this.workers.getMessageBodyWriterMediaTypes((Class)invocable.getRawResponseType(), invocable.getResponseType(), invocable.getHandlingMethod().getAnnotations()));
        }
        return result.isEmpty() ? RuntimeResourceModelValidator.StarTypeList : result;
    }
    
    private boolean sameHttpMethod(final ResourceMethod m1, final ResourceMethod m2) {
        return m1.getHttpMethod().equals(m2.getHttpMethod());
    }
    
    static {
        StarTypeList = Arrays.asList(new MediaType("*", "*"));
    }
}
