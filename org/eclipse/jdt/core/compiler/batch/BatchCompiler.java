package org.eclipse.jdt.core.compiler.batch;

import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import java.io.PrintWriter;

public final class BatchCompiler
{
    public static boolean compile(final String commandLine, final PrintWriter outWriter, final PrintWriter errWriter, final CompilationProgress progress) {
        return compile(Main.tokenize(commandLine), outWriter, errWriter, progress);
    }
    
    public static boolean compile(final String[] commandLineArguments, final PrintWriter outWriter, final PrintWriter errWriter, final CompilationProgress progress) {
        return Main.compile(commandLineArguments, outWriter, errWriter, progress);
    }
    
    private BatchCompiler() {
    }
}
