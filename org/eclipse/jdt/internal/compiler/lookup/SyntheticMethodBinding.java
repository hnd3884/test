package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;

public class SyntheticMethodBinding extends MethodBinding
{
    public FieldBinding targetReadField;
    public FieldBinding targetWriteField;
    public MethodBinding targetMethod;
    public TypeBinding targetEnumType;
    public LambdaExpression lambda;
    public ReferenceExpression serializableMethodRef;
    public int purpose;
    public int startIndex;
    public int endIndex;
    public static final int FieldReadAccess = 1;
    public static final int FieldWriteAccess = 2;
    public static final int SuperFieldReadAccess = 3;
    public static final int SuperFieldWriteAccess = 4;
    public static final int MethodAccess = 5;
    public static final int ConstructorAccess = 6;
    public static final int SuperMethodAccess = 7;
    public static final int BridgeMethod = 8;
    public static final int EnumValues = 9;
    public static final int EnumValueOf = 10;
    public static final int SwitchTable = 11;
    public static final int TooManyEnumsConstants = 12;
    public static final int LambdaMethod = 13;
    public static final int ArrayConstructor = 14;
    public static final int ArrayClone = 15;
    public static final int FactoryMethod = 16;
    public static final int DeserializeLambda = 17;
    public static final int SerializableMethodReference = 18;
    public int sourceStart;
    public int index;
    public int fakePaddedParameters;
    
