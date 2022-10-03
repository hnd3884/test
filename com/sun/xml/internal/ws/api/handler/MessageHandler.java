package com.sun.xml.internal.ws.api.handler;

import javax.xml.namespace.QName;
import java.util.Set;
import javax.xml.ws.handler.Handler;

public interface MessageHandler<C extends MessageHandlerContext> extends Handler<C>
{
    Set<QName> getHeaders();
}
