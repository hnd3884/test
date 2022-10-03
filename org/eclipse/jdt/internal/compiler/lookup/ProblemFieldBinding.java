package org.eclipse.jdt.internal.compiler.lookup;

public class ProblemFieldBinding extends FieldBinding
{
    private int problemId;
    public FieldBinding closestMatch;
    
    public ProblemFieldBinding(final ReferenceBinding declaringClass, final char[] name, final int problemId) {
        this(null, declaringClass, name, problemId);
    }
    
    public ProblemFieldBinding(final FieldBinding closestMatch, final ReferenceBinding declaringClass, final char[] name, final int problemId) {
        this.closestMatch = closestMatch;
        this.declaringClass = declaringClass;
        this.name = name;
        this.problemId = problemId;
    }
    
    @Override
    public final int problemId() {
        return this.problemId;
    }
}
