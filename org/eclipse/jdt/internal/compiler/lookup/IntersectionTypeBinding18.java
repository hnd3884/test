package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Set;
import org.eclipse.jdt.core.compiler.InvalidInputException;

public class IntersectionTypeBinding18 extends ReferenceBinding
{
    public ReferenceBinding[] intersectingTypes;
    private ReferenceBinding javaLangObject;
    int length;
    
    public IntersectionTypeBinding18(final ReferenceBinding[] intersectingTypes, final LookupEnvironment environment) {
        this.intersectingTypes = intersectingTypes;
        this.length = intersectingTypes.length;
        if (!intersectingTypes[0].isClass()) {
            this.javaLangObject = environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null);
            this.modifiers |= 0x200;
        }
    }
    
    private IntersectionTypeBinding18(final IntersectionTypeBinding18 prototype) {
        this.intersectingTypes = prototype.intersectingTypes;
        this.length = prototype.length;
        if (!this.intersectingTypes[0].isClass()) {
            this.javaLangObject = prototype.javaLangObject;
            this.modifiers |= 0x200;
        }
    }
    
    @Override
    public TypeBinding clone(final TypeBinding enclosingType) {
        return new IntersectionTypeBinding18(this);
    }
    
    @Override
    protected MethodBinding[] getInterfaceAbstractContracts(final Scope scope, final boolean replaceWildcards) throws InvalidInputException {
        final int typesLength = this.intersectingTypes.length;
        final MethodBinding[][] methods = new MethodBinding[typesLength][];
        int contractsLength = 0;
        for (int i = 0; i < typesLength; ++i) {
            methods[i] = this.intersectingTypes[i].getInterfaceAbstractContracts(scope, replaceWildcards);
            contractsLength += methods[i].length;
        }
        final MethodBinding[] contracts = new MethodBinding[contractsLength];
        int idx = 0;
        for (int j = 0; j < typesLength; ++j) {
            final int len = methods[j].length;
            System.arraycopy(methods[j], 0, contracts, idx, len);
            idx += len;
        }
        return contracts;
    }
    
    @Override
    public boolean hasTypeBit(final int bit) {
        for (int i = 0; i < this.length; ++i) {
            if (this.intersectingTypes[i].hasTypeBit(bit)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean canBeInstantiated() {
        return false;
    }
    
    @Override
    public boolean canBeSeenBy(final PackageBinding invocationPackage) {
        for (int i = 0; i < this.length; ++i) {
            if (!this.intersectingTypes[i].canBeSeenBy(invocationPackage)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean canBeSeenBy(final Scope scope) {
        for (int i = 0; i < this.length; ++i) {
            if (!this.intersectingTypes[i].canBeSeenBy(scope)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean canBeSeenBy(final ReferenceBinding receiverType, final ReferenceBinding invocationType) {
        for (int i = 0; i < this.length; ++i) {
            if (!this.intersectingTypes[i].canBeSeenBy(receiverType, invocationType)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public char[] constantPoolName() {
        return this.intersectingTypes[0].constantPoolName();
    }
    
    @Override
    public PackageBinding getPackage() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ReferenceBinding[] getIntersectingTypes() {
        return this.intersectingTypes;
    }
    
    @Override
    public ReferenceBinding superclass() {
        return this.intersectingTypes[0].isClass() ? this.intersectingTypes[0] : this.javaLangObject;
    }
    
    @Override
    public ReferenceBinding[] superInterfaces() {
        if (this.intersectingTypes[0].isClass()) {
            final ReferenceBinding[] superInterfaces = new ReferenceBinding[this.length - 1];
            System.arraycopy(this.intersectingTypes, 1, superInterfaces, 0, this.length - 1);
            return superInterfaces;
        }
        return this.intersectingTypes;
    }
    
    @Override
    public boolean isBoxedPrimitiveType() {
        return this.intersectingTypes[0].isBoxedPrimitiveType();
    }
    
    @Override
    public boolean isCompatibleWith(final TypeBinding right, final Scope scope) {
        if (TypeBinding.equalsEquals(this, right)) {
            return true;
        }
        final int rightKind = right.kind();
        TypeBinding[] rightIntersectingTypes = null;
        if (rightKind == 8196 && right.boundKind() == 1) {
            final TypeBinding allRightBounds = ((WildcardBinding)right).allBounds();
            if (allRightBounds instanceof IntersectionTypeBinding18) {
                rightIntersectingTypes = ((IntersectionTypeBinding18)allRightBounds).intersectingTypes;
            }
        }
        else if (rightKind == 32772) {
            rightIntersectingTypes = ((IntersectionTypeBinding18)right).intersectingTypes;
        }
        if (rightIntersectingTypes != null) {
            int numRequired = rightIntersectingTypes.length;
            final TypeBinding[] required = new TypeBinding[numRequired];
            System.arraycopy(rightIntersectingTypes, 0, required, 0, numRequired);
            for (int i = 0; i < this.length; ++i) {
                final TypeBinding provided = this.intersectingTypes[i];
                for (int j = 0; j < required.length; ++j) {
                    if (required[j] != null) {
                        if (provided.isCompatibleWith(required[j], scope)) {
                            required[j] = null;
                            if (--numRequired == 0) {
                                return true;
                            }
                            break;
                        }
                    }
                }
            }
            return false;
        }
        for (int k = 0; k < this.length; ++k) {
            if (this.intersectingTypes[k].isCompatibleWith(right, scope)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isSubtypeOf(final TypeBinding other) {
        if (TypeBinding.equalsEquals(this, other)) {
            return true;
        }
        for (int i = 0; i < this.intersectingTypes.length; ++i) {
            if (this.intersectingTypes[i].isSubtypeOf(other)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public char[] qualifiedSourceName() {
        final StringBuffer qualifiedSourceName = new StringBuffer(16);
        for (int i = 0; i < this.length; ++i) {
            qualifiedSourceName.append(this.intersectingTypes[i].qualifiedSourceName());
            if (i != this.length - 1) {
                qualifiedSourceName.append(" & ");
            }
        }
        return qualifiedSourceName.toString().toCharArray();
    }
    
    @Override
    public char[] sourceName() {
        final StringBuffer srcName = new StringBuffer(16);
        for (int i = 0; i < this.length; ++i) {
            srcName.append(this.intersectingTypes[i].sourceName());
            if (i != this.length - 1) {
                srcName.append(" & ");
            }
        }
        return srcName.toString().toCharArray();
    }
    
    @Override
    public char[] readableName() {
        final StringBuffer readableName = new StringBuffer(16);
        for (int i = 0; i < this.length; ++i) {
            readableName.append(this.intersectingTypes[i].readableName());
            if (i != this.length - 1) {
                readableName.append(" & ");
            }
        }
        return readableName.toString().toCharArray();
    }
    
    @Override
    public char[] shortReadableName() {
        final StringBuffer shortReadableName = new StringBuffer(16);
        for (int i = 0; i < this.length; ++i) {
            shortReadableName.append(this.intersectingTypes[i].shortReadableName());
            if (i != this.length - 1) {
                shortReadableName.append(" & ");
            }
        }
        return shortReadableName.toString().toCharArray();
    }
    
    @Override
    public boolean isIntersectionType18() {
        return true;
    }
    
    @Override
    public int kind() {
        return 32772;
    }
    
    @Override
    public String debugName() {
        final StringBuffer debugName = new StringBuffer(16);
        for (int i = 0; i < this.length; ++i) {
            debugName.append(this.intersectingTypes[i].debugName());
            if (i != this.length - 1) {
                debugName.append(" & ");
            }
        }
        return debugName.toString();
    }
    
    @Override
    public String toString() {
        return this.debugName();
    }
    
    public TypeBinding getSAMType(final Scope scope) {
        for (int i = 0, max = this.intersectingTypes.length; i < max; ++i) {
            final TypeBinding typeBinding = this.intersectingTypes[i];
            final MethodBinding methodBinding = typeBinding.getSingleAbstractMethod(scope, true);
            if (methodBinding != null && methodBinding.problemId() != 17) {
                return typeBinding;
            }
        }
        return null;
    }
    
    @Override
    void collectInferenceVariables(final Set<InferenceVariable> variables) {
        for (int i = 0; i < this.intersectingTypes.length; ++i) {
            this.intersectingTypes[i].collectInferenceVariables(variables);
        }
    }
    
    @Override
    public boolean mentionsAny(final TypeBinding[] parameters, final int idx) {
        if (super.mentionsAny(parameters, idx)) {
            return true;
        }
        for (int i = 0; i < this.intersectingTypes.length; ++i) {
            if (this.intersectingTypes[i].mentionsAny(parameters, -1)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public long updateTagBits() {
        ReferenceBinding[] intersectingTypes;
        for (int length = (intersectingTypes = this.intersectingTypes).length, i = 0; i < length; ++i) {
            final TypeBinding intersectingType = intersectingTypes[i];
            this.tagBits |= intersectingType.updateTagBits();
        }
        return super.updateTagBits();
    }
}
