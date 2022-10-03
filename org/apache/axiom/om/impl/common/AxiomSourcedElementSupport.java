package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import java.util.Iterator;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.om.QNameAwareOMDataSource;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.intf.AxiomInformationItem;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.impl.common.util.OMDataSourceUtil;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.impl.intf.AxiomNamedInformationItem;
import org.apache.axiom.core.CoreNode;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSourcedElementSupport
{
    private static final Log log;
    private static final Log forceExpandLog;
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSourcedElementSupport ajc$perSingletonInstance;
    
    static {
        try {
            log = LogFactory.getLog((Class)AxiomSourcedElementSupport.class);
            forceExpandLog = LogFactory.getLog(String.valueOf(AxiomSourcedElementSupport.class.getName()) + ".forceExpand");
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSourcedElementSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource(final AxiomSourcedElement ajc$this_) {
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace(final AxiomSourcedElement ajc$this_) {
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespaceSet(final AxiomSourcedElement ajc$this_) {
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded(final AxiomSourcedElement ajc$this_) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded(true);
    }
    
    private static OMNamespace getOMNamespace(final QName qName) {
        return (OMNamespace)((qName.getNamespaceURI().length() == 0) ? null : new OMNamespaceImpl(qName.getNamespaceURI(), qName.getPrefix()));
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$coreGetNodeClass(final AxiomSourcedElement ajc$this_) {
        return AxiomSourcedElement.class;
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$init(final AxiomSourcedElement ajc$this_, final OMDataSource source) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource(source);
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded(false);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$init(final AxiomSourcedElement ajc$this_, final String localName, OMNamespace ns, final OMDataSource source) {
        if (source == null) {
            throw new IllegalArgumentException("OMDataSource can't be null");
        }
        AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetLocalName(ajc$this_, localName);
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource(source);
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded(false);
        if (ns != null && ns.getNamespaceURI().length() == 0) {
            ns = null;
        }
        if (ns == null || (!isLossyPrefix(ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource()) && ns.getPrefix() != null)) {
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace(ns);
        }
        else {
            final String uri = ns.getNamespaceURI();
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace((OMNamespace)new DeferredNamespace(ajc$this_, uri));
        }
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespaceSet(true);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$init(final AxiomSourcedElement ajc$this_, final QName qName, final OMDataSource source) {
        if (source == null) {
            throw new IllegalArgumentException("OMDataSource can't be null");
        }
        AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetLocalName(ajc$this_, qName.getLocalPart());
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource(source);
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded(false);
        if (!isLossyPrefix(ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource())) {
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace(getOMNamespace(qName));
        }
        else {
            final String uri = qName.getNamespaceURI();
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace((OMNamespace)((uri.length() == 0) ? null : new DeferredNamespace(ajc$this_, uri)));
        }
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespaceSet(true);
    }
    
    private static boolean isLossyPrefix(final OMDataSource source) {
        Object lossyPrefix = null;
        if (source instanceof OMDataSourceExt) {
            lossyPrefix = ((OMDataSourceExt)source).getProperty("lossyPrefix");
        }
        return lossyPrefix == Boolean.TRUE;
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$getPrintableName(final AxiomSourcedElement ajc$this_) {
        if (!ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded() && (!ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespaceSet() || AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalGetLocalName(ajc$this_) == null)) {
            return "<unknown>";
        }
        String uri = null;
        if (ajc$this_.getNamespace() != null) {
            uri = ajc$this_.getNamespace().getNamespaceURI();
        }
        if (uri == null || uri.length() == 0) {
            return ajc$this_.getLocalName();
        }
        return "{" + uri + '}' + ajc$this_.getLocalName();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$forceExpand(final AxiomSourcedElement ajc$this_) {
        if (!ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded() && ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource() != null) {
            if (AxiomSourcedElementSupport.log.isDebugEnabled()) {
                AxiomSourcedElementSupport.log.debug((Object)("forceExpand: expanding element " + ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$getPrintableName()));
                if (AxiomSourcedElementSupport.forceExpandLog.isDebugEnabled()) {
                    final Exception e = new Exception("Debug Stack Trace");
                    AxiomSourcedElementSupport.forceExpandLog.debug((Object)"forceExpand stack", (Throwable)e);
                }
            }
            if (OMDataSourceUtil.isPushDataSource(ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource())) {
                ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded(true);
                try {
                    ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource().serialize((XMLStreamWriter)new PushOMBuilder(ajc$this_));
                    return;
                }
                catch (final XMLStreamException ex) {
                    throw new OMException("Failed to expand data source", (Throwable)ex);
                }
            }
            XMLStreamReader readerFromDS;
            try {
                readerFromDS = ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource().getReader();
            }
            catch (final XMLStreamException ex2) {
                throw new OMException("Error obtaining parser from data source for element " + ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$getPrintableName(), (Throwable)ex2);
            }
            String characterEncoding = readerFromDS.getCharacterEncodingScheme();
            if (characterEncoding != null) {
                characterEncoding = readerFromDS.getEncoding();
            }
            try {
                if (readerFromDS.getEventType() != 1) {
                    while (readerFromDS.next() != 1) {}
                }
            }
            catch (final XMLStreamException ex3) {
                throw new OMException("Error parsing data source document for element " + ajc$this_.getLocalName(), (Throwable)ex3);
            }
            ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_om_impl_common$validateName(readerFromDS.getPrefix(), readerFromDS.getLocalName(), readerFromDS.getNamespaceURI());
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded(true);
            final StAXOMBuilder builder = new StAXOMBuilder(AxiomInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomInformationItem$getOMFactory(ajc$this_), readerFromDS, (OMElement)ajc$this_, characterEncoding);
            builder.setAutoClose(true);
            ajc$this_.coreSetBuilder((OMXMLParserWrapper)builder);
            ajc$this_.setComplete(false);
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$validateName(final AxiomSourcedElement ajc$this_, String staxPrefix, final String staxLocalName, String staxNamespaceURI) {
        if (AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalGetLocalName(ajc$this_) == null) {
            AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetLocalName(ajc$this_, staxLocalName);
        }
        else if (!staxLocalName.equals(AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalGetLocalName(ajc$this_))) {
            throw new OMException("Element name from data source is " + staxLocalName + ", not the expected " + AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalGetLocalName(ajc$this_));
        }
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespaceSet()) {
            if (staxNamespaceURI == null) {
                staxNamespaceURI = "";
            }
            final String namespaceURI = (ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace() == null) ? "" : ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace().getNamespaceURI();
            if (!staxNamespaceURI.equals(namespaceURI)) {
                throw new OMException("Element namespace from data source is " + staxNamespaceURI + ", not the expected " + namespaceURI);
            }
            if (!(ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace() instanceof DeferredNamespace)) {
                if (staxPrefix == null) {
                    staxPrefix = "";
                }
                final String prefix = (ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace() == null) ? "" : ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace().getPrefix();
                if (!staxPrefix.equals(prefix)) {
                    throw new OMException("Element prefix from data source is '" + staxPrefix + "', not the expected '" + prefix + "'");
                }
            }
        }
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded(final AxiomSourcedElement ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded();
    }
    
    public static XMLStreamReader ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$getXMLStreamReader(final AxiomSourcedElement ajc$this_, final boolean cache, final OMXMLStreamReaderConfiguration configuration) {
        if (AxiomSourcedElementSupport.log.isDebugEnabled()) {
            AxiomSourcedElementSupport.log.debug((Object)("getting XMLStreamReader for " + ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$getPrintableName() + " with cache=" + cache));
        }
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded()) {
            return AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$defaultGetXMLStreamReader(ajc$this_, cache, configuration);
        }
        if ((cache && OMDataSourceUtil.isDestructiveRead(ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource())) || OMDataSourceUtil.isPushDataSource(ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource())) {
            ajc$this_.forceExpand();
            return AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$defaultGetXMLStreamReader(ajc$this_, true, configuration);
        }
        try {
            return ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource().getReader();
        }
        catch (final XMLStreamException ex) {
            throw new OMException("Error obtaining parser from data source for element " + ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$getPrintableName(), (Throwable)ex);
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$updateLocalName(final AxiomSourcedElement ajc$this_) {
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource() instanceof QNameAwareOMDataSource) {
            AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetLocalName(ajc$this_, ((QNameAwareOMDataSource)ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource()).getLocalName());
        }
        if (AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalGetLocalName(ajc$this_) == null) {
            ajc$this_.forceExpand();
        }
    }
    
    public static OMNamespace ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$getNamespace(final AxiomSourcedElement ajc$this_) throws OMException {
        if (ajc$this_.isExpanded()) {
            return AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$defaultGetNamespace(ajc$this_);
        }
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespaceSet()) {
            return ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace();
        }
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource() instanceof QNameAwareOMDataSource) {
            final String namespaceURI = ((QNameAwareOMDataSource)ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource()).getNamespaceURI();
            if (namespaceURI != null) {
                if (namespaceURI.length() == 0) {
                    ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespaceSet(true);
                }
                else {
                    final String prefix = ((QNameAwareOMDataSource)ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource()).getPrefix();
                    if (prefix == null) {
                        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace((OMNamespace)new DeferredNamespace(ajc$this_, namespaceURI));
                    }
                    else {
                        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace((OMNamespace)new OMNamespaceImpl(namespaceURI, prefix));
                    }
                    ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespaceSet(true);
                }
            }
        }
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespaceSet()) {
            return ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace();
        }
        ajc$this_.forceExpand();
        return AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$defaultGetNamespace(ajc$this_);
    }
    
    public static QName ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$getQName(final AxiomSourcedElement ajc$this_) {
        if (ajc$this_.isExpanded()) {
            return AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$defaultGetQName(ajc$this_);
        }
        if (ajc$this_.getNamespace() != null) {
            return new QName(ajc$this_.getNamespace().getNamespaceURI(), ajc$this_.getLocalName());
        }
        return new QName(ajc$this_.getLocalName());
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$initSource(final AxiomSourcedElement ajc$this_, final ClonePolicy<T> policy, final T options, final CoreElement other) {
        final AxiomSourcedElement o = (AxiomSourcedElement)other;
        final OMDataSource ds = o.getDataSource();
        if (!(options instanceof OMCloneOptions) || !((OMCloneOptions)options).isCopyOMDataSources() || ds == null || o.isExpanded() || !(ds instanceof OMDataSourceExt)) {
            return;
        }
        final OMDataSourceExt sourceDS = (OMDataSourceExt)ds;
        if (sourceDS.isDestructiveRead() || sourceDS.isDestructiveWrite()) {
            return;
        }
        final OMDataSourceExt targetDS = ((OMDataSourceExt)ds).copy();
        if (targetDS == null) {
            return;
        }
        ajc$this_.init((OMDataSource)targetDS);
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespaceSet(o.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespaceSet());
        if (o.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace() instanceof DeferredNamespace) {
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace((OMNamespace)new DeferredNamespace(ajc$this_, o.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace().getNamespaceURI()));
        }
        else {
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace(o.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace());
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$internalSerialize(final AxiomSourcedElement ajc$this_, final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        if (ajc$this_.isExpanded()) {
            AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$defaultInternalSerialize(ajc$this_, serializer, format, cache);
        }
        else if (cache) {
            if (OMDataSourceUtil.isDestructiveWrite(ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource())) {
                ajc$this_.forceExpand();
                AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$defaultInternalSerialize(ajc$this_, serializer, format, true);
            }
            else {
                serializer.serialize(ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource());
            }
        }
        else {
            serializer.serialize(ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource());
        }
    }
    
    public static OMDataSource ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$getDataSource(final AxiomSourcedElement ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource();
    }
    
    public static OMDataSource ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$setDataSource(final AxiomSourcedElement ajc$this_, final OMDataSource dataSource) {
        if (!ajc$this_.isExpanded()) {
            final OMDataSource oldDS = ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource();
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource(dataSource);
            return oldDS;
        }
        final OMDataSource oldDS = ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource();
        final Iterator it = AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getChildren(ajc$this_);
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource(dataSource);
        ajc$this_.setComplete(false);
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded(false);
        ajc$this_.coreSetBuilder(null);
        if (isLossyPrefix(dataSource)) {
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace((OMNamespace)new DeferredNamespace(ajc$this_, ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$definedNamespace().getNamespaceURI()));
        }
        return oldDS;
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$setComplete(final AxiomSourcedElement ajc$this_, final boolean complete) {
        ajc$this_.coreSetState(complete ? 0 : 1);
        if (complete && ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource() != null) {
            if (ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource() instanceof OMDataSourceExt) {
                ((OMDataSourceExt)ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource()).close();
            }
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource(null);
        }
    }
    
    public static Object ajc$interMethod$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$getObject(final AxiomSourcedElement ajc$this_, final Class dataSourceClass) {
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource() == null || ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded() || !dataSourceClass.isInstance(ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource())) {
            return null;
        }
        return ((OMDataSourceExt)ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$dataSource()).getObject();
    }
    
    public static AxiomSourcedElementSupport aspectOf() {
        if (AxiomSourcedElementSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomSourcedElementSupport", AxiomSourcedElementSupport.ajc$initFailureCause);
        }
        return AxiomSourcedElementSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSourcedElementSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSourcedElementSupport();
    }
}
