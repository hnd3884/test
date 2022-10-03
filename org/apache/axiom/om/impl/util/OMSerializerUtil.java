package org.apache.axiom.om.impl.util;

import org.apache.commons.logging.LogFactory;
import javax.xml.namespace.NamespaceContext;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.util.CommonUtils;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;
import org.apache.axiom.om.OMNode;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAttribute;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.logging.Log;

public class OMSerializerUtil
{
    private static final Log log;
    private static boolean ADV_DEBUG_ENABLED;
    static long nsCounter;
    private static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XSI_LOCAL_NAME = "type";
    
    @Deprecated
    public static void serializeEndpart(final XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
    }
    
    @Deprecated
    public static void serializeAttribute(final OMAttribute attr, final XMLStreamWriter writer) throws XMLStreamException {
        final OMNamespace ns = attr.getNamespace();
        String prefix = null;
        String namespaceName = null;
        if (ns != null) {
            prefix = ns.getPrefix();
            namespaceName = ns.getNamespaceURI();
            if (prefix != null) {
                writer.writeAttribute(prefix, namespaceName, attr.getLocalName(), attr.getAttributeValue());
            }
            else {
                writer.writeAttribute(namespaceName, attr.getLocalName(), attr.getAttributeValue());
            }
        }
        else {
            final String localName = attr.getLocalName();
            final String attributeValue = attr.getAttributeValue();
            writer.writeAttribute(localName, attributeValue);
        }
    }
    
    @Deprecated
    public static void serializeNamespace(final OMNamespace namespace, final XMLStreamWriter writer) throws XMLStreamException {
        if (namespace == null) {
            return;
        }
        final String uri = namespace.getNamespaceURI();
        String prefix = namespace.getPrefix();
        if (uri != null && !"".equals(uri)) {
            final String prefixFromWriter = writer.getPrefix(uri);
            if (("".equals(prefix) && "".equals(prefixFromWriter) && !uri.equals(writer.getNamespaceContext().getNamespaceURI(""))) || (prefix != null && "".equals(prefix) && (prefixFromWriter == null || !prefix.equals(prefixFromWriter)))) {
                writer.writeDefaultNamespace(uri);
                writer.setDefaultNamespace(uri);
            }
            else {
                prefix = ((prefix == null) ? getNextNSPrefix(writer) : prefix);
                if (prefix != null && !prefix.equals(prefixFromWriter) && !checkForPrefixInTheCurrentContext(writer, uri, prefix)) {
                    writer.writeNamespace(prefix, uri);
                    writer.setPrefix(prefix, uri);
                }
            }
        }
        else {
            final String currentDefaultNSURI = writer.getNamespaceContext().getNamespaceURI("");
            if ((currentDefaultNSURI != null && !currentDefaultNSURI.equals(uri)) || (uri != null && !uri.equals(currentDefaultNSURI))) {
                writer.writeDefaultNamespace(uri);
                writer.setDefaultNamespace(uri);
            }
        }
    }
    
    @Deprecated
    public static boolean isSetPrefixBeforeStartElement(final XMLStreamWriter writer) {
        return false;
    }
    
    @Deprecated
    public static void serializeStartpart(final OMElement element, final XMLStreamWriter writer) throws XMLStreamException {
        serializeStartpart(element, element.getLocalName(), writer);
    }
    
