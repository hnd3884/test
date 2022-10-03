package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.Nullable;
import org.w3c.dom.NamedNodeMap;
import java.util.Map;
import org.w3c.dom.Document;
import java.util.HashMap;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import org.w3c.dom.Node;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.NodeList;
import java.util.Iterator;
import javax.xml.transform.Source;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferSource;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.ws.util.DOMUtil;
import org.w3c.dom.Element;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferCreator;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import com.sun.istack.internal.NotNull;
import javax.xml.ws.EndpointReference;

public class EndpointReferenceUtil
{
    private static boolean w3cMetadataWritten;
    
    public static <T extends EndpointReference> T transform(final Class<T> clazz, @NotNull final EndpointReference epr) {
        assert epr != null;
        if (clazz.isAssignableFrom(W3CEndpointReference.class)) {
            if (epr instanceof W3CEndpointReference) {
                return (T)epr;
            }
            if (epr instanceof MemberSubmissionEndpointReference) {
                return (T)toW3CEpr((MemberSubmissionEndpointReference)epr);
            }
        }
        else if (clazz.isAssignableFrom(MemberSubmissionEndpointReference.class)) {
            if (epr instanceof W3CEndpointReference) {
                return (T)toMSEpr((W3CEndpointReference)epr);
            }
            if (epr instanceof MemberSubmissionEndpointReference) {
                return (T)epr;
            }
        }
        throw new WebServiceException("Unknwon EndpointReference: " + epr.getClass());
    }
    
