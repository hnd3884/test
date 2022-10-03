package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.lookup.MemberTypeBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortType;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;

public class TypeDeclaration extends Statement implements ProblemSeverities, ReferenceContext
{
    public static final int CLASS_DECL = 1;
    public static final int INTERFACE_DECL = 2;
    public static final int ENUM_DECL = 3;
    public static final int ANNOTATION_TYPE_DECL = 4;
    public int modifiers;
    public int modifiersSourceStart;
    public int functionalExpressionsCount;
    public Annotation[] annotations;
    public char[] name;
    public TypeReference superclass;
    public TypeReference[] superInterfaces;
    public FieldDeclaration[] fields;
    public AbstractMethodDeclaration[] methods;
    public TypeDeclaration[] memberTypes;
    public SourceTypeBinding binding;
    public ClassScope scope;
    public MethodScope initializerScope;
    public MethodScope staticInitializerScope;
    public boolean ignoreFurtherInvestigation;
    public int maxFieldCount;
    public int declarationSourceStart;
    public int declarationSourceEnd;
    public int bodyStart;
    public int bodyEnd;
    public CompilationResult compilationResult;
    public MethodDeclaration[] missingAbstractMethods;
    public Javadoc javadoc;
    public QualifiedAllocationExpression allocation;
    public TypeDeclaration enclosingType;
    public FieldBinding enumValuesSyntheticfield;
    public int enumConstantsCounter;
    public TypeParameter[] typeParameters;
    