    @Deprecated
    public static void serializeStartpart(final OMElement element, final String localName, final XMLStreamWriter writer) throws XMLStreamException {
        ArrayList writePrefixList = null;
        ArrayList writeNSList = null;
        final OMNamespace eOMNamespace = element.getNamespace();
        String ePrefix = null;
        String eNamespace = null;
        if (eOMNamespace != null) {
            ePrefix = eOMNamespace.getPrefix();
            eNamespace = eOMNamespace.getNamespaceURI();
        }
        ePrefix = ((ePrefix != null && ePrefix.length() == 0) ? null : ePrefix);
        eNamespace = ((eNamespace != null && eNamespace.length() == 0) ? null : eNamespace);
        if (eNamespace != null) {
            if (ePrefix == null) {
                if (!isAssociated("", eNamespace, writer)) {
                    if (writePrefixList == null) {
                        writePrefixList = new ArrayList();
                        writeNSList = new ArrayList();
                    }
                    if (!writePrefixList.contains("")) {
                        writePrefixList.add("");
                        writeNSList.add(eNamespace);
                    }
                }
                writer.writeStartElement("", localName, eNamespace);
            }
            else {
                if (!isAssociated(ePrefix, eNamespace, writer)) {
                    if (writePrefixList == null) {
                        writePrefixList = new ArrayList();
                        writeNSList = new ArrayList();
                    }
                    if (!writePrefixList.contains(ePrefix)) {
                        writePrefixList.add(ePrefix);
                        writeNSList.add(eNamespace);
                    }
                }
                writer.writeStartElement(ePrefix, localName, eNamespace);
            }
        }
        else {
            writer.writeStartElement(localName);
        }
        final Iterator it = element.getAllDeclaredNamespaces();
        while (it != null && it.hasNext()) {
            final OMNamespace omNamespace = it.next();
            String prefix = null;
            String namespace = null;
            if (omNamespace != null) {
                prefix = omNamespace.getPrefix();
                namespace = omNamespace.getNamespaceURI();
            }
            prefix = ((prefix != null && prefix.length() == 0) ? null : prefix);
            namespace = ((namespace != null && namespace.length() == 0) ? null : namespace);
            final String newPrefix = generateSetPrefix(prefix, namespace, writer, false);
            if (newPrefix != null) {
                if (writePrefixList == null) {
                    writePrefixList = new ArrayList();
                    writeNSList = new ArrayList();
                }
                if (writePrefixList.contains(newPrefix)) {
                    continue;
                }
                writePrefixList.add(newPrefix);
                writeNSList.add(namespace);
            }
        }
        String newPrefix2 = generateSetPrefix(ePrefix, eNamespace, writer, false);
        if (newPrefix2 != null) {
            if (writePrefixList == null) {
                writePrefixList = new ArrayList();
                writeNSList = new ArrayList();
            }
            if (!writePrefixList.contains(newPrefix2)) {
                writePrefixList.add(newPrefix2);
                writeNSList.add(eNamespace);
            }
        }
        Iterator attrs = element.getAllAttributes();
        while (attrs != null && attrs.hasNext()) {
            final OMAttribute attr = attrs.next();
            final OMNamespace omNamespace2 = attr.getNamespace();
            String prefix2 = null;
            String namespace2 = null;
            if (omNamespace2 != null) {
                prefix2 = omNamespace2.getPrefix();
                namespace2 = omNamespace2.getNamespaceURI();
            }
            prefix2 = ((prefix2 != null && prefix2.length() == 0) ? null : prefix2);
            namespace2 = ((namespace2 != null && namespace2.length() == 0) ? null : namespace2);
            if (prefix2 == null && namespace2 != null) {
                String writerPrefix = writer.getPrefix(namespace2);
                writerPrefix = ((writerPrefix != null && writerPrefix.length() == 0) ? null : writerPrefix);
                prefix2 = ((writerPrefix != null) ? writerPrefix : getNextNSPrefix());
            }
            newPrefix2 = generateSetPrefix(prefix2, namespace2, writer, true);
            if (newPrefix2 != null) {
                if (writePrefixList == null) {
                    writePrefixList = new ArrayList();
                    writeNSList = new ArrayList();
                }
                if (writePrefixList.contains(newPrefix2)) {
                    continue;
                }
                writePrefixList.add(newPrefix2);
                writeNSList.add(namespace2);
            }
        }
        attrs = element.getAllAttributes();
        while (attrs != null && attrs.hasNext()) {
            final OMAttribute attr = attrs.next();
            final OMNamespace omNamespace2 = attr.getNamespace();
            String prefix2 = null;
            String namespace2 = null;
            if (omNamespace2 != null) {
                prefix2 = omNamespace2.getPrefix();
                namespace2 = omNamespace2.getNamespaceURI();
            }
            prefix2 = ((prefix2 != null && prefix2.length() == 0) ? null : prefix2);
            namespace2 = ((namespace2 != null && namespace2.length() == 0) ? null : namespace2);
            final String local = attr.getLocalName();
            if ("http://www.w3.org/2001/XMLSchema-instance".equals(namespace2) && "type".equals(local)) {
                String value = attr.getAttributeValue();
                if (OMSerializerUtil.log.isDebugEnabled()) {
                    OMSerializerUtil.log.debug((Object)("The value of xsi:type is " + value));
                }
                if (value == null) {
                    continue;
                }
                value = value.trim();
                if (value.indexOf(":") <= 0) {
                    continue;
                }
                final String refPrefix = value.substring(0, value.indexOf(":"));
                final OMNamespace omNS = element.findNamespaceURI(refPrefix);
                final String refNamespace = (omNS == null) ? null : omNS.getNamespaceURI();
                if (refNamespace == null || refNamespace.length() <= 0) {
                    continue;
                }
                newPrefix2 = generateSetPrefix(refPrefix, refNamespace, writer, true);
                if (newPrefix2 == null) {
                    continue;
                }
                if (OMSerializerUtil.log.isDebugEnabled()) {
                    OMSerializerUtil.log.debug((Object)("An xmlns:" + newPrefix2 + "=\"" + refNamespace + "\" will be written"));
                }
                if (writePrefixList == null) {
                    writePrefixList = new ArrayList();
                    writeNSList = new ArrayList();
                }
                if (writePrefixList.contains(newPrefix2)) {
                    continue;
                }
                writePrefixList.add(newPrefix2);
                writeNSList.add(refNamespace);
            }
        }
        if (writePrefixList != null) {
            for (int i = 0; i < writePrefixList.size(); ++i) {
                final String prefix3 = writePrefixList.get(i);
                final String namespace3 = writeNSList.get(i);
                if (prefix3 != null) {
                    if (namespace3 == null) {
                        writer.writeNamespace(prefix3, "");
                    }
                    else {
                        writer.writeNamespace(prefix3, namespace3);
                    }
                }
                else {
                    writer.writeDefaultNamespace(namespace3);
                }
            }
        }
        attrs = element.getAllAttributes();
        while (attrs != null && attrs.hasNext()) {
            final OMAttribute attr = attrs.next();
            final OMNamespace omNamespace2 = attr.getNamespace();
            String prefix2 = null;
            String namespace2 = null;
            if (omNamespace2 != null) {
                prefix2 = omNamespace2.getPrefix();
                namespace2 = omNamespace2.getNamespaceURI();
            }
            prefix2 = ((prefix2 != null && prefix2.length() == 0) ? null : prefix2);
            namespace2 = ((namespace2 != null && namespace2.length() == 0) ? null : namespace2);
            if (prefix2 == null && namespace2 != null) {
                prefix2 = writer.getPrefix(namespace2);
                if (prefix2 == null || "".equals(prefix2)) {
                    for (int j = 0; j < writePrefixList.size(); ++j) {
                        if (namespace2.equals(writeNSList.get(j))) {
                            prefix2 = writePrefixList.get(j);
                        }
                    }
                }
            }
            else if (namespace2 != null) {
                final String writerPrefix = writer.getPrefix(namespace2);
                if (!prefix2.equals(writerPrefix) && writerPrefix != null && !"".equals(writerPrefix)) {
                    prefix2 = writerPrefix;
                }
            }
            if (namespace2 != null) {
                if (prefix2 == null && "http://www.w3.org/XML/1998/namespace".equals(namespace2)) {
                    prefix2 = "xml";
                }
                writer.writeAttribute(prefix2, namespace2, attr.getLocalName(), attr.getAttributeValue());
            }
            else {
                writer.writeAttribute(attr.getLocalName(), attr.getAttributeValue());
            }
        }
    }
    
