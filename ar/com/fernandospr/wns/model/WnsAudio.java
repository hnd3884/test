package ar.com.fernandospr.wns.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "audio")
public class WnsAudio
{
    @XmlAttribute
    public String src;
    @XmlAttribute
    public Boolean loop;
    @XmlAttribute
    public Boolean silent;
}
