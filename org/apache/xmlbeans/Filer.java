package org.apache.xmlbeans;

import java.io.Writer;
import java.io.IOException;
import java.io.OutputStream;

public interface Filer
{
    OutputStream createBinaryFile(final String p0) throws IOException;
    
    Writer createSourceFile(final String p0) throws IOException;
}
