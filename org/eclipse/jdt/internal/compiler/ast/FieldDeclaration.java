package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import java.util.List;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;

public class FieldDeclaration extends AbstractVariableDeclaration
{
    public FieldBinding binding;
    public Javadoc javadoc;
    public int endPart1Position;
    public int endPart2Position;
    
    public FieldDeclaration() {
    }
    
    public FieldDeclaration(final char[] name, final int sourceStart, final int sourceEnd) {
        this.name = name;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
    }
    
    public FlowInfo analyseCode(final MethodScope initializationScope, final FlowContext flowContext, FlowInfo flowInfo) {
        if (this.binding != null && !this.binding.isUsed() && this.binding.isOrEnclosedByPrivateType() && !initializationScope.referenceCompilationUnit().compilationResult.hasSyntaxError) {
            initializationScope.problemReporter().unusedPrivateField(this);
        }
        if (this.binding != null && this.binding.isValidBinding() && this.binding.isStatic() && this.binding.constant(initializationScope) == Constant.NotAConstant && this.binding.declaringClass.isNestedType() && !this.binding.declaringClass.isStatic()) {
            initializationScope.problemReporter().unexpectedStaticModifierForField((SourceTypeBinding)this.binding.declaringClass, this);
        }
        if (this.initialization != null) {
            flowInfo = this.initialization.analyseCode(initializationScope, flowContext, flowInfo).unconditionalInits();
            flowInfo.markAsDefinitelyAssigned(this.binding);
        }
        if (this.initialization != null && this.binding != null) {
            final CompilerOptions options = initializationScope.compilerOptions();
            if (options.isAnnotationBasedNullAnalysisEnabled && (this.binding.isNonNull() || options.sourceLevel >= 3407872L)) {
                final int nullStatus = this.initialization.nullStatus(flowInfo, flowContext);
                NullAnnotationMatching.checkAssignment(initializationScope, flowContext, this.binding, flowInfo, nullStatus, this.initialization, this.initialization.resolvedType);
            }
            this.initialization.checkNPEbyUnboxing(initializationScope, flowContext, flowInfo);
        }
        return flowInfo;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        final int pc = codeStream.position;
        final boolean isStatic;
        if (this.initialization != null && (!(isStatic = this.binding.isStatic()) || this.binding.constant() == Constant.NotAConstant)) {
            if (!isStatic) {
                codeStream.aload_0();
            }
            this.initialization.generateCode(currentScope, codeStream, true);
            if (isStatic) {
                codeStream.fieldAccess((byte)(-77), this.binding, null);
            }
            else {
                codeStream.fieldAccess((byte)(-75), this.binding, null);
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    public void getAllAnnotationContexts(final int targetType, final List allAnnotationContexts) {
        final TypeReference.AnnotationCollector collector = new TypeReference.AnnotationCollector(this.type, targetType, allAnnotationContexts);
        for (int i = 0, max = this.annotations.length; i < max; ++i) {
            final Annotation annotation = this.annotations[i];
            annotation.traverse(collector, (BlockScope)null);
        }
    }
    
    @Override
    public int getKind() {
        return (this.type == null) ? 3 : 1;
    }
    
    public boolean isStatic() {
        if (this.binding != null) {
            return this.binding.isStatic();
        }
        return (this.modifiers & 0x8) != 0x0;
    }
    
    public boolean isFinal() {
        if (this.binding != null) {
            return this.binding.isFinal();
        }
        return (this.modifiers & 0x10) != 0x0;
    }
    
    @Override
    public StringBuffer printStatement(final int indent, final StringBuffer output) {
        if (this.javadoc != null) {
            this.javadoc.print(indent, output);
        }
        return super.printStatement(indent, output);
    }
    
    public void resolve(final MethodScope initializationScope) {
        if ((this.bits & 0x10) != 0x0) {
            return;
        }
        if (this.binding == null || !this.binding.isValidBinding()) {
            return;
        }
        this.bits |= 0x10;
        final ClassScope classScope = initializationScope.enclosingClassScope();
        Label_0263: {
            if (classScope != null) {
                final SourceTypeBinding declaringType = classScope.enclosingSourceType();
                if (declaringType.superclass != null) {
                    final FieldBinding existingVariable = classScope.findField(declaringType.superclass, this.name, this, false, true);
                    if (existingVariable != null) {
                        if (existingVariable.isValidBinding()) {
                            if (existingVariable.original() != this.binding) {
                                if (existingVariable.canBeSeenBy(declaringType, this, initializationScope)) {
                                    initializationScope.problemReporter().fieldHiding(this, existingVariable);
                                    break Label_0263;
                                }
                            }
                        }
                    }
                }
                final Scope outerScope = classScope.parent;
                if (outerScope.kind != 4) {
                    final Binding existingVariable2 = outerScope.getBinding(this.name, 3, this, false);
                    if (existingVariable2 != null) {
                        if (existingVariable2.isValidBinding()) {
                            if (existingVariable2 != this.binding) {
                                if (existingVariable2 instanceof FieldBinding) {
                                    final FieldBinding existingField = (FieldBinding)existingVariable2;
                                    if (existingField.original() == this.binding) {
                                        break Label_0263;
                                    }
                                    if (!existingField.isStatic() && declaringType.isStatic()) {
                                        break Label_0263;
                                    }
                                }
                                initializationScope.problemReporter().fieldHiding(this, existingVariable2);
                            }
                        }
                    }
                }
            }
        }
        if (this.type != null) {
            this.type.resolvedType = this.binding.type;
        }
        final FieldBinding previousField = initializationScope.initializedField;
        final int previousFieldID = initializationScope.lastVisibleFieldID;
        try {
            initializationScope.initializedField = this.binding;
            initializationScope.lastVisibleFieldID = this.binding.id;
            ASTNode.resolveAnnotations(initializationScope, this.annotations, this.binding);
            if (this.annotations != null) {
                for (int i = 0, max = this.annotations.length; i < max; ++i) {
                    final TypeBinding resolvedAnnotationType = this.annotations[i].resolvedType;
                    if (resolvedAnnotationType != null && (resolvedAnnotationType.getAnnotationTagBits() & 0x20000000000000L) != 0x0L) {
                        this.bits |= 0x100000;
                        break;
                    }
                }
            }
            if ((this.binding.getAnnotationTagBits() & 0x400000000000L) == 0x0L && (this.binding.modifiers & 0x100000) != 0x0 && initializationScope.compilerOptions().sourceLevel >= 3211264L) {
                initializationScope.problemReporter().missingDeprecatedAnnotationForField(this);
            }
            if (this.initialization == null) {
                this.binding.setConstant(Constant.NotAConstant);
            }
            else {
                this.binding.setConstant(Constant.NotAConstant);
                final TypeBinding fieldType = this.binding.type;
                this.initialization.setExpressionContext(ExpressionContext.ASSIGNMENT_CONTEXT);
                this.initialization.setExpectedType(fieldType);
                if (this.initialization instanceof ArrayInitializer) {
                    final TypeBinding initializationType;
                    if ((initializationType = this.initialization.resolveTypeExpecting(initializationScope, fieldType)) != null) {
                        ((ArrayInitializer)this.initialization).binding = (ArrayBinding)initializationType;
                        this.initialization.computeConversion(initializationScope, fieldType, initializationType);
                    }
                }
                else {
                    final TypeBinding initializationType;
                    if ((initializationType = this.initialization.resolveType(initializationScope)) != null) {
                        if (TypeBinding.notEquals(fieldType, initializationType)) {
                            initializationScope.compilationUnitScope().recordTypeConversion(fieldType, initializationType);
                        }
                        if (this.initialization.isConstantValueOfTypeAssignableToType(initializationType, fieldType) || initializationType.isCompatibleWith(fieldType, classScope)) {
                            this.initialization.computeConversion(initializationScope, fieldType, initializationType);
                            if (initializationType.needsUncheckedConversion(fieldType)) {
                                initializationScope.problemReporter().unsafeTypeConversion(this.initialization, initializationType, fieldType);
                            }
                            if (this.initialization instanceof CastExpression && (this.initialization.bits & 0x4000) == 0x0) {
                                CastExpression.checkNeedForAssignedCast(initializationScope, fieldType, (CastExpression)this.initialization);
                            }
                        }
                        else if (this.isBoxingCompatible(initializationType, fieldType, this.initialization, initializationScope)) {
                            this.initialization.computeConversion(initializationScope, fieldType, initializationType);
                            if (this.initialization instanceof CastExpression && (this.initialization.bits & 0x4000) == 0x0) {
                                CastExpression.checkNeedForAssignedCast(initializationScope, fieldType, (CastExpression)this.initialization);
                            }
                        }
                        else if ((fieldType.tagBits & 0x80L) == 0x0L) {
                            initializationScope.problemReporter().typeMismatchError(initializationType, fieldType, this.initialization, null);
                        }
                        if (this.binding.isFinal()) {
                            this.binding.setConstant(this.initialization.constant.castTo((this.binding.type.id << 4) + this.initialization.constant.typeID()));
                        }
                    }
                    else {
                        this.binding.setConstant(Constant.NotAConstant);
                    }
                }
                if (this.binding == Expression.getDirectBinding(this.initialization)) {
                    initializationScope.problemReporter().assignmentHasNoEffect(this, this.name);
                }
            }
            if (this.javadoc != null) {
                this.javadoc.resolve(initializationScope);
            }
            else if (this.binding != null && this.binding.declaringClass != null && !this.binding.declaringClass.isLocalType()) {
                int javadocVisibility = this.binding.modifiers & 0x7;
                final ProblemReporter reporter = initializationScope.problemReporter();
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
        finally {
            initializationScope.initializedField = previousField;
            initializationScope.lastVisibleFieldID = previousFieldID;
            if (this.binding.constant(initializationScope) == null) {
                this.binding.setConstant(Constant.NotAConstant);
            }
        }
        initializationScope.initializedField = previousField;
        initializationScope.lastVisibleFieldID = previousFieldID;
        if (this.binding.constant(initializationScope) == null) {
            this.binding.setConstant(Constant.NotAConstant);
        }
    }
    
    public void traverse(final ASTVisitor visitor, final MethodScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.javadoc != null) {
                this.javadoc.traverse(visitor, scope);
            }
            if (this.annotations != null) {
                for (int annotationsLength = this.annotations.length, i = 0; i < annotationsLength; ++i) {
                    this.annotations[i].traverse(visitor, scope);
                }
            }
            if (this.type != null) {
                this.type.traverse(visitor, scope);
            }
            if (this.initialization != null) {
                this.initialization.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
}