    public TypeDeclaration(final CompilationResult compilationResult) {
        this.modifiers = 0;
        this.functionalExpressionsCount = 0;
        this.ignoreFurtherInvestigation = false;
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
            case 16: {
                throw new AbortMethod(this.compilationResult, problem);
            }
            default: {
                throw new AbortType(this.compilationResult, problem);
            }
        }
    }
    
    public final void addClinit() {
        if (this.needClassInitMethod()) {
            AbstractMethodDeclaration[] methodDeclarations;
            if ((methodDeclarations = this.methods) == null) {
                final int length = 0;
                methodDeclarations = new AbstractMethodDeclaration[] { null };
            }
            else {
                final int length = methodDeclarations.length;
                System.arraycopy(methodDeclarations, 0, methodDeclarations = new AbstractMethodDeclaration[length + 1], 1, length);
            }
            final Clinit clinit = new Clinit(this.compilationResult);
            methodDeclarations[0] = clinit;
            final Clinit clinit2 = clinit;
            final Clinit clinit3 = clinit;
            final int sourceStart = this.sourceStart;
            clinit3.sourceStart = sourceStart;
            clinit2.declarationSourceStart = sourceStart;
            final Clinit clinit4 = clinit;
            final Clinit clinit5 = clinit;
            final int sourceEnd = this.sourceEnd;
            clinit5.sourceEnd = sourceEnd;
            clinit4.declarationSourceEnd = sourceEnd;
            clinit.bodyEnd = this.sourceEnd;
            this.methods = methodDeclarations;
        }
    }
    
    public MethodDeclaration addMissingAbstractMethodFor(final MethodBinding methodBinding) {
        final TypeBinding[] argumentTypes = methodBinding.parameters;
        final int argumentsLength = argumentTypes.length;
        final MethodDeclaration methodDeclaration = new MethodDeclaration(this.compilationResult);
        methodDeclaration.selector = methodBinding.selector;
        methodDeclaration.sourceStart = this.sourceStart;
        methodDeclaration.sourceEnd = this.sourceEnd;
        methodDeclaration.modifiers = (methodBinding.getAccessFlags() & 0xFFFFFBFF);
        if (argumentsLength > 0) {
            final String baseName = "arg";
            final MethodDeclaration methodDeclaration2 = methodDeclaration;
            final Argument[] arguments2 = new Argument[argumentsLength];
            methodDeclaration2.arguments = arguments2;
            final Argument[] arguments = arguments2;
            int i = argumentsLength;
            while (--i >= 0) {
                arguments[i] = new Argument((String.valueOf(baseName) + i).toCharArray(), 0L, null, 0);
            }
        }
        if (this.missingAbstractMethods == null) {
            this.missingAbstractMethods = new MethodDeclaration[] { methodDeclaration };
        }
        else {
            final MethodDeclaration[] newMethods;
            System.arraycopy(this.missingAbstractMethods, 0, newMethods = new MethodDeclaration[this.missingAbstractMethods.length + 1], 1, this.missingAbstractMethods.length);
            newMethods[0] = methodDeclaration;
            this.missingAbstractMethods = newMethods;
        }
        methodDeclaration.binding = new MethodBinding(methodDeclaration.modifiers | 0x1000, methodBinding.selector, methodBinding.returnType, (argumentsLength == 0) ? Binding.NO_PARAMETERS : argumentTypes, methodBinding.thrownExceptions, this.binding);
        methodDeclaration.scope = new MethodScope(this.scope, methodDeclaration, true);
        methodDeclaration.bindArguments();
        return methodDeclaration;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        if (this.ignoreFurtherInvestigation) {
            return flowInfo;
        }
        try {
            if ((flowInfo.tagBits & 0x1) == 0x0) {
                this.bits |= Integer.MIN_VALUE;
                final LocalTypeBinding localType = (LocalTypeBinding)this.binding;
                localType.setConstantPoolName(currentScope.compilationUnitScope().computeConstantPoolName(localType));
            }
            this.manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
            this.updateMaxFieldCount();
            this.internalAnalyseCode(flowContext, flowInfo);
        }
        catch (final AbortType abortType) {
            this.ignoreFurtherInvestigation = true;
        }
        return flowInfo;
    }
    
    public void analyseCode(final ClassScope enclosingClassScope) {
        if (this.ignoreFurtherInvestigation) {
            return;
        }
        try {
            this.updateMaxFieldCount();
            this.internalAnalyseCode(null, FlowInfo.initial(this.maxFieldCount));
        }
        catch (final AbortType abortType) {
            this.ignoreFurtherInvestigation = true;
        }
    }
    
    public void analyseCode(final ClassScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        if (this.ignoreFurtherInvestigation) {
            return;
        }
        try {
            if ((flowInfo.tagBits & 0x1) == 0x0) {
                this.bits |= Integer.MIN_VALUE;
                final LocalTypeBinding localType = (LocalTypeBinding)this.binding;
                localType.setConstantPoolName(currentScope.compilationUnitScope().computeConstantPoolName(localType));
            }
            this.manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
            this.updateMaxFieldCount();
            this.internalAnalyseCode(flowContext, flowInfo);
        }
        catch (final AbortType abortType) {
            this.ignoreFurtherInvestigation = true;
        }
    }
    
    public void analyseCode(final CompilationUnitScope unitScope) {
        if (this.ignoreFurtherInvestigation) {
            return;
        }
        try {
            this.internalAnalyseCode(null, FlowInfo.initial(this.maxFieldCount));
        }
        catch (final AbortType abortType) {
            this.ignoreFurtherInvestigation = true;
        }
    }
    
    public boolean checkConstructors(final Parser parser) {
        boolean hasConstructor = false;
        if (this.methods != null) {
            int i = this.methods.length;
            while (--i >= 0) {
                final AbstractMethodDeclaration am;
                if ((am = this.methods[i]).isConstructor()) {
                    if (!CharOperation.equals(am.selector, this.name)) {
                        final ConstructorDeclaration c = (ConstructorDeclaration)am;
                        if (c.constructorCall != null && !c.constructorCall.isImplicitSuper()) {
                            continue;
                        }
                        final MethodDeclaration m = parser.convertToMethodDeclaration(c, this.compilationResult);
                        this.methods[i] = m;
                    }
                    else {
                        switch (kind(this.modifiers)) {
                            case 2: {
                                parser.problemReporter().interfaceCannotHaveConstructors((ConstructorDeclaration)am);
                                break;
                            }
                            case 4: {
                                parser.problemReporter().annotationTypeDeclarationCannotHaveConstructor((ConstructorDeclaration)am);
                                break;
                            }
                        }
                        hasConstructor = true;
                    }
                }
            }
        }
        return hasConstructor;
    }
    
    @Override
    public CompilationResult compilationResult() {
        return this.compilationResult;
    }
    
    public ConstructorDeclaration createDefaultConstructor(final boolean needExplicitConstructorCall, final boolean needToInsert) {
        final ConstructorDeclaration constructorDeclaration;
        final ConstructorDeclaration constructor = constructorDeclaration = new ConstructorDeclaration(this.compilationResult);
        constructorDeclaration.bits |= 0x80;
        constructor.selector = this.name;
        constructor.modifiers = (this.modifiers & 0x7);
        final ConstructorDeclaration constructorDeclaration2 = constructor;
        final ConstructorDeclaration constructorDeclaration3 = constructor;
        final int sourceStart = this.sourceStart;
        constructorDeclaration3.sourceStart = sourceStart;
        constructorDeclaration2.declarationSourceStart = sourceStart;
        final ConstructorDeclaration constructorDeclaration4 = constructor;
        final ConstructorDeclaration constructorDeclaration5 = constructor;
        final ConstructorDeclaration constructorDeclaration6 = constructor;
        final int sourceEnd = this.sourceEnd;
        constructorDeclaration6.bodyEnd = sourceEnd;
        constructorDeclaration5.sourceEnd = sourceEnd;
        constructorDeclaration4.declarationSourceEnd = sourceEnd;
        if (needExplicitConstructorCall) {
            constructor.constructorCall = SuperReference.implicitSuperConstructorCall();
            constructor.constructorCall.sourceStart = this.sourceStart;
            constructor.constructorCall.sourceEnd = this.sourceEnd;
        }
        if (needToInsert) {
            if (this.methods == null) {
                this.methods = new AbstractMethodDeclaration[] { constructor };
            }
            else {
                final AbstractMethodDeclaration[] newMethods;
                System.arraycopy(this.methods, 0, newMethods = new AbstractMethodDeclaration[this.methods.length + 1], 1, this.methods.length);
                newMethods[0] = constructor;
                this.methods = newMethods;
            }
        }
        return constructor;
    }
    
    public MethodBinding createDefaultConstructorWithBinding(final MethodBinding inheritedConstructorBinding, final boolean eraseThrownExceptions) {
        final String baseName = "$anonymous";
        final TypeBinding[] argumentTypes = inheritedConstructorBinding.parameters;
        final int argumentsLength = argumentTypes.length;
        final ConstructorDeclaration constructor = new ConstructorDeclaration(this.compilationResult);
        constructor.selector = new char[] { 'x' };
        constructor.sourceStart = this.sourceStart;
        constructor.sourceEnd = this.sourceEnd;
        int newModifiers = this.modifiers & 0x7;
        if (inheritedConstructorBinding.isVarargs()) {
            newModifiers |= 0x80;
        }
        constructor.modifiers = newModifiers;
        final ConstructorDeclaration constructorDeclaration = constructor;
        constructorDeclaration.bits |= 0x80;
        if (argumentsLength > 0) {
            final ConstructorDeclaration constructorDeclaration2 = constructor;
            final Argument[] arguments2 = new Argument[argumentsLength];
            constructorDeclaration2.arguments = arguments2;
            final Argument[] arguments = arguments2;
            int i = argumentsLength;
            while (--i >= 0) {
                arguments[i] = new Argument((String.valueOf(baseName) + i).toCharArray(), 0L, null, 0);
            }
        }
        constructor.constructorCall = SuperReference.implicitSuperConstructorCall();
        constructor.constructorCall.sourceStart = this.sourceStart;
        constructor.constructorCall.sourceEnd = this.sourceEnd;
        if (argumentsLength > 0) {
            final ExplicitConstructorCall constructorCall = constructor.constructorCall;
            final Expression[] arguments3 = new Expression[argumentsLength];
            constructorCall.arguments = arguments3;
            final Expression[] args = arguments3;
            int i = argumentsLength;
            while (--i >= 0) {
                args[i] = new SingleNameReference((String.valueOf(baseName) + i).toCharArray(), 0L);
            }
        }
        if (this.methods == null) {
            this.methods = new AbstractMethodDeclaration[] { constructor };
        }
        else {
            final AbstractMethodDeclaration[] newMethods;
            System.arraycopy(this.methods, 0, newMethods = new AbstractMethodDeclaration[this.methods.length + 1], 1, this.methods.length);
            newMethods[0] = constructor;
            this.methods = newMethods;
        }
        final ReferenceBinding[] thrownExceptions = eraseThrownExceptions ? this.scope.environment().convertToRawTypes(inheritedConstructorBinding.thrownExceptions, true, true) : inheritedConstructorBinding.thrownExceptions;
        final SourceTypeBinding sourceType = this.binding;
        constructor.binding = new MethodBinding(constructor.modifiers, (argumentsLength == 0) ? Binding.NO_PARAMETERS : argumentTypes, thrownExceptions, sourceType);
        final MethodBinding binding = constructor.binding;
        binding.tagBits |= (inheritedConstructorBinding.tagBits & 0x80L);
        final MethodBinding binding2 = constructor.binding;
        binding2.modifiers |= 0x4000000;
        if (inheritedConstructorBinding.parameterNonNullness != null && argumentsLength > 0) {
            final int len = inheritedConstructorBinding.parameterNonNullness.length;
            System.arraycopy(inheritedConstructorBinding.parameterNonNullness, 0, constructor.binding.parameterNonNullness = new Boolean[len], 0, len);
        }
        constructor.scope = new MethodScope(this.scope, constructor, true);
        constructor.bindArguments();
        constructor.constructorCall.resolve(constructor.scope);
        MethodBinding[] methodBindings = sourceType.methods();
        int length;
        System.arraycopy(methodBindings, 0, methodBindings = new MethodBinding[(length = methodBindings.length) + 1], 1, length);
        methodBindings[0] = constructor.binding;
        if (++length > 1) {
            ReferenceBinding.sortMethods(methodBindings, 0, length);
        }
        sourceType.setMethods(methodBindings);
        return constructor.binding;
    }
    
    public FieldDeclaration declarationOf(final FieldBinding fieldBinding) {
        if (fieldBinding != null && this.fields != null) {
            for (int i = 0, max = this.fields.length; i < max; ++i) {
                final FieldDeclaration fieldDecl;
                if ((fieldDecl = this.fields[i]).binding == fieldBinding) {
                    return fieldDecl;
                }
            }
        }
        return null;
    }
    
    public TypeDeclaration declarationOf(final MemberTypeBinding memberTypeBinding) {
        if (memberTypeBinding != null && this.memberTypes != null) {
            for (int i = 0, max = this.memberTypes.length; i < max; ++i) {
                final TypeDeclaration memberTypeDecl;
                if (TypeBinding.equalsEquals((memberTypeDecl = this.memberTypes[i]).binding, memberTypeBinding)) {
                    return memberTypeDecl;
                }
            }
        }
        return null;
    }
    
    public AbstractMethodDeclaration declarationOf(final MethodBinding methodBinding) {
        if (methodBinding != null && this.methods != null) {
            for (int i = 0, max = this.methods.length; i < max; ++i) {
                final AbstractMethodDeclaration methodDecl;
                if ((methodDecl = this.methods[i]).binding == methodBinding) {
                    return methodDecl;
                }
            }
        }
        return null;
    }
    
    public TypeDeclaration declarationOfType(final char[][] typeName) {
        final int typeNameLength = typeName.length;
        if (typeNameLength < 1 || !CharOperation.equals(typeName[0], this.name)) {
            return null;
        }
        if (typeNameLength == 1) {
            return this;
        }
        final char[][] subTypeName = new char[typeNameLength - 1][];
        System.arraycopy(typeName, 1, subTypeName, 0, typeNameLength - 1);
        for (int i = 0; i < this.memberTypes.length; ++i) {
            final TypeDeclaration typeDecl = this.memberTypes[i].declarationOfType(subTypeName);
            if (typeDecl != null) {
                return typeDecl;
            }
        }
        return null;
    }
    
    @Override
    public CompilationUnitDeclaration getCompilationUnitDeclaration() {
        if (this.scope != null) {
            return this.scope.compilationUnitScope().referenceContext;
        }
        return null;
    }
    
    public void generateCode(final ClassFile enclosingClassFile) {
        if ((this.bits & 0x2000) != 0x0) {
            return;
        }
        this.bits |= 0x2000;
        if (!this.ignoreFurtherInvestigation) {
            try {
                final ClassFile classFile = ClassFile.getNewInstance(this.binding);
                classFile.initialize(this.binding, enclosingClassFile, false);
                if (this.binding.isMemberType()) {
                    classFile.recordInnerClasses(this.binding);
                }
                else if (this.binding.isLocalType()) {
                    enclosingClassFile.recordInnerClasses(this.binding);
                    classFile.recordInnerClasses(this.binding);
                }
                final TypeVariableBinding[] typeVariables = this.binding.typeVariables();
                for (int i = 0, max = typeVariables.length; i < max; ++i) {
                    final TypeVariableBinding typeVariableBinding = typeVariables[i];
                    if ((typeVariableBinding.tagBits & 0x800L) != 0x0L) {
                        Util.recordNestedType(classFile, typeVariableBinding);
                    }
                }
                classFile.addFieldInfos();
                if (this.memberTypes != null) {
                    for (int i = 0, max = this.memberTypes.length; i < max; ++i) {
                        final TypeDeclaration memberType = this.memberTypes[i];
                        classFile.recordInnerClasses(memberType.binding);
                        memberType.generateCode(this.scope, classFile);
                    }
                }
                classFile.setForMethodInfos();
                if (this.methods != null) {
                    for (int i = 0, max = this.methods.length; i < max; ++i) {
                        this.methods[i].generateCode(this.scope, classFile);
                    }
                }
                classFile.addSpecialMethods();
                if (this.ignoreFurtherInvestigation) {
                    throw new AbortType(this.scope.referenceCompilationUnit().compilationResult, (CategorizedProblem)null);
                }
                classFile.addAttributes();
                this.scope.referenceCompilationUnit().compilationResult.record(this.binding.constantPoolName(), classFile);
            }
            catch (final AbortType abortType) {
                if (this.binding == null) {
                    return;
                }
                ClassFile.createProblemType(this, this.scope.referenceCompilationUnit().compilationResult);
            }
            return;
        }
        if (this.binding == null) {
            return;
        }
        ClassFile.createProblemType(this, this.scope.referenceCompilationUnit().compilationResult);
    }
    
    @Override
    public void generateCode(final BlockScope blockScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        if ((this.bits & 0x2000) != 0x0) {
            return;
        }
        final int pc = codeStream.position;
        if (this.binding != null) {
            final SyntheticArgumentBinding[] enclosingInstances = ((NestedTypeBinding)this.binding).syntheticEnclosingInstances();
            int i = 0;
            int slotSize = 0;
            for (int count = (enclosingInstances == null) ? 0 : enclosingInstances.length; i < count; ++i) {
                final SyntheticArgumentBinding enclosingInstance = enclosingInstances[i];
                enclosingInstance.resolvedPosition = ++slotSize;
                if (slotSize > 255) {
                    blockScope.problemReporter().noMoreAvailableSpaceForArgument(enclosingInstance, blockScope.referenceType());
                }
            }
        }
        this.generateCode(codeStream.classFile);
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    public void generateCode(final ClassScope classScope, final ClassFile enclosingClassFile) {
        if ((this.bits & 0x2000) != 0x0) {
            return;
        }
        if (this.binding != null) {
            final SyntheticArgumentBinding[] enclosingInstances = ((NestedTypeBinding)this.binding).syntheticEnclosingInstances();
            int i = 0;
            int slotSize = 0;
            for (int count = (enclosingInstances == null) ? 0 : enclosingInstances.length; i < count; ++i) {
                final SyntheticArgumentBinding enclosingInstance = enclosingInstances[i];
                enclosingInstance.resolvedPosition = ++slotSize;
                if (slotSize > 255) {
                    classScope.problemReporter().noMoreAvailableSpaceForArgument(enclosingInstance, classScope.referenceType());
                }
            }
        }
        this.generateCode(enclosingClassFile);
    }
    
    public void generateCode(final CompilationUnitScope unitScope) {
        this.generateCode((ClassFile)null);
    }
    
    @Override
    public boolean hasErrors() {
        return this.ignoreFurtherInvestigation;
    }
    
    private void internalAnalyseCode(final FlowContext flowContext, final FlowInfo flowInfo) {
        if (!this.binding.isUsed() && this.binding.isOrEnclosedByPrivateType() && !this.scope.referenceCompilationUnit().compilationResult.hasSyntaxError) {
            this.scope.problemReporter().unusedPrivateType(this);
        }
        if (this.typeParameters != null && !this.scope.referenceCompilationUnit().compilationResult.hasSyntaxError) {
            for (int i = 0, length = this.typeParameters.length; i < length; ++i) {
                final TypeParameter typeParameter = this.typeParameters[i];
                if ((typeParameter.binding.modifiers & 0x8000000) == 0x0) {
                    this.scope.problemReporter().unusedTypeParameter(typeParameter);
                }
            }
        }
        final FlowContext parentContext = (flowContext instanceof InitializationFlowContext) ? null : flowContext;
        final InitializationFlowContext initializerContext = new InitializationFlowContext(parentContext, this, flowInfo, flowContext, this.initializerScope);
        final InitializationFlowContext staticInitializerContext = new InitializationFlowContext(null, this, flowInfo, flowContext, this.staticInitializerScope);
        FlowInfo nonStaticFieldInfo = flowInfo.unconditionalFieldLessCopy();
        FlowInfo staticFieldInfo = flowInfo.unconditionalFieldLessCopy();
        if (this.fields != null) {
            for (int j = 0, count = this.fields.length; j < count; ++j) {
                final FieldDeclaration field = this.fields[j];
                if (field.isStatic()) {
                    if ((staticFieldInfo.tagBits & 0x1) != 0x0) {
                        final FieldDeclaration fieldDeclaration = field;
                        fieldDeclaration.bits &= Integer.MAX_VALUE;
                    }
                    staticInitializerContext.handledExceptions = Binding.ANY_EXCEPTION;
                    staticFieldInfo = field.analyseCode(this.staticInitializerScope, staticInitializerContext, staticFieldInfo);
                    if (staticFieldInfo == FlowInfo.DEAD_END) {
                        this.staticInitializerScope.problemReporter().initializerMustCompleteNormally(field);
                        staticFieldInfo = FlowInfo.initial(this.maxFieldCount).setReachMode(1);
                    }
                }
                else {
                    if ((nonStaticFieldInfo.tagBits & 0x1) != 0x0) {
                        final FieldDeclaration fieldDeclaration2 = field;
                        fieldDeclaration2.bits &= Integer.MAX_VALUE;
                    }
                    initializerContext.handledExceptions = Binding.ANY_EXCEPTION;
                    nonStaticFieldInfo = field.analyseCode(this.initializerScope, initializerContext, nonStaticFieldInfo);
                    if (nonStaticFieldInfo == FlowInfo.DEAD_END) {
                        this.initializerScope.problemReporter().initializerMustCompleteNormally(field);
                        nonStaticFieldInfo = FlowInfo.initial(this.maxFieldCount).setReachMode(1);
                    }
                }
            }
        }
        if (this.memberTypes != null) {
            for (int j = 0, count = this.memberTypes.length; j < count; ++j) {
                if (flowContext != null) {
                    this.memberTypes[j].analyseCode(this.scope, flowContext, nonStaticFieldInfo.copy().setReachMode(flowInfo.reachMode()));
                }
                else {
                    this.memberTypes[j].analyseCode(this.scope);
                }
            }
        }
        if (this.methods != null) {
            final UnconditionalFlowInfo outerInfo = flowInfo.unconditionalFieldLessCopy();
            final FlowInfo constructorInfo = nonStaticFieldInfo.unconditionalInits().discardNonFieldInitializations().addInitializationsFrom(outerInfo);
            for (int k = 0, count2 = this.methods.length; k < count2; ++k) {
                final AbstractMethodDeclaration method = this.methods[k];
                if (!method.ignoreFurtherInvestigation) {
                    if (method.isInitializationMethod()) {
                        if (method.isStatic()) {
                            ((Clinit)method).analyseCode(this.scope, staticInitializerContext, staticFieldInfo.unconditionalInits().discardNonFieldInitializations().addInitializationsFrom(outerInfo));
                        }
                        else {
                            ((ConstructorDeclaration)method).analyseCode(this.scope, initializerContext, constructorInfo.copy(), flowInfo.reachMode());
                        }
                    }
                    else {
                        ((MethodDeclaration)method).analyseCode(this.scope, parentContext, flowInfo.copy());
                    }
                }
            }
        }
        if (this.binding.isEnum() && !this.binding.isAnonymousType()) {
            this.enumValuesSyntheticfield = this.binding.addSyntheticFieldForEnumValues();
        }
    }
    
    public static final int kind(final int flags) {
        switch (flags & 0x6200) {
            case 512: {
                return 2;
            }
            case 8704: {
                return 4;
            }
            case 16384: {
                return 3;
            }
            default: {
                return 1;
            }
        }
    }
    
    public void manageEnclosingInstanceAccessIfNecessary(final BlockScope currentScope, final FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x1) != 0x0) {
            return;
        }
        final NestedTypeBinding nestedType = (NestedTypeBinding)this.binding;
        final MethodScope methodScope = currentScope.methodScope();
        if (!methodScope.isStatic && !methodScope.isConstructorCall) {
            nestedType.addSyntheticArgumentAndField(nestedType.enclosingType());
        }
        if (nestedType.isAnonymousType()) {
            final ReferenceBinding superclassBinding = (ReferenceBinding)nestedType.superclass.erasure();
            if (superclassBinding.enclosingType() != null && !superclassBinding.isStatic() && (!superclassBinding.isLocalType() || ((NestedTypeBinding)superclassBinding).getSyntheticField(superclassBinding.enclosingType(), true) != null || superclassBinding.isMemberType())) {
                nestedType.addSyntheticArgument(superclassBinding.enclosingType());
            }
            if (!methodScope.isStatic && methodScope.isConstructorCall && currentScope.compilerOptions().complianceLevel >= 3211264L) {
                final ReferenceBinding enclosing = nestedType.enclosingType();
                if (enclosing.isNestedType()) {
                    final NestedTypeBinding nestedEnclosing = (NestedTypeBinding)enclosing;
                    final SyntheticArgumentBinding syntheticEnclosingInstanceArgument = nestedEnclosing.getSyntheticArgument(nestedEnclosing.enclosingType(), true, false);
                    if (syntheticEnclosingInstanceArgument != null) {
                        nestedType.addSyntheticArgumentAndField(syntheticEnclosingInstanceArgument);
                    }
                }
            }
        }
    }
    
    public void manageEnclosingInstanceAccessIfNecessary(final ClassScope currentScope, final FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x1) == 0x0) {
            final NestedTypeBinding nestedType = (NestedTypeBinding)this.binding;
            nestedType.addSyntheticArgumentAndField(this.binding.enclosingType());
        }
    }
    
    public final boolean needClassInitMethod() {
        if ((this.bits & 0x1) != 0x0) {
            return true;
        }
        switch (kind(this.modifiers)) {
            case 2:
            case 4: {
                return this.fields != null;
            }
            case 3: {
                return true;
            }
            default: {
                if (this.fields != null) {
                    int i = this.fields.length;
                    while (--i >= 0) {
                        final FieldDeclaration field = this.fields[i];
                        if ((field.modifiers & 0x8) != 0x0) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
    }
    
    public void parseMethods(final Parser parser, final CompilationUnitDeclaration unit) {
        if (unit.ignoreMethodBodies) {
            return;
        }
        if (this.memberTypes != null) {
            for (int length = this.memberTypes.length, i = 0; i < length; ++i) {
                final TypeDeclaration typeDeclaration = this.memberTypes[i];
                typeDeclaration.parseMethods(parser, unit);
                this.bits |= (typeDeclaration.bits & 0x80000);
            }
        }
        if (this.methods != null) {
            for (int length = this.methods.length, i = 0; i < length; ++i) {
                final AbstractMethodDeclaration abstractMethodDeclaration = this.methods[i];
                abstractMethodDeclaration.parseStatements(parser, unit);
                this.bits |= (abstractMethodDeclaration.bits & 0x80000);
            }
        }
        if (this.fields != null) {
            for (int length = this.fields.length, i = 0; i < length; ++i) {
                final FieldDeclaration fieldDeclaration = this.fields[i];
                switch (fieldDeclaration.getKind()) {
                    case 2: {
                        ((Initializer)fieldDeclaration).parseStatements(parser, this, unit);
                        this.bits |= (fieldDeclaration.bits & 0x80000);
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    public StringBuffer print(final int indent, final StringBuffer output) {
        if (this.javadoc != null) {
            this.javadoc.print(indent, output);
        }
        if ((this.bits & 0x200) == 0x0) {
            ASTNode.printIndent(indent, output);
            this.printHeader(0, output);
        }
        return this.printBody(indent, output);
    }
    
    public StringBuffer printBody(final int indent, final StringBuffer output) {
        output.append(" {");
        if (this.memberTypes != null) {
            for (int i = 0; i < this.memberTypes.length; ++i) {
                if (this.memberTypes[i] != null) {
                    output.append('\n');
                    this.memberTypes[i].print(indent + 1, output);
                }
            }
        }
        if (this.fields != null) {
            for (int fieldI = 0; fieldI < this.fields.length; ++fieldI) {
                if (this.fields[fieldI] != null) {
                    output.append('\n');
                    this.fields[fieldI].print(indent + 1, output);
                }
            }
        }
        if (this.methods != null) {
            for (int i = 0; i < this.methods.length; ++i) {
                if (this.methods[i] != null) {
                    output.append('\n');
                    this.methods[i].print(indent + 1, output);
                }
            }
        }
        output.append('\n');
        return ASTNode.printIndent(indent, output).append('}');
    }
    
    public StringBuffer printHeader(final int indent, final StringBuffer output) {
        ASTNode.printModifiers(this.modifiers, output);
        if (this.annotations != null) {
            ASTNode.printAnnotations(this.annotations, output);
            output.append(' ');
        }
        switch (kind(this.modifiers)) {
            case 1: {
                output.append("class ");
                break;
            }
            case 2: {
                output.append("interface ");
                break;
            }
            case 3: {
                output.append("enum ");
                break;
            }
            case 4: {
                output.append("@interface ");
                break;
            }
        }
        output.append(this.name);
        if (this.typeParameters != null) {
            output.append("<");
            for (int i = 0; i < this.typeParameters.length; ++i) {
                if (i > 0) {
                    output.append(", ");
                }
                this.typeParameters[i].print(0, output);
            }
            output.append(">");
        }
        if (this.superclass != null) {
            output.append(" extends ");
            this.superclass.print(0, output);
        }
        if (this.superInterfaces != null && this.superInterfaces.length > 0) {
            switch (kind(this.modifiers)) {
                case 1:
                case 3: {
                    output.append(" implements ");
                    break;
                }
                case 2:
                case 4: {
                    output.append(" extends ");
                    break;
                }
            }
            for (int i = 0; i < this.superInterfaces.length; ++i) {
                if (i > 0) {
                    output.append(", ");
                }
                this.superInterfaces[i].print(0, output);
            }
        }
        return output;
    }
    
    @Override
    public StringBuffer printStatement(final int tab, final StringBuffer output) {
        return this.print(tab, output);
    }
    
    public int record(final FunctionalExpression expression) {
        return this.functionalExpressionsCount++;
    }
    
    public void resolve() {
        final SourceTypeBinding sourceType = this.binding;
        if (sourceType == null) {
            this.ignoreFurtherInvestigation = true;
            return;
        }
        try {
            final long annotationTagBits = sourceType.getAnnotationTagBits();
            if ((annotationTagBits & 0x400000000000L) == 0x0L && (sourceType.modifiers & 0x100000) != 0x0 && this.scope.compilerOptions().sourceLevel >= 3211264L) {
                this.scope.problemReporter().missingDeprecatedAnnotationForType(this);
            }
            if ((annotationTagBits & 0x800000000000000L) != 0x0L && !this.binding.isFunctionalInterface(this.scope)) {
                this.scope.problemReporter().notAFunctionalInterface(this);
            }
            if ((this.bits & 0x8) != 0x0) {
                this.scope.problemReporter().undocumentedEmptyBlock(this.bodyStart - 1, this.bodyEnd);
            }
            boolean needSerialVersion = this.scope.compilerOptions().getSeverity(536870920) != 256 && sourceType.isClass() && sourceType.findSuperTypeOriginatingFrom(56, false) == null && sourceType.findSuperTypeOriginatingFrom(37, false) != null;
            if (needSerialVersion) {
                final CompilationUnitScope compilationUnitScope = this.scope.compilationUnitScope();
                MethodBinding methodBinding = sourceType.getExactMethod(TypeConstants.WRITEREPLACE, Binding.NO_TYPES, compilationUnitScope);
                ReferenceBinding[] throwsExceptions;
                needSerialVersion = (methodBinding == null || !methodBinding.isValidBinding() || methodBinding.returnType.id != 1 || (throwsExceptions = methodBinding.thrownExceptions).length != 1 || throwsExceptions[0].id != 57);
                if (needSerialVersion) {
                    boolean hasWriteObjectMethod = false;
                    boolean hasReadObjectMethod = false;
                    TypeBinding argumentTypeBinding = this.scope.getType(TypeConstants.JAVA_IO_OBJECTOUTPUTSTREAM, 3);
                    if (argumentTypeBinding.isValidBinding()) {
                        methodBinding = sourceType.getExactMethod(TypeConstants.WRITEOBJECT, new TypeBinding[] { argumentTypeBinding }, compilationUnitScope);
                        hasWriteObjectMethod = (methodBinding != null && methodBinding.isValidBinding() && methodBinding.modifiers == 2 && methodBinding.returnType == TypeBinding.VOID && (throwsExceptions = methodBinding.thrownExceptions).length == 1 && throwsExceptions[0].id == 58);
                    }
                    argumentTypeBinding = this.scope.getType(TypeConstants.JAVA_IO_OBJECTINPUTSTREAM, 3);
                    if (argumentTypeBinding.isValidBinding()) {
                        methodBinding = sourceType.getExactMethod(TypeConstants.READOBJECT, new TypeBinding[] { argumentTypeBinding }, compilationUnitScope);
                        hasReadObjectMethod = (methodBinding != null && methodBinding.isValidBinding() && methodBinding.modifiers == 2 && methodBinding.returnType == TypeBinding.VOID && (throwsExceptions = methodBinding.thrownExceptions).length == 1 && throwsExceptions[0].id == 58);
                    }
                    needSerialVersion = (!hasWriteObjectMethod || !hasReadObjectMethod);
                }
            }
            Label_0604: {
                if (sourceType.findSuperTypeOriginatingFrom(21, true) != null) {
                    ReferenceBinding current = sourceType;
                    while (!current.isGenericType()) {
                        if (current.isStatic()) {
                            break Label_0604;
                        }
                        if (current.isLocalType()) {
                            final NestedTypeBinding nestedType = (NestedTypeBinding)current.erasure();
                            if (nestedType.scope.methodScope().isStatic) {
                                break Label_0604;
                            }
                        }
                        if ((current = current.enclosingType()) == null) {
                            break Label_0604;
                        }
                    }
                    this.scope.problemReporter().genericTypeCannotExtendThrowable(this);
                }
            }
            int localMaxFieldCount = 0;
            int lastVisibleFieldID = -1;
            boolean hasEnumConstants = false;
            FieldDeclaration[] enumConstantsWithoutBody = null;
            if (this.memberTypes != null) {
                for (int i = 0, count = this.memberTypes.length; i < count; ++i) {
                    this.memberTypes[i].resolve(this.scope);
                }
            }
            if (this.fields != null) {
                for (int i = 0, count = this.fields.length; i < count; ++i) {
                    final FieldDeclaration field = this.fields[i];
                    switch (field.getKind()) {
                        case 3: {
                            hasEnumConstants = true;
                            if (!(field.initialization instanceof QualifiedAllocationExpression)) {
                                if (enumConstantsWithoutBody == null) {
                                    enumConstantsWithoutBody = new FieldDeclaration[count];
                                }
                                enumConstantsWithoutBody[i] = field;
                            }
                        }
                        case 1: {
                            final FieldBinding fieldBinding = field.binding;
                            if (fieldBinding == null) {
                                if (field.initialization != null) {
                                    field.initialization.resolve(field.isStatic() ? this.staticInitializerScope : this.initializerScope);
                                }
                                this.ignoreFurtherInvestigation = true;
                                continue;
                            }
                            if (needSerialVersion && (fieldBinding.modifiers & 0x18) == 0x18 && CharOperation.equals(TypeConstants.SERIALVERSIONUID, fieldBinding.name) && TypeBinding.equalsEquals(TypeBinding.LONG, fieldBinding.type)) {
                                needSerialVersion = false;
                            }
                            ++localMaxFieldCount;
                            lastVisibleFieldID = field.binding.id;
                            break;
                        }
                        case 2: {
                            ((Initializer)field).lastVisibleFieldID = lastVisibleFieldID + 1;
                            break;
                        }
                    }
                    field.resolve(field.isStatic() ? this.staticInitializerScope : this.initializerScope);
                }
            }
            if (this.maxFieldCount < localMaxFieldCount) {
                this.maxFieldCount = localMaxFieldCount;
            }
            if (needSerialVersion) {
                final TypeBinding javaxRmiCorbaStub = this.scope.getType(TypeConstants.JAVAX_RMI_CORBA_STUB, 4);
                if (javaxRmiCorbaStub.isValidBinding()) {
                    for (ReferenceBinding superclassBinding = this.binding.superclass; superclassBinding != null; superclassBinding = superclassBinding.superclass()) {
                        if (TypeBinding.equalsEquals(superclassBinding, javaxRmiCorbaStub)) {
                            needSerialVersion = false;
                            break;
                        }
                    }
                }
                if (needSerialVersion) {
                    this.scope.problemReporter().missingSerialVersion(this);
                }
            }
            switch (kind(this.modifiers)) {
                case 4: {
                    if (this.superclass != null) {
                        this.scope.problemReporter().annotationTypeDeclarationCannotHaveSuperclass(this);
                    }
                    if (this.superInterfaces != null) {
                        this.scope.problemReporter().annotationTypeDeclarationCannotHaveSuperinterfaces(this);
                        break;
                    }
                    break;
                }
                case 3: {
                    if (!this.binding.isAbstract()) {
                        break;
                    }
                    if (!hasEnumConstants) {
                        for (int i = 0, count = this.methods.length; i < count; ++i) {
                            final AbstractMethodDeclaration methodDeclaration = this.methods[i];
                            if (methodDeclaration.isAbstract() && methodDeclaration.binding != null) {
                                this.scope.problemReporter().enumAbstractMethodMustBeImplemented(methodDeclaration);
                            }
                        }
                        break;
                    }
                    if (enumConstantsWithoutBody != null) {
                        for (int i = 0, count = this.methods.length; i < count; ++i) {
                            final AbstractMethodDeclaration methodDeclaration = this.methods[i];
                            if (methodDeclaration.isAbstract() && methodDeclaration.binding != null) {
                                for (int f = 0, l = enumConstantsWithoutBody.length; f < l; ++f) {
                                    if (enumConstantsWithoutBody[f] != null) {
                                        this.scope.problemReporter().enumConstantMustImplementAbstractMethod(methodDeclaration, enumConstantsWithoutBody[f]);
                                    }
                                }
                            }
                        }
                        break;
                    }
                    break;
                }
            }
            final int missingAbstractMethodslength = (this.missingAbstractMethods == null) ? 0 : this.missingAbstractMethods.length;
            final int methodsLength = (this.methods == null) ? 0 : this.methods.length;
            if (methodsLength + missingAbstractMethodslength > 65535) {
                this.scope.problemReporter().tooManyMethods(this);
            }
            if (this.methods != null) {
                for (int j = 0, count2 = this.methods.length; j < count2; ++j) {
                    this.methods[j].resolve(this.scope);
                }
            }
            if (this.javadoc != null) {
                if (this.scope != null && this.name != TypeConstants.PACKAGE_INFO_NAME) {
                    this.javadoc.resolve(this.scope);
                }
            }
            else if (!sourceType.isLocalType()) {
                int visibility = sourceType.modifiers & 0x7;
                final ProblemReporter reporter = this.scope.problemReporter();
                final int severity = reporter.computeSeverity(-1610612250);
                if (severity != 256) {
                    if (this.enclosingType != null) {
                        visibility = Util.computeOuterMostVisibility(this.enclosingType, visibility);
                    }
                    final int javadocModifiers = (this.binding.modifiers & 0xFFFFFFF8) | visibility;
                    reporter.javadocMissing(this.sourceStart, this.sourceEnd, severity, javadocModifiers);
                }
            }
        }
        catch (final AbortType abortType) {
            this.ignoreFurtherInvestigation = true;
        }
    }
    
    @Override
    public void resolve(final BlockScope blockScope) {
        if ((this.bits & 0x200) == 0x0) {
            final Binding existing = blockScope.getType(this.name);
            if (existing instanceof ReferenceBinding && existing != this.binding && existing.isValidBinding()) {
                final ReferenceBinding existingType = (ReferenceBinding)existing;
                if (existingType instanceof TypeVariableBinding) {
                    blockScope.problemReporter().typeHiding(this, (TypeVariableBinding)existingType);
                    for (Scope outerScope = blockScope.parent; outerScope != null; outerScope = outerScope.parent) {
                        final Binding existing2 = outerScope.getType(this.name);
                        if (existing2 instanceof TypeVariableBinding && existing2.isValidBinding()) {
                            final TypeVariableBinding tvb = (TypeVariableBinding)existingType;
                            final Binding declaringElement = tvb.declaringElement;
                            if (declaringElement instanceof ReferenceBinding && CharOperation.equals(((ReferenceBinding)declaringElement).sourceName(), this.name)) {
                                blockScope.problemReporter().typeCollidesWithEnclosingType(this);
                                break;
                            }
                        }
                        else {
                            if (existing2 instanceof ReferenceBinding && existing2.isValidBinding() && outerScope.isDefinedInType((ReferenceBinding)existing2)) {
                                blockScope.problemReporter().typeCollidesWithEnclosingType(this);
                                break;
                            }
                            if (existing2 == null) {
                                break;
                            }
                        }
                    }
                }
                else if (existingType instanceof LocalTypeBinding && ((LocalTypeBinding)existingType).scope.methodScope() == blockScope.methodScope()) {
                    blockScope.problemReporter().duplicateNestedType(this);
                }
                else if (existingType instanceof LocalTypeBinding && blockScope.isLambdaSubscope() && blockScope.enclosingLambdaScope().enclosingMethodScope() == ((LocalTypeBinding)existingType).scope.methodScope()) {
                    blockScope.problemReporter().duplicateNestedType(this);
                }
                else if (blockScope.isDefinedInType(existingType)) {
                    blockScope.problemReporter().typeCollidesWithEnclosingType(this);
                }
                else if (blockScope.isDefinedInSameUnit(existingType)) {
                    blockScope.problemReporter().typeHiding(this, existingType);
                }
            }
            blockScope.addLocalType(this);
        }
        if (this.binding != null) {
            blockScope.referenceCompilationUnit().record((LocalTypeBinding)this.binding);
            this.resolve();
            this.updateMaxFieldCount();
        }
    }
    
    public void resolve(final ClassScope upperScope) {
        if (this.binding != null && this.binding instanceof LocalTypeBinding) {
            upperScope.referenceCompilationUnit().record((LocalTypeBinding)this.binding);
        }
        this.resolve();
        this.updateMaxFieldCount();
    }
    
    public void resolve(final CompilationUnitScope upperScope) {
        this.resolve();
        this.updateMaxFieldCount();
    }
    
    @Override
    public void tagAsHavingErrors() {
        this.ignoreFurtherInvestigation = true;
    }
    
    @Override
    public void tagAsHavingIgnoredMandatoryErrors(final int problemId) {
    }
    
    public void traverse(final ASTVisitor visitor, final CompilationUnitScope unitScope) {
        try {
            if (visitor.visit(this, unitScope)) {
                if (this.javadoc != null) {
                    this.javadoc.traverse(visitor, this.scope);
                }
                if (this.annotations != null) {
                    for (int annotationsLength = this.annotations.length, i = 0; i < annotationsLength; ++i) {
                        this.annotations[i].traverse(visitor, this.staticInitializerScope);
                    }
                }
                if (this.superclass != null) {
                    this.superclass.traverse(visitor, this.scope);
                }
                if (this.superInterfaces != null) {
                    for (int length = this.superInterfaces.length, i = 0; i < length; ++i) {
                        this.superInterfaces[i].traverse(visitor, this.scope);
                    }
                }
                if (this.typeParameters != null) {
                    for (int length = this.typeParameters.length, i = 0; i < length; ++i) {
                        this.typeParameters[i].traverse(visitor, this.scope);
                    }
                }
                if (this.memberTypes != null) {
                    for (int length = this.memberTypes.length, i = 0; i < length; ++i) {
                        this.memberTypes[i].traverse(visitor, this.scope);
                    }
                }
                if (this.fields != null) {
                    for (int length = this.fields.length, i = 0; i < length; ++i) {
                        final FieldDeclaration field;
                        if ((field = this.fields[i]).isStatic()) {
                            field.traverse(visitor, this.staticInitializerScope);
                        }
                        else {
                            field.traverse(visitor, this.initializerScope);
                        }
                    }
                }
                if (this.methods != null) {
                    for (int length = this.methods.length, i = 0; i < length; ++i) {
                        this.methods[i].traverse(visitor, this.scope);
                    }
                }
            }
            visitor.endVisit(this, unitScope);
        }
        catch (final AbortType abortType) {}
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        try {
            if (visitor.visit(this, blockScope)) {
                if (this.javadoc != null) {
                    this.javadoc.traverse(visitor, this.scope);
                }
                if (this.annotations != null) {
                    for (int annotationsLength = this.annotations.length, i = 0; i < annotationsLength; ++i) {
                        this.annotations[i].traverse(visitor, this.staticInitializerScope);
                    }
                }
                if (this.superclass != null) {
                    this.superclass.traverse(visitor, this.scope);
                }
                if (this.superInterfaces != null) {
                    for (int length = this.superInterfaces.length, i = 0; i < length; ++i) {
                        this.superInterfaces[i].traverse(visitor, this.scope);
                    }
                }
                if (this.typeParameters != null) {
                    for (int length = this.typeParameters.length, i = 0; i < length; ++i) {
                        this.typeParameters[i].traverse(visitor, this.scope);
                    }
                }
                if (this.memberTypes != null) {
                    for (int length = this.memberTypes.length, i = 0; i < length; ++i) {
                        this.memberTypes[i].traverse(visitor, this.scope);
                    }
                }
                if (this.fields != null) {
                    for (int length = this.fields.length, i = 0; i < length; ++i) {
                        final FieldDeclaration field = this.fields[i];
                        if (!field.isStatic() || field.isFinal()) {
                            field.traverse(visitor, this.initializerScope);
                        }
                    }
                }
                if (this.methods != null) {
                    for (int length = this.methods.length, i = 0; i < length; ++i) {
                        this.methods[i].traverse(visitor, this.scope);
                    }
                }
            }
            visitor.endVisit(this, blockScope);
        }
        catch (final AbortType abortType) {}
    }
    
    public void traverse(final ASTVisitor visitor, final ClassScope classScope) {
        try {
            if (visitor.visit(this, classScope)) {
                if (this.javadoc != null) {
                    this.javadoc.traverse(visitor, this.scope);
                }
                if (this.annotations != null) {
                    for (int annotationsLength = this.annotations.length, i = 0; i < annotationsLength; ++i) {
                        this.annotations[i].traverse(visitor, this.staticInitializerScope);
                    }
                }
                if (this.superclass != null) {
                    this.superclass.traverse(visitor, this.scope);
                }
                if (this.superInterfaces != null) {
                    for (int length = this.superInterfaces.length, i = 0; i < length; ++i) {
                        this.superInterfaces[i].traverse(visitor, this.scope);
                    }
                }
                if (this.typeParameters != null) {
                    for (int length = this.typeParameters.length, i = 0; i < length; ++i) {
                        this.typeParameters[i].traverse(visitor, this.scope);
                    }
                }
                if (this.memberTypes != null) {
                    for (int length = this.memberTypes.length, i = 0; i < length; ++i) {
                        this.memberTypes[i].traverse(visitor, this.scope);
                    }
                }
                if (this.fields != null) {
                    for (int length = this.fields.length, i = 0; i < length; ++i) {
                        final FieldDeclaration field;
                        if ((field = this.fields[i]).isStatic()) {
                            field.traverse(visitor, this.staticInitializerScope);
                        }
                        else {
                            field.traverse(visitor, this.initializerScope);
                        }
                    }
                }
                if (this.methods != null) {
                    for (int length = this.methods.length, i = 0; i < length; ++i) {
                        this.methods[i].traverse(visitor, this.scope);
                    }
                }
            }
            visitor.endVisit(this, classScope);
        }
        catch (final AbortType abortType) {}
    }
    
    void updateMaxFieldCount() {
        if (this.binding == null) {
            return;
        }
        final TypeDeclaration outerMostType = this.scope.outerMostClassScope().referenceType();
        if (this.maxFieldCount > outerMostType.maxFieldCount) {
            outerMostType.maxFieldCount = this.maxFieldCount;
        }
        else {
            this.maxFieldCount = outerMostType.maxFieldCount;
        }
    }
    
    public boolean isPackageInfo() {
        return CharOperation.equals(this.name, TypeConstants.PACKAGE_INFO_NAME);
    }
    
    public boolean isSecondary() {
        return (this.bits & 0x1000) != 0x0;
    }
}
