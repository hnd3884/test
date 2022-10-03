package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;
import java.util.Iterator;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.env.INameEnvironmentExtension;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import java.util.HashSet;
import java.util.HashMap;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import org.eclipse.jdt.internal.compiler.impl.ITypeRequestor;
import org.eclipse.jdt.internal.compiler.ClassFilePool;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.util.HashtableOfPackage;
import java.util.Map;

public class LookupEnvironment implements ProblemReasons, TypeConstants
{
    private Map accessRestrictions;
    ImportBinding[] defaultImports;
    public PackageBinding defaultPackage;
    HashtableOfPackage knownPackages;
    private int lastCompletedUnitIndex;
    private int lastUnitIndex;
    TypeSystem typeSystem;
    public INameEnvironment nameEnvironment;
    public CompilerOptions globalOptions;
    public ProblemReporter problemReporter;
    public ClassFilePool classFilePool;
    private int stepCompleted;
    public ITypeRequestor typeRequestor;
    private SimpleLookupTable uniqueParameterizedGenericMethodBindings;
    private SimpleLookupTable uniquePolymorphicMethodBindings;
    private SimpleLookupTable uniqueGetClassMethodBinding;
    public CompilationUnitDeclaration unitBeingCompleted;
    public Object missingClassFileLocation;
    private CompilationUnitDeclaration[] units;
    private MethodVerifier verifier;
    public MethodBinding arrayClone;
    private ArrayList missingTypes;
    Set<SourceTypeBinding> typesBeingConnected;
    public boolean isProcessingAnnotations;
    public boolean mayTolerateMissingType;
    PackageBinding nullableAnnotationPackage;
    PackageBinding nonnullAnnotationPackage;
    PackageBinding nonnullByDefaultAnnotationPackage;
    AnnotationBinding nonNullAnnotation;
    AnnotationBinding nullableAnnotation;
    Map<String, Integer> allNullAnnotations;
    final List<MethodBinding> deferredEnumMethods;
    InferenceContext18 currentInferenceContext;
    static final int BUILD_FIELDS_AND_METHODS = 4;
    static final int BUILD_TYPE_HIERARCHY = 1;
    static final int CHECK_AND_SET_IMPORTS = 2;
    static final int CONNECT_TYPE_HIERARCHY = 3;
    static final ProblemPackageBinding TheNotFoundPackage;
    static final ProblemReferenceBinding TheNotFoundType;
    public IQualifiedTypeResolutionListener[] resolutionListeners;
    
    static {
        TheNotFoundPackage = new ProblemPackageBinding(CharOperation.NO_CHAR, 1);
        TheNotFoundType = new ProblemReferenceBinding(CharOperation.NO_CHAR_CHAR, null, 1);
    }
    
    public LookupEnvironment(final ITypeRequestor typeRequestor, final CompilerOptions globalOptions, final ProblemReporter problemReporter, final INameEnvironment nameEnvironment) {
        this.lastCompletedUnitIndex = -1;
        this.lastUnitIndex = -1;
        this.unitBeingCompleted = null;
        this.missingClassFileLocation = null;
        this.units = new CompilationUnitDeclaration[4];
        this.isProcessingAnnotations = false;
        this.mayTolerateMissingType = false;
        this.allNullAnnotations = null;
        this.deferredEnumMethods = new ArrayList<MethodBinding>();
        this.resolutionListeners = new IQualifiedTypeResolutionListener[0];
        this.typeRequestor = typeRequestor;
        this.globalOptions = globalOptions;
        this.problemReporter = problemReporter;
        this.defaultPackage = new PackageBinding(this);
        this.defaultImports = null;
        this.nameEnvironment = nameEnvironment;
        this.knownPackages = new HashtableOfPackage();
        this.uniqueParameterizedGenericMethodBindings = new SimpleLookupTable(3);
        this.uniquePolymorphicMethodBindings = new SimpleLookupTable(3);
        this.missingTypes = null;
        this.accessRestrictions = new HashMap(3);
        this.classFilePool = ClassFilePool.newInstance();
        this.typesBeingConnected = new HashSet<SourceTypeBinding>();
        this.typeSystem = ((this.globalOptions.sourceLevel >= 3407872L && this.globalOptions.storeAnnotations) ? new AnnotatableTypeSystem(this) : new TypeSystem(this));
    }
    
    public ReferenceBinding askForType(final char[][] compoundName) {
        final NameEnvironmentAnswer answer = this.nameEnvironment.findType(compoundName);
        if (answer == null) {
            return null;
        }
        if (answer.isBinaryType()) {
            this.typeRequestor.accept(answer.getBinaryType(), this.computePackageFrom(compoundName, false), answer.getAccessRestriction());
        }
        else if (answer.isCompilationUnit()) {
            this.typeRequestor.accept(answer.getCompilationUnit(), answer.getAccessRestriction());
        }
        else if (answer.isSourceType()) {
            this.typeRequestor.accept(answer.getSourceTypes(), this.computePackageFrom(compoundName, false), answer.getAccessRestriction());
        }
        return this.getCachedType(compoundName);
    }
    
    ReferenceBinding askForType(PackageBinding packageBinding, final char[] name) {
        if (packageBinding == null) {
            packageBinding = this.defaultPackage;
        }
        final NameEnvironmentAnswer answer = this.nameEnvironment.findType(name, packageBinding.compoundName);
        if (answer == null) {
            return null;
        }
        if (answer.isBinaryType()) {
            this.typeRequestor.accept(answer.getBinaryType(), packageBinding, answer.getAccessRestriction());
        }
        else {
            if (answer.isCompilationUnit()) {
                try {
                    this.typeRequestor.accept(answer.getCompilationUnit(), answer.getAccessRestriction());
                    return packageBinding.getType0(name);
                }
                catch (final AbortCompilation abort) {
                    if (CharOperation.equals(name, TypeConstants.PACKAGE_INFO_NAME)) {
                        return null;
                    }
                    throw abort;
                }
            }
            if (answer.isSourceType()) {
                this.typeRequestor.accept(answer.getSourceTypes(), packageBinding, answer.getAccessRestriction());
                final ReferenceBinding binding = packageBinding.getType0(name);
                final String externalAnnotationPath = answer.getExternalAnnotationPath();
                if (externalAnnotationPath != null && this.globalOptions.isAnnotationBasedNullAnalysisEnabled && binding instanceof SourceTypeBinding) {
                    ExternalAnnotationSuperimposer.apply((SourceTypeBinding)binding, externalAnnotationPath);
                }
                return binding;
            }
        }
        return packageBinding.getType0(name);
    }
    
    public void buildTypeBindings(final CompilationUnitDeclaration unit, final AccessRestriction accessRestriction) {
        final CompilationUnitScope scope = new CompilationUnitScope(unit, this);
        scope.buildTypeBindings(accessRestriction);
        final int unitsLength = this.units.length;
        if (++this.lastUnitIndex >= unitsLength) {
            System.arraycopy(this.units, 0, this.units = new CompilationUnitDeclaration[2 * unitsLength], 0, unitsLength);
        }
        this.units[this.lastUnitIndex] = unit;
    }
    
    public BinaryTypeBinding cacheBinaryType(final IBinaryType binaryType, final AccessRestriction accessRestriction) {
        return this.cacheBinaryType(binaryType, true, accessRestriction);
    }
    
    public BinaryTypeBinding cacheBinaryType(final IBinaryType binaryType, final boolean needFieldsAndMethods, final AccessRestriction accessRestriction) {
        final char[][] compoundName = CharOperation.splitOn('/', binaryType.getName());
        final ReferenceBinding existingType = this.getCachedType(compoundName);
        if (existingType == null || existingType instanceof UnresolvedReferenceBinding) {
            return this.createBinaryTypeFrom(binaryType, this.computePackageFrom(compoundName, false), needFieldsAndMethods, accessRestriction);
        }
        return null;
    }
    
    public void completeTypeBindings() {
        this.stepCompleted = 1;
        for (int i = this.lastCompletedUnitIndex + 1; i <= this.lastUnitIndex; ++i) {
            final CompilationUnitDeclaration unitBeingCompleted = this.units[i];
            this.unitBeingCompleted = unitBeingCompleted;
            unitBeingCompleted.scope.checkAndSetImports();
        }
        this.stepCompleted = 2;
        for (int i = this.lastCompletedUnitIndex + 1; i <= this.lastUnitIndex; ++i) {
            final CompilationUnitDeclaration unitBeingCompleted2 = this.units[i];
            this.unitBeingCompleted = unitBeingCompleted2;
            unitBeingCompleted2.scope.connectTypeHierarchy();
        }
        this.stepCompleted = 3;
        for (int i = this.lastCompletedUnitIndex + 1; i <= this.lastUnitIndex; ++i) {
            final CompilationUnitDeclaration unitBeingCompleted3 = this.units[i];
            this.unitBeingCompleted = unitBeingCompleted3;
            final CompilationUnitScope unitScope = unitBeingCompleted3.scope;
            unitScope.checkParameterizedTypes();
            unitScope.buildFieldsAndMethods();
            this.units[i] = null;
        }
        this.stepCompleted = 4;
        this.lastCompletedUnitIndex = this.lastUnitIndex;
        this.unitBeingCompleted = null;
    }
    
