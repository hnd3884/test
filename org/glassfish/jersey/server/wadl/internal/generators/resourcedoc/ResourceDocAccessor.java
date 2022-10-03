package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc;

import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.NamedValueType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ResponseDocType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.RepresentationDocType;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.AnnotationDocType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ParamDocType;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.MethodDocType;
import java.lang.reflect.Method;
import java.util.Iterator;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ClassDocType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ResourceDocType;
import java.util.logging.Logger;

public class ResourceDocAccessor
{
    private static final Logger LOGGER;
    private ResourceDocType _resourceDoc;
    
    public ResourceDocAccessor(final ResourceDocType resourceDoc) {
        this._resourceDoc = resourceDoc;
    }
    
    public ClassDocType getClassDoc(final Class<?> resourceClass) {
        if (resourceClass == null) {
            return null;
        }
        for (final ClassDocType classDocType : this._resourceDoc.getDocs()) {
            if (resourceClass.getName().equals(classDocType.getClassName())) {
                return classDocType;
            }
        }
        return null;
    }
    
    public MethodDocType getMethodDoc(final Class<?> resourceClass, final Method method) {
        if (resourceClass == null || method == null) {
            return null;
        }
        final ClassDocType classDoc = this.getClassDoc(resourceClass);
        if (classDoc == null) {
            return null;
        }
        MethodDocType candidate = null;
        int candidateCount = 0;
        final String methodName = method.getName();
        final String methodSignature = this.computeSignature(method);
        for (final MethodDocType methodDocType : classDoc.getMethodDocs()) {
            if (methodName.equals(methodDocType.getMethodName())) {
                ++candidateCount;
                if (candidate == null) {
                    candidate = methodDocType;
                }
                final String docMethodSignature = methodDocType.getMethodSignature();
                if (docMethodSignature != null && docMethodSignature.equals(methodSignature)) {
                    return methodDocType;
                }
                continue;
            }
        }
        if (candidate != null && candidateCount > 1 && ResourceDocAccessor.LOGGER.isLoggable(Level.CONFIG)) {
            ResourceDocAccessor.LOGGER.config(LocalizationMessages.WADL_RESOURCEDOC_AMBIGUOUS_METHOD_ENTRIES(resourceClass.getName(), methodName, methodSignature, candidateCount));
        }
        return candidate;
    }
    
    private String computeSignature(final Method method) {
        final String methodAsString = method.toGenericString();
        return methodAsString.substring(methodAsString.indexOf(40), methodAsString.lastIndexOf(41) + 1);
    }
    
    public ParamDocType getParamDoc(final Class<?> resourceClass, final Method method, final Parameter p) {
        final MethodDocType methodDoc = this.getMethodDoc(resourceClass, method);
        if (methodDoc != null) {
            for (final ParamDocType paramDocType : methodDoc.getParamDocs()) {
                for (final AnnotationDocType annotationDocType : paramDocType.getAnnotationDocs()) {
                    final Class<? extends Annotation> annotationType = p.getSourceAnnotation().annotationType();
                    if (annotationType != null) {
                        final String sourceName = this.getSourceName(annotationDocType);
                        if (sourceName != null && sourceName.equals(p.getSourceName())) {
                            return paramDocType;
                        }
                        continue;
                    }
                }
            }
        }
        return null;
    }
    
    public RepresentationDocType getRequestRepresentation(final Class<?> resourceClass, final Method method, final String mediaType) {
        if (mediaType == null) {
            return null;
        }
        final MethodDocType methodDoc = this.getMethodDoc(resourceClass, method);
        return (methodDoc != null && methodDoc.getRequestDoc() != null && methodDoc.getRequestDoc().getRepresentationDoc() != null) ? methodDoc.getRequestDoc().getRepresentationDoc() : null;
    }
    
    public ResponseDocType getResponse(final Class<?> resourceClass, final Method method) {
        final MethodDocType methodDoc = this.getMethodDoc(resourceClass, method);
        return (methodDoc != null && methodDoc.getResponseDoc() != null) ? methodDoc.getResponseDoc() : null;
    }
    
    private String getSourceName(final AnnotationDocType annotationDocType) {
        if (annotationDocType.hasAttributeDocs()) {
            for (final NamedValueType namedValueType : annotationDocType.getAttributeDocs()) {
                if ("value".equals(namedValueType.getName())) {
                    return namedValueType.getValue();
                }
            }
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(ResourceDocAccessor.class.getName());
    }
}