    private static W3CEndpointReference toW3CEpr(final MemberSubmissionEndpointReference msEpr) {
        final StreamWriterBufferCreator writer = new StreamWriterBufferCreator();
        EndpointReferenceUtil.w3cMetadataWritten = false;
        try {
            writer.writeStartDocument();
            writer.writeStartElement(AddressingVersion.W3C.getPrefix(), "EndpointReference", AddressingVersion.W3C.nsUri);
            writer.writeNamespace(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.nsUri);
            writer.writeStartElement(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.eprType.address, AddressingVersion.W3C.nsUri);
            writer.writeCharacters(msEpr.addr.uri);
            writer.writeEndElement();
            if ((msEpr.referenceProperties != null && msEpr.referenceProperties.elements.size() > 0) || (msEpr.referenceParameters != null && msEpr.referenceParameters.elements.size() > 0)) {
                writer.writeStartElement(AddressingVersion.W3C.getPrefix(), "ReferenceParameters", AddressingVersion.W3C.nsUri);
                if (msEpr.referenceProperties != null) {
                    for (final Element e : msEpr.referenceProperties.elements) {
                        DOMUtil.serializeNode(e, writer);
                    }
                }
                if (msEpr.referenceParameters != null) {
                    for (final Element e : msEpr.referenceParameters.elements) {
                        DOMUtil.serializeNode(e, writer);
                    }
                }
                writer.writeEndElement();
            }
            Element wsdlElement = null;
            if (msEpr.elements != null && msEpr.elements.size() > 0) {
                for (final Element e2 : msEpr.elements) {
                    if (e2.getNamespaceURI().equals(MemberSubmissionAddressingConstants.MEX_METADATA.getNamespaceURI()) && e2.getLocalName().equals(MemberSubmissionAddressingConstants.MEX_METADATA.getLocalPart())) {
                        final NodeList nl = e2.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", WSDLConstants.QNAME_DEFINITIONS.getLocalPart());
                        if (nl == null) {
                            continue;
                        }
                        wsdlElement = (Element)nl.item(0);
                    }
                }
            }
            if (wsdlElement != null) {
                DOMUtil.serializeNode(wsdlElement, writer);
            }
            if (EndpointReferenceUtil.w3cMetadataWritten) {
                writer.writeEndElement();
            }
            if (msEpr.elements != null && msEpr.elements.size() > 0) {
                for (final Element e2 : msEpr.elements) {
                    if (!e2.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/") || e2.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {}
                    DOMUtil.serializeNode(e2, writer);
                }
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
        }
        catch (final XMLStreamException e3) {
            throw new WebServiceException(e3);
        }
        return new W3CEndpointReference(new XMLStreamBufferSource(writer.getXMLStreamBuffer()));
    }
    
    private static MemberSubmissionEndpointReference toMSEpr(final W3CEndpointReference w3cEpr) {
        final DOMResult result = new DOMResult();
        w3cEpr.writeTo(result);
        final Node eprNode = result.getNode();
        final Element e = DOMUtil.getFirstElementChild(eprNode);
        if (e == null) {
            return null;
        }
        final MemberSubmissionEndpointReference msEpr = new MemberSubmissionEndpointReference();
        final NodeList nodes = e.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            if (nodes.item(i).getNodeType() == 1) {
                final Element child = (Element)nodes.item(i);
                if (child.getNamespaceURI().equals(AddressingVersion.W3C.nsUri) && child.getLocalName().equals(AddressingVersion.W3C.eprType.address)) {
                    if (msEpr.addr == null) {
                        msEpr.addr = new MemberSubmissionEndpointReference.Address();
                    }
                    msEpr.addr.uri = XmlUtil.getTextForNode(child);
                }
                else if (child.getNamespaceURI().equals(AddressingVersion.W3C.nsUri) && child.getLocalName().equals("ReferenceParameters")) {
                    final NodeList refParams = child.getChildNodes();
                    for (int j = 0; j < refParams.getLength(); ++j) {
                        if (refParams.item(j).getNodeType() == 1) {
                            if (msEpr.referenceParameters == null) {
                                msEpr.referenceParameters = new MemberSubmissionEndpointReference.Elements();
                                msEpr.referenceParameters.elements = new ArrayList<Element>();
                            }
                            msEpr.referenceParameters.elements.add((Element)refParams.item(j));
                        }
                    }
                }
                else if (child.getNamespaceURI().equals(AddressingVersion.W3C.nsUri) && child.getLocalName().equals(AddressingVersion.W3C.eprType.wsdlMetadata.getLocalPart())) {
                    final NodeList metadata = child.getChildNodes();
                    String wsdlLocation = child.getAttributeNS("http://www.w3.org/ns/wsdl-instance", "wsdlLocation");
                    Element wsdlDefinitions = null;
                    for (int k = 0; k < metadata.getLength(); ++k) {
                        final Node node = metadata.item(k);
                        if (node.getNodeType() == 1) {
                            final Element elm = (Element)node;
                            if ((elm.getNamespaceURI().equals(AddressingVersion.W3C.wsdlNsUri) || elm.getNamespaceURI().equals("http://www.w3.org/2007/05/addressing/metadata")) && elm.getLocalName().equals(AddressingVersion.W3C.eprType.serviceName)) {
                                msEpr.serviceName = new MemberSubmissionEndpointReference.ServiceNameType();
                                msEpr.serviceName.portName = elm.getAttribute(AddressingVersion.W3C.eprType.portName);
                                final String service = elm.getTextContent();
                                final String prefix = XmlUtil.getPrefix(service);
                                final String name = XmlUtil.getLocalPart(service);
                                if (name != null) {
                                    if (prefix != null) {
                                        final String ns = elm.lookupNamespaceURI(prefix);
                                        if (ns != null) {
                                            msEpr.serviceName.name = new QName(ns, name, prefix);
                                        }
                                    }
                                    else {
                                        msEpr.serviceName.name = new QName(null, name);
                                    }
                                    msEpr.serviceName.attributes = getAttributes(elm);
                                }
                            }
                            else if ((elm.getNamespaceURI().equals(AddressingVersion.W3C.wsdlNsUri) || elm.getNamespaceURI().equals("http://www.w3.org/2007/05/addressing/metadata")) && elm.getLocalName().equals(AddressingVersion.W3C.eprType.portTypeName)) {
                                msEpr.portTypeName = new MemberSubmissionEndpointReference.AttributedQName();
                                final String portType = elm.getTextContent();
                                final String prefix = XmlUtil.getPrefix(portType);
                                final String name = XmlUtil.getLocalPart(portType);
                                if (name != null) {
                                    if (prefix != null) {
                                        final String ns = elm.lookupNamespaceURI(prefix);
                                        if (ns != null) {
                                            msEpr.portTypeName.name = new QName(ns, name, prefix);
                                        }
                                    }
                                    else {
                                        msEpr.portTypeName.name = new QName(null, name);
                                    }
                                    msEpr.portTypeName.attributes = getAttributes(elm);
                                }
                            }
                            else if (elm.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/") && elm.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {
                                wsdlDefinitions = elm;
                            }
                            else {
                                if (msEpr.elements == null) {
                                    msEpr.elements = new ArrayList<Element>();
                                }
                                msEpr.elements.add(elm);
                            }
                        }
                    }
                    final Document doc = DOMUtil.createDom();
                    final Element mexEl = doc.createElementNS(MemberSubmissionAddressingConstants.MEX_METADATA.getNamespaceURI(), MemberSubmissionAddressingConstants.MEX_METADATA.getPrefix() + ":" + MemberSubmissionAddressingConstants.MEX_METADATA.getLocalPart());
                    final Element metadataEl = doc.createElementNS(MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getNamespaceURI(), MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getPrefix() + ":" + MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getLocalPart());
                    metadataEl.setAttribute("Dialect", "http://schemas.xmlsoap.org/wsdl/");
                    if (wsdlDefinitions == null && wsdlLocation != null && !wsdlLocation.equals("")) {
                        wsdlLocation = wsdlLocation.trim();
                        final String wsdlTns = wsdlLocation.substring(0, wsdlLocation.indexOf(32));
                        wsdlLocation = wsdlLocation.substring(wsdlLocation.indexOf(32) + 1);
                        final Element wsdlEl = doc.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:" + WSDLConstants.QNAME_DEFINITIONS.getLocalPart());
                        final Element wsdlImportEl = doc.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:" + WSDLConstants.QNAME_IMPORT.getLocalPart());
                        wsdlImportEl.setAttribute("namespace", wsdlTns);
                        wsdlImportEl.setAttribute("location", wsdlLocation);
                        wsdlEl.appendChild(wsdlImportEl);
                        metadataEl.appendChild(wsdlEl);
                    }
                    else if (wsdlDefinitions != null) {
                        metadataEl.appendChild(wsdlDefinitions);
                    }
                    mexEl.appendChild(metadataEl);
                    if (msEpr.elements == null) {
                        msEpr.elements = new ArrayList<Element>();
                    }
                    msEpr.elements.add(mexEl);
                }
                else {
                    if (msEpr.elements == null) {
                        msEpr.elements = new ArrayList<Element>();
                    }
                    msEpr.elements.add(child);
                }
            }
            else if (nodes.item(i).getNodeType() == 2) {
                final Node n = nodes.item(i);
                if (msEpr.attributes == null) {
                    msEpr.attributes = new HashMap<QName, String>();
                    final String prefix2 = fixNull(n.getPrefix());
                    final String ns2 = fixNull(n.getNamespaceURI());
                    final String localName = n.getLocalName();
                    msEpr.attributes.put(new QName(ns2, localName, prefix2), n.getNodeValue());
                }
            }
        }
        return msEpr;
    }
    
    private static Map<QName, String> getAttributes(final Node node) {
        Map<QName, String> attribs = null;
        final NamedNodeMap nm = node.getAttributes();
        for (int i = 0; i < nm.getLength(); ++i) {
            if (attribs == null) {
                attribs = new HashMap<QName, String>();
            }
            final Node n = nm.item(i);
            final String prefix = fixNull(n.getPrefix());
            final String ns = fixNull(n.getNamespaceURI());
            final String localName = n.getLocalName();
            if (!prefix.equals("xmlns")) {
                if (prefix.length() != 0 || !localName.equals("xmlns")) {
                    if (!localName.equals(AddressingVersion.W3C.eprType.portName)) {
                        attribs.put(new QName(ns, localName, prefix), n.getNodeValue());
                    }
                }
            }
        }
        return attribs;
    }
    
    @NotNull
    private static String fixNull(@Nullable final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    static {
        EndpointReferenceUtil.w3cMetadataWritten = false;
    }
}
