package org.eclipse.jdt.internal.compiler.apt.model;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import javax.lang.model.type.TypeVariable;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import java.util.ArrayList;
import java.util.Collections;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import javax.lang.model.type.ExecutableType;

public class ExecutableTypeImpl extends TypeMirrorImpl implements ExecutableType
{
    ExecutableTypeImpl(final BaseProcessingEnvImpl env, final MethodBinding binding) {
        super(env, binding);
    }
    
    @Override
    public List<? extends TypeMirror> getParameterTypes() {
        final MethodBinding binding = (MethodBinding)this._binding;
        final TypeBinding[] parameters = binding.parameters;
        final int length = parameters.length;
        final boolean isEnumConstructor = binding.isConstructor() && binding.declaringClass.isEnum() && binding.declaringClass.isBinaryBinding() && (binding.modifiers & 0x40000000) == 0x0;
        if (isEnumConstructor) {
            if (length == 2) {
                return Collections.emptyList();
            }
            final ArrayList<TypeMirror> list = new ArrayList<TypeMirror>();
            for (int i = 2; i < length; ++i) {
                list.add(this._env.getFactory().newTypeMirror(parameters[i]));
            }
            return Collections.unmodifiableList((List<? extends TypeMirror>)list);
        }
        else {
            if (length != 0) {
                final ArrayList<TypeMirror> list = new ArrayList<TypeMirror>();
                TypeBinding[] array;
                for (int length2 = (array = parameters).length, j = 0; j < length2; ++j) {
                    final TypeBinding typeBinding = array[j];
                    list.add(this._env.getFactory().newTypeMirror(typeBinding));
                }
                return Collections.unmodifiableList((List<? extends TypeMirror>)list);
            }
            return Collections.emptyList();
        }
    }
    
    @Override
    public TypeMirror getReturnType() {
        return this._env.getFactory().newTypeMirror(((MethodBinding)this._binding).returnType);
    }
    
    @Override
    protected AnnotationBinding[] getAnnotationBindings() {
        return ((MethodBinding)this._binding).returnType.getTypeAnnotations();
    }
    
    @Override
    public List<? extends TypeMirror> getThrownTypes() {
        final ArrayList<TypeMirror> list = new ArrayList<TypeMirror>();
        final ReferenceBinding[] thrownExceptions = ((MethodBinding)this._binding).thrownExceptions;
        if (thrownExceptions.length != 0) {
            ReferenceBinding[] array;
            for (int length = (array = thrownExceptions).length, i = 0; i < length; ++i) {
                final ReferenceBinding referenceBinding = array[i];
                list.add(this._env.getFactory().newTypeMirror(referenceBinding));
            }
        }
        return Collections.unmodifiableList((List<? extends TypeMirror>)list);
    }
    
    @Override
    public List<? extends TypeVariable> getTypeVariables() {
        final ArrayList<TypeVariable> list = new ArrayList<TypeVariable>();
        final TypeVariableBinding[] typeVariables = ((MethodBinding)this._binding).typeVariables();
        if (typeVariables.length != 0) {
            TypeVariableBinding[] array;
            for (int length = (array = typeVariables).length, i = 0; i < length; ++i) {
                final TypeVariableBinding typeVariableBinding = array[i];
                list.add((TypeVariable)this._env.getFactory().newTypeMirror(typeVariableBinding));
            }
        }
        return Collections.unmodifiableList((List<? extends TypeVariable>)list);
    }
    
    @Override
    public <R, P> R accept(final TypeVisitor<R, P> v, final P p) {
        return v.visitExecutable(this, p);
    }
    
    @Override
    public TypeKind getKind() {
        return TypeKind.EXECUTABLE;
    }
    
    @Override
    public TypeMirror getReceiverType() {
        return this._env.getFactory().getReceiverType((MethodBinding)this._binding);
    }
    
    @Override
    public String toString() {
        return new String(((MethodBinding)this._binding).returnType.readableName());
    }
}
