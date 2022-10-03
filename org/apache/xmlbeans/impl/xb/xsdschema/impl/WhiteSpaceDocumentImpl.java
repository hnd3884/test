package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.impl.xb.xsdschema.WhiteSpaceDocument.WhiteSpace;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.WhiteSpaceDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class WhiteSpaceDocumentImpl extends XmlComplexContentImpl implements WhiteSpaceDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName WHITESPACE$0;
    
    public WhiteSpaceDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public WhiteSpace getWhiteSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            WhiteSpace target = null;
            target = (WhiteSpace)this.get_store().find_element_user(WhiteSpaceDocumentImpl.WHITESPACE$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setWhiteSpace(final WhiteSpace whiteSpace) {
        this.generatedSetterHelperImpl(whiteSpace, WhiteSpaceDocumentImpl.WHITESPACE$0, 0, (short)1);
    }
    
    @Override
    public WhiteSpace addNewWhiteSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            WhiteSpace target = null;
            target = (WhiteSpace)this.get_store().add_element_user(WhiteSpaceDocumentImpl.WHITESPACE$0);
            return target;
        }
    }
    
    static {
        WHITESPACE$0 = new QName("http://www.w3.org/2001/XMLSchema", "whiteSpace");
    }
    
    public static class WhiteSpaceImpl extends FacetImpl implements WhiteSpace
    {
        private static final long serialVersionUID = 1L;
        
        public WhiteSpaceImpl(final SchemaType sType) {
            super(sType);
        }
        
        public static class ValueImpl extends JavaStringEnumerationHolderEx implements Value
        {
            private static final long serialVersionUID = 1L;
            
            public ValueImpl(final SchemaType sType) {
                super(sType, false);
            }
            
            protected ValueImpl(final SchemaType sType, final boolean b) {
                super(sType, b);
            }
        }
    }
}
