package org.eclipse.jdt.internal.compiler.apt.model;

import javax.lang.model.element.Modifier;
import java.util.Set;
import javax.lang.model.element.Element;
import java.lang.reflect.Array;
import javax.lang.model.element.AnnotationMirror;
import java.lang.annotation.Annotation;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeKind;
import org.eclipse.jdt.core.compiler.CharOperation;
import javax.lang.model.element.Name;
import javax.lang.model.element.NestingKind;
import java.util.Collections;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import javax.lang.model.element.ElementKind;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;

public class ErrorTypeElement extends TypeElementImpl
{
    ErrorTypeElement(final BaseProcessingEnvImpl env, final ReferenceBinding binding) {
        super(env, binding, null);
    }
    
    @Override
    public List<? extends TypeMirror> getInterfaces() {
        return Collections.emptyList();
    }
    
    @Override
    public NestingKind getNestingKind() {
        return NestingKind.TOP_LEVEL;
    }
    
    @Override
    public Name getQualifiedName() {
        final ReferenceBinding binding = (ReferenceBinding)this._binding;
        char[] qName;
        if (binding.isMemberType()) {
            qName = CharOperation.concatWith(binding.enclosingType().compoundName, binding.sourceName, '.');
            CharOperation.replace(qName, '$', '.');
        }
        else {
            qName = CharOperation.concatWith(binding.compoundName, '.');
        }
        return new NameImpl(qName);
    }
    
    @Override
    public TypeMirror getSuperclass() {
        return this._env.getFactory().getNoType(TypeKind.NONE);
    }
    
    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        return Collections.emptyList();
    }
    
    @Override
    public TypeMirror asType() {
        return this._env.getFactory().getErrorType((ReferenceBinding)this._binding);
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
        return null;
    }
    
    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return Collections.emptyList();
    }
    
    @Override
    public <A extends Annotation> A[] getAnnotationsByType(final Class<A> annotationType) {
        return (A[])Array.newInstance(annotationType, 0);
    }
    
    @Override
    public List<? extends Element> getEnclosedElements() {
        return Collections.emptyList();
    }
    
    @Override
    public Element getEnclosingElement() {
        return this._env.getFactory().newPackageElement(this._env.getLookupEnvironment().defaultPackage);
    }
    
    @Override
    public ElementKind getKind() {
        return ElementKind.CLASS;
    }
    
    @Override
    public Set<Modifier> getModifiers() {
        return Collections.emptySet();
    }
    
    @Override
    public Name getSimpleName() {
        final ReferenceBinding binding = (ReferenceBinding)this._binding;
        return new NameImpl(binding.sourceName());
    }
}
