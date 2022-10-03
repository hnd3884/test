package com.sun.xml.internal.ws.wsdl.writer;

import javax.xml.ws.FaultAction;
import com.sun.xml.internal.ws.api.model.CheckedException;
import java.net.URISyntaxException;
import java.net.URI;
import javax.xml.ws.Action;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.AddressingFeature;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;

public class W3CAddressingWSDLGeneratorExtension extends WSDLGeneratorExtension
{
    private boolean enabled;
    private boolean required;
    private static final Logger LOGGER;
    
    public W3CAddressingWSDLGeneratorExtension() {
        this.required = false;
    }
    
    @Override
    public void start(final WSDLGenExtnContext ctxt) {
        final WSBinding binding = ctxt.getBinding();
        final TypedXmlWriter root = ctxt.getRoot();
        if (!(this.enabled = binding.isFeatureEnabled(AddressingFeature.class))) {
            return;
        }
        final AddressingFeature ftr = binding.getFeature(AddressingFeature.class);
        this.required = ftr.isRequired();
        root._namespace(AddressingVersion.W3C.wsdlNsUri, AddressingVersion.W3C.getWsdlPrefix());
    }
    
    @Override
    public void addOperationInputExtension(final TypedXmlWriter input, final JavaMethod method) {
        if (!this.enabled) {
            return;
        }
        final Action a = method.getSEIMethod().getAnnotation(Action.class);
        if (a != null && !a.input().equals("")) {
            this.addAttribute(input, a.input());
        }
        else {
            final String soapAction = method.getBinding().getSOAPAction();
            if (soapAction == null || soapAction.equals("")) {
                final String defaultAction = getDefaultAction(method);
                this.addAttribute(input, defaultAction);
            }
        }
    }
    
    protected static final String getDefaultAction(final JavaMethod method) {
        String tns = method.getOwner().getTargetNamespace();
        String delim = "/";
        try {
            final URI uri = new URI(tns);
            if (uri.getScheme().equalsIgnoreCase("urn")) {
                delim = ":";
            }
        }
        catch (final URISyntaxException e) {
            W3CAddressingWSDLGeneratorExtension.LOGGER.warning("TargetNamespace of WebService is not a valid URI");
        }
        if (tns.endsWith(delim)) {
            tns = tns.substring(0, tns.length() - 1);
        }
        final String name = method.getMEP().isOneWay() ? method.getOperationName() : (method.getOperationName() + "Request");
        return tns + delim + method.getOwner().getPortTypeName().getLocalPart() + delim + name;
    }
    
    @Override
    public void addOperationOutputExtension(final TypedXmlWriter output, final JavaMethod method) {
        if (!this.enabled) {
            return;
        }
        final Action a = method.getSEIMethod().getAnnotation(Action.class);
        if (a != null && !a.output().equals("")) {
            this.addAttribute(output, a.output());
        }
    }
    
    @Override
    public void addOperationFaultExtension(final TypedXmlWriter fault, final JavaMethod method, final CheckedException ce) {
        if (!this.enabled) {
            return;
        }
        final Action a = method.getSEIMethod().getAnnotation(Action.class);
        final Class[] exs = method.getSEIMethod().getExceptionTypes();
        if (exs == null) {
            return;
        }
        if (a != null && a.fault() != null) {
            final FaultAction[] fault2 = a.fault();
            final int length = fault2.length;
            int i = 0;
            while (i < length) {
                final FaultAction fa = fault2[i];
                if (fa.className().getName().equals(ce.getExceptionClass().getName())) {
                    if (fa.value().equals("")) {
                        return;
                    }
                    this.addAttribute(fault, fa.value());
                }
                else {
                    ++i;
                }
            }
        }
    }
    
    private void addAttribute(final TypedXmlWriter writer, final String attrValue) {
        writer._attribute(AddressingVersion.W3C.wsdlActionTag, attrValue);
    }
    
    @Override
    public void addBindingExtension(final TypedXmlWriter binding) {
        if (!this.enabled) {
            return;
        }
        binding._element(AddressingVersion.W3C.wsdlExtensionTag, UsingAddressing.class);
    }
    
    static {
        LOGGER = Logger.getLogger(W3CAddressingWSDLGeneratorExtension.class.getName());
    }
}
