package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;

public class MethodScope extends BlockScope
{
    public ReferenceContext referenceContext;
    public boolean isStatic;
    public boolean isConstructorCall;
    public FieldBinding initializedField;
    public int lastVisibleFieldID;
    public int analysisIndex;
    public boolean isPropagatingInnerClassEmulation;
    public int lastIndex;
    public long[] definiteInits;
    public long[][] extraDefiniteInits;
    public SyntheticArgumentBinding[] extraSyntheticArguments;
    public boolean hasMissingSwitchDefault;
    
    public MethodScope(final Scope parent, final ReferenceContext context, final boolean isStatic) {
        super(2, parent);
        this.isConstructorCall = false;
        this.lastVisibleFieldID = -1;
        this.lastIndex = 0;
        this.definiteInits = new long[4];
        this.extraDefiniteInits = new long[4][];
        this.locals = new LocalVariableBinding[5];
        this.referenceContext = context;
        this.isStatic = isStatic;
        this.startIndex = 0;
    }
    
    public MethodScope(final Scope parent, final ReferenceContext context, final boolean isStatic, final int lastVisibleFieldID) {
        this(parent, context, isStatic);
        this.lastVisibleFieldID = lastVisibleFieldID;
    }
    
    @Override
    String basicToString(final int tab) {
        String newLine = "\n";
        int i = tab;
        while (--i >= 0) {
            newLine = String.valueOf(newLine) + "\t";
        }
        String s = String.valueOf(newLine) + "--- Method Scope ---";
        newLine = String.valueOf(newLine) + "\t";
        s = String.valueOf(s) + newLine + "locals:";
        for (int j = 0; j < this.localIndex; ++j) {
            s = String.valueOf(s) + newLine + "\t" + this.locals[j].toString();
        }
        s = String.valueOf(s) + newLine + "startIndex = " + this.startIndex;
        s = String.valueOf(s) + newLine + "isConstructorCall = " + this.isConstructorCall;
        s = String.valueOf(s) + newLine + "initializedField = " + this.initializedField;
        s = String.valueOf(s) + newLine + "lastVisibleFieldID = " + this.lastVisibleFieldID;
        s = String.valueOf(s) + newLine + "referenceContext = " + this.referenceContext;
        return s;
    }
    
    private void checkAndSetModifiersForConstructor(final MethodBinding methodBinding) {
        int modifiers = methodBinding.modifiers;
        final ReferenceBinding declaringClass = methodBinding.declaringClass;
        if ((modifiers & 0x400000) != 0x0) {
            this.problemReporter().duplicateModifierForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
        }
        final int flags;
        if ((((ConstructorDeclaration)this.referenceContext).bits & 0x80) != 0x0 && (flags = (declaringClass.modifiers & 0x4005)) != 0) {
            if ((flags & 0x4000) != 0x0) {
                modifiers &= 0xFFFFFFF8;
                modifiers |= 0x2;
            }
            else {
                modifiers &= 0xFFFFFFF8;
                modifiers |= flags;
            }
        }
        final int realModifiers = modifiers & 0xFFFF;
        if (declaringClass.isEnum() && (((ConstructorDeclaration)this.referenceContext).bits & 0x80) == 0x0) {
            if ((realModifiers & 0xFFFFF7FD) != 0x0) {
                this.problemReporter().illegalModifierForEnumConstructor((AbstractMethodDeclaration)this.referenceContext);
                modifiers &= 0xFFFF0802;
            }
            else if ((((AbstractMethodDeclaration)this.referenceContext).modifiers & 0x800) != 0x0) {
                this.problemReporter().illegalModifierForMethod((AbstractMethodDeclaration)this.referenceContext);
            }
            modifiers |= 0x2;
        }
        else if ((realModifiers & 0xFFFFF7F8) != 0x0) {
            this.problemReporter().illegalModifierForMethod((AbstractMethodDeclaration)this.referenceContext);
            modifiers &= 0xFFFF0807;
        }
        else if ((((AbstractMethodDeclaration)this.referenceContext).modifiers & 0x800) != 0x0) {
            this.problemReporter().illegalModifierForMethod((AbstractMethodDeclaration)this.referenceContext);
        }
        final int accessorBits = realModifiers & 0x7;
        if ((accessorBits & accessorBits - 1) != 0x0) {
            this.problemReporter().illegalVisibilityModifierCombinationForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
            if ((accessorBits & 0x1) != 0x0) {
                if ((accessorBits & 0x4) != 0x0) {
                    modifiers &= 0xFFFFFFFB;
                }
                if ((accessorBits & 0x2) != 0x0) {
                    modifiers &= 0xFFFFFFFD;
                }
            }
            else if ((accessorBits & 0x4) != 0x0 && (accessorBits & 0x2) != 0x0) {
                modifiers &= 0xFFFFFFFD;
            }
        }
        methodBinding.modifiers = modifiers;
    }
    
