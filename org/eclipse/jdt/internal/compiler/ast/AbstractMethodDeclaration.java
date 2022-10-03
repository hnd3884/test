package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import java.util.List;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.problem.AbortType;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;

public abstract class AbstractMethodDeclaration extends ASTNode implements ProblemSeverities, ReferenceContext
{
    public MethodScope scope;
    public char[] selector;
    public int declarationSourceStart;
    public int declarationSourceEnd;
    public int modifiers;
    public int modifiersSourceStart;
    public Annotation[] annotations;
    public Receiver receiver;
    public Argument[] arguments;
    public TypeReference[] thrownExceptions;
    public Statement[] statements;
    public int explicitDeclarations;
    public MethodBinding binding;
    public boolean ignoreFurtherInvestigation;
    public Javadoc javadoc;
    public int bodyStart;
    public int bodyEnd;
    public CompilationResult compilationResult;
    
    AbstractMethodDeclaration(final CompilationResult compilationResult) {
        this.ignoreFurtherInvestigation = false;
        this.bodyEnd = -1;
        this.compilationResult = compilationResult;
    }
    
    @Override
    public void abort(final int abortLevel, final CategorizedProblem problem) {
        switch (abortLevel) {
            case 2: {
                throw new AbortCompilation(this.compilationResult, problem);
            }
            case 4: {
                throw new AbortCompilationUnit(this.compilationResult, problem);
            }
            case 8: {
                throw new AbortType(this.compilationResult, problem);
            }
            default: {
                throw new AbortMethod(this.compilationResult, problem);
            }
        }
    }
    
    public void createArgumentBindings() {
        createArgumentBindings(this.arguments, this.binding, this.scope);
    }
    
    static void createArgumentBindings(final Argument[] arguments, final MethodBinding binding, final MethodScope scope) {
        final boolean useTypeAnnotations = scope.environment().usesNullTypeAnnotations();
        if (arguments != null && binding != null) {
            for (int i = 0, length = arguments.length; i < length; ++i) {
                final Argument argument = arguments[i];
                binding.parameters[i] = argument.createBinding(scope, binding.parameters[i]);
                if (!useTypeAnnotations) {
                    final long argTypeTagBits = argument.binding.tagBits & 0x180000000000000L;
                    if (argTypeTagBits != 0L) {
                        if (binding.parameterNonNullness == null) {
                            binding.parameterNonNullness = new Boolean[arguments.length];
                            binding.tagBits |= 0x1000L;
                        }
                        binding.parameterNonNullness[i] = (argTypeTagBits == 72057594037927936L);
                    }
                }
            }
        }
    }
    
    public void bindArguments() {
        if (this.arguments != null) {
            if (this.binding == null) {
                for (int i = 0, length = this.arguments.length; i < length; ++i) {
                    this.arguments[i].bind(this.scope, null, true);
                }
                return;
            }
            final boolean used = this.binding.isAbstract() || this.binding.isNative();
            AnnotationBinding[][] paramAnnotations = null;
            for (int j = 0, length2 = this.arguments.length; j < length2; ++j) {
                final Argument argument = this.arguments[j];
                this.binding.parameters[j] = argument.bind(this.scope, this.binding.parameters[j], used);
                if (argument.annotations != null) {
                    if (paramAnnotations == null) {
                        paramAnnotations = new AnnotationBinding[length2][];
                        for (int k = 0; k < j; ++k) {
                            paramAnnotations[k] = Binding.NO_ANNOTATIONS;
                        }
                    }
                    paramAnnotations[j] = argument.binding.getAnnotations();
                }
                else if (paramAnnotations != null) {
                    paramAnnotations[j] = Binding.NO_ANNOTATIONS;
                }
            }
            if (paramAnnotations != null) {
                this.binding.setParameterAnnotations(paramAnnotations);
            }
        }
    }
    
    public void bindThrownExceptions() {
        if (this.thrownExceptions != null && this.binding != null && this.binding.thrownExceptions != null) {
            final int thrownExceptionLength = this.thrownExceptions.length;
            final int length = this.binding.thrownExceptions.length;
            if (length == thrownExceptionLength) {
                for (int i = 0; i < length; ++i) {
                    this.thrownExceptions[i].resolvedType = this.binding.thrownExceptions[i];
                }
            }
            else {
                for (int bindingIndex = 0, j = 0; j < thrownExceptionLength && bindingIndex < length; ++j) {
                    final TypeReference thrownException = this.thrownExceptions[j];
                    final ReferenceBinding thrownExceptionBinding = this.binding.thrownExceptions[bindingIndex];
                    final char[][] bindingCompoundName = thrownExceptionBinding.compoundName;
                    if (bindingCompoundName != null) {
                        if (thrownException instanceof SingleTypeReference) {
                            final int lengthName = bindingCompoundName.length;
                            final char[] thrownExceptionTypeName = thrownException.getTypeName()[0];
                            if (CharOperation.equals(thrownExceptionTypeName, bindingCompoundName[lengthName - 1])) {
                                thrownException.resolvedType = thrownExceptionBinding;
                                ++bindingIndex;
                            }
                        }
                        else if (CharOperation.equals(thrownException.getTypeName(), bindingCompoundName)) {
                            thrownException.resolvedType = thrownExceptionBinding;
                            ++bindingIndex;
                        }
                    }
                }
            }
        }
    }
    
