package sun.reflect.generics.factory;

import sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl;
import java.lang.reflect.Array;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import sun.reflect.generics.reflectiveObjects.WildcardTypeImpl;
import java.lang.reflect.WildcardType;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;
import java.lang.reflect.TypeVariable;
import sun.reflect.generics.tree.FieldTypeSignature;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import sun.reflect.generics.scope.Scope;
import java.lang.reflect.GenericDeclaration;

public class CoreReflectionFactory implements GenericsFactory
{
    private final GenericDeclaration decl;
    private final Scope scope;
    
    private CoreReflectionFactory(final GenericDeclaration decl, final Scope scope) {
        this.decl = decl;
        this.scope = scope;
    }
    
    private GenericDeclaration getDecl() {
        return this.decl;
    }
    
    private Scope getScope() {
        return this.scope;
    }
    
    private ClassLoader getDeclsLoader() {
        if (this.decl instanceof Class) {
            return ((Class)this.decl).getClassLoader();
        }
        if (this.decl instanceof Method) {
            return ((Method)this.decl).getDeclaringClass().getClassLoader();
        }
        assert this.decl instanceof Constructor : "Constructor expected";
        return ((Constructor)this.decl).getDeclaringClass().getClassLoader();
    }
    
    public static CoreReflectionFactory make(final GenericDeclaration genericDeclaration, final Scope scope) {
        return new CoreReflectionFactory(genericDeclaration, scope);
    }
    
    @Override
    public TypeVariable<?> makeTypeVariable(final String s, final FieldTypeSignature[] array) {
        return TypeVariableImpl.make(this.getDecl(), s, array, this);
    }
    
    @Override
    public WildcardType makeWildcard(final FieldTypeSignature[] array, final FieldTypeSignature[] array2) {
        return WildcardTypeImpl.make(array, array2, this);
    }
    
    @Override
    public ParameterizedType makeParameterizedType(final Type type, final Type[] array, final Type type2) {
        return ParameterizedTypeImpl.make((Class<?>)type, array, type2);
    }
    
    @Override
    public TypeVariable<?> findTypeVariable(final String s) {
        return this.getScope().lookup(s);
    }
    
    @Override
    public Type makeNamedType(final String s) {
        try {
            return Class.forName(s, false, this.getDeclsLoader());
        }
        catch (final ClassNotFoundException ex) {
            throw new TypeNotPresentException(s, ex);
        }
    }
    
    @Override
    public Type makeArrayType(final Type type) {
        if (type instanceof Class) {
            return Array.newInstance((Class<?>)type, 0).getClass();
        }
        return GenericArrayTypeImpl.make(type);
    }
    
    @Override
    public Type makeByte() {
        return Byte.TYPE;
    }
    
    @Override
    public Type makeBool() {
        return Boolean.TYPE;
    }
    
    @Override
    public Type makeShort() {
        return Short.TYPE;
    }
    
    @Override
    public Type makeChar() {
        return Character.TYPE;
    }
    
    @Override
    public Type makeInt() {
        return Integer.TYPE;
    }
    
    @Override
    public Type makeLong() {
        return Long.TYPE;
    }
    
    @Override
    public Type makeFloat() {
        return Float.TYPE;
    }
    
    @Override
    public Type makeDouble() {
        return Double.TYPE;
    }
    
    @Override
    public Type makeVoid() {
        return Void.TYPE;
    }
}
