package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaField;
import javax.xml.namespace.QName;

public interface TypeStoreVisitor
{
    boolean visit(final QName p0);
    
    int get_elementflags();
    
    String get_default_text();
    
    SchemaField get_schema_field();
}
