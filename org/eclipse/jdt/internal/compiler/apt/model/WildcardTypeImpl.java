package org.eclipse.jdt.internal.compiler.apt.model;

import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.TypeKind;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import javax.lang.model.type.TypeMirror;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import javax.lang.model.type.WildcardType;

public class WildcardTypeImpl extends TypeMirrorImpl implements WildcardType
{
    WildcardTypeImpl(final BaseProcessingEnvImpl env, final WildcardBinding binding) {
        super(env, binding);
    }
    
    @Override
    public TypeMirror getExtendsBound() {
        final WildcardBinding wildcardBinding = (WildcardBinding)this._binding;
        if (wildcardBinding.boundKind != 1) {
            return null;
        }
        final TypeBinding bound = wildcardBinding.bound;
        if (bound == null) {
            return null;
        }
        return this._env.getFactory().newTypeMirror(bound);
    }
    
    @Override
    public TypeKind getKind() {
        return TypeKind.WILDCARD;
    }
    
    @Override
    public TypeMirror getSuperBound() {
        final WildcardBinding wildcardBinding = (WildcardBinding)this._binding;
        if (wildcardBinding.boundKind != 2) {
            return null;
        }
        final TypeBinding bound = wildcardBinding.bound;
        if (bound == null) {
            return null;
        }
        return this._env.getFactory().newTypeMirror(bound);
    }
    
    @Override
    public <R, P> R accept(final TypeVisitor<R, P> v, final P p) {
        return v.visitWildcard(this, p);
    }
}
