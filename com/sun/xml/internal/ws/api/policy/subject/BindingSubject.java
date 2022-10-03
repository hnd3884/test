package com.sun.xml.internal.ws.api.policy.subject;

import com.sun.xml.internal.ws.resources.BindingApiMessages;
import javax.xml.namespace.QName;
import com.sun.istack.internal.logging.Logger;

public class BindingSubject
{
    private static final Logger LOGGER;
    private final QName name;
    private final WsdlMessageType messageType;
    private final WsdlNameScope nameScope;
    private final BindingSubject parent;
    
    BindingSubject(final QName name, final WsdlNameScope scope, final BindingSubject parent) {
        this(name, WsdlMessageType.NO_MESSAGE, scope, parent);
    }
    
    BindingSubject(final QName name, final WsdlMessageType messageType, final WsdlNameScope scope, final BindingSubject parent) {
        this.name = name;
        this.messageType = messageType;
        this.nameScope = scope;
        this.parent = parent;
    }
    
    public static BindingSubject createBindingSubject(final QName bindingName) {
        return new BindingSubject(bindingName, WsdlNameScope.ENDPOINT, null);
    }
    
    public static BindingSubject createOperationSubject(final QName bindingName, final QName operationName) {
        final BindingSubject bindingSubject = createBindingSubject(bindingName);
        return new BindingSubject(operationName, WsdlNameScope.OPERATION, bindingSubject);
    }
    
    public static BindingSubject createInputMessageSubject(final QName bindingName, final QName operationName, final QName messageName) {
        final BindingSubject operationSubject = createOperationSubject(bindingName, operationName);
        return new BindingSubject(messageName, WsdlMessageType.INPUT, WsdlNameScope.MESSAGE, operationSubject);
    }
    
    public static BindingSubject createOutputMessageSubject(final QName bindingName, final QName operationName, final QName messageName) {
        final BindingSubject operationSubject = createOperationSubject(bindingName, operationName);
        return new BindingSubject(messageName, WsdlMessageType.OUTPUT, WsdlNameScope.MESSAGE, operationSubject);
    }
    
    public static BindingSubject createFaultMessageSubject(final QName bindingName, final QName operationName, final QName messageName) {
        if (messageName == null) {
            throw BindingSubject.LOGGER.logSevereException(new IllegalArgumentException(BindingApiMessages.BINDING_API_NO_FAULT_MESSAGE_NAME()));
        }
        final BindingSubject operationSubject = createOperationSubject(bindingName, operationName);
        return new BindingSubject(messageName, WsdlMessageType.FAULT, WsdlNameScope.MESSAGE, operationSubject);
    }
    
    public QName getName() {
        return this.name;
    }
    
    public BindingSubject getParent() {
        return this.parent;
    }
    
    public boolean isBindingSubject() {
        return this.nameScope == WsdlNameScope.ENDPOINT && this.parent == null;
    }
    
    public boolean isOperationSubject() {
        return this.nameScope == WsdlNameScope.OPERATION && this.parent != null && this.parent.isBindingSubject();
    }
    
    public boolean isMessageSubject() {
        return this.nameScope == WsdlNameScope.MESSAGE && this.parent != null && this.parent.isOperationSubject();
    }
    
    public boolean isInputMessageSubject() {
        return this.isMessageSubject() && this.messageType == WsdlMessageType.INPUT;
    }
    
    public boolean isOutputMessageSubject() {
        return this.isMessageSubject() && this.messageType == WsdlMessageType.OUTPUT;
    }
    
    public boolean isFaultMessageSubject() {
        return this.isMessageSubject() && this.messageType == WsdlMessageType.FAULT;
    }
    
    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || !(that instanceof BindingSubject)) {
            return false;
        }
        final BindingSubject thatSubject = (BindingSubject)that;
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
        result = 29 * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = 29 * result + this.messageType.hashCode();
        result = 29 * result + this.nameScope.hashCode();
        result = 29 * result + ((this.parent == null) ? 0 : this.parent.hashCode());
        return result;
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder("BindingSubject[");
        result.append(this.name).append(", ").append(this.messageType);
        result.append(", ").append(this.nameScope).append(", ").append(this.parent);
        return result.append("]").toString();
    }
    
    static {
        LOGGER = Logger.getLogger(BindingSubject.class);
    }
    
    private enum WsdlMessageType
    {
        NO_MESSAGE, 
        INPUT, 
        OUTPUT, 
        FAULT;
    }
    
    private enum WsdlNameScope
    {
        SERVICE, 
        ENDPOINT, 
        OPERATION, 
        MESSAGE;
    }
}
