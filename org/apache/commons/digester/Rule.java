package org.apache.commons.digester;

import org.xml.sax.Attributes;

public abstract class Rule
{
    protected Digester digester;
    protected String namespaceURI;
    
    public Rule(final Digester digester) {
        this.digester = null;
        this.namespaceURI = null;
        this.setDigester(digester);
    }
    
    public Rule() {
        this.digester = null;
        this.namespaceURI = null;
    }
    
    public Digester getDigester() {
        return this.digester;
    }
    
    public void setDigester(final Digester digester) {
        this.digester = digester;
    }
    
    public String getNamespaceURI() {
        return this.namespaceURI;
    }
    
    public void setNamespaceURI(final String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }
    
    public void begin(final Attributes attributes) throws Exception {
    }
    
    public void begin(final String namespace, final String name, final Attributes attributes) throws Exception {
        this.begin(attributes);
    }
    
    public void body(final String text) throws Exception {
    }
    
    public void body(final String namespace, final String name, final String text) throws Exception {
        this.body(text);
    }
    
    public void end() throws Exception {
    }
    
    public void end(final String namespace, final String name) throws Exception {
        this.end();
    }
    
    public void finish() throws Exception {
    }
}
