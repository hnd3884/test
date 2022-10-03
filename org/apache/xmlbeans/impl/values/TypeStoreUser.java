package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaField;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;

public interface TypeStoreUser
{
    void attach_store(final TypeStore p0);
    
    SchemaType get_schema_type();
    
    TypeStore get_store();
    
    void invalidate_value();
    
    boolean uses_invalidate_value();
    
    String build_text(final NamespaceManager p0);
    
    boolean build_nil();
    
    void invalidate_nilvalue();
    
    void invalidate_element_order();
    
    void validate_now();
    
    void disconnect_store();
    
    TypeStoreUser create_element_user(final QName p0, final QName p1);
    
    TypeStoreUser create_attribute_user(final QName p0);
    
    SchemaType get_element_type(final QName p0, final QName p1);
    
    SchemaType get_attribute_type(final QName p0);
    
    String get_default_element_text(final QName p0);
    
    String get_default_attribute_text(final QName p0);
    
    int get_elementflags(final QName p0);
    
    int get_attributeflags(final QName p0);
    
    SchemaField get_attribute_field(final QName p0);
    
    boolean is_child_element_order_sensitive();
    
    QNameSet get_element_ending_delimiters(final QName p0);
    
    TypeStoreVisitor new_visitor();
}
