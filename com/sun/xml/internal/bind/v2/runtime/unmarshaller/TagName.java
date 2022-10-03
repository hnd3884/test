package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.runtime.Name;
import org.xml.sax.Attributes;

public abstract class TagName
{
    public String uri;
    public String local;
    public Attributes atts;
    
    public final boolean matches(final String nsUri, final String local) {
        return this.uri == nsUri && this.local == local;
    }
    
    public final boolean matches(final Name name) {
        return this.local == name.localName && this.uri == name.nsUri;
    }
    
    @Override
    public String toString() {
        return '{' + this.uri + '}' + this.local;
    }
    
    public abstract String getQname();
    
    public String getPrefix() {
        final String qname = this.getQname();
        final int idx = qname.indexOf(58);
        if (idx < 0) {
            return "";
        }
        return qname.substring(0, idx);
    }
    
    public QName createQName() {
        return new QName(this.uri, this.local, this.getPrefix());
    }
}
