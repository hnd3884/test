package org.eclipse.jdt.internal.compiler.tool;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import javax.tools.Diagnostic;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import javax.tools.JavaFileObject;
import javax.tools.DiagnosticListener;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;

public class EclipseBatchRequestor implements ICompilerRequestor
{
    private final Main compiler;
    private int lineDelta;
    private final DiagnosticListener<? super JavaFileObject> diagnosticListener;
    private final DefaultProblemFactory problemFactory;
    
    public EclipseBatchRequestor(final Main compiler, final DiagnosticListener<? super JavaFileObject> diagnosticListener, final DefaultProblemFactory problemFactory) {
        this.lineDelta = 0;
        this.compiler = compiler;
        this.diagnosticListener = diagnosticListener;
        this.problemFactory = problemFactory;
    }
    
    @Override
    public void acceptResult(final CompilationResult compilationResult) {
        if (compilationResult.lineSeparatorPositions != null) {
            final int unitLineCount = compilationResult.lineSeparatorPositions.length;
            this.lineDelta += unitLineCount;
            if (this.compiler.showProgress && this.lineDelta > 2000) {
                this.compiler.logger.logProgress();
                this.lineDelta = 0;
            }
        }
        this.compiler.logger.startLoggingSource(compilationResult);
        if (compilationResult.hasProblems() || compilationResult.hasTasks()) {
            this.compiler.logger.logProblems(compilationResult.getAllProblems(), compilationResult.compilationUnit.getContents(), this.compiler);
            this.reportProblems(compilationResult);
        }
        this.compiler.outputClassFiles(compilationResult);
        this.compiler.logger.endLoggingSource();
    }
    
    private void reportProblems(final CompilationResult result) {
        CategorizedProblem[] allProblems;
        for (int length = (allProblems = result.getAllProblems()).length, i = 0; i < length; ++i) {
            final CategorizedProblem problem = allProblems[i];
            final EclipseDiagnostic diagnostic = EclipseDiagnostic.newInstance(problem, this.problemFactory);
            this.diagnosticListener.report(diagnostic);
        }
    }
}
