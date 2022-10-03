package org.eclipse.jdt.internal.compiler.apt.dispatch;

import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.apt.model.ElementImpl;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.AptSourceLocalVariableBinding;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import org.eclipse.jdt.internal.compiler.apt.util.ManyToMany;
import org.eclipse.jdt.internal.compiler.apt.model.Factory;
import org.eclipse.jdt.internal.compiler.ASTVisitor;

public class AnnotationDiscoveryVisitor extends ASTVisitor
{
    final BaseProcessingEnvImpl _env;
    final Factory _factory;
    final ManyToMany<TypeElement, Element> _annoToElement;
    
    public AnnotationDiscoveryVisitor(final BaseProcessingEnvImpl env) {
        this._env = env;
        this._factory = env.getFactory();
        this._annoToElement = new ManyToMany<TypeElement, Element>();
    }
    
    @Override
    public boolean visit(final Argument argument, final BlockScope scope) {
        final Annotation[] annotations = argument.annotations;
        final ReferenceContext referenceContext = scope.referenceContext();
        if (referenceContext instanceof AbstractMethodDeclaration) {
            final MethodBinding binding = ((AbstractMethodDeclaration)referenceContext).binding;
            if (binding != null) {
                final TypeDeclaration typeDeclaration = scope.referenceType();
                typeDeclaration.binding.resolveTypesFor(binding);
                if (argument.binding != null) {
                    argument.binding = new AptSourceLocalVariableBinding(argument.binding, binding);
                }
            }
            if (annotations != null && argument.binding != null) {
                this.resolveAnnotations(scope, annotations, argument.binding);
            }
        }
        return false;
    }
    
    @Override
    public boolean visit(final ConstructorDeclaration constructorDeclaration, final ClassScope scope) {
        final Annotation[] annotations = constructorDeclaration.annotations;
        if (annotations != null) {
            final MethodBinding constructorBinding = constructorDeclaration.binding;
            if (constructorBinding == null) {
                return false;
            }
            ((SourceTypeBinding)constructorBinding.declaringClass).resolveTypesFor(constructorBinding);
            this.resolveAnnotations(constructorDeclaration.scope, annotations, constructorBinding);
        }
        final TypeParameter[] typeParameters = constructorDeclaration.typeParameters;
        if (typeParameters != null) {
            for (int typeParametersLength = typeParameters.length, i = 0; i < typeParametersLength; ++i) {
                typeParameters[i].traverse(this, constructorDeclaration.scope);
            }
        }
        final Argument[] arguments = constructorDeclaration.arguments;
        if (arguments != null) {
            for (int argumentLength = arguments.length, j = 0; j < argumentLength; ++j) {
                arguments[j].traverse(this, constructorDeclaration.scope);
            }
        }
        return false;
    }
    
    @Override
    public boolean visit(final FieldDeclaration fieldDeclaration, final MethodScope scope) {
        final Annotation[] annotations = fieldDeclaration.annotations;
        if (annotations != null) {
            final FieldBinding fieldBinding = fieldDeclaration.binding;
            if (fieldBinding == null) {
                return false;
            }
            ((SourceTypeBinding)fieldBinding.declaringClass).resolveTypeFor(fieldBinding);
            this.resolveAnnotations(scope, annotations, fieldBinding);
        }
        return false;
    }
    
    @Override
    public boolean visit(final TypeParameter typeParameter, final ClassScope scope) {
        final Annotation[] annotations = typeParameter.annotations;
        if (annotations != null) {
            final TypeVariableBinding binding = typeParameter.binding;
            if (binding == null) {
                return false;
            }
            this.resolveAnnotations(scope.referenceContext.initializerScope, annotations, binding);
        }
        return false;
    }
    
    @Override
    public boolean visit(final TypeParameter typeParameter, final BlockScope scope) {
        final Annotation[] annotations = typeParameter.annotations;
        if (annotations != null) {
            final TypeVariableBinding binding = typeParameter.binding;
            if (binding == null) {
                return false;
            }
            final MethodBinding methodBinding = (MethodBinding)binding.declaringElement;
            ((SourceTypeBinding)methodBinding.declaringClass).resolveTypesFor(methodBinding);
            this.resolveAnnotations(scope, annotations, binding);
        }
        return false;
    }
    
    @Override
    public boolean visit(final MethodDeclaration methodDeclaration, final ClassScope scope) {
        final Annotation[] annotations = methodDeclaration.annotations;
        if (annotations != null) {
            final MethodBinding methodBinding = methodDeclaration.binding;
            if (methodBinding == null) {
                return false;
            }
            ((SourceTypeBinding)methodBinding.declaringClass).resolveTypesFor(methodBinding);
            this.resolveAnnotations(methodDeclaration.scope, annotations, methodBinding);
        }
        final TypeParameter[] typeParameters = methodDeclaration.typeParameters;
        if (typeParameters != null) {
            for (int typeParametersLength = typeParameters.length, i = 0; i < typeParametersLength; ++i) {
                typeParameters[i].traverse(this, methodDeclaration.scope);
            }
        }
        final Argument[] arguments = methodDeclaration.arguments;
        if (arguments != null) {
            for (int argumentLength = arguments.length, j = 0; j < argumentLength; ++j) {
                arguments[j].traverse(this, methodDeclaration.scope);
            }
        }
        return false;
    }
    
    @Override
    public boolean visit(final TypeDeclaration memberTypeDeclaration, final ClassScope scope) {
        final SourceTypeBinding binding = memberTypeDeclaration.binding;
        if (binding == null) {
            return false;
        }
        final Annotation[] annotations = memberTypeDeclaration.annotations;
        if (annotations != null) {
            this.resolveAnnotations(memberTypeDeclaration.staticInitializerScope, annotations, binding);
        }
        return true;
    }
    
    @Override
    public boolean visit(final TypeDeclaration typeDeclaration, final CompilationUnitScope scope) {
        final SourceTypeBinding binding = typeDeclaration.binding;
        if (binding == null) {
            return false;
        }
        final Annotation[] annotations = typeDeclaration.annotations;
        if (annotations != null) {
            this.resolveAnnotations(typeDeclaration.staticInitializerScope, annotations, binding);
        }
        return true;
    }
    
    private void resolveAnnotations(final BlockScope scope, final Annotation[] annotations, final Binding currentBinding) {
        final int length = (annotations == null) ? 0 : annotations.length;
        if (length == 0) {
            return;
        }
        final boolean old = scope.insideTypeAnnotation;
        scope.insideTypeAnnotation = true;
        currentBinding.getAnnotationTagBits();
        scope.insideTypeAnnotation = old;
        final ElementImpl element = (ElementImpl)this._factory.newElement(currentBinding);
        final AnnotationBinding[] annotationBindings = element.getPackedAnnotationBindings();
        AnnotationBinding[] array;
        for (int length2 = (array = annotationBindings).length, i = 0; i < length2; ++i) {
            final AnnotationBinding binding = array[i];
            final ReferenceBinding annotationType = binding.getAnnotationType();
            if (binding != null && Annotation.isAnnotationTargetAllowed(scope, annotationType, currentBinding)) {
                final TypeElement anno = (TypeElement)this._factory.newElement(annotationType);
                this._annoToElement.put(anno, element);
            }
        }
    }
}