    public SyntheticMethodBinding(final FieldBinding targetField, final boolean isReadAccess, final boolean isSuperAccess, final ReferenceBinding declaringClass) {
        this.sourceStart = 0;
        this.fakePaddedParameters = 0;
        this.modifiers = 4104;
        this.tagBits |= 0x600000000L;
        final SourceTypeBinding declaringSourceType = (SourceTypeBinding)declaringClass;
        final SyntheticMethodBinding[] knownAccessMethods = declaringSourceType.syntheticMethods();
        int methodId = (knownAccessMethods == null) ? 0 : knownAccessMethods.length;
        this.index = methodId;
        this.selector = CharOperation.concat(TypeConstants.SYNTHETIC_ACCESS_METHOD_PREFIX, String.valueOf(methodId).toCharArray());
        if (isReadAccess) {
            this.returnType = targetField.type;
            if (targetField.isStatic()) {
                this.parameters = Binding.NO_PARAMETERS;
            }
            else {
                (this.parameters = new TypeBinding[1])[0] = declaringSourceType;
            }
            this.targetReadField = targetField;
            this.purpose = (isSuperAccess ? 3 : 1);
        }
        else {
            this.returnType = TypeBinding.VOID;
            if (targetField.isStatic()) {
                (this.parameters = new TypeBinding[1])[0] = targetField.type;
            }
            else {
                (this.parameters = new TypeBinding[2])[0] = declaringSourceType;
                this.parameters[1] = targetField.type;
            }
            this.targetWriteField = targetField;
            this.purpose = (isSuperAccess ? 4 : 2);
        }
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
        this.declaringClass = declaringSourceType;
        boolean needRename;
        do {
            needRename = false;
            final MethodBinding[] methods = declaringSourceType.methods();
            Label_0444: {
                final long range;
                if ((range = ReferenceBinding.binarySearch(this.selector, methods)) >= 0L) {
                    final int paramCount = this.parameters.length;
                Label_0361:
                    for (int imethod = (int)range, end = (int)(range >> 32); imethod <= end; ++imethod) {
                        final MethodBinding method = methods[imethod];
                        if (method.parameters.length == paramCount) {
                            final TypeBinding[] toMatch = method.parameters;
                            for (int i = 0; i < paramCount; ++i) {
                                if (TypeBinding.notEquals(toMatch[i], this.parameters[i])) {
                                    continue Label_0361;
                                }
                            }
                            needRename = true;
                            break Label_0444;
                        }
                    }
                }
                if (knownAccessMethods != null) {
                    for (int j = 0, length = knownAccessMethods.length; j < length; ++j) {
                        if (knownAccessMethods[j] != null) {
                            if (CharOperation.equals(this.selector, knownAccessMethods[j].selector) && this.areParametersEqual(methods[j])) {
                                needRename = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (needRename) {
                this.setSelector(CharOperation.concat(TypeConstants.SYNTHETIC_ACCESS_METHOD_PREFIX, String.valueOf(++methodId).toCharArray()));
            }
        } while (needRename);
        final FieldDeclaration[] fieldDecls = declaringSourceType.scope.referenceContext.fields;
        if (fieldDecls != null) {
            for (int k = 0, max = fieldDecls.length; k < max; ++k) {
                if (fieldDecls[k].binding == targetField) {
                    this.sourceStart = fieldDecls[k].sourceStart;
                    return;
                }
            }
        }
        this.sourceStart = declaringSourceType.scope.referenceContext.sourceStart;
    }
    
    public SyntheticMethodBinding(final FieldBinding targetField, final ReferenceBinding declaringClass, final TypeBinding enumBinding, final char[] selector) {
        this.sourceStart = 0;
        this.fakePaddedParameters = 0;
        this.modifiers = ((declaringClass.isInterface() ? 1 : 0) | 0x8 | 0x1000);
        this.tagBits |= 0x600000000L;
        final SourceTypeBinding declaringSourceType = (SourceTypeBinding)declaringClass;
        final SyntheticMethodBinding[] knownAccessMethods = declaringSourceType.syntheticMethods();
        int methodId = (knownAccessMethods == null) ? 0 : knownAccessMethods.length;
        this.index = methodId;
        this.selector = selector;
        this.returnType = declaringSourceType.scope.createArrayType(TypeBinding.INT, 1);
        this.parameters = Binding.NO_PARAMETERS;
        this.targetReadField = targetField;
        this.targetEnumType = enumBinding;
        this.purpose = 11;
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
        this.declaringClass = declaringSourceType;
        if (declaringSourceType.isStrictfp()) {
            this.modifiers |= 0x800;
        }
        boolean needRename;
        do {
            needRename = false;
            final MethodBinding[] methods = declaringSourceType.methods();
            Label_0361: {
                final long range;
                if ((range = ReferenceBinding.binarySearch(this.selector, methods)) >= 0L) {
                    final int paramCount = this.parameters.length;
                Label_0278:
                    for (int imethod = (int)range, end = (int)(range >> 32); imethod <= end; ++imethod) {
                        final MethodBinding method = methods[imethod];
                        if (method.parameters.length == paramCount) {
                            final TypeBinding[] toMatch = method.parameters;
                            for (int i = 0; i < paramCount; ++i) {
                                if (TypeBinding.notEquals(toMatch[i], this.parameters[i])) {
                                    continue Label_0278;
                                }
                            }
                            needRename = true;
                            break Label_0361;
                        }
                    }
                }
                if (knownAccessMethods != null) {
                    for (int j = 0, length = knownAccessMethods.length; j < length; ++j) {
                        if (knownAccessMethods[j] != null) {
                            if (CharOperation.equals(this.selector, knownAccessMethods[j].selector) && this.areParametersEqual(methods[j])) {
                                needRename = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (needRename) {
                this.setSelector(CharOperation.concat(selector, String.valueOf(++methodId).toCharArray()));
            }
        } while (needRename);
        this.sourceStart = declaringSourceType.scope.referenceContext.sourceStart;
    }
    
    public SyntheticMethodBinding(final MethodBinding targetMethod, final boolean isSuperAccess, final ReferenceBinding declaringClass) {
        this.sourceStart = 0;
        this.fakePaddedParameters = 0;
        if (targetMethod.isConstructor()) {
            this.initializeConstructorAccessor(targetMethod);
        }
        else {
            this.initializeMethodAccessor(targetMethod, isSuperAccess, declaringClass);
        }
    }
    
    public SyntheticMethodBinding(final MethodBinding overridenMethodToBridge, final MethodBinding targetMethod, final SourceTypeBinding declaringClass) {
        this.sourceStart = 0;
        this.fakePaddedParameters = 0;
        this.declaringClass = declaringClass;
        this.selector = overridenMethodToBridge.selector;
        this.modifiers = ((targetMethod.modifiers | 0x40 | 0x1000) & 0xBFFFFACF);
        this.tagBits |= 0x600000000L;
        this.returnType = overridenMethodToBridge.returnType;
        this.parameters = overridenMethodToBridge.parameters;
        this.thrownExceptions = overridenMethodToBridge.thrownExceptions;
        this.targetMethod = targetMethod;
        this.purpose = 8;
        final SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
        final int methodId = (knownAccessMethods == null) ? 0 : knownAccessMethods.length;
        this.index = methodId;
    }
    
    public SyntheticMethodBinding(final SourceTypeBinding declaringEnum, final char[] selector) {
        this.sourceStart = 0;
        this.fakePaddedParameters = 0;
        this.declaringClass = declaringEnum;
        this.selector = selector;
        this.modifiers = 9;
        this.tagBits |= 0x600000000L;
        final LookupEnvironment environment = declaringEnum.scope.environment();
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
        if (selector == TypeConstants.VALUES) {
            this.returnType = environment.createArrayType(environment.convertToParameterizedType(declaringEnum), 1);
            this.parameters = Binding.NO_PARAMETERS;
            this.purpose = 9;
        }
        else if (selector == TypeConstants.VALUEOF) {
            this.returnType = environment.convertToParameterizedType(declaringEnum);
            this.parameters = new TypeBinding[] { declaringEnum.scope.getJavaLangString() };
            this.purpose = 10;
        }
        final SyntheticMethodBinding[] knownAccessMethods = ((SourceTypeBinding)this.declaringClass).syntheticMethods();
        final int methodId = (knownAccessMethods == null) ? 0 : knownAccessMethods.length;
        this.index = methodId;
        if (declaringEnum.isStrictfp()) {
            this.modifiers |= 0x800;
        }
    }
    
    public SyntheticMethodBinding(final SourceTypeBinding declaringClass) {
        this.sourceStart = 0;
        this.fakePaddedParameters = 0;
        this.declaringClass = declaringClass;
        this.selector = TypeConstants.DESERIALIZE_LAMBDA;
        this.modifiers = 4106;
        this.tagBits |= 0x600000000L;
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
        this.returnType = declaringClass.scope.getJavaLangObject();
        this.parameters = new TypeBinding[] { declaringClass.scope.getJavaLangInvokeSerializedLambda() };
        this.purpose = 17;
        final SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
        final int methodId = (knownAccessMethods == null) ? 0 : knownAccessMethods.length;
        this.index = methodId;
    }
    
    public SyntheticMethodBinding(final SourceTypeBinding declaringEnum, final int startIndex, final int endIndex) {
        this.sourceStart = 0;
        this.fakePaddedParameters = 0;
        this.declaringClass = declaringEnum;
        final SyntheticMethodBinding[] knownAccessMethods = declaringEnum.syntheticMethods();
        this.index = ((knownAccessMethods == null) ? 0 : knownAccessMethods.length);
        final StringBuffer buffer = new StringBuffer();
        buffer.append(TypeConstants.SYNTHETIC_ENUM_CONSTANT_INITIALIZATION_METHOD_PREFIX).append(this.index);
        this.selector = String.valueOf(buffer).toCharArray();
        this.modifiers = 10;
        this.tagBits |= 0x600000000L;
        this.purpose = 12;
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
        this.returnType = TypeBinding.VOID;
        this.parameters = Binding.NO_PARAMETERS;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }
    
    public SyntheticMethodBinding(final MethodBinding overridenMethodToBridge, final SourceTypeBinding declaringClass) {
        this.sourceStart = 0;
        this.fakePaddedParameters = 0;
        this.declaringClass = declaringClass;
        this.selector = overridenMethodToBridge.selector;
        this.modifiers = ((overridenMethodToBridge.modifiers | 0x40 | 0x1000) & 0xBFFFFACF);
        this.tagBits |= 0x600000000L;
        this.returnType = overridenMethodToBridge.returnType;
        this.parameters = overridenMethodToBridge.parameters;
        this.thrownExceptions = overridenMethodToBridge.thrownExceptions;
        this.targetMethod = overridenMethodToBridge;
        this.purpose = 7;
        final SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
        final int methodId = (knownAccessMethods == null) ? 0 : knownAccessMethods.length;
        this.index = methodId;
    }
    
    public SyntheticMethodBinding(final int purpose, final ArrayBinding arrayType, final char[] selector, final SourceTypeBinding declaringClass) {
        this.sourceStart = 0;
        this.fakePaddedParameters = 0;
        this.declaringClass = declaringClass;
        this.selector = selector;
        this.modifiers = 4106;
        this.tagBits |= 0x600000000L;
        this.returnType = arrayType;
        final LookupEnvironment environment = declaringClass.environment;
        if (environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
            if (environment.usesNullTypeAnnotations()) {
                this.returnType = environment.createAnnotatedType(this.returnType, new AnnotationBinding[] { environment.getNonNullAnnotation() });
            }
            else {
                this.tagBits |= 0x100000000000000L;
            }
        }
        this.parameters = new TypeBinding[] { (purpose == 14) ? TypeBinding.INT : arrayType };
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
        this.purpose = purpose;
        final SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
        final int methodId = (knownAccessMethods == null) ? 0 : knownAccessMethods.length;
        this.index = methodId;
    }
    
    public SyntheticMethodBinding(final LambdaExpression lambda, final char[] lambdaName, final SourceTypeBinding declaringClass) {
        this.sourceStart = 0;
        this.fakePaddedParameters = 0;
        this.lambda = lambda;
        this.declaringClass = declaringClass;
        this.selector = lambdaName;
        this.modifiers = lambda.binding.modifiers;
        this.tagBits |= (0x600000000L | (lambda.binding.tagBits & 0x400L));
        this.returnType = lambda.binding.returnType;
        this.parameters = lambda.binding.parameters;
        this.thrownExceptions = lambda.binding.thrownExceptions;
        this.purpose = 13;
        final SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
        final int methodId = (knownAccessMethods == null) ? 0 : knownAccessMethods.length;
        this.index = methodId;
    }
    
    public SyntheticMethodBinding(final ReferenceExpression ref, final SourceTypeBinding declaringClass) {
        this.sourceStart = 0;
        this.fakePaddedParameters = 0;
        this.serializableMethodRef = ref;
        this.declaringClass = declaringClass;
        this.selector = ref.binding.selector;
        this.modifiers = ref.binding.modifiers;
        this.tagBits |= (0x600000000L | (ref.binding.tagBits & 0x400L));
        this.returnType = ref.binding.returnType;
        this.parameters = ref.binding.parameters;
        this.thrownExceptions = ref.binding.thrownExceptions;
        this.purpose = 18;
        final SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
        final int methodId = (knownAccessMethods == null) ? 0 : knownAccessMethods.length;
        this.index = methodId;
    }
    
    public SyntheticMethodBinding(final MethodBinding privateConstructor, final MethodBinding publicConstructor, final char[] selector, final TypeBinding[] enclosingInstances, final SourceTypeBinding declaringClass) {
        this.sourceStart = 0;
        this.fakePaddedParameters = 0;
        this.declaringClass = declaringClass;
        this.selector = selector;
        this.modifiers = 4106;
        this.tagBits |= 0x600000000L;
        this.returnType = publicConstructor.declaringClass;
        final int realParametersLength = privateConstructor.parameters.length;
        final int enclosingInstancesLength = enclosingInstances.length;
        final int parametersLength = enclosingInstancesLength + realParametersLength;
        System.arraycopy(enclosingInstances, 0, this.parameters = new TypeBinding[parametersLength], 0, enclosingInstancesLength);
        System.arraycopy(privateConstructor.parameters, 0, this.parameters, enclosingInstancesLength, realParametersLength);
        this.fakePaddedParameters = publicConstructor.parameters.length - realParametersLength;
        this.thrownExceptions = publicConstructor.thrownExceptions;
        this.purpose = 16;
        this.targetMethod = publicConstructor;
        final SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
        final int methodId = (knownAccessMethods == null) ? 0 : knownAccessMethods.length;
        this.index = methodId;
    }
    
    public void initializeConstructorAccessor(final MethodBinding accessedConstructor) {
        this.targetMethod = accessedConstructor;
        this.modifiers = 4096;
        this.tagBits |= 0x600000000L;
        final SourceTypeBinding sourceType = (SourceTypeBinding)accessedConstructor.declaringClass;
        final SyntheticMethodBinding[] knownSyntheticMethods = sourceType.syntheticMethods();
        this.index = ((knownSyntheticMethods == null) ? 0 : knownSyntheticMethods.length);
        this.selector = accessedConstructor.selector;
        this.returnType = accessedConstructor.returnType;
        this.purpose = 6;
        final int parametersLength = accessedConstructor.parameters.length;
        this.parameters = new TypeBinding[parametersLength + 1];
        System.arraycopy(accessedConstructor.parameters, 0, this.parameters, 0, parametersLength);
        this.parameters[parametersLength] = accessedConstructor.declaringClass;
        this.thrownExceptions = accessedConstructor.thrownExceptions;
        this.declaringClass = sourceType;
        boolean needRename;
    Label_0310:
        do {
            needRename = false;
            final MethodBinding[] methods = sourceType.methods();
            int i = 0;
            int length = methods.length;
            while (true) {
                while (i < length) {
                    if (CharOperation.equals(this.selector, methods[i].selector) && this.areParameterErasuresEqual(methods[i])) {
                        needRename = true;
                        if (needRename) {
                            final int length2 = this.parameters.length;
                            System.arraycopy(this.parameters, 0, this.parameters = new TypeBinding[length2 + 1], 0, length2);
                            this.parameters[length2] = this.declaringClass;
                            continue Label_0310;
                        }
                        continue Label_0310;
                    }
                    else {
                        ++i;
                    }
                }
                if (knownSyntheticMethods != null) {
                    for (i = 0, length = knownSyntheticMethods.length; i < length; ++i) {
                        if (knownSyntheticMethods[i] != null) {
                            if (CharOperation.equals(this.selector, knownSyntheticMethods[i].selector) && this.areParameterErasuresEqual(knownSyntheticMethods[i])) {
                                needRename = true;
                                break;
                            }
                        }
                    }
                }
                continue;
            }
        } while (needRename);
        final AbstractMethodDeclaration[] methodDecls = sourceType.scope.referenceContext.methods;
        if (methodDecls != null) {
            for (int i = 0, length = methodDecls.length; i < length; ++i) {
                if (methodDecls[i].binding == accessedConstructor) {
                    this.sourceStart = methodDecls[i].sourceStart;
                    return;
                }
            }
        }
    }
    
    public void initializeMethodAccessor(final MethodBinding accessedMethod, final boolean isSuperAccess, final ReferenceBinding receiverType) {
        this.targetMethod = accessedMethod;
        if (isSuperAccess && receiverType.isInterface() && !accessedMethod.isStatic()) {
            this.modifiers = 4098;
        }
        else {
            this.modifiers = 4104;
        }
        this.tagBits |= 0x600000000L;
        final SourceTypeBinding declaringSourceType = (SourceTypeBinding)receiverType;
        final SyntheticMethodBinding[] knownAccessMethods = declaringSourceType.syntheticMethods();
        int methodId = (knownAccessMethods == null) ? 0 : knownAccessMethods.length;
        this.index = methodId;
        this.selector = CharOperation.concat(TypeConstants.SYNTHETIC_ACCESS_METHOD_PREFIX, String.valueOf(methodId).toCharArray());
        this.returnType = accessedMethod.returnType;
        this.purpose = (isSuperAccess ? 7 : 5);
        if (accessedMethod.isStatic() || (isSuperAccess && receiverType.isInterface())) {
            this.parameters = accessedMethod.parameters;
        }
        else {
            (this.parameters = new TypeBinding[accessedMethod.parameters.length + 1])[0] = declaringSourceType;
            System.arraycopy(accessedMethod.parameters, 0, this.parameters, 1, accessedMethod.parameters.length);
        }
        this.thrownExceptions = accessedMethod.thrownExceptions;
        this.declaringClass = declaringSourceType;
        boolean needRename;
    Label_0374:
        do {
            needRename = false;
            final MethodBinding[] methods = declaringSourceType.methods();
            int i = 0;
            int length = methods.length;
            while (true) {
                while (i < length) {
                    if (CharOperation.equals(this.selector, methods[i].selector) && this.areParameterErasuresEqual(methods[i])) {
                        needRename = true;
                        if (needRename) {
                            this.setSelector(CharOperation.concat(TypeConstants.SYNTHETIC_ACCESS_METHOD_PREFIX, String.valueOf(++methodId).toCharArray()));
                            continue Label_0374;
                        }
                        continue Label_0374;
                    }
                    else {
                        ++i;
                    }
                }
                if (knownAccessMethods != null) {
                    for (i = 0, length = knownAccessMethods.length; i < length; ++i) {
                        if (knownAccessMethods[i] != null) {
                            if (CharOperation.equals(this.selector, knownAccessMethods[i].selector) && this.areParameterErasuresEqual(knownAccessMethods[i])) {
                                needRename = true;
                                break;
                            }
                        }
                    }
                }
                continue;
            }
        } while (needRename);
        final AbstractMethodDeclaration[] methodDecls = declaringSourceType.scope.referenceContext.methods;
        if (methodDecls != null) {
            for (int i = 0, length = methodDecls.length; i < length; ++i) {
                if (methodDecls[i].binding == accessedMethod) {
                    this.sourceStart = methodDecls[i].sourceStart;
                    return;
                }
            }
        }
    }
    
    protected boolean isConstructorRelated() {
        return this.purpose == 6;
    }
    
    @Override
    public LambdaExpression sourceLambda() {
        return this.lambda;
    }
    
    public void markNonNull(final LookupEnvironment environment) {
        markNonNull(this, this.purpose, environment);
    }
    
    static void markNonNull(final MethodBinding method, final int purpose, final LookupEnvironment environment) {
        switch (purpose) {
            case 9: {
                if (environment.usesNullTypeAnnotations()) {
                    TypeBinding elementType = ((ArrayBinding)method.returnType).leafComponentType();
                    final AnnotationBinding nonNullAnnotation = environment.getNonNullAnnotation();
                    elementType = environment.createAnnotatedType(elementType, new AnnotationBinding[] { environment.getNonNullAnnotation() });
                    method.returnType = environment.createArrayType(elementType, 1, new AnnotationBinding[] { nonNullAnnotation, null });
                }
                else {
                    method.tagBits |= 0x100000000000000L;
                }
                return;
            }
            case 10: {
                if (environment.usesNullTypeAnnotations()) {
                    method.returnType = environment.createAnnotatedType(method.returnType, new AnnotationBinding[] { environment.getNonNullAnnotation() });
                }
                else {
                    method.tagBits |= 0x100000000000000L;
                }
            }
            default: {}
        }
    }
}
