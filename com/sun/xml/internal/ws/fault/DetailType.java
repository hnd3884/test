package com.sun.xml.internal.ws.fault;

import java.util.ArrayList;
import com.sun.istack.internal.Nullable;
import org.w3c.dom.Node;
import com.sun.istack.internal.NotNull;
import javax.xml.bind.annotation.XmlAnyElement;
import org.w3c.dom.Element;
import java.util.List;

class DetailType
{
    @XmlAnyElement
    private final List<Element> detailEntry;
    
    @NotNull
    List<Element> getDetails() {
        return this.detailEntry;
    }
    
    @Nullable
    Node getDetail(final int n) {
        if (n < this.detailEntry.size()) {
            return this.detailEntry.get(n);
        }
        return null;
    }
    
    DetailType(final Element detailObject) {
        this.detailEntry = new ArrayList<Element>();
        if (detailObject != null) {
            this.detailEntry.add(detailObject);
        }
    }
    
    DetailType() {
        this.detailEntry = new ArrayList<Element>();
    }
}