    static void analyseArguments(final LookupEnvironment environment, final FlowInfo flowInfo, final Argument[] methodArguments, final MethodBinding methodBinding) {
        if (methodArguments != null) {
            final boolean usesNullTypeAnnotations = environment.usesNullTypeAnnotations();
            for (int length = Math.min(methodBinding.parameters.length, methodArguments.length), i = 0; i < length; ++i) {
                if (usesNullTypeAnnotations) {
                    final long tagBits = methodBinding.parameters[i].tagBits & 0x180000000000000L;
                    if (tagBits == 72057594037927936L) {
                        flowInfo.markAsDefinitelyNonNull(methodArguments[i].binding);
                    }
                    else if (tagBits == 36028797018963968L) {
                        flowInfo.markPotentiallyNullBit(methodArguments[i].binding);
                    }
                    else if (methodBinding.parameters[i].isFreeTypeVariable()) {
                        flowInfo.markNullStatus(methodArguments[i].binding, 48);
                    }
                }
                else if (methodBinding.parameterNonNullness != null) {
                    final Boolean nonNullNess = methodBinding.parameterNonNullness[i];
                    if (nonNullNess != null) {
                        if (nonNullNess) {
                            flowInfo.markAsDefinitelyNonNull(methodArguments[i].binding);
                        }
                        else {
                            flowInfo.markPotentiallyNullBit(methodArguments[i].binding);
                        }
                    }
                }
                flowInfo.markAsDefinitelyAssigned(methodArguments[i].binding);
            }
        }
    }
    
    @Override
    public CompilationResult compilationResult() {
        return this.compilationResult;
    }
    
