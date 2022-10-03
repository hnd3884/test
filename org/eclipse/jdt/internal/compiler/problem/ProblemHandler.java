package org.eclipse.jdt.internal.compiler.problem;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;

public class ProblemHandler
{
    public static final String[] NoArgument;
    public IErrorHandlingPolicy policy;
    public final IProblemFactory problemFactory;
    public final CompilerOptions options;
    private IErrorHandlingPolicy rootPolicy;
    protected boolean suppressTagging;
    
    static {
        NoArgument = CharOperation.NO_STRINGS;
    }
    
    public ProblemHandler(final IErrorHandlingPolicy policy, final CompilerOptions options, final IProblemFactory problemFactory) {
        this.suppressTagging = false;
        this.policy = policy;
        this.problemFactory = problemFactory;
        this.options = options;
    }
    
    public int computeSeverity(final int problemId) {
        return 1;
    }
    
    public CategorizedProblem createProblem(final char[] fileName, final int problemId, final String[] problemArguments, final String[] messageArguments, final int severity, final int problemStartPosition, final int problemEndPosition, final int lineNumber, final int columnNumber) {
        return this.problemFactory.createProblem(fileName, problemId, problemArguments, messageArguments, severity, problemStartPosition, problemEndPosition, lineNumber, columnNumber);
    }
    
    public CategorizedProblem createProblem(final char[] fileName, final int problemId, final String[] problemArguments, final int elaborationId, final String[] messageArguments, final int severity, final int problemStartPosition, final int problemEndPosition, final int lineNumber, final int columnNumber) {
        return this.problemFactory.createProblem(fileName, problemId, problemArguments, elaborationId, messageArguments, severity, problemStartPosition, problemEndPosition, lineNumber, columnNumber);
    }
    
    public void handle(final int problemId, final String[] problemArguments, final int elaborationId, final String[] messageArguments, final int severity, final int problemStartPosition, final int problemEndPosition, final ReferenceContext referenceContext, final CompilationResult unitResult) {
        if (severity == 256) {
            return;
        }
        final boolean mandatory = (severity & 0x21) == 0x1;
        if ((severity & 0x200) == 0x0 && this.policy.ignoreAllErrors()) {
            if (referenceContext != null) {
                if (mandatory) {
                    referenceContext.tagAsHavingIgnoredMandatoryErrors(problemId);
                }
                return;
            }
            if ((severity & 0x1) != 0x0) {
                final CategorizedProblem problem = this.createProblem(null, problemId, problemArguments, elaborationId, messageArguments, severity, 0, 0, 0, 0);
                throw new AbortCompilation(null, problem);
            }
        }
        else {
            if ((severity & 0x20) != 0x0 && problemId != 536871362 && !this.options.ignoreSourceFolderWarningOption) {
                final ICompilationUnit cu = unitResult.getCompilationUnit();
                try {
                    if (cu != null && cu.ignoreOptionalProblems()) {
                        return;
                    }
                }
                catch (final AbstractMethodError abstractMethodError) {}
            }
            if (referenceContext == null) {
                if ((severity & 0x1) != 0x0) {
                    final CategorizedProblem problem = this.createProblem(null, problemId, problemArguments, elaborationId, messageArguments, severity, 0, 0, 0, 0);
                    throw new AbortCompilation(null, problem);
                }
            }
            else {
                int[] lineEnds;
                final int lineNumber = (problemStartPosition >= 0) ? Util.getLineNumber(problemStartPosition, lineEnds = unitResult.getLineSeparatorPositions(), 0, lineEnds.length - 1) : 0;
                final int columnNumber = (problemStartPosition >= 0) ? Util.searchColumnNumber(unitResult.getLineSeparatorPositions(), lineNumber, problemStartPosition) : 0;
                final CategorizedProblem problem2 = this.createProblem(unitResult.getFileName(), problemId, problemArguments, elaborationId, messageArguments, severity, problemStartPosition, problemEndPosition, lineNumber, columnNumber);
                if (problem2 == null) {
                    return;
                }
                switch (severity & 0x1) {
                    case 1: {
                        this.record(problem2, unitResult, referenceContext, mandatory);
                        if ((severity & 0x80) == 0x0) {
                            break;
                        }
                        if (!referenceContext.hasErrors() && !mandatory && this.options.suppressOptionalErrors) {
                            final CompilationUnitDeclaration unitDecl = referenceContext.getCompilationUnitDeclaration();
                            if (unitDecl != null && unitDecl.isSuppressed(problem2)) {
                                return;
                            }
                        }
                        if (!this.suppressTagging || this.options.treatOptionalErrorAsFatal) {
                            referenceContext.tagAsHavingErrors();
                        }
                        final int abortLevel;
                        if ((abortLevel = (this.policy.stopOnFirstError() ? 2 : (severity & 0x1E))) != 0) {
                            referenceContext.abort(abortLevel, problem2);
                            break;
                        }
                        break;
                    }
                    case 0: {
                        this.record(problem2, unitResult, referenceContext, false);
                        break;
                    }
                }
            }
        }
    }
    
    public void handle(final int problemId, final String[] problemArguments, final String[] messageArguments, final int problemStartPosition, final int problemEndPosition, final ReferenceContext referenceContext, final CompilationResult unitResult) {
        this.handle(problemId, problemArguments, 0, messageArguments, this.computeSeverity(problemId), problemStartPosition, problemEndPosition, referenceContext, unitResult);
    }
    
    public void record(final CategorizedProblem problem, final CompilationResult unitResult, final ReferenceContext referenceContext, final boolean mandatoryError) {
        unitResult.record(problem, referenceContext, mandatoryError);
    }
    
    public IErrorHandlingPolicy switchErrorHandlingPolicy(final IErrorHandlingPolicy newPolicy) {
        if (this.rootPolicy == null) {
            this.rootPolicy = this.policy;
        }
        final IErrorHandlingPolicy presentPolicy = this.policy;
        this.policy = newPolicy;
        return presentPolicy;
    }
    
    public IErrorHandlingPolicy suspendTempErrorHandlingPolicy() {
        final IErrorHandlingPolicy presentPolicy = this.policy;
        if (this.rootPolicy != null) {
            this.policy = this.rootPolicy;
        }
        return presentPolicy;
    }
    
    public void resumeTempErrorHandlingPolicy(final IErrorHandlingPolicy previousPolicy) {
        this.policy = previousPolicy;
    }
}
