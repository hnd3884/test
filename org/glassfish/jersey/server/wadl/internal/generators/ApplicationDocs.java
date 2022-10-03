package org.glassfish.jersey.server.wadl.internal.generators;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import com.sun.research.ws.wadl.Doc;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "applicationDocs", propOrder = { "docs" })
@XmlRootElement(name = "applicationDocs")
public class ApplicationDocs
{
    @XmlElement(name = "doc")
    protected List<Doc> docs;
    
    public List<Doc> getDocs() {
        if (this.docs == null) {
            this.docs = new ArrayList<Doc>();
        }
        return this.docs;
    }
}
