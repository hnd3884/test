package org.apache.axiom.soap.impl.common;

import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.om.impl.common.AxiomSourcedElementSupport;
import org.apache.axiom.soap.impl.intf.AxiomSOAPHeaderBlock;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.impl.intf.AxiomSOAPMessage;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.common.AxiomElementSupport;
import org.apache.axiom.om.impl.intf.AxiomNamedInformationItem;
import org.apache.axiom.om.impl.common.AxiomNamedInformationItemSupport;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.common.AxiomContainerSupport;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.soap.impl.intf.AxiomSOAPElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.impl.builder.SOAPFactoryEx;
import org.apache.axiom.om.impl.common.factory.OMFactoryImpl;

public abstract class SOAPFactoryImpl extends OMFactoryImpl implements SOAPFactoryEx
{
    public SOAPFactoryImpl(final OMMetaFactory metaFactory, final NodeFactory nodeFactory) {
        super(metaFactory, nodeFactory);
    }
    
    protected abstract SOAPHelper getSOAPHelper();
    
    public final String getSoapVersionURI() {
        return this.getSOAPHelper().getEnvelopeURI();
    }
    
    public final SOAPVersion getSOAPVersion() {
        return this.getSOAPHelper().getVersion();
    }
    
    public final OMNamespace getNamespace() {
        return this.getSOAPHelper().getNamespace();
    }
    
    public final <T extends AxiomSOAPElement> T createSOAPElement(final Class<T> type, final OMElement parent, final QName qname, final OMXMLParserWrapper builder) {
        final T element = this.createNode(type);
        if (builder != null) {
            element.coreSetBuilder(builder);
        }
        else if (parent != null) {
            element.checkParent(parent);
        }
        if (parent != null) {
            AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$addChild((AxiomContainer)parent, (OMNode)element, builder != null);
        }
        if (builder != null) {
            AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetLocalName(element, qname.getLocalPart());
        }
        else if (qname.getNamespaceURI().length() == 0) {
            AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$initName(element, qname.getLocalPart(), null, true);
        }
        else if (parent != null) {
            AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$initName(element, qname.getLocalPart(), parent.getNamespace(), false);
        }
        else {
            AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$initName(element, qname.getLocalPart(), this.getNamespace(), true);
        }
        return element;
    }
    
    public final SOAPMessage createSOAPMessage() {
        final AxiomSOAPMessage message = this.createNode(AxiomSOAPMessage.class);
        AxiomSOAPMessageSupport.ajc$interMethodDispatch1$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$initSOAPFactory(message, (SOAPFactory)this);
        return (SOAPMessage)message;
    }
    
    public final SOAPMessage createSOAPMessage(final OMXMLParserWrapper builder) {
        final AxiomSOAPMessage message = this.createNode(AxiomSOAPMessage.class);
        AxiomSOAPMessageSupport.ajc$interMethodDispatch1$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$initSOAPFactory(message, (SOAPFactory)this);
        if (builder != null) {
            message.coreSetBuilder(builder);
        }
        return (SOAPMessage)message;
    }
    
    public final SOAPEnvelope createSOAPEnvelope() {
        return this.createSOAPEnvelope(this.getNamespace());
    }
    
    public final SOAPEnvelope createSOAPEnvelope(final OMNamespace ns) {
        return this.createAxiomElement((Class<SOAPEnvelope>)this.getSOAPHelper().getEnvelopeClass(), null, "Envelope", ns, null, true);
    }
    
    public final SOAPEnvelope createSOAPEnvelope(final SOAPMessage message, final OMXMLParserWrapper builder) {
        return this.createAxiomElement((Class<SOAPEnvelope>)this.getSOAPHelper().getEnvelopeClass(), (OMContainer)message, "Envelope", null, builder, false);
    }
    
    public final SOAPHeader createSOAPHeader(final SOAPEnvelope parent, final OMXMLParserWrapper builder) {
        final SOAPHelper helper = this.getSOAPHelper();
        return this.createSOAPElement((Class<SOAPHeader>)helper.getHeaderClass(), (OMElement)parent, helper.getHeaderQName(), builder);
    }
    
    public final SOAPHeader createSOAPHeader(final SOAPEnvelope parent) {
        return this.createSOAPHeader(parent, null);
    }
    
    public final SOAPHeader createSOAPHeader() {
        return this.createSOAPHeader(null, null);
    }
    
    public final SOAPHeaderBlock createSOAPHeaderBlock(final String localName, final OMNamespace ns, final SOAPHeader parent) {
        return this.createAxiomElement((Class<SOAPHeaderBlock>)this.getSOAPHelper().getHeaderBlockClass(), (OMContainer)parent, localName, ns, null, true);
    }
    
    public final SOAPHeaderBlock createSOAPHeaderBlock(final String localName, final OMNamespace ns) {
        return this.createAxiomElement((Class<SOAPHeaderBlock>)this.getSOAPHelper().getHeaderBlockClass(), null, localName, ns, null, true);
    }
    
    public final SOAPHeaderBlock createSOAPHeaderBlock(final String localName, final SOAPHeader parent, final OMXMLParserWrapper builder) {
        return this.createAxiomElement((Class<SOAPHeaderBlock>)this.getSOAPHelper().getHeaderBlockClass(), (OMContainer)parent, localName, null, builder, false);
    }
    