    @Deprecated
    private static boolean checkForPrefixInTheCurrentContext(final XMLStreamWriter writer, final String nameSpaceName, final String prefix) throws XMLStreamException {
        final Iterator prefixesIter = writer.getNamespaceContext().getPrefixes(nameSpaceName);
        while (prefixesIter.hasNext()) {
            final String prefix_w = prefixesIter.next();
            if (prefix_w.equals(prefix)) {
                return true;
            }
        }
        return false;
    }
    
    @Deprecated
    public static void serializeNamespaces(final OMElement element, final XMLStreamWriter writer) throws XMLStreamException {
        final Iterator namespaces = element.getAllDeclaredNamespaces();
        if (namespaces != null) {
            while (namespaces.hasNext()) {
                serializeNamespace(namespaces.next(), writer);
            }
        }
    }
    
    @Deprecated
    public static void serializeAttributes(final OMElement element, final XMLStreamWriter writer) throws XMLStreamException {
        final Iterator attributes = element.getAllAttributes();
        if (attributes != null && attributes.hasNext()) {
            while (attributes.hasNext()) {
                serializeAttribute(attributes.next(), writer);
            }
        }
    }
    
    @Deprecated
    public static void serializeNormal(final OMElement element, final XMLStreamWriter writer, final boolean cache) throws XMLStreamException {
        if (cache) {
            element.build();
        }
        serializeStartpart(element, writer);
        final OMNode firstChild = element.getFirstOMChild();
        if (firstChild != null) {
            if (cache) {
                firstChild.serialize(writer);
            }
            else {
                firstChild.serializeAndConsume(writer);
            }
        }
        serializeEndpart(writer);
    }
    
