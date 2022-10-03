package org.eclipse.jdt.internal.compiler.apt.model;

import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import javax.lang.model.element.TypeElement;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationHolder;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.AptBinaryLocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import java.util.ArrayList;
import javax.lang.model.element.VariableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.Modifier;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import javax.lang.model.element.ElementKind;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import java.util.Collections;
import javax.lang.model.element.Element;
import java.util.List;
import javax.lang.model.element.AnnotationValue;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import javax.lang.model.element.ElementVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import javax.lang.model.element.Name;
import javax.lang.model.element.ExecutableElement;

public class ExecutableElementImpl extends ElementImpl implements ExecutableElement
{
    private Name _name;
    
    ExecutableElementImpl(final BaseProcessingEnvImpl env, final MethodBinding binding) {
        super(env, binding);
        this._name = null;
    }
    
    @Override
    public <R, P> R accept(final ElementVisitor<R, P> v, final P p) {
        return v.visitExecutable(this, p);
    }
    
    @Override
    protected AnnotationBinding[] getAnnotationBindings() {
        return ((MethodBinding)this._binding).getAnnotations();
    }
    
    @Override
    public AnnotationValue getDefaultValue() {
        final MethodBinding binding = (MethodBinding)this._binding;
        final Object defaultValue = binding.getDefaultValue();
        if (defaultValue != null) {
            return new AnnotationMemberValue(this._env, defaultValue, binding);
        }
        return null;
    }
    
    @Override
    public List<? extends Element> getEnclosedElements() {
        return Collections.emptyList();
    }
    
    @Override
    public Element getEnclosingElement() {
        final MethodBinding binding = (MethodBinding)this._binding;
        if (binding.declaringClass == null) {
            return null;
        }
        return this._env.getFactory().newElement(binding.declaringClass);
    }
    
    @Override
    public String getFileName() {
        final ReferenceBinding dc = ((MethodBinding)this._binding).declaringClass;
        final char[] name = dc.getFileName();
        if (name == null) {
            return null;
        }
        return new String(name);
    }
    
    @Override
    public ElementKind getKind() {
        final MethodBinding binding = (MethodBinding)this._binding;
        if (binding.isConstructor()) {
            return ElementKind.CONSTRUCTOR;
        }
        if (CharOperation.equals(binding.selector, TypeConstants.CLINIT)) {
            return ElementKind.STATIC_INIT;
        }
        if (CharOperation.equals(binding.selector, TypeConstants.INIT)) {
            return ElementKind.INSTANCE_INIT;
        }
        return ElementKind.METHOD;
    }
    
    @Override
    public Set<Modifier> getModifiers() {
        final MethodBinding binding = (MethodBinding)this._binding;
        return Factory.getModifiers(binding.modifiers, this.getKind());
    }
    
    @Override
    PackageElement getPackage() {
        final MethodBinding binding = (MethodBinding)this._binding;
        if (binding.declaringClass == null) {
            return null;
        }
        return this._env.getFactory().newPackageElement(binding.declaringClass.fPackage);
    }
    
    @Override
    public List<? extends VariableElement> getParameters() {
        final MethodBinding binding = (MethodBinding)this._binding;
        final int length = (binding.parameters == null) ? 0 : binding.parameters.length;
        if (length != 0) {
            final AbstractMethodDeclaration methodDeclaration = binding.sourceMethod();
            final List<VariableElement> params = new ArrayList<VariableElement>(length);
            if (methodDeclaration != null) {
                Argument[] arguments;
                for (int length2 = (arguments = methodDeclaration.arguments).length, j = 0; j < length2; ++j) {
                    final Argument argument = arguments[j];
                    final VariableElement param = new VariableElementImpl(this._env, argument.binding);
                    params.add(param);
                }
            }
            else {
                AnnotationBinding[][] parameterAnnotationBindings = null;
                final AnnotationHolder annotationHolder = binding.declaringClass.retrieveAnnotationHolder(binding, false);
                if (annotationHolder != null) {
                    parameterAnnotationBindings = annotationHolder.getParameterAnnotations();
                }
                int i = 0;
                TypeBinding[] parameters;
                for (int length3 = (parameters = binding.parameters).length, k = 0; k < length3; ++k) {
                    final TypeBinding typeBinding = parameters[k];
                    char[] name = (char[])((binding.parameterNames.length > i) ? binding.parameterNames[i] : null);
                    if (name == null) {
                        final StringBuilder builder = new StringBuilder("arg");
                        builder.append(i);
                        name = String.valueOf(builder).toCharArray();
                    }
                    final VariableElement param2 = new VariableElementImpl(this._env, new AptBinaryLocalVariableBinding(name, typeBinding, 0, (AnnotationBinding[])((parameterAnnotationBindings != null) ? parameterAnnotationBindings[i] : null), binding));
                    params.add(param2);
                    ++i;
                }
            }
            return Collections.unmodifiableList((List<? extends VariableElement>)params);
        }
        return Collections.emptyList();
    }
    
