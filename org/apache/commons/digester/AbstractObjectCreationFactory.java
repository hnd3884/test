package org.apache.commons.digester;

import org.xml.sax.Attributes;

public abstract class AbstractObjectCreationFactory implements ObjectCreationFactory
{
    protected Digester digester;
    
    public AbstractObjectCreationFactory() {
        this.digester = null;
    }
    
    public abstract Object createObject(final Attributes p0) throws Exception;
    
    public Digester getDigester() {
        return this.digester;
    }
    
    public void setDigester(final Digester digester) {
        this.digester = digester;
    }
}
