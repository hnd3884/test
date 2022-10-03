package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import java.util.List;
import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.CompilationResult;

public class ConstructorDeclaration extends AbstractMethodDeclaration
{
    public ExplicitConstructorCall constructorCall;
    public TypeParameter[] typeParameters;
    
    public ConstructorDeclaration(final CompilationResult compilationResult) {
        super(compilationResult);
    }
    
    public void analyseCode(final ClassScope classScope, final InitializationFlowContext initializerFlowContext, FlowInfo flowInfo, final int initialReachMode) {
        if (this.ignoreFurtherInvestigation) {
            return;
        }
        final int nonStaticFieldInfoReachMode = flowInfo.reachMode();
        flowInfo.setReachMode(initialReachMode);
        Label_0232: {
            final MethodBinding constructorBinding;
            if ((constructorBinding = this.binding) != null) {
                if ((this.bits & 0x80) == 0x0) {
                    if (!constructorBinding.isUsed()) {
                        if (constructorBinding.isPrivate()) {
                            if ((this.binding.declaringClass.tagBits & 0x1000000000000000L) == 0x0L) {
                                break Label_0232;
                            }
                        }
                        else if (!constructorBinding.isOrEnclosedByPrivateType()) {
                            break Label_0232;
                        }
                        if (this.constructorCall != null) {
                            if (this.constructorCall.accessMode != 3) {
                                final ReferenceBinding superClass = constructorBinding.declaringClass.superclass();
                                if (superClass == null) {
                                    break Label_0232;
                                }
                                final MethodBinding methodBinding = superClass.getExactConstructor(Binding.NO_PARAMETERS);
                                if (methodBinding == null) {
                                    break Label_0232;
                                }
                                if (!methodBinding.canBeSeenBy(SuperReference.implicitSuperConstructorCall(), this.scope)) {
                                    break Label_0232;
                                }
                                final ReferenceBinding declaringClass = constructorBinding.declaringClass;
                                if (constructorBinding.isPublic() && constructorBinding.parameters.length == 0 && declaringClass.isStatic() && declaringClass.findSuperTypeOriginatingFrom(56, false) != null) {
                                    break Label_0232;
                                }
                            }
                            this.scope.problemReporter().unusedPrivateConstructor(this);
                        }
                    }
                }
            }
        }
        if (this.isRecursive(null)) {
            this.scope.problemReporter().recursiveConstructorInvocation(this.constructorCall);
        }
        if (this.typeParameters != null && !this.scope.referenceCompilationUnit().compilationResult.hasSyntaxError) {
            for (int i = 0, length = this.typeParameters.length; i < length; ++i) {
                final TypeParameter typeParameter = this.typeParameters[i];
                if ((typeParameter.binding.modifiers & 0x8000000) == 0x0) {
                    this.scope.problemReporter().unusedTypeParameter(typeParameter);
                }
            }
        }
        try {
            final ExceptionHandlingFlowContext constructorContext = new ExceptionHandlingFlowContext(initializerFlowContext.parent, this, this.binding.thrownExceptions, initializerFlowContext, this.scope, FlowInfo.DEAD_END);
            initializerFlowContext.checkInitializerExceptions(this.scope, constructorContext, flowInfo);
            if (this.binding.declaringClass.isAnonymousType()) {
                final ArrayList computedExceptions = constructorContext.extendedExceptions;
                final int size;
                if (computedExceptions != null && (size = computedExceptions.size()) > 0) {
                    final ReferenceBinding[] actuallyThrownExceptions;
                    computedExceptions.toArray(actuallyThrownExceptions = new ReferenceBinding[size]);
                    this.binding.thrownExceptions = actuallyThrownExceptions;
                }
            }
            AbstractMethodDeclaration.analyseArguments(classScope.environment(), flowInfo, this.arguments, this.binding);
            if (this.constructorCall != null) {
                if (this.constructorCall.accessMode == 3) {
                    final FieldBinding[] fields = this.binding.declaringClass.fields();
                    for (int j = 0, count = fields.length; j < count; ++j) {
                        final FieldBinding field;
                        if (!(field = fields[j]).isStatic()) {
                            flowInfo.markAsDefinitelyAssigned(field);
                        }
                    }
                }
                flowInfo = this.constructorCall.analyseCode(this.scope, constructorContext, flowInfo);
            }
            flowInfo.setReachMode(nonStaticFieldInfoReachMode);
            if (this.statements != null) {
                final boolean enableSyntacticNullAnalysisForFields = this.scope.compilerOptions().enableSyntacticNullAnalysisForFields;
                int complaintLevel = ((nonStaticFieldInfoReachMode & 0x3) != 0x0) ? 1 : 0;
                for (int k = 0, count2 = this.statements.length; k < count2; ++k) {
                    final Statement stat = this.statements[k];
                    if ((complaintLevel = stat.complainIfUnreachable(flowInfo, this.scope, complaintLevel, true)) < 2) {
                        flowInfo = stat.analyseCode(this.scope, constructorContext, flowInfo);
                    }
                    if (enableSyntacticNullAnalysisForFields) {
                        constructorContext.expireNullCheckedFieldInfo();
                    }
                }
            }
            if ((flowInfo.tagBits & 0x1) == 0x0) {
                this.bits |= 0x40;
            }
            if (this.constructorCall != null && this.constructorCall.accessMode != 3) {
                flowInfo = flowInfo.mergedWith(constructorContext.initsOnReturn);
                final FieldBinding[] fields = this.binding.declaringClass.fields();
                for (int j = 0, count = fields.length; j < count; ++j) {
                    final FieldBinding field = fields[j];
                    if (!field.isStatic() && !flowInfo.isDefinitelyAssigned(field)) {
                        if (field.isFinal()) {
                            this.scope.problemReporter().uninitializedBlankFinalField(field, ((this.bits & 0x80) != 0x0) ? this.scope.referenceType().declarationOf(field.original()) : this);
                        }
                        else if (field.isNonNull() || field.type.isFreeTypeVariable()) {
                            final FieldDeclaration fieldDecl = this.scope.referenceType().declarationOf(field.original());
                            if (!this.isValueProvidedUsingAnnotation(fieldDecl)) {
                                this.scope.problemReporter().uninitializedNonNullField(field, ((this.bits & 0x80) != 0x0) ? fieldDecl : this);
                            }
                        }
                    }
                }
            }
            constructorContext.complainIfUnusedExceptionHandlers(this);
            this.scope.checkUnusedParameters(this.binding);
            this.scope.checkUnclosedCloseables(flowInfo, null, null, null);
        }
        catch (final AbortMethod abortMethod) {
            this.ignoreFurtherInvestigation = true;
        }
    }
    