    public void completeTypeBindings(final CompilationUnitDeclaration parsedUnit) {
        if (this.stepCompleted == 4) {
            this.completeTypeBindings();
        }
        else {
            if (parsedUnit.scope == null) {
                return;
            }
            if (this.stepCompleted >= 2) {
                this.unitBeingCompleted = parsedUnit;
                parsedUnit.scope.checkAndSetImports();
            }
            if (this.stepCompleted >= 3) {
                this.unitBeingCompleted = parsedUnit;
                parsedUnit.scope.connectTypeHierarchy();
            }
            this.unitBeingCompleted = null;
        }
    }
    
    public void completeTypeBindings(final CompilationUnitDeclaration parsedUnit, final boolean buildFieldsAndMethods) {
        if (parsedUnit.scope == null) {
            return;
        }
        this.unitBeingCompleted = parsedUnit;
        parsedUnit.scope.checkAndSetImports();
        parsedUnit.scope.connectTypeHierarchy();
        parsedUnit.scope.checkParameterizedTypes();
        if (buildFieldsAndMethods) {
            parsedUnit.scope.buildFieldsAndMethods();
        }
        this.unitBeingCompleted = null;
    }
    
    public void completeTypeBindings(final CompilationUnitDeclaration[] parsedUnits, final boolean[] buildFieldsAndMethods, final int unitCount) {
        for (final CompilationUnitDeclaration parsedUnit : parsedUnits) {
            if (parsedUnit.scope != null) {
                final CompilationUnitDeclaration unitBeingCompleted = parsedUnit;
                this.unitBeingCompleted = unitBeingCompleted;
                unitBeingCompleted.scope.checkAndSetImports();
            }
        }
        for (final CompilationUnitDeclaration parsedUnit : parsedUnits) {
            if (parsedUnit.scope != null) {
                final CompilationUnitDeclaration unitBeingCompleted2 = parsedUnit;
                this.unitBeingCompleted = unitBeingCompleted2;
                unitBeingCompleted2.scope.connectTypeHierarchy();
            }
        }
        for (int i = 0; i < unitCount; ++i) {
            final CompilationUnitDeclaration parsedUnit = parsedUnits[i];
            if (parsedUnit.scope != null) {
                final CompilationUnitDeclaration unitBeingCompleted3 = parsedUnit;
                this.unitBeingCompleted = unitBeingCompleted3;
                unitBeingCompleted3.scope.checkParameterizedTypes();
                if (buildFieldsAndMethods[i]) {
                    parsedUnit.scope.buildFieldsAndMethods();
                }
            }
        }
        this.unitBeingCompleted = null;
    }
    
    public MethodBinding computeArrayClone(final MethodBinding objectClone) {
        if (this.arrayClone == null) {
            this.arrayClone = new MethodBinding((objectClone.modifiers & 0xFFFFFFFB) | 0x1, TypeConstants.CLONE, objectClone.returnType, Binding.NO_PARAMETERS, Binding.NO_EXCEPTIONS, (ReferenceBinding)objectClone.returnType);
        }
        return this.arrayClone;
    }
    
    public TypeBinding computeBoxingType(final TypeBinding type) {
        switch (type.id) {
            case 33: {
                return TypeBinding.BOOLEAN;
            }
            case 26: {
                return TypeBinding.BYTE;
            }
            case 28: {
                return TypeBinding.CHAR;
            }
            case 27: {
                return TypeBinding.SHORT;
            }
            case 32: {
                return TypeBinding.DOUBLE;
            }
            case 31: {
                return TypeBinding.FLOAT;
            }
            case 29: {
                return TypeBinding.INT;
            }
            case 30: {
                return TypeBinding.LONG;
            }
            case 10: {
                final TypeBinding boxedType = this.getType(LookupEnvironment.JAVA_LANG_INTEGER);
                if (boxedType != null) {
                    return boxedType;
                }
                return new ProblemReferenceBinding(LookupEnvironment.JAVA_LANG_INTEGER, null, 1);
            }
            case 3: {
                final TypeBinding boxedType = this.getType(LookupEnvironment.JAVA_LANG_BYTE);
                if (boxedType != null) {
                    return boxedType;
                }
                return new ProblemReferenceBinding(LookupEnvironment.JAVA_LANG_BYTE, null, 1);
            }
            case 4: {
                final TypeBinding boxedType = this.getType(LookupEnvironment.JAVA_LANG_SHORT);
                if (boxedType != null) {
                    return boxedType;
                }
                return new ProblemReferenceBinding(LookupEnvironment.JAVA_LANG_SHORT, null, 1);
            }
            case 2: {
                final TypeBinding boxedType = this.getType(LookupEnvironment.JAVA_LANG_CHARACTER);
                if (boxedType != null) {
                    return boxedType;
                }
                return new ProblemReferenceBinding(LookupEnvironment.JAVA_LANG_CHARACTER, null, 1);
            }
            case 7: {
                final TypeBinding boxedType = this.getType(LookupEnvironment.JAVA_LANG_LONG);
                if (boxedType != null) {
                    return boxedType;
                }
                return new ProblemReferenceBinding(LookupEnvironment.JAVA_LANG_LONG, null, 1);
            }
            case 9: {
                final TypeBinding boxedType = this.getType(LookupEnvironment.JAVA_LANG_FLOAT);
                if (boxedType != null) {
                    return boxedType;
                }
                return new ProblemReferenceBinding(LookupEnvironment.JAVA_LANG_FLOAT, null, 1);
            }
            case 8: {
                final TypeBinding boxedType = this.getType(LookupEnvironment.JAVA_LANG_DOUBLE);
                if (boxedType != null) {
                    return boxedType;
                }
                return new ProblemReferenceBinding(LookupEnvironment.JAVA_LANG_DOUBLE, null, 1);
            }
            case 5: {
                final TypeBinding boxedType = this.getType(LookupEnvironment.JAVA_LANG_BOOLEAN);
                if (boxedType != null) {
                    return boxedType;
                }
                return new ProblemReferenceBinding(LookupEnvironment.JAVA_LANG_BOOLEAN, null, 1);
            }
            default: {
                Label_0558: {
                    switch (type.kind()) {
                        case 516:
                        case 4100:
                        case 8196: {
                            switch (type.erasure().id) {
                                case 33: {
                                    return TypeBinding.BOOLEAN;
                                }
                                case 26: {
                                    return TypeBinding.BYTE;
                                }
                                case 28: {
                                    return TypeBinding.CHAR;
                                }
                                case 27: {
                                    return TypeBinding.SHORT;
                                }
                                case 32: {
                                    return TypeBinding.DOUBLE;
                                }
                                case 31: {
                                    return TypeBinding.FLOAT;
                                }
                                case 29: {
                                    return TypeBinding.INT;
                                }
                                case 30: {
                                    return TypeBinding.LONG;
                                }
                                default: {
                                    break Label_0558;
                                }
                            }
                            break;
                        }
                        case 65540: {
                            return ((PolyTypeBinding)type).computeBoxingType();
                        }
                        case 32772: {
                            return this.computeBoxingType(type.getIntersectingTypes()[0]);
                        }
                    }
                }
                return type;
            }
        }
    }
    
    private PackageBinding computePackageFrom(final char[][] constantPoolName, final boolean isMissing) {
        if (constantPoolName.length == 1) {
            return this.defaultPackage;
        }
        PackageBinding packageBinding = this.getPackage0(constantPoolName[0]);
        if (packageBinding == null || packageBinding == LookupEnvironment.TheNotFoundPackage) {
            packageBinding = new PackageBinding(constantPoolName[0], this);
            if (isMissing) {
                final PackageBinding packageBinding2 = packageBinding;
                packageBinding2.tagBits |= 0x80L;
            }
            this.knownPackages.put(constantPoolName[0], packageBinding);
        }
        for (int i = 1, length = constantPoolName.length - 1; i < length; ++i) {
            final PackageBinding parent = packageBinding;
            if ((packageBinding = parent.getPackage0(constantPoolName[i])) == null || packageBinding == LookupEnvironment.TheNotFoundPackage) {
                packageBinding = new PackageBinding(CharOperation.subarray(constantPoolName, 0, i + 1), parent, this);
                if (isMissing) {
                    final PackageBinding packageBinding3 = packageBinding;
                    packageBinding3.tagBits |= 0x80L;
                }
                parent.addPackage(packageBinding);
            }
        }
        return packageBinding;
    }
    
