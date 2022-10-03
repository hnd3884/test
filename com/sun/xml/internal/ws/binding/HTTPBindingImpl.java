package com.sun.xml.internal.ws.binding;

import java.util.Iterator;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import java.util.Collections;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.ClientMessages;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.Handler;
import java.util.List;
import com.sun.xml.internal.ws.api.BindingID;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.http.HTTPBinding;

public class HTTPBindingImpl extends BindingImpl implements HTTPBinding
{
    HTTPBindingImpl() {
        this(HTTPBindingImpl.EMPTY_FEATURES);
    }
    
    HTTPBindingImpl(final WebServiceFeature... features) {
        super(BindingID.XML_HTTP, features);
    }
    
    @Override
    public void setHandlerChain(final List<Handler> chain) {
        for (final Handler handler : chain) {
            if (!(handler instanceof LogicalHandler)) {
                throw new WebServiceException(ClientMessages.NON_LOGICAL_HANDLER_SET(handler.getClass()));
            }
        }
        this.setHandlerConfig(new HandlerConfiguration(Collections.emptySet(), chain));
    }
}
