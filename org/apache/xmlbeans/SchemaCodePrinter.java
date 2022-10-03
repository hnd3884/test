package org.apache.xmlbeans;

import java.io.IOException;
import java.io.Writer;

public interface SchemaCodePrinter
{
    void printTypeImpl(final Writer p0, final SchemaType p1) throws IOException;
    
    void printType(final Writer p0, final SchemaType p1) throws IOException;
    
    @Deprecated
    void printLoader(final Writer p0, final SchemaTypeSystem p1) throws IOException;
}
