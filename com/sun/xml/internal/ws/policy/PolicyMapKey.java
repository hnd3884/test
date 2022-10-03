package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public final class PolicyMapKey
{
    private static final PolicyLogger LOGGER;
    private final QName service;
    private final QName port;
    private final QName operation;
    private final QName faultMessage;
    private PolicyMapKeyHandler handler;
    
    PolicyMapKey(final QName service, final QName port, final QName operation, final PolicyMapKeyHandler handler) {
        this(service, port, operation, null, handler);
    }
    
    PolicyMapKey(final QName service, final QName port, final QName operation, final QName faultMessage, final PolicyMapKeyHandler handler) {
        if (handler == null) {
            throw PolicyMapKey.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0046_POLICY_MAP_KEY_HANDLER_NOT_SET()));
        }
        this.service = service;
        this.port = port;
        this.operation = operation;
        this.faultMessage = faultMessage;
        this.handler = handler;
    }
    
    PolicyMapKey(final PolicyMapKey that) {
        this.service = that.service;
        this.port = that.port;
        this.operation = that.operation;
        this.faultMessage = that.faultMessage;
        this.handler = that.handler;
    }
    
    public QName getOperation() {
        return this.operation;
    }
    
    public QName getPort() {
        return this.port;
    }
    
    public QName getService() {
        return this.service;
    }
    
    void setHandler(final PolicyMapKeyHandler handler) {
        if (handler == null) {
            throw PolicyMapKey.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0046_POLICY_MAP_KEY_HANDLER_NOT_SET()));
        }
        this.handler = handler;
    }
    
    public QName getFaultMessage() {
        return this.faultMessage;
    }
    
    @Override
    public boolean equals(final Object that) {
        return this == that || (that != null && that instanceof PolicyMapKey && this.handler.areEqual(this, (PolicyMapKey)that));
    }
    
    @Override
    public int hashCode() {
        return this.handler.generateHashCode(this);
    }
    
    @Override
    public String toString() {
        final StringBuffer result = new StringBuffer("PolicyMapKey(");
        result.append(this.service).append(", ").append(this.port).append(", ").append(this.operation).append(", ").append(this.faultMessage);
        return result.append(")").toString();
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(PolicyMapKey.class);
    }
}
