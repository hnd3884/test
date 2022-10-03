package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.util.Iterator;
import java.util.Collections;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import java.util.Collection;
import java.util.LinkedList;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public final class ModelNode implements Iterable<ModelNode>, Cloneable
{
    private static final PolicyLogger LOGGER;
    private LinkedList<ModelNode> children;
    private Collection<ModelNode> unmodifiableViewOnContent;
    private final Type type;
    private ModelNode parentNode;
    private PolicySourceModel parentModel;
    private PolicyReferenceData referenceData;
    private PolicySourceModel referencedModel;
    private AssertionData nodeData;
    
    static ModelNode createRootPolicyNode(final PolicySourceModel model) throws IllegalArgumentException {
        if (model == null) {
            throw ModelNode.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0039_POLICY_SRC_MODEL_INPUT_PARAMETER_MUST_NOT_BE_NULL()));
        }
        return new ModelNode(Type.POLICY, model);
    }
    
    private ModelNode(final Type type, final PolicySourceModel parentModel) {
        this.type = type;
        this.parentModel = parentModel;
        this.children = new LinkedList<ModelNode>();
        this.unmodifiableViewOnContent = Collections.unmodifiableCollection((Collection<? extends ModelNode>)this.children);
    }
    
    private ModelNode(final Type type, final PolicySourceModel parentModel, final AssertionData data) {
        this(type, parentModel);
        this.nodeData = data;
    }
    
    private ModelNode(final PolicySourceModel parentModel, final PolicyReferenceData data) {
        this(Type.POLICY_REFERENCE, parentModel);
        this.referenceData = data;
    }
    
    private void checkCreateChildOperationSupportForType(final Type type) throws UnsupportedOperationException {
        if (!this.type.isChildTypeSupported(type)) {
            throw ModelNode.LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0073_CREATE_CHILD_NODE_OPERATION_NOT_SUPPORTED(type, this.type)));
        }
    }
    
    public ModelNode createChildPolicyNode() {
        this.checkCreateChildOperationSupportForType(Type.POLICY);
        final ModelNode node = new ModelNode(Type.POLICY, this.parentModel);
        this.addChild(node);
        return node;
    }
    
    public ModelNode createChildAllNode() {
        this.checkCreateChildOperationSupportForType(Type.ALL);
        final ModelNode node = new ModelNode(Type.ALL, this.parentModel);
        this.addChild(node);
        return node;
    }
    
    public ModelNode createChildExactlyOneNode() {
        this.checkCreateChildOperationSupportForType(Type.EXACTLY_ONE);
        final ModelNode node = new ModelNode(Type.EXACTLY_ONE, this.parentModel);
        this.addChild(node);
        return node;
    }
    
    public ModelNode createChildAssertionNode() {
        this.checkCreateChildOperationSupportForType(Type.ASSERTION);
        final ModelNode node = new ModelNode(Type.ASSERTION, this.parentModel);
        this.addChild(node);
        return node;
    }
    
    public ModelNode createChildAssertionNode(final AssertionData nodeData) {
        this.checkCreateChildOperationSupportForType(Type.ASSERTION);
        final ModelNode node = new ModelNode(Type.ASSERTION, this.parentModel, nodeData);
        this.addChild(node);
        return node;
    }
    
    public ModelNode createChildAssertionParameterNode() {
        this.checkCreateChildOperationSupportForType(Type.ASSERTION_PARAMETER_NODE);
        final ModelNode node = new ModelNode(Type.ASSERTION_PARAMETER_NODE, this.parentModel);
        this.addChild(node);
        return node;
    }
    
    ModelNode createChildAssertionParameterNode(final AssertionData nodeData) {
        this.checkCreateChildOperationSupportForType(Type.ASSERTION_PARAMETER_NODE);
        final ModelNode node = new ModelNode(Type.ASSERTION_PARAMETER_NODE, this.parentModel, nodeData);
        this.addChild(node);
        return node;
    }
    
    ModelNode createChildPolicyReferenceNode(final PolicyReferenceData referenceData) {
        this.checkCreateChildOperationSupportForType(Type.POLICY_REFERENCE);
        final ModelNode node = new ModelNode(this.parentModel, referenceData);
        this.parentModel.addNewPolicyReference(node);
        this.addChild(node);
        return node;
    }
    
    Collection<ModelNode> getChildren() {
        return this.unmodifiableViewOnContent;
    }
    
    void setParentModel(final PolicySourceModel model) throws IllegalAccessException {
        if (this.parentNode != null) {
            throw ModelNode.LOGGER.logSevereException(new IllegalAccessException(LocalizationMessages.WSP_0049_PARENT_MODEL_CAN_NOT_BE_CHANGED()));
        }
        this.updateParentModelReference(model);
    }
    
    private void updateParentModelReference(final PolicySourceModel model) {
        this.parentModel = model;
        for (final ModelNode child : this.children) {
            child.updateParentModelReference(model);
        }
    }
    
    public PolicySourceModel getParentModel() {
        return this.parentModel;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public ModelNode getParentNode() {
        return this.parentNode;
    }
    
    public AssertionData getNodeData() {
        return this.nodeData;
    }
    
    PolicyReferenceData getPolicyReferenceData() {
        return this.referenceData;
    }
    
    public AssertionData setOrReplaceNodeData(final AssertionData newData) {
        if (!this.isDomainSpecific()) {
            throw ModelNode.LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0051_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_ASSERTION_RELATED_NODE_TYPE(this.type)));
        }
        final AssertionData oldData = this.nodeData;
        this.nodeData = newData;
        return oldData;
    }
    
    boolean isDomainSpecific() {
        return this.type == Type.ASSERTION || this.type == Type.ASSERTION_PARAMETER_NODE;
    }
    
    private boolean addChild(final ModelNode child) {
        this.children.add(child);
        child.parentNode = this;
        return true;
    }
    
    void setReferencedModel(final PolicySourceModel model) {
        if (this.type != Type.POLICY_REFERENCE) {
            throw ModelNode.LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0050_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_POLICY_REFERENCE_NODE_TYPE(this.type)));
        }
        this.referencedModel = model;
    }
    
    PolicySourceModel getReferencedModel() {
        return this.referencedModel;
    }
    
    public int childrenSize() {
        return this.children.size();
    }
    
    public boolean hasChildren() {
        return !this.children.isEmpty();
    }
    
    @Override
    public Iterator<ModelNode> iterator() {
        return this.children.iterator();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ModelNode)) {
            return false;
        }
        boolean result = true;
        final ModelNode that = (ModelNode)obj;
        result = (result && this.type.equals(that.type));
        result = (result && ((this.nodeData != null) ? this.nodeData.equals(that.nodeData) : (that.nodeData == null)));
        result = (result && ((this.children != null) ? this.children.equals(that.children) : (that.children == null)));
        return result;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.type.hashCode();
        result = 37 * result + ((this.parentNode == null) ? 0 : this.parentNode.hashCode());
        result = 37 * result + ((this.nodeData == null) ? 0 : this.nodeData.hashCode());
        result = 37 * result + this.children.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return this.toString(0, new StringBuffer()).toString();
    }
    
    public StringBuffer toString(final int indentLevel, final StringBuffer buffer) {
        final String indent = PolicyUtils.Text.createIndent(indentLevel);
        final String innerIndent = PolicyUtils.Text.createIndent(indentLevel + 1);
        buffer.append(indent).append(this.type).append(" {").append(PolicyUtils.Text.NEW_LINE);
        if (this.type == Type.ASSERTION) {
            if (this.nodeData == null) {
                buffer.append(innerIndent).append("no assertion data set");
            }
            else {
                this.nodeData.toString(indentLevel + 1, buffer);
            }
            buffer.append(PolicyUtils.Text.NEW_LINE);
        }
        else if (this.type == Type.POLICY_REFERENCE) {
            if (this.referenceData == null) {
                buffer.append(innerIndent).append("no policy reference data set");
            }
            else {
                this.referenceData.toString(indentLevel + 1, buffer);
            }
            buffer.append(PolicyUtils.Text.NEW_LINE);
        }
        else if (this.type == Type.ASSERTION_PARAMETER_NODE) {
            if (this.nodeData == null) {
                buffer.append(innerIndent).append("no parameter data set");
            }
            else {
                this.nodeData.toString(indentLevel + 1, buffer);
            }
            buffer.append(PolicyUtils.Text.NEW_LINE);
        }
        if (this.children.size() > 0) {
            for (final ModelNode child : this.children) {
                child.toString(indentLevel + 1, buffer).append(PolicyUtils.Text.NEW_LINE);
            }
        }
        else {
            buffer.append(innerIndent).append("no child nodes").append(PolicyUtils.Text.NEW_LINE);
        }
        buffer.append(indent).append('}');
        return buffer;
    }
    
    @Override
    protected ModelNode clone() throws CloneNotSupportedException {
        final ModelNode clone = (ModelNode)super.clone();
        if (this.nodeData != null) {
            clone.nodeData = this.nodeData.clone();
        }
        if (this.referencedModel != null) {
            clone.referencedModel = this.referencedModel.clone();
        }
        clone.children = new LinkedList<ModelNode>();
        clone.unmodifiableViewOnContent = Collections.unmodifiableCollection((Collection<? extends ModelNode>)clone.children);
        for (final ModelNode thisChild : this.children) {
            clone.addChild(thisChild.clone());
        }
        return clone;
    }
    
    PolicyReferenceData getReferenceData() {
        return this.referenceData;
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(ModelNode.class);
    }
    
    public enum Type
    {
        POLICY(XmlToken.Policy), 
        ALL(XmlToken.All), 
        EXACTLY_ONE(XmlToken.ExactlyOne), 
        POLICY_REFERENCE(XmlToken.PolicyReference), 
        ASSERTION(XmlToken.UNKNOWN), 
        ASSERTION_PARAMETER_NODE(XmlToken.UNKNOWN);
        
        private XmlToken token;
        
        private Type(final XmlToken token) {
            this.token = token;
        }
        
        public XmlToken getXmlToken() {
            return this.token;
        }
        
        private boolean isChildTypeSupported(final Type childType) {
            switch (this) {
                case POLICY:
                case ALL:
                case EXACTLY_ONE: {
                    switch (childType) {
                        case ASSERTION_PARAMETER_NODE: {
                            return false;
                        }
                        default: {
                            return true;
                        }
                    }
                    break;
                }
                case POLICY_REFERENCE: {
                    return false;
                }
                case ASSERTION: {
                    switch (childType) {
                        case ASSERTION_PARAMETER_NODE:
                        case POLICY:
                        case POLICY_REFERENCE: {
                            return true;
                        }
                        default: {
                            return false;
                        }
                    }
                    break;
                }
                case ASSERTION_PARAMETER_NODE: {
                    switch (childType) {
                        case ASSERTION_PARAMETER_NODE: {
                            return true;
                        }
                        default: {
                            return false;
                        }
                    }
                    break;
                }
                default: {
                    throw ModelNode.LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0060_POLICY_ELEMENT_TYPE_UNKNOWN(this)));
                }
            }
        }
    }
}
