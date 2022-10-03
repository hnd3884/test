package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Iterator;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import java.util.List;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;

public class BlockScope extends Scope
{
    public LocalVariableBinding[] locals;
    public int localIndex;
    public int startIndex;
    public int offset;
    public int maxOffset;
    public BlockScope[] shiftScopes;
    public Scope[] subscopes;
    public int subscopeCount;
    public CaseStatement enclosingCase;
    public static final VariableBinding[] EmulationPathToImplicitThis;
    public static final VariableBinding[] NoEnclosingInstanceInConstructorCall;
    public static final VariableBinding[] NoEnclosingInstanceInStaticContext;
    public boolean insideTypeAnnotation;
    private List trackingVariables;
    public FlowInfo finallyInfo;
    
    static {
        EmulationPathToImplicitThis = new VariableBinding[0];
        NoEnclosingInstanceInConstructorCall = new VariableBinding[0];
        NoEnclosingInstanceInStaticContext = new VariableBinding[0];
    }
    
    public BlockScope(final BlockScope parent) {
        this(parent, true);
    }
    
    public BlockScope(final BlockScope parent, final boolean addToParentScope) {
        this(1, parent);
        this.locals = new LocalVariableBinding[5];
        if (addToParentScope) {
            parent.addSubscope(this);
        }
        this.startIndex = parent.localIndex;
    }
    
    public BlockScope(final BlockScope parent, final int variableCount) {
        this(1, parent);
        this.locals = new LocalVariableBinding[variableCount];
        parent.addSubscope(this);
        this.startIndex = parent.localIndex;
    }
    
    protected BlockScope(final int kind, final Scope parent) {
        super(kind, parent);
        this.subscopes = new Scope[1];
        this.subscopeCount = 0;
        this.insideTypeAnnotation = false;
    }
    
    public final void addAnonymousType(final TypeDeclaration anonymousType, final ReferenceBinding superBinding) {
        final ClassScope anonymousClassScope = new ClassScope(this, anonymousType);
        anonymousClassScope.buildAnonymousTypeBinding(this.enclosingSourceType(), superBinding);
        for (MethodScope methodScope = this.methodScope(); methodScope != null && methodScope.referenceContext instanceof LambdaExpression; methodScope = methodScope.enclosingMethodScope()) {
            final LambdaExpression lambda = (LambdaExpression)methodScope.referenceContext;
            if (!lambda.scope.isStatic && !lambda.scope.isConstructorCall) {
                lambda.shouldCaptureInstance = true;
            }
        }
    }
    
    public final void addLocalType(final TypeDeclaration localType) {
        final ClassScope localTypeScope = new ClassScope(this, localType);
        this.addSubscope(localTypeScope);
        localTypeScope.buildLocalTypeBinding(this.enclosingSourceType());
        for (MethodScope methodScope = this.methodScope(); methodScope != null && methodScope.referenceContext instanceof LambdaExpression; methodScope = methodScope.enclosingMethodScope()) {
            final LambdaExpression lambda = (LambdaExpression)methodScope.referenceContext;
            if (!lambda.scope.isStatic && !lambda.scope.isConstructorCall) {
                lambda.shouldCaptureInstance = true;
            }
        }
    }
    
    public final void addLocalVariable(final LocalVariableBinding binding) {
        this.checkAndSetModifiersForVariable(binding);
        if (this.localIndex == this.locals.length) {
            System.arraycopy(this.locals, 0, this.locals = new LocalVariableBinding[this.localIndex * 2], 0, this.localIndex);
        }
        this.locals[this.localIndex++] = binding;
        binding.declaringScope = this;
        binding.id = this.outerMostMethodScope().analysisIndex++;
    }
    
    public void addSubscope(final Scope childScope) {
        if (this.subscopeCount == this.subscopes.length) {
            System.arraycopy(this.subscopes, 0, this.subscopes = new Scope[this.subscopeCount * 2], 0, this.subscopeCount);
        }
        this.subscopes[this.subscopeCount++] = childScope;
    }
    
    public final boolean allowBlankFinalFieldAssignment(final FieldBinding binding) {
        if (TypeBinding.notEquals(this.enclosingReceiverType(), binding.declaringClass)) {
            return false;
        }
        final MethodScope methodScope = this.methodScope();
        return methodScope.isStatic == binding.isStatic() && !methodScope.isLambdaScope() && (methodScope.isInsideInitializer() || ((AbstractMethodDeclaration)methodScope.referenceContext).isInitializationMethod());
    }
    
    String basicToString(final int tab) {
        String newLine = "\n";
        int i = tab;
        while (--i >= 0) {
            newLine = String.valueOf(newLine) + "\t";
        }
        String s = String.valueOf(newLine) + "--- Block Scope ---";
        newLine = String.valueOf(newLine) + "\t";
        s = String.valueOf(s) + newLine + "locals:";
        for (int j = 0; j < this.localIndex; ++j) {
            s = String.valueOf(s) + newLine + "\t" + this.locals[j].toString();
        }
        s = String.valueOf(s) + newLine + "startIndex = " + this.startIndex;
        return s;
    }
    
