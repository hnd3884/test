package org.eclipse.jdt.internal.compiler.apt.dispatch;

import javax.lang.model.element.ElementKind;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.apt.model.AnnotationMemberValue;
import org.eclipse.jdt.internal.compiler.apt.model.AnnotationMirrorImpl;
import org.eclipse.jdt.internal.compiler.lookup.AptSourceLocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.apt.model.VariableElementImpl;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.apt.model.ExecutableElementImpl;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class BaseMessagerImpl
{
    static final String[] NO_ARGUMENTS;
    
    static {
        NO_ARGUMENTS = new String[0];
    }
    
    public static AptProblem createProblem(final Diagnostic.Kind kind, final CharSequence msg, final Element e, final AnnotationMirror a, final AnnotationValue v) {
        ReferenceContext referenceContext = null;
        Annotation[] elementAnnotations = null;
        int startPosition = 0;
        int endPosition = 0;
        if (e != null) {
            switch (e.getKind()) {
                case ENUM:
                case CLASS:
                case ANNOTATION_TYPE:
                case INTERFACE: {
                    final TypeElementImpl typeElementImpl = (TypeElementImpl)e;
                    final Binding typeBinding = typeElementImpl._binding;
                    if (typeBinding instanceof SourceTypeBinding) {
                        final SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)typeBinding;
                        final TypeDeclaration typeDeclaration = (TypeDeclaration)(referenceContext = sourceTypeBinding.scope.referenceContext());
                        elementAnnotations = typeDeclaration.annotations;
                        startPosition = typeDeclaration.sourceStart;
                        endPosition = typeDeclaration.sourceEnd;
                        break;
                    }
                    break;
                }
                case METHOD:
                case CONSTRUCTOR: {
                    final ExecutableElementImpl executableElementImpl = (ExecutableElementImpl)e;
                    final Binding binding = executableElementImpl._binding;
                    if (!(binding instanceof MethodBinding)) {
                        break;
                    }
                    final MethodBinding methodBinding = (MethodBinding)binding;
                    final AbstractMethodDeclaration sourceMethod = methodBinding.sourceMethod();
                    if (sourceMethod != null) {
                        referenceContext = sourceMethod;
                        elementAnnotations = sourceMethod.annotations;
                        startPosition = sourceMethod.sourceStart;
                        endPosition = sourceMethod.sourceEnd;
                        break;
                    }
                    break;
                }
                case ENUM_CONSTANT: {}
                case FIELD:
                case PARAMETER: {
                    final VariableElementImpl variableElementImpl = (VariableElementImpl)e;
                    final Binding binding = variableElementImpl._binding;
                    if (binding instanceof FieldBinding) {
                        final FieldBinding fieldBinding = (FieldBinding)binding;
                        final FieldDeclaration fieldDeclaration = fieldBinding.sourceField();
                        if (fieldDeclaration != null) {
                            final ReferenceBinding declaringClass = fieldBinding.declaringClass;
                            if (declaringClass instanceof SourceTypeBinding) {
                                final SourceTypeBinding sourceTypeBinding2 = (SourceTypeBinding)declaringClass;
                                final TypeDeclaration typeDeclaration2 = (TypeDeclaration)(referenceContext = sourceTypeBinding2.scope.referenceContext());
                            }
                            elementAnnotations = fieldDeclaration.annotations;
                            startPosition = fieldDeclaration.sourceStart;
                            endPosition = fieldDeclaration.sourceEnd;
                            break;
                        }
                        break;
                    }
                    else {
                        if (!(binding instanceof AptSourceLocalVariableBinding)) {
                            break;
                        }
                        final AptSourceLocalVariableBinding parameterBinding = (AptSourceLocalVariableBinding)binding;
                        final LocalDeclaration parameterDeclaration = parameterBinding.declaration;
                        if (parameterDeclaration != null) {
                            final MethodBinding methodBinding2 = parameterBinding.methodBinding;
                            if (methodBinding2 != null) {
                                referenceContext = methodBinding2.sourceMethod();
                            }
                            elementAnnotations = parameterDeclaration.annotations;
                            startPosition = parameterDeclaration.sourceStart;
                            endPosition = parameterDeclaration.sourceEnd;
                            break;
                        }
                        break;
                    }
                    break;
                }
                case STATIC_INIT:
                case INSTANCE_INIT: {}
            }
        }
        final StringBuilder builder = new StringBuilder();
        if (msg != null) {
            builder.append(msg);
        }
        if (a != null && elementAnnotations != null) {
            final AnnotationBinding annotationBinding = ((AnnotationMirrorImpl)a)._binding;
            final Annotation annotation = findAnnotation(elementAnnotations, annotationBinding);
            if (annotation != null) {
                startPosition = annotation.sourceStart;
                endPosition = annotation.sourceEnd;
                if (v != null && v instanceof AnnotationMemberValue) {
                    final MethodBinding methodBinding3 = ((AnnotationMemberValue)v).getMethodBinding();
                    final MemberValuePair[] memberValuePairs = annotation.memberValuePairs();
                    MemberValuePair memberValuePair = null;
                    for (int i = 0; memberValuePair == null && i < memberValuePairs.length; ++i) {
                        if (methodBinding3 == memberValuePairs[i].binding) {
                            memberValuePair = memberValuePairs[i];
                        }
                    }
                    if (memberValuePair != null) {
                        startPosition = memberValuePair.sourceStart;
                        endPosition = memberValuePair.sourceEnd;
                    }
                }
            }
        }
        int lineNumber = 0;
        int columnNumber = 1;
        char[] fileName = null;
        if (referenceContext != null) {
            final CompilationResult result = referenceContext.compilationResult();
            fileName = result.fileName;
            int[] lineEnds = null;
            lineNumber = ((startPosition >= 0) ? Util.getLineNumber(startPosition, lineEnds = result.getLineSeparatorPositions(), 0, lineEnds.length - 1) : 0);
            columnNumber = ((startPosition >= 0) ? Util.searchColumnNumber(result.getLineSeparatorPositions(), lineNumber, startPosition) : 0);
        }
        int severity = 0;
        switch (kind) {
            case ERROR: {
                severity = 1;
                break;
            }
            default: {
                severity = 0;
                break;
            }
        }
        return new AptProblem(referenceContext, fileName, String.valueOf(builder), 0, BaseMessagerImpl.NO_ARGUMENTS, severity, startPosition, endPosition, lineNumber, columnNumber);
    }
    
    private static Annotation findAnnotation(final Annotation[] elementAnnotations, final AnnotationBinding annotationBinding) {
        for (int i = 0; i < elementAnnotations.length; ++i) {
            final Annotation annotation = findAnnotation(elementAnnotations[i], annotationBinding);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }
    
    private static Annotation findAnnotation(final Annotation elementAnnotation, final AnnotationBinding annotationBinding) {
        if (annotationBinding == elementAnnotation.getCompilerAnnotation()) {
            return elementAnnotation;
        }
        final MemberValuePair[] memberValuePairs = elementAnnotation.memberValuePairs();
        MemberValuePair[] array;
        for (int length = (array = memberValuePairs).length, i = 0; i < length; ++i) {
            final MemberValuePair mvp = array[i];
            final Expression v = mvp.value;
            if (v instanceof Annotation) {
                final Annotation a = findAnnotation((Annotation)v, annotationBinding);
                if (a != null) {
                    return a;
                }
            }
            else if (v instanceof ArrayInitializer) {
                final Expression[] expressions = ((ArrayInitializer)v).expressions;
                Expression[] array2;
                for (int length2 = (array2 = expressions).length, j = 0; j < length2; ++j) {
                    final Expression e = array2[j];
                    if (e instanceof Annotation) {
                        final Annotation a2 = findAnnotation((Annotation)e, annotationBinding);
                        if (a2 != null) {
                            return a2;
                        }
                    }
                }
            }
        }
        return null;
    }
}