    public ReferenceBinding convertToParameterizedType(final ReferenceBinding originalType) {
        if (originalType != null) {
            final boolean isGeneric = originalType.isGenericType();
            ReferenceBinding convertedEnclosingType;
            final ReferenceBinding originalEnclosingType = convertedEnclosingType = originalType.enclosingType();
            boolean needToConvert = isGeneric;
            if (originalEnclosingType != null) {
                convertedEnclosingType = (ReferenceBinding)(originalType.isStatic() ? this.convertToRawType(originalEnclosingType, false) : this.convertToParameterizedType(originalEnclosingType));
                needToConvert |= TypeBinding.notEquals(originalEnclosingType, convertedEnclosingType);
            }
            if (needToConvert) {
                return this.createParameterizedType(originalType, (TypeBinding[])(isGeneric ? originalType.typeVariables() : null), convertedEnclosingType);
            }
        }
        return originalType;
    }
    
    public TypeBinding convertToRawType(final TypeBinding type, final boolean forceRawEnclosingType) {
        int dimension = 0;
        TypeBinding originalType = null;
        switch (type.kind()) {
            case 132:
            case 516:
            case 1028:
            case 4100:
            case 8196: {
                return type;
            }
            case 68: {
                dimension = type.dimensions();
                originalType = type.leafComponentType();
                break;
            }
            default: {
                if (type.id == 1) {
                    return type;
                }
                dimension = 0;
                originalType = type;
                break;
            }
        }
        boolean needToConvert = false;
        switch (originalType.kind()) {
            case 132: {
                return type;
            }
            case 2052: {
                needToConvert = true;
                break;
            }
            case 260: {
                final ParameterizedTypeBinding paramType = (ParameterizedTypeBinding)originalType;
                needToConvert = paramType.genericType().isGenericType();
                break;
            }
            default: {
                needToConvert = false;
                break;
            }
        }
        final ReferenceBinding originalEnclosing = originalType.enclosingType();
        TypeBinding convertedType;
        if (originalEnclosing == null) {
            convertedType = (needToConvert ? this.createRawType((ReferenceBinding)originalType.erasure(), null) : originalType);
        }
        else {
            ReferenceBinding convertedEnclosing;
            if (originalEnclosing.kind() == 1028) {
                needToConvert |= !((ReferenceBinding)originalType).isStatic();
                convertedEnclosing = originalEnclosing;
            }
            else if (forceRawEnclosingType && !needToConvert) {
                convertedEnclosing = (ReferenceBinding)this.convertToRawType(originalEnclosing, forceRawEnclosingType);
                needToConvert = TypeBinding.notEquals(originalEnclosing, convertedEnclosing);
            }
            else if (needToConvert || ((ReferenceBinding)originalType).isStatic()) {
                convertedEnclosing = (ReferenceBinding)this.convertToRawType(originalEnclosing, false);
            }
            else {
                convertedEnclosing = this.convertToParameterizedType(originalEnclosing);
            }
            if (needToConvert) {
                convertedType = this.createRawType((ReferenceBinding)originalType.erasure(), convertedEnclosing);
            }
            else if (TypeBinding.notEquals(originalEnclosing, convertedEnclosing)) {
                convertedType = this.createParameterizedType((ReferenceBinding)originalType.erasure(), null, convertedEnclosing);
            }
            else {
                convertedType = originalType;
            }
        }
        if (TypeBinding.notEquals(originalType, convertedType)) {
            return (dimension > 0) ? this.createArrayType(convertedType, dimension) : convertedType;
        }
        return type;
    }
    
    public ReferenceBinding[] convertToRawTypes(final ReferenceBinding[] originalTypes, final boolean forceErasure, final boolean forceRawEnclosingType) {
        if (originalTypes == null) {
            return null;
        }
        ReferenceBinding[] convertedTypes = originalTypes;
        for (int i = 0, length = originalTypes.length; i < length; ++i) {
            final ReferenceBinding originalType = originalTypes[i];
            final ReferenceBinding convertedType = (ReferenceBinding)this.convertToRawType(forceErasure ? originalType.erasure() : originalType, forceRawEnclosingType);
            if (TypeBinding.notEquals(convertedType, originalType)) {
                if (convertedTypes == originalTypes) {
                    System.arraycopy(originalTypes, 0, convertedTypes = new ReferenceBinding[length], 0, i);
                }
                convertedTypes[i] = convertedType;
            }
            else if (convertedTypes != originalTypes) {
                convertedTypes[i] = originalType;
            }
        }
        return convertedTypes;
    }
    
    public TypeBinding convertUnresolvedBinaryToRawType(final TypeBinding type) {
        int dimension = 0;
        TypeBinding originalType = null;
        switch (type.kind()) {
            case 132:
            case 516:
            case 1028:
            case 4100:
            case 8196: {
                return type;
            }
            case 68: {
                dimension = type.dimensions();
                originalType = type.leafComponentType();
                break;
            }
            default: {
                if (type.id == 1) {
                    return type;
                }
                dimension = 0;
                originalType = type;
                break;
            }
        }
        boolean needToConvert = false;
        switch (originalType.kind()) {
            case 132: {
                return type;
            }
            case 2052: {
                needToConvert = true;
                break;
            }
            case 260: {
                final ParameterizedTypeBinding paramType = (ParameterizedTypeBinding)originalType;
                needToConvert = paramType.genericType().isGenericType();
                break;
            }
            default: {
                needToConvert = false;
                break;
            }
        }
        final ReferenceBinding originalEnclosing = originalType.enclosingType();
        TypeBinding convertedType;
        if (originalEnclosing == null) {
            convertedType = (needToConvert ? this.createRawType((ReferenceBinding)originalType.erasure(), null) : originalType);
        }
        else {
            final ReferenceBinding convertedEnclosing = (ReferenceBinding)this.convertUnresolvedBinaryToRawType(originalEnclosing);
            if (TypeBinding.notEquals(convertedEnclosing, originalEnclosing)) {
                needToConvert |= !((ReferenceBinding)originalType).isStatic();
            }
            if (needToConvert) {
                convertedType = this.createRawType((ReferenceBinding)originalType.erasure(), convertedEnclosing);
            }
            else if (TypeBinding.notEquals(originalEnclosing, convertedEnclosing)) {
                convertedType = this.createParameterizedType((ReferenceBinding)originalType.erasure(), null, convertedEnclosing);
            }
            else {
                convertedType = originalType;
            }
        }
        if (TypeBinding.notEquals(originalType, convertedType)) {
            return (dimension > 0) ? this.createArrayType(convertedType, dimension) : convertedType;
        }
        return type;
    }
    
    public AnnotationBinding createAnnotation(final ReferenceBinding annotationType, final ElementValuePair[] pairs) {
        if (pairs.length != 0) {
            AnnotationBinding.setMethodBindings(annotationType, pairs);
            return new AnnotationBinding(annotationType, pairs);
        }
        return this.typeSystem.getAnnotationType(annotationType, true);
    }
    
    public AnnotationBinding createUnresolvedAnnotation(final ReferenceBinding annotationType, final ElementValuePair[] pairs) {
        if (pairs.length != 0) {
            return new UnresolvedAnnotationBinding(annotationType, pairs, this);
        }
        return this.typeSystem.getAnnotationType(annotationType, false);
    }
    
    public ArrayBinding createArrayType(final TypeBinding leafComponentType, final int dimensionCount) {
        return this.typeSystem.getArrayType(leafComponentType, dimensionCount);
    }
    
    public ArrayBinding createArrayType(final TypeBinding leafComponentType, final int dimensionCount, final AnnotationBinding[] annotations) {
        return this.typeSystem.getArrayType(leafComponentType, dimensionCount, annotations);
    }
    
    public TypeBinding createIntersectionType18(final ReferenceBinding[] intersectingTypes) {
        return this.typeSystem.getIntersectionType18(intersectingTypes);
    }
    
    public BinaryTypeBinding createBinaryTypeFrom(final IBinaryType binaryType, final PackageBinding packageBinding, final AccessRestriction accessRestriction) {
        return this.createBinaryTypeFrom(binaryType, packageBinding, true, accessRestriction);
    }
    
    public BinaryTypeBinding createBinaryTypeFrom(final IBinaryType binaryType, final PackageBinding packageBinding, final boolean needFieldsAndMethods, final AccessRestriction accessRestriction) {
        final BinaryTypeBinding binaryBinding = new BinaryTypeBinding(packageBinding, binaryType, this);
        final ReferenceBinding cachedType = packageBinding.getType0(binaryBinding.compoundName[binaryBinding.compoundName.length - 1]);
        if (cachedType == null || cachedType.isUnresolvedType()) {
            packageBinding.addType(binaryBinding);
            this.setAccessRestriction(binaryBinding, accessRestriction);
            binaryBinding.cachePartsFrom(binaryType, needFieldsAndMethods);
            return binaryBinding;
        }
        if (cachedType.isBinaryBinding()) {
            return (BinaryTypeBinding)cachedType;
        }
        return null;
    }
    
