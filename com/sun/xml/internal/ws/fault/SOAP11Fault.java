package com.sun.xml.internal.ws.fault;

import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.soap.SOAPFault;
import java.util.Iterator;
import org.w3c.dom.Node;
import com.sun.xml.internal.ws.util.DOMUtil;
import org.w3c.dom.Element;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "faultcode", "faultstring", "faultactor", "detail" })
@XmlRootElement(name = "Fault", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
class SOAP11Fault extends SOAPFaultBuilder
{
    @XmlElement(namespace = "")
    private QName faultcode;
    @XmlElement(namespace = "")
    private String faultstring;
    @XmlElement(namespace = "")
    private String faultactor;
    @XmlElement(namespace = "")
    private DetailType detail;
    
    SOAP11Fault() {
    }
    
    SOAP11Fault(final QName code, final String reason, final String actor, final Element detailObject) {
        this.faultcode = code;
        this.faultstring = reason;
        this.faultactor = actor;
        if (detailObject != null) {
            if ((detailObject.getNamespaceURI() == null || "".equals(detailObject.getNamespaceURI())) && "detail".equals(detailObject.getLocalName())) {
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
    
    SOAP11Fault(final SOAPFault fault) {
        this.faultcode = fault.getFaultCodeAsQName();
        this.faultstring = fault.getFaultString();
        this.faultactor = fault.getFaultActor();
        if (fault.getDetail() != null) {
            this.detail = new DetailType();
            final Iterator iter = fault.getDetail().getDetailEntries();
            while (iter.hasNext()) {
                final Element fd = iter.next();
                this.detail.getDetails().add(fd);
            }
        }
    }
    
    QName getFaultcode() {
        return this.faultcode;
    }
    
    void setFaultcode(final QName faultcode) {
        this.faultcode = faultcode;
    }
    
    @Override
    String getFaultString() {
        return this.faultstring;
    }
    
    void setFaultstring(final String faultstring) {
        this.faultstring = faultstring;
    }
    
    String getFaultactor() {
        return this.faultactor;
    }
    
    void setFaultactor(final String faultactor) {
        this.faultactor = faultactor;
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
    protected Throwable getProtocolException() {
        try {
            final SOAPFault fault = SOAPVersion.SOAP_11.getSOAPFactory().createFault(this.faultstring, this.faultcode);
            fault.setFaultActor(this.faultactor);
            if (this.detail != null) {
                final Detail d = fault.addDetail();
                for (final Element det : this.detail.getDetails()) {
                    final Node n = fault.getOwnerDocument().importNode(det, true);
                    d.appendChild(n);
                }
            }
            return new ServerSOAPFaultException(fault);
        }
        catch (final SOAPException e) {
            throw new WebServiceException(e);
        }
    }
}
