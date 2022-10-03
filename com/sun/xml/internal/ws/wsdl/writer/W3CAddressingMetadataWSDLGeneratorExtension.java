package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.xml.internal.ws.addressing.WsaActionUtil;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import java.net.URISyntaxException;
import java.net.URI;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;

public class W3CAddressingMetadataWSDLGeneratorExtension extends WSDLGeneratorExtension
{
    private static final Logger LOGGER;
    
    @Override
    public void start(final WSDLGenExtnContext ctxt) {
        final TypedXmlWriter root = ctxt.getRoot();
        root._namespace("http://www.w3.org/2007/05/addressing/metadata", "wsam");
    }
    
    @Override
    public void addOperationInputExtension(final TypedXmlWriter input, final JavaMethod method) {
        input._attribute(W3CAddressingMetadataConstants.WSAM_ACTION_QNAME, getInputAction(method));
    }
    
    @Override
    public void addOperationOutputExtension(final TypedXmlWriter output, final JavaMethod method) {
        output._attribute(W3CAddressingMetadataConstants.WSAM_ACTION_QNAME, getOutputAction(method));
    }
    
    @Override
    public void addOperationFaultExtension(final TypedXmlWriter fault, final JavaMethod method, final CheckedException ce) {
        fault._attribute(W3CAddressingMetadataConstants.WSAM_ACTION_QNAME, getFaultAction(method, ce));
    }
    
    private static final String getInputAction(final JavaMethod method) {
        String inputaction = ((JavaMethodImpl)method).getInputAction();
        if (inputaction.equals("")) {
            inputaction = getDefaultInputAction(method);
        }
        return inputaction;
    }
    
    protected static final String getDefaultInputAction(final JavaMethod method) {
        String tns = method.getOwner().getTargetNamespace();
        final String delim = getDelimiter(tns);
        if (tns.endsWith(delim)) {
            tns = tns.substring(0, tns.length() - 1);
        }
        final String name = method.getMEP().isOneWay() ? method.getOperationName() : (method.getOperationName() + "Request");
        return tns + delim + method.getOwner().getPortTypeName().getLocalPart() + delim + name;
    }
    
    private static final String getOutputAction(final JavaMethod method) {
        String outputaction = ((JavaMethodImpl)method).getOutputAction();
        if (outputaction.equals("")) {
            outputaction = getDefaultOutputAction(method);
        }
        return outputaction;
    }
    
    protected static final String getDefaultOutputAction(final JavaMethod method) {
        String tns = method.getOwner().getTargetNamespace();
        final String delim = getDelimiter(tns);
        if (tns.endsWith(delim)) {
            tns = tns.substring(0, tns.length() - 1);
        }
        final String name = method.getOperationName() + "Response";
        return tns + delim + method.getOwner().getPortTypeName().getLocalPart() + delim + name;
    }
    
    private static final String getDelimiter(final String tns) {
        String delim = "/";
        try {
            final URI uri = new URI(tns);
            if (uri.getScheme() != null && uri.getScheme().equalsIgnoreCase("urn")) {
                delim = ":";
            }
        }
        catch (final URISyntaxException e) {
            W3CAddressingMetadataWSDLGeneratorExtension.LOGGER.warning("TargetNamespace of WebService is not a valid URI");
        }
        return delim;
    }
    
    private static final String getFaultAction(final JavaMethod method, final CheckedException ce) {
        String faultaction = ((CheckedExceptionImpl)ce).getFaultAction();
        if (faultaction.equals("")) {
            faultaction = getDefaultFaultAction(method, ce);
        }
        return faultaction;
    }
    
    protected static final String getDefaultFaultAction(final JavaMethod method, final CheckedException ce) {
        return WsaActionUtil.getDefaultFaultAction(method, ce);
    }
    
    static {
        LOGGER = Logger.getLogger(W3CAddressingMetadataWSDLGeneratorExtension.class.getName());
    }
}
