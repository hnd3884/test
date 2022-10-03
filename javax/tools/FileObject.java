package javax.tools;

import java.io.Writer;
import java.io.Reader;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public interface FileObject
{
    URI toUri();
    
    String getName();
    
    InputStream openInputStream() throws IOException;
    
    OutputStream openOutputStream() throws IOException;
    
    Reader openReader(final boolean p0) throws IOException;
    
    CharSequence getCharContent(final boolean p0) throws IOException;
    
    Writer openWriter() throws IOException;
    
    long getLastModified();
    
    boolean delete();
}
