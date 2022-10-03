package org.apache.naming;

import javax.naming.RefAddr;
import javax.naming.StringRefAddr;

public class HandlerRef extends AbstractRef
{
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.HandlerFactory";
    public static final String HANDLER_NAME = "handlername";
    public static final String HANDLER_CLASS = "handlerclass";
    public static final String HANDLER_LOCALPART = "handlerlocalpart";
    public static final String HANDLER_NAMESPACE = "handlernamespace";
    public static final String HANDLER_PARAMNAME = "handlerparamname";
    public static final String HANDLER_PARAMVALUE = "handlerparamvalue";
    public static final String HANDLER_SOAPROLE = "handlersoaprole";
    public static final String HANDLER_PORTNAME = "handlerportname";
    
    public HandlerRef(final String refname, final String handlerClass) {
        this(refname, handlerClass, null, null);
    }
    
    public HandlerRef(final String refname, final String handlerClass, final String factory, final String factoryLocation) {
        super(refname, factory, factoryLocation);
        StringRefAddr refAddr = null;
        if (refname != null) {
            refAddr = new StringRefAddr("handlername", refname);
            this.add(refAddr);
        }
        if (handlerClass != null) {
            refAddr = new StringRefAddr("handlerclass", handlerClass);
            this.add(refAddr);
        }
    }
    
    @Override
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.HandlerFactory";
    }
}
