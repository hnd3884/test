package com.sun.xml.internal.ws.fault;

import javax.xml.soap.Detail;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.WebServiceException;
import javax.xml.soap.SOAPFault;
import java.util.Iterator;
import org.w3c.dom.Node;
import com.sun.xml.internal.ws.util.DOMUtil;
import org.w3c.dom.Element;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Fault", namespace = "http://www.w3.org/2003/05/soap-envelope")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "code", "reason", "node", "role", "detail" })
class SOAP12Fault extends SOAPFaultBuilder
{
    @XmlTransient
    private static final String ns = "http://www.w3.org/2003/05/soap-envelope";
    @XmlElement(namespace = "http://www.w3.org/2003/05/soap-envelope", name = "Code")
    private CodeType code;
    @XmlElement(namespace = "http://www.w3.org/2003/05/soap-envelope", name = "Reason")
    private ReasonType reason;
    @XmlElement(namespace = "http://www.w3.org/2003/05/soap-envelope", name = "Node")
    private String node;
    @XmlElement(namespace = "http://www.w3.org/2003/05/soap-envelope", name = "Role")
    private String role;
    @XmlElement(namespace = "http://www.w3.org/2003/05/soap-envelope", name = "Detail")
    private DetailType detail;
    
    SOAP12Fault() {
    }
    
    SOAP12Fault(final CodeType code, final ReasonType reason, final String node, final String role, final DetailType detail) {
        this.code = code;
        this.reason = reason;
        this.node = node;
        this.role = role;
        this.detail = detail;
    }
    
    SOAP12Fault(final CodeType code, final ReasonType reason, final String node, final String role, final Element detailObject) {
        this.code = code;
        this.reason = reason;
        this.node = node;
        this.role = role;
        if (detailObject != null) {
            if (detailObject.getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope") && detailObject.getLocalName().equals("Detail")) {
                this.detail = new DetailType();
                for (final Element detailEntry : DOMUtil.getChildElements(detailObject)) {
                    this.detail.getDetails().add(detailEntry);
                }
            }
            else {
                this.detail = new DetailType(detailObject);
            }
        }
    }
    
    SOAP12Fault(final SOAPFault fault) {
        this.code = new CodeType(fault.getFaultCodeAsQName());
        try {
            this.fillFaultSubCodes(fault);
        }
        catch (final SOAPException e) {
            throw new WebServiceException(e);
        }
        this.reason = new ReasonType(fault.getFaultString());
        this.role = fault.getFaultRole();
        this.node = fault.getFaultNode();
        if (fault.getDetail() != null) {
            this.detail = new DetailType();
            final Iterator iter = fault.getDetail().getDetailEntries();
            while (iter.hasNext()) {
                final Element fd = iter.next();
                this.detail.getDetails().add(fd);
            }
        }
    }
    
    SOAP12Fault(final QName code, final String reason, final Element detailObject) {
        this(new CodeType(code), new ReasonType(reason), null, null, detailObject);
    }
    
    CodeType getCode() {
        return this.code;
    }
    
    ReasonType getReason() {
        return this.reason;
    }
    
    String getNode() {
        return this.node;
    }
    
    String getRole() {
        return this.role;
    }
    
    @Override
    DetailType getDetail() {
        return this.detail;
    }
    
    @Override
    void setDetail(final DetailType detail) {
        this.detail = detail;
    }
    
    @Override
    String getFaultString() {
        return this.reason.texts().get(0).getText();
    }
    
    @Override
    protected Throwable getProtocolException() {
        try {
            final SOAPFault fault = SOAPVersion.SOAP_12.getSOAPFactory().createFault();
            if (this.reason != null) {
                for (final TextType tt : this.reason.texts()) {
                    fault.setFaultString(tt.getText());
                }
            }
            if (this.code != null) {
                fault.setFaultCode(this.code.getValue());
                this.fillFaultSubCodes(fault, this.code.getSubcode());
            }
            if (this.detail != null && this.detail.getDetail(0) != null) {
                final Detail detail = fault.addDetail();
                for (final Node obj : this.detail.getDetails()) {
                    final Node n = fault.getOwnerDocument().importNode(obj, true);
                    detail.appendChild(n);
                }
            }
            if (this.node != null) {
                fault.setFaultNode(this.node);
            }
            return new ServerSOAPFaultException(fault);
        }
        catch (final SOAPException e) {
            throw new WebServiceException(e);
        }
    }
    
    private void fillFaultSubCodes(final SOAPFault fault, final SubcodeType subcode) throws SOAPException {
        if (subcode != null) {
            fault.appendFaultSubcode(subcode.getValue());
            this.fillFaultSubCodes(fault, subcode.getSubcode());
        }
    }
    
    private void fillFaultSubCodes(final SOAPFault fault) throws SOAPException {
        final Iterator subcodes = fault.getFaultSubcodes();
        SubcodeType firstSct = null;
        while (subcodes.hasNext()) {
            final QName subcode = subcodes.next();
            if (firstSct == null) {
                firstSct = new SubcodeType(subcode);
                this.code.setSubcode(firstSct);
            }
            else {
                final SubcodeType nextSct = new SubcodeType(subcode);
                firstSct.setSubcode(nextSct);
                firstSct = nextSct;
            }
        }
    }
}
