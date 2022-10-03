package org.glassfish.jersey.server.internal.inject;

import javax.ws.rs.ext.ParamConverter;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.lang.reflect.Method;
import org.glassfish.jersey.internal.util.collection.ClassTypePair;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.util.SortedSet;
import java.util.Set;
import java.util.List;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.internal.inject.ExtractorException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import javax.inject.Singleton;

@Singleton
final class MultivaluedParameterExtractorFactory implements MultivaluedParameterExtractorProvider
{
    private final LazyValue<ParamConverterFactory> paramConverterFactory;
    
    public MultivaluedParameterExtractorFactory(final LazyValue<ParamConverterFactory> paramConverterFactory) {
        this.paramConverterFactory = paramConverterFactory;
    }
    
    @Override
    public MultivaluedParameterExtractor<?> get(final Parameter p) {
        return this.process((ParamConverterFactory)this.paramConverterFactory.get(), p.getDefaultValue(), p.getRawType(), p.getType(), p.getAnnotations(), p.getSourceName());
    }
    
    private MultivaluedParameterExtractor<?> process(final ParamConverterFactory paramConverterFactory, final String defaultValue, final Class<?> rawType, final Type type, final Annotation[] annotations, final String parameterName) {
        ParamConverter<?> converter = paramConverterFactory.getConverter(rawType, type, annotations);
        if (converter != null) {
            try {
                return new SingleValueExtractor<Object>(converter, parameterName, defaultValue);
            }
            catch (final ExtractorException e) {
                throw e;
            }
            catch (final Exception e2) {
                throw new ProcessingException(LocalizationMessages.ERROR_PARAMETER_TYPE_PROCESSING(rawType), (Throwable)e2);
            }
        }
        if (rawType == List.class || rawType == Set.class || rawType == SortedSet.class) {
            final List<ClassTypePair> typePairs = ReflectionHelper.getTypeArgumentAndClass(type);
            final ClassTypePair typePair = (typePairs.size() == 1) ? typePairs.get(0) : null;
            if (typePair == null || typePair.rawClass() == String.class) {
                return StringCollectionExtractor.getInstance(rawType, parameterName, defaultValue);
            }
            converter = paramConverterFactory.getConverter((Class<?>)typePair.rawClass(), typePair.type(), annotations);
            if (converter == null) {
                return null;
            }
            try {
                return CollectionExtractor.getInstance(rawType, converter, parameterName, defaultValue);
            }
            catch (final ExtractorException e3) {
                throw e3;
            }
            catch (final Exception e4) {
                throw new ProcessingException(LocalizationMessages.ERROR_PARAMETER_TYPE_PROCESSING(rawType), (Throwable)e4);
            }
        }
        if (rawType == String.class) {
            return new SingleStringValueExtractor(parameterName, defaultValue);
        }
        if (rawType == Character.class) {
            return new PrimitiveCharacterExtractor(parameterName, defaultValue, PrimitiveMapper.primitiveToDefaultValueMap.get(rawType));
        }
        if (rawType.isPrimitive()) {
            final Class<?> wrappedRaw = PrimitiveMapper.primitiveToClassMap.get(rawType);
            if (wrappedRaw == null) {
                return null;
            }
            if (wrappedRaw == Character.class) {
                return new PrimitiveCharacterExtractor(parameterName, defaultValue, PrimitiveMapper.primitiveToDefaultValueMap.get(wrappedRaw));
            }
            final Method valueOf = AccessController.doPrivileged((PrivilegedAction<Method>)ReflectionHelper.getValueOfStringMethodPA((Class)wrappedRaw));
            if (valueOf != null) {
                try {
                    return new PrimitiveValueOfExtractor(valueOf, parameterName, defaultValue, PrimitiveMapper.primitiveToDefaultValueMap.get(wrappedRaw));
                }
                catch (final Exception e4) {
                    throw new ProcessingException(LocalizationMessages.DEFAULT_COULD_NOT_PROCESS_METHOD(defaultValue, valueOf));
                }
            }
        }
        return null;
    }
}
