package org.eclipse.jdt.internal.compiler.apt.dispatch;

import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import javax.lang.model.util.ElementFilter;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl;
import javax.lang.model.element.ElementKind;
import java.util.Collections;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.apt.model.Factory;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import org.eclipse.jdt.internal.compiler.apt.util.ManyToMany;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import javax.annotation.processing.RoundEnvironment;

public class RoundEnvImpl implements RoundEnvironment
{
    private final BaseProcessingEnvImpl _processingEnv;
    private final boolean _isLastRound;
    private final CompilationUnitDeclaration[] _units;
    private final ManyToMany<TypeElement, Element> _annoToUnit;
    private final ReferenceBinding[] _binaryTypes;
    private final Factory _factory;
    private Set<Element> _rootElements;
    
    public RoundEnvImpl(final CompilationUnitDeclaration[] units, final ReferenceBinding[] binaryTypeBindings, final boolean isLastRound, final BaseProcessingEnvImpl env) {
        this._rootElements = null;
        this._processingEnv = env;
        this._isLastRound = isLastRound;
        this._units = units;
        this._factory = this._processingEnv.getFactory();
        final AnnotationDiscoveryVisitor visitor = new AnnotationDiscoveryVisitor(this._processingEnv);
        if (this._units != null) {
            CompilationUnitDeclaration[] units2;
            for (int length = (units2 = this._units).length, i = 0; i < length; ++i) {
                final CompilationUnitDeclaration unit = units2[i];
                unit.scope.suppressImportErrors = true;
                unit.traverse(visitor, unit.scope);
                unit.scope.suppressImportErrors = false;
            }
        }
        this._annoToUnit = visitor._annoToElement;
        if (binaryTypeBindings != null) {
            this.collectAnnotations(binaryTypeBindings);
        }
        this._binaryTypes = binaryTypeBindings;
    }
    
    private void collectAnnotations(final ReferenceBinding[] referenceBindings) {
        for (ReferenceBinding referenceBinding : referenceBindings) {
            if (referenceBinding instanceof ParameterizedTypeBinding) {
                referenceBinding = ((ParameterizedTypeBinding)referenceBinding).genericType();
            }
            AnnotationBinding[] annotationBindings = Factory.getPackedAnnotationBindings(referenceBinding.getAnnotations());
            AnnotationBinding[] array;
            for (int length2 = (array = annotationBindings).length, j = 0; j < length2; ++j) {
                final AnnotationBinding annotationBinding = array[j];
                final TypeElement anno = (TypeElement)this._factory.newElement(annotationBinding.getAnnotationType());
                final Element element = this._factory.newElement(referenceBinding);
                this._annoToUnit.put(anno, element);
            }
            final FieldBinding[] fieldBindings = referenceBinding.fields();
            FieldBinding[] array2;
            for (int length3 = (array2 = fieldBindings).length, k = 0; k < length3; ++k) {
                final FieldBinding fieldBinding = array2[k];
                annotationBindings = Factory.getPackedAnnotationBindings(fieldBinding.getAnnotations());
                AnnotationBinding[] array3;
                for (int length4 = (array3 = annotationBindings).length, l = 0; l < length4; ++l) {
                    final AnnotationBinding annotationBinding2 = array3[l];
                    final TypeElement anno2 = (TypeElement)this._factory.newElement(annotationBinding2.getAnnotationType());
                    final Element element2 = this._factory.newElement(fieldBinding);
                    this._annoToUnit.put(anno2, element2);
                }
            }
            final MethodBinding[] methodBindings = referenceBinding.methods();
            MethodBinding[] array4;
            for (int length5 = (array4 = methodBindings).length, n = 0; n < length5; ++n) {
                final MethodBinding methodBinding = array4[n];
                annotationBindings = Factory.getPackedAnnotationBindings(methodBinding.getAnnotations());
                AnnotationBinding[] array5;
                for (int length6 = (array5 = annotationBindings).length, n2 = 0; n2 < length6; ++n2) {
                    final AnnotationBinding annotationBinding3 = array5[n2];
                    final TypeElement anno3 = (TypeElement)this._factory.newElement(annotationBinding3.getAnnotationType());
                    final Element element3 = this._factory.newElement(methodBinding);
                    this._annoToUnit.put(anno3, element3);
                }
            }
            final ReferenceBinding[] memberTypes = referenceBinding.memberTypes();
            this.collectAnnotations(memberTypes);
        }
    }
    
    public Set<TypeElement> getRootAnnotations() {
        return Collections.unmodifiableSet((Set<? extends TypeElement>)this._annoToUnit.getKeySet());
    }
    
