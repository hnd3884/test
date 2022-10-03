package org.glassfish.jersey.server.model;

import java.util.Iterator;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Arrays;
import java.util.logging.Level;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.jersey.internal.util.collection.ClassTypePair;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import java.util.Collections;
import javax.ws.rs.BeanParam;
import org.glassfish.jersey.server.Uri;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.core.Context;
import java.util.WeakHashMap;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.logging.Logger;
import java.lang.reflect.AnnotatedElement;

public class Parameter implements AnnotatedElement
{
    private static final Logger LOGGER;
    private static final Map<Class, ParamAnnotationHelper> ANNOTATION_HELPER_MAP;
    private final Annotation[] annotations;
    private final Annotation sourceAnnotation;
    private final Source source;
    private final String sourceName;
    private final boolean encoded;
    private final String defaultValue;
    private final Class<?> rawType;
    private final Type type;
    
    private static Map<Class, ParamAnnotationHelper> createParamAnnotationHelperMap() {
        final Map<Class, ParamAnnotationHelper> m = new WeakHashMap<Class, ParamAnnotationHelper>();
        m.put(Context.class, new ParamAnnotationHelper<Context>() {
            @Override
            public String getValueOf(final Context a) {
                return null;
            }
            
            @Override
            public Source getSource() {
                return Source.CONTEXT;
            }
        });
        m.put(CookieParam.class, new ParamAnnotationHelper<CookieParam>() {
            @Override
            public String getValueOf(final CookieParam a) {
                return a.value();
            }
            
            @Override
            public Source getSource() {
                return Source.COOKIE;
            }
        });
        m.put(FormParam.class, new ParamAnnotationHelper<FormParam>() {
            @Override
            public String getValueOf(final FormParam a) {
                return a.value();
            }
            
            @Override
            public Source getSource() {
                return Source.FORM;
            }
        });
        m.put(HeaderParam.class, new ParamAnnotationHelper<HeaderParam>() {
            @Override
            public String getValueOf(final HeaderParam a) {
                return a.value();
            }
            
            @Override
            public Source getSource() {
                return Source.HEADER;
            }
        });
        m.put(MatrixParam.class, new ParamAnnotationHelper<MatrixParam>() {
            @Override
            public String getValueOf(final MatrixParam a) {
                return a.value();
            }
            
            @Override
            public Source getSource() {
                return Source.MATRIX;
            }
        });
        m.put(PathParam.class, new ParamAnnotationHelper<PathParam>() {
            @Override
            public String getValueOf(final PathParam a) {
                return a.value();
            }
            
            @Override
            public Source getSource() {
                return Source.PATH;
            }
        });
        m.put(QueryParam.class, new ParamAnnotationHelper<QueryParam>() {
            @Override
            public String getValueOf(final QueryParam a) {
                return a.value();
            }
            
            @Override
            public Source getSource() {
                return Source.QUERY;
            }
        });
        m.put(Suspended.class, new ParamAnnotationHelper<Suspended>() {
            @Override
            public String getValueOf(final Suspended a) {
                return Suspended.class.getName();
            }
            
            @Override
            public Source getSource() {
                return Source.SUSPENDED;
            }
        });
        m.put(Uri.class, new ParamAnnotationHelper<Uri>() {
            @Override
            public String getValueOf(final Uri a) {
                return a.value();
            }
            
            @Override
            public Source getSource() {
                return Source.URI;
            }
        });
        m.put(BeanParam.class, new ParamAnnotationHelper<BeanParam>() {
            @Override
            public String getValueOf(final BeanParam a) {
                return null;
            }
            
            @Override
            public Source getSource() {
                return Source.BEAN_PARAM;
            }
        });
        return (Map<Class, ParamAnnotationHelper>)Collections.unmodifiableMap((Map<? extends Class, ? extends ParamAnnotationHelper>)m);
    }
    
    public static Parameter create(final Class concreteClass, final Class declaringClass, final boolean encodeByDefault, final Class<?> rawType, final Type type, final Annotation[] annotations) {
        if (null == annotations) {
            return null;
        }
        Annotation paramAnnotation = null;
        Source paramSource = null;
        String paramName = null;
        boolean paramEncoded = encodeByDefault;
        String paramDefault = null;
        for (final Annotation annotation : annotations) {
            if (Parameter.ANNOTATION_HELPER_MAP.containsKey(annotation.annotationType())) {
                final ParamAnnotationHelper helper = Parameter.ANNOTATION_HELPER_MAP.get(annotation.annotationType());
                paramAnnotation = annotation;
                paramSource = helper.getSource();
                paramName = helper.getValueOf(annotation);
            }
            else if (Encoded.class == annotation.annotationType()) {
                paramEncoded = true;
            }
            else if (DefaultValue.class == annotation.annotationType()) {
                paramDefault = ((DefaultValue)annotation).value();
            }
            else if (paramAnnotation == null || paramSource == Source.UNKNOWN) {
                paramAnnotation = annotation;
                paramSource = Source.UNKNOWN;
                paramName = getValue(annotation);
            }
        }
        if (paramAnnotation == null) {
            paramSource = Source.ENTITY;
        }
        final ClassTypePair ct = ReflectionHelper.resolveGenericType(concreteClass, declaringClass, (Class)rawType, type);
        if (paramSource == Source.BEAN_PARAM) {
            return new BeanParameter(annotations, paramAnnotation, paramName, ct.rawClass(), ct.type(), paramEncoded, paramDefault);
        }
        return new Parameter(annotations, paramAnnotation, paramSource, paramName, ct.rawClass(), ct.type(), paramEncoded, paramDefault);
    }
    
