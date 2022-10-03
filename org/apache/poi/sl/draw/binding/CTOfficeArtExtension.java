package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_OfficeArtExtension", propOrder = { "any" })
public class CTOfficeArtExtension
{
    @XmlAnyElement(lax = true)
    protected Object any;
    @XmlAttribute(name = "uri")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String uri;
    
    public Object getAny() {
        return this.any;
    }
    
    public void setAny(final Object value) {
        this.any = value;
    }
    
    public boolean isSetAny() {
        return this.any != null;
    }
    
    public String getUri() {
        return this.uri;
    }
    
    public void setUri(final String value) {
        this.uri = value;
    }
    
    public boolean isSetUri() {
        return this.uri != null;
    }
}
