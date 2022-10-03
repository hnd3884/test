package org.openxmlformats.schemas.xpackage.x2006.digitalSignature.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.CTRelationshipReference;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class CTRelationshipReferenceImpl extends JavaStringHolderEx implements CTRelationshipReference
{
    private static final long serialVersionUID = 1L;
    private static final QName SOURCEID$0;
    
    public CTRelationshipReferenceImpl(final SchemaType schemaType) {
        super(schemaType, true);
    }
    
    protected CTRelationshipReferenceImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
    
    public String getSourceId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRelationshipReferenceImpl.SOURCEID$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetSourceId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRelationshipReferenceImpl.SOURCEID$0);
        }
    }
    
    public void setSourceId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRelationshipReferenceImpl.SOURCEID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRelationshipReferenceImpl.SOURCEID$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSourceId(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRelationshipReferenceImpl.SOURCEID$0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRelationshipReferenceImpl.SOURCEID$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    static {
        SOURCEID$0 = new QName("", "SourceId");
    }
}