    private void checkAndSetModifiersForVariable(final LocalVariableBinding varBinding) {
        final int modifiers = varBinding.modifiers;
        if ((modifiers & 0x400000) != 0x0 && varBinding.declaration != null) {
            this.problemReporter().duplicateModifierForVariable(varBinding.declaration, this instanceof MethodScope);
        }
        final int realModifiers = modifiers & 0xFFFF;
        final int unexpectedModifiers = -17;
        if ((realModifiers & unexpectedModifiers) != 0x0 && varBinding.declaration != null) {
            this.problemReporter().illegalModifierForVariable(varBinding.declaration, this instanceof MethodScope);
        }
        varBinding.modifiers = modifiers;
    }
    
    void computeLocalVariablePositions(int ilocal, final int initOffset, final CodeStream codeStream) {
        this.offset = initOffset;
        this.maxOffset = initOffset;
        final int maxLocals = this.localIndex;
        boolean hasMoreVariables = ilocal < maxLocals;
        int iscope = 0;
        final int maxScopes = this.subscopeCount;
        boolean hasMoreScopes = maxScopes > 0;
        while (hasMoreVariables || hasMoreScopes) {
            if (hasMoreScopes && (!hasMoreVariables || this.subscopes[iscope].startIndex() <= ilocal)) {
                if (this.subscopes[iscope] instanceof BlockScope) {
                    final BlockScope subscope = (BlockScope)this.subscopes[iscope];
                    final int subOffset = (subscope.shiftScopes == null) ? this.offset : subscope.maxShiftedOffset();
                    subscope.computeLocalVariablePositions(0, subOffset, codeStream);
                    if (subscope.maxOffset > this.maxOffset) {
                        this.maxOffset = subscope.maxOffset;
                    }
                }
                hasMoreScopes = (++iscope < maxScopes);
            }
            else {
                final LocalVariableBinding local = this.locals[ilocal];
                boolean generateCurrentLocalVar = local.useFlag > 0 && local.constant() == Constant.NotAConstant;
                if (local.useFlag == 0 && local.declaration != null && (local.declaration.bits & 0x40000000) != 0x0) {
                    if (local.isCatchParameter()) {
                        this.problemReporter().unusedExceptionParameter(local.declaration);
                    }
                    else {
                        this.problemReporter().unusedLocalVariable(local.declaration);
                    }
                }
                if (!generateCurrentLocalVar && local.declaration != null && this.compilerOptions().preserveAllLocalVariables) {
                    generateCurrentLocalVar = true;
                    if (local.useFlag == 0) {
                        local.useFlag = 1;
                    }
                }
                if (generateCurrentLocalVar) {
                    if (local.declaration != null) {
                        codeStream.record(local);
                    }
                    local.resolvedPosition = this.offset;
                    if (TypeBinding.equalsEquals(local.type, TypeBinding.LONG) || TypeBinding.equalsEquals(local.type, TypeBinding.DOUBLE)) {
                        this.offset += 2;
                    }
                    else {
                        ++this.offset;
                    }
                    if (this.offset > 65535) {
                        this.problemReporter().noMoreAvailableSpaceForLocal(local, (local.declaration == null) ? ((ASTNode)this.methodScope().referenceContext) : local.declaration);
                    }
                }
                else {
                    local.resolvedPosition = -1;
                }
                hasMoreVariables = (++ilocal < maxLocals);
            }
        }
        if (this.offset > this.maxOffset) {
            this.maxOffset = this.offset;
        }
    }
    
    public void emulateOuterAccess(final LocalVariableBinding outerLocalVariable) {
        final BlockScope outerVariableScope = outerLocalVariable.declaringScope;
        if (outerVariableScope == null) {
            return;
        }
        int depth = 0;
        for (Scope scope = this; outerVariableScope != scope; scope = scope.parent) {
            switch (scope.kind) {
                case 3: {
                    ++depth;
                    break;
                }
                case 2: {
                    if (scope.isLambdaScope()) {
                        final LambdaExpression lambdaExpression = (LambdaExpression)scope.referenceContext();
                        lambdaExpression.addSyntheticArgument(outerLocalVariable);
                        break;
                    }
                    break;
                }
            }
        }
        if (depth == 0) {
            return;
        }
        final MethodScope currentMethodScope = this.methodScope();
        if (outerVariableScope.methodScope() != currentMethodScope) {
            final NestedTypeBinding currentType = (NestedTypeBinding)this.enclosingSourceType();
            if (!currentType.isLocalType()) {
                return;
            }
            if (!currentMethodScope.isInsideInitializerOrConstructor()) {
                currentType.addSyntheticArgumentAndField(outerLocalVariable);
            }
            else {
                currentType.addSyntheticArgument(outerLocalVariable);
            }
        }
    }
    
