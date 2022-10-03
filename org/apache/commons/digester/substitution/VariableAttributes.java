package org.apache.commons.digester.substitution;

import java.util.ArrayList;
import org.xml.sax.Attributes;

public class VariableAttributes implements Attributes
{
    private ArrayList values;
    private Attributes attrs;
    private VariableExpander expander;
    
    public VariableAttributes() {
        this.values = new ArrayList(10);
    }
    
    public void init(final Attributes attrs, final VariableExpander expander) {
        this.attrs = attrs;
        this.expander = expander;
        this.values.clear();
    }
    
    public String getValue(final int index) {
        if (index >= this.values.size()) {
            this.values.ensureCapacity(index + 1);
            for (int i = this.values.size(); i <= index; ++i) {
                this.values.add(null);
            }
        }
        String s = this.values.get(index);
        if (s == null) {
            s = this.attrs.getValue(index);
            if (s != null) {
                s = this.expander.expand(s);
                this.values.set(index, s);
            }
        }
        return s;
    }
    
    public String getValue(final String qname) {
        final int index = this.attrs.getIndex(qname);
        if (index == -1) {
            return null;
        }
        return this.getValue(index);
    }
    
    public String getValue(final String uri, final String localname) {
        final int index = this.attrs.getIndex(uri, localname);
        if (index == -1) {
            return null;
        }
        return this.getValue(index);
    }
    
    public int getIndex(final String qname) {
        return this.attrs.getIndex(qname);
    }
    
    public int getIndex(final String uri, final String localpart) {
        return this.attrs.getIndex(uri, localpart);
    }
    
    public int getLength() {
        return this.attrs.getLength();
    }
    
    public String getLocalName(final int index) {
        return this.attrs.getLocalName(index);
    }
    
    public String getQName(final int index) {
        return this.attrs.getQName(index);
    }
    
    public String getType(final int index) {
        return this.attrs.getType(index);
    }
    
    public String getType(final String qname) {
        return this.attrs.getType(qname);
    }
    
    public String getType(final String uri, final String localname) {
        return this.attrs.getType(uri, localname);
    }
    
    public String getURI(final int index) {
        return this.attrs.getURI(index);
    }
}
