package com.sun.xml.internal.ws.policy.subject;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public class WsdlBindingSubject
{
    private static final PolicyLogger LOGGER;
    private final QName name;
    private final WsdlMessageType messageType;
    private final WsdlNameScope nameScope;
    private final WsdlBindingSubject parent;
    
    WsdlBindingSubject(final QName name, final WsdlNameScope scope, final WsdlBindingSubject parent) {
        this(name, WsdlMessageType.NO_MESSAGE, scope, parent);
    }
    
    WsdlBindingSubject(final QName name, final WsdlMessageType messageType, final WsdlNameScope scope, final WsdlBindingSubject parent) {
        this.name = name;
        this.messageType = messageType;
        this.nameScope = scope;
        this.parent = parent;
    }
    
    public static WsdlBindingSubject createBindingSubject(final QName bindingName) {
        return new WsdlBindingSubject(bindingName, WsdlNameScope.ENDPOINT, null);
    }
    
    public static WsdlBindingSubject createBindingOperationSubject(final QName bindingName, final QName operationName) {
        final WsdlBindingSubject bindingSubject = createBindingSubject(bindingName);
        return new WsdlBindingSubject(operationName, WsdlNameScope.OPERATION, bindingSubject);
    }
    
    public static WsdlBindingSubject createBindingMessageSubject(final QName bindingName, final QName operationName, final QName messageName, final WsdlMessageType messageType) {
        if (messageType == null) {
            throw WsdlBindingSubject.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0083_MESSAGE_TYPE_NULL()));
        }
        if (messageType == WsdlMessageType.NO_MESSAGE) {
            throw WsdlBindingSubject.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0084_MESSAGE_TYPE_NO_MESSAGE()));
        }
        if (messageType == WsdlMessageType.FAULT && messageName == null) {
            throw WsdlBindingSubject.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0085_MESSAGE_FAULT_NO_NAME()));
        }
        final WsdlBindingSubject operationSubject = createBindingOperationSubject(bindingName, operationName);
        return new WsdlBindingSubject(messageName, messageType, WsdlNameScope.MESSAGE, operationSubject);
    }
    
    public QName getName() {
        return this.name;
    }
    
    public WsdlMessageType getMessageType() {
        return this.messageType;
    }
    
    public WsdlBindingSubject getParent() {
        return this.parent;
    }
    
    public boolean isBindingSubject() {
        return this.nameScope == WsdlNameScope.ENDPOINT && this.parent == null;
    }
    
    public boolean isBindingOperationSubject() {
        return this.nameScope == WsdlNameScope.OPERATION && this.parent != null && this.parent.isBindingSubject();
    }
    
    public boolean isBindingMessageSubject() {
        return this.nameScope == WsdlNameScope.MESSAGE && this.parent != null && this.parent.isBindingOperationSubject();
    }
    
    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || !(that instanceof WsdlBindingSubject)) {
            return false;
        }
        final WsdlBindingSubject thatSubject = (WsdlBindingSubject)that;
        boolean isEqual = true;
        isEqual = (isEqual && ((this.name != null) ? this.name.equals(thatSubject.name) : (thatSubject.name == null)));
        isEqual = (isEqual && this.messageType.equals(thatSubject.messageType));
        isEqual = (isEqual && this.nameScope.equals(thatSubject.nameScope));
        isEqual = (isEqual && ((this.parent != null) ? this.parent.equals(thatSubject.parent) : (thatSubject.parent == null)));
        return isEqual;
    }
    
    @Override
    public int hashCode() {
        int result = 23;
        result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = 31 * result + this.messageType.hashCode();
        result = 31 * result + this.nameScope.hashCode();
        result = 31 * result + ((this.parent == null) ? 0 : this.parent.hashCode());
        return result;
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder("WsdlBindingSubject[");
        result.append(this.name).append(", ").append(this.messageType);
        result.append(", ").append(this.nameScope).append(", ").append(this.parent);
        return result.append("]").toString();
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(WsdlBindingSubject.class);
    }
    
    public enum WsdlMessageType
    {
        NO_MESSAGE, 
        INPUT, 
        OUTPUT, 
        FAULT;
    }
    
    public enum WsdlNameScope
    {
        SERVICE, 
        ENDPOINT, 
        OPERATION, 
        MESSAGE;
    }
}
