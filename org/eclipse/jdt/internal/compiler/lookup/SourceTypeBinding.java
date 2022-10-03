package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.util.Util;
import java.util.Iterator;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.classfmt.ExternalAnnotationProvider;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import java.util.HashMap;

public class SourceTypeBinding extends ReferenceBinding
{
    public ReferenceBinding superclass;
    public ReferenceBinding[] superInterfaces;
    private FieldBinding[] fields;
    private MethodBinding[] methods;
    public ReferenceBinding[] memberTypes;
    public TypeVariableBinding[] typeVariables;
    public ClassScope scope;
    protected SourceTypeBinding prototype;
    LookupEnvironment environment;
    private static final int METHOD_EMUL = 0;
    private static final int FIELD_EMUL = 1;
    private static final int CLASS_LITERAL_EMUL = 2;
    private static final int MAX_SYNTHETICS = 3;
    HashMap[] synthetics;
    char[] genericReferenceTypeSignature;
    private SimpleLookupTable storedAnnotations;
    public int defaultNullness;
    private int nullnessDefaultInitialized;
    private int lambdaOrdinal;
    private ReferenceBinding containerAnnotationType;
    public ExternalAnnotationProvider externalAnnotationProvider;
    
    public SourceTypeBinding(final char[][] compoundName, final PackageBinding fPackage, final ClassScope scope) {
        this.storedAnnotations = null;
        this.nullnessDefaultInitialized = 0;
        this.lambdaOrdinal = 0;
        this.containerAnnotationType = null;
        this.compoundName = compoundName;
        this.fPackage = fPackage;
        this.fileName = scope.referenceCompilationUnit().getFileName();
        this.modifiers = scope.referenceContext.modifiers;
        this.sourceName = scope.referenceContext.name;
        this.scope = scope;
        this.environment = scope.environment();
        this.fields = Binding.UNINITIALIZED_FIELDS;
        this.methods = Binding.UNINITIALIZED_METHODS;
        (this.prototype = this).computeId();
    }
    
    public SourceTypeBinding(final SourceTypeBinding prototype) {
        super(prototype);
        this.storedAnnotations = null;
        this.nullnessDefaultInitialized = 0;
        this.lambdaOrdinal = 0;
        this.containerAnnotationType = null;
        this.prototype = prototype.prototype;
        final SourceTypeBinding prototype2 = this.prototype;
        prototype2.tagBits |= 0x800000L;
        this.tagBits &= 0xFFFFFFFFFF7FFFFFL;
        this.superclass = prototype.superclass;
        this.superInterfaces = prototype.superInterfaces;
        this.fields = prototype.fields;
        this.methods = prototype.methods;
        this.memberTypes = prototype.memberTypes;
        this.typeVariables = prototype.typeVariables;
        this.environment = prototype.environment;
        this.synthetics = prototype.synthetics;
        this.genericReferenceTypeSignature = prototype.genericReferenceTypeSignature;
        this.storedAnnotations = prototype.storedAnnotations;
        this.defaultNullness = prototype.defaultNullness;
        this.nullnessDefaultInitialized = prototype.nullnessDefaultInitialized;
        this.lambdaOrdinal = prototype.lambdaOrdinal;
        this.containerAnnotationType = prototype.containerAnnotationType;
        this.tagBits |= 0x10000000L;
    }
    