    public MissingTypeBinding createMissingType(PackageBinding packageBinding, final char[][] compoundName) {
        if (packageBinding == null) {
            packageBinding = this.computePackageFrom(compoundName, true);
            if (packageBinding == LookupEnvironment.TheNotFoundPackage) {
                packageBinding = this.defaultPackage;
            }
        }
        final MissingTypeBinding missingType = new MissingTypeBinding(packageBinding, compoundName, this);
        if (missingType.id != 1) {
            ReferenceBinding objectType = this.getType(TypeConstants.JAVA_LANG_OBJECT);
            if (objectType == null) {
                objectType = this.createMissingType(null, TypeConstants.JAVA_LANG_OBJECT);
            }
            missingType.setMissingSuperclass(objectType);
        }
        packageBinding.addType(missingType);
        if (this.missingTypes == null) {
            this.missingTypes = new ArrayList(3);
        }
        this.missingTypes.add(missingType);
        return missingType;
    }
    
    public PackageBinding createPackage(final char[][] compoundName) {
        PackageBinding packageBinding = this.getPackage0(compoundName[0]);
        if (packageBinding == null || packageBinding == LookupEnvironment.TheNotFoundPackage) {
            packageBinding = new PackageBinding(compoundName[0], this);
            this.knownPackages.put(compoundName[0], packageBinding);
        }
        for (int i = 1, length = compoundName.length; i < length; ++i) {
            final ReferenceBinding type = packageBinding.getType0(compoundName[i]);
            if (type != null && type != LookupEnvironment.TheNotFoundType && !(type instanceof UnresolvedReferenceBinding)) {
                return null;
            }
            final PackageBinding parent = packageBinding;
            if ((packageBinding = parent.getPackage0(compoundName[i])) == null || packageBinding == LookupEnvironment.TheNotFoundPackage) {
                if (this.nameEnvironment instanceof INameEnvironmentExtension) {
                    if (((INameEnvironmentExtension)this.nameEnvironment).findType(compoundName[i], parent.compoundName, false) != null) {
                        return null;
                    }
                }
                else if (this.nameEnvironment.findType(compoundName[i], parent.compoundName) != null) {
                    return null;
                }
                packageBinding = new PackageBinding(CharOperation.subarray(compoundName, 0, i + 1), parent, this);
                parent.addPackage(packageBinding);
            }
        }
        return packageBinding;
    }
    
    public ParameterizedGenericMethodBinding createParameterizedGenericMethod(final MethodBinding genericMethod, final RawTypeBinding rawType) {
        ParameterizedGenericMethodBinding[] cachedInfo = (ParameterizedGenericMethodBinding[])this.uniqueParameterizedGenericMethodBindings.get(genericMethod);
        boolean needToGrow = false;
        int index = 0;
        if (cachedInfo != null) {
            for (int max = cachedInfo.length; index < max; ++index) {
                final ParameterizedGenericMethodBinding cachedMethod = cachedInfo[index];
                if (cachedMethod == null) {
                    break;
                }
                if (cachedMethod.isRaw) {
                    if (cachedMethod.declaringClass == ((rawType == null) ? genericMethod.declaringClass : rawType)) {
                        return cachedMethod;
                    }
                }
            }
            needToGrow = true;
        }
        else {
            cachedInfo = new ParameterizedGenericMethodBinding[5];
            this.uniqueParameterizedGenericMethodBindings.put(genericMethod, cachedInfo);
        }
        final int length = cachedInfo.length;
        if (needToGrow && index == length) {
            System.arraycopy(cachedInfo, 0, cachedInfo = new ParameterizedGenericMethodBinding[length * 2], 0, length);
            this.uniqueParameterizedGenericMethodBindings.put(genericMethod, cachedInfo);
        }
        final ParameterizedGenericMethodBinding parameterizedGenericMethod = new ParameterizedGenericMethodBinding(genericMethod, rawType, this);
        return cachedInfo[index] = parameterizedGenericMethod;
    }
    
    public ParameterizedGenericMethodBinding createParameterizedGenericMethod(final MethodBinding genericMethod, final TypeBinding[] typeArguments) {
        return this.createParameterizedGenericMethod(genericMethod, typeArguments, false, false);
    }
    
    public ParameterizedGenericMethodBinding createParameterizedGenericMethod(final MethodBinding genericMethod, final TypeBinding[] typeArguments, final boolean inferredWithUncheckedConversion, final boolean hasReturnProblem) {
        ParameterizedGenericMethodBinding[] cachedInfo = (ParameterizedGenericMethodBinding[])this.uniqueParameterizedGenericMethodBindings.get(genericMethod);
        final int argLength = (typeArguments == null) ? 0 : typeArguments.length;
        boolean needToGrow = false;
        int index = 0;
        if (cachedInfo != null) {
        Label_0229:
            for (int max = cachedInfo.length; index < max; ++index) {
                final ParameterizedGenericMethodBinding cachedMethod = cachedInfo[index];
                if (cachedMethod == null) {
                    break;
                }
                if (!cachedMethod.isRaw) {
                    if (cachedMethod.inferredWithUncheckedConversion == inferredWithUncheckedConversion) {
                        final TypeBinding[] cachedArguments = cachedMethod.typeArguments;
                        final int cachedArgLength = (cachedArguments == null) ? 0 : cachedArguments.length;
                        if (argLength == cachedArgLength) {
                            for (int j = 0; j < cachedArgLength; ++j) {
                                if (typeArguments[j] != cachedArguments[j]) {
                                    continue Label_0229;
                                }
                            }
                            if (inferredWithUncheckedConversion) {
                                if (cachedMethod.returnType.isParameterizedType()) {
                                    continue;
                                }
                                if (cachedMethod.returnType.isTypeVariable()) {
                                    continue;
                                }
                                ReferenceBinding[] thrownExceptions;
                                for (int length2 = (thrownExceptions = cachedMethod.thrownExceptions).length, i = 0; i < length2; ++i) {
                                    final TypeBinding exc = thrownExceptions[i];
                                    if (exc.isParameterizedType()) {
                                        continue Label_0229;
                                    }
                                    if (exc.isTypeVariable()) {
                                        continue Label_0229;
                                    }
                                }
                            }
                            return cachedMethod;
                        }
                    }
                }
            }
            needToGrow = true;
        }
        else {
            cachedInfo = new ParameterizedGenericMethodBinding[5];
            this.uniqueParameterizedGenericMethodBindings.put(genericMethod, cachedInfo);
        }
        final int length = cachedInfo.length;
        if (needToGrow && index == length) {
            System.arraycopy(cachedInfo, 0, cachedInfo = new ParameterizedGenericMethodBinding[length * 2], 0, length);
            this.uniqueParameterizedGenericMethodBindings.put(genericMethod, cachedInfo);
        }
        final ParameterizedGenericMethodBinding parameterizedGenericMethod = new ParameterizedGenericMethodBinding(genericMethod, typeArguments, this, inferredWithUncheckedConversion, hasReturnProblem);
        return cachedInfo[index] = parameterizedGenericMethod;
    }
    
    public PolymorphicMethodBinding createPolymorphicMethod(final MethodBinding originalPolymorphicMethod, final TypeBinding[] parameters) {
        final String key = new String(originalPolymorphicMethod.selector);
        PolymorphicMethodBinding[] cachedInfo = (PolymorphicMethodBinding[])this.uniquePolymorphicMethodBindings.get(key);
        final int parametersLength = (parameters == null) ? 0 : parameters.length;
        final TypeBinding[] parametersTypeBinding = new TypeBinding[parametersLength];
        for (int i = 0; i < parametersLength; ++i) {
            final TypeBinding parameterTypeBinding = parameters[i];
            if (parameterTypeBinding.id == 12) {
                parametersTypeBinding[i] = this.getType(LookupEnvironment.JAVA_LANG_VOID);
            }
            else {
                parametersTypeBinding[i] = parameterTypeBinding.erasure();
            }
        }
        boolean needToGrow = false;
        int index = 0;
        if (cachedInfo != null) {
            for (int max = cachedInfo.length; index < max; ++index) {
                final PolymorphicMethodBinding cachedMethod = cachedInfo[index];
                if (cachedMethod == null) {
                    break;
                }
                if (cachedMethod.matches(parametersTypeBinding, originalPolymorphicMethod.returnType)) {
                    return cachedMethod;
                }
            }
            needToGrow = true;
        }
        else {
            cachedInfo = new PolymorphicMethodBinding[5];
            this.uniquePolymorphicMethodBindings.put(key, cachedInfo);
        }
        final int length = cachedInfo.length;
        if (needToGrow && index == length) {
            System.arraycopy(cachedInfo, 0, cachedInfo = new PolymorphicMethodBinding[length * 2], 0, length);
            this.uniquePolymorphicMethodBindings.put(key, cachedInfo);
        }
        final PolymorphicMethodBinding polymorphicMethod = new PolymorphicMethodBinding(originalPolymorphicMethod, parametersTypeBinding);
        return cachedInfo[index] = polymorphicMethod;
    }
    
