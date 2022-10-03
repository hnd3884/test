package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.impl.common.XmlLocale;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import java.util.List;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaField;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.impl.common.ValidatorListener;
import org.apache.xmlbeans.XmlCursor;

public interface TypeStore extends NamespaceManager
{
    public static final int WS_UNSPECIFIED = 0;
    public static final int WS_PRESERVE = 1;
    public static final int WS_REPLACE = 2;
    public static final int WS_COLLAPSE = 3;
    public static final int NILLABLE = 1;
    public static final int HASDEFAULT = 2;
    public static final int FIXED = 4;
    
    XmlCursor new_cursor();
    
    void validate(final ValidatorListener p0);
    
    SchemaTypeLoader get_schematypeloader();
    
    TypeStoreUser change_type(final SchemaType p0);
    
    TypeStoreUser substitute(final QName p0, final SchemaType p1);
    
    boolean is_attribute();
    
    QName get_xsi_type();
    
    void invalidate_text();
    
    String fetch_text(final int p0);
    
    void store_text(final String p0);
    
    String compute_default_text();
    
    int compute_flags();
    
    boolean validate_on_set();
    
    SchemaField get_schema_field();
    
    void invalidate_nil();
    
    boolean find_nil();
    
    int count_elements(final QName p0);
    
    int count_elements(final QNameSet p0);
    
    TypeStoreUser find_element_user(final QName p0, final int p1);
    
    TypeStoreUser find_element_user(final QNameSet p0, final int p1);
    
    void find_all_element_users(final QName p0, final List p1);
    
    void find_all_element_users(final QNameSet p0, final List p1);
    
    TypeStoreUser insert_element_user(final QName p0, final int p1);
    
    TypeStoreUser insert_element_user(final QNameSet p0, final QName p1, final int p2);
    
    TypeStoreUser add_element_user(final QName p0);
    
    void remove_element(final QName p0, final int p1);
    
    void remove_element(final QNameSet p0, final int p1);
    
    TypeStoreUser find_attribute_user(final QName p0);
    
    TypeStoreUser add_attribute_user(final QName p0);
    
    void remove_attribute(final QName p0);
    
    TypeStoreUser copy_contents_from(final TypeStore p0);
    
    TypeStoreUser copy(final SchemaTypeLoader p0, final SchemaType p1, final XmlOptions p2);
    
    void array_setter(final XmlObject[] p0, final QName p1);
    
    void visit_elements(final TypeStoreVisitor p0);
    
    XmlObject[] exec_query(final String p0, final XmlOptions p1) throws XmlException;
    
    @Deprecated
    Object get_root_object();
    
    XmlLocale get_locale();
}
