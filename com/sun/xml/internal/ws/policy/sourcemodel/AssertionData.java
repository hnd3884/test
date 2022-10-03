package com.sun.xml.internal.ws.policy.sourcemodel;

import java.util.Iterator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.PolicyConstants;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import java.io.Serializable;

public final class AssertionData implements Cloneable, Serializable
{
    private static final long serialVersionUID = 4416256070795526315L;
    private static final PolicyLogger LOGGER;
    private final QName name;
    private final String value;
    private final Map<QName, String> attributes;
    private ModelNode.Type type;
    private boolean optional;
    private boolean ignorable;
    
    public static AssertionData createAssertionData(final QName name) throws IllegalArgumentException {
        return new AssertionData(name, null, null, ModelNode.Type.ASSERTION, false, false);
    }
    
    public static AssertionData createAssertionParameterData(final QName name) throws IllegalArgumentException {
        return new AssertionData(name, null, null, ModelNode.Type.ASSERTION_PARAMETER_NODE, false, false);
    }
    
    public static AssertionData createAssertionData(final QName name, final String value, final Map<QName, String> attributes, final boolean optional, final boolean ignorable) throws IllegalArgumentException {
        return new AssertionData(name, value, attributes, ModelNode.Type.ASSERTION, optional, ignorable);
    }
    
    public static AssertionData createAssertionParameterData(final QName name, final String value, final Map<QName, String> attributes) throws IllegalArgumentException {
        return new AssertionData(name, value, attributes, ModelNode.Type.ASSERTION_PARAMETER_NODE, false, false);
    }
    
    AssertionData(final QName name, final String value, final Map<QName, String> attributes, final ModelNode.Type type, final boolean optional, final boolean ignorable) throws IllegalArgumentException {
        this.name = name;
        this.value = value;
        this.optional = optional;
        this.ignorable = ignorable;
        this.attributes = new HashMap<QName, String>();
        if (attributes != null && !attributes.isEmpty()) {
            this.attributes.putAll(attributes);
        }
        this.setModelNodeType(type);
    }
    
    private void setModelNodeType(final ModelNode.Type type) throws IllegalArgumentException {
        if (type == ModelNode.Type.ASSERTION || type == ModelNode.Type.ASSERTION_PARAMETER_NODE) {
            this.type = type;
            return;
        }
        throw AssertionData.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0074_CANNOT_CREATE_ASSERTION_BAD_TYPE(type, ModelNode.Type.ASSERTION, ModelNode.Type.ASSERTION_PARAMETER_NODE)));
    }
    
    AssertionData(final AssertionData data) {
        this.name = data.name;
        this.value = data.value;
        this.attributes = new HashMap<QName, String>();
        if (!data.attributes.isEmpty()) {
            this.attributes.putAll(data.attributes);
        }
        this.type = data.type;
    }
    
    @Override
    protected AssertionData clone() throws CloneNotSupportedException {
        return (AssertionData)super.clone();
    }
    
    public boolean containsAttribute(final QName name) {
        synchronized (this.attributes) {
            return this.attributes.containsKey(name);
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AssertionData)) {
            return false;
        }
        boolean result = true;
        final AssertionData that = (AssertionData)obj;
        result = (result && this.name.equals(that.name));
        result = (result && ((this.value != null) ? this.value.equals(that.value) : (that.value == null)));
        synchronized (this.attributes) {
            result = (result && this.attributes.equals(that.attributes));
        }
        return result;
    }
    
    public String getAttributeValue(final QName name) {
        synchronized (this.attributes) {
            return this.attributes.get(name);
        }
    }
    
    public Map<QName, String> getAttributes() {
        synchronized (this.attributes) {
            return new HashMap<QName, String>(this.attributes);
        }
    }
    
    public Set<Map.Entry<QName, String>> getAttributesSet() {
        synchronized (this.attributes) {
            return new HashSet<Map.Entry<QName, String>>(this.attributes.entrySet());
        }
    }
    
    public QName getName() {
        return this.name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.name.hashCode();
        result = 37 * result + ((this.value == null) ? 0 : this.value.hashCode());
        synchronized (this.attributes) {
            result = 37 * result + this.attributes.hashCode();
        }
        return result;
    }
    
    public boolean isPrivateAttributeSet() {
        return "private".equals(this.getAttributeValue(PolicyConstants.VISIBILITY_ATTRIBUTE));
    }
    
    public String removeAttribute(final QName name) {
        synchronized (this.attributes) {
            return this.attributes.remove(name);
        }
    }
    
    public void setAttribute(final QName name, final String value) {
        synchronized (this.attributes) {
            this.attributes.put(name, value);
        }
    }
    
    public void setOptionalAttribute(final boolean value) {
        this.optional = value;
    }
    
    public boolean isOptionalAttributeSet() {
        return this.optional;
    }
    
    public void setIgnorableAttribute(final boolean value) {
        this.ignorable = value;
    }
    
    public boolean isIgnorableAttributeSet() {
        return this.ignorable;
    }
    
    @Override
    public String toString() {
        return this.toString(0, new StringBuffer()).toString();
    }
    
    public StringBuffer toString(final int indentLevel, final StringBuffer buffer) {
        final String indent = PolicyUtils.Text.createIndent(indentLevel);
        final String innerIndent = PolicyUtils.Text.createIndent(indentLevel + 1);
        final String innerDoubleIndent = PolicyUtils.Text.createIndent(indentLevel + 2);
        buffer.append(indent);
        if (this.type == ModelNode.Type.ASSERTION) {
            buffer.append("assertion data {");
        }
        else {
            buffer.append("assertion parameter data {");
        }
        buffer.append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("namespace = '").append(this.name.getNamespaceURI()).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("prefix = '").append(this.name.getPrefix()).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("local name = '").append(this.name.getLocalPart()).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("value = '").append(this.value).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("optional = '").append(this.optional).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("ignorable = '").append(this.ignorable).append('\'').append(PolicyUtils.Text.NEW_LINE);
        synchronized (this.attributes) {
            if (this.attributes.isEmpty()) {
                buffer.append(innerIndent).append("no attributes");
            }
            else {
                buffer.append(innerIndent).append("attributes {").append(PolicyUtils.Text.NEW_LINE);
                for (final Map.Entry<QName, String> entry : this.attributes.entrySet()) {
                    final QName aName = entry.getKey();
                    buffer.append(innerDoubleIndent).append("name = '").append(aName.getNamespaceURI()).append(':').append(aName.getLocalPart());
                    buffer.append("', value = '").append(entry.getValue()).append('\'').append(PolicyUtils.Text.NEW_LINE);
                }
                buffer.append(innerIndent).append('}');
            }
        }
        buffer.append(PolicyUtils.Text.NEW_LINE).append(indent).append('}');
        return buffer;
    }
    
    public ModelNode.Type getNodeType() {
        return this.type;
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(AssertionData.class);
    }
}
