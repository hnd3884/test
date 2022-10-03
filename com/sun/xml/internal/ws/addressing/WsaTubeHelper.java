package com.sun.xml.internal.ws.addressing;

import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import javax.xml.soap.SOAPFactory;
import org.w3c.dom.Element;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPFault;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.istack.internal.Nullable;
import java.util.Iterator;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.SEIModel;

public abstract class WsaTubeHelper
{
    protected SEIModel seiModel;
    protected WSDLPort wsdlPort;
    protected WSBinding binding;
    protected final SOAPVersion soapVer;
    protected final AddressingVersion addVer;
    
    public WsaTubeHelper(final WSBinding binding, final SEIModel seiModel, final WSDLPort wsdlPort) {
        this.binding = binding;
        this.wsdlPort = wsdlPort;
        this.seiModel = seiModel;
        this.soapVer = binding.getSOAPVersion();
        this.addVer = binding.getAddressingVersion();
    }
    
    public String getFaultAction(final Packet requestPacket, final Packet responsePacket) {
        String action = null;
        if (this.seiModel != null) {
            action = this.getFaultActionFromSEIModel(requestPacket, responsePacket);
        }
        if (action != null) {
            return action;
        }
        action = this.addVer.getDefaultFaultAction();
        if (this.wsdlPort != null) {
            final WSDLOperationMapping wsdlOp = requestPacket.getWSDLOperationMapping();
            if (wsdlOp != null) {
                final WSDLBoundOperation wbo = wsdlOp.getWSDLBoundOperation();
                return this.getFaultAction(wbo, responsePacket);
            }
        }
        return action;
    }
    
    String getFaultActionFromSEIModel(final Packet requestPacket, final Packet responsePacket) {
        final String action = null;
        if (this.seiModel == null || this.wsdlPort == null) {
            return action;
        }
        try {
            final SOAPMessage sm = responsePacket.getMessage().copy().readAsSOAPMessage();
            if (sm == null) {
                return action;
            }
            if (sm.getSOAPBody() == null) {
                return action;
            }
            if (sm.getSOAPBody().getFault() == null) {
                return action;
            }
            final Detail detail = sm.getSOAPBody().getFault().getDetail();
            if (detail == null) {
                return action;
            }
            final String ns = detail.getFirstChild().getNamespaceURI();
            final String name = detail.getFirstChild().getLocalName();
            final WSDLOperationMapping wsdlOp = requestPacket.getWSDLOperationMapping();
            final JavaMethodImpl jm = (wsdlOp != null) ? ((JavaMethodImpl)wsdlOp.getJavaMethod()) : null;
            if (jm != null) {
                for (final CheckedExceptionImpl ce : jm.getCheckedExceptions()) {
                    if (ce.getDetailType().tagName.getLocalPart().equals(name) && ce.getDetailType().tagName.getNamespaceURI().equals(ns)) {
                        return ce.getFaultAction();
                    }
                }
            }
            return action;
        }
        catch (final SOAPException e) {
            throw new WebServiceException(e);
        }
    }
    
    String getFaultAction(@Nullable final WSDLBoundOperation wbo, final Packet responsePacket) {
        String action = AddressingUtils.getAction(responsePacket.getMessage().getHeaders(), this.addVer, this.soapVer);
        if (action != null) {
            return action;
        }
        action = this.addVer.getDefaultFaultAction();
        if (wbo == null) {
            return action;
        }
        try {
            final SOAPMessage sm = responsePacket.getMessage().copy().readAsSOAPMessage();
            if (sm == null) {
                return action;
            }
            if (sm.getSOAPBody() == null) {
                return action;
            }
            if (sm.getSOAPBody().getFault() == null) {
                return action;
            }
            final Detail detail = sm.getSOAPBody().getFault().getDetail();
            if (detail == null) {
                return action;
            }
            final String ns = detail.getFirstChild().getNamespaceURI();
            final String name = detail.getFirstChild().getLocalName();
            final WSDLOperation o = wbo.getOperation();
            final WSDLFault fault = o.getFault(new QName(ns, name));
            if (fault == null) {
                return action;
            }
            action = fault.getAction();
            return action;
        }
        catch (final SOAPException e) {
            throw new WebServiceException(e);
        }
    }
    
    public String getInputAction(final Packet packet) {
        String action = null;
        if (this.wsdlPort != null) {
            final WSDLOperationMapping wsdlOp = packet.getWSDLOperationMapping();
            if (wsdlOp != null) {
                final WSDLBoundOperation wbo = wsdlOp.getWSDLBoundOperation();
                final WSDLOperation op = wbo.getOperation();
                action = op.getInput().getAction();
            }
        }
        return action;
    }
    
