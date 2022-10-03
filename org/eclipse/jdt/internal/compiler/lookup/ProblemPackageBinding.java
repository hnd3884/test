package org.eclipse.jdt.internal.compiler.lookup;

public class ProblemPackageBinding extends PackageBinding
{
    private int problemId;
    
    ProblemPackageBinding(final char[][] compoundName, final int problemId) {
        this.compoundName = compoundName;
        this.problemId = problemId;
    }
    
    ProblemPackageBinding(final char[] name, final int problemId) {
        this(new char[][] { name }, problemId);
    }
    
    @Override
    public final int problemId() {
        return this.problemId;
    }
}
