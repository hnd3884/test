package com.sun.xml.internal.ws.policy.sourcemodel;

import java.util.Queue;
import java.util.Set;
import javax.xml.namespace.QName;
import java.util.HashSet;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.PolicyException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.HashMap;
import com.sun.xml.internal.ws.policy.spi.PrefixMapper;
import java.util.Collection;
import java.util.List;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import java.util.Map;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public class PolicySourceModel implements Cloneable
{
    private static final PolicyLogger LOGGER;
    private static final Map<String, String> DEFAULT_NAMESPACE_TO_PREFIX;
    private final Map<String, String> namespaceToPrefix;
    private ModelNode rootNode;
    private final String policyId;
    private final String policyName;
    private final NamespaceVersion nsVersion;
    private final List<ModelNode> references;
    private boolean expanded;
    
    public static PolicySourceModel createPolicySourceModel(final NamespaceVersion nsVersion) {
        return new PolicySourceModel(nsVersion);
    }
    
    public static PolicySourceModel createPolicySourceModel(final NamespaceVersion nsVersion, final String policyId, final String policyName) {
        return new PolicySourceModel(nsVersion, policyId, policyName);
    }
    
    private PolicySourceModel(final NamespaceVersion nsVersion) {
        this(nsVersion, null, null);
    }
    
    private PolicySourceModel(final NamespaceVersion nsVersion, final String policyId, final String policyName) {
        this(nsVersion, policyId, policyName, null);
    }
    
    protected PolicySourceModel(final NamespaceVersion nsVersion, final String policyId, final String policyName, final Collection<PrefixMapper> prefixMappers) {
        this.namespaceToPrefix = new HashMap<String, String>(PolicySourceModel.DEFAULT_NAMESPACE_TO_PREFIX);
        this.references = new LinkedList<ModelNode>();
        this.expanded = false;
        this.rootNode = ModelNode.createRootPolicyNode(this);
        this.nsVersion = nsVersion;
        this.policyId = policyId;
        this.policyName = policyName;
        if (prefixMappers != null) {
            for (final PrefixMapper prefixMapper : prefixMappers) {
                this.namespaceToPrefix.putAll(prefixMapper.getPrefixMap());
            }
        }
    }
    
    public ModelNode getRootNode() {
        return this.rootNode;
    }
    
    public String getPolicyName() {
        return this.policyName;
    }
    
    public String getPolicyId() {
        return this.policyId;
    }
    
    public NamespaceVersion getNamespaceVersion() {
        return this.nsVersion;
    }
    
    Map<String, String> getNamespaceToPrefixMapping() throws PolicyException {
        final Map<String, String> nsToPrefixMap = new HashMap<String, String>();
        final Collection<String> namespaces = this.getUsedNamespaces();
        for (final String namespace : namespaces) {
            final String prefix = this.getDefaultPrefix(namespace);
            if (prefix != null) {
                nsToPrefixMap.put(namespace, prefix);
            }
        }
        return nsToPrefixMap;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PolicySourceModel)) {
            return false;
        }
        boolean result = true;
        final PolicySourceModel that = (PolicySourceModel)obj;
        result = (result && ((this.policyId != null) ? this.policyId.equals(that.policyId) : (that.policyId == null)));
        result = (result && ((this.policyName != null) ? this.policyName.equals(that.policyName) : (that.policyName == null)));
        result = (result && this.rootNode.equals(that.rootNode));
        return result;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + ((this.policyId == null) ? 0 : this.policyId.hashCode());
        result = 37 * result + ((this.policyName == null) ? 0 : this.policyName.hashCode());
        result = 37 * result + this.rootNode.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        final String innerIndent = PolicyUtils.Text.createIndent(1);
        final StringBuffer buffer = new StringBuffer(60);
        buffer.append("Policy source model {").append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("policy id = '").append(this.policyId).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("policy name = '").append(this.policyName).append('\'').append(PolicyUtils.Text.NEW_LINE);
        this.rootNode.toString(1, buffer).append(PolicyUtils.Text.NEW_LINE).append('}');
        return buffer.toString();
    }
    
    @Override
    protected PolicySourceModel clone() throws CloneNotSupportedException {
        final PolicySourceModel clone = (PolicySourceModel)super.clone();
        clone.rootNode = this.rootNode.clone();
        try {
            clone.rootNode.setParentModel(clone);
        }
        catch (final IllegalAccessException e) {
            throw PolicySourceModel.LOGGER.logSevereException(new CloneNotSupportedException(LocalizationMessages.WSP_0013_UNABLE_TO_SET_PARENT_MODEL_ON_ROOT()), e);
        }
        return clone;
    }
    
    public boolean containsPolicyReferences() {
        return !this.references.isEmpty();
    }
    
    private boolean isExpanded() {
        return this.references.isEmpty() || this.expanded;
    }
    
    public synchronized void expand(final PolicySourceModelContext context) throws PolicyException {
        if (!this.isExpanded()) {
            for (final ModelNode reference : this.references) {
                final PolicyReferenceData refData = reference.getPolicyReferenceData();
                final String digest = refData.getDigest();
                PolicySourceModel referencedModel;
                if (digest == null) {
                    referencedModel = context.retrieveModel(refData.getReferencedModelUri());
                }
                else {
                    referencedModel = context.retrieveModel(refData.getReferencedModelUri(), refData.getDigestAlgorithmUri(), digest);
                }
                reference.setReferencedModel(referencedModel);
            }
            this.expanded = true;
        }
    }
    
    void addNewPolicyReference(final ModelNode node) {
        if (node.getType() != ModelNode.Type.POLICY_REFERENCE) {
            throw new IllegalArgumentException(LocalizationMessages.WSP_0042_POLICY_REFERENCE_NODE_EXPECTED_INSTEAD_OF(node.getType()));
        }
        this.references.add(node);
    }
    
    private Collection<String> getUsedNamespaces() throws PolicyException {
        final Set<String> namespaces = new HashSet<String>();
        namespaces.add(this.getNamespaceVersion().toString());
        if (this.policyId != null) {
            namespaces.add("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
        }
        final Queue<ModelNode> nodesToBeProcessed = new LinkedList<ModelNode>();
        nodesToBeProcessed.add(this.rootNode);
        ModelNode processedNode;
        while ((processedNode = nodesToBeProcessed.poll()) != null) {
            for (final ModelNode child : processedNode.getChildren()) {
                if (child.hasChildren() && !nodesToBeProcessed.offer(child)) {
                    throw PolicySourceModel.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0081_UNABLE_TO_INSERT_CHILD(nodesToBeProcessed, child)));
                }
                if (!child.isDomainSpecific()) {
                    continue;
                }
                final AssertionData nodeData = child.getNodeData();
                namespaces.add(nodeData.getName().getNamespaceURI());
                if (nodeData.isPrivateAttributeSet()) {
                    namespaces.add("http://java.sun.com/xml/ns/wsit/policy");
                }
                for (final Map.Entry<QName, String> attribute : nodeData.getAttributesSet()) {
                    namespaces.add(attribute.getKey().getNamespaceURI());
                }
            }
        }
        return namespaces;
    }
    
    private String getDefaultPrefix(final String namespace) {
        return this.namespaceToPrefix.get(namespace);
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(PolicySourceModel.class);
        DEFAULT_NAMESPACE_TO_PREFIX = new HashMap<String, String>();
        final PrefixMapper[] prefixMappers = PolicyUtils.ServiceProvider.load(PrefixMapper.class);
        if (prefixMappers != null) {
            for (final PrefixMapper mapper : prefixMappers) {
                PolicySourceModel.DEFAULT_NAMESPACE_TO_PREFIX.putAll(mapper.getPrefixMap());
            }
        }
        for (final NamespaceVersion version : NamespaceVersion.values()) {
            PolicySourceModel.DEFAULT_NAMESPACE_TO_PREFIX.put(version.toString(), version.getDefaultNamespacePrefix());
        }
        PolicySourceModel.DEFAULT_NAMESPACE_TO_PREFIX.put("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu");
        PolicySourceModel.DEFAULT_NAMESPACE_TO_PREFIX.put("http://java.sun.com/xml/ns/wsit/policy", "sunwsp");
    }
}
