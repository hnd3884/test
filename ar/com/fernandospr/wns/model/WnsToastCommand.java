package ar.com.fernandospr.wns.model;

import javax.xml.bind.annotation.XmlAttribute;

public class WnsToastCommand
{
    @XmlAttribute
    public String id;
    @XmlAttribute
    public String arguments;
    
    public WnsToastCommand(final String id, final String arguments) {
        this.id = id;
        this.arguments = arguments;
    }
}
