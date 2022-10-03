package javax.tools;

import javax.annotation.processing.Processor;
import java.util.concurrent.Callable;
import java.nio.charset.Charset;
import java.util.Locale;
import java.io.Writer;

public interface JavaCompiler extends Tool, OptionChecker
{
    CompilationTask getTask(final Writer p0, final JavaFileManager p1, final DiagnosticListener<? super JavaFileObject> p2, final Iterable<String> p3, final Iterable<String> p4, final Iterable<? extends JavaFileObject> p5);
    
    StandardJavaFileManager getStandardFileManager(final DiagnosticListener<? super JavaFileObject> p0, final Locale p1, final Charset p2);
    
    public interface CompilationTask extends Callable<Boolean>
    {
        void setProcessors(final Iterable<? extends Processor> p0);
        
        void setLocale(final Locale p0);
        
        Boolean call();
    }
}