    public final ReferenceBinding findLocalType(final char[] name) {
        final long compliance = this.compilerOptions().complianceLevel;
        for (int i = this.subscopeCount - 1; i >= 0; --i) {
            if (this.subscopes[i] instanceof ClassScope) {
                final LocalTypeBinding sourceType = (LocalTypeBinding)((ClassScope)this.subscopes[i]).referenceContext.binding;
                if (compliance < 3145728L || sourceType.enclosingCase == null || this.isInsideCase(sourceType.enclosingCase)) {
                    if (CharOperation.equals(sourceType.sourceName(), name)) {
                        return sourceType;
                    }
                }
            }
        }
        return null;
    }
    
    public LocalDeclaration[] findLocalVariableDeclarations(final int position) {
        int ilocal = 0;
        final int maxLocals = this.localIndex;
        boolean hasMoreVariables = maxLocals > 0;
        LocalDeclaration[] localDeclarations = null;
        int declPtr = 0;
        int iscope = 0;
        final int maxScopes = this.subscopeCount;
        boolean hasMoreScopes = maxScopes > 0;
        while (hasMoreVariables || hasMoreScopes) {
            if (hasMoreScopes && (!hasMoreVariables || this.subscopes[iscope].startIndex() <= ilocal)) {
                final Scope subscope = this.subscopes[iscope];
                if (subscope.kind == 1) {
                    localDeclarations = ((BlockScope)subscope).findLocalVariableDeclarations(position);
                    if (localDeclarations != null) {
                        return localDeclarations;
                    }
                }
                hasMoreScopes = (++iscope < maxScopes);
            }
            else {
                final LocalVariableBinding local = this.locals[ilocal];
                if (local != null) {
                    final LocalDeclaration localDecl = local.declaration;
                    if (localDecl != null) {
                        if (localDecl.declarationSourceStart > position) {
                            return localDeclarations;
                        }
                        if (position <= localDecl.declarationSourceEnd) {
                            if (localDeclarations == null) {
                                localDeclarations = new LocalDeclaration[maxLocals];
                            }
                            localDeclarations[declPtr++] = localDecl;
                        }
                    }
                }
                hasMoreVariables = (++ilocal < maxLocals);
                if (!hasMoreVariables && localDeclarations != null) {
                    return localDeclarations;
                }
                continue;
            }
        }
        return null;
    }
    
    @Override
    public LocalVariableBinding findVariable(final char[] variableName) {
        final int varLength = variableName.length;
        for (int i = this.localIndex - 1; i >= 0; --i) {
            final LocalVariableBinding local;
            final char[] localName;
            if ((localName = (local = this.locals[i]).name).length == varLength && CharOperation.equals(localName, variableName)) {
                return local;
            }
        }
        return null;
    }
    
