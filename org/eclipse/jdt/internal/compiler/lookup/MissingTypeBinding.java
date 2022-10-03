package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.compiler.CharOperation;

public class MissingTypeBinding extends BinaryTypeBinding
{
    public MissingTypeBinding(final PackageBinding packageBinding, final char[][] compoundName, final LookupEnvironment environment) {
        this.compoundName = compoundName;
        this.computeId();
        this.tagBits |= 0x200C0L;
        this.environment = environment;
        this.fPackage = packageBinding;
        this.fileName = CharOperation.concatWith(compoundName, '/');
        this.sourceName = compoundName[compoundName.length - 1];
        this.modifiers = 1;
        this.superclass = null;
        this.superInterfaces = Binding.NO_SUPERINTERFACES;
        this.typeVariables = Binding.NO_TYPE_VARIABLES;
        this.memberTypes = Binding.NO_MEMBER_TYPES;
        this.fields = Binding.NO_FIELDS;
        this.methods = Binding.NO_METHODS;
    }
    
    @Override
    public TypeBinding clone(final TypeBinding outerType) {
        return this;
    }
    
    @Override
    public List<TypeBinding> collectMissingTypes(List<TypeBinding> missingTypes) {
        if (missingTypes == null) {
            missingTypes = new ArrayList<TypeBinding>(5);
        }
        else if (missingTypes.contains(this)) {
            return missingTypes;
        }
        missingTypes.add(this);
        return missingTypes;
    }
    
    @Override
    public int problemId() {
        return 1;
    }
    
    void setMissingSuperclass(final ReferenceBinding missingSuperclass) {
        this.superclass = missingSuperclass;
    }
    
    @Override
    public void setTypeAnnotations(final AnnotationBinding[] annotations, final boolean evalNullAnnotations) {
    }
    
    @Override
    public String toString() {
        return "[MISSING:" + new String(CharOperation.concatWith(this.compoundName, '.')) + "]";
    }
}
