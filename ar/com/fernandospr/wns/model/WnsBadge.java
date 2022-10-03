package ar.com.fernandospr.wns.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "badge")
public class WnsBadge extends WnsAbstractNotification
{
    @XmlAttribute
    public String value;
    @XmlAttribute
    public Integer version;
    
    @Override
    public String getType() {
        return "wns/badge";
    }
}