    public Binding getBinding(final char[][] compoundName, final int mask, final InvocationSite invocationSite, final boolean needResolve) {
        Binding binding = this.getBinding(compoundName[0], mask | 0x4 | 0x10, invocationSite, needResolve);
        invocationSite.setFieldIndex(1);
        if (binding instanceof VariableBinding) {
            return binding;
        }
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(compoundName);
        if (!binding.isValidBinding()) {
            return binding;
        }
        final int length = compoundName.length;
        int currentIndex = 1;
        Label_0276: {
            if (binding instanceof PackageBinding) {
                PackageBinding packageBinding = (PackageBinding)binding;
                while (currentIndex < length) {
                    unitScope.recordReference(packageBinding.compoundName, compoundName[currentIndex]);
                    binding = packageBinding.getTypeOrPackage(compoundName[currentIndex++]);
                    invocationSite.setFieldIndex(currentIndex);
                    if (binding == null) {
                        if (currentIndex == length) {
                            return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), null, 1);
                        }
                        return new ProblemBinding(CharOperation.subarray(compoundName, 0, currentIndex), 1);
                    }
                    else if (binding instanceof ReferenceBinding) {
                        if (!binding.isValidBinding()) {
                            return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (ReferenceBinding)((ReferenceBinding)binding).closestMatch(), binding.problemId());
                        }
                        if (!((ReferenceBinding)binding).canBeSeenBy(this)) {
                            return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (ReferenceBinding)binding, 2);
                        }
                        break Label_0276;
                    }
                    else {
                        packageBinding = (PackageBinding)binding;
                    }
                }
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), null, 1);
            }
        }
        ReferenceBinding referenceBinding = (ReferenceBinding)binding;
        binding = this.environment().convertToRawType(referenceBinding, false);
        if (invocationSite instanceof ASTNode) {
            final ASTNode invocationNode = (ASTNode)invocationSite;
            if (invocationNode.isTypeUseDeprecated(referenceBinding, this)) {
                this.problemReporter().deprecatedType(referenceBinding, invocationNode);
            }
        }
        Binding problemFieldBinding = null;
        while (currentIndex < length) {
            referenceBinding = (ReferenceBinding)binding;
            final char[] nextName = compoundName[currentIndex++];
            invocationSite.setFieldIndex(currentIndex);
            invocationSite.setActualReceiverType(referenceBinding);
            if ((mask & 0x1) != 0x0 && (binding = this.findField(referenceBinding, nextName, invocationSite, true)) != null) {
                if (binding.isValidBinding()) {
                    break;
                }
                problemFieldBinding = new ProblemFieldBinding(((ProblemFieldBinding)binding).closestMatch, ((ProblemFieldBinding)binding).declaringClass, CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), binding.problemId());
                if (binding.problemId() != 2) {
                    return problemFieldBinding;
                }
            }
            if ((binding = this.findMemberType(nextName, referenceBinding)) == null) {
                if (problemFieldBinding != null) {
                    return problemFieldBinding;
                }
                if ((mask & 0x1) != 0x0) {
                    return new ProblemFieldBinding(null, referenceBinding, nextName, 1);
                }
                if ((mask & 0x3) != 0x0) {
                    return new ProblemBinding(CharOperation.subarray(compoundName, 0, currentIndex), referenceBinding, 1);
                }
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), referenceBinding, 1);
            }
            else if (!binding.isValidBinding()) {
                if (problemFieldBinding != null) {
                    return problemFieldBinding;
                }
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (ReferenceBinding)((ReferenceBinding)binding).closestMatch(), binding.problemId());
            }
            else {
                if (!(invocationSite instanceof ASTNode)) {
                    continue;
                }
                referenceBinding = (ReferenceBinding)binding;
                final ASTNode invocationNode2 = (ASTNode)invocationSite;
                if (!invocationNode2.isTypeUseDeprecated(referenceBinding, this)) {
                    continue;
                }
                this.problemReporter().deprecatedType(referenceBinding, invocationNode2);
            }
        }
        if ((mask & 0x1) != 0x0 && binding instanceof FieldBinding) {
            final FieldBinding field = (FieldBinding)binding;
            if (!field.isStatic()) {
                return new ProblemFieldBinding(field, field.declaringClass, CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), 7);
            }
            return binding;
        }
        else {
            if ((mask & 0x4) != 0x0 && binding instanceof ReferenceBinding) {
                return binding;
            }
            return new ProblemBinding(CharOperation.subarray(compoundName, 0, currentIndex), 1);
        }
    }
    
    public final Binding getBinding(final char[][] compoundName, final InvocationSite invocationSite) {
        int currentIndex = 0;
        final int length = compoundName.length;
        Binding binding = this.getBinding(compoundName[currentIndex++], 23, invocationSite, true);
        if (!binding.isValidBinding()) {
            return binding;
        }
        Label_0193: {
            if (binding instanceof PackageBinding) {
                while (currentIndex < length) {
                    final PackageBinding packageBinding = (PackageBinding)binding;
                    binding = packageBinding.getTypeOrPackage(compoundName[currentIndex++]);
                    if (binding == null) {
                        if (currentIndex == length) {
                            return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), null, 1);
                        }
                        return new ProblemBinding(CharOperation.subarray(compoundName, 0, currentIndex), 1);
                    }
                    else {
                        if (!(binding instanceof ReferenceBinding)) {
                            continue;
                        }
                        if (!binding.isValidBinding()) {
                            return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (ReferenceBinding)((ReferenceBinding)binding).closestMatch(), binding.problemId());
                        }
                        if (!((ReferenceBinding)binding).canBeSeenBy(this)) {
                            return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (ReferenceBinding)binding, 2);
                        }
                        break Label_0193;
                    }
                }
                return binding;
            }
        }
        Label_0423: {
            if (binding instanceof ReferenceBinding) {
                while (currentIndex < length) {
                    final ReferenceBinding typeBinding = (ReferenceBinding)binding;
                    final char[] nextName = compoundName[currentIndex++];
                    final TypeBinding receiverType = typeBinding.capture(this, invocationSite.sourceStart(), invocationSite.sourceEnd());
                    if ((binding = this.findField(receiverType, nextName, invocationSite, true)) != null) {
                        if (!binding.isValidBinding()) {
                            return new ProblemFieldBinding((FieldBinding)binding, ((FieldBinding)binding).declaringClass, CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), binding.problemId());
                        }
                        if (!((FieldBinding)binding).isStatic()) {
                            return new ProblemFieldBinding((FieldBinding)binding, ((FieldBinding)binding).declaringClass, CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), 7);
                        }
                        break Label_0423;
                    }
                    else {
                        if ((binding = this.findMemberType(nextName, typeBinding)) == null) {
                            return new ProblemBinding(CharOperation.subarray(compoundName, 0, currentIndex), typeBinding, 1);
                        }
                        if (!binding.isValidBinding()) {
                            return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (ReferenceBinding)((ReferenceBinding)binding).closestMatch(), binding.problemId());
                        }
                        continue;
                    }
                }
                return binding;
            }
        }
        VariableBinding variableBinding = (VariableBinding)binding;
        while (currentIndex < length) {
            final TypeBinding typeBinding2 = variableBinding.type;
            if (typeBinding2 == null) {
                return new ProblemFieldBinding(null, null, CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), 1);
            }
            final TypeBinding receiverType = typeBinding2.capture(this, invocationSite.sourceStart(), invocationSite.sourceEnd());
            variableBinding = this.findField(receiverType, compoundName[currentIndex++], invocationSite, true);
            if (variableBinding == null) {
                return new ProblemFieldBinding(null, (receiverType instanceof ReferenceBinding) ? ((ReferenceBinding)receiverType) : null, CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), 1);
            }
            if (!variableBinding.isValidBinding()) {
                return variableBinding;
            }
        }
        return variableBinding;
    }
    
    public VariableBinding[] getEmulationPath(final LocalVariableBinding outerLocalVariable) {
        final MethodScope currentMethodScope = this.methodScope();
        final SourceTypeBinding sourceType = currentMethodScope.enclosingSourceType();
        final BlockScope variableScope = outerLocalVariable.declaringScope;
        if (variableScope == null || currentMethodScope == variableScope.methodScope()) {
            return new VariableBinding[] { outerLocalVariable };
        }
        if (currentMethodScope.isLambdaScope()) {
            final LambdaExpression lambda = (LambdaExpression)currentMethodScope.referenceContext;
            final SyntheticArgumentBinding syntheticArgument;
            if ((syntheticArgument = lambda.getSyntheticArgument(outerLocalVariable)) != null) {
                return new VariableBinding[] { syntheticArgument };
            }
        }
        final SyntheticArgumentBinding syntheticArg;
        if (currentMethodScope.isInsideInitializerOrConstructor() && sourceType.isNestedType() && (syntheticArg = ((NestedTypeBinding)sourceType).getSyntheticArgument(outerLocalVariable)) != null) {
            return new VariableBinding[] { syntheticArg };
        }
        final FieldBinding syntheticField;
        if (!currentMethodScope.isStatic && (syntheticField = sourceType.getSyntheticField(outerLocalVariable)) != null) {
            return new VariableBinding[] { syntheticField };
        }
        return null;
    }
    
    public Object[] getEmulationPath(final ReferenceBinding targetEnclosingType, final boolean onlyExactMatch, final boolean denyEnclosingArgInConstructorCall) {
        MethodScope currentMethodScope = this.methodScope();
        final SourceTypeBinding sourceType = currentMethodScope.enclosingSourceType();
        if (!currentMethodScope.isStatic && !currentMethodScope.isConstructorCall && (TypeBinding.equalsEquals(sourceType, targetEnclosingType) || (!onlyExactMatch && sourceType.findSuperTypeOriginatingFrom(targetEnclosingType) != null))) {
            return BlockScope.EmulationPathToImplicitThis;
        }
        if (!sourceType.isNestedType() || sourceType.isStatic()) {
            if (currentMethodScope.isConstructorCall) {
                return BlockScope.NoEnclosingInstanceInConstructorCall;
            }
            if (currentMethodScope.isStatic) {
                return BlockScope.NoEnclosingInstanceInStaticContext;
            }
            return null;
        }
        else {
            final boolean insideConstructor = currentMethodScope.isInsideInitializerOrConstructor();
            final SyntheticArgumentBinding syntheticArg;
            if (insideConstructor && (syntheticArg = ((NestedTypeBinding)sourceType).getSyntheticArgument(targetEnclosingType, onlyExactMatch, currentMethodScope.isConstructorCall)) != null) {
                final boolean isAnonymousAndHasEnclosing = sourceType.isAnonymousType() && sourceType.scope.referenceContext.allocation.enclosingInstance != null;
                if (denyEnclosingArgInConstructorCall && currentMethodScope.isConstructorCall && !isAnonymousAndHasEnclosing && (TypeBinding.equalsEquals(sourceType, targetEnclosingType) || (!onlyExactMatch && sourceType.findSuperTypeOriginatingFrom(targetEnclosingType) != null))) {
                    return BlockScope.NoEnclosingInstanceInConstructorCall;
                }
                return new Object[] { syntheticArg };
            }
            else {
                if (currentMethodScope.isStatic) {
                    return BlockScope.NoEnclosingInstanceInStaticContext;
                }
                if (sourceType.isAnonymousType()) {
                    final ReferenceBinding enclosingType = sourceType.enclosingType();
                    if (enclosingType.isNestedType()) {
                        final NestedTypeBinding nestedEnclosingType = (NestedTypeBinding)enclosingType;
                        final SyntheticArgumentBinding enclosingArgument = nestedEnclosingType.getSyntheticArgument(nestedEnclosingType.enclosingType(), onlyExactMatch, currentMethodScope.isConstructorCall);
                        if (enclosingArgument != null) {
                            final FieldBinding syntheticField = sourceType.getSyntheticField(enclosingArgument);
                            if (syntheticField != null && (TypeBinding.equalsEquals(syntheticField.type, targetEnclosingType) || (!onlyExactMatch && syntheticField.type.findSuperTypeOriginatingFrom(targetEnclosingType) != null))) {
                                return new Object[] { syntheticField };
                            }
                        }
                    }
                }
                FieldBinding syntheticField2 = sourceType.getSyntheticField(targetEnclosingType, onlyExactMatch);
                if (syntheticField2 == null) {
                    Object[] path = new Object[2];
                    ReferenceBinding currentType = sourceType.enclosingType();
                    if (insideConstructor) {
                        path[0] = ((NestedTypeBinding)sourceType).getSyntheticArgument(currentType, onlyExactMatch, currentMethodScope.isConstructorCall);
                    }
                    else {
                        if (currentMethodScope.isConstructorCall) {
                            return BlockScope.NoEnclosingInstanceInConstructorCall;
                        }
                        path[0] = sourceType.getSyntheticField(currentType, onlyExactMatch);
                    }
                    if (path[0] != null) {
                        int count = 1;
                        ReferenceBinding currentEnclosingType;
                        while ((currentEnclosingType = currentType.enclosingType()) != null && !TypeBinding.equalsEquals(currentType, targetEnclosingType)) {
                            if (!onlyExactMatch && currentType.findSuperTypeOriginatingFrom(targetEnclosingType) != null) {
                                break;
                            }
                            if (currentMethodScope != null) {
                                currentMethodScope = currentMethodScope.enclosingMethodScope();
                                if (currentMethodScope != null && currentMethodScope.isConstructorCall) {
                                    return BlockScope.NoEnclosingInstanceInConstructorCall;
                                }
                                if (currentMethodScope != null && currentMethodScope.isStatic) {
                                    return BlockScope.NoEnclosingInstanceInStaticContext;
                                }
                            }
                            syntheticField2 = ((NestedTypeBinding)currentType).getSyntheticField(currentEnclosingType, onlyExactMatch);
                            if (syntheticField2 == null) {
                                break;
                            }
                            if (count == path.length) {
                                System.arraycopy(path, 0, path = new Object[count + 1], 0, count);
                            }
                            path[count++] = ((SourceTypeBinding)syntheticField2.declaringClass).addSyntheticMethod(syntheticField2, true, false);
                            currentType = currentEnclosingType;
                        }
                        if (TypeBinding.equalsEquals(currentType, targetEnclosingType) || (!onlyExactMatch && currentType.findSuperTypeOriginatingFrom(targetEnclosingType) != null)) {
                            return path;
                        }
                    }
                    return null;
                }
                if (currentMethodScope.isConstructorCall) {
                    return BlockScope.NoEnclosingInstanceInConstructorCall;
                }
                return new Object[] { syntheticField2 };
            }
        }
    }
    
    public final boolean isDuplicateLocalVariable(final char[] name) {
        BlockScope current = this;
        while (true) {
            for (int i = 0; i < this.localIndex; ++i) {
                if (CharOperation.equals(name, current.locals[i].name)) {
                    return true;
                }
            }
            if (current.kind != 1) {
                return false;
            }
            current = (BlockScope)current.parent;
        }
    }
    
    public int maxShiftedOffset() {
        int max = -1;
        if (this.shiftScopes != null) {
            for (int i = 0, length = this.shiftScopes.length; i < length; ++i) {
                if (this.shiftScopes[i] != null) {
                    final int subMaxOffset = this.shiftScopes[i].maxOffset;
                    if (subMaxOffset > max) {
                        max = subMaxOffset;
                    }
                }
            }
        }
        return max;
    }
    
    public final boolean needBlankFinalFieldInitializationCheck(final FieldBinding binding) {
        final boolean isStatic = binding.isStatic();
        final ReferenceBinding fieldDeclaringClass = binding.declaringClass;
        for (MethodScope methodScope = this.namedMethodScope(); methodScope != null; methodScope = methodScope.enclosingMethodScope().namedMethodScope()) {
            if (methodScope.isStatic != isStatic) {
                return false;
            }
            if (!methodScope.isInsideInitializer() && !((AbstractMethodDeclaration)methodScope.referenceContext).isInitializationMethod()) {
                return false;
            }
            final ReferenceBinding enclosingType = methodScope.enclosingReceiverType();
            if (TypeBinding.equalsEquals(enclosingType, fieldDeclaringClass)) {
                return true;
            }
            if (!enclosingType.erasure().isAnonymousType()) {
                return false;
            }
        }
        return false;
    }
    
    @Override
    public ProblemReporter problemReporter() {
        return this.methodScope().problemReporter();
    }
    
    public void propagateInnerEmulation(final ReferenceBinding targetType, final boolean isEnclosingInstanceSupplied) {
        final SyntheticArgumentBinding[] syntheticArguments;
        if ((syntheticArguments = targetType.syntheticOuterLocalVariables()) != null) {
            for (int i = 0, max = syntheticArguments.length; i < max; ++i) {
                final SyntheticArgumentBinding syntheticArg = syntheticArguments[i];
                if (!isEnclosingInstanceSupplied || !TypeBinding.equalsEquals(syntheticArg.type, targetType.enclosingType())) {
                    this.emulateOuterAccess(syntheticArg.actualOuterLocalVariable);
                }
            }
        }
    }
    
    public TypeDeclaration referenceType() {
        return this.methodScope().referenceType();
    }
    
    public int scopeIndex() {
        if (this instanceof MethodScope) {
            return -1;
        }
        final BlockScope parentScope = (BlockScope)this.parent;
        final Scope[] parentSubscopes = parentScope.subscopes;
        for (int i = 0, max = parentScope.subscopeCount; i < max; ++i) {
            if (parentSubscopes[i] == this) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    int startIndex() {
        return this.startIndex;
    }
    
    @Override
    public String toString() {
        return this.toString(0);
    }
    
    public String toString(final int tab) {
        String s = this.basicToString(tab);
        for (int i = 0; i < this.subscopeCount; ++i) {
            if (this.subscopes[i] instanceof BlockScope) {
                s = String.valueOf(s) + ((BlockScope)this.subscopes[i]).toString(tab + 1) + "\n";
            }
        }
        return s;
    }
    
    public int registerTrackingVariable(final FakedTrackingVariable fakedTrackingVariable) {
        if (this.trackingVariables == null) {
            this.trackingVariables = new ArrayList(3);
        }
        this.trackingVariables.add(fakedTrackingVariable);
        final MethodScope outerMethodScope = this.outerMostMethodScope();
        return outerMethodScope.analysisIndex++;
    }
    
    public void removeTrackingVar(final FakedTrackingVariable trackingVariable) {
        if (trackingVariable.innerTracker != null) {
            trackingVariable.innerTracker.withdraw();
            trackingVariable.innerTracker = null;
        }
        if (this.trackingVariables != null && this.trackingVariables.remove(trackingVariable)) {
            return;
        }
        if (this.parent instanceof BlockScope) {
            ((BlockScope)this.parent).removeTrackingVar(trackingVariable);
        }
    }
    
    public void pruneWrapperTrackingVar(final FakedTrackingVariable trackingVariable) {
        this.trackingVariables.remove(trackingVariable);
    }
    
    public void checkUnclosedCloseables(final FlowInfo flowInfo, final FlowContext flowContext, final ASTNode location, final BlockScope locationScope) {
        if (!this.compilerOptions().analyseResourceLeaks) {
            return;
        }
        if (this.trackingVariables == null) {
            if (location != null && this.parent instanceof BlockScope) {
                ((BlockScope)this.parent).checkUnclosedCloseables(flowInfo, flowContext, location, locationScope);
            }
            return;
        }
        if (location != null && flowInfo.reachMode() != 0) {
            return;
        }
        final FakedTrackingVariable returnVar = (location instanceof ReturnStatement) ? FakedTrackingVariable.getCloseTrackingVariable(((ReturnStatement)location).expression, flowInfo, flowContext) : null;
        final Iterator<FakedTrackingVariable> iterator = new FakedTrackingVariable.IteratorForReporting(this.trackingVariables, this, location != null);
        while (iterator.hasNext()) {
            final FakedTrackingVariable trackingVar = iterator.next();
            if (returnVar != null && trackingVar.isResourceBeingReturned(returnVar)) {
                continue;
            }
            if (location != null && trackingVar.hasDefinitelyNoResource(flowInfo)) {
                continue;
            }
            if (location != null && flowContext != null && flowContext.recordExitAgainstResource(this, flowInfo, trackingVar, location)) {
                continue;
            }
            final int status = trackingVar.findMostSpecificStatus(flowInfo, this, locationScope);
            if (status == 2) {
                this.reportResourceLeak(trackingVar, location, status);
            }
            else {
                if (location == null && trackingVar.reportRecordedErrors(this, status, flowInfo.reachMode() != 0)) {
                    continue;
                }
                if (status == 16) {
                    this.reportResourceLeak(trackingVar, location, status);
                }
                else {
                    if (status != 4 || this.environment().globalOptions.complianceLevel < 3342336L) {
                        continue;
                    }
                    trackingVar.reportExplicitClosing(this.problemReporter());
                }
            }
        }
        if (location == null) {
            for (int i = 0; i < this.localIndex; ++i) {
                this.locals[i].closeTracker = null;
            }
            this.trackingVariables = null;
        }
    }
    
    private void reportResourceLeak(final FakedTrackingVariable trackingVar, final ASTNode location, final int nullStatus) {
        if (location != null) {
            trackingVar.recordErrorLocation(location, nullStatus);
        }
        else {
            trackingVar.reportError(this.problemReporter(), null, nullStatus);
        }
    }
    
    public void correlateTrackingVarsIfElse(final FlowInfo thenFlowInfo, final FlowInfo elseFlowInfo) {
        if (this.trackingVariables != null) {
            for (int trackVarCount = this.trackingVariables.size(), i = 0; i < trackVarCount; ++i) {
                final FakedTrackingVariable trackingVar = this.trackingVariables.get(i);
                if (trackingVar.originalBinding != null) {
                    if (thenFlowInfo.isDefinitelyNonNull(trackingVar.binding) && elseFlowInfo.isDefinitelyNull(trackingVar.originalBinding)) {
                        elseFlowInfo.markAsDefinitelyNonNull(trackingVar.binding);
                    }
                    else if (elseFlowInfo.isDefinitelyNonNull(trackingVar.binding) && thenFlowInfo.isDefinitelyNull(trackingVar.originalBinding)) {
                        thenFlowInfo.markAsDefinitelyNonNull(trackingVar.binding);
                    }
                    else if (thenFlowInfo != FlowInfo.DEAD_END) {
                        if (elseFlowInfo != FlowInfo.DEAD_END) {
                            for (int j = i + 1; j < trackVarCount; ++j) {
                                final FakedTrackingVariable var2 = this.trackingVariables.get(j);
                                if (trackingVar.originalBinding == var2.originalBinding) {
                                    final boolean var1SeenInThen = thenFlowInfo.hasNullInfoFor(trackingVar.binding);
                                    final boolean var1SeenInElse = elseFlowInfo.hasNullInfoFor(trackingVar.binding);
                                    final boolean var2SeenInThen = thenFlowInfo.hasNullInfoFor(var2.binding);
                                    final boolean var2SeenInElse = elseFlowInfo.hasNullInfoFor(var2.binding);
                                    int newStatus;
                                    if (!var1SeenInThen && var1SeenInElse && var2SeenInThen && !var2SeenInElse) {
                                        newStatus = FlowInfo.mergeNullStatus(thenFlowInfo.nullStatus(var2.binding), elseFlowInfo.nullStatus(trackingVar.binding));
                                    }
                                    else {
                                        if (!var1SeenInThen || var1SeenInElse || var2SeenInThen || !var2SeenInElse) {
                                            continue;
                                        }
                                        newStatus = FlowInfo.mergeNullStatus(thenFlowInfo.nullStatus(trackingVar.binding), elseFlowInfo.nullStatus(var2.binding));
                                    }
                                    thenFlowInfo.markNullStatus(trackingVar.binding, newStatus);
                                    elseFlowInfo.markNullStatus(trackingVar.binding, newStatus);
                                    trackingVar.originalBinding.closeTracker = trackingVar;
                                    thenFlowInfo.markNullStatus(var2.binding, 4);
                                    elseFlowInfo.markNullStatus(var2.binding, 4);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.parent instanceof BlockScope) {
            ((BlockScope)this.parent).correlateTrackingVarsIfElse(thenFlowInfo, elseFlowInfo);
        }
    }
    
    public void checkAppropriateMethodAgainstSupers(final char[] selector, final MethodBinding compileTimeMethod, final TypeBinding[] parameters, final InvocationSite site) {
        final ReferenceBinding enclosingType = this.enclosingReceiverType();
        MethodBinding otherMethod = this.getMethod(enclosingType.superclass(), selector, parameters, site);
        if (this.checkAppropriate(compileTimeMethod, otherMethod, site)) {
            final ReferenceBinding[] superInterfaces = enclosingType.superInterfaces();
            if (superInterfaces != null) {
                for (int i = 0; i < superInterfaces.length; ++i) {
                    otherMethod = this.getMethod(superInterfaces[i], selector, parameters, site);
                    if (!this.checkAppropriate(compileTimeMethod, otherMethod, site)) {
                        break;
                    }
                }
            }
        }
    }
    
    private boolean checkAppropriate(final MethodBinding compileTimeDeclaration, final MethodBinding otherMethod, final InvocationSite location) {
        if (otherMethod == null || !otherMethod.isValidBinding() || otherMethod == compileTimeDeclaration) {
            return true;
        }
        if (MethodVerifier.doesMethodOverride(otherMethod, compileTimeDeclaration, this.environment())) {
            this.problemReporter().illegalSuperCallBypassingOverride(location, compileTimeDeclaration, otherMethod.declaringClass);
            return false;
        }
        return true;
    }
    
    @Override
    public boolean hasDefaultNullnessFor(final int location) {
        return this.parent.hasDefaultNullnessFor(location);
    }
}