    @Override
    public TypeMirror getReturnType() {
        final MethodBinding binding = (MethodBinding)this._binding;
        if (binding.returnType == null) {
            return null;
        }
        return this._env.getFactory().newTypeMirror(binding.returnType);
    }
    
    @Override
    public Name getSimpleName() {
        final MethodBinding binding = (MethodBinding)this._binding;
        if (this._name == null) {
            this._name = new NameImpl(binding.selector);
        }
        return this._name;
    }
    
    @Override
    public List<? extends TypeMirror> getThrownTypes() {
        final MethodBinding binding = (MethodBinding)this._binding;
        if (binding.thrownExceptions.length == 0) {
            return Collections.emptyList();
        }
        final List<TypeMirror> list = new ArrayList<TypeMirror>(binding.thrownExceptions.length);
        ReferenceBinding[] thrownExceptions;
        for (int length = (thrownExceptions = binding.thrownExceptions).length, i = 0; i < length; ++i) {
            final ReferenceBinding exception = thrownExceptions[i];
            list.add(this._env.getFactory().newTypeMirror(exception));
        }
        return list;
    }
    
    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        final MethodBinding binding = (MethodBinding)this._binding;
        final TypeVariableBinding[] variables = binding.typeVariables();
        if (variables.length == 0) {
            return Collections.emptyList();
        }
        final List<TypeParameterElement> params = new ArrayList<TypeParameterElement>(variables.length);
        TypeVariableBinding[] array;
        for (int length = (array = variables).length, i = 0; i < length; ++i) {
            final TypeVariableBinding variable = array[i];
            params.add(this._env.getFactory().newTypeParameterElement(variable, this));
        }
        return Collections.unmodifiableList((List<? extends TypeParameterElement>)params);
    }
    
    @Override
    public boolean hides(final Element hidden) {
        if (!(hidden instanceof ExecutableElementImpl)) {
            return false;
        }
        final MethodBinding hiderBinding = (MethodBinding)this._binding;
        final MethodBinding hiddenBinding = (MethodBinding)((ExecutableElementImpl)hidden)._binding;
        return hiderBinding != hiddenBinding && !hiddenBinding.isPrivate() && hiderBinding.isStatic() && hiddenBinding.isStatic() && CharOperation.equals(hiddenBinding.selector, hiderBinding.selector) && this._env.getLookupEnvironment().methodVerifier().isMethodSubsignature(hiderBinding, hiddenBinding) && hiderBinding.declaringClass.findSuperTypeOriginatingFrom(hiddenBinding.declaringClass) != null;
    }
    
    @Override
    public boolean isVarArgs() {
        return ((MethodBinding)this._binding).isVarargs();
    }
    
    public boolean overrides(final ExecutableElement overridden, final TypeElement type) {
        final MethodBinding overriddenBinding = (MethodBinding)((ExecutableElementImpl)overridden)._binding;
        final ReferenceBinding overriderContext = (ReferenceBinding)((TypeElementImpl)type)._binding;
        if (this._binding == overriddenBinding || overriddenBinding.isStatic() || overriddenBinding.isPrivate() || ((MethodBinding)this._binding).isStatic()) {
            return false;
        }
        final char[] selector = ((MethodBinding)this._binding).selector;
        if (!CharOperation.equals(selector, overriddenBinding.selector)) {
            return false;
        }
        if (overriderContext.findSuperTypeOriginatingFrom(((MethodBinding)this._binding).declaringClass) == null && ((MethodBinding)this._binding).declaringClass.findSuperTypeOriginatingFrom(overriderContext) == null) {
            return false;
        }
        final MethodBinding overriderBinding = new MethodBinding((MethodBinding)this._binding, overriderContext);
        if (overriderBinding.isPrivate()) {
            return false;
        }
        final TypeBinding match = overriderBinding.declaringClass.findSuperTypeOriginatingFrom(overriddenBinding.declaringClass);
        if (!(match instanceof ReferenceBinding)) {
            return false;
        }
        final MethodBinding[] superMethods = ((ReferenceBinding)match).getMethods(selector);
        final LookupEnvironment lookupEnvironment = this._env.getLookupEnvironment();
        if (lookupEnvironment == null) {
            return false;
        }
        final MethodVerifier methodVerifier = lookupEnvironment.methodVerifier();
        for (int i = 0, length = superMethods.length; i < length; ++i) {
            if (superMethods[i].original() == overriddenBinding) {
                return methodVerifier.doesMethodOverride(overriderBinding, superMethods[i]);
            }
        }
        return false;
    }
    
    @Override
    public TypeMirror getReceiverType() {
        return this._env.getFactory().getReceiverType((MethodBinding)this._binding);
    }
    
    @Override
    public boolean isDefault() {
        return this._binding != null && ((MethodBinding)this._binding).isDefaultMethod();
    }
}
