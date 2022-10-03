package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.intf.AxiomDocument;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomDocumentSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomDocumentSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomDocumentSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static OMElement ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$getOMDocumentElement(final AxiomDocument ajc$this_) {
        return (OMElement)ajc$this_.coreGetDocumentElement();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$setOMDocumentElement(final AxiomDocument ajc$this_, final OMElement documentElement) {
        if (documentElement == null) {
            throw new IllegalArgumentException("documentElement must not be null");
        }
        final AxiomElement existingDocumentElement = (AxiomElement)ajc$this_.coreGetDocumentElement();
        if (existingDocumentElement == null) {
            AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$addChild(ajc$this_, (OMNode)documentElement);
        }
        else {
            existingDocumentElement.coreReplaceWith((CoreChildNode)documentElement, AxiomSemantics.INSTANCE);
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$internalSerialize(final AxiomDocument ajc$this_, final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        ajc$this_.internalSerialize(serializer, format, cache, !format.isIgnoreXMLDeclaration());
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$internalSerialize(final AxiomDocument ajc$this_, final Serializer serializer, final OMOutputFormat format, final boolean cache, final boolean includeXMLDeclaration) throws OutputException {
        if (includeXMLDeclaration) {
            String encoding = format.getCharSetEncoding();
            if (encoding == null || "".equals(encoding)) {
                encoding = ajc$this_.getCharsetEncoding();
            }
            String version = ajc$this_.getXMLVersion();
            if (version == null) {
                version = "1.0";
            }
            if (encoding == null) {
                serializer.writeStartDocument(version);
            }
            else {
                serializer.writeStartDocument(encoding, version);
            }
        }
        AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serializeChildren(ajc$this_, serializer, format, cache);
        serializer.writeEndDocument();
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$getCharsetEncoding(final AxiomDocument ajc$this_) {
        final String inputEncoding = ajc$this_.coreGetInputEncoding();
        return (inputEncoding == null) ? "UTF-8" : inputEncoding;
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$setCharsetEncoding(final AxiomDocument ajc$this_, final String charsetEncoding) {
        ajc$this_.coreSetInputEncoding(charsetEncoding);
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$getXMLVersion(final AxiomDocument ajc$this_) {
        return ajc$this_.coreGetXmlVersion();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$setXMLVersion(final AxiomDocument ajc$this_, final String xmlVersion) {
        ajc$this_.coreSetXmlVersion(xmlVersion);
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$getXMLEncoding(final AxiomDocument ajc$this_) {
        return ajc$this_.coreGetXmlEncoding();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$setXMLEncoding(final AxiomDocument ajc$this_, final String xmlEncoding) {
        ajc$this_.coreSetXmlEncoding(xmlEncoding);
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$isStandalone(final AxiomDocument ajc$this_) {
        return ajc$this_.coreIsStandalone() ? "yes" : "no";
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$setStandalone(final AxiomDocument ajc$this_, final String standalone) {
        ajc$this_.coreSetStandalone("yes".equalsIgnoreCase(standalone));
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$setComplete(final AxiomDocument ajc$this_, final boolean complete) {
        ajc$this_.coreSetState(complete ? 0 : 1);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$checkChild(final AxiomDocument ajc$this_, final OMNode child) {
        if (child instanceof OMElement) {
            if (ajc$this_.getOMDocumentElement() != null) {
                throw new OMException("Document element already exists");
            }
            ajc$this_.checkDocumentElement((OMElement)child);
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$checkDocumentElement(final AxiomDocument ajc$this_, final OMElement element) {
    }
    
    public static AxiomDocumentSupport aspectOf() {
        if (AxiomDocumentSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomDocumentSupport", AxiomDocumentSupport.ajc$initFailureCause);
        }
        return AxiomDocumentSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomDocumentSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomDocumentSupport();
    }
}
