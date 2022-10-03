package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.util.HashtableOfPackage;
import org.eclipse.jdt.internal.compiler.util.HashtableOfType;

public class PackageBinding extends Binding implements TypeConstants
{
    public long tagBits;
    public char[][] compoundName;
    PackageBinding parent;
    public LookupEnvironment environment;
    HashtableOfType knownTypes;
    HashtableOfPackage knownPackages;
    protected int defaultNullness;
    
    protected PackageBinding() {
        this.tagBits = 0L;
        this.defaultNullness = 0;
    }
    
    public PackageBinding(final char[] topLevelPackageName, final LookupEnvironment environment) {
        this(new char[][] { topLevelPackageName }, null, environment);
    }
    
    public PackageBinding(final char[][] compoundName, final PackageBinding parent, final LookupEnvironment environment) {
        this.tagBits = 0L;
        this.defaultNullness = 0;
        this.compoundName = compoundName;
        this.parent = parent;
        this.environment = environment;
        this.knownTypes = null;
        this.knownPackages = new HashtableOfPackage(3);
        if (compoundName != CharOperation.NO_CHAR_CHAR) {
            this.checkIfNullAnnotationPackage();
        }
    }
    
    public PackageBinding(final LookupEnvironment environment) {
        this(CharOperation.NO_CHAR_CHAR, null, environment);
    }
    
    private void addNotFoundPackage(final char[] simpleName) {
        this.knownPackages.put(simpleName, LookupEnvironment.TheNotFoundPackage);
    }
    
    private void addNotFoundType(final char[] simpleName) {
        if (this.knownTypes == null) {
            this.knownTypes = new HashtableOfType(25);
        }
        this.knownTypes.put(simpleName, LookupEnvironment.TheNotFoundType);
    }
    
    void addPackage(final PackageBinding element) {
        if ((element.tagBits & 0x80L) == 0x0L) {
            this.clearMissingTagBit();
        }
        this.knownPackages.put(element.compoundName[element.compoundName.length - 1], element);
    }
    
