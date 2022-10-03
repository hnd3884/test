package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import java.util.Iterator;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import java.util.Map;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import java.util.HashMap;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;

public class ClassScope extends Scope
{
    public TypeDeclaration referenceContext;
    public TypeReference superTypeReference;
    ArrayList<Object> deferredBoundChecks;
    
    public ClassScope(final Scope parent, final TypeDeclaration context) {
        super(3, parent);
        this.referenceContext = context;
        this.deferredBoundChecks = null;
    }
    
    void buildAnonymousTypeBinding(final SourceTypeBinding enclosingType, final ReferenceBinding supertype) {
        final LocalTypeBinding buildLocalType;
        final LocalTypeBinding anonymousType = buildLocalType = this.buildLocalType(enclosingType, enclosingType.fPackage);
        buildLocalType.modifiers |= 0x8000000;
        int inheritedBits = supertype.typeBits;
        if ((inheritedBits & 0x4) != 0x0) {
            final AbstractMethodDeclaration[] methods = this.referenceContext.methods;
            if (methods != null) {
                for (int i = 0; i < methods.length; ++i) {
                    if (CharOperation.equals(TypeConstants.CLOSE, methods[i].selector) && methods[i].arguments == null) {
                        inheritedBits &= 0x13;
                        break;
                    }
                }
            }
        }
        final LocalTypeBinding localTypeBinding = anonymousType;
        localTypeBinding.typeBits |= inheritedBits;
        if (supertype.isInterface()) {
            anonymousType.setSuperClass(this.getJavaLangObject());
            anonymousType.setSuperInterfaces(new ReferenceBinding[] { supertype });
            final TypeReference typeReference = this.referenceContext.allocation.type;
            if (typeReference != null) {
                this.referenceContext.superInterfaces = new TypeReference[] { typeReference };
                if ((supertype.tagBits & 0x40000000L) != 0x0L) {
                    this.problemReporter().superTypeCannotUseWildcard(anonymousType, typeReference, supertype);
                    final LocalTypeBinding localTypeBinding2 = anonymousType;
                    localTypeBinding2.tagBits |= 0x20000L;
                    anonymousType.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
                }
            }
        }
        else {
            anonymousType.setSuperClass(supertype);
            anonymousType.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
            final TypeReference typeReference = this.referenceContext.allocation.type;
            if (typeReference != null) {
                this.referenceContext.superclass = typeReference;
                if (supertype.erasure().id == 41) {
                    this.problemReporter().cannotExtendEnum(anonymousType, typeReference, supertype);
                    final LocalTypeBinding localTypeBinding3 = anonymousType;
                    localTypeBinding3.tagBits |= 0x20000L;
                    anonymousType.setSuperClass(this.getJavaLangObject());
                }
                else if (supertype.isFinal()) {
                    this.problemReporter().anonymousClassCannotExtendFinalClass(typeReference, supertype);
                    final LocalTypeBinding localTypeBinding4 = anonymousType;
                    localTypeBinding4.tagBits |= 0x20000L;
                    anonymousType.setSuperClass(this.getJavaLangObject());
                }
                else if ((supertype.tagBits & 0x40000000L) != 0x0L) {
                    this.problemReporter().superTypeCannotUseWildcard(anonymousType, typeReference, supertype);
                    final LocalTypeBinding localTypeBinding5 = anonymousType;
                    localTypeBinding5.tagBits |= 0x20000L;
                    anonymousType.setSuperClass(this.getJavaLangObject());
                }
            }
        }
        this.connectMemberTypes();
        this.buildFieldsAndMethods();
        anonymousType.faultInTypesForFieldsAndMethods();
        anonymousType.verifyMethods(this.environment().methodVerifier());
    }
    
    void buildFields() {
        final SourceTypeBinding sourceType = this.referenceContext.binding;
        if (sourceType.areFieldsInitialized()) {
            return;
        }
        if (this.referenceContext.fields == null) {
            sourceType.setFields(Binding.NO_FIELDS);
            return;
        }
        final FieldDeclaration[] fields = this.referenceContext.fields;
        final int size = fields.length;
        int count = 0;
        for (int i = 0; i < size; ++i) {
            switch (fields[i].getKind()) {
                case 1:
                case 3: {
                    ++count;
                    break;
                }
            }
        }
        FieldBinding[] fieldBindings = new FieldBinding[count];
        final HashtableOfObject knownFieldNames = new HashtableOfObject(count);
        count = 0;
        for (int j = 0; j < size; ++j) {
            final FieldDeclaration field = fields[j];
            if (field.getKind() != 2) {
                final FieldBinding fieldBinding = new FieldBinding(field, null, field.modifiers | 0x2000000, sourceType);
                fieldBinding.id = count;
                this.checkAndSetModifiersForField(fieldBinding, field);
                if (knownFieldNames.containsKey(field.name)) {
                    final FieldBinding previousBinding = (FieldBinding)knownFieldNames.get(field.name);
                    if (previousBinding != null) {
                        for (final FieldDeclaration previousField : fields) {
                            if (previousField.binding == previousBinding) {
                                this.problemReporter().duplicateFieldInType(sourceType, previousField);
                                break;
                            }
                        }
                    }
                    knownFieldNames.put(field.name, null);
                    this.problemReporter().duplicateFieldInType(sourceType, field);
                    field.binding = null;
                }
                else {
                    knownFieldNames.put(field.name, fieldBinding);
                    fieldBindings[count++] = fieldBinding;
                }
            }
        }
        if (count != fieldBindings.length) {
            System.arraycopy(fieldBindings, 0, fieldBindings = new FieldBinding[count], 0, count);
        }
        final SourceTypeBinding sourceTypeBinding = sourceType;
        sourceTypeBinding.tagBits &= 0xFFFFFFFFFFFFCFFFL;
        sourceType.setFields(fieldBindings);
    }
    
    void buildFieldsAndMethods() {
        this.buildFields();
        this.buildMethods();
        final SourceTypeBinding sourceType = this.referenceContext.binding;
        if (!sourceType.isPrivate() && sourceType.superclass instanceof SourceTypeBinding && sourceType.superclass.isPrivate()) {
            ((SourceTypeBinding)sourceType.superclass).tagIndirectlyAccessibleMembers();
        }
        if (sourceType.isMemberType() && !sourceType.isLocalType()) {
            ((MemberTypeBinding)sourceType).checkSyntheticArgsAndFields();
        }
        final ReferenceBinding[] memberTypes = sourceType.memberTypes;
        for (int i = 0, length = memberTypes.length; i < length; ++i) {
            ((SourceTypeBinding)memberTypes[i]).scope.buildFieldsAndMethods();
        }
    }
    
