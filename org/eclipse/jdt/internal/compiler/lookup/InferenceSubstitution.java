package org.eclipse.jdt.internal.compiler.lookup;

public class InferenceSubstitution extends Scope.Substitutor implements Substitution
{
    private LookupEnvironment environment;
    private InferenceVariable[] variables;
    private InvocationSite[] sites;
    
    public InferenceSubstitution(final LookupEnvironment environment, final InferenceVariable[] variables, final InvocationSite site) {
        this.environment = environment;
        this.variables = variables;
        this.sites = new InvocationSite[] { site };
    }
    
    public InferenceSubstitution(final InferenceContext18 context) {
        this(context.environment, context.inferenceVariables, context.currentInvocation);
    }
    
    public InferenceSubstitution addContext(final InferenceContext18 otherContext) {
        final InferenceSubstitution subst = new InferenceSubstitution(this.environment, null, null) {
            @Override
            protected boolean isSameParameter(final TypeBinding p1, final TypeBinding originalType) {
                if (TypeBinding.equalsEquals(p1, originalType)) {
                    return true;
                }
                if (p1 instanceof TypeVariableBinding && originalType instanceof TypeVariableBinding) {
                    final TypeVariableBinding var1 = (TypeVariableBinding)p1;
                    final TypeVariableBinding var2 = (TypeVariableBinding)originalType;
                    Binding declaring1 = var1.declaringElement;
                    Binding declaring2 = var2.declaringElement;
                    if (declaring1 instanceof MethodBinding && declaring2 instanceof MethodBinding) {
                        declaring1 = ((MethodBinding)declaring1).original();
                        declaring2 = ((MethodBinding)declaring2).original();
                    }
                    return declaring1 == declaring2 && var1.rank == var2.rank;
                }
                return false;
            }
        };
        final int l1 = this.sites.length;
        subst.sites = new InvocationSite[l1 + 1];
        System.arraycopy(this.sites, 0, subst.sites, 0, l1);
        subst.sites[l1] = otherContext.currentInvocation;
        subst.variables = this.variables;
        return subst;
    }
    
    @Override
    public TypeBinding substitute(final Substitution substitution, final TypeBinding originalType) {
        int i = 0;
        while (i < this.variables.length) {
            final InferenceVariable variable = this.variables[i];
            if (this.isInSites(variable.site) && this.isSameParameter(this.getP(i), originalType)) {
                if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled && originalType.hasNullTypeAnnotations()) {
                    return this.environment.createAnnotatedType(variable.withoutToplevelNullAnnotation(), originalType.getTypeAnnotations());
                }
                return variable;
            }
            else {
                ++i;
            }
        }
        return super.substitute(substitution, originalType);
    }
    
    private boolean isInSites(final InvocationSite otherSite) {
        for (int i = 0; i < this.sites.length; ++i) {
            if (InferenceContext18.isSameSite(this.sites[i], otherSite)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isSameParameter(final TypeBinding p1, final TypeBinding originalType) {
        return TypeBinding.equalsEquals(p1, originalType);
    }
    
    protected TypeBinding getP(final int i) {
        return this.variables[i].typeParameter;
    }
    
    @Override
    public TypeBinding substitute(TypeVariableBinding typeVariable) {
        ReferenceBinding superclass = typeVariable.superclass;
        ReferenceBinding[] superInterfaces = typeVariable.superInterfaces;
        boolean hasSubstituted = false;
        for (int i = 0; i < this.variables.length; ++i) {
            final InferenceVariable variable = this.variables[i];
            final TypeBinding pi = this.getP(i);
            if (TypeBinding.equalsEquals(pi, typeVariable)) {
                return variable;
            }
            if (TypeBinding.equalsEquals(pi, superclass)) {
                superclass = variable;
                hasSubstituted = true;
            }
            else if (superInterfaces != null) {
                for (int ifcLen = superInterfaces.length, j = 0; j < ifcLen; ++j) {
                    if (TypeBinding.equalsEquals(pi, superInterfaces[j])) {
                        if (superInterfaces == typeVariable.superInterfaces) {
                            System.arraycopy(superInterfaces, 0, superInterfaces = new ReferenceBinding[ifcLen], 0, ifcLen);
                        }
                        superInterfaces[j] = variable;
                        hasSubstituted = true;
                        break;
                    }
                }
            }
        }
        if (hasSubstituted) {
            typeVariable = new TypeVariableBinding(typeVariable.sourceName, typeVariable.declaringElement, typeVariable.rank, this.environment);
            typeVariable.superclass = superclass;
            typeVariable.superInterfaces = superInterfaces;
            typeVariable.firstBound = ((superclass != null) ? superclass : superInterfaces[0]);
            if (typeVariable.firstBound.hasNullTypeAnnotations()) {
                final TypeVariableBinding typeVariableBinding = typeVariable;
                typeVariableBinding.tagBits |= 0x100000L;
            }
        }
        return typeVariable;
    }
    
    @Override
    public LookupEnvironment environment() {
        return this.environment;
    }
    
    @Override
    public boolean isRawSubstitution() {
        return false;
    }
}
