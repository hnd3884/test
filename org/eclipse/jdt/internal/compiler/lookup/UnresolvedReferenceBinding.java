package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;

public class UnresolvedReferenceBinding extends ReferenceBinding
{
    ReferenceBinding resolvedType;
    TypeBinding[] wrappers;
    UnresolvedReferenceBinding prototype;
    
    UnresolvedReferenceBinding(final char[][] compoundName, final PackageBinding packageBinding) {
        this.compoundName = compoundName;
        this.sourceName = compoundName[compoundName.length - 1];
        this.fPackage = packageBinding;
        this.wrappers = null;
        (this.prototype = this).computeId();
    }
    
    public UnresolvedReferenceBinding(final UnresolvedReferenceBinding prototype) {
        super(prototype);
        this.resolvedType = prototype.resolvedType;
        this.wrappers = null;
        this.prototype = prototype.prototype;
    }
    
    @Override
    public TypeBinding clone(final TypeBinding outerType) {
        if (this.resolvedType != null) {
            return this.resolvedType.clone(outerType);
        }
        final UnresolvedReferenceBinding copy = new UnresolvedReferenceBinding(this);
        this.addWrapper(copy, null);
        return copy;
    }
    
    void addWrapper(final TypeBinding wrapper, final LookupEnvironment environment) {
        if (this.resolvedType != null) {
            wrapper.swapUnresolved(this, this.resolvedType, environment);
            return;
        }
        if (this.wrappers == null) {
            this.wrappers = new TypeBinding[] { wrapper };
        }
        else {
            final int length = this.wrappers.length;
            System.arraycopy(this.wrappers, 0, this.wrappers = new TypeBinding[length + 1], 0, length);
            this.wrappers[length] = wrapper;
        }
    }
    
    @Override
    public boolean isUnresolvedType() {
        return true;
    }
    
    @Override
    public String debugName() {
        return this.toString();
    }
    
    @Override
    public int depth() {
        final int last = this.compoundName.length - 1;
        return CharOperation.occurencesOf('$', this.compoundName[last]);
    }
    
    @Override
    public boolean hasTypeBit(final int bit) {
        return false;
    }
    
    @Override
    public TypeBinding prototype() {
        return this.prototype;
    }
    
    ReferenceBinding resolve(final LookupEnvironment environment, final boolean convertGenericToRawType) {
        if (this != this.prototype) {
            ReferenceBinding targetType = this.prototype.resolve(environment, convertGenericToRawType);
            if (convertGenericToRawType && targetType != null && targetType.isRawType()) {
                targetType = (ReferenceBinding)environment.createAnnotatedType(targetType, this.typeAnnotations);
            }
            else {
                targetType = this.resolvedType;
            }
            return targetType;
        }
        ReferenceBinding targetType = this.resolvedType;
        if (targetType == null) {
            final char[] typeName = this.compoundName[this.compoundName.length - 1];
            targetType = this.fPackage.getType0(typeName);
            if (targetType == this) {
                targetType = environment.askForType(this.compoundName);
            }
            if ((targetType == null || targetType == this) && CharOperation.contains('.', typeName)) {
                targetType = environment.askForType(this.fPackage, CharOperation.replaceOnCopy(typeName, '.', '$'));
            }
            if (targetType == null || targetType == this) {
                if ((this.tagBits & 0x80L) == 0x0L && !environment.mayTolerateMissingType) {
                    environment.problemReporter.isClassPathCorrect(this.compoundName, environment.unitBeingCompleted, environment.missingClassFileLocation);
                }
                targetType = environment.createMissingType(null, this.compoundName);
            }
            if (targetType.id != Integer.MAX_VALUE) {
                this.id = targetType.id;
            }
            this.setResolvedType(targetType, environment);
        }
        if (convertGenericToRawType) {
            targetType = (ReferenceBinding)environment.convertUnresolvedBinaryToRawType(targetType);
        }
        return targetType;
    }
    
    void setResolvedType(final ReferenceBinding targetType, final LookupEnvironment environment) {
        if (this.resolvedType == targetType) {
            return;
        }
        environment.updateCaches(this, this.resolvedType = targetType);
        if (this.wrappers != null) {
            for (int i = 0, l = this.wrappers.length; i < l; ++i) {
                this.wrappers[i].swapUnresolved(this, targetType, environment);
            }
        }
    }
    
    @Override
    public void swapUnresolved(final UnresolvedReferenceBinding unresolvedType, final ReferenceBinding unannotatedType, final LookupEnvironment environment) {
        if (this.resolvedType != null) {
            return;
        }
        final ReferenceBinding annotatedType = (ReferenceBinding)unannotatedType.clone(null);
        (this.resolvedType = annotatedType).setTypeAnnotations(this.getTypeAnnotations(), environment.globalOptions.isAnnotationBasedNullAnalysisEnabled);
        final ReferenceBinding referenceBinding = annotatedType;
        final int id = this.id;
        unannotatedType.id = id;
        referenceBinding.id = id;
        environment.updateCaches(this, annotatedType);
        if (this.wrappers != null) {
            for (int i = 0, l = this.wrappers.length; i < l; ++i) {
                this.wrappers[i].swapUnresolved(this, annotatedType, environment);
            }
        }
    }
    
    @Override
    public String toString() {
        if (this.hasTypeAnnotations()) {
            return String.valueOf(super.annotatedDebugName()) + "(unresolved)";
        }
        return "Unresolved type " + ((this.compoundName != null) ? CharOperation.toString(this.compoundName) : "UNNAMED");
    }
}
