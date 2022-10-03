package org.eclipse.jdt.internal.compiler.apt.model;

import javax.lang.model.type.TypeKind;
import java.lang.reflect.Array;
import java.lang.annotation.Annotation;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import java.util.ArrayList;
import java.util.Collections;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import java.util.List;
import javax.lang.model.type.TypeMirror;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import javax.lang.model.element.Element;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import javax.lang.model.type.ErrorType;

public class ErrorTypeImpl extends DeclaredTypeImpl implements ErrorType
{
    ErrorTypeImpl(final BaseProcessingEnvImpl env, final ReferenceBinding binding) {
        super(env, binding);
    }
    
    @Override
    public Element asElement() {
        return this._env.getFactory().newElement(this._binding);
    }
    
    @Override
    public TypeMirror getEnclosingType() {
        return NoTypeImpl.NO_TYPE_NONE;
    }
    
    @Override
    public List<? extends TypeMirror> getTypeArguments() {
        final ReferenceBinding binding = (ReferenceBinding)this._binding;
        if (binding.isParameterizedType()) {
            final ParameterizedTypeBinding ptb = (ParameterizedTypeBinding)this._binding;
            final TypeBinding[] arguments = ptb.arguments;
            final int length = (arguments == null) ? 0 : arguments.length;
            if (length == 0) {
                return Collections.emptyList();
            }
            final List<TypeMirror> args = new ArrayList<TypeMirror>(length);
            TypeBinding[] array;
            for (int length2 = (array = arguments).length, i = 0; i < length2; ++i) {
                final TypeBinding arg = array[i];
                args.add(this._env.getFactory().newTypeMirror(arg));
            }
            return Collections.unmodifiableList((List<? extends TypeMirror>)args);
        }
        else {
            if (binding.isGenericType()) {
                final TypeVariableBinding[] typeVariables = binding.typeVariables();
                final List<TypeMirror> args2 = new ArrayList<TypeMirror>(typeVariables.length);
                TypeVariableBinding[] array2;
                for (int length3 = (array2 = typeVariables).length, j = 0; j < length3; ++j) {
                    final TypeBinding arg2 = array2[j];
                    args2.add(this._env.getFactory().newTypeMirror(arg2));
                }
                return Collections.unmodifiableList((List<? extends TypeMirror>)args2);
            }
            return Collections.emptyList();
        }
    }
    
    @Override
    public <R, P> R accept(final TypeVisitor<R, P> v, final P p) {
        return v.visitError(this, p);
    }
    
    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return Factory.EMPTY_ANNOTATION_MIRRORS;
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
        return null;
    }
    
    @Override
    public <A extends Annotation> A[] getAnnotationsByType(final Class<A> annotationType) {
        return (A[])Array.newInstance(annotationType, 0);
    }
    
    @Override
    public TypeKind getKind() {
        return TypeKind.ERROR;
    }
    
    @Override
    public String toString() {
        return new String(this._binding.readableName());
    }
}