    private LocalTypeBinding buildLocalType(final SourceTypeBinding enclosingType, final PackageBinding packageBinding) {
        this.referenceContext.scope = this;
        this.referenceContext.staticInitializerScope = new MethodScope(this, this.referenceContext, true);
        this.referenceContext.initializerScope = new MethodScope(this, this.referenceContext, false);
        final LocalTypeBinding localType = new LocalTypeBinding(this, enclosingType, this.innermostSwitchCase());
        this.referenceContext.binding = localType;
        this.checkAndSetModifiers();
        this.buildTypeVariables();
        ReferenceBinding[] memberTypeBindings = Binding.NO_MEMBER_TYPES;
        if (this.referenceContext.memberTypes != null) {
            final int size = this.referenceContext.memberTypes.length;
            memberTypeBindings = new ReferenceBinding[size];
            int count = 0;
        Label_0315:
            for (int i = 0; i < size; ++i) {
                final TypeDeclaration memberContext = this.referenceContext.memberTypes[i];
                switch (TypeDeclaration.kind(memberContext.modifiers)) {
                    case 2:
                    case 4: {
                        this.problemReporter().illegalLocalTypeDeclaration(memberContext);
                        break;
                    }
                    default: {
                        ReferenceBinding type = localType;
                        while (!CharOperation.equals(type.sourceName, memberContext.name)) {
                            type = type.enclosingType();
                            if (type == null) {
                                for (int j = 0; j < i; ++j) {
                                    if (CharOperation.equals(this.referenceContext.memberTypes[j].name, memberContext.name)) {
                                        this.problemReporter().duplicateNestedType(memberContext);
                                        continue Label_0315;
                                    }
                                }
                                final ClassScope memberScope = new ClassScope(this, this.referenceContext.memberTypes[i]);
                                final LocalTypeBinding memberBinding = memberScope.buildLocalType(localType, packageBinding);
                                memberBinding.setAsMemberType();
                                memberTypeBindings[count++] = memberBinding;
                                continue Label_0315;
                            }
                        }
                        this.problemReporter().typeCollidesWithEnclosingType(memberContext);
                        break;
                    }
                }
            }
            if (count != size) {
                System.arraycopy(memberTypeBindings, 0, memberTypeBindings = new ReferenceBinding[count], 0, count);
            }
        }
        localType.setMemberTypes(memberTypeBindings);
        return localType;
    }
    
    void buildLocalTypeBinding(final SourceTypeBinding enclosingType) {
        final LocalTypeBinding localType = this.buildLocalType(enclosingType, enclosingType.fPackage);
        this.connectTypeHierarchy();
        if (this.compilerOptions().sourceLevel >= 3211264L) {
            this.checkParameterizedTypeBounds();
            this.checkParameterizedSuperTypeCollisions();
        }
        this.buildFieldsAndMethods();
        localType.faultInTypesForFieldsAndMethods();
        this.referenceContext.binding.verifyMethods(this.environment().methodVerifier());
    }
    
    private void buildMemberTypes(final AccessRestriction accessRestriction) {
        final SourceTypeBinding sourceType = this.referenceContext.binding;
        ReferenceBinding[] memberTypeBindings = Binding.NO_MEMBER_TYPES;
        if (this.referenceContext.memberTypes != null) {
            final int length = this.referenceContext.memberTypes.length;
            memberTypeBindings = new ReferenceBinding[length];
            int count = 0;
        Label_0286:
            for (int i = 0; i < length; ++i) {
                final TypeDeclaration memberContext = this.referenceContext.memberTypes[i];
                if (this.environment().isProcessingAnnotations && this.environment().isMissingType(memberContext.name)) {
                    throw new SourceTypeCollisionException();
                }
                switch (TypeDeclaration.kind(memberContext.modifiers)) {
                    case 2:
                    case 4: {
                        if (sourceType.isNestedType() && sourceType.isClass() && !sourceType.isStatic()) {
                            this.problemReporter().illegalLocalTypeDeclaration(memberContext);
                            continue;
                        }
                        break;
                    }
                }
                ReferenceBinding type = sourceType;
                while (!CharOperation.equals(type.sourceName, memberContext.name)) {
                    type = type.enclosingType();
                    if (type == null) {
                        for (int j = 0; j < i; ++j) {
                            if (CharOperation.equals(this.referenceContext.memberTypes[j].name, memberContext.name)) {
                                this.problemReporter().duplicateNestedType(memberContext);
                                continue Label_0286;
                            }
                        }
                        final ClassScope memberScope = new ClassScope(this, memberContext);
                        memberTypeBindings[count++] = memberScope.buildType(sourceType, sourceType.fPackage, accessRestriction);
                        continue Label_0286;
                    }
                }
                this.problemReporter().typeCollidesWithEnclosingType(memberContext);
            }
            if (count != length) {
                System.arraycopy(memberTypeBindings, 0, memberTypeBindings = new ReferenceBinding[count], 0, count);
            }
        }
        sourceType.setMemberTypes(memberTypeBindings);
    }
    
    void buildMethods() {
        final SourceTypeBinding sourceType = this.referenceContext.binding;
        if (sourceType.areMethodsInitialized()) {
            return;
        }
        final boolean isEnum = TypeDeclaration.kind(this.referenceContext.modifiers) == 3;
        if (this.referenceContext.methods == null && !isEnum) {
            this.referenceContext.binding.setMethods(Binding.NO_METHODS);
            return;
        }
        final AbstractMethodDeclaration[] methods = this.referenceContext.methods;
        final int size = (methods == null) ? 0 : methods.length;
        int clinitIndex = -1;
        for (int i = 0; i < size; ++i) {
            if (methods[i].isClinit()) {
                clinitIndex = i;
                break;
            }
        }
        int count = isEnum ? 2 : 0;
        MethodBinding[] methodBindings = new MethodBinding[((clinitIndex == -1) ? size : (size - 1)) + count];
        if (isEnum) {
            methodBindings[0] = sourceType.addSyntheticEnumMethod(TypeConstants.VALUES);
            methodBindings[1] = sourceType.addSyntheticEnumMethod(TypeConstants.VALUEOF);
        }
        boolean hasNativeMethods = false;
        if (sourceType.isAbstract()) {
            for (int j = 0; j < size; ++j) {
                if (j != clinitIndex) {
                    final MethodScope scope = new MethodScope(this, methods[j], false);
                    final MethodBinding methodBinding = scope.createMethod(methods[j]);
                    if (methodBinding != null) {
                        methodBindings[count++] = methodBinding;
                        hasNativeMethods = (hasNativeMethods || methodBinding.isNative());
                    }
                }
            }
        }
        else {
            boolean hasAbstractMethods = false;
            for (int k = 0; k < size; ++k) {
                if (k != clinitIndex) {
                    final MethodScope scope2 = new MethodScope(this, methods[k], false);
                    final MethodBinding methodBinding2 = scope2.createMethod(methods[k]);
                    if (methodBinding2 != null) {
                        methodBindings[count++] = methodBinding2;
                        hasAbstractMethods = (hasAbstractMethods || methodBinding2.isAbstract());
                        hasNativeMethods = (hasNativeMethods || methodBinding2.isNative());
                    }
                }
            }
            if (hasAbstractMethods) {
                this.problemReporter().abstractMethodInConcreteClass(sourceType);
            }
        }
        if (count != methodBindings.length) {
            System.arraycopy(methodBindings, 0, methodBindings = new MethodBinding[count], 0, count);
        }
        final SourceTypeBinding sourceTypeBinding = sourceType;
        sourceTypeBinding.tagBits &= 0xFFFFFFFFFFFF3FFFL;
        sourceType.setMethods(methodBindings);
        if (hasNativeMethods) {
            for (int j = 0; j < methodBindings.length; ++j) {
                final MethodBinding methodBinding3 = methodBindings[j];
                methodBinding3.modifiers |= 0x8000000;
            }
            final FieldBinding[] fields = sourceType.unResolvedFields();
            for (int k = 0; k < fields.length; ++k) {
                final FieldBinding fieldBinding = fields[k];
                fieldBinding.modifiers |= 0x8000000;
            }
        }
        if (isEnum && this.compilerOptions().isAnnotationBasedNullAnalysisEnabled) {
            final LookupEnvironment environment = this.environment();
            ((SyntheticMethodBinding)methodBindings[0]).markNonNull(environment);
            ((SyntheticMethodBinding)methodBindings[1]).markNonNull(environment);
        }
    }
    
