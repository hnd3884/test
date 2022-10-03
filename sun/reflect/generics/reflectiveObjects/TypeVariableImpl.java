package sun.reflect.generics.reflectiveObjects;

import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.RetentionPolicy;
import sun.reflect.annotation.AnnotationType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.lang.reflect.AnnotatedType;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.annotation.AnnotationSupport;
import java.util.Objects;
import java.lang.reflect.Member;
import sun.reflect.misc.ReflectUtil;
import sun.reflect.generics.visitor.Reifier;
import sun.reflect.generics.visitor.TypeTreeVisitor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import sun.reflect.generics.factory.GenericsFactory;
import java.lang.annotation.Annotation;
import sun.reflect.generics.tree.FieldTypeSignature;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.GenericDeclaration;

public class TypeVariableImpl<D extends GenericDeclaration> extends LazyReflectiveObjectGenerator implements TypeVariable<D>
{
    D genericDeclaration;
    private String name;
    private Type[] bounds;
    private FieldTypeSignature[] boundASTs;
    private static final Annotation[] EMPTY_ANNOTATION_ARRAY;
    
    private TypeVariableImpl(final D genericDeclaration, final String name, final FieldTypeSignature[] boundASTs, final GenericsFactory genericsFactory) {
        super(genericsFactory);
        this.genericDeclaration = genericDeclaration;
        this.name = name;
        this.boundASTs = boundASTs;
    }
    
    private FieldTypeSignature[] getBoundASTs() {
        assert this.bounds == null;
        return this.boundASTs;
    }
    
    public static <T extends GenericDeclaration> TypeVariableImpl<T> make(final T t, final String s, final FieldTypeSignature[] array, final GenericsFactory genericsFactory) {
        if (!(t instanceof Class) && !(t instanceof Method) && !(t instanceof Constructor)) {
            throw new AssertionError((Object)("Unexpected kind of GenericDeclaration" + t.getClass().toString()));
        }
        return new TypeVariableImpl<T>(t, s, array, genericsFactory);
    }
    
    @Override
    public Type[] getBounds() {
        if (this.bounds == null) {
            final FieldTypeSignature[] boundASTs = this.getBoundASTs();
            final Type[] bounds = new Type[boundASTs.length];
            for (int i = 0; i < boundASTs.length; ++i) {
                final Reifier reifier = this.getReifier();
                boundASTs[i].accept(reifier);
                bounds[i] = reifier.getResult();
            }
            this.bounds = bounds;
        }
        return this.bounds.clone();
    }
    
    @Override
    public D getGenericDeclaration() {
        if (this.genericDeclaration instanceof Class) {
            ReflectUtil.checkPackageAccess((Class<?>)this.genericDeclaration);
        }
        else {
            if (!(this.genericDeclaration instanceof Method) && !(this.genericDeclaration instanceof Constructor)) {
                throw new AssertionError((Object)"Unexpected kind of GenericDeclaration");
            }
            ReflectUtil.conservativeCheckMemberAccess((Member)this.genericDeclaration);
        }
        return this.genericDeclaration;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof TypeVariable && o.getClass() == TypeVariableImpl.class) {
            final TypeVariable typeVariable = (TypeVariable)o;
            final GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
            final String name = typeVariable.getName();
            return Objects.equals(this.genericDeclaration, genericDeclaration) && Objects.equals(this.name, name);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.genericDeclaration.hashCode() ^ this.name.hashCode();
    }
    
    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return (T)mapAnnotations(this.getAnnotations()).get(clazz);
    }
    
    @Override
    public <T extends Annotation> T getDeclaredAnnotation(final Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return (T)this.getAnnotation((Class<Annotation>)clazz);
    }
    
    @Override
    public <T extends Annotation> T[] getAnnotationsByType(final Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return AnnotationSupport.getDirectlyAndIndirectlyPresent(mapAnnotations(this.getAnnotations()), clazz);
    }
    
    @Override
    public <T extends Annotation> T[] getDeclaredAnnotationsByType(final Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return (T[])this.getAnnotationsByType((Class<Annotation>)clazz);
    }
    
    @Override
    public Annotation[] getAnnotations() {
        final int typeVarIndex = this.typeVarIndex();
        if (typeVarIndex < 0) {
            throw new AssertionError((Object)"Index must be non-negative.");
        }
        return TypeAnnotationParser.parseTypeVariableAnnotations(this.getGenericDeclaration(), typeVarIndex);
    }
    
    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.getAnnotations();
    }
    
    @Override
    public AnnotatedType[] getAnnotatedBounds() {
        return TypeAnnotationParser.parseAnnotatedBounds(this.getBounds(), this.getGenericDeclaration(), this.typeVarIndex());
    }
    
    private int typeVarIndex() {
        final TypeVariable<?>[] typeParameters = this.getGenericDeclaration().getTypeParameters();
        int n = -1;
        for (final TypeVariable<?> typeVariable : typeParameters) {
            ++n;
            if (this.equals(typeVariable)) {
                return n;
            }
        }
        return -1;
    }
    
    private static Map<Class<? extends Annotation>, Annotation> mapAnnotations(final Annotation[] array) {
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (final Annotation annotation : array) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (AnnotationType.getInstance(annotationType).retention() == RetentionPolicy.RUNTIME && linkedHashMap.put(annotationType, annotation) != null) {
                throw new AnnotationFormatError("Duplicate annotation for class: " + annotationType + ": " + annotation);
            }
        }
        return linkedHashMap;
    }
    
    static {
        EMPTY_ANNOTATION_ARRAY = new Annotation[0];
    }
}
