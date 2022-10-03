package org.apache.axiom.om.ds;

import javax.xml.namespace.QName;
import org.apache.axiom.om.QNameAwareOMDataSource;

public abstract class WrappedTextNodeOMDataSource extends AbstractPullOMDataSource implements QNameAwareOMDataSource
{
    protected final QName wrapperElementName;
    
    public WrappedTextNodeOMDataSource(final QName wrapperElementName) {
        this.wrapperElementName = wrapperElementName;
    }
    
    public String getLocalName() {
        return this.wrapperElementName.getLocalPart();
    }
    
    public String getNamespaceURI() {
        return this.wrapperElementName.getNamespaceURI();
    }
    
    public String getPrefix() {
        return this.wrapperElementName.getPrefix();
    }
}
