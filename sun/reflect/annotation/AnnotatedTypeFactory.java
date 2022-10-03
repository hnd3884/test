package sun.reflect.annotation;

import java.lang.reflect.AnnotatedWildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.WildcardType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.lang.reflect.AnnotatedType;

public final class AnnotatedTypeFactory
{
    static final AnnotatedType EMPTY_ANNOTATED_TYPE;
    static final AnnotatedType[] EMPTY_ANNOTATED_TYPE_ARRAY;
    
    public static AnnotatedType buildAnnotatedType(final Type type, final TypeAnnotation.LocationInfo locationInfo, final TypeAnnotation[] array, final TypeAnnotation[] array2, final AnnotatedElement annotatedElement) {
        if (type == null) {
            return AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE;
        }
        if (isArray(type)) {
            return new AnnotatedArrayTypeImpl(type, locationInfo, array, array2, annotatedElement);
        }
        if (type instanceof Class) {
            return new AnnotatedTypeBaseImpl(type, addNesting(type, locationInfo), array, array2, annotatedElement);
        }
        if (type instanceof TypeVariable) {
            return new AnnotatedTypeVariableImpl((TypeVariable<?>)type, locationInfo, array, array2, annotatedElement);
        }
        if (type instanceof ParameterizedType) {
            return new AnnotatedParameterizedTypeImpl((ParameterizedType)type, addNesting(type, locationInfo), array, array2, annotatedElement);
        }
        if (type instanceof WildcardType) {
            return new AnnotatedWildcardTypeImpl((WildcardType)type, locationInfo, array, array2, annotatedElement);
        }
        throw new AssertionError((Object)("Unknown instance of Type: " + type + "\nThis should not happen."));
    }
    
    private static TypeAnnotation.LocationInfo addNesting(final Type type, final TypeAnnotation.LocationInfo locationInfo) {
        if (isArray(type)) {
            return locationInfo;
        }
        if (type instanceof Class) {
            final Class clazz = (Class)type;
            if (clazz.getEnclosingClass() == null) {
                return locationInfo;
            }
            if (Modifier.isStatic(clazz.getModifiers())) {
                return addNesting(clazz.getEnclosingClass(), locationInfo);
            }
            return addNesting(clazz.getEnclosingClass(), locationInfo.pushInner());
        }
        else {
            if (!(type instanceof ParameterizedType)) {
                return locationInfo;
            }
            final ParameterizedType parameterizedType = (ParameterizedType)type;
            if (parameterizedType.getOwnerType() == null) {
                return locationInfo;
            }
            return addNesting(parameterizedType.getOwnerType(), locationInfo.pushInner());
        }
    }
    
    private static boolean isArray(final Type type) {
        if (type instanceof Class) {
            if (((Class)type).isArray()) {
                return true;
            }
        }
        else if (type instanceof GenericArrayType) {
            return true;
        }
        return false;
    }
    
    static {
        EMPTY_ANNOTATED_TYPE = new AnnotatedTypeBaseImpl(null, TypeAnnotation.LocationInfo.BASE_LOCATION, new TypeAnnotation[0], new TypeAnnotation[0], null);
        EMPTY_ANNOTATED_TYPE_ARRAY = new AnnotatedType[0];
    }
    
    private static class AnnotatedTypeBaseImpl implements AnnotatedType
    {
        private final Type type;
        private final AnnotatedElement decl;
        private final TypeAnnotation.LocationInfo location;
        private final TypeAnnotation[] allOnSameTargetTypeAnnotations;
        private final Map<Class<? extends Annotation>, Annotation> annotations;
        
        AnnotatedTypeBaseImpl(final Type type, final TypeAnnotation.LocationInfo location, final TypeAnnotation[] array, final TypeAnnotation[] allOnSameTargetTypeAnnotations, final AnnotatedElement decl) {
            this.type = type;
            this.decl = decl;
            this.location = location;
            this.allOnSameTargetTypeAnnotations = allOnSameTargetTypeAnnotations;
            this.annotations = TypeAnnotationParser.mapTypeAnnotations(location.filter(array));
        }
        
        @Override
        public final Annotation[] getAnnotations() {
            return this.getDeclaredAnnotations();
        }
        
        @Override
        public final <T extends Annotation> T getAnnotation(final Class<T> clazz) {
            return (T)this.getDeclaredAnnotation((Class<Annotation>)clazz);
        }
        
        @Override
        public final <T extends Annotation> T[] getAnnotationsByType(final Class<T> clazz) {
            return (T[])this.getDeclaredAnnotationsByType((Class<Annotation>)clazz);
        }
        
        @Override
        public final Annotation[] getDeclaredAnnotations() {
            return this.annotations.values().toArray(new Annotation[0]);
        }
        
        @Override
        public final <T extends Annotation> T getDeclaredAnnotation(final Class<T> clazz) {
            return (T)this.annotations.get(clazz);
        }
        
        @Override
        public final <T extends Annotation> T[] getDeclaredAnnotationsByType(final Class<T> clazz) {
            return AnnotationSupport.getDirectlyAndIndirectlyPresent(this.annotations, clazz);
        }
        
        @Override
        public final Type getType() {
            return this.type;
        }
        
        final TypeAnnotation.LocationInfo getLocation() {
            return this.location;
        }
        
        final TypeAnnotation[] getTypeAnnotations() {
            return this.allOnSameTargetTypeAnnotations;
        }
        
        final AnnotatedElement getDecl() {
            return this.decl;
        }
    }
    
