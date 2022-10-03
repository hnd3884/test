package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import java.util.List;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MemberTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.CompilationResult;

public class MethodDeclaration extends AbstractMethodDeclaration
{
    public TypeReference returnType;
    public TypeParameter[] typeParameters;
    
    public MethodDeclaration(final CompilationResult compilationResult) {
        super(compilationResult);
        this.bits |= 0x100;
    }
    
    public void analyseCode(final ClassScope classScope, final FlowContext flowContext, FlowInfo flowInfo) {
        if (this.ignoreFurtherInvestigation) {
            return;
        }
        try {
            if (this.binding == null) {
                return;
            }
            if (!this.binding.isUsed() && !this.binding.isAbstract() && (this.binding.isPrivate() || ((this.binding.modifiers & 0x30000000) == 0x0 && this.binding.isOrEnclosedByPrivateType())) && !classScope.referenceCompilationUnit().compilationResult.hasSyntaxError) {
                this.scope.problemReporter().unusedPrivateMethod(this);
            }
            if (this.binding.declaringClass.isEnum() && (this.selector == TypeConstants.VALUES || this.selector == TypeConstants.VALUEOF)) {
                return;
            }
            if (this.binding.isAbstract() || this.binding.isNative()) {
                return;
            }
            if (this.typeParameters != null && !this.scope.referenceCompilationUnit().compilationResult.hasSyntaxError) {
                for (int i = 0, length = this.typeParameters.length; i < length; ++i) {
                    final TypeParameter typeParameter = this.typeParameters[i];
                    if ((typeParameter.binding.modifiers & 0x8000000) == 0x0) {
                        this.scope.problemReporter().unusedTypeParameter(typeParameter);
                    }
                }
            }
            final ExceptionHandlingFlowContext methodContext = new ExceptionHandlingFlowContext(flowContext, this, this.binding.thrownExceptions, null, this.scope, FlowInfo.DEAD_END);
            AbstractMethodDeclaration.analyseArguments(classScope.environment(), flowInfo, this.arguments, this.binding);
            if (this.binding.declaringClass instanceof MemberTypeBinding && !this.binding.declaringClass.isStatic()) {
                this.bits &= 0xFFFFFEFF;
            }
            if (this.statements != null) {
                final boolean enableSyntacticNullAnalysisForFields = this.scope.compilerOptions().enableSyntacticNullAnalysisForFields;
                int complaintLevel = ((flowInfo.reachMode() & 0x3) != 0x0) ? 1 : 0;
                for (int j = 0, count = this.statements.length; j < count; ++j) {
                    final Statement stat = this.statements[j];
                    if ((complaintLevel = stat.complainIfUnreachable(flowInfo, this.scope, complaintLevel, true)) < 2) {
                        flowInfo = stat.analyseCode(this.scope, methodContext, flowInfo);
                    }
                    if (enableSyntacticNullAnalysisForFields) {
                        methodContext.expireNullCheckedFieldInfo();
                    }
                }
            }
            else {
                this.bits &= 0xFFFFFEFF;
            }
            final TypeBinding returnTypeBinding = this.binding.returnType;
            if (returnTypeBinding == TypeBinding.VOID || this.isAbstract()) {
                if ((flowInfo.tagBits & 0x1) == 0x0) {
                    this.bits |= 0x40;
                }
            }
            else if (flowInfo != FlowInfo.DEAD_END) {
                this.scope.problemReporter().shouldReturn(returnTypeBinding, this);
            }
            methodContext.complainIfUnusedExceptionHandlers(this);
            this.scope.checkUnusedParameters(this.binding);
            if (!this.binding.isStatic() && (this.bits & 0x100) != 0x0 && !this.isDefaultMethod() && !this.binding.isOverriding() && !this.binding.isImplementing()) {
                if (this.binding.isPrivate() || this.binding.isFinal() || this.binding.declaringClass.isFinal()) {
                    this.scope.problemReporter().methodCanBeDeclaredStatic(this);
                }
                else {
                    this.scope.problemReporter().methodCanBePotentiallyDeclaredStatic(this);
                }
            }
            this.scope.checkUnclosedCloseables(flowInfo, null, null, null);
        }
        catch (final AbortMethod abortMethod) {
            this.ignoreFurtherInvestigation = true;
        }
    }
    
    @Override
    public void getAllAnnotationContexts(final int targetType, final List allAnnotationContexts) {
        final TypeReference.AnnotationCollector collector = new TypeReference.AnnotationCollector(this.returnType, targetType, allAnnotationContexts);
        for (int i = 0, max = this.annotations.length; i < max; ++i) {
            final Annotation annotation = this.annotations[i];
            annotation.traverse(collector, (BlockScope)null);
        }
    }
    
    public boolean hasNullTypeAnnotation(final TypeReference.AnnotationPosition position) {
        return TypeReference.containsNullAnnotation(this.annotations) || (this.returnType != null && this.returnType.hasNullTypeAnnotation(position));
    }
    
    @Override
    public boolean isDefaultMethod() {
        return (this.modifiers & 0x10000) != 0x0;
    }
    
    @Override
    public boolean isMethod() {
        return true;
    }
    
    @Override
    public void parseStatements(final Parser parser, final CompilationUnitDeclaration unit) {
        parser.parse(this, unit);
    }
    
    @Override
    public StringBuffer printReturnType(final int indent, final StringBuffer output) {
        if (this.returnType == null) {
            return output;
        }
        return this.returnType.printExpression(0, output).append(' ');
    }
    
