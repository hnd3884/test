package ar.com.fernandospr.wns.model;

import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class WnsAbstractNotification
{
    @XmlTransient
    public abstract String getType();
}
