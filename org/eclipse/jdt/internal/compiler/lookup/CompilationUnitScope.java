package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Iterator;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.util.HashtableOfType;
import org.eclipse.jdt.internal.compiler.util.ObjectVector;
import org.eclipse.jdt.internal.compiler.util.SimpleNameVector;
import org.eclipse.jdt.internal.compiler.util.CompoundNameVector;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

public class CompilationUnitScope extends Scope
{
    public LookupEnvironment environment;
    public CompilationUnitDeclaration referenceContext;
    public char[][] currentPackageName;
    public PackageBinding fPackage;
    public ImportBinding[] imports;
    public int importPtr;
    public HashtableOfObject typeOrPackageCache;
    public SourceTypeBinding[] topLevelTypes;
    private CompoundNameVector qualifiedReferences;
    private SimpleNameVector simpleNameReferences;
    private SimpleNameVector rootReferences;
    private ObjectVector referencedTypes;
    private ObjectVector referencedSuperTypes;
    HashtableOfType constantPoolNameUsage;
    private int captureID;
    private ImportBinding[] tempImports;
    public boolean suppressImportErrors;
    private boolean skipCachingImports;
    boolean connectingHierarchy;
    private ArrayList<Invocation> inferredInvocations;
    Map<InferenceVariable.InferenceVarKey, InferenceVariable> uniqueInferenceVariables;
    
    public CompilationUnitScope(final CompilationUnitDeclaration unit, final LookupEnvironment environment) {
        super(4, null);
        this.captureID = 1;
        this.uniqueInferenceVariables = new HashMap<InferenceVariable.InferenceVarKey, InferenceVariable>();
        this.environment = environment;
        this.referenceContext = unit;
        unit.scope = this;
        this.currentPackageName = ((unit.currentPackage == null) ? CharOperation.NO_CHAR_CHAR : unit.currentPackage.tokens);
        if (this.compilerOptions().produceReferenceInfo) {
            this.qualifiedReferences = new CompoundNameVector();
            this.simpleNameReferences = new SimpleNameVector();
            this.rootReferences = new SimpleNameVector();
            this.referencedTypes = new ObjectVector();
            this.referencedSuperTypes = new ObjectVector();
        }
        else {
            this.qualifiedReferences = null;
            this.simpleNameReferences = null;
            this.rootReferences = null;
            this.referencedTypes = null;
            this.referencedSuperTypes = null;
        }
    }
    
    void buildFieldsAndMethods() {
        for (int i = 0, length = this.topLevelTypes.length; i < length; ++i) {
            this.topLevelTypes[i].scope.buildFieldsAndMethods();
        }
    }
    
