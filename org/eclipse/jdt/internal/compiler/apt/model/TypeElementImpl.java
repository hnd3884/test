package org.eclipse.jdt.internal.compiler.apt.model;

import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import java.util.IdentityHashMap;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import javax.lang.model.element.TypeParameterElement;
import org.eclipse.jdt.core.compiler.CharOperation;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.Modifier;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.element.VariableElement;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import javax.lang.model.element.ExecutableElement;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import java.util.Comparator;
import java.util.Collections;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import java.util.ArrayList;
import javax.lang.model.element.Element;
import java.util.List;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import javax.lang.model.element.ElementVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

public class TypeElementImpl extends ElementImpl implements TypeElement
{
    private final ElementKind _kindHint;
    
    TypeElementImpl(final BaseProcessingEnvImpl env, final ReferenceBinding binding, final ElementKind kindHint) {
        super(env, binding);
        this._kindHint = kindHint;
    }
    
    @Override
    public <R, P> R accept(final ElementVisitor<R, P> v, final P p) {
        return v.visitType(this, p);
    }
    
    @Override
    protected AnnotationBinding[] getAnnotationBindings() {
        return ((ReferenceBinding)this._binding).getAnnotations();
    }
    
    @Override
    public List<? extends Element> getEnclosedElements() {
        final ReferenceBinding binding = (ReferenceBinding)this._binding;
        final List<Element> enclosed = new ArrayList<Element>(binding.fieldCount() + binding.methods().length + binding.memberTypes().length);
        MethodBinding[] methods;
        for (int length = (methods = binding.methods()).length, i = 0; i < length; ++i) {
            final MethodBinding method = methods[i];
            final ExecutableElement executable = new ExecutableElementImpl(this._env, method);
            enclosed.add(executable);
        }
        FieldBinding[] fields;
        for (int length2 = (fields = binding.fields()).length, j = 0; j < length2; ++j) {
            final FieldBinding field = fields[j];
            if (!field.isSynthetic()) {
                final VariableElement variable = new VariableElementImpl(this._env, field);
                enclosed.add(variable);
            }
        }
        ReferenceBinding[] memberTypes;
        for (int length3 = (memberTypes = binding.memberTypes()).length, k = 0; k < length3; ++k) {
            final ReferenceBinding memberType = memberTypes[k];
            final TypeElement type = new TypeElementImpl(this._env, memberType, null);
            enclosed.add(type);
        }
        Collections.sort(enclosed, new SourceLocationComparator(null));
        return Collections.unmodifiableList((List<? extends Element>)enclosed);
    }
    
    @Override
    public Element getEnclosingElement() {
        final ReferenceBinding binding = (ReferenceBinding)this._binding;
        final ReferenceBinding enclosingType = binding.enclosingType();
        if (enclosingType == null) {
            return this._env.getFactory().newPackageElement(binding.fPackage);
        }
        return this._env.getFactory().newElement(binding.enclosingType());
    }
    
    @Override
    public String getFileName() {
        final char[] name = ((ReferenceBinding)this._binding).getFileName();
        if (name == null) {
            return null;
        }
        return new String(name);
    }
    
    @Override
    public List<? extends TypeMirror> getInterfaces() {
        final ReferenceBinding binding = (ReferenceBinding)this._binding;
        if (binding.superInterfaces() == null || binding.superInterfaces().length == 0) {
            return Collections.emptyList();
        }
        final List<TypeMirror> interfaces = new ArrayList<TypeMirror>(binding.superInterfaces().length);
        ReferenceBinding[] superInterfaces;
        for (int length = (superInterfaces = binding.superInterfaces()).length, i = 0; i < length; ++i) {
            final ReferenceBinding interfaceBinding = superInterfaces[i];
            final TypeMirror interfaceType = this._env.getFactory().newTypeMirror(interfaceBinding);
            if (interfaceType.getKind() == TypeKind.ERROR) {
                if (this._env.getSourceVersion().compareTo(SourceVersion.RELEASE_6) > 0) {
                    interfaces.add(interfaceType);
                }
            }
            else {
                interfaces.add(interfaceType);
            }
        }
        return Collections.unmodifiableList((List<? extends TypeMirror>)interfaces);
    }
    
    @Override
    public ElementKind getKind() {
        if (this._kindHint != null) {
            return this._kindHint;
        }
        final ReferenceBinding refBinding = (ReferenceBinding)this._binding;
        if (refBinding.isEnum()) {
            return ElementKind.ENUM;
        }
        if (refBinding.isAnnotationType()) {
            return ElementKind.ANNOTATION_TYPE;
        }
        if (refBinding.isInterface()) {
            return ElementKind.INTERFACE;
        }
        if (refBinding.isClass()) {
            return ElementKind.CLASS;
        }
        throw new IllegalArgumentException("TypeElement " + new String(refBinding.shortReadableName()) + " has unexpected attributes " + refBinding.modifiers);
    }
    