    public void generateCode(final ClassScope classScope, final ClassFile classFile) {
        classFile.codeStream.wideMode = false;
        if (!this.ignoreFurtherInvestigation) {
            int problemResetPC = 0;
            CompilationResult unitResult = null;
            int problemCount = 0;
            if (classScope != null) {
                final TypeDeclaration referenceContext = classScope.referenceContext;
                if (referenceContext != null) {
                    unitResult = referenceContext.compilationResult();
                    problemCount = unitResult.problemCount;
                }
            }
            boolean restart = false;
            boolean abort = false;
            do {
                try {
                    problemResetPC = classFile.contentsOffset;
                    this.generateCode(classFile);
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
                classFile.addProblemMethod(this, this.binding, problemsCopy, problemResetPC);
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
        classFile.addProblemMethod(this, this.binding, problemsCopy2);
    }
    
    public void generateCode(final ClassFile classFile) {
        classFile.generateMethodInfoHeader(this.binding);
        final int methodAttributeOffset = classFile.contentsOffset;
        int attributeNumber = classFile.generateMethodInfoAttributes(this.binding);
        if (!this.binding.isNative() && !this.binding.isAbstract()) {
            final int codeAttributeOffset = classFile.contentsOffset;
            classFile.generateCodeAttributeHeader();
            final CodeStream codeStream = classFile.codeStream;
            codeStream.reset(this, classFile);
            this.scope.computeLocalVariablePositions(this.binding.isStatic() ? 0 : 1, codeStream);
            if (this.arguments != null) {
                for (int i = 0, max = this.arguments.length; i < max; ++i) {
                    final LocalVariableBinding argBinding;
                    codeStream.addVisibleLocalVariable(argBinding = this.arguments[i].binding);
                    argBinding.recordInitializationStartPC(0);
                }
            }
            if (this.statements != null) {
                for (int i = 0, max = this.statements.length; i < max; ++i) {
                    this.statements[i].generateCode(this.scope, codeStream);
                }
            }
            if (this.ignoreFurtherInvestigation) {
                throw new AbortMethod(this.scope.referenceCompilationUnit().compilationResult, (CategorizedProblem)null);
            }
            if ((this.bits & 0x40) != 0x0) {
                codeStream.return_();
            }
            codeStream.exitUserScope(this.scope);
            codeStream.recordPositionsFrom(0, this.declarationSourceEnd);
            try {
                classFile.completeCodeAttribute(codeAttributeOffset);
            }
            catch (final NegativeArraySizeException ex) {
                throw new AbortMethod(this.scope.referenceCompilationUnit().compilationResult, (CategorizedProblem)null);
            }
            ++attributeNumber;
        }
        else {
            this.checkArgumentsSize();
        }
        classFile.completeMethodInfo(this.binding, methodAttributeOffset, attributeNumber);
    }
    
    public void getAllAnnotationContexts(final int targetType, final List allAnnotationContexts) {
    }
    
    private void checkArgumentsSize() {
        final TypeBinding[] parameters = this.binding.parameters;
        int size = 1;
        for (int i = 0, max = parameters.length; i < max; ++i) {
            switch (parameters[i].id) {
                case 7:
                case 8: {
                    size += 2;
                    break;
                }
                default: {
                    ++size;
                    break;
                }
            }
            if (size > 255) {
                this.scope.problemReporter().noMoreAvailableSpaceForArgument(this.scope.locals[i], this.scope.locals[i].declaration);
            }
        }
    }
    
    @Override
    public CompilationUnitDeclaration getCompilationUnitDeclaration() {
        if (this.scope != null) {
            return this.scope.compilationUnitScope().referenceContext;
        }
        return null;
    }
    
    @Override
    public boolean hasErrors() {
        return this.ignoreFurtherInvestigation;
    }
    
    public boolean isAbstract() {
        if (this.binding != null) {
            return this.binding.isAbstract();
        }
        return (this.modifiers & 0x400) != 0x0;
    }
    
    public boolean isAnnotationMethod() {
        return false;
    }
    
    public boolean isClinit() {
        return false;
    }
    
    public boolean isConstructor() {
        return false;
    }
    
    public boolean isDefaultConstructor() {
        return false;
    }
    
    public boolean isDefaultMethod() {
        return false;
    }
    
    public boolean isInitializationMethod() {
        return false;
    }
    
    public boolean isMethod() {
        return false;
    }
    
    public boolean isNative() {
        if (this.binding != null) {
            return this.binding.isNative();
        }
        return (this.modifiers & 0x100) != 0x0;
    }
    
    public boolean isStatic() {
        if (this.binding != null) {
            return this.binding.isStatic();
        }
        return (this.modifiers & 0x8) != 0x0;
    }
    
    public abstract void parseStatements(final Parser p0, final CompilationUnitDeclaration p1);
    
    @Override
    public StringBuffer print(final int tab, final StringBuffer output) {
        if (this.javadoc != null) {
            this.javadoc.print(tab, output);
        }
        ASTNode.printIndent(tab, output);
        ASTNode.printModifiers(this.modifiers, output);
        if (this.annotations != null) {
            ASTNode.printAnnotations(this.annotations, output);
            output.append(' ');
        }
        final TypeParameter[] typeParams = this.typeParameters();
        if (typeParams != null) {
            output.append('<');
            final int max = typeParams.length - 1;
            for (int j = 0; j < max; ++j) {
                typeParams[j].print(0, output);
                output.append(", ");
            }
            typeParams[max].print(0, output);
            output.append('>');
        }
        this.printReturnType(0, output).append(this.selector).append('(');
        if (this.receiver != null) {
            this.receiver.print(0, output);
        }
        if (this.arguments != null) {
            for (int i = 0; i < this.arguments.length; ++i) {
                if (i > 0 || this.receiver != null) {
                    output.append(", ");
                }
                this.arguments[i].print(0, output);
            }
        }
        output.append(')');
        if (this.thrownExceptions != null) {
            output.append(" throws ");
            for (int i = 0; i < this.thrownExceptions.length; ++i) {
                if (i > 0) {
                    output.append(", ");
                }
                this.thrownExceptions[i].print(0, output);
            }
        }
        this.printBody(tab + 1, output);
        return output;
    }
    
    public StringBuffer printBody(final int indent, final StringBuffer output) {
        if (this.isAbstract() || (this.modifiers & 0x1000000) != 0x0) {
            return output.append(';');
        }
        output.append(" {");
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
    
    public StringBuffer printReturnType(final int indent, final StringBuffer output) {
        return output;
    }
    
    public void resolve(final ClassScope upperScope) {
        if (this.binding == null) {
            this.ignoreFurtherInvestigation = true;
        }
        try {
            this.bindArguments();
            this.resolveReceiver();
            this.bindThrownExceptions();
            this.resolveJavadoc();
            ASTNode.resolveAnnotations(this.scope, this.annotations, this.binding, this.isConstructor());
            final long sourceLevel = this.scope.compilerOptions().sourceLevel;
            if (sourceLevel < 3407872L) {
                this.validateNullAnnotations(this.scope.environment().usesNullTypeAnnotations());
            }
            this.resolveStatements();
            if (this.binding != null && (this.binding.getAnnotationTagBits() & 0x400000000000L) == 0x0L && (this.binding.modifiers & 0x100000) != 0x0 && sourceLevel >= 3211264L) {
                this.scope.problemReporter().missingDeprecatedAnnotationForMethod(this);
            }
        }
        catch (final AbortMethod abortMethod) {
            this.ignoreFurtherInvestigation = true;
        }
    }
    
    public void resolveReceiver() {
        if (this.receiver == null) {
            return;
        }
        if (this.receiver.modifiers != 0) {
            this.scope.problemReporter().illegalModifiers(this.receiver.declarationSourceStart, this.receiver.declarationSourceEnd);
        }
        final TypeBinding resolvedReceiverType = this.receiver.type.resolvedType;
        if (this.binding == null || resolvedReceiverType == null || !resolvedReceiverType.isValidBinding()) {
            return;
        }
        final ReferenceBinding declaringClass = this.binding.declaringClass;
        if (this.isStatic() || declaringClass.isAnonymousType()) {
            this.scope.problemReporter().disallowedThisParameter(this.receiver);
            return;
        }
        ReferenceBinding enclosingReceiver = this.scope.enclosingReceiverType();
        if (this.isConstructor()) {
            if (declaringClass.isStatic() || (declaringClass.tagBits & 0x18L) == 0x0L) {
                this.scope.problemReporter().disallowedThisParameter(this.receiver);
                return;
            }
            enclosingReceiver = enclosingReceiver.enclosingType();
        }
        final char[][] tokens = (char[][])((this.receiver.qualifyingName == null) ? null : this.receiver.qualifyingName.getName());
        if (this.isConstructor()) {
            if (tokens == null || tokens.length > 1 || !CharOperation.equals(enclosingReceiver.sourceName(), tokens[0])) {
                this.scope.problemReporter().illegalQualifierForExplicitThis(this.receiver, enclosingReceiver);
                this.receiver.qualifyingName = null;
            }
        }
        else if (tokens != null && tokens.length > 0) {
            this.scope.problemReporter().illegalQualifierForExplicitThis2(this.receiver);
            this.receiver.qualifyingName = null;
        }
        if (TypeBinding.notEquals(enclosingReceiver, resolvedReceiverType)) {
            this.scope.problemReporter().illegalTypeForExplicitThis(this.receiver, enclosingReceiver);
        }
        if (this.receiver.type.hasNullTypeAnnotation(TypeReference.AnnotationPosition.ANY)) {
            this.scope.problemReporter().nullAnnotationUnsupportedLocation(this.receiver.type);
        }
    }
    
    public void resolveJavadoc() {
        if (this.binding == null) {
            return;
        }
        if (this.javadoc != null) {
            this.javadoc.resolve(this.scope);
            return;
        }
        if (this.binding.declaringClass != null && !this.binding.declaringClass.isLocalType()) {
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
    
    public void resolveStatements() {
        if (this.statements != null) {
            for (int i = 0, length = this.statements.length; i < length; ++i) {
                this.statements[i].resolve(this.scope);
            }
        }
        else if ((this.bits & 0x8) != 0x0 && (!this.isConstructor() || this.arguments != null)) {
            this.scope.problemReporter().undocumentedEmptyBlock(this.bodyStart - 1, this.bodyEnd + 1);
        }
    }
    
    @Override
    public void tagAsHavingErrors() {
        this.ignoreFurtherInvestigation = true;
    }
    
    @Override
    public void tagAsHavingIgnoredMandatoryErrors(final int problemId) {
    }
    
    public void traverse(final ASTVisitor visitor, final ClassScope classScope) {
    }
    
    public TypeParameter[] typeParameters() {
        return null;
    }
    
    void validateNullAnnotations(final boolean useTypeAnnotations) {
        if (this.binding == null) {
            return;
        }
        if (!useTypeAnnotations) {
            if (this.binding.parameterNonNullness != null) {
                for (int length = this.binding.parameters.length, i = 0; i < length; ++i) {
                    if (this.binding.parameterNonNullness[i] != null) {
                        final long nullAnnotationTagBit = this.binding.parameterNonNullness[i] ? 72057594037927936L : 36028797018963968L;
                        if (!this.scope.validateNullAnnotation(nullAnnotationTagBit, this.arguments[i].type, this.arguments[i].annotations)) {
                            this.binding.parameterNonNullness[i] = null;
                        }
                    }
                }
            }
        }
        else {
            for (int length = this.binding.parameters.length, i = 0; i < length; ++i) {
                this.scope.validateNullAnnotation(this.binding.parameters[i].tagBits, this.arguments[i].type, this.arguments[i].annotations);
            }
        }
    }
}
