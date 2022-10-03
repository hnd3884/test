package org.apache.axiom.om.impl.llom.factory;

import org.apache.axiom.soap.impl.intf.AxiomSOAPMessage;
import org.apache.axiom.soap.impl.common.SOAP12Factory;
import org.apache.axiom.soap.impl.common.SOAP11Factory;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.impl.common.factory.OMFactoryImpl;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.common.factory.AbstractOMMetaFactory;

public class OMLinkedListMetaFactory extends AbstractOMMetaFactory
{
    public static final OMLinkedListMetaFactory INSTANCE;
    private final OMFactory omFactory;
    private final SOAPFactory soap11Factory;
    private final SOAPFactory soap12Factory;
    
    static {
        INSTANCE = new OMLinkedListMetaFactory();
    }
    
    private OMLinkedListMetaFactory() {
        this.omFactory = (OMFactory)new OMFactoryImpl((OMMetaFactory)this, LLOMNodeFactory.INSTANCE);
        this.soap11Factory = (SOAPFactory)new SOAP11Factory((OMMetaFactory)this, LLOMNodeFactory.INSTANCE);
        this.soap12Factory = (SOAPFactory)new SOAP12Factory((OMMetaFactory)this, LLOMNodeFactory.INSTANCE);
    }
    
    public OMFactory getOMFactory() {
        return this.omFactory;
    }
    
    public SOAPFactory getSOAP11Factory() {
        return this.soap11Factory;
    }
    
    public SOAPFactory getSOAP12Factory() {
        return this.soap12Factory;
    }
    
    @Override
    public AxiomSOAPMessage createSOAPMessage() {
        return LLOMNodeFactory.INSTANCE.createNode(AxiomSOAPMessage.class);
    }
}
