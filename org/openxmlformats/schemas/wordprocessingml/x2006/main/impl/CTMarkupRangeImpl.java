package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDisplacedByCustomXml;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;

public class CTMarkupRangeImpl extends CTMarkupImpl implements CTMarkupRange
{
    private static final long serialVersionUID = 1L;
    private static final QName DISPLACEDBYCUSTOMXML$0;
    
    public CTMarkupRangeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public STDisplacedByCustomXml.Enum getDisplacedByCustomXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMarkupRangeImpl.DISPLACEDBYCUSTOMXML$0);
            if (simpleValue == null) {
                return null;
            }
            return (STDisplacedByCustomXml.Enum)simpleValue.getEnumValue();
        }
    }
    
    @Override
    public STDisplacedByCustomXml xgetDisplacedByCustomXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDisplacedByCustomXml)this.get_store().find_attribute_user(CTMarkupRangeImpl.DISPLACEDBYCUSTOMXML$0);
        }
    }
    
    @Override
    public boolean isSetDisplacedByCustomXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTMarkupRangeImpl.DISPLACEDBYCUSTOMXML$0) != null;
        }
    }
    
    @Override
    public void setDisplacedByCustomXml(final STDisplacedByCustomXml.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMarkupRangeImpl.DISPLACEDBYCUSTOMXML$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTMarkupRangeImpl.DISPLACEDBYCUSTOMXML$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    @Override
    public void xsetDisplacedByCustomXml(final STDisplacedByCustomXml stDisplacedByCustomXml) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDisplacedByCustomXml stDisplacedByCustomXml2 = (STDisplacedByCustomXml)this.get_store().find_attribute_user(CTMarkupRangeImpl.DISPLACEDBYCUSTOMXML$0);
            if (stDisplacedByCustomXml2 == null) {
                stDisplacedByCustomXml2 = (STDisplacedByCustomXml)this.get_store().add_attribute_user(CTMarkupRangeImpl.DISPLACEDBYCUSTOMXML$0);
            }
            stDisplacedByCustomXml2.set((XmlObject)stDisplacedByCustomXml);
        }
    }
    
    @Override
    public void unsetDisplacedByCustomXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTMarkupRangeImpl.DISPLACEDBYCUSTOMXML$0);
        }
    }
    
    static {
        DISPLACEDBYCUSTOMXML$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "displacedByCustomXml");
    }
}
