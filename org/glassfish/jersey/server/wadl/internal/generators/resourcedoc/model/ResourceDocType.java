package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resourceDoc", propOrder = { "classDoc" })
@XmlRootElement(name = "resourceDoc")
public class ResourceDocType
{
    @XmlElementWrapper(name = "classDocs")
    protected List<ClassDocType> classDoc;
    
    public List<ClassDocType> getDocs() {
        if (this.classDoc == null) {
            this.classDoc = new ArrayList<ClassDocType>();
        }
        return this.classDoc;
    }
}
