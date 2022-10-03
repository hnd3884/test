package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;

public abstract class NestedTypeBinding extends SourceTypeBinding
{
    public SourceTypeBinding enclosingType;
    public SyntheticArgumentBinding[] enclosingInstances;
    private ReferenceBinding[] enclosingTypes;
    public SyntheticArgumentBinding[] outerLocalVariables;
    private int outerLocalVariablesSlotSize;
    
    public NestedTypeBinding(final char[][] typeName, final ClassScope scope, final SourceTypeBinding enclosingType) {
        super(typeName, enclosingType.fPackage, scope);
        this.enclosingTypes = Binding.UNINITIALIZED_REFERENCE_TYPES;
        this.outerLocalVariablesSlotSize = -1;
        this.tagBits |= 0x804L;
        this.enclosingType = enclosingType;
    }
    
    public NestedTypeBinding(final NestedTypeBinding prototype) {
        super(prototype);
        this.enclosingTypes = Binding.UNINITIALIZED_REFERENCE_TYPES;
        this.outerLocalVariablesSlotSize = -1;
        this.enclosingType = prototype.enclosingType;
        this.enclosingInstances = prototype.enclosingInstances;
        this.enclosingTypes = prototype.enclosingTypes;
        this.outerLocalVariables = prototype.outerLocalVariables;
        this.outerLocalVariablesSlotSize = prototype.outerLocalVariablesSlotSize;
    }
    
