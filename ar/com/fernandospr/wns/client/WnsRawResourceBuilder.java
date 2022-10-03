package ar.com.fernandospr.wns.client;

import ar.com.fernandospr.wns.model.WnsRaw;
import ar.com.fernandospr.wns.model.WnsAbstractNotification;
import javax.ws.rs.client.Invocation;

public class WnsRawResourceBuilder extends WnsResourceBuilder
{
    @Override
    protected void addRequiredHeaders(final Invocation.Builder webResourceBuilder, final String type, final String accessToken) {
        super.addRequiredHeaders(webResourceBuilder, type, accessToken);
        webResourceBuilder.accept(new String[] { "application/octet-stream" });
    }
    
    @Override
    protected Object getEntityToSendWithNotification(final WnsAbstractNotification notification) {
        return ((WnsRaw)notification).getStreamAsByteArray();
    }
}
