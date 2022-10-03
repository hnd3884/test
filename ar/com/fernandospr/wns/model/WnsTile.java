package ar.com.fernandospr.wns.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tile")
public class WnsTile extends WnsAbstractNotification
{
    @XmlElement(name = "visual")
    public WnsVisual visual;
    
    @Override
    public String getType() {
        return "wns/tile";
    }
}
