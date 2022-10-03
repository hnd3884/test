package org.eclipse.jdt.internal.compiler.env;

public class NameEnvironmentAnswer
{
    IBinaryType binaryType;
    ICompilationUnit compilationUnit;
    ISourceType[] sourceTypes;
    AccessRestriction accessRestriction;
    String externalAnnotationPath;
    
    public NameEnvironmentAnswer(final IBinaryType binaryType, final AccessRestriction accessRestriction) {
        this.binaryType = binaryType;
        this.accessRestriction = accessRestriction;
    }
    
    public NameEnvironmentAnswer(final ICompilationUnit compilationUnit, final AccessRestriction accessRestriction) {
        this.compilationUnit = compilationUnit;
        this.accessRestriction = accessRestriction;
    }
    
    public NameEnvironmentAnswer(final ISourceType[] sourceTypes, final AccessRestriction accessRestriction, final String externalAnnotationPath) {
        this.sourceTypes = sourceTypes;
        this.accessRestriction = accessRestriction;
        this.externalAnnotationPath = externalAnnotationPath;
    }
    
    public AccessRestriction getAccessRestriction() {
        return this.accessRestriction;
    }
    
    public IBinaryType getBinaryType() {
        return this.binaryType;
    }
    
    public ICompilationUnit getCompilationUnit() {
        return this.compilationUnit;
    }
    
    public String getExternalAnnotationPath() {
        return this.externalAnnotationPath;
    }
    
    public ISourceType[] getSourceTypes() {
        return this.sourceTypes;
    }
    
    public boolean isBinaryType() {
        return this.binaryType != null;
    }
    
    public boolean isCompilationUnit() {
        return this.compilationUnit != null;
    }
    
    public boolean isSourceType() {
        return this.sourceTypes != null;
    }
    
    public boolean ignoreIfBetter() {
        return this.accessRestriction != null && this.accessRestriction.ignoreIfBetter();
    }
    
    public boolean isBetter(final NameEnvironmentAnswer otherAnswer) {
        return otherAnswer == null || this.accessRestriction == null || (otherAnswer.accessRestriction != null && this.accessRestriction.getProblemId() < otherAnswer.accessRestriction.getProblemId());
    }
}
