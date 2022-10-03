package javax.tools;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;

public interface JavaFileObject extends FileObject
{
    Kind getKind();
    
    boolean isNameCompatible(final String p0, final Kind p1);
    
    NestingKind getNestingKind();
    
    Modifier getAccessLevel();
    
    public enum Kind
    {
        SOURCE(".java"), 
        CLASS(".class"), 
        HTML(".html"), 
        OTHER("");
        
        public final String extension;
        
        private Kind(final String extension) {
            extension.getClass();
            this.extension = extension;
        }
    }
}
