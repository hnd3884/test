package ar.com.fernandospr.wns.client;

import javax.ws.rs.client.Invocation;
import ar.com.fernandospr.wns.model.WnsNotificationRequestOptional;
import ar.com.fernandospr.wns.model.WnsAbstractNotification;
import javax.ws.rs.client.WebTarget;

public abstract class WnsResourceBuilder
{
    public Invocation.Builder build(final WebTarget target, final WnsAbstractNotification notification, final String token, final WnsNotificationRequestOptional optional) {
        final Invocation.Builder webResourceBuilder = target.request();
        this.addRequiredHeaders(webResourceBuilder, notification.getType(), token);
        this.addOptionalHeaders(webResourceBuilder, optional);
        return webResourceBuilder;
    }
    
    protected abstract Object getEntityToSendWithNotification(final WnsAbstractNotification p0);
    
    protected void addOptionalHeaders(final Invocation.Builder webResourceBuilder, final WnsNotificationRequestOptional optional) {
        if (optional != null) {
            if (!this.emptyString(optional.cachePolicy)) {
                webResourceBuilder.header("X-WNS-Cache-Policy", (Object)optional.cachePolicy);
            }
            if (!this.emptyString(optional.requestForStatus)) {
                webResourceBuilder.header("X-WNS-RequestForStatus", (Object)optional.requestForStatus);
            }
            if (!this.emptyString(optional.tag)) {
                webResourceBuilder.header("X-WNS-Tag", (Object)optional.tag);
            }
            if (!this.emptyString(optional.ttl)) {
                webResourceBuilder.header("X-WNS-TTL", (Object)optional.ttl);
            }
        }
    }
    
    protected void addRequiredHeaders(final Invocation.Builder webResourceBuilder, final String type, final String accessToken) {
        if (type.equalsIgnoreCase("wns/raw")) {
            webResourceBuilder.accept(new String[] { "application/octet-stream" });
        }
        else {
            webResourceBuilder.accept(new String[] { "text/xml" });
        }
        webResourceBuilder.header("X-WNS-Type", (Object)type).header("Authorization", (Object)("Bearer " + accessToken));
    }
    
    private boolean emptyString(final String str) {
        return str == null || str.isEmpty();
    }
}
