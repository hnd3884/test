package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import org.xml.sax.SAXException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.internal.ws.policy.PolicyMap;
import java.util.Map;
import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;

public interface WSDLModel extends WSDLExtensible
{
    WSDLPortType getPortType(@NotNull final QName p0);
    
    WSDLBoundPortType getBinding(@NotNull final QName p0);
    
    WSDLBoundPortType getBinding(@NotNull final QName p0, @NotNull final QName p1);
    
    WSDLService getService(@NotNull final QName p0);
    
    @NotNull
    Map<QName, ? extends WSDLPortType> getPortTypes();
    
    @NotNull
    Map<QName, ? extends WSDLBoundPortType> getBindings();
    
    @NotNull
    Map<QName, ? extends WSDLService> getServices();
    
    QName getFirstServiceName();
    
    WSDLMessage getMessage(final QName p0);
    
    @NotNull
    Map<QName, ? extends WSDLMessage> getMessages();
    
    @Deprecated
    PolicyMap getPolicyMap();
    
    public static class WSDLParser
    {
        @NotNull
        public static WSDLModel parse(final XMLEntityResolver.Parser wsdlEntityParser, final XMLEntityResolver resolver, final boolean isClientSide, final WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
            return parse(wsdlEntityParser, resolver, isClientSide, Container.NONE, extensions);
        }
        
        @NotNull
        public static WSDLModel parse(final XMLEntityResolver.Parser wsdlEntityParser, final XMLEntityResolver resolver, final boolean isClientSide, @NotNull final Container container, final WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
            return parse(wsdlEntityParser, resolver, isClientSide, container, PolicyResolverFactory.create(), extensions);
        }
        
        @NotNull
        public static WSDLModel parse(final XMLEntityResolver.Parser wsdlEntityParser, final XMLEntityResolver resolver, final boolean isClientSide, @NotNull final Container container, final PolicyResolver policyResolver, final WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
            return RuntimeWSDLParser.parse(wsdlEntityParser, resolver, isClientSide, container, policyResolver, extensions);
        }
    }
}