    public SyntheticArgumentBinding addSyntheticArgument(final LocalVariableBinding actualOuterLocalVariable) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        SyntheticArgumentBinding synthLocal = null;
        if (this.outerLocalVariables == null) {
            synthLocal = new SyntheticArgumentBinding(actualOuterLocalVariable);
            this.outerLocalVariables = new SyntheticArgumentBinding[] { synthLocal };
        }
        else {
            int newArgIndex;
            int i;
            final int size = i = (newArgIndex = this.outerLocalVariables.length);
            while (--i >= 0) {
                if (this.outerLocalVariables[i].actualOuterLocalVariable == actualOuterLocalVariable) {
                    return this.outerLocalVariables[i];
                }
                if (this.outerLocalVariables[i].id <= actualOuterLocalVariable.id) {
                    continue;
                }
                newArgIndex = i;
            }
            final SyntheticArgumentBinding[] synthLocals = new SyntheticArgumentBinding[size + 1];
            System.arraycopy(this.outerLocalVariables, 0, synthLocals, 0, newArgIndex);
            synthLocal = (synthLocals[newArgIndex] = new SyntheticArgumentBinding(actualOuterLocalVariable));
            System.arraycopy(this.outerLocalVariables, newArgIndex, synthLocals, newArgIndex + 1, size - newArgIndex);
            this.outerLocalVariables = synthLocals;
        }
        if (this.scope.referenceCompilationUnit().isPropagatingInnerClassEmulation) {
            this.updateInnerEmulationDependents();
        }
        return synthLocal;
    }
    
    public SyntheticArgumentBinding addSyntheticArgument(final ReferenceBinding targetEnclosingType) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        SyntheticArgumentBinding synthLocal = null;
        if (this.enclosingInstances == null) {
            synthLocal = new SyntheticArgumentBinding(targetEnclosingType);
            this.enclosingInstances = new SyntheticArgumentBinding[] { synthLocal };
        }
        else {
            int newArgIndex;
            final int size = newArgIndex = this.enclosingInstances.length;
            if (TypeBinding.equalsEquals(this.enclosingType(), targetEnclosingType)) {
                newArgIndex = 0;
            }
            final SyntheticArgumentBinding[] newInstances = new SyntheticArgumentBinding[size + 1];
            System.arraycopy(this.enclosingInstances, 0, newInstances, (newArgIndex == 0) ? 1 : 0, size);
            synthLocal = (newInstances[newArgIndex] = new SyntheticArgumentBinding(targetEnclosingType));
            this.enclosingInstances = newInstances;
        }
        if (this.scope.referenceCompilationUnit().isPropagatingInnerClassEmulation) {
            this.updateInnerEmulationDependents();
        }
        return synthLocal;
    }
    
    public SyntheticArgumentBinding addSyntheticArgumentAndField(final LocalVariableBinding actualOuterLocalVariable) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        final SyntheticArgumentBinding synthLocal = this.addSyntheticArgument(actualOuterLocalVariable);
        if (synthLocal == null) {
            return null;
        }
        if (synthLocal.matchingField == null) {
            synthLocal.matchingField = this.addSyntheticFieldForInnerclass(actualOuterLocalVariable);
        }
        return synthLocal;
    }
    
    public SyntheticArgumentBinding addSyntheticArgumentAndField(final ReferenceBinding targetEnclosingType) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        final SyntheticArgumentBinding synthLocal = this.addSyntheticArgument(targetEnclosingType);
        if (synthLocal == null) {
            return null;
        }
        if (synthLocal.matchingField == null) {
            synthLocal.matchingField = this.addSyntheticFieldForInnerclass(targetEnclosingType);
        }
        return synthLocal;
    }
    
    @Override
    protected void checkRedundantNullnessDefaultRecurse(final ASTNode location, final Annotation[] annotations, final long nullBits, final boolean useNullTypeAnnotations) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        ReferenceBinding currentType = this.enclosingType;
        while (((SourceTypeBinding)currentType).checkRedundantNullnessDefaultOne(location, annotations, nullBits, useNullTypeAnnotations)) {
            currentType = currentType.enclosingType();
            if (!(currentType instanceof SourceTypeBinding)) {
                super.checkRedundantNullnessDefaultRecurse(location, annotations, nullBits, useNullTypeAnnotations);
            }
        }
    }
    
    @Override
    public ReferenceBinding enclosingType() {
        return this.enclosingType;
    }
    
    @Override
    public int getEnclosingInstancesSlotSize() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        return (this.enclosingInstances == null) ? 0 : this.enclosingInstances.length;
    }
    
    @Override
    public int getOuterLocalVariablesSlotSize() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.outerLocalVariablesSlotSize < 0) {
            this.outerLocalVariablesSlotSize = 0;
            for (int outerLocalsCount = (this.outerLocalVariables == null) ? 0 : this.outerLocalVariables.length, i = 0; i < outerLocalsCount; ++i) {
                final SyntheticArgumentBinding argument = this.outerLocalVariables[i];
                switch (argument.type.id) {
                    case 7:
                    case 8: {
                        this.outerLocalVariablesSlotSize += 2;
                        break;
                    }
                    default: {
                        ++this.outerLocalVariablesSlotSize;
                        break;
                    }
                }
            }
        }
        return this.outerLocalVariablesSlotSize;
    }
    
    public SyntheticArgumentBinding getSyntheticArgument(final LocalVariableBinding actualOuterLocalVariable) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.outerLocalVariables == null) {
            return null;
        }
        int i = this.outerLocalVariables.length;
        while (--i >= 0) {
            if (this.outerLocalVariables[i].actualOuterLocalVariable == actualOuterLocalVariable) {
                return this.outerLocalVariables[i];
            }
        }
        return null;
    }
    
    public SyntheticArgumentBinding getSyntheticArgument(final ReferenceBinding targetEnclosingType, final boolean onlyExactMatch, final boolean scopeIsConstructorCall) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.enclosingInstances == null) {
            return null;
        }
        if (scopeIsConstructorCall && this.enclosingInstances.length > 0 && TypeBinding.equalsEquals(this.enclosingInstances[0].type, targetEnclosingType) && this.enclosingInstances[0].actualOuterLocalVariable == null) {
            return this.enclosingInstances[0];
        }
        int i = this.enclosingInstances.length;
        while (--i >= 0) {
            if (TypeBinding.equalsEquals(this.enclosingInstances[i].type, targetEnclosingType) && this.enclosingInstances[i].actualOuterLocalVariable == null) {
                return this.enclosingInstances[i];
            }
        }
        if (!onlyExactMatch) {
            i = this.enclosingInstances.length;
            while (--i >= 0) {
                if (this.enclosingInstances[i].actualOuterLocalVariable == null && this.enclosingInstances[i].type.findSuperTypeOriginatingFrom(targetEnclosingType) != null) {
                    return this.enclosingInstances[i];
                }
            }
        }
        return null;
    }
    
    public SyntheticArgumentBinding[] syntheticEnclosingInstances() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        return this.enclosingInstances;
    }
    
    @Override
    public ReferenceBinding[] syntheticEnclosingInstanceTypes() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.enclosingTypes == NestedTypeBinding.UNINITIALIZED_REFERENCE_TYPES) {
            if (this.enclosingInstances == null) {
                this.enclosingTypes = null;
            }
            else {
                final int length = this.enclosingInstances.length;
                this.enclosingTypes = new ReferenceBinding[length];
                for (int i = 0; i < length; ++i) {
                    this.enclosingTypes[i] = (ReferenceBinding)this.enclosingInstances[i].type;
                }
            }
        }
        return this.enclosingTypes;
    }
    
    @Override
    public SyntheticArgumentBinding[] syntheticOuterLocalVariables() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        return this.outerLocalVariables;
    }
    
    public void updateInnerEmulationDependents() {
    }
}
