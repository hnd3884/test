package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.soap.impl.common.AxiomSOAPBodySupport;
import org.apache.axiom.soap.impl.intf.AxiomSOAPElement;
import org.apache.axiom.soap.impl.common.AxiomSOAPElementSupport;
import org.apache.axiom.core.DeferringParentNode;
import org.apache.axiom.core.DeferringParentNodeSupport;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomContainerSupport;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.common.AxiomElementSupport;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.soap.impl.intf.AxiomSOAPBody;

public abstract class SOAPBodyImpl extends SOAPElement implements AxiomSOAPBody, OMConstants
{
    private boolean enableLookAhead;
    private boolean lookAheadAttempted;
    private boolean lookAheadSuccessful;
    private String lookAheadLocalName;
    private OMNamespace lookAheadNS;
    
    public SOAPBodyImpl() {
        this.enableLookAhead = true;
        this.lookAheadAttempted = false;
        this.lookAheadSuccessful = false;
        this.lookAheadLocalName = null;
        this.lookAheadNS = null;
    }
    
    public boolean hasFault() {
        if (this.hasLookahead()) {
            return "Fault".equals(this.lookAheadLocalName) && this.lookAheadNS != null && ("http://schemas.xmlsoap.org/soap/envelope/".equals(this.lookAheadNS.getNamespaceURI()) || "http://www.w3.org/2003/05/soap-envelope".equals(this.lookAheadNS.getNamespaceURI()));
        }
        return AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getFirstElement(this) instanceof SOAPFault;
    }
    
    public SOAPFault getFault() {
        final OMElement element = AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getFirstElement(this);
        return (element instanceof SOAPFault) ? element : null;
    }
    
    public void addFault(final SOAPFault soapFault) throws OMException {
        if (this.hasFault()) {
            throw new OMException("SOAP Body already has a SOAP Fault and there can not be more than one SOAP fault");
        }
        AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$addChild(this, (OMNode)soapFault);
    }
    
    public void checkParent(final OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAPEnvelopeImpl)) {
            throw new SOAPProcessingException("Expecting an implementation of SOAP Envelope as the parent. But received some other implementation");
        }
    }
    
    public OMNode detach() throws OMException {
        throw new SOAPProcessingException("Can not detach SOAP Body, SOAP Envelope must have a Body !!");
    }
    
    private boolean hasLookahead() {
        if (!this.enableLookAhead) {
            return false;
        }
        if (this.lookAheadAttempted) {
            return this.lookAheadSuccessful;
        }
        this.lookAheadAttempted = true;
        final StAXSOAPModelBuilder soapBuilder = (StAXSOAPModelBuilder)DeferringParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_DeferringParentNodeSupport$org_apache_axiom_core_DeferringParentNode$getBuilder(this);
        if (soapBuilder != null && soapBuilder.isCache() && !soapBuilder.isCompleted() && !soapBuilder.isClosed()) {
            this.lookAheadSuccessful = soapBuilder.lookahead();
            if (this.lookAheadSuccessful) {
                this.lookAheadLocalName = soapBuilder.getName();
                final String ns = soapBuilder.getNamespace();
                if (ns == null) {
                    this.lookAheadNS = null;
                }
                else {
                    final String prefix = soapBuilder.getPrefix();
                    this.lookAheadNS = AxiomSOAPElementSupport.ajc$interMethodDispatch1$org_apache_axiom_soap_impl_common_AxiomSOAPElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPElement$getOMFactory(this).createOMNamespace(ns, (prefix == null) ? "" : prefix);
                }
            }
        }
        return this.lookAheadSuccessful;
    }
    
    public OMNamespace getFirstElementNS() {
        if (this.hasLookahead()) {
            return this.lookAheadNS;
        }
        final OMElement element = AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getFirstElement(this);
        if (element == null) {
            return null;
        }
        return element.getNamespace();
    }
    
    public String getFirstElementLocalName() {
        if (this.hasLookahead()) {
            return this.lookAheadLocalName;
        }
        final OMElement element = AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getFirstElement(this);
        if (element == null) {
            return null;
        }
        return element.getLocalName();
    }
    
    public void addChild(final OMNode child, final boolean fromBuilder) {
        this.enableLookAhead = false;
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$addChild(this, child, fromBuilder);
    }
    
    public final SOAPFault addFault(final Exception e) throws OMException {
        return AxiomSOAPBodySupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPBodySupport$org_apache_axiom_soap_impl_intf_AxiomSOAPBody$addFault(this, e);
    }
}