    void addType(final ReferenceBinding element) {
        if ((element.tagBits & 0x80L) == 0x0L) {
            this.clearMissingTagBit();
        }
        if (this.knownTypes == null) {
            this.knownTypes = new HashtableOfType(25);
        }
        final char[] name = element.compoundName[element.compoundName.length - 1];
        final ReferenceBinding priorType = this.knownTypes.getput(name, element);
        if (priorType != null && priorType.isUnresolvedType() && !element.isUnresolvedType()) {
            ((UnresolvedReferenceBinding)priorType).setResolvedType(element, this.environment);
        }
        if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled && (element.isAnnotationType() || element instanceof UnresolvedReferenceBinding)) {
            this.checkIfNullAnnotationType(element);
        }
    }
    
    void clearMissingTagBit() {
        PackageBinding current = this;
        do {
            final PackageBinding packageBinding = current;
            packageBinding.tagBits &= 0xFFFFFFFFFFFFFF7FL;
        } while ((current = current.parent) != null);
    }
    
    @Override
    public char[] computeUniqueKey(final boolean isLeaf) {
        return CharOperation.concatWith(this.compoundName, '/');
    }
    
    private PackageBinding findPackage(final char[] name) {
        if (!this.environment.isPackage(this.compoundName, name)) {
            return null;
        }
        final char[][] subPkgCompoundName = CharOperation.arrayConcat(this.compoundName, name);
        final PackageBinding subPackageBinding = new PackageBinding(subPkgCompoundName, this, this.environment);
        this.addPackage(subPackageBinding);
        return subPackageBinding;
    }
    
    PackageBinding getPackage(final char[] name) {
        PackageBinding binding = this.getPackage0(name);
        if (binding != null) {
            if (binding == LookupEnvironment.TheNotFoundPackage) {
                return null;
            }
            return binding;
        }
        else {
            if ((binding = this.findPackage(name)) != null) {
                return binding;
            }
            this.addNotFoundPackage(name);
            return null;
        }
    }
    
    PackageBinding getPackage0(final char[] name) {
        return this.knownPackages.get(name);
    }
    
    ReferenceBinding getType(final char[] name) {
        ReferenceBinding referenceBinding = this.getType0(name);
        if (referenceBinding == null && (referenceBinding = this.environment.askForType(this, name)) == null) {
            this.addNotFoundType(name);
            return null;
        }
        if (referenceBinding == LookupEnvironment.TheNotFoundType) {
            return null;
        }
        referenceBinding = (ReferenceBinding)BinaryTypeBinding.resolveType(referenceBinding, this.environment, false);
        if (referenceBinding.isNestedType()) {
            return new ProblemReferenceBinding(new char[][] { name }, referenceBinding, 4);
        }
        return referenceBinding;
    }
    
    ReferenceBinding getType0(final char[] name) {
        if (this.knownTypes == null) {
            return null;
        }
        return this.knownTypes.get(name);
    }
    
    public Binding getTypeOrPackage(final char[] name) {
        ReferenceBinding referenceBinding = this.getType0(name);
        if (referenceBinding != null && referenceBinding != LookupEnvironment.TheNotFoundType) {
            referenceBinding = (ReferenceBinding)BinaryTypeBinding.resolveType(referenceBinding, this.environment, false);
            if (referenceBinding.isNestedType()) {
                return new ProblemReferenceBinding(new char[][] { name }, referenceBinding, 4);
            }
            if ((referenceBinding.tagBits & 0x80L) == 0x0L) {
                return referenceBinding;
            }
        }
        PackageBinding packageBinding = this.getPackage0(name);
        if (packageBinding != null && packageBinding != LookupEnvironment.TheNotFoundPackage) {
            return packageBinding;
        }
        if (referenceBinding == null) {
            if ((referenceBinding = this.environment.askForType(this, name)) != null) {
                if (referenceBinding.isNestedType()) {
                    return new ProblemReferenceBinding(new char[][] { name }, referenceBinding, 4);
                }
                return referenceBinding;
            }
            else {
                this.addNotFoundType(name);
            }
        }
        if (packageBinding == null) {
            if ((packageBinding = this.findPackage(name)) != null) {
                return packageBinding;
            }
            if (referenceBinding != null && referenceBinding != LookupEnvironment.TheNotFoundType) {
                return referenceBinding;
            }
            this.addNotFoundPackage(name);
        }
        return null;
    }
    
    public final boolean isViewedAsDeprecated() {
        if ((this.tagBits & 0x400000000L) == 0x0L) {
            this.tagBits |= 0x400000000L;
            if (this.compoundName != CharOperation.NO_CHAR_CHAR) {
                final ReferenceBinding packageInfo = this.getType(TypeConstants.PACKAGE_INFO_NAME);
                if (packageInfo != null) {
                    packageInfo.initializeDeprecatedAnnotationTagBits();
                    this.tagBits |= (packageInfo.tagBits & 0x17FFFFF800000000L);
                }
            }
        }
        return (this.tagBits & 0x400000000000L) != 0x0L;
    }
    
    @Override
    public final int kind() {
        return 16;
    }
    
    @Override
    public int problemId() {
        if ((this.tagBits & 0x80L) != 0x0L) {
            return 1;
        }
        return 0;
    }
    
    void checkIfNullAnnotationPackage() {
        final LookupEnvironment env = this.environment;
        if (env.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
            if (this.isPackageOfQualifiedTypeName(this.compoundName, env.getNullableAnnotationName())) {
                env.nullableAnnotationPackage = this;
            }
            if (this.isPackageOfQualifiedTypeName(this.compoundName, env.getNonNullAnnotationName())) {
                env.nonnullAnnotationPackage = this;
            }
            if (this.isPackageOfQualifiedTypeName(this.compoundName, env.getNonNullByDefaultAnnotationName())) {
                env.nonnullByDefaultAnnotationPackage = this;
            }
        }
    }
    
    private boolean isPackageOfQualifiedTypeName(final char[][] packageName, final char[][] typeName) {
        final int length;
        if (typeName == null || (length = packageName.length) != typeName.length - 1) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (!CharOperation.equals(packageName[i], typeName[i])) {
                return false;
            }
        }
        return true;
    }
    
    void checkIfNullAnnotationType(final ReferenceBinding type) {
        if (this.environment.nullableAnnotationPackage == this && CharOperation.equals(type.compoundName, this.environment.getNullableAnnotationName())) {
            type.typeBits |= 0x40;
            if (!(type instanceof UnresolvedReferenceBinding)) {
                this.environment.nullableAnnotationPackage = null;
            }
        }
        else if (this.environment.nonnullAnnotationPackage == this && CharOperation.equals(type.compoundName, this.environment.getNonNullAnnotationName())) {
            type.typeBits |= 0x20;
            if (!(type instanceof UnresolvedReferenceBinding)) {
                this.environment.nonnullAnnotationPackage = null;
            }
        }
        else if (this.environment.nonnullByDefaultAnnotationPackage == this && CharOperation.equals(type.compoundName, this.environment.getNonNullByDefaultAnnotationName())) {
            type.typeBits |= 0x80;
            if (!(type instanceof UnresolvedReferenceBinding)) {
                this.environment.nonnullByDefaultAnnotationPackage = null;
            }
        }
        else {
            type.typeBits |= this.environment.getNullAnnotationBit(type.compoundName);
        }
    }
    
    @Override
    public char[] readableName() {
        return CharOperation.concatWith(this.compoundName, '.');
    }
    
    @Override
    public String toString() {
        String str;
        if (this.compoundName == CharOperation.NO_CHAR_CHAR) {
            str = "The Default Package";
        }
        else {
            str = "package " + ((this.compoundName != null) ? CharOperation.toString(this.compoundName) : "UNNAMED");
        }
        if ((this.tagBits & 0x80L) != 0x0L) {
            str = String.valueOf(str) + "[MISSING]";
        }
        return str;
    }
}
