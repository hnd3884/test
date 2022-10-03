package org.eclipse.jdt.internal.compiler.problem;

import java.io.IOException;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;

public class AbortCompilationUnit extends AbortCompilation
{
    private static final long serialVersionUID = -4253893529982226734L;
    public String encoding;
    
    public AbortCompilationUnit(final CompilationResult compilationResult, final CategorizedProblem problem) {
        super(compilationResult, problem);
    }
    
    public AbortCompilationUnit(final CompilationResult compilationResult, final IOException exception, final String encoding) {
        super(compilationResult, exception);
        this.encoding = encoding;
    }
}