    @Override
    public boolean errorRaised() {
        return this._processingEnv.errorRaised();
    }
    
    @Override
    public Set<? extends Element> getElementsAnnotatedWith(final TypeElement a) {
        if (a.getKind() != ElementKind.ANNOTATION_TYPE) {
            throw new IllegalArgumentException("Argument must represent an annotation type");
        }
        final Binding annoBinding = ((TypeElementImpl)a)._binding;
        if (0x0L != (annoBinding.getAnnotationTagBits() & 0x1000000000000L)) {
            final Set<Element> annotatedElements = new HashSet<Element>(this._annoToUnit.getValues(a));
            final ReferenceBinding annoTypeBinding = (ReferenceBinding)annoBinding;
            for (final TypeElement element : ElementFilter.typesIn(this.getRootElements())) {
                final ReferenceBinding typeBinding = (ReferenceBinding)((TypeElementImpl)element)._binding;
                this.addAnnotatedElements(annoTypeBinding, typeBinding, annotatedElements);
            }
            return Collections.unmodifiableSet((Set<? extends Element>)annotatedElements);
        }
        return Collections.unmodifiableSet((Set<? extends Element>)this._annoToUnit.getValues(a));
    }
    
    private void addAnnotatedElements(final ReferenceBinding anno, final ReferenceBinding type, final Set<Element> result) {
        if (type.isClass() && this.inheritsAnno(type, anno)) {
            result.add(this._factory.newElement(type));
        }
        ReferenceBinding[] memberTypes;
        for (int length = (memberTypes = type.memberTypes()).length, i = 0; i < length; ++i) {
            final ReferenceBinding element = memberTypes[i];
            this.addAnnotatedElements(anno, element, result);
        }
    }
    
    private boolean inheritsAnno(final ReferenceBinding element, final ReferenceBinding anno) {
        ReferenceBinding searchedElement = element;
        do {
            if (searchedElement instanceof ParameterizedTypeBinding) {
                searchedElement = ((ParameterizedTypeBinding)searchedElement).genericType();
            }
            final AnnotationBinding[] annos = Factory.getPackedAnnotationBindings(searchedElement.getAnnotations());
            AnnotationBinding[] array;
            for (int length = (array = annos).length, i = 0; i < length; ++i) {
                final AnnotationBinding annoBinding = array[i];
                if (annoBinding.getAnnotationType() == anno) {
                    return true;
                }
            }
        } while ((searchedElement = searchedElement.superclass()) != null);
        return false;
    }
    
    @Override
    public Set<? extends Element> getElementsAnnotatedWith(final Class<? extends Annotation> a) {
        final String canonicalName = a.getCanonicalName();
        if (canonicalName == null) {
            throw new IllegalArgumentException("Argument must represent an annotation type");
        }
        final TypeElement annoType = this._processingEnv.getElementUtils().getTypeElement(canonicalName);
        if (annoType == null) {
            return Collections.emptySet();
        }
        return this.getElementsAnnotatedWith(annoType);
    }
    
    @Override
    public Set<? extends Element> getRootElements() {
        if (this._units == null) {
            return Collections.emptySet();
        }
        if (this._rootElements == null) {
            final Set<Element> elements = new HashSet<Element>(this._units.length);
            CompilationUnitDeclaration[] units;
            for (int length = (units = this._units).length, i = 0; i < length; ++i) {
                final CompilationUnitDeclaration unit = units[i];
                if (unit.scope != null) {
                    if (unit.scope.topLevelTypes != null) {
                        SourceTypeBinding[] topLevelTypes;
                        for (int length2 = (topLevelTypes = unit.scope.topLevelTypes).length, j = 0; j < length2; ++j) {
                            final SourceTypeBinding binding = topLevelTypes[j];
                            final Element element = this._factory.newElement(binding);
                            if (element == null) {
                                throw new IllegalArgumentException("Top-level type binding could not be converted to element: " + binding);
                            }
                            elements.add(element);
                        }
                    }
                }
            }
            if (this._binaryTypes != null) {
                ReferenceBinding[] binaryTypes;
                for (int length3 = (binaryTypes = this._binaryTypes).length, k = 0; k < length3; ++k) {
                    final ReferenceBinding typeBinding = binaryTypes[k];
                    final Element element2 = this._factory.newElement(typeBinding);
                    if (element2 == null) {
                        throw new IllegalArgumentException("Top-level type binding could not be converted to element: " + typeBinding);
                    }
                    elements.add(element2);
                }
            }
            this._rootElements = elements;
        }
        return this._rootElements;
    }
    
    @Override
    public boolean processingOver() {
        return this._isLastRound;
    }
}