    @Deprecated
    public static void serializeByPullStream(final OMElement element, final XMLStreamWriter writer) throws XMLStreamException {
        serializeByPullStream(element, writer, false);
    }
    
    @Deprecated
    public static void serializeByPullStream(final OMElement element, final XMLStreamWriter writer, final boolean cache) throws XMLStreamException {
        final XMLStreamReader reader = element.getXMLStreamReader(cache);
        try {
            new StreamingOMSerializer().serialize(reader, writer);
        }
        finally {
            reader.close();
        }
    }
    
    @Deprecated
    public static String getNextNSPrefix() {
        final String prefix = "axis2ns" + ++OMSerializerUtil.nsCounter % Long.MAX_VALUE;
        if (OMSerializerUtil.log.isDebugEnabled()) {
            OMSerializerUtil.log.debug((Object)("Obtained next prefix:" + prefix));
            if (OMSerializerUtil.ADV_DEBUG_ENABLED && OMSerializerUtil.log.isTraceEnabled()) {
                OMSerializerUtil.log.trace((Object)CommonUtils.callStackToString());
            }
        }
        return prefix;
    }
    
    @Deprecated
    public static String getNextNSPrefix(final XMLStreamWriter writer) {
        String prefix;
        for (prefix = getNextNSPrefix(); writer.getNamespaceContext().getNamespaceURI(prefix) != null; prefix = getNextNSPrefix()) {}
        return prefix;
    }
    
    public static String generateSetPrefix(String prefix, final String namespace, final XMLStreamWriter writer, final boolean attr) throws XMLStreamException {
        prefix = ((prefix == null) ? "" : prefix);
        if (isAssociated(prefix, namespace, writer)) {
            return null;
        }
        if (prefix.length() == 0 && namespace == null && attr) {
            return null;
        }
        String newPrefix = null;
        if (namespace != null) {
            if (prefix.length() == 0) {
                writer.setDefaultNamespace(namespace);
                newPrefix = "";
            }
            else {
                writer.setPrefix(prefix, namespace);
                newPrefix = prefix;
            }
        }
        else {
            writer.setDefaultNamespace("");
            newPrefix = "";
        }
        return newPrefix;
    }
    
    public static boolean isAssociated(String prefix, String namespace, final XMLStreamWriter writer) throws XMLStreamException {
        if ("xml".equals(prefix)) {
            return true;
        }
        prefix = ((prefix == null) ? "" : prefix);
        namespace = ((namespace == null) ? "" : namespace);
        if (namespace.length() > 0) {
            final String writerPrefix = writer.getPrefix(namespace);
            if (prefix.equals(writerPrefix)) {
                return true;
            }
            if (writerPrefix != null) {
                final NamespaceContext nsContext = writer.getNamespaceContext();
                if (nsContext != null) {
                    final String writerNS = nsContext.getNamespaceURI(prefix);
                    return namespace.equals(writerNS);
                }
            }
            return false;
        }
        else {
            if (prefix.length() > 0) {
                throw new OMException("Invalid namespace declaration: Prefixed namespace bindings may not be empty.");
            }
            try {
                final String writerPrefix = writer.getPrefix("");
                if (writerPrefix != null && writerPrefix.length() == 0) {
                    return true;
                }
            }
            catch (final Throwable t) {
                if (OMSerializerUtil.log.isDebugEnabled()) {
                    OMSerializerUtil.log.debug((Object)("Caught exception from getPrefix(\"\"). Processing continues: " + t));
                }
            }
            final NamespaceContext nsContext2 = writer.getNamespaceContext();
            if (nsContext2 != null) {
                final String writerNS2 = nsContext2.getNamespaceURI("");
                if (writerNS2 != null && writerNS2.length() > 0) {
                    return false;
                }
            }
            return true;
        }
    }
    
    static {
        log = LogFactory.getLog((Class)OMSerializerUtil.class);
        OMSerializerUtil.ADV_DEBUG_ENABLED = true;
        OMSerializerUtil.nsCounter = 0L;
    }
}