    void buildTypeBindings(final AccessRestriction accessRestriction) {
        this.topLevelTypes = new SourceTypeBinding[0];
        boolean firstIsSynthetic = false;
        if (this.referenceContext.compilationResult.compilationUnit != null) {
            final char[][] expectedPackageName = this.referenceContext.compilationResult.compilationUnit.getPackageName();
            if (expectedPackageName != null && !CharOperation.equals(this.currentPackageName, expectedPackageName)) {
                if (this.referenceContext.currentPackage != null || this.referenceContext.types != null || this.referenceContext.imports != null) {
                    this.problemReporter().packageIsNotExpectedPackage(this.referenceContext);
                }
                this.currentPackageName = ((expectedPackageName.length == 0) ? CharOperation.NO_CHAR_CHAR : expectedPackageName);
            }
        }
        if (this.currentPackageName == CharOperation.NO_CHAR_CHAR) {
            this.fPackage = this.environment.defaultPackage;
        }
        else {
            if ((this.fPackage = this.environment.createPackage(this.currentPackageName)) == null) {
                if (this.referenceContext.currentPackage != null) {
                    this.problemReporter().packageCollidesWithType(this.referenceContext);
                }
                this.fPackage = this.environment.defaultPackage;
                return;
            }
            if (this.referenceContext.isPackageInfo()) {
                if (this.referenceContext.types == null || this.referenceContext.types.length == 0) {
                    this.referenceContext.types = new TypeDeclaration[1];
                    this.referenceContext.createPackageInfoType();
                    firstIsSynthetic = true;
                }
                if (this.referenceContext.currentPackage != null && this.referenceContext.currentPackage.annotations != null) {
                    this.referenceContext.types[0].annotations = this.referenceContext.currentPackage.annotations;
                }
            }
            this.recordQualifiedReference(this.currentPackageName);
        }
        final TypeDeclaration[] types = this.referenceContext.types;
        final int typeLength = (types == null) ? 0 : types.length;
        this.topLevelTypes = new SourceTypeBinding[typeLength];
        int count = 0;
        for (int i = 0; i < typeLength; ++i) {
            final TypeDeclaration typeDecl = types[i];
            if (this.environment.isProcessingAnnotations && this.environment.isMissingType(typeDecl.name)) {
                throw new SourceTypeCollisionException();
            }
            final ReferenceBinding typeBinding = this.fPackage.getType0(typeDecl.name);
            this.recordSimpleReference(typeDecl.name);
            if (typeBinding != null && typeBinding.isValidBinding() && !(typeBinding instanceof UnresolvedReferenceBinding)) {
                if (this.environment.isProcessingAnnotations) {
                    throw new SourceTypeCollisionException();
                }
                this.problemReporter().duplicateTypes(this.referenceContext, typeDecl);
            }
            else {
                if (this.fPackage != this.environment.defaultPackage && this.fPackage.getPackage(typeDecl.name) != null) {
                    this.problemReporter().typeCollidesWithPackage(this.referenceContext, typeDecl);
                }
                final char[] mainTypeName;
                if ((typeDecl.modifiers & 0x1) != 0x0 && (mainTypeName = this.referenceContext.getMainTypeName()) != null && !CharOperation.equals(mainTypeName, typeDecl.name)) {
                    this.problemReporter().publicClassMustMatchFileName(this.referenceContext, typeDecl);
                }
                final ClassScope child = new ClassScope(this, typeDecl);
                final SourceTypeBinding type = child.buildType(null, this.fPackage, accessRestriction);
                if (firstIsSynthetic && i == 0) {
                    final SourceTypeBinding sourceTypeBinding = type;
                    sourceTypeBinding.modifiers |= 0x1000;
                }
                if (type != null) {
                    this.topLevelTypes[count++] = type;
                }
            }
        }
        if (count != this.topLevelTypes.length) {
            System.arraycopy(this.topLevelTypes, 0, this.topLevelTypes = new SourceTypeBinding[count], 0, count);
        }
    }
    
    void checkAndSetImports() {
        if (this.referenceContext.imports == null) {
            this.imports = this.getDefaultImports();
            return;
        }
        final int numberOfStatements = this.referenceContext.imports.length;
        int numberOfImports = numberOfStatements + 1;
        for (int i = 0; i < numberOfStatements; ++i) {
            final ImportReference importReference = this.referenceContext.imports[i];
            if ((importReference.bits & 0x20000) != 0x0 && CharOperation.equals(TypeConstants.JAVA_LANG, importReference.tokens) && !importReference.isStatic()) {
                --numberOfImports;
                break;
            }
        }
        ImportBinding[] resolvedImports = new ImportBinding[numberOfImports];
        resolvedImports[0] = this.getDefaultImports()[0];
        int index = 1;
        int j = 0;
    Label_0328:
        while (j < numberOfStatements) {
            final ImportReference importReference2 = this.referenceContext.imports[j];
            final char[][] compoundName = importReference2.tokens;
            while (true) {
                for (int k = 0; k < index; ++k) {
                    final ImportBinding resolved = resolvedImports[k];
                    if (resolved.onDemand == ((importReference2.bits & 0x20000) != 0x0) && resolved.isStatic() == importReference2.isStatic() && CharOperation.equals(compoundName, resolvedImports[k].compoundName)) {
                        ++j;
                        continue Label_0328;
                    }
                }
                if ((importReference2.bits & 0x20000) == 0x0) {
                    resolvedImports[index++] = new ImportBinding(compoundName, false, null, importReference2);
                    continue;
                }
                if (CharOperation.equals(compoundName, this.currentPackageName)) {
                    continue;
                }
                final Binding importBinding = this.findImport(compoundName, compoundName.length);
                if (!importBinding.isValidBinding()) {
                    continue;
                }
                if (importReference2.isStatic() && importBinding instanceof PackageBinding) {
                    continue;
                }
                resolvedImports[index++] = new ImportBinding(compoundName, true, importBinding, importReference2);
                continue;
            }
        }
        if (resolvedImports.length > index) {
            System.arraycopy(resolvedImports, 0, resolvedImports = new ImportBinding[index], 0, index);
        }
        this.imports = resolvedImports;
    }
    
    void checkParameterizedTypes() {
        if (this.compilerOptions().sourceLevel < 3211264L) {
            return;
        }
        for (int i = 0, length = this.topLevelTypes.length; i < length; ++i) {
            final ClassScope scope = this.topLevelTypes[i].scope;
            scope.checkParameterizedTypeBounds();
            scope.checkParameterizedSuperTypeCollisions();
        }
    }
    