    SourceTypeBinding buildType(final SourceTypeBinding enclosingType, final PackageBinding packageBinding, final AccessRestriction accessRestriction) {
        this.referenceContext.scope = this;
        this.referenceContext.staticInitializerScope = new MethodScope(this, this.referenceContext, true);
        this.referenceContext.initializerScope = new MethodScope(this, this.referenceContext, false);
        if (enclosingType == null) {
            final char[][] className = CharOperation.arrayConcat(packageBinding.compoundName, this.referenceContext.name);
            this.referenceContext.binding = new SourceTypeBinding(className, packageBinding, this);
        }
        else {
            final char[][] className = CharOperation.deepCopy(enclosingType.compoundName);
            className[className.length - 1] = CharOperation.concat(className[className.length - 1], this.referenceContext.name, '$');
            final ReferenceBinding existingType = packageBinding.getType0(className[className.length - 1]);
            if (existingType != null && !(existingType instanceof UnresolvedReferenceBinding)) {
                this.parent.problemReporter().duplicateNestedType(this.referenceContext);
            }
            this.referenceContext.binding = new MemberTypeBinding(className, this, enclosingType);
        }
        final SourceTypeBinding sourceType = this.referenceContext.binding;
        this.environment().setAccessRestriction(sourceType, accessRestriction);
        final TypeParameter[] typeParameters = this.referenceContext.typeParameters;
        sourceType.typeVariables = (TypeVariableBinding[])((typeParameters == null || typeParameters.length == 0) ? Binding.NO_TYPE_VARIABLES : null);
        sourceType.fPackage.addType(sourceType);
        this.checkAndSetModifiers();
        this.buildTypeVariables();
        this.buildMemberTypes(accessRestriction);
        return sourceType;
    }
    
    private void buildTypeVariables() {
        final SourceTypeBinding sourceType = this.referenceContext.binding;
        final TypeParameter[] typeParameters = this.referenceContext.typeParameters;
        if (typeParameters == null || typeParameters.length == 0) {
            sourceType.setTypeVariables(Binding.NO_TYPE_VARIABLES);
            return;
        }
        sourceType.setTypeVariables(Binding.NO_TYPE_VARIABLES);
        if (sourceType.id == 1) {
            this.problemReporter().objectCannotBeGeneric(this.referenceContext);
            return;
        }
        sourceType.setTypeVariables(this.createTypeVariables(typeParameters, sourceType));
        final SourceTypeBinding sourceTypeBinding = sourceType;
        sourceTypeBinding.modifiers |= 0x40000000;
    }
    
    @Override
    void resolveTypeParameter(final TypeParameter typeParameter) {
        typeParameter.resolve(this);
    }
    
