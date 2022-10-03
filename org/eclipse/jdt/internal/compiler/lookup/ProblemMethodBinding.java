package org.eclipse.jdt.internal.compiler.lookup;

public class ProblemMethodBinding extends MethodBinding
{
    private int problemReason;
    public MethodBinding closestMatch;
    public InferenceContext18 inferenceContext;
    
    public ProblemMethodBinding(final char[] selector, final TypeBinding[] args, final int problemReason) {
        this.selector = selector;
        this.parameters = ((args == null || args.length == 0) ? Binding.NO_PARAMETERS : args);
        this.problemReason = problemReason;
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
    }
    
    public ProblemMethodBinding(final char[] selector, final TypeBinding[] args, final ReferenceBinding declaringClass, final int problemReason) {
        this.selector = selector;
        this.parameters = ((args == null || args.length == 0) ? Binding.NO_PARAMETERS : args);
        this.declaringClass = declaringClass;
        this.problemReason = problemReason;
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
    }
    
    public ProblemMethodBinding(final MethodBinding closestMatch, final char[] selector, final TypeBinding[] args, final int problemReason) {
        this(selector, args, problemReason);
        this.closestMatch = closestMatch;
        if (closestMatch != null && problemReason != 3) {
            this.declaringClass = closestMatch.declaringClass;
            this.returnType = closestMatch.returnType;
            if (problemReason == 23) {
                this.thrownExceptions = closestMatch.thrownExceptions;
                this.typeVariables = closestMatch.typeVariables;
                this.modifiers = closestMatch.modifiers;
                this.tagBits = closestMatch.tagBits;
            }
        }
    }
    
    @Override
    MethodBinding computeSubstitutedMethod(final MethodBinding method, final LookupEnvironment env) {
        return (this.closestMatch == null) ? this : this.closestMatch.computeSubstitutedMethod(method, env);
    }
    
    @Override
    public MethodBinding findOriginalInheritedMethod(final MethodBinding inheritedMethod) {
        return (this.closestMatch == null) ? this : this.closestMatch.findOriginalInheritedMethod(inheritedMethod);
    }
    
    @Override
    public MethodBinding genericMethod() {
        return (this.closestMatch == null) ? this : this.closestMatch.genericMethod();
    }
    
    @Override
    public MethodBinding original() {
        return (this.closestMatch == null) ? this : this.closestMatch.original();
    }
    
    @Override
    public MethodBinding shallowOriginal() {
        return (this.closestMatch == null) ? this : this.closestMatch.shallowOriginal();
    }
    
    @Override
    public MethodBinding tiebreakMethod() {
        return (this.closestMatch == null) ? this : this.closestMatch.tiebreakMethod();
    }
    
    @Override
    public boolean hasSubstitutedParameters() {
        return this.closestMatch != null && this.closestMatch.hasSubstitutedParameters();
    }
    
    @Override
    public boolean isParameterizedGeneric() {
        return this.closestMatch instanceof ParameterizedGenericMethodBinding;
    }
    
    @Override
    public final int problemId() {
        return this.problemReason;
    }
}
