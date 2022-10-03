package org.eclipse.jdt.internal.compiler.apt.model;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.ElementKind;
import java.lang.reflect.Array;
import java.lang.annotation.Annotation;
import javax.lang.model.element.AnnotationMirror;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import javax.lang.model.element.ElementVisitor;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import java.util.Collections;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeParameterElement;

public class TypeParameterElementImpl extends ElementImpl implements TypeParameterElement
{
    private final Element _declaringElement;
    private List<? extends TypeMirror> _bounds;
    
    TypeParameterElementImpl(final BaseProcessingEnvImpl env, final TypeVariableBinding binding, final Element declaringElement) {
        super(env, binding);
        this._bounds = null;
        this._declaringElement = declaringElement;
    }
    
    TypeParameterElementImpl(final BaseProcessingEnvImpl env, final TypeVariableBinding binding) {
        super(env, binding);
        this._bounds = null;
        this._declaringElement = this._env.getFactory().newElement(binding.declaringElement);
    }
    
    @Override
    public List<? extends TypeMirror> getBounds() {
        if (this._bounds == null) {
            this._bounds = this.calculateBounds();
        }
        return this._bounds;
    }
    
    private List<? extends TypeMirror> calculateBounds() {
        final TypeVariableBinding typeVariableBinding = (TypeVariableBinding)this._binding;
        final ReferenceBinding varSuperclass = typeVariableBinding.superclass();
        TypeBinding firstClassOrArrayBound = typeVariableBinding.firstBound;
        int boundsLength = 0;
        boolean isFirstBoundATypeVariable = false;
        if (firstClassOrArrayBound != null) {
            if (firstClassOrArrayBound.isTypeVariable()) {
                isFirstBoundATypeVariable = true;
            }
            if (TypeBinding.equalsEquals(firstClassOrArrayBound, varSuperclass)) {
                ++boundsLength;
                if (firstClassOrArrayBound.isTypeVariable()) {
                    isFirstBoundATypeVariable = true;
                }
            }
            else if (firstClassOrArrayBound.isArrayType()) {
                ++boundsLength;
            }
            else {
                firstClassOrArrayBound = null;
            }
        }
        final ReferenceBinding[] superinterfaces = typeVariableBinding.superInterfaces();
        int superinterfacesLength = 0;
        if (superinterfaces != null) {
            superinterfacesLength = superinterfaces.length;
            boundsLength += superinterfacesLength;
        }
        final List<TypeMirror> typeBounds = new ArrayList<TypeMirror>(boundsLength);
        if (boundsLength != 0) {
            if (firstClassOrArrayBound != null) {
                final TypeMirror typeBinding = this._env.getFactory().newTypeMirror(firstClassOrArrayBound);
                if (typeBinding == null) {
                    return Collections.emptyList();
                }
                typeBounds.add(typeBinding);
            }
            if (superinterfaces != null && !isFirstBoundATypeVariable) {
                for (int i = 0; i < superinterfacesLength; ++i) {
                    final TypeMirror typeBinding2 = this._env.getFactory().newTypeMirror(superinterfaces[i]);
                    if (typeBinding2 == null) {
                        return Collections.emptyList();
                    }
                    typeBounds.add(typeBinding2);
                }
            }
        }
        else {
            typeBounds.add(this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().getType(LookupEnvironment.JAVA_LANG_OBJECT)));
        }
        return Collections.unmodifiableList((List<? extends TypeMirror>)typeBounds);
    }
    
    @Override
    public Element getGenericElement() {
        return this._declaringElement;
    }
    
    @Override
    public <R, P> R accept(final ElementVisitor<R, P> v, final P p) {
        return v.visitTypeParameter(this, p);
    }
    
    @Override
    protected AnnotationBinding[] getAnnotationBindings() {
        return ((TypeVariableBinding)this._binding).getTypeAnnotations();
    }
    
    private boolean shouldEmulateJavacBug() {
        if (this._env.getLookupEnvironment().globalOptions.emulateJavacBug8031744) {
            final AnnotationBinding[] annotations = this.getAnnotationBindings();
            for (int i = 0, length = annotations.length; i < length; ++i) {
                final ReferenceBinding firstAnnotationType = annotations[i].getAnnotationType();
                for (int j = i + 1; j < length; ++j) {
                    final ReferenceBinding secondAnnotationType = annotations[j].getAnnotationType();
                    if (firstAnnotationType == secondAnnotationType) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        if (this.shouldEmulateJavacBug()) {
            return Collections.emptyList();
        }
        return super.getAnnotationMirrors();
    }
    
    @Override
    public <A extends Annotation> A[] getAnnotationsByType(final Class<A> annotationType) {
        if (this.shouldEmulateJavacBug()) {
            return (A[])Array.newInstance(annotationType, 0);
        }
        return super.getAnnotationsByType(annotationType);
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
        if (this.shouldEmulateJavacBug()) {
            return null;
        }
        return super.getAnnotation(annotationType);
    }
    
    @Override
    public List<? extends Element> getEnclosedElements() {
        return Collections.emptyList();
    }
    
    @Override
    public Element getEnclosingElement() {
        return this.getGenericElement();
    }
    
    @Override
    public ElementKind getKind() {
        return ElementKind.TYPE_PARAMETER;
    }
    
    @Override
    PackageElement getPackage() {
        return null;
    }
    
    @Override
    public String toString() {
        return new String(this._binding.readableName());
    }
}