    public boolean usesAnnotatedTypeSystem() {
        return this.typeSystem.isAnnotatedTypeSystem();
    }
    
    public MethodBinding updatePolymorphicMethodReturnType(final PolymorphicMethodBinding binding, final TypeBinding typeBinding) {
        final String key = new String(binding.selector);
        PolymorphicMethodBinding[] cachedInfo = (PolymorphicMethodBinding[])this.uniquePolymorphicMethodBindings.get(key);
        boolean needToGrow = false;
        int index = 0;
        final TypeBinding[] parameters = binding.parameters;
        if (cachedInfo != null) {
            for (int max = cachedInfo.length; index < max; ++index) {
                final PolymorphicMethodBinding cachedMethod = cachedInfo[index];
                if (cachedMethod == null) {
                    break;
                }
                if (cachedMethod.matches(parameters, typeBinding)) {
                    return cachedMethod;
                }
            }
            needToGrow = true;
        }
        else {
            cachedInfo = new PolymorphicMethodBinding[5];
            this.uniquePolymorphicMethodBindings.put(key, cachedInfo);
        }
        final int length = cachedInfo.length;
        if (needToGrow && index == length) {
            System.arraycopy(cachedInfo, 0, cachedInfo = new PolymorphicMethodBinding[length * 2], 0, length);
            this.uniquePolymorphicMethodBindings.put(key, cachedInfo);
        }
        final PolymorphicMethodBinding polymorphicMethod = new PolymorphicMethodBinding(binding.original(), typeBinding, parameters);
        return cachedInfo[index] = polymorphicMethod;
    }
    
    public ParameterizedMethodBinding createGetClassMethod(final TypeBinding receiverType, final MethodBinding originalMethod, final Scope scope) {
        ParameterizedMethodBinding retVal = null;
        if (this.uniqueGetClassMethodBinding == null) {
            this.uniqueGetClassMethodBinding = new SimpleLookupTable(3);
        }
        else {
            retVal = (ParameterizedMethodBinding)this.uniqueGetClassMethodBinding.get(receiverType);
        }
        if (retVal == null) {
            retVal = ParameterizedMethodBinding.instantiateGetClass(receiverType, originalMethod, scope);
            this.uniqueGetClassMethodBinding.put(receiverType, retVal);
        }
        return retVal;
    }
    
    public ReferenceBinding createMemberType(final ReferenceBinding memberType, final ReferenceBinding enclosingType) {
        return this.typeSystem.getMemberType(memberType, enclosingType);
    }
    
    public ParameterizedTypeBinding createParameterizedType(final ReferenceBinding genericType, final TypeBinding[] typeArguments, final ReferenceBinding enclosingType) {
        final AnnotationBinding[] annotations = genericType.typeAnnotations;
        if (annotations != Binding.NO_ANNOTATIONS) {
            return this.typeSystem.getParameterizedType((ReferenceBinding)genericType.unannotated(), typeArguments, enclosingType, annotations);
        }
        return this.typeSystem.getParameterizedType(genericType, typeArguments, enclosingType);
    }
    
    public ParameterizedTypeBinding createParameterizedType(final ReferenceBinding genericType, final TypeBinding[] typeArguments, final ReferenceBinding enclosingType, final AnnotationBinding[] annotations) {
        return this.typeSystem.getParameterizedType(genericType, typeArguments, enclosingType, annotations);
    }
    
    public TypeBinding createAnnotatedType(final TypeBinding type, final AnnotationBinding[][] annotations) {
        return this.typeSystem.getAnnotatedType(type, annotations);
    }
    
    public TypeBinding createAnnotatedType(final TypeBinding type, AnnotationBinding[] newbies) {
        final int newLength = (newbies == null) ? 0 : newbies.length;
        if (type == null || newLength == 0) {
            return type;
        }
        final AnnotationBinding[] oldies = type.getTypeAnnotations();
        final int oldLength = (oldies == null) ? 0 : oldies.length;
        if (oldLength > 0) {
            System.arraycopy(newbies, 0, newbies = new AnnotationBinding[newLength + oldLength], 0, newLength);
            System.arraycopy(oldies, 0, newbies, newLength, oldLength);
        }
        if (this.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
            long tagBitsSeen = 0L;
            final AnnotationBinding[] filtered = new AnnotationBinding[newbies.length];
            int count = 0;
            for (int i = 0; i < newbies.length; ++i) {
                if (newbies[i] == null) {
                    filtered[count++] = null;
                }
                else {
                    long tagBits = 0L;
                    if (newbies[i].type.hasNullBit(32)) {
                        tagBits = 72057594037927936L;
                    }
                    else if (newbies[i].type.hasNullBit(64)) {
                        tagBits = 36028797018963968L;
                    }
                    if ((tagBitsSeen & tagBits) == 0x0L) {
                        tagBitsSeen |= tagBits;
                        filtered[count++] = newbies[i];
                    }
                }
            }
            if (count < newbies.length) {
                System.arraycopy(filtered, 0, newbies = new AnnotationBinding[count], 0, count);
            }
        }
        return this.typeSystem.getAnnotatedType(type, new AnnotationBinding[][] { newbies });
    }
    
    public RawTypeBinding createRawType(final ReferenceBinding genericType, final ReferenceBinding enclosingType) {
        final AnnotationBinding[] annotations = genericType.typeAnnotations;
        if (annotations != Binding.NO_ANNOTATIONS) {
            return this.typeSystem.getRawType((ReferenceBinding)genericType.unannotated(), enclosingType, annotations);
        }
        return this.typeSystem.getRawType(genericType, enclosingType);
    }
    
    public RawTypeBinding createRawType(final ReferenceBinding genericType, final ReferenceBinding enclosingType, final AnnotationBinding[] annotations) {
        return this.typeSystem.getRawType(genericType, enclosingType, annotations);
    }
    
    public WildcardBinding createWildcard(final ReferenceBinding genericType, final int rank, final TypeBinding bound, final TypeBinding[] otherBounds, final int boundKind) {
        if (genericType != null) {
            final AnnotationBinding[] annotations = genericType.typeAnnotations;
            if (annotations != Binding.NO_ANNOTATIONS) {
                return this.typeSystem.getWildcard((ReferenceBinding)genericType.unannotated(), rank, bound, otherBounds, boundKind, annotations);
            }
        }
        return this.typeSystem.getWildcard(genericType, rank, bound, otherBounds, boundKind);
    }
    
    public CaptureBinding createCapturedWildcard(final WildcardBinding wildcard, final ReferenceBinding contextType, final int start, final int end, final ASTNode cud, final int id) {
        return this.typeSystem.getCapturedWildcard(wildcard, contextType, start, end, cud, id);
    }
    
    public WildcardBinding createWildcard(final ReferenceBinding genericType, final int rank, final TypeBinding bound, final TypeBinding[] otherBounds, final int boundKind, final AnnotationBinding[] annotations) {
        return this.typeSystem.getWildcard(genericType, rank, bound, otherBounds, boundKind, annotations);
    }
    
    public AccessRestriction getAccessRestriction(final TypeBinding type) {
        return this.accessRestrictions.get(type);
    }
    
    public ReferenceBinding getCachedType(final char[][] compoundName) {
        if (compoundName.length == 1) {
            return this.defaultPackage.getType0(compoundName[0]);
        }
        PackageBinding packageBinding = this.getPackage0(compoundName[0]);
        if (packageBinding == null || packageBinding == LookupEnvironment.TheNotFoundPackage) {
            return null;
        }
        for (int i = 1, packageLength = compoundName.length - 1; i < packageLength; ++i) {
            if ((packageBinding = packageBinding.getPackage0(compoundName[i])) == null || packageBinding == LookupEnvironment.TheNotFoundPackage) {
                return null;
            }
        }
        return packageBinding.getType0(compoundName[compoundName.length - 1]);
    }
    
    public AnnotationBinding getNullableAnnotation() {
        if (this.nullableAnnotation != null) {
            return this.nullableAnnotation;
        }
        final ReferenceBinding nullable = this.getResolvedType(this.globalOptions.nullableAnnotationName, null);
        return this.nullableAnnotation = this.typeSystem.getAnnotationType(nullable, true);
    }
    
    public char[][] getNullableAnnotationName() {
        return this.globalOptions.nullableAnnotationName;
    }
    
    public AnnotationBinding getNonNullAnnotation() {
        if (this.nonNullAnnotation != null) {
            return this.nonNullAnnotation;
        }
        final ReferenceBinding nonNull = this.getResolvedType(this.globalOptions.nonNullAnnotationName, null);
        return this.nonNullAnnotation = this.typeSystem.getAnnotationType(nonNull, true);
    }
    
