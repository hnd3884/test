package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.xhtml;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAnyElement;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "foo", propOrder = {})
@XmlRootElement(name = "foo")
public class XhtmlElementType
{
    @XmlAnyElement
    protected List<Object> any;
    
    public List<Object> getChildNodes() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }
}