    private void checkAndSetModifiers() {
        final SourceTypeBinding sourceType = this.referenceContext.binding;
        int modifiers = sourceType.modifiers;
        if ((modifiers & 0x400000) != 0x0) {
            this.problemReporter().duplicateModifierForType(sourceType);
        }
        final ReferenceBinding enclosingType = sourceType.enclosingType();
        final boolean isMemberType = sourceType.isMemberType();
        if (isMemberType) {
            modifiers |= (enclosingType.modifiers & 0x40000800);
            if (enclosingType.isInterface()) {
                modifiers |= 0x1;
            }
            if (sourceType.isEnum()) {
                if (!enclosingType.isStatic()) {
                    this.problemReporter().nonStaticContextForEnumMemberType(sourceType);
                }
                else {
                    modifiers |= 0x8;
                }
            }
            else if (sourceType.isInterface()) {
                modifiers |= 0x8;
            }
        }
        else if (sourceType.isLocalType()) {
            if (sourceType.isEnum()) {
                this.problemReporter().illegalLocalTypeDeclaration(this.referenceContext);
                sourceType.modifiers = 0;
                return;
            }
            if (sourceType.isAnonymousType()) {
                modifiers |= 0x10;
                if (this.referenceContext.allocation.type == null) {
                    modifiers |= 0x4000;
                }
            }
            Scope scope = this;
            do {
                switch (scope.kind) {
                    case 2: {
                        MethodScope methodScope = (MethodScope)scope;
                        if (methodScope.isLambdaScope()) {
                            methodScope = methodScope.namedMethodScope();
                        }
                        if (methodScope.isInsideInitializer()) {
                            final SourceTypeBinding type = ((TypeDeclaration)methodScope.referenceContext).binding;
                            if (methodScope.initializedField != null) {
                                if (methodScope.initializedField.isViewedAsDeprecated() && !sourceType.isDeprecated()) {
                                    modifiers |= 0x200000;
                                    break;
                                }
                                break;
                            }
                            else {
                                if (type.isStrictfp()) {
                                    modifiers |= 0x800;
                                }
                                if (type.isViewedAsDeprecated() && !sourceType.isDeprecated()) {
                                    modifiers |= 0x200000;
                                    break;
                                }
                                break;
                            }
                        }
                        else {
                            final MethodBinding method = ((AbstractMethodDeclaration)methodScope.referenceContext).binding;
                            if (method == null) {
                                break;
                            }
                            if (method.isStrictfp()) {
                                modifiers |= 0x800;
                            }
                            if (method.isViewedAsDeprecated() && !sourceType.isDeprecated()) {
                                modifiers |= 0x200000;
                                break;
                            }
                            break;
                        }
                        break;
                    }
                    case 3: {
                        if (enclosingType.isStrictfp()) {
                            modifiers |= 0x800;
                        }
                        if (enclosingType.isViewedAsDeprecated() && !sourceType.isDeprecated()) {
                            modifiers |= 0x200000;
                            break;
                        }
                        break;
                    }
                }
                scope = scope.parent;
            } while (scope != null);
        }
        int realModifiers = modifiers & 0xFFFF;
        Label_1000: {
            if ((realModifiers & 0x200) != 0x0) {
                if (isMemberType) {
                    if ((realModifiers & 0xFFFFD1F0) != 0x0) {
                        if ((realModifiers & 0x2000) != 0x0) {
                            this.problemReporter().illegalModifierForAnnotationMemberType(sourceType);
                        }
                        else {
                            this.problemReporter().illegalModifierForMemberInterface(sourceType);
                        }
                    }
                }
                else if ((realModifiers & 0xFFFFD1FE) != 0x0) {
                    if ((realModifiers & 0x2000) != 0x0) {
                        this.problemReporter().illegalModifierForAnnotationType(sourceType);
                    }
                    else {
                        this.problemReporter().illegalModifierForInterface(sourceType);
                    }
                }
                if (sourceType.sourceName == TypeConstants.PACKAGE_INFO_NAME && this.compilerOptions().targetJDK > 3211264L) {
                    modifiers |= 0x1000;
                }
                modifiers |= 0x400;
            }
            else if ((realModifiers & 0x4000) != 0x0) {
                if (isMemberType) {
                    if ((realModifiers & 0xFFFFB7F0) != 0x0) {
                        this.problemReporter().illegalModifierForMemberEnum(sourceType);
                        modifiers &= 0xFFFFFBFF;
                        realModifiers &= 0xFFFFFBFF;
                    }
                }
                else if (!sourceType.isLocalType() && (realModifiers & 0xFFFFB7FE) != 0x0) {
                    this.problemReporter().illegalModifierForEnum(sourceType);
                }
                if (!sourceType.isAnonymousType()) {
                    Label_0834: {
                        if ((this.referenceContext.bits & 0x800) != 0x0) {
                            modifiers |= 0x400;
                        }
                        else {
                            final TypeDeclaration typeDeclaration = this.referenceContext;
                            final FieldDeclaration[] fields = typeDeclaration.fields;
                            final int fieldsLength = (fields == null) ? 0 : fields.length;
                            if (fieldsLength != 0) {
                                final AbstractMethodDeclaration[] methods = typeDeclaration.methods;
                                final int methodsLength = (methods == null) ? 0 : methods.length;
                                boolean definesAbstractMethod = typeDeclaration.superInterfaces != null;
                                for (int i = 0; i < methodsLength && !definesAbstractMethod; definesAbstractMethod = methods[i].isAbstract(), ++i) {}
                                if (definesAbstractMethod) {
                                    boolean needAbstractBit = false;
                                    for (final FieldDeclaration fieldDecl : fields) {
                                        if (fieldDecl.getKind() == 3) {
                                            if (!(fieldDecl.initialization instanceof QualifiedAllocationExpression)) {
                                                break Label_0834;
                                            }
                                            needAbstractBit = true;
                                        }
                                    }
                                    if (needAbstractBit) {
                                        modifiers |= 0x400;
                                    }
                                }
                            }
                        }
                    }
                    final TypeDeclaration typeDeclaration = this.referenceContext;
                    final FieldDeclaration[] fields = typeDeclaration.fields;
                    if (fields != null) {
                        for (int k = 0, fieldsLength2 = fields.length; k < fieldsLength2; ++k) {
                            final FieldDeclaration fieldDecl2 = fields[k];
                            if (fieldDecl2.getKind() == 3 && fieldDecl2.initialization instanceof QualifiedAllocationExpression) {
                                break Label_1000;
                            }
                        }
                    }
                    modifiers |= 0x10;
                }
            }
            else {
                if (isMemberType) {
                    if ((realModifiers & 0xFFFFF3E0) != 0x0) {
                        this.problemReporter().illegalModifierForMemberClass(sourceType);
                    }
                }
                else if (sourceType.isLocalType()) {
                    if ((realModifiers & 0xFFFFF3EF) != 0x0) {
                        this.problemReporter().illegalModifierForLocalClass(sourceType);
                    }
                }
                else if ((realModifiers & 0xFFFFF3EE) != 0x0) {
                    this.problemReporter().illegalModifierForClass(sourceType);
                }
                if ((realModifiers & 0x410) == 0x410) {
                    this.problemReporter().illegalModifierCombinationFinalAbstractForClass(sourceType);
                }
            }
        }
        if (isMemberType) {
            if (enclosingType.isInterface()) {
                if ((realModifiers & 0x6) != 0x0) {
                    this.problemReporter().illegalVisibilityModifierForInterfaceMemberType(sourceType);
                    if ((realModifiers & 0x4) != 0x0) {
                        modifiers &= 0xFFFFFFFB;
                    }
                    if ((realModifiers & 0x2) != 0x0) {
                        modifiers &= 0xFFFFFFFD;
                    }
                }
            }
            else {
                final int accessorBits = realModifiers & 0x7;
                if ((accessorBits & accessorBits - 1) > 1) {
                    this.problemReporter().illegalVisibilityModifierCombinationForMemberType(sourceType);
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
            }
            if ((realModifiers & 0x8) == 0x0) {
                if (enclosingType.isInterface()) {
                    modifiers |= 0x8;
                }
            }
            else if (!enclosingType.isStatic()) {
                this.problemReporter().illegalStaticModifierForMemberType(sourceType);
            }
        }
        sourceType.modifiers = modifiers;
    }
    
    private void checkAndSetModifiersForField(final FieldBinding fieldBinding, final FieldDeclaration fieldDecl) {
        int modifiers = fieldBinding.modifiers;
        final ReferenceBinding declaringClass = fieldBinding.declaringClass;
        if ((modifiers & 0x400000) != 0x0) {
            this.problemReporter().duplicateModifierForField(declaringClass, fieldDecl);
        }
        if (declaringClass.isInterface()) {
            modifiers |= 0x19;
            if ((modifiers & 0xFFFF) != 0x19) {
                if ((declaringClass.modifiers & 0x2000) != 0x0) {
                    this.problemReporter().illegalModifierForAnnotationField(fieldDecl);
                }
                else {
                    this.problemReporter().illegalModifierForInterfaceField(fieldDecl);
                }
            }
            fieldBinding.modifiers = modifiers;
            return;
        }
        if (fieldDecl.getKind() == 3) {
            if ((modifiers & 0xFFFF) != 0x0) {
                this.problemReporter().illegalModifierForEnumConstant(declaringClass, fieldDecl);
            }
            fieldBinding.modifiers |= 0x8004019;
            return;
        }
        final int realModifiers = modifiers & 0xFFFF;
        if ((realModifiers & 0xFFFFFF20) != 0x0) {
            this.problemReporter().illegalModifierForField(declaringClass, fieldDecl);
            modifiers &= 0xFFFF00DF;
        }
        final int accessorBits = realModifiers & 0x7;
        if ((accessorBits & accessorBits - 1) > 1) {
            this.problemReporter().illegalVisibilityModifierCombinationForField(declaringClass, fieldDecl);
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
        if ((realModifiers & 0x50) == 0x50) {
            this.problemReporter().illegalModifierCombinationFinalVolatileForField(declaringClass, fieldDecl);
        }
        if (fieldDecl.initialization == null && (modifiers & 0x10) != 0x0) {
            modifiers |= 0x4000000;
        }
        fieldBinding.modifiers = modifiers;
    }
    
    public void checkParameterizedSuperTypeCollisions() {
        final SourceTypeBinding sourceType = this.referenceContext.binding;
        final ReferenceBinding[] interfaces = sourceType.superInterfaces;
        final Map invocations = new HashMap(2);
        final ReferenceBinding itsSuperclass = sourceType.isInterface() ? null : sourceType.superclass;
        for (int i = 0, length = interfaces.length; i < length; ++i) {
            final ReferenceBinding one = interfaces[i];
            if (one != null) {
                if (itsSuperclass == null || !this.hasErasedCandidatesCollisions(itsSuperclass, one, invocations, sourceType, this.referenceContext)) {
                    for (final ReferenceBinding two : interfaces) {
                        if (two != null) {
                            if (this.hasErasedCandidatesCollisions(one, two, invocations, sourceType, this.referenceContext)) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        final TypeParameter[] typeParameters = this.referenceContext.typeParameters;
    Label_0356:
        for (int k = 0, paramLength = (typeParameters == null) ? 0 : typeParameters.length; k < paramLength; ++k) {
            final TypeParameter typeParameter = typeParameters[k];
            final TypeVariableBinding typeVariable = typeParameter.binding;
            if (typeVariable != null) {
                if (typeVariable.isValidBinding()) {
                    final TypeReference[] boundRefs = typeParameter.bounds;
                    if (boundRefs != null) {
                        final boolean checkSuperclass = TypeBinding.equalsEquals(typeVariable.firstBound, typeVariable.superclass);
                        for (int l = 0, boundLength = boundRefs.length; l < boundLength; ++l) {
                            final TypeReference typeRef = boundRefs[l];
                            final TypeBinding superType = typeRef.resolvedType;
                            if (superType != null) {
                                if (superType.isValidBinding()) {
                                    if (checkSuperclass && this.hasErasedCandidatesCollisions(superType, typeVariable.superclass, invocations, typeVariable, typeRef)) {
                                        break;
                                    }
                                    int index = typeVariable.superInterfaces.length;
                                    while (--index >= 0) {
                                        if (this.hasErasedCandidatesCollisions(superType, typeVariable.superInterfaces[index], invocations, typeVariable, typeRef)) {
                                            continue Label_0356;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        final ReferenceBinding[] memberTypes = this.referenceContext.binding.memberTypes;
        if (memberTypes != null && memberTypes != Binding.NO_MEMBER_TYPES) {
            for (int m = 0, size = memberTypes.length; m < size; ++m) {
                ((SourceTypeBinding)memberTypes[m]).scope.checkParameterizedSuperTypeCollisions();
            }
        }
    }
    
    private void checkForInheritedMemberTypes(final SourceTypeBinding sourceType) {
        ReferenceBinding currentType = sourceType;
        ReferenceBinding[] interfacesToVisit = null;
        int nextPosition = 0;
        while (!currentType.hasMemberTypes()) {
            final ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
            if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                if (interfacesToVisit == null) {
                    interfacesToVisit = itsInterfaces;
                    nextPosition = interfacesToVisit.length;
                }
                else {
                    final int itsLength = itsInterfaces.length;
                    if (nextPosition + itsLength >= interfacesToVisit.length) {
                        System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
                    }
                    int a = 0;
                Label_0139:
                    while (a < itsLength) {
                        final ReferenceBinding next = itsInterfaces[a];
                        while (true) {
                            for (int b = 0; b < nextPosition; ++b) {
                                if (TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                    ++a;
                                    continue Label_0139;
                                }
                            }
                            interfacesToVisit[nextPosition++] = next;
                            continue;
                        }
                    }
                }
            }
            if ((currentType = currentType.superclass()) == null || (currentType.tagBits & 0x10000L) != 0x0L) {
                if (interfacesToVisit != null) {
                    boolean needToTag = false;
                    for (int i = 0; i < nextPosition; ++i) {
                        final ReferenceBinding anInterface = interfacesToVisit[i];
                        if ((anInterface.tagBits & 0x10000L) == 0x0L) {
                            if (anInterface.hasMemberTypes()) {
                                return;
                            }
                            needToTag = true;
                            final ReferenceBinding[] itsInterfaces2 = anInterface.superInterfaces();
                            if (itsInterfaces2 != null && itsInterfaces2 != Binding.NO_SUPERINTERFACES) {
                                final int itsLength2 = itsInterfaces2.length;
                                if (nextPosition + itsLength2 >= interfacesToVisit.length) {
                                    System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength2 + 5], 0, nextPosition);
                                }
                                int a2 = 0;
                            Label_0324:
                                while (a2 < itsLength2) {
                                    final ReferenceBinding next2 = itsInterfaces2[a2];
                                    while (true) {
                                        for (int b2 = 0; b2 < nextPosition; ++b2) {
                                            if (TypeBinding.equalsEquals(next2, interfacesToVisit[b2])) {
                                                ++a2;
                                                continue Label_0324;
                                            }
                                        }
                                        interfacesToVisit[nextPosition++] = next2;
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                    if (needToTag) {
                        for (final ReferenceBinding referenceBinding : interfacesToVisit) {
                            referenceBinding.tagBits |= 0x10000L;
                        }
                    }
                }
                currentType = sourceType;
                do {
                    final ReferenceBinding referenceBinding2 = currentType;
                    referenceBinding2.tagBits |= 0x10000L;
                } while ((currentType = currentType.superclass()) != null && (currentType.tagBits & 0x10000L) == 0x0L);
            }
        }
    }
    
    public void checkParameterizedTypeBounds() {
        for (int i = 0, l = (this.deferredBoundChecks == null) ? 0 : this.deferredBoundChecks.size(); i < l; ++i) {
            final Object toCheck = this.deferredBoundChecks.get(i);
            if (toCheck instanceof TypeReference) {
                ((TypeReference)toCheck).checkBounds(this);
            }
            else if (toCheck instanceof Runnable) {
                ((Runnable)toCheck).run();
            }
        }
        this.deferredBoundChecks = null;
        final ReferenceBinding[] memberTypes = this.referenceContext.binding.memberTypes;
        if (memberTypes != null && memberTypes != Binding.NO_MEMBER_TYPES) {
            for (int j = 0, size = memberTypes.length; j < size; ++j) {
                ((SourceTypeBinding)memberTypes[j]).scope.checkParameterizedTypeBounds();
            }
        }
    }
    
    private void connectMemberTypes() {
        final SourceTypeBinding sourceType = this.referenceContext.binding;
        final ReferenceBinding[] memberTypes = sourceType.memberTypes;
        if (memberTypes != null && memberTypes != Binding.NO_MEMBER_TYPES) {
            for (int i = 0, size = memberTypes.length; i < size; ++i) {
                ((SourceTypeBinding)memberTypes[i]).scope.connectTypeHierarchy();
            }
        }
    }
    
    private boolean connectSuperclass() {
        final SourceTypeBinding sourceType = this.referenceContext.binding;
        if (sourceType.id == 1) {
            sourceType.setSuperClass(null);
            sourceType.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
            if (!sourceType.isClass()) {
                this.problemReporter().objectMustBeClass(sourceType);
            }
            if (this.referenceContext.superclass != null || (this.referenceContext.superInterfaces != null && this.referenceContext.superInterfaces.length > 0)) {
                this.problemReporter().objectCannotHaveSuperTypes(sourceType);
            }
            return true;
        }
        if (this.referenceContext.superclass != null) {
            final TypeReference superclassRef = this.referenceContext.superclass;
            final ReferenceBinding superclass = this.findSupertype(superclassRef);
            if (superclass != null) {
                if (!superclass.isClass() && (superclass.tagBits & 0x80L) == 0x0L) {
                    this.problemReporter().superclassMustBeAClass(sourceType, superclassRef, superclass);
                }
                else if (superclass.isFinal()) {
                    this.problemReporter().classExtendFinalClass(sourceType, superclassRef, superclass);
                }
                else if ((superclass.tagBits & 0x40000000L) != 0x0L) {
                    this.problemReporter().superTypeCannotUseWildcard(sourceType, superclassRef, superclass);
                }
                else if (superclass.erasure().id == 41) {
                    this.problemReporter().cannotExtendEnum(sourceType, superclassRef, superclass);
                }
                else {
                    if ((superclass.tagBits & 0x20000L) != 0x0L || !superclassRef.resolvedType.isValidBinding()) {
                        sourceType.setSuperClass(superclass);
                        final SourceTypeBinding sourceTypeBinding = sourceType;
                        sourceTypeBinding.tagBits |= 0x20000L;
                        return superclassRef.resolvedType.isValidBinding();
                    }
                    sourceType.setSuperClass(superclass);
                    final SourceTypeBinding sourceTypeBinding2 = sourceType;
                    sourceTypeBinding2.typeBits |= (superclass.typeBits & 0x13);
                    if ((sourceType.typeBits & 0x3) != 0x0) {
                        final SourceTypeBinding sourceTypeBinding3 = sourceType;
                        sourceTypeBinding3.typeBits |= sourceType.applyCloseableClassWhitelists();
                    }
                    return true;
                }
            }
            final SourceTypeBinding sourceTypeBinding4 = sourceType;
            sourceTypeBinding4.tagBits |= 0x20000L;
            sourceType.setSuperClass(this.getJavaLangObject());
            if ((sourceType.superclass.tagBits & 0x100L) == 0x0L) {
                this.detectHierarchyCycle(sourceType, sourceType.superclass, null);
            }
            return false;
        }
        if (sourceType.isEnum() && this.compilerOptions().sourceLevel >= 3211264L) {
            return this.connectEnumSuperclass();
        }
        sourceType.setSuperClass(this.getJavaLangObject());
        return !this.detectHierarchyCycle(sourceType, sourceType.superclass, null);
    }
    
    private boolean connectEnumSuperclass() {
        final SourceTypeBinding sourceType = this.referenceContext.binding;
        final ReferenceBinding rootEnumType = this.getJavaLangEnum();
        if ((rootEnumType.tagBits & 0x80L) != 0x0L) {
            final SourceTypeBinding sourceTypeBinding = sourceType;
            sourceTypeBinding.tagBits |= 0x20000L;
            sourceType.setSuperClass(rootEnumType);
            return false;
        }
        final boolean foundCycle = this.detectHierarchyCycle(sourceType, rootEnumType, null);
        final TypeVariableBinding[] refTypeVariables = rootEnumType.typeVariables();
        if (refTypeVariables == Binding.NO_TYPE_VARIABLES) {
            this.problemReporter().nonGenericTypeCannotBeParameterized(0, null, rootEnumType, new TypeBinding[] { sourceType });
            return false;
        }
        if (1 != refTypeVariables.length) {
            this.problemReporter().incorrectArityForParameterizedType(null, rootEnumType, new TypeBinding[] { sourceType });
            return false;
        }
        final ParameterizedTypeBinding superType = this.environment().createParameterizedType(rootEnumType, new TypeBinding[] { this.environment().convertToRawType(sourceType, false) }, null);
        final SourceTypeBinding sourceTypeBinding2 = sourceType;
        sourceTypeBinding2.tagBits |= (superType.tagBits & 0x20000L);
        sourceType.setSuperClass(superType);
        if (!refTypeVariables[0].boundCheck(superType, sourceType, this, null).isOKbyJLS()) {
            this.problemReporter().typeMismatchError(rootEnumType, refTypeVariables[0], sourceType, null);
        }
        return !foundCycle;
    }
    
    private boolean connectSuperInterfaces() {
        final SourceTypeBinding sourceType = this.referenceContext.binding;
        sourceType.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
        if (this.referenceContext.superInterfaces == null) {
            if (sourceType.isAnnotationType() && this.compilerOptions().sourceLevel >= 3211264L) {
                final ReferenceBinding annotationType = this.getJavaLangAnnotationAnnotation();
                final boolean foundCycle = this.detectHierarchyCycle(sourceType, annotationType, null);
                sourceType.setSuperInterfaces(new ReferenceBinding[] { annotationType });
                return !foundCycle;
            }
            return true;
        }
        else {
            if (sourceType.id == 1) {
                return true;
            }
            boolean noProblems = true;
            final int length = this.referenceContext.superInterfaces.length;
            ReferenceBinding[] interfaceBindings = new ReferenceBinding[length];
            int count = 0;
        Label_0432:
            for (int i = 0; i < length; ++i) {
                final TypeReference superInterfaceRef = this.referenceContext.superInterfaces[i];
                final ReferenceBinding superInterface = this.findSupertype(superInterfaceRef);
                if (superInterface == null) {
                    final SourceTypeBinding sourceTypeBinding = sourceType;
                    sourceTypeBinding.tagBits |= 0x20000L;
                    noProblems = false;
                }
                else {
                    for (int j = 0; j < i; ++j) {
                        if (TypeBinding.equalsEquals(interfaceBindings[j], superInterface)) {
                            this.problemReporter().duplicateSuperinterface(sourceType, superInterfaceRef, superInterface);
                            final SourceTypeBinding sourceTypeBinding2 = sourceType;
                            sourceTypeBinding2.tagBits |= 0x20000L;
                            noProblems = false;
                            continue Label_0432;
                        }
                    }
                    if (!superInterface.isInterface() && (superInterface.tagBits & 0x80L) == 0x0L) {
                        this.problemReporter().superinterfaceMustBeAnInterface(sourceType, superInterfaceRef, superInterface);
                        final SourceTypeBinding sourceTypeBinding3 = sourceType;
                        sourceTypeBinding3.tagBits |= 0x20000L;
                        noProblems = false;
                    }
                    else {
                        if (superInterface.isAnnotationType()) {
                            this.problemReporter().annotationTypeUsedAsSuperinterface(sourceType, superInterfaceRef, superInterface);
                        }
                        if ((superInterface.tagBits & 0x40000000L) != 0x0L) {
                            this.problemReporter().superTypeCannotUseWildcard(sourceType, superInterfaceRef, superInterface);
                            final SourceTypeBinding sourceTypeBinding4 = sourceType;
                            sourceTypeBinding4.tagBits |= 0x20000L;
                            noProblems = false;
                        }
                        else {
                            if ((superInterface.tagBits & 0x20000L) != 0x0L || !superInterfaceRef.resolvedType.isValidBinding()) {
                                final SourceTypeBinding sourceTypeBinding5 = sourceType;
                                sourceTypeBinding5.tagBits |= 0x20000L;
                                noProblems &= superInterfaceRef.resolvedType.isValidBinding();
                            }
                            final SourceTypeBinding sourceTypeBinding6 = sourceType;
                            sourceTypeBinding6.typeBits |= (superInterface.typeBits & 0x13);
                            if ((sourceType.typeBits & 0x3) != 0x0) {
                                final SourceTypeBinding sourceTypeBinding7 = sourceType;
                                sourceTypeBinding7.typeBits |= sourceType.applyCloseableInterfaceWhitelists();
                            }
                            interfaceBindings[count++] = superInterface;
                        }
                    }
                }
            }
            if (count > 0) {
                if (count != length) {
                    System.arraycopy(interfaceBindings, 0, interfaceBindings = new ReferenceBinding[count], 0, count);
                }
                sourceType.setSuperInterfaces(interfaceBindings);
            }
            return noProblems;
        }
    }
    
    void connectTypeHierarchy() {
        final SourceTypeBinding sourceType = this.referenceContext.binding;
        final CompilationUnitScope compilationUnitScope = this.compilationUnitScope();
        final boolean wasAlreadyConnecting = compilationUnitScope.connectingHierarchy;
        compilationUnitScope.connectingHierarchy = true;
        try {
            if ((sourceType.tagBits & 0x100L) == 0x0L) {
                final SourceTypeBinding sourceTypeBinding = sourceType;
                sourceTypeBinding.tagBits |= 0x100L;
                this.environment().typesBeingConnected.add(sourceType);
                boolean noProblems = this.connectSuperclass();
                noProblems &= this.connectSuperInterfaces();
                this.environment().typesBeingConnected.remove(sourceType);
                final SourceTypeBinding sourceTypeBinding2 = sourceType;
                sourceTypeBinding2.tagBits |= 0x200L;
                noProblems &= this.connectTypeVariables(this.referenceContext.typeParameters, false);
                final SourceTypeBinding sourceTypeBinding3 = sourceType;
                sourceTypeBinding3.tagBits |= 0x40000L;
                if (noProblems && sourceType.isHierarchyInconsistent()) {
                    this.problemReporter().hierarchyHasProblems(sourceType);
                }
            }
            this.connectMemberTypes();
        }
        finally {
            compilationUnitScope.connectingHierarchy = wasAlreadyConnecting;
        }
        compilationUnitScope.connectingHierarchy = wasAlreadyConnecting;
        final LookupEnvironment env = this.environment();
        try {
            env.missingClassFileLocation = this.referenceContext;
            this.checkForInheritedMemberTypes(sourceType);
        }
        catch (final AbortCompilation e) {
            e.updateContext(this.referenceContext, this.referenceCompilationUnit().compilationResult);
            throw e;
        }
        finally {
            env.missingClassFileLocation = null;
        }
        env.missingClassFileLocation = null;
    }
    
    @Override
    public boolean deferCheck(final Runnable check) {
        if (this.compilationUnitScope().connectingHierarchy) {
            if (this.deferredBoundChecks == null) {
                this.deferredBoundChecks = new ArrayList<Object>();
            }
            this.deferredBoundChecks.add(check);
            return true;
        }
        return false;
    }
    
    private void connectTypeHierarchyWithoutMembers() {
        if (this.parent instanceof CompilationUnitScope) {
            if (((CompilationUnitScope)this.parent).imports == null) {
                ((CompilationUnitScope)this.parent).checkAndSetImports();
            }
        }
        else if (this.parent instanceof ClassScope) {
            ((ClassScope)this.parent).connectTypeHierarchyWithoutMembers();
        }
        final SourceTypeBinding sourceType = this.referenceContext.binding;
        if ((sourceType.tagBits & 0x100L) != 0x0L) {
            return;
        }
        final CompilationUnitScope compilationUnitScope = this.compilationUnitScope();
        final boolean wasAlreadyConnecting = compilationUnitScope.connectingHierarchy;
        compilationUnitScope.connectingHierarchy = true;
        try {
            final SourceTypeBinding sourceTypeBinding = sourceType;
            sourceTypeBinding.tagBits |= 0x100L;
            this.environment().typesBeingConnected.add(sourceType);
            boolean noProblems = this.connectSuperclass();
            noProblems &= this.connectSuperInterfaces();
            this.environment().typesBeingConnected.remove(sourceType);
            final SourceTypeBinding sourceTypeBinding2 = sourceType;
            sourceTypeBinding2.tagBits |= 0x200L;
            noProblems &= this.connectTypeVariables(this.referenceContext.typeParameters, false);
            final SourceTypeBinding sourceTypeBinding3 = sourceType;
            sourceTypeBinding3.tagBits |= 0x40000L;
            if (noProblems && sourceType.isHierarchyInconsistent()) {
                this.problemReporter().hierarchyHasProblems(sourceType);
            }
        }
        finally {
            compilationUnitScope.connectingHierarchy = wasAlreadyConnecting;
        }
        compilationUnitScope.connectingHierarchy = wasAlreadyConnecting;
    }
    
    public boolean detectHierarchyCycle(TypeBinding superType, final TypeReference reference) {
        if (!(superType instanceof ReferenceBinding)) {
            return false;
        }
        if (reference != this.superTypeReference) {
            if ((superType.tagBits & 0x100L) == 0x0L && superType instanceof SourceTypeBinding) {
                ((SourceTypeBinding)superType).scope.connectTypeHierarchyWithoutMembers();
            }
            return false;
        }
        if (superType.isTypeVariable()) {
            return false;
        }
        if (superType.isParameterizedType()) {
            superType = ((ParameterizedTypeBinding)superType).genericType();
        }
        this.compilationUnitScope().recordSuperTypeReference(superType);
        return this.detectHierarchyCycle(this.referenceContext.binding, (ReferenceBinding)superType, reference);
    }
    
    private boolean detectHierarchyCycle(final SourceTypeBinding sourceType, ReferenceBinding superType, final TypeReference reference) {
        if (superType.isRawType()) {
            superType = ((RawTypeBinding)superType).genericType();
        }
        if (TypeBinding.equalsEquals(sourceType, superType)) {
            this.problemReporter().hierarchyCircularity(sourceType, superType, reference);
            sourceType.tagBits |= 0x20000L;
            return true;
        }
        Label_0126: {
            if (superType.isMemberType()) {
                ReferenceBinding current = superType.enclosingType();
                while (!current.isHierarchyBeingActivelyConnected() || !TypeBinding.equalsEquals(current, sourceType)) {
                    if ((current = current.enclosingType()) == null) {
                        break Label_0126;
                    }
                }
                this.problemReporter().hierarchyCircularity(sourceType, current, reference);
                sourceType.tagBits |= 0x20000L;
                final ReferenceBinding referenceBinding = current;
                referenceBinding.tagBits |= 0x20000L;
                return true;
            }
        }
        if (!superType.isBinaryBinding()) {
            if (superType.isHierarchyBeingActivelyConnected()) {
                final TypeReference ref = ((SourceTypeBinding)superType).scope.superTypeReference;
                if (ref != null && ref.resolvedType != null && ((ReferenceBinding)ref.resolvedType).isHierarchyBeingActivelyConnected()) {
                    this.problemReporter().hierarchyCircularity(sourceType, superType, reference);
                    sourceType.tagBits |= 0x20000L;
                    final ReferenceBinding referenceBinding2 = superType;
                    referenceBinding2.tagBits |= 0x20000L;
                    return true;
                }
                if (ref != null && ref.resolvedType == null) {
                    final char[] referredName = ref.getLastToken();
                    for (final SourceTypeBinding type : this.environment().typesBeingConnected) {
                        if (CharOperation.equals(referredName, type.sourceName())) {
                            this.problemReporter().hierarchyCircularity(sourceType, superType, reference);
                            sourceType.tagBits |= 0x20000L;
                            final ReferenceBinding referenceBinding3 = superType;
                            referenceBinding3.tagBits |= 0x20000L;
                            return true;
                        }
                    }
                }
            }
            if ((superType.tagBits & 0x100L) == 0x0L) {
                ((SourceTypeBinding)superType).scope.connectTypeHierarchyWithoutMembers();
            }
            if ((superType.tagBits & 0x20000L) != 0x0L) {
                sourceType.tagBits |= 0x20000L;
            }
            return false;
        }
        if (superType.problemId() != 1 && (superType.tagBits & 0x20000L) != 0x0L) {
            sourceType.tagBits |= 0x20000L;
            this.problemReporter().hierarchyHasProblems(sourceType);
            return true;
        }
        boolean hasCycle = false;
        ReferenceBinding parentType = superType.superclass();
        if (parentType != null) {
            if (TypeBinding.equalsEquals(sourceType, parentType)) {
                this.problemReporter().hierarchyCircularity(sourceType, superType, reference);
                sourceType.tagBits |= 0x20000L;
                final ReferenceBinding referenceBinding4 = superType;
                referenceBinding4.tagBits |= 0x20000L;
                return true;
            }
            if (parentType.isParameterizedType()) {
                parentType = ((ParameterizedTypeBinding)parentType).genericType();
            }
            hasCycle |= this.detectHierarchyCycle(sourceType, parentType, reference);
            if ((parentType.tagBits & 0x20000L) != 0x0L) {
                sourceType.tagBits |= 0x20000L;
                final ReferenceBinding referenceBinding5 = parentType;
                referenceBinding5.tagBits |= 0x20000L;
            }
        }
        final ReferenceBinding[] itsInterfaces = superType.superInterfaces();
        if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
            for (int i = 0, length = itsInterfaces.length; i < length; ++i) {
                ReferenceBinding anInterface = itsInterfaces[i];
                if (TypeBinding.equalsEquals(sourceType, anInterface)) {
                    this.problemReporter().hierarchyCircularity(sourceType, superType, reference);
                    sourceType.tagBits |= 0x20000L;
                    final ReferenceBinding referenceBinding6 = superType;
                    referenceBinding6.tagBits |= 0x20000L;
                    return true;
                }
                if (anInterface.isParameterizedType()) {
                    anInterface = ((ParameterizedTypeBinding)anInterface).genericType();
                }
                hasCycle |= this.detectHierarchyCycle(sourceType, anInterface, reference);
                if ((anInterface.tagBits & 0x20000L) != 0x0L) {
                    sourceType.tagBits |= 0x20000L;
                    final ReferenceBinding referenceBinding7 = superType;
                    referenceBinding7.tagBits |= 0x20000L;
                }
            }
        }
        return hasCycle;
    }
    
    private ReferenceBinding findSupertype(final TypeReference typeReference) {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        final LookupEnvironment env = unitScope.environment;
        try {
            ((TypeReference)(env.missingClassFileLocation = typeReference)).aboutToResolve(this);
            unitScope.recordQualifiedReference(typeReference.getTypeName());
            this.superTypeReference = typeReference;
            final ReferenceBinding superType = (ReferenceBinding)typeReference.resolveSuperType(this);
            return superType;
        }
        catch (final AbortCompilation e) {
            final SourceTypeBinding sourceType = this.referenceContext.binding;
            if (sourceType.superInterfaces == null) {
                sourceType.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
            }
            e.updateContext(typeReference, this.referenceCompilationUnit().compilationResult);
            throw e;
        }
        finally {
            env.missingClassFileLocation = null;
            this.superTypeReference = null;
        }
    }
    
    @Override
    public ProblemReporter problemReporter() {
        final MethodScope outerMethodScope;
        if ((outerMethodScope = this.outerMostMethodScope()) == null) {
            final ProblemReporter problemReporter = this.referenceCompilationUnit().problemReporter;
            problemReporter.referenceContext = this.referenceContext;
            return problemReporter;
        }
        return outerMethodScope.problemReporter();
    }
    
    public TypeDeclaration referenceType() {
        return this.referenceContext;
    }
    
    @Override
    public boolean hasDefaultNullnessFor(final int location) {
        final SourceTypeBinding binding = this.referenceContext.binding;
        if (binding != null) {
            final int nullDefault = binding.getNullDefault();
            if (nullDefault != 0) {
                return (nullDefault & location) != 0x0;
            }
        }
        return this.parent.hasDefaultNullnessFor(location);
    }
    
    @Override
    public String toString() {
        if (this.referenceContext != null) {
            return "--- Class Scope ---\n\n" + this.referenceContext.binding.toString();
        }
        return "--- Class Scope ---\n\n Binding not initialized";
    }
}