    public char[] computeConstantPoolName(final LocalTypeBinding localType) {
        if (localType.constantPoolName != null) {
            return localType.constantPoolName;
        }
        if (this.constantPoolNameUsage == null) {
            this.constantPoolNameUsage = new HashtableOfType();
        }
        final ReferenceBinding outerMostEnclosingType = localType.scope.outerMostClassScope().enclosingSourceType();
        int index = 0;
        final boolean isCompliant15 = this.compilerOptions().complianceLevel >= 3211264L;
        char[] candidateName;
        while (true) {
            if (localType.isMemberType()) {
                if (index == 0) {
                    candidateName = CharOperation.concat(localType.enclosingType().constantPoolName(), localType.sourceName, '$');
                }
                else {
                    candidateName = CharOperation.concat(localType.enclosingType().constantPoolName(), '$', String.valueOf(index).toCharArray(), '$', localType.sourceName);
                }
            }
            else if (localType.isAnonymousType()) {
                if (isCompliant15) {
                    candidateName = CharOperation.concat(localType.enclosingType.constantPoolName(), String.valueOf(index + 1).toCharArray(), '$');
                }
                else {
                    candidateName = CharOperation.concat(outerMostEnclosingType.constantPoolName(), String.valueOf(index + 1).toCharArray(), '$');
                }
            }
            else if (isCompliant15) {
                candidateName = CharOperation.concat(CharOperation.concat(localType.enclosingType().constantPoolName(), String.valueOf(index + 1).toCharArray(), '$'), localType.sourceName);
            }
            else {
                candidateName = CharOperation.concat(outerMostEnclosingType.constantPoolName(), '$', String.valueOf(index + 1).toCharArray(), '$', localType.sourceName);
            }
            if (this.constantPoolNameUsage.get(candidateName) == null) {
                break;
            }
            ++index;
        }
        this.constantPoolNameUsage.put(candidateName, localType);
        return candidateName;
    }
    
    void connectTypeHierarchy() {
        for (int i = 0, length = this.topLevelTypes.length; i < length; ++i) {
            this.topLevelTypes[i].scope.connectTypeHierarchy();
        }
    }
    