    private void addDefaultAbstractMethods() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if ((this.tagBits & 0x400L) != 0x0L) {
            return;
        }
        this.tagBits |= 0x400L;
        if (this.isClass() && this.isAbstract()) {
            if (this.scope.compilerOptions().targetJDK >= 3014656L) {
                return;
            }
            ReferenceBinding[] itsInterfaces = this.superInterfaces();
            if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
                MethodBinding[] defaultAbstracts = null;
                int defaultAbstractsCount = 0;
                ReferenceBinding[] interfacesToVisit = itsInterfaces;
                for (int nextPosition = interfacesToVisit.length, i = 0; i < nextPosition; ++i) {
                    final ReferenceBinding superType = interfacesToVisit[i];
                    if (superType.isValidBinding()) {
                        final MethodBinding[] superMethods = superType.methods();
                        int m = superMethods.length;
                    Label_0285:
                        while (--m >= 0) {
                            final MethodBinding method = superMethods[m];
                            if (this.implementsMethod(method)) {
                                continue;
                            }
                            if (defaultAbstractsCount == 0) {
                                defaultAbstracts = new MethodBinding[5];
                            }
                            else {
                                for (final MethodBinding alreadyAdded : defaultAbstracts) {
                                    if (CharOperation.equals(alreadyAdded.selector, method.selector) && alreadyAdded.areParametersEqual(method)) {
                                        continue Label_0285;
                                    }
                                }
                            }
                            final MethodBinding defaultAbstract = new MethodBinding(method.modifiers | 0x80000 | 0x1000, method.selector, method.returnType, method.parameters, method.thrownExceptions, this);
                            if (defaultAbstractsCount == defaultAbstracts.length) {
                                System.arraycopy(defaultAbstracts, 0, defaultAbstracts = new MethodBinding[2 * defaultAbstractsCount], 0, defaultAbstractsCount);
                            }
                            defaultAbstracts[defaultAbstractsCount++] = defaultAbstract;
                        }
                        if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES) {
                            final int itsLength = itsInterfaces.length;
                            if (nextPosition + itsLength >= interfacesToVisit.length) {
                                System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
                            }
                            int a = 0;
                            Label_0400:
                            while (a < itsLength) {
                                final ReferenceBinding next = itsInterfaces[a];
                                while (true) {
                                    for (int b = 0; b < nextPosition; ++b) {
                                        if (TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                            ++a;
                                            continue Label_0400;
                                        }
                                    }
                                    interfacesToVisit[nextPosition++] = next;
                                    continue;
                                }
                            }
                        }
                    }
                }
                if (defaultAbstractsCount > 0) {
                    int length = this.methods.length;
                    System.arraycopy(this.methods, 0, this.setMethods(new MethodBinding[length + defaultAbstractsCount]), 0, length);
                    System.arraycopy(defaultAbstracts, 0, this.methods, length, defaultAbstractsCount);
                    length += defaultAbstractsCount;
                    if (length > 1) {
                        ReferenceBinding.sortMethods(this.methods, 0, length);
                    }
                }
            }
        }
    }
    
    public FieldBinding addSyntheticFieldForInnerclass(final LocalVariableBinding actualOuterLocalVariable) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[1] == null) {
            this.synthetics[1] = new HashMap(5);
        }
        FieldBinding synthField = this.synthetics[1].get(actualOuterLocalVariable);
        if (synthField == null) {
            synthField = new SyntheticFieldBinding(CharOperation.concat(TypeConstants.SYNTHETIC_OUTER_LOCAL_PREFIX, actualOuterLocalVariable.name), actualOuterLocalVariable.type, 4114, this, Constant.NotAConstant, this.synthetics[1].size());
            this.synthetics[1].put(actualOuterLocalVariable, synthField);
        }
        int index = 1;
        boolean needRecheck;
        do {
            needRecheck = false;
            final FieldBinding existingField;
            if ((existingField = this.getField(synthField.name, true)) != null) {
                final TypeDeclaration typeDecl = this.scope.referenceContext;
                final FieldDeclaration[] fieldDeclarations = typeDecl.fields;
                for (int max = (fieldDeclarations == null) ? 0 : fieldDeclarations.length, i = 0; i < max; ++i) {
                    final FieldDeclaration fieldDecl = fieldDeclarations[i];
                    if (fieldDecl.binding == existingField) {
                        synthField.name = CharOperation.concat(TypeConstants.SYNTHETIC_OUTER_LOCAL_PREFIX, actualOuterLocalVariable.name, ("$" + String.valueOf(index++)).toCharArray());
                        needRecheck = true;
                        break;
                    }
                }
            }
        } while (needRecheck);
        return synthField;
    }
    
    public FieldBinding addSyntheticFieldForInnerclass(final ReferenceBinding enclosingType) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[1] == null) {
            this.synthetics[1] = new HashMap(5);
        }
        FieldBinding synthField = this.synthetics[1].get(enclosingType);
        if (synthField == null) {
            synthField = new SyntheticFieldBinding(CharOperation.concat(TypeConstants.SYNTHETIC_ENCLOSING_INSTANCE_PREFIX, String.valueOf(enclosingType.depth()).toCharArray()), enclosingType, 4112, this, Constant.NotAConstant, this.synthetics[1].size());
            this.synthetics[1].put(enclosingType, synthField);
        }
        boolean needRecheck;
        do {
            needRecheck = false;
            final FieldBinding existingField;
            if ((existingField = this.getField(synthField.name, true)) != null) {
                final TypeDeclaration typeDecl = this.scope.referenceContext;
                final FieldDeclaration[] fieldDeclarations = typeDecl.fields;
                final int max = (fieldDeclarations == null) ? 0 : fieldDeclarations.length;
                int i = 0;
                while (i < max) {
                    final FieldDeclaration fieldDecl = fieldDeclarations[i];
                    if (fieldDecl.binding == existingField) {
                        if (this.scope.compilerOptions().complianceLevel >= 3211264L) {
                            synthField.name = CharOperation.concat(synthField.name, "$".toCharArray());
                            needRecheck = true;
                            break;
                        }
                        this.scope.problemReporter().duplicateFieldInType(this, fieldDecl);
                        break;
                    }
                    else {
                        ++i;
                    }
                }
            }
        } while (needRecheck);
        return synthField;
    }
    
    public FieldBinding addSyntheticFieldForClassLiteral(final TypeBinding targetType, final BlockScope blockScope) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[2] == null) {
            this.synthetics[2] = new HashMap(5);
        }
        FieldBinding synthField = this.synthetics[2].get(targetType);
        if (synthField == null) {
            synthField = new SyntheticFieldBinding(CharOperation.concat(TypeConstants.SYNTHETIC_CLASS, String.valueOf(this.synthetics[2].size()).toCharArray()), blockScope.getJavaLangClass(), 4104, this, Constant.NotAConstant, this.synthetics[2].size());
            this.synthetics[2].put(targetType, synthField);
        }
        final FieldBinding existingField;
        if ((existingField = this.getField(synthField.name, true)) != null) {
            final TypeDeclaration typeDecl = blockScope.referenceType();
            final FieldDeclaration[] typeDeclarationFields = typeDecl.fields;
            for (int max = (typeDeclarationFields == null) ? 0 : typeDeclarationFields.length, i = 0; i < max; ++i) {
                final FieldDeclaration fieldDecl = typeDeclarationFields[i];
                if (fieldDecl.binding == existingField) {
                    blockScope.problemReporter().duplicateFieldInType(this, fieldDecl);
                    break;
                }
            }
        }
        return synthField;
    }
    
    public FieldBinding addSyntheticFieldForAssert(final BlockScope blockScope) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[1] == null) {
            this.synthetics[1] = new HashMap(5);
        }
        FieldBinding synthField = this.synthetics[1].get("assertionEmulation");
        if (synthField == null) {
            synthField = new SyntheticFieldBinding(TypeConstants.SYNTHETIC_ASSERT_DISABLED, TypeBinding.BOOLEAN, (this.isInterface() ? 1 : 0) | 0x8 | 0x1000 | 0x10, this, Constant.NotAConstant, this.synthetics[1].size());
            this.synthetics[1].put("assertionEmulation", synthField);
        }
        int index = 0;
        boolean needRecheck;
        do {
            needRecheck = false;
            final FieldBinding existingField;
            if ((existingField = this.getField(synthField.name, true)) != null) {
                final TypeDeclaration typeDecl = this.scope.referenceContext;
                for (int max = (typeDecl.fields == null) ? 0 : typeDecl.fields.length, i = 0; i < max; ++i) {
                    final FieldDeclaration fieldDecl = typeDecl.fields[i];
                    if (fieldDecl.binding == existingField) {
                        synthField.name = CharOperation.concat(TypeConstants.SYNTHETIC_ASSERT_DISABLED, ("_" + String.valueOf(index++)).toCharArray());
                        needRecheck = true;
                        break;
                    }
                }
            }
        } while (needRecheck);
        return synthField;
    }
    
    public FieldBinding addSyntheticFieldForEnumValues() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[1] == null) {
            this.synthetics[1] = new HashMap(5);
        }
        FieldBinding synthField = this.synthetics[1].get("enumConstantValues");
        if (synthField == null) {
            synthField = new SyntheticFieldBinding(TypeConstants.SYNTHETIC_ENUM_VALUES, this.scope.createArrayType(this, 1), 4122, this, Constant.NotAConstant, this.synthetics[1].size());
            this.synthetics[1].put("enumConstantValues", synthField);
        }
        int index = 0;
        boolean needRecheck;
        do {
            needRecheck = false;
            final FieldBinding existingField;
            if ((existingField = this.getField(synthField.name, true)) != null) {
                final TypeDeclaration typeDecl = this.scope.referenceContext;
                final FieldDeclaration[] fieldDeclarations = typeDecl.fields;
                for (int max = (fieldDeclarations == null) ? 0 : fieldDeclarations.length, i = 0; i < max; ++i) {
                    final FieldDeclaration fieldDecl = fieldDeclarations[i];
                    if (fieldDecl.binding == existingField) {
                        synthField.name = CharOperation.concat(TypeConstants.SYNTHETIC_ENUM_VALUES, ("_" + String.valueOf(index++)).toCharArray());
                        needRecheck = true;
                        break;
                    }
                }
            }
        } while (needRecheck);
        return synthField;
    }
    
    public SyntheticMethodBinding addSyntheticMethod(final FieldBinding targetField, final boolean isReadAccess, final boolean isSuperAccess) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding accessMethod = null;
        SyntheticMethodBinding[] accessors = this.synthetics[0].get(targetField);
        if (accessors == null) {
            accessMethod = new SyntheticMethodBinding(targetField, isReadAccess, isSuperAccess, this);
            this.synthetics[0].put(targetField, accessors = new SyntheticMethodBinding[2]);
            accessors[!isReadAccess] = accessMethod;
        }
        else if ((accessMethod = accessors[!isReadAccess]) == null) {
            accessMethod = new SyntheticMethodBinding(targetField, isReadAccess, isSuperAccess, this);
            accessors[!isReadAccess] = accessMethod;
        }
        return accessMethod;
    }
    
    public SyntheticMethodBinding addSyntheticEnumMethod(final char[] selector) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding accessMethod = null;
        SyntheticMethodBinding[] accessors = this.synthetics[0].get(selector);
        if (accessors == null) {
            accessMethod = new SyntheticMethodBinding(this, selector);
            this.synthetics[0].put(selector, accessors = new SyntheticMethodBinding[2]);
            accessors[0] = accessMethod;
        }
        else if ((accessMethod = accessors[0]) == null) {
            accessMethod = new SyntheticMethodBinding(this, selector);
            accessors[0] = accessMethod;
        }
        return accessMethod;
    }
    
    public SyntheticFieldBinding addSyntheticFieldForSwitchEnum(final char[] fieldName, final String key) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[1] == null) {
            this.synthetics[1] = new HashMap(5);
        }
        SyntheticFieldBinding synthField = this.synthetics[1].get(key);
        if (synthField == null) {
            synthField = new SyntheticFieldBinding(fieldName, this.scope.createArrayType(TypeBinding.INT, 1), (this.isInterface() ? 17 : 2) | 0x8 | 0x1000, this, Constant.NotAConstant, this.synthetics[1].size());
            this.synthetics[1].put(key, synthField);
        }
        int index = 0;
        boolean needRecheck;
        do {
            needRecheck = false;
            final FieldBinding existingField;
            if ((existingField = this.getField(synthField.name, true)) != null) {
                final TypeDeclaration typeDecl = this.scope.referenceContext;
                final FieldDeclaration[] fieldDeclarations = typeDecl.fields;
                for (int max = (fieldDeclarations == null) ? 0 : fieldDeclarations.length, i = 0; i < max; ++i) {
                    final FieldDeclaration fieldDecl = fieldDeclarations[i];
                    if (fieldDecl.binding == existingField) {
                        synthField.name = CharOperation.concat(fieldName, ("_" + String.valueOf(index++)).toCharArray());
                        needRecheck = true;
                        break;
                    }
                }
            }
        } while (needRecheck);
        return synthField;
    }
    
    public SyntheticMethodBinding addSyntheticMethodForSwitchEnum(final TypeBinding enumBinding) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding accessMethod = null;
        final char[] selector = CharOperation.concat(TypeConstants.SYNTHETIC_SWITCH_ENUM_TABLE, enumBinding.constantPoolName());
        CharOperation.replace(selector, '/', '$');
        final String key = new String(selector);
        SyntheticMethodBinding[] accessors = this.synthetics[0].get(key);
        if (accessors == null) {
            final SyntheticFieldBinding fieldBinding = this.addSyntheticFieldForSwitchEnum(selector, key);
            accessMethod = new SyntheticMethodBinding(fieldBinding, this, enumBinding, selector);
            this.synthetics[0].put(key, accessors = new SyntheticMethodBinding[2]);
            accessors[0] = accessMethod;
        }
        else if ((accessMethod = accessors[0]) == null) {
            final SyntheticFieldBinding fieldBinding = this.addSyntheticFieldForSwitchEnum(selector, key);
            accessMethod = new SyntheticMethodBinding(fieldBinding, this, enumBinding, selector);
            accessors[0] = accessMethod;
        }
        return accessMethod;
    }
    
    public SyntheticMethodBinding addSyntheticMethodForEnumInitialization(final int begin, final int end) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        final SyntheticMethodBinding accessMethod = new SyntheticMethodBinding(this, begin, end);
        final SyntheticMethodBinding[] accessors = new SyntheticMethodBinding[2];
        this.synthetics[0].put(accessMethod.selector, accessors);
        return accessors[0] = accessMethod;
    }
    
    public SyntheticMethodBinding addSyntheticMethod(final LambdaExpression lambda) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding lambdaMethod = null;
        SyntheticMethodBinding[] lambdaMethods = this.synthetics[0].get(lambda);
        if (lambdaMethods == null) {
            lambdaMethod = new SyntheticMethodBinding(lambda, CharOperation.concat(TypeConstants.ANONYMOUS_METHOD, Integer.toString(this.lambdaOrdinal++).toCharArray()), this);
            this.synthetics[0].put(lambda, lambdaMethods = new SyntheticMethodBinding[] { null });
            lambdaMethods[0] = lambdaMethod;
        }
        else {
            lambdaMethod = lambdaMethods[0];
        }
        if (lambda.isSerializable) {
            this.addDeserializeLambdaMethod();
        }
        return lambdaMethod;
    }
    
    public SyntheticMethodBinding addSyntheticMethod(final ReferenceExpression ref) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (!ref.isSerializable) {
            return null;
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding lambdaMethod = null;
        SyntheticMethodBinding[] lambdaMethods = this.synthetics[0].get(ref);
        if (lambdaMethods == null) {
            lambdaMethod = new SyntheticMethodBinding(ref, this);
            this.synthetics[0].put(ref, lambdaMethods = new SyntheticMethodBinding[] { null });
            lambdaMethods[0] = lambdaMethod;
        }
        else {
            lambdaMethod = lambdaMethods[0];
        }
        this.addDeserializeLambdaMethod();
        return lambdaMethod;
    }
    
    private void addDeserializeLambdaMethod() {
        SyntheticMethodBinding[] deserializeLambdaMethods = this.synthetics[0].get(TypeConstants.DESERIALIZE_LAMBDA);
        if (deserializeLambdaMethods == null) {
            final SyntheticMethodBinding deserializeLambdaMethod = new SyntheticMethodBinding(this);
            this.synthetics[0].put(TypeConstants.DESERIALIZE_LAMBDA, deserializeLambdaMethods = new SyntheticMethodBinding[] { null });
            deserializeLambdaMethods[0] = deserializeLambdaMethod;
        }
    }
    
    public SyntheticMethodBinding addSyntheticMethod(final MethodBinding targetMethod, final boolean isSuperAccess) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding accessMethod = null;
        SyntheticMethodBinding[] accessors = this.synthetics[0].get(targetMethod);
        if (accessors == null) {
            accessMethod = new SyntheticMethodBinding(targetMethod, isSuperAccess, this);
            this.synthetics[0].put(targetMethod, accessors = new SyntheticMethodBinding[2]);
            accessors[!isSuperAccess] = accessMethod;
        }
        else if ((accessMethod = accessors[!isSuperAccess]) == null) {
            accessMethod = new SyntheticMethodBinding(targetMethod, isSuperAccess, this);
            accessors[!isSuperAccess] = accessMethod;
        }
        if (targetMethod.declaringClass.isStatic()) {
            if ((targetMethod.isConstructor() && targetMethod.parameters.length >= 254) || targetMethod.parameters.length >= 255) {
                this.scope.problemReporter().tooManyParametersForSyntheticMethod(targetMethod.sourceMethod());
            }
        }
        else if ((targetMethod.isConstructor() && targetMethod.parameters.length >= 253) || targetMethod.parameters.length >= 254) {
            this.scope.problemReporter().tooManyParametersForSyntheticMethod(targetMethod.sourceMethod());
        }
        return accessMethod;
    }
    
    public SyntheticMethodBinding addSyntheticArrayMethod(final ArrayBinding arrayType, final int purpose) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding arrayMethod = null;
        SyntheticMethodBinding[] arrayMethods = this.synthetics[0].get(arrayType);
        if (arrayMethods == null) {
            final char[] selector = CharOperation.concat(TypeConstants.ANONYMOUS_METHOD, Integer.toString(this.lambdaOrdinal++).toCharArray());
            arrayMethod = new SyntheticMethodBinding(purpose, arrayType, selector, this);
            this.synthetics[0].put(arrayType, arrayMethods = new SyntheticMethodBinding[2]);
            arrayMethods[purpose != 14] = arrayMethod;
        }
        else if ((arrayMethod = arrayMethods[purpose != 14]) == null) {
            final char[] selector = CharOperation.concat(TypeConstants.ANONYMOUS_METHOD, Integer.toString(this.lambdaOrdinal++).toCharArray());
            arrayMethod = new SyntheticMethodBinding(purpose, arrayType, selector, this);
            arrayMethods[purpose != 14] = arrayMethod;
        }
        return arrayMethod;
    }
    
    public SyntheticMethodBinding addSyntheticFactoryMethod(final MethodBinding privateConstructor, final MethodBinding publicConstructor, final TypeBinding[] enclosingInstances) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        final char[] selector = CharOperation.concat(TypeConstants.ANONYMOUS_METHOD, Integer.toString(this.lambdaOrdinal++).toCharArray());
        final SyntheticMethodBinding factory = new SyntheticMethodBinding(privateConstructor, publicConstructor, selector, enclosingInstances, this);
        this.synthetics[0].put(selector, new SyntheticMethodBinding[] { factory });
        return factory;
    }
    
    public SyntheticMethodBinding addSyntheticBridgeMethod(final MethodBinding inheritedMethodToBridge, final MethodBinding targetMethod) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.isInterface() && this.scope.compilerOptions().sourceLevel <= 3342336L) {
            return null;
        }
        if (TypeBinding.equalsEquals(inheritedMethodToBridge.returnType.erasure(), targetMethod.returnType.erasure()) && inheritedMethodToBridge.areParameterErasuresEqual(targetMethod)) {
            return null;
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        else {
            for (final Object synthetic : this.synthetics[0].keySet()) {
                if (synthetic instanceof MethodBinding) {
                    final MethodBinding method = (MethodBinding)synthetic;
                    if (CharOperation.equals(inheritedMethodToBridge.selector, method.selector) && TypeBinding.equalsEquals(inheritedMethodToBridge.returnType.erasure(), method.returnType.erasure()) && inheritedMethodToBridge.areParameterErasuresEqual(method)) {
                        return null;
                    }
                    continue;
                }
            }
        }
        SyntheticMethodBinding accessMethod = null;
        SyntheticMethodBinding[] accessors = this.synthetics[0].get(inheritedMethodToBridge);
        if (accessors == null) {
            accessMethod = new SyntheticMethodBinding(inheritedMethodToBridge, targetMethod, this);
            this.synthetics[0].put(inheritedMethodToBridge, accessors = new SyntheticMethodBinding[2]);
            accessors[1] = accessMethod;
        }
        else if ((accessMethod = accessors[1]) == null) {
            accessMethod = new SyntheticMethodBinding(inheritedMethodToBridge, targetMethod, this);
            accessors[1] = accessMethod;
        }
        return accessMethod;
    }
    
    public SyntheticMethodBinding addSyntheticBridgeMethod(final MethodBinding inheritedMethodToBridge) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.scope.compilerOptions().complianceLevel <= 3211264L) {
            return null;
        }
        if (this.isInterface() && !inheritedMethodToBridge.isDefaultMethod()) {
            return null;
        }
        if (inheritedMethodToBridge.isAbstract() || inheritedMethodToBridge.isFinal() || inheritedMethodToBridge.isStatic()) {
            return null;
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        else {
            for (final Object synthetic : this.synthetics[0].keySet()) {
                if (synthetic instanceof MethodBinding) {
                    final MethodBinding method = (MethodBinding)synthetic;
                    if (CharOperation.equals(inheritedMethodToBridge.selector, method.selector) && TypeBinding.equalsEquals(inheritedMethodToBridge.returnType.erasure(), method.returnType.erasure()) && inheritedMethodToBridge.areParameterErasuresEqual(method)) {
                        return null;
                    }
                    continue;
                }
            }
        }
        SyntheticMethodBinding accessMethod = null;
        SyntheticMethodBinding[] accessors = this.synthetics[0].get(inheritedMethodToBridge);
        if (accessors == null) {
            accessMethod = new SyntheticMethodBinding(inheritedMethodToBridge, this);
            this.synthetics[0].put(inheritedMethodToBridge, accessors = new SyntheticMethodBinding[2]);
            accessors[1] = accessMethod;
        }
        else if ((accessMethod = accessors[1]) == null) {
            accessMethod = new SyntheticMethodBinding(inheritedMethodToBridge, this);
            accessors[1] = accessMethod;
        }
        return accessMethod;
    }
    
    boolean areFieldsInitialized() {
        if (!this.isPrototype()) {
            return this.prototype.areFieldsInitialized();
        }
        return this.fields != Binding.UNINITIALIZED_FIELDS;
    }
    
    boolean areMethodsInitialized() {
        if (!this.isPrototype()) {
            return this.prototype.areMethodsInitialized();
        }
        return this.methods != Binding.UNINITIALIZED_METHODS;
    }
    
    @Override
    public int kind() {
        if (!this.isPrototype()) {
            return this.prototype.kind();
        }
        if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
            return 2052;
        }
        return 4;
    }
    
    @Override
    public TypeBinding clone(final TypeBinding immaterial) {
        return new SourceTypeBinding(this);
    }
    
    @Override
    public char[] computeUniqueKey(final boolean isLeaf) {
        if (!this.isPrototype()) {
            return this.prototype.computeUniqueKey();
        }
        char[] uniqueKey = super.computeUniqueKey(isLeaf);
        if (uniqueKey.length == 2) {
            return uniqueKey;
        }
        if (Util.isClassFileName(this.fileName)) {
            return uniqueKey;
        }
        int end = CharOperation.lastIndexOf('.', this.fileName);
        if (end != -1) {
            int start = CharOperation.lastIndexOf('/', this.fileName) + 1;
            final char[] mainTypeName = CharOperation.subarray(this.fileName, start, end);
            start = CharOperation.lastIndexOf('/', uniqueKey) + 1;
            if (start == 0) {
                start = 1;
            }
            if (this.isMemberType()) {
                end = CharOperation.indexOf('$', uniqueKey, start);
            }
            else {
                end = -1;
            }
            if (end == -1) {
                end = CharOperation.indexOf('<', uniqueKey, start);
            }
            if (end == -1) {
                end = CharOperation.indexOf(';', uniqueKey, start);
            }
            final char[] topLevelType = CharOperation.subarray(uniqueKey, start, end);
            if (!CharOperation.equals(topLevelType, mainTypeName)) {
                final StringBuffer buffer = new StringBuffer();
                buffer.append(uniqueKey, 0, start);
                buffer.append(mainTypeName);
                buffer.append('~');
                buffer.append(topLevelType);
                buffer.append(uniqueKey, end, uniqueKey.length - end);
                final int length = buffer.length();
                uniqueKey = new char[length];
                buffer.getChars(0, length, uniqueKey, 0);
                return uniqueKey;
            }
        }
        return uniqueKey;
    }
    
    private void checkAnnotationsInType() {
        this.getAnnotationTagBits();
        final ReferenceBinding enclosingType = this.enclosingType();
        if (enclosingType != null && enclosingType.isViewedAsDeprecated() && !this.isDeprecated()) {
            this.modifiers |= 0x200000;
        }
        for (int i = 0, length = this.memberTypes.length; i < length; ++i) {
            ((SourceTypeBinding)this.memberTypes[i]).checkAnnotationsInType();
        }
    }
    
    void faultInTypesForFieldsAndMethods() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        this.checkAnnotationsInType();
        this.internalFaultInTypeForFieldsAndMethods();
    }
    
    private void internalFaultInTypeForFieldsAndMethods() {
        this.fields();
        this.methods();
        for (int i = 0, length = this.memberTypes.length; i < length; ++i) {
            ((SourceTypeBinding)this.memberTypes[i]).internalFaultInTypeForFieldsAndMethods();
        }
    }
    
    @Override
    public FieldBinding[] fields() {
        if (!this.isPrototype()) {
            if ((this.tagBits & 0x2000L) != 0x0L) {
                return this.fields;
            }
            this.tagBits |= 0x2000L;
            return this.fields = this.prototype.fields();
        }
        else {
            if ((this.tagBits & 0x2000L) != 0x0L) {
                return this.fields;
            }
            int failed = 0;
            FieldBinding[] resolvedFields = this.fields;
            try {
                if ((this.tagBits & 0x1000L) == 0x0L) {
                    final int length = this.fields.length;
                    if (length > 1) {
                        ReferenceBinding.sortFields(this.fields, 0, length);
                    }
                    this.tagBits |= 0x1000L;
                }
                final FieldBinding[] fieldsSnapshot = this.fields;
                for (int i = 0, length2 = fieldsSnapshot.length; i < length2; ++i) {
                    if (this.resolveTypeFor(fieldsSnapshot[i]) == null) {
                        if (resolvedFields == fieldsSnapshot) {
                            System.arraycopy(fieldsSnapshot, 0, resolvedFields = new FieldBinding[length2], 0, length2);
                        }
                        resolvedFields[i] = null;
                        ++failed;
                    }
                }
            }
            finally {
                if (failed > 0) {
                    final int newSize = resolvedFields.length - failed;
                    if (newSize == 0) {
                        return this.setFields(Binding.NO_FIELDS);
                    }
                    final FieldBinding[] newFields = new FieldBinding[newSize];
                    int j = 0;
                    int k = 0;
                    for (int length3 = resolvedFields.length; j < length3; ++j) {
                        if (resolvedFields[j] != null) {
                            newFields[k++] = resolvedFields[j];
                        }
                    }
                    this.setFields(newFields);
                }
            }
            if (failed > 0) {
                final int newSize = resolvedFields.length - failed;
                if (newSize == 0) {
                    return this.setFields(Binding.NO_FIELDS);
                }
                final FieldBinding[] newFields = new FieldBinding[newSize];
                int j = 0;
                int k = 0;
                for (int length3 = resolvedFields.length; j < length3; ++j) {
                    if (resolvedFields[j] != null) {
                        newFields[k++] = resolvedFields[j];
                    }
                }
                this.setFields(newFields);
            }
            this.tagBits |= 0x2000L;
            return this.fields;
        }
    }
    
    @Override
    public char[] genericTypeSignature() {
        if (!this.isPrototype()) {
            return this.prototype.genericTypeSignature();
        }
        if (this.genericReferenceTypeSignature == null) {
            this.genericReferenceTypeSignature = this.computeGenericTypeSignature(this.typeVariables);
        }
        return this.genericReferenceTypeSignature;
    }
    
    public char[] genericSignature() {
        if (!this.isPrototype()) {
            return this.prototype.genericSignature();
        }
        StringBuffer sig = null;
        if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
            sig = new StringBuffer(10);
            sig.append('<');
            for (int i = 0, length = this.typeVariables.length; i < length; ++i) {
                sig.append(this.typeVariables[i].genericSignature());
            }
            sig.append('>');
        }
        else {
            Label_0140: {
                if (this.superclass == null || !this.superclass.isParameterizedType()) {
                    for (int i = 0, length = this.superInterfaces.length; i < length; ++i) {
                        if (this.superInterfaces[i].isParameterizedType()) {
                            break Label_0140;
                        }
                    }
                    return null;
                }
            }
            sig = new StringBuffer(10);
        }
        if (this.superclass != null) {
            sig.append(this.superclass.genericTypeSignature());
        }
        else {
            sig.append(this.scope.getJavaLangObject().genericTypeSignature());
        }
        for (int i = 0, length = this.superInterfaces.length; i < length; ++i) {
            sig.append(this.superInterfaces[i].genericTypeSignature());
        }
        return sig.toString().toCharArray();
    }
    
    @Override
    public long getAnnotationTagBits() {
        if (!this.isPrototype()) {
            return this.prototype.getAnnotationTagBits();
        }
        if ((this.tagBits & 0x200000000L) == 0x0L && this.scope != null) {
            final TypeDeclaration typeDecl = this.scope.referenceContext;
            final boolean old = typeDecl.staticInitializerScope.insideTypeAnnotation;
            try {
                typeDecl.staticInitializerScope.insideTypeAnnotation = true;
                ASTNode.resolveAnnotations(typeDecl.staticInitializerScope, typeDecl.annotations, this);
            }
            finally {
                typeDecl.staticInitializerScope.insideTypeAnnotation = old;
            }
            typeDecl.staticInitializerScope.insideTypeAnnotation = old;
            if ((this.tagBits & 0x400000000000L) != 0x0L) {
                this.modifiers |= 0x100000;
            }
        }
        return this.tagBits;
    }
    
    public MethodBinding[] getDefaultAbstractMethods() {
        if (!this.isPrototype()) {
            return this.prototype.getDefaultAbstractMethods();
        }
        int count = 0;
        int i = this.methods.length;
        while (--i >= 0) {
            if (this.methods[i].isDefaultAbstract()) {
                ++count;
            }
        }
        if (count == 0) {
            return Binding.NO_METHODS;
        }
        final MethodBinding[] result = new MethodBinding[count];
        count = 0;
        int j = this.methods.length;
        while (--j >= 0) {
            if (this.methods[j].isDefaultAbstract()) {
                result[count++] = this.methods[j];
            }
        }
        return result;
    }
    
    @Override
    public MethodBinding getExactConstructor(final TypeBinding[] argumentTypes) {
        if (!this.isPrototype()) {
            return this.prototype.getExactConstructor(argumentTypes);
        }
        final int argCount = argumentTypes.length;
        if ((this.tagBits & 0x8000L) != 0x0L) {
            final long range;
            if ((range = ReferenceBinding.binarySearch(TypeConstants.INIT, this.methods)) >= 0L) {
            Label_0125:
                for (int imethod = (int)range, end = (int)(range >> 32); imethod <= end; ++imethod) {
                    final MethodBinding method = this.methods[imethod];
                    if (method.parameters.length == argCount) {
                        final TypeBinding[] toMatch = method.parameters;
                        for (int iarg = 0; iarg < argCount; ++iarg) {
                            if (TypeBinding.notEquals(toMatch[iarg], argumentTypes[iarg])) {
                                continue Label_0125;
                            }
                        }
                        return method;
                    }
                }
            }
        }
        else {
            if ((this.tagBits & 0x4000L) == 0x0L) {
                final int length = this.methods.length;
                if (length > 1) {
                    ReferenceBinding.sortMethods(this.methods, 0, length);
                }
                this.tagBits |= 0x4000L;
            }
            final long range;
            if ((range = ReferenceBinding.binarySearch(TypeConstants.INIT, this.methods)) >= 0L) {
            Label_0304:
                for (int imethod = (int)range, end = (int)(range >> 32); imethod <= end; ++imethod) {
                    final MethodBinding method = this.methods[imethod];
                    if (this.resolveTypesFor(method) == null || method.returnType == null) {
                        this.methods();
                        return this.getExactConstructor(argumentTypes);
                    }
                    if (method.parameters.length == argCount) {
                        final TypeBinding[] toMatch = method.parameters;
                        for (int iarg = 0; iarg < argCount; ++iarg) {
                            if (TypeBinding.notEquals(toMatch[iarg], argumentTypes[iarg])) {
                                continue Label_0304;
                            }
                        }
                        return method;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public MethodBinding getExactMethod(final char[] selector, final TypeBinding[] argumentTypes, final CompilationUnitScope refScope) {
        if (!this.isPrototype()) {
            return this.prototype.getExactMethod(selector, argumentTypes, refScope);
        }
        final int argCount = argumentTypes.length;
        boolean foundNothing = true;
        if ((this.tagBits & 0x8000L) != 0x0L) {
            final long range;
            if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
            Label_0137:
                for (int imethod = (int)range, end = (int)(range >> 32); imethod <= end; ++imethod) {
                    final MethodBinding method = this.methods[imethod];
                    foundNothing = false;
                    if (method.parameters.length == argCount) {
                        final TypeBinding[] toMatch = method.parameters;
                        for (int iarg = 0; iarg < argCount; ++iarg) {
                            if (TypeBinding.notEquals(toMatch[iarg], argumentTypes[iarg])) {
                                continue Label_0137;
                            }
                        }
                        return method;
                    }
                }
            }
        }
        else {
            if ((this.tagBits & 0x4000L) == 0x0L) {
                final int length = this.methods.length;
                if (length > 1) {
                    ReferenceBinding.sortMethods(this.methods, 0, length);
                }
                this.tagBits |= 0x4000L;
            }
            final long range;
            if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
                final int start = (int)range;
                final int end = (int)(range >> 32);
                for (int imethod2 = start; imethod2 <= end; ++imethod2) {
                    final MethodBinding method2 = this.methods[imethod2];
                    if (this.resolveTypesFor(method2) == null || method2.returnType == null) {
                        this.methods();
                        return this.getExactMethod(selector, argumentTypes, refScope);
                    }
                }
                final boolean isSource15 = this.scope.compilerOptions().sourceLevel >= 3211264L;
                for (int i = start; i <= end; ++i) {
                    final MethodBinding method3 = this.methods[i];
                    for (int j = end; j > i; --j) {
                        final MethodBinding method4 = this.methods[j];
                        final boolean paramsMatch = isSource15 ? method3.areParameterErasuresEqual(method4) : method3.areParametersEqual(method4);
                        if (paramsMatch) {
                            this.methods();
                            return this.getExactMethod(selector, argumentTypes, refScope);
                        }
                    }
                }
            Label_0469:
                for (int imethod3 = start; imethod3 <= end; ++imethod3) {
                    final MethodBinding method5 = this.methods[imethod3];
                    final TypeBinding[] toMatch2 = method5.parameters;
                    if (toMatch2.length == argCount) {
                        for (int iarg2 = 0; iarg2 < argCount; ++iarg2) {
                            if (TypeBinding.notEquals(toMatch2[iarg2], argumentTypes[iarg2])) {
                                continue Label_0469;
                            }
                        }
                        return method5;
                    }
                }
            }
        }
        if (foundNothing) {
            if (this.isInterface()) {
                if (this.superInterfaces.length == 1) {
                    if (refScope != null) {
                        refScope.recordTypeReference(this.superInterfaces[0]);
                    }
                    return this.superInterfaces[0].getExactMethod(selector, argumentTypes, refScope);
                }
            }
            else if (this.superclass != null) {
                if (refScope != null) {
                    refScope.recordTypeReference(this.superclass);
                }
                return this.superclass.getExactMethod(selector, argumentTypes, refScope);
            }
        }
        return null;
    }
    
    @Override
    public FieldBinding getField(final char[] fieldName, final boolean needResolve) {
        if (!this.isPrototype()) {
            return this.prototype.getField(fieldName, needResolve);
        }
        if ((this.tagBits & 0x2000L) != 0x0L) {
            return ReferenceBinding.binarySearch(fieldName, this.fields);
        }
        if ((this.tagBits & 0x1000L) == 0x0L) {
            final int length = this.fields.length;
            if (length > 1) {
                ReferenceBinding.sortFields(this.fields, 0, length);
            }
            this.tagBits |= 0x1000L;
        }
        final FieldBinding field = ReferenceBinding.binarySearch(fieldName, this.fields);
        if (field != null) {
            FieldBinding result = null;
            try {
                result = this.resolveTypeFor(field);
                return result;
            }
            finally {
                if (result == null) {
                    final int newSize = this.fields.length - 1;
                    if (newSize == 0) {
                        this.setFields(Binding.NO_FIELDS);
                    }
                    else {
                        final FieldBinding[] newFields = new FieldBinding[newSize];
                        int index = 0;
                        for (int i = 0, length2 = this.fields.length; i < length2; ++i) {
                            final FieldBinding f = this.fields[i];
                            if (f != field) {
                                newFields[index++] = f;
                            }
                        }
                        this.setFields(newFields);
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public MethodBinding[] getMethods(final char[] selector) {
        if (!this.isPrototype()) {
            return this.prototype.getMethods(selector);
        }
        if ((this.tagBits & 0x8000L) != 0x0L) {
            final long range;
            if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
                final int start = (int)range;
                final int end = (int)(range >> 32);
                final int length = end - start + 1;
                final MethodBinding[] result;
                System.arraycopy(this.methods, start, result = new MethodBinding[length], 0, length);
                return result;
            }
            return Binding.NO_METHODS;
        }
        else {
            if ((this.tagBits & 0x4000L) == 0x0L) {
                final int length2 = this.methods.length;
                if (length2 > 1) {
                    ReferenceBinding.sortMethods(this.methods, 0, length2);
                }
                this.tagBits |= 0x4000L;
            }
            final long range2;
            if ((range2 = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
                final int start2 = (int)range2;
                final int end2 = (int)(range2 >> 32);
                for (int i = start2; i <= end2; ++i) {
                    final MethodBinding method = this.methods[i];
                    if (this.resolveTypesFor(method) == null || method.returnType == null) {
                        this.methods();
                        return this.getMethods(selector);
                    }
                }
                int length3 = end2 - start2 + 1;
                final MethodBinding[] result2;
                System.arraycopy(this.methods, start2, result2 = new MethodBinding[length3], 0, length3);
                final boolean isSource15 = this.scope.compilerOptions().sourceLevel >= 3211264L;
                int j;
                for (j = 0, length3 = result2.length - 1; j < length3; ++j) {
                    final MethodBinding method = result2[j];
                    for (int k = length3; k > j; --k) {
                        final boolean paramsMatch = isSource15 ? method.areParameterErasuresEqual(result2[k]) : method.areParametersEqual(result2[k]);
                        if (paramsMatch) {
                            this.methods();
                            return this.getMethods(selector);
                        }
                    }
                }
                return result2;
            }
            return Binding.NO_METHODS;
        }
    }
    
    public FieldBinding getSyntheticField(final LocalVariableBinding actualOuterLocalVariable) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null || this.synthetics[1] == null) {
            return null;
        }
        return this.synthetics[1].get(actualOuterLocalVariable);
    }
    
    public FieldBinding getSyntheticField(final ReferenceBinding targetEnclosingType, final boolean onlyExactMatch) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null || this.synthetics[1] == null) {
            return null;
        }
        FieldBinding field = this.synthetics[1].get(targetEnclosingType);
        if (field != null) {
            return field;
        }
        if (!onlyExactMatch) {
            final Iterator accessFields = this.synthetics[1].values().iterator();
            while (accessFields.hasNext()) {
                field = accessFields.next();
                if (CharOperation.prefixEquals(TypeConstants.SYNTHETIC_ENCLOSING_INSTANCE_PREFIX, field.name) && field.type.findSuperTypeOriginatingFrom(targetEnclosingType) != null) {
                    return field;
                }
            }
        }
        return null;
    }
    
    public SyntheticMethodBinding getSyntheticBridgeMethod(final MethodBinding inheritedMethodToBridge) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            return null;
        }
        if (this.synthetics[0] == null) {
            return null;
        }
        final SyntheticMethodBinding[] accessors = this.synthetics[0].get(inheritedMethodToBridge);
        if (accessors == null) {
            return null;
        }
        return accessors[1];
    }
    
    @Override
    public boolean hasTypeBit(final int bit) {
        if (!this.isPrototype()) {
            return this.prototype.hasTypeBit(bit);
        }
        return (this.typeBits & bit) != 0x0;
    }
    
    @Override
    public void initializeDeprecatedAnnotationTagBits() {
        if (!this.isPrototype()) {
            this.prototype.initializeDeprecatedAnnotationTagBits();
            return;
        }
        if ((this.tagBits & 0x400000000L) == 0x0L) {
            final TypeDeclaration typeDecl = this.scope.referenceContext;
            final boolean old = typeDecl.staticInitializerScope.insideTypeAnnotation;
            try {
                typeDecl.staticInitializerScope.insideTypeAnnotation = true;
                ASTNode.resolveDeprecatedAnnotations(typeDecl.staticInitializerScope, typeDecl.annotations, this);
                this.tagBits |= 0x400000000L;
            }
            finally {
                typeDecl.staticInitializerScope.insideTypeAnnotation = old;
            }
            typeDecl.staticInitializerScope.insideTypeAnnotation = old;
            if ((this.tagBits & 0x400000000000L) != 0x0L) {
                this.modifiers |= 0x100000;
            }
        }
    }
    
    @Override
    void initializeForStaticImports() {
        if (!this.isPrototype()) {
            this.prototype.initializeForStaticImports();
            return;
        }
        if (this.scope == null) {
            return;
        }
        if (this.superInterfaces == null) {
            this.scope.connectTypeHierarchy();
        }
        this.scope.buildFields();
        this.scope.buildMethods();
    }
    
    @Override
    int getNullDefault() {
        if (!this.isPrototype()) {
            return this.prototype.getNullDefault();
        }
        switch (this.nullnessDefaultInitialized) {
            case 0: {
                this.getAnnotationTagBits();
            }
            case 1: {
                this.getPackage().isViewedAsDeprecated();
                this.nullnessDefaultInitialized = 2;
                break;
            }
        }
        return this.defaultNullness;
    }
    
    @Override
    public boolean isEquivalentTo(final TypeBinding otherType) {
        if (!this.isPrototype()) {
            return this.prototype.isEquivalentTo(otherType);
        }
        if (TypeBinding.equalsEquals(this, otherType)) {
            return true;
        }
        if (otherType == null) {
            return false;
        }
        switch (otherType.kind()) {
            case 516:
            case 8196: {
                return ((WildcardBinding)otherType).boundCheck(this);
            }
            case 260: {
                if ((otherType.tagBits & 0x40000000L) == 0x0L && (!this.isMemberType() || !otherType.isMemberType())) {
                    return false;
                }
                final ParameterizedTypeBinding otherParamType = (ParameterizedTypeBinding)otherType;
                if (TypeBinding.notEquals(this, otherParamType.genericType())) {
                    return false;
                }
                if (!this.isStatic()) {
                    final ReferenceBinding enclosing = this.enclosingType();
                    if (enclosing != null) {
                        final ReferenceBinding otherEnclosing = otherParamType.enclosingType();
                        if (otherEnclosing == null) {
                            return false;
                        }
                        if ((otherEnclosing.tagBits & 0x40000000L) == 0x0L) {
                            if (TypeBinding.notEquals(enclosing, otherEnclosing)) {
                                return false;
                            }
                        }
                        else if (!enclosing.isEquivalentTo(otherParamType.enclosingType())) {
                            return false;
                        }
                    }
                }
                final int length = (this.typeVariables == null) ? 0 : this.typeVariables.length;
                final TypeBinding[] otherArguments = otherParamType.arguments;
                final int otherLength = (otherArguments == null) ? 0 : otherArguments.length;
                if (otherLength != length) {
                    return false;
                }
                for (int i = 0; i < length; ++i) {
                    if (!this.typeVariables[i].isTypeArgumentContainedBy(otherArguments[i])) {
                        return false;
                    }
                }
                return true;
            }
            case 1028: {
                return TypeBinding.equalsEquals(otherType.erasure(), this);
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean isGenericType() {
        if (!this.isPrototype()) {
            return this.prototype.isGenericType();
        }
        return this.typeVariables != Binding.NO_TYPE_VARIABLES;
    }
    
    @Override
    public boolean isHierarchyConnected() {
        if (!this.isPrototype()) {
            return this.prototype.isHierarchyConnected();
        }
        return (this.tagBits & 0x200L) != 0x0L;
    }
    
    @Override
    public boolean isRepeatableAnnotationType() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        return this.containerAnnotationType != null;
    }
    
    @Override
    public boolean isTaggedRepeatable() {
        return (this.tagBits & 0x1000000000000000L) != 0x0L;
    }
    
    @Override
    public ReferenceBinding[] memberTypes() {
        if (!this.isPrototype()) {
            if ((this.tagBits & 0x10000000L) == 0x0L) {
                return this.memberTypes;
            }
            final ReferenceBinding[] memberTypes = this.prototype.memberTypes();
            this.memberTypes = memberTypes;
            final ReferenceBinding[] members = memberTypes;
            final int membersLength = (members == null) ? 0 : members.length;
            this.memberTypes = new ReferenceBinding[membersLength];
            for (int i = 0; i < membersLength; ++i) {
                this.memberTypes[i] = this.environment.createMemberType(members[i], this);
            }
            this.tagBits &= 0xFFFFFFFFEFFFFFFFL;
        }
        return this.memberTypes;
    }
    
    @Override
    public boolean hasMemberTypes() {
        if (!this.isPrototype()) {
            return this.prototype.hasMemberTypes();
        }
        return this.memberTypes.length > 0;
    }
    
    @Override
    public MethodBinding[] methods() {
        if (!this.isPrototype()) {
            if ((this.tagBits & 0x8000L) != 0x0L) {
                return this.methods;
            }
            this.tagBits |= 0x8000L;
            return this.methods = this.prototype.methods();
        }
        else {
            if ((this.tagBits & 0x8000L) != 0x0L) {
                return this.methods;
            }
            if (!this.areMethodsInitialized()) {
                this.scope.buildMethods();
            }
            if ((this.tagBits & 0x4000L) == 0x0L) {
                final int length = this.methods.length;
                if (length > 1) {
                    ReferenceBinding.sortMethods(this.methods, 0, length);
                }
                this.tagBits |= 0x4000L;
            }
            int failed = 0;
            MethodBinding[] resolvedMethods = this.methods;
            try {
                for (int i = 0, length2 = this.methods.length; i < length2; ++i) {
                    if ((this.tagBits & 0x8000L) != 0x0L) {
                        return this.methods;
                    }
                    if (this.resolveTypesFor(this.methods[i]) == null) {
                        if (resolvedMethods == this.methods) {
                            System.arraycopy(this.methods, 0, resolvedMethods = new MethodBinding[length2], 0, length2);
                        }
                        resolvedMethods[i] = null;
                        ++failed;
                    }
                }
                final boolean complyTo15OrAbove = this.scope.compilerOptions().sourceLevel >= 3211264L;
                final boolean compliance16 = this.scope.compilerOptions().complianceLevel == 3276800L;
                for (int j = 0, length3 = this.methods.length; j < length3; ++j) {
                    int severity = 1;
                    final MethodBinding method = resolvedMethods[j];
                    if (method != null) {
                        final char[] selector = method.selector;
                        AbstractMethodDeclaration methodDecl = null;
                        for (int k = j + 1; k < length3; ++k) {
                            final MethodBinding method2 = resolvedMethods[k];
                            if (method2 != null) {
                                if (!CharOperation.equals(selector, method2.selector)) {
                                    break;
                                }
                                if (complyTo15OrAbove) {
                                    if (!method.areParameterErasuresEqual(method2)) {
                                        continue;
                                    }
                                    if (compliance16 && method.returnType != null && method2.returnType != null && TypeBinding.notEquals(method.returnType.erasure(), method2.returnType.erasure())) {
                                        final TypeBinding[] params1 = method.parameters;
                                        final TypeBinding[] params2 = method2.parameters;
                                        final int pLength = params1.length;
                                        final TypeVariableBinding[] vars = method.typeVariables;
                                        final TypeVariableBinding[] vars2 = method2.typeVariables;
                                        boolean equalTypeVars = vars == vars2;
                                        MethodBinding subMethod = method2;
                                        if (!equalTypeVars) {
                                            final MethodBinding temp = method.computeSubstitutedMethod(method2, this.scope.environment());
                                            if (temp != null) {
                                                equalTypeVars = true;
                                                subMethod = temp;
                                            }
                                        }
                                        final boolean equalParams = method.areParametersEqual(subMethod);
                                        if (!equalParams || !equalTypeVars) {
                                            if (vars != Binding.NO_TYPE_VARIABLES && vars2 != Binding.NO_TYPE_VARIABLES) {
                                                severity = 0;
                                            }
                                            else if (pLength > 0) {
                                                int index = pLength;
                                                while (--index >= 0) {
                                                    if (TypeBinding.notEquals(params1[index], params2[index].erasure())) {
                                                        if (!(params1[index] instanceof RawTypeBinding)) {
                                                            break;
                                                        }
                                                        if (TypeBinding.notEquals(params2[index].erasure(), ((RawTypeBinding)params1[index]).actualType())) {
                                                            break;
                                                        }
                                                    }
                                                    if (TypeBinding.equalsEquals(params1[index], params2[index])) {
                                                        final TypeBinding type = params1[index].leafComponentType();
                                                        if (type instanceof SourceTypeBinding && type.typeVariables() != Binding.NO_TYPE_VARIABLES) {
                                                            index = pLength;
                                                            break;
                                                        }
                                                        continue;
                                                    }
                                                }
                                                if (index >= 0 && index < pLength) {
                                                    index = pLength;
                                                    while (--index >= 0) {
                                                        if (TypeBinding.notEquals(params1[index].erasure(), params2[index])) {
                                                            if (!(params2[index] instanceof RawTypeBinding)) {
                                                                break;
                                                            }
                                                            if (TypeBinding.notEquals(params1[index].erasure(), ((RawTypeBinding)params2[index]).actualType())) {
                                                                break;
                                                            }
                                                            continue;
                                                        }
                                                    }
                                                }
                                                if (index >= 0) {
                                                    severity = 0;
                                                }
                                            }
                                            else if (pLength != 0) {
                                                severity = 0;
                                            }
                                        }
                                    }
                                }
                                else if (!method.areParametersEqual(method2)) {
                                    continue;
                                }
                                final boolean isEnumSpecialMethod = this.isEnum() && (CharOperation.equals(selector, TypeConstants.VALUEOF) || CharOperation.equals(selector, TypeConstants.VALUES));
                                boolean removeMethod2 = severity == 1;
                                if (methodDecl == null) {
                                    methodDecl = method.sourceMethod();
                                    if (methodDecl != null && methodDecl.binding != null) {
                                        boolean removeMethod3 = method.returnType == null && method2.returnType != null;
                                        if (isEnumSpecialMethod) {
                                            this.scope.problemReporter().duplicateEnumSpecialMethod(this, methodDecl);
                                            removeMethod3 = true;
                                        }
                                        else {
                                            this.scope.problemReporter().duplicateMethodInType(methodDecl, method.areParametersEqual(method2), severity);
                                        }
                                        if (removeMethod3) {
                                            removeMethod2 = false;
                                            methodDecl.binding = null;
                                            if (resolvedMethods == this.methods) {
                                                System.arraycopy(this.methods, 0, resolvedMethods = new MethodBinding[length3], 0, length3);
                                            }
                                            resolvedMethods[j] = null;
                                            ++failed;
                                        }
                                    }
                                }
                                final AbstractMethodDeclaration method2Decl = method2.sourceMethod();
                                if (method2Decl != null && method2Decl.binding != null) {
                                    if (isEnumSpecialMethod) {
                                        this.scope.problemReporter().duplicateEnumSpecialMethod(this, method2Decl);
                                        removeMethod2 = true;
                                    }
                                    else {
                                        this.scope.problemReporter().duplicateMethodInType(method2Decl, method.areParametersEqual(method2), severity);
                                    }
                                    if (removeMethod2) {
                                        method2Decl.binding = null;
                                        if (resolvedMethods == this.methods) {
                                            System.arraycopy(this.methods, 0, resolvedMethods = new MethodBinding[length3], 0, length3);
                                        }
                                        resolvedMethods[k] = null;
                                        ++failed;
                                    }
                                }
                            }
                        }
                        if (method.returnType == null && resolvedMethods[j] != null) {
                            methodDecl = method.sourceMethod();
                            if (methodDecl != null) {
                                methodDecl.binding = null;
                            }
                            if (resolvedMethods == this.methods) {
                                System.arraycopy(this.methods, 0, resolvedMethods = new MethodBinding[length3], 0, length3);
                            }
                            resolvedMethods[j] = null;
                            ++failed;
                        }
                    }
                }
            }
            finally {
                if ((this.tagBits & 0x8000L) != 0x0L) {
                    return this.methods;
                }
                if (failed > 0) {
                    final int newSize = resolvedMethods.length - failed;
                    if (newSize == 0) {
                        this.setMethods(Binding.NO_METHODS);
                    }
                    else {
                        final MethodBinding[] newMethods = new MethodBinding[newSize];
                        int l = 0;
                        int m = 0;
                        for (int length4 = resolvedMethods.length; l < length4; ++l) {
                            if (resolvedMethods[l] != null) {
                                newMethods[m++] = resolvedMethods[l];
                            }
                        }
                        this.setMethods(newMethods);
                    }
                }
                this.addDefaultAbstractMethods();
                this.tagBits |= 0x8000L;
            }
            if ((this.tagBits & 0x8000L) != 0x0L) {
                return this.methods;
            }
            if (failed > 0) {
                final int newSize = resolvedMethods.length - failed;
                if (newSize == 0) {
                    this.setMethods(Binding.NO_METHODS);
                }
                else {
                    final MethodBinding[] newMethods = new MethodBinding[newSize];
                    int l = 0;
                    int m = 0;
                    for (int length4 = resolvedMethods.length; l < length4; ++l) {
                        if (resolvedMethods[l] != null) {
                            newMethods[m++] = resolvedMethods[l];
                        }
                    }
                    this.setMethods(newMethods);
                }
            }
            this.addDefaultAbstractMethods();
            this.tagBits |= 0x8000L;
            return this.methods;
        }
    }
    
    @Override
    public TypeBinding prototype() {
        return this.prototype;
    }
    
    public boolean isPrototype() {
        return this == this.prototype;
    }
    
    @Override
    public ReferenceBinding containerAnnotationType() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.containerAnnotationType instanceof UnresolvedReferenceBinding) {
            this.containerAnnotationType = (ReferenceBinding)BinaryTypeBinding.resolveType(this.containerAnnotationType, this.scope.environment(), false);
        }
        return this.containerAnnotationType;
    }
    
    public FieldBinding resolveTypeFor(final FieldBinding field) {
        if (!this.isPrototype()) {
            return this.prototype.resolveTypeFor(field);
        }
        if ((field.modifiers & 0x2000000) == 0x0) {
            return field;
        }
        final long sourceLevel = this.scope.compilerOptions().sourceLevel;
        if (sourceLevel >= 3211264L && (field.getAnnotationTagBits() & 0x400000000000L) != 0x0L) {
            field.modifiers |= 0x100000;
        }
        if (this.isViewedAsDeprecated() && !field.isDeprecated()) {
            field.modifiers |= 0x200000;
        }
        if (this.hasRestrictedAccess()) {
            field.modifiers |= 0x40000;
        }
        final FieldDeclaration[] fieldDecls = this.scope.referenceContext.fields;
        for (int length = (fieldDecls == null) ? 0 : fieldDecls.length, f = 0; f < length; ++f) {
            if (fieldDecls[f].binding == field) {
                final MethodScope initializationScope = field.isStatic() ? this.scope.referenceContext.staticInitializerScope : this.scope.referenceContext.initializerScope;
                final FieldBinding previousField = initializationScope.initializedField;
                try {
                    initializationScope.initializedField = field;
                    final FieldDeclaration fieldDecl = fieldDecls[f];
                    final TypeBinding fieldType = (fieldDecl.getKind() == 3) ? initializationScope.environment().convertToRawType(this, false) : fieldDecl.type.resolveType(initializationScope, true);
                    field.type = fieldType;
                    field.modifiers &= 0xFDFFFFFF;
                    if (fieldType == null) {
                        return fieldDecl.binding = null;
                    }
                    if (fieldType == TypeBinding.VOID) {
                        this.scope.problemReporter().variableTypeCannotBeVoid(fieldDecl);
                        return fieldDecl.binding = null;
                    }
                    if (fieldType.isArrayType() && ((ArrayBinding)fieldType).leafComponentType == TypeBinding.VOID) {
                        this.scope.problemReporter().variableTypeCannotBeVoidArray(fieldDecl);
                        return fieldDecl.binding = null;
                    }
                    if ((fieldType.tagBits & 0x80L) != 0x0L) {
                        field.tagBits |= 0x80L;
                    }
                    final TypeBinding leafType = fieldType.leafComponentType();
                    if (leafType instanceof ReferenceBinding && (((ReferenceBinding)leafType).modifiers & 0x40000000) != 0x0) {
                        field.modifiers |= 0x40000000;
                    }
                    if (sourceLevel >= 3407872L) {
                        final Annotation[] annotations = fieldDecl.annotations;
                        if (annotations != null && annotations.length != 0) {
                            ASTNode.copySE8AnnotationsToType(initializationScope, field, annotations, fieldDecl.getKind() == 3);
                        }
                        Annotation.isTypeUseCompatible(fieldDecl.type, this.scope, annotations);
                    }
                    if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                        if (fieldDecl.getKind() == 3) {
                            field.tagBits |= 0x100000000000000L;
                        }
                        else {
                            if (this.hasNonNullDefaultFor(32, this.environment.usesNullTypeAnnotations())) {
                                field.fillInDefaultNonNullness(fieldDecl, initializationScope);
                            }
                            if (!this.scope.validateNullAnnotation(field.tagBits, fieldDecl.type, fieldDecl.annotations)) {
                                field.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                            }
                        }
                    }
                }
                finally {
                    initializationScope.initializedField = previousField;
                }
                initializationScope.initializedField = previousField;
                if (this.externalAnnotationProvider != null) {
                    ExternalAnnotationSuperimposer.annotateFieldBinding(field, this.externalAnnotationProvider, this.environment);
                }
                return field;
            }
        }
        return null;
    }
    
    public MethodBinding resolveTypesFor(final MethodBinding method) {
        if (!this.isPrototype()) {
            return this.prototype.resolveTypesFor(method);
        }
        if ((method.modifiers & 0x2000000) == 0x0) {
            return method;
        }
        final long sourceLevel = this.scope.compilerOptions().sourceLevel;
        if (sourceLevel >= 3211264L) {
            final ReferenceBinding object = this.scope.getJavaLangObject();
            final TypeVariableBinding[] tvb = method.typeVariables;
            for (int i = 0; i < tvb.length; ++i) {
                tvb[i].superclass = object;
            }
            if ((method.getAnnotationTagBits() & 0x400000000000L) != 0x0L) {
                method.modifiers |= 0x100000;
            }
        }
        if (this.isViewedAsDeprecated() && !method.isDeprecated()) {
            method.modifiers |= 0x200000;
        }
        if (this.hasRestrictedAccess()) {
            method.modifiers |= 0x40000;
        }
        final AbstractMethodDeclaration methodDecl = method.sourceMethod();
        if (methodDecl == null) {
            return null;
        }
        final TypeParameter[] typeParameters = methodDecl.typeParameters();
        if (typeParameters != null) {
            methodDecl.scope.connectTypeVariables(typeParameters, true);
            for (int i = 0, paramLength = typeParameters.length; i < paramLength; ++i) {
                typeParameters[i].checkBounds(methodDecl.scope);
            }
        }
        final TypeReference[] exceptionTypes = methodDecl.thrownExceptions;
        if (exceptionTypes != null) {
            final int size = exceptionTypes.length;
            method.thrownExceptions = new ReferenceBinding[size];
            int count = 0;
            for (int j = 0; j < size; ++j) {
                final ReferenceBinding resolvedExceptionType = (ReferenceBinding)exceptionTypes[j].resolveType(methodDecl.scope, true);
                if (resolvedExceptionType != null) {
                    if (resolvedExceptionType.isBoundParameterizedType()) {
                        methodDecl.scope.problemReporter().invalidParameterizedExceptionType(resolvedExceptionType, exceptionTypes[j]);
                    }
                    else if (resolvedExceptionType.findSuperTypeOriginatingFrom(21, true) == null && resolvedExceptionType.isValidBinding()) {
                        methodDecl.scope.problemReporter().cannotThrowType(exceptionTypes[j], resolvedExceptionType);
                    }
                    else {
                        if ((resolvedExceptionType.tagBits & 0x80L) != 0x0L) {
                            method.tagBits |= 0x80L;
                        }
                        if (exceptionTypes[j].hasNullTypeAnnotation(TypeReference.AnnotationPosition.ANY)) {
                            methodDecl.scope.problemReporter().nullAnnotationUnsupportedLocation(exceptionTypes[j]);
                        }
                        method.modifiers |= (resolvedExceptionType.modifiers & 0x40000000);
                        method.thrownExceptions[count++] = resolvedExceptionType;
                    }
                }
            }
            if (count < size) {
                System.arraycopy(method.thrownExceptions, 0, method.thrownExceptions = new ReferenceBinding[count], 0, count);
            }
        }
        if (methodDecl.receiver != null) {
            method.receiver = methodDecl.receiver.type.resolveType(methodDecl.scope, true);
        }
        final boolean reportUnavoidableGenericTypeProblems = this.scope.compilerOptions().reportUnavoidableGenericTypeProblems;
        boolean foundArgProblem = false;
        final Argument[] arguments = methodDecl.arguments;
        if (arguments != null) {
            final int size2 = arguments.length;
            method.parameters = Binding.NO_PARAMETERS;
            final TypeBinding[] newParameters = new TypeBinding[size2];
            for (int k = 0; k < size2; ++k) {
                final Argument arg = arguments[k];
                if (arg.annotations != null) {
                    method.tagBits |= 0x400L;
                }
                final boolean deferRawTypeCheck = !reportUnavoidableGenericTypeProblems && !method.isConstructor() && (arg.type.bits & 0x40000000) == 0x0;
                if (deferRawTypeCheck) {
                    final TypeReference type = arg.type;
                    type.bits |= 0x40000000;
                }
                TypeBinding parameterType;
                try {
                    parameterType = arg.type.resolveType(methodDecl.scope, true);
                }
                finally {
                    if (deferRawTypeCheck) {
                        final TypeReference type2 = arg.type;
                        type2.bits &= 0xBFFFFFFF;
                    }
                }
                if (deferRawTypeCheck) {
                    final TypeReference type3 = arg.type;
                    type3.bits &= 0xBFFFFFFF;
                }
                if (parameterType == null) {
                    foundArgProblem = true;
                }
                else if (parameterType == TypeBinding.VOID) {
                    methodDecl.scope.problemReporter().argumentTypeCannotBeVoid(methodDecl, arg);
                    foundArgProblem = true;
                }
                else {
                    if ((parameterType.tagBits & 0x80L) != 0x0L) {
                        method.tagBits |= 0x80L;
                    }
                    final TypeBinding leafType = parameterType.leafComponentType();
                    if (leafType instanceof ReferenceBinding && (((ReferenceBinding)leafType).modifiers & 0x40000000) != 0x0) {
                        method.modifiers |= 0x40000000;
                    }
                    newParameters[k] = parameterType;
                    arg.binding = new LocalVariableBinding(arg, parameterType, arg.modifiers, methodDecl.scope);
                }
            }
            if (!foundArgProblem) {
                method.parameters = newParameters;
            }
        }
        if (sourceLevel >= 3342336L) {
            if ((method.tagBits & 0x8000000000000L) != 0x0L) {
                if (!method.isVarargs()) {
                    methodDecl.scope.problemReporter().safeVarargsOnFixedArityMethod(method);
                }
                else if (!method.isStatic() && !method.isFinal() && !method.isConstructor()) {
                    methodDecl.scope.problemReporter().safeVarargsOnNonFinalInstanceMethod(method);
                }
            }
            else if (method.parameters != null && method.parameters.length > 0 && method.isVarargs() && !method.parameters[method.parameters.length - 1].isReifiable()) {
                methodDecl.scope.problemReporter().possibleHeapPollutionFromVararg(methodDecl.arguments[methodDecl.arguments.length - 1]);
            }
        }
        boolean foundReturnTypeProblem = false;
        if (!method.isConstructor()) {
            final TypeReference returnType = (methodDecl instanceof MethodDeclaration) ? ((MethodDeclaration)methodDecl).returnType : null;
            if (returnType == null) {
                methodDecl.scope.problemReporter().missingReturnType(methodDecl);
                method.returnType = null;
                foundReturnTypeProblem = true;
            }
            else {
                final boolean deferRawTypeCheck2 = !reportUnavoidableGenericTypeProblems && (returnType.bits & 0x40000000) == 0x0;
                if (deferRawTypeCheck2) {
                    final TypeReference typeReference = returnType;
                    typeReference.bits |= 0x40000000;
                }
                TypeBinding methodType;
                try {
                    methodType = returnType.resolveType(methodDecl.scope, true);
                }
                finally {
                    if (deferRawTypeCheck2) {
                        final TypeReference typeReference2 = returnType;
                        typeReference2.bits &= 0xBFFFFFFF;
                    }
                }
                if (deferRawTypeCheck2) {
                    final TypeReference typeReference3 = returnType;
                    typeReference3.bits &= 0xBFFFFFFF;
                }
                if (methodType == null) {
                    foundReturnTypeProblem = true;
                }
                else {
                    if ((methodType.tagBits & 0x80L) != 0x0L) {
                        method.tagBits |= 0x80L;
                    }
                    method.returnType = methodType;
                    if (sourceLevel >= 3407872L && !method.isVoidMethod()) {
                        final Annotation[] annotations = methodDecl.annotations;
                        if (annotations != null && annotations.length != 0) {
                            ASTNode.copySE8AnnotationsToType(methodDecl.scope, method, methodDecl.annotations, false);
                        }
                        Annotation.isTypeUseCompatible(returnType, this.scope, methodDecl.annotations);
                    }
                    final TypeBinding leafType2 = methodType.leafComponentType();
                    if (leafType2 instanceof ReferenceBinding && (((ReferenceBinding)leafType2).modifiers & 0x40000000) != 0x0) {
                        method.modifiers |= 0x40000000;
                    }
                    else if (leafType2 == TypeBinding.VOID && methodDecl.annotations != null) {
                        rejectTypeAnnotatedVoidMethod(methodDecl);
                    }
                }
            }
        }
        else if (sourceLevel >= 3407872L) {
            final Annotation[] annotations2 = methodDecl.annotations;
            if (annotations2 != null && annotations2.length != 0) {
                ASTNode.copySE8AnnotationsToType(methodDecl.scope, method, methodDecl.annotations, false);
            }
        }
        if (foundArgProblem) {
            methodDecl.binding = null;
            method.parameters = Binding.NO_PARAMETERS;
            if (typeParameters != null) {
                for (int l = 0, length = typeParameters.length; l < length; ++l) {
                    typeParameters[l].binding = null;
                }
            }
            return null;
        }
        final CompilerOptions compilerOptions = this.scope.compilerOptions();
        if (compilerOptions.isAnnotationBasedNullAnalysisEnabled && !method.isConstructor() && method.returnType != null) {
            final long nullTagBits = method.tagBits & 0x180000000000000L;
            if (nullTagBits != 0L) {
                final TypeReference returnTypeRef = ((MethodDeclaration)methodDecl).returnType;
                if (this.scope.environment().usesNullTypeAnnotations()) {
                    if (!this.scope.validateNullAnnotation(nullTagBits, returnTypeRef, methodDecl.annotations)) {
                        final TypeBinding returnType2 = method.returnType;
                        returnType2.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                    }
                    method.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                }
                else if (!this.scope.validateNullAnnotation(nullTagBits, returnTypeRef, methodDecl.annotations)) {
                    method.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                }
            }
        }
        if (compilerOptions.storeAnnotations) {
            this.createArgumentBindings(method, compilerOptions);
        }
        if (foundReturnTypeProblem) {
            return method;
        }
        method.modifiers &= 0xFDFFFFFF;
        if (this.externalAnnotationProvider != null) {
            ExternalAnnotationSuperimposer.annotateMethodBinding(method, this.externalAnnotationProvider, this.environment);
        }
        return method;
    }
    
    private static void rejectTypeAnnotatedVoidMethod(final AbstractMethodDeclaration methodDecl) {
        final Annotation[] annotations = methodDecl.annotations;
        for (int length = (annotations == null) ? 0 : annotations.length, i = 0; i < length; ++i) {
            final ReferenceBinding binding = (ReferenceBinding)annotations[i].resolvedType;
            if (binding != null && (binding.tagBits & 0x20000000000000L) != 0x0L && (binding.tagBits & 0x4000000000L) == 0x0L) {
                methodDecl.scope.problemReporter().illegalUsageOfTypeAnnotations(annotations[i]);
            }
        }
    }
    
    private void createArgumentBindings(final MethodBinding method, final CompilerOptions compilerOptions) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (compilerOptions.isAnnotationBasedNullAnalysisEnabled) {
            this.getNullDefault();
        }
        final AbstractMethodDeclaration methodDecl = method.sourceMethod();
        if (methodDecl != null) {
            if (method.parameters != Binding.NO_PARAMETERS) {
                methodDecl.createArgumentBindings();
            }
            if (compilerOptions.isAnnotationBasedNullAnalysisEnabled) {
                new ImplicitNullAnnotationVerifier(this.scope.environment()).checkImplicitNullAnnotations(method, methodDecl, true, this.scope);
            }
        }
    }
    
    public void evaluateNullAnnotations() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.nullnessDefaultInitialized > 0 || !this.scope.compilerOptions().isAnnotationBasedNullAnalysisEnabled) {
            return;
        }
        if ((this.tagBits & 0x180000000000000L) != 0x0L) {
            final Annotation[] annotations = this.scope.referenceContext.annotations;
            for (int i = 0; i < annotations.length; ++i) {
                final ReferenceBinding annotationType = annotations[i].getCompilerAnnotation().getAnnotationType();
                if (annotationType != null && annotationType.hasNullBit(96)) {
                    this.scope.problemReporter().nullAnnotationUnsupportedLocation(annotations[i]);
                    this.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                }
            }
        }
        final boolean isPackageInfo = CharOperation.equals(this.sourceName, TypeConstants.PACKAGE_INFO_NAME);
        final PackageBinding pkg = this.getPackage();
        final boolean isInDefaultPkg = pkg.compoundName == CharOperation.NO_CHAR_CHAR;
        if (!isPackageInfo) {
            final boolean isInNullnessAnnotationPackage = this.scope.environment().isNullnessAnnotationPackage(pkg);
            if (pkg.defaultNullness == 0 && !isInDefaultPkg && !isInNullnessAnnotationPackage && !(this instanceof NestedTypeBinding)) {
                final ReferenceBinding packageInfo = pkg.getType(TypeConstants.PACKAGE_INFO_NAME);
                if (packageInfo == null) {
                    this.scope.problemReporter().missingNonNullByDefaultAnnotation(this.scope.referenceContext);
                    pkg.defaultNullness = 2;
                }
                else if (packageInfo instanceof SourceTypeBinding && (packageInfo.tagBits & 0x200L) == 0x0L) {
                    final CompilationUnitScope pkgCUS = ((SourceTypeBinding)packageInfo).scope.compilationUnitScope();
                    final boolean current = pkgCUS.connectingHierarchy;
                    pkgCUS.connectingHierarchy = true;
                    try {
                        packageInfo.getAnnotationTagBits();
                    }
                    finally {
                        pkgCUS.connectingHierarchy = current;
                    }
                    pkgCUS.connectingHierarchy = current;
                }
                else {
                    packageInfo.getAnnotationTagBits();
                }
            }
        }
        this.nullnessDefaultInitialized = 1;
        final boolean usesNullTypeAnnotations = this.scope.environment().usesNullTypeAnnotations();
        if (usesNullTypeAnnotations) {
            if (this.defaultNullness != 0) {
                if (isPackageInfo) {
                    pkg.defaultNullness = this.defaultNullness;
                }
                else {
                    final TypeDeclaration typeDecl = this.scope.referenceContext;
                    this.checkRedundantNullnessDefaultRecurse(typeDecl, typeDecl.annotations, this.defaultNullness, true);
                }
            }
            else if (isPackageInfo || (isInDefaultPkg && !(this instanceof NestedTypeBinding))) {
                this.scope.problemReporter().missingNonNullByDefaultAnnotation(this.scope.referenceContext);
                if (!isInDefaultPkg) {
                    pkg.defaultNullness = 2;
                }
            }
        }
        else {
            long annotationTagBits = this.tagBits;
            int newDefaultNullness = 0;
            if ((annotationTagBits & 0x400000000000000L) != 0x0L) {
                newDefaultNullness = 2;
            }
            else if ((annotationTagBits & 0x200000000000000L) != 0x0L) {
                newDefaultNullness = 1;
            }
            else if (this.defaultNullness != 0) {
                if (this.defaultNullness == 2) {
                    annotationTagBits = 288230376151711744L;
                    newDefaultNullness = 2;
                }
                else {
                    annotationTagBits = 144115188075855872L;
                    newDefaultNullness = 1;
                }
            }
            if (newDefaultNullness != 0) {
                if (isPackageInfo) {
                    pkg.defaultNullness = newDefaultNullness;
                }
                else {
                    this.defaultNullness = newDefaultNullness;
                    final TypeDeclaration typeDecl2 = this.scope.referenceContext;
                    final long nullDefaultBits = annotationTagBits & 0x600000000000000L;
                    this.checkRedundantNullnessDefaultRecurse(typeDecl2, typeDecl2.annotations, nullDefaultBits, false);
                }
            }
            else if (isPackageInfo || (isInDefaultPkg && !(this instanceof NestedTypeBinding))) {
                this.scope.problemReporter().missingNonNullByDefaultAnnotation(this.scope.referenceContext);
                if (!isInDefaultPkg) {
                    pkg.defaultNullness = 2;
                }
            }
        }
        this.maybeMarkTypeParametersNonNull();
    }
    
    private void maybeMarkTypeParametersNonNull() {
        if (this.scope == null || !this.scope.hasDefaultNullnessFor(128)) {
            return;
        }
        if (this.typeVariables != null && this.typeVariables.length > 0) {
            final AnnotationBinding[] annots = { this.environment.getNonNullAnnotation() };
            for (int i = 0; i < this.typeVariables.length; ++i) {
                final TypeVariableBinding tvb = this.typeVariables[i];
                if ((tvb.tagBits & 0x180000000000000L) == 0x0L) {
                    this.typeVariables[i] = (TypeVariableBinding)this.environment.createAnnotatedType(tvb, annots);
                }
            }
        }
    }
    
    protected void checkRedundantNullnessDefaultRecurse(final ASTNode location, final Annotation[] annotations, final long nullBits, final boolean useNullTypeAnnotations) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.fPackage.defaultNullness != 0) {
            final boolean isRedundant = useNullTypeAnnotations ? (this.fPackage.defaultNullness == nullBits) : (this.fPackage.defaultNullness == 1 && (nullBits & 0x200000000000000L) != 0x0L);
            if (isRedundant) {
                this.scope.problemReporter().nullDefaultAnnotationIsRedundant(location, annotations, this.fPackage);
            }
        }
    }
    
    protected boolean checkRedundantNullnessDefaultOne(final ASTNode location, final Annotation[] annotations, final long nullBits, final boolean useNullTypeAnnotations) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        final int thisDefault = this.getNullDefault();
        if (thisDefault != 0) {
            final boolean isRedundant = useNullTypeAnnotations ? (thisDefault == nullBits) : ((nullBits & 0x200000000000000L) != 0x0L);
            if (isRedundant) {
                this.scope.problemReporter().nullDefaultAnnotationIsRedundant(location, annotations, this);
            }
            return false;
        }
        return true;
    }
    
    @Override
    boolean hasNonNullDefaultFor(final int location, final boolean useTypeAnnotations) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (!useTypeAnnotations) {
            SourceTypeBinding currentType = null;
            for (Scope currentScope = this.scope; currentScope != null; currentScope = currentScope.parent) {
                switch (currentScope.kind) {
                    case 2: {
                        final AbstractMethodDeclaration referenceMethod = ((MethodScope)currentScope).referenceMethod();
                        if (referenceMethod == null || referenceMethod.binding == null) {
                            break;
                        }
                        final long methodTagBits = referenceMethod.binding.tagBits;
                        if ((methodTagBits & 0x200000000000000L) != 0x0L) {
                            return true;
                        }
                        if ((methodTagBits & 0x400000000000000L) != 0x0L) {
                            return false;
                        }
                        break;
                    }
                    case 3: {
                        currentType = ((ClassScope)currentScope).referenceContext.binding;
                        if (currentType == null) {
                            break;
                        }
                        final int foundDefaultNullness = currentType.getNullDefault();
                        if ((foundDefaultNullness & 0x3FA) > 2) {
                            return true;
                        }
                        if (foundDefaultNullness != 0) {
                            return foundDefaultNullness == 1;
                        }
                        break;
                    }
                }
            }
            return currentType != null && currentType.getPackage().defaultNullness == 1;
        }
        if (this.scope == null) {
            return (this.defaultNullness & location) != 0x0;
        }
        return this.scope.hasDefaultNullnessFor(location);
    }
    
    @Override
    public AnnotationHolder retrieveAnnotationHolder(final Binding binding, final boolean forceInitialization) {
        if (!this.isPrototype()) {
            return this.prototype.retrieveAnnotationHolder(binding, forceInitialization);
        }
        if (forceInitialization) {
            binding.getAnnotationTagBits();
        }
        return super.retrieveAnnotationHolder(binding, false);
    }
    
    @Override
    public void setContainerAnnotationType(final ReferenceBinding value) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        this.containerAnnotationType = value;
    }
    
    @Override
    public void tagAsHavingDefectiveContainerType() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.containerAnnotationType != null && this.containerAnnotationType.isValidBinding()) {
            this.containerAnnotationType = new ProblemReferenceBinding(this.containerAnnotationType.compoundName, this.containerAnnotationType, 22);
        }
    }
    
    public FieldBinding[] setFields(final FieldBinding[] fields) {
        if (!this.isPrototype()) {
            return this.prototype.setFields(fields);
        }
        if ((this.tagBits & 0x800000L) != 0x0L) {
            final TypeBinding[] annotatedTypes = this.scope.environment().getAnnotatedTypes(this);
            for (int i = 0, length = (annotatedTypes == null) ? 0 : annotatedTypes.length; i < length; ++i) {
                final SourceTypeBinding annotatedType = (SourceTypeBinding)annotatedTypes[i];
                annotatedType.fields = fields;
            }
        }
        return this.fields = fields;
    }
    
    public ReferenceBinding[] setMemberTypes(final ReferenceBinding[] memberTypes) {
        if (!this.isPrototype()) {
            return this.prototype.setMemberTypes(memberTypes);
        }
        this.memberTypes = memberTypes;
        if ((this.tagBits & 0x800000L) != 0x0L) {
            final TypeBinding[] annotatedTypes = this.scope.environment().getAnnotatedTypes(this);
            for (int i = 0, length = (annotatedTypes == null) ? 0 : annotatedTypes.length; i < length; ++i) {
                final SourceTypeBinding sourceTypeBinding;
                final SourceTypeBinding annotatedType = sourceTypeBinding = (SourceTypeBinding)annotatedTypes[i];
                sourceTypeBinding.tagBits |= 0x10000000L;
                annotatedType.memberTypes();
            }
        }
        return this.memberTypes;
    }
    
    public MethodBinding[] setMethods(final MethodBinding[] methods) {
        if (!this.isPrototype()) {
            return this.prototype.setMethods(methods);
        }
        if ((this.tagBits & 0x800000L) != 0x0L) {
            final TypeBinding[] annotatedTypes = this.scope.environment().getAnnotatedTypes(this);
            for (int i = 0, length = (annotatedTypes == null) ? 0 : annotatedTypes.length; i < length; ++i) {
                final SourceTypeBinding annotatedType = (SourceTypeBinding)annotatedTypes[i];
                annotatedType.methods = methods;
            }
        }
        return this.methods = methods;
    }
    
    public ReferenceBinding setSuperClass(final ReferenceBinding superClass) {
        if (!this.isPrototype()) {
            return this.prototype.setSuperClass(superClass);
        }
        if ((this.tagBits & 0x800000L) != 0x0L) {
            final TypeBinding[] annotatedTypes = this.scope.environment().getAnnotatedTypes(this);
            for (int i = 0, length = (annotatedTypes == null) ? 0 : annotatedTypes.length; i < length; ++i) {
                final SourceTypeBinding annotatedType = (SourceTypeBinding)annotatedTypes[i];
                annotatedType.superclass = superClass;
            }
        }
        return this.superclass = superClass;
    }
    
    public ReferenceBinding[] setSuperInterfaces(final ReferenceBinding[] superInterfaces) {
        if (!this.isPrototype()) {
            return this.prototype.setSuperInterfaces(superInterfaces);
        }
        if ((this.tagBits & 0x800000L) != 0x0L) {
            final TypeBinding[] annotatedTypes = this.scope.environment().getAnnotatedTypes(this);
            for (int i = 0, length = (annotatedTypes == null) ? 0 : annotatedTypes.length; i < length; ++i) {
                final SourceTypeBinding annotatedType = (SourceTypeBinding)annotatedTypes[i];
                annotatedType.superInterfaces = superInterfaces;
            }
        }
        return this.superInterfaces = superInterfaces;
    }
    
    public TypeVariableBinding[] setTypeVariables(final TypeVariableBinding[] typeVariables) {
        if (!this.isPrototype()) {
            return this.prototype.setTypeVariables(typeVariables);
        }
        if ((this.tagBits & 0x800000L) != 0x0L) {
            final TypeBinding[] annotatedTypes = this.scope.environment().getAnnotatedTypes(this);
            for (int i = 0, length = (annotatedTypes == null) ? 0 : annotatedTypes.length; i < length; ++i) {
                final SourceTypeBinding annotatedType = (SourceTypeBinding)annotatedTypes[i];
                annotatedType.typeVariables = typeVariables;
            }
        }
        return this.typeVariables = typeVariables;
    }
    
    public final int sourceEnd() {
        if (!this.isPrototype()) {
            return this.prototype.sourceEnd();
        }
        return this.scope.referenceContext.sourceEnd;
    }
    
    public final int sourceStart() {
        if (!this.isPrototype()) {
            return this.prototype.sourceStart();
        }
        return this.scope.referenceContext.sourceStart;
    }
    
    @Override
    SimpleLookupTable storedAnnotations(final boolean forceInitialize) {
        if (!this.isPrototype()) {
            return this.prototype.storedAnnotations(forceInitialize);
        }
        if (forceInitialize && this.storedAnnotations == null && this.scope != null) {
            this.scope.referenceCompilationUnit().compilationResult.hasAnnotations = true;
            final CompilerOptions globalOptions = this.scope.environment().globalOptions;
            if (!globalOptions.storeAnnotations) {
                return null;
            }
            this.storedAnnotations = new SimpleLookupTable(3);
        }
        return this.storedAnnotations;
    }
    
    @Override
    public ReferenceBinding superclass() {
        if (!this.isPrototype()) {
            return this.superclass = this.prototype.superclass();
        }
        return this.superclass;
    }
    
    @Override
    public ReferenceBinding[] superInterfaces() {
        if (!this.isPrototype()) {
            return this.superInterfaces = this.prototype.superInterfaces();
        }
        return (ReferenceBinding[])((this.superInterfaces != null) ? this.superInterfaces : (this.isAnnotationType() ? (this.superInterfaces = new ReferenceBinding[] { this.scope.getJavaLangAnnotationAnnotation() }) : null));
    }
    
    public SyntheticMethodBinding[] syntheticMethods() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null || this.synthetics[0] == null || this.synthetics[0].size() == 0) {
            return null;
        }
        int index = 0;
        SyntheticMethodBinding[] bindings = { null };
        for (final SyntheticMethodBinding[] methodAccessors : this.synthetics[0].values()) {
            for (int i = 0, max = methodAccessors.length; i < max; ++i) {
                if (methodAccessors[i] != null) {
                    if (index + 1 > bindings.length) {
                        System.arraycopy(bindings, 0, bindings = new SyntheticMethodBinding[index + 1], 0, index);
                    }
                    bindings[index++] = methodAccessors[i];
                }
            }
        }
        final int length;
        final SyntheticMethodBinding[] sortedBindings = new SyntheticMethodBinding[length = bindings.length];
        for (final SyntheticMethodBinding binding : bindings) {
            sortedBindings[binding.index] = binding;
        }
        return sortedBindings;
    }
    
    public FieldBinding[] syntheticFields() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            return null;
        }
        final int fieldSize = (this.synthetics[1] == null) ? 0 : this.synthetics[1].size();
        final int literalSize = (this.synthetics[2] == null) ? 0 : this.synthetics[2].size();
        final int totalSize = fieldSize + literalSize;
        if (totalSize == 0) {
            return null;
        }
        final FieldBinding[] bindings = new FieldBinding[totalSize];
        if (this.synthetics[1] != null) {
            final Iterator elements = this.synthetics[1].values().iterator();
            for (int i = 0; i < fieldSize; ++i) {
                final SyntheticFieldBinding synthBinding = elements.next();
                bindings[synthBinding.index] = synthBinding;
            }
        }
        if (this.synthetics[2] != null) {
            final Iterator elements = this.synthetics[2].values().iterator();
            for (int i = 0; i < literalSize; ++i) {
                final SyntheticFieldBinding synthBinding = elements.next();
                bindings[fieldSize + synthBinding.index] = synthBinding;
            }
        }
        return bindings;
    }
    
    @Override
    public String toString() {
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        final StringBuffer buffer = new StringBuffer(30);
        buffer.append("(id=");
        if (this.id == Integer.MAX_VALUE) {
            buffer.append("NoId");
        }
        else {
            buffer.append(this.id);
        }
        buffer.append(")\n");
        if (this.isDeprecated()) {
            buffer.append("deprecated ");
        }
        if (this.isPublic()) {
            buffer.append("public ");
        }
        if (this.isProtected()) {
            buffer.append("protected ");
        }
        if (this.isPrivate()) {
            buffer.append("private ");
        }
        if (this.isAbstract() && this.isClass()) {
            buffer.append("abstract ");
        }
        if (this.isStatic() && this.isNestedType()) {
            buffer.append("static ");
        }
        if (this.isFinal()) {
            buffer.append("final ");
        }
        if (this.isEnum()) {
            buffer.append("enum ");
        }
        else if (this.isAnnotationType()) {
            buffer.append("@interface ");
        }
        else if (this.isClass()) {
            buffer.append("class ");
        }
        else {
            buffer.append("interface ");
        }
        buffer.append((this.compoundName != null) ? CharOperation.toString(this.compoundName) : "UNNAMED TYPE");
        if (this.typeVariables == null) {
            buffer.append("<NULL TYPE VARIABLES>");
        }
        else if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
            buffer.append("<");
            for (int i = 0, length = this.typeVariables.length; i < length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                if (this.typeVariables[i] == null) {
                    buffer.append("NULL TYPE VARIABLE");
                }
                else {
                    final char[] varChars = this.typeVariables[i].toString().toCharArray();
                    buffer.append(varChars, 1, varChars.length - 2);
                }
            }
            buffer.append(">");
        }
        buffer.append("\n\textends ");
        buffer.append((this.superclass != null) ? this.superclass.debugName() : "NULL TYPE");
        if (this.superInterfaces != null) {
            if (this.superInterfaces != Binding.NO_SUPERINTERFACES) {
                buffer.append("\n\timplements : ");
                for (int i = 0, length = this.superInterfaces.length; i < length; ++i) {
                    if (i > 0) {
                        buffer.append(", ");
                    }
                    buffer.append((this.superInterfaces[i] != null) ? this.superInterfaces[i].debugName() : "NULL TYPE");
                }
            }
        }
        else {
            buffer.append("NULL SUPERINTERFACES");
        }
        if (this.enclosingType() != null) {
            buffer.append("\n\tenclosing type : ");
            buffer.append(this.enclosingType().debugName());
        }
        if (this.fields != null) {
            if (this.fields != Binding.NO_FIELDS) {
                buffer.append("\n/*   fields   */");
                for (int i = 0, length = this.fields.length; i < length; ++i) {
                    buffer.append('\n').append((this.fields[i] != null) ? this.fields[i].toString() : "NULL FIELD");
                }
            }
        }
        else {
            buffer.append("NULL FIELDS");
        }
        if (this.methods != null) {
            if (this.methods != Binding.NO_METHODS) {
                buffer.append("\n/*   methods   */");
                for (int i = 0, length = this.methods.length; i < length; ++i) {
                    buffer.append('\n').append((this.methods[i] != null) ? this.methods[i].toString() : "NULL METHOD");
                }
            }
        }
        else {
            buffer.append("NULL METHODS");
        }
        if (this.memberTypes != null) {
            if (this.memberTypes != Binding.NO_MEMBER_TYPES) {
                buffer.append("\n/*   members   */");
                for (int i = 0, length = this.memberTypes.length; i < length; ++i) {
                    buffer.append('\n').append((this.memberTypes[i] != null) ? this.memberTypes[i].toString() : "NULL TYPE");
                }
            }
        }
        else {
            buffer.append("NULL MEMBER TYPES");
        }
        buffer.append("\n\n");
        return buffer.toString();
    }
    
    @Override
    public TypeVariableBinding[] typeVariables() {
        if (!this.isPrototype()) {
            return this.typeVariables = this.prototype.typeVariables();
        }
        return (this.typeVariables != null) ? this.typeVariables : Binding.NO_TYPE_VARIABLES;
    }
    
    void verifyMethods(final MethodVerifier verifier) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        verifier.verify(this);
        int i = this.memberTypes.length;
        while (--i >= 0) {
            ((SourceTypeBinding)this.memberTypes[i]).verifyMethods(verifier);
        }
    }
    
    @Override
    public TypeBinding unannotated() {
        return this.prototype;
    }
    
    @Override
    public TypeBinding withoutToplevelNullAnnotation() {
        if (!this.hasNullTypeAnnotations()) {
            return this;
        }
        final AnnotationBinding[] newAnnotations = this.environment.filterNullTypeAnnotations(this.typeAnnotations);
        if (newAnnotations.length > 0) {
            return this.environment.createAnnotatedType(this.prototype, newAnnotations);
        }
        return this.prototype;
    }
    
    @Override
    public FieldBinding[] unResolvedFields() {
        if (!this.isPrototype()) {
            return this.prototype.unResolvedFields();
        }
        return this.fields;
    }
    
    public void tagIndirectlyAccessibleMembers() {
        if (!this.isPrototype()) {
            this.prototype.tagIndirectlyAccessibleMembers();
            return;
        }
        for (int i = 0; i < this.fields.length; ++i) {
            if (!this.fields[i].isPrivate()) {
                final FieldBinding fieldBinding = this.fields[i];
                fieldBinding.modifiers |= 0x8000000;
            }
        }
        for (int i = 0; i < this.memberTypes.length; ++i) {
            if (!this.memberTypes[i].isPrivate()) {
                final ReferenceBinding referenceBinding = this.memberTypes[i];
                referenceBinding.modifiers |= 0x8000000;
            }
        }
        if (this.superclass.isPrivate() && this.superclass instanceof SourceTypeBinding) {
            ((SourceTypeBinding)this.superclass).tagIndirectlyAccessibleMembers();
        }
    }
}
