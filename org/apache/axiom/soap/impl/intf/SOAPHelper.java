package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.om.OMMetaFactory;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPVersion;

public abstract class SOAPHelper
{
    public static final SOAPHelper SOAP11;
    public static final SOAPHelper SOAP12;
    private final SOAPVersion version;
    private final OMNamespace namespace;
    private final String specName;
    private final Class<? extends AxiomSOAPEnvelope> envelopeClass;
    private final Class<? extends AxiomSOAPHeader> headerClass;
    private final QName headerQName;
    private final Class<? extends AxiomSOAPHeaderBlock> headerBlockClass;
    private final Class<? extends AxiomSOAPBody> bodyClass;
    private final QName bodyQName;
    private final Class<? extends AxiomSOAPFault> faultClass;
    private final QName faultQName;
    private final Class<? extends AxiomSOAPFaultCode> faultCodeClass;
    private final Class<? extends AxiomSOAPFaultReason> faultReasonClass;
    private final Class<? extends AxiomSOAPFaultRole> faultRoleClass;
    private final Class<? extends AxiomSOAPFaultDetail> faultDetailClass;
    private final QName mustUnderstandAttributeQName;
    private final QName roleAttributeQName;
    private final QName relayAttributeQName;
    
    static {
        SOAP11 = new SOAPHelper("SOAP 1.1", (Class)AxiomSOAP11Envelope.class, (Class)AxiomSOAP11Header.class, (Class)AxiomSOAP11HeaderBlock.class, (Class)AxiomSOAP11Body.class, (Class)AxiomSOAP11Fault.class, (Class)AxiomSOAP11FaultCode.class, (Class)AxiomSOAP11FaultReason.class, (Class)AxiomSOAP11FaultRole.class, (Class)AxiomSOAP11FaultDetail.class, "actor", (String)null) {
            @Override
            public SOAPFactory getSOAPFactory(final OMMetaFactory metaFactory) {
                return metaFactory.getSOAP11Factory();
            }
            
            @Override
            public Boolean parseBoolean(final String literal) {
                if (literal.equals("1")) {
                    return Boolean.TRUE;
                }
                if (literal.equals("0")) {
                    return Boolean.FALSE;
                }
                return null;
            }
            
            @Override
            public String formatBoolean(final boolean value) {
                return value ? "1" : "0";
            }
        };
        SOAP12 = new SOAPHelper("SOAP 1.2", (Class)AxiomSOAP12Envelope.class, (Class)AxiomSOAP12Header.class, (Class)AxiomSOAP12HeaderBlock.class, (Class)AxiomSOAP12Body.class, (Class)AxiomSOAP12Fault.class, (Class)AxiomSOAP12FaultCode.class, (Class)AxiomSOAP12FaultReason.class, (Class)AxiomSOAP12FaultRole.class, (Class)AxiomSOAP12FaultDetail.class, "role", "relay") {
            @Override
            public SOAPFactory getSOAPFactory(final OMMetaFactory metaFactory) {
                return metaFactory.getSOAP12Factory();
            }
            
            @Override
            public Boolean parseBoolean(final String literal) {
                if (literal.equals("true") || literal.equals("1")) {
                    return Boolean.TRUE;
                }
                if (literal.equals("false") || literal.equals("0")) {
                    return Boolean.FALSE;
                }
                return null;
            }
            
            @Override
            public String formatBoolean(final boolean value) {
                return String.valueOf(value);
            }
        };
    }
    