    private static List<Parameter> create(final Class concreteClass, final Class declaringClass, final boolean keepEncoded, final Class[] parameterTypes, final Type[] genericParameterTypes, final Annotation[][] parameterAnnotations) {
        final List<Parameter> parameters = new ArrayList<Parameter>(parameterTypes.length);
        for (int i = 0; i < parameterTypes.length; ++i) {
            final Parameter parameter = create(concreteClass, declaringClass, keepEncoded, parameterTypes[i], genericParameterTypes[i], parameterAnnotations[i]);
            if (null == parameter) {
                return Collections.emptyList();
            }
            parameters.add(parameter);
        }
        return parameters;
    }
    
    public static List<Parameter> create(final Class concreteClass, final Class declaringClass, final Constructor<?> ctor, final boolean keepEncoded) {
        final Class[] parameterTypes = ctor.getParameterTypes();
        Type[] genericParameterTypes = ctor.getGenericParameterTypes();
        if (parameterTypes.length != genericParameterTypes.length) {
            final Type[] _genericParameterTypes = new Type[parameterTypes.length];
            _genericParameterTypes[0] = parameterTypes[0];
            System.arraycopy(genericParameterTypes, 0, _genericParameterTypes, 1, genericParameterTypes.length);
            genericParameterTypes = _genericParameterTypes;
        }
        return create(concreteClass, declaringClass, null != ctor.getAnnotation(Encoded.class) || keepEncoded, parameterTypes, genericParameterTypes, ctor.getParameterAnnotations());
    }
    
    public static List<Parameter> create(final Class concreteClass, final Class declaringClass, final Method javaMethod, final boolean keepEncoded) {
        final AnnotatedMethod method = new AnnotatedMethod(javaMethod);
        return create(concreteClass, declaringClass, null != method.getAnnotation(Encoded.class) || keepEncoded, method.getParameterTypes(), method.getGenericParameterTypes(), method.getParameterAnnotations());
    }
    
    public static Parameter overrideSource(final Parameter original, final Source source) {
        return new Parameter(original.annotations, original.sourceAnnotation, source, source.name(), original.rawType, original.type, original.encoded, original.defaultValue);
    }
    
    private static String getValue(final Annotation a) {
        try {
            final Method m = a.annotationType().getMethod("value", (Class<?>[])new Class[0]);
            if (m.getReturnType() != String.class) {
                return null;
            }
            return (String)m.invoke(a, new Object[0]);
        }
        catch (final Exception ex) {
            if (Parameter.LOGGER.isLoggable(Level.FINER)) {
                Parameter.LOGGER.log(Level.FINER, String.format("Unable to get the %s annotation value property", a.getClass().getName()), ex);
            }
            return null;
        }
    }
    
    private Parameter(final Annotation[] markers, final Annotation marker, final Source source, final String sourceName, final Class<?> rawType, final Type type, final boolean encoded, final String defaultValue) {
        this.annotations = markers;
        this.sourceAnnotation = marker;
        this.source = source;
        this.sourceName = sourceName;
        this.rawType = rawType;
        this.type = type;
        this.encoded = encoded;
        this.defaultValue = defaultValue;
    }
    
    public Annotation getSourceAnnotation() {
        return this.sourceAnnotation;
    }
    
    public Source getSource() {
        return this.source;
    }
    
    public String getSourceName() {
        return this.sourceName;
    }
    
    public boolean isEncoded() {
        return this.encoded;
    }
    
