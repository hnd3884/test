package org.eclipse.jdt.internal.compiler.apt.model;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.element.Element;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import javax.lang.model.type.TypeVariable;

public class TypeVariableImpl extends TypeMirrorImpl implements TypeVariable
{
    TypeVariableImpl(final BaseProcessingEnvImpl env, final TypeVariableBinding binding) {
        super(env, binding);
    }
    
    @Override
    public Element asElement() {
        return this._env.getFactory().newElement(this._binding);
    }
    
    @Override
    public TypeMirror getLowerBound() {
        return this._env.getFactory().getNullType();
    }
    
    @Override
    public TypeMirror getUpperBound() {
        final TypeVariableBinding typeVariableBinding = (TypeVariableBinding)this._binding;
        final TypeBinding firstBound = typeVariableBinding.firstBound;
        final ReferenceBinding[] superInterfaces = typeVariableBinding.superInterfaces;
        if (firstBound == null || superInterfaces.length == 0) {
            return this._env.getFactory().newTypeMirror(typeVariableBinding.upperBound());
        }
        if (firstBound != null && superInterfaces.length == 1 && TypeBinding.equalsEquals(superInterfaces[0], firstBound)) {
            return this._env.getFactory().newTypeMirror(typeVariableBinding.upperBound());
        }
        return this._env.getFactory().newTypeMirror(this._binding);
    }
    
    @Override
    public <R, P> R accept(final TypeVisitor<R, P> v, final P p) {
        return v.visitTypeVariable(this, p);
    }
    
    @Override
    public TypeKind getKind() {
        return TypeKind.TYPEVAR;
    }
}
