package org.eclipse.jdt.internal.compiler.apt.model;

import java.lang.reflect.Array;
import java.lang.annotation.Annotation;
import javax.lang.model.element.AnnotationMirror;
import java.util.List;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import javax.lang.model.type.TypeMirror;

public class TypeMirrorImpl implements TypeMirror
{
    protected final BaseProcessingEnvImpl _env;
    protected final Binding _binding;
    
    TypeMirrorImpl(final BaseProcessingEnvImpl env, final Binding binding) {
        this._env = env;
        this._binding = binding;
    }
    
    Binding binding() {
        return this._binding;
    }
    
    @Override
    public <R, P> R accept(final TypeVisitor<R, P> v, final P p) {
        return v.visit(this, p);
    }
    
    @Override
    public TypeKind getKind() {
        switch (this._binding.kind()) {
            case 1:
            case 2:
            case 3:
            case 32: {
                throw new IllegalArgumentException("Invalid binding kind: " + this._binding.kind());
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public String toString() {
        return new String(this._binding.readableName());
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + ((this._binding == null) ? 0 : this._binding.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TypeMirrorImpl)) {
            return false;
        }
        final TypeMirrorImpl other = (TypeMirrorImpl)obj;
        return this._binding == other._binding;
    }
    
    public final AnnotationBinding[] getPackedAnnotationBindings() {
        return Factory.getPackedAnnotationBindings(this.getAnnotationBindings());
    }
    
    protected AnnotationBinding[] getAnnotationBindings() {
        return ((TypeBinding)this._binding).getTypeAnnotations();
    }
    
    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return (this._env == null) ? Factory.EMPTY_ANNOTATION_MIRRORS : this._env.getFactory().getAnnotationMirrors(this.getPackedAnnotationBindings());
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
        return (A)((this._env == null) ? null : this._env.getFactory().getAnnotation(this.getPackedAnnotationBindings(), annotationType));
    }
    
    @Override
    public <A extends Annotation> A[] getAnnotationsByType(final Class<A> annotationType) {
        if (this._env == null) {
            return (A[])Array.newInstance(annotationType, 0);
        }
        return this._env.getFactory().getAnnotationsByType(Factory.getUnpackedAnnotationBindings(this.getPackedAnnotationBindings()), annotationType);
    }
}
