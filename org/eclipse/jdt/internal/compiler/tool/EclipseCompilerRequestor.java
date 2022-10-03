package org.eclipse.jdt.internal.compiler.tool;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import javax.tools.Diagnostic;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import javax.tools.JavaFileObject;
import javax.tools.DiagnosticListener;
import org.eclipse.jdt.internal.compiler.batch.BatchCompilerRequestor;

public class EclipseCompilerRequestor extends BatchCompilerRequestor
{
    private final DiagnosticListener<? super JavaFileObject> diagnosticListener;
    private final DefaultProblemFactory problemFactory;
    
    public EclipseCompilerRequestor(final Main compiler, final DiagnosticListener<? super JavaFileObject> diagnosticListener, final DefaultProblemFactory problemFactory) {
        super(compiler);
        this.diagnosticListener = diagnosticListener;
        this.problemFactory = problemFactory;
    }
    
    @Override
    protected void reportProblems(final CompilationResult result) {
        if (this.diagnosticListener != null) {
            CategorizedProblem[] allProblems;
            for (int length = (allProblems = result.getAllProblems()).length, i = 0; i < length; ++i) {
                final CategorizedProblem problem = allProblems[i];
                final EclipseDiagnostic diagnostic = EclipseDiagnostic.newInstance(problem, this.problemFactory);
                this.diagnosticListener.report(diagnostic);
            }
        }
    }
}