    @Override
    public void resolveStatements() {
        if (this.returnType != null && this.binding != null) {
            this.bits |= (this.returnType.bits & 0x100000);
            this.returnType.resolvedType = this.binding.returnType;
        }
        if (CharOperation.equals(this.scope.enclosingSourceType().sourceName, this.selector)) {
            this.scope.problemReporter().methodWithConstructorName(this);
        }
        boolean returnsUndeclTypeVar = false;
        if (this.returnType != null && this.returnType.resolvedType instanceof TypeVariableBinding) {
            returnsUndeclTypeVar = true;
        }
        if (this.typeParameters != null) {
            for (int i = 0, length = this.typeParameters.length; i < length; ++i) {
                final TypeParameter typeParameter = this.typeParameters[i];
                this.bits |= (typeParameter.bits & 0x100000);
                if (returnsUndeclTypeVar && TypeBinding.equalsEquals(this.typeParameters[i].binding, this.returnType.resolvedType)) {
                    returnsUndeclTypeVar = false;
                }
            }
        }
        final CompilerOptions compilerOptions = this.scope.compilerOptions();
        if (this.binding != null) {
            final long complianceLevel = compilerOptions.complianceLevel;
            if (complianceLevel >= 3211264L) {
                final int bindingModifiers = this.binding.modifiers;
                final boolean hasOverrideAnnotation = (this.binding.tagBits & 0x2000000000000L) != 0x0L;
                final boolean hasUnresolvedArguments = (this.binding.tagBits & 0x200L) != 0x0L;
                if (hasOverrideAnnotation && !hasUnresolvedArguments) {
                    if ((bindingModifiers & 0x10000008) != 0x10000000) {
                        if (complianceLevel < 3276800L || (bindingModifiers & 0x20000008) != 0x20000000) {
                            this.scope.problemReporter().methodMustOverride(this, complianceLevel);
                        }
                    }
                }
                else if (!this.binding.declaringClass.isInterface()) {
                    if ((bindingModifiers & 0x10000008) == 0x10000000) {
                        this.scope.problemReporter().missingOverrideAnnotation(this);
                    }
                    else if (complianceLevel >= 3276800L && compilerOptions.reportMissingOverrideAnnotationForInterfaceMethodImplementation && this.binding.isImplementing()) {
                        this.scope.problemReporter().missingOverrideAnnotationForInterfaceMethodImplementation(this);
                    }
                }
                else if (complianceLevel >= 3276800L && compilerOptions.reportMissingOverrideAnnotationForInterfaceMethodImplementation && ((bindingModifiers & 0x10000008) == 0x10000000 || this.binding.isImplementing())) {
                    this.scope.problemReporter().missingOverrideAnnotationForInterfaceMethodImplementation(this);
                }
            }
        }
        switch (TypeDeclaration.kind(this.scope.referenceType().modifiers)) {
            case 3: {
                if (this.selector == TypeConstants.VALUES) {
                    break;
                }
                if (this.selector == TypeConstants.VALUEOF) {
                    break;
                }
            }
            case 1: {
                if ((this.modifiers & 0x1000000) != 0x0) {
                    if ((this.modifiers & 0x100) == 0x0 && (this.modifiers & 0x400) == 0x0) {
                        this.scope.problemReporter().methodNeedBody(this);
                        break;
                    }
                    break;
                }
                else {
                    if ((this.modifiers & 0x100) != 0x0 || (this.modifiers & 0x400) != 0x0) {
                        this.scope.problemReporter().methodNeedingNoBody(this);
                        break;
                    }
                    if (this.binding == null || this.binding.isStatic() || this.binding.declaringClass instanceof LocalTypeBinding || returnsUndeclTypeVar) {
                        this.bits &= 0xFFFFFEFF;
                        break;
                    }
                    break;
                }
                break;
            }
            case 2: {
                if (compilerOptions.sourceLevel >= 3407872L && (this.modifiers & 0x1000400) == 0x1000000 && (this.modifiers & 0x10008) != 0x0) {
                    this.scope.problemReporter().methodNeedBody(this);
                    break;
                }
                break;
            }
        }
        super.resolveStatements();
        if (compilerOptions.getSeverity(537919488) != 256 && this.binding != null) {
            final int bindingModifiers2 = this.binding.modifiers;
            if ((bindingModifiers2 & 0x30000000) == 0x10000000 && (this.bits & 0x10) == 0x0) {
                this.scope.problemReporter().overridesMethodWithoutSuperInvocation(this.binding);
            }
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope classScope) {
        if (visitor.visit(this, classScope)) {
            if (this.javadoc != null) {
                this.javadoc.traverse(visitor, this.scope);
            }
            if (this.annotations != null) {
                for (int annotationsLength = this.annotations.length, i = 0; i < annotationsLength; ++i) {
                    this.annotations[i].traverse(visitor, this.scope);
                }
            }
            if (this.typeParameters != null) {
                for (int typeParametersLength = this.typeParameters.length, i = 0; i < typeParametersLength; ++i) {
                    this.typeParameters[i].traverse(visitor, this.scope);
                }
            }
            if (this.returnType != null) {
                this.returnType.traverse(visitor, this.scope);
            }
            if (this.arguments != null) {
                for (int argumentLength = this.arguments.length, i = 0; i < argumentLength; ++i) {
                    this.arguments[i].traverse(visitor, this.scope);
                }
            }
            if (this.thrownExceptions != null) {
                for (int thrownExceptionsLength = this.thrownExceptions.length, i = 0; i < thrownExceptionsLength; ++i) {
                    this.thrownExceptions[i].traverse(visitor, this.scope);
                }
            }
            if (this.statements != null) {
                for (int statementsLength = this.statements.length, i = 0; i < statementsLength; ++i) {
                    this.statements[i].traverse(visitor, this.scope);
                }
            }
        }
        visitor.endVisit(this, classScope);
    }
    
    @Override
    public TypeParameter[] typeParameters() {
        return this.typeParameters;
    }
}