    private void checkAndSetModifiersForMethod(final MethodBinding methodBinding) {
        int modifiers = methodBinding.modifiers;
        final ReferenceBinding declaringClass = methodBinding.declaringClass;
        if ((modifiers & 0x400000) != 0x0) {
            this.problemReporter().duplicateModifierForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
        }
        int realModifiers = modifiers & 0xFFFF;
        if (declaringClass.isInterface()) {
            int expectedModifiers = 1025;
            final boolean isDefaultMethod = (modifiers & 0x10000) != 0x0;
            boolean reportIllegalModifierCombination = false;
            boolean isJDK18orGreater = false;
            if (this.compilerOptions().sourceLevel >= 3407872L && !declaringClass.isAnnotationType()) {
                expectedModifiers |= 0x10808;
                isJDK18orGreater = true;
                if (!methodBinding.isAbstract()) {
                    reportIllegalModifierCombination = (isDefaultMethod && methodBinding.isStatic());
                }
                else {
                    reportIllegalModifierCombination = (isDefaultMethod || methodBinding.isStatic());
                    if (methodBinding.isStrictfp()) {
                        this.problemReporter().illegalAbstractModifierCombinationForMethod((AbstractMethodDeclaration)this.referenceContext);
                    }
                }
                if (reportIllegalModifierCombination) {
                    this.problemReporter().illegalModifierCombinationForInterfaceMethod((AbstractMethodDeclaration)this.referenceContext);
                }
                if (isDefaultMethod) {
                    realModifiers |= 0x10000;
                }
            }
            if ((realModifiers & ~expectedModifiers) != 0x0) {
                if ((declaringClass.modifiers & 0x2000) != 0x0) {
                    this.problemReporter().illegalModifierForAnnotationMember((AbstractMethodDeclaration)this.referenceContext);
                }
                else {
                    this.problemReporter().illegalModifierForInterfaceMethod((AbstractMethodDeclaration)this.referenceContext, isJDK18orGreater);
                }
            }
            return;
        }
        if ((realModifiers & 0xFFFFF2C0) != 0x0) {
            this.problemReporter().illegalModifierForMethod((AbstractMethodDeclaration)this.referenceContext);
            modifiers &= 0xFFFF0D3F;
        }
        final int accessorBits = realModifiers & 0x7;
        if ((accessorBits & accessorBits - 1) != 0x0) {
            this.problemReporter().illegalVisibilityModifierCombinationForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
            if ((accessorBits & 0x1) != 0x0) {
                if ((accessorBits & 0x4) != 0x0) {
                    modifiers &= 0xFFFFFFFB;
                }
                if ((accessorBits & 0x2) != 0x0) {
                    modifiers &= 0xFFFFFFFD;
                }
            }
            else if ((accessorBits & 0x4) != 0x0 && (accessorBits & 0x2) != 0x0) {
                modifiers &= 0xFFFFFFFD;
            }
        }
        if ((modifiers & 0x400) != 0x0) {
            final int incompatibleWithAbstract = 2362;
            if ((modifiers & incompatibleWithAbstract) != 0x0) {
                this.problemReporter().illegalAbstractModifierCombinationForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
            }
            if (!methodBinding.declaringClass.isAbstract()) {
                this.problemReporter().abstractMethodInAbstractClass((SourceTypeBinding)declaringClass, (AbstractMethodDeclaration)this.referenceContext);
            }
        }
        if ((modifiers & 0x100) != 0x0 && (modifiers & 0x800) != 0x0) {
            this.problemReporter().nativeMethodsCannotBeStrictfp(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
        }
        if ((realModifiers & 0x8) != 0x0 && declaringClass.isNestedType() && !declaringClass.isStatic()) {
            this.problemReporter().unexpectedStaticModifierForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
        }
        methodBinding.modifiers = modifiers;
    }
    
    public void checkUnusedParameters(final MethodBinding method) {
        if (method.isAbstract() || (method.isImplementing() && !this.compilerOptions().reportUnusedParameterWhenImplementingAbstract) || (method.isOverriding() && !method.isImplementing() && !this.compilerOptions().reportUnusedParameterWhenOverridingConcrete) || method.isMain()) {
            return;
        }
        for (int i = 0, maxLocals = this.localIndex; i < maxLocals; ++i) {
            final LocalVariableBinding local = this.locals[i];
            if (local == null) {
                break;
            }
            if ((local.tagBits & 0x400L) == 0x0L) {
                break;
            }
            if (local.useFlag == 0 && (local.declaration.bits & 0x40000000) != 0x0) {
                this.problemReporter().unusedArgument(local.declaration);
            }
        }
    }
    
    public void computeLocalVariablePositions(final int initOffset, final CodeStream codeStream) {
        this.offset = initOffset;
        this.maxOffset = initOffset;
        int ilocal = 0;
        for (int maxLocals = this.localIndex; ilocal < maxLocals; ++ilocal) {
            final LocalVariableBinding local = this.locals[ilocal];
            if (local == null) {
                break;
            }
            if ((local.tagBits & 0x400L) == 0x0L) {
                break;
            }
            codeStream.record(local);
            local.resolvedPosition = this.offset;
            if (TypeBinding.equalsEquals(local.type, TypeBinding.LONG) || TypeBinding.equalsEquals(local.type, TypeBinding.DOUBLE)) {
                this.offset += 2;
            }
            else {
                ++this.offset;
            }
            if (this.offset > 255) {
                this.problemReporter().noMoreAvailableSpaceForArgument(local, local.declaration);
            }
        }
        if (this.extraSyntheticArguments != null) {
            for (int iarg = 0, maxArguments = this.extraSyntheticArguments.length; iarg < maxArguments; ++iarg) {
                final SyntheticArgumentBinding argument = this.extraSyntheticArguments[iarg];
                argument.resolvedPosition = this.offset;
                if (TypeBinding.equalsEquals(argument.type, TypeBinding.LONG) || TypeBinding.equalsEquals(argument.type, TypeBinding.DOUBLE)) {
                    this.offset += 2;
                }
                else {
                    ++this.offset;
                }
                if (this.offset > 255) {
                    this.problemReporter().noMoreAvailableSpaceForArgument(argument, (ASTNode)this.referenceContext);
                }
            }
        }
        this.computeLocalVariablePositions(ilocal, this.offset, codeStream);
    }
    
    MethodBinding createMethod(final AbstractMethodDeclaration method) {
        this.referenceContext = method;
        method.scope = this;
        final SourceTypeBinding declaringClass = this.referenceType().binding;
        int modifiers = method.modifiers | 0x2000000;
        if (method.isConstructor()) {
            if (method.isDefaultConstructor()) {
                modifiers |= 0x4000000;
            }
            this.checkAndSetModifiersForConstructor(method.binding = new MethodBinding(modifiers, null, null, declaringClass));
        }
        else {
            if (declaringClass.isInterface()) {
                if (method.isDefaultMethod() || method.isStatic()) {
                    modifiers |= 0x1;
                }
                else {
                    modifiers |= 0x401;
                }
            }
            this.checkAndSetModifiersForMethod(method.binding = new MethodBinding(modifiers, method.selector, null, null, null, declaringClass));
        }
        this.isStatic = method.binding.isStatic();
        final Argument[] argTypes = method.arguments;
        int argLength = (argTypes == null) ? 0 : argTypes.length;
        final long sourceLevel = this.compilerOptions().sourceLevel;
        if (argLength > 0) {
            Argument argument = argTypes[--argLength];
            if (argument.isVarArgs() && sourceLevel >= 3211264L) {
                final MethodBinding binding = method.binding;
                binding.modifiers |= 0x80;
            }
            if (CharOperation.equals(argument.name, ConstantPool.This)) {
                this.problemReporter().illegalThisDeclaration(argument);
            }
            while (--argLength >= 0) {
                argument = argTypes[argLength];
                if (argument.isVarArgs() && sourceLevel >= 3211264L) {
                    this.problemReporter().illegalVararg(argument, method);
                }
                if (CharOperation.equals(argument.name, ConstantPool.This)) {
                    this.problemReporter().illegalThisDeclaration(argument);
                }
            }
        }
        if (method.receiver != null) {
            if (sourceLevel <= 3342336L) {
                this.problemReporter().illegalSourceLevelForThis(method.receiver);
            }
            if (method.receiver.annotations != null) {
                method.bits |= 0x100000;
            }
        }
        final TypeParameter[] typeParameters = method.typeParameters();
        if (typeParameters == null || typeParameters.length == 0) {
            method.binding.typeVariables = Binding.NO_TYPE_VARIABLES;
        }
        else {
            method.binding.typeVariables = this.createTypeVariables(typeParameters, method.binding);
            final MethodBinding binding2 = method.binding;
            binding2.modifiers |= 0x40000000;
        }
        return method.binding;
    }
    
    @Override
    public FieldBinding findField(final TypeBinding receiverType, final char[] fieldName, final InvocationSite invocationSite, final boolean needResolve) {
        final FieldBinding field = super.findField(receiverType, fieldName, invocationSite, needResolve);
        if (field == null) {
            return null;
        }
        if (!field.isValidBinding()) {
            return field;
        }
        if (receiverType.isInterface() && invocationSite.isQualifiedSuper()) {
            return new ProblemFieldBinding(field, field.declaringClass, fieldName, 28);
        }
        if (field.isStatic()) {
            return field;
        }
        if (!this.isConstructorCall || TypeBinding.notEquals(receiverType, this.enclosingSourceType())) {
            return field;
        }
        if (invocationSite instanceof SingleNameReference) {
            return new ProblemFieldBinding(field, field.declaringClass, fieldName, 6);
        }
        if (invocationSite instanceof QualifiedNameReference) {
            final QualifiedNameReference name = (QualifiedNameReference)invocationSite;
            if (name.binding == null) {
                return new ProblemFieldBinding(field, field.declaringClass, fieldName, 6);
            }
        }
        return field;
    }
    
    public boolean isInsideConstructor() {
        return this.referenceContext instanceof ConstructorDeclaration;
    }
    
    public boolean isInsideInitializer() {
        return this.referenceContext instanceof TypeDeclaration;
    }
    
    @Override
    public boolean isLambdaScope() {
        return this.referenceContext instanceof LambdaExpression;
    }
    
    public boolean isInsideInitializerOrConstructor() {
        return this.referenceContext instanceof TypeDeclaration || this.referenceContext instanceof ConstructorDeclaration;
    }
    
    @Override
    public ProblemReporter problemReporter() {
        final ProblemReporter problemReporter = this.referenceCompilationUnit().problemReporter;
        problemReporter.referenceContext = this.referenceContext;
        return problemReporter;
    }
    
    public final int recordInitializationStates(final FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x1) != 0x0) {
            return -1;
        }
        final UnconditionalFlowInfo unconditionalFlowInfo = flowInfo.unconditionalInitsWithoutSideEffect();
        final long[] extraInits = (long[])((unconditionalFlowInfo.extra == null) ? null : unconditionalFlowInfo.extra[0]);
        final long inits = unconditionalFlowInfo.definiteInits;
        int i = this.lastIndex;
    Label_0139:
        while (--i >= 0) {
            if (this.definiteInits[i] == inits) {
                final long[] otherInits = this.extraDefiniteInits[i];
                if (extraInits != null && otherInits != null) {
                    if (extraInits.length == otherInits.length) {
                        for (int j = 0, max = extraInits.length; j < max; ++j) {
                            if (extraInits[j] != otherInits[j]) {
                                continue Label_0139;
                            }
                        }
                        return i;
                    }
                    continue;
                }
                else {
                    if (extraInits == null && otherInits == null) {
                        return i;
                    }
                    continue;
                }
            }
        }
        if (this.definiteInits.length == this.lastIndex) {
            System.arraycopy(this.definiteInits, 0, this.definiteInits = new long[this.lastIndex + 20], 0, this.lastIndex);
            System.arraycopy(this.extraDefiniteInits, 0, this.extraDefiniteInits = new long[this.lastIndex + 20][], 0, this.lastIndex);
        }
        this.definiteInits[this.lastIndex] = inits;
        if (extraInits != null) {
            System.arraycopy(extraInits, 0, this.extraDefiniteInits[this.lastIndex] = new long[extraInits.length], 0, extraInits.length);
        }
        return this.lastIndex++;
    }
    
    public AbstractMethodDeclaration referenceMethod() {
        if (this.referenceContext instanceof AbstractMethodDeclaration) {
            return (AbstractMethodDeclaration)this.referenceContext;
        }
        return null;
    }
    
    public MethodBinding referenceMethodBinding() {
        if (this.referenceContext instanceof LambdaExpression) {
            return ((LambdaExpression)this.referenceContext).binding;
        }
        if (this.referenceContext instanceof AbstractMethodDeclaration) {
            return ((AbstractMethodDeclaration)this.referenceContext).binding;
        }
        return null;
    }
    
    @Override
    public TypeDeclaration referenceType() {
        final ClassScope scope = this.enclosingClassScope();
        return (scope == null) ? null : scope.referenceContext;
    }
    
    @Override
    void resolveTypeParameter(final TypeParameter typeParameter) {
        typeParameter.resolve(this);
    }
    
    @Override
    public boolean hasDefaultNullnessFor(final int location) {
        if (this.referenceContext instanceof AbstractMethodDeclaration) {
            final MethodBinding binding = ((AbstractMethodDeclaration)this.referenceContext).binding;
            if (binding != null && binding.defaultNullness != 0) {
                return (binding.defaultNullness & location) != 0x0;
            }
        }
        return this.parent.hasDefaultNullnessFor(location);
    }
}
