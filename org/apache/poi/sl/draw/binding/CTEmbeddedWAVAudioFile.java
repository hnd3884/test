package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_EmbeddedWAVAudioFile")
public class CTEmbeddedWAVAudioFile
{
    @XmlAttribute(name = "embed", namespace = "http://schemas.openxmlformats.org/officeDocument/2006/relationships", required = true)
    protected String embed;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "builtIn")
    protected Boolean builtIn;
    
    public String getEmbed() {
        return this.embed;
    }
    
    public void setEmbed(final String value) {
        this.embed = value;
    }
    
    public boolean isSetEmbed() {
        return this.embed != null;
    }
    
    public String getName() {
        if (this.name == null) {
            return "";
        }
        return this.name;
    }
    
    public void setName(final String value) {
        this.name = value;
    }
    
    public boolean isSetName() {
        return this.name != null;
    }
    
    public boolean isBuiltIn() {
        return this.builtIn != null && this.builtIn;
    }
    
    public void setBuiltIn(final boolean value) {
        this.builtIn = value;
    }
    
    public boolean isSetBuiltIn() {
        return this.builtIn != null;
    }
    
    public void unsetBuiltIn() {
        this.builtIn = null;
    }
}
