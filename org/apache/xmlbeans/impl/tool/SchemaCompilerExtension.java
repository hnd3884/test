package org.apache.xmlbeans.impl.tool;

import java.util.Map;
import org.apache.xmlbeans.SchemaTypeSystem;

public interface SchemaCompilerExtension
{
    void schemaCompilerExtension(final SchemaTypeSystem p0, final Map p1);
    
    String getExtensionName();
}
