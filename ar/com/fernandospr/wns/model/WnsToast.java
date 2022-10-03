package ar.com.fernandospr.wns.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "toast")
public class WnsToast extends WnsAbstractNotification
{
    @XmlAttribute
    public String launch;
    @XmlAttribute
    public String duration;
    @XmlElement(name = "visual")
    public WnsVisual visual;
    @XmlElement(name = "audio")
    public WnsAudio audio;
    @XmlElement(name = "commands")
    public WnsToastCommands commands;
    
    @Override
    public String getType() {
        return "wns/toast";
    }
}
