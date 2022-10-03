package javax.annotation.processing;

import java.util.Locale;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Types;
import javax.lang.model.util.Elements;
import java.util.Map;

public interface ProcessingEnvironment
{
    Map<String, String> getOptions();
    
    Messager getMessager();
    
    Filer getFiler();
    
    Elements getElementUtils();
    
    Types getTypeUtils();
    
    SourceVersion getSourceVersion();
    
    Locale getLocale();
}
