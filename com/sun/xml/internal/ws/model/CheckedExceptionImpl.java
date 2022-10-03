package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.addressing.WsaActionUtil;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.ExceptionType;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.api.model.CheckedException;

public final class CheckedExceptionImpl implements CheckedException
{
    private final Class exceptionClass;
    private final TypeInfo detail;
    private final ExceptionType exceptionType;
    private final JavaMethodImpl javaMethod;
    private String messageName;
    private String faultAction;
    
    public CheckedExceptionImpl(final JavaMethodImpl jm, final Class exceptionClass, final TypeInfo detail, final ExceptionType exceptionType) {
        this.faultAction = "";
        this.detail = detail;
        this.exceptionType = exceptionType;
        this.exceptionClass = exceptionClass;
        this.javaMethod = jm;
    }
    
    @Override
    public AbstractSEIModelImpl getOwner() {
        return this.javaMethod.owner;
    }
    
    @Override
    public JavaMethod getParent() {
        return this.javaMethod;
    }
    
    @Override
    public Class getExceptionClass() {
        return this.exceptionClass;
    }
    
    @Override
    public Class getDetailBean() {
        return (Class)this.detail.type;
    }
    
    @Override
    @Deprecated
    public Bridge getBridge() {
        return null;
    }
    
    public XMLBridge getBond() {
        return this.getOwner().getXMLBridge(this.detail);
    }
    
    public TypeInfo getDetailType() {
        return this.detail;
    }
    
    @Override
    public ExceptionType getExceptionType() {
        return this.exceptionType;
    }
    
    @Override
    public String getMessageName() {
        return this.messageName;
    }
    
    public void setMessageName(final String messageName) {
        this.messageName = messageName;
    }
    
    public String getFaultAction() {
        return this.faultAction;
    }
    
    public void setFaultAction(final String faultAction) {
        this.faultAction = faultAction;
    }
    
    public String getDefaultFaultAction() {
        return WsaActionUtil.getDefaultFaultAction(this.javaMethod, this);
    }
}