    boolean isValueProvidedUsingAnnotation(final FieldDeclaration fieldDecl) {
        if (fieldDecl.annotations != null) {
            for (int length = fieldDecl.annotations.length, i = 0; i < length; ++i) {
                final Annotation annotation = fieldDecl.annotations[i];
                if (annotation.resolvedType.id == 80) {
                    return true;
                }
                if (annotation.resolvedType.id == 81) {
                    final MemberValuePair[] memberValuePairs = annotation.memberValuePairs();
                    if (memberValuePairs == Annotation.NoValuePairs) {
                        return true;
                    }
                    for (int j = 0; j < memberValuePairs.length; ++j) {
                        if (CharOperation.equals(memberValuePairs[j].name, TypeConstants.OPTIONAL)) {
                            return memberValuePairs[j].value instanceof FalseLiteral;
                        }
                    }
                }
                else if (annotation.resolvedType.id == 82) {
                    final MemberValuePair[] memberValuePairs = annotation.memberValuePairs();
                    if (memberValuePairs == Annotation.NoValuePairs) {
                        return true;
                    }
                    for (int j = 0; j < memberValuePairs.length; ++j) {
                        if (CharOperation.equals(memberValuePairs[j].name, TypeConstants.REQUIRED)) {
                            return memberValuePairs[j].value instanceof TrueLiteral;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public void generateCode(final ClassScope classScope, final ClassFile classFile) {
        int problemResetPC = 0;
        if (!this.ignoreFurtherInvestigation) {
            boolean restart = false;
            boolean abort = false;
            CompilationResult unitResult = null;
            int problemCount = 0;
            if (classScope != null) {
                final TypeDeclaration referenceContext = classScope.referenceContext;
                if (referenceContext != null) {
                    unitResult = referenceContext.compilationResult();
                    problemCount = unitResult.problemCount;
                }
            }
            do {
                try {
                    problemResetPC = classFile.contentsOffset;
                    this.internalGenerateCode(classScope, classFile);
                    restart = false;
                }
                catch (final AbortMethod e) {
                    if (e.compilationResult == CodeStream.RESTART_IN_WIDE_MODE) {
                        classFile.contentsOffset = problemResetPC;
                        --classFile.methodCount;
                        classFile.codeStream.resetInWideMode();
                        if (unitResult != null) {
                            unitResult.problemCount = problemCount;
                        }
                        restart = true;
                    }
                    else if (e.compilationResult == CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE) {
                        classFile.contentsOffset = problemResetPC;
                        --classFile.methodCount;
                        classFile.codeStream.resetForCodeGenUnusedLocals();
                        if (unitResult != null) {
                            unitResult.problemCount = problemCount;
                        }
                        restart = true;
                    }
                    else {
                        restart = false;
                        abort = true;
                    }
                }
            } while (restart);
            if (abort) {
                final CategorizedProblem[] problems = this.scope.referenceCompilationUnit().compilationResult.getAllProblems();
                final int problemsLength;
                final CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength = problems.length];
                System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
                classFile.addProblemConstructor(this, this.binding, problemsCopy, problemResetPC);
            }
            return;
        }
        if (this.binding == null) {
            return;
        }
        final CategorizedProblem[] problems2 = this.scope.referenceCompilationUnit().compilationResult.getProblems();
        final int problemsLength2;
        final CategorizedProblem[] problemsCopy2 = new CategorizedProblem[problemsLength2 = problems2.length];
        System.arraycopy(problems2, 0, problemsCopy2, 0, problemsLength2);
        classFile.addProblemConstructor(this, this.binding, problemsCopy2);
    }
    
    public void generateSyntheticFieldInitializationsIfNecessary(final MethodScope methodScope, final CodeStream codeStream, final ReferenceBinding declaringClass) {
        if (!declaringClass.isNestedType()) {
            return;
        }
        final NestedTypeBinding nestedType = (NestedTypeBinding)declaringClass;
        SyntheticArgumentBinding[] syntheticArgs = nestedType.syntheticEnclosingInstances();
        if (syntheticArgs != null) {
            for (int i = 0, max = syntheticArgs.length; i < max; ++i) {
                final SyntheticArgumentBinding syntheticArg;
                if ((syntheticArg = syntheticArgs[i]).matchingField != null) {
                    codeStream.aload_0();
                    codeStream.load(syntheticArg);
                    codeStream.fieldAccess((byte)(-75), syntheticArg.matchingField, null);
                }
            }
        }
        syntheticArgs = nestedType.syntheticOuterLocalVariables();
        if (syntheticArgs != null) {
            for (int i = 0, max = syntheticArgs.length; i < max; ++i) {
                final SyntheticArgumentBinding syntheticArg;
                if ((syntheticArg = syntheticArgs[i]).matchingField != null) {
                    codeStream.aload_0();
                    codeStream.load(syntheticArg);
                    codeStream.fieldAccess((byte)(-75), syntheticArg.matchingField, null);
                }
            }
        }
    }
    
    private void internalGenerateCode(final ClassScope classScope, final ClassFile classFile) {
        classFile.generateMethodInfoHeader(this.binding);
        final int methodAttributeOffset = classFile.contentsOffset;
        int attributeNumber = classFile.generateMethodInfoAttributes(this.binding);
        if (!this.binding.isNative() && !this.binding.isAbstract()) {
            final TypeDeclaration declaringType = classScope.referenceContext;
            final int codeAttributeOffset = classFile.contentsOffset;
            classFile.generateCodeAttributeHeader();
            final CodeStream codeStream = classFile.codeStream;
            codeStream.reset(this, classFile);
            final ReferenceBinding declaringClass = this.binding.declaringClass;
            final int enumOffset = declaringClass.isEnum() ? 2 : 0;
            int argSlotSize = 1 + enumOffset;
            if (declaringClass.isNestedType()) {
                this.scope.extraSyntheticArguments = declaringClass.syntheticOuterLocalVariables();
                this.scope.computeLocalVariablePositions(declaringClass.getEnclosingInstancesSlotSize() + 1 + enumOffset, codeStream);
                argSlotSize += declaringClass.getEnclosingInstancesSlotSize();
                argSlotSize += declaringClass.getOuterLocalVariablesSlotSize();
            }
            else {
                this.scope.computeLocalVariablePositions(1 + enumOffset, codeStream);
            }
            if (this.arguments != null) {
                for (int i = 0, max = this.arguments.length; i < max; ++i) {
                    final LocalVariableBinding argBinding;
                    codeStream.addVisibleLocalVariable(argBinding = this.arguments[i].binding);
                    argBinding.recordInitializationStartPC(0);
                    switch (argBinding.type.id) {
                        case 7:
                        case 8: {
                            argSlotSize += 2;
                            break;
                        }
                        default: {
                            ++argSlotSize;
                            break;
                        }
                    }
                }
            }
            final MethodScope initializerScope = declaringType.initializerScope;
            initializerScope.computeLocalVariablePositions(argSlotSize, codeStream);
            final boolean needFieldInitializations = this.constructorCall == null || this.constructorCall.accessMode != 3;
            final boolean preInitSyntheticFields = this.scope.compilerOptions().targetJDK >= 3145728L;
            if (needFieldInitializations && preInitSyntheticFields) {
                this.generateSyntheticFieldInitializationsIfNecessary(this.scope, codeStream, declaringClass);
                codeStream.recordPositionsFrom(0, this.bodyStart);
            }
            if (this.constructorCall != null) {
                this.constructorCall.generateCode(this.scope, codeStream);
            }
            if (needFieldInitializations) {
                if (!preInitSyntheticFields) {
                    this.generateSyntheticFieldInitializationsIfNecessary(this.scope, codeStream, declaringClass);
                }
                if (declaringType.fields != null) {
                    for (int j = 0, max2 = declaringType.fields.length; j < max2; ++j) {
                        final FieldDeclaration fieldDecl;
                        if (!(fieldDecl = declaringType.fields[j]).isStatic()) {
                            fieldDecl.generateCode(initializerScope, codeStream);
                        }
                    }
                }
            }
            if (this.statements != null) {
                for (int j = 0, max2 = this.statements.length; j < max2; ++j) {
                    this.statements[j].generateCode(this.scope, codeStream);
                }
            }
            if (this.ignoreFurtherInvestigation) {
                throw new AbortMethod(this.scope.referenceCompilationUnit().compilationResult, (CategorizedProblem)null);
            }
            if ((this.bits & 0x40) != 0x0) {
                codeStream.return_();
            }
            codeStream.exitUserScope(this.scope);
            codeStream.recordPositionsFrom(0, this.bodyEnd);
            try {
                classFile.completeCodeAttribute(codeAttributeOffset);
            }
            catch (final NegativeArraySizeException ex) {
                throw new AbortMethod(this.scope.referenceCompilationUnit().compilationResult, (CategorizedProblem)null);
            }
            ++attributeNumber;
            if (codeStream instanceof StackMapFrameCodeStream && needFieldInitializations && declaringType.fields != null) {
                ((StackMapFrameCodeStream)codeStream).resetSecretLocals();
            }
        }
        classFile.completeMethodInfo(this.binding, methodAttributeOffset, attributeNumber);
    }
    
    @Override
    public void getAllAnnotationContexts(final int targetType, final List allAnnotationContexts) {
        final TypeReference fakeReturnType = new SingleTypeReference(this.selector, 0L);
        fakeReturnType.resolvedType = this.binding.declaringClass;
        final TypeReference.AnnotationCollector collector = new TypeReference.AnnotationCollector(fakeReturnType, targetType, allAnnotationContexts);
        for (int i = 0, max = this.annotations.length; i < max; ++i) {
            final Annotation annotation = this.annotations[i];
            annotation.traverse(collector, (BlockScope)null);
        }
    }
    
    @Override
    public boolean isConstructor() {
        return true;
    }
    
    @Override
    public boolean isDefaultConstructor() {
        return (this.bits & 0x80) != 0x0;
    }
    
    @Override
    public boolean isInitializationMethod() {
        return true;
    }
    
    public boolean isRecursive(ArrayList visited) {
        if (this.binding == null || this.constructorCall == null || this.constructorCall.binding == null || this.constructorCall.isSuperAccess() || !this.constructorCall.binding.isValidBinding()) {
            return false;
        }
        final ConstructorDeclaration targetConstructor = (ConstructorDeclaration)this.scope.referenceType().declarationOf(this.constructorCall.binding.original());
        if (targetConstructor == null) {
            return false;
        }
        if (this == targetConstructor) {
            return true;
        }
        if (visited == null) {
            visited = new ArrayList(1);
        }
        else {
            final int index = visited.indexOf(this);
            if (index >= 0) {
                return index == 0;
            }
        }
        visited.add(this);
        return targetConstructor.isRecursive(visited);
    }
    
    @Override
    public void parseStatements(final Parser parser, final CompilationUnitDeclaration unit) {
        if ((this.bits & 0x80) != 0x0 && this.constructorCall == null) {
            this.constructorCall = SuperReference.implicitSuperConstructorCall();
            this.constructorCall.sourceStart = this.sourceStart;
            this.constructorCall.sourceEnd = this.sourceEnd;
            return;
        }
        parser.parse(this, unit, false);
    }
    
    @Override
    public StringBuffer printBody(final int indent, final StringBuffer output) {
        output.append(" {");
        if (this.constructorCall != null) {
            output.append('\n');
            this.constructorCall.printStatement(indent, output);
        }
        if (this.statements != null) {
            for (int i = 0; i < this.statements.length; ++i) {
                output.append('\n');
                this.statements[i].printStatement(indent, output);
            }
        }
        output.append('\n');
        ASTNode.printIndent((indent == 0) ? 0 : (indent - 1), output).append('}');
        return output;
    }
    
    @Override
    public void resolveJavadoc() {
        if (this.binding == null || this.javadoc != null) {
            super.resolveJavadoc();
        }
        else if ((this.bits & 0x80) == 0x0 && this.binding.declaringClass != null && !this.binding.declaringClass.isLocalType()) {
            int javadocVisibility = this.binding.modifiers & 0x7;
            final ClassScope classScope = this.scope.classScope();
            final ProblemReporter reporter = this.scope.problemReporter();
            final int severity = reporter.computeSeverity(-1610612250);
            if (severity != 256) {
                if (classScope != null) {
                    javadocVisibility = Util.computeOuterMostVisibility(classScope.referenceType(), javadocVisibility);
                }
                final int javadocModifiers = (this.binding.modifiers & 0xFFFFFFF8) | javadocVisibility;
                reporter.javadocMissing(this.sourceStart, this.sourceEnd, severity, javadocModifiers);
            }
        }
    }
    
    @Override
    public void resolveStatements() {
        final SourceTypeBinding sourceType = this.scope.enclosingSourceType();
        if (!CharOperation.equals(sourceType.sourceName, this.selector)) {
            this.scope.problemReporter().missingReturnType(this);
        }
        if (this.binding != null && !this.binding.isPrivate()) {
            final SourceTypeBinding sourceTypeBinding = sourceType;
            sourceTypeBinding.tagBits |= 0x1000000000000000L;
        }
        if (this.constructorCall != null) {
            if (sourceType.id == 1 && this.constructorCall.accessMode != 3) {
                if (this.constructorCall.accessMode == 2) {
                    this.scope.problemReporter().cannotUseSuperInJavaLangObject(this.constructorCall);
                }
                this.constructorCall = null;
            }
            else {
                this.constructorCall.resolve(this.scope);
            }
        }
        if ((this.modifiers & 0x1000000) != 0x0) {
            this.scope.problemReporter().methodNeedBody(this);
        }
        super.resolveStatements();
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
            if (this.constructorCall != null) {
                this.constructorCall.traverse(visitor, this.scope);
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
