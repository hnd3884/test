package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.Map;

public class InferenceVariable extends TypeVariableBinding
{
    InvocationSite site;
    TypeBinding typeParameter;
    long nullHints;
    private InferenceVariable prototype;
    int varId;
    
    public static InferenceVariable get(final TypeBinding typeParameter, final int rank, final InvocationSite site, final Scope scope, final ReferenceBinding object) {
        final Map<InferenceVarKey, InferenceVariable> uniqueInferenceVariables = scope.compilationUnitScope().uniqueInferenceVariables;
        InferenceVariable var = null;
        InferenceVarKey key = null;
        if (site != null && typeParameter != null) {
            key = new InferenceVarKey(typeParameter, site, rank);
            var = uniqueInferenceVariables.get(key);
        }
        if (var == null) {
            final int newVarId = uniqueInferenceVariables.size();
            var = new InferenceVariable(typeParameter, rank, newVarId, site, scope.environment(), object);
            if (key != null) {
                uniqueInferenceVariables.put(key, var);
            }
        }
        return var;
    }
    
    private InferenceVariable(final TypeBinding typeParameter, final int parameterRank, final int iVarId, final InvocationSite site, final LookupEnvironment environment, final ReferenceBinding object) {
        this(typeParameter, parameterRank, site, CharOperation.concat(typeParameter.shortReadableName(), Integer.toString(iVarId).toCharArray(), '#'), environment, object);
        this.varId = iVarId;
    }
    
    private InferenceVariable(final TypeBinding typeParameter, final int parameterRank, final InvocationSite site, final char[] sourceName, final LookupEnvironment environment, final ReferenceBinding object) {
        super(sourceName, null, parameterRank, environment);
        this.site = site;
        this.typeParameter = typeParameter;
        this.tagBits |= (typeParameter.tagBits & 0x180000000000000L);
        if (typeParameter.isTypeVariable()) {
            final TypeVariableBinding typeVariable = (TypeVariableBinding)typeParameter;
            if (typeVariable.firstBound != null) {
                final long boundBits = typeVariable.firstBound.tagBits & 0x180000000000000L;
                if (boundBits == 72057594037927936L) {
                    this.tagBits |= boundBits;
                }
                else {
                    this.nullHints |= boundBits;
                }
            }
        }
        this.superclass = object;
        this.prototype = this;
    }
    
    @Override
    public TypeBinding clone(final TypeBinding enclosingType) {
        final InferenceVariable clone = new InferenceVariable(this.typeParameter, this.rank, this.site, this.sourceName, this.environment, this.superclass);
        clone.tagBits = this.tagBits;
        clone.nullHints = this.nullHints;
        clone.varId = this.varId;
        clone.prototype = this;
        return clone;
    }
    
    @Override
    public InferenceVariable prototype() {
        return this.prototype;
    }
    
    @Override
    public char[] constantPoolName() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public PackageBinding getPackage() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isCompatibleWith(final TypeBinding right, final Scope scope) {
        return true;
    }
    
    @Override
    public boolean isProperType(final boolean admitCapture18) {
        return false;
    }
    
    @Override
    TypeBinding substituteInferenceVariable(final InferenceVariable var, final TypeBinding substituteType) {
        if (TypeBinding.equalsEquals(this, var)) {
            return substituteType;
        }
        return this;
    }
    
    @Override
    void collectInferenceVariables(final Set<InferenceVariable> variables) {
        variables.add(this);
    }
    
    @Override
    public ReferenceBinding[] superInterfaces() {
        return Binding.NO_SUPERINTERFACES;
    }
    
    @Override
    public char[] qualifiedSourceName() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public char[] sourceName() {
        return this.sourceName;
    }
    
    @Override
    public char[] readableName() {
        return this.sourceName;
    }
    
    @Override
    public boolean hasTypeBit(final int bit) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String debugName() {
        return String.valueOf(this.sourceName);
    }
    
    @Override
    public String toString() {
        return this.debugName();
    }
    
    @Override
    public int hashCode() {
        int code = this.typeParameter.hashCode() + 17 * this.rank;
        if (this.site != null) {
            code = 31 * code + this.site.sourceStart();
            code = 31 * code + this.site.sourceEnd();
        }
        return code;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof InferenceVariable)) {
            return false;
        }
        final InferenceVariable other = (InferenceVariable)obj;
        return this.rank == other.rank && InferenceContext18.isSameSite(this.site, other.site) && TypeBinding.equalsEquals(this.typeParameter, other.typeParameter);
    }
    
    @Override
    public TypeBinding erasure() {
        if (this.superclass == null) {
            this.superclass = this.environment.getType(TypeConstants.JAVA_LANG_OBJECT);
        }
        return super.erasure();
    }
    
    static class InferenceVarKey
    {
        TypeBinding typeParameter;
        long position;
        int rank;
        
        InferenceVarKey(final TypeBinding typeParameter, final InvocationSite site, final int rank) {
            this.typeParameter = typeParameter;
            this.position = ((long)site.sourceStart() << 32) + site.sourceEnd();
            this.rank = rank;
        }
        
        @Override
        public int hashCode() {
            int result = 1;
            result = 31 * result + (int)(this.position ^ this.position >>> 32);
            result = 31 * result + this.rank;
            result = 31 * result + this.typeParameter.id;
            return result;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof InferenceVarKey)) {
                return false;
            }
            final InferenceVarKey other = (InferenceVarKey)obj;
            return this.position == other.position && this.rank == other.rank && !TypeBinding.notEquals(this.typeParameter, other.typeParameter);
        }
    }
}
