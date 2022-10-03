package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtension;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTOfficeArtExtensionImpl extends XmlComplexContentImpl implements CTOfficeArtExtension
{
    private static final long serialVersionUID = 1L;
    private static final QName URI$0;
    
    public CTOfficeArtExtensionImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getUri() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOfficeArtExtensionImpl.URI$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlToken xgetUri() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlToken)this.get_store().find_attribute_user(CTOfficeArtExtensionImpl.URI$0);
        }
    }
    
    public boolean isSetUri() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOfficeArtExtensionImpl.URI$0) != null;
        }
    }
    
    public void setUri(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOfficeArtExtensionImpl.URI$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOfficeArtExtensionImpl.URI$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetUri(final XmlToken xmlToken) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken xmlToken2 = (XmlToken)this.get_store().find_attribute_user(CTOfficeArtExtensionImpl.URI$0);
            if (xmlToken2 == null) {
                xmlToken2 = (XmlToken)this.get_store().add_attribute_user(CTOfficeArtExtensionImpl.URI$0);
            }
            xmlToken2.set((XmlObject)xmlToken);
        }
    }
    
    public void unsetUri() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOfficeArtExtensionImpl.URI$0);
        }
    }
    
    static {
        URI$0 = new QName("", "uri");
    }
}
