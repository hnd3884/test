package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.flow.LoopingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ForeachStatement extends Statement
{
    public LocalDeclaration elementVariable;
    public int elementVariableImplicitWidening;
    public Expression collection;
    public Statement action;
    private int kind;
    private static final int ARRAY = 0;
    private static final int RAW_ITERABLE = 1;
    private static final int GENERIC_ITERABLE = 2;
    private TypeBinding iteratorReceiverType;
    private TypeBinding collectionElementType;
    private BranchLabel breakLabel;
    private BranchLabel continueLabel;
    public BlockScope scope;
    public LocalVariableBinding indexVariable;
    public LocalVariableBinding collectionVariable;
    public LocalVariableBinding maxVariable;
    private static final char[] SecretIteratorVariableName;
    private static final char[] SecretIndexVariableName;
    private static final char[] SecretCollectionVariableName;
    private static final char[] SecretMaxVariableName;
    int postCollectionInitStateIndex;
    int mergedInitStateIndex;
    
    static {
        SecretIteratorVariableName = " iterator".toCharArray();
        SecretIndexVariableName = " index".toCharArray();
        SecretCollectionVariableName = " collection".toCharArray();
        SecretMaxVariableName = " max".toCharArray();
    }
    
    public ForeachStatement(final LocalDeclaration elementVariable, final int start) {
        this.elementVariableImplicitWidening = -1;
        this.postCollectionInitStateIndex = -1;
        this.mergedInitStateIndex = -1;
        this.elementVariable = elementVariable;
        this.sourceStart = start;
        this.kind = -1;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        this.breakLabel = new BranchLabel();
        this.continueLabel = new BranchLabel();
        final int initialComplaintLevel = ((flowInfo.reachMode() & 0x3) != 0x0) ? 1 : 0;
        this.collection.checkNPE(currentScope, flowContext, flowInfo, 1);
        flowInfo = this.elementVariable.analyseCode(this.scope, flowContext, flowInfo);
        final FlowInfo condInfo = this.collection.analyseCode(this.scope, flowContext, flowInfo.copy());
        final LocalVariableBinding elementVarBinding = this.elementVariable.binding;
        condInfo.markAsDefinitelyAssigned(elementVarBinding);
        this.postCollectionInitStateIndex = currentScope.methodScope().recordInitializationStates(condInfo);
        final LoopingFlowContext loopingContext = new LoopingFlowContext(flowContext, flowInfo, this, this.breakLabel, this.continueLabel, this.scope, true);
        UnconditionalFlowInfo actionInfo = condInfo.nullInfoLessUnconditionalCopy();
        actionInfo.markAsDefinitelyUnknown(elementVarBinding);
        if (currentScope.compilerOptions().isAnnotationBasedNullAnalysisEnabled) {
            final int elementNullStatus = FlowInfo.tagBitsToNullStatus(this.collectionElementType.tagBits);
            final int nullStatus = NullAnnotationMatching.checkAssignment(currentScope, flowContext, elementVarBinding, null, elementNullStatus, this.collection, this.collectionElementType);
            if ((elementVarBinding.type.tagBits & 0x2L) == 0x0L) {
                actionInfo.markNullStatus(elementVarBinding, nullStatus);
            }
        }
        FlowInfo exitBranch;
        if (this.action != null && (!this.action.isEmptyBlock() || currentScope.compilerOptions().complianceLevel > 3080192L)) {
            if (this.action.complainIfUnreachable(actionInfo, this.scope, initialComplaintLevel, true) < 2) {
                actionInfo = this.action.analyseCode(this.scope, loopingContext, actionInfo).unconditionalCopy();
            }
            exitBranch = flowInfo.unconditionalCopy().addInitializationsFrom(condInfo.initsWhenFalse());
            if ((actionInfo.tagBits & loopingContext.initsOnContinue.tagBits & 0x1) != 0x0) {
                this.continueLabel = null;
            }
            else {
                actionInfo = actionInfo.mergedWith(loopingContext.initsOnContinue);
                loopingContext.complainOnDeferredFinalChecks(this.scope, actionInfo);
                exitBranch.addPotentialInitializationsFrom(actionInfo);
            }
        }
        else {
            exitBranch = condInfo.initsWhenFalse();
        }
        final boolean hasEmptyAction = this.action == null || this.action.isEmptyBlock() || (this.action.bits & 0x1) != 0x0;
        switch (this.kind) {
            case 0: {
                if (hasEmptyAction && elementVarBinding.resolvedPosition == -1) {
                    break;
                }
                this.collectionVariable.useFlag = 1;
                if (this.continueLabel != null) {
                    this.indexVariable.useFlag = 1;
                    this.maxVariable.useFlag = 1;
                    break;
                }
                break;
            }
            case 1:
            case 2: {
                this.indexVariable.useFlag = 1;
                break;
            }
        }
        loopingContext.complainOnDeferredNullChecks(currentScope, actionInfo);
        if (loopingContext.hasEscapingExceptions()) {
            FlowInfo loopbackFlowInfo = flowInfo.copy();
            if (this.continueLabel != null) {
                loopbackFlowInfo = loopbackFlowInfo.mergedWith(loopbackFlowInfo.unconditionalCopy().addNullInfoFrom(actionInfo).unconditionalInits());
            }
            loopingContext.simulateThrowAfterLoopBack(loopbackFlowInfo);
        }
        final FlowInfo mergedInfo = FlowInfo.mergedOptimizedBranches(((loopingContext.initsOnBreak.tagBits & 0x3) != 0x0) ? loopingContext.initsOnBreak : flowInfo.addInitializationsFrom(loopingContext.initsOnBreak), false, exitBranch, false, true);
        mergedInfo.resetAssignmentInfo(this.elementVariable.binding);
        this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
        return mergedInfo;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        final int pc = codeStream.position;
        final boolean hasEmptyAction = this.action == null || this.action.isEmptyBlock() || (this.action.bits & 0x1) != 0x0;
        if (hasEmptyAction && this.elementVariable.binding.resolvedPosition == -1 && this.kind == 0) {
            this.collection.generateCode(this.scope, codeStream, false);
            codeStream.exitUserScope(this.scope);
            if (this.mergedInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        switch (this.kind) {
            case 0: {
                this.collection.generateCode(this.scope, codeStream, true);
                codeStream.store(this.collectionVariable, true);
                codeStream.addVariable(this.collectionVariable);
                if (this.continueLabel != null) {
                    codeStream.arraylength();
                    codeStream.store(this.maxVariable, false);
                    codeStream.addVariable(this.maxVariable);
                    codeStream.iconst_0();
                    codeStream.store(this.indexVariable, false);
                    codeStream.addVariable(this.indexVariable);
                    break;
                }
                break;
            }
            case 1:
            case 2: {
                this.collection.generateCode(this.scope, codeStream, true);
                codeStream.invokeIterableIterator(this.iteratorReceiverType);
                codeStream.store(this.indexVariable, false);
                codeStream.addVariable(this.indexVariable);
                break;
            }
        }
        final BranchLabel branchLabel;
        final BranchLabel actionLabel = branchLabel = new BranchLabel(codeStream);
        branchLabel.tagBits |= 0x2;
        final BranchLabel branchLabel2;
        final BranchLabel conditionLabel = branchLabel2 = new BranchLabel(codeStream);
        branchLabel2.tagBits |= 0x2;
        this.breakLabel.initialize(codeStream);
        if (this.continueLabel == null) {
            conditionLabel.place();
            final int conditionPC = codeStream.position;
            switch (this.kind) {
                case 0: {
                    codeStream.arraylength();
                    codeStream.ifeq(this.breakLabel);
                    break;
                }
                case 1:
                case 2: {
                    codeStream.load(this.indexVariable);
                    codeStream.invokeJavaUtilIteratorHasNext();
                    codeStream.ifeq(this.breakLabel);
                    break;
                }
            }
            codeStream.recordPositionsFrom(conditionPC, this.elementVariable.sourceStart);
        }
        else {
            this.continueLabel.initialize(codeStream);
            final BranchLabel continueLabel = this.continueLabel;
            continueLabel.tagBits |= 0x2;
            codeStream.goto_(conditionLabel);
        }
        actionLabel.place();
        Label_0786: {
            switch (this.kind) {
                case 0: {
                    if (this.elementVariable.binding.resolvedPosition == -1) {
                        break;
                    }
                    codeStream.load(this.collectionVariable);
                    if (this.continueLabel == null) {
                        codeStream.iconst_0();
                    }
                    else {
                        codeStream.load(this.indexVariable);
                    }
                    codeStream.arrayAt(this.collectionElementType.id);
                    if (this.elementVariableImplicitWidening != -1) {
                        codeStream.generateImplicitConversion(this.elementVariableImplicitWidening);
                    }
                    codeStream.store(this.elementVariable.binding, false);
                    codeStream.addVisibleLocalVariable(this.elementVariable.binding);
                    if (this.postCollectionInitStateIndex != -1) {
                        codeStream.addDefinitelyAssignedVariables(currentScope, this.postCollectionInitStateIndex);
                        break;
                    }
                    break;
                }
                case 1:
                case 2: {
                    codeStream.load(this.indexVariable);
                    codeStream.invokeJavaUtilIteratorNext();
                    if (this.elementVariable.binding.type.id != 1) {
                        if (this.elementVariableImplicitWidening != -1) {
                            codeStream.checkcast(this.collectionElementType);
                            codeStream.generateImplicitConversion(this.elementVariableImplicitWidening);
                        }
                        else {
                            codeStream.checkcast(this.elementVariable.binding.type);
                        }
                    }
                    if (this.elementVariable.binding.resolvedPosition == -1) {
                        switch (this.elementVariable.binding.type.id) {
                            case 7:
                            case 8: {
                                codeStream.pop2();
                                break Label_0786;
                            }
                            default: {
                                codeStream.pop();
                                break Label_0786;
                            }
                        }
                    }
                    else {
                        codeStream.store(this.elementVariable.binding, false);
                        codeStream.addVisibleLocalVariable(this.elementVariable.binding);
                        if (this.postCollectionInitStateIndex != -1) {
                            codeStream.addDefinitelyAssignedVariables(currentScope, this.postCollectionInitStateIndex);
                            break;
                        }
                        break;
                    }
                    break;
                }
            }
        }
        if (!hasEmptyAction) {
            this.action.generateCode(this.scope, codeStream);
        }
        codeStream.removeVariable(this.elementVariable.binding);
        if (this.postCollectionInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.postCollectionInitStateIndex);
        }
        if (this.continueLabel != null) {
            this.continueLabel.place();
            final int continuationPC = codeStream.position;
            switch (this.kind) {
                case 0: {
                    if (!hasEmptyAction || this.elementVariable.binding.resolvedPosition >= 0) {
                        codeStream.iinc(this.indexVariable.resolvedPosition, 1);
                    }
                    conditionLabel.place();
                    codeStream.load(this.indexVariable);
                    codeStream.load(this.maxVariable);
                    codeStream.if_icmplt(actionLabel);
                    break;
                }
                case 1:
                case 2: {
                    conditionLabel.place();
                    codeStream.load(this.indexVariable);
                    codeStream.invokeJavaUtilIteratorHasNext();
                    codeStream.ifne(actionLabel);
                    break;
                }
            }
            codeStream.recordPositionsFrom(continuationPC, this.elementVariable.sourceStart);
        }
        switch (this.kind) {
            case 0: {
                codeStream.removeVariable(this.indexVariable);
                codeStream.removeVariable(this.maxVariable);
                codeStream.removeVariable(this.collectionVariable);
                break;
            }
            case 1:
            case 2: {
                codeStream.removeVariable(this.indexVariable);
                break;
            }
        }
        codeStream.exitUserScope(this.scope);
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
        this.breakLabel.place();
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public StringBuffer printStatement(final int indent, final StringBuffer output) {
        ASTNode.printIndent(indent, output).append("for (");
        this.elementVariable.printAsExpression(0, output);
        output.append(" : ");
        if (this.collection != null) {
            this.collection.print(0, output).append(") ");
        }
        else {
            output.append(')');
        }
        if (this.action == null) {
            output.append(';');
        }
        else {
            output.append('\n');
            this.action.printStatement(indent + 1, output);
        }
        return output;
    }
    
    @Override
    public void resolve(final BlockScope upperScope) {
        this.scope = new BlockScope(upperScope);
        this.elementVariable.resolve(this.scope);
        final TypeBinding elementType = this.elementVariable.type.resolvedType;
        TypeBinding collectionType = (this.collection == null) ? null : this.collection.resolveType(upperScope);
        TypeBinding expectedCollectionType = null;
        if (elementType != null && collectionType != null) {
            final boolean isTargetJsr14 = this.scope.compilerOptions().targetJDK == 3145728L;
            if (collectionType.isCapture()) {
                final TypeBinding upperBound = ((CaptureBinding)collectionType).firstBound;
                if (upperBound != null && upperBound.isArrayType()) {
                    collectionType = upperBound;
                }
            }
            Label_0980: {
                if (collectionType.isArrayType()) {
                    this.kind = 0;
                    this.collectionElementType = ((ArrayBinding)collectionType).elementsType();
                    if (!this.collectionElementType.isCompatibleWith(elementType) && !this.scope.isBoxingCompatibleWith(this.collectionElementType, elementType)) {
                        this.scope.problemReporter().notCompatibleTypesErrorInForeach(this.collection, this.collectionElementType, elementType);
                    }
                    else if (this.collectionElementType.needsUncheckedConversion(elementType)) {
                        this.scope.problemReporter().unsafeElementTypeConversion(this.collection, this.collectionElementType, elementType);
                    }
                    int compileTimeTypeID = this.collectionElementType.id;
                    if (elementType.isBaseType()) {
                        this.collection.computeConversion(this.scope, collectionType, collectionType);
                        if (!this.collectionElementType.isBaseType()) {
                            compileTimeTypeID = this.scope.environment().computeBoxingType(this.collectionElementType).id;
                            this.elementVariableImplicitWidening = 1024;
                            if (elementType.isBaseType()) {
                                this.elementVariableImplicitWidening |= (elementType.id << 4) + compileTimeTypeID;
                                this.scope.problemReporter().autoboxing(this.collection, this.collectionElementType, elementType);
                            }
                        }
                        else {
                            this.elementVariableImplicitWidening = (elementType.id << 4) + compileTimeTypeID;
                        }
                    }
                    else if (this.collectionElementType.isBaseType()) {
                        this.collection.computeConversion(this.scope, collectionType, collectionType);
                        final int boxedID = this.scope.environment().computeBoxingType(this.collectionElementType).id;
                        this.elementVariableImplicitWidening = (0x200 | compileTimeTypeID << 4 | compileTimeTypeID);
                        compileTimeTypeID = boxedID;
                        this.scope.problemReporter().autoboxing(this.collection, this.collectionElementType, elementType);
                    }
                    else {
                        expectedCollectionType = upperScope.createArrayType(elementType, 1);
                        this.collection.computeConversion(this.scope, expectedCollectionType, collectionType);
                    }
                }
                else if (collectionType instanceof ReferenceBinding) {
                    ReferenceBinding iterableType = collectionType.findSuperTypeOriginatingFrom(38, false);
                    if (iterableType == null && isTargetJsr14) {
                        iterableType = collectionType.findSuperTypeOriginatingFrom(59, false);
                    }
                    if (iterableType != null) {
                        this.iteratorReceiverType = collectionType.erasure();
                        if (isTargetJsr14) {
                            if (this.iteratorReceiverType.findSuperTypeOriginatingFrom(59, false) == null) {
                                this.iteratorReceiverType = iterableType;
                                this.collection.computeConversion(this.scope, iterableType, collectionType);
                            }
                            else {
                                this.collection.computeConversion(this.scope, collectionType, collectionType);
                            }
                        }
                        else if (this.iteratorReceiverType.findSuperTypeOriginatingFrom(38, false) == null) {
                            this.iteratorReceiverType = iterableType;
                            this.collection.computeConversion(this.scope, iterableType, collectionType);
                        }
                        else {
                            this.collection.computeConversion(this.scope, collectionType, collectionType);
                        }
                        TypeBinding[] arguments = null;
                        switch (iterableType.kind()) {
                            case 1028: {
                                this.kind = 1;
                                this.collectionElementType = this.scope.getJavaLangObject();
                                if (!this.collectionElementType.isCompatibleWith(elementType) && !this.scope.isBoxingCompatibleWith(this.collectionElementType, elementType)) {
                                    this.scope.problemReporter().notCompatibleTypesErrorInForeach(this.collection, this.collectionElementType, elementType);
                                }
                                break Label_0980;
                            }
                            case 2052: {
                                arguments = iterableType.typeVariables();
                                break;
                            }
                            case 260: {
                                arguments = ((ParameterizedTypeBinding)iterableType).arguments;
                                break;
                            }
                            default: {
                                break Label_0980;
                            }
                        }
                        if (arguments.length == 1) {
                            this.kind = 2;
                            this.collectionElementType = arguments[0];
                            if (!this.collectionElementType.isCompatibleWith(elementType) && !this.scope.isBoxingCompatibleWith(this.collectionElementType, elementType)) {
                                this.scope.problemReporter().notCompatibleTypesErrorInForeach(this.collection, this.collectionElementType, elementType);
                            }
                            else if (this.collectionElementType.needsUncheckedConversion(elementType)) {
                                this.scope.problemReporter().unsafeElementTypeConversion(this.collection, this.collectionElementType, elementType);
                            }
                            int compileTimeTypeID2 = this.collectionElementType.id;
                            if (elementType.isBaseType()) {
                                if (!this.collectionElementType.isBaseType()) {
                                    compileTimeTypeID2 = this.scope.environment().computeBoxingType(this.collectionElementType).id;
                                    this.elementVariableImplicitWidening = 1024;
                                    if (elementType.isBaseType()) {
                                        this.elementVariableImplicitWidening |= (elementType.id << 4) + compileTimeTypeID2;
                                    }
                                }
                                else {
                                    this.elementVariableImplicitWidening = (elementType.id << 4) + compileTimeTypeID2;
                                }
                            }
                            else if (this.collectionElementType.isBaseType()) {
                                this.elementVariableImplicitWidening = (0x200 | compileTimeTypeID2 << 4 | compileTimeTypeID2);
                            }
                        }
                    }
                }
            }
            switch (this.kind) {
                case 0: {
                    this.indexVariable = new LocalVariableBinding(ForeachStatement.SecretIndexVariableName, TypeBinding.INT, 0, false);
                    this.scope.addLocalVariable(this.indexVariable);
                    this.indexVariable.setConstant(Constant.NotAConstant);
                    this.maxVariable = new LocalVariableBinding(ForeachStatement.SecretMaxVariableName, TypeBinding.INT, 0, false);
                    this.scope.addLocalVariable(this.maxVariable);
                    this.maxVariable.setConstant(Constant.NotAConstant);
                    if (expectedCollectionType == null) {
                        this.collectionVariable = new LocalVariableBinding(ForeachStatement.SecretCollectionVariableName, collectionType, 0, false);
                    }
                    else {
                        this.collectionVariable = new LocalVariableBinding(ForeachStatement.SecretCollectionVariableName, expectedCollectionType, 0, false);
                    }
                    this.scope.addLocalVariable(this.collectionVariable);
                    this.collectionVariable.setConstant(Constant.NotAConstant);
                    break;
                }
                case 1:
                case 2: {
                    this.indexVariable = new LocalVariableBinding(ForeachStatement.SecretIteratorVariableName, this.scope.getJavaUtilIterator(), 0, false);
                    this.scope.addLocalVariable(this.indexVariable);
                    this.indexVariable.setConstant(Constant.NotAConstant);
                    break;
                }
                default: {
                    if (isTargetJsr14) {
                        this.scope.problemReporter().invalidTypeForCollectionTarget14(this.collection);
                        break;
                    }
                    this.scope.problemReporter().invalidTypeForCollection(this.collection);
                    break;
                }
            }
        }
        if (this.action != null) {
            this.action.resolve(this.scope);
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.elementVariable.traverse(visitor, this.scope);
            if (this.collection != null) {
                this.collection.traverse(visitor, this.scope);
            }
            if (this.action != null) {
                this.action.traverse(visitor, this.scope);
            }
        }
        visitor.endVisit(this, blockScope);
    }
    
    @Override
    public boolean doesNotCompleteNormally() {
        return false;
    }
}
