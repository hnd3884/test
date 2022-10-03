package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.DeferredParsingException;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.impl.common.serializer.push.stax.StAXSerializer;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.impl.intf.AxiomSerializable;
import org.apache.axiom.om.util.StAXUtils;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMOutputFormat;
import java.io.OutputStream;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import org.apache.axiom.om.OMContainer;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.apache.axiom.om.impl.common.serializer.push.sax.XMLReaderImpl;
import javax.xml.transform.sax.SAXSource;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMSerializable;
import javax.xml.namespace.QName;
import org.apache.axiom.core.Axis;
import org.apache.axiom.core.Semantics;
import java.util.Iterator;
import org.apache.axiom.om.NodeUnavailableException;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.apache.axiom.om.impl.intf.AxiomInformationItem;
import org.apache.axiom.om.impl.intf.AxiomChildNode;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLStreamReader;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.OMXMLStreamReaderValidator;
import org.apache.axiom.om.impl.common.serializer.pull.OMXMLStreamReaderExAdapter;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.om.impl.common.serializer.pull.PullSerializer;
import org.apache.axiom.om.impl.intf.AxiomCoreParentNode;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.commons.logging.LogFactory;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.commons.logging.Log;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomContainerSupport
{
    private static final Log log;
    private static final OMXMLStreamReaderConfiguration defaultReaderConfiguration;
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomContainerSupport ajc$perSingletonInstance;
    
    static {
        try {
            log = LogFactory.getLog((Class)AxiomContainerSupport.class);
            defaultReaderConfiguration = new OMXMLStreamReaderConfiguration();
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomContainerSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$discarded(final AxiomContainer ajc$this_) {
        ajc$this_.coreSetState(2);
    }
    
    public static XMLStreamReader ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getXMLStreamReader(final AxiomContainer ajc$this_) {
        return ajc$this_.getXMLStreamReader(true);
    }
    
    public static XMLStreamReader ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getXMLStreamReaderWithoutCaching(final AxiomContainer ajc$this_) {
        return ajc$this_.getXMLStreamReader(false);
    }
    
    public static XMLStreamReader ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getXMLStreamReader(final AxiomContainer ajc$this_, final boolean cache) {
        return ajc$this_.getXMLStreamReader(cache, AxiomContainerSupport.defaultReaderConfiguration);
    }
    
    public static XMLStreamReader ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getXMLStreamReader(final AxiomContainer ajc$this_, final boolean cache, final OMXMLStreamReaderConfiguration configuration) {
        return ajc$this_.defaultGetXMLStreamReader(cache, configuration);
    }
    
    public static XMLStreamReader ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$defaultGetXMLStreamReader(final AxiomContainer ajc$this_, final boolean cache, final OMXMLStreamReaderConfiguration configuration) {
        final OMXMLParserWrapper builder = ajc$this_.getBuilder();
        if (builder != null && builder.isCompleted() && !cache && !AxiomCoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomCoreParentNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreParentNode$isComplete(ajc$this_)) {
            throw new UnsupportedOperationException("The parser is already consumed!");
        }
        OMXMLStreamReader reader = (OMXMLStreamReader)new OMXMLStreamReaderExAdapter(new PullSerializer(ajc$this_, cache, configuration.isPreserveNamespaceContext()));
        if (configuration.isNamespaceURIInterning()) {
            reader = (OMXMLStreamReader)new NamespaceURIInterningXMLStreamReaderWrapper(reader);
        }
        if (AxiomContainerSupport.log.isDebugEnabled()) {
            reader = (OMXMLStreamReader)new OMXMLStreamReaderValidator(reader, false);
        }
        return (XMLStreamReader)reader;
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$addChild(final AxiomContainer ajc$this_, final OMNode omNode) {
        ajc$this_.addChild(omNode, false);
    }
    
    public static AxiomChildNode ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$prepareNewChild(final AxiomContainer ajc$this_, final OMNode omNode) {
        AxiomChildNode child;
        if (omNode.getOMFactory().getMetaFactory().equals(AxiomInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomInformationItem$getOMFactory(ajc$this_).getMetaFactory())) {
            child = (AxiomChildNode)omNode;
        }
        else {
            child = (AxiomChildNode)((OMFactoryEx)AxiomInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomInformationItem$getOMFactory(ajc$this_)).importNode(omNode);
        }
        ajc$this_.checkChild(omNode);
        return child;
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$addChild(final AxiomContainer ajc$this_, final OMNode omNode, final boolean fromBuilder) {
        AxiomChildNode child;
        if (fromBuilder) {
            child = (AxiomChildNode)omNode;
        }
        else {
            child = ajc$this_.prepareNewChild(omNode);
        }
        ajc$this_.coreAppendChild(child, fromBuilder);
        if (!fromBuilder && !child.isComplete() && !(child instanceof OMSourcedElement)) {
            ajc$this_.setComplete(false);
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$build(final AxiomContainer ajc$this_) {
        final OMXMLParserWrapper builder = ajc$this_.getBuilder();
        if (builder == null && ajc$this_.getState() == 1) {
            final Iterator childrenIterator = ajc$this_.getChildren();
            while (childrenIterator.hasNext()) {
                final OMNode omNode = childrenIterator.next();
                omNode.build();
            }
        }
        else {
            if (ajc$this_.getState() == 2) {
                if (builder != null) {
                    ((StAXBuilder)builder).debugDiscarded((Object)ajc$this_);
                }
                throw new NodeUnavailableException();
            }
            if (builder != null && builder.isCompleted()) {
                AxiomContainerSupport.log.debug((Object)"Builder is already complete.");
            }
            while (!AxiomCoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomCoreParentNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreParentNode$isComplete(ajc$this_)) {
                builder.next();
                if (builder.isCompleted() && !AxiomCoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomCoreParentNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreParentNode$isComplete(ajc$this_)) {
                    AxiomContainerSupport.log.debug((Object)"Builder is complete.  Setting OMObject to complete.");
                    ajc$this_.setComplete(true);
                }
            }
        }
    }
    
    public static OMNode ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstOMChild(final AxiomContainer ajc$this_) {
        return (OMNode)ajc$this_.coreGetFirstChild();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$removeChildren(final AxiomContainer ajc$this_) {
        ajc$this_.coreRemoveChildren(AxiomSemantics.INSTANCE);
    }
    
    public static Iterator ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getChildren(final AxiomContainer ajc$this_) {
        return ajc$this_.coreGetNodes(Axis.CHILDREN, OMNode.class, AxiomSemantics.INSTANCE);
    }
    
    public static Iterator ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getChildrenWithLocalName(final AxiomContainer ajc$this_, final String localName) {
        return (Iterator)new OMChildrenLocalNameIterator(ajc$this_.getFirstOMChild(), localName);
    }
    
    public static Iterator ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getChildrenWithNamespaceURI(final AxiomContainer ajc$this_, final String uri) {
        return (Iterator)new OMChildrenNamespaceIterator(ajc$this_.getFirstOMChild(), uri);
    }
    
    public static Iterator ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getChildrenWithName(final AxiomContainer ajc$this_, final QName elementQName) {
        final OMNode firstChild = ajc$this_.getFirstOMChild();
        Iterator it = (Iterator)new OMChildrenQNameIterator(firstChild, elementQName);
        if (elementQName != null && elementQName.getNamespaceURI().length() == 0 && firstChild != null && !it.hasNext()) {
            if (AxiomContainerSupport.log.isTraceEnabled()) {
                AxiomContainerSupport.log.trace((Object)("There are no child elements that match the unqualifed name: " + elementQName));
                AxiomContainerSupport.log.trace((Object)"Now looking for child elements that have the same local name.");
            }
            it = (Iterator)new OMChildrenLegacyQNameIterator(ajc$this_.getFirstOMChild(), elementQName);
        }
        return it;
    }
    
    public static Iterator ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getDescendants(final AxiomContainer ajc$this_, final boolean includeSelf) {
        return ajc$this_.coreGetNodes(includeSelf ? Axis.DESCENDANTS_OR_SELF : Axis.DESCENDANTS, OMSerializable.class, AxiomSemantics.INSTANCE);
    }
    
    public static OMElement ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstChildWithName(final AxiomContainer ajc$this_, final QName elementQName) throws OMException {
        final OMChildrenQNameIterator omChildrenQNameIterator = new OMChildrenQNameIterator(ajc$this_.getFirstOMChild(), elementQName);
        OMNode omNode = null;
        if (omChildrenQNameIterator.hasNext()) {
            omNode = (OMNode)omChildrenQNameIterator.next();
        }
        return (omNode != null && 1 == omNode.getType()) ? ((OMElement)omNode) : null;
    }
    
    public static SAXSource ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getSAXSource(final AxiomContainer ajc$this_, final boolean cache) {
        return new SAXSource((XMLReader)new XMLReaderImpl(ajc$this_, cache), new InputSource());
    }
    
    public static SAXResult ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getSAXResult(final AxiomContainer ajc$this_) {
        final SAXResultContentHandler handler = new SAXResultContentHandler((OMContainer)ajc$this_);
        final SAXResult result = new SAXResult();
        result.setHandler(handler);
        result.setLexicalHandler(handler);
        return result;
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serialize(final AxiomContainer ajc$this_, final OutputStream output) throws XMLStreamException {
        ajc$this_.serialize(output, new OMOutputFormat());
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serialize(final AxiomContainer ajc$this_, final Writer writer) throws XMLStreamException {
        final XMLStreamWriter xmlStreamWriter = StAXUtils.createXMLStreamWriter(writer);
        try {
            AxiomSerializableSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomSerializableSupport$org_apache_axiom_om_impl_intf_AxiomSerializable$serialize(ajc$this_, xmlStreamWriter);
        }
        finally {
            xmlStreamWriter.close();
        }
        xmlStreamWriter.close();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serializeAndConsume(final AxiomContainer ajc$this_, final OutputStream output) throws XMLStreamException {
        ajc$this_.serializeAndConsume(output, new OMOutputFormat());
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serializeAndConsume(final AxiomContainer ajc$this_, final Writer writer) throws XMLStreamException {
        final XMLStreamWriter xmlStreamWriter = StAXUtils.createXMLStreamWriter(writer);
        try {
            AxiomSerializableSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomSerializableSupport$org_apache_axiom_om_impl_intf_AxiomSerializable$serializeAndConsume(ajc$this_, xmlStreamWriter);
        }
        finally {
            xmlStreamWriter.close();
        }
        xmlStreamWriter.close();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serialize(final AxiomContainer ajc$this_, final OutputStream output, final OMOutputFormat format) throws XMLStreamException {
        final MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, format, true);
        try {
            ajc$this_.internalSerialize(new StAXSerializer((OMSerializable)ajc$this_, (XMLStreamWriter)writer), format, true);
        }
        catch (final OutputException ex) {
            throw (XMLStreamException)ex.getCause();
        }
        finally {
            writer.close();
        }
        writer.close();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serialize(final AxiomContainer ajc$this_, final Writer writer2, final OMOutputFormat format) throws XMLStreamException {
        final MTOMXMLStreamWriter writer3 = new MTOMXMLStreamWriter(StAXUtils.createXMLStreamWriter(writer2));
        writer3.setOutputFormat(format);
        try {
            ajc$this_.internalSerialize(new StAXSerializer((OMSerializable)ajc$this_, (XMLStreamWriter)writer3), format, true);
        }
        catch (final OutputException ex) {
            throw (XMLStreamException)ex.getCause();
        }
        finally {
            writer3.close();
        }
        writer3.close();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serializeAndConsume(final AxiomContainer ajc$this_, final OutputStream output, final OMOutputFormat format) throws XMLStreamException {
        final MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, format, false);
        try {
            ajc$this_.internalSerialize(new StAXSerializer((OMSerializable)ajc$this_, (XMLStreamWriter)writer), format, false);
        }
        catch (final OutputException ex) {
            throw (XMLStreamException)ex.getCause();
        }
        finally {
            writer.close();
        }
        writer.close();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serializeAndConsume(final AxiomContainer ajc$this_, final Writer writer2, final OMOutputFormat format) throws XMLStreamException {
        final MTOMXMLStreamWriter writer3 = new MTOMXMLStreamWriter(StAXUtils.createXMLStreamWriter(writer2));
        writer3.setOutputFormat(format);
        try {
            ajc$this_.internalSerialize(new StAXSerializer((OMSerializable)ajc$this_, (XMLStreamWriter)writer3), format, false);
        }
        catch (final OutputException ex) {
            throw (XMLStreamException)ex.getCause();
        }
        finally {
            writer3.close();
        }
        writer3.close();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serializeChildren(final AxiomContainer ajc$this_, final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        if (ajc$this_.getState() == 2) {
            final StAXBuilder builder = (StAXBuilder)ajc$this_.getBuilder();
            if (builder != null) {
                builder.debugDiscarded((Object)ajc$this_);
            }
            throw new NodeUnavailableException();
        }
        if (cache) {
            for (AxiomChildNode child = (AxiomChildNode)ajc$this_.getFirstOMChild(); child != null; child = (AxiomChildNode)AxiomChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$getNextOMSibling(child)) {
                child.internalSerialize(serializer, format, true);
            }
        }
        else {
            for (AxiomChildNode child = (AxiomChildNode)ajc$this_.coreGetFirstChildIfAvailable(); child != null; child = (AxiomChildNode)child.coreGetNextSiblingIfAvailable()) {
                child.internalSerialize(serializer, format, cache);
            }
            if (!AxiomCoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomCoreParentNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreParentNode$isComplete(ajc$this_) && ajc$this_.getBuilder() != null) {
                final StAXOMBuilder builder2 = (StAXOMBuilder)ajc$this_.getBuilder();
                final XMLStreamReader reader = builder2.disableCaching();
                final DataHandlerReader dataHandlerReader = XMLStreamReaderUtils.getDataHandlerReader(reader);
                boolean first = true;
                int depth = 0;
            Label_0296:
                while (true) {
                    int event;
                    if (first) {
                        event = reader.getEventType();
                        first = false;
                    }
                    else {
                        try {
                            event = reader.next();
                        }
                        catch (final XMLStreamException ex) {
                            throw new DeferredParsingException((Throwable)ex);
                        }
                    }
                    switch (event) {
                        case 1: {
                            ++depth;
                            break;
                        }
                        case 2: {
                            if (depth == 0) {
                                break Label_0296;
                            }
                            --depth;
                            break;
                        }
                        case 8: {
                            if (depth != 0) {
                                throw new IllegalStateException();
                            }
                            break Label_0296;
                        }
                    }
                    serializer.copyEvent(reader, dataHandlerReader);
                }
                builder2.reenableCaching((OMContainer)ajc$this_);
            }
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$notifyChildComplete(final AxiomContainer ajc$this_) {
        if (ajc$this_.getState() == 1 && ajc$this_.getBuilder() == null) {
            final Iterator iterator = ajc$this_.getChildren();
            while (iterator.hasNext()) {
                final OMNode node = iterator.next();
                if (!node.isComplete()) {
                    return;
                }
            }
            ajc$this_.setComplete(true);
        }
    }
    
    public static AxiomContainerSupport aspectOf() {
        if (AxiomContainerSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomContainerSupport", AxiomContainerSupport.ajc$initFailureCause);
        }
        return AxiomContainerSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomContainerSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomContainerSupport();
    }
}