    public boolean hasDefaultValue() {
        return this.defaultValue != null;
    }
    
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    public Class<?> getRawType() {
        return this.rawType;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public boolean isQualified() {
        for (final Annotation a : this.getAnnotations()) {
            if (a.annotationType().isAnnotationPresent(ParamQualifier.class)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
        return this.getAnnotation(annotationClass) != null;
    }
    
    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
        if (annotationClass == null) {
            return null;
        }
        for (final Annotation a : this.annotations) {
            if (a.annotationType() == annotationClass) {
                return annotationClass.cast(a);
            }
        }
        return null;
    }
    
    @Override
    public Annotation[] getAnnotations() {
        return this.annotations.clone();
    }
    
    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.annotations.clone();
    }
    
    @Override
    public String toString() {
        return String.format("Parameter [type=%s, source=%s, defaultValue=%s]", this.getRawType(), this.getSourceName(), this.getDefaultValue());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Parameter parameter = (Parameter)o;
        if (this.encoded != parameter.encoded) {
            return false;
        }
        if (!Arrays.equals(this.annotations, parameter.annotations)) {
            return false;
        }
        Label_0091: {
            if (this.defaultValue != null) {
                if (this.defaultValue.equals(parameter.defaultValue)) {
                    break Label_0091;
                }
            }
            else if (parameter.defaultValue == null) {
                break Label_0091;
            }
            return false;
        }
        Label_0124: {
            if (this.rawType != null) {
                if (this.rawType.equals(parameter.rawType)) {
                    break Label_0124;
                }
            }
            else if (parameter.rawType == null) {
                break Label_0124;
            }
            return false;
        }
        if (this.source != parameter.source) {
            return false;
        }
        Label_0172: {
            if (this.sourceAnnotation != null) {
                if (this.sourceAnnotation.equals(parameter.sourceAnnotation)) {
                    break Label_0172;
                }
            }
            else if (parameter.sourceAnnotation == null) {
                break Label_0172;
            }
            return false;
        }
        Label_0205: {
            if (this.sourceName != null) {
                if (this.sourceName.equals(parameter.sourceName)) {
                    break Label_0205;
                }
            }
            else if (parameter.sourceName == null) {
                break Label_0205;
            }
            return false;
        }
        if (this.type != null) {
            if (this.type.equals(parameter.type)) {
                return true;
            }
        }
        else if (parameter.type == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = (this.annotations != null) ? Arrays.hashCode(this.annotations) : 0;
        result = 31 * result + ((this.sourceAnnotation != null) ? this.sourceAnnotation.hashCode() : 0);
        result = 31 * result + ((this.source != null) ? this.source.hashCode() : 0);
        result = 31 * result + ((this.sourceName != null) ? this.sourceName.hashCode() : 0);
        result = 31 * result + (this.encoded ? 1 : 0);
        result = 31 * result + ((this.defaultValue != null) ? this.defaultValue.hashCode() : 0);
        result = 31 * result + ((this.rawType != null) ? this.rawType.hashCode() : 0);
        result = 31 * result + ((this.type != null) ? this.type.hashCode() : 0);
        return result;
    }
    
    static {
        LOGGER = Logger.getLogger(Parameter.class.getName());
        ANNOTATION_HELPER_MAP = createParamAnnotationHelperMap();
    }
    
    public enum Source
    {
        CONTEXT, 
        COOKIE, 
        ENTITY, 
        FORM, 
        HEADER, 
        URI, 
        MATRIX, 
        PATH, 
        QUERY, 
        SUSPENDED, 
        BEAN_PARAM, 
        UNKNOWN;
    }
    
    public static class BeanParameter extends Parameter
    {
        private final Collection<Parameter> parameters;
        
        private BeanParameter(final Annotation[] markers, final Annotation marker, final String sourceName, final Class<?> rawType, final Type type, final boolean encoded, final String defaultValue) {
            super(markers, marker, Source.BEAN_PARAM, sourceName, rawType, type, encoded, defaultValue, null);
            final Collection<Parameter> parameters = new LinkedList<Parameter>();
            for (final Field field : AccessController.doPrivileged((PrivilegedAction<Field[]>)ReflectionHelper.getDeclaredFieldsPA((Class)rawType))) {
                if (field.getDeclaredAnnotations().length > 0) {
                    final Parameter beanParamParameter = Parameter.create(rawType, field.getDeclaringClass(), field.isAnnotationPresent((Class<? extends Annotation>)Encoded.class), field.getType(), field.getGenericType(), field.getAnnotations());
                    parameters.add(beanParamParameter);
                }
            }
            for (final Constructor constructor : AccessController.doPrivileged((PrivilegedAction<Constructor[]>)ReflectionHelper.getDeclaredConstructorsPA((Class)rawType))) {
                for (final Parameter parameter : Parameter.create(rawType, rawType, constructor, false)) {
                    parameters.add(parameter);
                }
            }
            this.parameters = Collections.unmodifiableCollection((Collection<? extends Parameter>)parameters);
        }
        
        public Collection<Parameter> getParameters() {
            return this.parameters;
        }
    }
    
    private interface ParamAnnotationHelper<T extends Annotation>
    {
        String getValueOf(final T p0);
        
        Source getSource();
    }
}
