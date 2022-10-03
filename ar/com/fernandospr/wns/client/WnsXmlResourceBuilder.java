package ar.com.fernandospr.wns.client;

import ar.com.fernandospr.wns.model.WnsAbstractNotification;
import javax.ws.rs.client.Invocation;

public class WnsXmlResourceBuilder extends WnsResourceBuilder
{
    @Override
    protected void addRequiredHeaders(final Invocation.Builder webResourceBuilder, final String type, final String accessToken) {
        super.addRequiredHeaders(webResourceBuilder, type, accessToken);
        webResourceBuilder.accept(new String[] { "text/xml" });
    }
    
    @Override
    protected Object getEntityToSendWithNotification(final WnsAbstractNotification notification) {
        return notification;
    }
}