    public String getEffectiveInputAction(final Packet packet) {
        if (packet.soapAction != null && !packet.soapAction.equals("")) {
            return packet.soapAction;
        }
        String action;
        if (this.wsdlPort != null) {
            final WSDLOperationMapping wsdlOp = packet.getWSDLOperationMapping();
            if (wsdlOp != null) {
                final WSDLBoundOperation wbo = wsdlOp.getWSDLBoundOperation();
                final WSDLOperation op = wbo.getOperation();
                action = op.getInput().getAction();
            }
            else {
                action = packet.soapAction;
            }
        }
        else {
            action = packet.soapAction;
        }
        return action;
    }
    
    public boolean isInputActionDefault(final Packet packet) {
        if (this.wsdlPort == null) {
            return false;
        }
        final WSDLOperationMapping wsdlOp = packet.getWSDLOperationMapping();
        if (wsdlOp == null) {
            return false;
        }
        final WSDLBoundOperation wbo = wsdlOp.getWSDLBoundOperation();
        final WSDLOperation op = wbo.getOperation();
        return op.getInput().isDefaultAction();
    }
    
    public String getSOAPAction(final Packet packet) {
        String action = "";
        if (packet == null || packet.getMessage() == null) {
            return action;
        }
        if (this.wsdlPort == null) {
            return action;
        }
        final WSDLOperationMapping wsdlOp = packet.getWSDLOperationMapping();
        if (wsdlOp == null) {
            return action;
        }
        final WSDLBoundOperation op = wsdlOp.getWSDLBoundOperation();
        action = op.getSOAPAction();
        return action;
    }
    
    public String getOutputAction(final Packet packet) {
        final String action = null;
        final WSDLOperationMapping wsdlOp = packet.getWSDLOperationMapping();
        if (wsdlOp != null) {
            final JavaMethod javaMethod = wsdlOp.getJavaMethod();
            if (javaMethod != null) {
                final JavaMethodImpl jm = (JavaMethodImpl)javaMethod;
                if (jm != null && jm.getOutputAction() != null && !jm.getOutputAction().equals("")) {
                    return jm.getOutputAction();
                }
            }
            final WSDLBoundOperation wbo = wsdlOp.getWSDLBoundOperation();
            if (wbo != null) {
                return this.getOutputAction(wbo);
            }
        }
        return action;
    }
    
    String getOutputAction(@Nullable final WSDLBoundOperation wbo) {
        String action = "http://jax-ws.dev.java.net/addressing/output-action-not-set";
        if (wbo != null) {
            final WSDLOutput op = wbo.getOperation().getOutput();
            if (op != null) {
                action = op.getAction();
            }
        }
        return action;
    }
    
    public SOAPFault createInvalidAddressingHeaderFault(final InvalidAddressingHeaderException e, final AddressingVersion av) {
        final QName name = e.getProblemHeader();
        final QName subsubcode = e.getSubsubcode();
        final QName subcode = av.invalidMapTag;
        final String faultstring = String.format(av.getInvalidMapText(), name, subsubcode);
        try {
            SOAPFault fault;
            if (this.soapVer == SOAPVersion.SOAP_12) {
                final SOAPFactory factory = SOAPVersion.SOAP_12.getSOAPFactory();
                fault = factory.createFault();
                fault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
                fault.appendFaultSubcode(subcode);
                fault.appendFaultSubcode(subsubcode);
                this.getInvalidMapDetail(name, fault.addDetail());
            }
            else {
                final SOAPFactory factory = SOAPVersion.SOAP_11.getSOAPFactory();
                fault = factory.createFault();
                fault.setFaultCode(subsubcode);
            }
            fault.setFaultString(faultstring);
            return fault;
        }
        catch (final SOAPException se) {
            throw new WebServiceException(se);
        }
    }
    
    public SOAPFault newMapRequiredFault(final MissingAddressingHeaderException e) {
        final QName subcode = this.addVer.mapRequiredTag;
        final QName subsubcode = this.addVer.mapRequiredTag;
        final String faultstring = this.addVer.getMapRequiredText();
        try {
            SOAPFault fault;
            if (this.soapVer == SOAPVersion.SOAP_12) {
                final SOAPFactory factory = SOAPVersion.SOAP_12.getSOAPFactory();
                fault = factory.createFault();
                fault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
                fault.appendFaultSubcode(subcode);
                fault.appendFaultSubcode(subsubcode);
                this.getMapRequiredDetail(e.getMissingHeaderQName(), fault.addDetail());
            }
            else {
                final SOAPFactory factory = SOAPVersion.SOAP_11.getSOAPFactory();
                fault = factory.createFault();
                fault.setFaultCode(subsubcode);
            }
            fault.setFaultString(faultstring);
            return fault;
        }
        catch (final SOAPException se) {
            throw new WebServiceException(se);
        }
    }
    
    public abstract void getProblemActionDetail(final String p0, final Element p1);
    
    public abstract void getInvalidMapDetail(final QName p0, final Element p1);
    
    public abstract void getMapRequiredDetail(final QName p0, final Element p1);
}