    @Override
    public Set<Modifier> getModifiers() {
        final ReferenceBinding refBinding = (ReferenceBinding)this._binding;
        int modifiers = refBinding.modifiers;
        if (refBinding.isInterface() && refBinding.isNestedType()) {
            modifiers |= 0x8;
        }
        return Factory.getModifiers(modifiers, this.getKind(), refBinding.isBinaryBinding());
    }
    
    @Override
    public NestingKind getNestingKind() {
        final ReferenceBinding refBinding = (ReferenceBinding)this._binding;
        if (refBinding.isAnonymousType()) {
            return NestingKind.ANONYMOUS;
        }
        if (refBinding.isLocalType()) {
            return NestingKind.LOCAL;
        }
        if (refBinding.isMemberType()) {
            return NestingKind.MEMBER;
        }
        return NestingKind.TOP_LEVEL;
    }
    
    @Override
    PackageElement getPackage() {
        final ReferenceBinding binding = (ReferenceBinding)this._binding;
        return this._env.getFactory().newPackageElement(binding.fPackage);
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
    public Name getSimpleName() {
        final ReferenceBinding binding = (ReferenceBinding)this._binding;
        return new NameImpl(binding.sourceName());
    }
    
    @Override
    public TypeMirror getSuperclass() {
        final ReferenceBinding binding = (ReferenceBinding)this._binding;
        final ReferenceBinding superBinding = binding.superclass();
        if (superBinding == null || binding.isInterface()) {
            return this._env.getFactory().getNoType(TypeKind.NONE);
        }
        return this._env.getFactory().newTypeMirror(superBinding);
    }
    
    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        final ReferenceBinding binding = (ReferenceBinding)this._binding;
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
        if (!(hidden instanceof TypeElementImpl)) {
            return false;
        }
        final ReferenceBinding hiddenBinding = (ReferenceBinding)((TypeElementImpl)hidden)._binding;
        if (hiddenBinding.isPrivate()) {
            return false;
        }
        final ReferenceBinding hiderBinding = (ReferenceBinding)this._binding;
        return !TypeBinding.equalsEquals(hiddenBinding, hiderBinding) && hiddenBinding.isMemberType() && hiderBinding.isMemberType() && CharOperation.equals(hiddenBinding.sourceName, hiderBinding.sourceName) && hiderBinding.enclosingType().findSuperTypeOriginatingFrom(hiddenBinding.enclosingType()) != null;
    }
    
    @Override
    public String toString() {
        final ReferenceBinding binding = (ReferenceBinding)this._binding;
        final char[] concatWith = CharOperation.concatWith(binding.compoundName, '.');
        if (binding.isNestedType()) {
            CharOperation.replace(concatWith, '$', '.');
            return new String(concatWith);
        }
        return new String(concatWith);
    }
    
    private static final class SourceLocationComparator implements Comparator<Element>
    {
        private final IdentityHashMap<ElementImpl, Integer> sourceStartCache;
        
        private SourceLocationComparator() {
            this.sourceStartCache = new IdentityHashMap<ElementImpl, Integer>();
        }
        
        @Override
        public int compare(final Element o1, final Element o2) {
            final ElementImpl e1 = (ElementImpl)o1;
            final ElementImpl e2 = (ElementImpl)o2;
            return this.getSourceStart(e1) - this.getSourceStart(e2);
        }
        
        private int getSourceStart(final ElementImpl e) {
            Integer value = this.sourceStartCache.get(e);
            if (value == null) {
                value = this.determineSourceStart(e);
                this.sourceStartCache.put(e, value);
            }
            return value;
        }
        
        private int determineSourceStart(final ElementImpl e) {
            switch (e.getKind()) {
                case ENUM:
                case CLASS:
                case ANNOTATION_TYPE:
                case INTERFACE: {
                    final TypeElementImpl typeElementImpl = (TypeElementImpl)e;
                    final Binding typeBinding = typeElementImpl._binding;
                    if (typeBinding instanceof SourceTypeBinding) {
                        final SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)typeBinding;
                        final TypeDeclaration typeDeclaration = (TypeDeclaration)sourceTypeBinding.scope.referenceContext();
                        return typeDeclaration.sourceStart;
                    }
                    break;
                }
                case METHOD:
                case CONSTRUCTOR: {
                    final ExecutableElementImpl executableElementImpl = (ExecutableElementImpl)e;
                    final Binding binding = executableElementImpl._binding;
                    if (binding instanceof MethodBinding) {
                        final MethodBinding methodBinding = (MethodBinding)binding;
                        return methodBinding.sourceStart();
                    }
                    break;
                }
                case ENUM_CONSTANT:
                case FIELD: {
                    final VariableElementImpl variableElementImpl = (VariableElementImpl)e;
                    final Binding binding = variableElementImpl._binding;
                    if (!(binding instanceof FieldBinding)) {
                        break;
                    }
                    final FieldBinding fieldBinding = (FieldBinding)binding;
                    final FieldDeclaration fieldDeclaration = fieldBinding.sourceField();
                    if (fieldDeclaration != null) {
                        return fieldDeclaration.sourceStart;
                    }
                    break;
                }
            }
            return -1;
        }
    }
}
