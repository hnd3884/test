package org.eclipse.jdt.internal.compiler.apt.model;

import javax.lang.model.element.ElementKind;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import javax.lang.model.type.ArrayType;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.PrimitiveType;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import javax.lang.model.util.Types;

public class TypesImpl implements Types
{
    private final BaseProcessingEnvImpl _env;
    
    public TypesImpl(final BaseProcessingEnvImpl env) {
        this._env = env;
    }
    
    @Override
    public Element asElement(final TypeMirror t) {
        switch (t.getKind()) {
            case DECLARED:
            case TYPEVAR: {
                return this._env.getFactory().newElement(((TypeMirrorImpl)t).binding());
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public TypeMirror asMemberOf(final DeclaredType containing, final Element element) {
        final ElementImpl elementImpl = (ElementImpl)element;
        final DeclaredTypeImpl declaredTypeImpl = (DeclaredTypeImpl)containing;
        ReferenceBinding referenceBinding = (ReferenceBinding)declaredTypeImpl._binding;
        switch (element.getKind()) {
            case METHOD:
            case CONSTRUCTOR: {
                final MethodBinding methodBinding = (MethodBinding)elementImpl._binding;
                while (referenceBinding != null) {
                    MethodBinding[] methods;
                    for (int length = (methods = referenceBinding.methods()).length, i = 0; i < length; ++i) {
                        final MethodBinding method = methods[i];
                        if (CharOperation.equals(method.selector, methodBinding.selector) && (method.original() == methodBinding || method.areParameterErasuresEqual(methodBinding))) {
                            return this._env.getFactory().newTypeMirror(method);
                        }
                    }
                    referenceBinding = referenceBinding.superclass();
                }
                break;
            }
            case ENUM_CONSTANT:
            case FIELD: {
                final FieldBinding fieldBinding = (FieldBinding)elementImpl._binding;
                while (referenceBinding != null) {
                    FieldBinding[] fields;
                    for (int length2 = (fields = referenceBinding.fields()).length, j = 0; j < length2; ++j) {
                        final FieldBinding field = fields[j];
                        if (CharOperation.equals(field.name, fieldBinding.name)) {
                            return this._env.getFactory().newTypeMirror(field);
                        }
                    }
                    referenceBinding = referenceBinding.superclass();
                }
                break;
            }
            case ENUM:
            case CLASS:
            case ANNOTATION_TYPE:
            case INTERFACE: {
                final ReferenceBinding elementBinding = (ReferenceBinding)elementImpl._binding;
                while (referenceBinding != null) {
                    ReferenceBinding[] memberTypes;
                    for (int length3 = (memberTypes = referenceBinding.memberTypes()).length, k = 0; k < length3; ++k) {
                        final ReferenceBinding memberReferenceBinding = memberTypes[k];
                        if (CharOperation.equals(elementBinding.compoundName, memberReferenceBinding.compoundName)) {
                            return this._env.getFactory().newTypeMirror(memberReferenceBinding);
                        }
                    }
                    referenceBinding = referenceBinding.superclass();
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("element " + element + " has unrecognized element kind " + element.getKind());
            }
        }
        throw new IllegalArgumentException("element " + element + " is not a member of the containing type " + containing + " nor any of its superclasses");
    }
    
    @Override
    public TypeElement boxedClass(final PrimitiveType p) {
        final PrimitiveTypeImpl primitiveTypeImpl = (PrimitiveTypeImpl)p;
        final BaseTypeBinding baseTypeBinding = (BaseTypeBinding)primitiveTypeImpl._binding;
        final TypeBinding boxed = this._env.getLookupEnvironment().computeBoxingType(baseTypeBinding);
        return (TypeElement)this._env.getFactory().newElement(boxed);
    }
    
    @Override
    public TypeMirror capture(final TypeMirror t) {
        throw new UnsupportedOperationException("NYI: TypesImpl.capture(...)");
    }
    
    @Override
    public boolean contains(final TypeMirror t1, final TypeMirror t2) {
        switch (t1.getKind()) {
            case PACKAGE:
            case EXECUTABLE: {
                throw new IllegalArgumentException("Executable and package are illegal argument for Types.contains(..)");
            }
            default: {
                switch (t2.getKind()) {
                    case PACKAGE:
                    case EXECUTABLE: {
                        throw new IllegalArgumentException("Executable and package are illegal argument for Types.contains(..)");
                    }
                    default: {
                        throw new UnsupportedOperationException("NYI: TypesImpl.contains(" + t1 + ", " + t2 + ")");
                    }
                }
                break;
            }
        }
    }
    
    @Override
    public List<? extends TypeMirror> directSupertypes(final TypeMirror t) {
        switch (t.getKind()) {
            case PACKAGE:
            case EXECUTABLE: {
                throw new IllegalArgumentException("Invalid type mirror for directSupertypes");
            }
            default: {
                final TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl)t;
                final Binding binding = typeMirrorImpl._binding;
                if (binding instanceof ReferenceBinding) {
                    final ReferenceBinding referenceBinding = (ReferenceBinding)binding;
                    final ArrayList<TypeMirror> list = new ArrayList<TypeMirror>();
                    final ReferenceBinding superclass = referenceBinding.superclass();
                    if (superclass != null) {
                        list.add(this._env.getFactory().newTypeMirror(superclass));
                    }
                    ReferenceBinding[] superInterfaces;
                    for (int length = (superInterfaces = referenceBinding.superInterfaces()).length, i = 0; i < length; ++i) {
                        final ReferenceBinding interfaceBinding = superInterfaces[i];
                        list.add(this._env.getFactory().newTypeMirror(interfaceBinding));
                    }
                    return Collections.unmodifiableList((List<? extends TypeMirror>)list);
                }
                return Collections.emptyList();
            }
        }
    }
    
    @Override
    public TypeMirror erasure(final TypeMirror t) {
        final TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl)t;
        final Binding binding = typeMirrorImpl._binding;
        if (binding instanceof ReferenceBinding) {
            TypeBinding type = ((ReferenceBinding)binding).erasure();
            if (type.isGenericType()) {
                type = this._env.getLookupEnvironment().convertToRawType(type, false);
            }
            return this._env.getFactory().newTypeMirror(type);
        }
        if (binding instanceof ArrayBinding) {
            final TypeBinding typeBinding = (TypeBinding)binding;
            TypeBinding leafType = typeBinding.leafComponentType().erasure();
            if (leafType.isGenericType()) {
                leafType = this._env.getLookupEnvironment().convertToRawType(leafType, false);
            }
            return this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().createArrayType(leafType, typeBinding.dimensions()));
        }
        return t;
    }
    
    @Override
    public ArrayType getArrayType(final TypeMirror componentType) {
        final TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl)componentType;
        final TypeBinding typeBinding = (TypeBinding)typeMirrorImpl._binding;
        return (ArrayType)this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().createArrayType(typeBinding.leafComponentType(), typeBinding.dimensions() + 1));
    }
    
    @Override
    public DeclaredType getDeclaredType(final TypeElement typeElem, final TypeMirror... typeArgs) {
        final int typeArgsLength = typeArgs.length;
        final TypeElementImpl typeElementImpl = (TypeElementImpl)typeElem;
        final ReferenceBinding elementBinding = (ReferenceBinding)typeElementImpl._binding;
        final TypeVariableBinding[] typeVariables = elementBinding.typeVariables();
        final int typeVariablesLength = typeVariables.length;
        if (typeArgsLength == 0) {
            if (elementBinding.isGenericType()) {
                return (DeclaredType)this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().createRawType(elementBinding, null));
            }
            return (DeclaredType)typeElem.asType();
        }
        else {
            if (typeArgsLength != typeVariablesLength) {
                throw new IllegalArgumentException("Number of typeArguments doesn't match the number of formal parameters of typeElem");
            }
            final TypeBinding[] typeArguments = new TypeBinding[typeArgsLength];
            for (int i = 0; i < typeArgsLength; ++i) {
                final TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl)typeArgs[i];
                final Binding binding = typeMirrorImpl._binding;
                if (!(binding instanceof TypeBinding)) {
                    throw new IllegalArgumentException("Invalid type argument: " + typeMirrorImpl);
                }
                typeArguments[i] = (TypeBinding)binding;
            }
            return (DeclaredType)this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().createParameterizedType(elementBinding, typeArguments, null));
        }
    }
    
    @Override
    public DeclaredType getDeclaredType(final DeclaredType containing, final TypeElement typeElem, final TypeMirror... typeArgs) {
        final int typeArgsLength = typeArgs.length;
        final TypeElementImpl typeElementImpl = (TypeElementImpl)typeElem;
        final ReferenceBinding elementBinding = (ReferenceBinding)typeElementImpl._binding;
        final TypeVariableBinding[] typeVariables = elementBinding.typeVariables();
        final int typeVariablesLength = typeVariables.length;
        final DeclaredTypeImpl declaredTypeImpl = (DeclaredTypeImpl)containing;
        final ReferenceBinding enclosingType = (ReferenceBinding)declaredTypeImpl._binding;
        if (typeArgsLength == 0) {
            if (elementBinding.isGenericType()) {
                return (DeclaredType)this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().createRawType(elementBinding, enclosingType));
            }
            final ParameterizedTypeBinding ptb = this._env.getLookupEnvironment().createParameterizedType(elementBinding, null, enclosingType);
            return (DeclaredType)this._env.getFactory().newTypeMirror(ptb);
        }
        else {
            if (typeArgsLength != typeVariablesLength) {
                throw new IllegalArgumentException("Number of typeArguments doesn't match the number of formal parameters of typeElem");
            }
            final TypeBinding[] typeArguments = new TypeBinding[typeArgsLength];
            for (int i = 0; i < typeArgsLength; ++i) {
                final TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl)typeArgs[i];
                final Binding binding = typeMirrorImpl._binding;
                if (!(binding instanceof TypeBinding)) {
                    throw new IllegalArgumentException("Invalid type for a type arguments : " + typeMirrorImpl);
                }
                typeArguments[i] = (TypeBinding)binding;
            }
            return (DeclaredType)this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().createParameterizedType(elementBinding, typeArguments, enclosingType));
        }
    }
    
    @Override
    public NoType getNoType(final TypeKind kind) {
        return this._env.getFactory().getNoType(kind);
    }
    
    @Override
    public NullType getNullType() {
        return this._env.getFactory().getNullType();
    }
    
    @Override
    public PrimitiveType getPrimitiveType(final TypeKind kind) {
        return this._env.getFactory().getPrimitiveType(kind);
    }
    
    @Override
    public WildcardType getWildcardType(final TypeMirror extendsBound, final TypeMirror superBound) {
        if (extendsBound != null && superBound != null) {
            throw new IllegalArgumentException("Extends and super bounds cannot be set at the same time");
        }
        if (extendsBound != null) {
            final TypeMirrorImpl extendsBoundMirrorType = (TypeMirrorImpl)extendsBound;
            final TypeBinding typeBinding = (TypeBinding)extendsBoundMirrorType._binding;
            return (WildcardType)this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().createWildcard(null, 0, typeBinding, null, 1));
        }
        if (superBound != null) {
            final TypeMirrorImpl superBoundMirrorType = (TypeMirrorImpl)superBound;
            final TypeBinding typeBinding = (TypeBinding)superBoundMirrorType._binding;
            return new WildcardTypeImpl(this._env, this._env.getLookupEnvironment().createWildcard(null, 0, typeBinding, null, 2));
        }
        return new WildcardTypeImpl(this._env, this._env.getLookupEnvironment().createWildcard(null, 0, null, null, 0));
    }
    
    @Override
    public boolean isAssignable(final TypeMirror t1, final TypeMirror t2) {
        if (!(t1 instanceof TypeMirrorImpl) || !(t2 instanceof TypeMirrorImpl)) {
            return false;
        }
        final Binding b1 = ((TypeMirrorImpl)t1).binding();
        final Binding b2 = ((TypeMirrorImpl)t2).binding();
        if (!(b1 instanceof TypeBinding) || !(b2 instanceof TypeBinding)) {
            throw new IllegalArgumentException();
        }
        if (((TypeBinding)b1).isCompatibleWith((TypeBinding)b2)) {
            return true;
        }
        final TypeBinding convertedType = this._env.getLookupEnvironment().computeBoxingType((TypeBinding)b1);
        return convertedType != null && convertedType.isCompatibleWith((TypeBinding)b2);
    }
    
    @Override
    public boolean isSameType(final TypeMirror t1, final TypeMirror t2) {
        if (t1.getKind() == TypeKind.WILDCARD || t2.getKind() == TypeKind.WILDCARD) {
            return false;
        }
        if (t1 == t2) {
            return true;
        }
        if (!(t1 instanceof TypeMirrorImpl) || !(t2 instanceof TypeMirrorImpl)) {
            return false;
        }
        final Binding b1 = ((TypeMirrorImpl)t1).binding();
        final Binding b2 = ((TypeMirrorImpl)t2).binding();
        if (b1 == b2) {
            return true;
        }
        if (!(b1 instanceof TypeBinding) || !(b2 instanceof TypeBinding)) {
            return false;
        }
        final TypeBinding type1 = (TypeBinding)b1;
        final TypeBinding type2 = (TypeBinding)b2;
        return TypeBinding.equalsEquals(type1, type2) || CharOperation.equals(type1.computeUniqueKey(), type2.computeUniqueKey());
    }
    
    @Override
    public boolean isSubsignature(final ExecutableType m1, final ExecutableType m2) {
        final MethodBinding methodBinding1 = (MethodBinding)((ExecutableTypeImpl)m1)._binding;
        final MethodBinding methodBinding2 = (MethodBinding)((ExecutableTypeImpl)m2)._binding;
        return CharOperation.equals(methodBinding1.selector, methodBinding2.selector) && (methodBinding1.areParameterErasuresEqual(methodBinding2) && methodBinding1.areTypeVariableErasuresEqual(methodBinding2));
    }
    
    @Override
    public boolean isSubtype(final TypeMirror t1, final TypeMirror t2) {
        if (t1 instanceof NoTypeImpl) {
            return t2 instanceof NoTypeImpl && ((NoTypeImpl)t1).getKind() == ((NoTypeImpl)t2).getKind();
        }
        if (t2 instanceof NoTypeImpl) {
            return false;
        }
        if (!(t1 instanceof TypeMirrorImpl) || !(t2 instanceof TypeMirrorImpl)) {
            return false;
        }
        if (t1 == t2) {
            return true;
        }
        final Binding b1 = ((TypeMirrorImpl)t1).binding();
        final Binding b2 = ((TypeMirrorImpl)t2).binding();
        if (b1 == b2) {
            return true;
        }
        if (!(b1 instanceof TypeBinding) || !(b2 instanceof TypeBinding)) {
            return false;
        }
        if (b1.kind() == 132 || b2.kind() == 132) {
            return b1.kind() == b2.kind() && ((TypeBinding)b1).isCompatibleWith((TypeBinding)b2);
        }
        return ((TypeBinding)b1).isCompatibleWith((TypeBinding)b2);
    }
    
    @Override
    public PrimitiveType unboxedType(final TypeMirror t) {
        if (!(((TypeMirrorImpl)t)._binding instanceof ReferenceBinding)) {
            throw new IllegalArgumentException("Given type mirror cannot be unboxed");
        }
        final ReferenceBinding boxed = (ReferenceBinding)((TypeMirrorImpl)t)._binding;
        final TypeBinding unboxed = this._env.getLookupEnvironment().computeBoxingType(boxed);
        if (unboxed.kind() != 132) {
            throw new IllegalArgumentException();
        }
        return (PrimitiveType)this._env.getFactory().newTypeMirror(unboxed);
    }
}