    private SOAPHelper(final SOAPVersion version, final String specName, final Class<? extends AxiomSOAPEnvelope> envelopeClass, final Class<? extends AxiomSOAPHeader> headerClass, final Class<? extends AxiomSOAPHeaderBlock> headerBlockClass, final Class<? extends AxiomSOAPBody> bodyClass, final Class<? extends AxiomSOAPFault> faultClass, final Class<? extends AxiomSOAPFaultCode> faultCodeClass, final Class<? extends AxiomSOAPFaultReason> faultReasonClass, final Class<? extends AxiomSOAPFaultRole> faultRoleClass, final Class<? extends AxiomSOAPFaultDetail> faultDetailClass, final String roleAttributeLocalName, final String relayAttributeLocalName) {
        this.version = version;
        this.namespace = (OMNamespace)new OMNamespaceImpl(version.getEnvelopeURI(), "soapenv");
        this.specName = specName;
        this.envelopeClass = envelopeClass;
        this.headerClass = headerClass;
        this.headerQName = new QName(version.getEnvelopeURI(), "Header", "soapenv");
        this.headerBlockClass = headerBlockClass;
        this.bodyClass = bodyClass;
        this.bodyQName = new QName(version.getEnvelopeURI(), "Body", "soapenv");
        this.faultClass = faultClass;
        this.faultQName = new QName(version.getEnvelopeURI(), "Fault", "soapenv");
        this.faultCodeClass = faultCodeClass;
        this.faultReasonClass = faultReasonClass;
        this.faultRoleClass = faultRoleClass;
        this.faultDetailClass = faultDetailClass;
        this.mustUnderstandAttributeQName = new QName(version.getEnvelopeURI(), "mustUnderstand", "soapenv");
        this.roleAttributeQName = new QName(version.getEnvelopeURI(), roleAttributeLocalName, "soapenv");
        this.relayAttributeQName = ((relayAttributeLocalName == null) ? null : new QName(version.getEnvelopeURI(), relayAttributeLocalName, "soapenv"));
    }
    
    public final SOAPVersion getVersion() {
        return this.version;
    }
    
    public abstract SOAPFactory getSOAPFactory(final OMMetaFactory p0);
    
    public final String getEnvelopeURI() {
        return this.version.getEnvelopeURI();
    }
    
    public final OMNamespace getNamespace() {
        return this.namespace;
    }
    
    public final String getSpecName() {
        return this.specName;
    }
    
    public final Class<? extends AxiomSOAPEnvelope> getEnvelopeClass() {
        return this.envelopeClass;
    }
    
    public final Class<? extends AxiomSOAPHeader> getHeaderClass() {
        return this.headerClass;
    }
    
    public final QName getHeaderQName() {
        return this.headerQName;
    }
    
    public final Class<? extends AxiomSOAPHeaderBlock> getHeaderBlockClass() {
        return this.headerBlockClass;
    }
    
    public final Class<? extends AxiomSOAPBody> getBodyClass() {
        return this.bodyClass;
    }
    
    public final QName getBodyQName() {
        return this.bodyQName;
    }
    
    public final Class<? extends AxiomSOAPFault> getFaultClass() {
        return this.faultClass;
    }
    
    public final QName getFaultQName() {
        return this.faultQName;
    }
    
    public final Class<? extends AxiomSOAPFaultCode> getFaultCodeClass() {
        return this.faultCodeClass;
    }
    
    public final QName getFaultCodeQName() {
        return this.version.getFaultCodeQName();
    }
    
    public final Class<? extends AxiomSOAPFaultReason> getFaultReasonClass() {
        return this.faultReasonClass;
    }
    
    public final QName getFaultReasonQName() {
        return this.version.getFaultReasonQName();
    }
    
    public final Class<? extends AxiomSOAPFaultRole> getFaultRoleClass() {
        return this.faultRoleClass;
    }
    
    public final QName getFaultRoleQName() {
        return this.version.getFaultRoleQName();
    }
    
    public final Class<? extends AxiomSOAPFaultDetail> getFaultDetailClass() {
        return this.faultDetailClass;
    }
    
    public final QName getFaultDetailQName() {
        return this.version.getFaultDetailQName();
    }
    
    public final QName getMustUnderstandAttributeQName() {
        return this.mustUnderstandAttributeQName;
    }
    
    public final QName getRoleAttributeQName() {
        return this.roleAttributeQName;
    }
    
    public final QName getRelayAttributeQName() {
        return this.relayAttributeQName;
    }
    
    public abstract Boolean parseBoolean(final String p0);
    
    public abstract String formatBoolean(final boolean p0);
}
