package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import javax.activation.DataHandler;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMElement;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMException;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.om.impl.intf.TextContent;
import org.apache.axiom.om.impl.intf.AxiomText;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomTextSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomTextSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomTextSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static TextContent ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$getTextContent(final AxiomText ajc$this_, final boolean force) {
        final Object content = ajc$this_.coreGetCharacterData();
        if (content instanceof TextContent) {
            return (TextContent)content;
        }
        if (force) {
            final TextContent textContent = new TextContent((String)content);
            ajc$this_.coreSetCharacterData(textContent, AxiomSemantics.INSTANCE);
            return textContent;
        }
        return null;
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$isBinary(final AxiomText ajc$this_) {
        final TextContent textContent = ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_om_impl_common_AxiomTextSupport$getTextContent(false);
        return textContent != null && textContent.isBinary();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$setBinary(final AxiomText ajc$this_, final boolean binary) {
        final TextContent textContent = ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_om_impl_common_AxiomTextSupport$getTextContent(binary);
        if (textContent != null) {
            textContent.setBinary(binary);
        }
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$isOptimized(final AxiomText ajc$this_) {
        final TextContent textContent = ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_om_impl_common_AxiomTextSupport$getTextContent(false);
        return textContent != null && textContent.isOptimize();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$setOptimize(final AxiomText ajc$this_, final boolean optimize) {
        final TextContent textContent = ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_om_impl_common_AxiomTextSupport$getTextContent(optimize);
        if (textContent != null) {
            textContent.setOptimize(optimize);
        }
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$getText(final AxiomText ajc$this_) throws OMException {
        return ajc$this_.coreGetCharacterData().toString();
    }
    
    public static char[] ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$getTextCharacters(final AxiomText ajc$this_) {
        final Object content = ajc$this_.coreGetCharacterData();
        if (content instanceof TextContent) {
            return ((TextContent)content).toCharArray();
        }
        return ((String)content).toCharArray();
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$isCharacters(final AxiomText ajc$this_) {
        return false;
    }
    
    public static QName ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$getTextAsQName(final AxiomText ajc$this_) throws OMException {
        return ((OMElement)ajc$this_.getParent()).resolveQName(ajc$this_.getText());
    }
    
    public static OMNamespace ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$getNamespace(final AxiomText ajc$this_) {
        final QName qname = ajc$this_.getTextAsQName();
        if (qname == null) {
            return null;
        }
        final String namespaceURI = qname.getNamespaceURI();
        return (OMNamespace)((namespaceURI.length() == 0) ? null : new OMNamespaceImpl(namespaceURI, qname.getPrefix()));
    }
    
    public static DataHandler ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$getDataHandler(final AxiomText ajc$this_) {
        final Object content = ajc$this_.coreGetCharacterData();
        if (content instanceof TextContent) {
            return ((TextContent)content).getDataHandler();
        }
        throw new OMException("No DataHandler available");
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$getContentID(final AxiomText ajc$this_) {
        return ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_om_impl_common_AxiomTextSupport$getTextContent(true).getContentID();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$internalSerialize(final AxiomText ajc$this_, final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        final Object content = ajc$this_.coreGetCharacterData();
        if (content instanceof TextContent) {
            final TextContent textContent = (TextContent)content;
            if (textContent.isBinary()) {
                final Object dataHandlerObject = textContent.getDataHandlerObject();
                if (dataHandlerObject instanceof DataHandlerProvider) {
                    serializer.writeDataHandler((DataHandlerProvider)dataHandlerObject, textContent.getContentID(), textContent.isOptimize());
                }
                else {
                    serializer.writeDataHandler(textContent.getDataHandler(), textContent.getContentID(), textContent.isOptimize());
                }
            }
            else {
                serializer.writeText(ajc$this_.getType(), textContent.toString());
            }
        }
        else {
            serializer.writeText(ajc$this_.getType(), (String)content);
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$buildWithAttachments(final AxiomText ajc$this_) {
        if (ajc$this_.isOptimized()) {
            ajc$this_.getDataHandler().getDataSource();
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomTextSupport$org_apache_axiom_om_impl_intf_AxiomText$setContentID(final AxiomText ajc$this_, final String cid) {
        ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_om_impl_common_AxiomTextSupport$getTextContent(true).setContentID(cid);
    }
    
    public static AxiomTextSupport aspectOf() {
        if (AxiomTextSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomTextSupport", AxiomTextSupport.ajc$initFailureCause);
        }
        return AxiomTextSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomTextSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomTextSupport();
    }
}
