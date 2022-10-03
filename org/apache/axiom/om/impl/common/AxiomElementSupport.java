package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.OMCloneOptions;
import java.io.StringWriter;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.impl.intf.AxiomNamespaceDeclaration;
import org.apache.axiom.core.Mapper;
import org.apache.axiom.util.xml.NSUtils;
import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.om.impl.intf.AxiomChildNode;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.impl.intf.AxiomInformationItem;
import org.apache.axiom.core.Semantics;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.intf.AxiomElement$AxiomElementSupport;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.axiom.om.OMText;
import java.io.StringReader;
import org.apache.axiom.om.impl.intf.AxiomCoreParentNode;
import org.apache.axiom.om.OMSourcedElement;
import java.io.Reader;
import org.apache.axiom.core.ElementAction;
import javax.xml.namespace.QName;
import java.util.Map;
import org.apache.axiom.util.namespace.MapBasedNamespaceContext;
import java.util.HashMap;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.intf.AxiomNamedInformationItem;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.commons.logging.LogFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.intf.AxiomAttribute;
import org.apache.axiom.core.IdentityMapper;
import org.apache.commons.logging.Log;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomElementSupport
{
    private static final Log log;
    private static final IdentityMapper<AxiomAttribute> attributeIdentityMapper;
    private static final OMNamespace XMLNS;
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomElementSupport ajc$perSingletonInstance;
    
    static {
        try {
            log = LogFactory.getLog((Class)AxiomElementSupport.class);
            attributeIdentityMapper = new IdentityMapper<AxiomAttribute>();
            XMLNS = (OMNamespace)new OMNamespaceImpl("http://www.w3.org/XML/1998/namespace", "xml");
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomElementSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$lineNumber(final AxiomElement ajc$this_) {
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$initName(final AxiomElement ajc$this_, final String localName, final OMNamespace ns, final boolean generateNSDecl) {
        AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetLocalName(ajc$this_, localName);
        AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetNamespace(ajc$this_, generateNSDecl ? NSUtil.handleNamespace(ajc$this_, ns, false, true) : ns);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$beforeSetLocalName(final AxiomElement ajc$this_) {
        ajc$this_.forceExpand();
    }
    
    public static int ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getType(final AxiomElement ajc$this_) {
        return 1;
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$setNamespaceWithNoFindInCurrentScope(final AxiomElement ajc$this_, final OMNamespace namespace) {
        ajc$this_.forceExpand();
        AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetNamespace(ajc$this_, namespace);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$setNamespace(final AxiomElement ajc$this_, final OMNamespace namespace, final boolean decl) {
        ajc$this_.forceExpand();
        AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetNamespace(ajc$this_, NSUtil.handleNamespace(ajc$this_, namespace, false, decl));
    }
    
    public static OMElement ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getFirstElement(final AxiomElement ajc$this_) {
        for (OMNode node = AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstOMChild(ajc$this_); node != null; node = node.getNextOMSibling()) {
            if (node.getType() == 1) {
                return (OMElement)node;
            }
        }
        return null;
    }
    
    public static Iterator ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getChildElements(final AxiomElement ajc$this_) {
        return (Iterator)new OMChildElementIterator(ajc$this_.getFirstElement());
    }
    
    public static Iterator ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getNamespacesInScope(final AxiomElement ajc$this_) {
        return new NamespaceIterator((OMElement)ajc$this_);
    }
    
    public static NamespaceContext ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getNamespaceContext(final AxiomElement ajc$this_, final boolean detached) {
        if (detached) {
            final Map namespaces = new HashMap();
            final Iterator it = ajc$this_.getNamespacesInScope();
            while (it.hasNext()) {
                final OMNamespace ns = it.next();
                namespaces.put(ns.getPrefix(), ns.getNamespaceURI());
            }
            return (NamespaceContext)new MapBasedNamespaceContext(namespaces);
        }
        return (NamespaceContext)new LiveNamespaceContext((OMElement)ajc$this_);
    }
    
    public static QName ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$resolveQName(final AxiomElement ajc$this_, final String qname) {
        final int idx = qname.indexOf(58);
        if (idx == -1) {
            final OMNamespace ns = ajc$this_.getDefaultNamespace();
            return (ns == null) ? new QName(qname) : new QName(ns.getNamespaceURI(), qname, "");
        }
        final String prefix = qname.substring(0, idx);
        final OMNamespace ns2 = ajc$this_.findNamespace(null, prefix);
        return (ns2 == null) ? null : new QName(ns2.getNamespaceURI(), qname.substring(idx + 1), prefix);
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getText(final AxiomElement ajc$this_) {
        return ajc$this_.coreGetCharacterData(ElementAction.SKIP).toString();
    }
    
    public static QName ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getTextAsQName(final AxiomElement ajc$this_) {
        final String childText = ajc$this_.getText().trim();
        return (childText.length() == 0) ? null : ajc$this_.resolveQName(childText);
    }
    
    public static Reader ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getTextAsStream(final AxiomElement ajc$this_, final boolean cache) {
        if (!(ajc$this_ instanceof OMSourcedElement) && (!cache || AxiomCoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomCoreParentNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreParentNode$isComplete(ajc$this_))) {
            final OMNode child = AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstOMChild(ajc$this_);
            if (child == null) {
                return new StringReader("");
            }
            if (child.getNextOMSibling() == null) {
                return new StringReader((child instanceof OMText) ? ((OMText)child).getText() : "");
            }
        }
        try {
            final XMLStreamReader reader = AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getXMLStreamReader(ajc$this_, cache);
            if (reader.getEventType() == 7) {
                reader.next();
            }
            Reader stream = XMLStreamReaderUtils.getElementTextAsStream(reader, true);
            if (!cache) {
                stream = (Reader)new AxiomElement$AxiomElementSupport.AxiomElement$AxiomElementSupport$1(ajc$this_, stream, reader);
            }
            return stream;
        }
        catch (final XMLStreamException ex) {
            throw new OMException((Throwable)ex);
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$writeTextTo(final AxiomElement ajc$this_, final Writer out, final boolean cache) throws IOException {
        try {
            final XMLStreamReader reader = AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getXMLStreamReader(ajc$this_, cache);
            int depth = 0;
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case 4:
                    case 12: {
                        if (depth == 1) {
                            out.write(reader.getText());
                            continue;
                        }
                        continue;
                    }
                    default: {
                        continue;
                    }
                    case 1: {
                        ++depth;
                        continue;
                    }
                    case 2: {
                        --depth;
                        continue;
                    }
                }
            }
        }
        catch (final XMLStreamException ex) {
            throw new OMException((Throwable)ex);
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$setText(final AxiomElement ajc$this_, final String text) {
        ajc$this_.coreSetCharacterData(text, AxiomSemantics.INSTANCE);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$setText(final AxiomElement ajc$this_, final QName qname) {
        AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$removeChildren(ajc$this_);
        if (qname != null) {
            final OMNamespace ns = ajc$this_.handleNamespace(qname.getNamespaceURI(), qname.getPrefix());
            AxiomInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomInformationItem$getOMFactory(ajc$this_).createOMText((OMContainer)ajc$this_, (ns == null) ? qname.getLocalPart() : (String.valueOf(ns.getPrefix()) + ":" + qname.getLocalPart()));
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$discard(final AxiomElement ajc$this_) {
        if (ajc$this_.getState() == 1 && ajc$this_.getBuilder() != null) {
            ((StAXOMBuilder)ajc$this_.getBuilder()).discard((OMContainer)ajc$this_);
        }
        AxiomChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$detach(ajc$this_);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$detachAndDiscardParent(final AxiomElement ajc$this_) {
        ajc$this_.internalUnsetParent(null);
        ajc$this_.coreSetPreviousSibling(null);
        ajc$this_.coreSetNextSibling(null);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$insertChild(final AxiomElement ajc$this_, final Class[] sequence, final int pos, final OMNode newChild) {
        if (!sequence[pos].isInstance(newChild)) {
            throw new IllegalArgumentException();
        }
        for (OMNode child = AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstOMChild(ajc$this_); child != null; child = child.getNextOMSibling()) {
            if (child instanceof OMElement) {
                if (child == newChild) {
                    return;
                }
                if (sequence[pos].isInstance(child)) {
                    child.insertSiblingAfter(newChild);
                    child.detach();
                    return;
                }
                boolean isAfter = false;
                for (int i = 0; i < pos; ++i) {
                    if (sequence[i].isInstance(child)) {
                        isAfter = true;
                        break;
                    }
                }
                if (!isAfter) {
                    child.insertSiblingBefore(newChild);
                    return;
                }
            }
        }
        AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$addChild(ajc$this_, newChild);
    }
    
    public static OMNamespace ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$handleNamespace(final AxiomElement ajc$this_, final String namespaceURI, final String prefix) {
        if (prefix.length() == 0 && namespaceURI.length() == 0) {
            final OMNamespace namespace = ajc$this_.getDefaultNamespace();
            if (namespace != null) {
                ajc$this_.declareDefaultNamespace("");
            }
            return null;
        }
        OMNamespace namespace = ajc$this_.findNamespace(namespaceURI, prefix);
        if (namespace == null) {
            namespace = ajc$this_.declareNamespace(namespaceURI, (prefix.length() > 0) ? prefix : null);
        }
        return namespace;
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$internalAppendAttribute(final AxiomElement ajc$this_, final OMAttribute attr) {
        ajc$this_.coreSetAttribute(AxiomSemantics.ATTRIBUTE_MATCHER, (CoreAttribute)attr, AxiomSemantics.INSTANCE);
    }
    
    public static OMAttribute ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$addAttribute(final AxiomElement ajc$this_, OMAttribute attr) {
        final OMElement owner = attr.getOwner();
        if (owner != null) {
            if (owner == ajc$this_) {
                return attr;
            }
            attr = AxiomInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomInformationItem$getOMFactory(ajc$this_).createOMAttribute(attr.getLocalName(), attr.getNamespace(), attr.getAttributeValue());
        }
        NSUtil.handleNamespace(ajc$this_, attr.getNamespace(), true, true);
        ajc$this_.internalAppendAttribute(attr);
        return attr;
    }
    
    public static OMAttribute ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$addAttribute(final AxiomElement ajc$this_, final String localName, final String value, final OMNamespace ns) {
        OMNamespace namespace = null;
        if (ns != null) {
            final String namespaceURI = ns.getNamespaceURI();
            final String prefix = ns.getPrefix();
            if (namespaceURI.length() > 0 || prefix != null) {
                namespace = ajc$this_.findNamespace(namespaceURI, prefix);
                if (namespace == null || (prefix == null && namespace.getPrefix().length() == 0)) {
                    namespace = (OMNamespace)new OMNamespaceImpl(namespaceURI, (prefix != null) ? prefix : NSUtils.generatePrefix(namespaceURI));
                }
            }
        }
        return ajc$this_.addAttribute(AxiomInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomInformationItem$getOMFactory(ajc$this_).createOMAttribute(localName, namespace, value));
    }
    
    public static Iterator ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getAllAttributes(final AxiomElement ajc$this_) {
        return ajc$this_.coreGetAttributesByType(AxiomAttribute.class, (Mapper<AxiomAttribute, Object>)AxiomElementSupport.attributeIdentityMapper, AxiomSemantics.INSTANCE);
    }
    
    public static OMAttribute ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getAttribute(final AxiomElement ajc$this_, final QName qname) {
        return (OMAttribute)ajc$this_.coreGetAttribute(AxiomSemantics.ATTRIBUTE_MATCHER, qname.getNamespaceURI(), qname.getLocalPart());
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getAttributeValue(final AxiomElement ajc$this_, final QName qname) {
        final OMAttribute attr = ajc$this_.getAttribute(qname);
        return (attr == null) ? null : attr.getAttributeValue();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$_setAttributeValue(final AxiomElement ajc$this_, final QName qname, final String value) {
        final OMAttribute attr = ajc$this_.getAttribute(qname);
        if (attr != null) {
            attr.setAttributeValue(value);
        }
        else {
            ajc$this_.addAttribute(qname.getLocalPart(), value, (OMNamespace)new OMNamespaceImpl(qname.getNamespaceURI(), qname.getLocalPart()));
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$removeAttribute(final AxiomElement ajc$this_, final OMAttribute attr) {
        if (attr.getOwner() != ajc$this_) {
            throw new OMException("The attribute is not owned by this element");
        }
        ((AxiomAttribute)attr).coreRemove(AxiomSemantics.INSTANCE);
    }
    
    public static OMNamespace ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$addNamespaceDeclaration(final AxiomElement ajc$this_, final String uri, final String prefix) {
        final OMNamespace ns = (OMNamespace)new OMNamespaceImpl(uri, prefix);
        final AxiomNamespaceDeclaration decl = ajc$this_.coreGetNodeFactory().createNode(AxiomNamespaceDeclaration.class);
        AxiomNamespaceDeclarationSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$setDeclaredNamespace(decl, ns);
        ajc$this_.coreAppendAttribute(decl);
        return ns;
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$addNamespaceDeclaration(final AxiomElement ajc$this_, final OMNamespace ns) {
        final AxiomNamespaceDeclaration decl = ajc$this_.coreGetNodeFactory().createNode(AxiomNamespaceDeclaration.class);
        AxiomNamespaceDeclarationSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$setDeclaredNamespace(decl, ns);
        ajc$this_.coreSetAttribute(AxiomSemantics.NAMESPACE_DECLARATION_MATCHER, decl, AxiomSemantics.INSTANCE);
    }
    
    public static Iterator ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getAllDeclaredNamespaces(final AxiomElement ajc$this_) {
        return ajc$this_.coreGetAttributesByType(AxiomNamespaceDeclaration.class, (Mapper<AxiomNamespaceDeclaration, Object>)NamespaceDeclarationMapper.INSTANCE, AxiomSemantics.INSTANCE);
    }
    
    public static OMNamespace ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$declareNamespace(final AxiomElement ajc$this_, OMNamespace namespace) {
        String prefix = namespace.getPrefix();
        final String namespaceURI = namespace.getNamespaceURI();
        if (prefix == null) {
            prefix = NSUtils.generatePrefix(namespaceURI);
            namespace = (OMNamespace)new OMNamespaceImpl(namespaceURI, prefix);
        }
        if (prefix.length() > 0 && namespaceURI.length() == 0) {
            throw new IllegalArgumentException("Cannot bind a prefix to the empty namespace name");
        }
        ajc$this_.addNamespaceDeclaration(namespace);
        return namespace;
    }
    
    public static OMNamespace ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$declareNamespace(final AxiomElement ajc$this_, final String uri, String prefix) {
        if ("".equals(prefix)) {
            AxiomElementSupport.log.warn((Object)"Deprecated usage of OMElement#declareNamespace(String,String) with empty prefix");
            prefix = NSUtils.generatePrefix(uri);
        }
        final OMNamespaceImpl ns = new OMNamespaceImpl(uri, prefix);
        return ajc$this_.declareNamespace((OMNamespace)ns);
    }
    
    public static OMNamespace ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$declareDefaultNamespace(final AxiomElement ajc$this_, final String uri) {
        final OMNamespace elementNamespace = AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$getNamespace(ajc$this_);
        if ((elementNamespace == null && uri.length() > 0) || (elementNamespace != null && elementNamespace.getPrefix().length() == 0 && !elementNamespace.getNamespaceURI().equals(uri))) {
            throw new OMException("Attempt to add a namespace declaration that conflicts with the namespace information of the element");
        }
        final OMNamespace namespace = (OMNamespace)new OMNamespaceImpl((uri == null) ? "" : uri, "");
        ajc$this_.addNamespaceDeclaration(namespace);
        return namespace;
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$undeclarePrefix(final AxiomElement ajc$this_, final String prefix) {
        ajc$this_.addNamespaceDeclaration((OMNamespace)new OMNamespaceImpl("", prefix));
    }
    
    public static OMNamespace ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$findNamespace(final AxiomElement ajc$this_, final String uri, final String prefix) {
        OMNamespace namespace = ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_om_impl_common_AxiomElementSupport$findDeclaredNamespace(uri, prefix);
        if (namespace != null) {
            return namespace;
        }
        final OMContainer parent = AxiomChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$getParent(ajc$this_);
        if (parent != null && parent instanceof OMElement) {
            namespace = ((OMElement)parent).findNamespace(uri, prefix);
            if (namespace != null && ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_om_impl_common_AxiomElementSupport$findDeclaredNamespace(null, namespace.getPrefix()) != null) {
                namespace = null;
            }
        }
        return namespace;
    }
    
    public static OMNamespace ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$findDeclaredNamespace(final AxiomElement ajc$this_, final String uri, final String prefix) {
        for (CoreAttribute attr = ajc$this_.coreGetFirstAttribute(); attr != null; attr = attr.coreGetNextAttribute()) {
            if (attr instanceof AxiomNamespaceDeclaration) {
                final OMNamespace namespace = AxiomNamespaceDeclarationSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$getDeclaredNamespace((AxiomNamespaceDeclaration)attr);
                if ((prefix == null || prefix.equals(namespace.getPrefix())) && (uri == null || uri.equals(namespace.getNamespaceURI()))) {
                    return namespace;
                }
            }
        }
        if ((prefix == null || prefix.equals("xml")) && (uri == null || uri.equals("http://www.w3.org/XML/1998/namespace"))) {
            return AxiomElementSupport.XMLNS;
        }
        return null;
    }
    
    public static OMNamespace ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$findNamespaceURI(final AxiomElement ajc$this_, final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        for (CoreAttribute attr = ajc$this_.coreGetFirstAttribute(); attr != null; attr = attr.coreGetNextAttribute()) {
            if (attr instanceof AxiomNamespaceDeclaration) {
                final AxiomNamespaceDeclaration nsDecl = (AxiomNamespaceDeclaration)attr;
                if (AxiomNamespaceDeclarationSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$coreGetDeclaredPrefix(nsDecl).equals(prefix)) {
                    final OMNamespace ns = AxiomNamespaceDeclarationSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$getDeclaredNamespace(nsDecl);
                    if (ns.getNamespaceURI().length() == 0) {
                        return null;
                    }
                    return ns;
                }
            }
        }
        final OMContainer parent = AxiomChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$getParent(ajc$this_);
        if (parent instanceof OMElement) {
            return ((OMElement)parent).findNamespaceURI(prefix);
        }
        return null;
    }
    
    public static OMNamespace ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getDefaultNamespace(final AxiomElement ajc$this_) {
        return ajc$this_.findNamespaceURI("");
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$internalSerialize(final AxiomElement ajc$this_, final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        ajc$this_.defaultInternalSerialize(serializer, format, cache);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$defaultInternalSerialize(final AxiomElement ajc$this_, final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        serializer.serializeStartpart((OMElement)ajc$this_);
        AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serializeChildren(ajc$this_, serializer, format, cache);
        serializer.writeEndElement();
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$toStringWithConsume(final AxiomElement ajc$this_) throws XMLStreamException {
        final StringWriter sw = new StringWriter();
        AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serializeAndConsume(ajc$this_, sw);
        return sw.toString();
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$toString(final AxiomElement ajc$this_) {
        final StringWriter sw = new StringWriter();
        try {
            AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serialize(ajc$this_, sw);
        }
        catch (final XMLStreamException ex) {
            throw new OMException("Failed to serialize node", (Throwable)ex);
        }
        return sw.toString();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$setComplete(final AxiomElement ajc$this_, final boolean complete) {
        ajc$this_.coreSetState(complete ? 0 : 1);
        final AxiomContainer parent = (AxiomContainer)ajc$this_.coreGetParent();
        if (parent != null) {
            if (!complete) {
                parent.setComplete(false);
            }
            else {
                AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$notifyChildComplete(parent);
            }
        }
    }
    
    public static OMElement ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$cloneOMElement(final AxiomElement ajc$this_) {
        return (OMElement)AxiomInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomInformationItem$clone(ajc$this_, null);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$buildWithAttachments(final AxiomElement ajc$this_) {
        if (ajc$this_.getState() == 1) {
            AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$build(ajc$this_);
        }
        if (ajc$this_.isExpanded()) {
            for (OMNode child = AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstOMChild(ajc$this_); child != null; child = child.getNextOMSibling()) {
                child.buildWithAttachments();
            }
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$checkChild(final AxiomElement ajc$this_, final OMNode child) {
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$setNamespace(final AxiomElement ajc$this_, final OMNamespace namespace) {
        ajc$this_.setNamespace(namespace, true);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$setLineNumber(final AxiomElement ajc$this_, final int lineNumber) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$lineNumber(lineNumber);
    }
    
    public static int ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getLineNumber(final AxiomElement ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$lineNumber();
    }
    
    public static AxiomElementSupport aspectOf() {
        if (AxiomElementSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomElementSupport", AxiomElementSupport.ajc$initFailureCause);
        }
        return AxiomElementSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomElementSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomElementSupport();
    }
}