    private static final class AnnotatedArrayTypeImpl extends AnnotatedTypeBaseImpl implements AnnotatedArrayType
    {
        AnnotatedArrayTypeImpl(final Type type, final TypeAnnotation.LocationInfo locationInfo, final TypeAnnotation[] array, final TypeAnnotation[] array2, final AnnotatedElement annotatedElement) {
            super(type, locationInfo, array, array2, annotatedElement);
        }
        
        @Override
        public AnnotatedType getAnnotatedGenericComponentType() {
            return AnnotatedTypeFactory.buildAnnotatedType(this.getComponentType(), this.getLocation().pushArray(), this.getTypeAnnotations(), this.getTypeAnnotations(), this.getDecl());
        }
        
        private Type getComponentType() {
            final Type type = this.getType();
            if (type instanceof Class) {
                return ((Class)type).getComponentType();
            }
            return ((GenericArrayType)type).getGenericComponentType();
        }
    }
    
    private static final class AnnotatedTypeVariableImpl extends AnnotatedTypeBaseImpl implements AnnotatedTypeVariable
    {
        AnnotatedTypeVariableImpl(final TypeVariable<?> typeVariable, final TypeAnnotation.LocationInfo locationInfo, final TypeAnnotation[] array, final TypeAnnotation[] array2, final AnnotatedElement annotatedElement) {
            super(typeVariable, locationInfo, array, array2, annotatedElement);
        }
        
        @Override
        public AnnotatedType[] getAnnotatedBounds() {
            return this.getTypeVariable().getAnnotatedBounds();
        }
        
        private TypeVariable<?> getTypeVariable() {
            return (TypeVariable)this.getType();
        }
    }
    
    private static final class AnnotatedParameterizedTypeImpl extends AnnotatedTypeBaseImpl implements AnnotatedParameterizedType
    {
        AnnotatedParameterizedTypeImpl(final ParameterizedType parameterizedType, final TypeAnnotation.LocationInfo locationInfo, final TypeAnnotation[] array, final TypeAnnotation[] array2, final AnnotatedElement annotatedElement) {
            super(parameterizedType, locationInfo, array, array2, annotatedElement);
        }
        
        @Override
        public AnnotatedType[] getAnnotatedActualTypeArguments() {
            final Type[] actualTypeArguments = this.getParameterizedType().getActualTypeArguments();
            final AnnotatedType[] array = new AnnotatedType[actualTypeArguments.length];
            Arrays.fill(array, AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE);
            final int length = this.getTypeAnnotations().length;
            for (int i = 0; i < array.length; ++i) {
                final ArrayList list = new ArrayList(length);
                final TypeAnnotation.LocationInfo pushTypeArg = this.getLocation().pushTypeArg((byte)i);
                for (final TypeAnnotation typeAnnotation : this.getTypeAnnotations()) {
                    if (typeAnnotation.getLocationInfo().isSameLocationInfo(pushTypeArg)) {
                        list.add((Object)typeAnnotation);
                    }
                }
                array[i] = AnnotatedTypeFactory.buildAnnotatedType(actualTypeArguments[i], pushTypeArg, (TypeAnnotation[])list.toArray((Object[])new TypeAnnotation[0]), this.getTypeAnnotations(), this.getDecl());
            }
            return array;
        }
        
        private ParameterizedType getParameterizedType() {
            return (ParameterizedType)this.getType();
        }
    }
    
    private static final class AnnotatedWildcardTypeImpl extends AnnotatedTypeBaseImpl implements AnnotatedWildcardType
    {
        private final boolean hasUpperBounds;
        
        AnnotatedWildcardTypeImpl(final WildcardType wildcardType, final TypeAnnotation.LocationInfo locationInfo, final TypeAnnotation[] array, final TypeAnnotation[] array2, final AnnotatedElement annotatedElement) {
            super(wildcardType, locationInfo, array, array2, annotatedElement);
            this.hasUpperBounds = (wildcardType.getLowerBounds().length == 0);
        }
        
        @Override
        public AnnotatedType[] getAnnotatedUpperBounds() {
            if (!this.hasUpperBounds()) {
                return new AnnotatedType[0];
            }
            return this.getAnnotatedBounds(this.getWildcardType().getUpperBounds());
        }
        
        @Override
        public AnnotatedType[] getAnnotatedLowerBounds() {
            if (this.hasUpperBounds) {
                return new AnnotatedType[0];
            }
            return this.getAnnotatedBounds(this.getWildcardType().getLowerBounds());
        }
        
        private AnnotatedType[] getAnnotatedBounds(final Type[] array) {
            final AnnotatedType[] array2 = new AnnotatedType[array.length];
            Arrays.fill(array2, AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE);
            final TypeAnnotation.LocationInfo pushWildcard = this.getLocation().pushWildcard();
            final int length = this.getTypeAnnotations().length;
            for (int i = 0; i < array2.length; ++i) {
                final ArrayList list = new ArrayList(length);
                for (final TypeAnnotation typeAnnotation : this.getTypeAnnotations()) {
                    if (typeAnnotation.getLocationInfo().isSameLocationInfo(pushWildcard)) {
                        list.add((Object)typeAnnotation);
                    }
                }
                array2[i] = AnnotatedTypeFactory.buildAnnotatedType(array[i], pushWildcard, (TypeAnnotation[])list.toArray((Object[])new TypeAnnotation[0]), this.getTypeAnnotations(), this.getDecl());
            }
            return array2;
        }
        
        private WildcardType getWildcardType() {
            return (WildcardType)this.getType();
        }
        
        private boolean hasUpperBounds() {
            return this.hasUpperBounds;
        }
    }
}
