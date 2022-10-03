package com.sun.xml.internal.ws.policy.sourcemodel;

import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.PolicyConstants;
import com.sun.xml.internal.txw2.output.XmlSerializer;
import com.sun.xml.internal.txw2.TXW;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import java.util.Iterator;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.PolicyException;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.output.StaxSerializer;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public final class XmlPolicyModelMarshaller extends PolicyModelMarshaller
{
    private static final PolicyLogger LOGGER;
    private final boolean marshallInvisible;
    
    XmlPolicyModelMarshaller(final boolean marshallInvisible) {
        this.marshallInvisible = marshallInvisible;
    }
    
    @Override
    public void marshal(final PolicySourceModel model, final Object storage) throws PolicyException {
        if (storage instanceof StaxSerializer) {
            this.marshal(model, (StaxSerializer)storage);
        }
        else if (storage instanceof TypedXmlWriter) {
            this.marshal(model, (TypedXmlWriter)storage);
        }
        else {
            if (!(storage instanceof XMLStreamWriter)) {
                throw XmlPolicyModelMarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0022_STORAGE_TYPE_NOT_SUPPORTED(storage.getClass().getName())));
            }
            this.marshal(model, (XMLStreamWriter)storage);
        }
    }
    
    @Override
    public void marshal(final Collection<PolicySourceModel> models, final Object storage) throws PolicyException {
        for (final PolicySourceModel model : models) {
            this.marshal(model, storage);
        }
    }
    
    private void marshal(final PolicySourceModel model, final StaxSerializer writer) throws PolicyException {
        final TypedXmlWriter policy = TXW.create(model.getNamespaceVersion().asQName(XmlToken.Policy), TypedXmlWriter.class, writer);
        this.marshalDefaultPrefixes(model, policy);
        marshalPolicyAttributes(model, policy);
        this.marshal(model.getNamespaceVersion(), model.getRootNode(), policy);
        policy.commit();
    }
    
    private void marshal(final PolicySourceModel model, final TypedXmlWriter writer) throws PolicyException {
        final TypedXmlWriter policy = writer._element(model.getNamespaceVersion().asQName(XmlToken.Policy), TypedXmlWriter.class);
        this.marshalDefaultPrefixes(model, policy);
        marshalPolicyAttributes(model, policy);
        this.marshal(model.getNamespaceVersion(), model.getRootNode(), policy);
    }
    
    private void marshal(final PolicySourceModel model, final XMLStreamWriter writer) throws PolicyException {
        final StaxSerializer serializer = new StaxSerializer(writer);
        final TypedXmlWriter policy = TXW.create(model.getNamespaceVersion().asQName(XmlToken.Policy), TypedXmlWriter.class, serializer);
        this.marshalDefaultPrefixes(model, policy);
        marshalPolicyAttributes(model, policy);
        this.marshal(model.getNamespaceVersion(), model.getRootNode(), policy);
        policy.commit();
        serializer.flush();
    }
    
    private static void marshalPolicyAttributes(final PolicySourceModel model, final TypedXmlWriter writer) {
        final String policyId = model.getPolicyId();
        if (policyId != null) {
            writer._attribute(PolicyConstants.WSU_ID, policyId);
        }
        final String policyName = model.getPolicyName();
        if (policyName != null) {
            writer._attribute(model.getNamespaceVersion().asQName(XmlToken.Name), policyName);
        }
    }
    
    private void marshal(final NamespaceVersion nsVersion, final ModelNode rootNode, final TypedXmlWriter writer) {
        for (final ModelNode node : rootNode) {
            final AssertionData data = node.getNodeData();
            if (this.marshallInvisible || data == null || !data.isPrivateAttributeSet()) {
                TypedXmlWriter child = null;
                if (data == null) {
                    child = writer._element(nsVersion.asQName(node.getType().getXmlToken()), TypedXmlWriter.class);
                }
                else {
                    child = writer._element(data.getName(), TypedXmlWriter.class);
                    final String value = data.getValue();
                    if (value != null) {
                        child._pcdata(value);
                    }
                    if (data.isOptionalAttributeSet()) {
                        child._attribute(nsVersion.asQName(XmlToken.Optional), Boolean.TRUE);
                    }
                    if (data.isIgnorableAttributeSet()) {
                        child._attribute(nsVersion.asQName(XmlToken.Ignorable), Boolean.TRUE);
                    }
                    for (final Map.Entry<QName, String> entry : data.getAttributesSet()) {
                        child._attribute(entry.getKey(), entry.getValue());
                    }
                }
                this.marshal(nsVersion, node, child);
            }
        }
    }
    
    private void marshalDefaultPrefixes(final PolicySourceModel model, final TypedXmlWriter writer) throws PolicyException {
        final Map<String, String> nsMap = model.getNamespaceToPrefixMapping();
        if (!this.marshallInvisible && nsMap.containsKey("http://java.sun.com/xml/ns/wsit/policy")) {
            nsMap.remove("http://java.sun.com/xml/ns/wsit/policy");
        }
        for (final Map.Entry<String, String> nsMappingEntry : nsMap.entrySet()) {
            writer._namespace(nsMappingEntry.getKey(), nsMappingEntry.getValue());
        }
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(XmlPolicyModelMarshaller.class);
    }
}
