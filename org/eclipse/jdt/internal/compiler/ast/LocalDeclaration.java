package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.parser.RecoveryScanner;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import java.util.List;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;

public class LocalDeclaration extends AbstractVariableDeclaration
{
    public LocalVariableBinding binding;
    
    public LocalDeclaration(final char[] name, final int sourceStart, final int sourceEnd) {
        this.name = name;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
        this.declarationEnd = sourceEnd;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x1) == 0x0) {
            this.bits |= 0x40000000;
        }
        if (this.initialization == null) {
            return flowInfo;
        }
        this.initialization.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
        FlowInfo preInitInfo = null;
        final boolean shouldAnalyseResource = this.binding != null && flowInfo.reachMode() == 0 && currentScope.compilerOptions().analyseResourceLeaks && FakedTrackingVariable.isAnyCloseable(this.initialization.resolvedType);
        if (shouldAnalyseResource) {
            preInitInfo = flowInfo.unconditionalCopy();
            FakedTrackingVariable.preConnectTrackerAcrossAssignment(this, this.binding, this.initialization, flowInfo);
        }
        flowInfo = this.initialization.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
        if (shouldAnalyseResource) {
            FakedTrackingVariable.handleResourceAssignment(currentScope, preInitInfo, flowInfo, flowContext, this, this.initialization, this.binding);
        }
        else {
            FakedTrackingVariable.cleanUpAfterAssignment(currentScope, 2, this.initialization);
        }
        int nullStatus = this.initialization.nullStatus(flowInfo, flowContext);
        if (!flowInfo.isDefinitelyAssigned(this.binding)) {
            this.bits |= 0x8;
        }
        else {
            this.bits &= 0xFFFFFFF7;
        }
        flowInfo.markAsDefinitelyAssigned(this.binding);
        if (currentScope.compilerOptions().isAnnotationBasedNullAnalysisEnabled) {
            nullStatus = NullAnnotationMatching.checkAssignment(currentScope, flowContext, this.binding, flowInfo, nullStatus, this.initialization, this.initialization.resolvedType);
        }
        if ((this.binding.type.tagBits & 0x2L) == 0x0L) {
            flowInfo.markNullStatus(this.binding, nullStatus);
        }
        return flowInfo;
    }
    
    public void checkModifiers() {
        if ((this.modifiers & 0xFFFF & 0xFFFFFFEF) != 0x0) {
            this.modifiers = ((this.modifiers & 0xFFBFFFFF) | 0x800000);
        }
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if (this.binding.resolvedPosition != -1) {
            codeStream.addVisibleLocalVariable(this.binding);
        }
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        final int pc = codeStream.position;
        if (this.initialization != null) {
            if (this.binding.resolvedPosition < 0) {
                if (this.initialization.constant == Constant.NotAConstant) {
                    this.initialization.generateCode(currentScope, codeStream, false);
                }
            }
            else {
                this.initialization.generateCode(currentScope, codeStream, true);
                if (this.binding.type.isArrayType() && this.initialization instanceof CastExpression && ((CastExpression)this.initialization).innermostCastedExpression().resolvedType == TypeBinding.NULL) {
                    codeStream.checkcast(this.binding.type);
                }
                codeStream.store(this.binding, false);
                if ((this.bits & 0x8) != 0x0) {
                    this.binding.recordInitializationStartPC(codeStream.position);
                }
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public int getKind() {
        return 4;
    }
    
    public void getAllAnnotationContexts(final int targetType, final LocalVariableBinding localVariable, final List allAnnotationContexts) {
        final TypeReference.AnnotationCollector collector = new TypeReference.AnnotationCollector(this, targetType, localVariable, allAnnotationContexts);
        this.traverseWithoutInitializer(collector, null);
    }
    
    public void getAllAnnotationContexts(final int targetType, final int parameterIndex, final List allAnnotationContexts) {
        final TypeReference.AnnotationCollector collector = new TypeReference.AnnotationCollector(this, targetType, parameterIndex, allAnnotationContexts);
        this.traverse(collector, null);
    }
    
    public boolean isArgument() {
        return false;
    }
    
    public boolean isReceiver() {
        return false;
    }
    
    @Override
    public void resolve(final BlockScope scope) {
        final TypeBinding variableType = this.type.resolveType(scope, true);
        this.bits |= (this.type.bits & 0x100000);
        this.checkModifiers();
        if (variableType != null) {
            if (variableType == TypeBinding.VOID) {
                scope.problemReporter().variableTypeCannotBeVoid(this);
                return;
            }
            if (variableType.isArrayType() && ((ArrayBinding)variableType).leafComponentType == TypeBinding.VOID) {
                scope.problemReporter().variableTypeCannotBeVoidArray(this);
                return;
            }
        }
        final Binding existingVariable = scope.getBinding(this.name, 3, this, false);
        if (existingVariable != null && existingVariable.isValidBinding()) {
            final boolean localExists = existingVariable instanceof LocalVariableBinding;
            if (localExists && (this.bits & 0x200000) != 0x0 && scope.isLambdaSubscope() && this.hiddenVariableDepth == 0) {
                scope.problemReporter().lambdaRedeclaresLocal(this);
            }
            else if (localExists && this.hiddenVariableDepth == 0) {
                scope.problemReporter().redefineLocal(this);
            }
            else {
                scope.problemReporter().localVariableHiding(this, existingVariable, false);
            }
        }
        if ((this.modifiers & 0x10) != 0x0 && this.initialization == null) {
            this.modifiers |= 0x4000000;
        }
        scope.addLocalVariable(this.binding = new LocalVariableBinding(this, variableType, this.modifiers, false));
        this.binding.setConstant(Constant.NotAConstant);
        if (variableType == null) {
            if (this.initialization != null) {
                this.initialization.resolveType(scope);
            }
            return;
        }
        if (this.initialization != null) {
            if (this.initialization instanceof ArrayInitializer) {
                final TypeBinding initializationType = this.initialization.resolveTypeExpecting(scope, variableType);
                if (initializationType != null) {
                    ((ArrayInitializer)this.initialization).binding = (ArrayBinding)initializationType;
                    this.initialization.computeConversion(scope, variableType, initializationType);
                }
            }
            else {
                this.initialization.setExpressionContext(ExpressionContext.ASSIGNMENT_CONTEXT);
                this.initialization.setExpectedType(variableType);
                final TypeBinding initializationType = this.initialization.resolveType(scope);
                if (initializationType != null) {
                    if (TypeBinding.notEquals(variableType, initializationType)) {
                        scope.compilationUnitScope().recordTypeConversion(variableType, initializationType);
                    }
                    if (this.initialization.isConstantValueOfTypeAssignableToType(initializationType, variableType) || initializationType.isCompatibleWith(variableType, scope)) {
                        this.initialization.computeConversion(scope, variableType, initializationType);
                        if (initializationType.needsUncheckedConversion(variableType)) {
                            scope.problemReporter().unsafeTypeConversion(this.initialization, initializationType, variableType);
                        }
                        if (this.initialization instanceof CastExpression && (this.initialization.bits & 0x4000) == 0x0) {
                            CastExpression.checkNeedForAssignedCast(scope, variableType, (CastExpression)this.initialization);
                        }
                    }
                    else if (this.isBoxingCompatible(initializationType, variableType, this.initialization, scope)) {
                        this.initialization.computeConversion(scope, variableType, initializationType);
                        if (this.initialization instanceof CastExpression && (this.initialization.bits & 0x4000) == 0x0) {
                            CastExpression.checkNeedForAssignedCast(scope, variableType, (CastExpression)this.initialization);
                        }
                    }
                    else if ((variableType.tagBits & 0x80L) == 0x0L) {
                        scope.problemReporter().typeMismatchError(initializationType, variableType, this.initialization, null);
                    }
                }
            }
            if (this.binding == Expression.getDirectBinding(this.initialization)) {
                scope.problemReporter().assignmentHasNoEffect(this, this.name);
            }
            this.binding.setConstant(this.binding.isFinal() ? this.initialization.constant.castTo((variableType.id << 4) + this.initialization.constant.typeID()) : Constant.NotAConstant);
        }
        ASTNode.resolveAnnotations(scope, this.annotations, this.binding, true);
        Annotation.isTypeUseCompatible(this.type, scope, this.annotations);
        if (!scope.validateNullAnnotation(this.binding.tagBits, this.type, this.annotations)) {
            final LocalVariableBinding binding = this.binding;
            binding.tagBits &= 0xFE7FFFFFFFFFFFFFL;
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.annotations != null) {
                for (int annotationsLength = this.annotations.length, i = 0; i < annotationsLength; ++i) {
                    this.annotations[i].traverse(visitor, scope);
                }
            }
            this.type.traverse(visitor, scope);
            if (this.initialization != null) {
                this.initialization.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
    
    private void traverseWithoutInitializer(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.annotations != null) {
                for (int annotationsLength = this.annotations.length, i = 0; i < annotationsLength; ++i) {
                    this.annotations[i].traverse(visitor, scope);
                }
            }
            this.type.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
    
    public boolean isRecoveredFromLoneIdentifier() {
        return this.name == RecoveryScanner.FAKE_IDENTIFIER && (this.type instanceof SingleTypeReference || (this.type instanceof QualifiedTypeReference && !(this.type instanceof ArrayQualifiedTypeReference))) && this.initialization == null && !this.type.isBaseTypeReference();
    }
}