    void faultInImports() {
        boolean unresolvedFound = false;
        final boolean reportUnresolved = !this.suppressImportErrors;
        if (this.typeOrPackageCache != null && !this.skipCachingImports) {
            return;
        }
        if (this.referenceContext.imports == null) {
            this.typeOrPackageCache = new HashtableOfObject(1);
            return;
        }
        final int numberOfStatements = this.referenceContext.imports.length;
        HashtableOfType typesBySimpleNames = null;
        for (int i = 0; i < numberOfStatements; ++i) {
            if ((this.referenceContext.imports[i].bits & 0x20000) == 0x0) {
                typesBySimpleNames = new HashtableOfType(this.topLevelTypes.length + numberOfStatements);
                for (int j = 0, length = this.topLevelTypes.length; j < length; ++j) {
                    typesBySimpleNames.put(this.topLevelTypes[j].sourceName, this.topLevelTypes[j]);
                }
                break;
            }
        }
        int numberOfImports = numberOfStatements + 1;
        for (int k = 0; k < numberOfStatements; ++k) {
            final ImportReference importReference = this.referenceContext.imports[k];
            if ((importReference.bits & 0x20000) != 0x0 && CharOperation.equals(TypeConstants.JAVA_LANG, importReference.tokens) && !importReference.isStatic()) {
                --numberOfImports;
                break;
            }
        }
        (this.tempImports = new ImportBinding[numberOfImports])[0] = this.getDefaultImports()[0];
        this.importPtr = 1;
        int k = 0;
    Label_0643:
        while (k < numberOfStatements) {
            final ImportReference importReference = this.referenceContext.imports[k];
            final char[][] compoundName = importReference.tokens;
            while (true) {
                for (int l = 0; l < this.importPtr; ++l) {
                    final ImportBinding resolved = this.tempImports[l];
                    if (resolved.onDemand == ((importReference.bits & 0x20000) != 0x0) && resolved.isStatic() == importReference.isStatic() && CharOperation.equals(compoundName, resolved.compoundName)) {
                        this.problemReporter().unusedImport(importReference);
                        ++k;
                        continue Label_0643;
                    }
                }
                if ((importReference.bits & 0x20000) != 0x0) {
                    if (CharOperation.equals(compoundName, this.currentPackageName)) {
                        this.problemReporter().unusedImport(importReference);
                        continue;
                    }
                    final Binding importBinding = this.findImport(compoundName, compoundName.length);
                    if (!importBinding.isValidBinding()) {
                        this.problemReporter().importProblem(importReference, importBinding);
                        continue;
                    }
                    if (importReference.isStatic() && importBinding instanceof PackageBinding) {
                        this.problemReporter().cannotImportPackage(importReference);
                        continue;
                    }
                    this.recordImportBinding(new ImportBinding(compoundName, true, importBinding, importReference));
                    continue;
                }
                else {
                    final Binding importBinding = this.findSingleImport(compoundName, 13, importReference.isStatic());
                    if (!importBinding.isValidBinding() && importBinding.problemId() != 3) {
                        unresolvedFound = true;
                        if (reportUnresolved) {
                            this.problemReporter().importProblem(importReference, importBinding);
                        }
                        continue;
                    }
                    else {
                        if (importBinding instanceof PackageBinding) {
                            this.problemReporter().cannotImportPackage(importReference);
                            continue;
                        }
                        if (this.checkAndRecordImportBinding(importBinding, typesBySimpleNames, importReference, compoundName) == -1) {
                            continue;
                        }
                        if (!importReference.isStatic()) {
                            continue;
                        }
                        if (importBinding.kind() == 1) {
                            this.checkMoreStaticBindings(compoundName, typesBySimpleNames, 12, importReference);
                            continue;
                        }
                        if (importBinding.kind() == 8) {
                            this.checkMoreStaticBindings(compoundName, typesBySimpleNames, 4, importReference);
                        }
                        continue;
                    }
                }
                break;
            }
        }
        if (this.tempImports.length > this.importPtr) {
            System.arraycopy(this.tempImports, 0, this.tempImports = new ImportBinding[this.importPtr], 0, this.importPtr);
        }
        this.imports = this.tempImports;
        final int length2 = this.imports.length;
        this.typeOrPackageCache = new HashtableOfObject(length2);
        for (int m = 0; m < length2; ++m) {
            final ImportBinding binding = this.imports[m];
            if ((!binding.onDemand && binding.resolvedImport instanceof ReferenceBinding) || binding instanceof ImportConflictBinding) {
                this.typeOrPackageCache.put(binding.compoundName[binding.compoundName.length - 1], binding);
            }
        }
        this.skipCachingImports = (this.suppressImportErrors && unresolvedFound);
    }
    
    public void faultInTypes() {
        this.faultInImports();
        for (int i = 0, length = this.topLevelTypes.length; i < length; ++i) {
            this.topLevelTypes[i].faultInTypesForFieldsAndMethods();
        }
    }
    
    public Binding findImport(final char[][] compoundName, final boolean findStaticImports, final boolean onDemand) {
        if (onDemand) {
            return this.findImport(compoundName, compoundName.length);
        }
        return this.findSingleImport(compoundName, 13, findStaticImports);
    }
    