    public AnnotationBinding[] nullAnnotationsFromTagBits(final long nullTagBits) {
        if (nullTagBits == 72057594037927936L) {
            return new AnnotationBinding[] { this.getNonNullAnnotation() };
        }
        if (nullTagBits == 36028797018963968L) {
            return new AnnotationBinding[] { this.getNullableAnnotation() };
        }
        return null;
    }
    
    public char[][] getNonNullAnnotationName() {
        return this.globalOptions.nonNullAnnotationName;
    }
    
    public char[][] getNonNullByDefaultAnnotationName() {
        return this.globalOptions.nonNullByDefaultAnnotationName;
    }
    
    int getNullAnnotationBit(final char[][] qualifiedTypeName) {
        if (this.allNullAnnotations == null) {
            (this.allNullAnnotations = new HashMap<String, Integer>()).put(CharOperation.toString(this.globalOptions.nonNullAnnotationName), 32);
            this.allNullAnnotations.put(CharOperation.toString(this.globalOptions.nullableAnnotationName), 64);
            this.allNullAnnotations.put(CharOperation.toString(this.globalOptions.nonNullByDefaultAnnotationName), 128);
            String[] nullableAnnotationSecondaryNames;
            for (int length = (nullableAnnotationSecondaryNames = this.globalOptions.nullableAnnotationSecondaryNames).length, i = 0; i < length; ++i) {
                final String name = nullableAnnotationSecondaryNames[i];
                this.allNullAnnotations.put(name, 64);
            }
            String[] nonNullAnnotationSecondaryNames;
            for (int length2 = (nonNullAnnotationSecondaryNames = this.globalOptions.nonNullAnnotationSecondaryNames).length, j = 0; j < length2; ++j) {
                final String name = nonNullAnnotationSecondaryNames[j];
                this.allNullAnnotations.put(name, 32);
            }
            String[] nonNullByDefaultAnnotationSecondaryNames;
            for (int length3 = (nonNullByDefaultAnnotationSecondaryNames = this.globalOptions.nonNullByDefaultAnnotationSecondaryNames).length, k = 0; k < length3; ++k) {
                final String name = nonNullByDefaultAnnotationSecondaryNames[k];
                this.allNullAnnotations.put(name, 128);
            }
        }
        final String qualifiedTypeString = CharOperation.toString(qualifiedTypeName);
        final Integer typeBit = this.allNullAnnotations.get(qualifiedTypeString);
        return (typeBit == null) ? 0 : typeBit;
    }
    
    public boolean isNullnessAnnotationPackage(final PackageBinding pkg) {
        return this.nonnullAnnotationPackage == pkg || this.nullableAnnotationPackage == pkg || this.nonnullByDefaultAnnotationPackage == pkg;
    }
    
    public boolean usesNullTypeAnnotations() {
        if (this.globalOptions.useNullTypeAnnotations != null) {
            return this.globalOptions.useNullTypeAnnotations;
        }
        this.initializeUsesNullTypeAnnotation();
        for (final MethodBinding enumMethod : this.deferredEnumMethods) {
            int purpose = 0;
            if (CharOperation.equals(enumMethod.selector, TypeConstants.VALUEOF)) {
                purpose = 10;
            }
            else if (CharOperation.equals(enumMethod.selector, TypeConstants.VALUES)) {
                purpose = 9;
            }
            if (purpose != 0) {
                SyntheticMethodBinding.markNonNull(enumMethod, purpose, this);
            }
        }
        this.deferredEnumMethods.clear();
        return this.globalOptions.useNullTypeAnnotations;
    }
    
    private void initializeUsesNullTypeAnnotation() {
        this.globalOptions.useNullTypeAnnotations = Boolean.FALSE;
        if (!this.globalOptions.isAnnotationBasedNullAnalysisEnabled || this.globalOptions.originalSourceLevel < 3407872L) {
            return;
        }
        final ReferenceBinding nullable = (this.nullableAnnotation != null) ? this.nullableAnnotation.getAnnotationType() : this.getType(this.getNullableAnnotationName());
        final ReferenceBinding nonNull = (this.nonNullAnnotation != null) ? this.nonNullAnnotation.getAnnotationType() : this.getType(this.getNonNullAnnotationName());
        if (nullable == null && nonNull == null) {
            return;
        }
        if (nullable == null || nonNull == null) {
            return;
        }
        final long nullableMetaBits = nullable.getAnnotationTagBits() & 0x20000000000000L;
        final long nonNullMetaBits = nonNull.getAnnotationTagBits() & 0x20000000000000L;
        if (nullableMetaBits != nonNullMetaBits) {
            return;
        }
        if (nullableMetaBits == 0L) {
            return;
        }
        this.globalOptions.useNullTypeAnnotations = Boolean.TRUE;
    }
    
    PackageBinding getPackage0(final char[] name) {
        return this.knownPackages.get(name);
    }
    
    public ReferenceBinding getResolvedType(final char[][] compoundName, final Scope scope) {
        final ReferenceBinding type = this.getType(compoundName);
        if (type != null) {
            return type;
        }
        this.problemReporter.isClassPathCorrect(compoundName, (scope == null) ? this.unitBeingCompleted : scope.referenceCompilationUnit(), this.missingClassFileLocation);
        return this.createMissingType(null, compoundName);
    }
    
    PackageBinding getTopLevelPackage(final char[] name) {
        PackageBinding packageBinding = this.getPackage0(name);
        if (packageBinding != null) {
            if (packageBinding == LookupEnvironment.TheNotFoundPackage) {
                return null;
            }
            return packageBinding;
        }
        else {
            if (this.nameEnvironment.isPackage(null, name)) {
                this.knownPackages.put(name, packageBinding = new PackageBinding(name, this));
                return packageBinding;
            }
            this.knownPackages.put(name, LookupEnvironment.TheNotFoundPackage);
            return null;
        }
    }
    
    public ReferenceBinding getType(final char[][] compoundName) {
        ReferenceBinding referenceBinding;
        if (compoundName.length == 1) {
            if ((referenceBinding = this.defaultPackage.getType0(compoundName[0])) == null) {
                final PackageBinding packageBinding = this.getPackage0(compoundName[0]);
                if (packageBinding != null && packageBinding != LookupEnvironment.TheNotFoundPackage) {
                    return null;
                }
                referenceBinding = this.askForType(this.defaultPackage, compoundName[0]);
            }
        }
        else {
            PackageBinding packageBinding = this.getPackage0(compoundName[0]);
            if (packageBinding == LookupEnvironment.TheNotFoundPackage) {
                return null;
            }
            if (packageBinding != null) {
                for (int i = 1, packageLength = compoundName.length - 1; i < packageLength; ++i) {
                    if ((packageBinding = packageBinding.getPackage0(compoundName[i])) == null) {
                        break;
                    }
                    if (packageBinding == LookupEnvironment.TheNotFoundPackage) {
                        return null;
                    }
                }
            }
            if (packageBinding == null) {
                referenceBinding = this.askForType(compoundName);
            }
            else if ((referenceBinding = packageBinding.getType0(compoundName[compoundName.length - 1])) == null) {
                referenceBinding = this.askForType(packageBinding, compoundName[compoundName.length - 1]);
            }
        }
        if (referenceBinding == null || referenceBinding == LookupEnvironment.TheNotFoundType) {
            return null;
        }
        referenceBinding = (ReferenceBinding)BinaryTypeBinding.resolveType(referenceBinding, this, false);
        if (referenceBinding.isNestedType()) {
            return new ProblemReferenceBinding(compoundName, referenceBinding, 4);
        }
        return referenceBinding;
    }
    
    private TypeBinding[] getTypeArgumentsFromSignature(final SignatureWrapper wrapper, final TypeVariableBinding[] staticVariables, final ReferenceBinding enclosingType, final ReferenceBinding genericType, final char[][][] missingTypeNames, final ITypeAnnotationWalker walker) {
        final ArrayList args = new ArrayList(2);
        int rank = 0;
        do {
            args.add(this.getTypeFromVariantTypeSignature(wrapper, staticVariables, enclosingType, genericType, rank, missingTypeNames, walker.toTypeArgument(rank++)));
        } while (wrapper.signature[wrapper.start] != '>');
        ++wrapper.start;
        final TypeBinding[] typeArguments = new TypeBinding[args.size()];
        args.toArray(typeArguments);
        return typeArguments;
    }
    
    private ReferenceBinding getTypeFromCompoundName(final char[][] compoundName, final boolean isParameterized, final boolean wasMissingType) {
        ReferenceBinding binding = this.getCachedType(compoundName);
        if (binding == null) {
            final PackageBinding packageBinding = this.computePackageFrom(compoundName, false);
            binding = new UnresolvedReferenceBinding(compoundName, packageBinding);
            if (wasMissingType) {
                final ReferenceBinding referenceBinding = binding;
                referenceBinding.tagBits |= 0x80L;
            }
            packageBinding.addType(binding);
        }
        else if (binding == LookupEnvironment.TheNotFoundType) {
            if (!wasMissingType) {
                this.problemReporter.isClassPathCorrect(compoundName, this.unitBeingCompleted, this.missingClassFileLocation);
            }
            binding = this.createMissingType(null, compoundName);
        }
        else if (!isParameterized) {
            binding = (ReferenceBinding)this.convertUnresolvedBinaryToRawType(binding);
        }
        return binding;
    }
    
