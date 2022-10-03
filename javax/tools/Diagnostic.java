package javax.tools;

import java.util.Locale;

public interface Diagnostic<S>
{
    public static final long NOPOS = -1L;
    
    Kind getKind();
    
    S getSource();
    
    long getPosition();
    
    long getStartPosition();
    
    long getEndPosition();
    
    long getLineNumber();
    
    long getColumnNumber();
    
    String getCode();
    
    String getMessage(final Locale p0);
    
    public enum Kind
    {
        ERROR, 
        WARNING, 
        MANDATORY_WARNING, 
        NOTE, 
        OTHER;
    }
}