    private Binding findImport(final char[][] compoundName, final int length) {
        this.recordQualifiedReference(compoundName);
        Binding binding = this.environment.getTopLevelPackage(compoundName[0]);
        int i = 1;
        Label_0086: {
            if (binding != null) {
                PackageBinding packageBinding = (PackageBinding)binding;
                while (i < length) {
                    binding = packageBinding.getTypeOrPackage(compoundName[i++]);
                    if (binding == null || !binding.isValidBinding()) {
                        binding = null;
                        break Label_0086;
                    }
                    if (!(binding instanceof PackageBinding)) {
                        break Label_0086;
                    }
                    packageBinding = (PackageBinding)binding;
                }
                return packageBinding;
            }
        }
        ReferenceBinding type;
        if (binding == null) {
            if (this.compilerOptions().complianceLevel >= 3145728L) {
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, i), null, 1);
            }
            type = this.findType(compoundName[0], this.environment.defaultPackage, this.environment.defaultPackage);
            if (type == null || !type.isValidBinding()) {
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, i), null, 1);
            }
            i = 1;
        }
        else {
            type = (ReferenceBinding)binding;
        }
        while (i < length) {
            type = (ReferenceBinding)this.environment.convertToRawType(type, false);
            if (!type.canBeSeenBy(this.fPackage)) {
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, i), type, 2);
            }
            final char[] name = compoundName[i++];
            type = type.getMemberType(name);
            if (type == null) {
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, i), null, 1);
            }
        }
        if (!type.canBeSeenBy(this.fPackage)) {
            return new ProblemReferenceBinding(compoundName, type, 2);
        }
        return type;
    }
    
    private Binding findSingleImport(final char[][] compoundName, final int mask, final boolean findStaticImports) {
        if (compoundName.length == 1) {
            if (this.compilerOptions().complianceLevel >= 3145728L) {
                return new ProblemReferenceBinding(compoundName, null, 1);
            }
            final ReferenceBinding typeBinding = this.findType(compoundName[0], this.environment.defaultPackage, this.fPackage);
            if (typeBinding == null) {
                return new ProblemReferenceBinding(compoundName, null, 1);
            }
            return typeBinding;
        }
        else {
            if (findStaticImports) {
                return this.findSingleStaticImport(compoundName, mask);
            }
            return this.findImport(compoundName, compoundName.length);
        }
    }
    
    private Binding findSingleStaticImport(final char[][] compoundName, final int mask) {
        final Binding binding = this.findImport(compoundName, compoundName.length - 1);
        if (!binding.isValidBinding()) {
            return binding;
        }
        final char[] name = compoundName[compoundName.length - 1];
        if (binding instanceof PackageBinding) {
            final Binding temp = ((PackageBinding)binding).getTypeOrPackage(name);
            if (temp != null && temp instanceof ReferenceBinding) {
                return new ProblemReferenceBinding(compoundName, (ReferenceBinding)temp, 14);
            }
            return binding;
        }
        else {
            ReferenceBinding type = (ReferenceBinding)binding;
            final FieldBinding field = ((mask & 0x1) != 0x0) ? this.findField(type, name, null, true) : null;
            if (field != null) {
                if (field.problemId() == 3 && ((ProblemFieldBinding)field).closestMatch.isStatic()) {
                    return field;
                }
                if (field.isValidBinding() && field.isStatic() && field.canBeSeenBy(type, null, this)) {
                    return field;
                }
            }
            final MethodBinding method = ((mask & 0x8) != 0x0) ? this.findStaticMethod(type, name) : null;
            if (method != null) {
                return method;
            }
            type = this.findMemberType(name, type);
            if (type == null || !type.isStatic()) {
                if (field != null && !field.isValidBinding() && field.problemId() != 1) {
                    return field;
                }
                return new ProblemReferenceBinding(compoundName, type, 1);
            }
            else {
                if (type.isValidBinding() && !type.canBeSeenBy(this.fPackage)) {
                    return new ProblemReferenceBinding(compoundName, type, 2);
                }
                if (type.problemId() == 2) {
                    return new ProblemReferenceBinding(compoundName, ((ProblemReferenceBinding)type).closestMatch, 2);
                }
                return type;
            }
        }
    }
    
    private MethodBinding findStaticMethod(ReferenceBinding currentType, final char[] selector) {
        if (!currentType.canBeSeenBy(this)) {
            return null;
        }
        do {
            currentType.initializeForStaticImports();
            final MethodBinding[] methods = currentType.getMethods(selector);
            if (methods != Binding.NO_METHODS) {
                int i = methods.length;
                while (--i >= 0) {
                    final MethodBinding method = methods[i];
                    if (method.isStatic() && method.canBeSeenBy(this.fPackage)) {
                        return method;
                    }
                }
            }
        } while ((currentType = currentType.superclass()) != null);
        return null;
    }
    
    ImportBinding[] getDefaultImports() {
        if (this.environment.defaultImports != null) {
            return this.environment.defaultImports;
        }
        Binding importBinding = this.environment.getTopLevelPackage(TypeConstants.JAVA);
        if (importBinding != null) {
            importBinding = ((PackageBinding)importBinding).getTypeOrPackage(TypeConstants.JAVA_LANG[1]);
        }
        if (importBinding == null || !importBinding.isValidBinding()) {
            this.problemReporter().isClassPathCorrect(TypeConstants.JAVA_LANG_OBJECT, this.referenceContext, this.environment.missingClassFileLocation);
            final BinaryTypeBinding missingObject = this.environment.createMissingType(null, TypeConstants.JAVA_LANG_OBJECT);
            importBinding = missingObject.fPackage;
        }
        return this.environment.defaultImports = new ImportBinding[] { new ImportBinding(TypeConstants.JAVA_LANG, true, importBinding, null) };
    }
    
    public final Binding getImport(final char[][] compoundName, final boolean onDemand, final boolean isStaticImport) {
        if (onDemand) {
            return this.findImport(compoundName, compoundName.length);
        }
        return this.findSingleImport(compoundName, 13, isStaticImport);
    }
    
    public int nextCaptureID() {
        return this.captureID++;
    }
    
    @Override
    public ProblemReporter problemReporter() {
        final ProblemReporter problemReporter = this.referenceContext.problemReporter;
        problemReporter.referenceContext = this.referenceContext;
        return problemReporter;
    }
    
    void recordQualifiedReference(char[][] qualifiedName) {
        if (this.qualifiedReferences == null) {
            return;
        }
        int length = qualifiedName.length;
        if (length > 1) {
            this.recordRootReference(qualifiedName[0]);
            while (!this.qualifiedReferences.contains(qualifiedName)) {
                this.qualifiedReferences.add(qualifiedName);
                if (length == 2) {
                    this.recordSimpleReference(qualifiedName[0]);
                    this.recordSimpleReference(qualifiedName[1]);
                    return;
                }
                --length;
                this.recordSimpleReference(qualifiedName[length]);
                System.arraycopy(qualifiedName, 0, qualifiedName = new char[length][], 0, length);
            }
        }
        else if (length == 1) {
            this.recordRootReference(qualifiedName[0]);
            this.recordSimpleReference(qualifiedName[0]);
        }
    }
    
    void recordReference(final char[][] qualifiedEnclosingName, final char[] simpleName) {
        this.recordQualifiedReference(qualifiedEnclosingName);
        if (qualifiedEnclosingName.length == 0) {
            this.recordRootReference(simpleName);
        }
        this.recordSimpleReference(simpleName);
    }
    
    void recordReference(final ReferenceBinding type, final char[] simpleName) {
        final ReferenceBinding actualType = this.typeToRecord(type);
        if (actualType != null) {
            this.recordReference(actualType.compoundName, simpleName);
        }
    }
    
    void recordRootReference(final char[] simpleName) {
        if (this.rootReferences == null) {
            return;
        }
        if (!this.rootReferences.contains(simpleName)) {
            this.rootReferences.add(simpleName);
        }
    }
    
    void recordSimpleReference(final char[] simpleName) {
        if (this.simpleNameReferences == null) {
            return;
        }
        if (!this.simpleNameReferences.contains(simpleName)) {
            this.simpleNameReferences.add(simpleName);
        }
    }
    
    void recordSuperTypeReference(final TypeBinding type) {
        if (this.referencedSuperTypes == null) {
            return;
        }
        final ReferenceBinding actualType = this.typeToRecord(type);
        if (actualType != null && !this.referencedSuperTypes.containsIdentical(actualType)) {
            this.referencedSuperTypes.add(actualType);
        }
    }
    
    public void recordTypeConversion(final TypeBinding superType, final TypeBinding subType) {
        this.recordSuperTypeReference(subType);
    }
    
    void recordTypeReference(final TypeBinding type) {
        if (this.referencedTypes == null) {
            return;
        }
        final ReferenceBinding actualType = this.typeToRecord(type);
        if (actualType != null && !this.referencedTypes.containsIdentical(actualType)) {
            this.referencedTypes.add(actualType);
        }
    }
    
    void recordTypeReferences(final TypeBinding[] types) {
        if (this.referencedTypes == null) {
            return;
        }
        if (types == null || types.length == 0) {
            return;
        }
        for (int i = 0, max = types.length; i < max; ++i) {
            final ReferenceBinding actualType = this.typeToRecord(types[i]);
            if (actualType != null && !this.referencedTypes.containsIdentical(actualType)) {
                this.referencedTypes.add(actualType);
            }
        }
    }
    
    Binding resolveSingleImport(final ImportBinding importBinding, final int mask) {
        if (importBinding.resolvedImport == null) {
            importBinding.resolvedImport = this.findSingleImport(importBinding.compoundName, mask, importBinding.isStatic());
            if (!importBinding.resolvedImport.isValidBinding() || importBinding.resolvedImport instanceof PackageBinding) {
                if (importBinding.resolvedImport.problemId() == 3) {
                    return importBinding.resolvedImport;
                }
                if (this.imports != null) {
                    final ImportBinding[] newImports = new ImportBinding[this.imports.length - 1];
                    int i = 0;
                    int n = 0;
                    for (int max = this.imports.length; i < max; ++i) {
                        if (this.imports[i] != importBinding) {
                            newImports[n++] = this.imports[i];
                        }
                    }
                    this.imports = newImports;
                }
                return null;
            }
        }
        return importBinding.resolvedImport;
    }
    
    public void storeDependencyInfo() {
        for (int i = 0; i < this.referencedSuperTypes.size; ++i) {
            final ReferenceBinding type = (ReferenceBinding)this.referencedSuperTypes.elementAt(i);
            if (!this.referencedTypes.containsIdentical(type)) {
                this.referencedTypes.add(type);
            }
            if (!type.isLocalType()) {
                final ReferenceBinding enclosing = type.enclosingType();
                if (enclosing != null) {
                    this.recordSuperTypeReference(enclosing);
                }
            }
            final ReferenceBinding superclass = type.superclass();
            if (superclass != null) {
                this.recordSuperTypeReference(superclass);
            }
            final ReferenceBinding[] interfaces = type.superInterfaces();
            if (interfaces != null) {
                for (int j = 0, length = interfaces.length; j < length; ++j) {
                    this.recordSuperTypeReference(interfaces[j]);
                }
            }
        }
        for (int i = 0, l = this.referencedTypes.size; i < l; ++i) {
            final ReferenceBinding type2 = (ReferenceBinding)this.referencedTypes.elementAt(i);
            if (!type2.isLocalType()) {
                this.recordQualifiedReference(type2.isMemberType() ? CharOperation.splitOn('.', type2.readableName()) : type2.compoundName);
            }
        }
        int size = this.qualifiedReferences.size;
        final char[][][] qualifiedRefs = new char[size][][];
        for (int k = 0; k < size; ++k) {
            qualifiedRefs[k] = this.qualifiedReferences.elementAt(k);
        }
        this.referenceContext.compilationResult.qualifiedReferences = qualifiedRefs;
        size = this.simpleNameReferences.size;
        final char[][] simpleRefs = new char[size][];
        for (int m = 0; m < size; ++m) {
            simpleRefs[m] = this.simpleNameReferences.elementAt(m);
        }
        this.referenceContext.compilationResult.simpleNameReferences = simpleRefs;
        size = this.rootReferences.size;
        final char[][] rootRefs = new char[size][];
        for (int i2 = 0; i2 < size; ++i2) {
            rootRefs[i2] = this.rootReferences.elementAt(i2);
        }
        this.referenceContext.compilationResult.rootReferences = rootRefs;
    }
    
    @Override
    public String toString() {
        return "--- CompilationUnit Scope : " + new String(this.referenceContext.getFileName());
    }
    
    private ReferenceBinding typeToRecord(TypeBinding type) {
        if (type == null) {
            return null;
        }
        while (type.isArrayType()) {
            type = ((ArrayBinding)type).leafComponentType();
        }
        switch (type.kind()) {
            case 132:
            case 516:
            case 4100:
            case 8196:
            case 32772:
            case 65540: {
                return null;
            }
            case 260:
            case 1028: {
                type = type.erasure();
                break;
            }
        }
        final ReferenceBinding refType = (ReferenceBinding)type;
        if (refType.isLocalType()) {
            return null;
        }
        return refType;
    }
    
    public void verifyMethods(final MethodVerifier verifier) {
        for (int i = 0, length = this.topLevelTypes.length; i < length; ++i) {
            this.topLevelTypes[i].verifyMethods(verifier);
        }
    }
    
    private void recordImportBinding(final ImportBinding bindingToAdd) {
        if (this.tempImports.length == this.importPtr) {
            System.arraycopy(this.tempImports, 0, this.tempImports = new ImportBinding[this.importPtr + 1], 0, this.importPtr);
        }
        this.tempImports[this.importPtr++] = bindingToAdd;
    }
    
    private void checkMoreStaticBindings(final char[][] compoundName, final HashtableOfType typesBySimpleNames, int mask, final ImportReference importReference) {
        final Binding importBinding = this.findSingleStaticImport(compoundName, mask);
        if (!importBinding.isValidBinding()) {
            if (importBinding.problemId() == 3) {
                this.checkAndRecordImportBinding(importBinding, typesBySimpleNames, importReference, compoundName);
            }
        }
        else {
            this.checkAndRecordImportBinding(importBinding, typesBySimpleNames, importReference, compoundName);
        }
        if ((mask & 0x8) != 0x0 && importBinding.kind() == 8) {
            mask &= 0xFFFFFFF7;
            this.checkMoreStaticBindings(compoundName, typesBySimpleNames, mask, importReference);
        }
    }
    
    private int checkAndRecordImportBinding(final Binding importBinding, final HashtableOfType typesBySimpleNames, final ImportReference importReference, final char[][] compoundName) {
        ReferenceBinding conflictingType = null;
        if (importBinding instanceof MethodBinding) {
            conflictingType = (ReferenceBinding)this.getType(compoundName, compoundName.length);
            if (!conflictingType.isValidBinding() || (importReference.isStatic() && !conflictingType.isStatic())) {
                conflictingType = null;
            }
        }
        final char[] name = compoundName[compoundName.length - 1];
        if (importBinding instanceof ReferenceBinding || conflictingType != null) {
            final ReferenceBinding referenceBinding = (ReferenceBinding)((conflictingType == null) ? importBinding : conflictingType);
            final ReferenceBinding typeToCheck = (referenceBinding.problemId() == 3) ? ((ProblemReferenceBinding)referenceBinding).closestMatch : referenceBinding;
            if (importReference.isTypeUseDeprecated(typeToCheck, this)) {
                this.problemReporter().deprecatedType(typeToCheck, importReference);
            }
            final ReferenceBinding existingType = typesBySimpleNames.get(name);
            if (existingType != null) {
                if (TypeBinding.equalsEquals(existingType, referenceBinding)) {
                    for (int j = 0; j < this.importPtr; ++j) {
                        final ImportBinding resolved = this.tempImports[j];
                        if (resolved instanceof ImportConflictBinding) {
                            final ImportConflictBinding importConflictBinding = (ImportConflictBinding)resolved;
                            if (TypeBinding.equalsEquals(importConflictBinding.conflictingTypeBinding, referenceBinding) && !importReference.isStatic()) {
                                this.problemReporter().duplicateImport(importReference);
                                this.recordImportBinding(new ImportBinding(compoundName, false, importBinding, importReference));
                            }
                        }
                        else if (resolved.resolvedImport == referenceBinding && importReference.isStatic() != resolved.isStatic()) {
                            this.problemReporter().duplicateImport(importReference);
                            this.recordImportBinding(new ImportBinding(compoundName, false, importBinding, importReference));
                        }
                    }
                    return -1;
                }
                for (int j = 0, length = this.topLevelTypes.length; j < length; ++j) {
                    if (CharOperation.equals(this.topLevelTypes[j].sourceName, existingType.sourceName)) {
                        this.problemReporter().conflictingImport(importReference);
                        return -1;
                    }
                }
                if (importReference.isStatic() && importBinding instanceof ReferenceBinding && this.compilerOptions().sourceLevel >= 3407872L) {
                    for (int j = 0; j < this.importPtr; ++j) {
                        final ImportBinding resolved = this.tempImports[j];
                        if (resolved.isStatic() && resolved.resolvedImport instanceof ReferenceBinding && importBinding != resolved.resolvedImport && CharOperation.equals(name, resolved.compoundName[resolved.compoundName.length - 1])) {
                            final ReferenceBinding type = (ReferenceBinding)resolved.resolvedImport;
                            resolved.resolvedImport = new ProblemReferenceBinding(new char[][] { name }, type, 3);
                            return -1;
                        }
                    }
                }
                this.problemReporter().duplicateImport(importReference);
                return -1;
            }
            else {
                typesBySimpleNames.put(name, referenceBinding);
            }
        }
        else if (importBinding instanceof FieldBinding) {
            int i = 0;
            while (i < this.importPtr) {
                final ImportBinding resolved2 = this.tempImports[i];
                if (resolved2.isStatic() && resolved2.resolvedImport instanceof FieldBinding && importBinding != resolved2.resolvedImport && CharOperation.equals(name, resolved2.compoundName[resolved2.compoundName.length - 1])) {
                    if (this.compilerOptions().sourceLevel >= 3407872L) {
                        final FieldBinding field = (FieldBinding)resolved2.resolvedImport;
                        resolved2.resolvedImport = new ProblemFieldBinding(field, field.declaringClass, name, 3);
                        return -1;
                    }
                    this.problemReporter().duplicateImport(importReference);
                    return -1;
                }
                else {
                    ++i;
                }
            }
        }
        if (conflictingType == null) {
            this.recordImportBinding(new ImportBinding(compoundName, false, importBinding, importReference));
        }
        else {
            this.recordImportBinding(new ImportConflictBinding(compoundName, importBinding, conflictingType, importReference));
        }
        return this.importPtr;
    }
    
    @Override
    public boolean hasDefaultNullnessFor(final int location) {
        return this.fPackage != null && (this.fPackage.defaultNullness & location) != 0x0;
    }
    
    public void registerInferredInvocation(final Invocation invocation) {
        if (this.inferredInvocations == null) {
            this.inferredInvocations = new ArrayList<Invocation>();
        }
        this.inferredInvocations.add(invocation);
    }
    
    public void cleanUpInferenceContexts() {
        if (this.inferredInvocations == null) {
            return;
        }
        for (final Invocation invocation : this.inferredInvocations) {
            invocation.cleanUpInferenceContexts();
        }
        this.inferredInvocations = null;
    }
}