    ReferenceBinding getTypeFromConstantPoolName(final char[] signature, final int start, int end, final boolean isParameterized, final char[][][] missingTypeNames, final ITypeAnnotationWalker walker) {
        if (end == -1) {
            end = signature.length;
        }
        final char[][] compoundName = CharOperation.splitOn('/', signature, start, end);
        boolean wasMissingType = false;
        if (missingTypeNames != null) {
            for (int i = 0, max = missingTypeNames.length; i < max; ++i) {
                if (CharOperation.equals(compoundName, missingTypeNames[i])) {
                    wasMissingType = true;
                    break;
                }
            }
        }
        ReferenceBinding binding = this.getTypeFromCompoundName(compoundName, isParameterized, wasMissingType);
        if (walker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) {
            binding = (ReferenceBinding)this.annotateType(binding, walker, missingTypeNames);
        }
        return binding;
    }
    
    ReferenceBinding getTypeFromConstantPoolName(final char[] signature, final int start, final int end, final boolean isParameterized, final char[][][] missingTypeNames) {
        return this.getTypeFromConstantPoolName(signature, start, end, isParameterized, missingTypeNames, ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER);
    }
    
    TypeBinding getTypeFromSignature(final char[] signature, int start, int end, final boolean isParameterized, final TypeBinding enclosingType, final char[][][] missingTypeNames, ITypeAnnotationWalker walker) {
        int dimension;
        for (dimension = 0; signature[start] == '['; ++start, ++dimension) {}
        AnnotationBinding[][] annotationsOnDimensions = null;
        if (dimension > 0 && walker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) {
            for (int i = 0; i < dimension; ++i) {
                final AnnotationBinding[] annotations = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(0), this, missingTypeNames);
                if (annotations != Binding.NO_ANNOTATIONS) {
                    if (annotationsOnDimensions == null) {
                        annotationsOnDimensions = new AnnotationBinding[dimension][];
                    }
                    annotationsOnDimensions[i] = annotations;
                }
                walker = walker.toNextArrayDimension();
            }
        }
        if (end == -1) {
            end = signature.length - 1;
        }
        TypeBinding binding = null;
        if (start == end) {
            switch (signature[start]) {
                case 'I': {
                    binding = TypeBinding.INT;
                    break;
                }
                case 'Z': {
                    binding = TypeBinding.BOOLEAN;
                    break;
                }
                case 'V': {
                    binding = TypeBinding.VOID;
                    break;
                }
                case 'C': {
                    binding = TypeBinding.CHAR;
                    break;
                }
                case 'D': {
                    binding = TypeBinding.DOUBLE;
                    break;
                }
                case 'B': {
                    binding = TypeBinding.BYTE;
                    break;
                }
                case 'F': {
                    binding = TypeBinding.FLOAT;
                    break;
                }
                case 'J': {
                    binding = TypeBinding.LONG;
                    break;
                }
                case 'S': {
                    binding = TypeBinding.SHORT;
                    break;
                }
                default: {
                    this.problemReporter.corruptedSignature(enclosingType, signature, start);
                    break;
                }
            }
        }
        else {
            binding = this.getTypeFromConstantPoolName(signature, start + 1, end, isParameterized, missingTypeNames);
        }
        if (!isParameterized) {
            if (walker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) {
                binding = this.annotateType(binding, walker, missingTypeNames);
            }
            if (dimension != 0) {
                binding = this.typeSystem.getArrayType(binding, dimension, AnnotatableTypeSystem.flattenedAnnotations(annotationsOnDimensions));
            }
            return binding;
        }
        if (dimension != 0) {
            throw new IllegalStateException();
        }
        return binding;
    }
    
    private TypeBinding annotateType(TypeBinding binding, ITypeAnnotationWalker walker, final char[][][] missingTypeNames) {
        int depth = binding.depth() + 1;
        if (depth > 1) {
            if (binding.isUnresolvedType()) {
                binding = ((UnresolvedReferenceBinding)binding).resolve(this, true);
            }
            TypeBinding currentBinding = binding;
            depth = 0;
            while (currentBinding != null) {
                ++depth;
                if (currentBinding.isStatic()) {
                    break;
                }
                currentBinding = currentBinding.enclosingType();
            }
        }
        AnnotationBinding[][] annotations = null;
        for (int i = 0; i < depth; ++i) {
            final AnnotationBinding[] annots = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(binding.id), this, missingTypeNames);
            if (annots != null && annots.length > 0) {
                if (annotations == null) {
                    annotations = new AnnotationBinding[depth][];
                }
                annotations[i] = annots;
            }
            walker = walker.toNextNestedType();
        }
        if (annotations != null) {
            binding = this.createAnnotatedType(binding, annotations);
        }
        return binding;
    }
    
    boolean qualifiedNameMatchesSignature(final char[][] name, final char[] signature) {
        int s = 1;
        for (int i = 0; i < name.length; ++i) {
            final char[] n = name[i];
            for (int j = 0; j < n.length; ++j) {
                if (n[j] != signature[s++]) {
                    return false;
                }
            }
            if (signature[s] == ';' && i == name.length - 1) {
                return true;
            }
            if (signature[s++] != '/') {
                return false;
            }
        }
        return false;
    }
    
    public TypeBinding getTypeFromTypeSignature(final SignatureWrapper wrapper, final TypeVariableBinding[] staticVariables, ReferenceBinding enclosingType, final char[][][] missingTypeNames, ITypeAnnotationWalker walker) {
        int dimension = 0;
        while (wrapper.signature[wrapper.start] == '[') {
            ++wrapper.start;
            ++dimension;
        }
        AnnotationBinding[][] annotationsOnDimensions = null;
        if (dimension > 0 && walker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) {
            for (int i = 0; i < dimension; ++i) {
                final AnnotationBinding[] annotations = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(0), this, missingTypeNames);
                if (annotations != Binding.NO_ANNOTATIONS) {
                    if (annotationsOnDimensions == null) {
                        annotationsOnDimensions = new AnnotationBinding[dimension][];
                    }
                    annotationsOnDimensions[i] = annotations;
                }
                walker = walker.toNextArrayDimension();
            }
        }
        if (wrapper.signature[wrapper.start] == 'T') {
            final int varStart = wrapper.start + 1;
            final int varEnd = wrapper.computeEnd();
            int j = staticVariables.length;
            while (--j >= 0) {
                if (CharOperation.equals(staticVariables[j].sourceName, wrapper.signature, varStart, varEnd)) {
                    return this.getTypeFromTypeVariable(staticVariables[j], dimension, annotationsOnDimensions, walker, missingTypeNames);
                }
            }
            final ReferenceBinding initialType = enclosingType;
            do {
                TypeVariableBinding[] enclosingTypeVariables;
                if (enclosingType instanceof BinaryTypeBinding) {
                    enclosingTypeVariables = ((BinaryTypeBinding)enclosingType).typeVariables;
                }
                else {
                    enclosingTypeVariables = enclosingType.typeVariables();
                }
                int k = enclosingTypeVariables.length;
                while (--k >= 0) {
                    if (CharOperation.equals(enclosingTypeVariables[k].sourceName, wrapper.signature, varStart, varEnd)) {
                        return this.getTypeFromTypeVariable(enclosingTypeVariables[k], dimension, annotationsOnDimensions, walker, missingTypeNames);
                    }
                }
            } while ((enclosingType = enclosingType.enclosingType()) != null);
            this.problemReporter.undefinedTypeVariableSignature(CharOperation.subarray(wrapper.signature, varStart, varEnd), initialType);
            return null;
        }
        final boolean isParameterized;
        final TypeBinding type = this.getTypeFromSignature(wrapper.signature, wrapper.start, wrapper.computeEnd(), isParameterized = (wrapper.end == wrapper.bracket), enclosingType, missingTypeNames, walker);
        if (!isParameterized) {
            return (dimension == 0) ? type : this.createArrayType(type, dimension, AnnotatableTypeSystem.flattenedAnnotations(annotationsOnDimensions));
        }
        ReferenceBinding actualType = (ReferenceBinding)type;
        if (actualType instanceof UnresolvedReferenceBinding && CharOperation.indexOf('$', actualType.compoundName[actualType.compoundName.length - 1]) > 0) {
            actualType = (ReferenceBinding)BinaryTypeBinding.resolveType(actualType, this, false);
        }
        ReferenceBinding actualEnclosing = actualType.enclosingType();
        if (actualEnclosing != null) {
            actualEnclosing = (ReferenceBinding)this.convertToRawType(actualEnclosing, false);
        }
        AnnotationBinding[] annotations2 = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(actualType.id), this, missingTypeNames);
        TypeBinding[] typeArguments = this.getTypeArgumentsFromSignature(wrapper, staticVariables, enclosingType, actualType, missingTypeNames, walker);
        ParameterizedTypeBinding parameterizedType = this.createParameterizedType(actualType, typeArguments, actualEnclosing, annotations2);
        while (wrapper.signature[wrapper.start] == '.') {
            ++wrapper.start;
            final int memberStart = wrapper.start;
            final char[] memberName = wrapper.nextWord();
            BinaryTypeBinding.resolveType(parameterizedType, this, false);
            final ReferenceBinding memberType = parameterizedType.genericType().getMemberType(memberName);
            if (memberType == null) {
                this.problemReporter.corruptedSignature(parameterizedType, wrapper.signature, memberStart);
            }
            walker = walker.toNextNestedType();
            annotations2 = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(memberType.id), this, missingTypeNames);
            if (wrapper.signature[wrapper.start] == '<') {
                ++wrapper.start;
                typeArguments = this.getTypeArgumentsFromSignature(wrapper, staticVariables, enclosingType, memberType, missingTypeNames, walker);
            }
            else {
                typeArguments = null;
            }
            parameterizedType = this.createParameterizedType(memberType, typeArguments, parameterizedType, annotations2);
        }
        ++wrapper.start;
        return (dimension == 0) ? parameterizedType : this.createArrayType(parameterizedType, dimension, AnnotatableTypeSystem.flattenedAnnotations(annotationsOnDimensions));
    }
    
    private TypeBinding getTypeFromTypeVariable(TypeVariableBinding typeVariableBinding, final int dimension, final AnnotationBinding[][] annotationsOnDimensions, final ITypeAnnotationWalker walker, final char[][][] missingTypeNames) {
        final AnnotationBinding[] annotations = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(-1), this, missingTypeNames);
        if (annotations != null && annotations != Binding.NO_ANNOTATIONS) {
            typeVariableBinding = (TypeVariableBinding)this.createAnnotatedType(typeVariableBinding, new AnnotationBinding[][] { annotations });
        }
        if (dimension == 0) {
            return typeVariableBinding;
        }
        return this.typeSystem.getArrayType(typeVariableBinding, dimension, AnnotatableTypeSystem.flattenedAnnotations(annotationsOnDimensions));
    }
    
    TypeBinding getTypeFromVariantTypeSignature(final SignatureWrapper wrapper, final TypeVariableBinding[] staticVariables, final ReferenceBinding enclosingType, final ReferenceBinding genericType, final int rank, final char[][][] missingTypeNames, final ITypeAnnotationWalker walker) {
        switch (wrapper.signature[wrapper.start]) {
            case '-': {
                ++wrapper.start;
                final TypeBinding bound = this.getTypeFromTypeSignature(wrapper, staticVariables, enclosingType, missingTypeNames, walker.toWildcardBound());
                final AnnotationBinding[] annotations = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(-1), this, missingTypeNames);
                return this.typeSystem.getWildcard(genericType, rank, bound, null, 2, annotations);
            }
            case '+': {
                ++wrapper.start;
                final TypeBinding bound = this.getTypeFromTypeSignature(wrapper, staticVariables, enclosingType, missingTypeNames, walker.toWildcardBound());
                final AnnotationBinding[] annotations = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(-1), this, missingTypeNames);
                return this.typeSystem.getWildcard(genericType, rank, bound, null, 1, annotations);
            }
            case '*': {
                ++wrapper.start;
                final AnnotationBinding[] annotations = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(-1), this, missingTypeNames);
                return this.typeSystem.getWildcard(genericType, rank, null, null, 0, annotations);
            }
            default: {
                return this.getTypeFromTypeSignature(wrapper, staticVariables, enclosingType, missingTypeNames, walker);
            }
        }
    }
    
    boolean isMissingType(final char[] typeName) {
        int i = (this.missingTypes == null) ? 0 : this.missingTypes.size();
        while (--i >= 0) {
            final MissingTypeBinding missingType = this.missingTypes.get(i);
            if (CharOperation.equals(missingType.sourceName, typeName)) {
                return true;
            }
        }
        return false;
    }
    
    boolean isPackage(final char[][] compoundName, final char[] name) {
        if (compoundName == null || compoundName.length == 0) {
            return this.nameEnvironment.isPackage(null, name);
        }
        return this.nameEnvironment.isPackage(compoundName, name);
    }
    
    public MethodVerifier methodVerifier() {
        if (this.verifier == null) {
            this.verifier = this.newMethodVerifier();
        }
        return this.verifier;
    }
    
    public MethodVerifier newMethodVerifier() {
        return new MethodVerifier15(this);
    }
    
    public void releaseClassFiles(final ClassFile[] classFiles) {
        for (int i = 0, fileCount = classFiles.length; i < fileCount; ++i) {
            this.classFilePool.release(classFiles[i]);
        }
    }
    
    public void reset() {
        this.defaultPackage = new PackageBinding(this);
        this.defaultImports = null;
        this.knownPackages = new HashtableOfPackage();
        this.accessRestrictions = new HashMap(3);
        this.verifier = null;
        this.uniqueParameterizedGenericMethodBindings = new SimpleLookupTable(3);
        this.uniquePolymorphicMethodBindings = new SimpleLookupTable(3);
        this.uniqueGetClassMethodBinding = null;
        this.missingTypes = null;
        this.typesBeingConnected = new HashSet<SourceTypeBinding>();
        int i = this.units.length;
        while (--i >= 0) {
            this.units[i] = null;
        }
        this.lastUnitIndex = -1;
        this.lastCompletedUnitIndex = -1;
        this.unitBeingCompleted = null;
        this.classFilePool.reset();
        this.typeSystem.reset();
    }
    
    public void setAccessRestriction(final ReferenceBinding type, final AccessRestriction accessRestriction) {
        if (accessRestriction == null) {
            return;
        }
        type.modifiers |= 0x40000;
        this.accessRestrictions.put(type, accessRestriction);
    }
    
    void updateCaches(final UnresolvedReferenceBinding unresolvedType, final ReferenceBinding resolvedType) {
        this.typeSystem.updateCaches(unresolvedType, resolvedType);
    }
    
    public void addResolutionListener(final IQualifiedTypeResolutionListener resolutionListener) {
        final int length = this.resolutionListeners.length;
        for (int i = 0; i < length; ++i) {
            if (this.resolutionListeners[i].equals(resolutionListener)) {
                return;
            }
        }
        System.arraycopy(this.resolutionListeners, 0, this.resolutionListeners = new IQualifiedTypeResolutionListener[length + 1], 0, length);
        this.resolutionListeners[length] = resolutionListener;
    }
    
    public TypeBinding getUnannotatedType(final TypeBinding typeBinding) {
        return this.typeSystem.getUnannotatedType(typeBinding);
    }
    
    public TypeBinding[] getAnnotatedTypes(final TypeBinding type) {
        return this.typeSystem.getAnnotatedTypes(type);
    }
    
    public AnnotationBinding[] filterNullTypeAnnotations(final AnnotationBinding[] typeAnnotations) {
        if (typeAnnotations.length == 0) {
            return typeAnnotations;
        }
        AnnotationBinding[] filtered = new AnnotationBinding[typeAnnotations.length];
        int count = 0;
        for (int i = 0; i < typeAnnotations.length; ++i) {
            final AnnotationBinding typeAnnotation = typeAnnotations[i];
            if (typeAnnotation == null) {
                ++count;
            }
            else if (!typeAnnotation.type.hasNullBit(96)) {
                filtered[count++] = typeAnnotation;
            }
        }
        if (count == 0) {
            return Binding.NO_ANNOTATIONS;
        }
        if (count == typeAnnotations.length) {
            return typeAnnotations;
        }
        System.arraycopy(filtered, 0, filtered = new AnnotationBinding[count], 0, count);
        return filtered;
    }
    
    public boolean containsNullTypeAnnotation(final IBinaryAnnotation[] typeAnnotations) {
        if (typeAnnotations.length == 0) {
            return false;
        }
        for (int i = 0; i < typeAnnotations.length; ++i) {
            final IBinaryAnnotation typeAnnotation = typeAnnotations[i];
            final char[] typeName = typeAnnotation.getTypeName();
            if (typeName != null && typeName.length >= 3) {
                if (typeName[0] == 'L') {
                    final char[][] name = CharOperation.splitOn('/', typeName, 1, typeName.length - 1);
                    if (this.getNullAnnotationBit(name) != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean containsNullTypeAnnotation(final AnnotationBinding[] typeAnnotations) {
        if (typeAnnotations.length == 0) {
            return false;
        }
        for (int i = 0; i < typeAnnotations.length; ++i) {
            final AnnotationBinding typeAnnotation = typeAnnotations[i];
            if (typeAnnotation.type.hasNullBit(96)) {
                return true;
            }
        }
        return false;
    }
}
