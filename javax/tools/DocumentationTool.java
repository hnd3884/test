package javax.tools;

import java.util.concurrent.Callable;
import java.nio.charset.Charset;
import java.util.Locale;
import java.io.Writer;

public interface DocumentationTool extends Tool, OptionChecker
{
    DocumentationTask getTask(final Writer p0, final JavaFileManager p1, final DiagnosticListener<? super JavaFileObject> p2, final Class<?> p3, final Iterable<String> p4, final Iterable<? extends JavaFileObject> p5);
    
    StandardJavaFileManager getStandardFileManager(final DiagnosticListener<? super JavaFileObject> p0, final Locale p1, final Charset p2);
    
    public interface DocumentationTask extends Callable<Boolean>
    {
        void setLocale(final Locale p0);
        
        Boolean call();
    }
    
    public enum Location implements JavaFileManager.Location
    {
        DOCUMENTATION_OUTPUT, 
        DOCLET_PATH, 
        TAGLET_PATH;
        
        @Override
        public String getName() {
            return this.name();
        }
        
        @Override
        public boolean isOutputLocation() {
            switch (this) {
                case DOCUMENTATION_OUTPUT: {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
    }
}