    public final SOAPHeaderBlock createSOAPHeaderBlock(final OMDataSource source) {
        final AxiomSOAPHeaderBlock element = this.createNode(this.getSOAPHelper().getHeaderBlockClass());
        AxiomSourcedElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$init(element, source);
        return (SOAPHeaderBlock)element;
    }
    
    public final SOAPHeaderBlock createSOAPHeaderBlock(final String localName, final OMNamespace ns, final OMDataSource ds) {
        final AxiomSOAPHeaderBlock element = this.createNode(this.getSOAPHelper().getHeaderBlockClass());
        AxiomSourcedElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$init(element, localName, ns, ds);
        return (SOAPHeaderBlock)element;
    }
    
    public final SOAPBody createSOAPBody(final SOAPEnvelope parent, final OMXMLParserWrapper builder) {
        final SOAPHelper helper = this.getSOAPHelper();
        return this.createSOAPElement((Class<SOAPBody>)helper.getBodyClass(), (OMElement)parent, helper.getBodyQName(), builder);
    }
    
    public final SOAPBody createSOAPBody(final SOAPEnvelope parent) {
        return this.createSOAPBody(parent, null);
    }
    
    public final SOAPBody createSOAPBody() {
        return this.createSOAPBody(null, null);
    }
    
    public final SOAPFault createSOAPFault(final SOAPBody parent, final OMXMLParserWrapper builder) {
        final SOAPHelper helper = this.getSOAPHelper();
        return this.createSOAPElement((Class<SOAPFault>)helper.getFaultClass(), (OMElement)parent, helper.getFaultQName(), builder);
    }
    
    public final SOAPFault createSOAPFault(final SOAPBody parent) {
        return this.createSOAPFault(parent, (OMXMLParserWrapper)null);
    }
    
    public final SOAPFault createSOAPFault() {
        return this.createSOAPFault(null, (OMXMLParserWrapper)null);
    }
    
    public final SOAPFault createSOAPFault(final SOAPBody parent, final Exception e) {
        final SOAPFault fault = this.createSOAPFault(parent, (OMXMLParserWrapper)null);
        fault.setException(e);
        return fault;
    }
    
    public final SOAPFaultCode createSOAPFaultCode(final SOAPFault parent, final OMXMLParserWrapper builder) {
        final SOAPHelper helper = this.getSOAPHelper();
        return this.createSOAPElement((Class<SOAPFaultCode>)helper.getFaultCodeClass(), (OMElement)parent, helper.getFaultCodeQName(), builder);
    }
    
    public final SOAPFaultCode createSOAPFaultCode(final SOAPFault parent) {
        return this.createSOAPFaultCode(parent, null);
    }
    
    public final SOAPFaultCode createSOAPFaultCode() {
        return this.createSOAPFaultCode(null, null);
    }
    
    public final SOAPFaultReason createSOAPFaultReason(final SOAPFault parent, final OMXMLParserWrapper builder) {
        final SOAPHelper helper = this.getSOAPHelper();
        return this.createSOAPElement((Class<SOAPFaultReason>)helper.getFaultReasonClass(), (OMElement)parent, helper.getFaultReasonQName(), builder);
    }
    
    public final SOAPFaultReason createSOAPFaultReason(final SOAPFault parent) {
        return this.createSOAPFaultReason(parent, null);
    }
    
    public final SOAPFaultReason createSOAPFaultReason() {
        return this.createSOAPFaultReason(null, null);
    }
    
    public final SOAPFaultRole createSOAPFaultRole(final SOAPFault parent, final OMXMLParserWrapper builder) {
        final SOAPHelper helper = this.getSOAPHelper();
        return this.createSOAPElement((Class<SOAPFaultRole>)helper.getFaultRoleClass(), (OMElement)parent, helper.getFaultRoleQName(), builder);
    }
    
    public final SOAPFaultRole createSOAPFaultRole(final SOAPFault parent) {
        return this.createSOAPFaultRole(parent, null);
    }
    
    public final SOAPFaultRole createSOAPFaultRole() {
        return this.createSOAPFaultRole(null, null);
    }
    
    public final SOAPFaultDetail createSOAPFaultDetail(final SOAPFault parent, final OMXMLParserWrapper builder) {
        final SOAPHelper helper = this.getSOAPHelper();
        return this.createSOAPElement((Class<SOAPFaultDetail>)helper.getFaultDetailClass(), (OMElement)parent, helper.getFaultDetailQName(), builder);
    }
    
    public final SOAPFaultDetail createSOAPFaultDetail(final SOAPFault parent) {
        return this.createSOAPFaultDetail(parent, null);
    }
    
    public final SOAPFaultDetail createSOAPFaultDetail() {
        return this.createSOAPFaultDetail(null, null);
    }
    
    public final SOAPMessage createDefaultSOAPMessage() {
        final SOAPMessage message = this.createSOAPMessage();
        final SOAPEnvelope env = this.createSOAPEnvelope();
        message.addChild((OMNode)env);
        this.createSOAPBody(env);
        return message;
    }
    
    public final SOAPEnvelope getDefaultEnvelope() {
        final SOAPEnvelope env = this.createSOAPEnvelope();
        this.createSOAPHeader(env);
        this.createSOAPBody(env);
        return env;
    }
}
