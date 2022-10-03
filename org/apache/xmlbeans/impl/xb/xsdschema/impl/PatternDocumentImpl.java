package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.PatternDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class PatternDocumentImpl extends XmlComplexContentImpl implements PatternDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName PATTERN$0;
    
    public PatternDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Pattern getPattern() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Pattern target = null;
            target = (Pattern)this.get_store().find_element_user(PatternDocumentImpl.PATTERN$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setPattern(final Pattern pattern) {
        this.generatedSetterHelperImpl(pattern, PatternDocumentImpl.PATTERN$0, 0, (short)1);
    }
    
    @Override
    public Pattern addNewPattern() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Pattern target = null;
            target = (Pattern)this.get_store().add_element_user(PatternDocumentImpl.PATTERN$0);
            return target;
        }
    }
    
    static {
        PATTERN$0 = new QName("http://www.w3.org/2001/XMLSchema", "pattern");
    }
    
    public static class PatternImpl extends NoFixedFacetImpl implements Pattern
    {
        private static final long serialVersionUID = 1L;
        
        public PatternImpl(final SchemaType sType) {
            super(sType);
        }
    }
}
