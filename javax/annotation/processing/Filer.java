package javax.annotation.processing;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import java.io.IOException;
import javax.tools.JavaFileObject;
import javax.lang.model.element.Element;

public interface Filer
{
    JavaFileObject createSourceFile(final CharSequence p0, final Element... p1) throws IOException;
    
    JavaFileObject createClassFile(final CharSequence p0, final Element... p1) throws IOException;
    
    FileObject createResource(final JavaFileManager.Location p0, final CharSequence p1, final CharSequence p2, final Element... p3) throws IOException;
    
    FileObject getResource(final JavaFileManager.Location p0, final CharSequence p1, final CharSequence p2) throws IOException;
}
