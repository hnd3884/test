package org.dom4j.tree;

import org.dom4j.Namespace;
import org.dom4j.QName;

public class FlyweightAttribute extends AbstractAttribute
{
    private QName qname;
    protected String value;
    
    public FlyweightAttribute(final QName qname) {
        this.qname = qname;
    }
    
    public FlyweightAttribute(final QName qname, final String value) {
        this.qname = qname;
        this.value = value;
    }
    
    public FlyweightAttribute(final String name, final String value) {
        this.qname = this.getDocumentFactory().createQName(name);
        this.value = value;
    }
    
    public FlyweightAttribute(final String name, final String value, final Namespace namespace) {
        this.qname = this.getDocumentFactory().createQName(name, namespace);
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public QName getQName() {
        return this.qname;
    }
}
