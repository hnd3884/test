package ar.com.fernandospr.wns.model;

import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "text")
public class WnsText
{
    @XmlAttribute
    public Integer id;
    @XmlAttribute
    public String lang;
    @XmlValue
    public String value;
}
