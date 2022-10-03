package org.eclipse.jdt.internal.compiler.apt.model;

import javax.lang.model.type.TypeKind;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.TypeMirror;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import javax.lang.model.type.ArrayType;

public class ArrayTypeImpl extends TypeMirrorImpl implements ArrayType
{
    ArrayTypeImpl(final BaseProcessingEnvImpl env, final ArrayBinding binding) {
        super(env, binding);
    }
    
    @Override
    public TypeMirror getComponentType() {
        return this._env.getFactory().newTypeMirror(((ArrayBinding)this._binding).elementsType());
    }
    
    @Override
    public <R, P> R accept(final TypeVisitor<R, P> v, final P p) {
        return v.visitArray(this, p);
    }
    
    @Override
    protected AnnotationBinding[] getAnnotationBindings() {
        final AnnotationBinding[] oldies = ((ArrayBinding)this._binding).getTypeAnnotations();
        AnnotationBinding[] newbies = Binding.NO_ANNOTATIONS;
        for (int i = 0, length = (oldies == null) ? 0 : oldies.length; i < length; ++i) {
            if (oldies[i] == null) {
                System.arraycopy(oldies, 0, newbies = new AnnotationBinding[i], 0, i);
                return newbies;
            }
        }
        return newbies;
    }
    
    @Override
    public TypeKind getKind() {
        return TypeKind.ARRAY;
    }
}
